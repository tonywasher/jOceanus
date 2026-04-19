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
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianNewSignParams;
import org.bouncycastle.util.Arrays;

/**
 * Core Signature parameters.
 */
public class GordianCoreSignParams
        implements GordianNewSignParams {
    /**
     * KeyPair.
     */
    private final GordianKeyPair theKeyPair;

    /**
     * Context.
     */
    private final byte[] theContext;

    /**
     * Constructor.
     *
     * @param pKeyPair the keyPair
     * @param pContext the Context
     */
    GordianCoreSignParams(final GordianKeyPair pKeyPair,
                          final byte[] pContext) {
        theKeyPair = pKeyPair;
        theContext = pContext != null ? Arrays.clone(pContext) : null;
    }

    @Override
    public GordianKeyPair getKeyPair() {
        return theKeyPair;
    }

    @Override
    public byte[] getContext() {
        return theContext == null ? null : Arrays.clone(theContext);
    }
}
