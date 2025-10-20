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
package net.sourceforge.joceanus.gordianknot.api.encrypt;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairType;

import java.util.List;
import java.util.function.Predicate;

/**
 * GordianKnot EncryptorFactory API.
 */
public interface GordianEncryptorFactory {
    /**
     * Create keyPairEncryptor.
     * @param pSpec the encryptorSpec
     * @return the Encryptor
     * @throws GordianException on error
     */
    GordianEncryptor createEncryptor(GordianEncryptorSpec pSpec) throws GordianException;

    /**
     * Obtain predicate for Encryptor.
     * @return the predicate
     */
    Predicate<GordianEncryptorSpec> supportedEncryptors();

    /**
     * Obtain a list of supported encryptorSpecs.
     * @param pKeyType the keyType
     * @return the list of supported encryptorSpecs.
     */
    default List<GordianEncryptorSpec> listAllSupportedEncryptors(final GordianKeyPairType pKeyType) {
        return listPossibleEncryptors(pKeyType)
                .stream()
                .filter(supportedEncryptors())
                .toList();
    }

    /**
     * Check EncryptorSpec and KeyPair combination.
     * @param pKeyPair the keyPair
     * @param pEncryptorSpec the macSpec
     * @return true/false
     */
    default boolean validEncryptorSpecForKeyPair(final GordianKeyPair pKeyPair,
                                                 final GordianEncryptorSpec pEncryptorSpec) {
        return validEncryptorSpecForKeyPairSpec(pKeyPair.getKeyPairSpec(), pEncryptorSpec);
    }

    /**
     * Check EncryptorSpec and KeyPairSpec combination.
     * @param pKeyPairSpec the keyPairSpec
     * @param pEncryptorSpec the macSpec
     * @return true/false
     */
    boolean validEncryptorSpecForKeyPairSpec(GordianKeyPairSpec pKeyPairSpec,
                                             GordianEncryptorSpec pEncryptorSpec);

    /**
     * Obtain a list of supported encryptorSpecs.
     * @param pKeyPair the keyPair
     * @return the list of supported encryptorSpecs.
     */
    List<GordianEncryptorSpec> listAllSupportedEncryptors(GordianKeyPair pKeyPair);

    /**
     * Obtain a list of supported encryptorSpecs.
     * @param pKeyPairSpec the keySpec
     * @return the list of supported encryptorSpecs.
     */
    List<GordianEncryptorSpec> listAllSupportedEncryptors(GordianKeyPairSpec pKeyPairSpec);

    /**
     * Obtain a list of all possible encryptors for the keyType.
     * @param pKeyPairType the keyPairType
     * @return the list
     */
    List<GordianEncryptorSpec> listPossibleEncryptors(GordianKeyPairType pKeyPairType);

    /**
     * Create default signatureSpec for key.
     * @param pKeySpec the keySpec
     * @return the SignatureSpec
     */
    GordianEncryptorSpec defaultForKeyPair(GordianKeyPairSpec pKeySpec);
}
