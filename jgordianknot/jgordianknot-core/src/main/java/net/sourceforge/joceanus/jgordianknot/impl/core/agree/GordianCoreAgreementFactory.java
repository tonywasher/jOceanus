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
package net.sourceforge.joceanus.jgordianknot.impl.core.agree;

import java.util.function.Predicate;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementType;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot base for encryptorFactory.
 */
public abstract class GordianCoreAgreementFactory
        implements GordianAgreementFactory {
    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The algorithm Ids.
     */
    private GordianAgreementAlgId theAlgIds;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    public GordianCoreAgreementFactory(final GordianCoreFactory pFactory) {
        theFactory = pFactory;
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    protected GordianCoreFactory getFactory() {
        return theFactory;
    }

    @Override
    public Predicate<GordianAgreementSpec> supportedAgreements() {
        return this::validAgreementSpec;
    }

    /**
     * Check the agreementSpec.
     * @param pAgreementSpec the agreementSpec
     * @throws OceanusException on error
     */
    protected void checkAgreementSpec(final GordianAgreementSpec pAgreementSpec) throws OceanusException {
        /* Check validity of agreement */
        if (!validAgreementSpec(pAgreementSpec)) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pAgreementSpec));
        }
    }

    /**
     * Check AgreementSpec.
     *
     * @param pSpec the agreementSpec
     * @return true/false
     */
    protected boolean validAgreementSpec(final GordianAgreementSpec pSpec) {
        /* Reject invalid agreementSpec */
        if (pSpec == null || !pSpec.isValid()) {
            return false;
        }

        /* Check that spec is supported */
        return pSpec.isSupported();
    }

    /**
     * Check AgreementSpec and KeyPair combination.
     *
     * @param pKeyPair       the keyPair
     * @param pAgreementSpec the macSpec
     * @return true/false
     */
    public boolean validAgreementSpecForKeyPair(final GordianKeyPair pKeyPair,
                                                final GordianAgreementSpec pAgreementSpec) {
        /* Reject invalid agreementSpec */
        if (pAgreementSpec == null || !pAgreementSpec.isValid()) {
            return false;
        }

        /* Check agreement matches keyPair */
        if (pAgreementSpec.getAsymKeyType() != pKeyPair.getKeySpec().getKeyType()) {
            return false;
        }

        /* Check that the agreementSpec is supported */
        if (!validAgreementSpec(pAgreementSpec)) {
            return false;
        }

        /* Disallow MQV if group does not support it */
        final GordianAsymKeySpec myKeySpec = pKeyPair.getKeySpec();
        if (GordianAsymKeyType.DH.equals(myKeySpec.getKeyType())
                && GordianAgreementType.MQV.equals(pAgreementSpec.getAgreementType())) {
            return myKeySpec.getDHGroup().isMQV();
        }

        /* OK */
        return true;
    }

    /**
     * Obtain Identifier for AgreementSpec.
     * @param pSpec the agreementSpec.
     * @return the Identifier
     */
    public AlgorithmIdentifier getIdentifierForSpec(final GordianAgreementSpec pSpec) {
        return getAlgorithmIds().getIdentifierForSpec(pSpec);
    }

    /**
     * Obtain AgreementSpec for Identifier.
     * @param pIdentifier the identifier.
     * @return the agreementSpec (or null if not found)
     */
    public GordianAgreementSpec getSpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
        return getAlgorithmIds().getSpecForIdentifier(pIdentifier);
    }

    /**
     * Obtain the agreement algorithm Ids.
     * @return the agreement Algorithm Ids
     */
    private GordianAgreementAlgId getAlgorithmIds() {
        if (theAlgIds == null) {
            theAlgIds = new GordianAgreementAlgId(theFactory);
        }
        return theAlgIds;
    }
}
