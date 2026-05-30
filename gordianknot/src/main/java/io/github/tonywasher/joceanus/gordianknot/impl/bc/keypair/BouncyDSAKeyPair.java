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
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreDSASpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.DSAKeyPairGenerator;
import org.bouncycastle.crypto.generators.DSAParametersGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.DSAParameterGenerationParameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;

/**
 * DSA KeyPair classes.
 */
public final class BouncyDSAKeyPair {
    /**
     * Private constructor.
     */
    private BouncyDSAKeyPair() {
    }

    /**
     * Bouncy DSA PublicKey.
     */
    public static class BouncyDSAPublicKey
            extends BouncyPublicKey<DSAPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyDSAPublicKey(final GordianKeyPairSpec pKeySpec,
                           final DSAPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final DSAPublicKeyParameters myThis = getPublicKey();
            final DSAPublicKeyParameters myThat = (DSAPublicKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
        }

        /**
         * CompareKeys.
         *
         * @param pFirst  the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final DSAPublicKeyParameters pFirst,
                                           final DSAPublicKeyParameters pSecond) {
            return pFirst.getY().equals(pSecond.getY())
                    && pFirst.getParameters().equals(pSecond.getParameters());
        }
    }

    /**
     * Bouncy DSA PrivateKey.
     */
    public static class BouncyDSAPrivateKey
            extends BouncyPrivateKey<DSAPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyDSAPrivateKey(final GordianKeyPairSpec pKeySpec,
                            final DSAPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final DSAPrivateKeyParameters myThis = getPrivateKey();
            final DSAPrivateKeyParameters myThat = (DSAPrivateKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
        }

        /**
         * CompareKeys.
         *
         * @param pFirst  the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final DSAPrivateKeyParameters pFirst,
                                           final DSAPrivateKeyParameters pSecond) {
            return pFirst.getX().equals(pSecond.getX())
                    && pFirst.getParameters().equals(pSecond.getParameters());
        }
    }

    /**
     * BouncyCastle DSA KeyPair generator.
     */
    public static class BouncyDSAKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyDSAKeyPairGenerator(final GordianBaseFactory pFactory,
                                  final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create the parameter generator */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final GordianCoreDSASpec myKeyType = myKeySpec.getDSASpec();
            final DSAParameterGenerationParameters myGenParms = new DSAParameterGenerationParameters(myKeyType.getKeySize(),
                    myKeyType.getHashSize(), PRIME_CERTAINTY, getRandom());
            final DSAParametersGenerator myParmGenerator = new DSAParametersGenerator(new SHA256Digest());
            myParmGenerator.init(myGenParms);
            final DSAKeyGenerationParameters myParams = new DSAKeyGenerationParameters(getRandom(), myParmGenerator.generateParameters());

            /* Create and initialise the generator */
            setGenerator(new DSAKeyPairGenerator(), myParams);
            setFactorySet(BouncyStdKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyDSAPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyDSAPrivateKey(getKeySpec(), (DSAPrivateKeyParameters) pThat);
        }

        @Override
        BouncyDSAPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyDSAPublicKey(getKeySpec(), (DSAPublicKeyParameters) pThat);
        }
    }
}
