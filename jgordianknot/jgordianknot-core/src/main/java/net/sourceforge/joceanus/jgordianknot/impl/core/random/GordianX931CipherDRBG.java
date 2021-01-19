/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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

import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianByteArrayInteger;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Implementation of X931DRBG based on the BouncyCastle Code.
 * <p>
 * This implementation is modified so that it accepts any GordianCipher.
 */
public class GordianX931CipherDRBG
        implements GordianDRBGenerator {
    /**
     * The X931 prefix.
     */
    private static final String X931_PREFIX = "X931-";

    /**
     * The bit shift.
     */
    private static final int BIT_SHIFT = 3;

    /**
     * The power of 2 for RESEED calculation.
     */
    private static final int RESEED_POWER = 24;

    /**
     * Max # of bits before reSeed.
     */
    private static final long BLOCK128_RESEED_MAX = 1L << (RESEED_POWER - 1);

    /**
     * The power of 2 for BITS calculation.
     */
    private static final int BITS_POWER = 19;

    /**
     * Max # of bits per request.
     */
    private static final int BLOCK128_MAX_BITS_REQUEST = 1 << (BITS_POWER - 1);

    /**
     * The Cipher.
     */
    private final GordianSymCipher theCipher;

    /**
     * The Entropy Source.
     */
    private final EntropySource theEntropy;

    /**
     * The DateTime vector.
     */
    private final GordianByteArrayInteger theDT;

    /**
     * The ReSeed Counter.
     */
    private final GordianByteArrayInteger theReseedCounter;

    /**
     * The intermediate buffer.
     */
    private final byte[] theI;

    /**
     * The result buffer.
     */
    private final byte[] theR;

    /**
     * The entropy bytes.
     */
    private byte[] theV;

    /**
     * Constructor.
     * @param pCipher source cipher to use for DRB stream.
     * @param pEntropy source of entropy to use for seeding/reSeeding.
     * @param pInitVector nonce to further distinguish this DRBG.
     */
    public GordianX931CipherDRBG(final GordianSymCipher pCipher,
                                 final EntropySource pEntropy,
                                 final byte[] pInitVector) {
        /* Store parameters */
        theCipher = pCipher;
        theEntropy = pEntropy;

        /* Determine the bufferSize */
        final int mySize = getBlockSize();
        final int myLen = mySize >> BIT_SHIFT;

        /* Create DT Buffer */
        theDT = new GordianByteArrayInteger(myLen);
        final int myCopyLen = Math.min(myLen, pInitVector.length);
        System.arraycopy(pInitVector, 0, theDT.getBuffer(), 0, myCopyLen);

        /* Create intermediate buffers */
        theI = new byte[myLen];
        theR = new byte[myLen];

        /* Initialise reSeed counter */
        theReseedCounter = new GordianByteArrayInteger(TethysDataConverter.BYTES_LONG);
        theReseedCounter.iterate();
    }

    @Override
    public int generate(final byte[] pOutput,
                        final byte[] pXtraBytes,
                        final boolean isPredictionResistant) {
        /* Check valid # of bits */
        final int myNumBits = pOutput.length << BIT_SHIFT;
        if (myNumBits > BLOCK128_MAX_BITS_REQUEST) {
            throw new IllegalArgumentException("Number of bits per request limited to "
                    + BLOCK128_MAX_BITS_REQUEST);
        }

        /* Check for reSeed required */
        if (theReseedCounter.compareLimit(BLOCK128_RESEED_MAX)) {
            return -1;
        }

        /* If we are prediction resistant or have not allocated V */
        if (isPredictionResistant
                || theV == null) {
            /* Initialise V from entropy */
            initFromEntropy();
        }

        /* Protect against exceptions */
        try {
            /* Generate the bits */
            final byte[] myResult = cipherGen(myNumBits);

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

    /**
     * Stretch a cipher output to required # of bits.
     * @param pNumBits the number of output bits
     * @return the stretched cipher output
     * @throws OceanusException on error
     */
    private byte[] cipherGen(final int pNumBits) throws OceanusException {
        /* Determine # of iterations */
        final int mySize = getBlockSize() >> BIT_SHIFT;
        final int myLen = pNumBits >> BIT_SHIFT;

        /* Allocate counters */
        final byte[] myOutput = new byte[myLen];

        /* while we need to generate more bytes */
        int myBuilt = 0;
        while (myBuilt < myLen) {
            /* Generate a new block */
            theCipher.finish(theDT.getBuffer(), 0, mySize, theI);
            processBytes(theR, theI, theV);
            processBytes(theV, theR, theI);

            /* Determine how many bytes of this hash should be used */
            int myNeeded = myLen
                    - myBuilt;
            if (myNeeded > mySize) {
                myNeeded = mySize;
            }

            /* Copy bytes across */
            System.arraycopy(theR, 0, myOutput, myBuilt, myNeeded);
            myBuilt += myNeeded;

            /* Iterate the dateTime */
            theDT.iterate();
        }

        /* Return the result */
        return myOutput;
    }

    @Override
    public void reseed(final byte[] pSeed) {
        reseed();
    }

    /**
     * ReSeed the RNG.
     */
    private void reseed() {
        /* Initialise V from entropy */
        initFromEntropy();

        /* re-initialise reSeed counter */
        theReseedCounter.reset();
        theReseedCounter.iterate();
    }

    /**
     * Initialise from entropy.
     */
    private void initFromEntropy() {
        /* Initialise V from entropy */
        theV = theEntropy.getEntropy();
        if (theV.length != theI.length) {
            throw new IllegalStateException("Insufficient entropy returned");
        }
    }

    /**
     * Process bytes.
     * @param pResult the result
     * @param pFirst the first array
     * @param pSecond the second array
     * @throws OceanusException on error
     */
    private void processBytes(final byte[] pResult,
                              final byte[] pFirst,
                              final byte[] pSecond) throws OceanusException {
        /* Combine the two inputs */
        for (int i = 0; i != pResult.length; i++) {
            pResult[i] = (byte) (pFirst[i] ^ pSecond[i]);
        }

        /* Process the block via the cipher */
        theCipher.finish(pResult, 0, pResult.length, pResult);
    }

    @Override
    public int getBlockSize() {
        final GordianSymCipherSpec mySpec = (GordianSymCipherSpec) theCipher.getCipherSpec();
        return mySpec.getBlockLength().getLength();
    }

    @Override
    public String getAlgorithm() {
        return X931_PREFIX + theCipher.getCipherSpec().toString();
    }
}
