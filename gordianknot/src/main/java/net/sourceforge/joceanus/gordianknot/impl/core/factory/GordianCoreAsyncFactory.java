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
package net.sourceforge.joceanus.gordianknot.impl.core.factory;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementFactory;
import net.sourceforge.joceanus.gordianknot.api.encrypt.GordianEncryptorFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianAsyncFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairFactory;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreFactory;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureFactory;
import net.sourceforge.joceanus.gordianknot.api.xagree.GordianXAgreementFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;

/**
 * GordianKnot Core AsyncFactory.
 */
public class GordianCoreAsyncFactory
        implements GordianAsyncFactory {
    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The keyPair factory.
     */
    private GordianKeyPairFactory theKeyPairFactory;

    /**
     * The signature factory.
     */
    private GordianSignatureFactory theSignatureFactory;

    /**
     * The agreement factory.
     */
    private GordianAgreementFactory theAgreementFactory;

    /**
     * The XAgreement factory.
     */
    private GordianXAgreementFactory theXAgreementFactory;

    /**
     * The encryptor factory.
     */
    private GordianEncryptorFactory theEncryptorFactory;

    /**
     * The keyStore factory.
     */
    private GordianKeyStoreFactory theKeyStoreFactory;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    protected GordianCoreAsyncFactory(final GordianCoreFactory pFactory) {
        theFactory = pFactory;
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    public GordianCoreFactory getFactory() {
        return theFactory;
    }

    @Override
    public GordianKeyPairFactory getKeyPairFactory() {
        return theKeyPairFactory;
    }

    /**
     * Set the keyPair factory.
     * @param pFactory the factory
     */
    protected void setKeyPairFactory(final GordianKeyPairFactory pFactory) {
        theKeyPairFactory = pFactory;
    }

    @Override
    public GordianSignatureFactory getSignatureFactory() {
        return theSignatureFactory;
    }

    /**
     * Set the signature factory.
     * @param pFactory the factory
     */
    protected void setSignatureFactory(final GordianSignatureFactory pFactory) {
        theSignatureFactory = pFactory;
    }

    @Override
    public GordianAgreementFactory getAgreementFactory() {
        return theAgreementFactory;
    }

    /**
     * Set the agreement factory.
     * @param pFactory the factory
     */
    protected void setAgreementFactory(final GordianAgreementFactory pFactory) {
        theAgreementFactory = pFactory;
    }

    @Override
    public GordianXAgreementFactory getXAgreementFactory() {
        return theXAgreementFactory;
    }

    /**
     * Set the agreement factory.
     * @param pFactory the factory
     */
    protected void setXAgreementFactory(final GordianXAgreementFactory pFactory) {
        theXAgreementFactory = pFactory;
    }

    @Override
    public GordianEncryptorFactory getEncryptorFactory() {
        return theEncryptorFactory;
    }

    /**
     * Set the encryptor factory.
     * @param pFactory the factory
     */
    protected void setEncryptorFactory(final GordianEncryptorFactory pFactory) {
        theEncryptorFactory = pFactory;
    }

    @Override
    public GordianKeyStoreFactory getKeyStoreFactory() {
        return theKeyStoreFactory;
    }

    /**
     * Set the keyStore factory.
     * @param pFactory the factory
     */
    protected void setKeyStoreFactory(final GordianKeyStoreFactory pFactory) {
        theKeyStoreFactory = pFactory;
    }
}
