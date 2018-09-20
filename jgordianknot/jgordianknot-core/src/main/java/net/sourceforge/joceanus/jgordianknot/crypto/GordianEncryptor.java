/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto;

import java.security.KeyPair;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Asymmetric Encryptor.
 */
public abstract class GordianEncryptor {
    /**
     * The encryptorSpec.
     */
    private final GordianEncryptorSpec theSpec;

    /**
     * The KeyPair.
     */
    private GordianKeyPair theKeyPair;

    /**
     * Encrypt Mode.
     */
    private GordianEncryptMode theMode;

    /**
     * Constructor.
     * @param pSpec the encryptorSpec
     */
    GordianEncryptor(final GordianEncryptorSpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the encryptorSpec.
     * @return the spec
     */
    protected GordianEncryptorSpec getEncryptorSpec() {
        return theSpec;
    }

    /**
     * CheckKeyPair.
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    protected void checkKeyPair(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Check that the KeyPair is valid */
        if (pKeyPair.getKeySpec().getKeyType() != theSpec.getKeyType()) {
            throw new GordianDataException("Invalid KeyPair");
        }
    }

    /**
     * Obtain public key from pair.
     * @return the public key
     */
    protected GordianPublicKey getPublicKey() {
        return theKeyPair.getPublicKey();
    }

    /**
     * Obtain private key from pair.
     * @return the private key
     */
    protected GordianPrivateKey getPrivateKey() {
        return theKeyPair.getPrivateKey();
    }

    /**
     * Initialise for signature.
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    public void initForEncrypt(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Check that the keyPair matches */
        checkKeyPair(pKeyPair);

        /* Store details */
        theMode = GordianEncryptMode.ENCRYPT;
        theKeyPair = pKeyPair;
    }

    /**
     * Initialise for verify.
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    public void initForDecrypt(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Check that the keyPair matches */
        checkKeyPair(pKeyPair);

        /* Check that we have the private key */
        if (pKeyPair.isPublicOnly()) {
            throw new GordianDataException("Missing privateKey");
        }

        /* Store details */
        theMode = GordianEncryptMode.DECRYPT;
        theKeyPair = pKeyPair;
    }

    /**
     * Check that we are in the correct mode.
     * @param pMode the required mode
     * @throws OceanusException on error
     */
    protected void checkMode(final GordianEncryptMode pMode) throws OceanusException {
        if (!pMode.equals(theMode)) {
            throw new GordianDataException("Incorrect encryption Mode");
        }
    }

    /**
     * Encrypt the bytes.
     * @param pBytes the bytes to encrypt
     * @return the encrypted bytes
     * @throws OceanusException on error
     */
    public abstract byte[] encrypt(byte[] pBytes) throws OceanusException;

    /**
     * Decrypt the encrypted bytes.
     * @param pEncrypted the encrypted bytes
     * @return the decrypted bytes
     * @throws OceanusException on error
     */
    public abstract byte[] decrypt(byte[] pEncrypted) throws OceanusException;

    /**
     * SignatureMode.
     */
    public enum GordianEncryptMode {
        /**
         * Encrypt.
         */
        ENCRYPT,

        /**
         * Decrypt.
         */
        DECRYPT;
    }
}
