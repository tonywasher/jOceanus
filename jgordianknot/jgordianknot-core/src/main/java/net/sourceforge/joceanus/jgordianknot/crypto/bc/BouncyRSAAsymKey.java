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

import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.kems.RSAKeyEncapsulation;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.signers.ISO9796d2Signer;
import org.bouncycastle.crypto.signers.PSSSigner;
import org.bouncycastle.crypto.signers.X931Signer;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation.GordianKEMSender;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianModulus;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignature;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyEncapsulation.BouncyKeyDerivation;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * RSA AsymKey classes.
 */
public final class BouncyRSAAsymKey {
    /**
     * Private constructor.
     */
    private BouncyRSAAsymKey() {
    }

    /**
     * Bouncy RSA PublicKey.
     */
    public static class BouncyRSAPublicKey
            extends BouncyPublicKey {
        /**
         * Public Key details.
         */
        private final RSAKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        BouncyRSAPublicKey(final GordianAsymKeySpec pKeySpec,
                           final RSAKeyParameters pPublicKey) {
            super(pKeySpec);
            theKey = pPublicKey;
        }

        /**
         * Obtain the public key.
         * @return the key
         */
        protected RSAKeyParameters getPublicKey() {
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
            if (!(pThat instanceof BouncyRSAPublicKey)) {
                return false;
            }

            /* Access the target field */
            final BouncyRSAPublicKey myThat = (BouncyRSAPublicKey) pThat;

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
        public boolean validPrivate(final BouncyRSAPrivateKey pPrivate) {
            final RSAPrivateCrtKeyParameters myPrivate = pPrivate.getPrivateKey();
            return theKey.getExponent().equals(myPrivate.getExponent())
                   && theKey.getModulus().equals(myPrivate.getModulus());

        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final RSAKeyParameters pFirst,
                                           final RSAKeyParameters pSecond) {
            return pFirst.getExponent().equals(pSecond.getExponent())
                   && pFirst.getModulus().equals(pSecond.getModulus());
        }
    }

    /**
     * Bouncy RSA PrivateKey.
     */
    public static class BouncyRSAPrivateKey
            extends BouncyPrivateKey {
        /**
         * Private Key details.
         */
        private final RSAPrivateCrtKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        BouncyRSAPrivateKey(final GordianAsymKeySpec pKeySpec,
                            final RSAPrivateCrtKeyParameters pPrivateKey) {
            super(pKeySpec);
            theKey = pPrivateKey;
        }

        /**
         * Obtain the private key.
         * @return the key
         */
        protected RSAPrivateCrtKeyParameters getPrivateKey() {
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
            if (!(pThat instanceof BouncyRSAPrivateKey)) {
                return false;
            }

            /* Access the target field */
            final BouncyRSAPrivateKey myThat = (BouncyRSAPrivateKey) pThat;

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
        private static boolean compareKeys(final RSAPrivateCrtKeyParameters pFirst,
                                           final RSAPrivateCrtKeyParameters pSecond) {
            if (!pFirst.getExponent().equals(pSecond.getExponent())
                || !pFirst.getModulus().equals(pSecond.getModulus())) {
                return false;
            }

            if (!pFirst.getP().equals(pSecond.getP())
                || !pFirst.getQ().equals(pSecond.getQ())) {
                return false;
            }

            if (!pFirst.getDP().equals(pSecond.getDP())
                || !pFirst.getDQ().equals(pSecond.getDQ())) {
                return false;
            }

            return pFirst.getPublicExponent().equals(pSecond.getPublicExponent())
                   && pFirst.getQInv().equals(pSecond.getQInv());
        }
    }

    /**
     * BouncyCastle RSA KeyPair generator.
     */
    public static class BouncyRSAKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * RSA exponent.
         */
        private static final BigInteger RSA_EXPONENT = new BigInteger("10001", 16);

        /**
         * Generator.
         */
        private final RSAKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyRSAKeyPairGenerator(final BouncyFactory pFactory,
                                  final GordianAsymKeySpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = new RSAKeyPairGenerator();
            final RSAKeyGenerationParameters myParams = new RSAKeyGenerationParameters(RSA_EXPONENT, getRandom(), pKeySpec.getModulus().getModulus(), PRIME_CERTAINTY);
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyRSAPublicKey myPublic = new BouncyRSAPublicKey(getKeySpec(), RSAKeyParameters.class.cast(myPair.getPublic()));
            final BouncyRSAPrivateKey myPrivate = new BouncyRSAPrivateKey(getKeySpec(), RSAPrivateCrtKeyParameters.class.cast(myPair.getPrivate()));
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            final BouncyRSAPrivateKey myPrivateKey = BouncyRSAPrivateKey.class.cast(getPrivateKey(pKeyPair));
            final RSAPrivateCrtKeyParameters myParms = myPrivateKey.getPrivateKey();
            final byte[] myBytes = KeyUtil.getEncodedPrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE),
                    new RSAPrivateKey(myParms.getModulus(), myParms.getPublicExponent(), myParms.getExponent(),
                            myParms.getP(), myParms.getQ(), myParms.getDP(), myParms.getDQ(), myParms.getQInv()));
            return new PKCS8EncodedKeySpec(myBytes);
        }

        @Override
        public BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                           final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            try {
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final RSAPrivateKey myKey = RSAPrivateKey.getInstance(myInfo.parsePrivateKey());
                final RSAPrivateCrtKeyParameters myParms = new RSAPrivateCrtKeyParameters(myKey.getModulus(), myKey.getPublicExponent(), myKey.getPrivateExponent(),
                        myKey.getPrime1(), myKey.getPrime2(), myKey.getExponent1(), myKey.getExponent2(), myKey.getCoefficient());
                final BouncyRSAPrivateKey myPrivate = new BouncyRSAPrivateKey(getKeySpec(), myParms);
                final BouncyRSAPublicKey myPublic = derivePublicKey(pPublicKey);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            final BouncyRSAPublicKey myPublicKey = BouncyRSAPublicKey.class.cast(getPublicKey(pKeyPair));
            final RSAKeyParameters myParms = myPublicKey.getPublicKey();
            final byte[] myBytes = KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE),
                    new RSAPublicKey(myParms.getModulus(), myParms.getExponent()));
            return new X509EncodedKeySpec(myBytes);
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            final BouncyRSAPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws OceanusException on error
         */
        private BouncyRSAPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            try {
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final RSAPublicKey myKey = RSAPublicKey.getInstance(myInfo.parsePublicKey());
                final RSAKeyParameters myParms = new RSAKeyParameters(false, myKey.getModulus(), myKey.getPublicExponent());
                return new BouncyRSAPublicKey(getKeySpec(), myParms);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * PSS signature base.
     */
    private abstract static class BouncyPSSSignature
            extends GordianSignature {
        /**
         * MGF1 Salt length.
         */
        private static final int MGF1_SALTLEN = 64;

        /**
         * The RSA Signer.
         */
        private final Signer theSigner;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error
         */
        BouncyPSSSignature(final BouncyFactory pFactory,
                           final GordianSignatureSpec pSpec) throws OceanusException {
            super(pFactory, pSpec);
            theSigner = getRSASigner(pFactory, pSpec);
        }

        /**
         * Obtain the signer.
         * @return the signer.
         */
        protected Signer getSigner() {
            return theSigner;
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
         * Obtain RSASigner.
         * @param pFactory the factory
         * @param pSpec the signatureSpec
         * @return the RSASigner
         * @throws OceanusException on error
         */
        private static Signer getRSASigner(final BouncyFactory pFactory,
                                           final GordianSignatureSpec pSpec) throws OceanusException {
            /* Create the digest */
            final BouncyDigest myDigest = pFactory.createDigest(pSpec.getDigestSpec());

            /* Access the signature type */
            switch (pSpec.getSignatureType()) {
                case ISO9796D2:
                    return new ISO9796d2Signer(new RSABlindedEngine(), myDigest.getDigest(), true);
                case X931:
                    return new X931Signer(new RSABlindedEngine(), myDigest.getDigest(), true);
                case PSS:
                default:
                    return new PSSSigner(new RSAEngine(), myDigest.getDigest(), MGF1_SALTLEN);
            }
        }
    }

    /**
     * RSA signature.
     */
    public static class BouncyRSASignature
            extends BouncyPSSSignature {
        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the signatureSpec
         * @throws OceanusException on error
         */
        BouncyRSASignature(final BouncyFactory pFactory,
                           final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);
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
            final BouncyRSAPrivateKey myPrivate = (BouncyRSAPrivateKey) getKeyPair().getPrivateKey();
            final CipherParameters myParms = GordianSignatureType.PSS.equals(getSignatureSpec().getSignatureType())
                                             ? new ParametersWithRandom(myPrivate.getPrivateKey(), getRandom())
                                             : myPrivate.getPrivateKey();
            getSigner().init(true, myParms);
        }

        @Override
        public void initForVerify(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            super.initForVerify(pKeyPair);

            /* Initialise and set the signer */
            final BouncyRSAPublicKey myPublic = (BouncyRSAPublicKey) getKeyPair().getPublicKey();
            getSigner().init(false, myPublic.getPublicKey());
        }

        @Override
        public byte[] sign() throws OceanusException {
            /* Check that we are in signing mode */
            checkMode(GordianSignatureMode.SIGN);

            /* Sign the message */
            try {
                return getSigner().generateSignature();
            } catch (DataLengthException
                    | CryptoException e) {
                throw new GordianCryptoException(BouncySignature.ERROR_SIGGEN, e);
            }
        }

        @Override
        public boolean verify(final byte[] pSignature) throws OceanusException {
            /* Check that we are in verify mode */
            checkMode(GordianSignatureMode.VERIFY);

            /* Verify the message */
            return getSigner().verifySignature(pSignature);
        }
    }

    /**
     * ClientRSA Encapsulation.
     */
    public static class BouncyRSAKEMSender
            extends GordianKEMSender {
        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pPublicKey the target publicKey
         * @param pDigestSpec the digestSpec
         * @throws OceanusException on error
         */
        protected BouncyRSAKEMSender(final BouncyFactory pFactory,
                                     final BouncyRSAPublicKey pPublicKey,
                                     final GordianDigestSpec pDigestSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory);

            /* Create Key Encapsulation */
            final BouncyKeyDerivation myKDF = new BouncyKeyDerivation(getDigest(pDigestSpec));
            final RSAKeyEncapsulation myKEMS = new RSAKeyEncapsulation(myKDF, getRandom());

            /* Initialise the encapsulation */
            myKEMS.init(pPublicKey.getPublicKey());

            /* Create initVector */
            final byte[] myInitVector = new byte[INITLEN];
            getRandom().nextBytes(myInitVector);

            /* Create cipherText */
            final GordianModulus myModulus = pPublicKey.getKeySpec().getModulus();
            final int myLen = myModulus.getModulus() / Byte.SIZE;
            final byte[] myCipherText = new byte[myLen + INITLEN];
            final KeyParameter myParms = (KeyParameter) myKEMS.encrypt(myCipherText, INITLEN, myKDF.getKeyLen());
            System.arraycopy(myInitVector, 0, myCipherText, 0, INITLEN);

            /* Store secret and cipherText */
            storeSecret(myParms.getKey(), myInitVector);
            storeCipherText(myCipherText);
        }
    }

    /**
     * ServerRSA Encapsulation.
     */
    public static class BouncyRSAKEMReceiver
            extends GordianKeyEncapsulation {
        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pPrivateKey the target privateKey
         * @param pDigestSpec the digestSpec
         * @param pCipherText the cipherText
         * @throws OceanusException on error
         */
        protected BouncyRSAKEMReceiver(final BouncyFactory pFactory,
                                       final BouncyRSAPrivateKey pPrivateKey,
                                       final GordianDigestSpec pDigestSpec,
                                       final byte[] pCipherText) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory);

            /* Create Key Encapsulation */
            final BouncyKeyDerivation myKDF = new BouncyKeyDerivation(getDigest(pDigestSpec));
            final RSAKeyEncapsulation myKEMS = new RSAKeyEncapsulation(myKDF, null);

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
