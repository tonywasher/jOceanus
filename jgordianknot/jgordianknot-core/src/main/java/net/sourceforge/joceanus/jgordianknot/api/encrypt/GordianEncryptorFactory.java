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
package net.sourceforge.joceanus.jgordianknot.api.encrypt;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot EncryptorFactory API.
 */
public interface GordianEncryptorFactory {
    /**
     * Create Encryptor.
     * @param pSpec the encryptorSpec
     * @return the Encryptor
     * @throws OceanusException on error
     */
    GordianEncryptor createEncryptor(GordianEncryptorSpec pSpec) throws OceanusException;

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
    default List<GordianEncryptorSpec> listAllSupportedEncryptors(final GordianAsymKeyType pKeyType) {
        return GordianEncryptorSpec.listPossibleEncryptors(pKeyType)
                .stream()
                .filter(supportedEncryptors())
                .collect(Collectors.toList());
    }
    /**
     * Check EncryptorSpec and KeyPair combination.
     * @param pKeyPair the keyPair
     * @param pEncryptorSpec the macSpec
     * @return true/false
     */
    boolean validEncryptorSpecForKeyPair(GordianKeyPair pKeyPair,
                                         GordianEncryptorSpec pEncryptorSpec);

    /**
     * Obtain a list of supported encryptorSpecs.
     * @param pKeyPair the keyPair
     * @return the list of supported encryptorSpecs.
     */
    default List<GordianEncryptorSpec> listAllSupportedEncryptors(final GordianKeyPair pKeyPair) {
        return GordianEncryptorSpec.listPossibleEncryptors(pKeyPair.getKeySpec().getKeyType())
                .stream()
                .filter(s -> validEncryptorSpecForKeyPair(pKeyPair, s))
                .collect(Collectors.toList());
    }
}
