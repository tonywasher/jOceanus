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
package net.sourceforge.joceanus.jgordianknot.api.agree;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Key Agreement Specification.
 */
public interface GordianAgreement {
    /**
     * Obtain the agreementSpec.
     * @return the spec
     */
    GordianAgreementSpec getAgreementSpec();
    /**
     * Derive key.
     * @param <T> the type of key
     * @param pKeyType the key type
     * @return the key
     * @throws OceanusException on error
     */
    <T extends GordianKeySpec> GordianKey<T> deriveKey(T pKeyType) throws OceanusException;

    /**
     * Derive keySet.
     * @return the keySet
     * @throws OceanusException on error
     */
    GordianKeySet deriveKeySet() throws OceanusException;

    /**
     * Derive independent keySet.
     * @return the keySet
     * @throws OceanusException on error
     */
    GordianKeySet deriveIndependentKeySet() throws OceanusException;
}

