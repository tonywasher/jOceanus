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
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKeyLengths;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.base.GordianBundleLoader.GordianBundleId;

import java.util.EnumMap;
import java.util.Map;

/**
 * Stream Key Types. Available algorithms.
 */
public final class GordianCoreStreamKeyType {
    /**
     * The streamKeyTypeMap.
     */
    private static final Map<GordianStreamKeyType, GordianCoreStreamKeyType> TYPEMAP = newTypeMap();

    /**
     * The streamKeyTypeArray.
     */
    private static final GordianCoreStreamKeyType[] VALUES = TYPEMAP.values().toArray(new GordianCoreStreamKeyType[0]);

    /**
     * The StreamKeyType.
     */
    private final GordianStreamKeyType theType;

    /**
     * The Name.
     */
    private final String theName;

    /**
     * Constructor.
     *
     * @param pType the type
     */
    private GordianCoreStreamKeyType(final GordianStreamKeyType pType) {
        theType = pType;
        theName = bundleIdForStreamKeyType(pType).getValue();
    }

    /**
     * Obtain the keyType.
     *
     * @return the keyType
     */
    public GordianStreamKeyType getType() {
        return theType;
    }

    /**
     * Does the keyType need a subKeyType?
     *
     * @return true/false.
     */
    public boolean needsSubKeyType() {
        return switch (theType) {
            case CHACHA20, SALSA20, VMPC, SKEINXOF, BLAKE2XOF, ELEPHANT, ISAP, ROMULUS, SPARKLE -> true;
            default -> false;
        };
    }

    @Override
    public String toString() {
        return theName;
    }

    /**
     * Is this KeyType valid for keyLength?
     *
     * @param pKeyLen the keyLength
     * @return true/false
     */
    public boolean validForKeyLength(final GordianLength pKeyLen) {
        /* Reject unsupported keyLengths */
        if (!GordianKeyLengths.isSupportedLength(pKeyLen)) {
            return false;
        }

        /* Switch on keyType */
        return switch (theType) {
            case GRAIN, RABBIT, SNOW3G, ASCON, ELEPHANT, ISAP, PHOTONBEETLE, ROMULUS, XOODYAK ->
                    GordianLength.LEN_128 == pKeyLen;
            case HC, CHACHA20, SALSA20, SOSEMANUK, ZUC -> GordianLength.LEN_128 == pKeyLen
                    || GordianLength.LEN_256 == pKeyLen;
            case BLAKE2XOF -> GordianLength.LEN_1024 != pKeyLen;
            case BLAKE3XOF -> GordianLength.LEN_256 == pKeyLen;
            default -> true;
        };
    }

    /**
     * Is this KeyType valid for largeData?
     *
     * @return true/false
     */
    public boolean supportsLargeData() {
        return supportsLargeData(theType);
    }

    /**
     * Is this KeyType valid for largeData?
     *
     * @param pType the keyType
     * @return true/false
     */
    public static boolean supportsLargeData(final GordianStreamKeyType pType) {
        return switch (pType) {
            case SNOW3G, ZUC -> false;
            default -> true;
        };
    }

    /**
     * Does the keyType need reInit after finish?
     *
     * @return true/false.
     */
    public boolean needsReInit() {
        return switch (theType) {
            case ASCON, ELEPHANT, ISAP, PHOTONBEETLE, SPARKLE, XOODYAK -> true;
            default -> false;
        };
    }

    /**
     * Obtain the resource bundleId for this streamKeyType.
     *
     * @param pType the streamKeyType
     * @return the resource bundleId
     */
    private static GordianBundleId bundleIdForStreamKeyType(final GordianStreamKeyType pType) {
        /* Create the map and return it */
        return switch (pType) {
            case SALSA20 -> GordianCipherResource.STREAMKEY_SALSA20;
            case HC -> GordianCipherResource.STREAMKEY_HC;
            case CHACHA20 -> GordianCipherResource.STREAMKEY_CHACHA;
            case VMPC -> GordianCipherResource.STREAMKEY_VMPC;
            case ISAAC -> GordianCipherResource.STREAMKEY_ISAAC;
            case GRAIN -> GordianCipherResource.STREAMKEY_GRAIN;
            case RC4 -> GordianCipherResource.STREAMKEY_RC4;
            case SOSEMANUK -> GordianCipherResource.STREAMKEY_SOSEMANUK;
            case RABBIT -> GordianCipherResource.STREAMKEY_RABBIT;
            case SNOW3G -> GordianCipherResource.STREAMKEY_SNOW3G;
            case ZUC -> GordianCipherResource.STREAMKEY_ZUC;
            case SKEINXOF -> GordianCipherResource.STREAMKEY_SKEIN;
            case BLAKE2XOF -> GordianCipherResource.STREAMKEY_BLAKE2;
            case BLAKE3XOF -> GordianCipherResource.STREAMKEY_BLAKE3;
            case ASCON -> GordianCipherResource.STREAMKEY_ASCON;
            case ELEPHANT -> GordianCipherResource.STREAMKEY_ELEPHANT;
            case ISAP -> GordianCipherResource.STREAMKEY_ISAP;
            case PHOTONBEETLE -> GordianCipherResource.STREAMKEY_PHOTONBEETLE;
            case ROMULUS -> GordianCipherResource.STREAMKEY_ROMULUS;
            case SPARKLE -> GordianCipherResource.STREAMKEY_SPARKLE;
            case XOODYAK -> GordianCipherResource.STREAMKEY_XOODYAK;
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
        return pThat instanceof GordianCoreStreamKeyType myThat
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
    public static GordianCoreStreamKeyType mapCoreType(final Object pType) {
        return pType instanceof GordianStreamKeyType myType ? TYPEMAP.get(myType) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianStreamKeyType, GordianCoreStreamKeyType> newTypeMap() {
        final Map<GordianStreamKeyType, GordianCoreStreamKeyType> myMap = new EnumMap<>(GordianStreamKeyType.class);
        for (GordianStreamKeyType myType : GordianStreamKeyType.values()) {
            myMap.put(myType, new GordianCoreStreamKeyType(myType));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreStreamKeyType[] values() {
        return VALUES;
    }
}
