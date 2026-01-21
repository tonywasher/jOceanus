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
package io.github.tonywasher.joceanus.gordianknot.impl.core.factory;

import io.github.tonywasher.joceanus.gordianknot.api.agree.GordianAgreementFactory;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.GordianEncryptorFactory;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianAsyncFactory;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairFactory;
import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStoreFactory;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignatureFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keystore.GordianCoreKeyStoreFactory;

/**
 * GordianKnot Core AsyncFactory.
 */
public abstract class GordianCoreAsyncFactory
        implements GordianAsyncFactory {
    /**
     * The factory.
     */
    private final GordianBaseFactory theFactory;

    /**
     * The keyPair factory.
     */
    private final GordianKeyPairFactory theKeyPairFactory;

    /**
     * The signature factory.
     */
    private final GordianSignatureFactory theSignatureFactory;

    /**
     * The XAgreement factory.
     */
    private final GordianAgreementFactory theXAgreementFactory;

    /**
     * The encryptor factory.
     */
    private final GordianEncryptorFactory theEncryptorFactory;

    /**
     * The keyStore factory.
     */
    private final GordianKeyStoreFactory theKeyStoreFactory;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    protected GordianCoreAsyncFactory(final GordianBaseFactory pFactory) {
        theFactory = pFactory;
        theKeyPairFactory = newKeyPairFactory(theFactory);
        theSignatureFactory = newSignatureFactory(theFactory);
        theXAgreementFactory = newAgreementFactory(theFactory);
        theEncryptorFactory = newEncryptorFactory(theFactory);
        theKeyStoreFactory = new GordianCoreKeyStoreFactory(theFactory);
    }

    /**
     * Obtain the factory.
     *
     * @return the factory
     */
    public GordianBaseFactory getFactory() {
        return theFactory;
    }

    @Override
    public GordianKeyPairFactory getKeyPairFactory() {
        return theKeyPairFactory;
    }

    /**
     * Create a new keyPair factory.
     *
     * @param pFactory the factory
     * @return the new keyPair factory
     */
    public abstract GordianKeyPairFactory newKeyPairFactory(GordianBaseFactory pFactory);

    @Override
    public GordianSignatureFactory getSignatureFactory() {
        return theSignatureFactory;
    }

    /**
     * Create a new signature factory.
     *
     * @param pFactory the factory
     * @return the new keyPair factory
     */
    public abstract GordianSignatureFactory newSignatureFactory(GordianBaseFactory pFactory);

    @Override
    public GordianAgreementFactory getAgreementFactory() {
        return theXAgreementFactory;
    }

    /**
     * Create a new Agreement factory.
     *
     * @param pFactory the factory
     * @return the new agreement factory
     */
    public abstract GordianAgreementFactory newAgreementFactory(GordianBaseFactory pFactory);

    @Override
    public GordianEncryptorFactory getEncryptorFactory() {
        return theEncryptorFactory;
    }

    /**
     * Create a new encryptor factory.
     *
     * @param pFactory the factory
     * @return the new encryptor factory
     */
    public abstract GordianEncryptorFactory newEncryptorFactory(GordianBaseFactory pFactory);

    @Override
    public GordianKeyStoreFactory getKeyStoreFactory() {
        return theKeyStoreFactory;
    }
}
