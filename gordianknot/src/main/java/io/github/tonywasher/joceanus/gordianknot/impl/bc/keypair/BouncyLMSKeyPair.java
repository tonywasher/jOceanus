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
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyStateAwarePrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreLMSSpec;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.lms.HSSKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.lms.HSSKeyPairGenerator;
import org.bouncycastle.pqc.crypto.lms.HSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.HSSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSParameters;

import java.util.Arrays;

/**
 * LMS KeyPair classes.
 */
public final class BouncyLMSKeyPair {
    /**
     * Private constructor.
     */
    private BouncyLMSKeyPair() {
    }

    /**
     * Bouncy HSS PublicKey.
     */
    public static class BouncyHSSPublicKey
            extends BouncyPublicKey<HSSPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyHSSPublicKey(final GordianKeyPairSpec pKeySpec,
                           final HSSPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final HSSPublicKeyParameters myThis = getPublicKey();
            final HSSPublicKeyParameters myThat = (HSSPublicKeyParameters) pThat;

            /* Check equality */
            return myThis.equals(myThat);
        }
    }

    /**
     * Bouncy HSS PrivateKey.
     */
    public static class BouncyHSSPrivateKey
            extends BouncyStateAwarePrivateKey<HSSPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyHSSPrivateKey(final GordianKeyPairSpec pKeySpec,
                            final HSSPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        public long getUsagesRemaining() {
            return getPrivateKey().getUsagesRemaining();
        }

        @Override
        public BouncyHSSPrivateKey getKeyShard(final int pNumUsages) {
            return new BouncyHSSPrivateKey(getKeySpec(), getPrivateKey().extractKeyShard(pNumUsages));
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final HSSPrivateKeyParameters myThis = getPrivateKey();
            final HSSPrivateKeyParameters myThat = (HSSPrivateKeyParameters) pThat;

            /* Check equality */
            return myThis.equals(myThat);
        }
    }

    /**
     * BouncyCastle HSS KeyPair generator.
     */
    public static class BouncyHSSKeyPairGenerator
            extends BouncyKeyPairGenerator<HSSPrivateKeyParameters, HSSPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyHSSKeyPairGenerator(final GordianBaseFactory pFactory,
                                  final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final GordianCoreLMSSpec myHSSSpec = myKeySpec.getLMSSpec();
            final KeyGenerationParameters myParams = new HSSKeyGenerationParameters(deriveParameters(myHSSSpec), getRandom());

            /* Create and initialise the generator */
            setGenerator(new HSSKeyPairGenerator(), myParams);
            setFactorySet(BouncyPqKeyFactorySet.INSTANCE);
        }

        /**
         * Derive the parameters.
         *
         * @param pKeySpec the keySPec
         * @return the parameters.
         */
        private static LMSParameters[] deriveParameters(final GordianCoreLMSSpec pKeySpec) {
            final LMSParameters[] myParams = new LMSParameters[pKeySpec.getTreeDepth()];
            Arrays.fill(myParams, pKeySpec.getParameters());
            return myParams;
        }

        @Override
        BouncyHSSPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyHSSPrivateKey(getKeySpec(), (HSSPrivateKeyParameters) pThat);
        }

        @Override
        BouncyHSSPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyHSSPublicKey(getKeySpec(), (HSSPublicKeyParameters) pThat);
        }
    }
}
