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
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreDHSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair.JcaDHPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair.JcaDHPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair.JcaPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair.JcaPublicKey;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPublicKey;
import org.bouncycastle.jcajce.spec.DHDomainParameterSpec;

import java.security.InvalidAlgorithmParameterException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Jca DiffieHellman KeyPair generator.
 */
public class JcaDHKeyPairGenerator
        extends JcaKeyPairGenerator {
    /**
     * DH algorithm.
     */
    private static final String DH_ALGO = "DH";

    /**
     * Constructor.
     *
     * @param pFactory the Security Factory
     * @param pKeySpec the keySpec
     * @throws GordianException on error
     */
    JcaDHKeyPairGenerator(final GordianBaseFactory pFactory,
                          final GordianKeyPairSpec pKeySpec) throws GordianException {
        /* initialize underlying class */
        super(pFactory, pKeySpec);

        /* Protect against exceptions */
        try {
            /* Create the parameter generator */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final GordianCoreDHSpec myGroup = myKeySpec.getDHSpec();
            final DHParameters myParms = myGroup.getParameters();
            final DHDomainParameterSpec mySpec = new DHDomainParameterSpec(myParms);

            /* Create and initialize the generator */
            createFactories(DH_ALGO, false);
            getGenerator().initialize(mySpec, getRandom());

        } catch (InvalidAlgorithmParameterException e) {
            throw new GordianCryptoException("Failed to create DHgenerator", e);
        }
    }

    @Override
    protected JcaPrivateKey createPrivate(final PrivateKey pPrivateKey) {
        return new JcaDHPrivateKey(getKeySpec(), (BCDHPrivateKey) pPrivateKey);
    }

    @Override
    protected JcaPublicKey createPublic(final PublicKey pPublicKey) {
        return new JcaDHPublicKey(getKeySpec(), (BCDHPublicKey) pPublicKey);
    }
}
