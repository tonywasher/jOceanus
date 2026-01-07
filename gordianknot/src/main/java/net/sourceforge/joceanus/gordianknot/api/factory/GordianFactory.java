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
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianLock;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianLockFactory;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianPasswordLockSpec;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.gordianknot.api.random.GordianRandomFactory;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipFactory;

/**
 * Factory API.
 */
public interface GordianFactory {
    /**
     * Obtain factory type.
     * @return the factory type
     */
    GordianFactoryType getFactoryType();

    /**
     * Obtain the Digest Factory.
     * @return the digest factory
     */
    GordianDigestFactory getDigestFactory();

    /**
     * Obtain the Cipher Factory.
     * @return the cipher factory
     */
    GordianCipherFactory getCipherFactory();

    /**
     * Obtain the Mac Factory.
     * @return the Mac factory
     */
    GordianMacFactory getMacFactory();

    /**
     * Obtain the keySet Factory.
     * @return the keySet factory
     */
    GordianKeySetFactory getKeySetFactory();

    /**
     * Obtain the random Factory.
     * @return the random factory
     */
    GordianRandomFactory getRandomFactory();

    /**
     * Obtain the Lock Factory.
     * @return the lock factory
     */
    GordianLockFactory getLockFactory();

    /**
     * Obtain the Zip Factory.
     * @return the zip factory
     */
    GordianZipFactory getZipFactory();

    /**
     * Obtain the async Factory.
     * @return the async factory
     */
    GordianAsyncFactory getAsyncFactory();

    /**
     * ReSeed the random number generator.
     */
    void reSeedRandom();

    /**
     * Obtain the obfuscater.
     * @return the obfuscater
     */
    GordianKnuthObfuscater getObfuscater();

    /**
     * Obtain the embedded keySet.
     * @return the keySet (or null)
     */
    GordianKeySet getEmbeddedKeySet();

    /**
     * Create a new factoryLock.
     * @param pFactoryToLock the factory to lock
     * @param pPassword the password
     * @return the factoryLock
     * @throws GordianException on error
     */
    default GordianFactoryLock newFactoryLock(final GordianFactory pFactoryToLock,
                                              final char[] pPassword) throws GordianException {
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
    default GordianFactoryLock newFactoryLock(final GordianFactoryType pFactoryType,
                                              final char[] pPassword) throws GordianException {
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
     * Factory Lock.
     */
    interface GordianFactoryLock
            extends GordianLock<GordianFactory> {
        /**
         * Obtain the factory.
         * @return the factory
         */
        default GordianFactory getFactory() {
            return getLockedObject();
        }
    }
}
