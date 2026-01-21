/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.gordianknot.impl.ext.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

/**
 * Speck Cipher engine.
 * <p>Cut down version of Tim Whittington's implementation available at
 * https://github.com/timw/bc-java/blob/feature/simon-speck/core/src/main/java/org/bouncycastle/crypto/engines/SpeckEngine.java
 * </p>
 */
public class GordianSpeckEngine
        implements BlockCipher {
    /**
     * Base number of rounds.
     */
    private static final int BASEROUNDS = 32;

    /**
     * Number of words in state.
     */
    private static final int NUMWORDS = 2;

    /**
     * BlockSize.
     */
    private static final int BLOCKSIZE = NUMWORDS * Long.BYTES;

    /**
     * Rotate3.
     */
    private static final int ROT3 = 3;

    /**
     * Rotate8.
     */
    private static final int ROT8 = 8;

    /**
     * The # of rounds.
     */
    private int theRounds;

    /**
     * The expanded key schedule.
     */
    private long[] theRoundKeys;

    /**
     * Are we encrypting?
     */
    private boolean forEncryption;

    @Override
    public void init(final boolean pEncrypt,
                     final CipherParameters pParams) {
        /* Reject invalid parameters */
        if (!(pParams instanceof KeyParameter)) {
            throw new IllegalArgumentException("Invalid parameter passed to Speck init - "
                    + pParams.getClass().getName());
        }

        /* Validate keyLength */
        final byte[] myKey = ((KeyParameter) pParams).getKey();
        final int myKeyLen = myKey.length;
        if ((((myKeyLen << 1) % BLOCKSIZE) != 0)
                || myKeyLen < BLOCKSIZE
                || myKeyLen > (BLOCKSIZE << 1)) {
            throw new IllegalArgumentException("KeyBitSize must be 128, 192 or 256");
        }

        /* Generate the round keys */
        forEncryption = pEncrypt;
        generateRoundKeys(myKey);
    }

    @Override
    public void reset() {
        /* NoOp */
    }

    @Override
    public String getAlgorithmName() {
        return "Speck";
    }

    @Override
    public int getBlockSize() {
        return BLOCKSIZE;
    }

    @Override
    public int processBlock(final byte[] pInput,
                            final int pInOff,
                            final byte[] pOutput,
                            final int pOutOff) {
        /* Check buffers */
        if (pInput == null || pInput.length - pInOff < BLOCKSIZE) {
            throw new IllegalArgumentException("Invalid input buffer");
        }
        if (pOutput == null || pOutput.length - pOutOff < BLOCKSIZE) {
            throw new IllegalArgumentException("Invalid output buffer");
        }

        /* Perform the encryption/decryption */
        return forEncryption
                ? encryptBlock(pInput, pInOff, pOutput, pOutOff)
                : decryptBlock(pInput, pInOff, pOutput, pOutOff);
    }

    /**
     * Encrypt a block.
     *
     * @param pInput  the input buffer
     * @param pInOff  the input offset
     * @param pOutput the output offset
     * @param pOutOff the output offset
     * @return the bytes processed
     */
    private int encryptBlock(final byte[] pInput,
                             final int pInOff,
                             final byte[] pOutput,
                             final int pOutOff) {
        /* Load the bytes into the block */
        long myX = Pack.bigEndianToLong(pInput, pInOff);
        long myY = Pack.bigEndianToLong(pInput, pInOff + Long.BYTES);

        /* Loop through the rounds */
        for (int i = 0; i < theRounds; i++) {
            /* Perform the encryption round */
            myX = (ror64(myX, ROT8) + myY) ^ theRoundKeys[i];
            myY = rol64(myY, ROT3) ^ myX;
        }

        /* Output the bytes from the block */
        Pack.longToBigEndian(myX, pOutput, pOutOff);
        Pack.longToBigEndian(myY, pOutput, pOutOff + Long.BYTES);

        /* Return # of bytes processed */
        return BLOCKSIZE;
    }

    /**
     * Decrypt a block.
     *
     * @param pInput  the input buffer
     * @param pInOff  the input offset
     * @param pOutput the output offset
     * @param pOutOff the output offset
     * @return the bytes processed
     */
    private int decryptBlock(final byte[] pInput,
                             final int pInOff,
                             final byte[] pOutput,
                             final int pOutOff) {
        /* Load the bytes into the block */
        long myX = Pack.bigEndianToLong(pInput, pInOff);
        long myY = Pack.bigEndianToLong(pInput, pInOff + Long.BYTES);

        /* Loop through the rounds */
        for (int i = theRounds - 1; i >= 0; i--) {
            /* Perform the decryption round */
            myY = ror64(myX ^ myY, ROT3);
            myX = rol64((myX ^ theRoundKeys[i]) - myY, ROT8);
        }

        /* Output the bytes from the block */
        Pack.longToBigEndian(myX, pOutput, pOutOff);
        Pack.longToBigEndian(myY, pOutput, pOutOff + Long.BYTES);

        /* Return # of bytes processed */
        return BLOCKSIZE;
    }

    /**
     * Generate the round keys.
     *
     * @param pKey the key
     */
    private void generateRoundKeys(final byte[] pKey) {
        /* Determine number of key words */
        final int numWords = pKey.length / Long.BYTES;

        /* Number of rounds is increased by 1 for each key word > 2 */
        theRounds = BASEROUNDS + (numWords - 2);
        theRoundKeys = new long[theRounds];

        /* Load base key */
        theRoundKeys[0] = Pack.bigEndianToLong(pKey, (numWords - 1) * Long.BYTES);

        /* Load remaining key bytes */
        final long[] myL = new long[numWords];
        for (int i = 0; i < numWords - 1; i++) {
            myL[i] = Pack.bigEndianToLong(pKey, (numWords - i - 2) * Long.BYTES);
        }

        /* Key expansion using round function with round number as key */
        for (int i = 0; i < theRounds - 1; i++) {
            final int lw = (i + numWords - 1) % numWords;
            myL[lw] = (ror64(myL[i % numWords], ROT8) + theRoundKeys[i]) ^ i;
            theRoundKeys[i + 1] = rol64(theRoundKeys[i], ROT3) ^ myL[lw];
        }
    }

    /**
     * rotate left.
     *
     * @param pValue the value to rotate
     * @param pBits  the # of bits to rotate
     * @return the rotated value
     */
    private static long rol64(final long pValue,
                              final long pBits) {
        return (pValue << pBits) | (pValue >>> (Long.SIZE - pBits));
    }

    /**
     * rotate right.
     *
     * @param pValue the value to rotate
     * @param pBits  the # of bits to rotate
     * @return the rotated value
     */
    private static long ror64(final long pValue,
                              final long pBits) {
        return (pValue >>> pBits) | (pValue << (Long.SIZE - pBits));
    }
}
