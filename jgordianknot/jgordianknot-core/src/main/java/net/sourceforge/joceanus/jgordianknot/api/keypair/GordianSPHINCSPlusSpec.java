/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2022 Tony Washer
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

import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusParameters;
import org.bouncycastle.pqc.jcajce.spec.SPHINCSPlusParameterSpec;

/**
 * SphincsPlus KeySpecs.
 */
public enum GordianSPHINCSPlusSpec {
    /**
     * SHA2 128 f simple.
     */
    SHA128FS,

    /**
     * SHA2 128 s simple.
     */
    SHA128SS,

    /**
     * SHA2 128 f robust.
     */
    SHA128FR,

    /**
     * SHA2 128 s robust.
     */
    SHA128SR,

    /**
     * SHA2 192 f simple.
     */
    SHA192FS,

    /**
     * SHA2 192 s simple.
     */
    SHA192SS,

    /**
     * SHA2 128 f robust.
     */
    SHA192FR,

    /**
     * SHA2 128 s robust.
     */
    SHA192SR,

    /**
     * SHA2 256 f simple.
     */
    SHA256FS,

    /**
     * SHA2 256 s simple.
     */
    SHA256SS,

    /**
     * SHA2 256 f robust.
     */
    SHA256FR,

    /**
     * SHA2 256 s robust.
     */
    SHA256SR,

    /**
     * SHAKE 128 f simple.
     */
    SHAKE128FS,

    /**
     * SHAKE 128 s simple.
     */
    SHAKE128SS,

    /**
     * SHAKE 128 f robust.
     */
    SHAKE128FR,

    /**
     * SHAKE 128 s robust.
     */
    SHAKE128SR,

    /**
     * SHAKE 192 f simple.
     */
    SHAKE192FS,

    /**
     * SHAKE 192 s simple.
     */
    SHAKE192SS,

    /**
     * SHAKE 192 f robust.
     */
    SHAKE192FR,

    /**
     * SHAKE 192 s robust.
     */
    SHAKE192SR,

    /**
     * SHAKE 256 f simple.
     */
    SHAKE256FS,

    /**
     * SHAKE 256 s simple.
     */
    SHAKE256SS,

    /**
     * SHAKE 256 f robust.
     */
    SHAKE256FR,

    /**
     * SHAKE 256 s robust.
     */
    SHAKE256SR,

    /**
     * HARAKA 128 f simple.
     */
    HARAKA128FS,

    /**
     * HARAKA 128 s simple.
     */
    HARAKA128SS,

    /**
     * HARAKA 128 f robust.
     */
    HARAKA128FR,

    /**
     * HARAKA 128 s robust.
     */
    HARAKA128SR,

    /**
     * HARAKA 192 f simple.
     */
    HARAKA192FS,

    /**
     * HARAKA 192 s simple.
     */
    HARAKA192SS,

    /**
     * HARAKA 192 f robust.
     */
    HARAKA192FR,

    /**
     * HARAKA 192 s robust.
     */
    HARAKA192SR,

    /**
     * HARAKA 256 f simple.
     */
    HARAKA256FS,

    /**
     * HARAKA 256 s simple.
     */
    HARAKA256SS,

    /**
     * HARAKA 256 f robust.
     */
    HARAKA256FR,

    /**
     * HARAKA 256 s robust.
     */
    HARAKA256SR;

    /**
     * Obtain SPHINCSPlus Parameters.
     * @return the parameters.
     */
    public SPHINCSPlusParameters getParameters() {
        switch (this) {
            case SHA128FR:     return SPHINCSPlusParameters.sha2_128f;
            case SHA128SR:     return SPHINCSPlusParameters.sha2_128s;
            case SHA192FR:     return SPHINCSPlusParameters.sha2_192f;
            case SHA192SR:     return SPHINCSPlusParameters.sha2_192s;
            case SHA256FR:     return SPHINCSPlusParameters.sha2_256f;
            case SHA256SR:     return SPHINCSPlusParameters.sha2_256s;
            case SHA128FS:     return SPHINCSPlusParameters.sha2_128f_simple;
            case SHA128SS:     return SPHINCSPlusParameters.sha2_128s_simple;
            case SHA192FS:     return SPHINCSPlusParameters.sha2_192f_simple;
            case SHA192SS:     return SPHINCSPlusParameters.sha2_192s_simple;
            case SHA256FS:     return SPHINCSPlusParameters.sha2_256f_simple;
            case SHA256SS:     return SPHINCSPlusParameters.sha2_256s_simple;
            case SHAKE128FR:   return SPHINCSPlusParameters.shake_128f;
            case SHAKE128SR:   return SPHINCSPlusParameters.shake_128s;
            case SHAKE192FR:   return SPHINCSPlusParameters.shake_192f;
            case SHAKE192SR:   return SPHINCSPlusParameters.shake_192s;
            case SHAKE256FR:   return SPHINCSPlusParameters.shake_256f;
            case SHAKE256SR:   return SPHINCSPlusParameters.shake_256s;
            case SHAKE128FS:   return SPHINCSPlusParameters.shake_128f_simple;
            case SHAKE128SS:   return SPHINCSPlusParameters.shake_128s_simple;
            case SHAKE192FS:   return SPHINCSPlusParameters.shake_192f_simple;
            case SHAKE192SS:   return SPHINCSPlusParameters.shake_192s_simple;
            case SHAKE256FS:   return SPHINCSPlusParameters.shake_256f_simple;
            case SHAKE256SS:   return SPHINCSPlusParameters.shake_256s_simple;
            case HARAKA128FR:  return SPHINCSPlusParameters.haraka_128f;
            case HARAKA128SR:  return SPHINCSPlusParameters.haraka_128s;
            case HARAKA192FR:  return SPHINCSPlusParameters.haraka_192f;
            case HARAKA192SR:  return SPHINCSPlusParameters.haraka_192s;
            case HARAKA256FR:  return SPHINCSPlusParameters.haraka_256f;
            case HARAKA256SR:  return SPHINCSPlusParameters.haraka_256s;
            case HARAKA128FS:  return SPHINCSPlusParameters.haraka_128f_simple;
            case HARAKA128SS:  return SPHINCSPlusParameters.haraka_128s_simple;
            case HARAKA192FS:  return SPHINCSPlusParameters.haraka_192f_simple;
            case HARAKA192SS:  return SPHINCSPlusParameters.haraka_192s_simple;
            case HARAKA256FS:  return SPHINCSPlusParameters.haraka_256f_simple;
            case HARAKA256SS:  return SPHINCSPlusParameters.haraka_256s_simple;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain SPHINCSPlus ParameterSpec.
     * @return the parameters.
     */
    public SPHINCSPlusParameterSpec getParameterSpec() {
        switch (this) {
            case SHA128FR:    return SPHINCSPlusParameterSpec.sha2_128f;
            case SHA128SR:    return SPHINCSPlusParameterSpec.sha2_128s;
            case SHA192FR:    return SPHINCSPlusParameterSpec.sha2_192f;
            case SHA192SR:    return SPHINCSPlusParameterSpec.sha2_192s;
            case SHA256FR:    return SPHINCSPlusParameterSpec.sha2_256f;
            case SHA256SR:    return SPHINCSPlusParameterSpec.sha2_256s;
            case SHA128FS:    return SPHINCSPlusParameterSpec.sha2_128f_simple;
            case SHA128SS:    return SPHINCSPlusParameterSpec.sha2_128s_simple;
            case SHA192FS:    return SPHINCSPlusParameterSpec.sha2_192f_simple;
            case SHA192SS:    return SPHINCSPlusParameterSpec.sha2_192s_simple;
            case SHA256FS:    return SPHINCSPlusParameterSpec.sha2_256f_simple;
            case SHA256SS:    return SPHINCSPlusParameterSpec.sha2_256s_simple;
            case SHAKE128FR:  return SPHINCSPlusParameterSpec.shake_128f;
            case SHAKE128SR:  return SPHINCSPlusParameterSpec.shake_128s;
            case SHAKE192FR:  return SPHINCSPlusParameterSpec.shake_192f;
            case SHAKE192SR:  return SPHINCSPlusParameterSpec.shake_192s;
            case SHAKE256FR:  return SPHINCSPlusParameterSpec.shake_256f;
            case SHAKE256SR:  return SPHINCSPlusParameterSpec.shake_256s;
            case SHAKE128FS:  return SPHINCSPlusParameterSpec.shake_128f_simple;
            case SHAKE128SS:  return SPHINCSPlusParameterSpec.shake_128s_simple;
            case SHAKE192FS:  return SPHINCSPlusParameterSpec.shake_192f_simple;
            case SHAKE192SS:  return SPHINCSPlusParameterSpec.shake_192s_simple;
            case SHAKE256FS:  return SPHINCSPlusParameterSpec.shake_256f_simple;
            case SHAKE256SS:  return SPHINCSPlusParameterSpec.shake_256s_simple;
            case HARAKA128FR: return SPHINCSPlusParameterSpec.haraka_128f;
            case HARAKA128SR: return SPHINCSPlusParameterSpec.haraka_128s;
            case HARAKA192FR: return SPHINCSPlusParameterSpec.haraka_192f;
            case HARAKA192SR: return SPHINCSPlusParameterSpec.haraka_192s;
            case HARAKA256FR: return SPHINCSPlusParameterSpec.haraka_256f;
            case HARAKA256SR: return SPHINCSPlusParameterSpec.haraka_256s;
            case HARAKA128FS: return SPHINCSPlusParameterSpec.haraka_128f_simple;
            case HARAKA128SS: return SPHINCSPlusParameterSpec.haraka_128s_simple;
            case HARAKA192FS: return SPHINCSPlusParameterSpec.haraka_192f_simple;
            case HARAKA192SS: return SPHINCSPlusParameterSpec.haraka_192s_simple;
            case HARAKA256FS: return SPHINCSPlusParameterSpec.haraka_256f_simple;
            case HARAKA256SS: return SPHINCSPlusParameterSpec.haraka_256s_simple;
            default: throw new IllegalArgumentException();
        }
    }
}
