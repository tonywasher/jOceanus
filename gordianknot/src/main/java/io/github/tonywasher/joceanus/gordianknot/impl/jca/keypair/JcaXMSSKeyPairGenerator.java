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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianXMSSSpec.GordianXMSSDigestType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreXMSSSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPairGenerator.JcaStateAwareKeyPairGenerator;
import org.bouncycastle.pqc.jcajce.spec.XMSSMTParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.XMSSParameterSpec;

import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;

/**
 * Jca XMSS KeyPair generator.
 */
public class JcaXMSSKeyPairGenerator
        extends JcaStateAwareKeyPairGenerator {
    /**
     * Constructor.
     *
     * @param pFactory the Security Factory
     * @param pKeySpec the keySpec
     * @throws GordianException on error
     */
    JcaXMSSKeyPairGenerator(final GordianBaseFactory pFactory,
                            final GordianKeyPairSpec pKeySpec) throws GordianException {
        /* initialize underlying class */
        super(pFactory, pKeySpec);

        /* Protect against exceptions */
        try {
            /* Access the algorithm */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final GordianCoreXMSSSpec myXMSSKeySpec = myKeySpec.getXMSSSpec();
            final boolean isXMSSMT = myXMSSKeySpec.isMT();
            final GordianXMSSDigestType myType = myXMSSKeySpec.getDigestType();

            /* Create the parameters */
            final AlgorithmParameterSpec myAlgo = isXMSSMT
                    ? new XMSSMTParameterSpec(myXMSSKeySpec.getHeight().getHeight(),
                    myXMSSKeySpec.getLayers().getLayers(), myType.name())
                    : new XMSSParameterSpec(myXMSSKeySpec.getHeight().getHeight(), myType.name());

            /* Create and initialize the generator */
            final String myJavaType = myXMSSKeySpec.getKeyType().name();
            createFactories(myJavaType, true);
            getGenerator().initialize(myAlgo, getRandom());

        } catch (InvalidAlgorithmParameterException e) {
            throw new GordianCryptoException("Failed to create XMSSgenerator", e);
        }
    }
}
