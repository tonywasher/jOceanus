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
package io.github.tonywasher.joceanus.gordianknot.impl.bc;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.GordianEncryptorSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianDHGroup;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyKeyPair.BouncyPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyKeyPair.BouncyPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyRSAKeyPair.BouncyCoreEncryptor;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.GordianKeyPairValidity;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.oiw.ElGamalParameter;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.engines.ElGamalEngine;
import org.bouncycastle.crypto.generators.ElGamalKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.ElGamalKeyGenerationParameters;
import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;

import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * ElGamal KeyPair classes.
 */
public final class BouncyElGamalKeyPair {
    /**
     * Private constructor.
     */
    private BouncyElGamalKeyPair() {
    }

    /**
     * Bouncy ElGamal PublicKey.
     */
    public static class BouncyElGamalPublicKey
            extends BouncyPublicKey<ElGamalPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyElGamalPublicKey(final GordianKeyPairSpec pKeySpec,
                               final ElGamalPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final ElGamalPublicKeyParameters myThis = getPublicKey();
            final ElGamalPublicKeyParameters myThat = (ElGamalPublicKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
        }

        /**
         * Is the private key valid for this public key?
         *
         * @param pPrivate the private key
         * @return true/false
         */
        public boolean validPrivate(final BouncyElGamalPrivateKey pPrivate) {
            final ElGamalPrivateKeyParameters myPrivate = pPrivate.getPrivateKey();
            return getPublicKey().getParameters().equals(myPrivate.getParameters());
        }

        /**
         * CompareKeys.
         *
         * @param pFirst  the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final ElGamalPublicKeyParameters pFirst,
                                           final ElGamalPublicKeyParameters pSecond) {
            return pFirst.equals(pSecond);
        }
    }

    /**
     * Bouncy ElGamal PrivateKey.
     */
    public static class BouncyElGamalPrivateKey
            extends BouncyPrivateKey<ElGamalPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyElGamalPrivateKey(final GordianKeyPairSpec pKeySpec,
                                final ElGamalPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final ElGamalPrivateKeyParameters myThis = getPrivateKey();
            final ElGamalPrivateKeyParameters myThat = (ElGamalPrivateKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
        }

        /**
         * CompareKeys.
         *
         * @param pFirst  the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final ElGamalPrivateKeyParameters pFirst,
                                           final ElGamalPrivateKeyParameters pSecond) {
            return pFirst.equals(pSecond);
        }
    }

    /**
     * BouncyCastle ElGamal KeyPair generator.
     */
    public static class BouncyElGamalKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final ElGamalKeyPairGenerator theGenerator;

        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyElGamalKeyPairGenerator(final GordianBaseFactory pFactory,
                                      final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create the parameter generator */
            final GordianDHGroup myGroup = pKeySpec.getDHGroup();
            final DHParameters myDHParms = myGroup.getParameters();
            final ElGamalParameters myParms = new ElGamalParameters(myDHParms.getP(), myDHParms.getQ());
            final ElGamalKeyGenerationParameters myParams = new ElGamalKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            theGenerator = new ElGamalKeyPairGenerator();
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyElGamalPublicKey myPublic = new BouncyElGamalPublicKey(getKeySpec(), (ElGamalPublicKeyParameters) myPair.getPublic());
            final BouncyElGamalPrivateKey myPrivate = new BouncyElGamalPrivateKey(getKeySpec(), (ElGamalPrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Check the keyPair type and keySpecs */
                BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

                /* build and return the encoding */
                final BouncyElGamalPrivateKey myPrivateKey = (BouncyElGamalPrivateKey) getPrivateKey(pKeyPair);
                final ElGamalPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
                final AlgorithmIdentifier myAlgId = getAlgorithmIdentifier(myParms.getParameters());
                final PrivateKeyInfo myInfo = new PrivateKeyInfo(myAlgId, new ASN1Integer(myParms.getX()), null);
                return new PKCS8EncodedKeySpec(myInfo.getEncoded());

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                           final PKCS8EncodedKeySpec pPrivateKey) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pPrivateKey);

                /* derive keyPair */
                final BouncyElGamalPublicKey myPublic = derivePublicKey(pPublicKey);
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final ElGamalPrivateKeyParameters myParms = (ElGamalPrivateKeyParameters) PrivateKeyFactory.createKey(myInfo);
                final BouncyElGamalPrivateKey myPrivate = new BouncyElGamalPrivateKey(getKeySpec(), myParms);
                final BouncyKeyPair myPair = new BouncyKeyPair(myPublic, myPrivate);

                /* Check that we have a matching pair */
                GordianKeyPairValidity.checkValidity(getFactory(), myPair);

                /* Return the keyPair */
                return myPair;

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Check the keyPair type and keySpecs */
                BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

                /* build and return the encoding */
                final BouncyElGamalPublicKey myPublicKey = (BouncyElGamalPublicKey) getPublicKey(pKeyPair);
                final ElGamalPublicKeyParameters myParms = myPublicKey.getPublicKey();
                final AlgorithmIdentifier myAlgId = getAlgorithmIdentifier(myParms.getParameters());
                final SubjectPublicKeyInfo myInfo = new SubjectPublicKeyInfo(myAlgId, new ASN1Integer(myParms.getY()));
                return new X509EncodedKeySpec(myInfo.getEncoded());

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws GordianException {
            final BouncyElGamalPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         *
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws GordianException on error
         */
        private BouncyElGamalPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pEncodedKey);

                /* derive publicKey */
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final ElGamalPublicKeyParameters myParms = (ElGamalPublicKeyParameters) PublicKeyFactory.createKey(myInfo);
                return new BouncyElGamalPublicKey(getKeySpec(), myParms);

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        /**
         * Obtain algorithm Id from ElGamalParameters.
         *
         * @param pParams the parameters
         * @return the algorithmId.
         */
        private static AlgorithmIdentifier getAlgorithmIdentifier(final ElGamalParameters pParams) {
            return new AlgorithmIdentifier(OIWObjectIdentifiers.elGamalAlgorithm,
                    new ElGamalParameter(pParams.getP(), pParams.getG()).toASN1Primitive());
        }
    }

    /**
     * ElGamal Encryptor.
     */
    public static class BouncyElGamalEncryptor
            extends BouncyCoreEncryptor {
        /**
         * Constructor.
         *
         * @param pFactory the factory
         * @param pSpec    the encryptorSpec
         * @throws GordianException on error
         */
        BouncyElGamalEncryptor(final GordianBaseFactory pFactory,
                               final GordianEncryptorSpec pSpec) throws GordianException {
            /* Initialise underlying cipher */
            super(pFactory, pSpec, new ElGamalEngine());
        }

        @Override
        protected BouncyElGamalPublicKey getPublicKey() {
            return (BouncyElGamalPublicKey) super.getPublicKey();
        }

        @Override
        protected BouncyElGamalPrivateKey getPrivateKey() {
            return (BouncyElGamalPrivateKey) super.getPrivateKey();
        }
    }
}
