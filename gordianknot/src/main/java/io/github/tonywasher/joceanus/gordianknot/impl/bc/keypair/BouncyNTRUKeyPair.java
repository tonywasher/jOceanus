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
import org.bouncycastle.pqc.crypto.ntru.NTRUKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUKeyPairGenerator;
import org.bouncycastle.pqc.crypto.ntru.NTRUParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUPublicKeyParameters;

import java.util.Arrays;

/**
 * NTRU KeyPair classes.
 */
public final class BouncyNTRUKeyPair {
    /**
     * Private constructor.
     */
    private BouncyNTRUKeyPair() {
    }

    /**
     * Bouncy NTRU PublicKey.
     */
    public static class BouncyNTRUPublicKey
            extends BouncyPublicKey<NTRUPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyNTRUPublicKey(final GordianKeyPairSpec pKeySpec,
                            final NTRUPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final NTRUPublicKeyParameters myThis = getPublicKey();
            final NTRUPublicKeyParameters myThat = (NTRUPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy NTRU PrivateKey.
     */
    public static class BouncyNTRUPrivateKey
            extends BouncyPrivateKey<NTRUPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyNTRUPrivateKey(final GordianKeyPairSpec pKeySpec,
                             final NTRUPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final NTRUPrivateKeyParameters myThis = getPrivateKey();
            final NTRUPrivateKeyParameters myThat = (NTRUPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle NTRU KeyPair generator.
     */
    public static class BouncyNTRUKeyPairGenerator
            extends BouncyKeyPairGenerator<NTRUPrivateKeyParameters, NTRUPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyNTRUKeyPairGenerator(final GordianBaseFactory pFactory,
                                   final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final NTRUParameters myParms = myKeySpec.getNTRUSpec().getParameters();
            final NTRUKeyGenerationParameters myParams = new NTRUKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new NTRUKeyPairGenerator(), myParams);
            setFactorySet(BouncyPqKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyNTRUPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyNTRUPrivateKey(getKeySpec(), (NTRUPrivateKeyParameters) pThat);
        }

        @Override
        BouncyNTRUPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyNTRUPublicKey(getKeySpec(), (NTRUPublicKeyParameters) pThat);
        }
    }
}
