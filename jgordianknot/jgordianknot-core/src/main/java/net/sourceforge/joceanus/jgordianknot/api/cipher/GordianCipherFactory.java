/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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

import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
    GordianCipher<GordianSymKeySpec> createSymKeyCipher(GordianSymCipherSpec pCipherSpec) throws OceanusException;

    /**
     * create GordianAADCipher.
     * @param pCipherSpec the cipherSpec
     * @return the new Cipher
     * @throws OceanusException on error
     */
    GordianAADCipher createAADCipher(GordianSymCipherSpec pCipherSpec) throws OceanusException;

    /**
     * create GordianStreamCipher.
     * @param pCipherSpec the cipherSpec
     * @return the new Cipher
     * @throws OceanusException on error
     */
    GordianCipher<GordianStreamKeyType> createStreamKeyCipher(GordianStreamCipherSpec pCipherSpec) throws OceanusException;

    /**
     * Obtain predicate for supported symKeySpecs.
     * @return the predicate
     */
    Predicate<GordianSymKeySpec> supportedSymKeySpecs();

    /**
     * Obtain predicate for supported symCipherSpecs.
     * @return the predicate
     */
    BiPredicate<GordianSymCipherSpec, Boolean> supportedSymCipherSpecs();

    /**
     * Obtain predicate for supported SymKeyTypes.
     * @return the predicate
     */
    Predicate<GordianSymKeyType> supportedSymKeyTypes();

    /**
     * Obtain predicate for supported StreamKeyTypes.
     * @return the predicate
     */
    Predicate<GordianStreamKeyType> supportedStreamKeyTypes();

    /**
     * create GordianWrapper.
     * @param pKeySpec the KeySpec
     * @return the new wrapper
     * @throws OceanusException on error
     */
    GordianWrapper createKeyWrapper(GordianSymKeySpec pKeySpec) throws OceanusException;

    /**
     * Obtain a list of supported symKeySpecs.
     * @return the list of supported symKeySpecs.
     */
    default List<GordianSymKeySpec> listAllSupportedSymKeySpecs() {
        return GordianSymKeySpec.listAll()
                .stream()
                .filter(supportedSymKeySpecs())
                .collect(Collectors.toList());
    }

    /**
     * Obtain a list of supported symCipherSpecs.
     * @param isAAD are the cipherSpecs for an AADCipher?
     * @return the list of supported symCipherSpecs.
     */
    default List<GordianSymCipherSpec> listAllSupportedSymCipherSpecs(final boolean isAAD) {
        return GordianSymCipherSpec.listAll()
                .stream()
                .filter(s -> supportedSymCipherSpecs().test(s, isAAD))
                .collect(Collectors.toList());
    }

    /**
     * Obtain a list of supported symKeyTypes.
     * @return the list of supported symKeyTypes.
     */
    default List<GordianSymKeyType> listAllSupportedSymKeyTypes() {
        return Arrays.stream(GordianSymKeyType.values())
                .filter(supportedSymKeyTypes())
                .collect(Collectors.toList());
    }

    /**
     * Obtain a list of supported streamKeyTypes.
     * @return the list of supported streamKeyTypes.
     */
    default List<GordianStreamKeyType> listAllSupportedStreamKeyTypes() {
        return Arrays.stream(GordianStreamKeyType.values())
                .filter(supportedStreamKeyTypes())
                .collect(Collectors.toList());
    }
}
