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
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimeKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimeKeyPairGenerator;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimeParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimePublicKeyParameters;

import java.util.Arrays;

/**
 * SNTRUPrime KeyPair classes.
 */
public final class BouncySNTRUPrimeKeyPair {
    /**
     * Private constructor.
     */
    private BouncySNTRUPrimeKeyPair() {
    }

    /**
     * Bouncy SNTRUPrime PublicKey.
     */
    public static class BouncySNTRUPrimePublicKey
            extends BouncyPublicKey<SNTRUPrimePublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncySNTRUPrimePublicKey(final GordianKeyPairSpec pKeySpec,
                                  final SNTRUPrimePublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SNTRUPrimePublicKeyParameters myThis = getPublicKey();
            final SNTRUPrimePublicKeyParameters myThat = (SNTRUPrimePublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy SNTRUPrime PrivateKey.
     */
    public static class BouncySNTRUPrimePrivateKey
            extends BouncyPrivateKey<SNTRUPrimePrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncySNTRUPrimePrivateKey(final GordianKeyPairSpec pKeySpec,
                                   final SNTRUPrimePrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SNTRUPrimePrivateKeyParameters myThis = getPrivateKey();
            final SNTRUPrimePrivateKeyParameters myThat = (SNTRUPrimePrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle SNTRUPrime KeyPair generator.
     */
    public static class BouncySNTRUPrimeKeyPairGenerator
            extends BouncyKeyPairGenerator<SNTRUPrimePrivateKeyParameters, SNTRUPrimePublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncySNTRUPrimeKeyPairGenerator(final GordianBaseFactory pFactory,
                                         final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final SNTRUPrimeParameters myParms = myKeySpec.getNTRUPrimeSpec().getCoreParams().getSNTRUParameters();
            final SNTRUPrimeKeyGenerationParameters myParams = new SNTRUPrimeKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new SNTRUPrimeKeyPairGenerator(), myParams);
            setFactorySet(BouncyPqKeyFactorySet.INSTANCE);
        }

        @Override
        BouncySNTRUPrimePrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncySNTRUPrimePrivateKey(getKeySpec(), (SNTRUPrimePrivateKeyParameters) pThat);
        }

        @Override
        BouncySNTRUPrimePublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncySNTRUPrimePublicKey(getKeySpec(), (SNTRUPrimePublicKeyParameters) pThat);
        }
    }
}
