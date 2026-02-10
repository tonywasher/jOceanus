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

package io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.spec;

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewSnovaSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.snova.SnovaParameters;
import org.bouncycastle.pqc.jcajce.spec.SnovaParameterSpec;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * Snova KeySpec.
 */
public final class GordianCoreSnovaSpec {
    /**
     * The specMap.
     */
    private static final Map<GordianNewSnovaSpec, GordianCoreSnovaSpec> SPECMAP = newSpecMap();

    /**
     * The Spec.
     */
    private final GordianNewSnovaSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreSnovaSpec(final GordianNewSnovaSpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianNewSnovaSpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain Snova Parameters.
     *
     * @return the parameters.
     */
    public SnovaParameters getParameters() {
        switch (theSpec) {
            case SNOVA24A_SSK:
                return SnovaParameters.SNOVA_24_5_4_SSK;
            case SNOVA24A_ESK:
                return SnovaParameters.SNOVA_24_5_4_ESK;
            case SNOVA24A_SHAKE_SSK:
                return SnovaParameters.SNOVA_24_5_4_SHAKE_SSK;
            case SNOVA24A_SHAKE_ESK:
                return SnovaParameters.SNOVA_24_5_4_SHAKE_ESK;
            case SNOVA24B_SSK:
                return SnovaParameters.SNOVA_24_5_5_SSK;
            case SNOVA24B_ESK:
                return SnovaParameters.SNOVA_24_5_5_ESK;
            case SNOVA24B_SHAKE_SSK:
                return SnovaParameters.SNOVA_24_5_5_SHAKE_SSK;
            case SNOVA24B_SHAKE_ESK:
                return SnovaParameters.SNOVA_24_5_5_SHAKE_ESK;
            case SNOVA25_SSK:
                return SnovaParameters.SNOVA_25_8_3_SSK;
            case SNOVA25_ESK:
                return SnovaParameters.SNOVA_25_8_3_ESK;
            case SNOVA25_SHAKE_SSK:
                return SnovaParameters.SNOVA_25_8_3_SHAKE_SSK;
            case SNOVA25_SHAKE_ESK:
                return SnovaParameters.SNOVA_25_8_3_SHAKE_ESK;
            case SNOVA29_SSK:
                return SnovaParameters.SNOVA_29_6_5_SSK;
            case SNOVA29_ESK:
                return SnovaParameters.SNOVA_29_6_5_ESK;
            case SNOVA29_SHAKE_SSK:
                return SnovaParameters.SNOVA_29_6_5_SHAKE_SSK;
            case SNOVA29_SHAKE_ESK:
                return SnovaParameters.SNOVA_29_6_5_SHAKE_ESK;
            case SNOVA37A_SSK:
                return SnovaParameters.SNOVA_37_8_4_SSK;
            case SNOVA37A_ESK:
                return SnovaParameters.SNOVA_37_8_4_ESK;
            case SNOVA37A_SHAKE_SSK:
                return SnovaParameters.SNOVA_37_8_4_SHAKE_SSK;
            case SNOVA37A_SHAKE_ESK:
                return SnovaParameters.SNOVA_37_8_4_SHAKE_ESK;
            case SNOVA37B_SSK:
                return SnovaParameters.SNOVA_37_17_2_SSK;
            case SNOVA37B_ESK:
                return SnovaParameters.SNOVA_37_17_2_ESK;
            case SNOVA37B_SHAKE_SSK:
                return SnovaParameters.SNOVA_37_17_2_SHAKE_SSK;
            case SNOVA37B_SHAKE_ESK:
                return SnovaParameters.SNOVA_37_17_2_SHAKE_ESK;
            case SNOVA49_SSK:
                return SnovaParameters.SNOVA_49_11_3_SSK;
            case SNOVA49_ESK:
                return SnovaParameters.SNOVA_49_11_3_ESK;
            case SNOVA49_SHAKE_SSK:
                return SnovaParameters.SNOVA_49_11_3_SHAKE_SSK;
            case SNOVA49_SHAKE_ESK:
                return SnovaParameters.SNOVA_49_11_3_SHAKE_ESK;
            case SNOVA56_SSK:
                return SnovaParameters.SNOVA_56_25_2_SSK;
            case SNOVA56_ESK:
                return SnovaParameters.SNOVA_56_25_2_ESK;
            case SNOVA56_SHAKE_SSK:
                return SnovaParameters.SNOVA_56_25_2_SHAKE_SSK;
            case SNOVA56_SHAKE_ESK:
                return SnovaParameters.SNOVA_56_25_2_SHAKE_ESK;
            case SNOVA60_SSK:
                return SnovaParameters.SNOVA_60_10_4_SSK;
            case SNOVA60_ESK:
                return SnovaParameters.SNOVA_60_10_4_ESK;
            case SNOVA60_SHAKE_SSK:
                return SnovaParameters.SNOVA_60_10_4_SHAKE_SSK;
            case SNOVA60_SHAKE_ESK:
                return SnovaParameters.SNOVA_60_10_4_SHAKE_ESK;
            case SNOVA66_SSK:
                return SnovaParameters.SNOVA_66_15_3_SSK;
            case SNOVA66_ESK:
                return SnovaParameters.SNOVA_66_15_3_ESK;
            case SNOVA66_SHAKE_SSK:
                return SnovaParameters.SNOVA_66_15_3_SHAKE_SSK;
            case SNOVA66_SHAKE_ESK:
                return SnovaParameters.SNOVA_66_15_3_SHAKE_ESK;
            case SNOVA75_SSK:
                return SnovaParameters.SNOVA_75_33_2_SSK;
            case SNOVA75_ESK:
                return SnovaParameters.SNOVA_75_33_2_ESK;
            case SNOVA75_SHAKE_SSK:
                return SnovaParameters.SNOVA_75_33_2_SHAKE_SSK;
            case SNOVA75_SHAKE_ESK:
                return SnovaParameters.SNOVA_75_33_2_SHAKE_ESK;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain Snova ParameterSpec.
     *
     * @return the parameters.
     */
    public SnovaParameterSpec getParameterSpec() {
        switch (theSpec) {
            case SNOVA24A_SSK:
                return SnovaParameterSpec.SNOVA_24_5_4_SSK;
            case SNOVA24A_ESK:
                return SnovaParameterSpec.SNOVA_24_5_4_ESK;
            case SNOVA24A_SHAKE_SSK:
                return SnovaParameterSpec.SNOVA_24_5_4_SHAKE_SSK;
            case SNOVA24A_SHAKE_ESK:
                return SnovaParameterSpec.SNOVA_24_5_4_SHAKE_ESK;
            case SNOVA24B_SSK:
                return SnovaParameterSpec.SNOVA_24_5_5_SSK;
            case SNOVA24B_ESK:
                return SnovaParameterSpec.SNOVA_24_5_5_ESK;
            case SNOVA24B_SHAKE_SSK:
                return SnovaParameterSpec.SNOVA_24_5_5_SHAKE_SSK;
            case SNOVA24B_SHAKE_ESK:
                return SnovaParameterSpec.SNOVA_24_5_5_SHAKE_ESK;
            case SNOVA25_SSK:
                return SnovaParameterSpec.SNOVA_25_8_3_SSK;
            case SNOVA25_ESK:
                return SnovaParameterSpec.SNOVA_25_8_3_ESK;
            case SNOVA25_SHAKE_SSK:
                return SnovaParameterSpec.SNOVA_25_8_3_SHAKE_SSK;
            case SNOVA25_SHAKE_ESK:
                return SnovaParameterSpec.SNOVA_25_8_3_SHAKE_ESK;
            case SNOVA29_SSK:
                return SnovaParameterSpec.SNOVA_29_6_5_SSK;
            case SNOVA29_ESK:
                return SnovaParameterSpec.SNOVA_29_6_5_ESK;
            case SNOVA29_SHAKE_SSK:
                return SnovaParameterSpec.SNOVA_29_6_5_SHAKE_SSK;
            case SNOVA29_SHAKE_ESK:
                return SnovaParameterSpec.SNOVA_29_6_5_SHAKE_ESK;
            case SNOVA37A_SSK:
                return SnovaParameterSpec.SNOVA_37_8_4_SSK;
            case SNOVA37A_ESK:
                return SnovaParameterSpec.SNOVA_37_8_4_ESK;
            case SNOVA37A_SHAKE_SSK:
                return SnovaParameterSpec.SNOVA_37_8_4_SHAKE_SSK;
            case SNOVA37A_SHAKE_ESK:
                return SnovaParameterSpec.SNOVA_37_8_4_SHAKE_ESK;
            case SNOVA37B_SSK:
                return SnovaParameterSpec.SNOVA_37_17_2_SSK;
            case SNOVA37B_ESK:
                return SnovaParameterSpec.SNOVA_37_17_2_ESK;
            case SNOVA37B_SHAKE_SSK:
                return SnovaParameterSpec.SNOVA_37_17_2_SHAKE_SSK;
            case SNOVA37B_SHAKE_ESK:
                return SnovaParameterSpec.SNOVA_37_17_2_SHAKE_ESK;
            case SNOVA49_SSK:
                return SnovaParameterSpec.SNOVA_49_11_3_SSK;
            case SNOVA49_ESK:
                return SnovaParameterSpec.SNOVA_49_11_3_ESK;
            case SNOVA49_SHAKE_SSK:
                return SnovaParameterSpec.SNOVA_49_11_3_SHAKE_SSK;
            case SNOVA49_SHAKE_ESK:
                return SnovaParameterSpec.SNOVA_49_11_3_SHAKE_ESK;
            case SNOVA56_SSK:
                return SnovaParameterSpec.SNOVA_56_25_2_SSK;
            case SNOVA56_ESK:
                return SnovaParameterSpec.SNOVA_56_25_2_ESK;
            case SNOVA56_SHAKE_SSK:
                return SnovaParameterSpec.SNOVA_56_25_2_SHAKE_SSK;
            case SNOVA56_SHAKE_ESK:
                return SnovaParameterSpec.SNOVA_56_25_2_SHAKE_ESK;
            case SNOVA60_SSK:
                return SnovaParameterSpec.SNOVA_60_10_4_SSK;
            case SNOVA60_ESK:
                return SnovaParameterSpec.SNOVA_60_10_4_ESK;
            case SNOVA60_SHAKE_SSK:
                return SnovaParameterSpec.SNOVA_60_10_4_SHAKE_SSK;
            case SNOVA60_SHAKE_ESK:
                return SnovaParameterSpec.SNOVA_60_10_4_SHAKE_ESK;
            case SNOVA66_SSK:
                return SnovaParameterSpec.SNOVA_66_15_3_SSK;
            case SNOVA66_ESK:
                return SnovaParameterSpec.SNOVA_66_15_3_ESK;
            case SNOVA66_SHAKE_SSK:
                return SnovaParameterSpec.SNOVA_66_15_3_SHAKE_SSK;
            case SNOVA66_SHAKE_ESK:
                return SnovaParameterSpec.SNOVA_66_15_3_SHAKE_ESK;
            case SNOVA75_SSK:
                return SnovaParameterSpec.SNOVA_75_33_2_SSK;
            case SNOVA75_ESK:
                return SnovaParameterSpec.SNOVA_75_33_2_ESK;
            case SNOVA75_SHAKE_SSK:
                return SnovaParameterSpec.SNOVA_75_33_2_SHAKE_SSK;
            case SNOVA75_SHAKE_ESK:
                return SnovaParameterSpec.SNOVA_75_33_2_SHAKE_ESK;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain Snova algorithm Identifier.
     *
     * @return the identifier.
     */
    public ASN1ObjectIdentifier getIdentifier() {
        switch (theSpec) {
            case SNOVA24A_SSK:
                return BCObjectIdentifiers.snova_24_5_4_ssk;
            case SNOVA24A_ESK:
                return BCObjectIdentifiers.snova_24_5_4_esk;
            case SNOVA24A_SHAKE_SSK:
                return BCObjectIdentifiers.snova_24_5_4_shake_ssk;
            case SNOVA24A_SHAKE_ESK:
                return BCObjectIdentifiers.snova_24_5_4_shake_esk;
            case SNOVA24B_SSK:
                return BCObjectIdentifiers.snova_24_5_5_ssk;
            case SNOVA24B_ESK:
                return BCObjectIdentifiers.snova_24_5_5_esk;
            case SNOVA24B_SHAKE_SSK:
                return BCObjectIdentifiers.snova_24_5_5_shake_ssk;
            case SNOVA24B_SHAKE_ESK:
                return BCObjectIdentifiers.snova_24_5_5_shake_esk;
            case SNOVA25_SSK:
                return BCObjectIdentifiers.snova_25_8_3_ssk;
            case SNOVA25_ESK:
                return BCObjectIdentifiers.snova_25_8_3_esk;
            case SNOVA25_SHAKE_SSK:
                return BCObjectIdentifiers.snova_25_8_3_shake_ssk;
            case SNOVA25_SHAKE_ESK:
                return BCObjectIdentifiers.snova_25_8_3_shake_esk;
            case SNOVA29_SSK:
                return BCObjectIdentifiers.snova_29_6_5_ssk;
            case SNOVA29_ESK:
                return BCObjectIdentifiers.snova_29_6_5_esk;
            case SNOVA29_SHAKE_SSK:
                return BCObjectIdentifiers.snova_29_6_5_shake_ssk;
            case SNOVA29_SHAKE_ESK:
                return BCObjectIdentifiers.snova_29_6_5_shake_esk;
            case SNOVA37A_SSK:
                return BCObjectIdentifiers.snova_37_8_4_ssk;
            case SNOVA37A_ESK:
                return BCObjectIdentifiers.snova_37_8_4_esk;
            case SNOVA37A_SHAKE_SSK:
                return BCObjectIdentifiers.snova_37_8_4_shake_ssk;
            case SNOVA37A_SHAKE_ESK:
                return BCObjectIdentifiers.snova_37_8_4_shake_esk;
            case SNOVA37B_SSK:
                return BCObjectIdentifiers.snova_37_17_2_ssk;
            case SNOVA37B_ESK:
                return BCObjectIdentifiers.snova_37_17_2_esk;
            case SNOVA37B_SHAKE_SSK:
                return BCObjectIdentifiers.snova_37_17_2_shake_ssk;
            case SNOVA37B_SHAKE_ESK:
                return BCObjectIdentifiers.snova_37_17_2_shake_esk;
            case SNOVA49_SSK:
                return BCObjectIdentifiers.snova_49_11_3_ssk;
            case SNOVA49_ESK:
                return BCObjectIdentifiers.snova_49_11_3_esk;
            case SNOVA49_SHAKE_SSK:
                return BCObjectIdentifiers.snova_49_11_3_shake_ssk;
            case SNOVA49_SHAKE_ESK:
                return BCObjectIdentifiers.snova_49_11_3_shake_esk;
            case SNOVA56_SSK:
                return BCObjectIdentifiers.snova_56_25_2_ssk;
            case SNOVA56_ESK:
                return BCObjectIdentifiers.snova_56_25_2_esk;
            case SNOVA56_SHAKE_SSK:
                return BCObjectIdentifiers.snova_56_25_2_shake_ssk;
            case SNOVA56_SHAKE_ESK:
                return BCObjectIdentifiers.snova_56_25_2_shake_esk;
            case SNOVA60_SSK:
                return BCObjectIdentifiers.snova_60_10_4_ssk;
            case SNOVA60_ESK:
                return BCObjectIdentifiers.snova_60_10_4_esk;
            case SNOVA60_SHAKE_SSK:
                return BCObjectIdentifiers.snova_60_10_4_shake_ssk;
            case SNOVA60_SHAKE_ESK:
                return BCObjectIdentifiers.snova_60_10_4_shake_esk;
            case SNOVA66_SSK:
                return BCObjectIdentifiers.snova_66_15_3_ssk;
            case SNOVA66_ESK:
                return BCObjectIdentifiers.snova_66_15_3_esk;
            case SNOVA66_SHAKE_SSK:
                return BCObjectIdentifiers.snova_66_15_3_shake_ssk;
            case SNOVA66_SHAKE_ESK:
                return BCObjectIdentifiers.snova_66_15_3_shake_esk;
            case SNOVA75_SSK:
                return BCObjectIdentifiers.snova_75_33_2_ssk;
            case SNOVA75_ESK:
                return BCObjectIdentifiers.snova_75_33_2_esk;
            case SNOVA75_SHAKE_SSK:
                return BCObjectIdentifiers.snova_75_33_2_shake_ssk;
            case SNOVA75_SHAKE_ESK:
                return BCObjectIdentifiers.snova_75_33_2_shake_esk;
            default:
                throw new IllegalArgumentException();
        }
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
        return pThat instanceof GordianCoreSnovaSpec myThat
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
    public static GordianCoreSnovaSpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianNewSnovaSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianNewSnovaSpec, GordianCoreSnovaSpec> newSpecMap() {
        final Map<GordianNewSnovaSpec, GordianCoreSnovaSpec> myMap = new EnumMap<>(GordianNewSnovaSpec.class);
        for (GordianNewSnovaSpec mySpec : GordianNewSnovaSpec.values()) {
            myMap.put(mySpec, new GordianCoreSnovaSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static Collection<GordianCoreSnovaSpec> values() {
        return SPECMAP.values();
    }
}
