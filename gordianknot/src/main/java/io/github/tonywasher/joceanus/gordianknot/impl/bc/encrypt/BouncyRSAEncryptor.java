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

package io.github.tonywasher.joceanus.gordianknot.impl.bc.encrypt;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyRSAKeyPair.BouncyRSAPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyRSAKeyPair.BouncyRSAPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.encrypt.GordianCoreEncryptorSpec;
import org.bouncycastle.crypto.engines.RSABlindedEngine;

/**
 * RSA Encryptor.
 */
public class BouncyRSAEncryptor
        extends BouncyCoreEncryptor {
    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pSpec    the encryptorSpec
     * @throws GordianException on error
     */
    BouncyRSAEncryptor(final GordianBaseFactory pFactory,
                       final GordianCoreEncryptorSpec pSpec) throws GordianException {
        /* Initialise underlying cipher */
        super(pFactory, pSpec, new RSABlindedEngine());
    }

    @Override
    protected BouncyRSAPublicKey getPublicKey() {
        return (BouncyRSAPublicKey) super.getPublicKey();
    }

    @Override
    protected BouncyRSAPrivateKey getPrivateKey() {
        return (BouncyRSAPrivateKey) super.getPrivateKey();
    }
}
