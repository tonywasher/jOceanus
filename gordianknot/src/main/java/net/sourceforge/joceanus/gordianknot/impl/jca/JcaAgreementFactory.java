/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.gordianknot.impl.jca;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementType;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseData;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaAgreement.JcaAnonEngine;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaAgreement.JcaBasicEngine;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaAgreement.JcaMQVEngine;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaAgreement.JcaNewHopeEngine;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaAgreement.JcaPostQuantumEngine;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaAgreement.JcaUnifiedEngine;

/**
 * Jca Agreement Factory.
 */
public class JcaAgreementFactory
        extends GordianCoreAgreementFactory {
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
    JcaAgreementFactory(final GordianBaseFactory pFactory) {
        super(pFactory);
    }

    @Override
    public GordianCoreAgreementEngine createEngine(final GordianAgreementSpec pSpec) throws GordianException {
        switch (pSpec.getKeyPairSpec().getKeyPairType()) {
            case EC:
            case GOST2012:
            case DSTU4145:
            case SM2:
                return getECEngine(pSpec);
            case DH:
                return getDHEngine(pSpec);
            case NEWHOPE:
                return getNHEngine(pSpec);
            case CMCE:
            case FRODO:
            case SABER:
            case MLKEM:
            case HQC:
            case BIKE:
            case NTRU:
            case NTRUPRIME:
                return getPostQuantumEngine(pSpec);
            case XDH:
                return getXDHEngine(pSpec);
            case COMPOSITE:
            default:
                return super.createEngine(pSpec);
        }
    }

    /**
     * Create the PostQuantum Agreement.
     *
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianCoreAgreementEngine getPostQuantumEngine(final GordianAgreementSpec pAgreementSpec) throws GordianException {
        return new JcaPostQuantumEngine(this, pAgreementSpec, JcaAgreement.getJavaKeyGenerator(pAgreementSpec.getKeyPairSpec()));
    }

    /**
     * Create the NewHope Agreement.
     *
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianCoreAgreementEngine getNHEngine(final GordianAgreementSpec pAgreementSpec) throws GordianException {
        return new JcaNewHopeEngine(this, pAgreementSpec, JcaAgreement.getJavaKeyAgreement("NH", true));
    }

    /**
     * Create the DH Agreement.
     *
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianCoreAgreementEngine getDHEngine(final GordianAgreementSpec pAgreementSpec) throws GordianException {
        switch (pAgreementSpec.getAgreementType()) {
            case ANON:
                return new JcaAnonEngine(this, pAgreementSpec,
                        JcaAgreement.getJavaKeyAgreement(JcaAgreement.getFullAgreementName(DH_ALGO, pAgreementSpec), false));
            case SIGNED:
            case BASIC:
                return new JcaBasicEngine(this, pAgreementSpec,
                        JcaAgreement.getJavaKeyAgreement(JcaAgreement.getFullAgreementName(DH_ALGO, pAgreementSpec), false));
            case UNIFIED:
                return new JcaUnifiedEngine(this, pAgreementSpec,
                        JcaAgreement.getJavaKeyAgreement(JcaAgreement.getFullAgreementName(DH_ALGO + "U", pAgreementSpec), false));
            case MQV:
                return new JcaMQVEngine(this, pAgreementSpec,
                        JcaAgreement.getJavaKeyAgreement(JcaAgreement.getFullAgreementName("MQV", pAgreementSpec), false));
            default:
                throw new GordianDataException(GordianBaseData.getInvalidText(pAgreementSpec));
        }
    }

    /**
     * Create the EC Agreement.
     *
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianCoreAgreementEngine getECEngine(final GordianAgreementSpec pAgreementSpec) throws GordianException {
        switch (pAgreementSpec.getAgreementType()) {
            case ANON:
                return new JcaAnonEngine(this, pAgreementSpec,
                        JcaAgreement.getJavaKeyAgreement(JcaAgreement.getFullAgreementName(ECCDH_ALGO, pAgreementSpec), false));
            case SIGNED:
            case BASIC:
                return new JcaBasicEngine(this, pAgreementSpec,
                        JcaAgreement.getJavaKeyAgreement(JcaAgreement.getFullAgreementName(ECCDH_ALGO, pAgreementSpec), false));
            case UNIFIED:
                return new JcaUnifiedEngine(this, pAgreementSpec,
                        JcaAgreement.getJavaKeyAgreement(JcaAgreement.getFullAgreementName(ECCDH_ALGO + "U", pAgreementSpec), false));
            case MQV:
                return new JcaMQVEngine(this, pAgreementSpec,
                        JcaAgreement.getJavaKeyAgreement(JcaAgreement.getFullAgreementName("ECMQV", pAgreementSpec), false));
            default:
                throw new GordianDataException(GordianBaseData.getInvalidText(pAgreementSpec));
        }
    }

    /**
     * Create the XDH Agreement.
     *
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianCoreAgreementEngine getXDHEngine(final GordianAgreementSpec pAgreementSpec) throws GordianException {
        switch (pAgreementSpec.getAgreementType()) {
            case ANON:
                return new JcaAnonEngine(this, pAgreementSpec, null);
            case SIGNED:
            case BASIC:
                return new JcaBasicEngine(this, pAgreementSpec, null);
            case UNIFIED:
                return new JcaUnifiedEngine(this, pAgreementSpec, null);
            default:
                throw new GordianDataException(GordianBaseData.getInvalidText(pAgreementSpec));
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
            case COMPOSITE:
                return true;
            case EC:
            case GOST2012:
            case DSTU4145:
            case SM2:
            case DH:
                return !GordianAgreementType.KEM.equals(myType);
            case XDH:
                return !GordianAgreementType.KEM.equals(myType)
                        && !GordianAgreementType.MQV.equals(myType);
            case RSA:
            default:
                return false;
        }
    }
}
