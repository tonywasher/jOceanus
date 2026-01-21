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
package io.github.tonywasher.joceanus.gordianknot.api.digest;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianBundleLoader.GordianBundleId;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;

/**
 * DataDigest types. Available algorithms.
 */
public enum GordianDigestType {
    /**
     * SHA2.
     */
    SHA2(GordianLength.LEN_224, GordianLength.LEN_256, GordianLength.LEN_384, GordianLength.LEN_512),

    /**
     * Tiger.
     */
    TIGER(GordianLength.LEN_192),

    /**
     * WhirlPool.
     */
    WHIRLPOOL(GordianLength.LEN_512),

    /**
     * RIPEMD.
     */
    RIPEMD(GordianLength.LEN_128, GordianLength.LEN_160, GordianLength.LEN_256, GordianLength.LEN_320),

    /**
     * GOST2012.
     */
    STREEBOG(GordianLength.LEN_256, GordianLength.LEN_512),

    /**
     * GOST.
     */
    GOST(GordianLength.LEN_256),

    /**
     * SHA3.
     */
    SHA3(GordianLength.LEN_224, GordianLength.LEN_256, GordianLength.LEN_384, GordianLength.LEN_512),

    /**
     * SHAKE.
     */
    SHAKE(GordianLength.LEN_256, GordianLength.LEN_512),

    /**
     * Skein.
     */
    SKEIN(GordianLength.LEN_128, GordianLength.LEN_160, GordianLength.LEN_224, GordianLength.LEN_256, GordianLength.LEN_384, GordianLength.LEN_512, GordianLength.LEN_1024),

    /**
     * Kupyna.
     */
    KUPYNA(GordianLength.LEN_256, GordianLength.LEN_384, GordianLength.LEN_512),

    /**
     * SM3.
     */
    SM3(GordianLength.LEN_256),

    /**
     * Blake2.
     */
    BLAKE2(GordianLength.LEN_128, GordianLength.LEN_160, GordianLength.LEN_224, GordianLength.LEN_256, GordianLength.LEN_384, GordianLength.LEN_512),

    /**
     * SHA1.
     */
    SHA1(GordianLength.LEN_160),

    /**
     * MD5.
     */
    MD5(GordianLength.LEN_128),

    /**
     * MD4.
     */
    MD4(GordianLength.LEN_128),

    /**
     * MD2.
     */
    MD2(GordianLength.LEN_128),

    /**
     * JH.
     */
    JH(GordianLength.LEN_224, GordianLength.LEN_256, GordianLength.LEN_384, GordianLength.LEN_512),

    /**
     * GROESTL.
     */
    GROESTL(GordianLength.LEN_224, GordianLength.LEN_256, GordianLength.LEN_384, GordianLength.LEN_512),

    /**
     * CubeHash.
     */
    CUBEHASH(GordianLength.LEN_224, GordianLength.LEN_256, GordianLength.LEN_384, GordianLength.LEN_512),

    /**
     * Kangaroo.
     */
    KANGAROO(GordianLength.LEN_256, GordianLength.LEN_512),

    /**
     * Haraka.
     */
    HARAKA(GordianLength.LEN_256),

    /**
     * Blake3.
     */
    BLAKE3(GordianLength.LEN_256),

    /**
     * Ascon.
     */
    ASCON(GordianLength.LEN_256),

    /**
     * ISAP.
     */
    ISAP(GordianLength.LEN_256),

    /**
     * PhotonBeetle.
     */
    PHOTONBEETLE(GordianLength.LEN_256),

    /**
     * Romulus.
     */
    ROMULUS(GordianLength.LEN_256),

    /**
     * Sparkle.
     */
    SPARKLE(GordianLength.LEN_256, GordianLength.LEN_384),

    /**
     * Xoodyak.
     */
    XOODYAK(GordianLength.LEN_256);

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
     * @param pLengths the supported lengths
     */
    GordianDigestType(final GordianLength... pLengths) {
        theLengths = pLengths;
    }

    @Override
    public String toString() {
        if (theName == null) {
            theName = bundleIdForDigestType().getValue();
        }
        return theName;
    }

    /**
     * Obtain default length.
     *
     * @return the default length
     */
    public GordianLength getDefaultLength() {

        switch (this) {
            case MD2:
            case MD4:
            case MD5:
            case SHA1:
            case TIGER:
            case SM3:
            case GOST:
            case WHIRLPOOL:
            case HARAKA:
            case BLAKE3:
            case ASCON:
            case ISAP:
            case PHOTONBEETLE:
            case XOODYAK:
                return theLengths[0];
            case SHA2:
            case RIPEMD:
            case SHA3:
            case STREEBOG:
            case SHAKE:
            case SKEIN:
            case KUPYNA:
            case BLAKE2:
            case GROESTL:
            case JH:
            case CUBEHASH:
            case KANGAROO:
            case SPARKLE:
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
        return this != HARAKA;
    }

    /**
     * Is this digestType a natural Xof?
     *
     * @return true/false
     */
    public boolean isXof() {
        switch (this) {
            case SHAKE:
            case KANGAROO:
            case BLAKE3:
                return true;
            default:
                return false;
        }
    }

    /**
     * Obtain the resource bundleId for this digestType.
     *
     * @return the resource bundleId
     */
    private GordianBundleId bundleIdForDigestType() {
        /* Create the map and return it */
        switch (this) {
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
}
