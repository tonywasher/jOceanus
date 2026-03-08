/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec;

import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpec;

/**
 * SM2 EncryptionSpec.
 */
public interface GordianSM2EncryptionSpec {
    /**
     * Obtain the encryptionType.
     *
     * @return the encryptionType
     */
    GordianSM2EncryptionType getEncryptionType();

    /**
     * Obtain the digestSpec.
     *
     * @return the digestSpec
     */
    GordianDigestSpec getDigestSpec();

    /**
     * Is the keySpec valid?
     *
     * @return true/false.
     */
    boolean isValid();
}
