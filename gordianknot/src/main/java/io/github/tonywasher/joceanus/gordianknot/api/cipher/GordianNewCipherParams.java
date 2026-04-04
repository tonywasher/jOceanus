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

package io.github.tonywasher.joceanus.gordianknot.api.cipher;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianPBESpec;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKey;

/**
 * Cipher Parameters.
 */
public interface GordianNewCipherParams {
    /**
     * Key Parameters.
     *
     * @param <T> the keyType
     */
    interface GordianNewKeyCipherParameters<T extends GordianKeySpec>
            extends GordianNewCipherParams {
        /**
         * Obtain the key.
         *
         * @return the key
         */
        GordianKey<T> getKey();
    }

    /**
     * Nonce Parameters.
     */
    interface GordianNewNonceParameters {
        /**
         * Was a random Nonce requested?.
         *
         * @return true/false
         */
        boolean randomNonce();

        /**
         * Obtain the nonce.
         *
         * @return the nonce
         */
        byte[] getNonce();
    }

    /**
     * KeyAndNonce Parameters.
     *
     * @param <T> the keyType
     */
    interface GordianNewKeyAndNonceCipherParameters<T extends GordianKeySpec>
            extends GordianNewKeyCipherParameters<T>, GordianNewNonceParameters {
    }

    /**
     * AEAD Parameters.
     *
     * @param <T> the keyType
     */
    interface GordianNewAEADCipherParameters<T extends GordianKeySpec>
            extends GordianNewKeyAndNonceCipherParameters<T> {
        /**
         * Obtain the initialAEAD.
         *
         * @return the initialAEAD
         */
        byte[] getInitialAEAD();
    }

    /**
     * PBE Parameters.
     */
    interface GordianNewPBECipherParameters
            extends GordianNewCipherParams, GordianNewNonceParameters {
        /**
         * Obtain the PBESpec.
         *
         * @return the PBESpec
         */
        GordianPBESpec getPBESpec();

        /**
         * Obtain the password.
         *
         * @return the password
         */
        char[] getPassword();
    }
}
