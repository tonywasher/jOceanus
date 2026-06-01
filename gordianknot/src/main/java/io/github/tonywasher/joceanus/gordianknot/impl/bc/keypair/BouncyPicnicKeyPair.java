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
import org.bouncycastle.pqc.legacy.picnic.PicnicKeyGenerationParameters;
import org.bouncycastle.pqc.legacy.picnic.PicnicKeyPairGenerator;
import org.bouncycastle.pqc.legacy.picnic.PicnicParameters;
import org.bouncycastle.pqc.legacy.picnic.PicnicPrivateKeyParameters;
import org.bouncycastle.pqc.legacy.picnic.PicnicPublicKeyParameters;

import java.util.Arrays;

/**
 * PICNIC KeyPair classes.
 */
public final class BouncyPicnicKeyPair {
    /**
     * Private constructor.
     */
    private BouncyPicnicKeyPair() {
    }

    /**
     * Bouncy Picnic PublicKey.
     */
    public static class BouncyPicnicPublicKey
            extends BouncyPublicKey<PicnicPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyPicnicPublicKey(final GordianKeyPairSpec pKeySpec,
                              final PicnicPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final PicnicPublicKeyParameters myThis = getPublicKey();
            final PicnicPublicKeyParameters myThat = (PicnicPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy Picnic PrivateKey.
     */
    public static class BouncyPicnicPrivateKey
            extends BouncyPrivateKey<PicnicPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyPicnicPrivateKey(final GordianKeyPairSpec pKeySpec,
                               final PicnicPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final PicnicPrivateKeyParameters myThis = getPrivateKey();
            final PicnicPrivateKeyParameters myThat = (PicnicPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle Picnic KeyPair generator.
     */
    public static class BouncyPicnicKeyPairGenerator
            extends BouncyKeyPairGenerator<PicnicPrivateKeyParameters, PicnicPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyPicnicKeyPairGenerator(final GordianBaseFactory pFactory,
                                     final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final PicnicParameters myParms = myKeySpec.getPicnicSpec().getParameters();
            final PicnicKeyGenerationParameters myParams = new PicnicKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new PicnicKeyPairGenerator(), myParams);
            setFactorySet(BouncyPqKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyPicnicPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyPicnicPrivateKey(getKeySpec(), (PicnicPrivateKeyParameters) pThat);
        }

        @Override
        BouncyPicnicPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyPicnicPublicKey(getKeySpec(), (PicnicPublicKeyParameters) pThat);
        }
    }
}
