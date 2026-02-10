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

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewMLDSASpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.jcajce.spec.MLDSAParameterSpec;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * MLDSA KeySpec.
 */
public final class GordianCoreMLDSASpec {
    /**
     * The specMap.
     */
    private static final Map<GordianNewMLDSASpec, GordianCoreMLDSASpec> SPECMAP = newSpecMap();

    /**
     * The Spec.
     */
    private final GordianNewMLDSASpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreMLDSASpec(final GordianNewMLDSASpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianNewMLDSASpec getSpec() {
        return theSpec;
    }

    /**
     * Is this a hash signer?
     *
     * @return true/false
     */
    public boolean isHash() {
        switch (theSpec) {
            case MLDSA44SHA:
            case MLDSA65SHA:
            case MLDSA87SHA:
                return true;
            case MLDSA44:
            case MLDSA65:
            case MLDSA87:
            default:
                return false;
        }
    }

    /**
     * Obtain MLDSA Parameters.
     *
     * @return the parameters.
     */
    public MLDSAParameters getParameters() {
        switch (theSpec) {
            case MLDSA44:
                return MLDSAParameters.ml_dsa_44;
            case MLDSA65:
                return MLDSAParameters.ml_dsa_65;
            case MLDSA87:
                return MLDSAParameters.ml_dsa_87;
            case MLDSA44SHA:
                return MLDSAParameters.ml_dsa_44_with_sha512;
            case MLDSA65SHA:
                return MLDSAParameters.ml_dsa_65_with_sha512;
            case MLDSA87SHA:
                return MLDSAParameters.ml_dsa_87_with_sha512;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain MLDSA ParameterSpec.
     *
     * @return the parameters.
     */
    public MLDSAParameterSpec getParameterSpec() {
        switch (theSpec) {
            case MLDSA44:
                return MLDSAParameterSpec.ml_dsa_44;
            case MLDSA65:
                return MLDSAParameterSpec.ml_dsa_65;
            case MLDSA87:
                return MLDSAParameterSpec.ml_dsa_87;
            case MLDSA44SHA:
                return MLDSAParameterSpec.ml_dsa_44_with_sha512;
            case MLDSA65SHA:
                return MLDSAParameterSpec.ml_dsa_65_with_sha512;
            case MLDSA87SHA:
                return MLDSAParameterSpec.ml_dsa_87_with_sha512;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain MLDSA algorithm Identifier.
     *
     * @return the identifier.
     */
    public ASN1ObjectIdentifier getIdentifier() {
        switch (theSpec) {
            case MLDSA44:
                return NISTObjectIdentifiers.id_ml_dsa_44;
            case MLDSA65:
                return NISTObjectIdentifiers.id_ml_dsa_65;
            case MLDSA87:
                return NISTObjectIdentifiers.id_ml_dsa_87;
            case MLDSA44SHA:
                return NISTObjectIdentifiers.id_hash_ml_dsa_44_with_sha512;
            case MLDSA65SHA:
                return NISTObjectIdentifiers.id_hash_ml_dsa_65_with_sha512;
            case MLDSA87SHA:
                return NISTObjectIdentifiers.id_hash_ml_dsa_87_with_sha512;
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
        return pThat instanceof GordianCoreMLDSASpec myThat
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
    public static GordianCoreMLDSASpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianNewMLDSASpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianNewMLDSASpec, GordianCoreMLDSASpec> newSpecMap() {
        final Map<GordianNewMLDSASpec, GordianCoreMLDSASpec> myMap = new EnumMap<>(GordianNewMLDSASpec.class);
        for (GordianNewMLDSASpec mySpec : GordianNewMLDSASpec.values()) {
            myMap.put(mySpec, new GordianCoreMLDSASpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static Collection<GordianCoreMLDSASpec> values() {
        return SPECMAP.values();
    }
}
