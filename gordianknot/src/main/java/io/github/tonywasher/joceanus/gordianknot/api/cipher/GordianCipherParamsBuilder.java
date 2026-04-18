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
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianCipherParams.GordianAEADCipherParameters;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianCipherParams.GordianKeyCipherParameters;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianCipherParams.GordianPBECipherParameters;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianPBESpec;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKey;

/**
 * Cipher Parameters Builder.
 */
public interface GordianCipherParamsBuilder {
    /**
     * Obtain keySpec Parameters.
     *
     * @param <T>  the keyType
     * @param pKey the key
     * @return the keySpec
     */
    <T extends GordianKeySpec> GordianKeyCipherParameters<T> key(GordianKey<T> pKey);

    /**
     * Obtain keyAndNonce Parameters.
     *
     * @param <T>    the keyType
     * @param pKey   the key
     * @param pNonce the nonce
     * @return the keySpec
     */
    <T extends GordianKeySpec> GordianKeyCipherParameters<T> keyAndNonce(GordianKey<T> pKey,
                                                                         byte[] pNonce);

    /**
     * Obtain keyAndRandomNonce Parameters.
     *
     * @param <T>  the keyType
     * @param pKey the key
     * @return the keySpec
     */
    <T extends GordianKeySpec> GordianKeyCipherParameters<T> keyWithRandomNonce(GordianKey<T> pKey);

    /**
     * Obtain aeadAndNonce Parameters.
     *
     * @param <T>          the keyType
     * @param pKey         the key
     * @param pInitialAEAD the initialAEAD
     * @param pNonce       the nonce
     * @return the keySpec
     */
    <T extends GordianKeySpec> GordianAEADCipherParameters<T> aeadAndNonce(GordianKey<T> pKey,
                                                                           byte[] pInitialAEAD,
                                                                           byte[] pNonce);

    /**
     * Obtain aeadAndRandomNonce Parameters.
     *
     * @param <T>          the keyType
     * @param pKey         the key
     * @param pInitialAEAD the initialAEAD
     * @return the keySpec
     */
    <T extends GordianKeySpec> GordianAEADCipherParameters<T> aeadWithRandomNonce(GordianKey<T> pKey,
                                                                                  byte[] pInitialAEAD);

    /**
     * Obtain pbe Parameters.
     *
     * @param pPBESpec  the pbeSpec
     * @param pPassword the password
     * @return the keySpec
     */
    GordianPBECipherParameters pbe(GordianPBESpec pPBESpec,
                                   char[] pPassword);

    /**
     * Obtain pneAndNonce Parameters.
     *
     * @param pPBESpec  the pbeSpec
     * @param pPassword the password
     * @param pNonce    the nonce
     * @return the keySpec
     */
    GordianPBECipherParameters pbeAndNonce(GordianPBESpec pPBESpec,
                                           char[] pPassword,
                                           byte[] pNonce);
}
