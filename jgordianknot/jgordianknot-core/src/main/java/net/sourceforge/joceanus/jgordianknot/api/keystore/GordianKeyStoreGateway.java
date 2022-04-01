/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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
import java.io.OutputStream;
import java.util.List;
import java.util.function.Function;

import net.sourceforge.joceanus.jgordianknot.api.zip.GordianLock;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * KeyStore Gateway.
 */
public interface GordianKeyStoreGateway {
    /**
     * UnLock notification.
     */
    interface GordianLockResolver {
        /**
         * resolve Lock.
         * @param pLock the lock to resolve
         * @throws OceanusException on error
         */
        void resolveLock(GordianLock pLock) throws OceanusException;
    }

    /**
     * Obtain the keyStore.
     * @return the keyStore
     */
    GordianKeyStore getKeyStore();

    /**
     * Obtain the keyStoreManager.
     * @return the keyStoreMgr
     */
    GordianKeyStoreManager getKeyStoreManager();

    /**
     * export object to file.
     * @param pAlias the alias
     * @param pFile the target file
     * @param pPassword the keyStore password for the entry
     * @param pLock the lock for the file
     * @throws OceanusException on error
     */
    void exportEntry(String pAlias,
                     File pFile,
                     char[] pPassword,
                     GordianLock pLock) throws OceanusException;

    /**
     * export object to stream.
     * @param pAlias the alias
     * @param pStream the target stream
     * @param pPassword the keyStore password for the entry
     * @param pLock the lock for the file
     * @throws OceanusException on error
     */
    void exportEntry(String pAlias,
                     OutputStream pStream,
                     char[] pPassword,
                     GordianLock pLock) throws OceanusException;

    /**
     * set the certificateRequest encryption entry.
     * @param pAlias the alias
     * @throws OceanusException on error
     */
    void setEncryptionTarget(String pAlias) throws OceanusException;

    /**
     * set the Certifier.
     * @param pAlias the alias
     * @param pPassword the password
     * @throws OceanusException on error
     */
    void setCertifier(String pAlias,
                      char[] pPassword) throws OceanusException;

    /**
     * set the passwordResolver.
     * @param pResolver the resolver
     */
    void setPasswordResolver(Function<String, char[]> pResolver);

    /**
     * set the lockResolver.
     * @param pResolver the resolver
     */
    void setLockResolver(GordianLockResolver pResolver);

    /**
     * create certificate request.
     * @param pAlias the alias
     * @param pFile the target file
     * @param pPassword the password
     * @throws OceanusException on error
     */
    void createCertificateRequest(String pAlias,
                                  File pFile,
                                  char[] pPassword) throws OceanusException;

    /**
     * create certificate request.
     * @param pAlias the alias
     * @param pStream the target stream
     * @param pPassword the password
     * @throws OceanusException on error
     */
    void createCertificateRequest(String pAlias,
                                  OutputStream pStream,
                                  char[] pPassword) throws OceanusException;

    /**
     * process certificate request.
     * @param pInFile the input file
     * @param pOutFile the output file
     * @throws OceanusException on error
     */
    void processCertificateRequest(File pInFile,
                                   File pOutFile) throws OceanusException;

    /**
     * process certificate request.
     * @param pInStream the input stream
     * @param pOutStream the output stream
     * @throws OceanusException on error
     */
    void processCertificateRequest(InputStream pInStream,
                                   OutputStream pOutStream) throws OceanusException;

    /**
     * import object from file.
     * @param pFile the input file
     * @param pPassword the password
     * @return the parsed object
     * @throws OceanusException on error
     */
    GordianKeyStoreEntry importEntry(File pFile,
                                     char[] pPassword) throws OceanusException;

    /**
     * import object from stream.
     * @param pStream the input stream
     * @param pPassword the password
     * @return the parsed object
     * @throws OceanusException on error
     */
    GordianKeyStoreEntry importEntry(InputStream pStream,
                                     char[] pPassword) throws OceanusException;

    /**
     * import certificates from file.
     * @param pFile the input file
     * @return the parsed object
     * @throws OceanusException on error
     */
    List<GordianKeyStoreEntry> importCertificates(File pFile) throws OceanusException;

    /**
     * import certificates from stream.
     * @param pStream the input stream
     * @return the parsed object
     * @throws OceanusException on error
     */
    List<GordianKeyStoreEntry> importCertificates(InputStream pStream) throws OceanusException;
}
