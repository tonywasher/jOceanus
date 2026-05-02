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

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianMLDSASpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.params.MLDSAParameters;
import org.bouncycastle.jcajce.spec.MLDSAParameterSpec;

import java.util.EnumMap;
import java.util.Map;

/**
 * MLDSA KeySpec.
 */
public final class GordianCoreMLDSASpec {
    /**
     * The specMap.
     */
    private static final Map<GordianMLDSASpec, GordianCoreMLDSASpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreMLDSASpec[] VALUES = SPECMAP.values().toArray(new GordianCoreMLDSASpec[0]);

    /**
     * The Spec.
     */
    private final GordianMLDSASpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreMLDSASpec(final GordianMLDSASpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianMLDSASpec getSpec() {
        return theSpec;
    }

    /**
     * Is this a hash signer?
     *
     * @return true/false
     */
    public boolean isHash() {
        return switch (theSpec) {
            case MLDSA44SHA, MLDSA65SHA, MLDSA87SHA -> true;
            default -> false;
        };
    }

    /**
     * Obtain MLDSA Parameters.
     *
     * @return the parameters.
     */
    public MLDSAParameters getParameters() {
        return switch (theSpec) {
            case MLDSA44 -> MLDSAParameters.ml_dsa_44;
            case MLDSA65 -> MLDSAParameters.ml_dsa_65;
            case MLDSA87 -> MLDSAParameters.ml_dsa_87;
            case MLDSA44SHA -> MLDSAParameters.ml_dsa_44_with_sha512;
            case MLDSA65SHA -> MLDSAParameters.ml_dsa_65_with_sha512;
            case MLDSA87SHA -> MLDSAParameters.ml_dsa_87_with_sha512;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain MLDSA ParameterSpec.
     *
     * @return the parameters.
     */
    public MLDSAParameterSpec getParameterSpec() {
        return switch (theSpec) {
            case MLDSA44 -> MLDSAParameterSpec.ml_dsa_44;
            case MLDSA65 -> MLDSAParameterSpec.ml_dsa_65;
            case MLDSA87 -> MLDSAParameterSpec.ml_dsa_87;
            case MLDSA44SHA -> MLDSAParameterSpec.ml_dsa_44_with_sha512;
            case MLDSA65SHA -> MLDSAParameterSpec.ml_dsa_65_with_sha512;
            case MLDSA87SHA -> MLDSAParameterSpec.ml_dsa_87_with_sha512;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain MLDSA algorithm Identifier.
     *
     * @return the identifier.
     */
    public ASN1ObjectIdentifier getIdentifier() {
        return switch (theSpec) {
            case MLDSA44 -> NISTObjectIdentifiers.id_ml_dsa_44;
            case MLDSA65 -> NISTObjectIdentifiers.id_ml_dsa_65;
            case MLDSA87 -> NISTObjectIdentifiers.id_ml_dsa_87;
            case MLDSA44SHA -> NISTObjectIdentifiers.id_hash_ml_dsa_44_with_sha512;
            case MLDSA65SHA -> NISTObjectIdentifiers.id_hash_ml_dsa_65_with_sha512;
            case MLDSA87SHA -> NISTObjectIdentifiers.id_hash_ml_dsa_87_with_sha512;
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
        return pSpec instanceof GordianMLDSASpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianMLDSASpec, GordianCoreMLDSASpec> newSpecMap() {
        final Map<GordianMLDSASpec, GordianCoreMLDSASpec> myMap = new EnumMap<>(GordianMLDSASpec.class);
        for (GordianMLDSASpec mySpec : GordianMLDSASpec.values()) {
            myMap.put(mySpec, new GordianCoreMLDSASpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreMLDSASpec[] values() {
        return VALUES;
    }
}
