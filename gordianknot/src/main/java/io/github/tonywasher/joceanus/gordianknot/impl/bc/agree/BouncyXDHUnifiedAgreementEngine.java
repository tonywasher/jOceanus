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
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree.GordianCoreAgreementSpec;
import org.bouncycastle.crypto.agreement.XDHUnifiedAgreement;
import org.bouncycastle.crypto.params.XDHUPrivateParameters;
import org.bouncycastle.crypto.params.XDHUPublicParameters;

/**
 * XDH Unified Agreement Engine.
 */
public class BouncyXDHUnifiedAgreementEngine
        extends BouncyAgreementBase {
    /**
     * The agreement.
     */
    private final XDHUnifiedAgreement theAgreement;

    /**
     * Constructor.
     *
     * @param pFactory the security factory
     * @param pSpec    the agreementSpec
     * @throws GordianException on error
     */
    BouncyXDHUnifiedAgreementEngine(final GordianCoreAgreementFactory pFactory,
                                    final GordianCoreAgreementSpec pSpec) throws GordianException {
        /* Initialize underlying class */
        super(pFactory, pSpec);

        /* Create the agreement */
        theAgreement = new XDHUnifiedAgreement(BouncyXDHAnonAgreementEngine.establishAgreement(pSpec.getKeyPairSpec()));
    }

    @Override
    public void processClientHello() throws GordianException {
        /* Access keys */
        final BouncyPublicKey<?> myClientPublic = (BouncyPublicKey<?>) getPublicKey(getClientKeyPair());
        final BouncyPublicKey<?> myClientEphPublic = (BouncyPublicKey<?>) getPublicKey(getClientEphemeral());
        final BouncyPrivateKey<?> myPrivate = (BouncyPrivateKey<?>) getPrivateKey(getServerKeyPair());
        final BouncyPublicKey<?> myEphPublic = (BouncyPublicKey<?>) getPublicKey(getServerEphemeral());
        final BouncyPrivateKey<?> myEphPrivate = (BouncyPrivateKey<?>) getPrivateKey(getServerEphemeral());

        /* Derive the secret */
        final XDHUPrivateParameters myPrivParams
                = new XDHUPrivateParameters(myPrivate.getPrivateKey(), myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
        theAgreement.init(myPrivParams);
        final byte[] mySecret = new byte[theAgreement.getAgreementSize()];
        final XDHUPublicParameters myPubParams
                = new XDHUPublicParameters(myClientPublic.getPublicKey(), myClientEphPublic.getPublicKey());
        theAgreement.calculateAgreement(myPubParams, mySecret, 0);

        /* Store secret */
        storeSecret(mySecret);
    }

    @Override
    public void processServerHello() throws GordianException {
        /* Access keys */
        final BouncyPublicKey<?> myServerPublic = (BouncyPublicKey<?>) getPublicKey(getServerKeyPair());
        final BouncyPublicKey<?> myServerEphPublic = (BouncyPublicKey<?>) getPublicKey(getServerEphemeral());
        final BouncyPrivateKey<?> myPrivate = (BouncyPrivateKey<?>) getPrivateKey(getClientKeyPair());
        final BouncyPublicKey<?> myEphPublic = (BouncyPublicKey<?>) getPublicKey(getClientEphemeral());
        final BouncyPrivateKey<?> myEphPrivate = (BouncyPrivateKey<?>) getPrivateKey(getClientEphemeral());

        /* Derive the secret */
        final XDHUPrivateParameters myPrivParams
                = new XDHUPrivateParameters(myPrivate.getPrivateKey(), myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
        theAgreement.init(myPrivParams);
        final byte[] mySecret = new byte[theAgreement.getAgreementSize()];
        final XDHUPublicParameters myPubParams
                = new XDHUPublicParameters(myServerPublic.getPublicKey(), myServerEphPublic.getPublicKey());
        theAgreement.calculateAgreement(myPubParams, mySecret, 0);

        /* Store secret */
        storeSecret(mySecret);
    }
}
