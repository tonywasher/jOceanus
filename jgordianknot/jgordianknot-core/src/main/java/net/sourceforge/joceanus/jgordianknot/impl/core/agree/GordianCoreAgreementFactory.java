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
package net.sourceforge.joceanus.jgordianknot.impl.core.agree;

import java.util.Iterator;
import java.util.function.Predicate;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairAgreement;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairSetAgreement;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairSetAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianEdwardsElliptic;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
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
    protected GordianCoreAgreementFactory(final GordianCoreFactory pFactory) {
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
    public GordianKeyPairAgreement createKeyPairAgreement(final byte[] pClientHello) throws OceanusException {
        /* Parse the client hello message */
        final GordianAgreementClientHelloASN1 myASN1 = GordianAgreementClientHelloASN1.getInstance(pClientHello);
        final AlgorithmIdentifier myAlgId = myASN1.getAgreementId();
        final GordianKeyPairAgreementSpec mySpec = getSpecForIdentifier(myAlgId);
        return createKeyPairAgreement(mySpec);
    }

    @Override
    public Predicate<GordianKeyPairAgreementSpec> supportedKeyPairAgreements() {
        return this::validAgreementSpec;
    }

    /**
     * Check the agreementSpec.
     * @param pAgreementSpec the agreementSpec
     * @throws OceanusException on error
     */
    protected void checkAgreementSpec(final GordianKeyPairAgreementSpec pAgreementSpec) throws OceanusException {
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
    protected boolean validAgreementSpec(final GordianKeyPairAgreementSpec pSpec) {
        /* Reject invalid agreementSpec */
        if (pSpec == null || !pSpec.isValid()) {
            return false;
        }

        /* Check that spec is supported */
        return pSpec.isSupported();
    }

    @Override
    public boolean validAgreementSpecForKeyPairSpec(final GordianKeyPairSpec pKeyPairSpec,
                                                    final GordianKeyPairAgreementSpec pAgreementSpec) {
        /* Check that the agreementSpec is supported */
        if (!validAgreementSpec(pAgreementSpec)) {
            return false;
        }

        /* Check agreement matches keySpec */
        if (pAgreementSpec.getKeyPairType() != pKeyPairSpec.getKeyPairType()) {
            return false;
        }

        /* For Edwards XDH, disallow 512KDF for 25519 and 256KDF for 448 */
        if (pKeyPairSpec.getKeyPairType() == GordianKeyPairType.XDH) {
            final GordianEdwardsElliptic myEdwards = pKeyPairSpec.getEdwardsElliptic();
            switch (pAgreementSpec.getKDFType()) {
                case SHA256KDF:
                case SHA256CKDF:
                    return myEdwards.is25519();
                case SHA512KDF:
                case SHA512CKDF:
                    return !myEdwards.is25519();
                default:
                    break;
            }
        }

        /* For Composite AgreementSpec */
        if (pKeyPairSpec.getKeyPairType() == GordianKeyPairType.COMPOSITE) {
            /* Loop through the keyPairs */
            final Iterator<GordianKeyPairSpec> myIterator = pKeyPairSpec.keySpecIterator();
            while (myIterator.hasNext()) {
                final GordianKeyPairSpec mySpec = myIterator.next();
                final GordianKeyPairAgreementSpec mySubAgree
                        = new GordianKeyPairAgreementSpec(mySpec.getKeyPairType(),
                                                          pAgreementSpec.getAgreementType(),
                                                          pAgreementSpec.getKDFType(),
                                                          pAgreementSpec.withConfirm());
                if (!validAgreementSpecForKeyPairSpec(mySpec, mySubAgree)) {
                    return false;
                }
            }
        }

        /* OK */
        return true;
    }

    @Override
    public GordianKeyPairSetAgreement createKeyPairSetAgreement(final GordianKeyPairSetAgreementSpec pAgreementSpec) throws OceanusException {
        /* Check valid spec */
        if (pAgreementSpec == null || !pAgreementSpec.isValid()) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pAgreementSpec));
        }

        /* Switch on agreementType */
         switch (pAgreementSpec.getAgreementType()) {
            case ANON:
                return new GordianCoreKeyPairSetAnonymousAgreement(theFactory, pAgreementSpec);
            case SIGNED:
                return new GordianCoreKeyPairSetSignedAgreement(theFactory, pAgreementSpec);
            case UNIFIED:
                return new GordianCoreKeyPairSetHandshakeAgreement(theFactory, pAgreementSpec);
            default:
                throw new GordianLogicException(GordianCoreFactory.getInvalidText(pAgreementSpec));
        }
    }

    @Override
    public GordianKeyPairSetAgreement createKeyPairSetAgreement(final byte[] pClientHello) throws OceanusException {
        /* Parse the client hello message */
        final GordianKeyPairSetAgreeASN1 myASN1 = GordianKeyPairSetAgreeASN1.getInstance(pClientHello);
        final GordianKeyPairSetAgreementSpec mySpec = myASN1.getSpec();
        return createKeyPairSetAgreement(mySpec);
    }

    /**
     * Obtain Identifier for AgreementSpec.
     * @param pSpec the agreementSpec.
     * @return the Identifier
     */
    public AlgorithmIdentifier getIdentifierForSpec(final GordianKeyPairAgreementSpec pSpec) {
        return getAlgorithmIds().getIdentifierForSpec(pSpec);
    }

    /**
     * Obtain AgreementSpec for Identifier.
     * @param pIdentifier the identifier.
     * @return the agreementSpec (or null if not found)
     */
    public GordianKeyPairAgreementSpec getSpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
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
