/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyLengths;

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
    THREEFISH(GordianLength.LEN_256, GordianLength.LEN_512, GordianLength.LEN_1024),

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
     * CAST5.
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
    KALYNA(GordianLength.LEN_128, GordianLength.LEN_256, GordianLength.LEN_512),

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
    SIMON(GordianLength.LEN_128, GordianLength.LEN_64),

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
     * Obtain default block length.
     * @return the default length
     */
    public GordianLength getDefaultBlockLength() {
        return theLengths[0];
    }

    /**
     * Obtain supported block lengths.
     * @return the supported lengths (first is default)
     */
    public GordianLength[] getSupportedBlockLengths() {
        return theLengths;
    }

    /**
     * is block length valid?
     * @param pLength the length
     * @return true/false
     */
    private boolean isBlockLengthValid(final GordianLength pLength) {
        for (final GordianLength myLength : theLengths) {
            if (myLength.equals(pLength)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Does this KeyType have multiple block lengths?
     * @return true/false
     */
    public boolean hasMultipleBlockLengths() {
        return theLengths.length > 1;
    }

    /**
     * Is this KeyType valid for keyLength?
     * @param pKeyLen the key length
     * @return true/false
     */
    public boolean validForKeyLength(final GordianLength pKeyLen) {
        /* Reject unsupported keyLengths */
        if (!GordianKeyLengths.isSupportedLength(pKeyLen)) {
            return false;
        }

        /* switch on keyLength */
        switch (this) {
            case THREEFISH:
                return GordianLength.LEN_256 == pKeyLen
                        || GordianLength.LEN_512 == pKeyLen
                        || GordianLength.LEN_1024 == pKeyLen;
            case SHACAL2:
                return GordianLength.LEN_256 == pKeyLen
                        || GordianLength.LEN_512 == pKeyLen;
            case GOST:
            case KUZNYECHIK:
                return GordianLength.LEN_256 == pKeyLen;
            case SM4:
            case SEED:
            case NOEKEON:
            case CAST5:
            case RC5:
            case SKIPJACK:
            case TEA:
            case XTEA:
            case IDEA:
                return GordianLength.LEN_128 == pKeyLen;
            case DESEDE:
                return GordianLength.LEN_128 == pKeyLen
                        || GordianLength.LEN_192 == pKeyLen;
            case KALYNA:
                return GordianLength.LEN_128 == pKeyLen
                        || GordianLength.LEN_256 == pKeyLen
                        || GordianLength.LEN_512 == pKeyLen;
            default:
                return GordianLength.LEN_128 == pKeyLen
                        || GordianLength.LEN_192 == pKeyLen
                        || GordianLength.LEN_256 == pKeyLen;
        }
    }

    /**
     * Is this KeyType valid for blockLength and keyLength?
     * @param pBlkLen the block Length
     * @param pKeyLen the key length
     * @return true/false
     */
    public boolean validBlockAndKeyLengths(final GordianLength pBlkLen,
                                           final GordianLength pKeyLen) {
        /* Reject unsupported blockLengths */
        if (!isBlockLengthValid(pBlkLen)) {
            return false;
        }

        /* Reject unsupported keyLengths */
        if (!validForKeyLength(pKeyLen)) {
            return false;
        }

        /* Reject keys that are shorter than the block length */
        if (pBlkLen.getLength() > pKeyLen.getLength()) {
            return false;
        }

        /* Switch on keyType */
        switch (this) {
            /* ThreeFish keys must be same length as blockSize */
            case THREEFISH:
                return pKeyLen == pBlkLen;
            /* Explicitly disallow Kalyna128-512 */
            case KALYNA:
                return GordianLength.LEN_128 != pBlkLen
                        || GordianLength.LEN_512 != pKeyLen;
            /* Simon/Speck 64-bit blockSize can only be used with 128-bit keys */
            case SIMON:
            case SPECK:
                return pBlkLen != GordianLength.LEN_64
                        || pKeyLen == GordianLength.LEN_128;
            default:
                return true;
        }
    }
}

