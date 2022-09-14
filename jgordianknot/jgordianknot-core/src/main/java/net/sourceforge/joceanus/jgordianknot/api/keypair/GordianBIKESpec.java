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

import org.bouncycastle.pqc.crypto.bike.BIKEParameters;
import org.bouncycastle.pqc.jcajce.spec.BIKEParameterSpec;

/**
 * BIKE KeySpec.
 */
public enum GordianBIKESpec {
    /**
     * 128.
     */
    BIKE128,

    /**
     * 192.
     */
    BIKE192,

    /**
     * Bike 256.
     */
    BIKE256;

    /**
     * Obtain BIKE Parameters.
     * @return the parameters.
     */
    public BIKEParameters getParameters() {
        switch (this) {
            case BIKE128: return BIKEParameters.bike128;
            case BIKE192: return BIKEParameters.bike192;
            case BIKE256: return BIKEParameters.bike256;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain BIKE ParameterSpec.
     * @return the parameters.
     */
    public BIKEParameterSpec getParameterSpec() {
        switch (this) {
            case BIKE128: return BIKEParameterSpec.bike128;
            case BIKE192: return BIKEParameterSpec.bike192;
            case BIKE256: return BIKEParameterSpec.bike256;
            default: throw new IllegalArgumentException();
        }
    }
}
