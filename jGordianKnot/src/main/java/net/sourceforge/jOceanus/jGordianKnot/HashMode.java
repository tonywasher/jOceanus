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

import java.security.SecureRandom;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;

/**
 * Hash Mode. Encapsulates PasswordHash options.
 * @author Tony Washer
 */
public class HashMode
        extends SecurityMode {
    /**
     * Number of digests.
     */
    private static final int NUM_DIGESTS = 3;

    /**
     * Maximum iteration.
     */
    private static final int MAX_ITERATIONS = 15;

    /**
     * The locations (in nybbles).
     */
    private static final int PLACE_PRIMETYPE = 0;

    /**
     * The locations (in nybbles).
     */
    private static final int PLACE_ALTTYPE = 1;

    /**
     * The locations (in nybbles).
     */
    private static final int PLACE_SECRETTYPE = 2;

    /**
     * The locations (in nybbles).
     */
    private static final int PLACE_CIPHERTYPE = 3;

    /**
     * The locations (in nybbles).
     */
    private static final int PLACE_ADJUST = 4;

    /**
     * The various versions.
     */
    private static final short VERSION_CURRENT = 1;

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
     * The Cipher Digest type.
     */
    private final DigestType theCipherDigest;

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
     * Obtain the Cipher Digest type.
     * @return the digest type
     */
    public DigestType getCipherDigest() {
        return theCipherDigest;
    }

    /**
     * Obtain the Adjustment.
     * @return the adjustment
     */
    public int getAdjustment() {
        return theAdjust;
    }

    /**
     * Constructor at random.
     * @param useRestricted use restricted keys
     * @param pRandom the random generator
     * @throws JDataException on error
     */
    protected HashMode(final boolean useRestricted,
                       final SecureRandom pRandom) throws JDataException {
        /* Access a random set of DigestTypes */
        DigestType[] myDigest = DigestType.getRandomTypes(NUM_DIGESTS, pRandom);
        DigestType[] mySetDigest = DigestType.getRandomTypes(1, pRandom);

        /* Store Digest types */
        thePrimeDigest = myDigest[0];
        theAlternateDigest = myDigest[1];
        theSecretDigest = myDigest[2];
        theCipherDigest = mySetDigest[0];

        /* Access random adjustment value */
        theAdjust = 1 + pRandom.nextInt(MAX_ITERATIONS);

        /* Set the version */
        setVersion(VERSION_CURRENT);

        /* Set restricted flags */
        setRestricted(useRestricted);

        /* encode the keyMode */
        encodeKeyMode();
    }

    /**
     * Constructor from encoded format.
     * @param pEncoded the encoded format
     * @throws JDataException on error
     */
    protected HashMode(final byte[] pEncoded) throws JDataException {
        /* Set the initial encoded version */
        setEncoded(pEncoded);

        /* Not allowed unless version is current */
        if (getVersion() != VERSION_CURRENT) {
            throw new JDataException(ExceptionClass.LOGIC, "Invalid mode version: "
                                                           + getVersion());
        }

        /* Store Key type and digest */
        thePrimeDigest = DigestType.fromId(getDataValue(PLACE_PRIMETYPE));
        theAlternateDigest = DigestType.fromId(getDataValue(PLACE_ALTTYPE));
        theSecretDigest = DigestType.fromId(getDataValue(PLACE_SECRETTYPE));
        theCipherDigest = DigestType.fromId(getDataValue(PLACE_CIPHERTYPE));

        /* Access the adjustment */
        theAdjust = getDataValue(PLACE_ADJUST);

        /* Re-encode the key mode */
        encodeKeyMode();
    }

    /**
     * Encode the key mode.
     */
    private void encodeKeyMode() {
        /* Allocate the encoded array */
        allocateEncoded(PLACE_ADJUST);

        /* Set the values */
        setDataValue(PLACE_PRIMETYPE, thePrimeDigest.getId());
        setDataValue(PLACE_ALTTYPE, theAlternateDigest.getId());
        setDataValue(PLACE_SECRETTYPE, theSecretDigest.getId());
        setDataValue(PLACE_CIPHERTYPE, theCipherDigest.getId());
        setDataValue(PLACE_ADJUST, theAdjust);
    }
}
