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
package net.sourceforge.joceanus.gordianknot.api.keystore;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianPasswordLockSpec;

import java.io.File;
import java.io.InputStream;

/**
 * KeyStore Factory.
 */
public interface GordianKeyStoreFactory {
    /**
     * Create a new empty KeyStore.
     * @param pSpec the passwordLockSpec
     * @return the keyStore
     */
    GordianKeyStore createKeyStore(GordianPasswordLockSpec pSpec);

    /**
     * Load a keyStore from a File.
     * @param pSource the file to load from
     * @param pPassword the password
     * @return the loaded keyStore
     * @throws GordianException on error
     */
    GordianKeyStore loadKeyStore(File pSource,
                                 char[] pPassword) throws GordianException;

    /**
     * Load a keyStore from an InputStream.
     * @param pSource the stream to load from
     * @param pPassword the password
     * @return the loaded keyStore
     * @throws GordianException on error
     */
    GordianKeyStore loadKeyStore(InputStream pSource,
                                 char[] pPassword) throws GordianException;

    /**
     * Create a keyStore Manager for the KeyStore.
     * @param pKeyStore the keyStore
     * @return the keyStoreManager
     */
    GordianKeyStoreManager createKeyStoreManager(GordianKeyStore pKeyStore);

    /**
     * Create a keyStore Gateway for the Manager.
     * @param pKeyStoreMgr the keyStoreManager
     * @return the keyStoreGateway
     */
    GordianKeyStoreGateway createKeyStoreGateway(GordianKeyStoreManager pKeyStoreMgr);
}
