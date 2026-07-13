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

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianHAETAESpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.haetae.HAETAEParameters;
import org.bouncycastle.pqc.jcajce.spec.HaetaeParameterSpec;

import java.util.EnumMap;
import java.util.Map;

/**
 * HAETAE KeySpecs.
 */
public final class GordianCoreHAETAESpec
        implements GordianCoreKeyPairIdSpec<GordianHAETAESpec> {
    /**
     * The specMap.
     */
    private static final Map<GordianHAETAESpec, GordianCoreHAETAESpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreHAETAESpec[] VALUES = SPECMAP.values().toArray(new GordianCoreHAETAESpec[0]);

    /**
     * The Spec.
     */
    private final GordianHAETAESpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreHAETAESpec(final GordianHAETAESpec pSpec) {
        theSpec = pSpec;
    }

    @Override
    public GordianKeyPairType getKeyPairType() {
        return GordianKeyPairType.HAETAE;
    }

    @Override
    public GordianHAETAESpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain HAETAE Parameters.
     *
     * @return the parameters.
     */
    public HAETAEParameters getParameters() {
        return switch (theSpec) {
            case HAETAE2 -> HAETAEParameters.haetae2;
            case HAETAE3 -> HAETAEParameters.haetae3;
            case HAETAE5 -> HAETAEParameters.haetae5;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain HAETAE ParameterSpec.
     *
     * @return the parameters.
     */
    public HaetaeParameterSpec getParameterSpec() {
        return switch (theSpec) {
            case HAETAE2 -> HaetaeParameterSpec.haetae2;
            case HAETAE3 -> HaetaeParameterSpec.haetae3;
            case HAETAE5 -> HaetaeParameterSpec.haetae5;
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public ASN1ObjectIdentifier getIdentifier() {
        return switch (theSpec) {
            case HAETAE2 -> BCObjectIdentifiers.haetae2;
            case HAETAE3 -> BCObjectIdentifiers.haetae3;
            case HAETAE5 -> BCObjectIdentifiers.haetae5;
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
        return pThat instanceof GordianCoreHAETAESpec myThat
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
    public static GordianCoreHAETAESpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianHAETAESpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianHAETAESpec, GordianCoreHAETAESpec> newSpecMap() {
        final Map<GordianHAETAESpec, GordianCoreHAETAESpec> myMap = new EnumMap<>(GordianHAETAESpec.class);
        for (GordianHAETAESpec mySpec : GordianHAETAESpec.values()) {
            myMap.put(mySpec, new GordianCoreHAETAESpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreHAETAESpec[] values() {
        return VALUES;
    }
}
