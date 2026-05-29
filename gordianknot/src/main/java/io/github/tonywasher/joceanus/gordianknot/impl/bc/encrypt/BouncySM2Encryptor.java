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

package io.github.tonywasher.joceanus.gordianknot.impl.bc.encrypt;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigestFactory;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianSM2EncryptionSpec;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianSM2EncryptionType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.digest.BouncyDigest;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.encrypt.GordianCoreEncryptor;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.encrypt.GordianCoreEncryptorSpec;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.engines.SM2Engine.Mode;
import org.bouncycastle.crypto.params.ParametersWithRandom;

/**
 * SM2 Encryptor.
 */
public class BouncySM2Encryptor
        extends GordianCoreEncryptor {
    /**
     * The underlying encryptor.
     */
    private final SM2Engine theEncryptor;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pSpec    the encryptorSpec
     * @throws GordianException on error
     */
    BouncySM2Encryptor(final GordianBaseFactory pFactory,
                       final GordianCoreEncryptorSpec pSpec) throws GordianException {
        /* Initialise underlying cipher */
        super(pFactory, pSpec);
        final GordianDigestFactory myFactory = pFactory.getDigestFactory();
        final GordianSM2EncryptionSpec mySpec = pSpec.getSM2EncryptionSpec();
        final BouncyDigest myDigest = (BouncyDigest) myFactory.createDigest(mySpec.getDigestSpec());
        final Mode mySM2Mode = mySpec.getEncryptionType() == GordianSM2EncryptionType.C1C2C3
                ? Mode.C1C2C3 : Mode.C1C3C2;
        theEncryptor = new SM2Engine(myDigest.getDigest(), mySM2Mode);
    }

    @Override
    protected BouncyPublicKey<?> getPublicKey() {
        return (BouncyPublicKey<?>) super.getPublicKey();
    }

    @Override
    protected BouncyPrivateKey<?> getPrivateKey() {
        return (BouncyPrivateKey<?>) super.getPrivateKey();
    }

    @Override
    public void initForEncrypt(final GordianKeyPair pKeyPair) throws GordianException {
        /* Initialise underlying cipher */
        BouncyKeyPair.checkKeyPair(pKeyPair);
        super.initForEncrypt(pKeyPair);

        /* Initialise for encryption */
        final ParametersWithRandom myParms = new ParametersWithRandom(getPublicKey().getPublicKey(), getRandom());
        theEncryptor.init(true, myParms);
    }

    @Override
    public void initForDecrypt(final GordianKeyPair pKeyPair) throws GordianException {
        /* Initialise underlying cipher */
        BouncyKeyPair.checkKeyPair(pKeyPair);
        super.initForDecrypt(pKeyPair);

        /* Initialise for decryption */
        theEncryptor.init(false, getPrivateKey().getPrivateKey());
    }

    @Override
    public byte[] encrypt(final byte[] pBytes) throws GordianException {
        try {
            /* Check that we are in encryption mode */
            checkMode(GordianEncryptMode.ENCRYPT);

            /* Encrypt the message */
            return theEncryptor.processBlock(pBytes, 0, pBytes.length);
        } catch (InvalidCipherTextException e) {
            throw new GordianCryptoException("Failed to encrypt data", e);
        }
    }

    @Override
    public byte[] decrypt(final byte[] pBytes) throws GordianException {
        try {
            /* Check that we are in decryption mode */
            checkMode(GordianEncryptMode.DECRYPT);

            /* Decrypt the message */
            return theEncryptor.processBlock(pBytes, 0, pBytes.length);
        } catch (InvalidCipherTextException e) {
            throw new GordianCryptoException("Failed to decrypt data", e);
        }
    }
}
