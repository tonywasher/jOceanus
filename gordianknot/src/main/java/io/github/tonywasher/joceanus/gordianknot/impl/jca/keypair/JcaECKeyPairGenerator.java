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

import java.security.InvalidAlgorithmParameterException;
import java.security.spec.ECGenParameterSpec;

/**
 * Jca Elliptic KeyPair generator.
 */
public class JcaECKeyPairGenerator
        extends JcaKeyPairGenerator {
    /**
     * Constructor.
     *
     * @param pFactory the Security Factory
     * @param pKeySpec the keySpec
     * @throws GordianException on error
     */
    JcaECKeyPairGenerator(final GordianBaseFactory pFactory,
                          final GordianKeyPairSpec pKeySpec) throws GordianException {
        /* initialize underlying class */
        super(pFactory, pKeySpec);

        /* Protect against exceptions */
        try {
            /* Create and initialize the generator */
            final String myAlgo = getAlgorithm();
            createFactories(myAlgo, false);
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final ECGenParameterSpec myParms = new ECGenParameterSpec(myKeySpec.getElliptic().getCurveName());
            getGenerator().initialize(myParms, getRandom());

        } catch (InvalidAlgorithmParameterException e) {
            throw new GordianCryptoException("Failed to create ECgenerator for:  " + pKeySpec, e);
        }
    }

    /**
     * Obtain algorithm for keySpec.
     *
     * @return the algorithm
     */
    private String getAlgorithm() {
        return switch (this.getKeySpec().getKeyPairType()) {
            case DSTU -> "DSTU4145";
            case GOST -> "ECGOST3410-2012";
            default -> "EC";
        };
    }
}
