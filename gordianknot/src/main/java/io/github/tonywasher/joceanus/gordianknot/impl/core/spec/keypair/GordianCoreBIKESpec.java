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

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianBIKESpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.jcajce.spec.BIKEParameterSpec;
import org.bouncycastle.pqc.legacy.bike.BIKEParameters;

import java.util.EnumMap;
import java.util.Map;

public final class GordianCoreBIKESpec {
    /**
     * The specMap.
     */
    private static final Map<GordianBIKESpec, GordianCoreBIKESpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreBIKESpec[] VALUES = SPECMAP.values().toArray(new GordianCoreBIKESpec[0]);

    /**
     * The Spec.
     */
    private final GordianBIKESpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreBIKESpec(final GordianBIKESpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianBIKESpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain BIKE Parameters.
     *
     * @return the parameters.
     */
    public BIKEParameters getParameters() {
        switch (theSpec) {
            case BIKE128:
                return BIKEParameters.bike128;
            case BIKE192:
                return BIKEParameters.bike192;
            case BIKE256:
                return BIKEParameters.bike256;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain BIKE ParameterSpec.
     *
     * @return the parameters.
     */
    public BIKEParameterSpec getParameterSpec() {
        switch (theSpec) {
            case BIKE128:
                return BIKEParameterSpec.bike128;
            case BIKE192:
                return BIKEParameterSpec.bike192;
            case BIKE256:
                return BIKEParameterSpec.bike256;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain BIKE algorithm Identifier.
     *
     * @return the identifier.
     */
    public ASN1ObjectIdentifier getIdentifier() {
        switch (theSpec) {
            case BIKE128:
                return BCObjectIdentifiers.bike128;
            case BIKE192:
                return BCObjectIdentifiers.bike192;
            case BIKE256:
                return BCObjectIdentifiers.bike256;
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
        return pThat instanceof GordianCoreBIKESpec myThat
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
    public static GordianCoreBIKESpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianBIKESpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianBIKESpec, GordianCoreBIKESpec> newSpecMap() {
        final Map<GordianBIKESpec, GordianCoreBIKESpec> myMap = new EnumMap<>(GordianBIKESpec.class);
        for (GordianBIKESpec mySpec : GordianBIKESpec.values()) {
            myMap.put(mySpec, new GordianCoreBIKESpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreBIKESpec[] values() {
        return VALUES;
    }
}
