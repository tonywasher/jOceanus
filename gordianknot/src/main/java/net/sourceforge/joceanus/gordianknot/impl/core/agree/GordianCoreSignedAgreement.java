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
package net.sourceforge.joceanus.gordianknot.impl.core.agree;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementStatus;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianSignedAgreement;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignParams;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignature;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.agree.GordianAgreementMessageASN1.GordianMessageType;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.sign.GordianCoreSignatureFactory;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import java.security.spec.X509EncodedKeySpec;

/**
 * Signed Agreement.
 */
public abstract class GordianCoreSignedAgreement
        extends GordianCoreKeyPairAgreement
        implements GordianSignedAgreement {
    /**
     * The client ephemeral KeyPair.
     */
    private GordianKeyPair theClientEphemeral;

    /**
     * The server ephemeral KeyPair.
     */
    private GordianKeyPair theServerEphemeral;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSpec the agreementSpec
     */
    protected GordianCoreSignedAgreement(final GordianBaseFactory pFactory,
                                         final GordianAgreementSpec pSpec) {
        super(pFactory, pSpec);
    }

    /**
     * Obtain the client Ephemeral keyPair.
     * @return  the keyPair
     */
    protected GordianKeyPair getClientEphemeralKeyPair() {
        return theClientEphemeral;
    }

    /**
     * Obtain the server Ephemeral keyPair.
     * @return  the keyPair
     */
    protected GordianKeyPair getServerEphemeralKeyPair() {
        return theServerEphemeral;
    }

    @Override
    public void reset() {
        /* Reset underlying details */
        super.reset();

        /* Reset keyPair details */
        theClientEphemeral = null;
        theServerEphemeral = null;
    }

    /**
     * Store client ephemeral.
     * @param pEphemeral the server ephemeral
     */
    protected void storeClientEphemeral(final GordianKeyPair pEphemeral) {
        /* Store the ephemeral */
        theClientEphemeral = pEphemeral;
    }

    /**
     * Store server ephemeral.
     * @param pEphemeral the server ephemeral
     */
    protected void storeServerEphemeral(final GordianKeyPair pEphemeral) {
        /* Store the ephemeral */
        theServerEphemeral = pEphemeral;
    }

    @Override
    public byte[] createClientHello() throws GordianException {
        /* Create the clientHello and extract the encoded bytes */
        return createClientHelloASN1().getEncodedBytes();
    }

    /**
     * Create the clientHello ASN1.
     * @return the clientHello message
     * @throws GordianException on error
     */
    public GordianAgreementMessageASN1 createClientHelloASN1() throws GordianException {
        /* Create ephemeral key */
        final GordianKeyPairFactory myFactory = getFactory().getAsyncFactory().getKeyPairFactory();
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(getAgreementSpec().getKeyPairSpec());
        theClientEphemeral = myGenerator.generateKeyPair();
        final X509EncodedKeySpec myKeySpec = myGenerator.getX509Encoding(theClientEphemeral);

        /* Create the clientHello message */
        final GordianAgreementMessageASN1 myClientHello = buildClientHelloASN1(myKeySpec);

        /* Set status */
        setStatus(GordianAgreementStatus.AWAITING_SERVERHELLO);

        /* Return the clientHello */
        return myClientHello;
    }

    @Override
    public byte[] acceptClientHello(final GordianKeyPair pServer,
                                    final byte[] pClientHello) throws GordianException {
        /* Must be in clean state */
        checkStatus(GordianAgreementStatus.CLEAN);

        /* Access the sequence */
        final GordianAgreementMessageASN1 myClientHello = GordianAgreementMessageASN1.getInstance(pClientHello);
        myClientHello.checkMessageType(GordianMessageType.CLIENTHELLO);

        /* Accept the ASN1 */
        final GordianAgreementMessageASN1 myServerHello = acceptClientHelloASN1(pServer, myClientHello);
        return myServerHello.getEncodedBytes();
    }

    /**
     * Accept the clientHello.
     * @param pServer the server keyPair
     * @param pClientHello the incoming clientHello message
     * @return the serverHello message
     * @throws GordianException on error
     */
    public abstract GordianAgreementMessageASN1 acceptClientHelloASN1(GordianKeyPair pServer,
                                                                      GordianAgreementMessageASN1 pClientHello) throws GordianException;

    /**
     * Process the incoming clientHello message request.
     * @param pClientHello the incoming clientHello message
     * @throws GordianException on error
     */
    protected void processClientHelloASN1(final GordianAgreementMessageASN1 pClientHello) throws GordianException {
        /* Parse the request */
        parseClientHelloASN1(pClientHello);

        /* Parse the ephemeral encoding */
        final X509EncodedKeySpec myEncodedKeySpec = pClientHello.getEphemeral();

        /* Create ephemeral key */
        final GordianKeyPairFactory myFactory = getFactory().getAsyncFactory().getKeyPairFactory();
        final GordianKeyPairSpec myKeySpec = myFactory.determineKeyPairSpec(myEncodedKeySpec);
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(myKeySpec);
        theServerEphemeral = myGenerator.generateKeyPair();

        /* Derive partner ephemeral key */
        theClientEphemeral = myGenerator.derivePublicOnlyKeyPair(myEncodedKeySpec);

        /* Create the new serverIV */
        newServerIV();
    }

    /**
     * build the serverHello message.
     * @param pServer the server keyPair
     * @return the serverHello message
     * @throws GordianException on error
     */
    protected GordianAgreementMessageASN1 buildServerHelloASN1(final GordianKeyPair pServer) throws GordianException {
        /* Obtain the encoding for the server ephemeral publicKey */
        final GordianKeyPairFactory myFactory = getFactory().getAsyncFactory().getKeyPairFactory();
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(theServerEphemeral.getKeyPairSpec());
        final byte[] myClientEncoded = myGenerator.getX509Encoding(theClientEphemeral).getEncoded();
        final X509EncodedKeySpec myServerKeySpec = myGenerator.getX509Encoding(theServerEphemeral);

        /* Create the signer */
        final GordianSignatureSpec mySpec = getFactory().getAsyncFactory().getSignatureFactory().defaultForKeyPair(pServer.getKeyPairSpec());
        final GordianCoreSignatureFactory mySigns = (GordianCoreSignatureFactory) getFactory().getAsyncFactory().getSignatureFactory();
        final AlgorithmIdentifier myAlgId = mySigns.getIdentifierForSpecAndKeyPair(mySpec, pServer);
        final GordianSignature mySigner = mySigns.createSigner(mySpec);

        /* Build the signature */
        mySigner.initForSigning(GordianSignParams.keyPair(pServer));
        mySigner.update(myClientEncoded);
        mySigner.update(getClientIV());
        mySigner.update(myServerKeySpec.getEncoded());
        mySigner.update(getServerIV());
        final byte[] mySignature = mySigner.sign();

        /* Build the server hello */
        return buildServerHello(myServerKeySpec, myAlgId, mySignature);
    }

    @Override
    public void acceptServerHello(final GordianKeyPair pServer,
                                  final byte[] pServerHello) throws GordianException {
        /* Must be in clean state */
        checkStatus(GordianAgreementStatus.AWAITING_SERVERHELLO);

        /* Access the sequence */
        final GordianAgreementMessageASN1 myServerHello = GordianAgreementMessageASN1.getInstance(pServerHello);
        myServerHello.checkMessageType(GordianMessageType.SERVERHELLO);

        /* Accept the ASN1 */
        acceptServerHelloASN1(pServer, myServerHello);
    }

    /**
     * Accept the serverHello.
     * @param pServer the server keyPair
     * @param pServerHello the incoming serverHello message
     * @throws GordianException on error
     */
    public abstract void acceptServerHelloASN1(GordianKeyPair pServer,
                                               GordianAgreementMessageASN1 pServerHello) throws GordianException;

    /**
     * Process the serverHello.
     * @param pServer the server keyPair
     * @param pServerHello the serverHello message
     * @throws GordianException on error
     */
    protected void processServerHelloASN1(final GordianKeyPair pServer,
                                          final GordianAgreementMessageASN1 pServerHello) throws GordianException {
        /* Obtain keySpec */
        parseServerHelloASN1(pServerHello);
        final X509EncodedKeySpec myKeySpec = pServerHello.getEphemeral();

        /* Derive partner ephemeral key */
        final GordianKeyPairFactory myFactory = getFactory().getAsyncFactory().getKeyPairFactory();
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(theClientEphemeral.getKeyPairSpec());
        theServerEphemeral = myGenerator.derivePublicOnlyKeyPair(myKeySpec);
        final byte[] myClientEncoded = myGenerator.getX509Encoding(theClientEphemeral).getEncoded();

        /* Create the signer */
        final GordianCoreSignatureFactory mySigns = (GordianCoreSignatureFactory) getFactory().getAsyncFactory().getSignatureFactory();
        final AlgorithmIdentifier myAlgId = pServerHello.getSignatureId();
        final byte[] mySignature = pServerHello.getSignature();
        final GordianSignatureSpec mySignSpec = mySigns.getSpecForIdentifier(myAlgId);
        final GordianSignature mySigner = mySigns.createSigner(mySignSpec);

        /* Build the signature */
        mySigner.initForVerify(GordianSignParams.keyPair(pServer));
        mySigner.update(myClientEncoded);
        mySigner.update(getClientIV());
        mySigner.update(myKeySpec.getEncoded());
        mySigner.update(getServerIV());
        if (!mySigner.verify(mySignature)) {
            throw new GordianDataException("Signature failed");
        }
    }
}
