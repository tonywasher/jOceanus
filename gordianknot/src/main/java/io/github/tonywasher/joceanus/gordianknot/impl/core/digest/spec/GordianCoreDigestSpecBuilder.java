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

package io.github.tonywasher.joceanus.gordianknot.impl.core.digest.spec;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSubSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSubSpec.GordianNewDigestState;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestType;

/**
 * Digest Specification Builder.
 */
public class GordianCoreDigestSpecBuilder
        implements GordianNewDigestSpecBuilder {
    /**
     * The type.
     */
    private GordianNewDigestType theType;

    /**
     * The subSpec.
     */
    private GordianNewDigestSubSpec theSubSpec;

    /**
     * The length.
     */
    private GordianLength theLength;

    /**
     * The type.
     */
    private boolean asXof;

    @Override
    public GordianCoreDigestSpecBuilder withType(final GordianNewDigestType pType) {
        theType = pType;
        return this;
    }

    @Override
    public GordianCoreDigestSpecBuilder withState(final GordianNewDigestState pState) {
        theSubSpec = pState;
        return this;
    }

    @Override
    public GordianCoreDigestSpecBuilder withLength(final GordianLength pLength) {
        theLength = pLength;
        return this;
    }

    @Override
    public GordianCoreDigestSpecBuilder asXof() {
        asXof = true;
        return this;
    }

    @Override
    public GordianCoreDigestSpec build() {
        /* Handle defaults */
        theLength = theLength == null ? GordianCoreDigestType.getDefaultLength(theType) : theLength;
        theSubSpec = theSubSpec == null ? GordianCoreDigestSubSpec.getDefaultSubSpecForTypeAndLength(theType, theLength) : theSubSpec;

        /* Create subSpec, reset and return */
        final GordianCoreDigestSpec mySpec = new GordianCoreDigestSpec(theType, theSubSpec, theLength, asXof);
        reset();
        return mySpec;
    }

    /**
     * Reset state.
     */
    private void reset() {
        theType = null;
        theSubSpec = null;
        theLength = null;
        asXof = false;
    }
}
