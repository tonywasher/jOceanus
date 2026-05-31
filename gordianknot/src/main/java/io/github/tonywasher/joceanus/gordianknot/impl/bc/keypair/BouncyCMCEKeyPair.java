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
import org.bouncycastle.pqc.crypto.cmce.CMCEKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.cmce.CMCEKeyPairGenerator;
import org.bouncycastle.pqc.crypto.cmce.CMCEParameters;
import org.bouncycastle.pqc.crypto.cmce.CMCEPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.cmce.CMCEPublicKeyParameters;

import java.util.Arrays;

/**
 * CMCE KeyPair classes.
 */
public final class BouncyCMCEKeyPair {
    /**
     * Private constructor.
     */
    private BouncyCMCEKeyPair() {
    }

    /**
     * Bouncy CMCE PublicKey.
     */
    public static class BouncyCMCEPublicKey
            extends BouncyPublicKey<CMCEPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyCMCEPublicKey(final GordianKeyPairSpec pKeySpec,
                            final CMCEPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final CMCEPublicKeyParameters myThis = getPublicKey();
            final CMCEPublicKeyParameters myThat = (CMCEPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy CMCE PrivateKey.
     */
    public static class BouncyCMCEPrivateKey
            extends BouncyPrivateKey<CMCEPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyCMCEPrivateKey(final GordianKeyPairSpec pKeySpec,
                             final CMCEPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final CMCEPrivateKeyParameters myThis = getPrivateKey();
            final CMCEPrivateKeyParameters myThat = (CMCEPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle CMCE KeyPair generator.
     */
    public static class BouncyCMCEKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyCMCEKeyPairGenerator(final GordianBaseFactory pFactory,
                                   final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final CMCEParameters myParms = myKeySpec.getCMCESpec().getParameters();
            final CMCEKeyGenerationParameters myParams = new CMCEKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new CMCEKeyPairGenerator(), myParams);
            setFactorySet(BouncyPqKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyCMCEPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyCMCEPrivateKey(getKeySpec(), (CMCEPrivateKeyParameters) pThat);
        }

        @Override
        BouncyCMCEPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyCMCEPublicKey(getKeySpec(), (CMCEPublicKeyParameters) pThat);
        }
    }
}
