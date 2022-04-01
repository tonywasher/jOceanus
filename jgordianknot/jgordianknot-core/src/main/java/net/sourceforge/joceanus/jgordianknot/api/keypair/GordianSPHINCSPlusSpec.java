/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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
     * SHA256 128 f simple.
     */
    SHA128FS,

    /**
     * SHA256 128 s simple.
     */
    SHA128SS,

    /**
     * SHA256 128 f robust.
     */
    SHA128FR,

    /**
     * SHA256 128 s robust.
     */
    SHA128SR,

    /**
     * SHA256 192 f simple.
     */
    SHA192FS,

    /**
     * SHA256 192 s simple.
     */
    SHA192SS,

    /**
     * SHA256 128 f robust.
     */
    SHA192FR,

    /**
     * SHA256 128 s robust.
     */
    SHA192SR,

    /**
     * SHA256 256 f simple.
     */
    SHA256FS,

    /**
     * SHA256 256 s simple.
     */
    SHA256SS,

    /**
     * SHA256 256 f robust.
     */
    SHA256FR,

    /**
     * SHA256 256 s robust.
     */
    SHA256SR,

    /**
     * SHAKE256 128 f simple.
     */
    SHAKE128FS,

    /**
     * SHAKE256 128 s simple.
     */
    SHAKE128SS,

    /**
     * SHAKE256 128 f robust.
     */
    SHAKE128FR,

    /**
     * SHAKE256 128 s robust.
     */
    SHAKE128SR,

    /**
     * SHAKE256 192 f simple.
     */
    SHAKE192FS,

    /**
     * SHAKE256 192 s simple.
     */
    SHAKE192SS,

    /**
     * SHAKE256 192 f robust.
     */
    SHAKE192FR,

    /**
     * SHAKE256 192 s robust.
     */
    SHAKE192SR,

    /**
     * SHAKE256 256 f simple.
     */
    SHAKE256FS,

    /**
     * SHAKE256 256 s simple.
     */
    SHAKE256SS,

    /**
     * SHAKE256 256 f robust.
     */
    SHAKE256FR,

    /**
     * SHAKE256 256 s robust.
     */
    SHAKE256SR;

    /**
     * Obtain SPHINCSPlus Parameters.
     * @return the parameters.
     */
    public SPHINCSPlusParameters getParameters() {
        switch (this) {
            case SHA128FR:   return SPHINCSPlusParameters.sha256_128f;
            case SHA128SR:   return SPHINCSPlusParameters.sha256_128s;
            case SHA192FR:   return SPHINCSPlusParameters.sha256_192f;
            case SHA192SR:   return SPHINCSPlusParameters.sha256_192s;
            case SHA256FR:   return SPHINCSPlusParameters.sha256_256f;
            case SHA256SR:   return SPHINCSPlusParameters.sha256_256s;
            case SHA128FS:   return SPHINCSPlusParameters.sha256_128f_simple;
            case SHA128SS:   return SPHINCSPlusParameters.sha256_128s_simple;
            case SHA192FS:   return SPHINCSPlusParameters.sha256_192f_simple;
            case SHA192SS:   return SPHINCSPlusParameters.sha256_192s_simple;
            case SHA256FS:   return SPHINCSPlusParameters.sha256_256f_simple;
            case SHA256SS:   return SPHINCSPlusParameters.sha256_256s_simple;
            case SHAKE128FR: return SPHINCSPlusParameters.shake256_128f;
            case SHAKE128SR: return SPHINCSPlusParameters.shake256_128s;
            case SHAKE192FR: return SPHINCSPlusParameters.shake256_192f;
            case SHAKE192SR: return SPHINCSPlusParameters.shake256_192s;
            case SHAKE256FR: return SPHINCSPlusParameters.shake256_256f;
            case SHAKE256SR: return SPHINCSPlusParameters.shake256_256s;
            case SHAKE128FS: return SPHINCSPlusParameters.shake256_128f_simple;
            case SHAKE128SS: return SPHINCSPlusParameters.shake256_128s_simple;
            case SHAKE192FS: return SPHINCSPlusParameters.shake256_192f_simple;
            case SHAKE192SS: return SPHINCSPlusParameters.shake256_192s_simple;
            case SHAKE256FS: return SPHINCSPlusParameters.shake256_256f_simple;
            case SHAKE256SS: return SPHINCSPlusParameters.shake256_256s_simple;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain SPHINCSPlus ParameterSpec.
     * @return the parameters.
     */
    public SPHINCSPlusParameterSpec getParameterSpec() {
        switch (this) {
            case SHA128FR:   return SPHINCSPlusParameterSpec.sha256_128f;
            case SHA128SR:   return SPHINCSPlusParameterSpec.sha256_128s;
            case SHA192FR:   return SPHINCSPlusParameterSpec.sha256_192f;
            case SHA192SR:   return SPHINCSPlusParameterSpec.sha256_192s;
            case SHA256FR:   return SPHINCSPlusParameterSpec.sha256_256f;
            case SHA256SR:   return SPHINCSPlusParameterSpec.sha256_256s;
            case SHA128FS:   return SPHINCSPlusParameterSpec.sha256_128f_simple;
            case SHA128SS:   return SPHINCSPlusParameterSpec.sha256_128s_simple;
            case SHA192FS:   return SPHINCSPlusParameterSpec.sha256_192f_simple;
            case SHA192SS:   return SPHINCSPlusParameterSpec.sha256_192s_simple;
            case SHA256FS:   return SPHINCSPlusParameterSpec.sha256_256f_simple;
            case SHA256SS:   return SPHINCSPlusParameterSpec.sha256_256s_simple;
            case SHAKE128FR: return SPHINCSPlusParameterSpec.shake256_128f;
            case SHAKE128SR: return SPHINCSPlusParameterSpec.shake256_128s;
            case SHAKE192FR: return SPHINCSPlusParameterSpec.shake256_192f;
            case SHAKE192SR: return SPHINCSPlusParameterSpec.shake256_192s;
            case SHAKE256FR: return SPHINCSPlusParameterSpec.shake256_256f;
            case SHAKE256SR: return SPHINCSPlusParameterSpec.shake256_256s;
            case SHAKE128FS: return SPHINCSPlusParameterSpec.shake256_128f_simple;
            case SHAKE128SS: return SPHINCSPlusParameterSpec.shake256_128s_simple;
            case SHAKE192FS: return SPHINCSPlusParameterSpec.shake256_192f_simple;
            case SHAKE192SS: return SPHINCSPlusParameterSpec.shake256_192s_simple;
            case SHAKE256FS: return SPHINCSPlusParameterSpec.shake256_256f_simple;
            case SHAKE256SS: return SPHINCSPlusParameterSpec.shake256_256s_simple;
            default: throw new IllegalArgumentException();
        }
    }
}
