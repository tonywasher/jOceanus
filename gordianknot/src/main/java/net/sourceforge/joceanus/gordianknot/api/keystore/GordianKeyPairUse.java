/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.api.keystore;

import org.bouncycastle.asn1.x509.KeyUsage;

/**
 * KeyPair Usage.
 */
public enum GordianKeyPairUse {
    /**
     * Certificates.
     */
    CERTIFICATE(KeyUsage.keyCertSign),

    /**
     * Signatures.
     */
    SIGNATURE(KeyUsage.digitalSignature),

    /**
     * NonRepudiation.
     */
    NONREPUDIATION(KeyUsage.nonRepudiation),

    /**
     * KeyAgreement.
     */
    AGREEMENT(KeyUsage.keyAgreement),

    /**
     * keyEncryption.
     */
    KEYENCRYPT(KeyUsage.keyEncipherment),

    /**
     * dataEncryption.
     */
    DATAENCRYPT(KeyUsage.dataEncipherment),

    /**
     * EncryptOnly.
     */
    ENCRYPTONLY(KeyUsage.encipherOnly),

    /**
     * DecryptOnly.
     */
    DECRYPTONLY(KeyUsage.decipherOnly);

    /**
     * The KeyUsage.
     */
    private final int theUsage;

    /**
     * Constructor.
     *
     * @param pUsage the usage.
     */
    GordianKeyPairUse(final int pUsage) {
        theUsage = pUsage;
    }

    /**
     * Obtain the usage.
     *
     * @return the usage
     */
    public int getUsage() {
        return theUsage;
    }
}
