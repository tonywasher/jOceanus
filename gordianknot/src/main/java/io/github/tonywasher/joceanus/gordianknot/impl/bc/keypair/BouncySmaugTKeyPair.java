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
import org.bouncycastle.pqc.crypto.smaugt.SmaugTKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.smaugt.SmaugTKeyPairGenerator;
import org.bouncycastle.pqc.crypto.smaugt.SmaugTParameters;
import org.bouncycastle.pqc.crypto.smaugt.SmaugTPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.smaugt.SmaugTPublicKeyParameters;

import java.util.Arrays;

/**
 * SmaugT KeyPair classes.
 */
public final class BouncySmaugTKeyPair {
    /**
     * Private constructor.
     */
    private BouncySmaugTKeyPair() {
    }

    /**
     * Bouncy SmaugT PublicKey.
     */
    public static class BouncySmaugTPublicKey
            extends BouncyPublicKey<SmaugTPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncySmaugTPublicKey(final GordianKeyPairSpec pKeySpec,
                              final SmaugTPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SmaugTPublicKeyParameters myThis = getPublicKey();
            final SmaugTPublicKeyParameters myThat = (SmaugTPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy SmaugT PrivateKey.
     */
    public static class BouncySmaugTPrivateKey
            extends BouncyPrivateKey<SmaugTPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncySmaugTPrivateKey(final GordianKeyPairSpec pKeySpec,
                               final SmaugTPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SmaugTPrivateKeyParameters myThis = getPrivateKey();
            final SmaugTPrivateKeyParameters myThat = (SmaugTPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle SmaugT KeyPair generator.
     */
    public static class BouncySmaugTKeyPairGenerator
            extends BouncyKeyPairGenerator<SmaugTPrivateKeyParameters, SmaugTPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncySmaugTKeyPairGenerator(final GordianBaseFactory pFactory,
                                     final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final SmaugTParameters myParms = myKeySpec.getSmaugTSpec().getParameters();
            final SmaugTKeyGenerationParameters myParams = new SmaugTKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new SmaugTKeyPairGenerator(), myParams);
            setFactorySet(BouncyPqKeyFactorySet.INSTANCE);
        }

        @Override
        BouncySmaugTPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncySmaugTPrivateKey(getKeySpec(), (SmaugTPrivateKeyParameters) pThat);
        }

        @Override
        BouncySmaugTPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncySmaugTPublicKey(getKeySpec(), (SmaugTPublicKeyParameters) pThat);
        }
    }
}
