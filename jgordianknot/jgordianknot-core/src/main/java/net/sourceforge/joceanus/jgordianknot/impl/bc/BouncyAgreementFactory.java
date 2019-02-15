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
package net.sourceforge.joceanus.jgordianknot.impl.bc;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreement;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyDHAsymKey.BouncyDHBasicAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyDHAsymKey.BouncyDHEncapsulationAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyDHAsymKey.BouncyDHMQVAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyDHAsymKey.BouncyDHUnifiedAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEllipticAsymKey.BouncyECBasicAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEllipticAsymKey.BouncyECIESAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEllipticAsymKey.BouncyECMQVAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEllipticAsymKey.BouncyECSM2Agreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEllipticAsymKey.BouncyECUnifiedAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyNewHopeAsymKey.BouncyNewHopeAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyRSAAsymKey.BouncyRSAEncapsulationAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyXDHAsymKey.BouncyXDHBasicAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyXDHAsymKey.BouncyXDHUnifiedAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Bouncy Agreement Factory.
 */
public class BouncyAgreementFactory
        extends GordianCoreAgreementFactory {
    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    public BouncyAgreementFactory(final BouncyFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);
    }

    @Override
    protected BouncyFactory getFactory() { return (BouncyFactory) super.getFactory(); }

    @Override
    public GordianAgreement createAgreement(final GordianAgreementSpec pAgreementSpec) throws OceanusException {
        /* Check validity of Agreement */
        checkAgreementSpec(pAgreementSpec);

        /* Create the agreement */
        return getBCAgreement(pAgreementSpec);
    }

    /**
     * Create the BouncyCastle Agreement.
     *
     * @param pSpec the agreementSpec
     * @return the Agreement
     * @throws OceanusException on error
     */
    private GordianAgreement getBCAgreement(final GordianAgreementSpec pSpec) throws OceanusException {
        switch (pSpec.getAsymKeyType()) {
            case RSA:
                return new BouncyRSAEncapsulationAgreement(getFactory(), pSpec);
            case EC:
            case GOST2012:
            case DSTU4145:
            case SM2:
                return getBCECAgreement(pSpec);
            case DIFFIEHELLMAN:
                return getBCDHAgreement(pSpec);
            case NEWHOPE:
                return new BouncyNewHopeAgreement(getFactory(), pSpec);
            case X25519:
            case X448:
                return getBCXDHAgreement(pSpec);
            default:
                throw new GordianDataException(BouncyFactory.getInvalidText(pSpec));
        }
    }

    /**
     * Create the BouncyCastle EC Agreement.
     *
     * @param pSpec the agreementSpec
     * @return the Agreement
     * @throws OceanusException on error
     */
    private GordianAgreement getBCECAgreement(final GordianAgreementSpec pSpec) throws OceanusException {
        switch (pSpec.getAgreementType()) {
            case KEM:
                return new BouncyECIESAgreement(getFactory(), pSpec);
            case BASIC:
                return new BouncyECBasicAgreement(getFactory(), pSpec);
            case MQV:
                return new BouncyECMQVAgreement(getFactory(), pSpec);
            case UNIFIED:
                return new BouncyECUnifiedAgreement(getFactory(), pSpec);
            case SM2:
                return new BouncyECSM2Agreement(getFactory(), pSpec);
            default:
                throw new GordianDataException(BouncyFactory.getInvalidText(pSpec));
        }
    }

    /**
     * Create the BouncyCastle DH Agreement.
     *
     * @param pSpec the agreementSpec
     * @return the Agreement
     * @throws OceanusException on error
     */
    private GordianAgreement getBCDHAgreement(final GordianAgreementSpec pSpec) throws OceanusException {
        switch (pSpec.getAgreementType()) {
            case KEM:
                return new BouncyDHEncapsulationAgreement(getFactory(), pSpec);
            case BASIC:
                return new BouncyDHBasicAgreement(getFactory(), pSpec);
            case MQV:
                return new BouncyDHMQVAgreement(getFactory(), pSpec);
            case UNIFIED:
                return new BouncyDHUnifiedAgreement(getFactory(), pSpec);
            default:
                throw new GordianDataException(BouncyFactory.getInvalidText(pSpec));
        }
    }

    /**
     * Create the BouncyCastle XDH Agreement.
     *
     * @param pSpec the agreementSpec
     * @return the Agreement
     * @throws OceanusException on error
     */
    private GordianAgreement getBCXDHAgreement(final GordianAgreementSpec pSpec) throws OceanusException {
        switch (pSpec.getAgreementType()) {
            case BASIC:
                return new BouncyXDHBasicAgreement(getFactory(), pSpec);
            case UNIFIED:
                return new BouncyXDHUnifiedAgreement(getFactory(), pSpec);
            default:
                throw new GordianDataException(BouncyFactory.getInvalidText(pSpec));
        }
    }

}
