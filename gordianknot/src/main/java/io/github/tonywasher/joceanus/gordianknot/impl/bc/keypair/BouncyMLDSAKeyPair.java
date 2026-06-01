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
import org.bouncycastle.crypto.generators.MLDSAKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.MLDSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.MLDSAParameters;
import org.bouncycastle.crypto.params.MLDSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.MLDSAPublicKeyParameters;

import java.util.Arrays;

/**
 * MLDSA KeyPair classes.
 */
public final class BouncyMLDSAKeyPair {
    /**
     * Private constructor.
     */
    private BouncyMLDSAKeyPair() {
    }

    /**
     * Bouncy MLDSA PublicKey.
     */
    public static class BouncyMLDSAPublicKey
            extends BouncyPublicKey<MLDSAPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyMLDSAPublicKey(final GordianKeyPairSpec pKeySpec,
                             final MLDSAPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final MLDSAPublicKeyParameters myThis = getPublicKey();
            final MLDSAPublicKeyParameters myThat = (MLDSAPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy MLDSA PrivateKey.
     */
    public static class BouncyMLDSAPrivateKey
            extends BouncyPrivateKey<MLDSAPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyMLDSAPrivateKey(final GordianKeyPairSpec pKeySpec,
                              final MLDSAPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final MLDSAPrivateKeyParameters myThis = getPrivateKey();
            final MLDSAPrivateKeyParameters myThat = (MLDSAPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle MLDSA KeyPair generator.
     */
    public static class BouncyMLDSAKeyPairGenerator
            extends BouncyKeyPairGenerator<MLDSAPrivateKeyParameters, MLDSAPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyMLDSAKeyPairGenerator(final GordianBaseFactory pFactory,
                                    final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final MLDSAParameters myParms = myKeySpec.getMLDSASpec().getParameters();
            final MLDSAKeyGenerationParameters myParams = new MLDSAKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new MLDSAKeyPairGenerator(), myParams);
            setFactorySet(BouncyStdKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyMLDSAPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyMLDSAPrivateKey(getKeySpec(), (MLDSAPrivateKeyParameters) pThat);
        }

        @Override
        BouncyMLDSAPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyMLDSAPublicKey(getKeySpec(), (MLDSAPublicKeyParameters) pThat);
        }
    }
}
