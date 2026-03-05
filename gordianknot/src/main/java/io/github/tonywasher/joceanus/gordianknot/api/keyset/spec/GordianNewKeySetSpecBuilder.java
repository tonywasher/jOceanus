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

package io.github.tonywasher.joceanus.gordianknot.api.keyset.spec;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;

/**
 * KeySet Spec Builder.
 */
public interface GordianNewKeySetSpecBuilder {
    /**
     * Create keySetSpec.
     *
     * @return the keySetSpec
     */
    GordianNewKeySetSpec keySet();

    /**
     * Create keySetSpec.
     *
     * @param pKeyLen the keyLength.
     * @return the keySetSpec
     */
    GordianNewKeySetSpec keySet(GordianLength pKeyLen);

    /**
     * Constructor.
     *
     * @param pKeyLen   the keyLength.
     * @param pNumSteps the number of cipherSteps
     * @return the keySetSpec
     */
    GordianNewKeySetSpec keySet(GordianLength pKeyLen,
                                int pNumSteps);
}
