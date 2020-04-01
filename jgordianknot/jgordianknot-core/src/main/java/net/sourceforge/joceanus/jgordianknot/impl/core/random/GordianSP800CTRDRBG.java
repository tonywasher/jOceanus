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
package net.sourceforge.joceanus.jgordianknot.impl.core.random;

import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianByteArrayInteger;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreCipher;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Implementation of CTRSP800DRBG based on the BouncyCastle Code.
 * <p>
 * This implementation is modified so that it accepts any GordianCipher.
 */
public class GordianSP800CTRDRBG
        implements GordianDRBGenerator {
    /**
     * The endData flag.
     */
    private static final byte ENDDATA = (byte) 0x80;

    /**
     * The Cipher.
     */
    private final GordianCoreCipher<GordianSymKeySpec> theCipher;

    /**
     * The Entropy Source.
     */
    private final EntropySource theEntropy;

    /**
     * The ReSeed Counter.
     */
    private final GordianByteArrayInteger theReseedCounter;

    /**
     * The Seed Length.
     */
    private final int theSeedLen;

    /**
     * The Key.
     */
    private final byte[] theKey;

    /**
     * The Variable Source.
     */
    private final GordianByteArrayInteger theV;

    /**
     * The cipher blockSize (bytes).
     */
    private final int theBlockLen;

    /**
     * Construct a SP800-90A Hash DRBG.
     * @param pCipher SIC Cipher to base the DRBG on.
     * @param pEntropy source of entropy to use for seeding/reSeeding.
     * @param pSecurityBytes personalisation string to distinguish this DRBG (may be null).
     * @param pInitVector nonce to further distinguish this DRBG (may be null).
     * @throws OceanusException on error
     */
    public GordianSP800CTRDRBG(final GordianCoreCipher<GordianSymKeySpec> pCipher,
                               final EntropySource pEntropy,
                               final byte[] pSecurityBytes,
                               final byte[] pInitVector) throws OceanusException {
        /* Store cipher and entropy source */
        theCipher = pCipher;
        theEntropy = pEntropy;

        /* Initialise buffers */
        final int myKeyLen = pCipher.getKeyType().getKeyLength().getByteLength();
        theBlockLen = theCipher.getKeyType().getBlockLength().getByteLength();
        theKey = new byte[myKeyLen];
        theV = new GordianByteArrayInteger(theBlockLen);
        theSeedLen = (myKeyLen + theBlockLen) * Byte.SIZE;

        /* Create Seed Material */
        final byte[] myEntropy = theEntropy.getEntropy();
        final byte[] mySeedInput = Arrays.concatenate(myEntropy, pInitVector, pSecurityBytes);
        final byte[] mySeed = blockCipherDF(mySeedInput, theSeedLen);

        /* Update the state */
        ctrDRBGUpdate(mySeed);

        /* Initialise reSeed counter */
        theReseedCounter = new GordianByteArrayInteger(TethysDataConverter.BYTES_LONG);
        theReseedCounter.iterate();
    }

    /**
     * Block Cipher derivation function.
     * @param pInput the seed material
     * @param pNumBits the number of bits to return
     * @return the derived seed
     * @throws OceanusException on error
     */
    private byte[] blockCipherDF(final byte[] pInput,
                                 final int pNumBits) throws OceanusException {
        /* Check valid # of bits */
        if (pNumBits > GordianCoreRandomFactory.MAX_BITS_REQUEST) {
            throw new IllegalArgumentException("Number of bits per request limited to "
                    + GordianCoreRandomFactory.MAX_BITS_REQUEST);
        }

        /* Allocate the lengths */
        final byte[] myL = TethysDataConverter.integerToByteArray(pInput.length);
        final byte[] myN = TethysDataConverter.integerToByteArray(pNumBits / Byte.SIZE);

        /* Create the input buffer */
        final int myKeyLen = theKey.length;
        final int myBaseLen = (2 * Integer.BYTES) + pInput.length + 1;
        final int myDataLen = theBlockLen * ((myBaseLen + theBlockLen - 1) / theBlockLen);
        final byte[] myBlock = new byte[myDataLen];
        System.arraycopy(myL, 0, myBlock, 0, Integer.BYTES);
        System.arraycopy(myN, 0, myBlock, Integer.BYTES, Integer.BYTES);
        System.arraycopy(pInput, 0, myBlock, 2 * Integer.BYTES, pInput.length);
        myBlock[2 * Integer.BYTES + pInput.length] = ENDDATA;

        /* Create the key buffer and initialise it */
        final byte[] myKey = new byte[myKeyLen];
        for (int i = 0; i < myKeyLen; i++) {
            myKey[i] = (byte) i;
        }

        /* Create the temporary buffers */
        final byte[] myTemp = new byte[myKeyLen + theBlockLen];
        final byte[] myOut = new byte[theBlockLen];
        final byte[] myIV = new byte[theBlockLen];

        /* while we need to generate more bytes */
        int myBuilt = 0;
        final GordianByteArrayInteger myI = new GordianByteArrayInteger();
        while (myBuilt < myTemp.length) {
            /* Update the buffer */
            System.arraycopy(myI.getBuffer(), 0, myIV, 0, Integer.BYTES);
            blockCC(myOut, myKey, myIV, myBlock);

            /* Determine how many bytes of this hash should be used */
            int myNeeded = myTemp.length
                    - myBuilt;
            if (myNeeded > theBlockLen) {
                myNeeded = theBlockLen;
            }

            /* Copy bytes across */
            System.arraycopy(myOut, 0, myTemp, myBuilt, myNeeded);
            myBuilt += myNeeded;
            myI.iterate();
        }

        /* Access key and IV from temp */
        System.arraycopy(myTemp, 0, myKey, 0, myKeyLen);
        System.arraycopy(myTemp, myKeyLen, myOut, 0, theBlockLen);

        /* while we need to generate more bytes */
        myBuilt = 0;
        while (myBuilt < myTemp.length) {
            /* Encrypt the bytes */
            theCipher.initKeyBytes(myKey);
            theCipher.finish(myOut, 0, myOut.length, myOut, 0);

            /* Determine how many bytes of this hash should be used */
            int myNeeded = myTemp.length
                    - myBuilt;
            if (myNeeded > theBlockLen) {
                myNeeded = theBlockLen;
            }

            /* Copy bytes across */
            System.arraycopy(myOut, 0, myTemp, myBuilt, myNeeded);
            myBuilt += myNeeded;
            myI.iterate();
        }

        /* Return the derived bytes */
        return myTemp;
    }

    /**
     * BCC Cipher Chaining.
     * @param pOutput the output buffer
     * @param pKey the key
     * @param pIV the initVector
     * @param pData the data
     * @throws OceanusException on error
     */
    private void blockCC(final byte[] pOutput,
                         final byte[] pKey,
                         final byte[] pIV,
                         final byte[] pData) throws OceanusException {
        /* Build the buffers */
        final byte[] myChain = new byte[theBlockLen];
        final byte[] myIn  = new byte[theBlockLen];

        /* Encrypt the IV */
        theCipher.initKeyBytes(pKey);
        theCipher.finish(pIV, 0, theBlockLen, myChain, 0);

        /* Loop through the data */
        final int myNumBlocks = pData.length / theBlockLen;
        for (int i = 0; i < myNumBlocks; i++) {
            /* Xor in the value */
            final int offset = i * theBlockLen;
            for (int j = 0; j < theBlockLen; j++) {
                myIn[j] = (byte) (myChain[j] ^ pData[offset + j]);
            }

            /* Encrypt it */
            theCipher.finish(myIn, 0, theBlockLen, myChain, 0);
        }

        /* Copy the output */
        System.arraycopy(myChain, 0, pOutput, 0, theBlockLen);
    }

    /**
     * Update state.
     * @param pSeed the seed material
     * @throws OceanusException on error
     */
    private void ctrDRBGUpdate(final byte[] pSeed) throws OceanusException {
        /* Create the buffers */
        final byte[] myOut = new byte[theBlockLen];
        final byte[] myResult = new byte[theSeedLen / Byte.SIZE];

        /* while we need to generate more bytes */
        int myBuilt = 0;
        while (myBuilt < myResult.length) {
            /* Encrypt the bytes */
            theCipher.initKeyBytes(theKey);
            theV.iterate();
            theCipher.finish(theV.getBuffer(), 0, theBlockLen, myOut, 0);

            /* Determine how many bytes of this hash should be used */
            int myNeeded = myResult.length
                    - myBuilt;
            if (myNeeded > theBlockLen) {
                myNeeded = theBlockLen;
            }

            /* Copy bytes across */
            System.arraycopy(myOut, 0, myResult, myBuilt, myNeeded);
            myBuilt += myNeeded;
        }

        /* Xor in the Seed */
        for (int i = 0; i < myResult.length; i++) {
            myResult[i] ^= pSeed[i];
        }

        /* Update key and IV from result*/
        System.arraycopy(myResult, 0, theKey, 0, theKey.length);
        System.arraycopy(myResult, theKey.length, theV.getBuffer(), 0, theBlockLen);
    }

    @Override
    public void reseed(final byte[] pXtraBytes) {
        try {
            /* Hash the new input and add to variable hash */
            final byte[] myEntropy = theEntropy.getEntropy();
            final byte[] mySeedInput = Arrays.concatenate(myEntropy, pXtraBytes);
            final byte[] mySeed = blockCipherDF(mySeedInput, theSeedLen);

            /* Update the state */
            ctrDRBGUpdate(mySeed);

            /* re-initialise reSeed counter */
            theReseedCounter.reset();
            theReseedCounter.iterate();
        } catch (OceanusException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public int generate(final byte[] pOutput,
                        final byte[] pXtraBytes,
                        final boolean isPredictionResistant) {
        /* Check valid # of bits */
        final int myNumBits = pOutput.length << GordianCoreRandomFactory.BIT_SHIFT;
        if (myNumBits > GordianCoreRandomFactory.MAX_BITS_REQUEST) {
            throw new IllegalArgumentException("Number of bits per request limited to "
                    + GordianCoreRandomFactory.MAX_BITS_REQUEST);
        }

        /* Check for reSeed required */
        if (theReseedCounter.compareLimit(GordianCoreRandomFactory.RESEED_MAX)) {
            return -1;
        }

        /* If we are prediction resistant */
        byte[] mySeed = pXtraBytes;
        if (isPredictionResistant) {
            /* reSeed */
            reseed(pXtraBytes);
            mySeed = null;
        }

        try {
            /* if we have extra bytes */
            if (mySeed != null) {
                /* Derive the new input and process */
                mySeed = blockCipherDF(pXtraBytes, theSeedLen);
                ctrDRBGUpdate(mySeed);

                /* else allocate a buffer of zeroes */
            } else {
                mySeed = new byte[theBlockLen + theKey.length];
            }

            /* Create the buffers */
            final byte[] myOut = new byte[theBlockLen];
            final byte[] myResult = new byte[pOutput.length];

            /* while we need to generate more bytes */
            int myBuilt = 0;
            while (myBuilt < myResult.length) {
                /* Encrypt the bytes */
                theCipher.initKeyBytes(theKey);
                theV.iterate();
                theCipher.finish(theV.getBuffer(), 0, theBlockLen, myOut, 0);

                /* Determine how many bytes of this hash should be used */
                int myNeeded = myResult.length
                        - myBuilt;
                if (myNeeded > theBlockLen) {
                    myNeeded = theBlockLen;
                }

                /* Copy bytes across */
                System.arraycopy(myOut, 0, myResult, myBuilt, myNeeded);
                myBuilt += myNeeded;
            }

            /* Update the state */
            ctrDRBGUpdate(mySeed);

            /* Iterate the reSeed counter */
            theReseedCounter.iterate();

            /* Return the bytes */
            System.arraycopy(myResult, 0, pOutput, 0, pOutput.length);
        } catch (OceanusException e) {
            throw new IllegalStateException(e);
        }

        /* Return the number of bits generated */
        return myNumBits;
    }

    @Override
    public int getBlockSize() {
        return theBlockLen * Byte.SIZE;
    }

    @Override
    public String getAlgorithm() {
        return GordianCoreRandomFactory.SP800_PREFIX + theCipher.getCipherSpec().toString();
    }
}
