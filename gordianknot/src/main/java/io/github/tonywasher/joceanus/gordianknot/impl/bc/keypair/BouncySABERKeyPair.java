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
import org.bouncycastle.pqc.crypto.saber.SABERKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.saber.SABERKeyPairGenerator;
import org.bouncycastle.pqc.crypto.saber.SABERParameters;
import org.bouncycastle.pqc.crypto.saber.SABERPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.saber.SABERPublicKeyParameters;

import java.util.Arrays;

/**
 * SABER KeyPair classes.
 */
public final class BouncySABERKeyPair {
    /**
     * Private constructor.
     */
    private BouncySABERKeyPair() {
    }

    /**
     * Bouncy SABER PublicKey.
     */
    public static class BouncySABERPublicKey
            extends BouncyPublicKey<SABERPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncySABERPublicKey(final GordianKeyPairSpec pKeySpec,
                             final SABERPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SABERPublicKeyParameters myThis = getPublicKey();
            final SABERPublicKeyParameters myThat = (SABERPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy SABER PrivateKey.
     */
    public static class BouncySABERPrivateKey
            extends BouncyPrivateKey<SABERPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncySABERPrivateKey(final GordianKeyPairSpec pKeySpec,
                              final SABERPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SABERPrivateKeyParameters myThis = getPrivateKey();
            final SABERPrivateKeyParameters myThat = (SABERPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle SABER KeyPair generator.
     */
    public static class BouncySABERKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncySABERKeyPairGenerator(final GordianBaseFactory pFactory,
                                    final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final SABERParameters myParms = myKeySpec.getSABERSpec().getParameters();
            final SABERKeyGenerationParameters myParams = new SABERKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new SABERKeyPairGenerator(), myParams);
            setFactorySet(BouncyPqKeyFactorySet.INSTANCE);
        }

        @Override
        BouncySABERPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncySABERPrivateKey(getKeySpec(), (SABERPrivateKeyParameters) pThat);
        }

        @Override
        BouncySABERPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncySABERPublicKey(getKeySpec(), (SABERPublicKeyParameters) pThat);
        }
    }
}
