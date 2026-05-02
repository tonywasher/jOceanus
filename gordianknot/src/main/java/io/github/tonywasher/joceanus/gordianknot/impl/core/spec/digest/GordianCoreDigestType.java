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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.base.GordianBundleLoader.GordianBundleId;

import java.util.EnumMap;
import java.util.Map;

/**
 * DataDigest types. Available algorithms.
 */
public final class GordianCoreDigestType {
    /**
     * The digestTypeMap.
     */
    private static final Map<GordianDigestType, GordianCoreDigestType> TYPEMAP = newTypeMap();

    /**
     * The digestTypeArray.
     */
    private static final GordianCoreDigestType[] VALUES = TYPEMAP.values().toArray(new GordianCoreDigestType[0]);

    /**
     * The DigestType.
     */
    private final GordianDigestType theType;

    /**
     * The Supported lengths.
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
    private GordianCoreDigestType(final GordianDigestType pType) {
        theType = pType;
        theLengths = lengthsForDigestType(pType);
        theName = bundleIdForDigestType(pType).getValue();
    }

    /**
     * Obtain the type.
     *
     * @return the type
     */
    public GordianDigestType getType() {
        return theType;
    }

    @Override
    public String toString() {
        return theName;
    }

    /**
     * Obtain default length.
     *
     * @param pType the digestType
     * @return the default length
     */
    public static GordianLength getDefaultLength(final GordianDigestType pType) {
        switch (pType) {
            case MD2:
            case MD4:
            case MD5:
                return GordianLength.LEN_128;
            case SHA1:
                return GordianLength.LEN_160;
            case TIGER:
                return GordianLength.LEN_192;
            case WHIRLPOOL:
                return GordianLength.LEN_512;
            default:
                return GordianLength.LEN_256;
        }
    }

    /**
     * Obtain supported lengths.
     *
     * @return the supported lengths (first is default)
     */
    public GordianLength[] getSupportedLengths() {
        return theLengths;
    }

    /**
     * Obtain supported lengths for a type.
     *
     * @param pType the type
     * @return the supported lengths (first is default)
     */
    public static GordianLength[] getSupportedLengths(final GordianDigestType pType) {
        final GordianCoreDigestType myType = mapCoreType(pType);
        return myType == null ? new GordianLength[0] : myType.getSupportedLengths();
    }

    /**
     * is length valid?
     *
     * @param pLength the length
     * @return true/false
     */
    public boolean isLengthValid(final GordianLength pLength) {
        return isLengthValid(theType, pLength);
    }

    /**
     * is digestLength valid?
     *
     * @param pType   the digestType
     * @param pLength the length
     * @return true/false
     */
    public static boolean isLengthValid(final GordianDigestType pType,
                                        final GordianLength pLength) {
        for (final GordianLength myLength : getSupportedLengths(pType)) {
            if (myLength.equals(pLength)) {
                return true;
            }
        }
        return false;
    }

    /**
     * does this digest support large amounts of data?
     *
     * @return true/false
     */
    public boolean supportsLargeData() {
        return supportsLargeData(theType);
    }

    /**
     * does the digest support large amounts of data?
     *
     * @param pType the digestType
     * @return true/false
     */
    public static boolean supportsLargeData(final GordianDigestType pType) {
        return pType != GordianDigestType.HARAKA;
    }

    /**
     * Is this digestType a natural Xof?
     *
     * @return true/false
     */
    public boolean isXof() {
        return switch (theType) {
            case SHAKE, KANGAROO, BLAKE3 -> true;
            default -> false;
        };
    }

    /**
     * Obtain the resource bundleId for the digestType.
     *
     * @param pType the digestType
     * @return the resource bundleId
     */
    private static GordianBundleId bundleIdForDigestType(final GordianDigestType pType) {
        /* Create the map and return it */
        return switch (pType) {
            case SHA2 -> GordianDigestResource.DIGEST_SHA2;
            case TIGER -> GordianDigestResource.DIGEST_TIGER;
            case WHIRLPOOL -> GordianDigestResource.DIGEST_WHIRLPOOL;
            case RIPEMD -> GordianDigestResource.DIGEST_RIPEMD;
            case STREEBOG -> GordianDigestResource.DIGEST_STREEBOG;
            case GOST -> GordianDigestResource.DIGEST_GOST;
            case SHA3 -> GordianDigestResource.DIGEST_SHA3;
            case SHAKE -> GordianDigestResource.DIGEST_SHAKE;
            case SKEIN -> GordianDigestResource.DIGEST_SKEIN;
            case SM3 -> GordianDigestResource.DIGEST_SM3;
            case BLAKE2 -> GordianDigestResource.DIGEST_BLAKE2;
            case BLAKE3 -> GordianDigestResource.DIGEST_BLAKE3;
            case KUPYNA -> GordianDigestResource.DIGEST_KUPYNA;
            case SHA1 -> GordianDigestResource.DIGEST_SHA1;
            case MD5 -> GordianDigestResource.DIGEST_MD5;
            case MD4 -> GordianDigestResource.DIGEST_MD4;
            case MD2 -> GordianDigestResource.DIGEST_MD2;
            case JH -> GordianDigestResource.DIGEST_JH;
            case GROESTL -> GordianDigestResource.DIGEST_GROESTL;
            case CUBEHASH -> GordianDigestResource.DIGEST_CUBEHASH;
            case KANGAROO -> GordianDigestResource.DIGEST_KANGAROO;
            case HARAKA -> GordianDigestResource.DIGEST_HARAKA;
            case ASCON -> GordianDigestResource.DIGEST_ASCON;
            case ISAP -> GordianDigestResource.DIGEST_ISAP;
            case PHOTONBEETLE -> GordianDigestResource.DIGEST_PHOTONBEETLE;
            case ROMULUS -> GordianDigestResource.DIGEST_ROMULUS;
            case SPARKLE -> GordianDigestResource.DIGEST_SPARKLE;
            case XOODYAK -> GordianDigestResource.DIGEST_XOODYAK;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain the available lengths for the digestType.
     *
     * @param pType the digestType
     * @return the available lengths
     */
    private static GordianLength[] lengthsForDigestType(final GordianDigestType pType) {
        /* Create the map and return it */
        return switch (pType) {
            case SHA2, SHA3, JH, GROESTL, CUBEHASH -> new GordianLength[]{
                    GordianLength.LEN_224, GordianLength.LEN_256, GordianLength.LEN_384, GordianLength.LEN_512
            };
            case TIGER -> new GordianLength[]{GordianLength.LEN_192};
            case WHIRLPOOL -> new GordianLength[]{GordianLength.LEN_512};
            case RIPEMD -> new GordianLength[]{
                    GordianLength.LEN_128, GordianLength.LEN_160, GordianLength.LEN_256, GordianLength.LEN_320
            };
            case STREEBOG, SHAKE, KANGAROO -> new GordianLength[]{
                    GordianLength.LEN_256, GordianLength.LEN_512
            };
            case GOST, SM3, HARAKA, BLAKE3, ASCON, ISAP, PHOTONBEETLE, ROMULUS, XOODYAK ->
                    new GordianLength[]{GordianLength.LEN_256};
            case SPARKLE -> new GordianLength[]{
                    GordianLength.LEN_256, GordianLength.LEN_384
            };
            case SKEIN -> new GordianLength[]{
                    GordianLength.LEN_128, GordianLength.LEN_160, GordianLength.LEN_224, GordianLength.LEN_256,
                    GordianLength.LEN_384, GordianLength.LEN_512, GordianLength.LEN_1024
            };
            case BLAKE2 -> new GordianLength[]{
                    GordianLength.LEN_128, GordianLength.LEN_160, GordianLength.LEN_224,
                    GordianLength.LEN_256, GordianLength.LEN_384, GordianLength.LEN_512
            };
            case KUPYNA -> new GordianLength[]{
                    GordianLength.LEN_256, GordianLength.LEN_384, GordianLength.LEN_512
            };
            case SHA1 -> new GordianLength[]{GordianLength.LEN_160};
            case MD5, MD4, MD2 -> new GordianLength[]{GordianLength.LEN_128};
            default -> throw new IllegalArgumentException();
        };
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
        return pThat instanceof GordianCoreDigestType myThat
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
    public static GordianCoreDigestType mapCoreType(final Object pType) {
        return pType instanceof GordianDigestType myType ? TYPEMAP.get(myType) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianDigestType, GordianCoreDigestType> newTypeMap() {
        final Map<GordianDigestType, GordianCoreDigestType> myMap = new EnumMap<>(GordianDigestType.class);
        for (GordianDigestType myType : GordianDigestType.values()) {
            myMap.put(myType, new GordianCoreDigestType(myType));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreDigestType[] values() {
        return VALUES;
    }
}
