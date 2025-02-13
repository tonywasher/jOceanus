/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.ext.modes;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.macs.Poly1305;
import org.bouncycastle.crypto.modes.AEADCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

/**
 * ChaCha20Poly1305 Engine.
 * Donated to BouncyCastle.
 */
public class GordianChaChaPoly1305
        implements AEADCipher {
    /**
     * The MacSize.
     */
    private static final int MACSIZE = 16;

    /**
     * The Zero padding.
     */
    private static final byte[] PADDING = new byte[MACSIZE - 1];

    /**
     * The Underlying cipher.
     */
    private final StreamCipher theCipher;

    /**
     * The Poly1305Mac.
     */
    private final Poly1305 polyMac;

    /**
     * The cachedBytes.
     */
    private final byte[] cachedBytes;

    /**
     * The lastMac.
     */
    private byte[] lastMac;

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
     * Have we completed AEAD?
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
     * @param pChaChaEngine the ChaCha engine.
     */
    public GordianChaChaPoly1305(final StreamCipher pChaChaEngine) {
        theCipher = pChaChaEngine;
        polyMac = new Poly1305();
        cachedBytes = new byte[MACSIZE];
    }

    /**
     * Obtain algorithm name.
     * @return the algorithm name
     */
    @Override
    public String getAlgorithmName() {
        return theCipher.getAlgorithmName() + "Poly1305";
    }

    /**
     * Initialise the cipher.
     * @param forEncryption true/false
     * @param params the parameters
     */
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
        theCipher.init(forEncryption, parms);

        /* Reset the cipher and init the Mac */
        reset();

        /* Note that we are initialised */
        encrypting = forEncryption;
        initialised = true;
    }

    @Override
    public void reset() {
        /* Reset state */
        dataLength = 0;
        aeadLength = 0;
        aeadComplete = false;
        cacheBytes = 0;
        theCipher.reset();

        /* Run the cipher once to initialise the mac */
        final byte[] firstBlock = new byte[Long.SIZE]; // ChaCha stateLength
        theCipher.processBytes(firstBlock, 0, firstBlock.length, firstBlock, 0);
        polyMac.init(new KeyParameter(firstBlock, 0, Integer.SIZE)); // Poly1305 KeyLength
        Arrays.fill(firstBlock, (byte) 0);

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

    @Override
    public int processByte(final byte pByte,
                           final byte[] out,
                           final int outOffset) throws DataLengthException {
        final byte[] myByte = new byte[] { pByte };
        return processBytes(myByte, 0, 1, out, outOffset);
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
     * Process single byte (not supported).
     * @param in the input byte
     * @return the output byte
     */
    public byte returnByte(final byte in) {
        throw new UnsupportedOperationException();
    }

    /**
     * Process bytes.
     * @param in the input buffer
     * @param inOff the starting offset in the input buffer
     * @param len the length of data in the input buffer
     * @param out the output buffer
     * @param outOff the starting offset in the output buffer
     * @return the number of bytes returned in the output buffer
     */
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
     * Obtain the maximum output length for an update.
     * @param len the data length to update
     * @return the maximum output length
     */
    public int getUpdateOutputSize(final int len) {
        return len;
    }

    /**
     * Obtain the last calculated Mac.
     * @return the last calculated Mac
     */
    public byte[] getMac() {
        return lastMac == null
                ? new byte[MACSIZE]
                : Arrays.clone(lastMac);
    }

    /**
     * Finish processing.
     * @param out the output buffer
     * @param outOff the offset from which to start writing output
     * @return the length of data written out
     * @throws InvalidCipherTextException on mac misMatch
     */
    public int doFinal(final byte[] out,
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
     * Obtain buffer length (allowing for null).
     * @param pBuffer the buffere
     * @return the length
     */
    private static int bufLength(final byte[] pBuffer) {
        return pBuffer == null ? 0 : pBuffer.length;
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
        if (bufLength(in) < (len + inOff)) {
            throw new DataLengthException("Input buffer too short.");
        }
        if (bufLength(out) < (len + outOff)) {
            throw new OutputLengthException("Output buffer too short.");
        }

        /* Process the bytes */
        theCipher.processBytes(in, inOff, len, out, outOff);

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
        if (bufLength(out) < (MACSIZE + outOff)) {
            throw new OutputLengthException("Output buffer too short.");
        }

        /* complete the data portion of the Mac */
        completeDataMac();

        /* Calculate the Mac */
        lastMac = new byte[MACSIZE];
        polyMac.doFinal(lastMac, 0);

        /* Update and return the mac in the output buffer */
        System.arraycopy(lastMac, 0, out, outOff, MACSIZE);
        return MACSIZE;
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
        if (bufLength(in) < (len + inOff)) {
            throw new DataLengthException("Input buffer too short.");
        }
        if (bufLength(out) < (len + outOff + cacheBytes - MACSIZE)) {
            throw new OutputLengthException("Output buffer too short.");
        }

        /* Count how much we have processed */
        int processed = 0;

        /* Calculate the number of bytes to process from the cache */
        final int numInputBytes = len - MACSIZE;
        int numCacheBytes = Math.max(cacheBytes + numInputBytes, 0);
        numCacheBytes = Math.min(cacheBytes, numCacheBytes);

        /* If we should process bytes from the cache */
        if (numCacheBytes > 0) {
            /* Process any required cachedBytes */
            polyMac.update(cachedBytes, 0, numCacheBytes);
            dataLength += numCacheBytes;

            /* Process the cached bytes */
            processed = theCipher.processBytes(cachedBytes, 0, numCacheBytes, out, outOff);

            /* Move any remaining cached bytes down in the buffer */
            cacheBytes -= numCacheBytes;
            if (cacheBytes > 0) {
                System.arraycopy(cachedBytes, numCacheBytes, cachedBytes, 0, cacheBytes);
            }
        }

        /* Process any excess bytes from the input buffer */
        if (numInputBytes > 0) {
            /* Process the data */
            polyMac.update(in, inOff, numInputBytes);
            dataLength += numInputBytes;

            /* Process the input */
            processed += theCipher.processBytes(in, inOff, numInputBytes, out, outOff + processed);
        }

        /* Store the remaining input into the cache */
        final int numToCache = Math.min(len, MACSIZE);
        System.arraycopy(in, inOff + len - numToCache, cachedBytes, cacheBytes, numToCache);
        cacheBytes += numToCache;

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

        /* Calculate the Mac */
        lastMac = new byte[MACSIZE];
        polyMac.doFinal(lastMac, 0);

        /* Check that the calculated Mac is identical to that contained in the cache */
        if (!Arrays.constantTimeAreEqual(lastMac, cachedBytes)) {
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
        padToBoundary(aeadLength);

        /* Set flag */
        aeadComplete = true;
    }

    /**
     * Complete Mac data input.
     */
    private void completeDataMac() {
        /* Pad to boundary */
        padToBoundary(dataLength);

        /* Write the lengths */
        final byte[] len = new byte[Long.BYTES << 1]; // 2 * Long.BYTES
        Pack.longToLittleEndian(aeadLength, len, 0);
        Pack.longToLittleEndian(dataLength, len, Long.BYTES); // Long.BYTES
        polyMac.update(len, 0, len.length);
    }

    /**
     * Pad to boundary.
     * @param pDataLen the length of the data to pad
     */
    private void padToBoundary(final long pDataLen) {
        /* Pad to boundary */
        final int xtra = (int) pDataLen & (MACSIZE - 1);
        if (xtra != 0) {
            final int numPadding = MACSIZE - xtra;
            polyMac.update(PADDING, 0, numPadding);
        }
    }
}
