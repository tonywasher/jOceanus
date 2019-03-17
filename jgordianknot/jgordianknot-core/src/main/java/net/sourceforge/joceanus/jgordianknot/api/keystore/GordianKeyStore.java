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

import java.util.List;

import org.bouncycastle.asn1.x500.X500Name;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreHash;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreKey;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * KeyStore.
 */
public interface GordianKeyStore {
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
     * @throws OceanusException on error
     */
    void setCertificate(String pAlias,
                        GordianCertificate pCertificate) throws OceanusException;

    /**
     * Set keyPair entry.
     * @param pAlias the alias
     * @param pKeyPair the keyPair
     * @param pPassword the securing password.
     * @param pCertificateChain the certificateChain
     * @throws OceanusException on error
     */
    void setKeyPair(String pAlias,
                    GordianKeyPair pKeyPair,
                    char[] pPassword,
                    GordianCertificate[] pCertificateChain) throws OceanusException;

    /**
     * Set key entry.
     * @param <T> the key type
     * @param pAlias the alias
     * @param pKey the key
     * @param pPassword the securing password.
     * @throws OceanusException on error
     */
    <T extends GordianKeySpec> void setKey(String pAlias,
                                           GordianKey<T> pKey,
                                           char[] pPassword) throws OceanusException;

    /**
     * Set keySet entry.
     * @param pAlias the alias
     * @param pKeySet the keySet
     * @param pPassword the securing password.
     * @throws OceanusException on error
     */
    void setKeySet(String pAlias,
                   GordianKeySet pKeySet,
                   char[] pPassword) throws OceanusException;

    /**
     * Set keySetHash entry.
     * @param pAlias the alias
     * @param pHash the keySetHash
     */
    void setKeySetHash(String pAlias,
                       GordianKeySetHash pHash);

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
     * Determine whether the alias is a keySetHash entry.
     * @param pAlias the alias
     * @return true/false
     */
    boolean isKeySetHashEntry(String pAlias);

    /**
     * Obtain the Entry for the alias.
     * @param pAlias the alias
     * @param pPassword the password
     * @return the entry
     * @throws OceanusException on error
     */
    GordianKeyStoreEntry getEntry(String pAlias,
                                  char[] pPassword) throws OceanusException;

    /**
     * Obtain the certificate for the alias.
     * @param pAlias the alias
     * @return the keyPair
     */
    GordianKeyStoreCertificate getCertificate(String pAlias);

    /**
     * Obtain the keyPair for the alias.
     * @param pAlias the alias
     * @param pPassword the password
     * @return the keyPair
     * @throws OceanusException on error
     */
    GordianKeyStorePair getKeyPair(String pAlias,
                                   char[] pPassword) throws OceanusException;

    /**
     * Obtain the key for the alias.
     * @param <T> the keyType
     * @param pAlias the alias
     * @param pPassword the password
     * @return the key
     * @throws OceanusException on error
     */
     <T extends GordianKeySpec> GordianKeyStoreKey<T> getKey(String pAlias,
                                                             char[] pPassword) throws OceanusException;

    /**
     * Obtain the keySet for the alias.
     * @param pAlias the alias
     * @param pPassword the password
     * @return the keySet
     * @throws OceanusException on error
     */
    GordianKeyStoreSet getKeySet(String pAlias,
                                 char[] pPassword) throws OceanusException;

    /**
     * Obtain the keySetHash for the alias.
     * @param pAlias the alias
     * @param pPassword the password
     * @return the keySetHash
     * @throws OceanusException on error
     */
    GordianKeyStoreHash getKeySetHash(String pAlias,
                                      char[] pPassword) throws OceanusException;

    /**
     * Obtain the creationDate of the alias.
     * @param pAlias the alias
     * @return the creation date
     */
    TethysDate getCreationDate(String pAlias);

    /**
     * Obtain the alias for this certificate.
     * @param pCertificate the certificate
     * @return the Alias if it exists
     */
    String getCertificateAlias(GordianCertificate pCertificate);

    /**
     * Create a new keyPair with root certificate.
     *
     * @param pKeySpec the spec of the new keyPair
     * @param pSubject the name of the entity
     * @param pAlias the alias
     * @param pPassword the password
     * @return the new keyPair entry
     * @throws OceanusException on error
     */
    GordianKeyStorePair createRootKeyPair(GordianAsymKeySpec pKeySpec,
                                          X500Name pSubject,
                                          String pAlias,
                                          char[] pPassword) throws OceanusException;

    /**
     * Create a new keyPair with certificate.
     *
     * @param pKeySpec the spec of the new keyPair
     * @param pSubject the name of the entity
     * @param pUsage   the key usage
     * @param pSigner the signer
     * @param pAlias the alias
     * @param pPassword the password
     * @return the new keyPair entry
     * @throws OceanusException on error
     */
    GordianKeyStorePair createKeyPair(GordianAsymKeySpec pKeySpec,
                                      X500Name pSubject,
                                      GordianKeyPairUsage pUsage,
                                      GordianKeyStorePair pSigner,
                                      String pAlias,
                                      char[] pPassword) throws OceanusException;
}
