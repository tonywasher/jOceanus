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
import org.bouncycastle.crypto.generators.X25519KeyPairGenerator;
import org.bouncycastle.crypto.generators.X448KeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448KeyGenerationParameters;
import org.bouncycastle.crypto.params.X448PrivateKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;

import java.util.Arrays;

/**
 * EdwardsCurve XDH KeyPair classes.
 */
public final class BouncyXDHKeyPair {
    /**
     * Private constructor.
     */
    private BouncyXDHKeyPair() {
    }

    /**
     * Bouncy EdwardsX25519 PublicKey.
     */
    public static class BouncyX25519PublicKey
            extends BouncyPublicKey<X25519PublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyX25519PublicKey(final GordianKeyPairSpec pKeySpec,
                              final X25519PublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final X25519PublicKeyParameters myThis = getPublicKey();
            final X25519PublicKeyParameters myThat = (X25519PublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy EdwardsX25519 PrivateKey.
     */
    public static class BouncyX25519PrivateKey
            extends BouncyPrivateKey<X25519PrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyX25519PrivateKey(final GordianKeyPairSpec pKeySpec,
                               final X25519PrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final X25519PrivateKeyParameters myThis = getPrivateKey();
            final X25519PrivateKeyParameters myThat = (X25519PrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy EdwardsX448 PublicKey.
     */
    public static class BouncyX448PublicKey
            extends BouncyPublicKey<X448PublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyX448PublicKey(final GordianKeyPairSpec pKeySpec,
                            final X448PublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final X448PublicKeyParameters myThis = getPublicKey();
            final X448PublicKeyParameters myThat = (X448PublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy EdwardsX448 PrivateKey.
     */
    public static class BouncyX448PrivateKey
            extends BouncyPrivateKey<X448PrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyX448PrivateKey(final GordianKeyPairSpec pKeySpec,
                             final X448PrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final X448PrivateKeyParameters myThis = getPrivateKey();
            final X448PrivateKeyParameters myThat = (X448PrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle EdwardsX25519 KeyPair generator.
     */
    public static class BouncyX25519KeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyX25519KeyPairGenerator(final GordianBaseFactory pFactory,
                                     final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            final X25519KeyGenerationParameters myParams = new X25519KeyGenerationParameters(getRandom());
            setGenerator(new X25519KeyPairGenerator(), myParams);
            setFactorySet(BouncyStdKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyX25519PrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyX25519PrivateKey(getKeySpec(), (X25519PrivateKeyParameters) pThat);
        }

        @Override
        BouncyX25519PublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyX25519PublicKey(getKeySpec(), (X25519PublicKeyParameters) pThat);
        }
    }

    /**
     * BouncyCastle EdwardsX448 KeyPair generator.
     */
    public static class BouncyX448KeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyX448KeyPairGenerator(final GordianBaseFactory pFactory,
                                   final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            final X448KeyGenerationParameters myParams = new X448KeyGenerationParameters(getRandom());
            setGenerator(new X448KeyPairGenerator(), myParams);
            setFactorySet(BouncyStdKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyX448PrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyX448PrivateKey(getKeySpec(), (X448PrivateKeyParameters) pThat);
        }

        @Override
        BouncyX448PublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyX448PublicKey(getKeySpec(), (X448PublicKeyParameters) pThat);
        }
    }
}
