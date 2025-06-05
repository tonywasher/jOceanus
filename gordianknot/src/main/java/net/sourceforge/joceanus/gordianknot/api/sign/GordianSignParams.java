/* *****************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.gordianknot.api.sign;

import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import org.bouncycastle.util.Arrays;

/**
 * Signature parameters.
 */
public final class GordianSignParams {
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
     * @param pKeyPair the keyPair
     * @param pContext the Context
     */
    private GordianSignParams(final GordianKeyPair pKeyPair,
                              final byte[] pContext) {
        theKeyPair = pKeyPair;
        theContext = pContext != null ? Arrays.clone(pContext) : null;
    }

    /**
     * Create keyPair parameters.
     * @param pKeyPair the keyPair
     * @return the new params
     */
    public static GordianSignParams keyPair(final GordianKeyPair pKeyPair) {
        return new GordianSignParams(pKeyPair, null);
    }

    /**
     * Create keyPair and context parameters.
     * @param pKeyPair the keyPair
     * @param pContext the context
     * @return the new params
     */
    public static GordianSignParams keyPair(final GordianKeyPair pKeyPair,
                                            final byte[] pContext) {
        return new GordianSignParams(pKeyPair, pContext);
    }

    /**
     * Obtain the keyPair.
     * @return the keyPair
     */
    public GordianKeyPair getKeyPair() {
        return theKeyPair;
    }

    /**
     * Obtain the context.
     * @return the context
     */
    public byte[] getContext() {
        return theContext == null ? null : Arrays.clone(theContext);
    }
}
