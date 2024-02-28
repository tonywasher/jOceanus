/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.api.keypair;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusParameters;
import org.bouncycastle.pqc.jcajce.spec.SPHINCSPlusParameterSpec;

/**
 * SphincsPlus KeySpecs.
 */
public enum GordianSPHINCSPlusSpec {
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
     * SHA2 128f robust.
     */
    SHA128F_R,

    /**
     * SHA2 128s robust.
     */
    SHA128S_R,

    /**
     * SHA2 192f robust.
     */
    SHA192F_R,

    /**
     * SHA2 192s robust.
     */
    SHA192S_R,

    /**
     * SHA2 256f robust.
     */
    SHA256F_R,

    /**
     * SHA2 256s robust.
     */
    SHA256S_R,

    /**
     * SHAKE 128f robust.
     */
    SHAKE128F_R,

    /**
     * SHAKE 128s robust.
     */
    SHAKE128S_R,

    /**
     * SHAKE 192f robust.
     */
    SHAKE192F_R,

    /**
     * SHAKE 192s robust.
     */
    SHAKE192S_R,

    /**
     * SHAKE 256f robust.
     */
    SHAKE256F_R,

    /**
     * SHAKE 256s robust.
     */
    SHAKE256S_R,

    /**
     * HARAKA 128f simple.
     */
    HARAKA128F_S,

    /**
     * HARAKA 128s simple.
     */
    HARAKA128S_S,

    /**
     * HARAKA 192f simple.
     */
    HARAKA192F_S,

    /**
     * HARAKA 192s simple.
     */
    HARAKA192S_S,

    /**
     * HARAKA 256f simple.
     */
    HARAKA256F_S,

    /**
     * HARAKA 256s simple.
     */
    HARAKA256S_S;

    /**
     * Obtain SPHINCSPlus Parameters.
     * @return the parameters.
     */
    public SPHINCSPlusParameters getParameters() {
        switch (this) {
            case SHA128F:       return SPHINCSPlusParameters.sha2_128f;
            case SHA128S:       return SPHINCSPlusParameters.sha2_128s;
            case SHA192F:       return SPHINCSPlusParameters.sha2_192f;
            case SHA192S:       return SPHINCSPlusParameters.sha2_192s;
            case SHA256F:       return SPHINCSPlusParameters.sha2_256f;
            case SHA256S:       return SPHINCSPlusParameters.sha2_256s;
            case SHAKE128F:     return SPHINCSPlusParameters.shake_128f;
            case SHAKE128S:     return SPHINCSPlusParameters.shake_128s;
            case SHAKE192F:     return SPHINCSPlusParameters.shake_192f;
            case SHAKE192S:     return SPHINCSPlusParameters.shake_192s;
            case SHAKE256F:     return SPHINCSPlusParameters.shake_256f;
            case SHAKE256S:     return SPHINCSPlusParameters.shake_256s;
            case SHA128F_R:     return SPHINCSPlusParameters.sha2_128f_robust;
            case SHA128S_R:     return SPHINCSPlusParameters.sha2_128s_robust;
            case SHA192F_R:     return SPHINCSPlusParameters.sha2_192f_robust;
            case SHA192S_R:     return SPHINCSPlusParameters.sha2_192s_robust;
            case SHA256F_R:     return SPHINCSPlusParameters.sha2_256f_robust;
            case SHA256S_R:     return SPHINCSPlusParameters.sha2_256s_robust;
            case SHAKE128F_R:   return SPHINCSPlusParameters.shake_128f_robust;
            case SHAKE128S_R:   return SPHINCSPlusParameters.shake_128s_robust;
            case SHAKE192F_R:   return SPHINCSPlusParameters.shake_192f_robust;
            case SHAKE192S_R:   return SPHINCSPlusParameters.shake_192s_robust;
            case SHAKE256F_R:   return SPHINCSPlusParameters.shake_256f_robust;
            case SHAKE256S_R:   return SPHINCSPlusParameters.shake_256s_robust;
            case HARAKA128F_S:  return SPHINCSPlusParameters.haraka_128f_simple;
            case HARAKA128S_S:  return SPHINCSPlusParameters.haraka_128s_simple;
            case HARAKA192F_S:  return SPHINCSPlusParameters.haraka_192f_simple;
            case HARAKA192S_S:  return SPHINCSPlusParameters.haraka_192s_simple;
            case HARAKA256F_S:  return SPHINCSPlusParameters.haraka_256f_simple;
            case HARAKA256S_S:  return SPHINCSPlusParameters.haraka_256s_simple;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain SPHINCSPlus ParameterSpec.
     * @return the parameters.
     */
    public SPHINCSPlusParameterSpec getParameterSpec() {
        switch (this) {
            case SHA128F:      return SPHINCSPlusParameterSpec.sha2_128f;
            case SHA128S:      return SPHINCSPlusParameterSpec.sha2_128s;
            case SHA192F:      return SPHINCSPlusParameterSpec.sha2_192f;
            case SHA192S:      return SPHINCSPlusParameterSpec.sha2_192s;
            case SHA256F:      return SPHINCSPlusParameterSpec.sha2_256f;
            case SHA256S:      return SPHINCSPlusParameterSpec.sha2_256s;
            case SHAKE128F:    return SPHINCSPlusParameterSpec.shake_128f;
            case SHAKE128S:    return SPHINCSPlusParameterSpec.shake_128s;
            case SHAKE192F:    return SPHINCSPlusParameterSpec.shake_192f;
            case SHAKE192S:    return SPHINCSPlusParameterSpec.shake_192s;
            case SHAKE256F:    return SPHINCSPlusParameterSpec.shake_256f;
            case SHAKE256S:    return SPHINCSPlusParameterSpec.shake_256s;
            case SHA128F_R:    return SPHINCSPlusParameterSpec.sha2_128f_robust;
            case SHA128S_R:    return SPHINCSPlusParameterSpec.sha2_128s_robust;
            case SHA192F_R:    return SPHINCSPlusParameterSpec.sha2_192f_robust;
            case SHA192S_R:    return SPHINCSPlusParameterSpec.sha2_192s_robust;
            case SHA256F_R:    return SPHINCSPlusParameterSpec.sha2_256f_robust;
            case SHA256S_R:    return SPHINCSPlusParameterSpec.sha2_256s_robust;
            case SHAKE128F_R:  return SPHINCSPlusParameterSpec.shake_128f_robust;
            case SHAKE128S_R:  return SPHINCSPlusParameterSpec.shake_128s_robust;
            case SHAKE192F_R:  return SPHINCSPlusParameterSpec.shake_192f_robust;
            case SHAKE192S_R:  return SPHINCSPlusParameterSpec.shake_192s_robust;
            case SHAKE256F_R:  return SPHINCSPlusParameterSpec.shake_256f_robust;
            case SHAKE256S_R:  return SPHINCSPlusParameterSpec.shake_256s_robust;
            case HARAKA128F_S: return SPHINCSPlusParameterSpec.haraka_128f_simple;
            case HARAKA128S_S: return SPHINCSPlusParameterSpec.haraka_128s_simple;
            case HARAKA192F_S: return SPHINCSPlusParameterSpec.haraka_192f_simple;
            case HARAKA192S_S: return SPHINCSPlusParameterSpec.haraka_192s_simple;
            case HARAKA256F_S: return SPHINCSPlusParameterSpec.haraka_256f_simple;
            case HARAKA256S_S: return SPHINCSPlusParameterSpec.haraka_256s_simple;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain SPHINCSPlus ParameterSpec.
     * @return the parameters.
     */
    public ASN1ObjectIdentifier getIdentifier() {
        switch (this) {
            case SHA128F:       return BCObjectIdentifiers.sphincsPlus_sha2_128f_r3;
            case SHA128S:       return BCObjectIdentifiers.sphincsPlus_sha2_128s_r3;
            case SHA192F:       return BCObjectIdentifiers.sphincsPlus_sha2_192f_r3;
            case SHA192S:       return BCObjectIdentifiers.sphincsPlus_sha2_192s_r3;
            case SHA256F:       return BCObjectIdentifiers.sphincsPlus_sha2_256f_r3;
            case SHA256S:       return BCObjectIdentifiers.sphincsPlus_sha2_256s_r3;
            case SHAKE128F:     return BCObjectIdentifiers.sphincsPlus_shake_128f_r3;
            case SHAKE128S:     return BCObjectIdentifiers.sphincsPlus_shake_128s_r3;
            case SHAKE192F:     return BCObjectIdentifiers.sphincsPlus_shake_192f_r3;
            case SHAKE192S:     return BCObjectIdentifiers.sphincsPlus_shake_192s_r3;
            case SHAKE256F:     return BCObjectIdentifiers.sphincsPlus_shake_256f_r3;
            case SHAKE256S:     return BCObjectIdentifiers.sphincsPlus_shake_256s_r3;
            case SHA128F_R:     return BCObjectIdentifiers.sphincsPlus_sha2_128f_r3_simple;
            case SHA128S_R:     return BCObjectIdentifiers.sphincsPlus_sha2_128s_r3_simple;
            case SHA192F_R:     return BCObjectIdentifiers.sphincsPlus_sha2_192f_r3_simple;
            case SHA192S_R:     return BCObjectIdentifiers.sphincsPlus_sha2_192s_r3_simple;
            case SHA256F_R:     return BCObjectIdentifiers.sphincsPlus_sha2_256f_r3_simple;
            case SHA256S_R:     return BCObjectIdentifiers.sphincsPlus_sha2_256s_r3_simple;
            case SHAKE128F_R:   return BCObjectIdentifiers.sphincsPlus_shake_128f_r3_simple;
            case SHAKE128S_R:   return BCObjectIdentifiers.sphincsPlus_shake_128s_r3_simple;
            case SHAKE192F_R:   return BCObjectIdentifiers.sphincsPlus_shake_192f_r3_simple;
            case SHAKE192S_R:   return BCObjectIdentifiers.sphincsPlus_shake_192s_r3_simple;
            case SHAKE256F_R:   return BCObjectIdentifiers.sphincsPlus_shake_256f_r3_simple;
            case SHAKE256S_R:   return BCObjectIdentifiers.sphincsPlus_shake_256s_r3_simple;
            case HARAKA128F_S:  return BCObjectIdentifiers.sphincsPlus_haraka_128f_r3_simple;
            case HARAKA128S_S:  return BCObjectIdentifiers.sphincsPlus_haraka_128s_r3_simple;
            case HARAKA192F_S:  return BCObjectIdentifiers.sphincsPlus_haraka_192f_r3_simple;
            case HARAKA192S_S:  return BCObjectIdentifiers.sphincsPlus_haraka_192s_r3_simple;
            case HARAKA256F_S:  return BCObjectIdentifiers.sphincsPlus_haraka_256f_r3_simple;
            case HARAKA256S_S:  return BCObjectIdentifiers.sphincsPlus_haraka_256s_r3_simple;
            default: throw new IllegalArgumentException();
        }
    }
}
