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
import org.bouncycastle.crypto.agreement.ECDHCBasicAgreement;
import org.bouncycastle.util.BigIntegers;

import java.math.BigInteger;

/**
 * EC Anonymous Agreement Engine.
 */
public class BouncyECAnonAgreementEngine
        extends BouncyAgreementBase {
    /**
     * The agreement.
     */
    private final ECDHCBasicAgreement theAgreement;

    /**
     * Constructor.
     *
     * @param pFactory the security factory
     * @param pSpec    the agreementSpec
     * @throws GordianException on error
     */
    BouncyECAnonAgreementEngine(final GordianCoreAgreementFactory pFactory,
                                final GordianCoreAgreementSpec pSpec) throws GordianException {
        /* Initialize underlying class */
        super(pFactory, pSpec);

        /* Create the agreement */
        theAgreement = new ECDHCBasicAgreement();
    }

    @Override
    public void buildClientHello() throws GordianException {
        /* Access keys */
        final BouncyECPublicKey myPublic = (BouncyECPublicKey) getPublicKey(getServerKeyPair());
        final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getPrivateKey(getClientEphemeral());

        /* Derive the secret */
        theAgreement.init(myPrivate.getPrivateKey());
        final BigInteger mySecretInt = theAgreement.calculateAgreement(myPublic.getPublicKey());
        final byte[] mySecret = BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(), mySecretInt);

        /* Store secret */
        storeSecret(mySecret);
    }

    @Override
    public void processClientHello() throws GordianException {
        /* Access keys */
        final BouncyECPublicKey myPublic = (BouncyECPublicKey) getPublicKey(getClientEphemeral());
        final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getPrivateKey(getServerKeyPair());

        /* Derive the secret */
        theAgreement.init(myPrivate.getPrivateKey());
        final BigInteger mySecretInt = theAgreement.calculateAgreement(myPublic.getPublicKey());
        final byte[] mySecret = BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(), mySecretInt);

        /* Store secret */
        storeSecret(mySecret);
    }
}
