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

import org.bouncycastle.jcajce.spec.MLDSAParameterSpec;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters;

/**
 * MLDSA KeySpec.
 */
public enum GordianMLDSASpec {
    /**
     * mldsa44.
     */
    MLDSA44,

    /**
     * mldsa65.
     */
    MLDSA65,

    /**
     * mldsa87.
     */
    MLDSA87,

    /**
     * mldsa44sha2.
     */
    MLDSA44SHA,

    /**
     * mldsa65sha2.
     */
    MLDSA65SHA,

    /**
     * mldsa87sha2.
     */
    MLDSA87SHA;

    /**
     * Obtain MLDSA Parameters.
     * @return the parameters.
     */
    public MLDSAParameters getParameters() {
        switch (this) {
            case MLDSA44:    return MLDSAParameters.ml_dsa_44;
            case MLDSA65:    return MLDSAParameters.ml_dsa_65;
            case MLDSA87:    return MLDSAParameters.ml_dsa_87;
            case MLDSA44SHA: return MLDSAParameters.ml_dsa_44_with_sha512;
            case MLDSA65SHA: return MLDSAParameters.ml_dsa_65_with_sha512;
            case MLDSA87SHA: return MLDSAParameters.ml_dsa_87_with_sha512;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain MLDSA ParameterSpec.
     * @return the parameters.
     */
    public MLDSAParameterSpec getParameterSpec() {
        switch (this) {
            case MLDSA44:    return MLDSAParameterSpec.ml_dsa_44;
            case MLDSA65:    return MLDSAParameterSpec.ml_dsa_65;
            case MLDSA87:    return MLDSAParameterSpec.ml_dsa_87;
            case MLDSA44SHA: return MLDSAParameterSpec.ml_dsa_44_with_sha512;
            case MLDSA65SHA: return MLDSAParameterSpec.ml_dsa_65_with_sha512;
            case MLDSA87SHA: return MLDSAParameterSpec.ml_dsa_87_with_sha512;
            default: throw new IllegalArgumentException();
        }
    }
}
