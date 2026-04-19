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
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianCipherParams.GordianAEADCipherParameters;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianCipherParams.GordianKeyCipherParameters;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianCipherParams.GordianPBECipherParameters;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianCipherParamsBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianPBESpec;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.cipher.GordianCoreCipherParams.GordianCoreAEADCipherParameters;
import io.github.tonywasher.joceanus.gordianknot.impl.core.cipher.GordianCoreCipherParams.GordianCoreKeyAndNonceCipherParameters;
import io.github.tonywasher.joceanus.gordianknot.impl.core.cipher.GordianCoreCipherParams.GordianCoreKeyCipherParameters;
import io.github.tonywasher.joceanus.gordianknot.impl.core.cipher.GordianCoreCipherParams.GordianCorePBECipherParameters;

/**
 * Core CipherParameters Builder.
 */
public final class GordianCoreCipherParamsBuilder
        implements GordianCipherParamsBuilder {
    /**
     * Constructor.
     */
    private GordianCoreCipherParamsBuilder() {
    }

    /**
     * Create new CipherParamsBuilder.
     *
     * @return the Builder
     */
    public static GordianCipherParamsBuilder newInstance() {
        return new GordianCoreCipherParamsBuilder();
    }

    @Override
    public <T extends GordianKeySpec> GordianKeyCipherParameters<T> key(final GordianKey<T> pKey) {
        return new GordianCoreKeyCipherParameters<>(pKey);
    }

    @Override
    public <T extends GordianKeySpec> GordianKeyCipherParameters<T> keyAndNonce(final GordianKey<T> pKey,
                                                                                final byte[] pNonce) {
        return new GordianCoreKeyAndNonceCipherParameters<>(pKey, pNonce);
    }

    @Override
    public <T extends GordianKeySpec> GordianKeyCipherParameters<T> keyWithRandomNonce(final GordianKey<T> pKey) {
        return new GordianCoreKeyAndNonceCipherParameters<>(pKey);
    }

    @Override
    public <T extends GordianKeySpec> GordianAEADCipherParameters<T> aeadAndNonce(final GordianKey<T> pKey,
                                                                                  final byte[] pInitialAEAD,
                                                                                  final byte[] pNonce) {
        return new GordianCoreAEADCipherParameters<>(pKey, pNonce, pInitialAEAD);
    }

    @Override
    public <T extends GordianKeySpec> GordianAEADCipherParameters<T> aeadWithRandomNonce(final GordianKey<T> pKey,
                                                                                         final byte[] pInitialAEAD) {
        return new GordianCoreAEADCipherParameters<>(pKey, null, pInitialAEAD);
    }

    @Override
    public GordianPBECipherParameters pbe(final GordianPBESpec pPBESpec,
                                          final char[] pPassword) {
        return new GordianCorePBECipherParameters(pPBESpec, pPassword);
    }

    /**
     * Obtain pneAndNonce Parameters.
     *
     * @param pPBESpec  the pbeSpec
     * @param pPassword the password
     * @param pNonce    the nonce
     * @return the keySpec
     */
    public GordianPBECipherParameters pbeAndNonce(final GordianPBESpec pPBESpec,
                                                  final char[] pPassword,
                                                  final byte[] pNonce) {
        return new GordianCorePBECipherParameters(pPBESpec, pNonce, pPassword);
    }
}
