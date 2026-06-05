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

package io.github.tonywasher.joceanus.gordianknot.impl.core.sign;

import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignParams;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignParamsBuilder;

/**
 * Core Signature parameters builder.
 */
public class GordianCoreSignParamsBuilder
        implements GordianSignParamsBuilder {
    /**
     * The keyPair.
     */
    private GordianKeyPair theKeyPair;

    /**
     * The context.
     */
    private byte[] theContext;

    /**
     * Constructor.
     */
    private GordianCoreSignParamsBuilder() {
    }

    /**
     * Create new SignParamsBuilder.
     *
     * @return the Builder
     */
    public static GordianSignParamsBuilder newInstance() {
        return new GordianCoreSignParamsBuilder();
    }

    @Override
    public GordianSignParamsBuilder withKeyPair(final GordianKeyPair pPair) {
        theKeyPair = pPair;
        return this;
    }

    @Override
    public GordianSignParamsBuilder withContext(final byte[] pContext) {
        theContext = pContext == null ? null : pContext.clone();
        return this;
    }

    @Override
    public GordianSignParams build() {
        /* Create params, reset and return */
        final GordianCoreSignParams myParams = new GordianCoreSignParams(theKeyPair, theContext);
        reset();
        return myParams;
    }

    /**
     * Reset state.
     */
    private void reset() {
        theKeyPair = null;
        theContext = null;
    }
}
