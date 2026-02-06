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

package io.github.tonywasher.joceanus.gordianknot.impl.core.mac.spec;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewMacSpec;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewMacSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewMacType;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewSipHashType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.digest.spec.GordianCoreDigestSpecBuilder;

/**
 * Mac Specification Builder.
 */
public class GordianCoreMacSpecBuilder
        implements GordianNewMacSpecBuilder {
    /**
     * The digestSpec builder.
     */
    private final GordianNewDigestSpecBuilder theBuilder;

    /**
     * The type.
     */
    private GordianNewMacType theType;

    /**
     * The subSpec.
     */
    private GordianLength theKeyLength;

    /**
     * The subSpec.
     */
    private Object theSubSpec;

    /**
     * Constructor.
     */
    public GordianCoreMacSpecBuilder() {
        theBuilder = new GordianCoreDigestSpecBuilder();
    }

    @Override
    public GordianNewMacSpecBuilder withType(final GordianNewMacType pType) {
        theType = pType;
        return this;
    }

    @Override
    public GordianNewMacSpecBuilder withKeyLength(final GordianLength pKeyLength) {
        theKeyLength = pKeyLength;
        return this;
    }

    @Override
    public GordianNewMacSpecBuilder withDigestSubSpec(final GordianNewDigestSpec pDigest) {
        theSubSpec = pDigest;
        return this;
    }

    @Override
    public GordianNewMacSpecBuilder withSymKeySubSpec(final GordianNewSymKeySpec pSymKey) {
        theSubSpec = pSymKey;
        return this;
    }

    @Override
    public GordianNewMacSpecBuilder withSipHashSubSpec(final GordianNewSipHashType pSipHash) {
        theSubSpec = pSipHash;
        return this;
    }

    @Override
    public GordianNewMacSpecBuilder withLengthSubSpec(final GordianLength pLength) {
        theSubSpec = pLength;
        return this;
    }

    @Override
    public GordianNewDigestSpecBuilder usingDigestSpecBuilder() {
        return theBuilder;
    }

    @Override
    public GordianNewMacSpec build() {
        /* Handle defaults */
        theKeyLength = determineKeyLength(theKeyLength);

        /* Create spec, reset and return */
        final GordianCoreMacSpec mySpec = new GordianCoreMacSpec(theType, theKeyLength, theSubSpec);
        reset();
        return mySpec;
    }

    /**
     * Reset state.
     */
    private void reset() {
        theType = null;
        theSubSpec = null;
        theKeyLength = null;
    }

    /**
     * Determine keyLength.
     *
     * @param pKeyLength the proposed keyLength
     * @return the keyLength
     */
    private GordianLength determineKeyLength(final GordianLength pKeyLength) {
        /* Honour proposed keyLength */
        if (pKeyLength != null) {
            return pKeyLength;
        }

        /* Handle sipHashType */
        if (theSubSpec instanceof GordianNewSipHashType) {
            return GordianLength.LEN_128;

            /* Handle symKeySpec */
        } else if (theSubSpec instanceof GordianNewDigestSpec myDigest) {
            return myDigest.getDigestLength();

            /* Handle symKeySpec */
        } else if (theSubSpec instanceof GordianNewSymKeySpec mySym) {
            return GordianNewMacType.POLY1305 == theType
                    ? GordianLength.LEN_256
                    : mySym.getKeyLength();
        }

        /* Default to supplied length */
        return pKeyLength;
    }
}
