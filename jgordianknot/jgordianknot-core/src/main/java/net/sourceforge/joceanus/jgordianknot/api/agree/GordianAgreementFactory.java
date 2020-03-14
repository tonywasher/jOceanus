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
package net.sourceforge.joceanus.jgordianknot.api.agree;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
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
    GordianAgreement createAgreement(GordianAgreementSpec pSpec) throws OceanusException;

    /**
     * Obtain predicate for keyAgreement.
     * @return the predicate
     */
    Predicate<GordianAgreementSpec> supportedAgreements();

    /**
     * Obtain a list of supported agreementSpecs.
     * @param pKeyType the keyType
     * @return the list of supported agreementSpecs.
     */
    default List<GordianAgreementSpec> listAllSupportedAgreements(final GordianAsymKeyType pKeyType) {
        return GordianAgreementSpec.listPossibleAgreements(pKeyType)
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
                                                 GordianAgreementSpec pAgreementSpec) {
        return validAgreementSpecForKeySpec(pKeyPair.getKeySpec(), pAgreementSpec);
    }

    /**
     * Check AgreementSpec and KeySpec combination.
     * @param pKeySpec the keySpec
     * @param pAgreementSpec the macSpec
     * @return true/false
     */
    boolean validAgreementSpecForKeySpec(GordianAsymKeySpec pKeySpec,
                                         GordianAgreementSpec pAgreementSpec);

    /**
     * Obtain a list of supported agreementSpecs.
     * @param pKeyPair the keyPair
     * @return the list of supported agreementSpecs.
     */
    default List<GordianAgreementSpec> listAllSupportedAgreements(final GordianKeyPair pKeyPair) {
        return listAllSupportedAgreements(pKeyPair.getKeySpec());
    }

    /**
     * Obtain a list of supported agreementSpecs.
     * @param pKeySpec the keySpec
     * @return the list of supported agreementSpecs.
     */
    default List<GordianAgreementSpec> listAllSupportedAgreements(final GordianAsymKeySpec pKeySpec) {
        return GordianAgreementSpec.listPossibleAgreements(pKeySpec.getKeyType())
                .stream()
                .filter(s -> validAgreementSpecForKeySpec(pKeySpec, s))
                .collect(Collectors.toList());
    }
}
