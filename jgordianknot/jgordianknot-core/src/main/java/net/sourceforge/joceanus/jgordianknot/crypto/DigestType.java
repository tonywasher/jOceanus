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
 * DataDigest types. Available algorithms.
 */
public enum DigestType {
    /**
     * SHA2.
     */
    SHA2,

    /**
     * Tiger.
     */
    TIGER,

    /**
     * WhirlPool.
     */
    WHIRLPOOL,

    /**
     * RIPEMD.
     */
    RIPEMD,

    /**
     * GOST.
     */
    GOST,

    /**
     * KECCAK.
     */
    KECCAK,

    /**
     * Skein.
     */
    SKEIN,

    /**
     * SM3.
     */
    SM3,

    /**
     * Blake.
     */
    BLAKE;

    /**
     * Predicate for all digestTypes.
     */
    private static final Predicate<DigestType> PREDICATE_ALL = p -> true;

    /**
     * Predicate for all digest types except Blake2b.
     */
    private static final Predicate<DigestType> PREDICATE_SUPPORTED = p -> p != BLAKE;

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = CryptoResource.getKeyForDigest(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Return the associated algorithm.
     * @return the algorithm
     */
    protected String getAlgorithm() {
        switch (this) {
            case SKEIN:
                return "SKEIN-512-512";
            case KECCAK:
                return "KECCAK-512";
            case SHA2:
                return "SHA512";
            case RIPEMD:
                return "RIPEMD320";
            case GOST:
                return "GOST3411";
            case BLAKE:
                return "Blake2b";
            default:
                return name();
        }
    }

    /**
     * Return the associated HMac algorithm.
     * @return the algorithm
     */
    protected String getMacAlgorithm() {
        switch (this) {
            case KECCAK:
                return "HMacKECCAK512";
            default:
                return "HMac"
                       + getAlgorithm();
        }
    }

    /**
     * Obtain predicate for all digestTypes.
     * @return the predicate
     */
    public static Predicate<DigestType> allTypes() {
        return PREDICATE_ALL;
    }

    /**
     * Obtain predicate for all supported digestTypes.
     * @return the predicate
     */
    public static Predicate<DigestType> allSupported() {
        return PREDICATE_SUPPORTED;
    }
}
