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

package io.github.tonywasher.joceanus.gordianknot.impl.jca.encrypt;


import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.encrypt.GordianCoreEncryptor;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.encrypt.GordianCoreEncryptorSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.encrypt.GordianCoreSM2EncryptionSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair.JcaPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair.JcaPublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;

/**
 * Hybrid Encryptor.
 */
public class JcaHybridEncryptor
        extends GordianCoreEncryptor {
    /**
     * Error string.
     */
    private static final String ERROR_INIT = "Failed to initialise";

    /**
     * The underlying encryptor.
     */
    private final Cipher theEncryptor;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pSpec    the encryptorSpec
     * @throws GordianException on error
     */
    JcaHybridEncryptor(final GordianBaseFactory pFactory,
                       final GordianCoreEncryptorSpec pSpec) throws GordianException {
        /* Initialise underlying cipher */
        super(pFactory, pSpec);
        theEncryptor = JcaEncryptor.getJavaEncryptor(getAlgorithmName(pSpec));
    }

    @Override
    protected JcaPublicKey getPublicKey() {
        return (JcaPublicKey) super.getPublicKey();
    }

    @Override
    protected JcaPrivateKey getPrivateKey() {
        return (JcaPrivateKey) super.getPrivateKey();
    }

    @Override
    public void initForEncrypt(final GordianKeyPair pKeyPair) throws GordianException {
        try {
            /* Initialise underlying cipher */
            JcaKeyPair.checkKeyPair(pKeyPair);
            super.initForEncrypt(pKeyPair);

            /* Initialise for encryption */
            theEncryptor.init(Cipher.ENCRYPT_MODE, getPublicKey().getPublicKey(), getRandom());
        } catch (InvalidKeyException e) {
            throw new GordianCryptoException(ERROR_INIT, e);
        }
    }

    @Override
    public void initForDecrypt(final GordianKeyPair pKeyPair) throws GordianException {
        try {
            /* Initialise underlying cipher */
            JcaKeyPair.checkKeyPair(pKeyPair);
            super.initForDecrypt(pKeyPair);

            /* Initialise for decryption */
            theEncryptor.init(Cipher.DECRYPT_MODE, getPrivateKey().getPrivateKey());
        } catch (InvalidKeyException e) {
            throw new GordianCryptoException(ERROR_INIT, e);
        }
    }

    @Override
    public byte[] encrypt(final byte[] pBytes) throws GordianException {
        /* Check that we are in encryption mode */
        checkMode(GordianEncryptMode.ENCRYPT);

        /* Encrypt the message */
        return processData(pBytes);
    }

    @Override
    public byte[] decrypt(final byte[] pBytes) throws GordianException {
        /* Check that we are in decryption mode */
        checkMode(GordianEncryptMode.DECRYPT);

        /* Decrypt the message */
        return processData(pBytes);
    }

    /**
     * Process a data buffer.
     *
     * @param pData the buffer to process
     * @return the processed buffer
     * @throws GordianException on error
     */
    private byte[] processData(final byte[] pData) throws GordianException {
        try {
            return theEncryptor.doFinal(pData, 0, pData.length);
        } catch (IllegalBlockSizeException
                 | BadPaddingException e) {
            throw new GordianCryptoException("Failed to process data", e);
        }
    }

    /**
     * Obtain the algorithmName.
     *
     * @param pSpec the Spec
     * @return the algorithm name
     */
    private static String getAlgorithmName(final GordianCoreEncryptorSpec pSpec) {
        /* Switch on encryptor type */
        final GordianCoreSM2EncryptionSpec mySpec = pSpec.getSM2EncryptionSpec();
        final GordianDigestSpec myDigestSpec = mySpec.getDigestSpec();
        final GordianDigestType myDigestType = myDigestSpec.getDigestType();
        final String myName = switch (myDigestType) {
            case SHA2 -> "SM2withSHA" + myDigestSpec.getDigestLength();
            case BLAKE2 -> "SM2withBlake2" + (GordianLength.LEN_512.equals(myDigestSpec.getDigestLength()) ? "b" : "s");
            default -> "SM2with" + myDigestType;
        };
        return myName + "/" + mySpec.getEncryptionType() + "/NOPADDING";
    }
}
