/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto;

import java.security.SecureRandom;
import java.util.Arrays;

import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Class for assembling/disassembling KeySetHashes.
 */
public final class GordianKeySetHashRecipe {
    /**
     * Number of digests.
     */
    private static final int NUM_DIGESTS = 3;

    /**
     * Recipe length (Integer).
     */
    private static final int RECIPELEN = Integer.BYTES;

    /**
     * Hash margins.
     */
    private static final int HASH_MARGIN = 4;

    /**
     * InitVector length.
     */
    protected static final int INITVECTORLEN = 32;

    /**
     * The Recipe.
     */
    private final byte[] theRecipe;

    /**
     * The Initialisation Vector.
     */
    private final byte[] theInitVector;

    /**
     * The Hash.
     */
    private final byte[] theHash;

    /**
     * The Hash Parameters.
     */
    private final HashParameters theParams;

    /**
     * Constructor for random choices.
     * @param pFactory the factory
     */
    protected GordianKeySetHashRecipe(final GordianFactory pFactory) {
        /* Access the secureRandom */
        final SecureRandom myRandom = pFactory.getRandom();

        /* Create the Initialisation vector */
        theInitVector = new byte[INITVECTORLEN];
        myRandom.nextBytes(theInitVector);

        /* Allocate new set of parameters */
        theParams = new HashParameters(pFactory);
        theRecipe = theParams.getRecipe();
        theHash = null;
    }

    /**
     * Constructor for external form parse.
     * @param pFactory the factory
     * @param pPassLength the password length
     * @param pExternal the external form
     * 
     */
    protected GordianKeySetHashRecipe(final GordianFactory pFactory,
                                      final int pPassLength,
                                      final byte[] pExternal) {
        /* Determine hash length */
        final int myLen = pExternal.length;
        final int myHashLen = myLen
                              - RECIPELEN
                              - INITVECTORLEN;

        /* Create the byte arrays */
        theRecipe = new byte[RECIPELEN];
        theInitVector = new byte[INITVECTORLEN];
        theHash = new byte[myHashLen];

        /* Determine offset position */
        int myOffSet = Math.max(pPassLength, HASH_MARGIN);
        myOffSet = Math.min(myOffSet, myHashLen
                                      - HASH_MARGIN);

        /* Copy Data into buffers */
        System.arraycopy(pExternal, 0, theHash, 0, myOffSet);
        System.arraycopy(pExternal, myOffSet, theRecipe, 0, RECIPELEN);
        System.arraycopy(pExternal, myOffSet
                                    + RECIPELEN, theInitVector, 0, INITVECTORLEN);
        System.arraycopy(pExternal, myOffSet
                                    + RECIPELEN
                                    + INITVECTORLEN, theHash, myOffSet, myHashLen
                                                                        - myOffSet);

        /* Allocate new set of parameters */
        theParams = new HashParameters(pFactory, theRecipe);
    }

    /**
     * Obtain the Prime Digest type.
     * @return the digest type
     */
    protected GordianDigestType getPrimeDigest() {
        return theParams.getPrimeDigest();
    }

    /**
     * Obtain the Alternate Digest type.
     * @return the digest type
     */
    protected GordianDigestType getAlternateDigest() {
        return theParams.getAlternateDigest();
    }

    /**
     * Obtain the Secret Digest type.
     * @return the digest type
     */
    protected GordianDigestType getSecretDigest() {
        return theParams.getSecretDigest();
    }

    /**
     * Obtain the Adjustment.
     * @return the adjustment
     */
    protected int getAdjustment() {
        return theParams.getAdjustment();
    }

    /**
     * Obtain the Initialisation vector.
     * @return the initialisation vector
     */
    protected byte[] getInitVector() {
        return theInitVector == null
                                     ? null
                                     : Arrays.copyOf(theInitVector, theInitVector.length);
    }

    /**
     * Obtain the Hash.
     * @return the Hash
     */
    public byte[] getHash() {
        return theHash == null
                               ? null
                               : Arrays.copyOf(theHash, theHash.length);
    }

    /**
     * Build External Format for hash and password length.
     * @param pPassLength the password length
     * @param pHash the calculated hash
     * @return the external form
     */
    protected byte[] buildExternal(final int pPassLength,
                                   final byte[] pHash) {
        /* Allocate the new buffer */
        final int myHashLen = pHash.length;
        final int myLen = RECIPELEN
                          + INITVECTORLEN
                          + myHashLen;
        final byte[] myBuffer = new byte[myLen];

        /* Determine offset position */
        int myOffSet = Math.max(pPassLength, HASH_MARGIN);
        myOffSet = Math.min(myOffSet, myHashLen
                                      - HASH_MARGIN);

        /* Copy Data into buffer */
        System.arraycopy(pHash, 0, myBuffer, 0, myOffSet);
        System.arraycopy(theRecipe, 0, myBuffer, myOffSet, RECIPELEN);
        System.arraycopy(theInitVector, 0, myBuffer, myOffSet
                                                     + RECIPELEN, INITVECTORLEN);
        System.arraycopy(pHash, myOffSet, myBuffer, myOffSet
                                                    + RECIPELEN
                                                    + INITVECTORLEN, myHashLen
                                                                     - myOffSet);

        /* return the external format */
        return myBuffer;
    }

    /**
     * The parameters class.
     */
    private static final class HashParameters {
        /**
         * The Recipe.
         */
        private final byte[] theRecipe;

        /**
         * The Digest types.
         */
        private final GordianDigestType[] theDigests;

        /**
         * The Adjustment.
         */
        private final int theAdjust;

        /**
         * Construct the parameters from random.
         * @param pFactory the factory
         */
        private HashParameters(final GordianFactory pFactory) {
            /* Obtain Id manager and random */
            final GordianIdManager myManager = pFactory.getIdManager();
            final SecureRandom myRandom = pFactory.getRandom();

            /* Generate recipe and derive digestTypes */
            int mySeed = myRandom.nextInt();
            theRecipe = TethysDataConverter.integerToByteArray(mySeed);
            theDigests = new GordianDigestType[NUM_DIGESTS];
            mySeed = myManager.deriveKeyHashDigestTypesFromSeed(mySeed, theDigests);

            /* Derive random adjustment value */
            theAdjust = mySeed & TethysDataConverter.NYBBLE_MASK;
        }

        /**
         * Construct the parameters from recipe.
         * @param pFactory the factory
         * @param pRecipe the recipe bytes
         */
        private HashParameters(final GordianFactory pFactory,
                               final byte[] pRecipe) {
            /* Obtain Id manager */
            final GordianIdManager myManager = pFactory.getIdManager();

            /* Store recipe and derive symKeyTypes */
            theRecipe = pRecipe;
            int mySeed = TethysDataConverter.byteArrayToInteger(theRecipe);
            theDigests = new GordianDigestType[NUM_DIGESTS];
            mySeed = myManager.deriveKeyHashDigestTypesFromSeed(mySeed, theDigests);

            /* Derive random adjustment value */
            theAdjust = mySeed & TethysDataConverter.NYBBLE_MASK;
        }

        /**
         * Obtain the Recipe.
         * @return the recipe
         */
        private byte[] getRecipe() {
            return theRecipe;
        }

        /**
         * Obtain the Prime Digest type.
         * @return the digest type
         */
        public GordianDigestType getPrimeDigest() {
            return theDigests[0];
        }

        /**
         * Obtain the Alternate Digest type.
         * @return the digest type
         */
        public GordianDigestType getAlternateDigest() {
            return theDigests[1];
        }

        /**
         * Obtain the Secret Digest type.
         * @return the digest type
         */
        public GordianDigestType getSecretDigest() {
            return theDigests[2];
        }

        /**
         * Obtain the Adjustment.
         * @return the adjustment
         */
        public int getAdjustment() {
            return theAdjust;
        }
    }
}
