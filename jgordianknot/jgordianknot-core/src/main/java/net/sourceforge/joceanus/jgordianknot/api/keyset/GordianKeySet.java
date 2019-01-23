/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.keyset;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * keySet API.
 */
public interface GordianKeySet {
    /**
     * Encrypt bytes.
     * @param pBytes the bytes to encrypt
     * @return the encrypted bytes
     * @throws OceanusException on error
     */
    byte[] encryptBytes(byte[] pBytes) throws OceanusException;

    /**
     * Decrypt bytes.
     * @param pBytes the bytes to decrypt
     * @return the decrypted bytes
     * @throws OceanusException on error
     */
    byte[] decryptBytes(byte[] pBytes) throws OceanusException;

    /**
     * secure Key.
     * @param pKeyToSecure the key to wrap
     * @return the securedKey
     * @throws OceanusException on error
     */
    byte[] secureKey(GordianKey<?> pKeyToSecure) throws OceanusException;

    /**
     * derive Key.
     * @param <T> the keyType class
     * @param pSecuredKey the secured key
     * @param pKeyType the key type
     * @return the derived key
     * @throws OceanusException on error
     */
    <T extends GordianKeySpec> GordianKey<T> deriveKey(byte[] pSecuredKey,
                                                       T pKeyType) throws OceanusException;

    /**
     * secure privateKey.
     * @param pKeyPair the keyPair to secure
     * @return the securedPrivateKey
     * @throws OceanusException on error
     */
    byte[] securePrivateKey(GordianKeyPair pKeyPair) throws OceanusException;

    /**
     * derive keyPair.
     * @param pPublicKeySpec the publicKeySpec
     * @param pSecuredPrivateKey the secured privateKey
     * @return the keyPair
     * @throws OceanusException on error
     */
    GordianKeyPair deriveKeyPair(X509EncodedKeySpec pPublicKeySpec,
                                 byte[] pSecuredPrivateKey) throws OceanusException;

    /**
     * Declare symmetricKey.
     * @param pKey the key
     * @throws OceanusException on error
     */
    void declareSymKey(final GordianKey<GordianSymKeySpec> pKey) throws OceanusException;
}
