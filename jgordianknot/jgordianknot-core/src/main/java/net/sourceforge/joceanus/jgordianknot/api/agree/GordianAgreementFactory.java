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
package net.sourceforge.joceanus.jgordianknot.api.agree;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
     * Check AgreementSpec and KeyPair combination.
     * @param pKeyPair the keyPair
     * @param pAgreementSpec the macSpec
     * @return true/false
     */
    boolean validAgreementSpecForKeyPair(GordianKeyPair pKeyPair,
                                         GordianAgreementSpec pAgreementSpec);

    /**
     * Obtain a list of supported agreementSpecs.
     * @param pKeyPair the keyPair
     * @return the list of supported agreementSpecs.
     */
    default List<GordianAgreementSpec> listAllSupportedAgreementSpecs(final GordianKeyPair pKeyPair) {
        return GordianAgreementSpec.listPossibleAgreements(pKeyPair)
                .stream()
                .filter(s -> validAgreementSpecForKeyPair(pKeyPair, s))
                .collect(Collectors.toList());
    }
}
