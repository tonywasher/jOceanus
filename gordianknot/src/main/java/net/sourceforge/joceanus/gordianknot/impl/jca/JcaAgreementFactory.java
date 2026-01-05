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

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreement;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementType;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaAgreement.JcaAnonymousAgreement;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaAgreement.JcaBasicAgreement;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaAgreement.JcaEncapsulationAgreement;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaAgreement.JcaMQVAgreement;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaAgreement.JcaPostQuantumAgreement;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaAgreement.JcaSignedAgreement;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaAgreement.JcaUnifiedAgreement;

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
    JcaAgreementFactory(final GordianCoreFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);
    }

    @Override
    public GordianAgreement createAgreement(final GordianAgreementSpec pAgreementSpec) throws GordianException {
        /* Check validity of agreement */
        checkAgreementSpec(pAgreementSpec);

        /* Create the agreement */
        return getJcaAgreement(pAgreementSpec);
    }

    /**
     * Create the Jca Agreement.
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianAgreement getJcaAgreement(final GordianAgreementSpec pAgreementSpec) throws GordianException {
        switch (pAgreementSpec.getKeyPairSpec().getKeyPairType()) {
            case CMCE:
            case FRODO:
            case SABER:
            case MLKEM:
            case HQC:
            case BIKE:
            case NTRU:
            case NTRUPRIME:
                return getPostQuantumAgreement(pAgreementSpec);
            case NEWHOPE:
                return getNHAgreement(pAgreementSpec);
            case EC:
            case GOST2012:
            case DSTU4145:
            case SM2:
                return getECAgreement(pAgreementSpec);
            case DH:
                return getDHAgreement(pAgreementSpec);
            case XDH:
                return getXDHAgreement(pAgreementSpec);
            case COMPOSITE:
                return getCompositeAgreement(pAgreementSpec);
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pAgreementSpec.getKeyPairSpec()));
        }
    }

    /**
     * Create the PostQuantum Agreement.
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianAgreement getPostQuantumAgreement(final GordianAgreementSpec pAgreementSpec) throws GordianException {
        return new JcaPostQuantumAgreement(getFactory(), pAgreementSpec, JcaAgreement.getJavaKeyGenerator(pAgreementSpec.getKeyPairSpec()));
    }

    /**
     * Create the NewHope Agreement.
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianAgreement getNHAgreement(final GordianAgreementSpec pAgreementSpec) throws GordianException {
        return new JcaEncapsulationAgreement(getFactory(), pAgreementSpec, JcaAgreement.getJavaKeyAgreement("NH", true));
    }

    /**
     * Create the EC Agreement.
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianAgreement getECAgreement(final GordianAgreementSpec pAgreementSpec) throws GordianException {
        switch (pAgreementSpec.getAgreementType()) {
            case ANON:
                return new JcaAnonymousAgreement(getFactory(), pAgreementSpec,
                        JcaAgreement.getJavaKeyAgreement(JcaAgreement.getFullAgreementName(ECCDH_ALGO, pAgreementSpec), false));
            case BASIC:
                return new JcaBasicAgreement(getFactory(), pAgreementSpec,
                        JcaAgreement.getJavaKeyAgreement(JcaAgreement.getFullAgreementName(ECCDH_ALGO, pAgreementSpec), false));
            case SIGNED:
                return new JcaSignedAgreement(getFactory(), pAgreementSpec,
                        JcaAgreement.getJavaKeyAgreement(JcaAgreement.getFullAgreementName(ECCDH_ALGO, pAgreementSpec), false));
            case UNIFIED:
                return new JcaUnifiedAgreement(getFactory(), pAgreementSpec,
                        JcaAgreement.getJavaKeyAgreement(JcaAgreement.getFullAgreementName(ECCDH_ALGO + "U", pAgreementSpec), false));
            case MQV:
                return new JcaMQVAgreement(getFactory(), pAgreementSpec,
                        JcaAgreement.getJavaKeyAgreement(JcaAgreement.getFullAgreementName("ECMQV", pAgreementSpec), false));
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pAgreementSpec));
        }
    }

    /**
     * Create the DH Agreement.
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianAgreement getDHAgreement(final GordianAgreementSpec pAgreementSpec) throws GordianException {
        switch (pAgreementSpec.getAgreementType()) {
            case ANON:
                return new JcaAnonymousAgreement(getFactory(), pAgreementSpec,
                        JcaAgreement.getJavaKeyAgreement(JcaAgreement.getFullAgreementName(DH_ALGO, pAgreementSpec), false));
            case BASIC:
                return new JcaBasicAgreement(getFactory(), pAgreementSpec,
                        JcaAgreement.getJavaKeyAgreement(JcaAgreement.getFullAgreementName(DH_ALGO, pAgreementSpec), false));
            case SIGNED:
                return new JcaSignedAgreement(getFactory(), pAgreementSpec,
                        JcaAgreement.getJavaKeyAgreement(JcaAgreement.getFullAgreementName(DH_ALGO, pAgreementSpec), false));
            case UNIFIED:
                return new JcaUnifiedAgreement(getFactory(), pAgreementSpec,
                        JcaAgreement.getJavaKeyAgreement(JcaAgreement.getFullAgreementName(DH_ALGO + "U", pAgreementSpec), false));
            case MQV:
                return new JcaMQVAgreement(getFactory(), pAgreementSpec,
                        JcaAgreement.getJavaKeyAgreement(JcaAgreement.getFullAgreementName("MQV", pAgreementSpec), false));
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pAgreementSpec));
        }
    }

    /**
     * Create the XDH Agreement.
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianAgreement getXDHAgreement(final GordianAgreementSpec pAgreementSpec) throws GordianException {
        switch (pAgreementSpec.getAgreementType()) {
            case ANON:
                return new JcaAnonymousAgreement(getFactory(), pAgreementSpec, null);
            case BASIC:
                return new JcaBasicAgreement(getFactory(), pAgreementSpec, null);
            case SIGNED:
                return new JcaSignedAgreement(getFactory(), pAgreementSpec, null);
            case UNIFIED:
                return new JcaUnifiedAgreement(getFactory(), pAgreementSpec, null);
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pAgreementSpec));
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
