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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.mac;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewMacType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.base.GordianBundleLoader.GordianBundleId;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCipherResource;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymKeyType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Mac types. Available algorithms.
 */
public final class GordianCoreMacType {
    /**
     * The macTypeMap.
     */
    private static final Map<GordianNewMacType, GordianCoreMacType> TYPEMAP = newTypeMap();

    /**
     * The macTypeArray.
     */
    private static final GordianCoreMacType[] VALUES = TYPEMAP.values().toArray(new GordianCoreMacType[0]);

    /**
     * The MacType.
     */
    private final GordianNewMacType theType;

    /**
     * The Name.
     */
    private final String theName;

    /**
     * Constructor.
     *
     * @param pType the type
     */
    private GordianCoreMacType(final GordianNewMacType pType) {
        theType = pType;
        theName = bundleIdForMacType(pType).getValue();
    }

    /**
     * Obtain the type.
     *
     * @return the type
     */
    public GordianNewMacType getType() {
        return theType;
    }

    @Override
    public String toString() {
        return theName;
    }

    /**
     * Needs re-initialisation after final.
     *
     * @return true/false
     */
    public boolean needsReInitialisation() {
        return needsReInitialisation(theType);
    }

    /**
     * Does the macType need re-initialisation after final.
     *
     * @param pMacType the macType
     * @return true/false
     */
    public static boolean needsReInitialisation(final GordianNewMacType pMacType) {
        return pMacType == GordianNewMacType.GMAC;
    }

    /**
     * Is this MacType valid for largeData?
     *
     * @return true/false
     */
    public boolean supportsLargeData() {
        return supportsLargeData(theType);
    }

    /**
     * Is the MacType valid for largeData?
     *
     * @param pMacType the macType
     * @return true/false
     */
    public static boolean supportsLargeData(final GordianNewMacType pMacType) {
        return pMacType != GordianNewMacType.ZUC;
    }

    /**
     * Is this MacType valid for keyLength?
     *
     * @param pKeyLen the keyLength
     * @return true/false
     */
    public boolean validForKeyLength(final GordianLength pKeyLen) {
        switch (theType) {
            case POLY1305:
            case GOST:
            case BLAKE3:
                return GordianLength.LEN_256 == pKeyLen;
            case ZUC:
                return GordianLength.LEN_128 == pKeyLen
                        || GordianLength.LEN_256 == pKeyLen;
            case KALYNA:
                return GordianCoreSymKeyType.validForKeyLength(GordianNewSymKeyType.KALYNA, pKeyLen);
            case SIPHASH:
                return GordianLength.LEN_128 == pKeyLen;
            default:
                return true;
        }
    }

    /**
     * Obtain the resource bundleId for this macType.
     *
     * @param pType the macType
     * @return the resource bundleId
     */
    private static GordianBundleId bundleIdForMacType(final GordianNewMacType pType) {
        /* Create the map and return it */
        switch (pType) {
            case HMAC:
                return GordianMacResource.MAC_HMAC;
            case GMAC:
                return GordianMacResource.MAC_GMAC;
            case CMAC:
                return GordianMacResource.MAC_CMAC;
            case KMAC:
                return GordianMacResource.MAC_KMAC;
            case POLY1305:
                return GordianMacResource.MAC_POLY;
            case SKEIN:
                return GordianMacResource.MAC_SKEIN;
            case KALYNA:
                return GordianMacResource.MAC_KALYNA;
            case KUPYNA:
                return GordianMacResource.MAC_KUPYNA;
            case BLAKE2:
                return GordianMacResource.MAC_BLAKE2;
            case BLAKE3:
                return GordianMacResource.MAC_BLAKE3;
            case VMPC:
                return GordianMacResource.MAC_VMPC;
            case ZUC:
                return GordianCipherResource.STREAMKEY_ZUC;
            case CBCMAC:
                return GordianMacResource.MAC_CBC;
            case CFBMAC:
                return GordianMacResource.MAC_CFB;
            case SIPHASH:
                return GordianMacResource.MAC_SIPHASH;
            case GOST:
                return GordianMacResource.MAC_GOST;
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
        return pThat instanceof GordianCoreMacType myThat
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
    public static GordianCoreMacType mapCoreType(final Object pType) {
        return pType instanceof GordianNewMacType myType ? TYPEMAP.get(myType) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianNewMacType, GordianCoreMacType> newTypeMap() {
        final Map<GordianNewMacType, GordianCoreMacType> myMap = new EnumMap<>(GordianNewMacType.class);
        for (GordianNewMacType myType : GordianNewMacType.values()) {
            myMap.put(myType, new GordianCoreMacType(myType));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreMacType[] values() {
        return VALUES;
    }
}
