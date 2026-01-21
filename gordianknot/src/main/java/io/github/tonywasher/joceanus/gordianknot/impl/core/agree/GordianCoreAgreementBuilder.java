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

import io.github.tonywasher.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.api.agree.GordianAgreementStatus;
import io.github.tonywasher.joceanus.gordianknot.api.agree.GordianAgreementType;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianCertificate;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairFactory;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.api.mac.GordianMac;
import io.github.tonywasher.joceanus.gordianknot.api.mac.GordianMacFactory;
import io.github.tonywasher.joceanus.gordianknot.api.mac.GordianMacSpec;
import io.github.tonywasher.joceanus.gordianknot.api.mac.GordianMacSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignParams;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignature;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementCalculator.GordianDerivationId;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.sign.GordianCoreSignatureFactory;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;

/**
 * Agreement Builder.
 */
public class GordianCoreAgreementBuilder {
    /**
     * InitVectorLength.
     */
    private static final int INITLEN = 32;

    /**
     * The Supplier.
     */
    private final GordianCoreAgreementSupplier theSupplier;

    /**
     * The factory.
     */
    private final GordianBaseFactory theFactory;

    /**
     * Secure Random.
     */
    private final SecureRandom theRandom;

    /**
     * The KeyPair factory.
     */
    private final GordianKeyPairGenerator theKeyPairGenerator;

    /**
     * The state.
     */
    private final GordianCoreAgreementState theState;

    /**
     * The result calculator.
     */
    private final GordianCoreAgreementCalculator theResultCalc;

    /**
     * Should we fail signature during testing?
     */
    private boolean failSignature;

    /**
     * Should we fail confirmation during testing?
     */
    private boolean failConfirmation;

    /**
     * Constructor.
     *
     * @param pSupplier the supplier
     * @param pSpec     the agreement spec
     * @throws GordianException on error
     */
    GordianCoreAgreementBuilder(final GordianCoreAgreementSupplier pSupplier,
                                final GordianAgreementSpec pSpec) throws GordianException {
        /* Store parameters and access factory and random */
        theSupplier = pSupplier;
        theFactory = pSupplier.getFactory();
        theRandom = theFactory.getRandomSource().getRandom();

        /* Create the state */
        theState = new GordianCoreAgreementState(pSpec);
        final GordianKeyPairFactory myFactory = theFactory.getAsyncFactory().getKeyPairFactory();
        theKeyPairGenerator = myFactory.getKeyPairGenerator(pSpec.getKeyPairSpec());

        /* Create the result calculator */
        theResultCalc = new GordianCoreAgreementCalculator(theFactory, theState);
    }

    /**
     * Obtain the supplier.
     *
     * @return the supplier
     */
    public GordianCoreAgreementSupplier getSupplier() {
        return theSupplier;
    }

    /**
     * Obtain the state.
     *
     * @return the state
     */
    public GordianCoreAgreementState getState() {
        return theState;
    }

    /**
     * Obtain the random.
     *
     * @return the random
     */
    public SecureRandom getRandom() {
        return theFactory.getRandomSource().getRandom();
    }

    /**
     * Ask to fail signature during testing.
     */
    void failSignature() {
        failSignature = true;
    }

    /**
     * Ask to fail confirmation during testing.
     */
    void failConfirmation() {
        failConfirmation = true;
    }

    /**
     * Set the status.
     *
     * @param pStatus the status
     */
    void setStatus(final GordianAgreementStatus pStatus) {
        theState.setStatus(pStatus);
    }

    /**
     * Set the resultType.
     *
     * @param pResultType the resultType
     * @throws GordianException on error
     */
    void setResultType(final Object pResultType) throws GordianException {
        theState.setResultType(pResultType);
    }

    /**
     * Store secret.
     *
     * @param pSecret the secret
     * @throws GordianException on error
     */
    public void storeSecret(final byte[] pSecret) throws GordianException {
        /* Protect against failure */
        try {
            /* Just process the secret */
            processSecret(pSecret);

            /* Clear buffers */
        } finally {
            /* Clear the secret */
            Arrays.fill(pSecret, (byte) 0);
        }
    }

    /**
     * Process the secret.
     *
     * @param pSecret the secret
     * @throws GordianException on error
     */
    private void processSecret(final byte[] pSecret) throws GordianException {
        /* If we are using confirmation */
        boolean bSuccess = true;
        final GordianAgreementSpec mySpec = theState.getSpec();
        if (Boolean.TRUE.equals(mySpec.withConfirm())
                && mySpec.getAgreementType() != GordianAgreementType.SM2) {
            /* calculate the confirmation tags */
            bSuccess = calculateConfirmationTags(pSecret);
        }

        /* Calculate result */
        if (bSuccess) {
            theState.setResult(theResultCalc.processSecret(pSecret, theState.getResultType()));
        }
    }

    /**
     * Set the result as an error.
     *
     * @param pError the error
     */
    public void setError(final String pError) {
        /* Store details of the error */
        theState.setResultType(pError);
        theState.setResult(new GordianDataException(pError));
    }

    /**
     * Is the agreement rejected?.
     *
     * @return true/false
     */
    public boolean isRejected() {
        return theState.getResultType() instanceof String;
    }

    /**
     * Set the clientCertificate.
     *
     * @param pCert the Certificate
     */
    void setClientCertificate(final GordianCertificate pCert) {
        theState.getClient().setCertificate(pCert);
    }

    /**
     * Set the serverCertificate.
     *
     * @param pCert the Certificate
     */
    void setServerCertificate(final GordianCertificate pCert) {
        theState.getServer().setCertificate(pCert);
    }

    /**
     * Set the signerCertificate.
     *
     * @param pCert the Certificate
     * @return the Builder
     */
    GordianCoreAgreementBuilder setSignerCertificate(final GordianCertificate pCert) {
        theState.setSignerCertificate(pCert);
        return this;
    }

    /**
     * Set the signSpec.
     *
     * @param pSpec the signSpec
     * @return the Builder
     */
    GordianCoreAgreementBuilder setSignSpec(final GordianSignatureSpec pSpec) {
        theState.setSignSpec(pSpec);
        return this;
    }

    /**
     * Create a new clientId.
     */
    void newClientId() {
        theState.getClient().setId(theSupplier.getNextId());
    }

    /**
     * Create a new serverId.
     */
    void newServerId() {
        theState.getServer().setId(theSupplier.getNextId());
    }

    /**
     * Create a new client initVector.
     */
    void newClientIV() {
        final byte[] myClientIV = new byte[INITLEN];
        theRandom.nextBytes(myClientIV);
        theState.getClient().setInitVector(myClientIV);
    }

    /**
     * Create a new server initVector.
     */
    void newServerIV() {
        final byte[] myServerIV = new byte[INITLEN];
        theRandom.nextBytes(myServerIV);
        theState.getServer().setInitVector(myServerIV);
    }

    /**
     * Create a new client ephemeral.
     *
     * @throws GordianException on error
     */
    public void newClientEphemeral() throws GordianException {
        final GordianKeyPair myPair = theKeyPairGenerator.generateKeyPair();
        theState.getClient()
                .setEphemeralKeyPair(myPair)
                .setEphemeralKeySpec(theKeyPairGenerator.getX509Encoding(myPair));
    }

    /**
     * Set the client ephemeral as Encapsulated.
     *
     * @param pEphemeral the keyPair
     * @throws GordianException on error
     */
    public void setClientEphemeralAsEncapsulated(final GordianKeyPair pEphemeral) throws GordianException {
        final X509EncodedKeySpec myKeySpec = theKeyPairGenerator.getX509Encoding(pEphemeral);
        theState.setEncapsulated(myKeySpec.getEncoded());
    }

    /**
     * Create a new client ephemeral.
     *
     * @throws GordianException on error
     */
    public void newServerEphemeral() throws GordianException {
        final GordianKeyPair myPair = theKeyPairGenerator.generateKeyPair();
        theState.getServer()
                .setEphemeralKeyPair(myPair)
                .setEphemeralKeySpec(theKeyPairGenerator.getX509Encoding(myPair));
    }

    /**
     * Copy ephemeral keyPairs to main keyPairs.
     */
    void copyEphemerals() {
        theState.getClient().copyEphemeral();
        theState.getServer().copyEphemeral();
    }

    /**
     * Set the client confirm.
     *
     * @param pConfirm the clientConfirm
     * @return noError true/false
     */
    boolean setClientConfirm(final byte[] pConfirm) {
        /* Access any expected value */
        final GordianCoreAgreementParticipant myClient = theState.getClient();
        final byte[] myExpected = myClient.getConfirm();

        /* If we have an expected value, reject any difference */
        if (failConfirmation
                || (myExpected != null && !Arrays.constantTimeAreEqual(myExpected, pConfirm))) {
            setError("Client Confirmation failed");
            return false;
        }

        /* Store the value */
        myClient.setConfirm(pConfirm);
        return true;
    }

    /**
     * Set the server confirm.
     *
     * @param pConfirm the serverConfirm
     * @return noError true/false
     */
    boolean setServerConfirm(final byte[] pConfirm) {
        /* Access any expected value */
        final GordianCoreAgreementParticipant myServer = theState.getServer();
        final byte[] myExpected = myServer.getConfirm();

        /* If we have an expected value, reject any difference */
        if (myExpected != null
                && (failConfirmation || !Arrays.constantTimeAreEqual(myExpected, pConfirm))) {
            setError("Server Confirmation failed");
            return false;
        }

        /* Store the value */
        myServer.setConfirm(pConfirm);
        return true;
    }

    /**
     * Build the clientHello.
     *
     * @return the clientHello message
     * @throws GordianException on error
     */
    GordianCoreAgreementMessageASN1 newClientHello() throws GordianException {
        /* Access details */
        final GordianCoreAgreementMessageASN1 myMsg = GordianCoreAgreementMessageASN1.newClientHello();
        final GordianCoreAgreementParticipant myClient = theState.getClient();
        final GordianCoreAgreementParticipant myServer = theState.getServer();

        /* Set standard details */
        myMsg.setAgreementId(theSupplier.getIdentifierForSpec(theState.getSpec()))
                .setResultId(theSupplier.getIdentifierForResultType(theState.getResultType()))
                .setClientId(myClient.getId())
                .setInitVector(myClient.getInitVector())
                .setEphemeral(myClient.getEphemeralKeySpec())
                .setEncapsulated(theState.getEncapsulated());

        /* Store certificates */
        myMsg.setClientCertificate(myClient.getCertificate())
                .setServerCertificate(myServer.getCertificate());

        /* Return the message */
        return myMsg;
    }

    /**
     * Build the serverHello.
     *
     * @return the serverHello message
     * @throws GordianException on error
     */
    GordianCoreAgreementMessageASN1 newServerHello() throws GordianException {
        /* Access details */
        final GordianCoreAgreementMessageASN1 myMsg = GordianCoreAgreementMessageASN1.newServerHello();
        final GordianCoreAgreementParticipant myClient = theState.getClient();
        final GordianCoreAgreementParticipant myServer = theState.getServer();

        /* Set standard details */
        myMsg.setAgreementId(theSupplier.getIdentifierForSpec(theState.getSpec()))
                .setClientId(myClient.getId());

        /* Handle error result */
        if (theState.getResultType() instanceof String myType) {
            myMsg.setResultId(theSupplier.getIdentifierForResultType(myType));
            return myMsg;
        }

        /* Set server details */
        myMsg.setServerId(myServer.getId())
                .setInitVector(myServer.getInitVector())
                .setEphemeral(myServer.getEphemeralKeySpec())
                .setConfirmation(myServer.getConfirm());

        /* Store signing details */
        final GordianCertificate mySignerCert = theState.getSignerCertificate();
        if (mySignerCert != null) {
            /* Access details */
            final GordianCoreSignatureFactory mySigns = (GordianCoreSignatureFactory) theFactory.getAsyncFactory().getSignatureFactory();
            final GordianSignatureSpec mySignSpec = theState.getSignSpec();
            final GordianKeyPair mySignerPair = mySignerCert.getKeyPair();
            final AlgorithmIdentifier myAlgId = mySigns.getIdentifierForSpecAndKeyPair(mySignSpec, mySignerPair);

            /* Build the signature */
            final GordianSignature mySigner = mySigns.createSigner(mySignSpec);
            mySigner.initForSigning(GordianSignParams.keyPair(mySignerPair));
            mySigner.update(myClient.getEphemeralKeySpec().getEncoded());
            mySigner.update(myClient.getInitVector());
            mySigner.update(myServer.getEphemeralKeySpec().getEncoded());
            mySigner.update(myServer.getInitVector());
            myMsg.setSignerCertificate(mySignerCert)
                    .setSignature(myAlgId, mySigner.sign());
        }

        /* Return the message */
        return myMsg;
    }

    /**
     * Build the clientConfirm.
     *
     * @return the clientConfirm
     * @throws GordianException on error
     */
    GordianCoreAgreementMessageASN1 newClientConfirm() throws GordianException {
        /* Access details */
        final GordianCoreAgreementMessageASN1 myMsg = GordianCoreAgreementMessageASN1.newClientConfirm();
        final GordianCoreAgreementParticipant myClient = theState.getClient();
        final GordianCoreAgreementParticipant myServer = theState.getServer();

        /* Set standard details */
        myMsg.setAgreementId(theSupplier.getIdentifierForSpec(theState.getSpec()))
                .setServerId(myServer.getId());

        /* Handle error result */
        if (theState.getResultType() instanceof String myType) {
            myMsg.setResultId(theSupplier.getIdentifierForResultType(myType));
            return myMsg;
        }

        /* Set confirmation */
        myMsg.setConfirmation(myClient.getConfirm());

        /* Return the message */
        return myMsg;
    }

    /**
     * Parse the clientHello.
     *
     * @param pClientHello the message
     * @throws GordianException on error
     */
    void parseClientHello(final GordianCoreAgreementMessageASN1 pClientHello) throws GordianException {
        /* Access details */
        final GordianCoreAgreementParticipant myClient = theState.getClient();
        final GordianCoreAgreementParticipant myServer = theState.getServer();

        /* Set standard details */
        theState.setResultType(theSupplier.getResultTypeForIdentifier(pClientHello.getResultId()));
        parseEncapsulated(pClientHello.getEncapsulated());

        /* Store client details */
        myClient.setId(pClientHello.getClientId())
                .setInitVector(pClientHello.getInitVector());
        final X509EncodedKeySpec myEphemeral = pClientHello.getEphemeral();
        if (myEphemeral != null) {
            myClient.setEphemeralKeySpec(myEphemeral)
                    .setEphemeralKeyPair(theKeyPairGenerator.derivePublicOnlyKeyPair(myEphemeral));
        }

        /* Store certificates */
        myClient.setCertificate(pClientHello.getClientCertificate(theFactory));
        myServer.setCertificate(pClientHello.getServerCertificate(theFactory));
    }

    /**
     * Parse the serverHello.
     *
     * @param pServerHello the message
     * @return noError true/false
     * @throws GordianException on error
     */
    boolean parseServerHello(final GordianCoreAgreementMessageASN1 pServerHello) throws GordianException {
        /* Access details */
        final GordianCoreAgreementParticipant myClient = theState.getClient();
        final GordianCoreAgreementParticipant myServer = theState.getServer();

        /* Handle error result */
        final AlgorithmIdentifier myResId = pServerHello.getResultId();
        if (myResId != null) {
            setError((String) theSupplier.getResultTypeForIdentifier(myResId));
            return false;
        }

        /* Store server details */
        myServer.setId(pServerHello.getServerId())
                .setInitVector(pServerHello.getInitVector())
                .setConfirm(pServerHello.getConfirmation());
        final X509EncodedKeySpec myEphemeral = pServerHello.getEphemeral();
        if (myEphemeral != null) {
            myServer.setEphemeralKeySpec(myEphemeral)
                    .setEphemeralKeyPair(theKeyPairGenerator.derivePublicOnlyKeyPair(myEphemeral));
        }

        /* Store signing details */
        final GordianCertificate mySignerCert = pServerHello.getSignerCertificate(theFactory);
        if (mySignerCert != null) {
            /* Access details */
            final GordianCoreSignatureFactory mySigns = (GordianCoreSignatureFactory) theFactory.getAsyncFactory().getSignatureFactory();
            final GordianSignatureSpec mySignSpec = mySigns.getSpecForIdentifier(pServerHello.getSignatureId());
            final GordianKeyPair mySignerPair = mySignerCert.getKeyPair();
            theState.setSignerCertificate(mySignerCert)
                    .setSignSpec(mySignSpec);
            final GordianSignature mySigner = mySigns.createSigner(mySignSpec);

            /* Build the signature */
            mySigner.initForVerify(GordianSignParams.keyPair(mySignerPair));
            mySigner.update(myClient.getEphemeralKeySpec().getEncoded());
            mySigner.update(myClient.getInitVector());
            mySigner.update(myServer.getEphemeralKeySpec().getEncoded());
            mySigner.update(myServer.getInitVector());
            if (failSignature || !mySigner.verify(pServerHello.getSignature())) {
                setError("Signature Failed");
                return false;
            }
        }

        /* Success */
        return true;
    }

    /**
     * Parse the clientConfirm.
     *
     * @param pClientConfirm the clientConfirm
     * @return noError true/false
     * @throws GordianException on error
     */
    boolean parseClientConfirm(final GordianCoreAgreementMessageASN1 pClientConfirm) throws GordianException {
        /* Handle error result */
        final AlgorithmIdentifier myResId = pClientConfirm.getResultId();
        if (myResId != null) {
            setError((String) theSupplier.getResultTypeForIdentifier(myResId));
            return false;
        }

        /* Store client details */
        return setClientConfirm(pClientConfirm.getConfirmation());
    }

    /**
     * Parse the clientHello.
     *
     * @param pEncapsulated the encapsulated
     * @throws GordianException on error
     */
    void parseEncapsulated(final byte[] pEncapsulated) throws GordianException {
        if (pEncapsulated != null
                && GordianKeyPairType.NEWHOPE.equals(theState.getSpec().getKeyPairSpec().getKeyPairType())) {
            final GordianKeyPair myKeyPair
                    = theKeyPairGenerator.derivePublicOnlyKeyPair(new X509EncodedKeySpec(pEncapsulated));
            theState.getClient().setEphemeralKeyPair(myKeyPair);
        } else {
            theState.setEncapsulated(pEncapsulated);
        }
    }

    /**
     * Calculate the confirmation tags.
     *
     * @param pSecret the secret
     * @return noError true/false
     * @throws GordianException on error
     */
    private boolean calculateConfirmationTags(final byte[] pSecret) throws GordianException {
        /* Access details */
        final GordianCoreAgreementParticipant myClient = theState.getClient();
        final GordianCoreAgreementParticipant myServer = theState.getServer();

        /* Derive the key */
        final byte[] myKey = theResultCalc.calculateDerivedSecret(pSecret, GordianDerivationId.TAGS, GordianLength.LEN_512.getByteLength());

        /* Create the hMac and initialize with the key */
        final GordianMacFactory myMacs = theFactory.getMacFactory();
        final GordianMacSpec mySpec = GordianMacSpecBuilder.hMac(GordianDerivationId.TAGS.getDigestType());
        final GordianMac myMac = myMacs.createMac(mySpec);
        myMac.initKeyBytes(myKey);

        /* Access the public encodings */
        final byte[] myClientSpec = theKeyPairGenerator.getX509Encoding(myClient.getKeyPair()).getEncoded();
        final byte[] myClientEphemeral = myClient.getEphemeralKeySpec().getEncoded();
        final byte[] myServerSpec = theKeyPairGenerator.getX509Encoding(myServer.getKeyPair()).getEncoded();
        final byte[] myServerEphemeral = myServer.getEphemeralKeySpec().getEncoded();

        /* Build Server Confirmation tag */
        myMac.update(myServerSpec);
        myMac.update(myClientSpec);
        myMac.update(myServerEphemeral);
        myMac.update(myClientEphemeral);
        boolean bSuccess = setServerConfirm(myMac.finish());

        /* If we are OK */
        if (bSuccess) {
            /* Build Client Confirmation tag */
            myMac.update(myClientSpec);
            myMac.update(myServerSpec);
            myMac.update(myClientEphemeral);
            myMac.update(myServerEphemeral);
            bSuccess = setClientConfirm(myMac.finish());
        }

        /* Return success */
        return bSuccess;
    }
}
