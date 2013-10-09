/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2013 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.jOceanus.jGordianKnot;

import java.security.MessageDigest;

import net.sourceforge.jOceanus.jDataManager.DataConverter;

import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.drbg.SP80090DRBG;
import org.bouncycastle.util.Arrays;

/**
 * Implementation of HashSP800DRBG based on the BouncyCastle Code.
 * <p>
 * This implementation is modified so that it accepts any JCE Digest.
 */
public class HashSP800DRBG
        implements SP80090DRBG {
    /**
     * The bit shift.
     */
    private static final int BIT_SHIFT = 3;

    /**
     * The Initial Seed Id.
     */
    private static final byte[] INIT_ID = { 0 };

    /**
     * The ReSeed Id.
     */
    private static final byte[] RESEED_ID = { 1 };

    /**
     * The Extra bytes Id.
     */
    private static final byte[] XTRA_ID = { 2 };

    /**
     * The ReHash Id.
     */
    private static final byte[] REHASH_ID = { 3 };

    /**
     * The Seed length.
     */
    private static final int SEED_LENGTH = 880;

    /**
     * The Message Digest.
     */
    private final MessageDigest theDigest;

    /**
     * The Entropy Source.
     */
    private final EntropySource theEntropy;

    /**
     * The Variable Hash.
     */
    private ByteArrayInteger theV;

    /**
     * The Constant Hash.
     */
    private ByteArrayInteger theC;

    /**
     * The ReSeed Counter.
     */
    private ByteArrayInteger theReseedCounter;

    /**
     * Construct a SP800-90A Hash DRBG.
     * @param pDigest source digest to use for DRB stream.
     * @param pEntropy source of entropy to use for seeding/reSeeding.
     * @param pSecurityBytes personalisation string to distinguish this DRBG (may be null).
     * @param pInitVector nonce to further distinguish this DRBG (may be null).
     */
    protected HashSP800DRBG(final MessageDigest pDigest,
                            final EntropySource pEntropy,
                            final byte[] pSecurityBytes,
                            final byte[] pInitVector) {
        /* Store digest and entropy source */
        theDigest = pDigest;
        theEntropy = pEntropy;

        /* Create variable Hash */
        byte[] myEntropy = theEntropy.getEntropy();
        byte[] mySeed = Arrays.concatenate(myEntropy, pInitVector, pSecurityBytes);
        theV = hashDerive(mySeed, SEED_LENGTH);

        /* Create constant hash */
        byte[] myTempH = Arrays.concatenate(INIT_ID, theV.getBuffer());
        theC = hashDerive(myTempH, SEED_LENGTH);

        /* Initialise reSeed counter */
        theReseedCounter = new ByteArrayInteger(DataConverter.BYTES_LONG);
        theReseedCounter.iterate();
    }

    @Override
    public void reseed(final byte[] pXtraBytes) {
        /* Create variable Hash */
        byte[] myEntropy = theEntropy.getEntropy();
        byte[] mySeed = Arrays.concatenate(RESEED_ID, theV.getBuffer(), myEntropy, pXtraBytes);
        theV = hashDerive(mySeed, SEED_LENGTH);

        /* Create constant hash */
        byte[] myTempH = Arrays.concatenate(INIT_ID, theV.getBuffer());
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
        int myNumBits = pOutput.length << BIT_SHIFT;
        if (myNumBits > SP800SecureRandomBuilder.MAX_BITS_REQUEST) {
            throw new IllegalArgumentException("Number of bits per request limited to "
                                               + SP800SecureRandomBuilder.MAX_BITS_REQUEST);
        }

        /* Check for reSeed required */
        if (theReseedCounter.compareLimit(SP800SecureRandomBuilder.RESEED_MAX)) {
            return -1;
        }

        /* If we are prediction resistant */
        if (isPredictionResistant) {
            /* ReSeed */
            reseed(pXtraBytes);

            /* else if we have extra bytes */
        } else if (pXtraBytes != null) {
            /* Hash the new input and add to variable hash */
            byte[] newInput = Arrays.concatenate(XTRA_ID, theV.getBuffer(), pXtraBytes);
            theV.addTo(theDigest.digest(newInput));
        }

        /* Generate the requested bits */
        byte[] myResult = hashgen(theV.getBuffer(), myNumBits);

        /* Adjust the variable hash */
        byte[] myTempH = Arrays.concatenate(REHASH_ID, theV.getBuffer());

        /* Add the hash and constant */
        theV.addTo(theDigest.digest(myTempH));
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
        int mySize = theDigest.getDigestLength();
        int myLen = (pNumBits >> BIT_SHIFT);

        /* Allocate counters */
        ByteArrayInteger myData = new ByteArrayInteger(pInputBytes);
        byte[] myOutput = new byte[myLen];

        /* while we need to generate more bytes */
        int myBuilt = 0;
        while (myBuilt < myLen) {
            /* Calculate the digest */
            byte[] myDigest = theDigest.digest(myData.getBuffer());

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
    private ByteArrayInteger hashDerive(final byte[] pSeedMaterial,
                                        final int pSeedLength) {
        /* Determine sizes */
        int mySize = theDigest.getDigestLength();
        int myLen = pSeedLength >> BIT_SHIFT;
        byte myCount = 1;

        /* Create output buffer */
        byte[] myOutput = new byte[myLen];

        /* Create seed array */
        byte[] mySeed = new byte[DataConverter.BYTES_INTEGER];
        int mySeedLength = pSeedLength;
        for (int i = mySeed.length - 1; i >= 0; i--) {
            mySeed[i] = (byte) mySeedLength;
            mySeedLength >>= DataConverter.BYTE_SHIFT;
        }

        /* while we need to generate more bytes */
        int myBuilt = 0;
        while (myBuilt < myLen) {
            /* Update with the count */
            theDigest.update(myCount);

            /* Update with the seed length */
            theDigest.update(mySeed);

            /* Create digest with the seed material */
            byte[] myDigest = theDigest.digest(pSeedMaterial);

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
        return new ByteArrayInteger(myOutput);
    }
}
