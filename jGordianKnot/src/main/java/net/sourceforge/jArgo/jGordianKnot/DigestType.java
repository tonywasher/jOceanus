/*******************************************************************************
 * JGordianKnot: Security Suite
 * Copyright 2012 Tony Washer
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
package net.sourceforge.jArgo.jGordianKnot;

import java.security.SecureRandom;

import net.sourceforge.jArgo.jDataManager.JDataException;
import net.sourceforge.jArgo.jDataManager.JDataException.ExceptionClass;

/**
 * Digest types. Available algorithms.
 * @author Tony Washer
 */
public enum DigestType {
    /**
     * SHA256.
     */
    SHA256(1, 256),

    /**
     * Tiger.
     */
    Tiger(2, 192),

    /**
     * WhirlPool.
     */
    WHIRLPOOL(3, 512),

    /**
     * RIPEMD.
     */
    RIPEMD(4, 320),

    /**
     * GOST.
     */
    GOST(5, 256),

    /**
     * SHA512.
     */
    SHA512(6, 512);

    /**
     * The external Id of the algorithm.
     */
    private int theId = 0;

    /**
     * The length of the hash.
     */
    private int theHashLen = 0;

    /**
     * Obtain the external Id.
     * @return the external Id
     */
    public int getId() {
        return theId;
    }

    /**
     * Obtain the length of the hash.
     * @return the length
     */
    public int getHashLen() {
        return theHashLen;
    }

    /**
     * Constructor.
     * @param id the id
     * @param iLen the hash length
     */
    private DigestType(final int id,
                       final int iLen) {
        theId = id;
        theHashLen = iLen;
    }

    /**
     * get value from id.
     * @param id the id value
     * @return the corresponding enumeration object
     * @throws JDataException on error
     */
    public static DigestType fromId(final int id) throws JDataException {
        for (DigestType myType : values()) {
            if (myType.getId() == id) {
                return myType;
            }
        }
        throw new JDataException(ExceptionClass.DATA, "Invalid DigestType: " + id);
    }

    /**
     * Return the associated algorithm.
     * @return the algorithm
     */
    public String getAlgorithm() {
        switch (this) {
            case SHA256:
                return "SHA-256";
            case SHA512:
                return "SHA-512";
            case RIPEMD:
                return "RIPEMD320";
            case GOST:
                return "GOST3411";
            default:
                return toString();
        }
    }

    /**
     * Return the associated HMac algorithm.
     * @return the algorithm
     */
    public String getHMacAlgorithm() {
        switch (this) {
            case RIPEMD:
                return "HMacRIPEMD320";
            case GOST:
                return "HMacGOST3411";
            default:
                return "HMac" + toString();
        }
    }

    /**
     * Get random unique set of digest types.
     * @param pNumTypes the number of types
     * @param pRandom the random generator
     * @return the random set
     * @throws JDataException on error
     */
    public static DigestType[] getRandomTypes(final int pNumTypes,
                                              final SecureRandom pRandom) throws JDataException {
        /* Access the values */
        DigestType[] myValues = values();
        int iNumValues = myValues.length;
        int iIndex;

        /* Reject call if invalid number of types */
        if ((pNumTypes < 1) || (pNumTypes > iNumValues)) {
            throw new JDataException(ExceptionClass.LOGIC, "Invalid number of digests: " + pNumTypes);
        }

        /* Create the result set */
        DigestType[] myTypes = new DigestType[pNumTypes];

        /* Loop through the types */
        for (int i = 0; i < pNumTypes; i++) {
            /* Access the next random index */
            iIndex = pRandom.nextInt(iNumValues);

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
