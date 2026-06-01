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
import org.bouncycastle.crypto.generators.MLKEMKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.MLKEMKeyGenerationParameters;
import org.bouncycastle.crypto.params.MLKEMParameters;
import org.bouncycastle.crypto.params.MLKEMPrivateKeyParameters;
import org.bouncycastle.crypto.params.MLKEMPublicKeyParameters;

import java.util.Arrays;

/**
 * MLKEM KeyPair classes.
 */
public final class BouncyMLKEMKeyPair {
    /**
     * Private constructor.
     */
    private BouncyMLKEMKeyPair() {
    }

    /**
     * Bouncy KYBER PublicKey.
     */
    public static class BouncyMLKEMPublicKey
            extends BouncyPublicKey<MLKEMPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyMLKEMPublicKey(final GordianKeyPairSpec pKeySpec,
                             final MLKEMPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final MLKEMPublicKeyParameters myThis = getPublicKey();
            final MLKEMPublicKeyParameters myThat = (MLKEMPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy MLKEM PrivateKey.
     */
    public static class BouncyMLKEMPrivateKey
            extends BouncyPrivateKey<MLKEMPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyMLKEMPrivateKey(final GordianKeyPairSpec pKeySpec,
                              final MLKEMPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final MLKEMPrivateKeyParameters myThis = getPrivateKey();
            final MLKEMPrivateKeyParameters myThat = (MLKEMPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle MLKEM KeyPair generator.
     */
    public static class BouncyMLKEMKeyPairGenerator
            extends BouncyKeyPairGenerator<MLKEMPrivateKeyParameters, MLKEMPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyMLKEMKeyPairGenerator(final GordianBaseFactory pFactory,
                                    final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final MLKEMParameters myParms = myKeySpec.getMLKEMSpec().getParameters();
            final MLKEMKeyGenerationParameters myParams = new MLKEMKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new MLKEMKeyPairGenerator(), myParams);
            setFactorySet(BouncyStdKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyMLKEMPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyMLKEMPrivateKey(getKeySpec(), (MLKEMPrivateKeyParameters) pThat);
        }

        @Override
        BouncyMLKEMPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyMLKEMPublicKey(getKeySpec(), (MLKEMPublicKeyParameters) pThat);
        }
    }
}
