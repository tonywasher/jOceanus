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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot;

import java.security.SecureRandom;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * DataDigest types. Available algorithms.
 */
public enum DigestType {
    /**
     * SHA2.
     */
    SHA2(1),

    /**
     * Tiger.
     */
    TIGER(2),

    /**
     * WhirlPool.
     */
    WHIRLPOOL(3),

    /**
     * RIPEMD.
     */
    RIPEMD(4),

    /**
     * GOST.
     */
    GOST(5),

    /**
     * SHA3.
     */
    SHA3(6),

    /**
     * Skein.
     */
    SKEIN(7);

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(DigestType.class.getName());

    /**
     * Invalid number of digests error text.
     */
    private static final String ERROR_NUMTYPES = "Invalid number of Digests: ";
    /**
     * The external Id of the algorithm.
     */
    private int theId = 0;

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = NLS_BUNDLE.getString(name());
        }

        /* return the name */
        return theName;
    }

    /**
     * Obtain the external Id.
     * @return the external Id
     */
    public int getId() {
        return theId;
    }

    /**
     * Constructor.
     * @param id the id
     */
    private DigestType(final int id) {
        theId = id;
    }

    /**
     * get value from id.
     * @param id the id value
     * @return the corresponding enumeration object
     * @throws JOceanusException on error
     */
    public static DigestType fromId(final int id) throws JOceanusException {
        for (DigestType myType : values()) {
            if (myType.getId() == id) {
                return myType;
            }
        }
        throw new JOceanusException("Invalid DigestType: "
                                    + id);
    }

    /**
     * Return the associated algorithm.
     * @param bLong use long hash
     * @return the algorithm
     */
    protected String getAlgorithm(final boolean bLong) {
        switch (this) {
            case SKEIN:
                return (bLong)
                        ? "SKEIN-512-512"
                        : "SKEIN-256-256";
            case SHA3:
                return (bLong)
                        ? "SHA3-512"
                        : "SHA3-256";
            case SHA2:
                return (bLong)
                        ? "SHA512"
                        : "SHA-256";
            case RIPEMD:
                return "RIPEMD320";
            case GOST:
                return "GOST3411";
            default:
                return name();
        }
    }

    /**
     * Return the associated HMac algorithm.
     * @param bLong use long hash
     * @return the algorithm
     */
    protected String getMacAlgorithm(final boolean bLong) {
        return "HMac"
               + getAlgorithm(bLong);
    }

    /**
     * Get random unique set of digest types.
     * @param pNumTypes the number of types
     * @param pRandom the random generator
     * @return the random set
     * @throws JOceanusException on error
     */
    public static DigestType[] getRandomTypes(final int pNumTypes,
                                              final SecureRandom pRandom) throws JOceanusException {
        /* Access the values */
        DigestType[] myValues = values();
        int iNumValues = myValues.length;

        /* Reject call if invalid number of types */
        if ((pNumTypes < 1)
            || (pNumTypes > iNumValues)) {
            throw new JOceanusException(ERROR_NUMTYPES
                                        + pNumTypes);
        }

        /* Create the result set */
        DigestType[] myTypes = new DigestType[pNumTypes];

        /* Loop through the types */
        for (int i = 0; i < pNumTypes; i++) {
            /* Access the next random index */
            int iIndex = pRandom.nextInt(iNumValues);

            /* Store the type */
            myTypes[i] = myValues[iIndex];

            /* Shift last value down in place of the one thats been used */
            myValues[iIndex] = myValues[iNumValues - 1];
            iNumValues--;
        }

        /* Return the types */
        return myTypes;
    }

    /**
     * Get keyed unique set of digest types.
     * @param pNumTypes the number of types
     * @param pKeySeed the seed to determine digest
     * @return the random set
     * @throws JOceanusException on error
     */
    public static DigestType[] getSeedTypes(final int pNumTypes,
                                            final long pKeySeed) throws JOceanusException {
        /* Access the values */
        DigestType[] myValues = values();
        long myKeySeed = pKeySeed;
        int iNumValues = myValues.length;

        /* Reject call if invalid number of types */
        if ((pNumTypes < 1)
            || (pNumTypes > iNumValues)) {
            throw new JOceanusException(ERROR_NUMTYPES
                                        + pNumTypes);
        }

        /* Make sure that seed is positive */
        if (myKeySeed < 0) {
            myKeySeed = -myKeySeed;
        }

        /* Create the result set */
        DigestType[] myTypes = new DigestType[pNumTypes];

        /* Loop through the types */
        for (int i = 0; i < pNumTypes; i++) {
            /* Access the next random index */
            int iIndex = (int) (myKeySeed % iNumValues);
            myKeySeed /= iNumValues;

            /* Store the type */
            myTypes[i] = myValues[iIndex];

            /* Shift last value down in place of the one thats been used */
            myValues[iIndex] = myValues[iNumValues - 1];
            iNumValues--;
        }

        /* Return the types */
        return myTypes;
    }
}
