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

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewBIKESpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.bike.BIKEParameters;
import org.bouncycastle.pqc.jcajce.spec.BIKEParameterSpec;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

public final class GordianCoreBIKESpec {
    /**
     * The specMap.
     */
    private static final Map<GordianNewBIKESpec, GordianCoreBIKESpec> SPECMAP = newSpecMap();

    /**
     * The Spec.
     */
    private final GordianNewBIKESpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreBIKESpec(final GordianNewBIKESpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianNewBIKESpec getSpec() {
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
        return pSpec instanceof GordianNewBIKESpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianNewBIKESpec, GordianCoreBIKESpec> newSpecMap() {
        final Map<GordianNewBIKESpec, GordianCoreBIKESpec> myMap = new EnumMap<>(GordianNewBIKESpec.class);
        for (GordianNewBIKESpec mySpec : GordianNewBIKESpec.values()) {
            myMap.put(mySpec, new GordianCoreBIKESpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static Collection<GordianCoreBIKESpec> values() {
        return SPECMAP.values();
    }
}
