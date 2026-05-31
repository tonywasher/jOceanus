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
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyNTRUKeyPair.BouncyNTRUPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyNTRUKeyPair.BouncyNTRUPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianIOException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree.GordianCoreAgreementSpec;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.pqc.crypto.ntru.NTRUKEMExtractor;
import org.bouncycastle.pqc.crypto.ntru.NTRUKEMGenerator;

import javax.security.auth.DestroyFailedException;

/**
 * NTRU Agreement Engine.
 */
public class BouncyNTRUAgreementEngine
        extends BouncyAgreementBase {
    /**
     * Constructor.
     *
     * @param pFactory the security factory
     * @param pSpec    the agreementSpec
     * @throws GordianException on error
     */
    BouncyNTRUAgreementEngine(final GordianCoreAgreementFactory pFactory,
                              final GordianCoreAgreementSpec pSpec) throws GordianException {
        /* Initialize underlying class */
        super(pFactory, pSpec);
    }

    @Override
    public void buildClientHello() throws GordianException {
        /* Protect against exceptions */
        try {
            /* Create encapsulation */
            final BouncyNTRUPublicKey myPublic = (BouncyNTRUPublicKey) getPublicKey(getServerKeyPair());
            final NTRUKEMGenerator myGenerator = new NTRUKEMGenerator(getRandom());
            final SecretWithEncapsulation myResult = myGenerator.generateEncapsulated(myPublic.getPublicKey());

            /* Store the encapsulation */
            setEncapsulated(myResult.getEncapsulation());

            /* Store secret and create initVector */
            storeSecret(myResult.getSecret());
            myResult.destroy();

        } catch (DestroyFailedException e) {
            throw new GordianIOException("Failed to destroy secret", e);
        }
    }

    @Override
    public void processClientHello() throws GordianException {
        /* Create encapsulation */
        final BouncyNTRUPrivateKey myPrivate = (BouncyNTRUPrivateKey) getPrivateKey(getServerKeyPair());
        final NTRUKEMExtractor myExtractor = new NTRUKEMExtractor(myPrivate.getPrivateKey());

        /* Parse encapsulated message and store secret */
        final byte[] myMessage = getEncapsulated();
        storeSecret(myExtractor.extractSecret(myMessage));
    }
}
