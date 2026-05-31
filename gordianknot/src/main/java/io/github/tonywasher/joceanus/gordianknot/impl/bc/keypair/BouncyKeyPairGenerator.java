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


import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyStateAwareKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyStateAwarePrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.GordianCoreKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.GordianKeyPairValidity;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * BouncyCastle KeyPair generator.
 */
public abstract class BouncyKeyPairGenerator
        extends GordianCoreKeyPairGenerator {
    /**
     * Parsing error.
     */
    static final String ERROR_PARSE = "Failed to parse encoding";

    /**
     * Prime certainty.
     */
    static final int PRIME_CERTAINTY = 128;

    /**
     * Generator.
     */
    private AsymmetricCipherKeyPairGenerator theGenerator;

    /**
     * The factorySet.
     */
    private BouncyKeyFactorySet theFactorySet;

    /**
     * Constructor.
     *
     * @param pFactory the Security Factory
     * @param pKeySpec the keySpec
     */
    protected BouncyKeyPairGenerator(final GordianBaseFactory pFactory,
                                     final GordianKeyPairSpec pKeySpec) {
        super(pFactory, pKeySpec);
    }

    /**
     * set keyPairGenerator.
     *
     * @param pGenerator the generator
     * @param pParams    the initiation parameters
     */
    void setGenerator(final AsymmetricCipherKeyPairGenerator pGenerator,
                      final KeyGenerationParameters pParams) {
        theGenerator = pGenerator;
        theGenerator.init(pParams);
    }

    /**
     * set factorySet.
     *
     * @param pFactorySet the factorySet
     */
    void setFactorySet(final BouncyKeyFactorySet pFactorySet) {
        theFactorySet = pFactorySet;
    }

    @Override
    public BouncyKeyPair generateKeyPair() {
        /* Generate and return the keyPair */
        final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
        final BouncyPublicKey<?> myPublic = newPublicKey(myPair.getPublic());
        final BouncyPrivateKey<?> myPrivate = newPrivateKey(myPair.getPrivate());
        return createKeyPair(myPublic, myPrivate);
    }

    /**
     * Create keyPair.
     *
     * @param pPublicKey  the public key
     * @param pPrivateKey the private key
     * @return the keyPair
     */
    private BouncyKeyPair createKeyPair(final BouncyPublicKey<?> pPublicKey,
                                        final BouncyPrivateKey<?> pPrivateKey) {
        return pPrivateKey instanceof BouncyStateAwarePrivateKey<?> myPrivate
                ? new BouncyStateAwareKeyPair(pPublicKey, myPrivate)
                : new BouncyKeyPair(pPublicKey, pPrivateKey);
    }

    @Override
    public BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                       final PKCS8EncodedKeySpec pPrivateKey) throws GordianException {
        /* Check the keySpecs */
        checkKeySpec(pPrivateKey);

        /* derive keyPair */
        final BouncyPublicKey<?> myPublic = derivePublicKey(pPublicKey);
        final AsymmetricKeyParameter myParms = theFactorySet.parsePKCS8EncodedKeySpec(pPrivateKey);
        final BouncyPrivateKey<?> myPrivate = newPrivateKey(myParms);
        final BouncyKeyPair myPair = createKeyPair(myPublic, myPrivate);

        /* Check that we have a matching pair */
        GordianKeyPairValidity.checkValidity(getFactory(), myPair);

        /* If this is a stateAware privateKey */
        if (myPrivate instanceof BouncyStateAwarePrivateKey) {
            /* Rebuild and return the keyPair to avoid incrementing usage count */
            final AsymmetricKeyParameter myNewParms = theFactorySet.parsePKCS8EncodedKeySpec(pPrivateKey);
            final BouncyStateAwarePrivateKey<?> myNewPrivate = (BouncyStateAwarePrivateKey<?>) newPrivateKey(myNewParms);
            return new BouncyStateAwareKeyPair(myPublic, myNewPrivate);
        }

        /* Return the keyPair */
        return myPair;
    }

    @Override
    public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws GordianException {
        /* Check the keyPair type and keySpecs */
        BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

        /* build and return the encoding */
        final BouncyPrivateKey<?> myPrivateKey = (BouncyPrivateKey<?>) getPrivateKey(pKeyPair);
        final AsymmetricKeyParameter myParms = myPrivateKey.getPrivateKey();
        return theFactorySet.createPKCS8EncodedKeySpec(myParms);
    }

    @Override
    public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws GordianException {
        /* Check the keyPair type and keySpecs */
        BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

        /* build and return the encoding */
        final BouncyPublicKey<?> myPublicKey = (BouncyPublicKey<?>) getPublicKey(pKeyPair);
        final AsymmetricKeyParameter myParms = myPublicKey.getPublicKey();
        return theFactorySet.createX509EncodedKeySpec(myParms);
    }

    @Override
    public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws GordianException {
        final BouncyPublicKey<?> myPublic = derivePublicKey(pEncodedKey);
        return new BouncyKeyPair(myPublic);
    }

    /**
     * Derive public key from encoded.
     *
     * @param pEncodedKey the encoded key
     * @return the public key
     * @throws GordianException on error
     */
    private BouncyPublicKey<?> derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws GordianException {
        /* Check the keySpecs */
        checkKeySpec(pEncodedKey);

        /* derive publicKey */
        final AsymmetricKeyParameter myParms = theFactorySet.parseX509EncodedKeySpec(pEncodedKey);
        return newPublicKey(myParms);
    }

    /**
     * Create new private key from parameters.
     *
     * @param pParms the parameters
     * @return the new public key
     */
    abstract BouncyPrivateKey<?> newPrivateKey(AsymmetricKeyParameter pParms);

    /**
     * Create new public key from parameters.
     *
     * @param pParms the parameters
     * @return the new public key
     */
    abstract BouncyPublicKey<?> newPublicKey(AsymmetricKeyParameter pParms);

    /**
     * KeyFactorySet.
     */
    interface BouncyKeyFactorySet {
        /**
         * parse PKCS8EncodedKeySpec to PrivateKeyParams.
         *
         * @param pEncoded the encodedKeySpec
         * @return the PrivateKeyParams
         * @throws GordianException on error
         */
        AsymmetricKeyParameter parsePKCS8EncodedKeySpec(PKCS8EncodedKeySpec pEncoded) throws GordianException;

        /**
         * create PKCS8EncodedKeySpec for PrivateKeyParams.
         *
         * @param pParams the privateKeyParameters
         * @return the PKCS8EncodedKeySpec
         * @throws GordianException on error
         */
        PKCS8EncodedKeySpec createPKCS8EncodedKeySpec(AsymmetricKeyParameter pParams) throws GordianException;

        /**
         * parse X509EncodedKeySpec to PublicKeyParams.
         *
         * @param pEncoded the encodedKeySpec
         * @return the PublicKeyParams
         * @throws GordianException on error
         */
        AsymmetricKeyParameter parseX509EncodedKeySpec(X509EncodedKeySpec pEncoded) throws GordianException;

        /**
         * create X509EncodedKeySpec for PublicKeyParams.
         *
         * @param pParams the publicKeyParameters
         * @return the X509EncodedKeySpec
         * @throws GordianException on error
         */
        X509EncodedKeySpec createX509EncodedKeySpec(AsymmetricKeyParameter pParams) throws GordianException;
    }
}
