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
import java.util.List;
import java.util.function.Predicate;

/**
 * KeyPair Factory API.
 */
public interface GordianKeyPairFactory {
    /**
     * Obtain keyPair generator.
     * @param pKeySpec the keySpec
     * @return the generator
     * @throws GordianException on error
     */
    GordianKeyPairGenerator getKeyPairGenerator(GordianKeyPairSpec pKeySpec) throws GordianException;

    /**
     * Determine KeySpec from PKCS8EncodedKeySpec.
     * @param pEncoded the encodedKeySpec
     * @return the keySpec
     * @throws GordianException on error
     */
    GordianKeyPairSpec determineKeyPairSpec(PKCS8EncodedKeySpec pEncoded) throws GordianException;

    /**
     * Determine KeySpec from X509EncodedKeySpec.
     * @param pEncoded the encodedKeySpec
     * @return the keySpec
     * @throws GordianException on error
     */
    GordianKeyPairSpec determineKeyPairSpec(X509EncodedKeySpec pEncoded) throws GordianException;

    /**
     * Obtain predicate for keyPairSpecs.
     * @return the predicate
     */
    Predicate<GordianKeyPairSpec> supportedKeyPairSpecs();

    /**
     * Obtain a list of supported keyPairSpecs.
     * @return the list of supported keyPairSpecs.
     */
    List<GordianKeyPairSpec> listAllSupportedKeyPairSpecs();

    /**
     * Obtain a list of supported KeyPairSpecs for a KeyPairType.
     * @param pKeyPairType the keyPairType
     * @return the list of supported asymKeySpecs.
     */
    List<GordianKeyPairSpec> listAllSupportedKeyPairSpecs(GordianKeyPairType pKeyPairType);

    /**
     * Obtain a list of all possible keyPairSpecs.
     * @return the list
     */
    List<GordianKeyPairSpec> listPossibleKeySpecs();
}
