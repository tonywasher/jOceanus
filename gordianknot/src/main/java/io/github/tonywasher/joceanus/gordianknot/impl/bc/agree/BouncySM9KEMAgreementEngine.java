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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSM9Spec.GordianSM9EncryptType;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncySM9KeyPair.BouncySM9EncMasterPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncySM9KeyPair.BouncySM9EncMasterPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncySM9KeyPair.BouncySM9EncUserPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncySM9KeyPair.BouncySM9EncUserPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianIOException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree.GordianCoreAgreementSpec;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.kems.SM9KEMExtractor;
import org.bouncycastle.crypto.kems.SM9KEMGenerator;

import javax.security.auth.DestroyFailedException;

/**
 * SM9 KEM Agreement Engine.
 */
public class BouncySM9KEMAgreementEngine
        extends BouncyAgreementBase {
    /**
     * Key length.
     */
    private static final int KEYLEN = 256;

    /**
     * Constructor.
     *
     * @param pFactory the security factory
     * @param pSpec    the agreementSpec
     * @throws GordianException on error
     */
    BouncySM9KEMAgreementEngine(final GordianCoreAgreementFactory pFactory,
                                final GordianCoreAgreementSpec pSpec) throws GordianException {
        /* Initialize underlying class */
        super(pFactory, pSpec);
    }

    /**
     * Obtain the User publicKey.
     *
     * @return the publicKey
     * @throws GordianException on error
     */
    private BouncySM9EncUserPublicKey getUserPublicKey() throws GordianException {
        final GordianKeyPair myKeyPair = getServerKeyPair();
        final GordianSM9EncryptType myKeyType = (GordianSM9EncryptType) myKeyPair.getKeyPairSpec().getSubSpec();
        return switch (myKeyType) {
            case ENCMASTER -> {
                final BouncySM9EncMasterPublicKey myPublic = (BouncySM9EncMasterPublicKey) getPublicKey(myKeyPair);
                yield myPublic.deriveUserPublicKey(GordianSM9EncryptType.ENCRYPT, getServerName());
            }
            case ENCRYPT -> (BouncySM9EncUserPublicKey) getPublicKey(myKeyPair);
            default -> throw new GordianDataException("Unsupported keyPairType: " + myKeyType);
        };
    }

    /**
     * Obtain the User privateKey.
     *
     * @return the privateKey
     * @throws GordianException on error
     */
    private BouncySM9EncUserPrivateKey getUserPrivateKey() throws GordianException {
        final GordianKeyPair myKeyPair = getServerKeyPair();
        final GordianSM9EncryptType myKeyType = (GordianSM9EncryptType) myKeyPair.getKeyPairSpec().getSubSpec();
        return switch (myKeyType) {
            case ENCMASTER -> {
                final BouncySM9EncMasterPrivateKey myPrivate = (BouncySM9EncMasterPrivateKey) getPrivateKey(myKeyPair);
                yield myPrivate.newUserPrivateKey(GordianSM9EncryptType.ENCRYPT, getServerName());
            }
            case ENCRYPT -> (BouncySM9EncUserPrivateKey) getPrivateKey(myKeyPair);
            default -> throw new GordianDataException("Unsupported keyPairType: " + myKeyType);
        };
    }

    @Override
    public void buildClientHello() throws GordianException {
        /* Protect against exceptions */
        try {
            /* Create encapsulation */
            final BouncySM9EncUserPublicKey myPublic = getUserPublicKey();
            final SM9KEMGenerator myGenerator = new SM9KEMGenerator(KEYLEN, getRandom());
            final SecretWithEncapsulation myResult = myGenerator.generateEncapsulated(myPublic.getPublicKey());

            /* Store the encapsulation */
            setClientEncapsulated(myResult.getEncapsulation());

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
        final BouncySM9EncUserPrivateKey myPrivate = getUserPrivateKey();
        final SM9KEMExtractor myExtractor = new SM9KEMExtractor(myPrivate.getPrivateKey(), KEYLEN);

        /* Parse encapsulated message and store secret */
        final byte[] myMessage = getClientEncapsulated();
        storeSecret(myExtractor.extractSecret(myMessage));
    }
}
