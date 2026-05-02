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

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSLHDSASpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.params.SLHDSAParameters;
import org.bouncycastle.jcajce.spec.SLHDSAParameterSpec;

import java.util.EnumMap;
import java.util.Map;

/**
 * SphincsPlus KeySpecs.
 */
public final class GordianCoreSLHDSASpec {
    /**
     * The specMap.
     */
    private static final Map<GordianSLHDSASpec, GordianCoreSLHDSASpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreSLHDSASpec[] VALUES = SPECMAP.values().toArray(new GordianCoreSLHDSASpec[0]);

    /**
     * The Spec.
     */
    private final GordianSLHDSASpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreSLHDSASpec(final GordianSLHDSASpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianSLHDSASpec getSpec() {
        return theSpec;
    }

    /**
     * Is this a hash signer?
     *
     * @return true/false
     */
    public boolean isHash() {
        return switch (theSpec) {
            case SHA128F, SHA128S, SHA192F, SHA192S, SHA256F, SHA256S, SHAKE128F, SHAKE128S, SHAKE192F, SHAKE192S,
                 SHAKE256F, SHAKE256S -> false;
            default -> true;
        };
    }

    /**
     * Obtain SLHDSA Parameters.
     *
     * @return the parameters.
     */
    public SLHDSAParameters getParameters() {
        return switch (theSpec) {
            case SHA128F -> SLHDSAParameters.sha2_128f;
            case SHA128S -> SLHDSAParameters.sha2_128s;
            case SHA192F -> SLHDSAParameters.sha2_192f;
            case SHA192S -> SLHDSAParameters.sha2_192s;
            case SHA256F -> SLHDSAParameters.sha2_256f;
            case SHA256S -> SLHDSAParameters.sha2_256s;
            case SHAKE128F -> SLHDSAParameters.shake_128f;
            case SHAKE128S -> SLHDSAParameters.shake_128s;
            case SHAKE192F -> SLHDSAParameters.shake_192f;
            case SHAKE192S -> SLHDSAParameters.shake_192s;
            case SHAKE256F -> SLHDSAParameters.shake_256f;
            case SHAKE256S -> SLHDSAParameters.shake_256s;
            case SHA128F_HASH -> SLHDSAParameters.sha2_128f_with_sha256;
            case SHA128S_HASH -> SLHDSAParameters.sha2_128s_with_sha256;
            case SHA192F_HASH -> SLHDSAParameters.sha2_192f_with_sha512;
            case SHA192S_HASH -> SLHDSAParameters.sha2_192s_with_sha512;
            case SHA256F_HASH -> SLHDSAParameters.sha2_256f_with_sha512;
            case SHA256S_HASH -> SLHDSAParameters.sha2_256s_with_sha512;
            case SHAKE128F_HASH -> SLHDSAParameters.shake_128f_with_shake128;
            case SHAKE128S_HASH -> SLHDSAParameters.shake_128s_with_shake128;
            case SHAKE192F_HASH -> SLHDSAParameters.shake_192f_with_shake256;
            case SHAKE192S_HASH -> SLHDSAParameters.shake_192s_with_shake256;
            case SHAKE256F_HASH -> SLHDSAParameters.shake_256f_with_shake256;
            case SHAKE256S_HASH -> SLHDSAParameters.shake_256s_with_shake256;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain SPHINCSPlus ParameterSpec.
     *
     * @return the parameters.
     */
    public SLHDSAParameterSpec getParameterSpec() {
        return switch (theSpec) {
            case SHA128F -> SLHDSAParameterSpec.slh_dsa_sha2_128f;
            case SHA128S -> SLHDSAParameterSpec.slh_dsa_sha2_128s;
            case SHA192F -> SLHDSAParameterSpec.slh_dsa_sha2_192f;
            case SHA192S -> SLHDSAParameterSpec.slh_dsa_sha2_192s;
            case SHA256F -> SLHDSAParameterSpec.slh_dsa_sha2_256f;
            case SHA256S -> SLHDSAParameterSpec.slh_dsa_sha2_256s;
            case SHAKE128F -> SLHDSAParameterSpec.slh_dsa_shake_128f;
            case SHAKE128S -> SLHDSAParameterSpec.slh_dsa_shake_128s;
            case SHAKE192F -> SLHDSAParameterSpec.slh_dsa_shake_192f;
            case SHAKE192S -> SLHDSAParameterSpec.slh_dsa_shake_192s;
            case SHAKE256F -> SLHDSAParameterSpec.slh_dsa_shake_256f;
            case SHAKE256S -> SLHDSAParameterSpec.slh_dsa_shake_256s;
            case SHA128F_HASH -> SLHDSAParameterSpec.slh_dsa_sha2_128f_with_sha256;
            case SHA128S_HASH -> SLHDSAParameterSpec.slh_dsa_sha2_128s_with_sha256;
            case SHA192F_HASH -> SLHDSAParameterSpec.slh_dsa_sha2_192f_with_sha512;
            case SHA192S_HASH -> SLHDSAParameterSpec.slh_dsa_sha2_192s_with_sha512;
            case SHA256F_HASH -> SLHDSAParameterSpec.slh_dsa_sha2_256f_with_sha512;
            case SHA256S_HASH -> SLHDSAParameterSpec.slh_dsa_sha2_256s_with_sha512;
            case SHAKE128F_HASH -> SLHDSAParameterSpec.slh_dsa_shake_128f_with_shake128;
            case SHAKE128S_HASH -> SLHDSAParameterSpec.slh_dsa_shake_128s_with_shake128;
            case SHAKE192F_HASH -> SLHDSAParameterSpec.slh_dsa_shake_192f_with_shake256;
            case SHAKE192S_HASH -> SLHDSAParameterSpec.slh_dsa_shake_192s_with_shake256;
            case SHAKE256F_HASH -> SLHDSAParameterSpec.slh_dsa_shake_256f_with_shake256;
            case SHAKE256S_HASH -> SLHDSAParameterSpec.slh_dsa_shake_256s_with_shake256;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain SLHDSA algorithm Identifier.
     *
     * @return the identifier.
     */
    public ASN1ObjectIdentifier getIdentifier() {
        return switch (theSpec) {
            case SHA128F -> NISTObjectIdentifiers.id_slh_dsa_sha2_128f;
            case SHA128S -> NISTObjectIdentifiers.id_slh_dsa_sha2_128s;
            case SHA192F -> NISTObjectIdentifiers.id_slh_dsa_sha2_192f;
            case SHA192S -> NISTObjectIdentifiers.id_slh_dsa_sha2_192s;
            case SHA256F -> NISTObjectIdentifiers.id_slh_dsa_sha2_256f;
            case SHA256S -> NISTObjectIdentifiers.id_slh_dsa_sha2_256s;
            case SHAKE128F -> NISTObjectIdentifiers.id_slh_dsa_shake_128f;
            case SHAKE128S -> NISTObjectIdentifiers.id_slh_dsa_shake_128s;
            case SHAKE192F -> NISTObjectIdentifiers.id_slh_dsa_shake_192f;
            case SHAKE192S -> NISTObjectIdentifiers.id_slh_dsa_shake_192s;
            case SHAKE256F -> NISTObjectIdentifiers.id_slh_dsa_shake_256f;
            case SHAKE256S -> NISTObjectIdentifiers.id_slh_dsa_shake_256s;
            case SHA128F_HASH -> NISTObjectIdentifiers.id_hash_slh_dsa_sha2_128f_with_sha256;
            case SHA128S_HASH -> NISTObjectIdentifiers.id_hash_slh_dsa_sha2_128s_with_sha256;
            case SHA192F_HASH -> NISTObjectIdentifiers.id_hash_slh_dsa_sha2_192f_with_sha512;
            case SHA192S_HASH -> NISTObjectIdentifiers.id_hash_slh_dsa_sha2_192s_with_sha512;
            case SHA256F_HASH -> NISTObjectIdentifiers.id_hash_slh_dsa_sha2_256f_with_sha512;
            case SHA256S_HASH -> NISTObjectIdentifiers.id_hash_slh_dsa_sha2_256s_with_sha512;
            case SHAKE128F_HASH -> NISTObjectIdentifiers.id_hash_slh_dsa_shake_128f_with_shake128;
            case SHAKE128S_HASH -> NISTObjectIdentifiers.id_hash_slh_dsa_shake_128s_with_shake128;
            case SHAKE192F_HASH -> NISTObjectIdentifiers.id_hash_slh_dsa_shake_192f_with_shake256;
            case SHAKE192S_HASH -> NISTObjectIdentifiers.id_hash_slh_dsa_shake_192s_with_shake256;
            case SHAKE256F_HASH -> NISTObjectIdentifiers.id_hash_slh_dsa_shake_256f_with_shake256;
            case SHAKE256S_HASH -> NISTObjectIdentifiers.id_hash_slh_dsa_shake_256s_with_shake256;
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
        return pThat instanceof GordianCoreSLHDSASpec myThat
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
    public static GordianCoreSLHDSASpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianSLHDSASpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianSLHDSASpec, GordianCoreSLHDSASpec> newSpecMap() {
        final Map<GordianSLHDSASpec, GordianCoreSLHDSASpec> myMap = new EnumMap<>(GordianSLHDSASpec.class);
        for (GordianSLHDSASpec mySpec : GordianSLHDSASpec.values()) {
            myMap.put(mySpec, new GordianCoreSLHDSASpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreSLHDSASpec[] values() {
        return VALUES;
    }
}
