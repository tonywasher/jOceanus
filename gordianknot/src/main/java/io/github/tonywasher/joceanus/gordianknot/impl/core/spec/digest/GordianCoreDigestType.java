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

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianBundleLoader.GordianBundleId;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigestResource;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestType;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * DataDigest types. Available algorithms.
 */
public final class GordianCoreDigestType {
    /**
     * The digestTypeMap.
     */
    private static final Map<GordianNewDigestType, GordianCoreDigestType> TYPEMAP = newTypeMap();

    /**
     * The DigestType.
     */
    private final GordianNewDigestType theType;

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
    private GordianCoreDigestType(final GordianNewDigestType pType) {
        theType = pType;
        theLengths = lengthsForDigestType(pType);
        theName = bundleIdForDigestType(pType).getValue();
    }

    /**
     * Obtain the type.
     *
     * @return the type
     */
    public GordianNewDigestType getType() {
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
    static GordianLength getDefaultLength(final GordianNewDigestType pType) {
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
    public static GordianLength[] getSupportedLengths(final GordianNewDigestType pType) {
        return mapCoreType(pType).getSupportedLengths();
    }

    /**
     * is length valid?
     *
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
     * does this digest support large amounts of data?
     *
     * @return true/false
     */
    public boolean supportsLargeData() {
        return theType != GordianNewDigestType.HARAKA;
    }

    /**
     * Is this digestType a natural Xof?
     *
     * @return true/false
     */
    public boolean isXof() {
        switch (theType) {
            case SHAKE:
            case KANGAROO:
            case BLAKE3:
                return true;
            default:
                return false;
        }
    }

    /**
     * Obtain the resource bundleId for the digestType.
     *
     * @param pType the digestType
     * @return the resource bundleId
     */
    private static GordianBundleId bundleIdForDigestType(final GordianNewDigestType pType) {
        /* Create the map and return it */
        switch (pType) {
            case SHA2:
                return GordianDigestResource.DIGEST_SHA2;
            case TIGER:
                return GordianDigestResource.DIGEST_TIGER;
            case WHIRLPOOL:
                return GordianDigestResource.DIGEST_WHIRLPOOL;
            case RIPEMD:
                return GordianDigestResource.DIGEST_RIPEMD;
            case STREEBOG:
                return GordianDigestResource.DIGEST_STREEBOG;
            case GOST:
                return GordianDigestResource.DIGEST_GOST;
            case SHA3:
                return GordianDigestResource.DIGEST_SHA3;
            case SHAKE:
                return GordianDigestResource.DIGEST_SHAKE;
            case SKEIN:
                return GordianDigestResource.DIGEST_SKEIN;
            case SM3:
                return GordianDigestResource.DIGEST_SM3;
            case BLAKE2:
                return GordianDigestResource.DIGEST_BLAKE2;
            case BLAKE3:
                return GordianDigestResource.DIGEST_BLAKE3;
            case KUPYNA:
                return GordianDigestResource.DIGEST_KUPYNA;
            case SHA1:
                return GordianDigestResource.DIGEST_SHA1;
            case MD5:
                return GordianDigestResource.DIGEST_MD5;
            case MD4:
                return GordianDigestResource.DIGEST_MD4;
            case MD2:
                return GordianDigestResource.DIGEST_MD2;
            case JH:
                return GordianDigestResource.DIGEST_JH;
            case GROESTL:
                return GordianDigestResource.DIGEST_GROESTL;
            case CUBEHASH:
                return GordianDigestResource.DIGEST_CUBEHASH;
            case KANGAROO:
                return GordianDigestResource.DIGEST_KANGAROO;
            case HARAKA:
                return GordianDigestResource.DIGEST_HARAKA;
            case ASCON:
                return GordianDigestResource.DIGEST_ASCON;
            case ISAP:
                return GordianDigestResource.DIGEST_ISAP;
            case PHOTONBEETLE:
                return GordianDigestResource.DIGEST_PHOTONBEETLE;
            case ROMULUS:
                return GordianDigestResource.DIGEST_ROMULUS;
            case SPARKLE:
                return GordianDigestResource.DIGEST_SPARKLE;
            case XOODYAK:
                return GordianDigestResource.DIGEST_XOODYAK;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain the available lengths for the digestType.
     *
     * @param pType the digestType
     * @return the available lengths
     */
    private static GordianLength[] lengthsForDigestType(final GordianNewDigestType pType) {
        /* Create the map and return it */
        switch (pType) {
            case SHA2:
            case SHA3:
            case JH:
            case GROESTL:
            case CUBEHASH:
                return new GordianLength[]{
                        GordianLength.LEN_224, GordianLength.LEN_256, GordianLength.LEN_384, GordianLength.LEN_512
                };
            case TIGER:
                return new GordianLength[]{GordianLength.LEN_192};
            case WHIRLPOOL:
                return new GordianLength[]{GordianLength.LEN_512};
            case RIPEMD:
                return new GordianLength[]{
                        GordianLength.LEN_128, GordianLength.LEN_160, GordianLength.LEN_256, GordianLength.LEN_320
                };
            case STREEBOG:
            case SHAKE:
            case KANGAROO:
                return new GordianLength[]{
                        GordianLength.LEN_256, GordianLength.LEN_512
                };
            case GOST:
            case SM3:
            case HARAKA:
            case BLAKE3:
            case ASCON:
            case ISAP:
            case PHOTONBEETLE:
            case ROMULUS:
            case XOODYAK:
                return new GordianLength[]{GordianLength.LEN_256};
            case SPARKLE:
                return new GordianLength[]{
                        GordianLength.LEN_256, GordianLength.LEN_384
                };
            case SKEIN:
                return new GordianLength[]{
                        GordianLength.LEN_128, GordianLength.LEN_160, GordianLength.LEN_224, GordianLength.LEN_256,
                        GordianLength.LEN_384, GordianLength.LEN_512, GordianLength.LEN_1024
                };
            case BLAKE2:
                return new GordianLength[]{
                        GordianLength.LEN_128, GordianLength.LEN_160, GordianLength.LEN_224,
                        GordianLength.LEN_256, GordianLength.LEN_384, GordianLength.LEN_512
                };
            case KUPYNA:
                return new GordianLength[]{
                        GordianLength.LEN_256, GordianLength.LEN_384, GordianLength.LEN_512
                };
            case SHA1:
                return new GordianLength[]{GordianLength.LEN_160};
            case MD5:
            case MD4:
            case MD2:
                return new GordianLength[]{GordianLength.LEN_128};
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
        return pType instanceof GordianNewDigestType myType ? TYPEMAP.get(myType) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianNewDigestType, GordianCoreDigestType> newTypeMap() {
        final Map<GordianNewDigestType, GordianCoreDigestType> myMap = new EnumMap<>(GordianNewDigestType.class);
        for (GordianNewDigestType myType : GordianNewDigestType.values()) {
            myMap.put(myType, new GordianCoreDigestType(myType));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static Collection<GordianCoreDigestType> values() {
        return TYPEMAP.values();
    }
}
