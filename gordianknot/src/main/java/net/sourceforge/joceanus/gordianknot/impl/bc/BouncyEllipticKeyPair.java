/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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

import java.io.IOException;
import java.math.BigInteger;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.security.auth.DestroyFailedException;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.agreement.ECDHCBasicAgreement;
import org.bouncycastle.crypto.agreement.ECDHCUnifiedAgreement;
import org.bouncycastle.crypto.agreement.ECMQVBasicAgreement;
import org.bouncycastle.crypto.ext.engines.EllipticEncryptor;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.kems.ECIESKEMExtractor;
import org.bouncycastle.crypto.kems.ECIESKEMGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDHUPrivateParameters;
import org.bouncycastle.crypto.params.ECDHUPublicParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.MQVPrivateParameters;
import org.bouncycastle.crypto.params.MQVPublicParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.util.BigIntegers;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianElliptic;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncySignature.BouncyDERCoder;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncySignature.BouncyDigestSignature;
import net.sourceforge.joceanus.gordianknot.impl.core.agree.GordianAgreementMessageASN1;
import net.sourceforge.joceanus.gordianknot.impl.core.agree.GordianCoreAnonymousAgreement;
import net.sourceforge.joceanus.gordianknot.impl.core.agree.GordianCoreBasicAgreement;
import net.sourceforge.joceanus.gordianknot.impl.core.agree.GordianCoreEphemeralAgreement;
import net.sourceforge.joceanus.gordianknot.impl.core.agree.GordianCoreSignedAgreement;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.gordianknot.impl.core.encrypt.GordianCoreEncryptor;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianKeyPairValidity;
import net.sourceforge.joceanus.oceanus.OceanusException;

/**
 * EllipticCurve KeyPair classes.
 */
public final class BouncyEllipticKeyPair {
    /**
     * Private constructor.
     */
    private BouncyEllipticKeyPair() {
    }

    /**
     * Bouncy Elliptic PublicKey.
     */
    public static class BouncyECPublicKey
            extends BouncyPublicKey<ECPublicKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        BouncyECPublicKey(final GordianKeyPairSpec pKeySpec,
                          final ECPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final ECPublicKeyParameters myThis = getPublicKey();
            final ECPublicKeyParameters myThat = (ECPublicKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
        }

        /**
         * Is the private key valid for this public key?
         * @param pPrivate the private key
         * @return true/false
         */
        public boolean validPrivate(final BouncyECPrivateKey pPrivate) {
            final ECPrivateKeyParameters myPrivate = pPrivate.getPrivateKey();
            return getPublicKey().getParameters().equals(myPrivate.getParameters());
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final ECPublicKeyParameters pFirst,
                                           final ECPublicKeyParameters pSecond) {
            return pFirst.getQ().equals(pSecond.getQ())
                    && pFirst.getParameters().equals(pSecond.getParameters());
        }
    }

    /**
     * Bouncy Elliptic PrivateKey.
     */
    public static class BouncyECPrivateKey
            extends BouncyPrivateKey<ECPrivateKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        BouncyECPrivateKey(final GordianKeyPairSpec pKeySpec,
                           final ECPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final ECPrivateKeyParameters myThis = getPrivateKey();
            final ECPrivateKeyParameters myThat = (ECPrivateKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final ECPrivateKeyParameters pFirst,
                                           final ECPrivateKeyParameters pSecond) {
            return pFirst.getD().equals(pSecond.getD())
                    && pFirst.getParameters().equals(pSecond.getParameters());
        }
    }

    /**
     * BouncyCastle Elliptic KeyPair generator.
     */
    public static class BouncyECKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final ECKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws OceanusException on error
         */
        BouncyECKeyPairGenerator(final BouncyFactory pFactory,
                                 final GordianKeyPairSpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create the generator */
            theGenerator = newGenerator();

            /* Lookup the parameters */
            final GordianElliptic myElliptic = pKeySpec.getElliptic();
            final String myCurve = myElliptic.getCurveName();
            final X9ECParameters x9 = myElliptic.hasCustomCurve()
                                ? ECUtil.getNamedCurveByName(myCurve)
                                : ECNamedCurveTable.getByName(myCurve);
            if (x9 == null) {
                throw new GordianLogicException("Invalid KeySpec - " + pKeySpec);
            }

            /* Initialise the generator */
            final ASN1ObjectIdentifier myOid = ECUtil.getNamedCurveOid(myCurve);
            final ECNamedDomainParameters myDomain = new ECNamedDomainParameters(myOid, x9.getCurve(), x9.getG(), x9.getN(), x9.getH(), x9.getSeed());
            final ECKeyGenerationParameters myParams = new ECKeyGenerationParameters(myDomain, getRandom());
            theGenerator.init(myParams);
        }

        /**
         * Create the generator.
         * @return the generator
         */
        ECKeyPairGenerator newGenerator() {
            return new ECKeyPairGenerator();
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyECPublicKey myPublic = new BouncyECPublicKey(getKeySpec(), (ECPublicKeyParameters) myPair.getPublic());
            final BouncyECPrivateKey myPrivate = new BouncyECPrivateKey(getKeySpec(), (ECPrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check the keyPair type and keySpecs */
                BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

                /* build and return the encoding */
                final BouncyECPrivateKey myPrivateKey = (BouncyECPrivateKey) getPrivateKey(pKeyPair);
                final ECPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
                final PrivateKeyInfo myInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(myParms);
                return new PKCS8EncodedKeySpec(myInfo.getEncoded());

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                           final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pPrivateKey);

                /* derive keyPair */
                final BouncyECPublicKey myPublic = derivePublicKey(pPublicKey);
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final ECPrivateKeyParameters myParms = (ECPrivateKeyParameters) PrivateKeyFactory.createKey(myInfo);
                final BouncyECPrivateKey myPrivate = new BouncyECPrivateKey(getKeySpec(), myParms);
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
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check the keyPair type and keySpecs */
                BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

                /* build and return the encoding */
                final BouncyECPublicKey myPublicKey = (BouncyECPublicKey) getPublicKey(pKeyPair);
                final ECPublicKeyParameters myParms = myPublicKey.getPublicKey();
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(myParms);
                return new X509EncodedKeySpec(myInfo.getEncoded());

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            final BouncyECPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws OceanusException on error
         */
        private BouncyECPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pEncodedKey);

                /* derive publicKey */
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final ECPublicKeyParameters myParms = (ECPublicKeyParameters) PublicKeyFactory.createKey(myInfo);
                return new BouncyECPublicKey(getKeySpec(), myParms);

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * EC signer.
     */
    public static class BouncyECSignature
            extends BouncyDigestSignature {
        /**
         * The Signer.
         */
        private final DSA theSigner;

        /**
         * The Coder.
         */
        private final BouncyDERCoder theCoder;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error
         */
        BouncyECSignature(final BouncyFactory pFactory,
                          final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the signer and Coder */
            theSigner = BouncySignature.getDSASigner(pFactory, pSpec);
            theCoder = new BouncyDERCoder();
        }

        @Override
        public void initForSigning(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            BouncyKeyPair.checkKeyPair(pKeyPair);
            super.initForSigning(pKeyPair);

            /* Initialise and set the signer */
            final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getKeyPair().getPrivateKey();
            final ParametersWithRandom myParms = new ParametersWithRandom(myPrivate.getPrivateKey(), getRandom());
            theSigner.init(true, myParms);
        }

        @Override
        public void initForVerify(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            BouncyKeyPair.checkKeyPair(pKeyPair);
            super.initForVerify(pKeyPair);

            /* Initialise and set the signer */
            final BouncyECPublicKey myPublic = (BouncyECPublicKey) getKeyPair().getPublicKey();
            theSigner.init(false, myPublic.getPublicKey());
        }

        @Override
        public byte[] sign() throws OceanusException {
            /* Check that we are in signing mode */
            checkMode(GordianSignatureMode.SIGN);

            /* Sign the message */
            final BigInteger[] myValues = theSigner.generateSignature(getDigest());
            return theCoder.dsaEncode(myValues[0], myValues[1]);
        }

        @Override
        public boolean verify(final byte[] pSignature) throws OceanusException {
            /* Check that we are in verify mode */
            checkMode(GordianSignatureMode.VERIFY);

            /* Verify the message */
            final BigInteger[] myValues = theCoder.dsaDecode(pSignature);
            return theSigner.verifySignature(getDigest(), myValues[0], myValues[1]);
        }
    }

    /**
     * ECIES Encapsulation.
     */
    public static class BouncyECIESAgreement
            extends GordianCoreAnonymousAgreement {
        /**
         * Key Length.
         */
        private static final int KEYLEN = 32;

        /**
         * Key Generator.
         */
        private final ECIESKEMGenerator theGenerator;

        /**
         * Derivation function.
         */
        private final DerivationFunction theDerivation;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         */
        BouncyECIESAgreement(final BouncyFactory pFactory,
                             final GordianAgreementSpec pSpec) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create Key Encapsulation */
            theDerivation = newDerivationFunction();
            theGenerator = new ECIESKEMGenerator(KEYLEN, theDerivation, getRandom());
        }

        @Override
        public GordianAgreementMessageASN1 createClientHelloASN1(final GordianKeyPair pServer) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check keyPair */
                BouncyKeyPair.checkKeyPair(pServer);
                checkKeyPair(pServer);

                /* Create encapsulation */
                final BouncyECPublicKey myPublic = (BouncyECPublicKey) getPublicKey(pServer);
                final SecretWithEncapsulation myResult = theGenerator.generateEncapsulated(myPublic.getPublicKey());

                /* Build the clientHello Message */
                final GordianAgreementMessageASN1 myClientHello = buildClientHelloASN1(myResult.getEncapsulation());

                /* Store secret and create initVector */
                storeSecret(myResult.getSecret());
                myResult.destroy();

                /* Return the clientHello message  */
                return myClientHello;
            } catch (DestroyFailedException e) {
                throw new GordianIOException("Failed to destroy secret", e);
            }
        }

        @Override
        public void acceptClientHelloASN1(final GordianKeyPair pServer,
                                          final GordianAgreementMessageASN1 pClientHello) throws OceanusException {
            /* Check keyPair */
            BouncyKeyPair.checkKeyPair(pServer);
            checkKeyPair(pServer);

            /* initialise Key Encapsulation */
            final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getPrivateKey(pServer);
            final ECIESKEMExtractor myExtractor = new ECIESKEMExtractor(myPrivate.getPrivateKey(), KEYLEN, theDerivation);

            /* Parse clientHello message and store secret */
            final byte[] myMessage = pClientHello.getEncapsulated();
            storeSecret(myExtractor.extractSecret(myMessage));
        }
    }

    /**
     * EC Anonymous.
     */
    public static class BouncyECAnonymousAgreement
            extends GordianCoreAnonymousAgreement {
        /**
         * The agreement.
         */
        private final ECDHCBasicAgreement theAgreement;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         */
        BouncyECAnonymousAgreement(final BouncyFactory pFactory,
                                   final GordianAgreementSpec pSpec) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the agreement */
            theAgreement = new ECDHCBasicAgreement();

            /* Add in the derivation function */
            enableDerivation();
        }

        @Override
        public GordianAgreementMessageASN1 createClientHelloASN1(final GordianKeyPair pServer) throws OceanusException {
            /* Check keyPair */
            BouncyKeyPair.checkKeyPair(pServer);
            checkKeyPair(pServer);

            /* Create an ephemeral keyPair */
            final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
            final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(pServer.getKeyPairSpec());
            final GordianKeyPair myPair = myGenerator.generateKeyPair();
            final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getPrivateKey(myPair);

            /* Create the request  */
            final X509EncodedKeySpec myKeySpec = myGenerator.getX509Encoding(myPair);
            final GordianAgreementMessageASN1 myClientHello = buildClientHelloASN1(myKeySpec);

            /* Derive the secret */
            theAgreement.init(myPrivate.getPrivateKey());
            final BouncyECPublicKey myTarget = (BouncyECPublicKey) getPublicKey(pServer);
            final BigInteger mySecret = theAgreement.calculateAgreement(myTarget.getPublicKey());
            storeSecret(BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(), mySecret));
            return myClientHello;
        }

        @Override
        public void acceptClientHelloASN1(final GordianKeyPair pSelf,
                                          final GordianAgreementMessageASN1 pClientHello) throws OceanusException {
            /* Check keyPair */
            BouncyKeyPair.checkKeyPair(pSelf);
            checkKeyPair(pSelf);

            /* Obtain source keySpec */
            final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getPrivateKey(pSelf);
            final X509EncodedKeySpec myKeySpec = pClientHello.getEphemeral();
            final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
            final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(pSelf.getKeyPairSpec());

            /* Derive partner key */
            final GordianKeyPair myPartner = myGenerator.derivePublicOnlyKeyPair(myKeySpec);
            final BouncyECPublicKey myPublicKey = (BouncyECPublicKey) getPublicKey(myPartner);

            /* Derive the secret */
            theAgreement.init(myPrivate.getPrivateKey());
            final BigInteger mySecret = theAgreement.calculateAgreement(myPublicKey.getPublicKey());

            /* Store secret */
            storeSecret(BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(), mySecret));
        }
    }

    /**
     * EC Basic Agreement.
     */
    public static class BouncyECBasicAgreement
            extends GordianCoreBasicAgreement {
        /**
         * Key Agreement.
         */
        private final ECDHCBasicAgreement theAgreement;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         */
        BouncyECBasicAgreement(final BouncyFactory pFactory,
                               final GordianAgreementSpec pSpec) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Derive the secret */
            theAgreement = new ECDHCBasicAgreement();

            /* Add in the derivation function */
            enableDerivation();
        }

        @Override
        public GordianAgreementMessageASN1 acceptClientHelloASN1(final GordianKeyPair pClient,
                                                                 final GordianKeyPair pServer,
                                                                 final GordianAgreementMessageASN1 pClientHello) throws OceanusException {
            /* Check keyPair */
            BouncyKeyPair.checkKeyPair(pClient);
            checkKeyPair(pClient);
            BouncyKeyPair.checkKeyPair(pServer);
            checkKeyPair(pServer);

            /* Process the clientHello */
            processClientHelloASN1(pServer, pClientHello);
            final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getPrivateKey(pServer);
            final BouncyECPublicKey myPublic = (BouncyECPublicKey) getPublicKey(pClient);

            /* Derive the secret */
            theAgreement.init(myPrivate.getPrivateKey());
            final BigInteger mySecret = theAgreement.calculateAgreement(myPublic.getPublicKey());

            /* Store secret */
            storeSecret(BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(), mySecret));

            /* Return the serverHello */
            return buildServerHello();
        }

        @Override
        public void acceptServerHelloASN1(final GordianKeyPair pServer,
                                          final GordianAgreementMessageASN1 pServerHello) throws OceanusException {
            /* Check keyPair */
            BouncyKeyPair.checkKeyPair(pServer);
            checkKeyPair(pServer);

            /* process the serverHello */
            processServerHelloASN1(pServerHello);
            final BouncyPrivateKey<?> myPrivate = (BouncyPrivateKey<?>) getPrivateKey(getClientKeyPair());

            /* Calculate agreement */
            theAgreement.init(myPrivate.getPrivateKey());
            final BouncyPublicKey<?> mySrcPublic = (BouncyPublicKey<?>) getPublicKey(pServer);
            final BigInteger mySecret = theAgreement.calculateAgreement(mySrcPublic.getPublicKey());
            storeSecret(BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(), mySecret));
        }
    }

    /**
     * EC Signed Agreement.
     */
    public static class BouncyECSignedAgreement
            extends GordianCoreSignedAgreement {
        /**
         * Agreement.
         */
        private final ECDHCBasicAgreement theAgreement;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         */
        BouncyECSignedAgreement(final BouncyFactory pFactory,
                                final GordianAgreementSpec pSpec) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the agreement */
            theAgreement = new ECDHCBasicAgreement();
            enableDerivation();
        }

        @Override
        public GordianAgreementMessageASN1 acceptClientHelloASN1(final GordianKeyPair pServer,
                                                                 final GordianAgreementMessageASN1 pClientHello) throws OceanusException {
            /* Process clientHello */
            BouncyKeyPair.checkKeyPair(pServer);
            processClientHelloASN1(pClientHello);
            final BouncyPrivateKey<?> myPrivate = (BouncyPrivateKey<?>) getPrivateKey(getServerEphemeralKeyPair());
            final BouncyPublicKey<?> myPublic = (BouncyPublicKey<?>) getPublicKey(getClientEphemeralKeyPair());

            /* Derive the secret */
            theAgreement.init(myPrivate.getPrivateKey());
            final BigInteger mySecretInt = theAgreement.calculateAgreement(myPublic.getPublicKey());
            final byte[] mySecret = BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(), mySecretInt);

            /* Store secret */
            storeSecret(mySecret);

            /* Return the serverHello */
            return buildServerHelloASN1(pServer);
        }

        @Override
        public void acceptServerHelloASN1(final GordianKeyPair pServer,
                                          final GordianAgreementMessageASN1 pServerHello) throws OceanusException {
            /* process the serverHello */
            BouncyKeyPair.checkKeyPair(pServer);
            processServerHelloASN1(pServer, pServerHello);
            final BouncyPrivateKey<?> myPrivate = (BouncyPrivateKey<?>) getPrivateKey(getClientEphemeralKeyPair());

            /* Calculate agreement */
            theAgreement.init(myPrivate.getPrivateKey());
            final BouncyPublicKey<?> myPublic = (BouncyPublicKey<?>) getPublicKey(getServerEphemeralKeyPair());
            final BigInteger mySecretInt = theAgreement.calculateAgreement(myPublic.getPublicKey());
            final byte[] mySecret = BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(), mySecretInt);

            /* Store secret */
            storeSecret(mySecret);
        }
    }

    /**
     * EC Unified Agreement.
     */
    public static class BouncyECUnifiedAgreement
            extends GordianCoreEphemeralAgreement {
        /**
         * Key Agreement.
         */
        private final ECDHCUnifiedAgreement theAgreement;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         */
        BouncyECUnifiedAgreement(final BouncyFactory pFactory,
                                 final GordianAgreementSpec pSpec) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create Key Agreement */
            theAgreement = new ECDHCUnifiedAgreement();

            /* Add in the derivation function */
            enableDerivation();
        }

        @Override
        public GordianAgreementMessageASN1 acceptClientHelloASN1(final GordianKeyPair pClient,
                                                                 final GordianKeyPair pServer,
                                                                 final GordianAgreementMessageASN1 pClientHello) throws OceanusException {
            /* process clientHello */
            BouncyKeyPair.checkKeyPair(pClient);
            BouncyKeyPair.checkKeyPair(pServer);
            processClientHelloASN1(pClient, pServer, pClientHello);

            /* Initialise agreement */
            final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getPrivateKey(pServer);
            final BouncyECPrivateKey myEphPrivate = (BouncyECPrivateKey) getPrivateKey(getServerEphemeralKeyPair());
            final BouncyECPublicKey myEphPublic = (BouncyECPublicKey) getPublicKey(getServerEphemeralKeyPair());
            final ECDHUPrivateParameters myPrivParams = new ECDHUPrivateParameters(myPrivate.getPrivateKey(),
                    myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
            theAgreement.init(myPrivParams);

            /* Calculate agreement */
            final BouncyECPublicKey mySrcPublic = (BouncyECPublicKey) getPublicKey(pClient);
            final BouncyECPublicKey mySrcEphPublic = (BouncyECPublicKey) getPublicKey(getClientEphemeralKeyPair());
            final ECDHUPublicParameters myPubParams = new ECDHUPublicParameters(mySrcPublic.getPublicKey(),
                    mySrcEphPublic.getPublicKey());
            storeSecret(theAgreement.calculateAgreement(myPubParams));

            /* Return the serverHello */
            return buildServerHello();
        }

        @Override
        public GordianAgreementMessageASN1 acceptServerHelloASN1(final GordianKeyPair pServer,
                                                                 final GordianAgreementMessageASN1 pServerHello) throws OceanusException {
            /* Check keyPair */
            BouncyKeyPair.checkKeyPair(pServer);
            checkKeyPair(pServer);

            /* process the serverHello */
            processServerHelloASN1(pServer, pServerHello);

            /* Initialise agreement */
            final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getPrivateKey(getClientKeyPair());
            final BouncyECPrivateKey myEphPrivate = (BouncyECPrivateKey) getPrivateKey(getClientEphemeralKeyPair());
            final BouncyECPublicKey myEphPublic = (BouncyECPublicKey) getPublicKey(getClientEphemeralKeyPair());
            final ECDHUPrivateParameters myPrivParams = new ECDHUPrivateParameters(myPrivate.getPrivateKey(),
                    myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
            theAgreement.init(myPrivParams);

            /* Calculate agreement */
            final BouncyECPublicKey mySrcPublic = (BouncyECPublicKey) getPublicKey(pServer);
            final BouncyECPublicKey mySrcEphPublic = (BouncyECPublicKey) getPublicKey(getServerEphemeralKeyPair());
            final ECDHUPublicParameters myPubParams = new ECDHUPublicParameters(mySrcPublic.getPublicKey(),
                    mySrcEphPublic.getPublicKey());
            storeSecret(theAgreement.calculateAgreement(myPubParams));

            /* Return confirmation if needed */
            return buildClientConfirmASN1();
         }
    }

    /**
     * EC MQV Agreement.
     */
    public static class BouncyECMQVAgreement
            extends GordianCoreEphemeralAgreement {
        /**
         * Key Agreement.
         */
        private final ECMQVBasicAgreement theAgreement;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         */
        BouncyECMQVAgreement(final BouncyFactory pFactory,
                             final GordianAgreementSpec pSpec) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create Key Agreement */
            theAgreement = new ECMQVBasicAgreement();

            /* Add in the derivation function */
            enableDerivation();
        }

        @Override
        public GordianAgreementMessageASN1 acceptClientHelloASN1(final GordianKeyPair pClient,
                                                                 final GordianKeyPair pServer,
                                                                 final GordianAgreementMessageASN1 pClientHello) throws OceanusException {
            /* process clientHello */
            BouncyKeyPair.checkKeyPair(pClient);
            BouncyKeyPair.checkKeyPair(pServer);
            processClientHelloASN1(pClient, pServer, pClientHello);

            /* Initialise agreement */
            final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getPrivateKey(pServer);
            final BouncyECPrivateKey myEphPrivate = (BouncyECPrivateKey) getPrivateKey(getServerEphemeralKeyPair());
            final BouncyECPublicKey myEphPublic = (BouncyECPublicKey) getPublicKey(getServerEphemeralKeyPair());
            final MQVPrivateParameters myPrivParams = new MQVPrivateParameters(myPrivate.getPrivateKey(),
                    myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
            theAgreement.init(myPrivParams);

            /* Calculate agreement */
            final BouncyECPublicKey mySrcPublic = (BouncyECPublicKey) getPublicKey(pClient);
            final BouncyECPublicKey mySrcEphPublic = (BouncyECPublicKey) getPublicKey(getClientEphemeralKeyPair());
            final MQVPublicParameters myPubParams = new MQVPublicParameters(mySrcPublic.getPublicKey(),
                    mySrcEphPublic.getPublicKey());
            storeSecret(BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(), theAgreement.calculateAgreement(myPubParams)));

            /* Return the serverHello */
            return buildServerHello();
        }

        @Override
        public GordianAgreementMessageASN1 acceptServerHelloASN1(final GordianKeyPair pServer,
                                                                 final GordianAgreementMessageASN1 pServerHello) throws OceanusException {
            /* Check keyPair */
            BouncyKeyPair.checkKeyPair(pServer);
            checkKeyPair(pServer);

            /* process the serverHello */
            processServerHelloASN1(pServer, pServerHello);

            /* Initialise agreement */
            final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getPrivateKey(getClientKeyPair());
            final BouncyECPrivateKey myEphPrivate = (BouncyECPrivateKey) getPrivateKey(getClientEphemeralKeyPair());
            final BouncyECPublicKey myEphPublic = (BouncyECPublicKey) getPublicKey(getClientEphemeralKeyPair());
            final MQVPrivateParameters myPrivParams = new MQVPrivateParameters(myPrivate.getPrivateKey(),
                    myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
            theAgreement.init(myPrivParams);

            /* Calculate agreement */
            final BouncyECPublicKey mySrcPublic = (BouncyECPublicKey) getPublicKey(pServer);
            final BouncyECPublicKey mySrcEphPublic = (BouncyECPublicKey) getPublicKey(getServerEphemeralKeyPair());
            final MQVPublicParameters myPubParams = new MQVPublicParameters(mySrcPublic.getPublicKey(),
                    mySrcEphPublic.getPublicKey());
            storeSecret(BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(), theAgreement.calculateAgreement(myPubParams)));

            /* Return confirmation if needed */
            return buildClientConfirmASN1();
        }
    }

    /**
     * EC Encryptor.
     */
    public static class BouncyECEncryptor
            extends GordianCoreEncryptor {
        /**
         * The underlying encryptor.
         */
        private final EllipticEncryptor theEncryptor;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the encryptorSpec
         */
        BouncyECEncryptor(final BouncyFactory pFactory,
                          final GordianEncryptorSpec pSpec) {
            /* Initialise underlying cipher */
            super(pFactory, pSpec);
            theEncryptor = new EllipticEncryptor();
        }

        @Override
        protected BouncyPublicKey<?> getPublicKey() {
            return (BouncyPublicKey<?>) super.getPublicKey();
        }

        @Override
        protected BouncyPrivateKey<?> getPrivateKey() {
            return (BouncyPrivateKey<?>) super.getPrivateKey();
        }

        @Override
        public void initForEncrypt(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise underlying cipher */
            BouncyKeyPair.checkKeyPair(pKeyPair);
            super.initForEncrypt(pKeyPair);

            /* Initialise for encryption */
            final ECPublicKeyParameters myParms = (ECPublicKeyParameters) getPublicKey().getPublicKey();
            theEncryptor.initForEncrypt(myParms, getRandom());
        }

        @Override
        public void initForDecrypt(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise underlying cipher */
            BouncyKeyPair.checkKeyPair(pKeyPair);
            super.initForDecrypt(pKeyPair);

            /* Initialise for decryption */
            final ECPrivateKeyParameters myParms = (ECPrivateKeyParameters) getPrivateKey().getPrivateKey();
            theEncryptor.initForDecrypt(myParms);
        }

        @Override
        public byte[] encrypt(final byte[] pBytes) throws OceanusException {
            try {
                /* Check that we are in encryption mode */
                checkMode(GordianEncryptMode.ENCRYPT);

                /* Encrypt the message */
                return theEncryptor.encrypt(pBytes);
            } catch (InvalidCipherTextException e) {
                throw new GordianCryptoException("Failed to process data", e);
            }
        }

        @Override
        public byte[] decrypt(final byte[] pBytes) throws OceanusException {
            try {
                /* Check that we are in decryption mode */
                checkMode(GordianEncryptMode.DECRYPT);

                /* Decrypt the message */
                return theEncryptor.decrypt(pBytes);
            } catch (InvalidCipherTextException e) {
                throw new GordianCryptoException("Failed to process data", e);
            }
        }
    }
}
