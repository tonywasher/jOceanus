/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.api.cipher;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;

/**
 * Symmetric Key Types. Available algorithms.
 */
public enum GordianSymKeyType {
    /**
     * AES.
     */
    AES(GordianLength.LEN_128),

    /**
     * TwoFish.
     */
    TWOFISH(GordianLength.LEN_128),

    /**
     * Serpent.
     */
    SERPENT(GordianLength.LEN_128),

    /**
     * CAMELLIA.
     */
    CAMELLIA(GordianLength.LEN_128),

    /**
     * RC6.
     */
    RC6(GordianLength.LEN_128),

    /**
     * CAST6.
     */
    CAST6(GordianLength.LEN_128),

    /**
     * ThreeFish.
     */
    THREEFISH(GordianLength.LEN_256),

    /**
     * ARIA.
     */
    ARIA(GordianLength.LEN_128),

    /**
     * SM4.
     */
    SM4(GordianLength.LEN_128),

    /**
     * NoeKeon.
     */
    NOEKEON(GordianLength.LEN_128),

    /**
     * SEED.
     */
    SEED(GordianLength.LEN_128),

    /**
     * SkipJack.
     */
    SKIPJACK(GordianLength.LEN_64),

    /**
     * IDEA.
     */
    IDEA(GordianLength.LEN_64),

    /**
     * TEA.
     */
    TEA(GordianLength.LEN_64),

    /**
     * XTEA.
     */
    XTEA(GordianLength.LEN_64),

    /**
     * DESede.
     */
    DESEDE(GordianLength.LEN_64),

    /**
     * DESede.
     */
    CAST5(GordianLength.LEN_64),

    /**
     * RC2.
     */
    RC2(GordianLength.LEN_64),

    /**
     * RC5.
     */
    RC5(GordianLength.LEN_128, GordianLength.LEN_64),

    /**
     * Blowfish.
     */
    BLOWFISH(GordianLength.LEN_64),

    /**
     * Kalyna.
     */
    KALYNA(GordianLength.LEN_128, GordianLength.LEN_256),

    /**
     * GOST.
     */
    GOST(GordianLength.LEN_64),

    /**
     * Kuznyechik.
     */
    KUZNYECHIK(GordianLength.LEN_128),

    /**
     * SHACAL2.
     */
    SHACAL2(GordianLength.LEN_256),

    /**
     * SPECK.
     */
    SPECK(GordianLength.LEN_128, GordianLength.LEN_64),

    /**
     * Anubis.
     */
    ANUBIS(GordianLength.LEN_128),

    /**
     * Simon.
     */
    SIMON(GordianLength.LEN_128),

    /**
     * MARS.
     */
    MARS(GordianLength.LEN_128);

    /**
     * The Supported lengths.
     */
    private final GordianLength[] theLengths;

    /**
     * The String name.
     */
    private String theName;

    /**
     * Constructor.
     * @param pLengths the valid lengths
     */
    GordianSymKeyType(final GordianLength... pLengths) {
        theLengths = pLengths;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = GordianCipherResource.getKeyForSym(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Obtain default length.
     * @return the default length
     */
    public GordianLength getDefaultLength() {
        return theLengths[0];
    }

    /**
     * Obtain supported lengths.
     * @return the supported lengths (first is default)
     */
    public GordianLength[] getSupportedLengths() {
        return theLengths;
    }

    /**
     * is length valid?
     * @param pLength the length
     * @return true/false
     */
    public boolean isLengthValid(final GordianLength pLength) {
        for (final GordianLength myLength : theLengths) {
            if (myLength.equals(pLength)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Does this KeyType have multiple lengths?
     * @return true/false
     */
    public boolean hasMultipleLengths() {
        return theLengths.length > 1;
    }

    /**
     * Is this KeyType valid for restriction?
     * @param pRestricted true/false
     * @return true/false
     */
    public boolean validForRestriction(final boolean pRestricted) {
        switch (this) {
            case THREEFISH:
            case GOST:
            case KUZNYECHIK:
                return !pRestricted;
            case SM4:
            case SEED:
            case NOEKEON:
            case CAST5:
            case RC5:
            case SKIPJACK:
            case TEA:
            case XTEA:
            case IDEA:
            case DESEDE:
                return pRestricted;
            default:
                return true;
        }
    }
}

