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
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianSM9EncryptionMode;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.encrypt.GordianCoreEncryptor;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.encrypt.GordianCoreEncryptorSpec;
import org.bouncycastle.asn1.gm.SM9Cipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM9Engine;
import org.bouncycastle.crypto.engines.SM9Engine.Mode;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;

import java.io.IOException;


/**
 * SM9 Encryptor.
 */
public class BouncySM9Encryptor
        extends GordianCoreEncryptor {
    /**
     * The underlying encryptor.
     */
    private final SM9Engine theEncryptor;

    /**
     * The Mode.
     */
    private final Mode theMode;

    /**
     * The Private Key.
     */
    private CipherParameters thePrivateKey;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pSpec    the encryptorSpec
     * @throws GordianException on error
     */
    BouncySM9Encryptor(final GordianBaseFactory pFactory,
                       final GordianCoreEncryptorSpec pSpec) throws GordianException {
        /* Initialise underlying cipher */
        super(pFactory, pSpec);
        theMode = pSpec.getSM9EncryptionMode() == GordianSM9EncryptionMode.SM4
                ? Mode.SM4 : Mode.STREAM;
        theEncryptor = new SM9Engine(theMode);
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
        thePrivateKey = getPrivateKey().getPrivateKey();
    }

    @Override
    public byte[] encrypt(final byte[] pBytes) throws GordianException {
        try {
            /* Check that we are in encryption mode */
            checkMode(GordianEncryptMode.ENCRYPT);

            /* Reject null data */
            if (pBytes == null) {
                throw new GordianDataException("Null Message");
            }

            /* Encrypt the message */
            final byte[] myRaw = theEncryptor.processBlock(pBytes, 0, pBytes.length);
            final byte[] c1 = new byte[GordianLength.LEN_64.getLength() + 1];
            c1[0] = 0x04;
            System.arraycopy(myRaw, 0, c1, 1, GordianLength.LEN_64.getLength());
            final byte[] c3 = Arrays.copyOfRange(myRaw, GordianLength.LEN_64.getLength(), GordianLength.LEN_96.getLength());
            final byte[] c2 = Arrays.copyOfRange(myRaw, GordianLength.LEN_96.getLength(), myRaw.length);
            final int enType = theMode == Mode.SM4 ? SM9Cipher.EN_TYPE_SM4 : SM9Cipher.EN_TYPE_STREAM;
            return new SM9Cipher(enType, c1, c3, c2).getEncoded();

        } catch (InvalidCipherTextException
                 | IOException e) {
            throw new GordianCryptoException("Failed to encrypt data", e);
        }
    }

    @Override
    public byte[] decrypt(final byte[] pBytes) throws GordianException {
        try {
            /* Check that we are in decryption mode */
            checkMode(GordianEncryptMode.DECRYPT);

            /* Reject null data */
            if (pBytes == null) {
                throw new GordianDataException("Null Message");
            }

            /* Decrypt the message */
            final SM9Cipher c = SM9Cipher.getInstance(pBytes);
            final byte[] c1 = c.getC1();
            final byte[] myRaw = Arrays.concatenate(Arrays.copyOfRange(c1, 1, GordianLength.LEN_64.getLength() + 1), c.getC3(), c.getC2());
            final SM9Engine myEngine = new SM9Engine(
                    (c.getEnType() == SM9Cipher.EN_TYPE_SM4) ? Mode.SM4 : Mode.STREAM);
            myEngine.init(false, thePrivateKey);
            return myEngine.processBlock(myRaw, 0, myRaw.length);

        } catch (InvalidCipherTextException e) {
            throw new GordianCryptoException("Failed to decrypt data", e);
        }
    }
}
