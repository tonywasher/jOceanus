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
package net.sourceforge.joceanus.gordianknot.api.keypair;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * KeyPair Generator API.
 */
public interface GordianKeyPairGenerator {
    /**
     * Obtain keySpec.
     * @return the keySpec
     */
    GordianKeyPairSpec getKeySpec();

    /**
     * Generate a new KeyPair.
     * @return the new KeyPair
     */
    GordianKeyPair generateKeyPair();

    /**
     * Extract the X509 encoding for the public key.
     * @param pKeyPair the keyPair
     * @return the X509 publicKeySpec
     * @throws GordianException on error
     */
    X509EncodedKeySpec getX509Encoding(GordianKeyPair pKeyPair) throws GordianException;

    /**
     * Obtain PKCS8EncodedKeySpec.
     * @param pKeyPair the keyPair
     * @return the PrivateKeySpec
     * @throws GordianException on error
     */
    PKCS8EncodedKeySpec getPKCS8Encoding(GordianKeyPair pKeyPair) throws GordianException;

    /**
     * Create the keyPair from the PKCS8/X509 encodings.
     * @param pPublicKey the encoded public key
     * @param pPrivateKey the secured private key
     * @return the keyPair
     * @throws GordianException on error
     */
    GordianKeyPair deriveKeyPair(X509EncodedKeySpec pPublicKey,
                                 PKCS8EncodedKeySpec pPrivateKey) throws GordianException;

    /**
     * Derive the public-only keyPair from the X509 encoding.
     * @param pPublicKeySpec the publicKeySpec
     * @return the derived public-only keyPair
     * @throws GordianException on error
     */
    GordianKeyPair derivePublicOnlyKeyPair(X509EncodedKeySpec pPublicKeySpec) throws GordianException;
}
