/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.keypairset;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * KeyPairSetGenerator.
 */
public interface GordianKeyPairSetGenerator {
    /**
     * Obtain keySpec.
     * @return the keySpec
     */
    GordianKeyPairSetSpec getKeyPairSetSpec();

    /**
     * Generate a new KeyPair.
     * @return the new KeyPair
     * @throws OceanusException on error
     */
    GordianKeyPairSet generateKeyPairSet() throws OceanusException;

    /**
     * Extract the X509 encoding for the public keyPairSet.
     * @param pKeyPairSet the keyPairSet
     * @return the X509 publicKeySetSpec
     * @throws OceanusException on error
     */
    X509EncodedKeySpec getX509Encoding(GordianKeyPairSet pKeyPairSet) throws OceanusException;

    /**
     * Obtain PKCS8EncodedKeySpec.
     * @param pKeyPairSet the keyPairSet
     * @return the PrivateKeySetSpec
     * @throws OceanusException on error
     */
    PKCS8EncodedKeySpec getPKCS8Encoding(GordianKeyPairSet pKeyPairSet) throws OceanusException;

    /**
     * Create the keyPairSet from the PKCS8/X509 encodings.
     * @param pPublicKeySet the encoded public key
     * @param pPrivateKeySet the secured private key
     * @return the keyPairSet
     * @throws OceanusException on error
     */
    GordianKeyPairSet deriveKeyPairSet(X509EncodedKeySpec pPublicKeySet,
                                       PKCS8EncodedKeySpec pPrivateKeySet) throws OceanusException;

    /**
     * Derive the public-only keyPairSet from the X509 encoding.
     * @param pPublicKeySet the publicKeySetSpec
     * @return the derived public-only keyPairSet
     * @throws OceanusException on error
     */
    GordianKeyPairSet derivePublicOnlyKeyPairSet(X509EncodedKeySpec pPublicKeySet) throws OceanusException;
}
