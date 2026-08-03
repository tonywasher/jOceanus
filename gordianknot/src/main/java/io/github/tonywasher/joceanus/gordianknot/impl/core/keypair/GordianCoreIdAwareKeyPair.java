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

package io.github.tonywasher.joceanus.gordianknot.impl.core.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianIdAwareKeyType;

/**
 * Core IdAware keyPair.
 */
public interface GordianCoreIdAwareKeyPair {
    /**
     * IdAware PrivateKey.
     *
     * @param <K> the keyType.
     */
    public interface GordianIdAwarePrivateKey<K extends GordianIdAwareKeyType> {
        /**
         * get subKeyType.
         *
         * @return the keyType
         */
        K getSubKeyType();

        /**
         * Obtain identity.
         *
         * @return the identity
         */
        default byte[] getIdentity() {
            return new byte[0];
        }

        /**
         * Obtain a new user keyPair for identity.
         *
         * @param pKeyType  the user keyType
         * @param pIdentity the identity
         * @return the new keyPair
         */
        default GordianIdAwarePrivateKey<K> newUserPrivateKey(K pKeyType,
                                                              byte[] pIdentity) {
            return null;
        }
    }

    /**
     * IdAware Public Key.
     *
     * @param <K> the keyType.
     */
    public interface GordianIdAwarePublicKey<K extends GordianIdAwareKeyType> {
        /**
         * get subKeyType.
         *
         * @return the keyType
         */
        K getSubKeyType();

        /**
         * Obtain identity.
         *
         * @return the identity
         */
        default byte[] getIdentity() {
            return new byte[0];
        }

        /**
         * Obtain a new user keyPair for identity.
         *
         * @param pKeyType  the user keyType
         * @param pIdentity the identity
         * @return the new keyPair
         */
        default GordianIdAwarePublicKey<K> deriveUserPublicKey(K pKeyType,
                                                               byte[] pIdentity) {
            return null;
        }
    }
}
