/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2023 Tony Washer
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

import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumParameters;
import org.bouncycastle.pqc.jcajce.spec.DilithiumParameterSpec;

/**
 * Dilithium KeySpec.
 */
public enum GordianDILITHIUMSpec {
    /**
     * Dilithium2.
     */
    DILITHIUM2,

    /**
     * Dilithium3.
     */
    DILITHIUM3,

    /**
     * Dilithium5.
     */
    DILITHIUM5;

    /**
     * Obtain Dilithium Parameters.
     * @return the parameters.
     */
    public DilithiumParameters getParameters() {
        switch (this) {
            case DILITHIUM2:    return DilithiumParameters.dilithium2;
            case DILITHIUM3:    return DilithiumParameters.dilithium3;
            case DILITHIUM5:    return DilithiumParameters.dilithium5;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain Dilithium ParameterSpec.
     * @return the parameters.
     */
    public DilithiumParameterSpec getParameterSpec() {
        switch (this) {
            case DILITHIUM2:    return DilithiumParameterSpec.dilithium2;
            case DILITHIUM3:    return DilithiumParameterSpec.dilithium3;
            case DILITHIUM5:    return DilithiumParameterSpec.dilithium5;
            default: throw new IllegalArgumentException();
        }
    }
}
