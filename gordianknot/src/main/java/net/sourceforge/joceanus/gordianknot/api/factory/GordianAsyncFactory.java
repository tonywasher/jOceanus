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
package net.sourceforge.joceanus.gordianknot.api.factory;

import net.sourceforge.joceanus.gordianknot.api.encrypt.GordianEncryptorFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairFactory;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreFactory;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureFactory;
import net.sourceforge.joceanus.gordianknot.api.xagree.GordianXAgreementFactory;

/**
 * Async Factory API.
 */
public interface GordianAsyncFactory {
    /**
     * Obtain the keyPairFactory.
     *
     * @return the keyPair factory
     */
    GordianKeyPairFactory getKeyPairFactory();

    /**
     * Obtain the signatureFactory.
     *
     * @return the signature factory
     */
    GordianSignatureFactory getSignatureFactory();

    /**
     * Obtain the XagreementFactory.
     *
     * @return the agreement factory
     */
    GordianXAgreementFactory getXAgreementFactory();

    /**
     * Obtain the encryptorFactory.
     *
     * @return the encryptor factory
     */
    GordianEncryptorFactory getEncryptorFactory();

    /**
     * Obtain the keyStore Factory.
     *
     * @return the keyStore factory
     */
    GordianKeyStoreFactory getKeyStoreFactory();
}
