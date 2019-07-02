/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.bouncycastle.crypto.ext.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.engines.ChaCha7539Engine;
import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.crypto.engines.Salsa20Engine;
import org.bouncycastle.crypto.macs.Poly1305;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

/**
 * ChaCha20Poly1305 Engine.
 */
public class ChaChaPolyEngine
        extends ChaCha7539Engine {
    /**
     * The MacSize.
     */
    private static final int MACSIZE = 16;

    /**
     * The Zero padding.
     */
    private static final byte[] PADDING = new byte[MACSIZE - 1];

    /**
     * The Poly1305Mac.
     */
    private final Poly1305 polyMac;

    /**
     * Are we the XChaCha variant.
     */
    private final boolean xChaCha;

    /**
     * The cachedBytes.
     */
    private final byte[] cachedBytes;

    /**
     * number of bytes in the cache.
     */
    private int cacheBytes;

    /**
     * Are we initialised?
     */
    private boolean initialised;

    /**
     * Are we encrypting?
     */
    private boolean encrypting;

    /**
     * The Initial AEAD Data.
     */
    private byte[] initialAEAD;

    /**
     * Have we completed AEAD.
     */
    private boolean aeadComplete;

    /**
     * The AEAD DataLength.
     */
    private long aeadLength;

    /**
     * The dataLength.
     */
    private long dataLength;

    /**
     * Constructor.
     * @param pXChaCha are we the XChaCha variant?
     */
    public ChaChaPolyEngine(final boolean pXChaCha) {
        xChaCha = pXChaCha;
        polyMac = new Poly1305();
        cachedBytes = new byte[MACSIZE];
    }

    @Override
    public String getAlgorithmName() {
        return (xChaCha ? "X" : "") + "ChaCha20Poly1305";
    }

    @Override
    protected int getNonceSize() {
        return xChaCha ? 24 : 12;
    }

    @Override
    protected void resetCounter() {
        engineState[12] = 1;
    }

    /**
     * Poly1305 key generation: process 256 bit input key and 128 bits of the input nonce
     * using a core ChaCha20 function without input addition to produce 256 bit working key
     * and use that with the remaining 64 bits of nonce to initialize a standard ChaCha20 engine state.
     * @param keyBytes the keyBytes
     * @param ivBytes the ivBytes
     */
    @Override
    protected void setKey(final byte[] keyBytes,
                          final byte[] ivBytes) {
        /* Check key */
        if (keyBytes == null) {
            throw new IllegalArgumentException(getAlgorithmName() + " doesn't support re-init with null key");
        }

        /* Set Key and explicit set counter to zero */
        super.setKey(keyBytes, ivBytes);
        engineState[12] = 0;

        /* If this is an XChaCha variant */
        final int[] hChaCha20Out = new int[engineState.length];
        if (xChaCha) {
            /* Pack first 128 bits of IV into engine state */
            Pack.littleEndianToInt(ivBytes, 0, engineState, 12, 4);

            /* Process engine state to generate ChaCha20 key */
            ChaChaEngine.chachaCore(Salsa20Engine.DEFAULT_ROUNDS, engineState, hChaCha20Out);

            /* Set new key, removing addition in last round of chachaCore */
            engineState[4] = hChaCha20Out[0] - engineState[0];
            engineState[5] = hChaCha20Out[1] - engineState[1];
            engineState[6] = hChaCha20Out[2] - engineState[2];
            engineState[7] = hChaCha20Out[3] - engineState[3];

            engineState[8] = hChaCha20Out[12] - engineState[12];
            engineState[9] = hChaCha20Out[13] - engineState[13];
            engineState[10] = hChaCha20Out[14] - engineState[14];
            engineState[11] = hChaCha20Out[15] - engineState[15];

            /* Last 64 bits of input IV and reset counter */
            Pack.littleEndianToInt(ivBytes, 16, engineState, 14, 2);
            engineState[12] = 0;
            engineState[13] = 0;
        }

        /* Process engine state to generate ChaCha20 key */
        ChaChaEngine.chachaCore(Salsa20Engine.DEFAULT_ROUNDS, engineState, hChaCha20Out);

        /* Access the key as a set of integers */
        final int[] keyInt = new int[8];
        keyInt[0] = hChaCha20Out[0];
        keyInt[1] = hChaCha20Out[1];
        keyInt[2] = hChaCha20Out[2];
        keyInt[3] = hChaCha20Out[3];
        keyInt[4] = hChaCha20Out[4];
        keyInt[5] = hChaCha20Out[5];
        keyInt[6] = hChaCha20Out[6];
        keyInt[7] = hChaCha20Out[7];

        /* Access the key as bytes */
        final byte[] key = new byte[32];
        Pack.intToLittleEndian(keyInt, key, 0);

        /* Set the Poly1305 key */
        polyMac.init(new KeyParameter(key));
    }

    /**
     * Intercept init to handle AEAD and encryption direction.
     * @param forEncryption whether or not we are for encryption
     * @param params the parameters required to set up the cipher
     */
    @Override
    public void init(final boolean forEncryption,
                     final CipherParameters params) {
        /* Access parameters */
        CipherParameters parms = params;

        /* Reset details */
        initialised = false;
        initialAEAD = null;

        /* If we have AEAD parameters */
        if (params instanceof AEADParameters) {
            final AEADParameters param = (AEADParameters) params;
            initialAEAD = param.getAssociatedText();
            final byte[] nonce = param.getNonce();
            final KeyParameter key = param.getKey();
            parms = new ParametersWithIV(key, nonce);
        }

        /* Initialise the cipher */
        super.init(forEncryption, parms);

        /* Note that we are initialised */
        encrypting = forEncryption;
        initialised = true;
    }

    /**
     * Reset the cipher.
     */
    @Override
    public void reset() {
        /* Reset state */
        dataLength = 0;
        aeadLength = 0;
        aeadComplete = false;
        cacheBytes = 0;
        polyMac.reset();
        super.reset();

        /* If we have initial AEAD data */
        if (initialAEAD != null) {
            /* Reapply initial AEAD data */
            aeadLength = initialAEAD.length;
            polyMac.update(initialAEAD, 0, (int) aeadLength);
        }
    }

    /**
     * Process AAD byte.
     * @param in the byte to process
     */
    public void processAADByte(final byte in) {
        /* Check AAD is allowed */
        checkAEADStatus();

        /* Process the byte */
        polyMac.update(in);
        aeadLength++;
    }

    /**
     * Process AAD bytes.
     * @param in the bytes to process
     * @param inOff the offset from which to start processing
     * @param len the number of bytes to process
     */
    public void processAADBytes(final byte[] in,
                                final int inOff,
                                final int len) {
        /* Check AAD is allowed */
        checkAEADStatus();

        /* Process the bytes */
        polyMac.update(in, inOff, len);
        aeadLength += len;
    }

    /**
     * check AEAD status.
     */
    private void checkAEADStatus() {
        /* Check we are initialised */
        if (!initialised) {
            throw new IllegalStateException("Cipher is not initialised");
        }

        /* Check AAD is allowed */
        if (aeadComplete) {
            throw new IllegalStateException("AEAD data cannot be processed after ordinary data");
        }
    }

    /**
     * check status.
     */
    private void checkStatus() {
        /* Check we are initialised */
        if (!initialised) {
            throw new IllegalStateException("Cipher is not initialised");
        }

        /* Complete the AEAD section if this is the first data */
        if (!aeadComplete) {
            completeAEADMac();
        }
    }

    /**
     * Process bytes.
     * @param in the input buffer
     * @param inOff the offset from which to start processing
     * @param len the length of data to process
     * @param out the output buffer
     * @param outOff the offset from which to start writing output
     * @return the length of data written out
     */
    @Override
    public int processBytes(final byte[] in,
                            final int inOff,
                            final int len,
                            final byte[] out,
                            final int outOff) {
        /* Check status */
        checkStatus();

        /* process the bytes */
        return encrypting
                  ? processEncryptionBytes(in, inOff, len, out, outOff)
                  : processDecryptionBytes(in, inOff, len, out, outOff);
    }

    /**
     * Obtain the maximum output length for a given input length.
     * @param len the length of data to process
     * @return the maximum output length
     */
    public int getOutputSize(final int len) {
        if (encrypting) {
            return len + MACSIZE;
        }

        /* Allow for cacheSpace */
        final int cacheSpace = MACSIZE - cacheBytes;
        return len < cacheSpace ? 0 : len - cacheSpace;
    }

    /**
     * Finish processing.
     * @param out the output buffer
     * @param outOff the offset from which to start writing output
     * @return the length of data written out
     * @throws InvalidCipherTextException on mac misMatch
     */
    public int finish(final byte[] out,
                      final int outOff) throws InvalidCipherTextException {
        /* Check status */
        checkStatus();

        /* finish the mac */
        final int outLen = encrypting
                     ? finishEncryptionMac(out, outOff)
                     : finishDecryptionMac();

        /* Reset the cipher */
        reset();

        /* return the number of bytes processed */
        return outLen;
    }

    /**
     * Process encryption bytes.
     * @param in the input buffer
     * @param inOff the offset from which to start processing
     * @param len the length of data to process
     * @param out the output buffer
     * @param outOff the offset from which to start writing output
     * @return the length of data written out
     */
    private int processEncryptionBytes(final byte[] in,
                                       final int inOff,
                                       final int len,
                                       final byte[] out,
                                       final int outOff) {
        /* Check that the buffers are sufficient */
        if (in.length < (len + inOff)) {
            throw new DataLengthException("Input buffer too short.");
        }
        if (out.length < (len + outOff)) {
            throw new OutputLengthException("Output buffer too short.");
        }

        /* Process the bytes */
        super.processBytes(in, inOff, len, out, outOff);

        /* Update the mac */
        polyMac.update(out, outOff, len);
        dataLength += len;

        /* Return the number of bytes processed */
        return len;
    }

    /**
     * finish the encryption Mac.
     * @param out the output buffer
     * @param outOff the offset from which to start writing output
     * @return the length of data written out
     */
    private int finishEncryptionMac(final byte[] out,
                                    final int outOff) {
        /* Check that the output buffer is sufficient */
        if (out.length < (MACSIZE + outOff)) {
            throw new OutputLengthException("Output buffer too short.");
        }

        /* complete the data portion of the Mac */
        completeDataMac();

        /* Update and return the mac in the output buffer */
        return polyMac.doFinal(out, outOff);
    }

    /**
     * Process decryption bytes.
     * @param in the input buffer
     * @param inOff the offset from which to start processing
     * @param len the length of data to process
     * @param out the output buffer
     * @param outOff the offset from which to start writing output
     * @return the length of data written out
     */
    private int processDecryptionBytes(final byte[] in,
                                       final int inOff,
                                       final int len,
                                       final byte[] out,
                                       final int outOff) {
        /* Check that the buffers are sufficient */
        if (in.length < (len + inOff)) {
            throw new DataLengthException("Input buffer too short.");
        }
        if (out.length < (len + outOff + cacheBytes - MACSIZE)) {
            throw new OutputLengthException("Output buffer too short.");
        }

        /* Count how much we have processed */
        int processed = 0;

        /* If we have sufficient data */
        if (len >= MACSIZE) {
            /* If we have cached mac bytes */
            if (cacheBytes > 0) {
                /* Process any existing cachedBytes */
                polyMac.update(cachedBytes, 0, cacheBytes);
                dataLength += cacheBytes;

                /* Process the cached bytes */
                processed = super.processBytes(cachedBytes, 0, cacheBytes, out, outOff);
            }

            /* Determine how many bytes to process */
            final int numBytes = len - MACSIZE;
            if (numBytes > 0) {
                /* Process the data */
                polyMac.update(in, inOff, numBytes);
                dataLength += numBytes;

                /* Process the input */
                processed += super.processBytes(in, inOff, numBytes, out, outOff + processed);
            }

            /* Store the remaining input into the cache */
            System.arraycopy(in, inOff + numBytes, cachedBytes, 0, MACSIZE);
            cacheBytes = MACSIZE;

            /* else all new data will be placed into the cache */
        } else {
            /* Calculate number of bytes in the cache to process */
            final int numBytes = cacheBytes + len - MACSIZE;
            if (numBytes > 0) {
                /* Process the excess cachedBytes */
                polyMac.update(cachedBytes, 0, numBytes);
                dataLength += numBytes;

                /* Process the cached bytes */
                processed = super.processBytes(cachedBytes, 0, numBytes, out, outOff);

                /* Move remaining cached bytes down */
                cacheBytes -= numBytes;
                System.arraycopy(cachedBytes, numBytes, cachedBytes, 0, cacheBytes);
            }

            /* Store the data into the cache */
            System.arraycopy(in, inOff, cachedBytes, cacheBytes, len);
            cacheBytes += len;
        }

        /* Return the number of bytes processed */
        return processed;
    }

    /**
     * finish the decryption Mac.
     * @return the length of data written out
     * @throws InvalidCipherTextException on mac misMatch
     */
    private int finishDecryptionMac() throws InvalidCipherTextException {
        /* If we do not have sufficient data */
        if (cacheBytes < MACSIZE) {
            throw new InvalidCipherTextException("data too short");
        }

        /* complete the data portion of the Mac */
        completeDataMac();

        /* Update and return the mac in the output buffer */
        final byte[] mac = new byte[MACSIZE];
        polyMac.doFinal(mac, 0);

        /* Check that the buffers compare */
        if (!Arrays.constantTimeAreEqual(mac, cachedBytes)) {
            throw new InvalidCipherTextException("mac check failed");
        }

        /* No bytes returned */
        return 0;
    }

    /**
     * Complete AEAD Mac input.
     */
    private void completeAEADMac() {
        /* Pad to boundary */
        final int xtra = (int) aeadLength % MACSIZE;
        if (xtra != 0) {
            final int numPadding = MACSIZE - xtra;
            polyMac.update(PADDING, 0, numPadding);
        }
        aeadComplete = true;
    }

    /**
     * Complete Mac data input.
     */
    private void completeDataMac() {
        /* Pad to boundary */
        final int xtra = (int) dataLength % MACSIZE;
        if (xtra != 0) {
            final int numPadding = MACSIZE - xtra;
            polyMac.update(PADDING, 0, numPadding);
        }

        /* Write the lengths */
        final byte[] len = new byte[8];
        Pack.longToLittleEndian(aeadLength, len, 0);
        polyMac.update(len, 0, 8);
        Pack.longToLittleEndian(dataLength, len, 0);
        polyMac.update(len, 0, 8);
    }
}
