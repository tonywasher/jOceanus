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

import org.bouncycastle.pqc.crypto.hqc.HQCParameters;
import org.bouncycastle.pqc.jcajce.spec.HQCParameterSpec;

/**
 * HQC KeySpec.
 */
public enum GordianHQCSpec {
    /**
     * HQC 128.
     */
    HQC128,

    /**
     * HQC 192.
     */
    HQC192,

    /**
     * HQC 256.
     */
    HQC256;

    /**
     * Obtain HQC Parameters.
     * @return the parameters.
     */
    public HQCParameters getParameters() {
        switch (this) {
            case HQC128: return HQCParameters.hqc128;
            case HQC192: return HQCParameters.hqc192;
            case HQC256: return HQCParameters.hqc256;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain HQC ParameterSpec.
     * @return the parameters.
     */
    public HQCParameterSpec getParameterSpec() {
        switch (this) {
            case HQC128: return HQCParameterSpec.hqc128;
            case HQC192: return HQCParameterSpec.hqc192;
            case HQC256: return HQCParameterSpec.hqc256;
            default: throw new IllegalArgumentException();
        }
    }
}
