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

package io.github.tonywasher.joceanus.gordianknot.api.lock.spec;

import io.github.tonywasher.joceanus.gordianknot.api.keyset.spec.GordianKeySetSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keyset.spec.GordianKeySetSpecBuilder;

/**
 * PasswordLock Spec Builder.
 */
public interface GordianPasswordLockSpecBuilder {
    /**
     * create passwordLockSpec.
     *
     * @return the passwordLockSpec
     */
    GordianPasswordLockSpec passwordLock();

    /**
     * create passwordLockSpec.
     *
     * @param pKIterations the iterations (x 1K).
     * @return the passwordLockSpec
     */
    GordianPasswordLockSpec passwordLock(int pKIterations);

    /**
     * create passwordLockSpec.
     *
     * @param pKeySetSpec the keySetSpec.
     * @return the passwordLockSpec
     */
    GordianPasswordLockSpec passwordLock(GordianKeySetSpec pKeySetSpec);

    /**
     * create passwordLockSpec.
     *
     * @param pKIterations the iterations (x 1K).
     * @param pKeySetSpec  the keySetSpec
     * @return the passwordLockSpec
     */
    GordianPasswordLockSpec passwordLock(int pKIterations,
                                         GordianKeySetSpec pKeySetSpec);

    /**
     * Access keySetSpecBuilder.
     *
     * @return the keySetSpec builder
     */
    GordianKeySetSpecBuilder usingKeySetSpecBuilder();
}
