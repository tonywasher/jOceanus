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
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyNewHopeKeyPair.BouncyNewHopePrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyNewHopeKeyPair.BouncyNewHopePublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree.GordianCoreAgreementSpec;
import org.bouncycastle.pqc.crypto.ExchangePair;
import org.bouncycastle.pqc.crypto.newhope.NHAgreement;
import org.bouncycastle.pqc.crypto.newhope.NHExchangePairGenerator;
import org.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;

/**
 * NewHope Agreement Engine.
 */
public class BouncyNewHopeAgreementEngine
        extends BouncyAgreementBase {
    /**
     * Constructor.
     *
     * @param pFactory the security factory
     * @param pSpec    the agreementSpec
     * @throws GordianException on error
     */
    BouncyNewHopeAgreementEngine(final GordianCoreAgreementFactory pFactory,
                                 final GordianCoreAgreementSpec pSpec) throws GordianException {
        /* Initialize underlying class */
        super(pFactory, pSpec);
    }

    @Override
    public void buildClientHello() throws GordianException {
        /* Generate an Exchange KeyPair */
        final NHExchangePairGenerator myGenerator = new NHExchangePairGenerator(getRandom());
        final BouncyNewHopePublicKey myTarget = (BouncyNewHopePublicKey) getPublicKey(getServerKeyPair());
        final ExchangePair myPair = myGenerator.GenerateExchange(myTarget.getPublicKey());

        /* Store the ephemeral keyPair */
        final GordianKeyPairSpec mySpec = getSpec().getKeyPairSpec();
        final BouncyNewHopePublicKey myPublic = new BouncyNewHopePublicKey(mySpec, (NHPublicKeyParameters) myPair.getPublicKey());
        final BouncyKeyPair myEphemeral = new BouncyKeyPair(myPublic);
        setClientEphemeralAsEncapsulated(myEphemeral);

        /* Store secret */
        storeSecret(myPair.getSharedValue());
    }

    @Override
    public void processClientHello() throws GordianException {
        /* Create extractor */
        final BouncyNewHopePublicKey myPublic = (BouncyNewHopePublicKey) getPublicKey(getClientEphemeral());
        final BouncyNewHopePrivateKey myPrivate = (BouncyNewHopePrivateKey) getPrivateKey(getServerKeyPair());

        /* Create agreement */
        final NHAgreement myAgreement = new NHAgreement();
        myAgreement.init(myPrivate.getPrivateKey());
        final byte[] mySecret = myAgreement.calculateAgreement(myPublic.getPublicKey());

        /* Store secret */
        storeSecret(mySecret);
    }
}
