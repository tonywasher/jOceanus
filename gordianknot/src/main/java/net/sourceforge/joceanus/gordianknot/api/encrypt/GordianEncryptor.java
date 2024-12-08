/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.api.encrypt;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;

/**
 * Asymmetric Encryptor.
 */
public interface GordianEncryptor {
    /**
     * Obtain the encryptorSpec.
     * @return the spec
     */
    GordianEncryptorSpec getEncryptorSpec();

    /**
     * Initialise for encrypt.
     * @param pKeyPair the keyPair
     * @throws GordianException on error
     */
    void initForEncrypt(GordianKeyPair pKeyPair) throws GordianException;

    /**
     * Initialise for decrypt.
     * @param pKeyPair the keyPair
     * @throws GordianException on error
     */
    void initForDecrypt(GordianKeyPair pKeyPair) throws GordianException;

     /**
     * Encrypt the bytes.
     * @param pBytes the bytes to encrypt
     * @return the encrypted bytes
     * @throws GordianException on error
     */
    byte[] encrypt(byte[] pBytes) throws GordianException;

    /**
     * Decrypt the encrypted bytes.
     * @param pEncrypted the encrypted bytes
     * @return the decrypted bytes
     * @throws GordianException on error
     */
    byte[] decrypt(byte[] pEncrypted) throws GordianException;
}
