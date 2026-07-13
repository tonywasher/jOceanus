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
import org.bouncycastle.pqc.crypto.uov.UOVKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.uov.UOVKeyPairGenerator;
import org.bouncycastle.pqc.crypto.uov.UOVParameters;
import org.bouncycastle.pqc.crypto.uov.UOVPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.uov.UOVPublicKeyParameters;

import java.util.Arrays;

/**
 * UOV KeyPair classes.
 */
public final class BouncyUOVKeyPair {
    /**
     * Private constructor.
     */
    private BouncyUOVKeyPair() {
    }

    /**
     * Bouncy UOV PublicKey.
     */
    public static class BouncyUOVPublicKey
            extends BouncyPublicKey<UOVPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyUOVPublicKey(final GordianKeyPairSpec pKeySpec,
                           final UOVPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final UOVPublicKeyParameters myThis = getPublicKey();
            final UOVPublicKeyParameters myThat = (UOVPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy UOV PrivateKey.
     */
    public static class BouncyUOVPrivateKey
            extends BouncyPrivateKey<UOVPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyUOVPrivateKey(final GordianKeyPairSpec pKeySpec,
                            final UOVPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final UOVPrivateKeyParameters myThis = getPrivateKey();
            final UOVPrivateKeyParameters myThat = (UOVPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle UOV KeyPair generator.
     */
    public static class BouncyUOVKeyPairGenerator
            extends BouncyKeyPairGenerator<UOVPrivateKeyParameters, UOVPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyUOVKeyPairGenerator(final GordianBaseFactory pFactory,
                                  final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final UOVParameters myParms = myKeySpec.getUOVSpec().getParameters();
            final UOVKeyGenerationParameters myParams = new UOVKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new UOVKeyPairGenerator(), myParams);
            setFactorySet(BouncyPqKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyUOVPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyUOVPrivateKey(getKeySpec(), (UOVPrivateKeyParameters) pThat);
        }

        @Override
        BouncyUOVPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyUOVPublicKey(getKeySpec(), (UOVPublicKeyParameters) pThat);
        }
    }
}
