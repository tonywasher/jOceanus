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
 * Stream Key Type.
 */
public enum StreamKeyType {
    /**
     * XSalsa20.
     */
    XSALSA20,

    /**
     * Salsa20.
     */
    SALSA20,

    /**
     * HC.
     */
    HC,

    /**
     * ChaCha.
     */
    CHACHA,

    /**
     * VMPC.
     */
    VMPC,

    /**
     * ISAAC.
     */
    ISAAC,

    /**
     * Grain.
     */
    GRAIN;

    /**
     * Predicate for long keys.
     */
    private static final Predicate<StreamKeyType> PREDICATE_LONGKEY = p -> p.validForKeyLen(true);

    /**
     * Predicate for short keys.
     */
    private static final Predicate<StreamKeyType> PREDICATE_SHORTKEY = p -> p.validForKeyLen(false);

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = CryptoResource.getKeyForStream(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Obtain the algorithm.
     * @param pRestricted use restricted algorithms
     * @return the algorithm
     */
    public String getAlgorithm(final boolean pRestricted) {
        switch (this) {
            case VMPC:
                return "VMPC-KSA3";
            case GRAIN:
                return "Grain128";
            case HC:
                return pRestricted
                                   ? "HC128"
                                   : "HC256";
            default:
                return name();
        }
    }

    /**
     * Is this KeyType valid for required keyLength?
     * @param pLongKeys true/false
     * @return true/false
     */
    private boolean validForKeyLen(final boolean pLongKeys) {
        switch (this) {
            case XSALSA20:
                return pLongKeys;
            case GRAIN:
                return !pLongKeys;
            case ISAAC:
                return false;
            default:
                return true;
        }
    }

    /**
     * Obtain predicate for all symKeyTypes for KeyLength.
     * @param pLongKeys true/false
     * @return the predicate
     */
    public static Predicate<StreamKeyType> allForKeyLen(final boolean pLongKeys) {
        return pLongKeys
                         ? PREDICATE_LONGKEY
                         : PREDICATE_SHORTKEY;
    }
}
