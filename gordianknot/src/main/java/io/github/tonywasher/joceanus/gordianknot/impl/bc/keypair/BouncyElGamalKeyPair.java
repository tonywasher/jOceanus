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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPairGenerator.BouncyKeyFactorySet;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreDHSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.oiw.ElGamalParameter;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.generators.ElGamalKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.ElGamalKeyGenerationParameters;
import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;

import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

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
            return Objects.equals(myThis, myThat);
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
            return Objects.equals(myThis, myThat);
        }
    }

    /**
     * BouncyCastle ElGamal KeyPair generator.
     */
    public static class BouncyElGamalKeyPairGenerator
            extends BouncyKeyPairGenerator<ElGamalPrivateKeyParameters, ElGamalPublicKeyParameters> {
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
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final GordianCoreDHSpec myGroup = myKeySpec.getDHSpec();
            final DHParameters myDHParms = myGroup.getParameters();
            final ElGamalParameters myParms = new ElGamalParameters(myDHParms.getP(), myDHParms.getQ());
            final ElGamalKeyGenerationParameters myParams = new ElGamalKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new ElGamalKeyPairGenerator(), myParams);
            setFactorySet(BouncyElGamalKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyElGamalPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyElGamalPrivateKey(getKeySpec(), (ElGamalPrivateKeyParameters) pThat);
        }

        @Override
        BouncyElGamalPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyElGamalPublicKey(getKeySpec(), (ElGamalPublicKeyParameters) pThat);
        }
    }

    /**
     * ElGamal KeyFactorySet.
     */
    private enum BouncyElGamalKeyFactorySet
            implements BouncyKeyFactorySet {
        /**
         * Instance.
         */
        INSTANCE;

        @Override
        public AsymmetricKeyParameter parsePKCS8EncodedKeySpec(final PKCS8EncodedKeySpec pEncodedKey) throws GordianException {
            return BouncyStdKeyFactorySet.INSTANCE.parsePKCS8EncodedKeySpec(pEncodedKey);
        }

        @Override
        public PKCS8EncodedKeySpec createPKCS8EncodedKeySpec(final AsymmetricKeyParameter pParams) throws GordianException {
            /* Protect against exceptions */
            try {
                /* build and return the encoding */
                final ElGamalPrivateKeyParameters myKey = (ElGamalPrivateKeyParameters) pParams;
                final AlgorithmIdentifier myAlgId = getAlgorithmIdentifier(myKey.getParameters());
                final PrivateKeyInfo myInfo = new PrivateKeyInfo(myAlgId, new ASN1Integer(myKey.getX()), null);
                return new PKCS8EncodedKeySpec(myInfo.getEncoded());

            } catch (IOException e) {
                throw new GordianCryptoException(BouncyKeyPairGenerator.ERROR_PARSE, e);
            }
        }

        @Override
        public AsymmetricKeyParameter parseX509EncodedKeySpec(final X509EncodedKeySpec pEncodedKey) throws GordianException {
            /* Parse the encoded keySpec */
            return BouncyStdKeyFactorySet.INSTANCE.parseX509EncodedKeySpec(pEncodedKey);
        }

        @Override
        public X509EncodedKeySpec createX509EncodedKeySpec(final AsymmetricKeyParameter pParams) throws GordianException {
            /* Protect against exceptions */
            try {
                /* build and return the encoding */
                final ElGamalPublicKeyParameters myKey = (ElGamalPublicKeyParameters) pParams;
                final AlgorithmIdentifier myAlgId = getAlgorithmIdentifier(myKey.getParameters());
                final SubjectPublicKeyInfo myInfo = new SubjectPublicKeyInfo(myAlgId, new ASN1Integer(myKey.getY()));
                return new X509EncodedKeySpec(myInfo.getEncoded());

            } catch (IOException e) {
                throw new GordianCryptoException(BouncyKeyPairGenerator.ERROR_PARSE, e);
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
}
