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

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewMLKEMSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.jcajce.spec.MLKEMParameterSpec;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * Kyber KeySpec.
 */
public final class GordianCoreMLKEMSpec {
    /**
     * The specMap.
     */
    private static final Map<GordianNewMLKEMSpec, GordianCoreMLKEMSpec> SPECMAP = newSpecMap();

    /**
     * The Spec.
     */
    private final GordianNewMLKEMSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreMLKEMSpec(final GordianNewMLKEMSpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianNewMLKEMSpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain KYBER Parameters.
     *
     * @return the parameters.
     */
    public MLKEMParameters getParameters() {
        switch (theSpec) {
            case MLKEM512:
                return MLKEMParameters.ml_kem_512;
            case MLKEM768:
                return MLKEMParameters.ml_kem_768;
            case MLKEM1024:
                return MLKEMParameters.ml_kem_1024;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain Kyber ParameterSpec.
     *
     * @return the parameters.
     */
    public MLKEMParameterSpec getParameterSpec() {
        switch (theSpec) {
            case MLKEM512:
                return MLKEMParameterSpec.ml_kem_512;
            case MLKEM768:
                return MLKEMParameterSpec.ml_kem_768;
            case MLKEM1024:
                return MLKEMParameterSpec.ml_kem_1024;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain MLKEM algorithm Identifier.
     *
     * @return the identifier.
     */
    public ASN1ObjectIdentifier getIdentifier() {
        switch (theSpec) {
            case MLKEM512:
                return NISTObjectIdentifiers.id_alg_ml_kem_512;
            case MLKEM768:
                return NISTObjectIdentifiers.id_alg_ml_kem_768;
            case MLKEM1024:
                return NISTObjectIdentifiers.id_alg_ml_kem_1024;
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
        return pThat instanceof GordianCoreMLKEMSpec myThat
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
    public static GordianCoreMLKEMSpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianNewMLKEMSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianNewMLKEMSpec, GordianCoreMLKEMSpec> newSpecMap() {
        final Map<GordianNewMLKEMSpec, GordianCoreMLKEMSpec> myMap = new EnumMap<>(GordianNewMLKEMSpec.class);
        for (GordianNewMLKEMSpec mySpec : GordianNewMLKEMSpec.values()) {
            myMap.put(mySpec, new GordianCoreMLKEMSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static Collection<GordianCoreMLKEMSpec> values() {
        return SPECMAP.values();
    }
}
