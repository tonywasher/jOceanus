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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree.GordianCoreAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import org.bouncycastle.crypto.RawAgreement;
import org.bouncycastle.crypto.agreement.X25519Agreement;
import org.bouncycastle.crypto.agreement.X448Agreement;

/**
 * XDH Anonymous Agreement Engine.
 */
public class BouncyXDHAnonAgreementEngine
        extends BouncyAgreementBase {
    /**
     * The agreement.
     */
    private final RawAgreement theAgreement;

    /**
     * Constructor.
     *
     * @param pFactory the security factory
     * @param pSpec    the agreementSpec
     * @throws GordianException on error
     */
    BouncyXDHAnonAgreementEngine(final GordianCoreAgreementFactory pFactory,
                                 final GordianCoreAgreementSpec pSpec) throws GordianException {
        /* Initialize underlying class */
        super(pFactory, pSpec);

        /* Create the agreement */
        theAgreement = establishAgreement(pSpec.getKeyPairSpec());
    }

    @Override
    public void buildClientHello() throws GordianException {
        /* Access keys */
        final BouncyPublicKey<?> myPublic = (BouncyPublicKey<?>) getPublicKey(getServerKeyPair());
        final BouncyPrivateKey<?> myPrivate = (BouncyPrivateKey<?>) getPrivateKey(getClientEphemeral());

        /* Derive the secret */
        theAgreement.init(myPrivate.getPrivateKey());
        final byte[] mySecret = new byte[theAgreement.getAgreementSize()];
        theAgreement.calculateAgreement(myPublic.getPublicKey(), mySecret, 0);

        /* Store secret */
        storeSecret(mySecret);
    }

    @Override
    public void processClientHello() throws GordianException {
        /* Access keys */
        final BouncyPublicKey<?> myPublic = (BouncyPublicKey<?>) getPublicKey(getClientEphemeral());
        final BouncyPrivateKey<?> myPrivate = (BouncyPrivateKey<?>) getPrivateKey(getServerKeyPair());

        /* Derive the secret */
        theAgreement.init(myPrivate.getPrivateKey());
        final byte[] mySecret = new byte[theAgreement.getAgreementSize()];
        theAgreement.calculateAgreement(myPublic.getPublicKey(), mySecret, 0);

        /* Store secret */
        storeSecret(mySecret);
    }

    /**
     * Establish the agreement.
     *
     * @param pSpec the keyPairSpec
     * @return the agreement
     */
    static RawAgreement establishAgreement(final GordianKeyPairSpec pSpec) {
        final GordianCoreKeyPairSpec mySpec = (GordianCoreKeyPairSpec) pSpec;
        return mySpec.getEdwardsSpec().is25519()
                ? new X25519Agreement()
                : new X448Agreement();
    }
}

