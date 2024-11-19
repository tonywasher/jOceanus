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
package net.sourceforge.joceanus.gordianknot.api.factory;

import net.sourceforge.joceanus.gordianknot.api.base.GordianIdSpec;
import net.sourceforge.joceanus.tethys.OceanusException;

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
    int deriveExternalIdFromType(GordianIdSpec pType,
                                 int pAdjustment) throws OceanusException;

    /**
     * Obtain external Id from Type.
     * @param pType the type
     * @return the externalId
     * @throws OceanusException on error
     */
    int deriveExternalIdFromType(GordianIdSpec pType) throws OceanusException;

    /**
     * Obtain IdSpec from external Id.
     * @param pId the externalId
     * @param pAdjustment the adjustment
     * @return the derived Type
     * @throws OceanusException on error
     */
    GordianIdSpec deriveTypeFromExternalId(int pId,
                                           int pAdjustment) throws OceanusException;

    /**
     * Obtain IdSpec from external Id.
     * @param pId the externalId
     * @return the derived Type
     * @throws OceanusException on error
     */
    GordianIdSpec deriveTypeFromExternalId(int pId) throws OceanusException;
}
