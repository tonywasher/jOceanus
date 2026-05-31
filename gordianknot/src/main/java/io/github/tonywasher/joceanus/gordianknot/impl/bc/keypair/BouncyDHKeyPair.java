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
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.parser.GordianDHEncodedParser;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreDHSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.pkcs.DHParameter;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.DomainParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.generators.DHKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DHKeyGenerationParameters;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;

import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

/**
 * DiffieHellman KeyPair classes.
 */
public final class BouncyDHKeyPair {
    /**
     * Private constructor.
     */
    private BouncyDHKeyPair() {
    }

    /**
     * Bouncy DH PublicKey.
     */
    public static class BouncyDHPublicKey
            extends BouncyPublicKey<DHPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyDHPublicKey(final GordianKeyPairSpec pKeySpec,
                          final DHPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final DHPublicKeyParameters myThis = getPublicKey();
            final DHPublicKeyParameters myThat = (DHPublicKeyParameters) pThat;

            /* Compare keys */
            return Objects.equals(myThis, myThat);
        }
    }

    /**
     * Bouncy DH PrivateKey.
     */
    public static class BouncyDHPrivateKey
            extends BouncyPrivateKey<DHPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyDHPrivateKey(final GordianKeyPairSpec pKeySpec,
                           final DHPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final DHPrivateKeyParameters myThis = getPrivateKey();
            final DHPrivateKeyParameters myThat = (DHPrivateKeyParameters) pThat;

            /* Compare keys */
            return Objects.equals(myThis, myThat);
        }
    }

    /**
     * BouncyCastle DH KeyPair generator.
     */
    public static class BouncyDHKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyDHKeyPairGenerator(final GordianBaseFactory pFactory,
                                 final GordianKeyPairSpec pKeySpec) {
            /* Initialize underlying class */
            super(pFactory, pKeySpec);

            /* Create the parameter generator */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final GordianCoreDHSpec myGroup = myKeySpec.getDHSpec();
            final DHParameters myParms = myGroup.getParameters();
            final DHKeyGenerationParameters myParams = new DHKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            setGenerator(new DHKeyPairGenerator(), myParams);
            setFactorySet(BouncyDHKeyFactorySet.INSTANCE);
        }

        @Override
        BouncyDHPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyDHPrivateKey(getKeySpec(), (DHPrivateKeyParameters) pThat);
        }

        @Override
        BouncyDHPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyDHPublicKey(getKeySpec(), (DHPublicKeyParameters) pThat);
        }
    }

    /**
     * DH KeyFactorySet.
     */
    private enum BouncyDHKeyFactorySet
            implements BouncyKeyFactorySet {
        /**
         * Instance.
         */
        INSTANCE;

        @Override
        public AsymmetricKeyParameter parsePKCS8EncodedKeySpec(final PKCS8EncodedKeySpec pEncodedKey) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Parse the encoded keySpec */
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pEncodedKey.getEncoded());
                final BCDHPrivateKey myKey = new BCDHPrivateKey(myInfo);
                final DHParameters myParms = GordianDHEncodedParser.determineParameters(myInfo.getPrivateKeyAlgorithm());
                return new DHPrivateKeyParameters(myKey.getX(), myParms);

            } catch (IOException e) {
                throw new GordianCryptoException(BouncyKeyPairGenerator.ERROR_PARSE, e);
            }
        }

        @Override
        public PKCS8EncodedKeySpec createPKCS8EncodedKeySpec(final AsymmetricKeyParameter pParams) throws GordianException {
            /* Protect against exceptions */
            try {
                /* build and return the encoding */
                final DHPrivateKeyParameters myKey = (DHPrivateKeyParameters) pParams;
                final DHParameters myParms = myKey.getParameters();
                final AlgorithmIdentifier myId = getAlgorithmIdentifier(myParms);
                final PrivateKeyInfo myInfo = new PrivateKeyInfo(myId, new ASN1Integer(myKey.getX()));
                return new PKCS8EncodedKeySpec(myInfo.getEncoded());

            } catch (IOException e) {
                throw new GordianCryptoException(BouncyKeyPairGenerator.ERROR_PARSE, e);
            }
        }

        @Override
        public AsymmetricKeyParameter parseX509EncodedKeySpec(final X509EncodedKeySpec pEncodedKey) {
            /* Parse the encoded keySpec */
            final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
            final BCDHPublicKey myKey = new BCDHPublicKey(myInfo);
            return myKey.engineGetKeyParameters();
        }

        @Override
        public X509EncodedKeySpec createX509EncodedKeySpec(final AsymmetricKeyParameter pParams) {
            /* build and return the encoding */
            final DHPublicKeyParameters myKey = (DHPublicKeyParameters) pParams;
            final DHParameters myParms = myKey.getParameters();
            final AlgorithmIdentifier myId = getAlgorithmIdentifier(myParms);
            final byte[] myBytes = KeyUtil.getEncodedSubjectPublicKeyInfo(myId, new ASN1Integer(myKey.getY()));
            return new X509EncodedKeySpec(myBytes);
        }

        /**
         * Obtain algorithm Id from DHParameters.
         *
         * @param pParams the parameters
         * @return the algorithmId.
         */
        private static AlgorithmIdentifier getAlgorithmIdentifier(final DHParameters pParams) {
            /* If this is a public # algorithm */
            return pParams.getQ() != null
                    ? new AlgorithmIdentifier(X9ObjectIdentifiers.dhpublicnumber,
                    new DomainParameters(pParams.getP(), pParams.getG(), pParams.getQ(), pParams.getJ(), null).toASN1Primitive())
                    : new AlgorithmIdentifier(PKCSObjectIdentifiers.dhKeyAgreement,
                    new DHParameter(pParams.getP(), pParams.getG(), pParams.getL()).toASN1Primitive());
        }
    }
}
