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
import org.bouncycastle.pqc.legacy.bike.BIKEKeyGenerationParameters;
import org.bouncycastle.pqc.legacy.bike.BIKEKeyPairGenerator;
import org.bouncycastle.pqc.legacy.bike.BIKEParameters;
import org.bouncycastle.pqc.legacy.bike.BIKEPrivateKeyParameters;
import org.bouncycastle.pqc.legacy.bike.BIKEPublicKeyParameters;

import java.util.Arrays;

/**
 * BIKE KeyPair classes.
 */
public final class BouncyBIKEKeyPair {
    /**
     * Private constructor.
     */
    private BouncyBIKEKeyPair() {
    }

    /**
     * Bouncy BIKE PublicKey.
     */
    public static class BouncyBIKEPublicKey
            extends BouncyPublicKey<BIKEPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyBIKEPublicKey(final GordianKeyPairSpec pKeySpec,
                            final BIKEPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final BIKEPublicKeyParameters myThis = getPublicKey();
            final BIKEPublicKeyParameters myThat = (BIKEPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy BIKE PrivateKey.
     */
    public static class BouncyBIKEPrivateKey
            extends BouncyPrivateKey<BIKEPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyBIKEPrivateKey(final GordianKeyPairSpec pKeySpec,
                             final BIKEPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final BIKEPrivateKeyParameters myThis = getPrivateKey();
            final BIKEPrivateKeyParameters myThat = (BIKEPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle BIKE KeyPair generator.
     */
    public static class BouncyBIKEKeyPairGenerator
            extends BouncyKeyPairGenerator<BIKEPrivateKeyParameters, BIKEPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyBIKEKeyPairGenerator(final GordianBaseFactory pFactory,
                                   final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final BIKEParameters myParms = myKeySpec.getBIKESpec().getParameters();
            final BIKEKeyGenerationParameters myParams = new BIKEKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new BIKEKeyPairGenerator(), myParams);
            setFactorySet(BouncyPqKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyBIKEPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyBIKEPrivateKey(getKeySpec(), (BIKEPrivateKeyParameters) pThat);
        }

        @Override
        BouncyBIKEPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyBIKEPublicKey(getKeySpec(), (BIKEPublicKeyParameters) pThat);
        }
    }
}
