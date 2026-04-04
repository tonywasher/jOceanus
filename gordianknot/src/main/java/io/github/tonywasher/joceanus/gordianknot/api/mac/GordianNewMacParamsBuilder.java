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

package io.github.tonywasher.joceanus.gordianknot.api.mac;

import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKey;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianMacSpec;

/**
 * MacParams Builder.
 */
public interface GordianNewMacParamsBuilder {
    /**
     * Set the key.
     *
     * @param pKey the key
     * @return the Builder
     */
    GordianNewMacParamsBuilder setKey(GordianKey<GordianMacSpec> pKey);

    /**
     * Set the nonce.
     *
     * @param pNonce the nonce
     * @return the Builder
     */
    GordianNewMacParamsBuilder setNonce(byte[] pNonce);

    /**
     * Use random nonce.
     *
     * @return the Builder
     */
    GordianNewMacParamsBuilder withRandomNonce();

    /**
     * Set the personalisation.
     *
     * @param pPersonal the personalisation
     * @return the Builder
     */
    GordianNewMacParamsBuilder setPersonalisation(byte[] pPersonal);

    /**
     * Set the output length.
     *
     * @param pOutLen the outputLen
     * @return the Builder
     */
    GordianNewMacParamsBuilder setOutputLength(long pOutLen);

    /**
     * Set the treeConfig.
     *
     * @param pFanOut   the fanout.
     * @param pMaxDepth the maxDepth.
     * @param pLeafLen  the leafLength.
     * @return the Builder
     */
    GordianNewMacParamsBuilder setTreeConfig(int pFanOut,
                                             int pMaxDepth,
                                             int pLeafLen);

    /**
     * Build the parameters.
     *
     * @return the parameters
     */
    GordianNewMacParams build();

    /**
     * Generate keyOnly Parameters.
     *
     * @param pKey the key
     * @return the macParameters
     */
    GordianNewMacParams key(GordianKey<GordianMacSpec> pKey);

    /**
     * Obtain keyAndNonce Parameters.
     *
     * @param pKey   the key
     * @param pNonce the nonce
     * @return the macParameters
     */
    GordianNewMacParams keyAndNonce(GordianKey<GordianMacSpec> pKey,
                                    byte[] pNonce);

    /**
     * Obtain keyAndRandomNonce Parameters.
     *
     * @param pKey the key
     * @return the macParameters
     */
    GordianNewMacParams keyWithRandomNonce(GordianKey<GordianMacSpec> pKey);
}
