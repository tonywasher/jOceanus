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
import java.util.Arrays;

import javax.crypto.spec.PSource;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.encodings.OAEPEncoding;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.kems.RSAKeyEncapsulation;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.signers.ISO9796d2Signer;
import org.bouncycastle.crypto.signers.ISOTrailers;
import org.bouncycastle.crypto.signers.PSSSigner;
import org.bouncycastle.crypto.signers.X931Signer;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreement.GordianEncapsulationAgreement;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianEncryptor;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianRSAModulus;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignature;
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
            extends BouncyPublicKey<RSAKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        BouncyRSAPublicKey(final GordianAsymKeySpec pKeySpec,
                           final RSAKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final RSAKeyParameters myThis = getPublicKey();
            final RSAKeyParameters myThat = (RSAKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
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

        /**
         * Is the private key valid for this public key?
         * @param pPrivate the private key
         * @return true/false
         */
        public boolean validPrivate(final BouncyRSAPrivateKey pPrivate) {
            final RSAPrivateCrtKeyParameters myPrivate = pPrivate.getPrivateKey();
            return getPublicKey().getExponent().equals(myPrivate.getExponent())
                    && getPublicKey().getModulus().equals(myPrivate.getModulus());
        }
    }

    /**
     * Bouncy RSA PrivateKey.
     */
    public static class BouncyRSAPrivateKey
            extends BouncyPrivateKey<RSAPrivateCrtKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        BouncyRSAPrivateKey(final GordianAsymKeySpec pKeySpec,
                            final RSAPrivateCrtKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final RSAPrivateCrtKeyParameters myThis = getPrivateKey();
            final RSAPrivateCrtKeyParameters myThat = (RSAPrivateCrtKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
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
            final RSAKeyGenerationParameters myParams = new RSAKeyGenerationParameters(RSA_EXPONENT, getRandom(), pKeySpec.getModulus().getLength(), PRIME_CERTAINTY);
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyRSAPublicKey myPublic = new BouncyRSAPublicKey(getKeySpec(), (RSAKeyParameters) myPair.getPublic());
            final BouncyRSAPrivateKey myPrivate = new BouncyRSAPrivateKey(getKeySpec(), (RSAPrivateCrtKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
                final BouncyRSAPrivateKey myPrivateKey = (BouncyRSAPrivateKey) getPrivateKey(pKeyPair);
                final RSAPrivateCrtKeyParameters myParms = myPrivateKey.getPrivateKey();
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
                final RSAPrivateCrtKeyParameters myParms = (RSAPrivateCrtKeyParameters) PrivateKeyFactory.createKey(myInfo);
                final BouncyRSAPrivateKey myPrivate = new BouncyRSAPrivateKey(getKeySpec(), myParms);
                final BouncyRSAPublicKey myPublic = derivePublicKey(pPublicKey);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
                final BouncyRSAPublicKey myPublicKey = (BouncyRSAPublicKey) getPublicKey(pKeyPair);
                final RSAKeyParameters myParms = myPublicKey.getPublicKey();
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(myParms);
                return new X509EncodedKeySpec(myInfo.getEncoded());
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
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
                final RSAKeyParameters myParms = (RSAKeyParameters) PublicKeyFactory.createKey(myInfo);
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
            final GordianDigestSpec myDigestSpec = pSpec.getDigestSpec();
            final BouncyDigest myDigest = pFactory.createDigest(myDigestSpec);
            final int mySaltLength = myDigestSpec.getDigestLength().getByteLength();

            /* Access the signature type */
            switch (pSpec.getSignatureType()) {
                case ISO9796D2:
                    return new ISO9796d2Signer(new RSABlindedEngine(), myDigest.getDigest(), true);
                case X931:
                    return new X931Signer(new RSABlindedEngine(), myDigest.getDigest(), ISOTrailers.noTrailerAvailable(myDigest.getDigest()));
                case PSS:
                default:
                    return new PSSSigner(new RSABlindedEngine(), myDigest.getDigest(), mySaltLength);
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
     * RSA Encapsulation.
     */
    public static class BouncyRSAEncapsulationAgreement
            extends GordianEncapsulationAgreement {
        /**
         * The agreement.
         */
        private final RSAKeyEncapsulation theAgreement;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         */
        BouncyRSAEncapsulationAgreement(final BouncyFactory pFactory,
                                        final GordianAgreementSpec pSpec) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create Agreement */
            final DerivationFunction myKDF = newDerivationFunction();
            theAgreement = new RSAKeyEncapsulation(myKDF, getRandom());
        }

        @Override
        public byte[] initiateAgreement(final GordianKeyPair pTarget) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pTarget);

            /* Initialise Key Encapsulation */
            final BouncyRSAPublicKey myPublic = (BouncyRSAPublicKey) getPublicKey(pTarget);
            theAgreement.init(myPublic.getPublicKey());

            /* Create message */
            final GordianRSAModulus myModulus = myPublic.getKeySpec().getModulus();
            final int myLen = myModulus.getLength() / Byte.SIZE;
            final byte[] myMessage = new byte[myLen];
            final KeyParameter myParms = (KeyParameter) theAgreement.encrypt(myMessage, 0, myLen);

            /* Store secret and cipherText */
            storeSecret(myParms.getKey());

            /* Create the message  */
            return createMessage(myMessage);
        }

        @Override
        public void acceptAgreement(final GordianKeyPair pSelf,
                                    final byte[] pMessage) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pSelf);

            /* Initialise Key Encapsulation */
            final BouncyRSAPrivateKey myPrivate = (BouncyRSAPrivateKey) getPrivateKey(pSelf);
            theAgreement.init(myPrivate.getPrivateKey());

            /* Parse source message */
            final GordianRSAModulus myModulus = myPrivate.getKeySpec().getModulus();
            final int myLen = myModulus.getLength() / Byte.SIZE;
            final byte[] myMessage = parseMessage(pMessage);
            final KeyParameter myParms = (KeyParameter) theAgreement.decrypt(myMessage, 0, myMessage.length, myLen);
            storeSecret(myParms.getKey());
        }
    }

    /**
     * RSA Encryptor.
     */
    public static class BouncyRSAEncryptor
            extends GordianEncryptor {
        /**
         * The underlying encryptor.
         */
        private final AsymmetricBlockCipher theEncryptor;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the encryptorSpec
         * @throws OceanusException on error
         */
        BouncyRSAEncryptor(final BouncyFactory pFactory,
                           final GordianEncryptorSpec pSpec) throws OceanusException {
            /* Initialise underlying cipher */
            super(pFactory, pSpec);
            final BouncyDigest myDigest = pFactory.createDigest(pSpec.getDigestSpec());
            theEncryptor = new OAEPEncoding(new RSABlindedEngine(), myDigest.getDigest(), PSource.PSpecified.DEFAULT.getValue());
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
            /* Check that we are in encryption mode */
            checkMode(GordianEncryptMode.ENCRYPT);

            /* Encrypt the message */
            return processData(pBytes);
        }

        @Override
        public byte[] decrypt(final byte[] pBytes) throws OceanusException {
            /* Check that we are in decryption mode */
            checkMode(GordianEncryptMode.DECRYPT);

            /* Decrypt the message */
            return processData(pBytes);
        }

        /**
         * Process a data buffer.
         * @param pData the buffer to process
         * @return the processed buffer
         * @throws OceanusException on error
         */
        private byte[] processData(final byte[] pData) throws OceanusException {
            try {
                /* Create the output buffer */
                int myInLen = pData.length;
                final byte[] myOutput = new byte[getProcessedLength(myInLen)];

                /* Access input block length */
                final int myInBlockLength = theEncryptor.getInputBlockSize();

                /* Loop encrypting the blocks */
                int myInOff = 0;
                int myOutOff = 0;
                while (myInLen > 0) {
                    /* Process the data */
                    final int myLen = myInLen >= myInBlockLength
                                      ? myInBlockLength
                                      : myInLen;
                    final byte[] myBlock = theEncryptor.processBlock(pData, myInOff, myLen);

                    /* Copy to the output buffer */
                    final int myOutLen = myBlock.length;
                    System.arraycopy(myBlock, 0, myOutput, myOutOff, myOutLen);
                    myOutOff += myOutLen;

                    /* Move to next block */
                    myInOff += myInBlockLength;
                    myInLen -= myInBlockLength;
                }

                /* Return full buffer if possible */
                if (myOutOff == myOutput.length) {
                    return myOutput;
                }

                /* Cut down buffer */
                final byte[] myReturn = Arrays.copyOf(myOutput, myOutOff);
                Arrays.fill(myOutput, (byte) 0);
                return myReturn;

            } catch (InvalidCipherTextException e) {
                throw new GordianCryptoException("Failed to process data", e);
            }
        }

        /**
         * Obtain the length of the buffer required for the processed output.
         * @param pLength the length of input data
         * @return the number of bytes.
         */
        private int getProcessedLength(final int pLength) {
            return theEncryptor.getOutputBlockSize() * getNumBlocks(pLength, theEncryptor.getInputBlockSize());
        }

        /**
         * Obtain the number of blocks required for the length in terms of blocks.
         * @param pLength the length of data
         * @param pBlockLength the blockLength
         * @return the number of blocks.
         */
        private static int getNumBlocks(final int pLength, final int pBlockLength) {
            return (pLength + pBlockLength - 1) / pBlockLength;
        }
    }
}
