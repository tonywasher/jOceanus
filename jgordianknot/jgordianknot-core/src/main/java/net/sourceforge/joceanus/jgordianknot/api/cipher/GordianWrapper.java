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
package net.sourceforge.joceanus.jgordianknot.api.cipher;

import java.security.spec.X509EncodedKeySpec;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
     * @param pKey the key to use to secure the key
     * @param pKeyToSecure the key to secure
     * @return the securedKey
     * @throws OceanusException on error
     */
    byte[] secureKey(GordianKey<GordianSymKeySpec> pKey,
                     GordianKey<?> pKeyToSecure) throws OceanusException;

    /**
     * Derive key from bytes.
     * @param <T> type of key to be derived
     * @param pKey the key to use to derive the key
     * @param pSecuredKey the securedKey
     * @param pKeyType the type of key to be derived
     * @return the derived key
     * @throws OceanusException on error
     */
    <T extends GordianKeySpec> GordianKey<T> deriveKey(GordianKey<GordianSymKeySpec> pKey,
                                                       byte[] pSecuredKey,
                                                       T pKeyType) throws OceanusException;

    /**
     * Secure privateKey.
     * @param pKey the key to use to secure privateKey
     * @param pKeyPair the keyPair to secure
     * @return the securedPrivateKey
     * @throws OceanusException on error
     */
    byte[] securePrivateKey(GordianKey<GordianSymKeySpec> pKey,
                            GordianKeyPair pKeyPair) throws OceanusException;

    /**
     * Derive the keyPair from the PKCS8/X509 encodings.
     * @param pKey the key to use to derive privateKey
     * @param pPublicKeySpec the publicKeySpec
     * @param pSecuredPrivateKey the secured privateKey
     * @return the derived keyPair
     * @throws OceanusException on error
     */
    GordianKeyPair deriveKeyPair(GordianKey<GordianSymKeySpec> pKey,
                                 X509EncodedKeySpec pPublicKeySpec,
                                 byte[] pSecuredPrivateKey) throws OceanusException;

    /**
     * secure bytes.
     * @param pKey the key to use to secure the key
     * @param pBytesToSecure the bytes to secure
     * @return the securedBytes
     * @throws OceanusException on error
     */
    byte[] secureBytes(GordianKey<GordianSymKeySpec> pKey,
                       byte[] pBytesToSecure) throws OceanusException;

    /**
     * derive bytes.
     * @param pKey the key to use to derive the bytes
     * @param pSecuredBytes the secured bytes
     * @return the derivedBytes
     * @throws OceanusException on error
     */
    byte[] deriveBytes(GordianKey<GordianSymKeySpec> pKey,
                       byte[] pSecuredBytes) throws OceanusException;
}
