/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreement;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpecBuilder;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementType;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKDFType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianEdwardsElliptic;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianAgreementMessageASN1.GordianMessageType;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCompositeAgreement.GordianCompositeAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCompositeAgreement.GordianCompositeBasicAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCompositeAgreement.GordianCompositeHandshakeAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCompositeAgreement.GordianCompositeSignedAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
     * The id control.
     */
    private final AtomicInteger theNextId;

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
        theNextId = new AtomicInteger();
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    protected GordianCoreFactory getFactory() {
        return theFactory;
    }

    @Override
    public GordianAgreement createAgreement(final byte[] pClientHello) throws OceanusException {
        /* Parse the client hello message */
        final GordianAgreementMessageASN1 myASN1 = GordianAgreementMessageASN1.getInstance(pClientHello);
        myASN1.checkMessageType(GordianMessageType.CLIENTHELLO);
        final AlgorithmIdentifier myAlgId = myASN1.getAgreementId();
        final GordianAgreementSpec mySpec = getSpecForIdentifier(myAlgId);
        return createAgreement(mySpec);
    }

    /**
     * Create the BouncyCastle Agreement.
     *
     * @param pSpec the agreementSpec
     * @return the Agreement
     * @throws OceanusException on error
     */
    protected GordianAgreement getCompositeAgreement(final GordianAgreementSpec pSpec) throws OceanusException {
        switch (pSpec.getAgreementType()) {
            case KEM:
            case ANON:
                return new GordianCompositeAnonymousAgreement(getFactory(), pSpec);
            case BASIC:
                return new GordianCompositeBasicAgreement(getFactory(), pSpec);
            case SIGNED:
                return new GordianCompositeSignedAgreement(getFactory(), pSpec);
            case MQV:
            case UNIFIED:
            case SM2:
                return new GordianCompositeHandshakeAgreement(getFactory(), pSpec);
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pSpec));
        }
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
     * Obtain the nextId.
     * @return the nextId
     */
    Integer getNextId() {
        return theNextId.incrementAndGet();
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

    @Override
    public boolean validAgreementSpecForKeyPairSpec(final GordianKeyPairSpec pKeyPairSpec,
                                                    final GordianAgreementSpec pAgreementSpec) {
        /* Check that the agreementSpec is supported */
        if (!validAgreementSpec(pAgreementSpec)) {
            return false;
        }

        /* Check agreement matches keySpec */
        if (!pAgreementSpec.getKeyPairSpec().equals(pKeyPairSpec)) {
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
            /* Access the subSpecs  */
            final List<GordianAgreementSpec> mySubAgrees = GordianCompositeAgreement.getSubAgreements(pAgreementSpec);

            /* Loop through the subAgreements */
            for (GordianAgreementSpec mySpec : mySubAgrees) {
                if (!validAgreementSpecForKeyPairSpec(mySpec.getKeyPairSpec(), mySpec)) {
                    return false;
                }
            }

            /* Check confirmation */
            if (Boolean.TRUE.equals(pAgreementSpec.withConfirm())
                && !pAgreementSpec.getAgreementType().canConfirm()) {
                return false;
            }

            /* Disallow SM2 with confirm */
            return pAgreementSpec.getAgreementType() != GordianAgreementType.SM2
                    || !pAgreementSpec.withConfirm();
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
        return getAlgorithmIds().determineIdentifier(pSpec);
    }

    /**
     * Obtain AgreementSpec for Identifier.
     * @param pIdentifier the identifier.
     * @return the agreementSpec (or null if not found)
     */
    public GordianAgreementSpec getSpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
        return getAlgorithmIds().determineAgreementSpec(pIdentifier);
    }

    /**
     * Obtain the agreement algorithm Ids.
     * @return the agreement Algorithm Ids
     */
    public GordianAgreementAlgId getAlgorithmIds() {
        if (theAlgIds == null) {
            theAlgIds = new GordianAgreementAlgId(theFactory);
        }
        return theAlgIds;
    }

    @Override
    public List<GordianAgreementSpec> listAllSupportedAgreements(final GordianKeyPair pKeyPair) {
        return listAllSupportedAgreements(pKeyPair.getKeyPairSpec());
    }

    @Override
    public List<GordianAgreementSpec> listAllSupportedAgreements(final GordianKeyPairSpec pKeyPairSpec) {
        return listPossibleAgreements(pKeyPairSpec)
                .stream()
                .filter(supportedAgreements())
                .filter(s -> validAgreementSpecForKeyPairSpec(pKeyPairSpec, s))
                .collect(Collectors.toList());
    }

    /**
     * Obtain a list of all possible agreements for the keyPairSpec.
     * @param pKeyPairSpec the keyPairSpec
     * @return the list
     */
    public List<GordianAgreementSpec> listPossibleAgreements(final GordianKeyPairSpec pKeyPairSpec) {
        /* Create list */
        final List<GordianAgreementSpec> myAgreements = new ArrayList<>();

        /* Switch on keyPairType */
        switch (pKeyPairSpec.getKeyPairType()) {
            case RSA:
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.KEM));
                break;
            case CMCE:
            case FRODO:
            case SABER:
            case MLKEM:
            case HQC:
            case BIKE:
            case NTRU:
            case NTRUPRIME:
                myAgreements.add(GordianAgreementSpecBuilder.kem(pKeyPairSpec, GordianKDFType.NONE));
                break;
            case EC:
            case SM2:
            case GOST2012:
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.ANON));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.KEM));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.BASIC));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.SIGNED));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.UNIFIED));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.UNIFIED, Boolean.TRUE));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.MQV));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.MQV, Boolean.TRUE));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.SM2));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.SM2, Boolean.TRUE));
                break;
            case DH:
            case DSTU4145:
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.ANON));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.KEM));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.BASIC));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.SIGNED));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.UNIFIED));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.UNIFIED, Boolean.TRUE));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.MQV));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.MQV, Boolean.TRUE));
                break;
            case XDH:
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.ANON));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.BASIC));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.SIGNED));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.UNIFIED));
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.UNIFIED, Boolean.TRUE));
                break;
            case COMPOSITE:
                /* Loop through the possible keySpecs for the first key */
                final Iterator<GordianKeyPairSpec> myIterator = pKeyPairSpec.keySpecIterator();
                for (GordianAgreementSpec mySpec : listPossibleAgreements(myIterator.next())) {
                    final GordianAgreementSpec myTest = new GordianAgreementSpec(pKeyPairSpec, mySpec.getAgreementType(), mySpec.getKDFType(), mySpec.withConfirm());
                    if (myTest.isValid()) {
                        myAgreements.add(myTest);
                    }
                }
                break;
            default:
                break;
        }

        /* Return the list */
        return myAgreements;
    }

    /**
     * Create list of KDF variants.
     * @param pKeyPairSpec the keyPairSpec
     * @param pAgreementType the agreementType
     * @return the list
     */
    public static List<GordianAgreementSpec> listAllKDFs(final GordianKeyPairSpec pKeyPairSpec,
                                                         final GordianAgreementType pAgreementType) {
        return listAllKDFs(pKeyPairSpec, pAgreementType, Boolean.FALSE);
    }

    @Override
    public GordianAgreementSpec defaultForKeyPair(final GordianKeyPairSpec pKeySpec) {
        final Iterator<GordianAgreementSpec> myIterator = listAllSupportedAgreements(pKeySpec).iterator();
        return myIterator.hasNext() ? myIterator.next() : null;
    }

    /**
     * Create list of KDF variants.
     * @param pKeyPairSpec the keyPairSpec
     * @param pAgreementType the agreementType
     * @param pConfirm with key confirmation
     * @return the list
     */
    public static List<GordianAgreementSpec> listAllKDFs(final GordianKeyPairSpec pKeyPairSpec,
                                                         final GordianAgreementType pAgreementType,
                                                         final Boolean pConfirm) {
        /* Create list */
        final List<GordianAgreementSpec> myAgreements = new ArrayList<>();

        /* Loop through the KDFs */
        for (final GordianKDFType myKDF : GordianKDFType.values()) {
            myAgreements.add(new GordianAgreementSpec(pKeyPairSpec, pAgreementType, myKDF, pConfirm));
        }

        /* Return the list */
        return myAgreements;
    }
}
