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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSM9Spec.GordianSM9EncryptType;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncySM9KeyPair.BouncySM9EncMasterPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncySM9KeyPair.BouncySM9EncUserPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree.GordianCoreAgreementSpec;
import org.bouncycastle.crypto.agreement.SM9KeyExchange;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.sm9.SM9Curve;

/**
 * SM9 Exchange Agreement Engine.
 */
public class BouncySM9XchgAgreementEngine
        extends BouncyAgreementBase {
    /**
     * The SM9KeyExchange.
     */
    private SM9KeyExchange theExchange;

    /**
     * Constructor.
     *
     * @param pFactory the security factory
     * @param pSpec    the agreementSpec
     * @throws GordianException on error
     */
    BouncySM9XchgAgreementEngine(final GordianCoreAgreementFactory pFactory,
                                 final GordianCoreAgreementSpec pSpec) throws GordianException {
        /* Initialize underlying class */
        super(pFactory, pSpec);
    }

    /**
     * Obtain the User privateKey.
     *
     * @param pKeyPair  the keyPair
     * @param pIdentity the identity
     * @return the privateKey
     * @throws GordianException on error
     */
    private BouncySM9EncUserPrivateKey getUserPrivateKey(final GordianKeyPair pKeyPair,
                                                         final byte[] pIdentity) throws GordianException {
        final GordianSM9EncryptType myKeyType = (GordianSM9EncryptType) pKeyPair.getKeyPairSpec().getSubSpec();
        return switch (myKeyType) {
            case ENCMASTER -> {
                final BouncySM9EncMasterPrivateKey myPrivate = (BouncySM9EncMasterPrivateKey) getPrivateKey(pKeyPair);
                yield myPrivate.newUserPrivateKey(GordianSM9EncryptType.EXCHANGE, pIdentity);
            }
            case EXCHANGE -> (BouncySM9EncUserPrivateKey) getPrivateKey(pKeyPair);
            default -> throw new GordianDataException("Unsupported keyPairType: " + myKeyType);
        };
    }

    @Override
    public void buildClientHello() throws GordianException {
        /* Access keys */
        final BouncySM9EncUserPrivateKey myPrivate = getUserPrivateKey(getClientKeyPair(), getClientName());

        /* Create the exchange */
        theExchange = new SM9KeyExchange(myPrivate.getPrivateKey(), getServerName(), true);

        /* Generate the ephemeral key and stor as encapsulated */
        final ECPoint myEphemeral = theExchange.generateEphemeral(getRandom());
        setClientEncapsulated(SM9Curve.g1ToBytes(myEphemeral));
    }

    @Override
    public void processClientHello() throws GordianException {
        /* Access keys */
        final byte[] myEncapsulated = getClientEncapsulated();
        final ECPoint myClientEphemeral = SM9Curve.g1FromBytes(myEncapsulated, 0).normalize();
        final BouncySM9EncUserPrivateKey myPrivate = getUserPrivateKey(getServerKeyPair(), getServerName());

        /* Create the exchange */
        final SM9KeyExchange myExchange = new SM9KeyExchange(myPrivate.getPrivateKey(),
                getClientName(), false);

        /* Generate the ephemeral key */
        final ECPoint myEphemeral = myExchange.generateEphemeral(getRandom());
        setServerEncapsulated(SM9Curve.g1ToBytes(myEphemeral));

        /* Calculate and store the secret */
        storeSecret(myExchange.calculateKey(KEYLEN, myClientEphemeral));

        /* If we are confirming */
        if (getSpec().withConfirm()) {
            /* Store confirmations */
            setClientConfirm(myExchange.getInitiatorConfirmation());
            setServerConfirm(myExchange.getResponderConfirmation());
        }
    }

    @Override
    public void processServerHello() throws GordianException {
        /* Access keys */
        final byte[] myEncapsulated = getServerEncapsulated();
        final ECPoint myServerEphemeral = SM9Curve.g1FromBytes(myEncapsulated, 0).normalize();

        /* Calculate and store the secret */
        storeSecret(theExchange.calculateKey(KEYLEN, myServerEphemeral));

        /* If we are confirming */
        if (getSpec().withConfirm()) {
            /* Store confirmations */
            setClientConfirm(theExchange.getInitiatorConfirmation());
            setServerConfirm(theExchange.getResponderConfirmation());
        }
    }
}
