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
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeKeyPairGenerator;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimePublicKeyParameters;

import java.util.Arrays;

/**
 * NTRULPrime KeyPair classes.
 */
public final class BouncyNTRULPrimeKeyPair {
    /**
     * Private constructor.
     */
    private BouncyNTRULPrimeKeyPair() {
    }

    /**
     * Bouncy NTRULPrime PublicKey.
     */
    public static class BouncyNTRULPrimePublicKey
            extends BouncyPublicKey<NTRULPRimePublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyNTRULPrimePublicKey(final GordianKeyPairSpec pKeySpec,
                                  final NTRULPRimePublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final NTRULPRimePublicKeyParameters myThis = getPublicKey();
            final NTRULPRimePublicKeyParameters myThat = (NTRULPRimePublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy NTRULPrime PrivateKey.
     */
    public static class BouncyNTRULPrimePrivateKey
            extends BouncyPrivateKey<NTRULPRimePrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyNTRULPrimePrivateKey(final GordianKeyPairSpec pKeySpec,
                                   final NTRULPRimePrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final NTRULPRimePrivateKeyParameters myThis = getPrivateKey();
            final NTRULPRimePrivateKeyParameters myThat = (NTRULPRimePrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle NTRULPrime KeyPair generator.
     */
    public static class BouncyNTRULPrimeKeyPairGenerator
            extends BouncyKeyPairGenerator<NTRULPRimePrivateKeyParameters, NTRULPRimePublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyNTRULPrimeKeyPairGenerator(final GordianBaseFactory pFactory,
                                         final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final NTRULPRimeParameters myParms = myKeySpec.getNTRUPrimeSpec().getCoreParams().getNTRULParameters();
            final NTRULPRimeKeyGenerationParameters myParams = new NTRULPRimeKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new NTRULPRimeKeyPairGenerator(), myParams);
            setFactorySet(BouncyPqKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyNTRULPrimePrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyNTRULPrimePrivateKey(getKeySpec(), (NTRULPRimePrivateKeyParameters) pThat);
        }

        @Override
        BouncyNTRULPrimePublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyNTRULPrimePublicKey(getKeySpec(), (NTRULPRimePublicKeyParameters) pThat);
        }
    }
}
