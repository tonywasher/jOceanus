/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
package io.github.tonywasher.joceanus.gordianknot.impl.core.keystore;

import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStoreGateway;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.cert.GordianCoreCertificate;
import org.bouncycastle.asn1.x500.X500Name;

import java.util.function.Function;

/**
 * keyStoreGateway base.
 */
public interface GordianBaseKeyStoreGateway
        extends GordianKeyStoreGateway {
    /**
     * Obtain the factory.
     *
     * @return the factory
     */
    GordianBaseFactory getFactory();

    /**
     * Obtain the MACSecret.
     *
     * @param pName the name to resolve for
     * @return the secret
     */
    byte[] getMACSecret(X500Name pName);

    /**
     * Obtain the encryptor.
     *
     * @return the encryptor
     */
    GordianCRMEncryptor getEncryptor();

    /**
     * Obtain the signer.
     *
     * @return the signer
     */
    GordianKeyStorePair getSigner();

    /**
     * Obtain the EncryptionTarget.
     *
     * @return the target
     */
    GordianCoreCertificate getTarget();

    /**
     * Obtain the PasswordResolver.
     *
     * @return the passwordResolver
     */
    Function<String, char[]> getPasswordResolver();
}
