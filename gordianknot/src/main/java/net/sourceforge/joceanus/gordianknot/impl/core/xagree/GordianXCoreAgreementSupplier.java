/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.xagree;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.xagree.GordianXAgreement;
import net.sourceforge.joceanus.gordianknot.api.xagree.GordianXAgreementFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/**
 * Factory supplier interface.
 */
public interface GordianXCoreAgreementSupplier
        extends GordianXAgreementFactory {
    /**
     * Obtain the factory.
     * @return the factory
     */
    GordianBaseFactory getFactory();

    /**
     * Check the agreementSpec.
     * @param pAgreementSpec the agreementSpec
     * @throws GordianException on error
     */
    void checkAgreementSpec(GordianAgreementSpec pAgreementSpec) throws GordianException;

    /**
     * Obtain Identifier for AgreementSpec.
     * @param pSpec the agreementSpec.
     * @return the Identifier
     */
    AlgorithmIdentifier getIdentifierForSpec(GordianAgreementSpec pSpec);

    /**
     * Obtain AgreementSpec for Identifier.
     * @param pIdentifier the identifier.
     * @return the agreementSpec (or null if not found)
     */
    GordianAgreementSpec getSpecForIdentifier(AlgorithmIdentifier pIdentifier);

    /**
     * Obtain Identifier for ResultType.
     * @param pResultType the resultType.
     * @return the Identifier
     * @throws GordianException on error
     */
    AlgorithmIdentifier getIdentifierForResultType(Object pResultType) throws GordianException;

    /**
     * Obtain AgreementSpec for Identifier.
     * @param pIdentifier the identifier.
     * @return the resultType
     * @throws GordianException on error
     */
    Object getResultTypeForIdentifier(AlgorithmIdentifier pIdentifier) throws GordianException;

    /**
     * Obtain the next Id.
     * @return the nextId
     */
    Long getNextId();

    /**
     * Store agreement under id.
     * @param pId the id
     * @param pAgreement the agreement
     */
    void storeAgreement(Long pId,
                        GordianXAgreement pAgreement);

    /**
     * Remove agreement under id.
     * @param pId the id
     */
    void removeAgreement(Long pId);
}
