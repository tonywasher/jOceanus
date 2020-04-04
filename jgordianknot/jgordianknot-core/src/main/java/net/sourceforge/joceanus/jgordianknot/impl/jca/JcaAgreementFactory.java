/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.jca;

import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyAgreement;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreement;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementType;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaAgreement.JcaAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaAgreement.JcaBasicAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaAgreement.JcaEncapsulationAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaAgreement.JcaMQVAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaAgreement.JcaSignedAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaAgreement.JcaUnifiedAgreement;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
    JcaAgreementFactory(final JcaFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);
    }

    @Override
    public JcaFactory getFactory() {
        return (JcaFactory) super.getFactory();
    }

    @Override
    public GordianAgreement createAgreement(final GordianAgreementSpec pAgreementSpec) throws OceanusException {
        /* Check validity of agreement */
        checkAgreementSpec(pAgreementSpec);

        /* Create the agreement */
        return getJcaAgreement(pAgreementSpec);
    }

    /**
     * Create the Jca Agreement.
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws OceanusException on error
     */
    private GordianAgreement getJcaAgreement(final GordianAgreementSpec pAgreementSpec) throws OceanusException {
        switch (pAgreementSpec.getAsymKeyType()) {
            case NEWHOPE:
                return getNHAgreement(pAgreementSpec);
            case EC:
                return getECAgreement(pAgreementSpec);
            case DH:
                return getDHAgreement(pAgreementSpec);
            case XDH:
                return getXDHAgreement(pAgreementSpec);
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pAgreementSpec.getAsymKeyType()));
        }
    }
    /**
     * Create the NewHope Agreement.
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws OceanusException on error
     */
    private GordianAgreement getNHAgreement(final GordianAgreementSpec pAgreementSpec) throws OceanusException {
        return new JcaEncapsulationAgreement(getFactory(), pAgreementSpec, getJavaKeyAgreement("NH", true));
    }

    /**
     * Create the EC Agreement.
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws OceanusException on error
     */
    private GordianAgreement getECAgreement(final GordianAgreementSpec pAgreementSpec) throws OceanusException {
        switch (pAgreementSpec.getAgreementType()) {
            case ANON:
                return new JcaAnonymousAgreement(getFactory(), pAgreementSpec, getJavaKeyAgreement(getFullAgreementName(ECCDH_ALGO, pAgreementSpec), false));
            case BASIC:
                return new JcaBasicAgreement(getFactory(), pAgreementSpec, getJavaKeyAgreement(getFullAgreementName(ECCDH_ALGO, pAgreementSpec), false));
            case SIGNED:
                return new JcaSignedAgreement(getFactory(), pAgreementSpec, getJavaKeyAgreement(getFullAgreementName(ECCDH_ALGO, pAgreementSpec), false));
            case UNIFIED:
                return new JcaUnifiedAgreement(getFactory(), pAgreementSpec, getJavaKeyAgreement(getFullAgreementName(ECCDH_ALGO + "U", pAgreementSpec), false));
            case MQV:
                return new JcaMQVAgreement(getFactory(), pAgreementSpec, getJavaKeyAgreement(getFullAgreementName("ECMQV", pAgreementSpec), false));
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pAgreementSpec));
        }
    }

    /**
     * Create the DH Agreement.
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws OceanusException on error
     */
    private GordianAgreement getDHAgreement(final GordianAgreementSpec pAgreementSpec) throws OceanusException {
        switch (pAgreementSpec.getAgreementType()) {
            case ANON:
                return new JcaAnonymousAgreement(getFactory(), pAgreementSpec, getJavaKeyAgreement(getFullAgreementName(DH_ALGO, pAgreementSpec), false));
            case BASIC:
                return new JcaBasicAgreement(getFactory(), pAgreementSpec, getJavaKeyAgreement(getFullAgreementName(DH_ALGO, pAgreementSpec), false));
            case SIGNED:
                return new JcaSignedAgreement(getFactory(), pAgreementSpec, getJavaKeyAgreement(getFullAgreementName(DH_ALGO, pAgreementSpec), false));
            case UNIFIED:
                return new JcaUnifiedAgreement(getFactory(), pAgreementSpec, getJavaKeyAgreement(getFullAgreementName(DH_ALGO + "U", pAgreementSpec), false));
            case MQV:
                return new JcaMQVAgreement(getFactory(), pAgreementSpec, getJavaKeyAgreement(getFullAgreementName("MQV", pAgreementSpec), false));
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pAgreementSpec));
        }
    }

    /**
     * Create the XDH Agreement.
     * @param pAgreementSpec the agreementSpec
     * @return the Agreement
     * @throws OceanusException on error
     */
    private GordianAgreement getXDHAgreement(final GordianAgreementSpec pAgreementSpec) throws OceanusException {
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

    /**
     * Obtain the agreement name.
     * @param pBase the base agreement
     * @param pAgreementSpec the agreementSpec
     * @return the full agreement name
     * @throws OceanusException on error
     */
    static String getFullAgreementName(final String pBase,
                                       final GordianAgreementSpec pAgreementSpec) throws OceanusException {
        switch (pAgreementSpec.getKDFType()) {
            case NONE:
                return pBase;
            case SHA256KDF:
                return pBase + "withSHA256KDF";
            case SHA512KDF:
                return pBase + "withSHA512KDF";
            case SHA256CKDF:
                return pBase + "withSHA256CKDF";
            case SHA512CKDF:
                return pBase + "withSHA512CKDF";
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pAgreementSpec));
        }
    }

    /**
     * Create the BouncyCastle KeyFactory via JCA.
     * @param pAlgorithm the Algorithm
     * @param postQuantum is this a postQuantum algorithm?
     * @return the KeyFactory
     * @throws OceanusException on error
     */
    static KeyAgreement getJavaKeyAgreement(final String pAlgorithm,
                                            final boolean postQuantum) throws OceanusException {
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

    @Override
    protected boolean validAgreementSpec(final GordianAgreementSpec pSpec) {
        /* validate the agreementSpec */
        if (!super.validAgreementSpec(pSpec)) {
            return false;
        }

        /* Switch on KeyType */
        final GordianAgreementType myType = pSpec.getAgreementType();
        switch (pSpec.getAsymKeyType()) {
            case NEWHOPE:
                return true;
            case EC:
                return !GordianAgreementType.KEM.equals(myType);
            case DH:
                return !GordianAgreementType.KEM.equals(myType)
                        && !GordianAgreementType.SM2.equals(myType);
            case XDH:
                return !GordianAgreementType.KEM.equals(myType)
                        && !GordianAgreementType.MQV.equals(myType)
                        && !GordianAgreementType.SM2.equals(myType);
            case RSA:
            case DSTU4145:
            case GOST2012:
            case SM2:
            default:
                return false;
        }
    }
}
