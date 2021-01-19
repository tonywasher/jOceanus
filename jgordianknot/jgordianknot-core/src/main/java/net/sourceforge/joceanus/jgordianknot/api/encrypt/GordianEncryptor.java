/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.encrypt;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Asymmetric Encryptor.
 * @param <S> the specification type
 * @param <K> the keyPair Type
 */
public interface GordianEncryptor<S, K> {
    /**
     * Obtain the encryptorSpec.
     * @return the spec
     */
    S getEncryptorSpec();

    /**
     * Initialise for encrypt.
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    void initForEncrypt(K pKeyPair) throws OceanusException;

    /**
     * Initialise for decrypt.
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    void initForDecrypt(K pKeyPair) throws OceanusException;

     /**
     * Encrypt the bytes.
     * @param pBytes the bytes to encrypt
     * @return the encrypted bytes
     * @throws OceanusException on error
     */
    byte[] encrypt(byte[] pBytes) throws OceanusException;

    /**
     * Decrypt the encrypted bytes.
     * @param pEncrypted the encrypted bytes
     * @return the decrypted bytes
     * @throws OceanusException on error
     */
    byte[] decrypt(byte[] pEncrypted) throws OceanusException;
}
