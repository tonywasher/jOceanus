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
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.generators.Ed448KeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.Ed448KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed448PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed448PublicKeyParameters;

import java.util.Arrays;

/**
 * EdwardsCurve DSA KeyPair classes.
 */
public final class BouncyEdDSAKeyPair {
    /**
     * Private constructor.
     */
    private BouncyEdDSAKeyPair() {
    }

    /**
     * Bouncy EdwardsDSA25519 PublicKey.
     */
    public static class BouncyEd25519PublicKey
            extends BouncyPublicKey<Ed25519PublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyEd25519PublicKey(final GordianKeyPairSpec pKeySpec,
                               final Ed25519PublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final Ed25519PublicKeyParameters myThis = getPublicKey();
            final Ed25519PublicKeyParameters myThat = (Ed25519PublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy EdwardsDSA25519 PrivateKey.
     */
    public static class BouncyEd25519PrivateKey
            extends BouncyPrivateKey<Ed25519PrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyEd25519PrivateKey(final GordianKeyPairSpec pKeySpec,
                                final Ed25519PrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final Ed25519PrivateKeyParameters myThis = getPrivateKey();
            final Ed25519PrivateKeyParameters myThat = (Ed25519PrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy EdwardsDSA448 PublicKey.
     */
    public static class BouncyEd448PublicKey
            extends BouncyPublicKey<Ed448PublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyEd448PublicKey(final GordianKeyPairSpec pKeySpec,
                             final Ed448PublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final Ed448PublicKeyParameters myThis = getPublicKey();
            final Ed448PublicKeyParameters myThat = (Ed448PublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy EdwardsDSA448 PrivateKey.
     */
    public static class BouncyEd448PrivateKey
            extends BouncyPrivateKey<Ed448PrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyEd448PrivateKey(final GordianKeyPairSpec pKeySpec,
                              final Ed448PrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final Ed448PrivateKeyParameters myThis = getPrivateKey();
            final Ed448PrivateKeyParameters myThat = (Ed448PrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle EdwardsDSA25519 KeyPair generator.
     */
    public static class BouncyEd25519KeyPairGenerator
            extends BouncyKeyPairGenerator<Ed25519PrivateKeyParameters, Ed25519PublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyEd25519KeyPairGenerator(final GordianBaseFactory pFactory,
                                      final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            final Ed25519KeyGenerationParameters myParams = new Ed25519KeyGenerationParameters(getRandom());
            setGenerator(new Ed25519KeyPairGenerator(), myParams);
            setFactorySet(BouncyStdKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyEd25519PrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyEd25519PrivateKey(getKeySpec(), (Ed25519PrivateKeyParameters) pThat);
        }

        @Override
        BouncyEd25519PublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyEd25519PublicKey(getKeySpec(), (Ed25519PublicKeyParameters) pThat);
        }
    }

    /**
     * BouncyCastle EdwardsDSA448 KeyPair generator.
     */
    public static class BouncyEd448KeyPairGenerator
            extends BouncyKeyPairGenerator<Ed448PrivateKeyParameters, Ed448PublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyEd448KeyPairGenerator(final GordianBaseFactory pFactory,
                                    final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            final Ed448KeyGenerationParameters myParams = new Ed448KeyGenerationParameters(getRandom());
            setGenerator(new Ed448KeyPairGenerator(), myParams);
            setFactorySet(BouncyStdKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyEd448PrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyEd448PrivateKey(getKeySpec(), (Ed448PrivateKeyParameters) pThat);
        }

        @Override
        BouncyEd448PublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyEd448PublicKey(getKeySpec(), (Ed448PublicKeyParameters) pThat);
        }
    }
}
