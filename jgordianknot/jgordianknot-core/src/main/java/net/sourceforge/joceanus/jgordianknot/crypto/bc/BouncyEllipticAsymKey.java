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
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.ua.DSTU4145NamedCurves;
import org.bouncycastle.asn1.ua.UAObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.generators.DSTU4145KeyPairGenerator;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.kems.ECIESKeyEncapsulation;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.math.ec.ECCurve;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.GordianLogicException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianConsumer;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation.GordianKEMSender;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianLength;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSigner;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianValidator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyEncapsulation.BouncyKeyDerivation;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncySignature.BouncyDSACoder;
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
            extends BouncyPublicKey {
        /**
         * Public Key details.
         */
        private final ECPublicKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        protected BouncyECPublicKey(final GordianAsymKeySpec pKeySpec,
                                    final ECPublicKeyParameters pPublicKey) {
            super(pKeySpec);
            theKey = pPublicKey;
        }

        /**
         * Obtain the public key.
         * @return the key
         */
        protected ECPublicKeyParameters getPublicKey() {
            return theKey;
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle the trivial cases */
            if (pThat == this) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Make sure that the object is the same class */
            if (!(pThat instanceof BouncyECPublicKey)) {
                return false;
            }

            /* Access the target field */
            final BouncyECPublicKey myThat = (BouncyECPublicKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(theKey, myThat.getPublicKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
        }

        /**
         * Is the private key valid for this public key?
         * @param pPrivate the private key
         * @return true/false
         */
        public boolean validPrivate(final BouncyECPrivateKey pPrivate) {
            final ECPrivateKeyParameters myPrivate = pPrivate.getPrivateKey();
            return theKey.getParameters().equals(myPrivate.getParameters());
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
            extends BouncyPrivateKey {
        /**
         * Private Key details.
         */
        private final ECPrivateKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        protected BouncyECPrivateKey(final GordianAsymKeySpec pKeySpec,
                                     final ECPrivateKeyParameters pPrivateKey) {
            super(pKeySpec);
            theKey = pPrivateKey;
        }

        /**
         * Obtain the private key.
         * @return the key
         */
        protected ECPrivateKeyParameters getPrivateKey() {
            return theKey;
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle the trivial cases */
            if (pThat == this) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Make sure that the object is the same class */
            if (!(pThat instanceof BouncyECPrivateKey)) {
                return false;
            }

            /* Access the target field */
            final BouncyECPrivateKey myThat = (BouncyECPrivateKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(theKey, myThat.getPrivateKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
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
         * Curve.
         */
        private final String theCurve;

        /**
         * Generator.
         */
        private final ECKeyPairGenerator theGenerator;

        /**
         * Domain.
         */
        private final ECDomainParameters theDomain;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws OceanusException on error
         */
        protected BouncyECKeyPairGenerator(final BouncyFactory pFactory,
                                           final GordianAsymKeySpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create the generator */
            final GordianAsymKeyType myType = pKeySpec.getKeyType();
            theGenerator = GordianAsymKeyType.DSTU4145.equals(myType)
                                                                      ? new DSTU4145KeyPairGenerator()
                                                                      : new ECKeyPairGenerator();
            theCurve = pKeySpec.getElliptic().getCurveName();

            /* Lookup the parameters */
            final X9ECParameters x9 = getDomainParameters();
            if (x9 == null) {
                throw new GordianLogicException("Invalid KeySpec - " + pKeySpec);
            }

            /* Initialise the generator */
            theDomain = new ECDomainParameters(x9.getCurve(), x9.getG(), x9.getN(), x9.getH(), x9.getSeed());
            final ECKeyGenerationParameters myParams = new ECKeyGenerationParameters(theDomain, getRandom());
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyECPublicKey myPublic = new BouncyECPublicKey(getKeySpec(), ECPublicKeyParameters.class.cast(myPair.getPublic()));
            final BouncyECPrivateKey myPrivate = new BouncyECPrivateKey(getKeySpec(), ECPrivateKeyParameters.class.cast(myPair.getPrivate()));
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        protected PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            final BouncyECPrivateKey myPrivateKey = BouncyECPrivateKey.class.cast(getPrivateKey(pKeyPair));
            final ECPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
            final X962Parameters myX962Parms = new X962Parameters(ECUtil.getNamedCurveOid(theCurve));
            final BigInteger myOrder = myParms.getParameters().getCurve().getOrder();
            final ECPrivateKey myKey = new ECPrivateKey(myOrder.bitLength(), myParms.getD(), myX962Parms);
            final byte[] myBytes = KeyUtil.getEncodedPrivateKeyInfo(new AlgorithmIdentifier(getObjectIdentifier(), myX962Parms.toASN1Primitive()),
                    myKey.toASN1Primitive());
            return new PKCS8EncodedKeySpec(myBytes);
        }

        @Override
        protected BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                              final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            try {
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final ECPrivateKey myKey = ECPrivateKey.getInstance(myInfo.parsePrivateKey());
                final ECPrivateKeyParameters myParms = new ECPrivateKeyParameters(myKey.getKey(), theDomain);
                final BouncyECPrivateKey myPrivate = new BouncyECPrivateKey(getKeySpec(), myParms);
                final BouncyECPublicKey myPublic = derivePublicKey(pPublicKey);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            final BouncyECPublicKey myPublicKey = BouncyECPublicKey.class.cast(getPublicKey(pKeyPair));
            final ECPublicKeyParameters myParms = myPublicKey.getPublicKey();
            final X962Parameters myX962Parms = new X962Parameters(ECUtil.getNamedCurveOid(theCurve));
            final ECCurve myCurve = theDomain.getCurve();
            final ASN1OctetString p = (ASN1OctetString) new X9ECPoint(myCurve.createPoint(myParms.getQ().getAffineXCoord().toBigInteger(),
                    myParms.getQ().getAffineYCoord().toBigInteger())).toASN1Primitive();
            final SubjectPublicKeyInfo myInfo = new SubjectPublicKeyInfo(new AlgorithmIdentifier(getObjectIdentifier(), myX962Parms), p.getOctets());
            final byte[] myBytes = KeyUtil.getEncodedSubjectPublicKeyInfo(myInfo);
            return new X509EncodedKeySpec(myBytes);
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
         */
        private BouncyECPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) {
            final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
            final X9ECPoint myKey = new X9ECPoint(theDomain.getCurve(), new DEROctetString(myInfo.getPublicKeyData().getBytes()));
            final ECPublicKeyParameters myParms = new ECPublicKeyParameters(myKey.getPoint(), theDomain);
            return new BouncyECPublicKey(getKeySpec(), myParms);
        }

        /**
         * Look up parameters.
         * @return the domain parameters
         */
        private ASN1ObjectIdentifier getObjectIdentifier() {
            final GordianAsymKeySpec myKeySpec = getKeySpec();
            switch (myKeySpec.getKeyType()) {
                case GOST2012:
                    return myKeySpec.getElliptic().getKeySize() > GordianLength.LEN_256.getLength()
                                                                                                    ? RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512
                                                                                                    : RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256;
                case DSTU4145:
                    return UAObjectIdentifiers.dstu4145be;
                case SM2:
                case EC:
                default:
                    return X9ObjectIdentifiers.id_ecPublicKey;
            }
        }

        /**
         * Look up parameters.
         * @return the domain parameters
         */
        private X9ECParameters getDomainParameters() {
            switch (getKeySpec().getKeyType()) {
                case GOST2012:
                    return fromDomainParameters(ECGOST3410NamedCurves.getByName(theCurve));
                case DSTU4145:
                    return fromDomainParameters(DSTU4145NamedCurves.getByOID(new ASN1ObjectIdentifier(theCurve)));
                case SM2:
                    return GMNamedCurves.getByName(theCurve);
                case EC:
                default:
                    return ECNamedCurveTable.getByName(theCurve);
            }
        }

        /**
         * Convert from ECDomain parameters to X9EC parameters.
         * @param pECParms the EC domain parameters
         * @return X9EC parameters
         */
        private static X9ECParameters fromDomainParameters(final ECDomainParameters pECParms) {
            return pECParms == null
                                    ? null
                                    : new X9ECParameters(pECParms.getCurve(), pECParms.getG(), pECParms.getN(), pECParms.getH(), pECParms.getSeed());
        }
    }

    /**
     * EC signer.
     */
    public static class BouncyECSigner
            extends BouncyDigestSignature
            implements GordianSigner {
        /**
         * The Signer.
         */
        private final DSA theSigner;

        /**
         * The Coder.
         */
        private final BouncyDSACoder theCoder;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pPrivateKey the private key
         * @param pSpec the signatureSpec.
         * @param pRandom the random generator
         * @throws OceanusException on error
         */
        protected BouncyECSigner(final BouncyFactory pFactory,
                                 final BouncyECPrivateKey pPrivateKey,
                                 final GordianSignatureSpec pSpec,
                                 final SecureRandom pRandom) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the signer and Coder */
            theSigner = BouncySignature.getDSASigner(pFactory, pPrivateKey.getKeySpec(), pSpec);
            theCoder = BouncySignature.getDSACoder(pPrivateKey.getKeySpec());

            /* Initialise and set the signer */
            final ParametersWithRandom myParms = new ParametersWithRandom(pPrivateKey.getPrivateKey(), pRandom);
            theSigner.init(true, myParms);
        }

        @Override
        public byte[] sign() throws OceanusException {
            final BigInteger[] myValues = theSigner.generateSignature(getDigest());
            return theCoder.dsaEncode(myValues[0], myValues[1]);
        }
    }

    /**
     * EC validator.
     */
    public static class BouncyECValidator
            extends BouncyDigestSignature
            implements GordianValidator {
        /**
         * The EC Signer.
         */
        private final DSA theSigner;

        /**
         * The Coder.
         */
        private final BouncyDSACoder theCoder;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pPublicKey the public key
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error-
         */
        protected BouncyECValidator(final BouncyFactory pFactory,
                                    final BouncyECPublicKey pPublicKey,
                                    final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the signer */
            theSigner = BouncySignature.getDSASigner(pFactory, pPublicKey.getKeySpec(), pSpec);
            theCoder = BouncySignature.getDSACoder(pPublicKey.getKeySpec());

            /* Initialise and set the signer */
            theSigner.init(false, pPublicKey.getPublicKey());
        }

        @Override
        public boolean verify(final byte[] pSignature) throws OceanusException {
            final BigInteger[] myValues = theCoder.dsaDecode(pSignature);
            return theSigner.verifySignature(getDigest(), myValues[0], myValues[1]);
        }
    }

    /**
     * SM2 signature base.
     */
    private abstract static class BouncySM2Signature
            implements GordianConsumer {
        /**
         * The Signer.
         */
        private final SM2Signer theSigner;

        /**
         * Constructor.
         * @throws OceanusException on error
         */
        protected BouncySM2Signature() {
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

        /**
         * Obtain the signer.
         * @return the signer
         */
        protected SM2Signer getSigner() {
            return theSigner;
        }
    }

    /**
     * SM2 signer.
     */
    public static class BouncySM2Signer
            extends BouncySM2Signature
            implements GordianSigner {
        /**
         * Constructor.
         * @param pFactory the factory
         * @param pPrivateKey the private key
         * @param pRandom the random generator
         */
        protected BouncySM2Signer(final BouncyFactory pFactory,
                                  final BouncyECPrivateKey pPrivateKey,
                                  final SecureRandom pRandom) {
            /* Initialise and set the signer */
            final ParametersWithRandom myParms = new ParametersWithRandom(pPrivateKey.getPrivateKey(), pRandom);
            getSigner().init(true, myParms);
        }

        @Override
        public byte[] sign() throws OceanusException {
            try {
                return getSigner().generateSignature();
            } catch (CryptoException e) {
                throw new GordianCryptoException(BouncySignature.ERROR_SIGGEN, e);
            }
        }
    }

    /**
     * SM2 validator.
     */
    public static class BouncySM2Validator
            extends BouncySM2Signature
            implements GordianValidator {
        /**
         * Constructor.
         * @param pFactory the factory
         * @param pPublicKey the public key
         */
        protected BouncySM2Validator(final BouncyFactory pFactory,
                                     final BouncyECPublicKey pPublicKey) {
            /* Initialise and set the signer */
            getSigner().init(false, pPublicKey.getPublicKey());
        }

        @Override
        public boolean verify(final byte[] pSignature) {
            return getSigner().verifySignature(pSignature);
        }
    }

    /**
     * ClientECIES Encapsulation.
     */
    public static class BouncyECIESSender
            extends GordianKEMSender {
        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pPublicKey the target publicKey
         * @param pDigestSpec the digestSpec
         * @throws OceanusException on error
         */
        protected BouncyECIESSender(final BouncyFactory pFactory,
                                    final BouncyECPublicKey pPublicKey,
                                    final GordianDigestSpec pDigestSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory);

            /* Create Key Encapsulation */
            final BouncyKeyDerivation myKDF = new BouncyKeyDerivation(getDigest(pDigestSpec));
            final ECIESKeyEncapsulation myKEMS = new ECIESKeyEncapsulation(myKDF, getRandom());

            /* Initialise the encapsulation */
            myKEMS.init(pPublicKey.getPublicKey());

            /* Create initVector */
            final byte[] myInitVector = new byte[INITLEN];
            getRandom().nextBytes(myInitVector);

            /* Determine cipher text length */
            int myFieldSize = pPublicKey.getPublicKey().getParameters().getCurve().getFieldSize();
            myFieldSize = (myFieldSize + Byte.SIZE - 1) / Byte.SIZE;
            final int myLen = 2 * myFieldSize + 1;

            /* Create cipherText */
            final byte[] myCipherText = new byte[myLen + INITLEN];
            final KeyParameter myParms = (KeyParameter) myKEMS.encrypt(myCipherText, INITLEN, myKDF.getKeyLen());
            System.arraycopy(myInitVector, 0, myCipherText, 0, INITLEN);

            /* Store secret and cipherText */
            storeSecret(myParms.getKey(), myInitVector);
            storeCipherText(myCipherText);
        }
    }

    /**
     * ServerECIES Encapsulation.
     */
    public static class BouncyECIESReceiver
            extends GordianKeyEncapsulation {
        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pPrivateKey the target privateKey
         * @param pDigestSpec the digestSpec
         * @param pCipherText the cipherText
         * @throws OceanusException on error
         */
        protected BouncyECIESReceiver(final BouncyFactory pFactory,
                                      final BouncyECPrivateKey pPrivateKey,
                                      final GordianDigestSpec pDigestSpec,
                                      final byte[] pCipherText) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory);

            /* Create Key Encapsulation */
            final BouncyKeyDerivation myKDF = new BouncyKeyDerivation(getDigest(pDigestSpec));
            final ECIESKeyEncapsulation myKEMS = new ECIESKeyEncapsulation(myKDF, null);

            /* Initialise the encapsulation */
            myKEMS.init(pPrivateKey.getPrivateKey());

            /* Obtain initVector */
            final byte[] myInitVector = new byte[INITLEN];
            System.arraycopy(pCipherText, 0, myInitVector, 0, INITLEN);

            /* Parse cipherText */
            final KeyParameter myParms = (KeyParameter) myKEMS.decrypt(pCipherText, INITLEN, pCipherText.length - INITLEN, myKDF.getKeyLen());

            /* Store secret */
            storeSecret(myParms.getKey(), myInitVector);
        }
    }
}
