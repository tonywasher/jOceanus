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
import org.bouncycastle.crypto.agreement.ECMQVBasicAgreement;
import org.bouncycastle.crypto.params.MQVPrivateParameters;
import org.bouncycastle.crypto.params.MQVPublicParameters;
import org.bouncycastle.util.BigIntegers;

/**
 * EC MQV Agreement Engine.
 */
public class BouncyECMQVAgreementEngine
        extends BouncyAgreementBase {
    /**
     * The agreement.
     */
    private final ECMQVBasicAgreement theAgreement;

    /**
     * Constructor.
     *
     * @param pFactory the security factory
     * @param pSpec    the agreementSpec
     * @throws GordianException on error
     */
    BouncyECMQVAgreementEngine(final GordianCoreAgreementFactory pFactory,
                               final GordianCoreAgreementSpec pSpec) throws GordianException {
        /* Initialize underlying class */
        super(pFactory, pSpec);

        /* Create the agreement */
        theAgreement = new ECMQVBasicAgreement();
    }

    @Override
    public void processClientHello() throws GordianException {
        /* Access keys */
        final BouncyECPublicKey myClientPublic = (BouncyECPublicKey) getPublicKey(getClientKeyPair());
        final BouncyECPublicKey myClientEphPublic = (BouncyECPublicKey) getPublicKey(getClientEphemeral());
        final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getPrivateKey(getServerKeyPair());
        final BouncyECPublicKey myEphPublic = (BouncyECPublicKey) getPublicKey(getServerEphemeral());
        final BouncyECPrivateKey myEphPrivate = (BouncyECPrivateKey) getPrivateKey(getServerEphemeral());

        /* Derive the secret */
        final MQVPrivateParameters myPrivParams
                = new MQVPrivateParameters(myPrivate.getPrivateKey(), myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
        theAgreement.init(myPrivParams);
        final MQVPublicParameters myPubParams
                = new MQVPublicParameters(myClientPublic.getPublicKey(), myClientEphPublic.getPublicKey());
        storeSecret(BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(), theAgreement.calculateAgreement(myPubParams)));
    }

    @Override
    public void processServerHello() throws GordianException {
        /* Access keys */
        final BouncyECPublicKey myServerPublic = (BouncyECPublicKey) getPublicKey(getServerKeyPair());
        final BouncyECPublicKey myServerEphPublic = (BouncyECPublicKey) getPublicKey(getServerEphemeral());
        final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getPrivateKey(getClientKeyPair());
        final BouncyECPublicKey myEphPublic = (BouncyECPublicKey) getPublicKey(getClientEphemeral());
        final BouncyECPrivateKey myEphPrivate = (BouncyECPrivateKey) getPrivateKey(getClientEphemeral());

        /* Derive the secret */
        final MQVPrivateParameters myPrivParams
                = new MQVPrivateParameters(myPrivate.getPrivateKey(), myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
        theAgreement.init(myPrivParams);
        final MQVPublicParameters myPubParams
                = new MQVPublicParameters(myServerPublic.getPublicKey(), myServerEphPublic.getPublicKey());
        storeSecret(BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(), theAgreement.calculateAgreement(myPubParams)));
    }
}
