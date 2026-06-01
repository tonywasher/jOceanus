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
import org.bouncycastle.pqc.crypto.faest.FaestKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.faest.FaestKeyPairGenerator;
import org.bouncycastle.pqc.crypto.faest.FaestParameters;
import org.bouncycastle.pqc.crypto.faest.FaestPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.faest.FaestPublicKeyParameters;

import java.util.Arrays;

/**
 * Faest KeyPair classes.
 */
public final class BouncyFaestKeyPair {
    /**
     * Private constructor.
     */
    private BouncyFaestKeyPair() {
    }

    /**
     * Bouncy Faest PublicKey.
     */
    public static class BouncyFaestPublicKey
            extends BouncyPublicKey<FaestPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyFaestPublicKey(final GordianKeyPairSpec pKeySpec,
                             final FaestPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final FaestPublicKeyParameters myThis = getPublicKey();
            final FaestPublicKeyParameters myThat = (FaestPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy Faest PrivateKey.
     */
    public static class BouncyFaestPrivateKey
            extends BouncyPrivateKey<FaestPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyFaestPrivateKey(final GordianKeyPairSpec pKeySpec,
                              final FaestPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final FaestPrivateKeyParameters myThis = getPrivateKey();
            final FaestPrivateKeyParameters myThat = (FaestPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle Faest KeyPair generator.
     */
    public static class BouncyFaestKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyFaestKeyPairGenerator(final GordianBaseFactory pFactory,
                                    final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final FaestParameters myParms = myKeySpec.getFaestSpec().getParameters();
            final FaestKeyGenerationParameters myParams = new FaestKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new FaestKeyPairGenerator(), myParams);
            setFactorySet(BouncyPqKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyFaestPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyFaestPrivateKey(getKeySpec(), (FaestPrivateKeyParameters) pThat);
        }

        @Override
        BouncyFaestPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyFaestPublicKey(getKeySpec(), (FaestPublicKeyParameters) pThat);
        }
    }
}
