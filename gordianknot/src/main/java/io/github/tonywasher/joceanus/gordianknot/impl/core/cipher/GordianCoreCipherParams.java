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

package io.github.tonywasher.joceanus.gordianknot.impl.core.cipher;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianNewCipherParams.GordianNewAEADCipherParameters;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianNewCipherParams.GordianNewKeyAndNonceCipherParameters;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianNewCipherParams.GordianNewKeyCipherParameters;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianNewCipherParams.GordianNewPBECipherParameters;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianPBESpec;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKey;

/**
 * Core Cipher Parameters.
 */
public interface GordianCoreCipherParams {
    /**
     * Key Parameters.
     *
     * @param <T> the keyType
     */
    class GordianCoreKeyCipherParameters<T extends GordianKeySpec>
            implements GordianNewKeyCipherParameters<T> {
        /**
         * The Key.
         */
        private final GordianKey<T> theKey;

        /**
         * Constructor.
         *
         * @param pKey the key
         */
        GordianCoreKeyCipherParameters(final GordianKey<T> pKey) {
            theKey = pKey;
        }

        @Override
        public GordianKey<T> getKey() {
            return theKey;
        }
    }

    /**
     * KeyAndNonce Parameters.
     *
     * @param <T> the keyType
     */
    class GordianCoreKeyAndNonceCipherParameters<T extends GordianKeySpec>
            extends GordianCoreKeyCipherParameters<T>
            implements GordianNewKeyAndNonceCipherParameters<T> {
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
         *
         * @param pKey the key
         */
        GordianCoreKeyAndNonceCipherParameters(final GordianKey<T> pKey) {
            super(pKey);
            theNonce = null;
            randomNonce = true;
        }

        /**
         * Constructor.
         *
         * @param pKey   the key
         * @param pNonce the nonce
         */
        GordianCoreKeyAndNonceCipherParameters(final GordianKey<T> pKey,
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

    /**
     * AEAD Parameters.
     *
     * @param <T> the keyType
     */
    class GordianCoreAEADCipherParameters<T extends GordianKeySpec>
            extends GordianCoreKeyAndNonceCipherParameters<T>
            implements GordianNewAEADCipherParameters<T> {
        /**
         * The InitialAEAD.
         */
        private final byte[] theInitialAEAD;

        /**
         * Constructor.
         *
         * @param pKey         the key
         * @param pInitialAEAD the initialAEAD
         */
        GordianCoreAEADCipherParameters(final GordianKey<T> pKey,
                                        final byte[] pInitialAEAD) {
            super(pKey);
            theInitialAEAD = pInitialAEAD;
        }

        /**
         * Constructor.
         *
         * @param pKey         the key
         * @param pNonce       the nonce
         * @param pInitialAEAD the initialAEAD
         */
        GordianCoreAEADCipherParameters(final GordianKey<T> pKey,
                                        final byte[] pNonce,
                                        final byte[] pInitialAEAD) {
            super(pKey, pNonce);
            theInitialAEAD = pInitialAEAD;
        }

        @Override
        public byte[] getInitialAEAD() {
            return theInitialAEAD;
        }
    }

    /**
     * PBE Parameters.
     */
    class GordianCorePBECipherParameters
            implements GordianNewPBECipherParameters {
        /**
         * The PBESpec.
         */
        private final GordianPBESpec thePBESpec;

        /**
         * The Nonce.
         */
        private final byte[] theNonce;

        /**
         * Random Nonce requested?
         */
        private final boolean randomNonce;

        /**
         * The Password.
         */
        private final char[] thePassword;

        /**
         * Constructor for random nonce.
         *
         * @param pPBESpec  the PBESpec
         * @param pPassword the password
         */
        GordianCorePBECipherParameters(final GordianPBESpec pPBESpec,
                                       final char[] pPassword) {
            thePBESpec = pPBESpec;
            theNonce = null;
            randomNonce = true;
            thePassword = pPassword;
        }

        /**
         * Constructor.
         *
         * @param pPBESpec  the PBESpec
         * @param pNonce    the nonce
         * @param pPassword the password
         */
        GordianCorePBECipherParameters(final GordianPBESpec pPBESpec,
                                       final byte[] pNonce,
                                       final char[] pPassword) {
            thePBESpec = pPBESpec;
            theNonce = pNonce;
            randomNonce = false;
            thePassword = pPassword;
        }

        @Override
        public GordianPBESpec getPBESpec() {
            return thePBESpec;
        }

        @Override
        public byte[] getNonce() {
            return theNonce;
        }

        @Override
        public boolean randomNonce() {
            return randomNonce;
        }

        @Override
        public char[] getPassword() {
            return thePassword;
        }
    }
}
