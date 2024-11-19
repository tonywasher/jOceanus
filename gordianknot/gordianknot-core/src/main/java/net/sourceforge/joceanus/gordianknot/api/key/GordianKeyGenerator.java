/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.api.key;

import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.tethys.OceanusException;

/**
 * Key Generator API.
 * @param <T> the keyType
 */
public interface GordianKeyGenerator<T extends GordianKeySpec> {
    /**
     * Obtain keyType.
     * @return the keyType
     */
    T getKeyType();

    /**
     * Generate a new Key.
     * @return the new Key
     */
    GordianKey<T> generateKey();

    /**
     * translate a compatible key into a key for this keySpec.
     * @param <X> the source keySpec
     * @param pSource the source key
     * @return the new Key
     * @throws OceanusException on error
     */
    <X extends GordianKeySpec> GordianKey<T> translateKey(GordianKey<X> pSource) throws OceanusException;
}
