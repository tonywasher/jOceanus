/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
package io.github.tonywasher.joceanus.gordianknot.api.keypair;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.jcajce.spec.SLHDSAParameterSpec;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAParameters;

/**
 * SphincsPlus KeySpecs.
 */
public enum GordianSLHDSASpec {
    /**
     * SHA2 128f.
     */
    SHA128F,

    /**
     * SHA2 128s.
     */
    SHA128S,

    /**
     * SHA2 192f.
     */
    SHA192F,

    /**
     * SHA2 192s.
     */
    SHA192S,

    /**
     * SHA2 256f.
     */
    SHA256F,

    /**
     * SHA2 256s.
     */
    SHA256S,

    /**
     * SHAKE 128f.
     */
    SHAKE128F,

    /**
     * SHAKE 128s.
     */
    SHAKE128S,

    /**
     * SHAKE 192f.
     */
    SHAKE192F,

    /**
     * SHAKE 192s.
     */
    SHAKE192S,

    /**
     * SHAKE 256f.
     */
    SHAKE256F,

    /**
     * SHAKE 256s.
     */
    SHAKE256S,

    /**
     * SHA2 128f hash.
     */
    SHA128F_HASH,

    /**
     * SHA2 128s hash.
     */
    SHA128S_HASH,

    /**
     * SHA2 192f hash.
     */
    SHA192F_HASH,

    /**
     * SHA2 192s hash.
     */
    SHA192S_HASH,

    /**
     * SHA2 256f hash.
     */
    SHA256F_HASH,

    /**
     * SHA2 256s hash.
     */
    SHA256S_HASH,

    /**
     * SHAKE 128f hash.
     */
    SHAKE128F_HASH,

    /**
     * SHAKE 128s hash.
     */
    SHAKE128S_HASH,

    /**
     * SHAKE 192f hash.
     */
    SHAKE192F_HASH,

    /**
     * SHAKE 192s hash.
     */
    SHAKE192S_HASH,

    /**
     * SHAKE 256f hash.
     */
    SHAKE256F_HASH,

    /**
     * SHAKE 256s hash.
     */
    SHAKE256S_HASH;


    /**
     * Is this a hash signer?
     *
     * @return true/false
     */
    public boolean isHash() {
        switch (this) {
            case SHA128F:
            case SHA128S:
            case SHA192F:
            case SHA192S:
            case SHA256F:
            case SHA256S:
            case SHAKE128F:
            case SHAKE128S:
            case SHAKE192F:
            case SHAKE192S:
            case SHAKE256F:
            case SHAKE256S:
                return false;
            case SHA128F_HASH:
            case SHA128S_HASH:
            case SHA192F_HASH:
            case SHA192S_HASH:
            case SHA256F_HASH:
            case SHA256S_HASH:
            case SHAKE128F_HASH:
            case SHAKE128S_HASH:
            case SHAKE192F_HASH:
            case SHAKE192S_HASH:
            case SHAKE256F_HASH:
            case SHAKE256S_HASH:
            default:
                return true;
        }
    }

    /**
     * Obtain SLHDSA Parameters.
     *
     * @return the parameters.
     */
    public SLHDSAParameters getParameters() {
        switch (this) {
            case SHA128F:
                return SLHDSAParameters.sha2_128f;
            case SHA128S:
                return SLHDSAParameters.sha2_128s;
            case SHA192F:
                return SLHDSAParameters.sha2_192f;
            case SHA192S:
                return SLHDSAParameters.sha2_192s;
            case SHA256F:
                return SLHDSAParameters.sha2_256f;
            case SHA256S:
                return SLHDSAParameters.sha2_256s;
            case SHAKE128F:
                return SLHDSAParameters.shake_128f;
            case SHAKE128S:
                return SLHDSAParameters.shake_128s;
            case SHAKE192F:
                return SLHDSAParameters.shake_192f;
            case SHAKE192S:
                return SLHDSAParameters.shake_192s;
            case SHAKE256F:
                return SLHDSAParameters.shake_256f;
            case SHAKE256S:
                return SLHDSAParameters.shake_256s;
            case SHA128F_HASH:
                return SLHDSAParameters.sha2_128f_with_sha256;
            case SHA128S_HASH:
                return SLHDSAParameters.sha2_128s_with_sha256;
            case SHA192F_HASH:
                return SLHDSAParameters.sha2_192f_with_sha512;
            case SHA192S_HASH:
                return SLHDSAParameters.sha2_192s_with_sha512;
            case SHA256F_HASH:
                return SLHDSAParameters.sha2_256f_with_sha512;
            case SHA256S_HASH:
                return SLHDSAParameters.sha2_256s_with_sha512;
            case SHAKE128F_HASH:
                return SLHDSAParameters.shake_128f_with_shake128;
            case SHAKE128S_HASH:
                return SLHDSAParameters.shake_128s_with_shake128;
            case SHAKE192F_HASH:
                return SLHDSAParameters.shake_192f_with_shake256;
            case SHAKE192S_HASH:
                return SLHDSAParameters.shake_192s_with_shake256;
            case SHAKE256F_HASH:
                return SLHDSAParameters.shake_256f_with_shake256;
            case SHAKE256S_HASH:
                return SLHDSAParameters.shake_256s_with_shake256;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain SPHINCSPlus ParameterSpec.
     *
     * @return the parameters.
     */
    public SLHDSAParameterSpec getParameterSpec() {
        switch (this) {
            case SHA128F:
                return SLHDSAParameterSpec.slh_dsa_sha2_128f;
            case SHA128S:
                return SLHDSAParameterSpec.slh_dsa_sha2_128s;
            case SHA192F:
                return SLHDSAParameterSpec.slh_dsa_sha2_192f;
            case SHA192S:
                return SLHDSAParameterSpec.slh_dsa_sha2_192s;
            case SHA256F:
                return SLHDSAParameterSpec.slh_dsa_sha2_256f;
            case SHA256S:
                return SLHDSAParameterSpec.slh_dsa_sha2_256s;
            case SHAKE128F:
                return SLHDSAParameterSpec.slh_dsa_shake_128f;
            case SHAKE128S:
                return SLHDSAParameterSpec.slh_dsa_shake_128s;
            case SHAKE192F:
                return SLHDSAParameterSpec.slh_dsa_shake_192f;
            case SHAKE192S:
                return SLHDSAParameterSpec.slh_dsa_shake_192s;
            case SHAKE256F:
                return SLHDSAParameterSpec.slh_dsa_shake_256f;
            case SHAKE256S:
                return SLHDSAParameterSpec.slh_dsa_shake_256s;
            case SHA128F_HASH:
                return SLHDSAParameterSpec.slh_dsa_sha2_128f_with_sha256;
            case SHA128S_HASH:
                return SLHDSAParameterSpec.slh_dsa_sha2_128s_with_sha256;
            case SHA192F_HASH:
                return SLHDSAParameterSpec.slh_dsa_sha2_192f_with_sha512;
            case SHA192S_HASH:
                return SLHDSAParameterSpec.slh_dsa_sha2_192s_with_sha512;
            case SHA256F_HASH:
                return SLHDSAParameterSpec.slh_dsa_sha2_256f_with_sha512;
            case SHA256S_HASH:
                return SLHDSAParameterSpec.slh_dsa_sha2_256s_with_sha512;
            case SHAKE128F_HASH:
                return SLHDSAParameterSpec.slh_dsa_shake_128f_with_shake128;
            case SHAKE128S_HASH:
                return SLHDSAParameterSpec.slh_dsa_shake_128s_with_shake128;
            case SHAKE192F_HASH:
                return SLHDSAParameterSpec.slh_dsa_shake_192f_with_shake256;
            case SHAKE192S_HASH:
                return SLHDSAParameterSpec.slh_dsa_shake_192s_with_shake256;
            case SHAKE256F_HASH:
                return SLHDSAParameterSpec.slh_dsa_shake_256f_with_shake256;
            case SHAKE256S_HASH:
                return SLHDSAParameterSpec.slh_dsa_shake_256s_with_shake256;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain SLHDSA algorithm Identifier.
     *
     * @return the identifier.
     */
    public ASN1ObjectIdentifier getIdentifier() {
        switch (this) {
            case SHA128F:
                return NISTObjectIdentifiers.id_slh_dsa_sha2_128f;
            case SHA128S:
                return NISTObjectIdentifiers.id_slh_dsa_sha2_128s;
            case SHA192F:
                return NISTObjectIdentifiers.id_slh_dsa_sha2_192f;
            case SHA192S:
                return NISTObjectIdentifiers.id_slh_dsa_sha2_192s;
            case SHA256F:
                return NISTObjectIdentifiers.id_slh_dsa_sha2_256f;
            case SHA256S:
                return NISTObjectIdentifiers.id_slh_dsa_sha2_256s;
            case SHAKE128F:
                return NISTObjectIdentifiers.id_slh_dsa_shake_128f;
            case SHAKE128S:
                return NISTObjectIdentifiers.id_slh_dsa_shake_128s;
            case SHAKE192F:
                return NISTObjectIdentifiers.id_slh_dsa_shake_192f;
            case SHAKE192S:
                return NISTObjectIdentifiers.id_slh_dsa_shake_192s;
            case SHAKE256F:
                return NISTObjectIdentifiers.id_slh_dsa_shake_256f;
            case SHAKE256S:
                return NISTObjectIdentifiers.id_slh_dsa_shake_256s;
            case SHA128F_HASH:
                return NISTObjectIdentifiers.id_hash_slh_dsa_sha2_128f_with_sha256;
            case SHA128S_HASH:
                return NISTObjectIdentifiers.id_hash_slh_dsa_sha2_128s_with_sha256;
            case SHA192F_HASH:
                return NISTObjectIdentifiers.id_hash_slh_dsa_sha2_192f_with_sha512;
            case SHA192S_HASH:
                return NISTObjectIdentifiers.id_hash_slh_dsa_sha2_192s_with_sha512;
            case SHA256F_HASH:
                return NISTObjectIdentifiers.id_hash_slh_dsa_sha2_256f_with_sha512;
            case SHA256S_HASH:
                return NISTObjectIdentifiers.id_hash_slh_dsa_sha2_256s_with_sha512;
            case SHAKE128F_HASH:
                return NISTObjectIdentifiers.id_hash_slh_dsa_shake_128f_with_shake128;
            case SHAKE128S_HASH:
                return NISTObjectIdentifiers.id_hash_slh_dsa_shake_128s_with_shake128;
            case SHAKE192F_HASH:
                return NISTObjectIdentifiers.id_hash_slh_dsa_shake_192f_with_shake256;
            case SHAKE192S_HASH:
                return NISTObjectIdentifiers.id_hash_slh_dsa_shake_192s_with_shake256;
            case SHAKE256F_HASH:
                return NISTObjectIdentifiers.id_hash_slh_dsa_shake_256f_with_shake256;
            case SHAKE256S_HASH:
                return NISTObjectIdentifiers.id_hash_slh_dsa_shake_256s_with_shake256;
            default:
                throw new IllegalArgumentException();
        }
    }
}
