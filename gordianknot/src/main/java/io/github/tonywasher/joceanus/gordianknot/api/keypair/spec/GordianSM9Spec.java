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

package io.github.tonywasher.joceanus.gordianknot.api.keypair.spec;

/**
 * SM9 keySpec.
 */
public interface GordianSM9Spec {
    /**
     * SM9 keyType.
     */
    interface GordianSM9KeyType
            extends GordianIdAwareKeyType {
    }

    /**
     * SM9 Encrypt subtypes.
     */
    enum GordianSM9EncryptType
            implements GordianSM9KeyType {
        /**
         * Master.
         */
        ENCMASTER,

        /**
         * Encrypt.
         */
        ENCRYPT,

        /**
         * Exchange.
         */
        EXCHANGE;

        @Override
        public boolean isUserKey() {
            return this != ENCMASTER;
        }
    }

    /**
     * SM9 Sign subtypes.
     */
    enum GordianSM9SignType
            implements GordianSM9KeyType {
        /**
         * Master.
         */
        SIGNMASTER,

        /**
         * Sign.
         */
        SIGN;

        @Override
        public boolean isUserKey() {
            return this != SIGNMASTER;
        }
    }
}
