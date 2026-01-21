/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.gordianknot.api.cipher;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianBundleLoader.GordianBundleId;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKeyLengths;

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
    SPECK(GordianLength.LEN_128),

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
    MARS(GordianLength.LEN_128),

    /**
     * LEA.
     */
    LEA(GordianLength.LEN_128);

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
     *
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
            theName = bundleIdForSymKeyType().getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Obtain default block length.
     *
     * @return the default length
     */
    public GordianLength getDefaultBlockLength() {
        return theLengths[0];
    }

    /**
     * Obtain supported block lengths.
     *
     * @return the supported lengths (first is default)
     */
    public GordianLength[] getSupportedBlockLengths() {
        return theLengths;
    }

    /**
     * is block length valid?
     *
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
     *
     * @return true/false
     */
    public boolean hasMultipleBlockLengths() {
        return theLengths.length > 1;
    }

    /**
     * Is this KeyType valid for keyLength?
     *
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
            case RC2:
                return true;
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
     *
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
            default:
                return true;
        }
    }

    /**
     * Obtain the resource bundleId for this symKeyType.
     *
     * @return the resource bundleId
     */
    private GordianBundleId bundleIdForSymKeyType() {
        /* Create the map and return it */
        switch (this) {
            case AES:
                return GordianCipherResource.SYMKEY_AES;
            case SERPENT:
                return GordianCipherResource.SYMKEY_SERPENT;
            case TWOFISH:
                return GordianCipherResource.SYMKEY_TWOFISH;
            case CAMELLIA:
                return GordianCipherResource.SYMKEY_CAMELLIA;
            case RC6:
                return GordianCipherResource.SYMKEY_RC6;
            case CAST6:
                return GordianCipherResource.SYMKEY_CAST6;
            case ARIA:
                return GordianCipherResource.SYMKEY_ARIA;
            case THREEFISH:
                return GordianCipherResource.SYMKEY_THREEFISH;
            case NOEKEON:
                return GordianCipherResource.SYMKEY_NOEKEON;
            case SEED:
                return GordianCipherResource.SYMKEY_SEED;
            case SM4:
                return GordianCipherResource.SYMKEY_SM4;
            case RC2:
                return GordianCipherResource.SYMKEY_RC2;
            case RC5:
                return GordianCipherResource.SYMKEY_RC5;
            case CAST5:
                return GordianCipherResource.SYMKEY_CAST5;
            case TEA:
                return GordianCipherResource.SYMKEY_TEA;
            case XTEA:
                return GordianCipherResource.SYMKEY_XTEA;
            case IDEA:
                return GordianCipherResource.SYMKEY_IDEA;
            case SKIPJACK:
                return GordianCipherResource.SYMKEY_SKIPJACK;
            case BLOWFISH:
                return GordianCipherResource.SYMKEY_BLOWFISH;
            case DESEDE:
                return GordianCipherResource.SYMKEY_DESEDE;
            case GOST:
                return GordianCipherResource.SYMKEY_GOST;
            case KUZNYECHIK:
                return GordianCipherResource.SYMKEY_KUZNYECHIK;
            case KALYNA:
                return GordianCipherResource.SYMKEY_KALYNA;
            case SHACAL2:
                return GordianCipherResource.SYMKEY_SHACAL2;
            case SPECK:
                return GordianCipherResource.SYMKEY_SPECK;
            case ANUBIS:
                return GordianCipherResource.SYMKEY_ANUBIS;
            case SIMON:
                return GordianCipherResource.SYMKEY_SIMON;
            case MARS:
                return GordianCipherResource.SYMKEY_MARS;
            case LEA:
                return GordianCipherResource.SYMKEY_LEA;
            default:
                throw new IllegalArgumentException();
        }
    }
}
