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
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianNTRUPrimeSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import net.sourceforge.joceanus.gordianknot.impl.core.xagree.GordianXCoreAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.core.xagree.GordianXCoreAgreementFactory;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaXAgreement.JcaNewHopeXAgreement;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaXAgreement.JcaPostQuantumXAgreement;

import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

/**
 * Jca Agreement Factory.
 */
public class JcaXAgreementFactory
        extends GordianXCoreAgreementFactory {
    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    JcaXAgreementFactory(final JcaFactory pFactory) {
        super(pFactory);
    }

    @Override
    public GordianXCoreAgreementEngine createEngine(final GordianAgreementSpec pSpec) throws GordianException {
        switch (pSpec.getKeyPairSpec().getKeyPairType()) {
            //case EC:
            //case GOST2012:
            //case DSTU4145:
            //case SM2:
            //    return getBCECEngine(pSpec);
            //case DH:
            //    return getBCDHEngine(pSpec);
            case NEWHOPE:   return getNHAgreement(pSpec);
            case CMCE:
            case FRODO:
            case SABER:
            case MLKEM:
            case HQC:
            case BIKE:
            case NTRU:
            case NTRUPRIME: return getPostQuantumAgreement(pSpec);
            //case XDH:
            //    return getBCXDHEngine(pSpec);
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
    private GordianXCoreAgreementEngine getPostQuantumAgreement(final GordianAgreementSpec pAgreementSpec) throws GordianException {
        return new JcaPostQuantumXAgreement(this, pAgreementSpec, getJavaKeyGenerator(pAgreementSpec.getKeyPairSpec()));
    }

    /**
     * Create the NewHope Agreement.
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianXCoreAgreementEngine getNHAgreement(final GordianAgreementSpec pAgreementSpec) throws GordianException {
        return new JcaNewHopeXAgreement(this, pAgreementSpec, getJavaKeyAgreement("NH", true));
    }

    /**
     * Create the BouncyCastle KeyFactory via JCA.
     * @param pAlgorithm the Algorithm
     * @param postQuantum is this a postQuantum algorithm?
     * @return the KeyFactory
     * @throws GordianException on error
     */
    private static KeyAgreement getJavaKeyAgreement(final String pAlgorithm,
                                                    final boolean postQuantum) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Return a KeyAgreement for the algorithm */
            return KeyAgreement.getInstance(pAlgorithm, postQuantum
                    ? JcaFactory.BCPQPROV
                    : JcaFactory.BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create KeyAgreement", e);
        }
    }

    /**
     * Create the BouncyCastle KeyGenerator via JCA.
     * @param pSpec the KeySpec
     * @return the KeyFactory
     * @throws GordianException on error
     */
    private static KeyGenerator getJavaKeyGenerator(final GordianKeyPairSpec pSpec) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Determine the algorithm name */
            String myName = pSpec.getKeyPairType().toString();
            switch (pSpec.getKeyPairType()) {
                case NTRUPRIME:
                    final GordianNTRUPrimeSpec myNTRUSpec = pSpec.getNTRUPrimeKeySpec();
                    myName = myNTRUSpec.getType() + "PRIME";
                    break;
                case MLKEM:
                    myName = "ML-KEM";
                    break;
                default:
                    break;
            }

            /* Determine source of keyGenerator */
            final Provider myProvider = pSpec.getKeyPairType().isStandardJca() ? JcaFactory.BCPROV : JcaFactory.BCPQPROV;

            /* Return a KeyAgreement for the algorithm */
            return KeyGenerator.getInstance(myName, myProvider);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create KeyGenerator", e);
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
