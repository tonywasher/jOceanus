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
package net.sourceforge.joceanus.gordianknot.api.zip;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianLock;

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
     * Obtain lockBytes.
     * @return the lockBytes
     * @throws GordianException on error
     */
    byte[] getLockBytes() throws GordianException;

    /**
     * Unlock with resolved lock.
     * @param pLock the resolved lock
     * @throws GordianException on error
     */
    void unlock(GordianLock<?> pLock) throws GordianException;

    /**
     * Unlock with password.
     * @param pPassword the password
     * @throws GordianException on error
     */
    void unlock(char[] pPassword) throws GordianException;

    /**
     * Unlock with keyPair and password.
     * @param pKeyPair the keyPair
     * @param pPassword the password
     * @throws GordianException on error
     */
    void unlock(GordianKeyPair pKeyPair,
                char[] pPassword) throws GordianException;
}
