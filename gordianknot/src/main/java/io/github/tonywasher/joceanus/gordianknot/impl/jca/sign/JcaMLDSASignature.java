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

import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignParams;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
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
     * @throws GordianException on error
     */
    JcaMLDSASignature(final GordianBaseFactory pFactory,
                      final GordianSignatureSpec pSignatureSpec) throws GordianException {
        /* Initialise class */
        super(pFactory, pSignatureSpec);
        final boolean isHash = GordianSignatureType.PREHASH.equals(pSignatureSpec.getSignatureType());
        final String myName = isHash ? PQC_HASH_PFX + BASE_NAME : BASE_NAME;
        setSigner(getJavaSignature(myName, false));
    }

    @Override
    public void initForSigning(final GordianSignParams pParams) throws GordianException {
        /* Determine the required signer */
        final GordianKeyPair myPair = pParams.getKeyPair();
        JcaKeyPair.checkKeyPair(myPair);
        checkKeyPairForSignature(myPair);

        /* pass on call */
        super.initForSigning(pParams);
    }

    @Override
    public void initForVerify(final GordianSignParams pParams) throws GordianException {
        /* Determine the required signer */
        final GordianKeyPair myPair = pParams.getKeyPair();
        JcaKeyPair.checkKeyPair(myPair);
        checkKeyPairForSignature(myPair);

        /* pass on call */
        super.initForVerify(pParams);
    }
}
