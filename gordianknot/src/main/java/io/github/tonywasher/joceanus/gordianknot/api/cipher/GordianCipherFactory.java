/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPBESpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKey;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKeyGenerator;

import java.util.List;
import java.util.function.Predicate;

/**
 * Cipher factory.
 */
public interface GordianCipherFactory {
    /**
     * obtain GordianKeyGenerator for SymKeySpec.
     *
     * @param <T>      the key type
     * @param pKeyType the KeyType
     * @return the new KeyGenerator
     * @throws GordianException on error
     */
    <T extends GordianKeySpec> GordianKeyGenerator<T> getKeyGenerator(T pKeyType) throws GordianException;

    /**
     * create GordianSymCipher.
     *
     * @param pCipherSpec the cipherSpec
     * @return the new Cipher
     * @throws GordianException on error
     */
    GordianSymCipher createSymKeyCipher(GordianNewSymCipherSpec pCipherSpec) throws GordianException;

    /**
     * create GordianStreamCipher.
     *
     * @param pCipherSpec the cipherSpec
     * @return the new Cipher
     * @throws GordianException on error
     */
    GordianStreamCipher createStreamKeyCipher(GordianNewStreamCipherSpec pCipherSpec) throws GordianException;

    /**
     * create new GordianSymKeySpecBuilder.
     *
     * @return the new SymKeySpecBuilder
     */
    GordianNewSymKeySpecBuilder newSymKeySpecBuilder();

    /**
     * create new GordianStreamKeySpecBuilder.
     *
     * @return the new StreamKeySpecBuilder
     */
    GordianNewStreamKeySpecBuilder newStreamKeySpecBuilder();

    /**
     * create new GordianSymCipherSpecBuilder.
     *
     * @return the new SymCipherSpecBuilder
     */
    GordianNewSymCipherSpecBuilder newSymCipherSpecBuilder();

    /**
     * create new GordianStreamCipherSpecBuilder.
     *
     * @return the new StreamCipherSpecBuilder
     */
    GordianNewStreamCipherSpecBuilder newStreamCipherSpecBuilder();

    /**
     * create new GordianPBESpecBuilder.
     *
     * @return the new PBESpecBuilder
     */
    GordianNewPBESpecBuilder newPBESpecBuilder();

    /**
     * Obtain predicate for supported symKeySpecs.
     *
     * @return the predicate
     */
    Predicate<GordianNewSymKeySpec> supportedSymKeySpecs();

    /**
     * Obtain predicate for supported symCipherSpecs.
     *
     * @return the predicate
     */
    Predicate<GordianNewSymCipherSpec> supportedSymCipherSpecs();

    /**
     * Obtain predicate for supported SymKeyTypes.
     *
     * @return the predicate
     */
    Predicate<GordianNewSymKeyType> supportedSymKeyTypes();

    /**
     * Obtain predicate for supported streamKeySpecs.
     *
     * @return the predicate
     */
    Predicate<GordianNewStreamKeySpec> supportedStreamKeySpecs();

    /**
     * Obtain predicate for supported streamCipherSpecs.
     *
     * @return the predicate
     */
    Predicate<GordianNewStreamCipherSpec> supportedStreamCipherSpecs();

    /**
     * Obtain predicate for supported StreamKeyTypes.
     *
     * @return the predicate
     */
    Predicate<GordianNewStreamKeyType> supportedStreamKeyTypes();

    /**
     * create GordianWrapper.
     *
     * @param pKey the Key
     * @return the new wrapper
     * @throws GordianException on error
     */
    GordianWrapper createKeyWrapper(GordianKey<GordianNewSymKeySpec> pKey) throws GordianException;

    /**
     * Obtain a list of supported symCipherSpecs.
     *
     * @param pSpec the symKeySpec
     * @return the list of supported symCipherSpecs.
     */
    List<GordianNewSymCipherSpec> listAllSupportedSymCipherSpecs(GordianNewSymKeySpec pSpec);

    /**
     * Obtain a list of supported symKeySpecs for the keyLength.
     *
     * @param pKeyLen the keyLength
     * @return the list of supported symKeySpecs.
     */
    List<GordianNewSymKeySpec> listAllSupportedSymKeySpecs(GordianLength pKeyLen);

    /**
     * List all possible symKeySpecs for the keyLength.
     *
     * @param pKeyLen the keyLength
     * @return the list
     */
    List<GordianNewSymKeySpec> listAllSymKeySpecs(GordianLength pKeyLen);

    /**
     * Obtain a list of supported symKeyTypes.
     *
     * @return the list of supported symKeyTypes.
     */
    List<GordianNewSymKeyType> listAllSupportedSymKeyTypes();

    /**
     * Obtain a list of supported streamCipherSpecs for the keyLength.
     *
     * @param pKeyLen the keyLength
     * @return the list of supported streamCipherSpecs.
     */
    List<GordianNewStreamCipherSpec> listAllSupportedStreamCipherSpecs(GordianLength pKeyLen);

    /**
     * Obtain a list of supported streamKeySpecs for the keyLength.
     *
     * @param pKeyLen the keyLength
     * @return the list of supported streamKeySpecs.
     */
    List<GordianNewStreamKeySpec> listAllSupportedStreamKeySpecs(GordianLength pKeyLen);

    /**
     * Obtain a list of supported streamKeyTypes.
     *
     * @return the list of supported streamKeyTypes.
     */
    List<GordianNewStreamKeyType> listAllSupportedStreamKeyTypes();

    /**
     * List all possible cipherSpecs for a SymKeySpec.
     *
     * @param pSpec the keySpec
     * @return the list
     */
    List<GordianNewSymCipherSpec> listAllSymCipherSpecs(GordianNewSymKeySpec pSpec);
}
