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

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementStatus;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairHandshakeAgreement;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * New Handshake Basic Agreement.
 */
public abstract class GordianCoreBasicAgreement
        extends GordianCoreAgreement
        implements GordianKeyPairHandshakeAgreement {
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
    public byte[] createClientHello(final GordianKeyPair pClient) throws OceanusException {
        /* Check the keyPair */
        checkKeyPair(pClient);

        /* Store the keyPair */
        theClient = pClient;

        /* Create the clientHello message */
        final byte[] myClientHello = buildClientHello();

        /* Set status */
        setStatus(GordianAgreementStatus.AWAITING_SERVERHELLO);

        /* Return the clientHello */
        return myClientHello;
    }

    /**
     * Process the incoming clientHello message request.
     * @param pServer the server keyPair
     * @param pClientHello the incoming clientHello message
     * @throws OceanusException on error
     */
    protected void processClientHello(final GordianKeyPair pServer,
                                      final byte[] pClientHello) throws OceanusException {
        /* Check the keyPair */
        checkKeyPair(pServer);

        /* Parse the request */
        parseClientHello(pClientHello);

        /* Create the new serverIV */
        newServerIV();
    }

    /**
     * Process the serverHello.
     * @param pServerHello the serverHello message
     * @throws OceanusException on error
     */
    protected void processServerHello(final byte[] pServerHello) throws OceanusException {
        /* Parse the server hello */
        parseServerHello(pServerHello);
    }

    /**
     * Build clientConfirm message.
     * @return the clientConfirm message
     * @throws OceanusException on error
     */
    protected byte[] buildClientConfirm() throws OceanusException {
        /* There is never a client confirm */
        return null;
    }

    @Override
    public void acceptClientConfirm(final byte[] pClientConfirm) throws OceanusException {
        /* We will never hit this status so this will reject the request */
        checkStatus(GordianAgreementStatus.AWAITING_CLIENTCONFIRM);
    }
}
