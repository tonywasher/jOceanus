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

package io.github.tonywasher.joceanus.gordianknot.impl.core.sign.spec;

import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianNewSignatureType;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

public final class GordianCoreSignatureType {
    /**
     * The signatureTypeMap.
     */
    private static final Map<GordianNewSignatureType, GordianCoreSignatureType> TYPEMAP = newTypeMap();

    /**
     * The signatureType.
     */
    private final GordianNewSignatureType theType;

    /**
     * Constructor.
     *
     * @param pType the type
     */
    private GordianCoreSignatureType(final GordianNewSignatureType pType) {
        theType = pType;
    }

    /**
     * Obtain the type.
     *
     * @return the type
     */
    public GordianNewSignatureType getType() {
        return theType;
    }

    /**
     * Is this Signature supported for this AsymKeyType?
     *
     * @param pKeyType the asymKeyType
     * @return true/false
     */
    public boolean isSupported(final GordianKeyPairType pKeyType) {
        switch (theType) {
            case ISO9796D2:
            case PSSMGF1:
            case PSS128:
            case PSS256:
            case X931:
                return GordianKeyPairType.RSA == pKeyType;
            case NR:
                return GordianKeyPairType.EC == pKeyType;
            case PREHASH:
                return hasPreHash(pKeyType);
            case NATIVE:
                return hasNative(pKeyType);
            case DSA:
            case DDSA:
                return hasDSA(pKeyType);
            default:
                return false;
        }
    }

    /**
     * Does the AsymKeyType have a PreHash signature?
     *
     * @param pKeyType the asymKeyType
     * @return true/false
     */
    public static boolean hasPreHash(final GordianKeyPairType pKeyType) {
        switch (pKeyType) {
            case XMSS:
            case RSA:
                return true;
            default:
                return false;
        }
    }

    /**
     * Does the AsymKeyType have a DSA signature?
     *
     * @param pKeyType the asymKeyType
     * @return true/false
     */
    public static boolean hasDSA(final GordianKeyPairType pKeyType) {
        switch (pKeyType) {
            case EC:
            case DSA:
                return true;
            default:
                return false;
        }
    }

    /**
     * Does the AsymKeyType have a Native signature?
     *
     * @param pKeyType the asymKeyType
     * @return true/false
     */
    public static boolean hasNative(final GordianKeyPairType pKeyType) {
        switch (pKeyType) {
            case SM2:
            case EDDSA:
            case DSTU4145:
            case GOST2012:
            case SLHDSA:
            case MLDSA:
            case FALCON:
            case PICNIC:
            case MAYO:
            case SNOVA:
            case XMSS:
            case LMS:
            case COMPOSITE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is this signature type PSS?
     *
     * @return true/false
     */
    public boolean isPSS() {
        switch (theType) {
            case PSSMGF1:
            case PSS128:
            case PSS256:
                return true;
            default:
                return false;
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
        return pThat instanceof GordianCoreSignatureType myThat
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
    public static GordianCoreSignatureType mapCoreType(final GordianNewSignatureType pType) {
        return TYPEMAP.get(pType);
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianNewSignatureType, GordianCoreSignatureType> newTypeMap() {
        final Map<GordianNewSignatureType, GordianCoreSignatureType> myMap = new EnumMap<>(GordianNewSignatureType.class);
        for (GordianNewSignatureType myType : GordianNewSignatureType.values()) {
            myMap.put(myType, new GordianCoreSignatureType(myType));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static Collection<GordianCoreSignatureType> values() {
        return TYPEMAP.values();
    }
}
