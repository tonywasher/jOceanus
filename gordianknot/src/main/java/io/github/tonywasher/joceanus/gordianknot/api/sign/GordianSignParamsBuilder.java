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

package io.github.tonywasher.joceanus.gordianknot.api.sign;

import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;

/**
 * Signature parameters.
 */
public interface GordianSignParamsBuilder {
    /**
     * use keyPair.
     *
     * @param pKeyPair the keyPair
     * @return the builder
     */
    GordianSignParamsBuilder withKeyPair(GordianKeyPair pKeyPair);

    /**
     * use context.
     *
     * @param pContext the context
     * @return the Builder
     */
    GordianSignParamsBuilder withContext(byte[] pContext);

    /**
     * Build signatureParams.
     *
     * @return the signParams
     */
    GordianSignParams build();

    /**
     * Create keyPair parameters.
     *
     * @param pKeyPair the keyPair
     * @return the new params
     */
    default GordianSignParams keyPair(final GordianKeyPair pKeyPair) {
        return withKeyPair(pKeyPair).build();
    }

    /**
     * Create keyPair and Context parameters.
     *
     * @param pKeyPair the keyPair
     * @param pContext the context
     * @return the new params
     */
    default GordianSignParams keyPairAndContext(final GordianKeyPair pKeyPair,
                                                final byte[] pContext) {
        return withKeyPair(pKeyPair).withContext(pContext).build();
    }
}
