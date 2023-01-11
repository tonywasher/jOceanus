/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2023 Tony Washer
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

import java.security.spec.X509EncodedKeySpec;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
     * @throws OceanusException on error
     */
    GordianKeySetCipher createCipher() throws OceanusException;

    /**
     * Encrypt bytes.
     * @param pBytesToEncrypt the bytes to encrypt
     * @return the encrypted bytes
     * @throws OceanusException on error
     */
    byte[] encryptBytes(byte[] pBytesToEncrypt) throws OceanusException;

    /**
     * Decrypt bytes.
     * @param pBytesToDecrypt the bytes to decrypt
     * @return the decrypted bytes
     * @throws OceanusException on error
     */
    byte[] decryptBytes(byte[] pBytesToDecrypt) throws OceanusException;

    /**
     * Create a keySetAADCipher.
     * @return the keySetCipher
     * @throws OceanusException on error
     */
    GordianKeySetAADCipher createAADCipher() throws OceanusException;

    /**
     * Encrypt AAD bytes.
     * @param pBytesToEncrypt the bytes to encrypt
     * @return the encrypted bytes
     * @throws OceanusException on error
     */
    default byte[] encryptAADBytes(byte[] pBytesToEncrypt) throws OceanusException {
        return encryptAADBytes(pBytesToEncrypt, null);
    }

    /**
     * Encrypt AAD bytes.
     * @param pBytesToEncrypt the bytes to encrypt
     * @param pAAD the AAD data
     * @return the encrypted bytes
     * @throws OceanusException on error
     */
    byte[] encryptAADBytes(byte[] pBytesToEncrypt,
                           byte[] pAAD) throws OceanusException;

    /**
     * Decrypt AAD bytes.
     * @param pBytesToDecrypt the bytes to decrypt
     * @return the decrypted bytes
     * @throws OceanusException on error
     */
    default byte[] decryptAADBytes(byte[] pBytesToDecrypt) throws OceanusException {
        return decryptAADBytes(pBytesToDecrypt, null);
    }

    /**
     * Decrypt AAD bytes.
     * @param pBytesToDecrypt the bytes to decrypt
     * @param pAAD the AAD data
     * @return the decrypted bytes
     * @throws OceanusException on error
     */
    byte[] decryptAADBytes(byte[] pBytesToDecrypt,
                           byte[] pAAD) throws OceanusException;

    /**
     * secure bytes.
     * @param pBytesToSecure the bytes to secure
     * @return the securedBytes
     * @throws OceanusException on error
     */
    byte[] secureBytes(byte[] pBytesToSecure) throws OceanusException;

    /**
     * derive bytes.
     * @param pSecuredBytes the secured bytes
     * @return the derivedBytes
     * @throws OceanusException on error
     */
    byte[] deriveBytes(byte[] pSecuredBytes) throws OceanusException;

    /**
     * secure Key.
     * @param pKeyToSecure the key to secure
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
     * secure KeySet.
     * @param pKeySetToSecure the keySet to secure
     * @return the securedKeySet
     * @throws OceanusException on error
     */
    byte[] secureKeySet(GordianKeySet pKeySetToSecure) throws OceanusException;

    /**
     * derive KeySet.
     * @param pSecuredKeySet the secured keySet
     * @return the derived keySet
     * @throws OceanusException on error
     */
    GordianKeySet deriveKeySet(byte[] pSecuredKeySet) throws OceanusException;

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
     * Obtain wrapped size of a key.
     * @param pKeyLen the keyLength
     * @return the wrapped length
     */
    int getKeyWrapLength(GordianLength pKeyLen);

    /**
     * Obtain wrapped size of a byte array of the given length.
     * @param pDataLength the length of the byte array
     * @return the wrapped length
     */
    int getDataWrapLength(int pDataLength);

    /**
     * Obtain wrapped size of the privateKey of a keyPair.
     * @param pKeyPair the keyPair
     * @return the wrapped length
     * @throws OceanusException on error
     */
    int getPrivateKeyWrapLength(GordianKeyPair pKeyPair) throws OceanusException;

    /**
     * Obtain the keySet wrap length.
     * @return the length
     */
    int getKeySetWrapLength();

    /**
     * Clone the keySet.
     * @return the cloned keySet
     * @throws OceanusException on error
     */
    GordianKeySet cloneIt() throws OceanusException;
}
