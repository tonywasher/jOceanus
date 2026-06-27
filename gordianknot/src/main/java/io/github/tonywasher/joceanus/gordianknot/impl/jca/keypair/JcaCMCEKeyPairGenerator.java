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
import org.bouncycastle.jcajce.spec.CMCEParameterSpec;

import java.security.InvalidAlgorithmParameterException;

/**
 * Jca CMCE KeyPair generator.
 */
public class JcaCMCEKeyPairGenerator
        extends JcaKeyPairGenerator {
    /**
     * CMCE algorithm.
     */
    private static final String CMCE_ALGO = "CMCE";

    /**
     * Constructor.
     *
     * @param pFactory the Security Factory
     * @param pKeySpec the keySpec
     * @throws GordianException on error
     */
    JcaCMCEKeyPairGenerator(final GordianBaseFactory pFactory,
                            final GordianKeyPairSpec pKeySpec) throws GordianException {
        /* initialize underlying class */
        super(pFactory, pKeySpec);

        /* Protect against exceptions */
        try {
            /* Create and initialize the generator */
            createFactories(CMCE_ALGO, false);
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final CMCEParameterSpec myParms = myKeySpec.getCMCESpec().getParameterSpec();
            getGenerator().initialize(myParms, getRandom());

        } catch (InvalidAlgorithmParameterException e) {
            throw new GordianCryptoException("Failed to create CMCEgenerator", e);
        }
    }
}
