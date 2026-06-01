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
import org.bouncycastle.pqc.crypto.frodo.FrodoKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoKeyPairGenerator;
import org.bouncycastle.pqc.crypto.frodo.FrodoParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoPublicKeyParameters;

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
            extends BouncyPublicKey<FrodoPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyFrodoPublicKey(final GordianKeyPairSpec pKeySpec,
                             final FrodoPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final FrodoPublicKeyParameters myThis = getPublicKey();
            final FrodoPublicKeyParameters myThat = (FrodoPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy Frodo PrivateKey.
     */
    public static class BouncyFrodoPrivateKey
            extends BouncyPrivateKey<FrodoPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyFrodoPrivateKey(final GordianKeyPairSpec pKeySpec,
                              final FrodoPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final FrodoPrivateKeyParameters myThis = getPrivateKey();
            final FrodoPrivateKeyParameters myThat = (FrodoPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle Frodo KeyPair generator.
     */
    public static class BouncyFrodoKeyPairGenerator
            extends BouncyKeyPairGenerator<FrodoPrivateKeyParameters, FrodoPublicKeyParameters> {
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
            final FrodoParameters myParms = myKeySpec.getFRODOSpec().getParameters();
            final FrodoKeyGenerationParameters myParams = new FrodoKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new FrodoKeyPairGenerator(), myParams);
            setFactorySet(BouncyPqKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyFrodoPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyFrodoPrivateKey(getKeySpec(), (FrodoPrivateKeyParameters) pThat);
        }

        @Override
        BouncyFrodoPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyFrodoPublicKey(getKeySpec(), (FrodoPublicKeyParameters) pThat);
        }
    }
}
