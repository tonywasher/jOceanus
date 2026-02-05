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

package io.github.tonywasher.joceanus.gordianknot.impl.core.cipher.spec;

import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewCipherMode;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPadding;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpec;

public class GordianCoreSymCipherSpecBuilder
        implements GordianNewSymCipherSpecBuilder {
    /**
     * The keySpec.
     */
    private GordianCoreSymKeySpec theKeySpec;

    /**
     * The mode.
     */
    private GordianNewCipherMode theMode;

    /**
     * The padding.
     */
    private GordianNewPadding thePadding;

    @Override
    public GordianNewSymCipherSpecBuilder withKeySpec(final GordianNewSymKeySpec pKeySpec) {
        theKeySpec = (GordianCoreSymKeySpec) pKeySpec;
        return null;
    }

    @Override
    public GordianNewSymCipherSpecBuilder withMode(final GordianNewCipherMode pMode) {
        theMode = pMode;
        return this;
    }

    @Override
    public GordianNewSymCipherSpecBuilder withPadding(final GordianNewPadding pPadding) {
        thePadding = pPadding;
        return this;
    }

    @Override
    public GordianNewSymCipherSpec build() {
        thePadding = thePadding == null ? GordianNewPadding.NONE : thePadding;
        final GordianNewSymCipherSpec mySpec = new GordianCoreSymCipherSpec(theKeySpec, theMode, thePadding);
        reset();
        return mySpec;
    }

    /**
     * Reset state.
     */
    private void reset() {
        theKeySpec = null;
        theMode = null;
        thePadding = null;
    }
}
