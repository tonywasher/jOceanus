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
import org.bouncycastle.pqc.crypto.hawk.HawkKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.hawk.HawkKeyPairGenerator;
import org.bouncycastle.pqc.crypto.hawk.HawkParameters;
import org.bouncycastle.pqc.crypto.hawk.HawkPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.hawk.HawkPublicKeyParameters;

import java.util.Arrays;

/**
 * Hawk KeyPair classes.
 */
public final class BouncyHawkKeyPair {
    /**
     * Private constructor.
     */
    private BouncyHawkKeyPair() {
    }

    /**
     * Bouncy Hawk PublicKey.
     */
    public static class BouncyHawkPublicKey
            extends BouncyPublicKey<HawkPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyHawkPublicKey(final GordianKeyPairSpec pKeySpec,
                            final HawkPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final HawkPublicKeyParameters myThis = getPublicKey();
            final HawkPublicKeyParameters myThat = (HawkPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy Hawk PrivateKey.
     */
    public static class BouncyHawkPrivateKey
            extends BouncyPrivateKey<HawkPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyHawkPrivateKey(final GordianKeyPairSpec pKeySpec,
                             final HawkPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final HawkPrivateKeyParameters myThis = getPrivateKey();
            final HawkPrivateKeyParameters myThat = (HawkPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle Hawk KeyPair generator.
     */
    public static class BouncyHawkKeyPairGenerator
            extends BouncyKeyPairGenerator<HawkPrivateKeyParameters, HawkPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyHawkKeyPairGenerator(final GordianBaseFactory pFactory,
                                   final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final HawkParameters myParms = myKeySpec.getHawkSpec().getParameters();
            final HawkKeyGenerationParameters myParams = new HawkKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new HawkKeyPairGenerator(), myParams);
            setFactorySet(BouncyPqKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyHawkPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyHawkPrivateKey(getKeySpec(), (HawkPrivateKeyParameters) pThat);
        }

        @Override
        BouncyHawkPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyHawkPublicKey(getKeySpec(), (HawkPublicKeyParameters) pThat);
        }
    }
}
