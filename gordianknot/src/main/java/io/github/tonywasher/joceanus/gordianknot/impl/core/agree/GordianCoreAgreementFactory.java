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
package io.github.tonywasher.joceanus.gordianknot.impl.core.agree;

import io.github.tonywasher.joceanus.gordianknot.api.agree.GordianAgreement;
import io.github.tonywasher.joceanus.gordianknot.api.agree.GordianAgreementParams;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianNewAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianNewAgreementSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianNewAgreementType;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianCertificate;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianKeyPairUsage;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianKeyPairUse;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignatureFactory;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianNewSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.cert.GordianMiniCertificate;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree.GordianCoreAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree.GordianCoreAgreementSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreEdwardsSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Core agreement factory.
 */
public abstract class GordianCoreAgreementFactory
        implements GordianCoreAgreementSupplier {
    /**
     * The factory.
     */
    private final GordianBaseFactory theFactory;

    /**
     * The id cache.
     */
    private final GordianCoreAgreementCache theCache;

    /**
     * The algorithm Ids.
     */
    private GordianCoreAgreementAlgId theAlgIds;

    /**
     * The signer certificate.
     */
    private GordianCertificate theSignerCertificate;

    /**
     * The signatureSpec.
     */
    private GordianNewSignatureSpec theSignSpec;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    protected GordianCoreAgreementFactory(final GordianBaseFactory pFactory) {
        theFactory = pFactory;
        theCache = new GordianCoreAgreementCache(theFactory.getRandomSource());
    }

    @Override
    public GordianNewAgreementSpecBuilder newAgreementSpecBuilder() {
        return GordianCoreAgreementSpecBuilder.newInstance();
    }

    @Override
    public GordianBaseFactory getFactory() {
        return theFactory;
    }

    @Override
    public GordianAgreementParams newAgreementParams(final GordianNewAgreementSpec pSpec,
                                                     final Object pResultType) throws GordianException {
        return new GordianCoreAgreementParams(this, pSpec, pResultType);
    }

    @Override
    public GordianCertificate newMiniCertificate(final X500Name pSubject,
                                                 final GordianKeyPair pKeyPair,
                                                 final GordianKeyPairUsage pUsage) throws GordianException {
        return new GordianMiniCertificate(theFactory, pSubject, pKeyPair, pUsage);
    }

    @Override
    public GordianAgreement createAgreement(final GordianAgreementParams pParams) throws GordianException {
        /* Check validity of Agreement */
        final GordianNewAgreementSpec mySpec = pParams.getAgreementSpec();
        checkAgreementSpec(mySpec);

        /* Create the agreement */
        final GordianCoreAgreementEngine myEngine = createEngine(mySpec);
        final GordianCoreAgreement myAgreement = new GordianCoreAgreement(myEngine);

        /* Set the details */
        myAgreement.setClientCertificate(pParams.getClientCertificate());
        myAgreement.setServerCertificate(pParams.getServerCertificate());
        myAgreement.setResultType(pParams.getResultType());
        myAgreement.setAdditionalData(pParams.getAdditionalData());

        /* Build the clientHello */
        myAgreement.buildClientHello();

        /* Return the agreement */
        return myAgreement;
    }

    @Override
    public GordianAgreement parseAgreementMessage(final byte[] pMessage) throws GordianException {
        /* Parse the message */
        final GordianCoreAgreementMessageASN1 myASN1 = GordianCoreAgreementMessageASN1.getInstance(pMessage);

        /* Switch on the messageType */
        switch (myASN1.getMessageType()) {
            case CLIENTHELLO:
                return parseClientHello(myASN1);
            case SERVERHELLO:
                return parseServerHello(myASN1);
            case CLIENTCONFIRM:
                return parseClientConfirm(myASN1);
            default:
                throw new GordianDataException("Unexpected MessageType: " + myASN1.getMessageType());
        }
    }

    /**
     * Parse a clientHello message.
     *
     * @param pClientHello the message
     * @return the agreement
     * @throws GordianException error
     */
    private GordianAgreement parseClientHello(final GordianCoreAgreementMessageASN1 pClientHello) throws GordianException {
        /* Create a new agreement */
        final GordianNewAgreementSpec mySpec = getSpecForIdentifier(pClientHello.getAgreementId());
        final GordianCoreAgreementEngine myEngine = createEngine(mySpec);
        final GordianCoreAgreement myAgreement = new GordianCoreAgreement(myEngine);

        /* Parse the clientHello */
        myAgreement.parseClientHello(pClientHello);
        myAgreement.setSignerCertificate(theSignSpec, theSignerCertificate);

        /* Return the agreement */
        return myAgreement;
    }

    /**
     * Parse a serverHello message.
     *
     * @param pServerHello the message
     * @return the agreement
     * @throws GordianException error
     */
    private GordianAgreement parseServerHello(final GordianCoreAgreementMessageASN1 pServerHello) throws GordianException {
        /* Look up the agreement in the cache */
        final GordianNewAgreementSpec mySpec = getSpecForIdentifier(pServerHello.getAgreementId());
        final GordianCoreAgreement myAgreement = theCache.lookUpAgreement(pServerHello.getClientId(), mySpec);

        /* Process the serverHello */
        myAgreement.processServerHello(pServerHello);

        /* Return the agreement */
        return myAgreement;
    }

    /**
     * Parse a clientConfirm message.
     *
     * @param pClientConfirm the message
     * @return the agreement
     * @throws GordianException error
     */
    private GordianAgreement parseClientConfirm(final GordianCoreAgreementMessageASN1 pClientConfirm) throws GordianException {
        /* Look up the agreement in the cache */
        final GordianNewAgreementSpec mySpec = getSpecForIdentifier(pClientConfirm.getAgreementId());
        final GordianCoreAgreement myAgreement = theCache.lookUpAgreement(pClientConfirm.getServerId(), mySpec);

        /* Process the clientConfirm */
        myAgreement.processClientConfirm(pClientConfirm);

        /* Return the agreement */
        return myAgreement;
    }

    /**
     * Create engine.
     *
     * @param pSpec the agreement Spec
     * @return the engine
     * @throws GordianException on error
     */
    protected GordianCoreAgreementEngine createEngine(final GordianNewAgreementSpec pSpec) throws GordianException {
        /* If this is a composite agreement */
        if (pSpec.getKeyPairSpec().getKeyPairType() == GordianNewKeyPairType.COMPOSITE) {
            /* Create an engine for each sub-agreement */
            final List<GordianNewAgreementSpec> mySpecs = GordianCoreAgreementComposite.getSubAgreements(pSpec);
            final List<GordianCoreAgreementEngine> myEngines = new ArrayList<>();
            for (GordianNewAgreementSpec mySpec : mySpecs) {
                myEngines.add(createEngine(mySpec));
            }
            return new GordianCoreAgreementComposite(this, (GordianCoreAgreementSpec) pSpec, myEngines);
        }

        /* Unsupported spec */
        throw new GordianDataException(GordianBaseData.getInvalidText(pSpec));
    }

    @Override
    public Predicate<GordianNewAgreementSpec> supportedAgreements() {
        return this::validAgreementSpec;
    }

    @Override
    public void checkAgreementSpec(final GordianNewAgreementSpec pAgreementSpec) throws GordianException {
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
                               final GordianAgreement pAgreement) {
        theCache.storeAgreement(pId, pAgreement);
    }

    @Override
    public void removeAgreement(final Long pId) {
        theCache.removeAgreement(pId);
    }

    @Override
    public void setSigner(final GordianCertificate pSigner) throws GordianException {
        final GordianSignatureFactory mySignFactory = theFactory.getAsyncFactory().getSignatureFactory();
        final GordianNewSignatureSpec mySignSpec = pSigner == null ? null : mySignFactory.defaultForKeyPair(pSigner.getKeyPair().getKeyPairSpec());
        setSigner(pSigner, mySignSpec);
    }

    @Override
    public void setSigner(final GordianCertificate pSigner,
                          final GordianNewSignatureSpec pSignSpec) throws GordianException {
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
    protected boolean validAgreementSpec(final GordianNewAgreementSpec pSpec) {
        /* Reject invalid agreementSpec */
        if (pSpec == null || !pSpec.isValid()) {
            return false;
        }

        /* Check that spec is supported */
        return pSpec.isSupported();
    }

    @Override
    public boolean validAgreementSpecForKeyPairSpec(final GordianNewKeyPairSpec pKeyPairSpec,
                                                    final GordianNewAgreementSpec pAgreementSpec) {
        /* Check that the agreementSpec is supported */
        if (!validAgreementSpec(pAgreementSpec)) {
            return false;
        }

        /* Check agreement matches keySpec */
        if (!pAgreementSpec.getKeyPairSpec().equals(pKeyPairSpec)) {
            return false;
        }

        /* For Edwards XDH, disallow 512KDF for 25519 and 256KDF for 448 */
        final GordianCoreAgreementSpec myAgreeSpec = (GordianCoreAgreementSpec) pAgreementSpec;
        final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeyPairSpec;
        if (pKeyPairSpec.getKeyPairType() == GordianNewKeyPairType.XDH) {
            final GordianCoreEdwardsSpec myEdwards = myKeySpec.getEdwardsSpec();
            switch (pAgreementSpec.getKDFType()) {
                case SHA256KDF:
                case SHA256CKDF:
                case SHA256HKDF:
                    return myEdwards.is25519();
                case SHA512KDF:
                case SHA512CKDF:
                case SHA512HKDF:
                    return !myEdwards.is25519();
                default:
                    break;
            }
        }

        /* For Composite AgreementSpec */
        if (pKeyPairSpec.getKeyPairType() == GordianNewKeyPairType.COMPOSITE) {
            /* Access the subSpecs  */
            final List<GordianNewAgreementSpec> mySubAgrees = GordianCoreAgreementComposite.getSubAgreements(pAgreementSpec);

            /* Loop through the subAgreements */
            for (GordianNewAgreementSpec mySpec : mySubAgrees) {
                if (!validAgreementSpecForKeyPairSpec(mySpec.getKeyPairSpec(), mySpec)) {
                    return false;
                }
            }

            /* Check confirmation */
            if (pAgreementSpec.withConfirm()
                    && !myAgreeSpec.getCoreAgreementType().canConfirm()) {
                return false;
            }

            /* Disallow SM2 with confirm */
            return pAgreementSpec.getAgreementType() != GordianNewAgreementType.SM2
                    || !pAgreementSpec.withConfirm();
        }

        /* OK */
        return true;
    }

    @Override
    public AlgorithmIdentifier getIdentifierForSpec(final GordianNewAgreementSpec pSpec) {
        return getAlgorithmIds().determineIdentifier(pSpec);
    }

    @Override
    public GordianNewAgreementSpec getSpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
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
     *
     * @return the agreement Algorithm Ids
     */
    private GordianCoreAgreementAlgId getAlgorithmIds() {
        if (theAlgIds == null) {
            theAlgIds = new GordianCoreAgreementAlgId(theFactory);
        }
        return theAlgIds;
    }

    @Override
    public List<GordianNewAgreementSpec> listAllSupportedAgreements(final GordianKeyPair pKeyPair) {
        return listAllSupportedAgreements(pKeyPair.getKeyPairSpec());
    }

    @Override
    public List<GordianNewAgreementSpec> listAllSupportedAgreements(final GordianNewKeyPairSpec pKeyPairSpec) {
        return listPossibleAgreements(pKeyPairSpec)
                .stream()
                .filter(supportedAgreements())
                .filter(s -> validAgreementSpecForKeyPairSpec(pKeyPairSpec, s))
                .toList();
    }

    /**
     * Obtain a list of all possible agreements for the keyPairSpec.
     *
     * @param pKeyPairSpec the keyPairSpec
     * @return the list
     */
    private List<GordianNewAgreementSpec> listPossibleAgreements(final GordianNewKeyPairSpec pKeyPairSpec) {
        return GordianCoreAgreementSpecBuilder.listAllPossibleSpecs(pKeyPairSpec);
    }

    @Override
    public GordianNewAgreementSpec defaultForKeyPair(final GordianNewKeyPairSpec pKeySpec) {
        final Iterator<GordianNewAgreementSpec> myIterator = listAllSupportedAgreements(pKeySpec).iterator();
        return myIterator.hasNext() ? myIterator.next() : null;
    }
}
