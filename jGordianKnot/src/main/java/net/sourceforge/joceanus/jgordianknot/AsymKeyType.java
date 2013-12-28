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

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;

/**
 * Asymmetric Key Types. Available Algorithms
 */
public enum AsymKeyType {
    // RSA(1, 2048),

    /**
     * Elliptic Curve 1.
     */
    ECPSEC384_1(2, "secp384r1"),

    /**
     * Elliptic Curve 2.
     */
    ECPSEC521_1(3, "secp521r1"),

    /**
     * Elliptic Curve 3.
     */
    ECMX431_1(4, "c2tnb431r1"),

    /**
     * Elliptic Curve 4.
     */
    ECMSEC409_1(5, "sect409r1"),

    /**
     * Elliptic Curve 5.
     */
    ECMSEC571_1(6, "sect571r1"),

    /**
     * Elliptic Curve 6.
     */
    ECMTT384T_1(7, "brainpoolp384t1"),

    /**
     * Elliptic Curve 7.
     */
    ECMTT512T_1(8, "brainpoolp512t1");

    /**
     * Encryption algorithm.
     */
    private static final String BASEALGORITHM = "/None/OAEPWithSHA256AndMGF1Padding";

    /**
     * Signature algorithm.
     */
    private static final String BASESIGNATURE = "SHA256with";

    @Override
    public String toString() {
        /* Elliptic Curve use the curve name */
        if (isElliptic) {
            return theCurve;
        }

        /* return the name */
        return name();
    }

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
            return BASESIGNATURE
                   + "ECDSA";
        }
        return BASESIGNATURE
               + toString();
    }

    /**
     * Obtain the cipher algorithm.
     * @return the algorithm
     */
    public String getCipher() {
        if (isElliptic) {
            return "Null";
        }
        return toString()
               + BASEALGORITHM;
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
     * @param pCurve the name of the elliptic curve
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
        throw new JDataException(ExceptionClass.DATA, "Invalid AsymKeyType: "
                                                      + id);
    }

    /**
     * Check the number of types.
     * @param pNumTypes the number of asymmetric keys
     */
    private static void checkNumTypes(final int pNumTypes) {
        /* Access the values */
        AsymKeyType[] myValues = values();
        int myNumTypes = myValues.length;

        /* Validate number of types */
        if ((pNumTypes < 1)
            || (pNumTypes > myNumTypes)) {
            /* Throw exception */
            throw new IllegalArgumentException("Invalid number of asymmetric keys");
        }
    }

    /**
     * Determine bound of random integer for choice of random AsymKeyTypes.
     * @param pNumTypes the number of Asymmetric keys
     * @return the bound of the random integer.
     */
    public static int getRandomBound(final int pNumTypes) {
        /* Validate number of types */
        checkNumTypes(pNumTypes);

        /* Access the values */
        AsymKeyType[] myValues = values();
        int myNumTypes = myValues.length;

        /* Initialise the bounds */
        int myBound = myNumTypes--;

        /* Loop through the types */
        for (int i = 1; i < pNumTypes; i++) {
            /* Factor in additional types */
            myBound *= myNumTypes--;
        }

        /* Return the bound */
        return myBound;
    }

    /**
     * Get random unique set of asymmetric key types.
     * @param pNumTypes the number of types
     * @param pRandom the random generator
     * @return the random set
     */
    public static AsymKeyType[] getRandomTypes(final int pNumTypes,
                                               final SecureRandom pRandom) {
        /* Determine bound for the number of types */
        int myBound = getRandomBound(pNumTypes);

        /* Generate the seed */
        int mySeed = pRandom.nextInt(myBound);

        /* Generate the random types */
        return getRandomAsymKeyTypes(pNumTypes, mySeed);
    }

    /**
     * Get random unique set of asymmetric key types.
     * @param pNumTypes the number of types
     * @param pSeed the seed value
     * @return the random set
     */
    public static AsymKeyType[] getRandomTypes(final int pNumTypes,
                                               final long pSeed) {
        /* Validate number of types */
        checkNumTypes(pNumTypes);

        /* Generate the random types */
        return getRandomAsymKeyTypes(pNumTypes, pSeed);
    }

    /**
     * Get unique set of asymmetric key types from seed.
     * @param pNumTypes the number of types
     * @param pSeed the seed value
     * @return the random set
     */
    private static AsymKeyType[] getRandomAsymKeyTypes(final int pNumTypes,
                                                       final long pSeed) {
        /* Access the values */
        AsymKeyType[] myValues = values();
        int iNumValues = myValues.length;
        long mySeed = pSeed;

        /* Create the result set */
        AsymKeyType[] myTypes = new AsymKeyType[pNumTypes];

        /* Loop through the types */
        for (int i = 0; i < pNumTypes; i++) {
            /* Extract the index */
            int myIndex = (int) (mySeed % iNumValues);
            mySeed /= iNumValues;

            /* Store the type */
            myTypes[i] = myValues[myIndex];

            /* Shift last value down in place of the one thats been used */
            myValues[myIndex] = myValues[iNumValues - 1];
            iNumValues--;
        }

        /* Return the types */
        return myTypes;
    }
}
