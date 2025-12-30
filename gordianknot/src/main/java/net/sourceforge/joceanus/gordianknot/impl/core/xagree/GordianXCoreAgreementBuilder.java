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
package net.sourceforge.joceanus.gordianknot.impl.core.xagree;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementType;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianCertificate;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignParams;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignature;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.gordianknot.api.xagree.GordianXAgreementStatus;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.cert.GordianCoreCertificate;
import net.sourceforge.joceanus.gordianknot.impl.core.cipher.GordianCoreCipherFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianLogicException;
import net.sourceforge.joceanus.gordianknot.impl.core.keyset.GordianCoreKeySetFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.sign.GordianCoreSignatureFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.xagree.GordianXCoreAgreementCalculator.GordianDerivationId;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.util.Arrays;

import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;

/**
 * Agreement Builder.
 */
public class GordianXCoreAgreementBuilder {
    /**
     * InitVectorLength.
     */
    private static final int INITLEN = 32;

    /**
     * The Supplier.
     */
    private final GordianXCoreAgreementSupplier theSupplier;

    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

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
    private final GordianXCoreAgreementState theState;

    /**
     * The result calculator.
     */
    private final GordianXCoreAgreementCalculator theResultCalc;

    /**
     * Constructor.
     * @param pSupplier the supplier
     * @param pSpec the agreement spec
     * @throws GordianException on error
     */
    GordianXCoreAgreementBuilder(final GordianXCoreAgreementSupplier pSupplier,
                                 final GordianAgreementSpec pSpec) throws GordianException {
        /* Store parameters and access factory and random */
        theSupplier = pSupplier;
        theFactory = pSupplier.getFactory();
        theRandom = theFactory.getRandomSource().getRandom();

        /* Create the state */
        theState = new GordianXCoreAgreementState(pSpec);
        final GordianKeyPairFactory myFactory = theFactory.getKeyPairFactory();
        theKeyPairGenerator = myFactory.getKeyPairGenerator(pSpec.getKeyPairSpec());

        /* Create the result calculator */
        theResultCalc = new GordianXCoreAgreementCalculator(theFactory, theState);
    }

    /**
     * Obtain the state.
     * @return the state
     */
    public GordianXCoreAgreementState getState() {
        return theState;
    }

    /**
     * Obtain the random.
     * @return the random
     */
    public SecureRandom getRandom() {
        return theFactory.getRandomSource().getRandom();
    }

    /**
     * Set the status.
     * @param pStatus the status
     */
    void setStatus(final GordianXAgreementStatus pStatus) {
        theState.setStatus(pStatus);
    }

    /**
     * Set the resultType.
     * @param pResultType the resultType
     * @throws GordianException on error
     */
    void setResultType(final Object pResultType) throws GordianException {
        checkResultType(pResultType);
        theState.setResultType(pResultType);
    }

    /**
     * Check the resultType is valid.
     * @param pResultType the resultType
     * @throws GordianException on error
     */
    private void checkResultType(final Object pResultType) throws GordianException {
        /* No need to check FactoryType or null */
        if (pResultType instanceof GordianFactoryType
                || pResultType == null) {
            return;
        }

        /* Validate a keySetSpec */
        if (pResultType instanceof GordianKeySetSpec mySpec) {
            /* Check Spec */
            final GordianCoreKeySetFactory myKeySetFactory = (GordianCoreKeySetFactory) theFactory.getKeySetFactory();
            myKeySetFactory.checkKeySetSpec(mySpec);
            return;
        }

        /* Validate a symCipherSpec */
        if (pResultType instanceof GordianSymCipherSpec mySpec) {
            /* Check Spec */
            final GordianCoreCipherFactory myCipherFactory = (GordianCoreCipherFactory) theFactory.getCipherFactory();
            myCipherFactory.checkSymCipherSpec(mySpec);
            return;
        }

        /* Validate a streamCipherSpec */
        if (pResultType instanceof GordianStreamCipherSpec mySpec) {
            /* Check Spec */
            final GordianCoreCipherFactory myCipherFactory = (GordianCoreCipherFactory) theFactory.getCipherFactory();
            myCipherFactory.checkStreamCipherSpec(mySpec);
            return;
        }

        /* Validate a byte array */
        if (pResultType instanceof Integer myInt) {
            if (myInt <= 0) {
                throw new GordianLogicException("Invalid length for byteArray");
            }
            return;
        }

        /* Invalid resultType */
        throw new GordianLogicException("Invalid resultType");
    }

    /**
     * Store secret.
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
     * @param pSecret the secret
     * @throws GordianException on error
     */
    private void processSecret(final byte[] pSecret) throws GordianException {
        /* If we are using confirmation and are not SM2 */
        final GordianAgreementSpec mySpec = theState.getSpec();
        if (Boolean.TRUE.equals(mySpec.withConfirm())
                && mySpec.getAgreementType() != GordianAgreementType.SM2) {
            /* calculate the confirmation tags */
            calculateConfirmationTags(pSecret);
        }

        /* Calculate result */
        theState.setResult(theResultCalc.processSecret(pSecret, theState.getResultType()));
    }

    /**
     * Set the result as an error.
     * @param pError the error
     */
    void setError(final String pError) {
        /* Store details of the error */
        theState.setResultType(pError);
        theState.setResult(new GordianDataException(pError));
    }

    /**
     * Is there an errorState?.
     * @return true/false
     */
    public boolean isError() {
        return theState.getResultType() instanceof String;
    }

    /**
     * Set the clientCertificate.
     * @param pCert the Certificate
     */
    void setClientCertificate(final GordianCertificate pCert) {
        theState.getClient().setCertificate(pCert);
    }

    /**
     * Set the serverCertificate.
     * @param pCert the Certificate
     */
    void setServerCertificate(final GordianCertificate pCert) {
        theState.getServer().setCertificate(pCert);
    }

    /**
     * Set the signerCertificate.
     * @param pCert the Certificate
     * @return the Builder
     */
    GordianXCoreAgreementBuilder setSignerCertificate(final GordianCertificate pCert) {
        theState.setSignerCertificate(pCert);
        return this;
    }

    /**
     * Set the signSpec.
     * @param pSpec the signSpec
     * @return the Builder
     */
    GordianXCoreAgreementBuilder setSignSpec(final GordianSignatureSpec pSpec) {
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
     * @throws GordianException on error
     */
    public void newClientEphemeral() throws GordianException {
        final GordianKeyPair myPair = theKeyPairGenerator.generateKeyPair();
        theState.getClient()
                .setEphemeralKeyPair(myPair)
                .setEphemeralKeySpec(theKeyPairGenerator.getX509Encoding(myPair));
    }

    /**
     * Create a new client ephemeral.
     * @throws GordianException on error
     */
    public void setClientEphemeral(final GordianKeyPair pEphemeral) throws GordianException {
        theState.getClient()
                .setEphemeralKeyPair(pEphemeral)
                .setEphemeralKeySpec(theKeyPairGenerator.getX509Encoding(pEphemeral));
    }

    /**
     * Create a new client ephemeral.
     * @throws GordianException on error
     */
    public void newServerEphemeral() throws GordianException {
        final GordianKeyPair myPair = theKeyPairGenerator.generateKeyPair();
        theState.getServer()
                .setEphemeralKeyPair(myPair)
                .setEphemeralKeySpec(theKeyPairGenerator.getX509Encoding(myPair));
     }

    /**
     * Set the encapsulated message.
     * @param pEncapsulated the message
     */
    public void setEncapsulated(final byte[] pEncapsulated) {
        theState.setEncapsulated(pEncapsulated);
    }

    /**
     * Set the client confirm.
     * @param pConfirm the clientConfirm
     * @throws GordianException on error
     */
    void setClientConfirm(final byte[] pConfirm) throws GordianException {
        /* Access any expected value */
        final GordianXCoreAgreementParticipant myClient = theState.getClient();
        final byte[] myExpected = myClient.getConfirm();

        /* If we have an expected value, reject any difference */
        if (myExpected != null
                && !Arrays.constantTimeAreEqual(myExpected, pConfirm)) {
            throw new GordianDataException("Client Confirmation failed");
        }
        myClient.setConfirm(pConfirm);
    }

    /**
     * Set the client confirm.
     * @param pConfirm the clientConfirm
     * @throws GordianException on error
     */
    void setServerConfirm(final byte[] pConfirm) throws GordianException {
        /* Access any expected value */
        final GordianXCoreAgreementParticipant myServer = theState.getServer();
        final byte[] myExpected = myServer.getConfirm();

        /* If we have an expected value, reject any difference */
        if (myExpected != null
                && !Arrays.constantTimeAreEqual(myExpected, pConfirm)) {
            throw new GordianDataException("Server Confirmation failed");
        }
        myServer.setConfirm(pConfirm);
    }

    /**
     * Build the clientHello.
     * @return the clientHello message
     * @throws GordianException on error
     */
    GordianXCoreAgreementMessageASN1 newClientHello() throws GordianException {
        /* Access details */
        final GordianXCoreAgreementMessageASN1 myMsg = GordianXCoreAgreementMessageASN1.newClientHello();
        final GordianXCoreAgreementParticipant myClient = theState.getClient();
        final GordianXCoreAgreementParticipant myServer = theState.getServer();

        /* Set standard details */
        myMsg.setAgreementId(theSupplier.getIdentifierForSpec(theState.getSpec()))
                .setResultId(theSupplier.getIdentifierForResultType(theState.getResultType()))
                .setClientId(myClient.getId())
                .setInitVector(myClient.getInitVector())
                .setEphemeral(myClient.getEphemeralKeySpec())
                .setEncapsulated(theState.getEncapsulated());

        /* Store certificates */
        final GordianCoreCertificate myClientCert = (GordianCoreCertificate) myClient.getCertificate();
        final GordianCoreCertificate myServerCert = (GordianCoreCertificate) myServer.getCertificate();
        myMsg.setClientCertificate(myClientCert == null ? null : myClientCert.getCertificate())
                .setServerCertificate(myServerCert == null ? null : myServerCert.getCertificate());

        /* Return the message */
        return myMsg;
    }

    /**
     * Build the serverHello.
     * @return the serverHello message
     * @throws GordianException on error
     */
    GordianXCoreAgreementMessageASN1 newServerHello() throws GordianException {
        /* Access details */
        final GordianXCoreAgreementMessageASN1 myMsg = GordianXCoreAgreementMessageASN1.newServerHello();
        final GordianXCoreAgreementParticipant myClient = theState.getClient();
        final GordianXCoreAgreementParticipant myServer = theState.getServer();

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
        final GordianCoreCertificate mySignerCert = (GordianCoreCertificate) theState.getSignerCertificate();
        if (mySignerCert != null) {
            /* Access details */
            final GordianCoreSignatureFactory mySigns = (GordianCoreSignatureFactory) theFactory.getKeyPairFactory().getSignatureFactory();
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
            myMsg.setSignerCertificate(mySignerCert.getCertificate())
                    .setSignature(myAlgId, mySigner.sign());
        }

        /* Return the message */
        return myMsg;
    }

    /**
     * Build the clientConfirm.
     * @return the clientConfirm
     * @throws GordianException on error
     */
    GordianXCoreAgreementMessageASN1 newClientConfirm() throws GordianException {
        /* Access details */
        final GordianXCoreAgreementMessageASN1 myMsg = GordianXCoreAgreementMessageASN1.newClientConfirm();
        final GordianXCoreAgreementParticipant myClient = theState.getClient();
        final GordianXCoreAgreementParticipant myServer = theState.getServer();

        /* Handle error result */
        if (theState.getResultType() instanceof String myType) {
            myMsg.setResultId(theSupplier.getIdentifierForResultType(myType));
            return myMsg;
        }

        /* Set standard details */
        myMsg.setAgreementId(theSupplier.getIdentifierForSpec(theState.getSpec()))
                .setServerId(myServer.getId())
                .setConfirmation(myClient.getConfirm());

        /* Return the message */
        return myMsg;
    }

    /**
     * Parse the clientHello.
     * @param pClientHello the message
     * @throws GordianException on error
     */
    void parseClientHello(final GordianXCoreAgreementMessageASN1 pClientHello) throws GordianException {
        /* Access details */
        final GordianXCoreAgreementParticipant myClient = theState.getClient();
        final GordianXCoreAgreementParticipant myServer = theState.getServer();

        /* Set standard details */
        theState.setResultType(theSupplier.getResultTypeForIdentifier(pClientHello.getResultId()))
                .setEncapsulated(pClientHello.getEncapsulated());

        /* Store client details */
        myClient.setId(pClientHello.getClientId())
                .setInitVector(pClientHello.getInitVector());
        final X509EncodedKeySpec myEphemeral = pClientHello.getEphemeral();
        if (myEphemeral != null) {
            myClient.setEphemeralKeySpec(myEphemeral)
                    .setEphemeralKeyPair(theKeyPairGenerator.derivePublicOnlyKeyPair(myEphemeral));
        }

        /* Store certificates */
        final Certificate myClientCert = pClientHello.getClientCertificate();
        myClient.setCertificate(myClientCert == null ? null : new GordianCoreCertificate(theFactory, myClientCert));
        final Certificate myServerCert = pClientHello.getServerCertificate();
        myServer.setCertificate(myServerCert == null ? null : new GordianCoreCertificate(theFactory, myServerCert));
    }

    /**
     * Parse the serverHello.
     * @param pServerHello the message
     * @throws GordianException on error
     */
    void parseServerHello(final GordianXCoreAgreementMessageASN1 pServerHello) throws GordianException {
        /* Access details */
        final GordianXCoreAgreementParticipant myClient = theState.getClient();
        final GordianXCoreAgreementParticipant myServer = theState.getServer();

        /* Handle error result */
        final AlgorithmIdentifier myResId = pServerHello.getResultId();
        if (myResId != null) {
            setError((String) theSupplier.getResultTypeForIdentifier(myResId));
            return;
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
        final Certificate mySignerCert = pServerHello.getSignerCertificate();
        if (mySignerCert != null) {
            /* Access details */
            final GordianCoreSignatureFactory mySigns = (GordianCoreSignatureFactory) theFactory.getKeyPairFactory().getSignatureFactory();
            final GordianSignatureSpec mySignSpec = mySigns.getSpecForIdentifier(pServerHello.getSignatureId());
            final GordianCoreCertificate myCert = new GordianCoreCertificate(theFactory, mySignerCert);
            final GordianKeyPair mySignerPair = myCert.getKeyPair();
            theState.setSignerCertificate(myCert)
                    .setSignSpec(mySignSpec);
            final GordianSignature mySigner = mySigns.createSigner(mySignSpec);

            /* Build the signature */
            mySigner.initForVerify(GordianSignParams.keyPair(mySignerPair));
            mySigner.update(myClient.getEphemeralKeySpec().getEncoded());
            mySigner.update(myClient.getInitVector());
            mySigner.update(myServer.getEphemeralKeySpec().getEncoded());
            mySigner.update(myServer.getInitVector());
            if (!mySigner.verify(pServerHello.getSignature())) {
                setError("Signature Failed");
            }
        }
    }

    /**
     * Parse the clientConfirm.
     * @param pClientConfirm the clientConfirm
     * @throws GordianException on error
     */
    void parseClientConfirm(final GordianXCoreAgreementMessageASN1 pClientConfirm) throws GordianException {
        /* Access details */
        final GordianXCoreAgreementParticipant myClient = theState.getClient();

        /* Handle error result */
        final AlgorithmIdentifier myResId = pClientConfirm.getResultId();
        if (myResId != null) {
            setError((String) theSupplier.getResultTypeForIdentifier(myResId));
            return;
        }

        /* Store client details */
        myClient.setConfirm(pClientConfirm.getConfirmation());
    }

    /**
     * Calculate the confirmation tags.
     * @param pSecret the secret
     * @throws GordianException on error
     */
    private void calculateConfirmationTags(final byte[] pSecret) throws GordianException {
        /* Access details */
        final GordianXCoreAgreementParticipant myClient = theState.getClient();
        final GordianXCoreAgreementParticipant myServer = theState.getServer();

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
        setServerConfirm(myMac.finish());

        /* Build Client Confirmation tag */
        myMac.update(myClientSpec);
        myMac.update(myServerSpec);
        myMac.update(myClientEphemeral);
        myMac.update(myServerEphemeral);
        setClientConfirm(myMac.finish());
    }
}
