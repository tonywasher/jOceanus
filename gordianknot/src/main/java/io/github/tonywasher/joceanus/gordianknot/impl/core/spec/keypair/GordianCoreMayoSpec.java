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

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewMayoSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.mayo.MayoParameters;
import org.bouncycastle.pqc.jcajce.spec.MayoParameterSpec;

import java.util.EnumMap;
import java.util.Map;

/**
 * Mayo KeySpec.
 */
public final class GordianCoreMayoSpec {
    /**
     * The specMap.
     */
    private static final Map<GordianNewMayoSpec, GordianCoreMayoSpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreMayoSpec[] VALUES = SPECMAP.values().toArray(new GordianCoreMayoSpec[0]);

    /**
     * The Spec.
     */
    private final GordianNewMayoSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreMayoSpec(final GordianNewMayoSpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianNewMayoSpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain Mayo Parameters.
     *
     * @return the parameters.
     */
    public MayoParameters getParameters() {
        switch (theSpec) {
            case MAYO1:
                return MayoParameters.mayo1;
            case MAYO2:
                return MayoParameters.mayo2;
            case MAYO3:
                return MayoParameters.mayo3;
            case MAYO5:
                return MayoParameters.mayo5;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain Mayo ParameterSpec.
     *
     * @return the parameters.
     */
    public MayoParameterSpec getParameterSpec() {
        switch (theSpec) {
            case MAYO1:
                return MayoParameterSpec.mayo1;
            case MAYO2:
                return MayoParameterSpec.mayo2;
            case MAYO3:
                return MayoParameterSpec.mayo3;
            case MAYO5:
                return MayoParameterSpec.mayo5;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain MAYO algorithm Identifier.
     *
     * @return the identifier.
     */
    public ASN1ObjectIdentifier getIdentifier() {
        switch (theSpec) {
            case MAYO1:
                return BCObjectIdentifiers.mayo1;
            case MAYO2:
                return BCObjectIdentifiers.mayo2;
            case MAYO3:
                return BCObjectIdentifiers.mayo3;
            case MAYO5:
                return BCObjectIdentifiers.mayo5;
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
        return pThat instanceof GordianCoreMayoSpec myThat
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
    public static GordianCoreMayoSpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianNewMayoSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianNewMayoSpec, GordianCoreMayoSpec> newSpecMap() {
        final Map<GordianNewMayoSpec, GordianCoreMayoSpec> myMap = new EnumMap<>(GordianNewMayoSpec.class);
        for (GordianNewMayoSpec mySpec : GordianNewMayoSpec.values()) {
            myMap.put(mySpec, new GordianCoreMayoSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreMayoSpec[] values() {
        return VALUES;
    }
}
