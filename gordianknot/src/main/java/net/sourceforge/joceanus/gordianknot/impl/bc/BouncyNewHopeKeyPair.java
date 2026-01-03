/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2022 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.gordianknot.impl.bc;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianRSAModulus;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.gordianknot.impl.core.agree.GordianAgreementMessageASN1;
import net.sourceforge.joceanus.gordianknot.impl.core.agree.GordianCoreAnonymousAgreement;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianKeyPairValidity;
import net.sourceforge.joceanus.gordianknot.impl.core.xagree.GordianXCoreAgreementFactory;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.ExchangePair;
import org.bouncycastle.pqc.crypto.newhope.NHAgreement;
import org.bouncycastle.pqc.crypto.newhope.NHExchangePairGenerator;
import org.bouncycastle.pqc.crypto.newhope.NHKeyPairGenerator;
import org.bouncycastle.pqc.crypto.newhope.NHPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory;

import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

/**
 * NewHope KeyPair classes.
 */
public final class BouncyNewHopeKeyPair {
    /**
     * Private constructor.
     */
    private BouncyNewHopeKeyPair() {
    }

    /**
     * Bouncy NewHope PublicKey.
     */
    public static class BouncyNewHopePublicKey
            extends BouncyPublicKey<NHPublicKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        BouncyNewHopePublicKey(final GordianKeyPairSpec pKeySpec,
                               final NHPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final NHPublicKeyParameters myThis = getPublicKey();
            final NHPublicKeyParameters myThat = (NHPublicKeyParameters) pThat;

            /* Check equality */
            return Arrays.equals(myThis.getPubData(), myThat.getPubData());
        }
    }

    /**
     * Bouncy NewHope PrivateKey.
     */
    public static class BouncyNewHopePrivateKey
            extends BouncyPrivateKey<NHPrivateKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        BouncyNewHopePrivateKey(final GordianKeyPairSpec pKeySpec,
                                final NHPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final NHPrivateKeyParameters myThis = getPrivateKey();
            final NHPrivateKeyParameters myThat = (NHPrivateKeyParameters) pThat;

            /* Check equality */
            return Arrays.equals(myThis.getSecData(), myThat.getSecData());
        }
    }

    /**
     * BouncyCastle NewHope KeyPair generator.
     */
    public static class BouncyNewHopeKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final NHKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyNewHopeKeyPairGenerator(final BouncyFactory pFactory,
                                      final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = new NHKeyPairGenerator();
            final KeyGenerationParameters myParams = new KeyGenerationParameters(getRandom(), GordianRSAModulus.MOD1024.getLength());
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyNewHopePublicKey myPublic = new BouncyNewHopePublicKey(getKeySpec(), (NHPublicKeyParameters) myPair.getPublic());
            final BouncyNewHopePrivateKey myPrivate = new BouncyNewHopePrivateKey(getKeySpec(), (NHPrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Check the keyPair type and keySpecs */
                BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

                /* build and return the encoding */
                final BouncyNewHopePrivateKey myPrivateKey = (BouncyNewHopePrivateKey) getPrivateKey(pKeyPair);
                final NHPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
                final PrivateKeyInfo myInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(myParms, null);
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
                final BouncyNewHopePublicKey myPublic = derivePublicKey(pPublicKey);
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final NHPrivateKeyParameters myParms = (NHPrivateKeyParameters) PrivateKeyFactory.createKey(myInfo);
                final BouncyNewHopePrivateKey myPrivate = new BouncyNewHopePrivateKey(getKeySpec(), myParms);
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
                final BouncyNewHopePublicKey myPublicKey = (BouncyNewHopePublicKey) getPublicKey(pKeyPair);
                final NHPublicKeyParameters myParms = myPublicKey.getPublicKey();
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(myParms);
                return new X509EncodedKeySpec(myInfo.getEncoded());

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws GordianException {
            final BouncyNewHopePublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws GordianException on error
         */
        private BouncyNewHopePublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pEncodedKey);

                /* derive publicKey */
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final NHPublicKeyParameters myParms = (NHPublicKeyParameters) PublicKeyFactory.createKey(myInfo);
                return new BouncyNewHopePublicKey(getKeySpec(), myParms);

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * NewHope Encapsulation.
     */
    public static class BouncyNewHopeAgreement
            extends GordianCoreAnonymousAgreement {
        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         */
        BouncyNewHopeAgreement(final BouncyFactory pFactory,
                               final GordianAgreementSpec pSpec) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Add in the derivation function */
            enableDerivation();
        }

        @Override
        public GordianAgreementMessageASN1 createClientHelloASN1(final GordianKeyPair pServer) throws GordianException {
            try {
                /* Check keyPair */
                BouncyKeyPair.checkKeyPair(pServer);
                checkKeyPair(pServer);

                /* Generate an Exchange KeyPair */
                final NHExchangePairGenerator myGenerator = new NHExchangePairGenerator(getRandom());
                final BouncyNewHopePublicKey myTarget = (BouncyNewHopePublicKey) getPublicKey(pServer);
                final ExchangePair myPair = myGenerator.GenerateExchange(myTarget.getPublicKey());

                /* Obtain the encoded keySpec of the public key */
                final NHPublicKeyParameters myParms = (NHPublicKeyParameters) myPair.getPublicKey();
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(myParms);
                final X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(myInfo.getEncoded());

                /* Build the clientHello Message */
                final GordianAgreementMessageASN1 myClientHello = buildClientHelloASN1(myKeySpec);

                /* Derive the secret */
                final byte[] mySecret = myPair.getSharedValue();
                storeSecret(mySecret);

                /* Return the clientHello  */
                return myClientHello;

            } catch (IOException e) {
                throw new GordianCryptoException(BouncyKeyPairGenerator.ERROR_PARSE, e);
            }
        }

        @Override
        public void acceptClientHelloASN1(final GordianKeyPair pServer,
                                          final GordianAgreementMessageASN1 pClientHello) throws GordianException {
            /* Check keyPair */
            BouncyKeyPair.checkKeyPair(pServer);
            checkKeyPair(pServer);

            /* Obtain keySpec */
            final X509EncodedKeySpec myKeySpec = pClientHello.getEphemeral();

            /* Derive ephemeral Public key */
            final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
            final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(pServer.getKeyPairSpec());
            final GordianKeyPair myPair = myGenerator.derivePublicOnlyKeyPair(myKeySpec);
            final BouncyNewHopePublicKey myPublic = (BouncyNewHopePublicKey) getPublicKey(myPair);

            /* Derive the secret */
            final BouncyNewHopePrivateKey myPrivate = (BouncyNewHopePrivateKey) getPrivateKey(pServer);
            final NHAgreement myAgreement = new NHAgreement();
            myAgreement.init(myPrivate.getPrivateKey());
            final byte[] mySecret = myAgreement.calculateAgreement(myPublic.getPublicKey());

            /* Store secret */
            storeSecret(mySecret);
        }
    }

    /**
     * NewHope XAgreement Engine.
     */
    public static class BouncyNewHopeXAgreementEngine
            extends BouncyXAgreementBase {
        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         * @throws GordianException on error
         */
        BouncyNewHopeXAgreementEngine(final GordianXCoreAgreementFactory pFactory,
                                      final GordianAgreementSpec pSpec) throws GordianException {
            /* Initialize underlying class */
            super(pFactory, pSpec);
        }

        @Override
        public void buildClientHello() throws GordianException {
            /* Generate an Exchange KeyPair */
            final NHExchangePairGenerator myGenerator = new NHExchangePairGenerator(getRandom());
            final BouncyNewHopePublicKey myTarget = (BouncyNewHopePublicKey) getPublicKey(getServerKeyPair());
            final ExchangePair myPair = myGenerator.GenerateExchange(myTarget.getPublicKey());

            /* Store the ephemeral keyPair */
            final GordianKeyPairSpec mySpec = getSpec().getKeyPairSpec();
            final BouncyNewHopePublicKey myPublic = new BouncyNewHopePublicKey(mySpec, (NHPublicKeyParameters) myPair.getPublicKey());
            final BouncyKeyPair myEphemeral = new BouncyKeyPair(myPublic);
            setClientEphemeral(myEphemeral);

            /* Store secret */
            storeSecret(myPair.getSharedValue());
        }

        @Override
        public void processClientHello() throws GordianException {
            /* Create extractor */
            final BouncyNewHopePublicKey myPublic = (BouncyNewHopePublicKey) getPublicKey(getClientEphemeral());
            final BouncyNewHopePrivateKey myPrivate = (BouncyNewHopePrivateKey) getPrivateKey(getServerKeyPair());

            /* Create agreement */
            final NHAgreement myAgreement = new NHAgreement();
            myAgreement.init(myPrivate.getPrivateKey());
            final byte[] mySecret = myAgreement.calculateAgreement(myPublic.getPublicKey());

            /* Store secret */
            storeSecret(mySecret);
        }
    }
}
