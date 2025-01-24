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
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipLock;
import org.bouncycastle.asn1.x500.X500Name;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Function;

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
         * @throws GordianException on error
         */
        void resolveLock(GordianZipLock pLock) throws GordianException;
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
     * export object to stream.
     * @param pAlias the alias
     * @param pStream the target stream
     * @param pLock the lock for the file
     * @throws GordianException on error
     */
    void exportEntry(String pAlias,
                     OutputStream pStream,
                     GordianZipLock pLock) throws GordianException;

    /**
     * set the certificateRequest encryption entry.
     * @param pAlias the alias
     * @throws GordianException on error
     */
    void setEncryptionTarget(String pAlias) throws GordianException;

    /**
     * set the Certifier.
     * @param pAlias the alias
     * @throws GordianException on error
     */
    void setCertifier(String pAlias) throws GordianException;

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
     * Set the MAC secret resolver.
     * @param pResolver the resolver
     */
    void setMACSecretResolver(Function<X500Name, String> pResolver);

    /**
     * create certificate request.
     * @param pAlias the alias
     * @param pStream the target stream
     * @throws GordianException on error
     */
    void createCertificateRequest(String pAlias,
                                  OutputStream pStream) throws GordianException;

    /**
     * process certificate request.
     * @param pInStream the input stream
     * @param pOutStream the output stream
     * @throws GordianException on error
     */
    void processCertificateRequest(InputStream pInStream,
                                   OutputStream pOutStream) throws GordianException;

    /**
     * process certificate response.
     * @param pInStream the input stream
     * @param pOutStream the output stream
     * @return the response id
     * @throws GordianException on error
     */
    Integer processCertificateResponse(InputStream pInStream,
                                       OutputStream pOutStream) throws GordianException;

    /**
     * process certificate ack.
     * @param pInStream the input stream
     * @throws GordianException on error
     */
    void processCertificateAck(InputStream pInStream) throws GordianException;

    /**
     * import object from stream.
     * @param pStream the input stream
     * @return the parsed object
     * @throws GordianException on error
     */
    GordianKeyStoreEntry importEntry(InputStream pStream) throws GordianException;

    /**
     * import certificates from stream.
     * @param pStream the input stream
     * @return the parsed object
     * @throws GordianException on error
     */
    List<GordianKeyStoreEntry> importCertificates(InputStream pStream) throws GordianException;
}
