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
import org.bouncycastle.crypto.generators.SLHDSAKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.SLHDSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.SLHDSAParameters;
import org.bouncycastle.crypto.params.SLHDSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.SLHDSAPublicKeyParameters;

import java.util.Arrays;

/**
 * SPHINCSPlus KeyPair classes.
 */
public final class BouncySLHDSAKeyPair {
    /**
     * Private constructor.
     */
    private BouncySLHDSAKeyPair() {
    }

    /**
     * Bouncy SLHDSA PublicKey.
     */
    public static class BouncySLHDSAPublicKey
            extends BouncyPublicKey<SLHDSAPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncySLHDSAPublicKey(final GordianKeyPairSpec pKeySpec,
                              final SLHDSAPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SLHDSAPublicKeyParameters myThis = getPublicKey();
            final SLHDSAPublicKeyParameters myThat = (SLHDSAPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy SLHDSA PrivateKey.
     */
    public static class BouncySLHDSAPrivateKey
            extends BouncyPrivateKey<SLHDSAPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncySLHDSAPrivateKey(final GordianKeyPairSpec pKeySpec,
                               final SLHDSAPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SLHDSAPrivateKeyParameters myThis = getPrivateKey();
            final SLHDSAPrivateKeyParameters myThat = (SLHDSAPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle SLHDSA KeyPair generator.
     */
    public static class BouncySLHDSAKeyPairGenerator
            extends BouncyKeyPairGenerator<SLHDSAPrivateKeyParameters, SLHDSAPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncySLHDSAKeyPairGenerator(final GordianBaseFactory pFactory,
                                     final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final SLHDSAParameters myParms = myKeySpec.getSLHDSASpec().getParameters();
            final SLHDSAKeyGenerationParameters myParams = new SLHDSAKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new SLHDSAKeyPairGenerator(), myParams);
            setFactorySet(BouncyStdKeyFactorySet.INSTANCE);
        }

        @Override
        BouncySLHDSAPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncySLHDSAPrivateKey(getKeySpec(), (SLHDSAPrivateKeyParameters) pThat);
        }

        @Override
        BouncySLHDSAPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncySLHDSAPublicKey(getKeySpec(), (SLHDSAPublicKeyParameters) pThat);
        }
    }
}
