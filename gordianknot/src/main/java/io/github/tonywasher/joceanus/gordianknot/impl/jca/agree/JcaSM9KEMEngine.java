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
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSM9Spec.GordianSM9EncryptType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree.GordianCoreAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.agree.JcaAgreement.JcaAgreementBase;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaSM9KeyPairGenerator.JcaSM9EncMasterPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaSM9KeyPairGenerator.JcaSM9EncMasterPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaSM9KeyPairGenerator.JcaSM9EncUserPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaSM9KeyPairGenerator.JcaSM9EncUserPublicKey;
import org.bouncycastle.jcajce.SecretKeyWithEncapsulation;
import org.bouncycastle.jcajce.spec.KEMExtractSpec;
import org.bouncycastle.jcajce.spec.KEMGenerateSpec;

import javax.crypto.KeyGenerator;
import java.security.InvalidAlgorithmParameterException;

/**
 * Jca SM9 KEM Agreement.
 */
public class JcaSM9KEMEngine
        extends JcaAgreementBase {
    /**
     * Key length.
     */
    private static final int KEYLEN = 256;

    /**
     * Key Agreement.
     */
    private final KeyGenerator theGenerator;

    /**
     * Constructor.
     *
     * @param pFactory   the security factory
     * @param pSpec      the agreementSpec
     * @param pGenerator the generator
     */
    JcaSM9KEMEngine(final GordianCoreAgreementFactory pFactory,
                    final GordianCoreAgreementSpec pSpec,
                    final KeyGenerator pGenerator) throws GordianException {
        /* Initialize underlying class */
        super(pFactory, pSpec);

        /* Store the generator */
        theGenerator = pGenerator;
    }

    /**
     * Obtain the User publicKey.
     *
     * @return the publicKey
     * @throws GordianException on error
     */
    private JcaSM9EncUserPublicKey getUserPublicKey() throws GordianException {
        final GordianKeyPair myKeyPair = getServerKeyPair();
        final GordianSM9EncryptType myKeyType = (GordianSM9EncryptType) myKeyPair.getKeyPairSpec().getSubSpec();
        return switch (myKeyType) {
            case ENCMASTER -> {
                final JcaSM9EncMasterPublicKey myPublic = (JcaSM9EncMasterPublicKey) getPublicKey(myKeyPair);
                yield myPublic.deriveUserPublicKey(GordianSM9EncryptType.ENCRYPT, getServerName());
            }
            case ENCRYPT -> (JcaSM9EncUserPublicKey) getPublicKey(myKeyPair);
            default -> throw new GordianDataException("Unsupported keyPairType: " + myKeyType);
        };
    }

    /**
     * Obtain the User privateKey.
     *
     * @return the privateKey
     * @throws GordianException on error
     */
    private JcaSM9EncUserPrivateKey getUserPrivateKey() throws GordianException {
        final GordianKeyPair myKeyPair = getServerKeyPair();
        final GordianSM9EncryptType myKeyType = (GordianSM9EncryptType) myKeyPair.getKeyPairSpec().getSubSpec();
        return switch (myKeyType) {
            case ENCMASTER -> {
                final JcaSM9EncMasterPrivateKey myPrivate = (JcaSM9EncMasterPrivateKey) getPrivateKey(myKeyPair);
                yield myPrivate.newUserPrivateKey(GordianSM9EncryptType.ENCRYPT, getServerName());
            }
            case ENCRYPT -> (JcaSM9EncUserPrivateKey) getPrivateKey(myKeyPair);
            default -> throw new GordianDataException("Unsupported keyPairType: " + myKeyType);
        };
    }

    @Override
    public void buildClientHello() throws GordianException {
        /* Protect against exceptions */
        try {
            /* Create encapsulation */
            final JcaSM9EncUserPublicKey myPublic = getUserPublicKey();
            final KEMGenerateSpec mySpec = new KEMGenerateSpec.Builder(myPublic.getPublicKey(),
                    GordianSymKeyType.AES.toString(), KEYLEN).withKdfAlgorithm(derivationAlgorithmId()).build();
            theGenerator.init(mySpec, getRandom());
            final SecretKeyWithEncapsulation mySecret = (SecretKeyWithEncapsulation) theGenerator.generateKey();

            /* Store the encapsulation */
            setClientEncapsulated(mySecret.getEncapsulation());

            /* Store secret */
            storeSecret(mySecret.getEncoded());

        } catch (InvalidAlgorithmParameterException e) {
            throw new GordianCryptoException(JcaAgreement.ERR_AGREEMENT, e);
        }
    }

    @Override
    public void processClientHello() throws GordianException {
        /* Protect against exceptions */
        try {
            /* Create extractor */
            final JcaSM9EncUserPrivateKey myUserPrivate = getUserPrivateKey();
            final KEMExtractSpec mySpec = new KEMExtractSpec.Builder(myUserPrivate.getPrivateKey(), getClientEncapsulated(),
                    GordianSymKeyType.AES.toString(), KEYLEN).withKdfAlgorithm(derivationAlgorithmId()).build();
            theGenerator.init(mySpec);

            /* Store secret */
            final SecretKeyWithEncapsulation mySecret = (SecretKeyWithEncapsulation) theGenerator.generateKey();
            storeSecret(mySecret.getEncoded());

        } catch (InvalidAlgorithmParameterException e) {
            throw new GordianCryptoException(JcaAgreement.ERR_AGREEMENT, e);
        }
    }
}
