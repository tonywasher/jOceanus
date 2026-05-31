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
import org.bouncycastle.crypto.agreement.ECDHCUnifiedAgreement;
import org.bouncycastle.crypto.params.ECDHUPrivateParameters;
import org.bouncycastle.crypto.params.ECDHUPublicParameters;

/**
 * EC Unified Agreement Engine.
 */
public class BouncyECUnifiedAgreementEngine
        extends BouncyAgreementBase {
    /**
     * The agreement.
     */
    private final ECDHCUnifiedAgreement theAgreement;

    /**
     * Constructor.
     *
     * @param pFactory the security factory
     * @param pSpec    the agreementSpec
     * @throws GordianException on error
     */
    BouncyECUnifiedAgreementEngine(final GordianCoreAgreementFactory pFactory,
                                   final GordianCoreAgreementSpec pSpec) throws GordianException {
        /* Initialize underlying class */
        super(pFactory, pSpec);

        /* Create the agreement */
        theAgreement = new ECDHCUnifiedAgreement();
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
        final ECDHUPrivateParameters myPrivParams
                = new ECDHUPrivateParameters(myPrivate.getPrivateKey(), myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
        theAgreement.init(myPrivParams);
        final ECDHUPublicParameters myPubParams
                = new ECDHUPublicParameters(myClientPublic.getPublicKey(), myClientEphPublic.getPublicKey());
        storeSecret(theAgreement.calculateAgreement(myPubParams));
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
        final ECDHUPrivateParameters myPrivParams
                = new ECDHUPrivateParameters(myPrivate.getPrivateKey(), myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
        theAgreement.init(myPrivParams);
        final ECDHUPublicParameters myPubParams
                = new ECDHUPublicParameters(myServerPublic.getPublicKey(), myServerEphPublic.getPublicKey());
        storeSecret(theAgreement.calculateAgreement(myPubParams));
    }
}
