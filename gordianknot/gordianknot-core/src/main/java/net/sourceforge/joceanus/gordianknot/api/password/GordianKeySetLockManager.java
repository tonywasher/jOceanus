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
package net.sourceforge.joceanus.gordianknot.api.password;

import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianKeySetLock;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * KeySetLock Manager.
 */
public interface GordianKeySetLockManager {
    /**
     * Create a new keySetLock.
     * @param pSource the description of the secured resource
     * @return the keySetLock
     * @throws OceanusException on error
     */
    GordianKeySetLock newKeySetLock(String pSource) throws OceanusException;

    /**
     * Create a new keySetLock.
     * @param pKeySet the keySet to lock
     * @param pSource the description of the secured resource
     * @return the keySetLock
     * @throws OceanusException on error
     */
    GordianKeySetLock newKeySetLock(GordianKeySet pKeySet,
                                    String pSource) throws OceanusException;

    /**
     * Resolve the keySetLock bytes.
     * @param pLockBytes the lock bytes to resolve
     * @param pSource the description of the secured resource
     * @return the keySetLock
     * @throws OceanusException on error
     */
    GordianKeySetLock resolveKeySetLock(byte[] pLockBytes,
                                        String pSource) throws OceanusException;

    /**
     * obtain new locked keySet (same password).
     * @param pReference the reference to clone password from
     * @return the similar keySetLock
     * @throws OceanusException on error
     */
    GordianKeySetLock similarKeySetLock(Object pReference) throws OceanusException;
}
