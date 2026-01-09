/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.api.digest;

import net.sourceforge.joceanus.gordianknot.api.base.GordianBundleLoader;
import net.sourceforge.joceanus.gordianknot.api.base.GordianBundleLoader.GordianBundleId;

/**
 * Resource IDs for Digests package.
 */
public enum GordianDigestResource
        implements GordianBundleId {
    /**
     * Digest SHA2.
     */
    DIGEST_SHA2("SHA2"),

    /**
     * Digest Tiger.
     */
    DIGEST_TIGER("TIGER"),

    /**
     * Digest WhirlPool.
     */
    DIGEST_WHIRLPOOL("WHIRLPOOL"),

    /**
     * Digest SHA2.
     */
    DIGEST_RIPEMD("RIPEMD"),

    /**
     * Digest Streebog.
     */
    DIGEST_STREEBOG("STREEBOG"),

    /**
     * Digest GOST.
     */
    DIGEST_GOST("GOST"),

    /**
     * Digest SHA3.
     */
    DIGEST_SHA3("SHA3"),

    /**
     * Digest SHAKE.
     */
    DIGEST_SHAKE("SHAKE"),

    /**
     * Digest Skein.
     */
    DIGEST_SKEIN("SKEIN"),

    /**
     * Digest SM3.
     */
    DIGEST_SM3("SM3"),

    /**
     * Digest BLAKE2.
     */
    DIGEST_BLAKE2("BLAKE2"),

    /**
     * Digest BLAKE3.
     */
    DIGEST_BLAKE3("BLAKE3"),

    /**
     * Digest Kupyna.
     */
    DIGEST_KUPYNA("KUPYNA"),

    /**
     * Digest SHA.
     */
    DIGEST_SHA1("SHA1"),

    /**
     * Digest MD5.
     */
    DIGEST_MD5("MD5"),

    /**
     * Digest MD4.
     */
    DIGEST_MD4("MD4"),

    /**
     * Digest MD2.
     */
    DIGEST_MD2("MD2"),

    /**
     * Digest JH.
     */
    DIGEST_JH("JH"),

    /**
     * Digest Groestl.
     */
    DIGEST_GROESTL("GROESTL"),

    /**
     * Digest CubeHash.
     */
    DIGEST_CUBEHASH("CubeHash"),

    /**
     * Digest Kangaroo.
     */
    DIGEST_KANGAROO("Kangaroo"),

    /**
     * Digest Marsupilami.
     */
    DIGEST_MARSUPILAMI("Marsupilami"),

    /**
     * Digest Haraka.
     */
    DIGEST_HARAKA("Haraka"),

    /**
     * Digest Ascon.
     */
    DIGEST_ASCON("Ascon"),

    /**
     * Digest ISAP.
     */
    DIGEST_ISAP("ISAP"),

    /**
     * Digest PhotonBeetle.
     */
    DIGEST_PHOTONBEETLE("PhotonBeetle"),

    /**
     * Digest Romulus.
     */
    DIGEST_ROMULUS("Romulus"),

    /**
     * Digest Sparkle.
     */
    DIGEST_SPARKLE("Sparkle"),

    /**
     * Digest Haraka.
     */
    DIGEST_XOODYAK("Xoodyak");

    /**
     * The Resource Loader.
     */
    private static final GordianBundleLoader LOADER = GordianBundleLoader.getLoader(GordianDigestResource.class.getCanonicalName());

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * The Value.
     */
    private String theValue;

    /**
     * Constructor.
     * @param pKeyName the key name
     */
    GordianDigestResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "digest";
    }

    @Override
    public String getValue() {
        /* If we have not initialised the value */
        if (theValue == null) {
            /* Derive the value */
            theValue = LOADER.getValue(this);
        }

        /* return the value */
        return theValue;
    }
}
