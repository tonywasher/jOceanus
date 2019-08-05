/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.mac;

import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherParameters.GordianNonceParameters;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;

/**
 * Mac Parameters.
 */
public interface GordianMacParameters {
    /**
     * Obtain keySpec Parameters.
     * @param pKey the key
     * @return the keySpec
     */
    static GordianKeyMacParameters key(final GordianKey<GordianMacSpec> pKey) {
        return new GordianKeyMacParameters(pKey);
    }

    /**
     * Obtain keyAndNonce Parameters.
     * @param pKey the key
     * @param pNonce the nonce
     * @return the keySpec
     */
    static GordianKeyMacParameters keyAndNonce(final GordianKey<GordianMacSpec> pKey,
                                               final byte[] pNonce) {
        return new GordianKeyAndNonceMacParameters(pKey, pNonce);
    }

    /**
     * Obtain keyAndRandomNonce Parameters.
     * @param pKey the key
     * @return the keySpec
     */
    static GordianKeyMacParameters keyWithRandomNonce(final GordianKey<GordianMacSpec> pKey) {
        return new GordianKeyAndNonceMacParameters(pKey);
    }
    /**
     * Key Parameters.
     */
    class GordianKeyMacParameters
            implements GordianMacParameters {
        /**
         * The Key.
         */
        private final GordianKey<GordianMacSpec> theKey;

        /**
         * Constructor.
         * @param pKey the key
         */
        GordianKeyMacParameters(final GordianKey<GordianMacSpec> pKey) {
            theKey = pKey;
        }

        /**
         * Obtain the key.
         * @return the key
         */
        public GordianKey<GordianMacSpec> getKey() {
            return theKey;
        }
    }

    /**
     * KeyAndNonce Parameters.
     */
    class GordianKeyAndNonceMacParameters
            extends GordianKeyMacParameters
            implements GordianNonceParameters {
        /**
         * The Nonce.
         */
        private final byte[] theNonce;

        /**
         * Random Nonce requested?
         */
        private final boolean randomNonce;

        /**
         * Constructor for random nonce.
         * @param pKey the key
         */
        GordianKeyAndNonceMacParameters(final GordianKey<GordianMacSpec> pKey) {
            super(pKey);
            theNonce = null;
            randomNonce = true;
        }

        /**
         * Constructor.
         * @param pKey the key
         * @param pNonce the nonce
         */
        GordianKeyAndNonceMacParameters(final GordianKey<GordianMacSpec> pKey,
                                        final byte[] pNonce) {
            super(pKey);
            theNonce = pNonce;
            randomNonce = false;
        }

        @Override
        public byte[] getNonce() {
            return theNonce;
        }

        @Override
        public boolean randomNonce() {
            return randomNonce;
        }
    }
}
