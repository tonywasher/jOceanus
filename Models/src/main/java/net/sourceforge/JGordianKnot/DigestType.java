/*******************************************************************************
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
package net.sourceforge.JGordianKnot;

import java.security.SecureRandom;

import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.ModelException.ExceptionClass;

public enum DigestType {
    /**
     * SHA256
     */
    SHA256(1, 256),

    /**
     * Tiger
     */
    Tiger(2, 192),

    /**
     * WhirlPool
     */
    WHIRLPOOL(3, 512),

    /**
     * RIPEMD
     */
    RIPEMD(4, 320),

    /**
     * GOST
     */
    GOST(5, 256),

    /**
     * SHA512
     */
    SHA512(6, 512);

    /**
     * Key values
     */
    private int theId = 0;
    private int theHashLen = 0;

    /* Access methods */
    public int getId() {
        return theId;
    }

    public int getHashLen() {
        return theHashLen;
    }

    /**
     * Constructor
     * @param id the id
     * @param iLen the hash length
     */
    private DigestType(int id,
                       int iLen) {
        theId = id;
        theHashLen = iLen;
    }

    /**
     * get value from id
     * @param id the id value
     * @return the corresponding enumeration object
     * @throws ModelException
     */
    public static DigestType fromId(int id) throws ModelException {
        for (DigestType myType : values()) {
            if (myType.getId() == id)
                return myType;
        }
        throw new ModelException(ExceptionClass.DATA, "Invalid DigestType: " + id);
    }

    /**
     * Return the associated algorithm
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
     * Return the associated HMac algorithm
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
     * Get random unique set of digest types
     * @param pNumTypes the number of types
     * @param pRandom the random generator
     * @return the random set
     * @throws ModelException
     */
    public static DigestType[] getRandomTypes(int pNumTypes,
                                              SecureRandom pRandom) throws ModelException {
        /* Access the values */
        DigestType[] myValues = values();
        int iNumValues = myValues.length;
        int iIndex;

        /* Reject call if invalid number of types */
        if ((pNumTypes < 1) || (pNumTypes > iNumValues))
            throw new ModelException(ExceptionClass.LOGIC, "Invalid number of digests: " + pNumTypes);

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
