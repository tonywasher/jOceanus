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

import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.lock.GordianKeyPairLock;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * KeyPair Lock Manager.
 */
public interface GordianKeyPairLockManager {
    /**
     * Create a new keyPairLock.
     * @param pKeyPair the keyPair
     * @param pSource the description of the secured resource
     * @return the keyPairLock
     * @throws OceanusException on error
     */
    GordianKeyPairLock newKeyPairLock(GordianKeyPair pKeyPair,
                                      String pSource) throws OceanusException;

    /**
     * Resolve the keyPairLock.
     * @param pLockBytes the LockBytes to resolve
     * @param pKeyPair the keyPair
     * @param pSource the description of the secured resource
     * @return the keyPairLock
     * @throws OceanusException on error
     */
    GordianKeyPairLock resolveKeyPairLock(byte[] pLockBytes,
                                          GordianKeyPair pKeyPair,
                                          String pSource) throws OceanusException;

    /**
     * obtain similar (same password) zipLock.
     * @param pKeyPair the keyPair
     * @param pReference the reference to clone password from
     * @return the similar keyPairLock
     * @throws OceanusException on error
     */
    GordianKeyPairLock similarKeyPairLock(GordianKeyPair pKeyPair,
                                          Object pReference) throws OceanusException;
}
