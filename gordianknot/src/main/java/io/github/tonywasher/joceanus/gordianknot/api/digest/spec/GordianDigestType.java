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

package io.github.tonywasher.joceanus.gordianknot.api.digest.spec;

/**
 * DataDigest types. Available algorithms.
 */
public enum GordianDigestType {
    /**
     * SHA2.
     */
    SHA2,

    /**
     * Tiger.
     */
    TIGER,

    /**
     * WhirlPool.
     */
    WHIRLPOOL,

    /**
     * RIPEMD.
     */
    RIPEMD,

    /**
     * GOST2012.
     */
    STREEBOG,

    /**
     * GOST.
     */
    GOST,

    /**
     * SHA3.
     */
    SHA3,

    /**
     * SHAKE.
     */
    SHAKE,

    /**
     * Skein.
     */
    SKEIN,

    /**
     * Kupyna.
     */
    KUPYNA,

    /**
     * SM3.
     */
    SM3,

    /**
     * Blake2.
     */
    BLAKE2,

    /**
     * SHA1.
     */
    SHA1,

    /**
     * MD5.
     */
    MD5,

    /**
     * MD4.
     */
    MD4,

    /**
     * MD2.
     */
    MD2,

    /**
     * JH.
     */
    JH,

    /**
     * GROESTL.
     */
    GROESTL,

    /**
     * CubeHash.
     */
    CUBEHASH,

    /**
     * Kangaroo.
     */
    KANGAROO,

    /**
     * Haraka.
     */
    HARAKA,

    /**
     * Blake3.
     */
    BLAKE3,

    /**
     * Ascon.
     */
    ASCON,

    /**
     * ISAP.
     */
    ISAP,

    /**
     * PhotonBeetle.
     */
    PHOTONBEETLE,

    /**
     * Romulus.
     */
    ROMULUS,

    /**
     * Sparkle.
     */
    SPARKLE,

    /**
     * Xoodyak.
     */
    XOODYAK;
}
