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
package net.sourceforge.joceanus.gordianknot.impl.core.xagree;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementType;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianCertificate;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipher;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipher;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.gordianknot.api.xagree.GordianXAgreement;
import net.sourceforge.joceanus.gordianknot.api.xagree.GordianXAgreementParams;
import net.sourceforge.joceanus.gordianknot.api.xagree.GordianXAgreementStatus;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianLogicException;

import java.util.Objects;

/**
 * Key Agreement.
 */
public class GordianXCoreAgreement
        implements GordianXAgreement {
    /**
     * The Engine.
     */
    private final GordianXCoreAgreementEngine theEngine;

    /**
     * The supplier.
     */
    private final GordianXCoreAgreementSupplier theSupplier;

    /**
     * The Builder.
     */
    private final GordianXCoreAgreementBuilder theBuilder;

    /**
     * The State.
     */
    private final GordianXCoreAgreementState theState;

    /**
     * The Agreement Spec.
     */
    private final GordianAgreementSpec theSpec;

    /**
     * The Parameters.
     */
    private GordianXCoreAgreementParams theParams;

    /**
     * The Next Message.
     */
    private byte[] theNextMsg;

    /**
     * Constructor.
     *
     * @param pEngine the engine
     * @throws GordianException on error
     */
    public GordianXCoreAgreement(final GordianXCoreAgreementEngine pEngine) throws GordianException {
        /* Store details */
        theEngine = pEngine;
        theSupplier = theEngine.getSupplier();
        theBuilder = theEngine.getBuilder();
        theState = theBuilder.getState();
        theSpec = theState.getSpec();
    }

    @Override
    public GordianXAgreementParams getAgreementParams() {
        return new GordianXCoreAgreementParams(theParams);
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    GordianAgreementSpec getAgreementSpec() {
        return theSpec;
    }

    /**
     * Set the status.
     *
     * @param pStatus the status
     */
    void setStatus(final GordianXAgreementStatus pStatus) {
        theState.setStatus(pStatus);
    }

    @Override
    public GordianXAgreementStatus getStatus() {
        return theState.getStatus();
    }

    /**
     * Set the resultType.
     *
     * @param pResultType the resultType
     * @throws GordianException on error
     */
    void setResultType(final Object pResultType) throws GordianException {
        theBuilder.setResultType(pResultType);
    }

    @Override
    public Object getResult() throws GordianException {
        checkStatus(GordianXAgreementStatus.RESULT_AVAILABLE);
        return theState.getResult();
    }

    @Override
    public GordianFactory getFactoryResult() {
        return GordianXAgreementStatus.RESULT_AVAILABLE.equals(theState.getStatus())
                && theState.getResultType() instanceof GordianFactoryType
                ? (GordianFactory) theState.getResult() : null;
    }

    @Override
    public GordianKeySet getKeySetResult() {
        return GordianXAgreementStatus.RESULT_AVAILABLE.equals(theState.getStatus())
                && theState.getResultType() instanceof GordianKeySetSpec
                ? (GordianKeySet) theState.getResult() : null;
    }

    @Override
    public GordianSymCipher[] getSymCipherPairResult() {
        return GordianXAgreementStatus.RESULT_AVAILABLE.equals(theState.getStatus())
                && theState.getResultType() instanceof GordianSymCipherSpec
                ? (GordianSymCipher[]) theState.getResult() : null;
    }

    @Override
    public GordianStreamCipher[] getStreamCipherPairResult() {
        return GordianXAgreementStatus.RESULT_AVAILABLE.equals(theState.getStatus())
                && theState.getResultType() instanceof GordianStreamCipherSpec
                ? (GordianStreamCipher[]) theState.getResult() : null;
    }

    @Override
    public byte[] getByteArrayResult() {
        return GordianXAgreementStatus.RESULT_AVAILABLE.equals(theState.getStatus())
                && theState.getResultType() instanceof Integer
                ? (byte[]) theState.getResult() : null;
    }

    @Override
    public GordianException getRejectionResult() {
        return GordianXAgreementStatus.RESULT_AVAILABLE.equals(theState.getStatus())
                && theState.getResultType() instanceof String
                ? (GordianException) theState.getResult() : null;
    }

    /**
     * Check status.
     *
     * @param pStatus the required status
     * @throws GordianException on error
     */
    protected void checkStatus(final GordianXAgreementStatus pStatus) throws GordianException {
        /* If we are in the wrong state */
        final GordianXAgreementStatus myStatus = theState.getStatus();
        if (myStatus != pStatus) {
            throw new GordianLogicException("Invalid State: " + myStatus);
        }
    }

    /**
     * Ask to fail signature during testing.
     */
    public void failSignature() {
        theBuilder.failSignature();
    }

    /**
     * Ask to fail confirmation during testing.
     */
    public void failConfirmation() {
        theBuilder.failConfirmation();
    }

    /**
     * Set the client certificate.
     *
     * @param pClient the client certificate
     * @throws GordianException on error
     */
    void setClientCertificate(final GordianCertificate pClient) throws GordianException {
        /* Handle null client certificate */
        if (pClient == null) {
            final GordianAgreementType myType = theSpec.getAgreementType();
            if (!myType.isAnonymous() && !myType.isSigned()) {
                throw new GordianDataException("Client Certificate must be provided");
            }
            return;
        }

        /* Store the certificate */
        theBuilder.setClientCertificate(pClient);
    }

    /**
     * Set the server certificate.
     *
     * @param pServer the server certificate
     * @throws GordianException on error
     */
    void setServerCertificate(final GordianCertificate pServer) throws GordianException {
        /* Check that we have a certificate */
        if (pServer == null) {
            final GordianAgreementType myType = theSpec.getAgreementType();
            if (!myType.isSigned()) {
                throw new GordianDataException("Server Certificate must be provided");
            }
        }

        /* Store the certificate */
        theBuilder.setServerCertificate(pServer);
    }

    /**
     * Set the signer details.
     *
     * @param pSignSpec the signature spec
     * @param pSigner   the signer certificate
     * @throws GordianException on error
     */
    void setSignerCertificate(final GordianSignatureSpec pSignSpec,
                              final GordianCertificate pSigner) throws GordianException {
        theBuilder.setSignSpec(pSignSpec)
                .setSignerCertificate(pSigner);
    }

    @Override
    public void updateParams(final GordianXAgreementParams pParams) throws GordianException {
        /* Must be looking for serverPrivate */
        checkStatus(GordianXAgreementStatus.AWAITING_SERVERPRIVATE);

        /* Ensure that we are updating from correct parameters */
        if (!Objects.equals(theParams.getId(), ((GordianXCoreAgreementParams) pParams).getId())) {
            throw new GordianDataException("Invalid parameters provided");
        }

        /* Determine agreement type */
        final GordianAgreementSpec mySpec = theState.getSpec();
        final boolean isSigned = mySpec.getAgreementType().isSigned();

        /* If this is a signed agreement */
        if (isSigned) {
            /* Handle no signer certificate */
            final GordianCertificate mySignerCert = pParams.getSignerCertificate();
            if (mySignerCert == null) {
                throw new GordianLogicException("No signer declared for Signed agreement");
            }

            /* Declare the signer */
            setSignerCertificate(pParams.getSignatureSpec(), mySignerCert);

        } else {
            /* Ensure that the server has a private key */
            final GordianCertificate myServerCert = pParams.getServerCertificate();
            if (myServerCert.getKeyPair().isPublicOnly()) {
                throw new GordianDataException("Server Certificate is Public Only");
            }

            /* Update the server certificate */
            setServerCertificate(myServerCert);
        }

        /* Store additional data */
        theState.setAdditionalData(pParams.getAdditionalData());

        /* Update the parameters */
        theParams = new GordianXCoreAgreementParams((GordianXCoreAgreementParams) pParams);

        /* Process the augmented clientHello and return the agreement */
        processClientHello();
    }

    @Override
    public void setError(final String pError) throws GordianException {
        /* Only allowed while awaiting ServerPrivate */
        checkStatus(GordianXAgreementStatus.AWAITING_SERVERPRIVATE);
        theBuilder.setError(pError);

        /* If we are not anonymous */
        if (!theSpec.getAgreementType().isAnonymous()) {
            /* Create the rejection serverHello */
            setNextMessage(theBuilder.newServerHello());
        }

        /* Set result available */
        theBuilder.setStatus(GordianXAgreementStatus.RESULT_AVAILABLE);
    }

    @Override
    public boolean isRejected() {
        return theBuilder.isRejected();
    }

    /**
     * Set the next message (or null).
     *
     * @param pMessage the next message
     * @throws GordianException on error
     */
    void setNextMessage(final GordianXCoreAgreementMessageASN1 pMessage) throws GordianException {
        theNextMsg = pMessage == null ? null : pMessage.getEncodedBytes();
    }

    @Override
    public byte[] nextMessage() {
        return theNextMsg;
    }

    /**
     * Build the clientHello.
     *
     * @throws GordianException on error
     */
    void buildClientHello() throws GordianException {
        /* Create ClientId and InitVector */
        if (!theSpec.getAgreementType().isAnonymous()) {
            theBuilder.newClientId();
        }
        theBuilder.newClientIV();

        /* Create clientEphemeral if needed */
        if (needClientEphemeral()) {
            theBuilder.newClientEphemeral();
        }

        /* Create the new ClientHello */
        theEngine.buildClientHello();
        final GordianXCoreAgreementMessageASN1 myMsg = theBuilder.newClientHello();

        /* Set next message and status */
        setNextMessage(myMsg);
        theBuilder.setStatus(theSpec.getAgreementType().isAnonymous()
                ? GordianXAgreementStatus.RESULT_AVAILABLE
                : GordianXAgreementStatus.AWAITING_SERVERHELLO);

        /* Store into cache if required */
        if (!theSpec.getAgreementType().isAnonymous()) {
            theSupplier.storeAgreement(myMsg.getClientId(), this);
        }
    }

    /**
     * Process the clientHello.
     *
     * @param pClientHello the clientHello
     * @throws GordianException on error
     */
    void parseClientHello(final GordianXCoreAgreementMessageASN1 pClientHello) throws GordianException {
        /* Parse the clientHello */
        theBuilder.parseClientHello(pClientHello);
        theParams = new GordianXCoreAgreementParams(theBuilder);
        theBuilder.setStatus(GordianXAgreementStatus.AWAITING_SERVERPRIVATE);
    }

    /**
     * Process the clientHello.
     *
     * @throws GordianException on error
     */
    void processClientHello() throws GordianException {
        /* Create ServerId and InitVector */
        if (!theSpec.getAgreementType().isAnonymous()) {
            theBuilder.newServerId();
            theBuilder.newServerIV();
        }

        /* Create serverEphemeral if needed */
        if (needServerEphemeral()) {
            theBuilder.newServerEphemeral();
        }

        /* Copy ephemerals to keyPairs for signed */
        if (theSpec.getAgreementType().isSigned()) {
            theBuilder.copyEphemerals();
        }

        /* Process the clientHello */
        theEngine.processClientHello();

        /* If we are anonymous */
        if (theSpec.getAgreementType().isAnonymous()) {
            /* Set that the result is available */
            theBuilder.setStatus(GordianXAgreementStatus.RESULT_AVAILABLE);

            /* Else we need to build a serverHello */
        } else {
            /* Build the new serverHello */
            final GordianXCoreAgreementMessageASN1 myMsg = theBuilder.newServerHello();
            setNextMessage(myMsg);
            theBuilder.setStatus(Boolean.TRUE.equals(theSpec.withConfirm())
                    ? GordianXAgreementStatus.AWAITING_CLIENTCONFIRM
                    : GordianXAgreementStatus.RESULT_AVAILABLE);

            /* Store into cache if required */
            if (Boolean.TRUE.equals(theSpec.withConfirm())) {
                theSupplier.storeAgreement(myMsg.getServerId(), this);
            }
        }
    }

    /**
     * Process the serverHello.
     *
     * @param pServerHello the serverHello
     * @throws GordianException on error
     */
    public void processServerHello(final GordianXCoreAgreementMessageASN1 pServerHello) throws GordianException {
        /* Check that we are expecting a serverHello */
        checkStatus(GordianXAgreementStatus.AWAITING_SERVERHELLO);

        /* Parse the serverHello */
        final boolean bSuccess = theBuilder.parseServerHello(pServerHello);
        if (bSuccess) {
            /* Copy ephemerals to keyPairs for signed */
            if (theSpec.getAgreementType().isSigned()) {
                theBuilder.copyEphemerals();
            }

            /* Process the serverHello */
            theEngine.processServerHello();
        }

        /* If we need to send confirm */
        if (bSuccess && Boolean.TRUE.equals(theSpec.withConfirm())) {
            /* Build the new clientConfirm */
            setNextMessage(theBuilder.newClientConfirm());
        } else {
            setNextMessage(null);
        }
        theBuilder.setStatus(GordianXAgreementStatus.RESULT_AVAILABLE);

        /* remove from cache */
        theSupplier.removeAgreement(pServerHello.getClientId());
    }

    /**
     * Process the clientConfirm.
     *
     * @param pClientConfirm the clientConfirm
     * @throws GordianException on error
     */
    public void processClientConfirm(final GordianXCoreAgreementMessageASN1 pClientConfirm) throws GordianException {
        /* Check that we are expecting a confirmation */
        checkStatus(GordianXAgreementStatus.AWAITING_CLIENTCONFIRM);

        /* Parse the clientConfirm */
        if (theBuilder.parseClientConfirm(pClientConfirm)) {
            /* Process if we have no error */
            theEngine.processClientConfirm();
        }

        /* Update status */
        setNextMessage(null);
        theBuilder.setStatus(GordianXAgreementStatus.RESULT_AVAILABLE);

        /* remove from cache */
        theSupplier.removeAgreement(pClientConfirm.getServerId());
    }

    /**
     * Do we need a client ephemeral?
     *
     * @return true/false
     */
    private boolean needClientEphemeral() {
        switch (theSpec.getAgreementType()) {
            case ANON:
            case SIGNED:
            case SM2:
            case MQV:
            case UNIFIED:
                return true;
            case KEM:
            case BASIC:
            default:
                return false;
        }
    }

    /**
     * Do we need a server ephemeral?
     *
     * @return true/false
     */
    private boolean needServerEphemeral() {
        switch (theSpec.getAgreementType()) {
            case SIGNED:
            case SM2:
            case MQV:
            case UNIFIED:
                return true;
            case ANON:
            case KEM:
            case BASIC:
            default:
                return false;
        }
    }
}
