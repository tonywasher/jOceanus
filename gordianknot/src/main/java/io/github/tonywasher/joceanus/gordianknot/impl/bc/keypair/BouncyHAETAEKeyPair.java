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

package io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.haetae.HAETAEKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.haetae.HAETAEKeyPairGenerator;
import org.bouncycastle.pqc.crypto.haetae.HAETAEParameters;
import org.bouncycastle.pqc.crypto.haetae.HAETAEPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.haetae.HAETAEPublicKeyParameters;

import java.util.Arrays;

/**
 * HAETAE KeyPair classes.
 */
public final class BouncyHAETAEKeyPair {
    /**
     * Private constructor.
     */
    private BouncyHAETAEKeyPair() {
    }

    /**
     * Bouncy HAETAE PublicKey.
     */
    public static class BouncyHAETAEPublicKey
            extends BouncyPublicKey<HAETAEPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyHAETAEPublicKey(final GordianKeyPairSpec pKeySpec,
                              final HAETAEPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final HAETAEPublicKeyParameters myThis = getPublicKey();
            final HAETAEPublicKeyParameters myThat = (HAETAEPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy HAETAE PrivateKey.
     */
    public static class BouncyHAETAEPrivateKey
            extends BouncyPrivateKey<HAETAEPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyHAETAEPrivateKey(final GordianKeyPairSpec pKeySpec,
                               final HAETAEPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final HAETAEPrivateKeyParameters myThis = getPrivateKey();
            final HAETAEPrivateKeyParameters myThat = (HAETAEPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle HAETAE KeyPair generator.
     */
    public static class BouncyHAETAEKeyPairGenerator
            extends BouncyKeyPairGenerator<HAETAEPrivateKeyParameters, HAETAEPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyHAETAEKeyPairGenerator(final GordianBaseFactory pFactory,
                                     final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final HAETAEParameters myParms = myKeySpec.getHAETAESpec().getParameters();
            final HAETAEKeyGenerationParameters myParams = new HAETAEKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new HAETAEKeyPairGenerator(), myParams);
            setFactorySet(BouncyPqKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyHAETAEPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyHAETAEPrivateKey(getKeySpec(), (HAETAEPrivateKeyParameters) pThat);
        }

        @Override
        BouncyHAETAEPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyHAETAEPublicKey(getKeySpec(), (HAETAEPublicKeyParameters) pThat);
        }
    }
}
