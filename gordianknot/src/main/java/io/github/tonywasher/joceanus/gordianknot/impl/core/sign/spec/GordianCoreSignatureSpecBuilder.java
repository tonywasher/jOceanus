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

package io.github.tonywasher.joceanus.gordianknot.impl.core.sign.spec;

import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianNewSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianNewSignatureSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianNewSignatureType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.digest.spec.GordianCoreDigestSpecBuilder;

import java.util.List;

/**
 * Signature Specification Builder.
 */
public class GordianCoreSignatureSpecBuilder
        implements GordianNewSignatureSpecBuilder {
    /**
     * The digestSpec builder.
     */
    private final GordianNewDigestSpecBuilder theBuilder;

    /**
     * The keyPairType.
     */
    private GordianKeyPairType theKeyPairType;

    /**
     * The type.
     */
    private GordianNewSignatureType theSignatureType;

    /**
     * The subSpec.
     */
    private Object theSubSpec;

    /**
     * Constructor.
     */
    public GordianCoreSignatureSpecBuilder() {
        theBuilder = new GordianCoreDigestSpecBuilder();
    }

    @Override
    public GordianNewSignatureSpecBuilder withKeyPairType(final GordianKeyPairType pType) {
        theKeyPairType = pType;
        return this;
    }

    @Override
    public GordianNewSignatureSpecBuilder withSignatureType(final GordianNewSignatureType pType) {
        theSignatureType = pType;
        return this;
    }

    @Override
    public GordianNewSignatureSpecBuilder withDigestSpec(final GordianNewDigestSpec pDigest) {
        theSubSpec = pDigest;
        return this;
    }

    @Override
    public GordianNewSignatureSpecBuilder withSignatureSpecs(final List<GordianNewSignatureSpec> pSpecs) {
        theSubSpec = pSpecs;
        return this;
    }

    @Override
    public GordianNewDigestSpecBuilder usingDigestSpecBuilder() {
        return theBuilder;
    }

    @Override
    public GordianNewSignatureSpec build() {
        /* Handle defaults */
        theSignatureType = theSignatureType == null ? GordianNewSignatureType.NATIVE : theSignatureType;

        /* Create spec, reset and return */
        final GordianCoreSignatureSpec mySpec = new GordianCoreSignatureSpec(theKeyPairType, theSignatureType, theSubSpec);
        reset();
        return mySpec;
    }

    /**
     * Reset state.
     */
    private void reset() {
        theKeyPairType = null;
        theSignatureType = null;
        theSubSpec = null;
    }
}
