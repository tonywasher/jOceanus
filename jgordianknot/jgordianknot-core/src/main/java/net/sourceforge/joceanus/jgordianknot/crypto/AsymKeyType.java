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

import java.util.function.Predicate;

/**
 * Asymmetric Key Types. Available Algorithms
 */
public enum AsymKeyType {
    /**
     * RSA.
     */
    RSA(2048),

    /**
     * Elliptic Curve 1.
     */
    ECPSEC384_1("secp384r1"),

    /**
     * Elliptic Curve 2.
     */
    ECPSEC521_1("secp521r1"),

    /**
     * Elliptic Curve 3.
     */
    ECMX431_1("c2tnb431r1"),

    /**
     * Elliptic Curve 4.
     */
    ECMSEC409_1("sect409r1"),

    /**
     * Elliptic Curve 5.
     */
    ECMSEC571_1("sect571r1"),

    /**
     * Elliptic Curve 6.
     */
    ECMTT384T_1("brainpoolp384t1"),

    /**
     * Elliptic Curve 7.
     */
    ECMTT512T_1("brainpoolp512t1");

    /**
     * Encryption algorithm.
     */
    private static final String BASEALGORITHM = "/None/OAEPWithSHA256AndMGF1Padding";

    /**
     * Signature algorithm.
     */
    private static final String BASESIGNATURE = "SHA256with";

    /**
     * Predicate for all asymKeyTypes.
     */
    private static final Predicate<AsymKeyType> PREDICATE_ALL = p -> true;

    /**
     * Predicate for elliptic asymKeyTypes.
     */
    private static final Predicate<AsymKeyType> PREDICATE_ELLIPTIC = p -> p.isElliptic();

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
     * @param keySize the RSA Key size
     */
    AsymKeyType(final int keySize) {
        theKeySize = keySize;
        theCurve = null;
        isElliptic = false;
    }

    /**
     * Constructor.
     * @param pCurve the name of the elliptic curve
     */
    AsymKeyType(final String pCurve) {
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
     * Obtain predicate for elliptic asymKeyTypes.
     * @return the predicate
     */
    public static Predicate<AsymKeyType> onlyElliptic() {
        return PREDICATE_ELLIPTIC;
    }

    /**
     * Obtain predicate for all asymKeyTypes.
     * @return the predicate
     */
    public static Predicate<AsymKeyType> allTypes() {
        return PREDICATE_ALL;
    }
}
