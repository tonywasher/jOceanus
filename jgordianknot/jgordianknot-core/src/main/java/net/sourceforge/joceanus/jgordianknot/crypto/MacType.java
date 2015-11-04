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
 * Mac types. Available algorithms.
 */
public enum MacType {
    /**
     * HMAC.
     */
    HMAC,

    /**
     * GMAC.
     */
    GMAC,

    /**
     * Poly1305.
     */
    POLY1305,

    /**
     * Skein.
     */
    SKEIN,

    /**
     * VMPC.
     */
    VMPC;

    /**
     * Predicate for all macTypes.
     */
    private static final Predicate<MacType> PREDICATE_ALL = p -> true;

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = CryptoResource.getKeyForHMac(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Obtain the algorithm.
     * @param pKeyType the symmetric key type (or null)
     * @return the algorithm
     */
    public String getAlgorithm(final SymKeyType pKeyType) {
        switch (this) {
            case GMAC:
                return pKeyType.isStdBlock()
                                             ? pKeyType.name()
                                               + "-GMAC"
                                             : null;
            case POLY1305:
                return pKeyType.isStdBlock()
                                             ? "POLY1305-"
                                               + pKeyType.name()
                                             : null;
            default:
                return null;
        }
    }

    /**
     * Obtain the mac algorithm.
     * @return the algorithm
     */
    public String getAlgorithm() {
        switch (this) {
            case VMPC:
                return "VMPC-MAC";
            case SKEIN:
                return "SKEIN-MAC-512-512";
            default:
                return null;
        }
    }

    /**
     * Obtain the IV length.
     * @return the IV Length
     */
    public int getIVLen() {
        switch (this) {
            case VMPC:
            case GMAC:
                return CipherSet.IVSIZE;
            case POLY1305:
                return CipherSet.IVSIZE >> 1;
            default:
                return 0;
        }
    }

    /**
     * Obtain the Key generation algorithm.
     * @return the algorithm
     */
    public String getKeyAlgorithm() {
        switch (this) {
            case VMPC:
                return "VMPC-KSA3";
            case SKEIN:
                return getAlgorithm();
            default:
                return null;
        }
    }

    /**
     * Obtain predicate for all digestTypes.
     * @return the predicate
     */
    public static Predicate<MacType> allTypes() {
        return PREDICATE_ALL;
    }
}
