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

import org.bouncycastle.pqc.crypto.frodo.FrodoParameters;
import org.bouncycastle.pqc.jcajce.spec.FrodoParameterSpec;

/**
 * FRODO KeySpecs.
 */
public enum GordianFRODOSpec {
    /**
     * AES 19888.
     */
    AES19888,

    /**
     * SHAKE 19888.
     */
    SHAKE19888,

    /**
     * AES 31296.
     */
    AES31296,

    /**
     * SHAKE 31296.
     */
    SHAKE31296,

    /**
     * AES 42088.
     */
    AES43088,

    /**
     * SHAKE 43088.
     */
    SHAKE43088;

    /**
     * Obtain Frodo Parameters.
     * @return the parameters.
     */
    public FrodoParameters getParameters() {
        switch (this) {
            case AES19888:   return FrodoParameters.frodokem19888r3;
            case SHAKE19888: return FrodoParameters.frodokem19888shaker3;
            case AES31296:   return FrodoParameters.frodokem31296r3;
            case SHAKE31296: return FrodoParameters.frodokem31296shaker3;
            case AES43088:   return FrodoParameters.frodokem43088r3;
            case SHAKE43088: return FrodoParameters.frodokem43088shaker3;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain Frodo ParameterSpec.
     * @return the parameters.
     */
    public FrodoParameterSpec getParameterSpec() {
        switch (this) {
            case AES19888:   return FrodoParameterSpec.frodokem19888r3;
            case SHAKE19888: return FrodoParameterSpec.frodokem19888shaker3;
            case AES31296:   return FrodoParameterSpec.frodokem31296r3;
            case SHAKE31296: return FrodoParameterSpec.frodokem31296shaker3;
            case AES43088:   return FrodoParameterSpec.frodokem43088r3;
            case SHAKE43088: return FrodoParameterSpec.frodokem43088shaker3;
            default: throw new IllegalArgumentException();
        }
    }
}
