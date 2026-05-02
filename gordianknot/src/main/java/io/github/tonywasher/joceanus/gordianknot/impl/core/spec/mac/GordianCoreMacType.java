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
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianMacType;
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
    private static final Map<GordianMacType, GordianCoreMacType> TYPEMAP = newTypeMap();

    /**
     * The macTypeArray.
     */
    private static final GordianCoreMacType[] VALUES = TYPEMAP.values().toArray(new GordianCoreMacType[0]);

    /**
     * The MacType.
     */
    private final GordianMacType theType;

    /**
     * The Name.
     */
    private final String theName;

    /**
     * Constructor.
     *
     * @param pType the type
     */
    private GordianCoreMacType(final GordianMacType pType) {
        theType = pType;
        theName = bundleIdForMacType(pType).getValue();
    }

    /**
     * Obtain the type.
     *
     * @return the type
     */
    public GordianMacType getType() {
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
    public static boolean needsReInitialisation(final GordianMacType pMacType) {
        return pMacType == GordianMacType.GMAC;
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
    public static boolean supportsLargeData(final GordianMacType pMacType) {
        return pMacType != GordianMacType.ZUC;
    }

    /**
     * Is this MacType valid for keyLength?
     *
     * @param pKeyLen the keyLength
     * @return true/false
     */
    public boolean validForKeyLength(final GordianLength pKeyLen) {
        return switch (theType) {
            case POLY1305, GOST, BLAKE3 -> GordianLength.LEN_256 == pKeyLen;
            case ZUC -> GordianLength.LEN_128 == pKeyLen
                    || GordianLength.LEN_256 == pKeyLen;
            case KALYNA -> GordianCoreSymKeyType.validForKeyLength(GordianSymKeyType.KALYNA, pKeyLen);
            case SIPHASH -> GordianLength.LEN_128 == pKeyLen;
            default -> true;
        };
    }

    /**
     * Obtain the resource bundleId for this macType.
     *
     * @param pType the macType
     * @return the resource bundleId
     */
    private static GordianBundleId bundleIdForMacType(final GordianMacType pType) {
        /* Create the map and return it */
        return switch (pType) {
            case HMAC -> GordianMacResource.MAC_HMAC;
            case GMAC -> GordianMacResource.MAC_GMAC;
            case CMAC -> GordianMacResource.MAC_CMAC;
            case KMAC -> GordianMacResource.MAC_KMAC;
            case POLY1305 -> GordianMacResource.MAC_POLY;
            case SKEIN -> GordianMacResource.MAC_SKEIN;
            case KALYNA -> GordianMacResource.MAC_KALYNA;
            case KUPYNA -> GordianMacResource.MAC_KUPYNA;
            case BLAKE2 -> GordianMacResource.MAC_BLAKE2;
            case BLAKE3 -> GordianMacResource.MAC_BLAKE3;
            case VMPC -> GordianMacResource.MAC_VMPC;
            case ZUC -> GordianCipherResource.STREAMKEY_ZUC;
            case CBCMAC -> GordianMacResource.MAC_CBC;
            case CFBMAC -> GordianMacResource.MAC_CFB;
            case SIPHASH -> GordianMacResource.MAC_SIPHASH;
            case GOST -> GordianMacResource.MAC_GOST;
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
        return pType instanceof GordianMacType myType ? TYPEMAP.get(myType) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianMacType, GordianCoreMacType> newTypeMap() {
        final Map<GordianMacType, GordianCoreMacType> myMap = new EnumMap<>(GordianMacType.class);
        for (GordianMacType myType : GordianMacType.values()) {
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
