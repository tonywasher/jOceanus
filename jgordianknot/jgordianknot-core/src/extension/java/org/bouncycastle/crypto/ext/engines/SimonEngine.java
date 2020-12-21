/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

/**
 * Simon Cipher engine.
 * <p>Cut down version of Tim Whittington's implementation available at
 *  https://github.com/timw/bc-java/blob/feature/simon-speck/core/src/main/java/org/bouncycastle/crypto/engines/SimonEngine.java
 * </p>
 */
public class SimonEngine
        implements BlockCipher {
    /**
     * Number of rounds.
     */
    private static final byte[] ROUNDS = { 68, 69, 72 };

    /**
     * Pre-computed z0...z4 round constants.
     */
    private static final byte[][] Z = {
                    { 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0, 0,
                      0, 0, 1, 1, 1, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 1,
                      0, 0, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 0 },
                    { 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 1, 1, 0,
                      0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1,
                      1, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0 },
                    { 1, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0,
                      1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0,
                      0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1 },
                    { 1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0,
                      1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0,
                      1, 0, 0, 1, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, },
                    { 1, 1, 0, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1, 0,
                      1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0,
                      1, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1 }
            };

    /**
     * Number of words in state.
     */
    private static final int NUMWORDS = 2;

    /**
     * BlockSize.
     */
    private static final int BLOCKSIZE = NUMWORDS * Long.BYTES;

    /**
     * Number of words in 128-bit key.
     */
    private static final int NUMWORDS128 = 2;

    /**
     * Number of words in 192-bit key.
     */
    private static final int NUMWORDS192 = 3;

    /**
     * Number of words in 256-bit key.
     */
    private static final int NUMWORDS256 = 4;

    /**
     * Rotate1.
     */
    private static final int ROT1 = 1;

    /**
     * Rotate2.
     */
    private static final int ROT2 = 2;

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

    /**
     * Constructor.
     */
    public SimonEngine() {
    }

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
        return "Simon";
    }

    @Override
    public int getBlockSize() {
        return BLOCKSIZE;
    }

    @Override
    public int processBlock(final byte[] pInput,
                            final int pInOff,
                            final byte[] pOutput,
                            final int pOutOff) throws IllegalStateException {
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
     * @param pInput the input buffer
     * @param pInOff the input offset
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
            final long myTmp = myX;
            myX = myY ^ (rol64(myX, ROT1) & rol64(myX, ROT8)) ^ rol64(myX, ROT2) ^ theRoundKeys[i];
            myY = myTmp;
        }

        /* Output the bytes from the block */
        Pack.longToBigEndian(myX, pOutput, pOutOff);
        Pack.longToBigEndian(myY, pOutput, pOutOff + Long.BYTES);

        /* Return # of bytes processed */
        return BLOCKSIZE;
    }

    /**
     * Decrypt a block.
     * @param pInput the input buffer
     * @param pInOff the input offset
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
            final long myTmp = myY;
            myY = myX ^ (rol64(myY, ROT1) & rol64(myY, ROT8)) ^ rol64(myY, ROT2) ^ theRoundKeys[i];
            myX = myTmp;
        }

        /* Output the bytes from the block */
        Pack.longToBigEndian(myX, pOutput, pOutOff);
        Pack.longToBigEndian(myY, pOutput, pOutOff + Long.BYTES);

        /* Return # of bytes processed */
        return BLOCKSIZE;
    }

    /**
     * Generate the round keys.
     * @param pKey the key
     */
    private void generateRoundKeys(final byte[] pKey) {
        /* Determine number of key words */
        final int numWords = pKey.length / Long.BYTES;
        final byte[] myConstants = Z[numWords];

        /* Determine # of rounds and allocate round keys */
        theRounds = ROUNDS[numWords - NUMWORDS128];
        theRoundKeys = new long[theRounds];

        /* Load the key */
        for (int i = 0; i < numWords; i++) {
            theRoundKeys[i] = Pack.bigEndianToLong(pKey, (numWords - i - 1) * Long.BYTES);
        }

        /* Key expansion */
        for (int i = numWords; i < theRounds; i++) {
            long tmp = ror64(theRoundKeys[i - 1], ROT3);
            if (numWords == NUMWORDS256) {
                tmp ^= theRoundKeys[i - NUMWORDS192];
            }
            tmp = tmp ^ ror64(tmp, ROT1);
            theRoundKeys[i] = tmp ^ theRoundKeys[i - numWords]
                    ^ myConstants[(i - numWords) % myConstants.length] ^ ~NUMWORDS192;
        }
    }

    /**
     * rotate left.
     * @param pValue the value to rotate
     * @param pBits the # of bits to rotate
     * @return the rotated value
     */
    private static long rol64(final long pValue,
                              final long pBits) {
        return (pValue << pBits) | (pValue >>> (Long.SIZE - pBits));
    }

    /**
     * rotate right.
     * @param pValue the value to rotate
     * @param pBits the # of bits to rotate
     * @return the rotated value
     */
    private static long ror64(final long pValue,
                              final long pBits) {
        return (pValue >>> pBits) | (pValue << (Long.SIZE - pBits));
    }
}
