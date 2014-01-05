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
package net.sourceforge.joceanus.jgordianknot;

import java.security.SecureRandom;
import java.util.Arrays;

import net.sourceforge.joceanus.jdatamanager.DataConverter;
import net.sourceforge.joceanus.jdatamanager.JDataException;

/**
 * Class for assembling/disassembling Hashes.
 */
public class HashKey {
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
     * The KeyBytes.
     */
    private final byte[] theKeyBytes;

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
     * Obtain the Prime Digest type.
     * @return the digest type
     */
    public DigestType getPrimeDigest() {
        return theParams.getPrimeDigest();
    }

    /**
     * Obtain the Alternate Digest type.
     * @return the digest type
     */
    public DigestType getAlternateDigest() {
        return theParams.getAlternateDigest();
    }

    /**
     * Obtain the Secret Digest type.
     * @return the digest type
     */
    public DigestType getSecretDigest() {
        return theParams.getSecretDigest();
    }

    /**
     * Obtain the Adjustment.
     * @return the adjustment
     */
    public int getAdjustment() {
        return theParams.getAdjustment();
    }

    /**
     * Obtain the Initialisation vector.
     * @return the initialisation vector
     */
    public byte[] getInitVector() {
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
     * Constructor for random choices.
     * @param pGenerator the security generator
     * @throws JDataException on error
     */
    protected HashKey(final SecurityGenerator pGenerator) throws JDataException {
        /* Access the secureRandom */
        SecureRandom myRandom = pGenerator.getRandom();

        /* Create the Random Key */
        theKeyBytes = new byte[KEYBYTES_LEN];

        /* Create the Initialisation vector */
        theInitVector = new byte[INITVECTOR_LEN];
        myRandom.nextBytes(theInitVector);

        /* Allocate new set of parameters */
        theParams = new HashParameters(myRandom);
        theHash = null;
    }

    /**
     * Constructor for external form parse.
     * @param pPassLength the password length
     * @param pExternal the external form
     * @throws JDataException on error
     */
    protected HashKey(final int pPassLength,
                      final byte[] pExternal) throws JDataException {
        /* Determine hash length */
        int myLen = pExternal.length;
        int myHashLen = myLen
                        - KEYBYTES_LEN
                        - INITVECTOR_LEN;

        /* Create the byte arrays */
        theKeyBytes = new byte[KEYBYTES_LEN];
        theInitVector = new byte[INITVECTOR_LEN];
        theHash = new byte[myHashLen];

        /* Determine offset position */
        int myOffSet = Math.max(pPassLength, HASH_MARGIN);
        myOffSet = Math.min(myOffSet, myHashLen
                                      - HASH_MARGIN);

        /* Copy Data into buffers */
        System.arraycopy(pExternal, 0, theKeyBytes, 0, KEYBYTES_LEN);
        System.arraycopy(pExternal, KEYBYTES_LEN, theHash, 0, myOffSet);
        System.arraycopy(pExternal, myOffSet
                                    + KEYBYTES_LEN, theInitVector, 0, INITVECTOR_LEN);
        System.arraycopy(pExternal, myOffSet
                                    + KEYBYTES_LEN
                                    + INITVECTOR_LEN, theHash, myOffSet, myHashLen
                                                                         - myOffSet);

        /* Allocate new set of parameters */
        theParams = new HashParameters();
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
        System.arraycopy(theKeyBytes, 0, myBuffer, 0, KEYBYTES_LEN);
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
    private final class HashParameters {
        /**
         * The Prime Digest type.
         */
        private final DigestType thePrimeDigest;

        /**
         * The Alternate Digest type.
         */
        private final DigestType theAlternateDigest;

        /**
         * The Secret Digest type.
         */
        private final DigestType theSecretDigest;

        /**
         * The Adjustment.
         */
        private final int theAdjust;

        /**
         * Obtain the Prime Digest type.
         * @return the digest type
         */
        public DigestType getPrimeDigest() {
            return thePrimeDigest;
        }

        /**
         * Obtain the Alternate Digest type.
         * @return the digest type
         */
        public DigestType getAlternateDigest() {
            return theAlternateDigest;
        }

        /**
         * Obtain the Secret Digest type.
         * @return the digest type
         */
        public DigestType getSecretDigest() {
            return theSecretDigest;
        }

        /**
         * Obtain the Adjustment.
         * @return the adjustment
         */
        public int getAdjustment() {
            return theAdjust;
        }

        /**
         * Construct the parameters from random.
         * @param pRandom the random generator
         * @throws JDataException on error
         */
        private HashParameters(final SecureRandom pRandom) throws JDataException {
            /* Obtain Digest list */
            DigestType[] myDigest = DigestType.getRandomTypes(NUM_DIGESTS, pRandom);

            /* Store Digest types */
            thePrimeDigest = myDigest[0];
            theAlternateDigest = myDigest[1];
            theSecretDigest = myDigest[2];

            /* Access random adjustment value */
            theAdjust = pRandom.nextInt(DataConverter.NYBBLE_MASK + 1);

            /* Build the key bytes */
            int i = 0;
            theKeyBytes[i++] = (byte) ((thePrimeDigest.getId() << DataConverter.NYBBLE_SHIFT) + theAlternateDigest.getId());
            theKeyBytes[i] = (byte) ((theSecretDigest.getId() << DataConverter.NYBBLE_SHIFT) + theAdjust);
        }

        /**
         * Construct the parameters from key bytes.
         * @throws JDataException on error
         */
        private HashParameters() throws JDataException {
            /* Access prime and alternate digests */
            int i = 0;
            byte myValue = theKeyBytes[i++];
            int myId = (myValue >> DataConverter.NYBBLE_SHIFT)
                       & DataConverter.NYBBLE_MASK;
            thePrimeDigest = DigestType.fromId(myId);
            myId = myValue
                   & DataConverter.NYBBLE_MASK;
            theAlternateDigest = DigestType.fromId(myId);

            /* Access secret and cipher digests */
            myValue = theKeyBytes[i++];
            myId = (myValue >> DataConverter.NYBBLE_SHIFT)
                   & DataConverter.NYBBLE_MASK;
            theSecretDigest = DigestType.fromId(myId);
            theAdjust = myValue
                        & DataConverter.NYBBLE_MASK;
        }
    }
}
