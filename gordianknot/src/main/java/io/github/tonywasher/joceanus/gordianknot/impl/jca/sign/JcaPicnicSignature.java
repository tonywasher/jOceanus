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
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.sign.GordianCoreSignatureSpec;

/**
 * Picnic signature.
 */
public class JcaPicnicSignature
        extends JcaSignature {
    /**
     * Signature base.
     */
    private static final String BASE_NAME = "PICNIC";

    /**
     * Constructor.
     *
     * @param pFactory       the factory
     * @param pSignatureSpec the signatureSpec
     * @throws GordianException on error
     */
    JcaPicnicSignature(final GordianBaseFactory pFactory,
                       final GordianSignatureSpec pSignatureSpec) throws GordianException {
        /* Initialise class */
        super(pFactory, pSignatureSpec);

        /* Create the signature class */
        final String myName = determineSignatureName(pSignatureSpec);
        setSigner(getJavaSignature(myName, true));
    }

    /**
     * Determine signatureName.
     *
     * @param pSignatureSpec the signatureSpec
     * @return the algorithm name
     */
    private static String determineSignatureName(final GordianSignatureSpec pSignatureSpec) {
        /* If we do not have a digest */
        if (pSignatureSpec.getSignatureSpec() == null) {
            return BASE_NAME;
        }

        /* Switch on digest Type */
        final GordianCoreSignatureSpec mySpec = (GordianCoreSignatureSpec) pSignatureSpec;
        return switch (mySpec.getDigestSpec().getDigestType()) {
            case SHA2 -> "SHA512With" + BASE_NAME;
            case SHA3 -> "SHA3-512With" + BASE_NAME;
            case SHAKE -> "SHAKE256With" + BASE_NAME;
            default -> throw new IllegalArgumentException("Bad SignatureSpec");
        };
    }
}
