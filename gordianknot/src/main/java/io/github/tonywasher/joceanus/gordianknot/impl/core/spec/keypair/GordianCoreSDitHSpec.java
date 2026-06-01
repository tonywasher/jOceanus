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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSDitHSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.sdith.SDitHParameters;
import org.bouncycastle.pqc.jcajce.spec.SDitHParameterSpec;

import java.util.EnumMap;
import java.util.Map;

/**
 * SDitH KeySpec.
 */
public final class GordianCoreSDitHSpec
        implements GordianCoreKeyPairIdSpec<GordianSDitHSpec> {
    /**
     * The specMap.
     */
    private static final Map<GordianSDitHSpec, GordianCoreSDitHSpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreSDitHSpec[] VALUES = SPECMAP.values().toArray(new GordianCoreSDitHSpec[0]);

    /**
     * The Spec.
     */
    private final GordianSDitHSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreSDitHSpec(final GordianSDitHSpec pSpec) {
        theSpec = pSpec;
    }

    @Override
    public GordianKeyPairType getKeyPairType() {
        return GordianKeyPairType.SDITH;
    }

    @Override
    public GordianSDitHSpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain SDitH Parameters.
     *
     * @return the parameters.
     */
    public SDitHParameters getParameters() {
        return switch (theSpec) {
            case HYPERCAT1GF256 -> SDitHParameters.sdith_hypercube_cat1_gf256;
            case HYPERCAT3GF256 -> SDitHParameters.sdith_hypercube_cat3_gf256;
            case HYPERCAT5GF256 -> SDitHParameters.sdith_hypercube_cat5_gf256;
            case HYPERCAT1P251 -> SDitHParameters.sdith_hypercube_cat1_p251;
            case HYPERCAT3P251 -> SDitHParameters.sdith_hypercube_cat3_p251;
            case HYPERCAT5P251 -> SDitHParameters.sdith_hypercube_cat5_p251;
            case THRESCAT1GF256 -> SDitHParameters.sdith_threshold_cat1_gf256;
            case THRESCAT3GF256 -> SDitHParameters.sdith_threshold_cat3_gf256;
            case THRESCAT5GF256 -> SDitHParameters.sdith_threshold_cat5_gf256;
            case THRESCAT1P251 -> SDitHParameters.sdith_threshold_cat1_p251;
            case THRESCAT3P251 -> SDitHParameters.sdith_threshold_cat3_p251;
            case THRESCAT5P251 -> SDitHParameters.sdith_threshold_cat5_p251;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain SABER ParameterSpec.
     *
     * @return the parameters.
     */
    public SDitHParameterSpec getParameterSpec() {
        return switch (theSpec) {
            case HYPERCAT1GF256 -> SDitHParameterSpec.sdith_hypercube_cat1_gf256;
            case HYPERCAT3GF256 -> SDitHParameterSpec.sdith_hypercube_cat3_gf256;
            case HYPERCAT5GF256 -> SDitHParameterSpec.sdith_hypercube_cat5_gf256;
            case HYPERCAT1P251 -> SDitHParameterSpec.sdith_hypercube_cat1_p251;
            case HYPERCAT3P251 -> SDitHParameterSpec.sdith_hypercube_cat3_p251;
            case HYPERCAT5P251 -> SDitHParameterSpec.sdith_hypercube_cat5_p251;
            case THRESCAT1GF256 -> SDitHParameterSpec.sdith_threshold_cat1_gf256;
            case THRESCAT3GF256 -> SDitHParameterSpec.sdith_threshold_cat3_gf256;
            case THRESCAT5GF256 -> SDitHParameterSpec.sdith_threshold_cat5_gf256;
            case THRESCAT1P251 -> SDitHParameterSpec.sdith_threshold_cat1_p251;
            case THRESCAT3P251 -> SDitHParameterSpec.sdith_threshold_cat3_p251;
            case THRESCAT5P251 -> SDitHParameterSpec.sdith_threshold_cat5_p251;
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public ASN1ObjectIdentifier getIdentifier() {
        return switch (theSpec) {
            case HYPERCAT1GF256 -> BCObjectIdentifiers.sdith_hypercube_cat1_gf256;
            case HYPERCAT3GF256 -> BCObjectIdentifiers.sdith_hypercube_cat3_gf256;
            case HYPERCAT5GF256 -> BCObjectIdentifiers.sdith_hypercube_cat5_gf256;
            case HYPERCAT1P251 -> BCObjectIdentifiers.sdith_hypercube_cat1_p251;
            case HYPERCAT3P251 -> BCObjectIdentifiers.sdith_hypercube_cat3_p251;
            case HYPERCAT5P251 -> BCObjectIdentifiers.sdith_hypercube_cat5_p251;
            case THRESCAT1GF256 -> BCObjectIdentifiers.sdith_threshold_cat1_gf256;
            case THRESCAT3GF256 -> BCObjectIdentifiers.sdith_threshold_cat3_gf256;
            case THRESCAT5GF256 -> BCObjectIdentifiers.sdith_threshold_cat5_gf256;
            case THRESCAT1P251 -> BCObjectIdentifiers.sdith_threshold_cat1_p251;
            case THRESCAT3P251 -> BCObjectIdentifiers.sdith_threshold_cat3_p251;
            case THRESCAT5P251 -> BCObjectIdentifiers.sdith_threshold_cat5_p251;
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
        return pThat instanceof GordianCoreSDitHSpec myThat
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
    public static GordianCoreSDitHSpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianSDitHSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianSDitHSpec, GordianCoreSDitHSpec> newSpecMap() {
        final Map<GordianSDitHSpec, GordianCoreSDitHSpec> myMap = new EnumMap<>(GordianSDitHSpec.class);
        for (GordianSDitHSpec mySpec : GordianSDitHSpec.values()) {
            myMap.put(mySpec, new GordianCoreSDitHSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreSDitHSpec[] values() {
        return VALUES;
    }
}
