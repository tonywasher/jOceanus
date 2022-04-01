/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyCMCEKeyPair.BouncyCMCEAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyDHKeyPair.BouncyDHAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyDHKeyPair.BouncyDHBasicAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyDHKeyPair.BouncyDHMQVAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyDHKeyPair.BouncyDHSignedAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyDHKeyPair.BouncyDHUnifiedAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECBasicAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECIESAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECMQVAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECSignedAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECUnifiedAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyFrodoKeyPair.BouncyFrodoAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyNewHopeKeyPair.BouncyNewHopeAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyRSAKeyPair.BouncyRSAEncapsulationAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncySABERKeyPair.BouncySABERAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncySM2KeyPair.BouncyECSM2Agreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyXDHKeyPair.BouncyXDHAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyXDHKeyPair.BouncyXDHBasicAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyXDHKeyPair.BouncyXDHSignedAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyXDHKeyPair.BouncyXDHUnifiedAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
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
    BouncyAgreementFactory(final BouncyFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);
    }

    @Override
    protected BouncyFactory getFactory() {
        return (BouncyFactory) super.getFactory();
    }

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
        switch (pSpec.getKeyPairSpec().getKeyPairType()) {
            case RSA:
                return new BouncyRSAEncapsulationAgreement(getFactory(), pSpec);
            case EC:
            case GOST2012:
            case DSTU4145:
            case SM2:
                return getBCECAgreement(pSpec);
            case DH:
                return getBCDHAgreement(pSpec);
            case NEWHOPE:
                return new BouncyNewHopeAgreement(getFactory(), pSpec);
            case CMCE:
                return new BouncyCMCEAgreement(getFactory(), pSpec);
            case FRODO:
                return new BouncyFrodoAgreement(getFactory(), pSpec);
            case SABER:
                return new BouncySABERAgreement(getFactory(), pSpec);
            case XDH:
                return getBCXDHAgreement(pSpec);
            case COMPOSITE:
                return getCompositeAgreement(pSpec);
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pSpec));
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
            case ANON:
                return new BouncyECAnonymousAgreement(getFactory(), pSpec);
            case BASIC:
                return new BouncyECBasicAgreement(getFactory(), pSpec);
            case SIGNED:
                return new BouncyECSignedAgreement(getFactory(), pSpec);
            case MQV:
                return new BouncyECMQVAgreement(getFactory(), pSpec);
            case UNIFIED:
                return new BouncyECUnifiedAgreement(getFactory(), pSpec);
            case SM2:
                return new BouncyECSM2Agreement(getFactory(), pSpec);
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pSpec));
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
            case ANON:
                return new BouncyDHAnonymousAgreement(getFactory(), pSpec);
            case BASIC:
                return new BouncyDHBasicAgreement(getFactory(), pSpec);
            case SIGNED:
                return new BouncyDHSignedAgreement(getFactory(), pSpec);
            case MQV:
                return new BouncyDHMQVAgreement(getFactory(), pSpec);
            case UNIFIED:
                return new BouncyDHUnifiedAgreement(getFactory(), pSpec);
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pSpec));
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
            case ANON:
                return new BouncyXDHAnonymousAgreement(getFactory(), pSpec);
            case BASIC:
                return new BouncyXDHBasicAgreement(getFactory(), pSpec);
            case SIGNED:
                return new BouncyXDHSignedAgreement(getFactory(), pSpec);
            case UNIFIED:
                return new BouncyXDHUnifiedAgreement(getFactory(), pSpec);
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pSpec));
        }
    }
}
