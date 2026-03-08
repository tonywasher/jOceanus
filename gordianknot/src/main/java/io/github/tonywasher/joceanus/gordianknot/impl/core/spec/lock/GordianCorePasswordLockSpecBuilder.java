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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.lock;

import io.github.tonywasher.joceanus.gordianknot.api.keyset.spec.GordianKeySetSpec;
import io.github.tonywasher.joceanus.gordianknot.api.lock.spec.GordianPasswordLockSpec;
import io.github.tonywasher.joceanus.gordianknot.api.lock.spec.GordianPasswordLockSpecBuilder;

/**
 * PasswordLock Spec Builder.
 */
public final class GordianCorePasswordLockSpecBuilder
        implements GordianPasswordLockSpecBuilder {
    /**
     * Private Constructor.
     */
    private GordianCorePasswordLockSpecBuilder() {
    }

    /**
     * Obtain new instance.
     *
     * @return the new instance
     */
    public static GordianCorePasswordLockSpecBuilder newInstance() {
        return new GordianCorePasswordLockSpecBuilder();
    }

    @Override
    public GordianPasswordLockSpec passwordLock() {
        return new GordianCorePasswordLockSpec();
    }

    @Override
    public GordianPasswordLockSpec passwordLock(final int pKIterations) {
        return new GordianCorePasswordLockSpec(pKIterations);
    }

    @Override
    public GordianPasswordLockSpec passwordLock(final GordianKeySetSpec pKeySetSpec) {
        return new GordianCorePasswordLockSpec(pKeySetSpec);
    }

    @Override
    public GordianPasswordLockSpec passwordLock(final int pKIterations,
                                                final GordianKeySetSpec pKeySetSpec) {
        return new GordianCorePasswordLockSpec(pKIterations, pKeySetSpec);
    }
}
