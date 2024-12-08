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
package net.sourceforge.joceanus.gordianknot.api.keystore;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;

import java.io.File;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;

/**
 * KeyStore.
 */
public interface GordianKeyStore {
    /**
     * Store the keyStore to a file.
     * @param pTarget the file to store to
     * @param pPassword the password
     * @throws GordianException on error
     */
    void storeToFile(File pTarget,
                     char[] pPassword) throws GordianException;

    /**
     * Store the keyStore to an OutputStream
     * .
     * @param pTarget the stream to store to
     * @param pPassword the password
     * @throws GordianException on error
     */
    void storeToStream(OutputStream pTarget,
                       char[] pPassword) throws GordianException;

    /**
     * Obtain a list of all aliases.
     * @return the list
     */
    List<String> getAliases();

    /**
     * Does the store contain this alias?
     * @param pAlias the alias
     * @return true/false
     */
    boolean containsAlias(String pAlias);

    /**
     * Obtain the number of entries in this keyStore.
     * @return the # of entries
     */
    int size();

    /**
     * Delete the entry relating to this alias (if it exists).
     * @param pAlias the alias to remove
     */
    void deleteEntry(String pAlias);

    /**
     * Set certificate entry.
     * @param pAlias the alias
     * @param pCertificate the certificate
     * @throws GordianException on error
     */
    void setCertificate(String pAlias,
                        GordianCertificate pCertificate) throws GordianException;

    /**
     * Set keyPair entry.
     * @param pAlias the alias
     * @param pKeyPair the keyPair
     * @param pPassword the securing password.
     * @param pCertificateChain the certificateChain
     * @throws GordianException on error
     */
    void setKeyPair(String pAlias,
                    GordianKeyPair pKeyPair,
                    char[] pPassword,
                    List<GordianCertificate> pCertificateChain) throws GordianException;

    /**
     * Update certificateChain.
     * @param pAlias the alias
     * @param pCertificateChain the certificateChain
     * @throws GordianException on error
     */
    void updateCertificateChain(String pAlias,
                                List<GordianCertificate> pCertificateChain) throws GordianException;

    /**
     * Set key entry.
     * @param <T> the key type
     * @param pAlias the alias
     * @param pKey the key
     * @param pPassword the securing password.
     * @throws GordianException on error
     */
    <T extends GordianKeySpec> void setKey(String pAlias,
                                           GordianKey<T> pKey,
                                           char[] pPassword) throws GordianException;

    /**
     * Set keySet entry.
     * @param pAlias the alias
     * @param pKeySet the keySet
     * @param pPassword the securing password.
     * @throws GordianException on error
     */
    void setKeySet(String pAlias,
                   GordianKeySet pKeySet,
                   char[] pPassword) throws GordianException;

    /**
     * Determine whether the alias is a specified entry type.
     * @param <T> the entry class
     * @param pAlias the alias
     * @param pClazz the entry class
     * @return true/false
     */
    <T extends GordianKeyStoreEntry> boolean entryInstanceOf(String pAlias,
                                                             Class<T> pClazz);

    /**
     * Determine whether the alias is a certificate entry.
     * @param pAlias the alias
     * @return true/false
     */
    boolean isCertificateEntry(String pAlias);

    /**
     * Determine whether the alias is a keyPair entry.
     * @param pAlias the alias
     * @return true/false
     */
    boolean isKeyPairEntry(String pAlias);

    /**
     * Determine whether the alias is a certificate/pair entry.
     * @param pAlias the alias
     * @return true/false
     */
    default boolean isCertificate(final String pAlias) {
        return isKeyPairEntry(pAlias) || isCertificateEntry(pAlias);
    }

    /**
     * Determine whether the alias is a key entry.
     * @param pAlias the alias
     * @return true/false
     */
    boolean isKeyEntry(String pAlias);

    /**
     * Determine whether the alias is a keySet entry.
     * @param pAlias the alias
     * @return true/false
     */
    boolean isKeySetEntry(String pAlias);

    /**
     * Obtain the Entry for the alias.
     * @param pAlias the alias
     * @param pPassword the password (or null if not required)
     * @return the entry (or null)
     * @throws GordianException on error
     */
    GordianKeyStoreEntry getEntry(String pAlias,
                                  char[] pPassword) throws GordianException;

    /**
     * Obtain the Certificate for the alias.
     * @param pAlias the alias
     * @return the keyPairCertificate (or null)
     */
    GordianCertificate getCertificate(String pAlias);

    /**
     * Obtain the CertificateChain for the alias.
     * @param pAlias the alias
     * @return the keyPairCertificateChain (or null)
     */
    List<GordianCertificate> getCertificateChain(String pAlias);

    /**
     * Obtain the keyPair for the alias.
     * @param pAlias the alias
     * @param pPassword the password
     * @return the keyPair (or null)
     * @throws GordianException on error
     */
    GordianKeyPair getKeyPair(String pAlias,
                              char[] pPassword) throws GordianException;

    /**
     * Obtain the key for the alias.
     * @param <T> the keyType
     * @param pAlias the alias
     * @param pPassword the password
     * @return the key (or null)
     * @throws GordianException on error
     */
     <T extends GordianKeySpec> GordianKey<T> getKey(String pAlias,
                                                     char[] pPassword) throws GordianException;

    /**
     * Obtain the keySet for the alias.
     * @param pAlias the alias
     * @param pPassword the password
     * @return the keySet (or null)
     * @throws GordianException on error
     */
    GordianKeySet getKeySet(String pAlias,
                            char[] pPassword) throws GordianException;

    /**
     * Obtain the creationDate of the alias.
     * @param pAlias the alias
     * @return the creation date
     */
    LocalDate getCreationDate(String pAlias);

    /**
     * Obtain the alias for this certificate.
     * @param pCertificate the certificate
     * @return the Alias if it exists
     */
    String getCertificateAlias(GordianCertificate pCertificate);
}
