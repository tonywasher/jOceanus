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
 * Asymmetric Key Types. Available Algorithms
 * @author Tony Washer
 */
public enum AsymKeyType {
    // RSA(1, 2048),

    /**
     * Elliptic Curve 1.
     */
    EC1(2, "secp384r1"),

    /**
     * Elliptic Curve 2.
     */
    EC2(3, "secp521r1"),

    /**
     * Elliptic Curve 3.
     */
    EC3(4, "c2tnb431r1"),

    /**
     * Elliptic Curve 4.
     */
    EC4(5, "sect409r1"),

    /**
     * Elliptic Curve 5.
     */
    EC5(6, "sect571r1"),

    /**
     * Elliptic Curve 6.
     */
    EC6(7, "brainpoolp384t1");

    /**
     * Encryption algorithm.
     */
    private static final String BASEALGORITHM = "/None/OAEPWithSHA256AndMGF1Padding";

    /**
     * Signature algorithm.
     */
    private static final String BASESIGNATURE = "SHA256with";

    /**
     * The external Id of the algorithm.
     */
    private final int theId;

    /**
     * The key size of the algorithm.
     */
    private final int theKeySize;

    /**
     * The elliptic curve name.
     */
    private final String theCurve;

    /**
     * Is this key an elliptic curve.
     */
    private final boolean isElliptic;

    /**
     * Obtain the external Id.
     * @return the external Id
     */
    public int getId() {
        return theId;
    }

    /**
     * Obtain the keySize.
     * @return the keySize
     */
    public int getKeySize() {
        return theKeySize;
    }

    /**
     * Obtain the curve name.
     * @return the curve name
     */
    public String getCurve() {
        return theCurve;
    }

    /**
     * Is this key an elliptic curve?
     * @return true/false
     */
    public boolean isElliptic() {
        return isElliptic;
    }

    /**
     * Obtain the algorithm.
     * @return the algorithm
     */
    public String getAlgorithm() {
        if (isElliptic) {
            return "EC";
        }
        return toString();
    }

    /**
     * Obtain the signature algorithm.
     * @return the algorithm
     */
    public String getSignature() {
        if (isElliptic) {
            return BASESIGNATURE + "ECDSA";
        }
        return BASESIGNATURE + toString();
    }

    /**
     * Obtain the cipher algorithm.
     * @return the algorithm
     */
    public String getCipher() {
        if (isElliptic) {
            return "Null";
        }
        return toString() + BASEALGORITHM;
    }

    /**
     * Constructor.
     * @param id the id
     * @param keySize the RSA Key size
     */
    private AsymKeyType(final int id,
                        final int keySize) {
        theId = id;
        theKeySize = keySize;
        theCurve = null;
        isElliptic = false;
    }

    /**
     * Constructor.
     * @param id the id
     * @param pCurve the keySize the RSA Key size
     */
    private AsymKeyType(final int id,
                        final String pCurve) {
        theId = id;
        theKeySize = 0;
        theCurve = pCurve;
        isElliptic = true;
    }

    /**
     * get value from id.
     * @param id the id value
     * @return the corresponding enum object
     * @throws JDataException if id is invalid
     */
    public static AsymKeyType fromId(final int id) throws JDataException {
        for (AsymKeyType myType : values()) {
            if (myType.getId() == id) {
                return myType;
            }
        }
        throw new JDataException(ExceptionClass.DATA, "Invalid AsymKeyType: " + id);
    }

    /**
     * Get random unique set of key types.
     * @param pNumTypes the number of types
     * @param pRandom the random generator
     * @return the random set
     * @throws JDataException if the number of types is invalid
     */
    public static AsymKeyType[] getRandomTypes(final int pNumTypes,
                                               final SecureRandom pRandom) throws JDataException {
        /* Access the values */
        AsymKeyType[] myValues = values();
        int iNumValues = myValues.length;
        int iIndex;

        /* Reject call if invalid number of types */
        if ((pNumTypes < 1) || (pNumTypes > iNumValues)) {
            throw new JDataException(ExceptionClass.LOGIC, "Invalid number of types: " + pNumTypes);
        }

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
