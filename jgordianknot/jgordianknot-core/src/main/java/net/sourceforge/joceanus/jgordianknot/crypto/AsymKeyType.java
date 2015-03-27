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
package net.sourceforge.joceanus.jgordianknot.crypto;

import java.security.SecureRandom;

import net.sourceforge.joceanus.jgordianknot.JGordianDataException;
import net.sourceforge.joceanus.jgordianknot.JGordianLogicException;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Asymmetric Key Types. Available Algorithms
 */
public enum AsymKeyType {
    /**
     * RSA.
     */
    RSA(1, 2048),

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
        return name();
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
     * get value from id.
     * @param id the id value
     * @return the corresponding enum object
     * @throws JOceanusException if id is invalid
     */
    public static AsymKeyType fromId(final int id) throws JOceanusException {
        for (AsymKeyType myType : values()) {
            if (myType.getId() == id) {
                return myType;
            }
        }
        throw new JGordianDataException("Invalid AsymKeyType: "
                                        + id);
    }

    /**
     * Get random unique set of asymmetric key types.
     * @param pNumTypes the number of types
     * @param pRandom the random generator
     * @return the random set
     * @throws JOceanusException on error
     */
    public static AsymKeyType[] getRandomTypes(final int pNumTypes,
                                               final SecureRandom pRandom) throws JOceanusException {
        /* Use all values */
        return getRandomTypes(pNumTypes, pRandom, false);
    }

    /**
     * Get random unique set of asymmetric key types.
     * @param pNumTypes the number of types
     * @param pRandom the random generator
     * @param pElliptic use only elliptic keys
     * @return the random set
     * @throws JOceanusException on error
     */
    public static AsymKeyType[] getRandomTypes(final int pNumTypes,
                                               final SecureRandom pRandom,
                                               final boolean pElliptic) throws JOceanusException {
        /* Access the values */
        AsymKeyType[] myValues = values();
        int iNumValues = myValues.length;

        /* Reject call if invalid number of types */
        if ((pNumTypes < 1)
            || (pNumTypes > iNumValues)) {
            throw new JGordianLogicException("Invalid number of asymmetric keys: "
                                             + pNumTypes);
        }

        /* Create the result set */
        AsymKeyType[] myTypes = new AsymKeyType[pNumTypes];

        /* Loop through the types */
        int i = 0;
        while (i < pNumTypes) {
            /* Access the next random index */
            int iIndex = pRandom.nextInt(iNumValues);

            /* If we are using all keyTypes or else this is elliptic */
            AsymKeyType myType = myValues[iIndex];
            if ((!pElliptic)
                || myType.isElliptic()) {
                /* Store the type */
                myTypes[i++] = myValues[iIndex];
            }

            /* Shift last value down in place of the one thats been used */
            myValues[iIndex] = myValues[iNumValues - 1];
            iNumValues--;
        }

        /* Return the types */
        return myTypes;
    }
}
