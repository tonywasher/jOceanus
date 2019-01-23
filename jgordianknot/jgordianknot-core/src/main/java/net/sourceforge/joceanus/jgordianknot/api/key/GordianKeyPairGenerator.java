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
package net.sourceforge.joceanus.jgordianknot.api.key;

import java.security.spec.X509EncodedKeySpec;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * KeyPair Generator API.
 */
public interface GordianKeyPairGenerator {
    /**
     * Obtain keySpec.
     * @return the keySpec
     */
    GordianAsymKeySpec getKeySpec();

    /**
     * Generate a new KeyPair.
     * @return the new KeyPair
     */
    GordianKeyPair generateKeyPair();

    /**
     * Extract the X509 encoding for the public key.
     * @param pKeyPair the keyPair
     * @return the X509 publicKeySpec
     * @throws OceanusException on error
     */
    X509EncodedKeySpec getX509Encoding(GordianKeyPair pKeyPair) throws OceanusException;

    /**
     * Derive the public-only keyPair from the X509 encoding.
     * @param pPublicKeySpec the publicKeySpec
     * @return the derived public-only keyPair
     * @throws OceanusException on error
     */
    GordianKeyPair derivePublicOnlyKeyPair(X509EncodedKeySpec pPublicKeySpec) throws OceanusException;
}
