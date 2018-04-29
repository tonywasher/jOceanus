/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto.prng;

import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianByteArrayInteger;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigest;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Implementation of HashSP800DRBG based on the BouncyCastle Code.
 * <p>
 * This implementation is modified so that it accepts any GordianDigest.
 */
public final class GordianSP800HashDRBG
        implements GordianDRBGenerator {
    /**
     * The SP800 prefix.
     */
    static final String SP800_PREFIX = "SP800-";

    /**
     * The bit shift.
     */
    private static final int BIT_SHIFT = 3;

    /**
     * The Initial Seed Id.
     */
    private static final byte[] INIT_ID =
    { 0 };

    /**
     * The ReSeed Id.
     */
    private static final byte[] RESEED_ID =
    { 1 };

    /**
     * The Extra bytes Id.
     */
    private static final byte[] XTRA_ID =
    { 2 };

    /**
     * The ReHash Id.
     */
    private static final byte[] REHASH_ID =
    { 3 };

    /**
     * The Seed length.
     */
    private static final int SEED_LENGTH = 880;

    /**
     * The Message Digest.
     */
    private final GordianDigest theDigest;

    /**
     * The Entropy Source.
     */
    private final EntropySource theEntropy;

    /**
     * The ReSeed Counter.
     */
    private final GordianByteArrayInteger theReseedCounter;

    /**
     * The Variable Hash.
     */
    private GordianByteArrayInteger theV;

    /**
     * The Constant Hash.
     */
    private GordianByteArrayInteger theC;

    /**
     * Construct a SP800-90A Hash DRBG.
     * @param pDigest source digest to use for DRB stream.
     * @param pEntropy source of entropy to use for seeding/reSeeding.
     * @param pSecurityBytes personalisation string to distinguish this DRBG (may be null).
     * @param pInitVector nonce to further distinguish this DRBG (may be null).
     */
    protected GordianSP800HashDRBG(final GordianDigest pDigest,
                                   final EntropySource pEntropy,
                                   final byte[] pSecurityBytes,
                                   final byte[] pInitVector) {
        /* Store digest and entropy source */
        theDigest = pDigest;
        theEntropy = pEntropy;

        /* Create variable Hash */
        final byte[] myEntropy = theEntropy.getEntropy();
        final byte[] mySeed = Arrays.concatenate(myEntropy, pInitVector, pSecurityBytes);
        theV = hashDerive(mySeed, SEED_LENGTH);

        /* Create constant hash */
        final byte[] myTempH = Arrays.concatenate(INIT_ID, theV.getBuffer());
        theC = hashDerive(myTempH, SEED_LENGTH);

        /* Initialise reSeed counter */
        theReseedCounter = new GordianByteArrayInteger(TethysDataConverter.BYTES_LONG);
        theReseedCounter.iterate();
    }

    @Override
    public void reseed(final byte[] pXtraBytes) {
        /* Create variable Hash */
        final byte[] myEntropy = theEntropy.getEntropy();
        final byte[] mySeed = Arrays.concatenate(RESEED_ID, theV.getBuffer(), myEntropy, pXtraBytes);
        theV = hashDerive(mySeed, SEED_LENGTH);

        /* Create constant hash */
        final byte[] myTempH = Arrays.concatenate(INIT_ID, theV.getBuffer());
        theC = hashDerive(myTempH, SEED_LENGTH);

        /* re-initialise reSeed counter */
        theReseedCounter.reset();
        theReseedCounter.iterate();
    }

    @Override
    public int generate(final byte[] pOutput,
                        final byte[] pXtraBytes,
                        final boolean isPredictionResistant) {
        /* Check valid # of bits */
        final int myNumBits = pOutput.length << BIT_SHIFT;
        if (myNumBits > GordianRandomFactory.MAX_BITS_REQUEST) {
            throw new IllegalArgumentException("Number of bits per request limited to "
                                               + GordianRandomFactory.MAX_BITS_REQUEST);
        }

        /* Check for reSeed required */
        if (theReseedCounter.compareLimit(GordianRandomFactory.RESEED_MAX)) {
            return -1;
        }

        /* If we are prediction resistant */
        if (isPredictionResistant) {
            /* ReSeed */
            reseed(pXtraBytes);

            /* else if we have extra bytes */
        } else if (pXtraBytes != null) {
            /* Hash the new input and add to variable hash */
            final byte[] newInput = Arrays.concatenate(XTRA_ID, theV.getBuffer(), pXtraBytes);
            theV.addTo(theDigest.finish(newInput));
        }

        /* Generate the requested bits */
        final byte[] myResult = hashgen(theV.getBuffer(), myNumBits);

        /* Adjust the variable hash */
        final byte[] myTempH = Arrays.concatenate(REHASH_ID, theV.getBuffer());

        /* Add the hash and constant */
        theV.addTo(theDigest.finish(myTempH));
        theV.addTo(theC.getBuffer());

        /* Add the reSeed counter */
        theV.addTo(theReseedCounter.getBuffer());

        /* Iterate the reSeed counter */
        theReseedCounter.iterate();

        /* Return the bytes */
        System.arraycopy(myResult, 0, pOutput, 0, pOutput.length);

        /* Return the number of bits generated */
        return myNumBits;
    }

    /**
     * Stretch a hash output to required # of bits.
     * @param pInputBytes the input bytes to hash
     * @param pNumBits the number of output bits
     * @return the stretched hash
     */
    private byte[] hashgen(final byte[] pInputBytes,
                           final int pNumBits) {
        /* Determine # of iterations */
        final int mySize = theDigest.getDigestSize();
        final int myLen = pNumBits >> BIT_SHIFT;

        /* Allocate counters */
        final GordianByteArrayInteger myData = new GordianByteArrayInteger(pInputBytes);
        final byte[] myOutput = new byte[myLen];

        /* while we need to generate more bytes */
        int myBuilt = 0;
        while (myBuilt < myLen) {
            /* Calculate the digest */
            final byte[] myDigest = theDigest.finish(myData.getBuffer());

            /* Determine how many bytes of this hash should be used */
            int myNeeded = myLen
                           - myBuilt;
            if (myNeeded > mySize) {
                myNeeded = mySize;
            }

            /* Copy bytes across */
            System.arraycopy(myDigest, 0, myOutput, myBuilt, myNeeded);
            myBuilt += myNeeded;

            /* Iterate the data */
            myData.iterate();
        }

        /* Return the result */
        return myOutput;
    }

    /**
     * Hash derivation function (hash_df).
     * @param pSeedMaterial the seed material
     * @param pSeedLength the length of seed required
     * @return the new hash as a counter
     */
    private GordianByteArrayInteger hashDerive(final byte[] pSeedMaterial,
                                               final int pSeedLength) {
        /* Determine sizes */
        final int mySize = theDigest.getDigestSize();
        final int myLen = pSeedLength >> BIT_SHIFT;
        byte myCount = 1;

        /* Create output buffer */
        final byte[] myOutput = new byte[myLen];

        /* Create seed array */
        final byte[] mySeed = new byte[TethysDataConverter.BYTES_INTEGER];
        int mySeedLength = pSeedLength;
        for (int i = mySeed.length - 1; i >= 0; i--) {
            mySeed[i] = (byte) mySeedLength;
            mySeedLength >>= TethysDataConverter.BYTE_SHIFT;
        }

        /* while we need to generate more bytes */
        int myBuilt = 0;
        while (myBuilt < myLen) {
            /* Update with the count */
            theDigest.update(myCount);

            /* Update with the seed length */
            theDigest.update(mySeed);

            /* Create digest with the seed material */
            final byte[] myDigest = theDigest.finish(pSeedMaterial);

            /* Determine how many bytes of this hash should be used */
            int myNeeded = myLen
                           - myBuilt;
            if (myNeeded > mySize) {
                myNeeded = mySize;
            }

            /* Copy bytes across */
            System.arraycopy(myDigest, 0, myOutput, myBuilt, myNeeded);
            myBuilt += myNeeded;

            /* Iterate the counter */
            myCount++;
        }

        /* Return byte array counter */
        return new GordianByteArrayInteger(myOutput);
    }

    @Override
    public int getBlockSize() {
        return theDigest.getDigestSize();
    }

    @Override
    public String getAlgorithm() {
        return SP800_PREFIX + theDigest.getDigestSpec().toString();
    }
}
