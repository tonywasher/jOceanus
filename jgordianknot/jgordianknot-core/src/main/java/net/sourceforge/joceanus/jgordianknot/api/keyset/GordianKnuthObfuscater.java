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
package net.sourceforge.joceanus.jgordianknot.api.keyset;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot Knuth Obfuscator API.
 */
public interface GordianKnuthObfuscater {
    /**
     * Obtain external Id from Type.
     * @param pType the type
     * @param pAdjustment the adjustment
     * @return the externalId
     * @throws OceanusException on error
     */
    int deriveExternalIdFromType(Object pType,
                                 int pAdjustment) throws OceanusException;

    /**
     * Obtain external Id from Type.
     * @param pType the type
     * @return the externalId
     * @throws OceanusException on error
     */
    int deriveExternalIdFromType(Object pType) throws OceanusException;

    /**
     * Obtain external Id from Type.
     * @param <T> the type class
     * @param pId the externalId
     * @param pAdjustment the adjustment
     * @param pClazz the class of the type
     * @return the derived Type
     * @throws OceanusException on error
     */
    <T> T deriveTypeFromExternalId(int pId,
                                   int pAdjustment,
                                   Class<T> pClazz) throws OceanusException;

    /**
     * Obtain external Id from Type.
     * @param <T> the type class
     * @param pId the externalId
     * @param pClazz the class of the type
     * @return the derived Type
     * @throws OceanusException on error
     */
    <T> T deriveTypeFromExternalId(int pId,
                                   Class<T> pClazz) throws OceanusException;
}
