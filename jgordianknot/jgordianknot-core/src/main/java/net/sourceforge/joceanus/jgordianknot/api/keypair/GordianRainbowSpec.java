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

import org.bouncycastle.pqc.crypto.rainbow.RainbowParameters;
import org.bouncycastle.pqc.jcajce.spec.RainbowParameterSpec;

/**
 * Rainbow KeySpec.
 */
public enum GordianRainbowSpec {
    /**
     * Classic 3.
     */
    CLASSIC3,

    /**
     * Circumzenithal 3.
     */
    CIRCUM3,

    /**
     * Compressed 3.
     */
    COMPRESSED3,

    /**
     * Classic 5.
     */
    CLASSIC5,

    /**
     * Circumzenithal 5.
     */
    CIRCUM5,

    /**
     * Compressed 5.
     */
    COMPRESSED5;

    /**
     * Obtain Rainbow Parameters.
     * @return the parameters.
     */
    public RainbowParameters getParameters() {
        switch (this) {
            case CLASSIC3:    return RainbowParameters.rainbowIIIclassic;
            case CIRCUM3:     return RainbowParameters.rainbowIIIcircumzenithal;
            case COMPRESSED3: return RainbowParameters.rainbowIIIcompressed;
            case CLASSIC5:    return RainbowParameters.rainbowVclassic;
            case CIRCUM5:     return RainbowParameters.rainbowVcircumzenithal;
            case COMPRESSED5: return RainbowParameters.rainbowVcompressed;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain Rainbow ParameterSpec.
     * @return the parameters.
     */
    public RainbowParameterSpec getParameterSpec() {
        switch (this) {
            case CLASSIC3:    return RainbowParameterSpec.rainbowIIIclassic;
            case CIRCUM3:     return RainbowParameterSpec.rainbowIIIcircumzenithal;
            case COMPRESSED3: return RainbowParameterSpec.rainbowIIIcompressed;
            case CLASSIC5:    return RainbowParameterSpec.rainbowVclassic;
            case CIRCUM5:     return RainbowParameterSpec.rainbowVcircumzenithal;
            case COMPRESSED5: return RainbowParameterSpec.rainbowVcompressed;
            default: throw new IllegalArgumentException();
        }
    }
}
