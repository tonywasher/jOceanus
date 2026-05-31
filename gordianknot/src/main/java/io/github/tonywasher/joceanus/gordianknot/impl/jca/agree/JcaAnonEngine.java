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

package io.github.tonywasher.joceanus.gordianknot.impl.jca.agree;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree.GordianCoreAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.agree.JcaAgreement.JcaAgreementBase;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair.JcaPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair.JcaPublicKey;

import javax.crypto.KeyAgreement;
import java.security.InvalidKeyException;

/**
 * Jca Anonymous Agreement.
 */
public class JcaAnonEngine
        extends JcaAgreementBase {
    /**
     * Key Agreement.
     */
    private KeyAgreement theAgreement;

    /**
     * Constructor.
     *
     * @param pFactory   the security factory
     * @param pSpec      the agreementSpec
     * @param pAgreement the agreement
     */
    JcaAnonEngine(final GordianCoreAgreementFactory pFactory,
                  final GordianCoreAgreementSpec pSpec,
                  final KeyAgreement pAgreement) throws GordianException {
        /* Initialize underlying class */
        super(pFactory, pSpec);

        /* Store the agreement */
        theAgreement = pAgreement;
    }

    @Override
    public void buildClientHello() throws GordianException {
        /* Protect against exceptions */
        try {
            /* Access keys */
            final JcaPublicKey myPublic = (JcaPublicKey) getPublicKey(getServerKeyPair());
            final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(getClientEphemeral());
            theAgreement = adjustAgreement(theAgreement, getServerKeyPair());

            /* Derive the secret */
            initAgreement(theAgreement, myPrivate);
            theAgreement.doPhase(myPublic.getPublicKey(), true);
            storeSecret(theAgreement.generateSecret());

        } catch (InvalidKeyException e) {
            throw new GordianCryptoException(JcaAgreement.ERR_AGREEMENT, e);
        }
    }

    @Override
    public void processClientHello() throws GordianException {
        /* Protect against exceptions */
        try {
            /* Access keys */
            final JcaPublicKey myPublic = (JcaPublicKey) getPublicKey(getClientEphemeral());
            final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(getServerKeyPair());
            theAgreement = adjustAgreement(theAgreement, getServerKeyPair());

            /* Derive the secret */
            initAgreement(theAgreement, myPrivate);
            theAgreement.doPhase(myPublic.getPublicKey(), true);
            storeSecret(theAgreement.generateSecret());

        } catch (InvalidKeyException e) {
            throw new GordianCryptoException(JcaAgreement.ERR_AGREEMENT, e);
        }
    }
}
