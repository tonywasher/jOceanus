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

package io.github.tonywasher.joceanus.gordianknot.impl.jca.sign;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignParams;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair;

/**
 * MLDSA signature.
 */
public class JcaMLDSASignature
        extends JcaSignature {
    /**
     * Base name.
     */
    private static final String BASE_NAME = "ML-DSA";

    /**
     * Constructor.
     *
     * @param pFactory       the factory
     * @param pSignatureSpec the signatureSpec
     */
    JcaMLDSASignature(final GordianBaseFactory pFactory,
                      final GordianSignatureSpec pSignatureSpec) {
        /* Initialise class */
        super(pFactory, pSignatureSpec);
    }

    @Override
    public void initForSigning(final GordianSignParams pParams) throws GordianException {
        /* Determine the required signer */
        final GordianKeyPair myPair = pParams.getKeyPair();
        JcaKeyPair.checkKeyPair(myPair);
        final String mySignName = getAlgorithmForKeyPair(myPair);
        setSigner(getJavaSignature(mySignName, false));

        /* pass on call */
        super.initForSigning(pParams);
    }

    @Override
    public void initForVerify(final GordianSignParams pParams) throws GordianException {
        /* Determine the required signer */
        final GordianKeyPair myPair = pParams.getKeyPair();
        JcaKeyPair.checkKeyPair(myPair);
        final String mySignName = getAlgorithmForKeyPair(myPair);
        setSigner(getJavaSignature(mySignName, false));

        /* pass on call */
        super.initForVerify(pParams);
    }

    /**
     * Obtain algorithmName for keyPair.
     *
     * @param pKeyPair the keyPair
     * @return the name
     */
    private static String getAlgorithmForKeyPair(final GordianKeyPair pKeyPair) {
        /* Build the algorithm */
        final GordianCoreKeyPairSpec mySpec = (GordianCoreKeyPairSpec) pKeyPair.getKeyPairSpec();
        final boolean isHash = mySpec.getMLDSASpec().isHash();
        return isHash ? PQC_HASH_PFX + BASE_NAME : BASE_NAME;
    }
}
