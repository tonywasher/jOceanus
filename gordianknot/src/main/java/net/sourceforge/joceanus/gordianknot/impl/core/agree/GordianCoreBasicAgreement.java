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
package net.sourceforge.joceanus.gordianknot.impl.core.agree;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementStatus;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianHandshakeAgreement;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.impl.core.agree.GordianAgreementMessageASN1.GordianMessageType;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.oceanus.OceanusException;

/**
 * New Handshake Basic Agreement.
 */
public abstract class GordianCoreBasicAgreement
        extends GordianCoreKeyPairAgreement
        implements GordianHandshakeAgreement {
    /**
     * The client KeyPair.
     */
    private GordianKeyPair theClient;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSpec the agreementSpec
     */
    protected GordianCoreBasicAgreement(final GordianCoreFactory pFactory,
                                        final GordianAgreementSpec pSpec) {
        super(pFactory, pSpec);
    }

    /**
     * Obtain the Client keyPair.
     * @return  the keyPair
     */
    protected GordianKeyPair getClientKeyPair() {
        return theClient;
    }

    @Override
    public byte[] createClientHello(final GordianKeyPair pServer) throws OceanusException {
        /* Create the clientHello and extract the encoded bytes */
        return createClientHelloASN1(pServer).getEncodedBytes();
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

        /* Create the clientHello message */
        final GordianAgreementMessageASN1 myClientHello = buildClientHelloASN1();

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
     * @param pServer the server keyPair
     * @param pClientHello the incoming clientHello message
     * @throws OceanusException on error
     */
    protected void processClientHelloASN1(final GordianKeyPair pServer,
                                          final GordianAgreementMessageASN1 pClientHello) throws OceanusException {
        /* Check the keyPair */
        checkKeyPair(pServer);

        /* Parse the request */
        parseClientHelloASN1(pClientHello);

        /* Create the new serverIV */
        newServerIV();
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
        acceptServerHelloASN1(pServer, myServerHello);
        return null;
    }

    /**
     * Accept the serverHello.
     * @param pServer the server keyPair
     * @param pServerHello the incoming serverHello message
     * @throws OceanusException on error
     */
    public abstract void acceptServerHelloASN1(GordianKeyPair pServer,
                                               GordianAgreementMessageASN1 pServerHello) throws OceanusException;

    /**
     * Process the serverHello.
     * @param pServerHello the serverHello message
     * @throws OceanusException on error
     */
    protected void processServerHelloASN1(final GordianAgreementMessageASN1 pServerHello) throws OceanusException {
        /* Parse the server hello */
        parseServerHelloASN1(pServerHello);
    }

    @Override
    public void acceptClientConfirm(final byte[] pClientConfirm) throws OceanusException {
        /* We will never hit this status so this will reject the request */
        checkStatus(GordianAgreementStatus.AWAITING_CLIENTCONFIRM);
    }
}
