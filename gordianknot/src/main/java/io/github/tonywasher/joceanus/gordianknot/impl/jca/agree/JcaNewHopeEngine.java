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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree.GordianCoreAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.agree.JcaAgreement.JcaAgreementBase;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair.JcaPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair.JcaPublicKey;

import javax.crypto.KeyAgreement;
import java.security.InvalidKeyException;
import java.security.PublicKey;

/**
 * Jca NewHope Agreement.
 */
public class JcaNewHopeEngine
        extends JcaAgreementBase {
    /**
     * Key Agreement.
     */
    private final KeyAgreement theAgreement;

    /**
     * Constructor.
     *
     * @param pFactory   the security factory
     * @param pSpec      the agreementSpec
     * @param pAgreement the agreement
     */
    JcaNewHopeEngine(final GordianCoreAgreementFactory pFactory,
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
            /* Derive the secret */
            theAgreement.init(null, getRandom());
            final JcaPublicKey myTarget = (JcaPublicKey) getPublicKey(getServerKeyPair());
            final PublicKey myKey = (PublicKey) theAgreement.doPhase(myTarget.getPublicKey(), true);

            /* Store the ephemeral */
            final GordianKeyPairSpec mySpec = getSpec().getKeyPairSpec();
            final JcaPublicKey myPublic = new JcaPublicKey(mySpec, myKey);
            final JcaKeyPair myEphemeral = new JcaKeyPair(myPublic);
            setClientEphemeralAsEncapsulated(myEphemeral);

            /* Store secret */
            storeSecret(theAgreement.generateSecret());

        } catch (InvalidKeyException e) {
            throw new GordianCryptoException(JcaAgreement.ERR_AGREEMENT, e);
        }
    }

    @Override
    public void processClientHello() throws GordianException {
        /* Protect against exceptions */
        try {
            /* Derive the secret */
            final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(getServerKeyPair());
            final JcaPublicKey myPublic = (JcaPublicKey) getPublicKey(getClientEphemeral());
            theAgreement.init(myPrivate.getPrivateKey());
            theAgreement.doPhase(myPublic.getPublicKey(), true);

            /* Store secret */
            storeSecret(theAgreement.generateSecret());

        } catch (InvalidKeyException e) {
            throw new GordianCryptoException(JcaAgreement.ERR_AGREEMENT, e);
        }
    }
}
