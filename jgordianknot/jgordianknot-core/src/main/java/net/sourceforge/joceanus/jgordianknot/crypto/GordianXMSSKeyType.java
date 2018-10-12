/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto;

/**
 * XMSS keyType.
 */
public enum GordianXMSSKeyType {
    /**
     * SHA256.
     */
    SHA256,

    /**
     * SHA512.
     */
    SHA512,

    /**
     * SHAKE128.
     */
    SHAKE128,

    /**
     * SHAKE256.
     */
    SHAKE256;

    /**
     * Default height for XMSS key.
     */
    public static final int DEFAULT_HEIGHT = 6;

    /**
     * Default layers for XMSS key.
     */
    public static final int DEFAULT_LAYERS = 3;

    /**
     * Obtain the required digestSpec.
     * @return the digestSpec
     */
    public GordianDigestSpec getDigestSpec() {
        switch (this) {
            case SHA256:
                return GordianDigestSpec.sha2(GordianLength.LEN_256);
            case SHA512:
                return GordianDigestSpec.sha2(GordianLength.LEN_512);
            case SHAKE128:
                return GordianDigestSpec.shake(GordianLength.LEN_128);
            case SHAKE256:
                return GordianDigestSpec.shake(GordianLength.LEN_256);
            default:
                throw new IllegalStateException();
        }
    }
}
