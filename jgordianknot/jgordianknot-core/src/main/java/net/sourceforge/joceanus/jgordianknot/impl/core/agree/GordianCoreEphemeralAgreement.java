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
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianAsymFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.mac.GordianCoreMac;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Ephemeral Agreement.
 */
public abstract class GordianCoreEphemeralAgreement
        extends GordianCoreAgreement
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
        /* Check the keyPair */
        checkKeyPair(pClient);

        /* Store the keyPair */
        theClient = pClient;

        /* Create ephemeral key */
        final GordianAsymFactory myAsym = getFactory().getAsymmetricFactory();
        final GordianKeyPairGenerator myGenerator = myAsym.getKeyPairGenerator(theClient.getKeySpec());
        theClientEphemeral = myGenerator.generateKeyPair();
        final X509EncodedKeySpec myKeySpec = myGenerator.getX509Encoding(theClientEphemeral);
        final byte[] myKeyBytes = myKeySpec.getEncoded();

        /* Create the clientHello message */
        final byte[] myClientHello = buildClientHello(myKeyBytes);

        /* Set status */
        setStatus(GordianAgreementStatus.AWAITING_SERVERHELLO);

        /* Return the clientHello */
        return myClientHello;
    }

    /**
     * Process the incoming clientHello message request.
     * @param pClient the client keyPair
     * @param pServer the server keyPair
     * @param pClientHello the incoming clientHello message
     * @throws OceanusException on error
     */
    protected void processClientHello(final GordianKeyPair pClient,
                                      final GordianKeyPair pServer,
                                      final byte[] pClientHello) throws OceanusException {
        /* Check the keyPair */
        checkKeyPair(pClient);
        checkKeyPair(pServer);

        /* Store the keyPair */
        theClient = pClient;
        theServer = pServer;

        /* Parse the request */
        final byte[] myKeyBytes = parseClientHello(pClientHello);

        /* Parse the ephemeral encoding */
        final X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(myKeyBytes);

        /* Create ephemeral key */
        final GordianAsymFactory myAsym = getFactory().getAsymmetricFactory();
        final GordianKeyPairGenerator myGenerator = myAsym.getKeyPairGenerator(theServer.getKeySpec());
        theServerEphemeral = myGenerator.generateKeyPair();

        /* Derive partner ephemeral key */
        theClientEphemeral = myGenerator.derivePublicOnlyKeyPair(myKeySpec);

        /* Create the new serverIV */
        newServerIV();
    }

    @Override
    protected byte[] buildServerHello() throws OceanusException {
        final GordianAsymFactory myAsym = getFactory().getAsymmetricFactory();
        final GordianKeyPairGenerator myGenerator = myAsym.getKeyPairGenerator(theServerEphemeral.getKeySpec());
        return buildServerHello(myGenerator.getX509Encoding(theServerEphemeral).getEncoded(), theServerConfirmation);
    }

    /**
     * Process the serverHello.
     * @param pServer the server keyPair
     * @param pServerHello the serverHello message
     * @throws OceanusException on error
     */
    protected void processServerHello(final GordianKeyPair pServer,
                                      final byte[] pServerHello) throws OceanusException {
        /* Check the keyPair */
        checkKeyPair(pServer);

        /* Store the server keyPair */
        theServer = pServer;

        /* Obtain keySpec */
        final byte[] myBytes = parseServerHello(pServerHello);
        final X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(myBytes);

        /* Derive partner ephemeral key */
        final GordianAsymFactory myAsym = getFactory().getAsymmetricFactory();
        final GordianKeyPairGenerator myGenerator = myAsym.getKeyPairGenerator(theClient.getKeySpec());
        theServerEphemeral = myGenerator.derivePublicOnlyKeyPair(myKeySpec);
    }

    /**
     * Build clientConfirm message.
     * @return the clientConfirm message
     * @throws OceanusException on error
     */
    protected byte[] buildClientConfirm() throws OceanusException {
        /* If there is no client confirmation, return null */
        if (theClientConfirmation == null) {
            return null;
        }

        /* Create the response */
        final GordianCoreAgreementFactory myFactory = getAgreementFactory();
        final AlgorithmIdentifier myAlgId = myFactory.getIdentifierForSpec(getAgreementSpec());
        final GordianAgreementClientConfirmASN1 myClientConfirm
                = new GordianAgreementClientConfirmASN1(myAlgId, theClientConfirmation);
        return myClientConfirm.getEncodedBytes();
    }

    @Override
    public void acceptClientConfirm(final byte[] pClientConfirm) throws OceanusException {
        /* Must be in awaiting clientConfirm state */
        checkStatus(GordianAgreementStatus.AWAITING_CLIENTCONFIRM);

        /* Access the sequence */
        final GordianAgreementClientConfirmASN1 myClientConfirm = GordianAgreementClientConfirmASN1.getInstance(pClientConfirm);

        /* Access message parts */
        final AlgorithmIdentifier myAlgId = myClientConfirm.getAgreementId();
        final byte[] myConfirm = myClientConfirm.getConfirmation();

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

        /* Set result available status */
        setStatus(GordianAgreementStatus.RESULT_AVAILABLE);
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
        final GordianFactory myFactory = getFactory();
        final GordianMacFactory myMacs = myFactory.getMacFactory();
        final GordianMacSpec mySpec = GordianMacSpec.hMac(GordianDigestType.WHIRLPOOL);
        final GordianCoreMac myMac = (GordianCoreMac) myMacs.createMac(mySpec);
        myMac.initKeyBytes(myKey);

        /* Access the keyPairGenerator and obtain public encodings */
        final GordianAsymFactory myAsym = myFactory.getAsymmetricFactory();
        final GordianKeyPairGenerator myGenerator = myAsym.getKeyPairGenerator(getClientKeyPair().getKeySpec());
        final byte[] myClient = myGenerator.getX509Encoding(getClientKeyPair()).getEncoded();
        final byte[] myClientEphemeral = myGenerator.getX509Encoding(getClientEphemeralKeyPair()).getEncoded();
        final byte[] myServer = myGenerator.getX509Encoding(getServerKeyPair()).getEncoded();
        final byte[] myServerEphemeral = myGenerator.getX509Encoding(getServerEphemeralKeyPair()).getEncoded();

        /* Build Server Confirmation tag */
        myMac.update(GordianAgreementServerHelloASN1.MSG_ID);
        myMac.update(myServer);
        myMac.update(myClient);
        myMac.update(myServerEphemeral);
        myMac.update(myClientEphemeral);
        theServerConfirmation = myMac.finish();

        /* Check that the server value matches any sent value */
        final byte[] myReceivedTag = getConfirmationTag();
        if (myReceivedTag != null
                && !Arrays.constantTimeAreEqual(myReceivedTag, theServerConfirmation)) {
            throw new GordianDataException("Confirmation failed");
        }

        /* Build Client Confirmation tag */
        myMac.update(GordianAgreementClientConfirmASN1.MSG_ID);
        myMac.update(myClient);
        myMac.update(myServer);
        myMac.update(myClientEphemeral);
        myMac.update(myServerEphemeral);
        theClientConfirmation = myMac.finish();
    }
}
