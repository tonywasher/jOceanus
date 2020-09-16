/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.agree;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot AgreementFactory API.
 */
public interface GordianAgreementFactory {
    /**
     * Create Agreement.
     * @param pSpec the agreementSpec
     * @return the Agreement
     * @throws OceanusException on error
     */
    GordianKeyPairAgreement createAgreement(GordianKeyPairAgreementSpec pSpec) throws OceanusException;

    /**
     * Create Agreement for clientHello message.
     * @param pClientHello the clientHello message
     * @return the Agreement
     * @throws OceanusException on error
     */
    GordianAgreement<GordianKeyPairAgreementSpec> createAgreement(byte[] pClientHello) throws OceanusException;

    /**
     * Obtain predicate for keyAgreement.
     * @return the predicate
     */
    Predicate<GordianKeyPairAgreementSpec> supportedAgreements();

    /**
     * Obtain a list of supported agreementSpecs.
     * @param pKeyType the keyType
     * @return the list of supported agreementSpecs.
     */
    default List<GordianKeyPairAgreementSpec> listAllSupportedAgreements(final GordianKeyPairType pKeyType) {
        return GordianKeyPairAgreementSpec.listPossibleAgreements(pKeyType)
                .stream()
                .filter(supportedAgreements())
                .collect(Collectors.toList());
    }

    /**
     * Check AgreementSpec and KeyPair combination.
     * @param pKeyPair the keyPair
     * @param pAgreementSpec the macSpec
     * @return true/false
     */
    default boolean validAgreementSpecForKeyPair(GordianKeyPair pKeyPair,
                                                 GordianKeyPairAgreementSpec pAgreementSpec) {
        return validAgreementSpecForKeyPairSpec(pKeyPair.getKeyPairSpec(), pAgreementSpec);
    }

    /**
     * Check AgreementSpec and KeyPairSpec combination.
     * @param pKeyPairSpec the keyPairSpec
     * @param pAgreementSpec the agreementSpec
     * @return true/false
     */
    boolean validAgreementSpecForKeyPairSpec(GordianKeyPairSpec pKeyPairSpec,
                                             GordianKeyPairAgreementSpec pAgreementSpec);

    /**
     * Obtain a list of supported agreementSpecs.
     * @param pKeyPair the keyPair
     * @return the list of supported agreementSpecs.
     */
    default List<GordianKeyPairAgreementSpec> listAllSupportedAgreements(final GordianKeyPair pKeyPair) {
        return listAllSupportedAgreements(pKeyPair.getKeyPairSpec());
    }

    /**
     * Obtain a list of supported agreementSpecs.
     * @param pKeyPairSpec the keySpec
     * @return the list of supported agreementSpecs.
     */
    default List<GordianKeyPairAgreementSpec> listAllSupportedAgreements(final GordianKeyPairSpec pKeyPairSpec) {
        return GordianKeyPairAgreementSpec.listPossibleAgreements(pKeyPairSpec.getKeyPairType())
                .stream()
                .filter(s -> validAgreementSpecForKeyPairSpec(pKeyPairSpec, s))
                .collect(Collectors.toList());
    }
}
