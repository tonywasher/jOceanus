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

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewSparkleKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeyType;

/**
 * StreamKey specification Builder.
 */

public class GordianCoreStreamKeySpecBuilder
        implements GordianNewStreamKeySpecBuilder {
    /**
     * The type.
     */
    private GordianNewStreamKeyType theType;

    /**
     * The subSpec.
     */
    private GordianNewStreamKeySubType theSubType;

    /**
     * The keyLength.
     */
    private GordianLength theKeyLength;

    @Override
    public GordianNewStreamKeySpecBuilder withType(final GordianNewStreamKeyType pType) {
        theType = pType;
        return this;
    }

    @Override
    public GordianNewStreamKeySpecBuilder withSubType(final GordianNewStreamKeySubType pSubType) {
        theSubType = pSubType;
        return this;
    }

    @Override
    public GordianNewStreamKeySpecBuilder withKeyLength(final GordianLength pKeyLength) {
        theKeyLength = pKeyLength;
        return this;
    }

    @Override
    public GordianNewStreamKeySpec build() {
        /* Handle defaults */
        theSubType = theSubType == null ? GordianCoreStreamKeySubType.defaultSubKeyType(theType) : theSubType;
        theKeyLength = theKeyLength == null ? defaultKeyLength() : theKeyLength;

        /* Create spec, reset and return */
        final GordianCoreStreamKeySpec mySpec = new GordianCoreStreamKeySpec(theType, theSubType, theKeyLength);
        reset();
        return mySpec;
    }

    /**
     * Reset state.
     */
    private void reset() {
        theType = null;
        theSubType = null;
        theKeyLength = null;
    }

    /**
     * Default length.
     *
     * @return the default
     */
    private GordianLength defaultKeyLength() {
        /* Switch on keyType */
        switch (theType) {
            case GRAIN:
            case HC:
            case RABBIT:
            case SNOW3G:
            case SALSA20:
            case CHACHA20:
            case ZUC:
            case SOSEMANUK:
            case RC4:
            case VMPC:
            case ASCON:
            case ELEPHANT:
            case ISAP:
            case PHOTONBEETLE:
            case ROMULUS:
            case XOODYAK:
                return GordianLength.LEN_128;
            case ISAAC:
            case SKEINXOF:
            case BLAKE2XOF:
            case BLAKE3XOF:
                return GordianLength.LEN_256;
            case SPARKLE:
                return GordianCoreStreamKeySubType.requiredSparkleKeyLength((GordianNewSparkleKey) theSubType);
            default:
                throw new IllegalArgumentException();
        }
    }
}
