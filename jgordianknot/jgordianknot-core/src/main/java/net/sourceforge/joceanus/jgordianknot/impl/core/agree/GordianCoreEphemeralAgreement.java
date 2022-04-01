/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2022 Tony Washer
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

import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementStatus;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementType;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianHandshakeAgreement;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianAgreementMessageASN1.GordianMessageType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.mac.GordianCoreMac;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Ephemeral Agreement.
 */
public abstract class GordianCoreEphemeralAgreement
        extends GordianCoreKeyPairAgreement
        implements GordianHandshakeAgreement {
    /**
     * The client KeyPair.
     */
    private GordianKeyPair theClient;

    /**
     * The server KeyPair.
     */
    private GordianKeyPair theServer;

    /**
     * The client ephemeral KeyPair.
     */
    private GordianKeyPair theClientEphemeral;

    /**
     * The server ephemeral KeyPair.
     */
    private GordianKeyPair theServerEphemeral;

    /**
     * The client confirmation tag.
     */
    private byte[] theClientConfirmation;

    /**
     * The server confirmation tag.
     */
    private byte[] theServerConfirmation;

    /**
     * The serverId.
     */
    private Integer theServerId;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSpec the agreementSpec
     */
    protected GordianCoreEphemeralAgreement(final GordianCoreFactory pFactory,
                                            final GordianAgreementSpec pSpec) {
        super(pFactory, pSpec);
    }

    /**
     * Obtain the client keyPair.
     * @return  the keyPair
     */
    protected GordianKeyPair getClientKeyPair() {
        return theClient;
    }

    /**
     * Obtain the client keyPair.
     * @return  the keyPair
     */
    protected GordianKeyPair getServerKeyPair() {
        return theServer;
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

    /**
     * Obtain the server Confirmation Tag.
     * @return  the tag
     */
    protected byte[] getServerConfirmationTag() {
        return theServerConfirmation;
    }

    @Override
    public void reset() {
        /* Reset underlying details */
        super.reset();

        /* Reset client details */
        theClient = null;
        theClientEphemeral = null;
        theClientConfirmation = null;

        /* Reset server details */
        theServer = null;
        theServerId = null;
        theServerEphemeral = null;
        theServerConfirmation = null;
    }

    @Override
    protected void processSecret(final byte[] pSecret) throws OceanusException {
        /* If we are using confirmation and are not SM2 */
        final GordianAgreementSpec mySpec = getAgreementSpec();
        if (Boolean.TRUE.equals(mySpec.withConfirm())
                && mySpec.getAgreementType() != GordianAgreementType.SM2) {
            /* calculate the confirmation tags */
            calculateConfirmationTags(pSecret);
        }

        /* Pass the call down */
        super.processSecret(pSecret);
    }

    /**
     * Store client.
     * @param pClient the client
     */
    protected void storeClient(final GordianKeyPair pClient) {
        /* Store the client */
        theClient = pClient;
    }

    /**
     * Store server.
     * @param pServer the server
     */
    protected void storeServer(final GordianKeyPair pServer) {
        /* Store the server */
        theServer = pServer;
    }

    /**
     * Store client ephemeral.
     * @param pEphemeral the client ephemeral
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

    /**
     * Record confirmation tags.
     * @param pServerConfirmation the server confirmation
     * @param pClientConfirmation the client confirmation
     */
    protected void storeConfirmationTags(final byte[] pServerConfirmation,
                                         final byte[] pClientConfirmation) {
        theServerConfirmation = pServerConfirmation;
        theClientConfirmation = pClientConfirmation;
    }

    /**
     * Record confirmation tag.
     * @param pClientConfirmation the client confirmation
     */
    protected void storeConfirmationTag(final byte[] pClientConfirmation) {
        theClientConfirmation = pClientConfirmation;
    }

    @Override
    public byte[] createClientHello(final GordianKeyPair pClient) throws OceanusException {
        /* Create the clientHello and extract the encoded bytes */
        return createClientHelloASN1(pClient).getEncodedBytes();
    }

    /**
     * Create the clientHello ASN1.
     * @param pClient the client keyPair
     * @return the clientHello message
     * @throws OceanusException on error
     */
    public GordianAgreementMessageASN1 createClientHelloASN1(final GordianKeyPair pClient) throws OceanusException {
        /* Check the keyPair */
        checkKeyPair(pClient);

        /* Store the keyPair */
        theClient = pClient;

        /* Create ephemeral key */
        final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(theClient.getKeyPairSpec());
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
    public byte[] acceptClientHello(final GordianKeyPair pClient,
                                    final GordianKeyPair pServer,
                                    final byte[] pClientHello) throws OceanusException {
        /* Must be in clean state */
        checkStatus(GordianAgreementStatus.CLEAN);

        /* Access the sequence */
        final GordianAgreementMessageASN1 myClientHello = GordianAgreementMessageASN1.getInstance(pClientHello);
        myClientHello.checkMessageType(GordianMessageType.CLIENTHELLO);

        /* Accept the ASN1 */
        final GordianAgreementMessageASN1 myServerHello = acceptClientHelloASN1(pClient, pServer, myClientHello);
        return myServerHello.getEncodedBytes();
    }

    /**
     * Accept the clientHello.
     * @param pClient the client keyPair
     * @param pServer the server keyPair
     * @param pClientHello the incoming clientHello message
     * @return the serverHello message
     * @throws OceanusException on error
     */
    public abstract GordianAgreementMessageASN1 acceptClientHelloASN1(GordianKeyPair pClient,
                                                                      GordianKeyPair pServer,
                                                                      GordianAgreementMessageASN1 pClientHello) throws OceanusException;

    /**
     * Process the incoming clientHello message request.
     * @param pClient the client keyPair
     * @param pServer the server keyPair
     * @param pClientHello the incoming clientHello message
     * @throws OceanusException on error
     */
    protected void processClientHelloASN1(final GordianKeyPair pClient,
                                          final GordianKeyPair pServer,
                                          final GordianAgreementMessageASN1 pClientHello) throws OceanusException {
        /* Check the keyPair */
        checkKeyPair(pClient);
        checkKeyPair(pServer);

        /* Store the keyPair */
        theClient = pClient;
        theServer = pServer;

        /* Parse the request */
        parseClientHelloASN1(pClientHello);

        /* Parse the ephemeral encoding */
        final X509EncodedKeySpec myKeySpec = pClientHello.getEphemeral();

        /* Create ephemeral key */
        final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(theServer.getKeyPairSpec());
        theServerEphemeral = myGenerator.generateKeyPair();

        /* Derive partner ephemeral key */
        theClientEphemeral = myGenerator.derivePublicOnlyKeyPair(myKeySpec);

        /* Create the new serverIV */
        newServerIV();
    }

    @Override
    protected GordianAgreementMessageASN1 buildServerHello() throws OceanusException {
        /* Add server ephemeral and any server confirmation tag to the serverHello */
        final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(theServerEphemeral.getKeyPairSpec());
        final GordianAgreementMessageASN1 myHello = buildServerHello(myGenerator.getX509Encoding(theServerEphemeral), theServerConfirmation);
        theServerId = myHello.getServerId();
        return myHello;
    }

    @Override
    public byte[] acceptServerHello(final GordianKeyPair pServer,
                                    final byte[] pServerHello) throws OceanusException {
        /* Must be in clean state */
        checkStatus(GordianAgreementStatus.AWAITING_SERVERHELLO);

        /* Access the sequence */
        final GordianAgreementMessageASN1 myServerHello = GordianAgreementMessageASN1.getInstance(pServerHello);
        myServerHello.checkMessageType(GordianMessageType.SERVERHELLO);

        /* Accept the ASN1 */
        final GordianAgreementMessageASN1 myConfirm = acceptServerHelloASN1(pServer, myServerHello);
        return myConfirm == null ? null : myConfirm.getEncodedBytes();
    }

    /**
     * Accept the serverHello.
     * @param pServer the server keyPair
     * @param pServerHello the serverHello message
     * @return the clientConfirm (or null if no confirmation)
     * @throws OceanusException on error
     */
    public abstract GordianAgreementMessageASN1 acceptServerHelloASN1(GordianKeyPair pServer,
                                                                      GordianAgreementMessageASN1 pServerHello) throws OceanusException;

    /**
     * Process the serverHello.
     * @param pServer the server keyPair
     * @param pServerHello the serverHello message
     * @throws OceanusException on error
     */
    protected void processServerHelloASN1(final GordianKeyPair pServer,
                                          final GordianAgreementMessageASN1 pServerHello) throws OceanusException {
        /* Check the keyPair */
        checkKeyPair(pServer);

        /* Store the server keyPair */
        theServer = pServer;

        /* Obtain details from the serverHello */
        parseServerHelloASN1(pServerHello);
        theServerConfirmation = pServerHello.getConfirmation();
        theServerId = pServerHello.getServerId();
        final X509EncodedKeySpec myKeySpec = pServerHello.getEphemeral();

        /* Derive partner ephemeral key */
        final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(theClient.getKeyPairSpec());
        theServerEphemeral = myGenerator.derivePublicOnlyKeyPair(myKeySpec);
    }

    /**
     * Build clientConfirm message.
     * @return the clientConfirm message
     * @throws OceanusException on error
     */
    protected GordianAgreementMessageASN1 buildClientConfirmASN1() throws OceanusException {
        /* If there is no client confirmation, return null */
        if (theClientConfirmation == null) {
            return null;
        }

        /* Create the response */
        final GordianCoreAgreementFactory myFactory = getAgreementFactory();
        final AlgorithmIdentifier myAlgId = myFactory.getIdentifierForSpec(getAgreementSpec());
        return GordianAgreementMessageASN1.newClientConfirm(theServerId)
                .setAgreementId(myAlgId)
                .setConfirmation(theClientConfirmation);
    }

    @Override
    public void acceptClientConfirm(final byte[] pClientConfirm) throws OceanusException {
        /* Must be in awaiting clientConfirm state */
        checkStatus(GordianAgreementStatus.AWAITING_CLIENTCONFIRM);

        /* Access the sequence */
        final GordianAgreementMessageASN1 myClientConfirm = GordianAgreementMessageASN1.getInstance(pClientConfirm);
        myClientConfirm.checkMessageType(GordianMessageType.CLIENTCONFIRM);

        /* Process the confirmation */
        acceptClientConfirmASN1(myClientConfirm);

        /* Set result available status */
        setStatus(GordianAgreementStatus.RESULT_AVAILABLE);
    }

    /**
     * Accept a client confirm message.
     * @param pClientConfirm the confirm message
     * @throws OceanusException on error
     */
    public void acceptClientConfirmASN1(final GordianAgreementMessageASN1 pClientConfirm) throws OceanusException {
        /* Access message parts */
        final AlgorithmIdentifier myAlgId = pClientConfirm.getAgreementId();
        final byte[] myConfirm = pClientConfirm.getConfirmation();

        /* Check serverId */
        if (!Objects.equals(theServerId, pClientConfirm.getServerId())) {
            throw new GordianDataException("Mismatch on serverId");
        }

        /* Check agreementSpec */
        final GordianCoreAgreementFactory myFactory = getAgreementFactory();
        final GordianAgreementSpec mySpec = myFactory.getSpecForIdentifier(myAlgId);
        if (!Objects.equals(mySpec, getAgreementSpec())) {
            throw new GordianDataException(ERROR_INVSPEC);
        }

        /* Validate the confirmation */
        if (!Arrays.constantTimeAreEqual(theClientConfirmation, myConfirm)) {
            throw new GordianDataException("Confirmation failed");
        }
    }

    /**
     * Create the confirmation tags.
     * @param pSecret the secret
     * @throws OceanusException on error
     */
    private void calculateConfirmationTags(final byte[] pSecret) throws OceanusException {
        /* Derive the key */
        final byte[] myKey = new byte[GordianLength.LEN_512.getByteLength()];
        calculateDerivedSecret(GordianDigestType.SHA2, pSecret, myKey);

        /* Create the hMac and initialise with the key */
        final GordianFactory myBaseFactory = getFactory();
        final GordianMacFactory myMacs = myBaseFactory.getMacFactory();
        final GordianMacSpec mySpec = GordianMacSpec.hMac(GordianDigestType.WHIRLPOOL);
        final GordianCoreMac myMac = (GordianCoreMac) myMacs.createMac(mySpec);
        myMac.initKeyBytes(myKey);

        /* Access the keyPairGenerator and obtain public encodings */
        final GordianKeyPairFactory myFactory = myBaseFactory.getKeyPairFactory();
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(getClientKeyPair().getKeyPairSpec());
        final byte[] myClient = myGenerator.getX509Encoding(getClientKeyPair()).getEncoded();
        final byte[] myClientEphemeral = myGenerator.getX509Encoding(getClientEphemeralKeyPair()).getEncoded();
        final byte[] myServer = myGenerator.getX509Encoding(getServerKeyPair()).getEncoded();
        final byte[] myServerEphemeral = myGenerator.getX509Encoding(getServerEphemeralKeyPair()).getEncoded();

        /* Build Server Confirmation tag */
        myMac.update(myServer);
        myMac.update(myClient);
        myMac.update(myServerEphemeral);
        myMac.update(myClientEphemeral);
        final byte[] myServerConfirmation = myMac.finish();

        /* Check that the server value matches any sent value */
        if (theServerConfirmation != null
                && !Arrays.constantTimeAreEqual(myServerConfirmation, theServerConfirmation)) {
            throw new GordianDataException("Confirmation failed");
        }
        theServerConfirmation = myServerConfirmation;

        /* Build Client Confirmation tag */
        myMac.update(myClient);
        myMac.update(myServer);
        myMac.update(myClientEphemeral);
        myMac.update(myServerEphemeral);
        theClientConfirmation = myMac.finish();
    }
}
