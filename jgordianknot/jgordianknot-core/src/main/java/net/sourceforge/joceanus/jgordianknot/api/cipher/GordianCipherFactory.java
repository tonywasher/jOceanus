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

import java.util.List;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Cipher factory.
 */
public interface GordianCipherFactory {
    /**
     * obtain GordianKeyGenerator for SymKeySpec.
     * @param <T> the key type
     * @param pKeyType the KeyType
     * @return the new KeyGenerator
     * @throws OceanusException on error
     */
    <T extends GordianKeySpec> GordianKeyGenerator<T> getKeyGenerator(T pKeyType) throws OceanusException;

    /**
     * create GordianSymCipher.
     * @param pCipherSpec the cipherSpec
     * @return the new Cipher
     * @throws OceanusException on error
     */
    GordianSymCipher createSymKeyCipher(GordianSymCipherSpec pCipherSpec) throws OceanusException;

    /**
     * create GordianStreamCipher.
     * @param pCipherSpec the cipherSpec
     * @return the new Cipher
     * @throws OceanusException on error
     */
    GordianStreamCipher createStreamKeyCipher(GordianStreamCipherSpec pCipherSpec) throws OceanusException;

    /**
     * Obtain predicate for supported symKeySpecs.
     * @return the predicate
     */
    Predicate<GordianSymKeySpec> supportedSymKeySpecs();

    /**
     * Obtain predicate for supported symCipherSpecs.
     * @return the predicate
     */
    Predicate<GordianSymCipherSpec> supportedSymCipherSpecs();

    /**
     * Obtain predicate for supported SymKeyTypes.
     * @return the predicate
     */
    Predicate<GordianSymKeyType> supportedSymKeyTypes();

    /**
     * Obtain predicate for supported streamKeySpecs.
     * @return the predicate
     */
    Predicate<GordianStreamKeySpec> supportedStreamKeySpecs();

    /**
     * Obtain predicate for supported streamCipherSpecs.
     * @return the predicate
     */
    Predicate<GordianStreamCipherSpec> supportedStreamCipherSpecs();

    /**
     * Obtain predicate for supported StreamKeyTypes.
     * @return the predicate
     */
    Predicate<GordianStreamKeyType> supportedStreamKeyTypes();

    /**
     * create GordianWrapper.
     * @param pKey the Key
     * @return the new wrapper
     * @throws OceanusException on error
     */
    GordianWrapper createKeyWrapper(GordianKey<GordianSymKeySpec> pKey) throws OceanusException;

    /**
     * Obtain a list of supported symCipherSpecs.
     * @param pSpec the symKeySpec
     * @return the list of supported symCipherSpecs.
     */
    List<GordianSymCipherSpec> listAllSupportedSymCipherSpecs(GordianSymKeySpec pSpec);

    /**
     * Obtain a list of supported symKeySpecs for the keyLength.
     * @param pKeyLen the keyLength
     * @return the list of supported symKeySpecs.
     */
    List<GordianSymKeySpec> listAllSupportedSymKeySpecs(GordianLength pKeyLen);

    /**
     * List all possible symKeySpecs for the keyLength.
     * @param pKeyLen the keyLength
     * @return the list
     */
    List<GordianSymKeySpec> listAllSymKeySpecs(GordianLength pKeyLen);

    /**
     * Obtain a list of supported symKeyTypes.
     * @return the list of supported symKeyTypes.
     */
    List<GordianSymKeyType> listAllSupportedSymKeyTypes();

    /**
     * Obtain a list of supported streamCipherSpecs for the keyLength.
     * @param pKeyLen the keyLength
     * @return the list of supported streamCipherSpecs.
     */
    List<GordianStreamCipherSpec> listAllSupportedStreamCipherSpecs(GordianLength pKeyLen);

    /**
     * Obtain a list of supported streamKeySpecs for the keyLength.
     * @param pKeyLen the keyLength
     * @return the list of supported streamKeySpecs.
     */
    List<GordianStreamKeySpec> listAllSupportedStreamKeySpecs(GordianLength pKeyLen);

    /**
     * Obtain a list of supported streamKeyTypes.
     * @return the list of supported streamKeyTypes.
     */
    List<GordianStreamKeyType> listAllSupportedStreamKeyTypes();

    /**
     * List all possible cipherSpecs for a SymKeySpec.
     * @param pSpec the keySpec
     * @return the list
     */
    List<GordianSymCipherSpec> listAllSymCipherSpecs(GordianSymKeySpec pSpec);
}
