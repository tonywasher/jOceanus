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
import org.bouncycastle.util.Arrays;

/**
 * Core Signature parameters.
 */
public class GordianCoreSignParams
        implements GordianSignParams {
    /**
     * KeyPair.
     */
    private final GordianKeyPair theKeyPair;

    /**
     * Context.
     */
    private final byte[] theContext;

    /**
     * Identity.
     */
    private final byte[] theIdentity;

    /**
     * Constructor.
     *
     * @param pKeyPair  the keyPair
     * @param pContext  the Context
     * @param pIdentity the Identity
     */
    GordianCoreSignParams(final GordianKeyPair pKeyPair,
                          final byte[] pContext,
                          final byte[] pIdentity) {
        theKeyPair = pKeyPair;
        theContext = Arrays.clone(pContext);
        theIdentity = Arrays.clone(pIdentity);
    }

    @Override
    public GordianKeyPair getKeyPair() {
        return theKeyPair;
    }

    @Override
    public byte[] getContext() {
        return Arrays.clone(theContext);
    }

    @Override
    public byte[] getIdentity() {
        return Arrays.clone(theIdentity);
    }
}
