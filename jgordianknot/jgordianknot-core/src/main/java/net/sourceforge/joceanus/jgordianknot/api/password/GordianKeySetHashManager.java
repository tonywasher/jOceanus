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
package net.sourceforge.joceanus.jgordianknot.api.password;

import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * KeySetHash Manager.
 */
public interface GordianKeySetHashManager {
    /**
     * Create a new keySet Hash.
     * @param pSource the description of the secured resource
     * @return the keySetHash
     * @throws OceanusException on error
     */
    default GordianKeySetHash newKeySetHash(String pSource) throws OceanusException {
        return newKeySetHash(new GordianKeySetHashSpec(), pSource);
    }

    /**
     * Create a new keySet Hash.
     * @param pKeySetHashSpec the keySetHashSpec
     * @param pSource the description of the secured resource
     * @return the keySetHash
     * @throws OceanusException on error
     */
    GordianKeySetHash newKeySetHash(GordianKeySetHashSpec pKeySetHashSpec,
                                    String pSource) throws OceanusException;

    /**
     * Resolve the keySet Hash.
     * @param pHashBytes the hash bytes to resolve
     * @param pSource the description of the secured resource
     * @return the keySetHash
     * @throws OceanusException on error
     */
    GordianKeySetHash resolveKeySetHash(byte[] pHashBytes,
                                        String pSource) throws OceanusException;

    /**
     * obtain similar (same password) hash.
     * @param pReference the reference to clone password from
     * @return the similar keySetHash
     * @throws OceanusException on error
     */
    default GordianKeySetHash similarKeySetHash(Object pReference) throws OceanusException {
        return similarKeySetHash(new GordianKeySetHashSpec(), pReference);
    }

    /**
     * obtain similar (same password) hash.
     * @param pKeySetHashSpec the keySetHashSpec
     * @param pReference the reference to clone password from
     * @return the similar keySetHash
     * @throws OceanusException on error
     */
    GordianKeySetHash similarKeySetHash(GordianKeySetHashSpec pKeySetHashSpec,
                                        Object pReference) throws OceanusException;
}
