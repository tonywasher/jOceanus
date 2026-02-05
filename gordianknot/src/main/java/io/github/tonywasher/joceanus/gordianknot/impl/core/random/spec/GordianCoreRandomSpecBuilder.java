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

package io.github.tonywasher.joceanus.gordianknot.impl.core.random.spec;

import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.random.spec.GordianNewRandomSpec;
import io.github.tonywasher.joceanus.gordianknot.api.random.spec.GordianNewRandomSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.random.spec.GordianNewRandomType;

/**
 * SecureRandom Specification Builder.
 */
public class GordianCoreRandomSpecBuilder
        implements GordianNewRandomSpecBuilder {

    /**
     * The type.
     */
    private GordianNewRandomType theType;

    /**
     * The subSpec.
     */
    private Object theSubSpec;

    /**
     * The subSpec.
     */
    private boolean withResistance;

    @Override
    public GordianNewRandomSpecBuilder withType(final GordianNewRandomType pType) {
        theType = pType;
        return this;
    }

    @Override
    public GordianNewRandomSpecBuilder withDigestSubSpec(final GordianNewDigestSpec pDigest) {
        theSubSpec = pDigest;
        return this;
    }

    @Override
    public GordianNewRandomSpecBuilder withSymKeySubSpec(final GordianNewSymKeySpec pSymKey) {
        theSubSpec = pSymKey;
        return this;
    }

    @Override
    public GordianNewRandomSpecBuilder withResistance() {
        withResistance = true;
        return this;
    }

    @Override
    public GordianNewRandomSpec build() {
        /* Create spec, reset and return */
        final GordianCoreRandomSpec mySpec = new GordianCoreRandomSpec(theType, theSubSpec, withResistance);
        reset();
        return mySpec;
    }

    /**
     * Reset state.
     */
    private void reset() {
        theType = null;
        theSubSpec = null;
        withResistance = false;
    }
}
