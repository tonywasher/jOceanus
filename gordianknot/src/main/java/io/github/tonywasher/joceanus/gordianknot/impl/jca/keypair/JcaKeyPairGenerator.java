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
package io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.GordianCoreKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.GordianKeyPairValidity;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.base.JcaProvider;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair.JcaPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair.JcaPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair.JcaStateAwareKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair.JcaStateAwarePrivateKey;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Jca KeyPair generator.
 */
public abstract class JcaKeyPairGenerator
        extends GordianCoreKeyPairGenerator {
    /**
     * Parse error.
     */
    private static final String PARSE_ERROR = "Failed to parse encoding";

    /**
     * Factory.
     */
    private KeyFactory theFactory;

    /**
     * Generator.
     */
    private KeyPairGenerator theGenerator;

    /**
     * Constructor.
     *
     * @param pFactory the Security Factory
     * @param pKeySpec the keySpec
     */
    JcaKeyPairGenerator(final GordianBaseFactory pFactory,
                        final GordianKeyPairSpec pKeySpec) {
        super(pFactory, pKeySpec);
    }

    /**
     * Obtain the keyPair generator.
     *
     * @return the keyPairGenerator
     */
    KeyPairGenerator getGenerator() {
        return theGenerator;
    }

    /**
     * Set the keyPairGenerator.
     *
     * @param pGenerator the keyPairGenerator
     */
    void setKeyPairGenerator(final KeyPairGenerator pGenerator) {
        theGenerator = pGenerator;
    }

    /**
     * Obtain the key factory.
     *
     * @return the keyFactory
     */
    KeyFactory getKeyFactory() {
        return theFactory;
    }

    /**
     * Set the key factory.
     *
     * @param pFactory the keyFactory
     */
    void setKeyFactory(final KeyFactory pFactory) {
        theFactory = pFactory;
    }

    @Override
    public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws GordianException {
        /* Check the keyPair */
        JcaKeyPair.checkKeyPair(pKeyPair);

        /* derive the encoding */
        final JcaPrivateKey myPrivateKey = (JcaPrivateKey) getPrivateKey(pKeyPair);
        return new PKCS8EncodedKeySpec(myPrivateKey.getPrivateKey().getEncoded());
    }

    @Override
    public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws GordianException {
        /* Check the keyPair */
        JcaKeyPair.checkKeyPair(pKeyPair);

        /* derive the encoding */
        final JcaPublicKey myPublicKey = (JcaPublicKey) getPublicKey(pKeyPair);
        return new X509EncodedKeySpec(myPublicKey.getPublicKey().getEncoded());
    }

    @Override
    public JcaKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                    final PKCS8EncodedKeySpec pPrivateKey) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Check the keySpec */
            checkKeySpec(pPrivateKey);

            /* derive the keyPair */
            final JcaPublicKey myPublic = derivePublicKey(pPublicKey);
            final JcaPrivateKey myPrivate = createPrivate(theFactory.generatePrivate(pPrivateKey));
            final JcaKeyPair myPair = new JcaKeyPair(myPublic, myPrivate);

            /* Check that we have a matching pair */
            GordianKeyPairValidity.checkValidity(getFactory(), myPair);

            /* Return the keyPair */
            return myPair;

        } catch (InvalidKeySpecException e) {
            throw new GordianCryptoException(PARSE_ERROR, e);
        }
    }

    /**
     * Create private key.
     *
     * @param pPrivateKey the private key
     * @return the private key
     */
    protected JcaPrivateKey createPrivate(final PrivateKey pPrivateKey) {
        return new JcaPrivateKey(getKeySpec(), pPrivateKey);
    }

    /**
     * Create public key.
     *
     * @param pPublicKey the public key
     * @return the public key
     */
    protected JcaPublicKey createPublic(final PublicKey pPublicKey) {
        return new JcaPublicKey(getKeySpec(), pPublicKey);
    }

    @Override
    public JcaKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pPublicKey) throws GordianException {
        final JcaPublicKey myPublic = derivePublicKey(pPublicKey);
        return new JcaKeyPair(myPublic);
    }

    /**
     * Derive the public key.
     *
     * @param pEncodedKey the encoded public key
     * @return the public key
     * @throws GordianException on error
     */
    protected JcaPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Check the keySpec */
            checkKeySpec(pEncodedKey);

            /* derive the key */
            return createPublic(theFactory.generatePublic(pEncodedKey));

        } catch (InvalidKeySpecException e) {
            throw new GordianCryptoException(PARSE_ERROR, e);
        }
    }

    @Override
    public JcaKeyPair generateKeyPair() {
        /* Generate and return the keyPair */
        final KeyPair myPair = theGenerator.generateKeyPair();
        final JcaPublicKey myPublic = createPublic(myPair.getPublic());
        final JcaPrivateKey myPrivate = createPrivate(myPair.getPrivate());
        return new JcaKeyPair(myPublic, myPrivate);
    }

    /**
     * Create generator and factory.
     *
     * @param pAlgorithm  the Algorithm
     * @param postQuantum is this a postQuantum algorithm?
     * @throws GordianException on error
     */
    void createFactories(final String pAlgorithm,
                         final boolean postQuantum) throws GordianException {
        final KeyPairGenerator myGenerator = getJavaKeyPairGenerator(pAlgorithm, postQuantum);
        setKeyPairGenerator(myGenerator);

        /* Create the factory */
        setKeyFactory(getJavaKeyFactory(pAlgorithm, postQuantum));
    }

    /**
     * Jca StateAware KeyPair generator.
     */
    public abstract static class JcaStateAwareKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws GordianException on error
         */
        JcaStateAwareKeyPairGenerator(final GordianBaseFactory pFactory,
                                      final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* initialize underlying class */
            super(pFactory, pKeySpec);
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final KeyPair myPair = getGenerator().generateKeyPair();
            final JcaPublicKey myPublic = createPublic(myPair.getPublic());
            final JcaStateAwarePrivateKey myPrivate = createPrivate(myPair.getPrivate());
            return new JcaStateAwareKeyPair(myPublic, myPrivate);
        }

        @Override
        protected JcaStateAwarePrivateKey createPrivate(final PrivateKey pPrivateKey) {
            return new JcaStateAwarePrivateKey(getKeySpec(), pPrivateKey);
        }

        @Override
        public JcaKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                        final PKCS8EncodedKeySpec pPrivateKey) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pPrivateKey);

                /* derive keyPair */
                final JcaPublicKey myPublic = derivePublicKey(pPublicKey);
                JcaStateAwarePrivateKey myPrivate = createPrivate(getKeyFactory().generatePrivate(pPrivateKey));
                final JcaKeyPair myPair = new JcaStateAwareKeyPair(myPublic, myPrivate);

                /* Check that we have a matching pair */
                GordianKeyPairValidity.checkValidity(getFactory(), myPair);

                /* Rebuild and return the keyPair to avoid incrementing usage count */
                myPrivate = createPrivate(getKeyFactory().generatePrivate(pPrivateKey));
                return new JcaStateAwareKeyPair(myPublic, myPrivate);

            } catch (InvalidKeySpecException e) {
                throw new GordianCryptoException(PARSE_ERROR, e);
            }
        }
    }

    /**
     * Create the BouncyCastle KeyFactory via JCA.
     *
     * @param pAlgorithm  the Algorithm
     * @param postQuantum is this a postQuantum algorithm?
     * @return the KeyFactory
     * @throws GordianException on error
     */
    static KeyFactory getJavaKeyFactory(final String pAlgorithm,
                                        final boolean postQuantum) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Return a KeyFactory for the algorithm */
            return KeyFactory.getInstance(pAlgorithm, postQuantum
                    ? JcaProvider.BCPQPROV
                    : JcaProvider.BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create KeyFactory", e);
        }
    }

    /**
     * Create the BouncyCastle KeyPairGenerator via JCA.
     *
     * @param pAlgorithm  the Algorithm
     * @param postQuantum is this a postQuantum algorithm?
     * @return the KeyPairGenerator
     * @throws GordianException on error
     */
    static KeyPairGenerator getJavaKeyPairGenerator(final String pAlgorithm,
                                                    final boolean postQuantum) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Return a KeyPairGenerator for the algorithm */
            return KeyPairGenerator.getInstance(pAlgorithm, postQuantum
                    ? JcaProvider.BCPQPROV
                    : JcaProvider.BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create KeyPairGenerator", e);
        }
    }
}
