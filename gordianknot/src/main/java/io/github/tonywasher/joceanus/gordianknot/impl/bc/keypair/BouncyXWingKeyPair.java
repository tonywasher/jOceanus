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

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.xwing.XWingKeyPairGenerator;
import org.bouncycastle.pqc.crypto.xwing.XWingPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xwing.XWingPublicKeyParameters;

import java.util.Arrays;

/**
 * XWing KeyPair classes.
 */

public class BouncyXWingKeyPair {
    /**
     * Private constructor.
     */
    private BouncyXWingKeyPair() {
    }

    /**
     * Bouncy XWing PublicKey.
     */
    public static class BouncyXWingPublicKey
            extends BouncyPublicKey<XWingPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        public BouncyXWingPublicKey(final GordianKeyPairSpec pKeySpec,
                                    final XWingPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final XWingPublicKeyParameters myThis = getPublicKey();
            final XWingPublicKeyParameters myThat = (XWingPublicKeyParameters) pThat;

            /* Check equality */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy XWing PrivateKey.
     */
    public static class BouncyXWingPrivateKey
            extends BouncyPrivateKey<XWingPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyXWingPrivateKey(final GordianKeyPairSpec pKeySpec,
                              final XWingPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final XWingPrivateKeyParameters myThis = getPrivateKey();
            final XWingPrivateKeyParameters myThat = (XWingPrivateKeyParameters) pThat;

            /* Check equality */
            return Arrays.equals(myThis.getSeed(), myThat.getSeed());
        }
    }

    /**
     * BouncyCastle XWing KeyPair generator.
     */
    public static class BouncyXWingKeyPairGenerator
            extends BouncyKeyPairGenerator<XWingPrivateKeyParameters, XWingPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyXWingKeyPairGenerator(final GordianBaseFactory pFactory,
                                    final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            final KeyGenerationParameters myParams = new KeyGenerationParameters(getRandom(), GordianLength.LEN_1024.getLength());
            setGenerator(new XWingKeyPairGenerator(), myParams);
            setFactorySet(BouncyStdKeyFactorySet.INSTANCE);
        }


        @Override
        BouncyXWingPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyXWingPrivateKey(getKeySpec(), (XWingPrivateKeyParameters) pThat);
        }

        @Override
        BouncyXWingPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyXWingPublicKey(getKeySpec(), (XWingPublicKeyParameters) pThat);
        }
    }
}
