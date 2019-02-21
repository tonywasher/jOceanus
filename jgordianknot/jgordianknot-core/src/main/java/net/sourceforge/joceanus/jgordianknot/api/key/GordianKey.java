/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.key;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;

/**
 * GordianKnot Key abstraction.
 * @param <T> the Key type
 */
public interface GordianKey<T extends GordianKeySpec> {
    /**
     * Obtain the keyType.
     * @return the keyType
     */
    T getKeyType();

    /**
     * Convert key to different type with the same keyBytes.
     * @param <X> the keyClass
     * @param pKeyType the new keyType
     * @return the new key
     */
    <X extends GordianKeySpec> GordianKey<X> convertToKeyType(X pKeyType);
}