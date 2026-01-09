/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.api.keyset;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;

import java.security.spec.X509EncodedKeySpec;

/**
 * keySet API.
 */
public interface GordianKeySet {
    /**
     * Obtain the keySetSpec.
     * @return the keySetSpec
     */
    GordianKeySetSpec getKeySetSpec();

    /**
     * Create a keySetCipher.
     * @return the keySetCipher
     * @throws GordianException on error
     */
    GordianKeySetCipher createCipher() throws GordianException;

    /**
     * Encrypt bytes.
     * @param pBytesToEncrypt the bytes to encrypt
     * @return the encrypted bytes
     * @throws GordianException on error
     */
    byte[] encryptBytes(byte[] pBytesToEncrypt) throws GordianException;

    /**
     * Decrypt bytes.
     * @param pBytesToDecrypt the bytes to decrypt
     * @return the decrypted bytes
     * @throws GordianException on error
     */
    byte[] decryptBytes(byte[] pBytesToDecrypt) throws GordianException;

    /**
     * Create a keySetAADCipher.
     * @return the keySetCipher
     * @throws GordianException on error
     */
    GordianKeySetAADCipher createAADCipher() throws GordianException;

    /**
     * Encrypt AAD bytes.
     * @param pBytesToEncrypt the bytes to encrypt
     * @return the encrypted bytes
     * @throws GordianException on error
     */
    default byte[] encryptAADBytes(final byte[] pBytesToEncrypt) throws GordianException {
        return encryptAADBytes(pBytesToEncrypt, null);
    }

    /**
     * Encrypt AAD bytes.
     * @param pBytesToEncrypt the bytes to encrypt
     * @param pAAD the AAD data
     * @return the encrypted bytes
     * @throws GordianException on error
     */
    byte[] encryptAADBytes(byte[] pBytesToEncrypt,
                           byte[] pAAD) throws GordianException;

    /**
     * Decrypt AAD bytes.
     * @param pBytesToDecrypt the bytes to decrypt
     * @return the decrypted bytes
     * @throws GordianException on error
     */
    default byte[] decryptAADBytes(final byte[] pBytesToDecrypt) throws GordianException {
        return decryptAADBytes(pBytesToDecrypt, null);
    }

    /**
     * Decrypt AAD bytes.
     * @param pBytesToDecrypt the bytes to decrypt
     * @param pAAD the AAD data
     * @return the decrypted bytes
     * @throws GordianException on error
     */
    byte[] decryptAADBytes(byte[] pBytesToDecrypt,
                           byte[] pAAD) throws GordianException;

    /**
     * secure KeySet.
     * @param pKeySetToSecure the keySet to secure
     * @return the encryptedKeySet
     * @throws GordianException on error
     */
    byte[] secureKeySet(GordianKeySet pKeySetToSecure) throws GordianException;

    /**
     * derive KeySet.
     * @param pSecuredKeySet the secured keySet
     * @return the decrypted keySet
     * @throws GordianException on error
     */
    GordianKeySet deriveKeySet(byte[] pSecuredKeySet) throws GordianException;

    /**
     * secure bytes.
     * @param pBytesToSecure the bytes to secure
     * @return the securedBytes
     * @throws GordianException on error
     */
    byte[] secureBytes(byte[] pBytesToSecure) throws GordianException;

    /**
     * derive bytes.
     * @param pSecuredBytes the secured bytes
     * @return the derivedBytes
     * @throws GordianException on error
     */
    byte[] deriveBytes(byte[] pSecuredBytes) throws GordianException;

    /**
     * secure Key.
     * @param pKeyToSecure the key to secure
     * @return the securedKey
     * @throws GordianException on error
     */
    byte[] secureKey(GordianKey<?> pKeyToSecure) throws GordianException;

    /**
     * derive Key.
     * @param <T> the keyType class
     * @param pSecuredKey the secured key
     * @param pKeyType the key type
     * @return the derived key
     * @throws GordianException on error
     */
    <T extends GordianKeySpec> GordianKey<T> deriveKey(byte[] pSecuredKey,
                                                       T pKeyType) throws GordianException;

    /**
     * secure privateKey.
     * @param pKeyPair the keyPair to secure
     * @return the securedPrivateKey
     * @throws GordianException on error
     */
    byte[] securePrivateKey(GordianKeyPair pKeyPair) throws GordianException;

    /**
     * derive keyPair.
     * @param pPublicKeySpec the publicKeySpec
     * @param pSecuredPrivateKey the secured privateKey
     * @return the keyPair
     * @throws GordianException on error
     */
    GordianKeyPair deriveKeyPair(X509EncodedKeySpec pPublicKeySpec,
                                 byte[] pSecuredPrivateKey) throws GordianException;

    /**
     * Obtain wrapped size of a key.
     * @param pKeyLen the keyLength
     * @return the wrapped length
     */
    int getKeyWrapLength(GordianLength pKeyLen);

    /**
     * Obtain wrapped size of the privateKey of a keyPair.
     * @param pKeyPair the keyPair
     * @return the wrapped length
     * @throws GordianException on error
     */
    int getPrivateKeyWrapLength(GordianKeyPair pKeyPair) throws GordianException;

    /**
     * Obtain the keySet wrap length.
     * @return the length
     */
    int getKeySetWrapLength();

    /**
     * Clone the keySet.
     * @return the cloned keySet
     * @throws GordianException on error
     */
    GordianKeySet cloneIt() throws GordianException;
}
