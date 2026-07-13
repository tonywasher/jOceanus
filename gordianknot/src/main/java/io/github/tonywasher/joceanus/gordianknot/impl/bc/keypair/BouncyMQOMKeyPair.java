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
import org.bouncycastle.pqc.crypto.mqom.MQOMKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mqom.MQOMKeyPairGenerator;
import org.bouncycastle.pqc.crypto.mqom.MQOMParameters;
import org.bouncycastle.pqc.crypto.mqom.MQOMPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mqom.MQOMPublicKeyParameters;

import java.util.Arrays;

/**
 * MQOM KeyPair classes.
 */
public final class BouncyMQOMKeyPair {
    /**
     * Private constructor.
     */
    private BouncyMQOMKeyPair() {
    }

    /**
     * Bouncy MQOM PublicKey.
     */
    public static class BouncyMQOMPublicKey
            extends BouncyPublicKey<MQOMPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyMQOMPublicKey(final GordianKeyPairSpec pKeySpec,
                            final MQOMPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final MQOMPublicKeyParameters myThis = getPublicKey();
            final MQOMPublicKeyParameters myThat = (MQOMPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy MQOM PrivateKey.
     */
    public static class BouncyMQOMPrivateKey
            extends BouncyPrivateKey<MQOMPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyMQOMPrivateKey(final GordianKeyPairSpec pKeySpec,
                             final MQOMPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final MQOMPrivateKeyParameters myThis = getPrivateKey();
            final MQOMPrivateKeyParameters myThat = (MQOMPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle MQOM KeyPair generator.
     */
    public static class BouncyMQOMKeyPairGenerator
            extends BouncyKeyPairGenerator<MQOMPrivateKeyParameters, MQOMPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyMQOMKeyPairGenerator(final GordianBaseFactory pFactory,
                                   final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final MQOMParameters myParms = myKeySpec.getMQOMSpec().getParameters();
            final MQOMKeyGenerationParameters myParams = new MQOMKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new MQOMKeyPairGenerator(), myParams);
            setFactorySet(BouncyPqKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyMQOMPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyMQOMPrivateKey(getKeySpec(), (MQOMPrivateKeyParameters) pThat);
        }

        @Override
        BouncyMQOMPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyMQOMPublicKey(getKeySpec(), (MQOMPublicKeyParameters) pThat);
        }
    }
}
