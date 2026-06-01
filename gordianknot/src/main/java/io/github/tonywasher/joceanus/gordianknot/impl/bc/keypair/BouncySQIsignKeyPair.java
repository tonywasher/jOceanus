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
import org.bouncycastle.pqc.crypto.sqisign.SQIsignKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.sqisign.SQIsignKeyPairGenerator;
import org.bouncycastle.pqc.crypto.sqisign.SQIsignParameters;
import org.bouncycastle.pqc.crypto.sqisign.SQIsignPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sqisign.SQIsignPublicKeyParameters;

import java.util.Arrays;

/**
 * SQIsign KeyPair classes.
 */
public final class BouncySQIsignKeyPair {
    /**
     * Private constructor.
     */
    private BouncySQIsignKeyPair() {
    }

    /**
     * Bouncy SQIsign PublicKey.
     */
    public static class BouncySQIsignPublicKey
            extends BouncyPublicKey<SQIsignPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncySQIsignPublicKey(final GordianKeyPairSpec pKeySpec,
                               final SQIsignPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SQIsignPublicKeyParameters myThis = getPublicKey();
            final SQIsignPublicKeyParameters myThat = (SQIsignPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy SQIsign PrivateKey.
     */
    public static class BouncySQIsignPrivateKey
            extends BouncyPrivateKey<SQIsignPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncySQIsignPrivateKey(final GordianKeyPairSpec pKeySpec,
                                final SQIsignPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SQIsignPrivateKeyParameters myThis = getPrivateKey();
            final SQIsignPrivateKeyParameters myThat = (SQIsignPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle SQIsign KeyPair generator.
     */
    public static class BouncySQIsignKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncySQIsignKeyPairGenerator(final GordianBaseFactory pFactory,
                                      final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final SQIsignParameters myParms = myKeySpec.getSQIsignSpec().getParameters();
            final SQIsignKeyGenerationParameters myParams = new SQIsignKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new SQIsignKeyPairGenerator(), myParams);
            setFactorySet(BouncyPqKeyFactorySet.INSTANCE);
        }

        @Override
        BouncySQIsignPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncySQIsignPrivateKey(getKeySpec(), (SQIsignPrivateKeyParameters) pThat);
        }

        @Override
        BouncySQIsignPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncySQIsignPublicKey(getKeySpec(), (SQIsignPublicKeyParameters) pThat);
        }
    }
}
