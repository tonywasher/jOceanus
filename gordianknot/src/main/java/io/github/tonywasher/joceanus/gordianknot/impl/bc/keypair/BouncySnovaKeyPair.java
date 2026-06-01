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
import org.bouncycastle.pqc.crypto.snova.SnovaKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaKeyPairGenerator;
import org.bouncycastle.pqc.crypto.snova.SnovaParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaPublicKeyParameters;

import java.util.Arrays;

/**
 * Snova KeyPair classes.
 */
public final class BouncySnovaKeyPair {
    /**
     * Private constructor.
     */
    private BouncySnovaKeyPair() {
    }

    /**
     * Bouncy Snova PublicKey.
     */
    public static class BouncySnovaPublicKey
            extends BouncyPublicKey<SnovaPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncySnovaPublicKey(final GordianKeyPairSpec pKeySpec,
                             final SnovaPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SnovaPublicKeyParameters myThis = getPublicKey();
            final SnovaPublicKeyParameters myThat = (SnovaPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy Snova PrivateKey.
     */
    public static class BouncySnovaPrivateKey
            extends BouncyPrivateKey<SnovaPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncySnovaPrivateKey(final GordianKeyPairSpec pKeySpec,
                              final SnovaPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SnovaPrivateKeyParameters myThis = getPrivateKey();
            final SnovaPrivateKeyParameters myThat = (SnovaPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle Snova KeyPair generator.
     */
    public static class BouncySnovaKeyPairGenerator
            extends BouncyKeyPairGenerator<SnovaPrivateKeyParameters, SnovaPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncySnovaKeyPairGenerator(final GordianBaseFactory pFactory,
                                    final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final SnovaParameters myParms = myKeySpec.getSnovaSpec().getParameters();
            final SnovaKeyGenerationParameters myParams = new SnovaKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new SnovaKeyPairGenerator(), myParams);
            setFactorySet(BouncyPqKeyFactorySet.INSTANCE);
        }

        @Override
        BouncySnovaPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncySnovaPrivateKey(getKeySpec(), (SnovaPrivateKeyParameters) pThat);
        }

        @Override
        BouncySnovaPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncySnovaPublicKey(getKeySpec(), (SnovaPublicKeyParameters) pThat);
        }
    }
}
