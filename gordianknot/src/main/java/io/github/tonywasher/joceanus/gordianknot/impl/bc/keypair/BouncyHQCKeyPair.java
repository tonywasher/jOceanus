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
import org.bouncycastle.pqc.crypto.hqc.HQCKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCKeyPairGenerator;
import org.bouncycastle.pqc.crypto.hqc.HQCParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCPublicKeyParameters;

import java.util.Arrays;

/**
 * BIKE KeyPair classes.
 */
public final class BouncyHQCKeyPair {
    /**
     * Private constructor.
     */
    private BouncyHQCKeyPair() {
    }

    /**
     * Bouncy HQC PublicKey.
     */
    public static class BouncyHQCPublicKey
            extends BouncyPublicKey<HQCPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyHQCPublicKey(final GordianKeyPairSpec pKeySpec,
                           final HQCPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final HQCPublicKeyParameters myThis = getPublicKey();
            final HQCPublicKeyParameters myThat = (HQCPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy HQC PrivateKey.
     */
    public static class BouncyHQCPrivateKey
            extends BouncyPrivateKey<HQCPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyHQCPrivateKey(final GordianKeyPairSpec pKeySpec,
                            final HQCPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final HQCPrivateKeyParameters myThis = getPrivateKey();
            final HQCPrivateKeyParameters myThat = (HQCPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle HQC KeyPair generator.
     */
    public static class BouncyHQCKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyHQCKeyPairGenerator(final GordianBaseFactory pFactory,
                                  final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final HQCParameters myParms = myKeySpec.getHQCSpec().getParameters();
            final HQCKeyGenerationParameters myParams = new HQCKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new HQCKeyPairGenerator(), myParams);
            setFactorySet(BouncyPqKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyHQCPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyHQCPrivateKey(getKeySpec(), (HQCPrivateKeyParameters) pThat);
        }

        @Override
        BouncyHQCPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyHQCPublicKey(getKeySpec(), (HQCPublicKeyParameters) pThat);
        }
    }
}
