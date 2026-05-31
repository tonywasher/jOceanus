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
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyDHKeyPair.BouncyDHPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyDHKeyPair.BouncyDHPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree.GordianCoreAgreementSpec;
import org.bouncycastle.crypto.agreement.MQVBasicAgreement;
import org.bouncycastle.crypto.params.DHMQVPrivateParameters;
import org.bouncycastle.crypto.params.DHMQVPublicParameters;
import org.bouncycastle.util.BigIntegers;

/**
 * DH MQV XAgreement Engine.
 */
public class BouncyDHMQVAgreementEngine
        extends BouncyAgreementBase {
    /**
     * The agreement.
     */
    private final MQVBasicAgreement theAgreement;

    /**
     * Constructor.
     *
     * @param pFactory the security factory
     * @param pSpec    the agreementSpec
     * @throws GordianException on error
     */
    BouncyDHMQVAgreementEngine(final GordianCoreAgreementFactory pFactory,
                               final GordianCoreAgreementSpec pSpec) throws GordianException {
        /* Initialize underlying class */
        super(pFactory, pSpec);

        /* Create the agreement */
        theAgreement = new MQVBasicAgreement();
    }

    @Override
    public void processClientHello() throws GordianException {
        /* Access keys */
        final BouncyDHPublicKey myClientPublic = (BouncyDHPublicKey) getPublicKey(getClientKeyPair());
        final BouncyDHPublicKey myClientEphPublic = (BouncyDHPublicKey) getPublicKey(getClientEphemeral());
        final BouncyDHPrivateKey myPrivate = (BouncyDHPrivateKey) getPrivateKey(getServerKeyPair());
        final BouncyDHPublicKey myEphPublic = (BouncyDHPublicKey) getPublicKey(getServerEphemeral());
        final BouncyDHPrivateKey myEphPrivate = (BouncyDHPrivateKey) getPrivateKey(getServerEphemeral());

        /* Derive the secret */
        final DHMQVPrivateParameters myPrivParams
                = new DHMQVPrivateParameters(myPrivate.getPrivateKey(), myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
        theAgreement.init(myPrivParams);

        /* Store secret */
        final DHMQVPublicParameters myPubParams
                = new DHMQVPublicParameters(myClientPublic.getPublicKey(), myClientEphPublic.getPublicKey());
        storeSecret(BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(),
                theAgreement.calculateAgreement(myPubParams)));
    }

    @Override
    public void processServerHello() throws GordianException {
        /* Access keys */
        final BouncyDHPublicKey myServerPublic = (BouncyDHPublicKey) getPublicKey(getServerKeyPair());
        final BouncyDHPublicKey myServerEphPublic = (BouncyDHPublicKey) getPublicKey(getServerEphemeral());
        final BouncyDHPrivateKey myPrivate = (BouncyDHPrivateKey) getPrivateKey(getClientKeyPair());
        final BouncyDHPublicKey myEphPublic = (BouncyDHPublicKey) getPublicKey(getClientEphemeral());
        final BouncyDHPrivateKey myEphPrivate = (BouncyDHPrivateKey) getPrivateKey(getClientEphemeral());

        /* Derive the secret */
        final DHMQVPrivateParameters myPrivParams
                = new DHMQVPrivateParameters(myPrivate.getPrivateKey(), myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
        theAgreement.init(myPrivParams);

        /* Store secret */
        final DHMQVPublicParameters myPubParams
                = new DHMQVPublicParameters(myServerPublic.getPublicKey(), myServerEphPublic.getPublicKey());
        storeSecret(BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(),
                theAgreement.calculateAgreement(myPubParams)));
    }
}
