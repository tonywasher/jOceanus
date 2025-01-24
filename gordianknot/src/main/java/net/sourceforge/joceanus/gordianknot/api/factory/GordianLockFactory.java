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
package net.sourceforge.joceanus.gordianknot.api.factory;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianKeyPairLock;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianKeySetLock;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianPasswordLockSpec;

/**
 * Lock Factory API.
 */
public interface GordianLockFactory {
    /**
     * Create a new factoryLock.
     * @param pFactoryToLock the factory to lock
     * @param pPassword the password
     * @return the factoryLock
     * @throws GordianException on error
     */
    default GordianFactoryLock newFactoryLock(GordianFactory pFactoryToLock,
                                              char[] pPassword) throws GordianException {
        /* Create the factoryLock */
        return newFactoryLock(pFactoryToLock, new GordianPasswordLockSpec(), pPassword);
    }

    /**
     * Create a new factoryLock for a factory.
     * @param pFactoryToLock the factory to lock
     * @param pLockSpec the locking spec
     * @param pPassword the password
     * @return the factoryLock
     * @throws GordianException on error
     */
    GordianFactoryLock newFactoryLock(GordianFactory pFactoryToLock,
                                      GordianPasswordLockSpec pLockSpec,
                                      char[] pPassword) throws GordianException;

    /**
     * Create a new factoryLock.
     * @param pFactoryType the factoryType
     * @param pPassword the password
     * @return the factoryLock
     * @throws GordianException on error
     */
    default GordianFactoryLock newFactoryLock(GordianFactoryType pFactoryType,
                                              char[] pPassword) throws GordianException {
        /* Create the factoryLock */
        return newFactoryLock(new GordianPasswordLockSpec(), pFactoryType, pPassword);
    }

    /**
     * Create a new factoryLock for a new random factory.
     * @param pLockSpec the locking spec
     * @param pFactoryType the factoryType
     * @param pPassword the password
     * @return the factoryLock
     * @throws GordianException on error
     */
    GordianFactoryLock newFactoryLock(GordianPasswordLockSpec pLockSpec,
                                      GordianFactoryType pFactoryType,
                                      char[] pPassword) throws GordianException;

    /**
     * Resolve a factoryLock.
     * @param pLockBytes the lockBytes
     * @param pPassword the password
     * @return the resolved factoryLock
     * @throws GordianException on error
     */
    GordianFactoryLock resolveFactoryLock(byte[] pLockBytes,
                                          char[] pPassword) throws GordianException;

    /**
     * Create a new keySetLock for a keySet.
     * @param pKeySetToLock the keySet to lock
     * @param pPassword the password
     * @return the keySet lock
     * @throws GordianException on error
     */
    default GordianKeySetLock newKeySetLock(final GordianKeySet pKeySetToLock,
                                            final char[] pPassword) throws GordianException {
        return newKeySetLock(pKeySetToLock, new GordianPasswordLockSpec(), pPassword);
    }

    /**
     * Create a new keySetLock for a keySet.
     * @param pKeySetToLock the keySet to lock
     * @param pLockSpec the locking spec
     * @param pPassword the password
     * @return the keySet lock
     * @throws GordianException on error
     */
    GordianKeySetLock newKeySetLock(GordianKeySet pKeySetToLock,
                                    GordianPasswordLockSpec pLockSpec,
                                    char[] pPassword) throws GordianException;

    /**
     * Create a new keySetLock for a new random keySet.
     * @param pPassword the password
     * @return the keySet lock
     * @throws GordianException on error
     */
    default GordianKeySetLock newKeySetLock(final char[] pPassword) throws GordianException {
        return newKeySetLock(new GordianPasswordLockSpec(), pPassword);
    }

    /**
     * Create a new keySetLock for a new random keySet.
     * @param pLockSpec the locking spec
     * @param pPassword the password
     * @return the keySet lock
     * @throws GordianException on error
     */
    GordianKeySetLock newKeySetLock(GordianPasswordLockSpec pLockSpec,
                                    char[] pPassword) throws GordianException;

    /**
     * Resolve a keySetLock.
     * @param pLockBytes the lockBytes
     * @param pPassword the password
     * @return the resolved keySetLock
     * @throws GordianException on error
     */
    GordianKeySetLock resolveKeySetLock(byte[] pLockBytes,
                                        char[] pPassword) throws GordianException;

    /**
     * Create a new keyPairLock.
     * @param pKeyPair the keyPair
     * @param pPassword the password
     * @return the keySet lock
     * @throws GordianException on error
     */
    default GordianKeyPairLock newKeyPairLock(final GordianKeyPair pKeyPair,
                                              final char[] pPassword) throws GordianException {
        return newKeyPairLock(new GordianPasswordLockSpec(), pKeyPair, pPassword);
    }

    /**
     * Create a new keyPairLock.
     * @param pLockSpec the locking spec
     * @param pKeyPair the keyPair
     * @param pPassword the password
     * @return the keySet lock
     * @throws GordianException on error
     */
    GordianKeyPairLock newKeyPairLock(GordianPasswordLockSpec pLockSpec,
                                      GordianKeyPair pKeyPair,
                                      char[] pPassword) throws GordianException;

    /**
     * Resolve a keySetLock.
     * @param pLockBytes the lockBytes
     * @param pKeyPair the keyPair
     * @param pPassword the password
     * @return the resolved keySetLock
     * @throws GordianException on error
     */
    GordianKeyPairLock resolveKeyPairLock(byte[] pLockBytes,
                                          GordianKeyPair pKeyPair,
                                          char[] pPassword) throws GordianException;
}
