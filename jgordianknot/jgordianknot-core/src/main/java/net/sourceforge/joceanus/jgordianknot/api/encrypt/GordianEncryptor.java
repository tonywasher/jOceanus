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
package net.sourceforge.joceanus.jgordianknot.api.encrypt;

import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
     * Initialise for signature.
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    void initForEncrypt(GordianKeyPair pKeyPair) throws OceanusException;

    /**
     * Initialise for verify.
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    void initForDecrypt(GordianKeyPair pKeyPair) throws OceanusException;

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

