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
package net.sourceforge.joceanus.jgordianknot.api.zip;

import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Lock interface.
 */
public interface GordianZipLock {
    /**
     * Is this still locked?
     * @return true/false
     */
    boolean isLocked();

    /**
     * Is this available to lock a zipFile?
     * @return true/false
     */
    boolean isFresh();

    /**
     * Obtain lockType.
     * @return the lockType
     */
    GordianZipLockType getLockType();

    /**
     * Unlock with password.
     * @param pPassword the password
     * @throws OceanusException on error
     */
    void unlock(char[] pPassword) throws OceanusException;

    /**
     * Unlock with keyPair and password.
     * @param pKeyPair the keyPair
     * @param pPassword the password
     * @throws OceanusException on error
     */
    void unlock(GordianKeyPair pKeyPair,
                char[] pPassword) throws OceanusException;
}
