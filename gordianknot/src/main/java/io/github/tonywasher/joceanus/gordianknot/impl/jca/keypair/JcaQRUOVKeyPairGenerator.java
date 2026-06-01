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

package io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import org.bouncycastle.pqc.jcajce.spec.QRUOVParameterSpec;

import java.security.InvalidAlgorithmParameterException;

/**
 * Jca QRUOV KeyPair generator.
 */
public class JcaQRUOVKeyPairGenerator
        extends JcaKeyPairGenerator {
    /**
     * QRUOV algorithm.
     */
    private static final String QRUOV_ALGO = "QRUOV";

    /**
     * Constructor.
     *
     * @param pFactory the Security Factory
     * @param pKeySpec the keySpec
     * @throws GordianException on error
     */
    JcaQRUOVKeyPairGenerator(final GordianBaseFactory pFactory,
                             final GordianKeyPairSpec pKeySpec) throws GordianException {
        /* initialize underlying class */
        super(pFactory, pKeySpec);

        /* Protect against exceptions */
        try {
            /* Create and initialize the generator */
            createFactories(QRUOV_ALGO, true);
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final QRUOVParameterSpec myParms = myKeySpec.getQRUOVSpec().getParameterSpec();
            getGenerator().initialize(myParms, getRandom());

        } catch (InvalidAlgorithmParameterException e) {
            throw new GordianCryptoException("Failed to create QRUOVGenerator", e);
        }
    }
}
