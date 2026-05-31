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
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianNewSignParams;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreXMSSSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.digest.JcaDigest;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair;

/**
 * XMSS signature.
 */
public class JcaXMSSSignature
        extends JcaSignature {
    /**
     * Is this a preHash signature?
     */
    private final boolean preHash;

    /**
     * Constructor.
     *
     * @param pFactory       the factory
     * @param pSignatureSpec the signatureSpec
     */
    JcaXMSSSignature(final GordianBaseFactory pFactory,
                     final GordianSignatureSpec pSignatureSpec) {
        /* Initialise class */
        super(pFactory, pSignatureSpec);

        /* Determine preHash */
        preHash = GordianSignatureType.PREHASH.equals(pSignatureSpec.getSignatureType());
    }

    @Override
    public void initForSigning(final GordianNewSignParams pParams) throws GordianException {
        /* Determine the required signer */
        final GordianKeyPair myPair = pParams.getKeyPair();
        JcaKeyPair.checkKeyPair(myPair);
        final String mySignName = getAlgorithmForKeyPair(myPair);
        setSigner(getJavaSignature(mySignName, true));

        /* pass on call */
        super.initForSigning(pParams);
    }

    @Override
    public void initForVerify(final GordianNewSignParams pParams) throws GordianException {
        /* Determine the required signer */
        final GordianKeyPair myPair = pParams.getKeyPair();
        JcaKeyPair.checkKeyPair(myPair);
        final String mySignName = getAlgorithmForKeyPair(myPair);
        setSigner(getJavaSignature(mySignName, true));

        /* pass on call */
        super.initForVerify(pParams);
    }

    /**
     * Obtain algorithmName for keyPair.
     *
     * @param pKeyPair the keyPair
     * @return the name
     * @throws GordianException on error
     */
    private String getAlgorithmForKeyPair(final GordianKeyPair pKeyPair) throws GordianException {
        /* Determine the required signer */
        final GordianCoreKeyPairSpec mySpec = (GordianCoreKeyPairSpec) pKeyPair.getKeyPairSpec();
        final GordianCoreXMSSSpec myXMSSKeySpec = mySpec.getXMSSSpec();
        final GordianCoreDigestSpec myDigestSpec = (GordianCoreDigestSpec) myXMSSKeySpec.getDigestSpec();
        final String myDigest = JcaDigest.getAlgorithm(myDigestSpec);

        /* Create builder */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(myXMSSKeySpec.getKeyType().name())
                .append('-')
                .append(myDigest);
        if (preHash) {
            myBuilder.insert(0, "with")
                    .insert(0, myDigest);
        }

        /* Build the algorithm */
        return myBuilder.toString();
    }
}
