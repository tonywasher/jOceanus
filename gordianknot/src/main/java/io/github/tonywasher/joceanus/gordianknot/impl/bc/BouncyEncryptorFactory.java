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
package io.github.tonywasher.joceanus.gordianknot.impl.bc;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.GordianEncryptor;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianEncryptorSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyElGamalKeyPair.BouncyElGamalEncryptor;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECEncryptor;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyRSAKeyPair.BouncyRSAEncryptor;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncySM2KeyPair.BouncySM2Encryptor;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.encrypt.GordianCompositeEncryptor;
import io.github.tonywasher.joceanus.gordianknot.impl.core.encrypt.GordianCoreEncryptorFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.encrypt.GordianCoreEncryptorSpec;

/**
 * Bouncy Encryptor Factory.
 */
public class BouncyEncryptorFactory
        extends GordianCoreEncryptorFactory {
    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    BouncyEncryptorFactory(final GordianBaseFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);
    }

    @Override
    public GordianEncryptor createEncryptor(final GordianEncryptorSpec pEncryptorSpec) throws GordianException {
        /* Check validity of Encryptor */
        checkEncryptorSpec(pEncryptorSpec);

        /* Create the encryptor */
        return getBCEncryptor((GordianCoreEncryptorSpec) pEncryptorSpec);
    }

    /**
     * Create the BouncyCastle Encryptor.
     *
     * @param pSpec the agreementSpec
     * @return the Agreement
     * @throws GordianException on error
     */
    private GordianEncryptor getBCEncryptor(final GordianCoreEncryptorSpec pSpec) throws GordianException {
        return switch (pSpec.getKeyPairType()) {
            case RSA -> new BouncyRSAEncryptor(getFactory(), pSpec);
            case ELGAMAL -> new BouncyElGamalEncryptor(getFactory(), pSpec);
            case EC -> new BouncyECEncryptor(getFactory(), pSpec);
            case SM2 -> new BouncySM2Encryptor(getFactory(), pSpec);
            case COMPOSITE -> new GordianCompositeEncryptor(getFactory(), pSpec);
            default -> throw new GordianDataException(GordianBaseData.getInvalidText(pSpec));
        };
    }
}
