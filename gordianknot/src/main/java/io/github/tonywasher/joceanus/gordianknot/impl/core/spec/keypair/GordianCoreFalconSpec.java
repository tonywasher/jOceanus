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

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianFalconSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.falcon.FalconParameters;
import org.bouncycastle.pqc.jcajce.spec.FalconParameterSpec;

import java.util.EnumMap;
import java.util.Map;

/**
 * FALCON KeySpec.
 */
public final class GordianCoreFalconSpec {
    /**
     * The specMap.
     */
    private static final Map<GordianFalconSpec, GordianCoreFalconSpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreFalconSpec[] VALUES = SPECMAP.values().toArray(new GordianCoreFalconSpec[0]);

    /**
     * The Spec.
     */
    private final GordianFalconSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreFalconSpec(final GordianFalconSpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianFalconSpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain FALCON Parameters.
     *
     * @return the parameters.
     */
    public FalconParameters getParameters() {
        switch (theSpec) {
            case FALCON512:
                return FalconParameters.falcon_512;
            case FALCON1024:
                return FalconParameters.falcon_1024;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain Falcon ParameterSpec.
     *
     * @return the parameters.
     */
    public FalconParameterSpec getParameterSpec() {
        switch (theSpec) {
            case FALCON512:
                return FalconParameterSpec.falcon_512;
            case FALCON1024:
                return FalconParameterSpec.falcon_1024;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain Falcon algorithm Identifier.
     *
     * @return the identifier.
     */
    public ASN1ObjectIdentifier getIdentifier() {
        switch (theSpec) {
            case FALCON512:
                return BCObjectIdentifiers.falcon_512;
            case FALCON1024:
                return BCObjectIdentifiers.falcon_1024;
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
        return pThat instanceof GordianCoreFalconSpec myThat
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
    public static GordianCoreFalconSpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianFalconSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianFalconSpec, GordianCoreFalconSpec> newSpecMap() {
        final Map<GordianFalconSpec, GordianCoreFalconSpec> myMap = new EnumMap<>(GordianFalconSpec.class);
        for (GordianFalconSpec mySpec : GordianFalconSpec.values()) {
            myMap.put(mySpec, new GordianCoreFalconSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreFalconSpec[] values() {
        return VALUES;
    }
}
