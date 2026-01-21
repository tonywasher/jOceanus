/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
package io.github.tonywasher.joceanus.gordianknot.impl.bc;

import io.github.tonywasher.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.GordianPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.GordianPublicKey;

/**
 * Base Agreement Engine class.
 */
public abstract class BouncyAgreementBase
        extends GordianCoreAgreementEngine {
    /**
     * Constructor.
     *
     * @param pFactory the security factory
     * @param pSpec    the agreementSpec
     * @throws GordianException on error
     */
    BouncyAgreementBase(final GordianCoreAgreementFactory pFactory,
                        final GordianAgreementSpec pSpec) throws GordianException {
        /* Invoke underlying constructor */
        super(pFactory, pSpec);

        /* Enable derivation */
        enableDerivation();
    }

    @Override
    protected GordianPublicKey getPublicKey(final GordianKeyPair pKeyPair) throws GordianException {
        /* Validate the keyPair */
        if (!(pKeyPair instanceof BouncyKeyPair)) {
            /* Reject keyPair */
            throw new GordianDataException("Invalid KeyPair");
        }

        /* Access public key */
        return super.getPublicKey(pKeyPair);
    }

    @Override
    protected GordianPrivateKey getPrivateKey(final GordianKeyPair pKeyPair) throws GordianException {
        /* Validate the keyPair */
        if (!(pKeyPair instanceof BouncyKeyPair)) {
            /* Reject keyPair */
            throw new GordianDataException("Invalid KeyPair");
        }

        /* Access private key */
        return super.getPrivateKey(pKeyPair);
    }
}
