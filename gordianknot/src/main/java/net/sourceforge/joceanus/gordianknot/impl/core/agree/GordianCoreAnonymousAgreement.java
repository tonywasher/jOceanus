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
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAnonymousAgreement;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.impl.core.agree.GordianAgreementMessageASN1.GordianMessageType;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;

/**
 * Encapsulation Agreement.
 */
public abstract class GordianCoreAnonymousAgreement
        extends GordianCoreKeyPairAgreement
        implements GordianAnonymousAgreement {
    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSpec the agreementSpec
     */
    protected GordianCoreAnonymousAgreement(final GordianCoreFactory pFactory,
                                            final GordianAgreementSpec pSpec) {
        super(pFactory, pSpec);
    }

    @Override
    public byte[] createClientHello(final GordianKeyPair pServer) throws GordianException {
        /* Create the clientHello and extract the encoded bytes */
        final GordianAgreementMessageASN1 myHello = createClientHelloASN1(pServer);
        return myHello.getEncodedBytes();
    }

    /**
     * Create the clientHello ASN1.
     * @param pServer the server keyPair
     * @return the clientHello message
     * @throws GordianException on error
     */
    public abstract GordianAgreementMessageASN1 createClientHelloASN1(GordianKeyPair pServer) throws GordianException;

    @Override
    public void acceptClientHello(final GordianKeyPair pServer,
                                  final byte[] pClientHello) throws GordianException {
        /* Must be in clean state */
        checkStatus(GordianAgreementStatus.CLEAN);

        /* Access the sequence */
        final GordianAgreementMessageASN1 myClientHello = GordianAgreementMessageASN1.getInstance(pClientHello);
        myClientHello.checkMessageType(GordianMessageType.CLIENTHELLO);

        /* Process the clientHello */
        parseClientHelloASN1(myClientHello);
        acceptClientHelloASN1(pServer, myClientHello);
    }

    /**
     * Accept the clientHello.
     * @param pServer the server keyPair
     * @param pClientHello the incoming clientHello message
     * @throws GordianException on error
     */
    public abstract void acceptClientHelloASN1(GordianKeyPair pServer,
                                               GordianAgreementMessageASN1 pClientHello)  throws GordianException;
}
