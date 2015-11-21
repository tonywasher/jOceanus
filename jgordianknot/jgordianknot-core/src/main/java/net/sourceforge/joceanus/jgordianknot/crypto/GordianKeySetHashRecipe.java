/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jgordianknot/jgordianknot-core/src/main/java/net/sourceforge/joceanus/jgordianknot/crypto/MacType.java $
 * $Revision: 647 $
 * $Author: Tony $
 * $Date: 2015-11-04 08:58:02 +0000 (Wed, 04 Nov 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto;

import java.security.SecureRandom;
import java.util.Arrays;

import net.sourceforge.joceanus.jtethys.DataConverter;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Class for assembling/disassembling KeySetHashes.
 */
public final class GordianKeySetHashRecipe {
    /**
     * Number of digests.
     */
    private static final int NUM_DIGESTS = 3;

    /**
     * KeyBytes length.
     */
    private static final int KEYBYTES_LEN = 2;

    /**
     * Hash margins.
     */
    private static final int HASH_MARGIN = 4;

    /**
     * InitVector length.
     */
    protected static final int INITVECTOR_LEN = 32;

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
     * @throws JOceanusException on error
     */
    protected GordianKeySetHashRecipe(final GordianFactory pFactory) throws JOceanusException {
        /* Access the secureRandom */
        SecureRandom myRandom = pFactory.getRandom();

        /* Create the Random Key */
        theRecipe = new byte[KEYBYTES_LEN];

        /* Create the Initialisation vector */
        theInitVector = new byte[INITVECTOR_LEN];
        myRandom.nextBytes(theInitVector);

        /* Allocate new set of parameters */
        theParams = new HashParameters(pFactory);
        theParams.buildRecipe(pFactory, theRecipe);
        theHash = null;
    }

    /**
     * Constructor for external form parse.
     * @param pFactory the factory
     * @param pPassLength the password length
     * @param pExternal the external form
     * @throws JOceanusException on error
     */
    protected GordianKeySetHashRecipe(final GordianFactory pFactory,
                                      final int pPassLength,
                                      final byte[] pExternal) throws JOceanusException {
        /* Determine hash length */
        int myLen = pExternal.length;
        int myHashLen = myLen
                        - KEYBYTES_LEN
                        - INITVECTOR_LEN;

        /* Create the byte arrays */
        theRecipe = new byte[KEYBYTES_LEN];
        theInitVector = new byte[INITVECTOR_LEN];
        theHash = new byte[myHashLen];

        /* Determine offset position */
        int myOffSet = Math.max(pPassLength, HASH_MARGIN);
        myOffSet = Math.min(myOffSet, myHashLen
                                      - HASH_MARGIN);

        /* Copy Data into buffers */
        System.arraycopy(pExternal, 0, theRecipe, 0, KEYBYTES_LEN);
        System.arraycopy(pExternal, KEYBYTES_LEN, theHash, 0, myOffSet);
        System.arraycopy(pExternal, myOffSet
                                    + KEYBYTES_LEN, theInitVector, 0, INITVECTOR_LEN);
        System.arraycopy(pExternal, myOffSet
                                    + KEYBYTES_LEN
                                    + INITVECTOR_LEN, theHash, myOffSet, myHashLen
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
        return (theInitVector == null)
                                       ? null
                                       : Arrays.copyOf(theInitVector, theInitVector.length);
    }

    /**
     * Obtain the Hash.
     * @return the Hash
     */
    public byte[] getHash() {
        return (theHash == null)
                                 ? null
                                 : Arrays.copyOf(theHash, theHash.length);
    }

    /**
     * Build External Format for hash and password length.
     * @param pPassLength the password length
     * @param pHash the calculated hash
     * @return the external form
     */
    public byte[] buildExternal(final int pPassLength,
                                final byte[] pHash) {
        /* Allocate the new buffer */
        int myHashLen = pHash.length;
        int myLen = KEYBYTES_LEN
                    + INITVECTOR_LEN
                    + myHashLen;
        byte[] myBuffer = new byte[myLen];

        /* Determine offset position */
        int myOffSet = Math.max(pPassLength, HASH_MARGIN);
        myOffSet = Math.min(myOffSet, myHashLen
                                      - HASH_MARGIN);

        /* Copy Data into buffer */
        System.arraycopy(theRecipe, 0, myBuffer, 0, KEYBYTES_LEN);
        System.arraycopy(pHash, 0, myBuffer, KEYBYTES_LEN, myOffSet);
        System.arraycopy(theInitVector, 0, myBuffer, myOffSet
                                                     + KEYBYTES_LEN, INITVECTOR_LEN);
        System.arraycopy(pHash, myOffSet, myBuffer, myOffSet
                                                    + KEYBYTES_LEN
                                                    + INITVECTOR_LEN, myHashLen
                                                                      - myOffSet);

        /* return the external format */
        return myBuffer;
    }

    /**
     * The parameters class.
     */
    private static final class HashParameters {
        /**
         * The Prime Digest type.
         */
        private final GordianDigestType thePrimeDigest;

        /**
         * The Alternate Digest type.
         */
        private final GordianDigestType theAlternateDigest;

        /**
         * The Secret Digest type.
         */
        private final GordianDigestType theSecretDigest;

        /**
         * The Adjustment.
         */
        private final int theAdjust;

        /**
         * Construct the parameters from random.
         * @param pFactory the factory
         * @throws JOceanusException on error
         */
        private HashParameters(final GordianFactory pFactory) throws JOceanusException {
            /* Obtain Digest list */
            GordianIdManager myManager = pFactory.getIdManager();
            GordianDigestType[] myDigests = myManager.generateRandomDigestTypes(NUM_DIGESTS, pFactory.supportedDigests());

            /* Store Digest types */
            thePrimeDigest = myDigests[0];
            theAlternateDigest = myDigests[1];
            theSecretDigest = myDigests[2];

            /* Access random adjustment value */
            SecureRandom myRandom = pFactory.getRandom();
            theAdjust = myRandom.nextInt(DataConverter.NYBBLE_MASK + 1);
        }

        /**
         * Construct the parameters from recipe.
         * @param pFactory the factory
         * @param pRecipe the recipe bytes
         * @throws JOceanusException on error
         */
        private HashParameters(final GordianFactory pFactory,
                               final byte[] pRecipe) throws JOceanusException {
            /* Obtain Id manager */
            GordianIdManager myManager = pFactory.getIdManager();

            /* Access prime and alternate digests */
            int i = 0;
            byte myValue = pRecipe[i++];
            int myId = (myValue >> DataConverter.NYBBLE_SHIFT)
                       & DataConverter.NYBBLE_MASK;
            thePrimeDigest = myManager.deriveDigestTypeFromExternalId(myId);
            myId = myValue
                   & DataConverter.NYBBLE_MASK;
            theAlternateDigest = myManager.deriveDigestTypeFromExternalId(myId);

            /* Access secret and cipher digests */
            myValue = pRecipe[i];
            myId = (myValue >> DataConverter.NYBBLE_SHIFT)
                   & DataConverter.NYBBLE_MASK;
            theSecretDigest = myManager.deriveDigestTypeFromExternalId(myId);
            theAdjust = myValue
                        & DataConverter.NYBBLE_MASK;
        }

        /**
         * Construct the external recipe.
         * @param pFactory the factory
         * @param pRecipe the recipe bytes to build
         * @throws JOceanusException on error
         */
        private void buildRecipe(final GordianFactory pFactory,
                                 final byte[] pRecipe) throws JOceanusException {
            /* Obtain Id manager */
            GordianIdManager myManager = pFactory.getIdManager();

            /* Build the recipe */
            int i = 0;
            pRecipe[i++] = (byte) ((myManager.deriveExternalIdFromDigestType(thePrimeDigest) << DataConverter.NYBBLE_SHIFT)
                                   + myManager.deriveExternalIdFromDigestType(theAlternateDigest));
            pRecipe[i] = (byte) ((myManager.deriveExternalIdFromDigestType(theSecretDigest) << DataConverter.NYBBLE_SHIFT) + theAdjust);
        }

        /**
         * Obtain the Prime Digest type.
         * @return the digest type
         */
        public GordianDigestType getPrimeDigest() {
            return thePrimeDigest;
        }

        /**
         * Obtain the Alternate Digest type.
         * @return the digest type
         */
        public GordianDigestType getAlternateDigest() {
            return theAlternateDigest;
        }

        /**
         * Obtain the Secret Digest type.
         * @return the digest type
         */
        public GordianDigestType getSecretDigest() {
            return theSecretDigest;
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
