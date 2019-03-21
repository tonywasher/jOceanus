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
package net.sourceforge.joceanus.jgordianknot.api.keystore;

import java.io.File;
import java.io.InputStream;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * KeyStore Factory.
 */
public interface GordianKeyStoreFactory {
    /**
     * Create a new empty KeyStore.
     * @return the keyStore
     */
    GordianKeyStore createKeyStore();

    /**
     * Load a keyStore from a File.
     * @param pSource the file to load from
     * @param pPassword the password
     * @return the loaded keyStore
     * @throws OceanusException on error
     */
    GordianKeyStore loadKeyStore(File pSource,
                                 char[] pPassword) throws OceanusException;

    /**
     * Load a keyStore from an InputStream.
     * @param pSource the stream to load from
     * @param pPassword the password
     * @return the loaded keyStore
     * @throws OceanusException on error
     */
    GordianKeyStore loadKeyStore(InputStream pSource,
                                 char[] pPassword) throws OceanusException;
}