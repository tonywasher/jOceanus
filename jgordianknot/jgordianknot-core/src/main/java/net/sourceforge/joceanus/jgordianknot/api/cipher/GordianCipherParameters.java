/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.cipher;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;

/**
 * Cipher Parameters.
 */
public interface GordianCipherParameters {
    /**
     * Obtain keySpec Parameters.
     * @param <T> the keyType
     * @param pKey the key
     * @return the keySpec
     */
    static <T extends GordianKeySpec> GordianKeyCipherParameters<T> key(final GordianKey<T> pKey) {
        return new GordianKeyCipherParameters<>(pKey);
    }

    /**
     * Obtain keyAndNonce Parameters.
     * @param <T> the keyType
     * @param pKey the key
     * @param pNonce the nonce
     * @return the keySpec
     */
    static <T extends GordianKeySpec> GordianKeyCipherParameters<T> keyAndNonce(final GordianKey<T> pKey,
                                                                                final byte[] pNonce) {
        return new GordianKeyAndNonceCipherParameters<>(pKey, pNonce);
    }

    /**
     * Obtain keyAndRandomNonce Parameters.
     * @param <T> the keyType
     * @param pKey the key
     * @return the keySpec
     */
    static <T extends GordianKeySpec> GordianKeyCipherParameters<T> keyWithRandomNonce(final GordianKey<T> pKey) {
        return new GordianKeyAndNonceCipherParameters<>(pKey);
    }

    /**
     * Obtain aeadAndNonce Parameters.
     * @param <T> the keyType
     * @param pKey the key
     * @param pInitialAEAD the initialAEAD
     * @param pNonce the nonce
     * @return the keySpec
     */
    static <T extends GordianKeySpec> GordianAEADCipherParameters<T> aeadAndNonce(final GordianKey<T> pKey,
                                                                                  final byte[] pInitialAEAD,
                                                                                  final byte[] pNonce) {
        return new GordianAEADCipherParameters<>(pKey, pNonce, pInitialAEAD);
    }

    /**
     * Obtain aeadAndRandomNonce Parameters.
     * @param <T> the keyType
     * @param pKey the key
     * @param pInitialAEAD the initialAEAD
     * @return the keySpec
     */
    static <T extends GordianKeySpec> GordianAEADCipherParameters<T> aeadWithRandomNonce(final GordianKey<T> pKey,
                                                                                         final byte[] pInitialAEAD) {
        return new GordianAEADCipherParameters<>(pKey, null, pInitialAEAD);
    }

    /**
     * Obtain pbe Parameters.
     * @param pPBESpec the pbeSpec
     * @param pPassword the password
     * @return the keySpec
     */
    static GordianPBECipherParameters pbe(final GordianPBESpec pPBESpec,
                                          final char[] pPassword) {
        return new GordianPBECipherParameters(pPBESpec, pPassword);
    }

    /**
     * Obtain pneAndNonce Parameters.
     * @param pPBESpec the pbeSpec
     * @param pPassword the password
     * @param pNonce the nonce
     * @return the keySpec
     */
    static GordianPBECipherParameters pbeAndNonce(final GordianPBESpec pPBESpec,
                                                  final char[] pPassword,
                                                  final byte[] pNonce) {
        return new GordianPBECipherParameters(pPBESpec, pNonce, pPassword);
    }

    /**
     * Key Parameters.
     * @param <T> the keyType
     */
    class GordianKeyCipherParameters<T extends GordianKeySpec>
        implements GordianCipherParameters {
        /**
         * The Key.
         */
        private final GordianKey<T> theKey;

        /**
         * Constructor.
         * @param pKey the key
         */
        GordianKeyCipherParameters(final GordianKey<T> pKey) {
            theKey = pKey;
        }

        /**
         * Obtain the key.
         * @return the key
         */
        public GordianKey<T> getKey() {
            return theKey;
        }
    }

    /**
     * Nonce Parameters.
     */
    interface GordianNonceParameters {
        /**
         * Was a random Nonce requested?.
         * @return true/false
         */
        boolean randomNonce();

        /**
         * Obtain the nonce.
         * @return the nonce
         */
        byte[] getNonce();
    }

    /**
     * KeyAndNonce Parameters.
     * @param <T> the keyType
     */
    class GordianKeyAndNonceCipherParameters<T extends GordianKeySpec>
            extends GordianKeyCipherParameters<T>
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
        GordianKeyAndNonceCipherParameters(final GordianKey<T> pKey) {
            super(pKey);
            theNonce = null;
            randomNonce = true;
        }

        /**
         * Constructor.
         * @param pKey the key
         * @param pNonce the nonce
         */
        GordianKeyAndNonceCipherParameters(final GordianKey<T> pKey,
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
     * @param <T> the keyType
     */
    class GordianAEADCipherParameters<T extends GordianKeySpec>
            extends GordianKeyAndNonceCipherParameters<T> {
        /**
         * The InitialAEAD.
         */
        private final byte[] theInitialAEAD;

        /**
         * Constructor.
         * @param pKey the key
         * @param pInitialAEAD the initialAEAD
         */
        GordianAEADCipherParameters(final GordianKey<T> pKey,
                                    final byte[] pInitialAEAD) {
            super(pKey);
            theInitialAEAD = pInitialAEAD;
        }

        /**
         * Constructor.
         * @param pKey the key
         * @param pNonce the nonce
         * @param pInitialAEAD the initialAEAD
         */
        GordianAEADCipherParameters(final GordianKey<T> pKey,
                                    final byte[] pNonce,
                                    final byte[] pInitialAEAD) {
            super(pKey, pNonce);
            theInitialAEAD = pInitialAEAD;
        }

        /**
         * Obtain the initialAEAD.
         * @return the initialAEAD
         */
        public byte[] getInitialAEAD() {
            return theInitialAEAD;
        }
    }

    /**
     * PBE Parameters.
     */
    class GordianPBECipherParameters
            implements GordianCipherParameters, GordianNonceParameters {
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
         * @param pPBESpec the PBESpec
         * @param pPassword the password
         */
        GordianPBECipherParameters(final GordianPBESpec pPBESpec,
                                   final char[] pPassword) {
            thePBESpec = pPBESpec;
            theNonce = null;
            randomNonce = true;
            thePassword = pPassword;
        }

        /**
         * Constructor.
         * @param pPBESpec the PBESpec
         * @param pNonce the nonce
         * @param pPassword the password
         */
        GordianPBECipherParameters(final GordianPBESpec pPBESpec,
                                   final byte[] pNonce,
                                   final char[] pPassword) {
            thePBESpec = pPBESpec;
            theNonce = pNonce;
            randomNonce = false;
            thePassword = pPassword;
        }

        /**
         * Obtain the PBESpec.
         * @return the PBESpec
         */
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

        /**
         * Obtain the password.
         * @return the password
         */
        public char[] getPassword() {
            return thePassword;
        }
    }
}
