/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKeyLengths;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.base.GordianBundleLoader.GordianBundleId;

import java.util.EnumMap;
import java.util.Map;

/**
 * Symmetric Key Types. Available algorithms.
 */
public final class GordianCoreSymKeyType {
    /**
     * The symKeyTypeMap.
     */
    private static final Map<GordianNewSymKeyType, GordianCoreSymKeyType> TYPEMAP = newTypeMap();

    /**
     * The symKeyTypeArray.
     */
    private static final GordianCoreSymKeyType[] VALUES = TYPEMAP.values().toArray(new GordianCoreSymKeyType[0]);

    /**
     * The SymKeyType.
     */
    private final GordianNewSymKeyType theType;

    /**
     * The Supported blockLengths.
     */
    private final GordianLength[] theLengths;

    /**
     * The Name.
     */
    private final String theName;

    /**
     * Constructor.
     *
     * @param pType the type
     */
    private GordianCoreSymKeyType(final GordianNewSymKeyType pType) {
        theType = pType;
        theLengths = blockLengthsForSymKeyType(pType);
        theName = bundleIdForSymKeyType(pType).getValue();
    }

    /**
     * Obtain the type.
     *
     * @return the type
     */
    public GordianNewSymKeyType getType() {
        return theType;
    }

    @Override
    public String toString() {
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
     * Obtain default block length.
     *
     * @param pType the type
     * @return the default length
     */
    public static GordianLength getDefaultBlockLength(final GordianNewSymKeyType pType) {
        final GordianCoreSymKeyType myType = mapCoreType(pType);
        return myType == null ? null : myType.getDefaultBlockLength();
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
     * Is the KeyType valid for keyLength?
     *
     * @param pKeyType the key type
     * @param pKeyLen  the key length
     * @return true/false
     */
    public static boolean validForKeyLength(final GordianNewSymKeyType pKeyType,
                                            final GordianLength pKeyLen) {
        final GordianCoreSymKeyType myType = mapCoreType(pKeyType);
        return myType != null && myType.validForKeyLength(pKeyLen);
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
        switch (theType) {
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
        switch (theType) {
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
     * @param pType the symKeyType
     * @return the resource bundleId
     */
    private static GordianBundleId bundleIdForSymKeyType(final GordianNewSymKeyType pType) {
        /* Create the map and return it */
        switch (pType) {
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

    /**
     * Obtain the available block lengths for the symKeyType.
     *
     * @param pType the symKeyType
     * @return the block lengths
     */
    private static GordianLength[] blockLengthsForSymKeyType(final GordianNewSymKeyType pType) {
        /* Create the map and return it */
        switch (pType) {
            case AES:
            case TWOFISH:
            case SERPENT:
            case CAMELLIA:
            case RC6:
            case CAST6:
            case ARIA:
            case SM4:
            case NOEKEON:
            case KUZNYECHIK:
            case SPECK:
            case ANUBIS:
            case SIMON:
            case MARS:
            case LEA:
            case SEED:
                return new GordianLength[]{GordianLength.LEN_128};
            case THREEFISH:
                return new GordianLength[]{GordianLength.LEN_256, GordianLength.LEN_512, GordianLength.LEN_1024};
            case TEA:
            case XTEA:
            case IDEA:
            case SKIPJACK:
            case RC2:
            case DESEDE:
            case CAST5:
            case BLOWFISH:
            case GOST:
                return new GordianLength[]{GordianLength.LEN_64};
            case RC5:
                return new GordianLength[]{GordianLength.LEN_128, GordianLength.LEN_64};
            case KALYNA:
                return new GordianLength[]{GordianLength.LEN_128, GordianLength.LEN_256, GordianLength.LEN_512};
            case SHACAL2:
                return new GordianLength[]{GordianLength.LEN_256};
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain the default block lengths for the symKeyType and keyLength.
     *
     * @param pType      the symKeyType
     * @param pKeyLength the keyLength
     * @return the block lengths
     */
    static GordianLength defaultBlockLengthForSymKeyTypeAndKeyLength(final GordianNewSymKeyType pType,
                                                                     final GordianLength pKeyLength) {
        /* Create the map and return it */
        switch (pType) {
            case AES:
            case TWOFISH:
            case SERPENT:
            case CAMELLIA:
            case RC6:
            case CAST6:
            case ARIA:
            case SM4:
            case NOEKEON:
            case KUZNYECHIK:
            case SPECK:
            case ANUBIS:
            case SIMON:
            case MARS:
            case LEA:
            case RC5:
            case SEED:
                return GordianLength.LEN_128;
            case THREEFISH:
                return pKeyLength;
            case KALYNA:
                return GordianLength.LEN_512.equals(pKeyLength) ? GordianLength.LEN_256 : GordianLength.LEN_128;
            case TEA:
            case XTEA:
            case IDEA:
            case SKIPJACK:
            case RC2:
            case DESEDE:
            case CAST5:
            case BLOWFISH:
            case GOST:
                return GordianLength.LEN_64;
            case SHACAL2:
                return GordianLength.LEN_256;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain the default length for the symKeyType.
     *
     * @param pType the symKeyType
     * @return the block lengths
     */
    static GordianLength defaultKeyLengthForSymKeyType(final GordianNewSymKeyType pType) {
        /* Create the map and return it */
        switch (pType) {
            case AES:
            case TWOFISH:
            case SERPENT:
            case CAMELLIA:
            case RC6:
            case CAST6:
            case THREEFISH:
            case ARIA:
            case KALYNA:
            case KUZNYECHIK:
            case SPECK:
            case ANUBIS:
            case SIMON:
            case MARS:
            case LEA:
            case GOST:
            case SHACAL2:
                return GordianLength.LEN_256;
            case RC2:
            case NOEKEON:
            case RC5:
            case DESEDE:
            case CAST5:
            case BLOWFISH:
            case SEED:
            case TEA:
            case XTEA:
            case IDEA:
            case SKIPJACK:
            case SM4:
                return GordianLength.LEN_128;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check subFields */
        return pThat instanceof GordianCoreSymKeyType myThat
                && theType == myThat.getType();
    }

    @Override
    public int hashCode() {
        return theType.hashCode();
    }

    /**
     * Obtain the core type.
     *
     * @param pType the base type
     * @return the core type
     */
    public static GordianCoreSymKeyType mapCoreType(final Object pType) {
        return pType instanceof GordianNewSymKeyType myType ? TYPEMAP.get(myType) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianNewSymKeyType, GordianCoreSymKeyType> newTypeMap() {
        final Map<GordianNewSymKeyType, GordianCoreSymKeyType> myMap = new EnumMap<>(GordianNewSymKeyType.class);
        for (GordianNewSymKeyType myType : GordianNewSymKeyType.values()) {
            myMap.put(myType, new GordianCoreSymKeyType(myType));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreSymKeyType[] values() {
        return VALUES;
    }
}
