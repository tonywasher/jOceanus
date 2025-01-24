/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.api.mac;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKeyGenerator;

import java.util.List;
import java.util.function.Predicate;

/**
 * Mac factory.
 */
public interface GordianMacFactory {
    /**
     * obtain GordianKeyGenerator for MacSpec.
     * @param <T> the keyType
     * @param pKeyType the KeyType
     * @return the new KeyGenerator
     * @throws GordianException on error
     */
    <T extends GordianKeySpec> GordianKeyGenerator<T> getKeyGenerator(T pKeyType) throws GordianException;

    /**
     * create GordianMac.
     * @param pMacSpec the MacSpec
     * @return the new MAC
     * @throws GordianException on error
     */
    GordianMac createMac(GordianMacSpec pMacSpec) throws GordianException;

    /**
     * Obtain predicate for supported macSpecs.
     * @return the predicate
     */
    Predicate<GordianMacSpec> supportedMacSpecs();

    /**
     * Obtain predicate for supported macTypes.
     * @return the predicate
     */
    Predicate<GordianMacType> supportedMacTypes();

    /**
     * Obtain a list of supported macSpecs for a keyLength.
     * @param pKeyLen the keyLength
     * @return the list of supported macSpecs.
     */
    List<GordianMacSpec> listAllSupportedSpecs(GordianLength pKeyLen);
}
