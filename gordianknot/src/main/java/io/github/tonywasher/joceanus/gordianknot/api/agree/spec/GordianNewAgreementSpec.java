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

package io.github.tonywasher.joceanus.gordianknot.api.agree.spec;

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewKeyPairSpec;

/**
 * KeyPair Agreement Specification.
 */
public interface GordianNewAgreementSpec {
    /**
     * Obtain the keyPairSpec.
     *
     * @return the keyPairSpec
     */
    GordianNewKeyPairSpec getKeyPairSpec();

    /**
     * Obtain the agreementType.
     *
     * @return the agreementType
     */
    GordianNewAgreementType getAgreementType();

    /**
     * Obtain the kdfType.
     *
     * @return the kdfType
     */
    GordianNewAgreementKDF getKDFType();

    /**
     * Is this agreement with key confirmation?
     *
     * @return true/false
     */
    boolean withConfirm();

    /**
     * Is this Agreement supported?
     *
     * @return true/false
     */
    boolean isSupported();

    /**
     * Is the agreementSpec valid?
     *
     * @return true/false.
     */
    boolean isValid();
}
