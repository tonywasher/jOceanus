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
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.sign.GordianCoreSignatureSpec;

/**
 * GOST signature.
 */
public class JcaGOSTSignature
        extends JcaSignature {
    /**
     * The DSTU Signature.
     */
    private static final String DSTU_SIGN = "DSTU4145";

    /**
     * Constructor.
     *
     * @param pFactory       the factory
     * @param pSignatureSpec the signatureSpec
     * @throws GordianException on error
     */
    JcaGOSTSignature(final GordianBaseFactory pFactory,
                     final GordianSignatureSpec pSignatureSpec) throws GordianException {
        /* Initialise class */
        super(pFactory, pSignatureSpec);

        /* Create the signature class */
        setSigner(getJavaSignature(getSignature(pSignatureSpec), false));
    }

    /**
     * Obtain Signer base.
     *
     * @param pSignatureSpec the signatureSpec
     * @return the base
     */
    private static String getSignature(final GordianSignatureSpec pSignatureSpec) {
        /* Handle DSTU explicitly */
        if (GordianKeyPairType.DSTU.equals(pSignatureSpec.getKeyPairType())) {
            return DSTU_SIGN;
        }

        /* Obtain the digest length */
        final GordianCoreSignatureSpec mySpec = (GordianCoreSignatureSpec) pSignatureSpec;
        final GordianLength myLength = mySpec.getDigestSpec().getDigestLength();

        /* Build the algorithm */
        return "GOST3411-2012-"
                + myLength.getLength()
                + "withECGOST3410-2012-"
                + myLength.getLength();
    }
}
