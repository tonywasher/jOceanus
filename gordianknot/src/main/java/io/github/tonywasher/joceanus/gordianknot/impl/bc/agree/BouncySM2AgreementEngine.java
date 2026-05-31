/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.gordianknot.impl.bc.agree;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyEllipticKeyPair.BouncyECPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyEllipticKeyPair.BouncyECPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree.GordianCoreAgreementSpec;
import org.bouncycastle.crypto.agreement.SM2KeyExchange;
import org.bouncycastle.crypto.params.ParametersWithID;
import org.bouncycastle.crypto.params.SM2KeyExchangePrivateParameters;
import org.bouncycastle.crypto.params.SM2KeyExchangePublicParameters;

/**
 * SM2 Agreement Engine.
 */
public class BouncySM2AgreementEngine
        extends BouncyAgreementBase {
    /**
     * Empty byteArray.
     */
    private static final byte[] EMPTY = new byte[0];

    /**
     * Key length.
     */
    private static final int KEYLEN = 128;

    /**
     * The agreement.
     */
    private final SM2KeyExchange theAgreement;

    /**
     * Constructor.
     *
     * @param pFactory the security factory
     * @param pSpec    the agreementSpec
     * @throws GordianException on error
     */
    BouncySM2AgreementEngine(final GordianCoreAgreementFactory pFactory,
                             final GordianCoreAgreementSpec pSpec) throws GordianException {
        /* Initialize underlying class */
        super(pFactory, pSpec);

        /* Create the agreement */
        theAgreement = new SM2KeyExchange();
    }

    @Override
    public void processClientHello() throws GordianException {
        /* Access keys */
        final BouncyECPublicKey myClientPublic = (BouncyECPublicKey) getPublicKey(getClientKeyPair());
        final BouncyECPublicKey myClientEphPublic = (BouncyECPublicKey) getPublicKey(getClientEphemeral());
        final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getPrivateKey(getServerKeyPair());
        final BouncyECPrivateKey myEphPrivate = (BouncyECPrivateKey) getPrivateKey(getServerEphemeral());

        /* Access IDs */
        final byte[] myClientID = getClientName() == null ? EMPTY : getClientName();
        final byte[] myServerID = getServerName() == null ? EMPTY : getServerName();

        /* Derive the secret */
        final SM2KeyExchangePrivateParameters myPrivParams = new SM2KeyExchangePrivateParameters(false,
                myPrivate.getPrivateKey(), myEphPrivate.getPrivateKey());
        final ParametersWithID myPrivIDParams = new ParametersWithID(myPrivParams, myServerID);
        theAgreement.init(myPrivIDParams);
        final SM2KeyExchangePublicParameters myPubParams = new SM2KeyExchangePublicParameters(myClientPublic.getPublicKey(),
                myClientEphPublic.getPublicKey());
        final ParametersWithID myPubIDParams = new ParametersWithID(myPubParams, myClientID);

        /* If we are confirming */
        if (getSpec().withConfirm()) {
            /* Create agreement and confirmation tags */
            final byte[][] myResults = theAgreement.calculateKeyWithConfirmation(KEYLEN, null, myPubIDParams);

            /* Store the confirmationTags */
            setServerConfirm(myResults[1]);
            setClientConfirm(myResults[2]);

            /* Store the secret */
            storeSecret(myResults[0]);

            /* else standard agreement */
        } else {
            /* Calculate and store the secret */
            storeSecret(theAgreement.calculateKey(KEYLEN, myPubIDParams));
        }
    }

    @Override
    public void processServerHello() throws GordianException {
        /* Access keys */
        final BouncyECPublicKey myServerPublic = (BouncyECPublicKey) getPublicKey(getServerKeyPair());
        final BouncyECPublicKey myServerEphPublic = (BouncyECPublicKey) getPublicKey(getServerEphemeral());
        final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getPrivateKey(getClientKeyPair());
        final BouncyECPrivateKey myEphPrivate = (BouncyECPrivateKey) getPrivateKey(getClientEphemeral());

        /* Access IDs */
        final byte[] myClientID = getClientName() == null ? EMPTY : getClientName();
        final byte[] myServerID = getServerName() == null ? EMPTY : getServerName();

        /* Derive the secret */
        final SM2KeyExchangePrivateParameters myPrivParams = new SM2KeyExchangePrivateParameters(true,
                myPrivate.getPrivateKey(), myEphPrivate.getPrivateKey());
        final ParametersWithID myPrivIDParams = new ParametersWithID(myPrivParams, myClientID);
        theAgreement.init(myPrivIDParams);
        final SM2KeyExchangePublicParameters myPubParams = new SM2KeyExchangePublicParameters(myServerPublic.getPublicKey(),
                myServerEphPublic.getPublicKey());
        final ParametersWithID myPubIDParams = new ParametersWithID(myPubParams, myServerID);

        /* If we are confirming */
        if (getSpec().withConfirm()) {
            /* Obtain confirmationTag in serverHello */
            final byte[] myConfirm = getServerConfirm();

            /* Protect against exception */
            try {
                /* Create agreement and confirmation tags */
                final byte[][] myResults = theAgreement.calculateKeyWithConfirmation(KEYLEN, myConfirm, myPubIDParams);

                /* Store the confirmationTag */
                if (setClientConfirm(myResults[1])) {
                    /* Store the secret */
                    storeSecret(myResults[0]);
                }

                /* Catch mismatch on confirmation tag */
            } catch (IllegalStateException e) {
                getBuilder().setError("Server Confirmation failed");
            }

            /* else standard agreement */
        } else {
            /* Calculate and store the secret */
            storeSecret(theAgreement.calculateKey(KEYLEN, myPubIDParams));
        }
    }
}
