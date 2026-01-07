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
package net.sourceforge.joceanus.gordianknot.impl.jca;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementType;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseData;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.xagree.GordianXCoreAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.core.xagree.GordianXCoreAgreementFactory;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaXAgreement.JcaAnonXEngine;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaXAgreement.JcaBasicXEngine;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaXAgreement.JcaMQVXEngine;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaXAgreement.JcaNewHopeXEngine;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaXAgreement.JcaPostQuantumXEngine;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaXAgreement.JcaUnifiedXEngine;

/**
 * Jca Agreement Factory.
 */
public class JcaXAgreementFactory
        extends GordianXCoreAgreementFactory {
    /**
     * DH algorithm.
     */
    private static final String DH_ALGO = "DH";

    /**
     * ECCDH algorithm.
     */
    private static final String ECCDH_ALGO = "ECCDH";

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    JcaXAgreementFactory(final GordianBaseFactory pFactory) {
        super(pFactory);
    }

    @Override
    public GordianXCoreAgreementEngine createEngine(final GordianAgreementSpec pSpec) throws GordianException {
        switch (pSpec.getKeyPairSpec().getKeyPairType()) {
            case EC:
            case GOST2012:
            case DSTU4145:
            case SM2:       return getECEngine(pSpec);
            case DH:        return getDHEngine(pSpec);
            case NEWHOPE:   return getNHEngine(pSpec);
            case CMCE:
            case FRODO:
            case SABER:
            case MLKEM:
            case HQC:
            case BIKE:
            case NTRU:
            case NTRUPRIME: return getPostQuantumEngine(pSpec);
            case XDH:       return getXDHEngine(pSpec);
            case COMPOSITE:
            default:
                return super.createEngine(pSpec);
        }
    }

    /**
     * Create the PostQuantum Agreement.
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianXCoreAgreementEngine getPostQuantumEngine(final GordianAgreementSpec pAgreementSpec) throws GordianException {
        return new JcaPostQuantumXEngine(this, pAgreementSpec, JcaXAgreement.getJavaKeyGenerator(pAgreementSpec.getKeyPairSpec()));
    }

    /**
     * Create the NewHope Agreement.
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianXCoreAgreementEngine getNHEngine(final GordianAgreementSpec pAgreementSpec) throws GordianException {
        return new JcaNewHopeXEngine(this, pAgreementSpec, JcaXAgreement.getJavaKeyAgreement("NH", true));
    }

    /**
     * Create the DH Agreement.
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianXCoreAgreementEngine getDHEngine(final GordianAgreementSpec pAgreementSpec) throws GordianException {
        switch (pAgreementSpec.getAgreementType()) {
            case ANON:      return new JcaAnonXEngine(this, pAgreementSpec,
                    JcaXAgreement.getJavaKeyAgreement(JcaXAgreement.getFullAgreementName(DH_ALGO, pAgreementSpec), false));
            case SIGNED:
            case BASIC:     return new JcaBasicXEngine(this, pAgreementSpec,
                    JcaXAgreement.getJavaKeyAgreement(JcaXAgreement.getFullAgreementName(DH_ALGO, pAgreementSpec), false));
            case UNIFIED:   return new JcaUnifiedXEngine(this, pAgreementSpec,
                    JcaXAgreement.getJavaKeyAgreement(JcaXAgreement.getFullAgreementName(DH_ALGO + "U", pAgreementSpec), false));
            case MQV:       return new JcaMQVXEngine(this, pAgreementSpec,
                    JcaXAgreement.getJavaKeyAgreement(JcaXAgreement.getFullAgreementName("MQV", pAgreementSpec), false));
            default:        throw new GordianDataException(GordianBaseData.getInvalidText(pAgreementSpec));
        }
    }

    /**
     * Create the EC Agreement.
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianXCoreAgreementEngine getECEngine(final GordianAgreementSpec pAgreementSpec) throws GordianException {
        switch (pAgreementSpec.getAgreementType()) {
            case ANON:      return new JcaAnonXEngine(this, pAgreementSpec,
                    JcaXAgreement.getJavaKeyAgreement(JcaXAgreement.getFullAgreementName(ECCDH_ALGO, pAgreementSpec), false));
            case SIGNED:
            case BASIC:     return new JcaBasicXEngine(this, pAgreementSpec,
                    JcaXAgreement.getJavaKeyAgreement(JcaXAgreement.getFullAgreementName(ECCDH_ALGO, pAgreementSpec), false));
            case UNIFIED:   return new JcaUnifiedXEngine(this, pAgreementSpec,
                    JcaXAgreement.getJavaKeyAgreement(JcaXAgreement.getFullAgreementName(ECCDH_ALGO + "U", pAgreementSpec), false));
            case MQV:       return new JcaMQVXEngine(this, pAgreementSpec,
                    JcaXAgreement.getJavaKeyAgreement(JcaXAgreement.getFullAgreementName("ECMQV", pAgreementSpec), false));
            default:        throw new GordianDataException(GordianBaseData.getInvalidText(pAgreementSpec));
        }
    }

    /**
     * Create the XDH Agreement.
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianXCoreAgreementEngine getXDHEngine(final GordianAgreementSpec pAgreementSpec) throws GordianException {
        switch (pAgreementSpec.getAgreementType()) {
            case ANON:      return new JcaAnonXEngine(this, pAgreementSpec, null);
            case SIGNED:
            case BASIC:     return new JcaBasicXEngine(this, pAgreementSpec, null);
            case UNIFIED:   return new JcaUnifiedXEngine(this, pAgreementSpec, null);
            default:        throw new GordianDataException(GordianBaseData.getInvalidText(pAgreementSpec));
        }
    }

    @Override
    protected boolean validAgreementSpec(final GordianAgreementSpec pSpec) {
        /* validate the agreementSpec */
        if (!super.validAgreementSpec(pSpec)) {
            return false;
        }

        /* Disallow SM2 */
        final GordianAgreementType myType = pSpec.getAgreementType();
        if (GordianAgreementType.SM2.equals(myType)) {
            return false;
        }

        /* Switch on KeyType */
        switch (pSpec.getKeyPairSpec().getKeyPairType()) {
            case NEWHOPE:
            case CMCE:
            case FRODO:
            case SABER:
            case MLKEM:
            case HQC:
            case BIKE:
            case NTRU:
            case NTRUPRIME:
            case DH:
            case COMPOSITE:
                return true;
            case EC:
            case GOST2012:
            case DSTU4145:
            case SM2:
                return !GordianAgreementType.KEM.equals(myType);
            case RSA:
            default:
                return false;
        }
    }
}
