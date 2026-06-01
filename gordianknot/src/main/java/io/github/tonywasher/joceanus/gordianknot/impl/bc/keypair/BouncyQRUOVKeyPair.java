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
import org.bouncycastle.pqc.crypto.qruov.QRUOVKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.qruov.QRUOVKeyPairGenerator;
import org.bouncycastle.pqc.crypto.qruov.QRUOVParameters;
import org.bouncycastle.pqc.crypto.qruov.QRUOVPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.qruov.QRUOVPublicKeyParameters;

import java.util.Arrays;

/**
 * QRUOV KeyPair classes.
 */
public final class BouncyQRUOVKeyPair {
    /**
     * Private constructor.
     */
    private BouncyQRUOVKeyPair() {
    }

    /**
     * Bouncy QRUOV PublicKey.
     */
    public static class BouncyQRUOVPublicKey
            extends BouncyPublicKey<QRUOVPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyQRUOVPublicKey(final GordianKeyPairSpec pKeySpec,
                             final QRUOVPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final QRUOVPublicKeyParameters myThis = getPublicKey();
            final QRUOVPublicKeyParameters myThat = (QRUOVPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy QRUOV PrivateKey.
     */
    public static class BouncyQRUOVPrivateKey
            extends BouncyPrivateKey<QRUOVPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyQRUOVPrivateKey(final GordianKeyPairSpec pKeySpec,
                              final QRUOVPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final QRUOVPrivateKeyParameters myThis = getPrivateKey();
            final QRUOVPrivateKeyParameters myThat = (QRUOVPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle QRUOV KeyPair generator.
     */
    public static class BouncyQRUOVKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyQRUOVKeyPairGenerator(final GordianBaseFactory pFactory,
                                    final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final QRUOVParameters myParms = myKeySpec.getQRUOVSpec().getParameters();
            final QRUOVKeyGenerationParameters myParams = new QRUOVKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new QRUOVKeyPairGenerator(), myParams);
            setFactorySet(BouncyPqKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyQRUOVPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyQRUOVPrivateKey(getKeySpec(), (QRUOVPrivateKeyParameters) pThat);
        }

        @Override
        BouncyQRUOVPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyQRUOVPublicKey(getKeySpec(), (QRUOVPublicKeyParameters) pThat);
        }
    }
}
