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
package net.sourceforge.joceanus.gordianknot.api.cipher;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;

import java.security.spec.X509EncodedKeySpec;

/**
 * GordianKnot base for Wrap Cipher.
 */
public interface GordianWrapper {
    /**
     * Obtain the keyType.
     * @return the keyType
     */
    GordianSymKeySpec getKeySpec();

    /**
     * Secure key.
     * @param pKeyToSecure the key to secure
     * @return the securedKey
     * @throws GordianException on error
     */
    byte[] secureKey(GordianKey<?> pKeyToSecure) throws GordianException;

    /**
     * Derive key from bytes.
     * @param <T> type of key to be derived
     * @param pSecuredKey the securedKey
     * @param pKeyType the type of key to be derived
     * @return the derived key
     * @throws GordianException on error
     */
    <T extends GordianKeySpec> GordianKey<T> deriveKey(byte[] pSecuredKey,
                                                       T pKeyType) throws GordianException;

    /**
     * Secure privateKey.
     * @param pKeyPair the keyPair to secure
     * @return the securedPrivateKey
     * @throws GordianException on error
     */
    byte[] securePrivateKey(GordianKeyPair pKeyPair) throws GordianException;

    /**
     * Derive the keyPair from the PKCS8/X509 encodings.
     * @param pPublicKeySpec the publicKeySpec
     * @param pSecuredPrivateKey the secured privateKey
     * @return the derived keyPair
     * @throws GordianException on error
     */
    GordianKeyPair deriveKeyPair(X509EncodedKeySpec pPublicKeySpec,
                                 byte[] pSecuredPrivateKey) throws GordianException;

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
    default byte[] deriveBytes(final byte[] pSecuredBytes) throws GordianException {
        return deriveBytes(pSecuredBytes, 0);
    }

    /**
     * derive bytes.
     * @param pSecuredBytes the secured bytes
     * @param pOffset the offset within the secured bytes
     * @return the derivedBytes
     * @throws GordianException on error
     */
    byte[] deriveBytes(byte[] pSecuredBytes,
                       int pOffset) throws GordianException;

    /**
     * Obtain wrapped size of a key.
     * @param pKey the keyToWrap
     * @return the wrapped length
     */
    int getKeyWrapLength(GordianKey<?> pKey);

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
     * @throws GordianException on error
     */
    int getPrivateKeyWrapLength(GordianKeyPair pKeyPair) throws GordianException;
}
