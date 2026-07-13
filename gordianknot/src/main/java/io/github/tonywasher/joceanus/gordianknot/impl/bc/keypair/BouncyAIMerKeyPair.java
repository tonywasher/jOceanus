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
import org.bouncycastle.pqc.crypto.aimer.AIMerKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.aimer.AIMerKeyPairGenerator;
import org.bouncycastle.pqc.crypto.aimer.AIMerParameters;
import org.bouncycastle.pqc.crypto.aimer.AIMerPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.aimer.AIMerPublicKeyParameters;

import java.util.Arrays;

/**
 * AIMer KeyPair classes.
 */
public final class BouncyAIMerKeyPair {
    /**
     * Private constructor.
     */
    private BouncyAIMerKeyPair() {
    }

    /**
     * Bouncy AIMer PublicKey.
     */
    public static class BouncyAIMerPublicKey
            extends BouncyPublicKey<AIMerPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyAIMerPublicKey(final GordianKeyPairSpec pKeySpec,
                             final AIMerPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final AIMerPublicKeyParameters myThis = getPublicKey();
            final AIMerPublicKeyParameters myThat = (AIMerPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy AIMer PrivateKey.
     */
    public static class BouncyAIMerPrivateKey
            extends BouncyPrivateKey<AIMerPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyAIMerPrivateKey(final GordianKeyPairSpec pKeySpec,
                              final AIMerPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final AIMerPrivateKeyParameters myThis = getPrivateKey();
            final AIMerPrivateKeyParameters myThat = (AIMerPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle AIMer KeyPair generator.
     */
    public static class BouncyAIMerKeyPairGenerator
            extends BouncyKeyPairGenerator<AIMerPrivateKeyParameters, AIMerPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyAIMerKeyPairGenerator(final GordianBaseFactory pFactory,
                                    final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final AIMerParameters myParms = myKeySpec.getAIMerSpec().getParameters();
            final AIMerKeyGenerationParameters myParams = new AIMerKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new AIMerKeyPairGenerator(), myParams);
            setFactorySet(BouncyPqKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyAIMerPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyAIMerPrivateKey(getKeySpec(), (AIMerPrivateKeyParameters) pThat);
        }

        @Override
        BouncyAIMerPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyAIMerPublicKey(getKeySpec(), (AIMerPublicKeyParameters) pThat);
        }
    }
}
