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
import org.bouncycastle.pqc.crypto.ntruplus.NTRUPlusKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.ntruplus.NTRUPlusKeyPairGenerator;
import org.bouncycastle.pqc.crypto.ntruplus.NTRUPlusParameters;
import org.bouncycastle.pqc.crypto.ntruplus.NTRUPlusPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntruplus.NTRUPlusPublicKeyParameters;

import java.util.Arrays;


/**
 * NTRUPlus KeyPair classes.
 */
public final class BouncyNTRUPlusKeyPair {
    /**
     * Private constructor.
     */
    private BouncyNTRUPlusKeyPair() {
    }

    /**
     * Bouncy NTRUPlus PublicKey.
     */
    public static class BouncyNTRUPlusPublicKey
            extends BouncyPublicKey<NTRUPlusPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyNTRUPlusPublicKey(final GordianKeyPairSpec pKeySpec,
                                final NTRUPlusPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final NTRUPlusPublicKeyParameters myThis = getPublicKey();
            final NTRUPlusPublicKeyParameters myThat = (NTRUPlusPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy NTRUPlus PrivateKey.
     */
    public static class BouncyNTRUPlusPrivateKey
            extends BouncyPrivateKey<NTRUPlusPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyNTRUPlusPrivateKey(final GordianKeyPairSpec pKeySpec,
                                 final NTRUPlusPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final NTRUPlusPrivateKeyParameters myThis = getPrivateKey();
            final NTRUPlusPrivateKeyParameters myThat = (NTRUPlusPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle NTRUPlus KeyPair generator.
     */
    public static class BouncyNTRUPlusKeyPairGenerator
            extends BouncyKeyPairGenerator<NTRUPlusPrivateKeyParameters, NTRUPlusPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyNTRUPlusKeyPairGenerator(final GordianBaseFactory pFactory,
                                       final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final NTRUPlusParameters myParms = myKeySpec.getNTRUPlusSpec().getParameters();
            final NTRUPlusKeyGenerationParameters myParams = new NTRUPlusKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new NTRUPlusKeyPairGenerator(), myParams);
            setFactorySet(BouncyPqKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyNTRUPlusPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyNTRUPlusPrivateKey(getKeySpec(), (NTRUPlusPrivateKeyParameters) pThat);
        }

        @Override
        BouncyNTRUPlusPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyNTRUPlusPublicKey(getKeySpec(), (NTRUPlusPublicKeyParameters) pThat);
        }
    }
}
