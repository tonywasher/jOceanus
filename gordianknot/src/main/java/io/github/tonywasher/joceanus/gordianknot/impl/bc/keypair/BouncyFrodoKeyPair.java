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
import org.bouncycastle.crypto.generators.FrodoKEMKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.FrodoKEMKeyGenerationParameters;
import org.bouncycastle.crypto.params.FrodoKEMParameters;
import org.bouncycastle.crypto.params.FrodoKEMPrivateKeyParameters;
import org.bouncycastle.crypto.params.FrodoKEMPublicKeyParameters;

import java.util.Arrays;

/**
 * Frodo KeyPair classes.
 */
public final class BouncyFrodoKeyPair {
    /**
     * Private constructor.
     */
    private BouncyFrodoKeyPair() {
    }

    /**
     * Bouncy Frodo PublicKey.
     */
    public static class BouncyFrodoPublicKey
            extends BouncyPublicKey<FrodoKEMPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyFrodoPublicKey(final GordianKeyPairSpec pKeySpec,
                             final FrodoKEMPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final FrodoKEMPublicKeyParameters myThis = getPublicKey();
            final FrodoKEMPublicKeyParameters myThat = (FrodoKEMPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy Frodo PrivateKey.
     */
    public static class BouncyFrodoPrivateKey
            extends BouncyPrivateKey<FrodoKEMPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyFrodoPrivateKey(final GordianKeyPairSpec pKeySpec,
                              final FrodoKEMPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final FrodoKEMPrivateKeyParameters myThis = getPrivateKey();
            final FrodoKEMPrivateKeyParameters myThat = (FrodoKEMPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle Frodo KeyPair generator.
     */
    public static class BouncyFrodoKeyPairGenerator
            extends BouncyKeyPairGenerator<FrodoKEMPrivateKeyParameters, FrodoKEMPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyFrodoKeyPairGenerator(final GordianBaseFactory pFactory,
                                    final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final FrodoKEMParameters myParms = myKeySpec.getFRODOSpec().getParameters();
            final FrodoKEMKeyGenerationParameters myParams = new FrodoKEMKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new FrodoKEMKeyPairGenerator(), myParams);
            setFactorySet(BouncyStdKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyFrodoPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyFrodoPrivateKey(getKeySpec(), (FrodoKEMPrivateKeyParameters) pThat);
        }

        @Override
        BouncyFrodoPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyFrodoPublicKey(getKeySpec(), (FrodoKEMPublicKeyParameters) pThat);
        }
    }
}
