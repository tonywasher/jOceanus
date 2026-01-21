/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.gordianknot.api.cipher;

/**
 * The StreamCipherSpec Builder class.
 */
public final class GordianStreamCipherSpecBuilder {
    /**
     * Private constructor.
     */
    private GordianStreamCipherSpecBuilder() {
    }

    /**
     * Create a streamCipherSpec.
     *
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianStreamCipherSpec stream(final GordianStreamKeySpec pKeySpec) {
        return new GordianStreamCipherSpec(pKeySpec);
    }

    /**
     * Create a streamCipherSpec.
     *
     * @param pKeySpec the keySpec
     * @param pAAD     is this an AAD cipher?
     * @return the cipherSpec
     */
    public static GordianStreamCipherSpec stream(final GordianStreamKeySpec pKeySpec,
                                                 final boolean pAAD) {
        return new GordianStreamCipherSpec(pKeySpec, pAAD);
    }
}
