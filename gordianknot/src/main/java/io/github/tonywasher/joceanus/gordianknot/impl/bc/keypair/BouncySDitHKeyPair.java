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
import org.bouncycastle.pqc.crypto.sdith.SDitHKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.sdith.SDitHKeyPairGenerator;
import org.bouncycastle.pqc.crypto.sdith.SDitHParameters;
import org.bouncycastle.pqc.crypto.sdith.SDitHPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sdith.SDitHPublicKeyParameters;

import java.util.Arrays;

/**
 * SDitH KeyPair classes.
 */
public final class BouncySDitHKeyPair {
    /**
     * Private constructor.
     */
    private BouncySDitHKeyPair() {
    }

    /**
     * Bouncy SDitH PublicKey.
     */
    public static class BouncySDitHPublicKey
            extends BouncyPublicKey<SDitHPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncySDitHPublicKey(final GordianKeyPairSpec pKeySpec,
                             final SDitHPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SDitHPublicKeyParameters myThis = getPublicKey();
            final SDitHPublicKeyParameters myThat = (SDitHPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy SDitH PrivateKey.
     */
    public static class BouncySDitHPrivateKey
            extends BouncyPrivateKey<SDitHPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncySDitHPrivateKey(final GordianKeyPairSpec pKeySpec,
                              final SDitHPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SDitHPrivateKeyParameters myThis = getPrivateKey();
            final SDitHPrivateKeyParameters myThat = (SDitHPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle SDitH KeyPair generator.
     */
    public static class BouncySDitHKeyPairGenerator
            extends BouncyKeyPairGenerator<SDitHPrivateKeyParameters, SDitHPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncySDitHKeyPairGenerator(final GordianBaseFactory pFactory,
                                    final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final SDitHParameters myParms = myKeySpec.getSDitHSpec().getParameters();
            final SDitHKeyGenerationParameters myParams = new SDitHKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new SDitHKeyPairGenerator(), myParams);
            setFactorySet(BouncyPqKeyFactorySet.INSTANCE);
        }

        @Override
        BouncySDitHPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncySDitHPrivateKey(getKeySpec(), (SDitHPrivateKeyParameters) pThat);
        }

        @Override
        BouncySDitHPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncySDitHPublicKey(getKeySpec(), (SDitHPublicKeyParameters) pThat);
        }
    }
}
