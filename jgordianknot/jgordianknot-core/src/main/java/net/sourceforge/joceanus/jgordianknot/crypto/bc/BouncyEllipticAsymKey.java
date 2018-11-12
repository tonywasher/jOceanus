/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto.bc;

import java.io.IOException;
import java.math.BigInteger;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.agreement.ECDHCBasicAgreement;
import org.bouncycastle.crypto.agreement.ECDHCUnifiedAgreement;
import org.bouncycastle.crypto.agreement.ECMQVBasicAgreement;
import org.bouncycastle.crypto.agreement.SM2KeyExchange;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.kems.ECIESKeyEncapsulation;
import org.bouncycastle.crypto.newengines.EllipticEncryptor;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDHUPrivateParameters;
import org.bouncycastle.crypto.params.ECDHUPublicParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.MQVPrivateParameters;
import org.bouncycastle.crypto.params.MQVPublicParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.SM2KeyExchangePrivateParameters;
import org.bouncycastle.crypto.params.SM2KeyExchangePublicParameters;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.util.BigIntegers;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.GordianLogicException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreement.GordianBasicAgreement;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreement.GordianEncapsulationAgreement;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreement.GordianEphemeralAgreement;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianEncryptor;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignature;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncySignature.BouncyDERCoder;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncySignature.BouncyDigestSignature;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * EllipticCurve AsymKey classes.
 */
public final class BouncyEllipticAsymKey {
    /**
     * Private constructor.
     */
    private BouncyEllipticAsymKey() {
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
        BouncyECPublicKey(final GordianAsymKeySpec pKeySpec,
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
        BouncyECPrivateKey(final GordianAsymKeySpec pKeySpec,
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
         * Domain.
         */
        private final ECNamedDomainParameters theDomain;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws OceanusException on error
         */
        BouncyECKeyPairGenerator(final BouncyFactory pFactory,
                                 final GordianAsymKeySpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create the generator */
            final GordianAsymKeyType myType = pKeySpec.getKeyType();
            theGenerator = new ECKeyPairGenerator();
            final String myCurve = pKeySpec.getElliptic().getCurveName();

            /* Lookup the parameters */
            final X9ECParameters x9 = GordianAsymKeyType.SM2.equals(myType)
                                      ? GMNamedCurves.getByName(myCurve)
                                      : ECNamedCurveTable.getByName(myCurve);
            if (x9 == null) {
                throw new GordianLogicException("Invalid KeySpec - " + pKeySpec);
            }

            /* Initialise the generator */
            final ASN1ObjectIdentifier myOid = ECUtil.getNamedCurveOid(myCurve);
            theDomain = new ECNamedDomainParameters(myOid, x9.getCurve(), x9.getG(), x9.getN(), x9.getH(), x9.getSeed());
            final ECKeyGenerationParameters myParams = new ECKeyGenerationParameters(theDomain, getRandom());
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyECPublicKey myPublic = new BouncyECPublicKey(getKeySpec(), (ECPublicKeyParameters) myPair.getPublic());
            final BouncyECPrivateKey myPrivate = new BouncyECPrivateKey(getKeySpec(), (ECPrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
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
            try {
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final ECPrivateKeyParameters myParms = (ECPrivateKeyParameters) PrivateKeyFactory.createKey(myInfo);
                final BouncyECPrivateKey myPrivate = new BouncyECPrivateKey(getKeySpec(), myParms);
                final BouncyECPublicKey myPublic = derivePublicKey(pPublicKey);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
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
            try {
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
            super.initForSigning(pKeyPair);

            /* Initialise and set the signer */
            final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getKeyPair().getPrivateKey();
            final ParametersWithRandom myParms = new ParametersWithRandom(myPrivate.getPrivateKey(), getRandom());
            theSigner.init(true, myParms);
        }

        @Override
        public void initForVerify(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
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
     * SM2 signature.
     */
    public static class BouncySM2Signature
            extends GordianSignature {
        /**
         * The Signer.
         */
        private final SM2Signer theSigner;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error
          */
        BouncySM2Signature(final BouncyFactory pFactory,
                           final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the signer */
            theSigner = new SM2Signer();
        }

        @Override
        public void update(final byte[] pBytes,
                           final int pOffset,
                           final int pLength) {
            theSigner.update(pBytes, pOffset, pLength);
        }

        @Override
        public void update(final byte pByte) {
            theSigner.update(pByte);
        }

        @Override
        public void update(final byte[] pBytes) {
            theSigner.update(pBytes, 0, pBytes.length);
        }

        @Override
        public void reset() {
            theSigner.reset();
        }

        @Override
        protected BouncyKeyPair getKeyPair() {
            return (BouncyKeyPair) super.getKeyPair();
        }

        @Override
        public void initForSigning(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            super.initForSigning(pKeyPair);

            /* Initialise and set the signer */
            final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getKeyPair().getPrivateKey();
            final ParametersWithRandom myParms = new ParametersWithRandom(myPrivate.getPrivateKey(), getRandom());
            theSigner.init(true, myParms);
        }

        @Override
        public void initForVerify(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
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
            try {
                return theSigner.generateSignature();
            } catch (CryptoException e) {
                throw new GordianCryptoException(BouncySignature.ERROR_SIGGEN, e);
            }
        }

        @Override
        public boolean verify(final byte[] pSignature) throws OceanusException {
            /* Check that we are in verify mode */
            checkMode(GordianSignatureMode.VERIFY);

            /* Verify the message */
            return theSigner.verifySignature(pSignature);
        }
    }

    /**
     * ECIES Encapsulation.
     */
    public static class BouncyECIESAgreement
            extends GordianEncapsulationAgreement {
        /**
         * Key Agreement.
         */
        private final ECIESKeyEncapsulation theAgreement;

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
            final GordianNullKeyDerivation myKDF = new GordianNullKeyDerivation();
            theAgreement = new ECIESKeyEncapsulation(myKDF, getRandom(), true, false, false);
        }

        @Override
        public byte[] initiateAgreement(final GordianKeyPair pTarget) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pTarget);

            /* initialise Key Encapsulation */
            final BouncyECPublicKey myPublic = (BouncyECPublicKey) getPublicKey(pTarget);
            theAgreement.init(myPublic.getPublicKey());

             /* Determine key length */
            int myFieldSize = myPublic.getPublicKey().getParameters().getCurve().getFieldSize();
            myFieldSize = (myFieldSize + Byte.SIZE - 1) / Byte.SIZE;
            final int myLen = 2 * myFieldSize + 1;

            /* Create cipherText */
            final byte[] myMessage = new byte[myLen];
            final KeyParameter myParms = (KeyParameter) theAgreement.encrypt(myMessage, 0, myLen);

            /* Store secret */
            storeSecret(myParms.getKey());

            /* Create the message  */
            return createMessage(myMessage);
        }

        @Override
        public void acceptAgreement(final GordianKeyPair pSelf,
                                    final byte[] pMessage) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pSelf);

            /* initialise Key Encapsulation */
            final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getPrivateKey(pSelf);
            theAgreement.init(myPrivate.getPrivateKey());

            /* Parse message */
            final byte[] myMessage = parseMessage(pMessage);
            final KeyParameter myParms = (KeyParameter) theAgreement.decrypt(myMessage, 0, myMessage.length, myMessage.length);

            /* Store secret */
            storeSecret(myParms.getKey());
        }
    }

    /**
     * EC Basic Agreement.
     */
    public static class BouncyECBasicAgreement
            extends GordianBasicAgreement {
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
        }

        @Override
        public byte[] initiateAgreement(final GordianKeyPair pSource,
                                        final GordianKeyPair pTarget) throws OceanusException {
            /* Check keyPairs */
            checkKeyPair(pSource);
            checkKeyPair(pTarget);

            /* Derive the secret */
            final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getPrivateKey(pSource);
            theAgreement.init(myPrivate.getPrivateKey());
            final BouncyECPublicKey myTarget = (BouncyECPublicKey) getPublicKey(pTarget);
            final BigInteger mySecret = theAgreement.calculateAgreement(myTarget.getPublicKey());
            storeSecret(BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(), mySecret));

            /* Create the message  */
            return createMessage();
        }

        @Override
        public void acceptAgreement(final GordianKeyPair pSource,
                                    final GordianKeyPair pSelf,
                                    final byte[] pMessage) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pSource);
            checkKeyPair(pSelf);

            /* Determine initVector */
            parseMessage(pMessage);
            final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getPrivateKey(pSelf);
            final BouncyECPublicKey myPublic = (BouncyECPublicKey) getPublicKey(pSource);

            /* Derive the secret */
            theAgreement.init(myPrivate.getPrivateKey());
            final BigInteger mySecret = theAgreement.calculateAgreement(myPublic.getPublicKey());

            /* Store secret */
            storeSecret(BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(), mySecret));
        }
    }

    /**
     * EC Unified Agreement.
     */
    public static class BouncyECUnifiedAgreement
            extends GordianEphemeralAgreement {
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
        }

        @Override
        public byte[] acceptAgreement(final GordianKeyPair pSource,
                                      final GordianKeyPair pResponder,
                                      final byte[] pMessage) throws OceanusException {
            /* process message */
            final byte[] myResponse = parseMessage(pResponder, pMessage);

             /* Initialise agreement */
            final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getPrivateKey(pResponder);
            final BouncyECPrivateKey myEphPrivate = (BouncyECPrivateKey) getPrivateKey(getEphemeralKeyPair());
            final BouncyECPublicKey myEphPublic = (BouncyECPublicKey) getPublicKey(getEphemeralKeyPair());
            final ECDHUPrivateParameters myPrivParams = new ECDHUPrivateParameters(myPrivate.getPrivateKey(),
                    myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
            theAgreement.init(myPrivParams);

            /* Calculate agreement */
            final BouncyECPublicKey mySrcPublic = (BouncyECPublicKey) getPublicKey(pSource);
            final BouncyECPublicKey mySrcEphPublic = (BouncyECPublicKey) getPublicKey(getPartnerEphemeralKeyPair());
            final ECDHUPublicParameters myPubParams = new ECDHUPublicParameters(mySrcPublic.getPublicKey(),
                    mySrcEphPublic.getPublicKey());
            storeSecret(theAgreement.calculateAgreement(myPubParams));

            /* Return the response */
            return myResponse;
        }

        @Override
        public void confirmAgreement(final GordianKeyPair pResponder,
                                     final byte[] pMessage) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pResponder);

            /* parse the ephemeral message */
            parseEphemeral(pMessage);

            /* Initialise agreement */
            final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getPrivateKey(getOwnerKeyPair());
            final BouncyECPrivateKey myEphPrivate = (BouncyECPrivateKey) getPrivateKey(getEphemeralKeyPair());
            final BouncyECPublicKey myEphPublic = (BouncyECPublicKey) getPublicKey(getEphemeralKeyPair());
            final ECDHUPrivateParameters myPrivParams = new ECDHUPrivateParameters(myPrivate.getPrivateKey(),
                    myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
            theAgreement.init(myPrivParams);

            /* Calculate agreement */
            final BouncyECPublicKey mySrcPublic = (BouncyECPublicKey) getPublicKey(pResponder);
            final BouncyECPublicKey mySrcEphPublic = (BouncyECPublicKey) getPublicKey(getPartnerEphemeralKeyPair());
            final ECDHUPublicParameters myPubParams = new ECDHUPublicParameters(mySrcPublic.getPublicKey(),
                    mySrcEphPublic.getPublicKey());
            storeSecret(theAgreement.calculateAgreement(myPubParams));
        }
    }

    /**
     * EC MQV Agreement.
     */
    public static class BouncyECMQVAgreement
            extends GordianEphemeralAgreement {
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
        }

        @Override
        public byte[] acceptAgreement(final GordianKeyPair pSource,
                                      final GordianKeyPair pResponder,
                                      final byte[] pMessage) throws OceanusException {
            /* process message */
            final byte[] myResponse = parseMessage(pResponder, pMessage);

            /* Initialise agreement */
            final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getPrivateKey(pResponder);
            final BouncyECPrivateKey myEphPrivate = (BouncyECPrivateKey) getPrivateKey(getEphemeralKeyPair());
            final BouncyECPublicKey myEphPublic = (BouncyECPublicKey) getPublicKey(getEphemeralKeyPair());
            final MQVPrivateParameters myPrivParams = new MQVPrivateParameters(myPrivate.getPrivateKey(),
                    myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
            theAgreement.init(myPrivParams);

            /* Calculate agreement */
            final BouncyECPublicKey mySrcPublic = (BouncyECPublicKey) getPublicKey(pSource);
            final BouncyECPublicKey mySrcEphPublic = (BouncyECPublicKey) getPublicKey(getPartnerEphemeralKeyPair());
            final MQVPublicParameters myPubParams = new MQVPublicParameters(mySrcPublic.getPublicKey(),
                    mySrcEphPublic.getPublicKey());
            storeSecret(BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(), theAgreement.calculateAgreement(myPubParams)));

            /* Return the response */
            return myResponse;
        }

        @Override
        public void confirmAgreement(final GordianKeyPair pResponder,
                                     final byte[] pMessage) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pResponder);

            /* parse the ephemeral message */
            parseEphemeral(pMessage);

            /* Initialise agreement */
            final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getPrivateKey(getOwnerKeyPair());
            final BouncyECPrivateKey myEphPrivate = (BouncyECPrivateKey) getPrivateKey(getEphemeralKeyPair());
            final BouncyECPublicKey myEphPublic = (BouncyECPublicKey) getPublicKey(getEphemeralKeyPair());
            final MQVPrivateParameters myPrivParams = new MQVPrivateParameters(myPrivate.getPrivateKey(),
                    myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
            theAgreement.init(myPrivParams);

            /* Calculate agreement */
            final BouncyECPublicKey mySrcPublic = (BouncyECPublicKey) getPublicKey(pResponder);
            final BouncyECPublicKey mySrcEphPublic = (BouncyECPublicKey) getPublicKey(getPartnerEphemeralKeyPair());
            final MQVPublicParameters myPubParams = new MQVPublicParameters(mySrcPublic.getPublicKey(),
                    mySrcEphPublic.getPublicKey());
            storeSecret(BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(), theAgreement.calculateAgreement(myPubParams)));
        }
    }

    /**
     * EC SM2 Agreement.
     */
    public static class BouncyECSM2Agreement
            extends GordianEphemeralAgreement {
        /**
         * Key length.
         */
        private static final int KEYLEN = 32;

        /**
         * Key Agreement.
         */
        private final SM2KeyExchange theAgreement;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         */
        BouncyECSM2Agreement(final BouncyFactory pFactory,
                             final GordianAgreementSpec pSpec) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the agreement */
            theAgreement = new SM2KeyExchange();
        }

        @Override
        public byte[] acceptAgreement(final GordianKeyPair pSource,
                                      final GordianKeyPair pResponder,
                                      final byte[] pMessage) throws OceanusException {
            /* process message */
            final byte[] myResponse = parseMessage(pResponder, pMessage);

            /* Initialise agreement */
            final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getPrivateKey(pResponder);
            final BouncyECPrivateKey myEphPrivate = (BouncyECPrivateKey) getPrivateKey(getEphemeralKeyPair());
            final SM2KeyExchangePrivateParameters myPrivParams = new SM2KeyExchangePrivateParameters(false,
                    myPrivate.getPrivateKey(), myEphPrivate.getPrivateKey());
            theAgreement.init(myPrivParams);

            /* Calculate agreement */
            final BouncyECPublicKey mySrcPublic = (BouncyECPublicKey) getPublicKey(pSource);
            final BouncyECPublicKey mySrcEphPublic = (BouncyECPublicKey) getPublicKey(getPartnerEphemeralKeyPair());
            final SM2KeyExchangePublicParameters myPubParams = new SM2KeyExchangePublicParameters(mySrcPublic.getPublicKey(),
                    mySrcEphPublic.getPublicKey());
            storeSecret(theAgreement.calculateKey(KEYLEN, myPubParams));

            /* Return the response */
            return myResponse;
        }

        @Override
        public void confirmAgreement(final GordianKeyPair pResponder,
                                     final byte[] pMessage) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pResponder);

            /* parse the ephemeral message */
            parseEphemeral(pMessage);

            /* Initialise agreement */
            final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) getPrivateKey(getOwnerKeyPair());
            final BouncyECPrivateKey myEphPrivate = (BouncyECPrivateKey) getPrivateKey(getEphemeralKeyPair());
            final SM2KeyExchangePrivateParameters myPrivParams = new SM2KeyExchangePrivateParameters(true,
                    myPrivate.getPrivateKey(), myEphPrivate.getPrivateKey());
            theAgreement.init(myPrivParams);

            /* Calculate agreement */
            final BouncyECPublicKey mySrcPublic = (BouncyECPublicKey) getPublicKey(pResponder);
            final BouncyECPublicKey mySrcEphPublic = (BouncyECPublicKey) getPublicKey(getPartnerEphemeralKeyPair());
            final SM2KeyExchangePublicParameters myPubParams = new SM2KeyExchangePublicParameters(mySrcPublic.getPublicKey(),
                    mySrcEphPublic.getPublicKey());
            storeSecret(theAgreement.calculateKey(KEYLEN, myPubParams));
        }
    }


    /**
     * EC Encryptor.
     */
    public static class BouncyECEncryptor
            extends GordianEncryptor {
        /**
         * The underlying encryptor.
         */
        private final EllipticEncryptor theEncryptor;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the encryptorSpec
         * @throws OceanusException on error
         */
        BouncyECEncryptor(final BouncyFactory pFactory,
                          final GordianEncryptorSpec pSpec) throws OceanusException {
            /* Initialise underlying cipher */
            super(pFactory, pSpec);
            theEncryptor = new EllipticEncryptor();
        }

        @Override
        protected BouncyPublicKey getPublicKey() {
            return (BouncyPublicKey) super.getPublicKey();
        }

        @Override
        protected BouncyPrivateKey getPrivateKey() {
            return (BouncyPrivateKey) super.getPrivateKey();
        }

        @Override
        public void initForEncrypt(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise underlying cipher */
            super.initForEncrypt(pKeyPair);

            /* Initialise for encryption */
            final ECPublicKeyParameters myParms = (ECPublicKeyParameters) getPublicKey().getPublicKey();
            theEncryptor.initForEncrypt(myParms, getRandom());
        }

        @Override
        public void initForDecrypt(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise underlying cipher */
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

    /**
     * SM2 Encryptor.
     */
    public static class BouncySM2Encryptor
            extends GordianEncryptor {
        /**
         * The underlying encryptor.
         */
        private final SM2Engine theEncryptor;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the encryptorSpec
         */
        BouncySM2Encryptor(final BouncyFactory pFactory,
                           final GordianEncryptorSpec pSpec) {
            /* Initialise underlying cipher */
            super(pFactory, pSpec);
            theEncryptor = new SM2Engine();
        }

        @Override
        protected BouncyPublicKey getPublicKey() {
            return (BouncyPublicKey) super.getPublicKey();
        }

        @Override
        protected BouncyPrivateKey getPrivateKey() {
            return (BouncyPrivateKey) super.getPrivateKey();
        }

        @Override
        public void initForEncrypt(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise underlying cipher */
            super.initForEncrypt(pKeyPair);

            /* Initialise for encryption */
            final ParametersWithRandom myParms = new ParametersWithRandom(getPublicKey().getPublicKey(), getRandom());
            theEncryptor.init(true, myParms);
        }

        @Override
        public void initForDecrypt(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise underlying cipher */
            super.initForDecrypt(pKeyPair);

            /* Initialise for decryption */
            theEncryptor.init(false, getPrivateKey().getPrivateKey());
        }

        @Override
        public byte[] encrypt(final byte[] pBytes) throws OceanusException {
            try {
                /* Check that we are in encryption mode */
                checkMode(GordianEncryptMode.ENCRYPT);

                /* Encrypt the message */
                return theEncryptor.processBlock(pBytes, 0, pBytes.length);
            } catch (InvalidCipherTextException e) {
                throw new GordianCryptoException("Failed to encrypt data", e);
            }
        }

        @Override
        public byte[] decrypt(final byte[] pBytes) throws OceanusException {
            try {
                /* Check that we are in decryption mode */
                checkMode(GordianEncryptMode.DECRYPT);

                /* Decrypt the message */
                return theEncryptor.processBlock(pBytes, 0, pBytes.length);
            } catch (InvalidCipherTextException e) {
                throw new GordianCryptoException("Failed to decrypt data", e);
            }
        }
    }
}
