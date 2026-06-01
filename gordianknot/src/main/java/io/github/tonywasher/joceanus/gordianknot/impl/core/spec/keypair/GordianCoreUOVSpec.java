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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianUOVSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.uov.UOVParameters;
import org.bouncycastle.pqc.jcajce.spec.UOVParameterSpec;

import java.util.EnumMap;
import java.util.Map;

/**
 * UOV KeySpec.
 */
public final class GordianCoreUOVSpec
        implements GordianCoreKeyPairIdSpec<GordianUOVSpec> {
    /**
     * The specMap.
     */
    private static final Map<GordianUOVSpec, GordianCoreUOVSpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreUOVSpec[] VALUES = SPECMAP.values().toArray(new GordianCoreUOVSpec[0]);

    /**
     * The Spec.
     */
    private final GordianUOVSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreUOVSpec(final GordianUOVSpec pSpec) {
        theSpec = pSpec;
    }

    @Override
    public GordianKeyPairType getKeyPairType() {
        return GordianKeyPairType.UOV;
    }

    @Override
    public GordianUOVSpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain UOV Parameters.
     *
     * @return the parameters.
     */
    public UOVParameters getParameters() {
        return switch (theSpec) {
            case UOV_IS_CLASSIC -> UOVParameters.uov_Is;
            case UOV_IS_PKC -> UOVParameters.uov_Is_pkc;
            case UOV_IS_PKC_SKC -> UOVParameters.uov_Is_pkc_skc;
            case UOV_IP_CLASSIC -> UOVParameters.uov_Ip;
            case UOV_IP_PKC -> UOVParameters.uov_Ip_pkc;
            case UOV_IP_PKC_SKC -> UOVParameters.uov_Ip_pkc_skc;
            case UOV_III_CLASSIC -> UOVParameters.uov_III;
            case UOV_III_PKC -> UOVParameters.uov_III_pkc;
            case UOV_III_PKC_SKC -> UOVParameters.uov_III_pkc_skc;
            case UOV_V_CLASSIC -> UOVParameters.uov_V;
            case UOV_V_PKC -> UOVParameters.uov_V_pkc;
            case UOV_V_PKC_SKC -> UOVParameters.uov_V_pkc_skc;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain UOV ParameterSpec.
     *
     * @return the parameters.
     */
    public UOVParameterSpec getParameterSpec() {
        return switch (theSpec) {
            case UOV_IS_CLASSIC -> UOVParameterSpec.uov_Is;
            case UOV_IS_PKC -> UOVParameterSpec.uov_Is_pkc;
            case UOV_IS_PKC_SKC -> UOVParameterSpec.uov_Is_pkc_skc;
            case UOV_IP_CLASSIC -> UOVParameterSpec.uov_Ip;
            case UOV_IP_PKC -> UOVParameterSpec.uov_Ip_pkc;
            case UOV_IP_PKC_SKC -> UOVParameterSpec.uov_Ip_pkc_skc;
            case UOV_III_CLASSIC -> UOVParameterSpec.uov_III;
            case UOV_III_PKC -> UOVParameterSpec.uov_III_pkc;
            case UOV_III_PKC_SKC -> UOVParameterSpec.uov_III_pkc_skc;
            case UOV_V_CLASSIC -> UOVParameterSpec.uov_V;
            case UOV_V_PKC -> UOVParameterSpec.uov_V_pkc;
            case UOV_V_PKC_SKC -> UOVParameterSpec.uov_V_pkc_skc;
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public ASN1ObjectIdentifier getIdentifier() {
        return switch (theSpec) {
            case UOV_IS_CLASSIC -> BCObjectIdentifiers.uov_Is_classic;
            case UOV_IS_PKC -> BCObjectIdentifiers.uov_Is_pkc;
            case UOV_IS_PKC_SKC -> BCObjectIdentifiers.uov_Is_pkc_skc;
            case UOV_IP_CLASSIC -> BCObjectIdentifiers.uov_Ip_classic;
            case UOV_IP_PKC -> BCObjectIdentifiers.uov_Ip_pkc;
            case UOV_IP_PKC_SKC -> BCObjectIdentifiers.uov_Ip_pkc_skc;
            case UOV_III_CLASSIC -> BCObjectIdentifiers.uov_III_classic;
            case UOV_III_PKC -> BCObjectIdentifiers.uov_III_pkc;
            case UOV_III_PKC_SKC -> BCObjectIdentifiers.uov_III_pkc_skc;
            case UOV_V_CLASSIC -> BCObjectIdentifiers.uov_V_classic;
            case UOV_V_PKC -> BCObjectIdentifiers.uov_V_pkc;
            case UOV_V_PKC_SKC -> BCObjectIdentifiers.uov_V_pkc_skc;
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public String toString() {
        return theSpec.toString();
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
        return pThat instanceof GordianCoreUOVSpec myThat
                && theSpec == myThat.getSpec();
    }

    @Override
    public int hashCode() {
        return theSpec.hashCode();
    }

    /**
     * Obtain the core spec.
     *
     * @param pSpec the base spec
     * @return the core spec
     */
    public static GordianCoreUOVSpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianUOVSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianUOVSpec, GordianCoreUOVSpec> newSpecMap() {
        final Map<GordianUOVSpec, GordianCoreUOVSpec> myMap = new EnumMap<>(GordianUOVSpec.class);
        for (GordianUOVSpec mySpec : GordianUOVSpec.values()) {
            myMap.put(mySpec, new GordianCoreUOVSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreUOVSpec[] values() {
        return VALUES;
    }
}
