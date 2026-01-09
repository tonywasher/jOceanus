/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.xagree;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementType;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianKDFType;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianCertificate;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianKeyPairUse;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianEdwardsElliptic;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureFactory;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.gordianknot.api.xagree.GordianXAgreement;
import net.sourceforge.joceanus.gordianknot.api.xagree.GordianXAgreementParams;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseData;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianLogicException;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Core agreement factory.
 */
public abstract class GordianXCoreAgreementFactory
        implements GordianXCoreAgreementSupplier {
    /**
     * The factory.
     */
    private final GordianBaseFactory theFactory;

    /**
     * The id cache.
     */
    private final GordianXCoreAgreementCache theCache;

    /**
     * The algorithm Ids.
     */
    private GordianXCoreAgreementAlgId theAlgIds;

    /**
     * The signer certificate.
     */
    private GordianCertificate theSignerCertificate;

    /**
     * The signatureSpec.
     */
    private GordianSignatureSpec theSignSpec;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    protected GordianXCoreAgreementFactory(final GordianBaseFactory pFactory) {
        theFactory = pFactory;
        theCache = new GordianXCoreAgreementCache(theFactory.getRandomSource());
    }

    @Override
    public GordianBaseFactory getFactory() {
        return theFactory;
    }

    @Override
    public GordianXAgreementParams newAgreementParams(final GordianAgreementSpec pSpec,
                                                      final Object pResultType) throws GordianException {
        return new GordianXCoreAgreementParams(this, pSpec, pResultType);
    }

    @Override
    public GordianXAgreement createAgreement(final GordianXAgreementParams pParams) throws GordianException {
        /* Check validity of Agreement */
        final GordianAgreementSpec mySpec = pParams.getAgreementSpec();
        checkAgreementSpec(mySpec);

        /* Create the agreement */
        final GordianXCoreAgreementEngine myEngine = createEngine(mySpec);
        final GordianXCoreAgreement myAgreement = new GordianXCoreAgreement(myEngine);

        /* Set the details */
        myAgreement.setClientCertificate(pParams.getClientCertificate());
        myAgreement.setServerCertificate(pParams.getServerCertificate());
        myAgreement.setResultType(pParams.getResultType());

        /* Store the parameters */

        /* Build the clientHello */
        myAgreement.buildClientHello();

        /* Return the agreement */
        return myAgreement;
    }

    @Override
    public GordianXAgreement parseAgreementMessage(final byte[] pMessage) throws GordianException {
        /* Parse the message */
        final GordianXCoreAgreementMessageASN1 myASN1 = GordianXCoreAgreementMessageASN1.getInstance(pMessage);

        /* Switch on the messageType */
        switch (myASN1.getMessageType()) {
            case CLIENTHELLO:
                return parseClientHello(myASN1);
            case SERVERHELLO:
                return parseServerHello(myASN1);
            case CLIENTCONFIRM:
                return parseClientConfirm(myASN1);
            default:
                throw new GordianDataException("Unexpected MessageType: " +  myASN1.getMessageType());
        }
    }

    /**
     * Parse a clientHello message.
     * @param pClientHello the message
     * @return the agreement
     * @throws GordianException error
     */
    private GordianXAgreement parseClientHello(final GordianXCoreAgreementMessageASN1 pClientHello) throws GordianException {
        /* Create a new agreement */
        final GordianAgreementSpec mySpec = getSpecForIdentifier(pClientHello.getAgreementId());
        final GordianXCoreAgreementEngine myEngine = createEngine(mySpec);
        final GordianXCoreAgreement myAgreement = new GordianXCoreAgreement(myEngine);

        /* If this is a signed agreement */
        if (GordianAgreementType.SIGNED.equals(mySpec.getAgreementType())) {
            /* Handle no signer certificate */
            if (theSignerCertificate == null) {
                throw new GordianLogicException("No signer declared for Signed agreement");
            }

            /* Declare the signer */
            myAgreement.setSignerCertificate(theSignSpec, theSignerCertificate);
        }

        /* Parse the clientHello */
        myAgreement.parseClientHello(pClientHello);

        /* Return the agreement */
        return myAgreement;
    }

    /**
     * Parse a serverHello message.
     * @param pServerHello the message
     * @return the agreement
     * @throws GordianException error
     */
    private GordianXAgreement parseServerHello(final GordianXCoreAgreementMessageASN1 pServerHello) throws GordianException {
        /* Look up the agreement in the cache */
        final GordianAgreementSpec mySpec = getSpecForIdentifier(pServerHello.getAgreementId());
        final GordianXCoreAgreement myAgreement = theCache.lookUpAgreement(pServerHello.getClientId(), mySpec);

        /* Process the serverHello */
        myAgreement.processServerHello(pServerHello);

        /* Return the agreement */
        return myAgreement;
    }

    /**
     * Parse a clientConfirm message.
     * @param pClientConfirm the message
     * @return the agreement
     * @throws GordianException error
     */
    private GordianXAgreement parseClientConfirm(final GordianXCoreAgreementMessageASN1 pClientConfirm) throws GordianException {
        /* Look up the agreement in the cache */
        final GordianAgreementSpec mySpec = getSpecForIdentifier(pClientConfirm.getAgreementId());
        final GordianXCoreAgreement myAgreement = theCache.lookUpAgreement(pClientConfirm.getServerId(), mySpec);

        /* Process the clientConfirm */
        myAgreement.processClientConfirm(pClientConfirm);

        /* Return the agreement */
        return myAgreement;
    }

    /**
     * Create engine.
     * @param pSpec the agreement Spec
     * @return the engine
     * @throws GordianException on error
     */
    protected GordianXCoreAgreementEngine createEngine(final GordianAgreementSpec pSpec) throws GordianException {
        /* If this is a composite agreement */
        if (pSpec.getKeyPairSpec().getKeyPairType() == GordianKeyPairType.COMPOSITE) {
            /* Create an engine for each sub-agreement */
            final List<GordianAgreementSpec> mySpecs = GordianXCoreAgreementComposite.getSubAgreements(pSpec);
            final List<GordianXCoreAgreementEngine> myEngines = new ArrayList<>();
            for (GordianAgreementSpec mySpec : mySpecs) {
                myEngines.add(createEngine(mySpec));
            }
            return new GordianXCoreAgreementComposite(this, pSpec, myEngines);
        }

        /* Unsupported spec */
        throw new GordianDataException(GordianBaseData.getInvalidText(pSpec));
    }

    @Override
    public Predicate<GordianAgreementSpec> supportedAgreements() {
        return this::validAgreementSpec;
    }

    @Override
    public void checkAgreementSpec(final GordianAgreementSpec pAgreementSpec) throws GordianException {
        /* Check validity of agreement */
        if (!validAgreementSpec(pAgreementSpec)) {
            throw new GordianDataException(GordianBaseData.getInvalidText(pAgreementSpec));
        }
    }

    @Override
    public Long getNextId() {
        return theCache.getNextId();
    }

    @Override
    public void storeAgreement(final Long pId,
                               final GordianXAgreement pAgreement) {
        theCache.storeAgreement(pId, pAgreement);
    }

    @Override
    public void removeAgreement(final Long pId) {
        theCache.removeAgreement(pId);
    }

    @Override
    public void setSigner(final GordianCertificate pSigner) throws GordianException {
        final GordianSignatureFactory mySignFactory = theFactory.getAsyncFactory().getSignatureFactory();
        final GordianSignatureSpec mySignSpec = pSigner == null ? null : mySignFactory.defaultForKeyPair(pSigner.getKeyPair().getKeyPairSpec());
        setSigner(pSigner, mySignSpec);
    }

    @Override
    public void setSigner(final GordianCertificate pSigner,
                          final GordianSignatureSpec pSignSpec) throws GordianException {
        /* Check that certificate can sign data */
        if (pSigner == null || !pSigner.getUsage().hasUse(GordianKeyPairUse.SIGNATURE)) {
            throw new GordianDataException("Certificate must be capable of signing data");
        }

        /* Check that certificate can sign data */
        final GordianSignatureFactory mySignFactory = theFactory.getAsyncFactory().getSignatureFactory();
        if (!mySignFactory.validSignatureSpecForKeyPair(pSigner.getKeyPair(), pSignSpec)) {
            throw new GordianDataException(GordianBaseData.getInvalidText(pSignSpec));
        }

        /* Store parameters */
        theSignerCertificate = pSigner;
        theSignSpec = pSignSpec;
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
            final List<GordianAgreementSpec> mySubAgrees = GordianXCoreAgreementComposite.getSubAgreements(pAgreementSpec);

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

    @Override
    public AlgorithmIdentifier getIdentifierForSpec(final GordianAgreementSpec pSpec) {
        return getAlgorithmIds().determineIdentifier(pSpec);
    }

    @Override
    public GordianAgreementSpec getSpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
        return getAlgorithmIds().determineAgreementSpec(pIdentifier);
    }

    @Override
    public AlgorithmIdentifier getIdentifierForResultType(final Object pResult) throws GordianException {
        return getAlgorithmIds().getIdentifierForResult(pResult);
    }

    @Override
    public Object getResultTypeForIdentifier(final AlgorithmIdentifier pIdentifier) throws GordianException {
        return getAlgorithmIds().processResultIdentifier(pIdentifier);
    }

    /**
     * Obtain the agreement algorithm Ids.
     * @return the agreement Algorithm Ids
     */
    private GordianXCoreAgreementAlgId getAlgorithmIds() {
        if (theAlgIds == null) {
            theAlgIds = new GordianXCoreAgreementAlgId(theFactory);
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
                .toList();
    }

    /**
     * Obtain a list of all possible agreements for the keyPairSpec.
     * @param pKeyPairSpec the keyPairSpec
     * @return the list
     */
    private List<GordianAgreementSpec> listPossibleAgreements(final GordianKeyPairSpec pKeyPairSpec) {
        /* Create list */
        final List<GordianAgreementSpec> myAgreements = new ArrayList<>();

        /* Switch on keyPairType */
        switch (pKeyPairSpec.getKeyPairType()) {
            case RSA:
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.KEM));
                break;
            case NEWHOPE:
                myAgreements.addAll(listAllKDFs(pKeyPairSpec, GordianAgreementType.ANON));
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
    private static List<GordianAgreementSpec> listAllKDFs(final GordianKeyPairSpec pKeyPairSpec,
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
