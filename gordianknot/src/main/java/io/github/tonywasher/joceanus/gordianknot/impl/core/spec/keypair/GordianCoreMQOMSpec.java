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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianMQOMSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.mqom.MQOMParameters;
import org.bouncycastle.pqc.jcajce.spec.MQOMParameterSpec;

import java.util.EnumMap;
import java.util.Map;

/**
 * MQOM KeySpec.
 */
public final class GordianCoreMQOMSpec
        implements GordianCoreKeyPairIdSpec<GordianMQOMSpec> {
    /**
     * The specMap.
     */
    private static final Map<GordianMQOMSpec, GordianCoreMQOMSpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreMQOMSpec[] VALUES = SPECMAP.values().toArray(new GordianCoreMQOMSpec[0]);

    /**
     * The Spec.
     */
    private final GordianMQOMSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreMQOMSpec(final GordianMQOMSpec pSpec) {
        theSpec = pSpec;
    }

    @Override
    public GordianKeyPairType getKeyPairType() {
        return GordianKeyPairType.MQOM;
    }

    @Override
    public GordianMQOMSpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain MQOM Parameters.
     *
     * @return the parameters.
     */
    public MQOMParameters getParameters() {
        return switch (theSpec) {
            case CAT1GF2FASTR3 -> MQOMParameters.mqom2_cat1_gf2_fast_r3;
            case CAT1GF2FASTR5 -> MQOMParameters.mqom2_cat1_gf2_fast_r5;
            case CAT1GF2SHORTR3 -> MQOMParameters.mqom2_cat1_gf2_short_r3;
            case CAT1GF2SHORTR5 -> MQOMParameters.mqom2_cat1_gf2_short_r5;
            case CAT1GF16FASTR3 -> MQOMParameters.mqom2_cat1_gf16_fast_r3;
            case CAT1GF16FASTR5 -> MQOMParameters.mqom2_cat1_gf16_fast_r5;
            case CAT1GF16SHORTR3 -> MQOMParameters.mqom2_cat1_gf16_short_r3;
            case CAT1GF16SHORTR5 -> MQOMParameters.mqom2_cat1_gf16_short_r5;
            case CAT1GF256FASTR3 -> MQOMParameters.mqom2_cat1_gf256_fast_r3;
            case CAT1GF256FASTR5 -> MQOMParameters.mqom2_cat1_gf256_fast_r5;
            case CAT1GF256SHORTR3 -> MQOMParameters.mqom2_cat1_gf256_short_r3;
            case CAT1GF256SHORTR5 -> MQOMParameters.mqom2_cat1_gf256_short_r5;
            case CAT3GF2FASTR3 -> MQOMParameters.mqom2_cat3_gf2_fast_r3;
            case CAT3GF2FASTR5 -> MQOMParameters.mqom2_cat3_gf2_fast_r5;
            case CAT3GF2SHORTR3 -> MQOMParameters.mqom2_cat3_gf2_short_r3;
            case CAT3GF2SHORTR5 -> MQOMParameters.mqom2_cat3_gf2_short_r5;
            case CAT3GF16FASTR3 -> MQOMParameters.mqom2_cat3_gf16_fast_r3;
            case CAT3GF16FASTR5 -> MQOMParameters.mqom2_cat3_gf16_fast_r5;
            case CAT3GF16SHORTR3 -> MQOMParameters.mqom2_cat3_gf16_short_r3;
            case CAT3GF16SHORTR5 -> MQOMParameters.mqom2_cat3_gf16_short_r5;
            case CAT3GF256FASTR3 -> MQOMParameters.mqom2_cat3_gf256_fast_r3;
            case CAT3GF256FASTR5 -> MQOMParameters.mqom2_cat3_gf256_fast_r5;
            case CAT3GF256SHORTR3 -> MQOMParameters.mqom2_cat3_gf256_short_r3;
            case CAT3GF256SHORTR5 -> MQOMParameters.mqom2_cat3_gf256_short_r5;
            case CAT5GF2FASTR3 -> MQOMParameters.mqom2_cat5_gf2_fast_r3;
            case CAT5GF2FASTR5 -> MQOMParameters.mqom2_cat5_gf2_fast_r5;
            case CAT5GF2SHORTR3 -> MQOMParameters.mqom2_cat5_gf2_short_r3;
            case CAT5GF2SHORTR5 -> MQOMParameters.mqom2_cat5_gf2_short_r5;
            case CAT5GF16FASTR3 -> MQOMParameters.mqom2_cat5_gf16_fast_r3;
            case CAT5GF16FASTR5 -> MQOMParameters.mqom2_cat5_gf16_fast_r5;
            case CAT5GF16SHORTR3 -> MQOMParameters.mqom2_cat5_gf16_short_r3;
            case CAT5GF16SHORTR5 -> MQOMParameters.mqom2_cat5_gf16_short_r5;
            case CAT5GF256FASTR3 -> MQOMParameters.mqom2_cat5_gf256_fast_r3;
            case CAT5GF256FASTR5 -> MQOMParameters.mqom2_cat5_gf256_fast_r5;
            case CAT5GF256SHORTR3 -> MQOMParameters.mqom2_cat5_gf256_short_r3;
            case CAT5GF256SHORTR5 -> MQOMParameters.mqom2_cat5_gf256_short_r5;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain MQOM ParameterSpec.
     *
     * @return the parameters.
     */
    public MQOMParameterSpec getParameterSpec() {
        return switch (theSpec) {
            case CAT1GF2FASTR3 -> MQOMParameterSpec.mqom2_cat1_gf2_fast_r3;
            case CAT1GF2FASTR5 -> MQOMParameterSpec.mqom2_cat1_gf2_fast_r5;
            case CAT1GF2SHORTR3 -> MQOMParameterSpec.mqom2_cat1_gf2_short_r3;
            case CAT1GF2SHORTR5 -> MQOMParameterSpec.mqom2_cat1_gf2_short_r5;
            case CAT1GF16FASTR3 -> MQOMParameterSpec.mqom2_cat1_gf16_fast_r3;
            case CAT1GF16FASTR5 -> MQOMParameterSpec.mqom2_cat1_gf16_fast_r5;
            case CAT1GF16SHORTR3 -> MQOMParameterSpec.mqom2_cat1_gf16_short_r3;
            case CAT1GF16SHORTR5 -> MQOMParameterSpec.mqom2_cat1_gf16_short_r5;
            case CAT1GF256FASTR3 -> MQOMParameterSpec.mqom2_cat1_gf256_fast_r3;
            case CAT1GF256FASTR5 -> MQOMParameterSpec.mqom2_cat1_gf256_fast_r5;
            case CAT1GF256SHORTR3 -> MQOMParameterSpec.mqom2_cat1_gf256_short_r3;
            case CAT1GF256SHORTR5 -> MQOMParameterSpec.mqom2_cat1_gf256_short_r5;
            case CAT3GF2FASTR3 -> MQOMParameterSpec.mqom2_cat3_gf2_fast_r3;
            case CAT3GF2FASTR5 -> MQOMParameterSpec.mqom2_cat3_gf2_fast_r5;
            case CAT3GF2SHORTR3 -> MQOMParameterSpec.mqom2_cat3_gf2_short_r3;
            case CAT3GF2SHORTR5 -> MQOMParameterSpec.mqom2_cat3_gf2_short_r5;
            case CAT3GF16FASTR3 -> MQOMParameterSpec.mqom2_cat3_gf16_fast_r3;
            case CAT3GF16FASTR5 -> MQOMParameterSpec.mqom2_cat3_gf16_fast_r5;
            case CAT3GF16SHORTR3 -> MQOMParameterSpec.mqom2_cat3_gf16_short_r3;
            case CAT3GF16SHORTR5 -> MQOMParameterSpec.mqom2_cat3_gf16_short_r5;
            case CAT3GF256FASTR3 -> MQOMParameterSpec.mqom2_cat3_gf256_fast_r3;
            case CAT3GF256FASTR5 -> MQOMParameterSpec.mqom2_cat3_gf256_fast_r5;
            case CAT3GF256SHORTR3 -> MQOMParameterSpec.mqom2_cat3_gf256_short_r3;
            case CAT3GF256SHORTR5 -> MQOMParameterSpec.mqom2_cat3_gf256_short_r5;
            case CAT5GF2FASTR3 -> MQOMParameterSpec.mqom2_cat5_gf2_fast_r3;
            case CAT5GF2FASTR5 -> MQOMParameterSpec.mqom2_cat5_gf2_fast_r5;
            case CAT5GF2SHORTR3 -> MQOMParameterSpec.mqom2_cat5_gf2_short_r3;
            case CAT5GF2SHORTR5 -> MQOMParameterSpec.mqom2_cat5_gf2_short_r5;
            case CAT5GF16FASTR3 -> MQOMParameterSpec.mqom2_cat5_gf16_fast_r3;
            case CAT5GF16FASTR5 -> MQOMParameterSpec.mqom2_cat5_gf16_fast_r5;
            case CAT5GF16SHORTR3 -> MQOMParameterSpec.mqom2_cat5_gf16_short_r3;
            case CAT5GF16SHORTR5 -> MQOMParameterSpec.mqom2_cat5_gf16_short_r5;
            case CAT5GF256FASTR3 -> MQOMParameterSpec.mqom2_cat5_gf256_fast_r3;
            case CAT5GF256FASTR5 -> MQOMParameterSpec.mqom2_cat5_gf256_fast_r5;
            case CAT5GF256SHORTR3 -> MQOMParameterSpec.mqom2_cat5_gf256_short_r3;
            case CAT5GF256SHORTR5 -> MQOMParameterSpec.mqom2_cat5_gf256_short_r5;
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public ASN1ObjectIdentifier getIdentifier() {
        return switch (theSpec) {
            case CAT1GF2FASTR3 -> BCObjectIdentifiers.mqom2_cat1_gf2_fast_r3;
            case CAT1GF2FASTR5 -> BCObjectIdentifiers.mqom2_cat1_gf2_fast_r5;
            case CAT1GF2SHORTR3 -> BCObjectIdentifiers.mqom2_cat1_gf2_short_r3;
            case CAT1GF2SHORTR5 -> BCObjectIdentifiers.mqom2_cat1_gf2_short_r5;
            case CAT1GF16FASTR3 -> BCObjectIdentifiers.mqom2_cat1_gf16_fast_r3;
            case CAT1GF16FASTR5 -> BCObjectIdentifiers.mqom2_cat1_gf16_fast_r5;
            case CAT1GF16SHORTR3 -> BCObjectIdentifiers.mqom2_cat1_gf16_short_r3;
            case CAT1GF16SHORTR5 -> BCObjectIdentifiers.mqom2_cat1_gf16_short_r5;
            case CAT1GF256FASTR3 -> BCObjectIdentifiers.mqom2_cat1_gf256_fast_r3;
            case CAT1GF256FASTR5 -> BCObjectIdentifiers.mqom2_cat1_gf256_fast_r5;
            case CAT1GF256SHORTR3 -> BCObjectIdentifiers.mqom2_cat1_gf256_short_r3;
            case CAT1GF256SHORTR5 -> BCObjectIdentifiers.mqom2_cat1_gf256_short_r5;
            case CAT3GF2FASTR3 -> BCObjectIdentifiers.mqom2_cat3_gf2_fast_r3;
            case CAT3GF2FASTR5 -> BCObjectIdentifiers.mqom2_cat3_gf2_fast_r5;
            case CAT3GF2SHORTR3 -> BCObjectIdentifiers.mqom2_cat3_gf2_short_r3;
            case CAT3GF2SHORTR5 -> BCObjectIdentifiers.mqom2_cat3_gf2_short_r5;
            case CAT3GF16FASTR3 -> BCObjectIdentifiers.mqom2_cat3_gf16_fast_r3;
            case CAT3GF16FASTR5 -> BCObjectIdentifiers.mqom2_cat3_gf16_fast_r5;
            case CAT3GF16SHORTR3 -> BCObjectIdentifiers.mqom2_cat3_gf16_short_r3;
            case CAT3GF16SHORTR5 -> BCObjectIdentifiers.mqom2_cat3_gf16_short_r5;
            case CAT3GF256FASTR3 -> BCObjectIdentifiers.mqom2_cat3_gf256_fast_r3;
            case CAT3GF256FASTR5 -> BCObjectIdentifiers.mqom2_cat3_gf256_fast_r5;
            case CAT3GF256SHORTR3 -> BCObjectIdentifiers.mqom2_cat3_gf256_short_r3;
            case CAT3GF256SHORTR5 -> BCObjectIdentifiers.mqom2_cat3_gf256_short_r5;
            case CAT5GF2FASTR3 -> BCObjectIdentifiers.mqom2_cat5_gf2_fast_r3;
            case CAT5GF2FASTR5 -> BCObjectIdentifiers.mqom2_cat5_gf2_fast_r5;
            case CAT5GF2SHORTR3 -> BCObjectIdentifiers.mqom2_cat5_gf2_short_r3;
            case CAT5GF2SHORTR5 -> BCObjectIdentifiers.mqom2_cat5_gf2_short_r5;
            case CAT5GF16FASTR3 -> BCObjectIdentifiers.mqom2_cat5_gf16_fast_r3;
            case CAT5GF16FASTR5 -> BCObjectIdentifiers.mqom2_cat5_gf16_fast_r5;
            case CAT5GF16SHORTR3 -> BCObjectIdentifiers.mqom2_cat5_gf16_short_r3;
            case CAT5GF16SHORTR5 -> BCObjectIdentifiers.mqom2_cat5_gf16_short_r5;
            case CAT5GF256FASTR3 -> BCObjectIdentifiers.mqom2_cat5_gf256_fast_r3;
            case CAT5GF256FASTR5 -> BCObjectIdentifiers.mqom2_cat5_gf256_fast_r5;
            case CAT5GF256SHORTR3 -> BCObjectIdentifiers.mqom2_cat5_gf256_short_r3;
            case CAT5GF256SHORTR5 -> BCObjectIdentifiers.mqom2_cat5_gf256_short_r5;
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
        return pThat instanceof GordianCoreMQOMSpec myThat
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
    public static GordianCoreMQOMSpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianMQOMSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianMQOMSpec, GordianCoreMQOMSpec> newSpecMap() {
        final Map<GordianMQOMSpec, GordianCoreMQOMSpec> myMap = new EnumMap<>(GordianMQOMSpec.class);
        for (GordianMQOMSpec mySpec : GordianMQOMSpec.values()) {
            myMap.put(mySpec, new GordianCoreMQOMSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreMQOMSpec[] values() {
        return VALUES;
    }
}
