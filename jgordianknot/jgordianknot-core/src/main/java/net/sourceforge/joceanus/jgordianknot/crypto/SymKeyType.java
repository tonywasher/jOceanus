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
 * Symmetric Key Types. Available algorithms.
 */
public enum SymKeyType {
    /**
     * AES.
     */
    AES,

    /**
     * TwoFish.
     */
    TWOFISH,

    /**
     * Serpent.
     */
    SERPENT,

    /**
     * CAMELLIA.
     */
    CAMELLIA,

    /**
     * RC6.
     */
    RC6,

    /**
     * CAST6.
     */
    CAST6,

    /**
     * ThreeFish.
     */
    THREEFISH,

    /**
     * SM4.
     */
    SM4,

    /**
     * Noekeon.
     */
    NOEKEON,

    /**
     * SEED.
     */
    SEED;

    /**
     * Predicate for long keys.
     */
    private static final Predicate<SymKeyType> PREDICATE_LONGKEY = p -> p.validForKeyLen(true);

    /**
     * Predicate for MAC long keys.
     */
    private static final Predicate<SymKeyType> PREDICATE_LONGMACKEY = p -> p.validForKeyLen(true) && p.isStdBlock();

    /**
     * Predicate for short keys.
     */
    private static final Predicate<SymKeyType> PREDICATE_SHORTKEY = p -> p.validForKeyLen(false);

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = CryptoResource.getKeyForSym(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Obtain the algorithm.
     * @return the algorithm
     */
    public String getAlgorithm() {
        switch (this) {
            case TWOFISH:
                return "TwoFish";
            case SERPENT:
                return "Serpent";
            case THREEFISH:
                return "ThreeFish-256";
            default:
                return name();
        }
    }

    /**
     * Obtain the data cipher name.
     * @param pMode the cipher mode
     * @return the data cipher name
     */
    public String getDataCipher(final CipherMode pMode) {
        /* Build the algorithm */
        return getAlgorithm()
               + "/"
               + pMode.getCipherMode()
               + getPadding(pMode);
    }

    /**
     * Obtain the padding.
     * @param pMode the cipher mode
     * @return the data cipher name
     */
    private String getPadding(final CipherMode pMode) {
        switch (pMode) {
            case CBC:
                return "/PKCS7PADDING";
            default:
                return "/NOPADDING";
        }
    }

    /**
     * Does this KeyType use a standard block size?
     * @return true/false
     */
    public boolean isStdBlock() {
        switch (this) {
            case THREEFISH:
                return false;
            default:
                return true;
        }
    }

    /**
     * Is this KeyType valid for required keyLength?
     * @param pLongKeys true/false
     * @return true/false
     */
    private boolean validForKeyLen(final boolean pLongKeys) {
        switch (this) {
            case THREEFISH:
                return pLongKeys;
            case SM4:
            case SEED:
            case NOEKEON:
                return !pLongKeys;
            default:
                return true;
        }
    }

    /**
     * Obtain predicate for all symKeyTypes for KeyLength.
     * @param pLongKeys true/false
     * @return the predicate
     */
    public static Predicate<SymKeyType> allForKeyLen(final boolean pLongKeys) {
        return pLongKeys
                         ? PREDICATE_LONGKEY
                         : PREDICATE_SHORTKEY;
    }

    /**
     * Obtain predicate for all macSymKeyTypes for KeyLength.
     * @param pLongKeys true/false
     * @return the predicate
     */
    public static Predicate<SymKeyType> allMacForKeyLen(final boolean pLongKeys) {
        return pLongKeys
                         ? PREDICATE_LONGMACKEY
                         : PREDICATE_SHORTKEY;
    }
}
