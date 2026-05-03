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
package io.github.tonywasher.joceanus.gordianknot.impl.jca;

import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementKDF;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementType;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree.GordianCoreAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaAgreement.JcaAnonEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaAgreement.JcaBasicEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaAgreement.JcaMQVEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaAgreement.JcaNewHopeEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaAgreement.JcaPostQuantumEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaAgreement.JcaSM2Engine;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaAgreement.JcaUnifiedEngine;

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
        final GordianCoreAgreementSpec mySpec = (GordianCoreAgreementSpec) pSpec;
        return switch (pSpec.getKeyPairSpec().getKeyPairType()) {
            case EC, GOST, DSTU -> getECEngine(mySpec);
            case SM2 -> mySpec.getAgreementType() == GordianAgreementType.SM2
                    ? getSM2Engine(mySpec) : getECEngine(mySpec);
            case DH -> getDHEngine(mySpec);
            case NEWHOPE -> getNHEngine(mySpec);
            case CMCE, FRODO, SABER, MLKEM, HQC, BIKE, NTRU, NTRUPLUS, NTRUPRIME -> getPostQuantumEngine(mySpec);
            case XDH -> getXDHEngine(mySpec);
            default -> super.createEngine(pSpec);
        };
    }

    /**
     * Create the PostQuantum Agreement.
     *
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianCoreAgreementEngine getPostQuantumEngine(final GordianCoreAgreementSpec pAgreementSpec) throws GordianException {
        return new JcaPostQuantumEngine(this, pAgreementSpec, JcaAgreement.getJavaKeyGenerator(pAgreementSpec.getKeyPairSpec()));
    }

    /**
     * Create the NewHope Agreement.
     *
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianCoreAgreementEngine getNHEngine(final GordianCoreAgreementSpec pAgreementSpec) throws GordianException {
        return new JcaNewHopeEngine(this, pAgreementSpec, JcaAgreement.getJavaKeyAgreement("NH", true));
    }

    /**
     * Create the SM2 Agreement.
     *
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianCoreAgreementEngine getSM2Engine(final GordianCoreAgreementSpec pAgreementSpec) throws GordianException {
        return new JcaSM2Engine(this, pAgreementSpec, JcaAgreement.getJavaKeyAgreement("SM2", false));
    }

    /**
     * Create the DH Agreement.
     *
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianCoreAgreementEngine getDHEngine(final GordianCoreAgreementSpec pAgreementSpec) throws GordianException {
        return switch (pAgreementSpec.getAgreementType()) {
            case ANON -> new JcaAnonEngine(this, pAgreementSpec,
                    JcaAgreement.getJavaKeyAgreement(JcaAgreement.getFullAgreementName(DH_ALGO, pAgreementSpec), false));
            case SIGNED, BASIC -> new JcaBasicEngine(this, pAgreementSpec,
                    JcaAgreement.getJavaKeyAgreement(JcaAgreement.getFullAgreementName(DH_ALGO, pAgreementSpec), false));
            case UNIFIED -> new JcaUnifiedEngine(this, pAgreementSpec,
                    JcaAgreement.getJavaKeyAgreement(JcaAgreement.getFullAgreementName(DH_ALGO + "U", pAgreementSpec), false));
            case MQV -> new JcaMQVEngine(this, pAgreementSpec,
                    JcaAgreement.getJavaKeyAgreement(JcaAgreement.getFullAgreementName("MQV", pAgreementSpec), false));
            default -> throw new GordianDataException(GordianBaseData.getInvalidText(pAgreementSpec));
        };
    }

    /**
     * Create the EC Agreement.
     *
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianCoreAgreementEngine getECEngine(final GordianCoreAgreementSpec pAgreementSpec) throws GordianException {
        return switch (pAgreementSpec.getAgreementType()) {
            case ANON -> new JcaAnonEngine(this, pAgreementSpec,
                    JcaAgreement.getJavaKeyAgreement(JcaAgreement.getFullAgreementName(ECCDH_ALGO, pAgreementSpec), false));
            case SIGNED, BASIC -> new JcaBasicEngine(this, pAgreementSpec,
                    JcaAgreement.getJavaKeyAgreement(JcaAgreement.getFullAgreementName(ECCDH_ALGO, pAgreementSpec), false));
            case UNIFIED -> new JcaUnifiedEngine(this, pAgreementSpec,
                    JcaAgreement.getJavaKeyAgreement(JcaAgreement.getFullAgreementName(ECCDH_ALGO + "U", pAgreementSpec), false));
            case MQV -> new JcaMQVEngine(this, pAgreementSpec,
                    JcaAgreement.getJavaKeyAgreement(JcaAgreement.getFullAgreementName("ECMQV", pAgreementSpec), false));
            case SM2 -> getSM2Engine(pAgreementSpec);
            default -> throw new GordianDataException(GordianBaseData.getInvalidText(pAgreementSpec));
        };
    }

    /**
     * Create the XDH Agreement.
     *
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianCoreAgreementEngine getXDHEngine(final GordianCoreAgreementSpec pAgreementSpec) throws GordianException {
        return switch (pAgreementSpec.getAgreementType()) {
            case ANON -> new JcaAnonEngine(this, pAgreementSpec, null);
            case SIGNED, BASIC -> new JcaBasicEngine(this, pAgreementSpec, null);
            case UNIFIED -> new JcaUnifiedEngine(this, pAgreementSpec, null);
            default -> throw new GordianDataException(GordianBaseData.getInvalidText(pAgreementSpec));
        };
    }

    @Override
    protected boolean validAgreementSpec(final GordianAgreementSpec pSpec) {
        /* validate the agreementSpec */
        if (!super.validAgreementSpec(pSpec)) {
            return false;
        }

        /* Only allow SM2 for NoKDF */
        final GordianAgreementType myType = pSpec.getAgreementType();
        if (GordianAgreementType.SM2.equals(myType)) {
            return GordianAgreementKDF.NONE.equals(pSpec.getKDFType())
                    && !GordianKeyPairType.GOST.equals(pSpec.getKeyPairSpec().getKeyPairType())
                    && !pSpec.withConfirm();
        }

        /* Switch on KeyType */
        return switch (pSpec.getKeyPairSpec().getKeyPairType()) {
            case NEWHOPE, CMCE, FRODO, SABER, MLKEM, HQC, BIKE, NTRU, NTRUPLUS, NTRUPRIME, COMPOSITE -> true;
            case EC, GOST, DSTU, SM2, DH -> !GordianAgreementType.KEM.equals(myType);
            case XDH -> !GordianAgreementType.KEM.equals(myType)
                    && !GordianAgreementType.MQV.equals(myType);
            default -> false;
        };
    }
}
