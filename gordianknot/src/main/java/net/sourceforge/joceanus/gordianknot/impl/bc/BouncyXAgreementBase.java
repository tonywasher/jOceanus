/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.gordianknot.impl.bc;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianPrivateKey;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianPublicKey;
import net.sourceforge.joceanus.gordianknot.impl.core.xagree.GordianXCoreAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.core.xagree.GordianXCoreAgreementFactory;

/**
 * Base Agreement Engine class
 */
public abstract class BouncyXAgreementBase
        extends GordianXCoreAgreementEngine {
    /**
     * Constructor.
     * @param pFactory the security factory
     * @param pSpec the agreementSpec
     * @throws GordianException on error
     */
    BouncyXAgreementBase(final GordianXCoreAgreementFactory pFactory,
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
