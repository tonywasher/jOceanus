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
package io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.falcon.FalconKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconKeyPairGenerator;
import org.bouncycastle.pqc.crypto.falcon.FalconParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPublicKeyParameters;

import java.util.Arrays;

/**
 * FALCON KeyPair classes.
 */
public final class BouncyFalconKeyPair {
    /**
     * Private constructor.
     */
    private BouncyFalconKeyPair() {
    }

    /**
     * Bouncy Falcon PublicKey.
     */
    public static class BouncyFalconPublicKey
            extends BouncyPublicKey<FalconPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyFalconPublicKey(final GordianKeyPairSpec pKeySpec,
                              final FalconPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final FalconPublicKeyParameters myThis = getPublicKey();
            final FalconPublicKeyParameters myThat = (FalconPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getH(), myThat.getH());
        }
    }

    /**
     * Bouncy Falcon PrivateKey.
     */
    public static class BouncyFalconPrivateKey
            extends BouncyPrivateKey<FalconPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyFalconPrivateKey(final GordianKeyPairSpec pKeySpec,
                               final FalconPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final FalconPrivateKeyParameters myThis = getPrivateKey();
            final FalconPrivateKeyParameters myThat = (FalconPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle Falcon KeyPair generator.
     */
    public static class BouncyFalconKeyPairGenerator
            extends BouncyKeyPairGenerator<FalconPrivateKeyParameters, FalconPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyFalconKeyPairGenerator(final GordianBaseFactory pFactory,
                                     final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final FalconParameters myParms = myKeySpec.getFalconSpec().getParameters();
            final FalconKeyGenerationParameters myParams = new FalconKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new FalconKeyPairGenerator(), myParams);
            setFactorySet(BouncyPqKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyFalconPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyFalconPrivateKey(getKeySpec(), (FalconPrivateKeyParameters) pThat);
        }

        @Override
        BouncyFalconPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyFalconPublicKey(getKeySpec(), (FalconPublicKeyParameters) pThat);
        }
    }
}
