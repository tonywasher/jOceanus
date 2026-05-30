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
import org.bouncycastle.pqc.crypto.mayo.MayoKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoKeyPairGenerator;
import org.bouncycastle.pqc.crypto.mayo.MayoParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoPublicKeyParameters;

import java.util.Arrays;

/**
 * Mayo KeyPair classes.
 */
public final class BouncyMayoKeyPair {
    /**
     * Private constructor.
     */
    private BouncyMayoKeyPair() {
    }

    /**
     * Bouncy Mayo PublicKey.
     */
    public static class BouncyMayoPublicKey
            extends BouncyPublicKey<MayoPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyMayoPublicKey(final GordianKeyPairSpec pKeySpec,
                            final MayoPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final MayoPublicKeyParameters myThis = getPublicKey();
            final MayoPublicKeyParameters myThat = (MayoPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy Mayo PrivateKey.
     */
    public static class BouncyMayoPrivateKey
            extends BouncyPrivateKey<MayoPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyMayoPrivateKey(final GordianKeyPairSpec pKeySpec,
                             final MayoPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final MayoPrivateKeyParameters myThis = getPrivateKey();
            final MayoPrivateKeyParameters myThat = (MayoPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle Mayo KeyPair generator.
     */
    public static class BouncyMayoKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyMayoKeyPairGenerator(final GordianBaseFactory pFactory,
                                   final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final MayoParameters myParms = myKeySpec.getMayoSpec().getParameters();
            final MayoKeyGenerationParameters myParams = new MayoKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new MayoKeyPairGenerator(), myParams);
            setFactorySet(BouncyPqKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyMayoPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyMayoPrivateKey(getKeySpec(), (MayoPrivateKeyParameters) pThat);
        }

        @Override
        BouncyMayoPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyMayoPublicKey(getKeySpec(), (MayoPublicKeyParameters) pThat);
        }
    }
}
