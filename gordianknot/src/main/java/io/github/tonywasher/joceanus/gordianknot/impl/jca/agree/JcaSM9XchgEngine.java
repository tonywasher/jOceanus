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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSM9Spec.GordianSM9EncryptType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree.GordianCoreAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.agree.JcaAgreement.JcaAgreementBase;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaSM9KeyPairGenerator.JcaSM9EncMasterPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaSM9KeyPairGenerator.JcaSM9EncMasterPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaSM9KeyPairGenerator.JcaSM9EncUserPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaSM9KeyPairGenerator.JcaSM9EncUserPublicKey;
import org.bouncycastle.jcajce.interfaces.SM9EncMasterPublicKey;
import org.bouncycastle.jcajce.spec.SM9KeyExchangeSpec;

import javax.crypto.KeyAgreement;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.PublicKey;

/**
 * Jca SM9 Exchange Agreement.
 */
public class JcaSM9XchgEngine
        extends JcaAgreementBase {
    /**
     * Key length.
     */
    private static final int KEYLEN = 256;

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
    JcaSM9XchgEngine(final GordianCoreAgreementFactory pFactory,
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
            /* Access private key */
            final JcaSM9EncMasterPrivateKey myMasterPrivate = (JcaSM9EncMasterPrivateKey) getPrivateKey(getClientKeyPair());
            final JcaSM9EncUserPrivateKey myUserPrivate = myMasterPrivate.newUserPrivateKey(GordianSM9EncryptType.EXCHANGE, getClientName());
            final JcaSM9EncMasterPublicKey myMasterPublic = (JcaSM9EncMasterPublicKey) getPublicKey(getServerKeyPair());
            final JcaSM9EncUserPublicKey myUserPublic = myMasterPublic.deriveUserPublicKey(GordianSM9EncryptType.EXCHANGE, getServerName());

            /* Initialise the agreement */
            final SM9KeyExchangeSpec mySpec = new SM9KeyExchangeSpec(true, KEYLEN);
            theAgreement.init(myUserPrivate.getPrivateKey(), mySpec, getRandom());
            final PublicKey myEphemeral = (PublicKey) theAgreement.doPhase(myUserPublic.getPublicKey(), false);
            setClientEncapsulated(myEphemeral.getEncoded());

        } catch (InvalidKeyException
                 | InvalidAlgorithmParameterException e) {
            throw new GordianCryptoException(JcaAgreement.ERR_AGREEMENT, e);
        }
    }

    @Override
    public void processClientHello() throws GordianException {
        /* Protect against exceptions */
        try {
            /* Access keys */
            final JcaSM9EncMasterPrivateKey myMasterPrivate = (JcaSM9EncMasterPrivateKey) getPrivateKey(getServerKeyPair());
            final JcaSM9EncUserPrivateKey myUserPrivate = myMasterPrivate.newUserPrivateKey(GordianSM9EncryptType.EXCHANGE, getServerName());
            final JcaSM9EncMasterPublicKey myMasterPublic = (JcaSM9EncMasterPublicKey) getPublicKey(getClientKeyPair());
            final JcaSM9EncUserPublicKey myUserPublic = myMasterPublic.deriveUserPublicKey(GordianSM9EncryptType.EXCHANGE, getClientName());

            /* Access client ephemeral */
            final byte[] myClientEncapsulated = getClientEncapsulated();
            final SM9EncMasterPublicKey myPublic = (SM9EncMasterPublicKey) myMasterPublic.getPublicKey();
            final PublicKey myClientEphemeral = myPublic.getExchangeEphemeral(myClientEncapsulated);

            /* Process the agreement */
            final SM9KeyExchangeSpec mySpec = new SM9KeyExchangeSpec(false, KEYLEN);
            theAgreement.init(myUserPrivate.getPrivateKey(), mySpec, getRandom());
            final PublicKey myEphemeral = (PublicKey) theAgreement.doPhase(myUserPublic.getPublicKey(), false);
            theAgreement.doPhase(myClientEphemeral, true);
            setServerEncapsulated(myEphemeral.getEncoded());

            /* Store the secret */
            storeSecret(theAgreement.generateSecret());

        } catch (InvalidKeyException
                 | InvalidAlgorithmParameterException e) {
            throw new GordianCryptoException(JcaAgreement.ERR_AGREEMENT, e);
        }
    }

    @Override
    public void processServerHello() throws GordianException {
        /* Protect against exceptions */
        try {
            /* Access keys */
            final JcaSM9EncMasterPublicKey myMasterPublic = (JcaSM9EncMasterPublicKey) getPublicKey(getClientKeyPair());

            /* Access server ephemeral */
            final byte[] myServerEncapsulated = getServerEncapsulated();
            final SM9EncMasterPublicKey myPublic = (SM9EncMasterPublicKey) myMasterPublic.getPublicKey();
            final PublicKey myServerEphemeral = myPublic.getExchangeEphemeral(myServerEncapsulated);

            /* Finalise the secret */
            theAgreement.doPhase(myServerEphemeral, true); /* Encapsulated */
            storeSecret(theAgreement.generateSecret());

        } catch (InvalidKeyException e) {
            throw new GordianCryptoException(JcaAgreement.ERR_AGREEMENT, e);
        }
    }
}
