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

import org.bouncycastle.asn1.x500.X500Name;

import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreKey;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreSet;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

/**
 * KeyStore Manager.
 */
public interface GordianKeyStoreManager {
    /**
     * Obtain the keyStore.
     * @return the keyStore
     */
    GordianKeyStore getKeyStore();

    /**
     * Create a new keySet.
     *
     * @param pKeySetSpec the spec of the new keySet
     * @param pAlias the alias
     * @param pPassword the password
     * @return the new keySet entry
     * @throws OceanusException on error
     */
    GordianKeyStoreSet createKeySet(GordianKeySetSpec pKeySetSpec,
                                    String pAlias,
                                    char[] pPassword) throws OceanusException;

    /**
     * Create a new key.
     *
     * @param <K> the type of the new key
     * @param pKeySpec the spec of the new key
     * @param pAlias the alias
     * @param pPassword the password
     * @return the new keySet entry
     * @throws OceanusException on error
     */
    <K extends GordianKeySpec> GordianKeyStoreKey<K> createKey(K pKeySpec,
                                                               String pAlias,
                                                               char[] pPassword) throws OceanusException;

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
    GordianKeyStorePair createRootKeyPair(GordianKeyPairSpec pKeySpec,
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
    GordianKeyStorePair createKeyPair(GordianKeyPairSpec pKeySpec,
                                      X500Name pSubject,
                                      GordianKeyPairUsage pUsage,
                                      GordianKeyStorePair pSigner,
                                      String pAlias,
                                      char[] pPassword) throws OceanusException;

    /**
     * Create an alternate certificate for keyPair.
     *
     * @param pKeyPair the existing keyPair record
     * @param pUsage   the key usage
     * @param pSigner the signer
     * @param pAlias the alias
     * @param pPassword the password
     * @return the new keyPair entry
     * @throws OceanusException on error
     */
    GordianKeyStorePair createAlternate(GordianKeyStorePair pKeyPair,
                                        GordianKeyPairUsage pUsage,
                                        GordianKeyStorePair pSigner,
                                        String pAlias,
                                        char[] pPassword) throws OceanusException;
}
