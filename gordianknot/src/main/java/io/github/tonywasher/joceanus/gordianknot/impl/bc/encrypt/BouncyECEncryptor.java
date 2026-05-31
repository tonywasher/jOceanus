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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.encrypt.GordianCoreEncryptor;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.encrypt.GordianCoreEncryptorSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.engines.GordianEllipticEncryptor;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;

/**
 * EC Encryptor.
 */
public class BouncyECEncryptor
        extends GordianCoreEncryptor {
    /**
     * The underlying encryptor.
     */
    private final GordianEllipticEncryptor theEncryptor;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pSpec    the encryptorSpec
     */
    BouncyECEncryptor(final GordianBaseFactory pFactory,
                      final GordianCoreEncryptorSpec pSpec) {
        /* Initialise underlying cipher */
        super(pFactory, pSpec);
        theEncryptor = new GordianEllipticEncryptor();
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
        /* Initialize underlying cipher */
        BouncyKeyPair.checkKeyPair(pKeyPair);
        super.initForEncrypt(pKeyPair);

        /* Initialize for encryption */
        final ECPublicKeyParameters myParms = (ECPublicKeyParameters) getPublicKey().getPublicKey();
        theEncryptor.initForEncrypt(myParms, getRandom());
    }

    @Override
    public void initForDecrypt(final GordianKeyPair pKeyPair) throws GordianException {
        /* Initialize underlying cipher */
        BouncyKeyPair.checkKeyPair(pKeyPair);
        super.initForDecrypt(pKeyPair);

        /* Initialize for decryption */
        final ECPrivateKeyParameters myParms = (ECPrivateKeyParameters) getPrivateKey().getPrivateKey();
        theEncryptor.initForDecrypt(myParms);
    }

    @Override
    public byte[] encrypt(final byte[] pBytes) throws GordianException {
        try {
            /* Check that we are in encryption mode */
            checkMode(GordianEncryptMode.ENCRYPT);

            /* Encrypt the message */
            return theEncryptor.encrypt(pBytes);
        } catch (InvalidCipherTextException e) {
            throw new GordianCryptoException("Failed to process data", e);
        }
    }

    @Override
    public byte[] decrypt(final byte[] pBytes) throws GordianException {
        try {
            /* Check that we are in decryption mode */
            checkMode(GordianEncryptMode.DECRYPT);

            /* Decrypt the message */
            return theEncryptor.decrypt(pBytes);
        } catch (InvalidCipherTextException e) {
            throw new GordianCryptoException("Failed to process data", e);
        }
    }
}
