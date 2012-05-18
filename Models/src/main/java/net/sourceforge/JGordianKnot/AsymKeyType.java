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

import net.sourceforge.JDataWalker.ModelException;
import net.sourceforge.JDataWalker.ModelException.ExceptionClass;

public enum AsymKeyType {
    // RSA(1, 2048),

    /**
     * Elliptic Curve 1
     */
    EC1(2, "secp384r1"),

    /**
     * Elliptic Curve 2
     */
    EC2(3, "secp521r1"),

    /**
     * Elliptic Curve 3
     */
    EC3(4, "c2tnb431r1"),

    /**
     * Elliptic Curve 4
     */
    EC4(5, "sect409r1"),

    /**
     * Elliptic Curve 5
     */
    EC5(6, "sect571r1"),

    /**
     * Elliptic Curve 6
     */
    EC6(7, "brainpoolp384t1");

    /**
     * Encryption algorithm
     */
    private final static String BASEALGORITHM = "/None/OAEPWithSHA256AndMGF1Padding";

    /**
     * Signature algorithm
     */
    private final static String BASESIGNATURE = "SHA256with";

    /**
     * Key values
     */
    private int theId = 0;
    private int theKeySize = 0;
    private String theCurve = null;
    private boolean isElliptic = false;

    /* Access methods */
    public int getId() {
        return theId;
    }

    public int getKeySize() {
        return theKeySize;
    }

    public String getCurve() {
        return theCurve;
    }

    public boolean isElliptic() {
        return isElliptic;
    }

    public String getAlgorithm() {
        if (isElliptic)
            return "EC";
        else
            return toString();
    }

    public String getSignature() {
        if (isElliptic)
            return BASESIGNATURE + "ECDSA";
        else
            return BASESIGNATURE + toString();
    }

    public String getCipher() {
        if (isElliptic)
            return "Null";
        else
            return toString() + BASEALGORITHM;
    }

    /**
     * Constructor
     * @param id the id
     * @param keySize the RSA Key size
     */
    private AsymKeyType(int id, int keySize) {
        theId = id;
        theKeySize = keySize;
    }

    /**
     * Constructor
     * @param id the id
     * @param pCurve the keySize the RSA Key size
     */
    private AsymKeyType(int id, String pCurve) {
        theId = id;
        theCurve = pCurve;
        isElliptic = true;
    }

    /**
     * get value from id
     * @param id the id value
     * @return the corresponding enum object
     * @throws ModelException
     */
    public static AsymKeyType fromId(int id) throws ModelException {
        for (AsymKeyType myType : values()) {
            if (myType.getId() == id)
                return myType;
        }
        throw new ModelException(ExceptionClass.DATA, "Invalid AsymKeyType: " + id);
    }

    /**
     * Get random unique set of key types
     * @param pNumTypes the number of types
     * @param pRandom the random generator
     * @return the random set
     * @throws ModelException
     */
    public static AsymKeyType[] getRandomTypes(int pNumTypes,
                                               SecureRandom pRandom) throws ModelException {
        /* Access the values */
        AsymKeyType[] myValues = values();
        int iNumValues = myValues.length;
        int iIndex;

        /* Reject call if invalid number of types */
        if ((pNumTypes < 1) || (pNumTypes > iNumValues))
            throw new ModelException(ExceptionClass.LOGIC, "Invalid number of types: " + pNumTypes);

        /* Create the result set */
        AsymKeyType[] myTypes = new AsymKeyType[pNumTypes];

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
