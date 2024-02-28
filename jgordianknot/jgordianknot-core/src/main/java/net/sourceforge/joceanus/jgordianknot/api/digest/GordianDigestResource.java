/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.digest;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jtethys.resource.TethysBundleId;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleLoader;

/**
 * Resource IDs for Digests package.
 */
public enum GordianDigestResource implements TethysBundleId {
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
    DIGEST_HARAKA("Haraka");

    /**
     * The Digest Map.
     */
    private static final Map<GordianDigestType, TethysBundleId> DIGEST_MAP = buildDigestMap();

    /**
     * The Resource Loader.
     */
    private static final TethysBundleLoader LOADER = TethysBundleLoader.getLoader(GordianDigest.class.getCanonicalName(),
            ResourceBundle::getBundle);

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

    /**
     * Build digest map.
     * @return the map
     */
    private static Map<GordianDigestType, TethysBundleId> buildDigestMap() {
        /* Create the map and return it */
        final Map<GordianDigestType, TethysBundleId> myMap = new EnumMap<>(GordianDigestType.class);
        myMap.put(GordianDigestType.SHA2, DIGEST_SHA2);
        myMap.put(GordianDigestType.TIGER, DIGEST_TIGER);
        myMap.put(GordianDigestType.WHIRLPOOL, DIGEST_WHIRLPOOL);
        myMap.put(GordianDigestType.RIPEMD, DIGEST_RIPEMD);
        myMap.put(GordianDigestType.STREEBOG, DIGEST_STREEBOG);
        myMap.put(GordianDigestType.GOST, DIGEST_GOST);
        myMap.put(GordianDigestType.SHA3, DIGEST_SHA3);
        myMap.put(GordianDigestType.SHAKE, DIGEST_SHAKE);
        myMap.put(GordianDigestType.SKEIN, DIGEST_SKEIN);
        myMap.put(GordianDigestType.SM3, DIGEST_SM3);
        myMap.put(GordianDigestType.BLAKE2, DIGEST_BLAKE2);
        myMap.put(GordianDigestType.BLAKE3, DIGEST_BLAKE3);
        myMap.put(GordianDigestType.KUPYNA, DIGEST_KUPYNA);
        myMap.put(GordianDigestType.SHA1, DIGEST_SHA1);
        myMap.put(GordianDigestType.MD5, DIGEST_MD5);
        myMap.put(GordianDigestType.MD4, DIGEST_MD4);
        myMap.put(GordianDigestType.MD2, DIGEST_MD2);
        myMap.put(GordianDigestType.JH, DIGEST_JH);
        myMap.put(GordianDigestType.GROESTL, DIGEST_GROESTL);
        myMap.put(GordianDigestType.CUBEHASH, DIGEST_CUBEHASH);
        myMap.put(GordianDigestType.KANGAROO, DIGEST_KANGAROO);
        myMap.put(GordianDigestType.HARAKA, DIGEST_HARAKA);
        return myMap;
    }

    /**
     * Obtain key for Digest.
     * @param pDigest the DigestType
     * @return the resource key
     */
    protected static TethysBundleId getKeyForDigest(final GordianDigestType pDigest) {
        return TethysBundleLoader.getKeyForEnum(DIGEST_MAP, pDigest);
    }
}
