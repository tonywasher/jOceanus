/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
package io.github.tonywasher.joceanus.gordianknot.impl.jca;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.digest.GordianCoreDigestFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpec;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Jca Digest Factory.
 */
public class JcaDigestFactory
        extends GordianCoreDigestFactory {
    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    JcaDigestFactory(final GordianBaseFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);
    }

    @Override
    public JcaDigest createDigest(final GordianNewDigestSpec pDigestSpec) throws GordianException {
        /* Check validity of DigestSpec */
        checkDigestSpec(pDigestSpec);
        final GordianCoreDigestSpec mySpec = (GordianCoreDigestSpec) pDigestSpec;

        /* Create digest */
        final MessageDigest myJavaDigest = getJavaDigest(mySpec);
        return new JcaDigest(mySpec, myJavaDigest);
    }

    /**
     * Create the BouncyCastle digest via JCA.
     *
     * @param pDigestSpec the digestSpec
     * @return the digest
     * @throws GordianException on error
     */
    private static MessageDigest getJavaDigest(final GordianCoreDigestSpec pDigestSpec) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Return a digest for the algorithm */
            return MessageDigest.getInstance(JcaDigest.getFullAlgorithm(pDigestSpec), JcaProvider.BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create Digest", e);
        }
    }
}
