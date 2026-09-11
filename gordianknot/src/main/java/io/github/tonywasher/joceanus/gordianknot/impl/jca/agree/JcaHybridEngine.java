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

package io.github.tonywasher.joceanus.gordianknot.impl.jca.agree;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigestFactory;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.GordianEncryptor;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.GordianEncryptorFactory;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianEncryptorSpec;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianEncryptorSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree.GordianCoreAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.agree.JcaAgreement.JcaAgreementBase;

/**
 * RSA Wrap Agreement Engine.
 */
public class JcaHybridEngine
        extends JcaAgreementBase {
    /**
     * Key Length.
     */
    private static final int KEYLEN = 32;

    /**
     * Encryptor.
     */
    private final GordianEncryptor theEncryptor;

    /**
     * Constructor.
     *
     * @param pFactory the security factory
     * @param pSpec    the agreementSpec
     * @throws GordianException on error
     */
    JcaHybridEngine(final GordianCoreAgreementFactory pFactory,
                    final GordianCoreAgreementSpec pSpec) throws GordianException {
        /* Initialize underlying class */
        super(pFactory, pSpec);

        /* Create the encryptor */
        final GordianDigestFactory myDigestFactory = pFactory.getFactory().getDigestFactory();
        final GordianEncryptorFactory myEncryptorFactory = pFactory.getFactory().getAsymFactory().getEncryptorFactory();
        final GordianDigestSpecBuilder myDigestBuilder = myDigestFactory.newDigestSpecBuilder();
        final GordianEncryptorSpecBuilder myEncBuilder = myEncryptorFactory.newEncryptorSpecBuilder();
        final GordianEncryptorSpec myEncSpec = myEncBuilder.rsa(myDigestBuilder.sha2(GordianLength.LEN_256));
        theEncryptor = myEncryptorFactory.createEncryptor(myEncSpec);
    }

    @Override
    public void buildClientHello() throws GordianException {
        /* Create the secret */
        final byte[] mySecret = new byte[KEYLEN];
        getRandom().nextBytes(mySecret);

        /* Wrap the secret */
        theEncryptor.initForEncrypt(getServerKeyPair());
        final byte[] myEncapsulation = theEncryptor.encrypt(mySecret);

        /* Store the encapsulation */
        setClientEncapsulated(myEncapsulation);

        /* Store secret */
        storeSecret(mySecret);
    }

    @Override
    public void processClientHello() throws GordianException {
        /* Unwrap the secret */
        theEncryptor.initForDecrypt(getServerKeyPair());
        final byte[] mySecret = theEncryptor.decrypt(getClientEncapsulated());

        /* Store the secret */
        storeSecret(mySecret);
    }
}
