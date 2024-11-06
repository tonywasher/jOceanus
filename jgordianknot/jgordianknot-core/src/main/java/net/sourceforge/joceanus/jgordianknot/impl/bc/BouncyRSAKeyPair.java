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
package net.sourceforge.joceanus.jgordianknot.impl.bc;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpecBuilder;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianAgreementMessageASN1;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.encrypt.GordianCoreEncryptor;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianKeyPairValidity;
import net.sourceforge.joceanus.jgordianknot.impl.core.sign.GordianCoreSignature;
import net.sourceforge.joceanus.jtethys.OceanusException;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.encodings.OAEPEncoding;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.kems.RSAKEMExtractor;
import org.bouncycastle.crypto.kems.RSAKEMGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.signers.ISO9796d2Signer;
import org.bouncycastle.crypto.signers.ISOTrailers;
import org.bouncycastle.crypto.signers.PSSSigner;
import org.bouncycastle.crypto.signers.RSADigestSigner;
import org.bouncycastle.crypto.signers.X931Signer;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;

import javax.crypto.spec.PSource;
import javax.security.auth.DestroyFailedException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

/**
 * RSA KeyPair classes.
 */
public final class BouncyRSAKeyPair {
    /**
     * Private constructor.
     */
    private BouncyRSAKeyPair() {
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
        BouncyRSAPublicKey(final GordianKeyPairSpec pKeySpec,
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
        BouncyRSAPrivateKey(final GordianKeyPairSpec pKeySpec,
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
                                  final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = new RSAKeyPairGenerator();
            final RSAKeyGenerationParameters myParams
                    = new RSAKeyGenerationParameters(RSA_EXPONENT, getRandom(), pKeySpec.getRSAModulus().getLength(), PRIME_CERTAINTY);
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyRSAPublicKey myPublic = new BouncyRSAPublicKey(getKeySpec(), (RSAKeyParameters) myPair.getPublic());
            final BouncyRSAPrivateKey myPrivate = new BouncyRSAPrivateKey(getKeySpec(), (RSAPrivateCrtKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check the keyPair type and keySpecs */
                BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

                /* build and return the encoding */
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
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pPrivateKey);

                /* derive keyPair */
                final BouncyRSAPublicKey myPublic = derivePublicKey(pPublicKey);
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final RSAPrivateCrtKeyParameters myParms = (RSAPrivateCrtKeyParameters) PrivateKeyFactory.createKey(myInfo);
                final BouncyRSAPrivateKey myPrivate = new BouncyRSAPrivateKey(getKeySpec(), myParms);
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
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pEncodedKey);

                /* derive publicKey */
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
            extends GordianCoreSignature {
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
            final BouncyDigest myDigest = pFactory.getDigestFactory().createDigest(myDigestSpec);
            final int mySaltLength = myDigestSpec.getDigestLength().getByteLength();

            /* Access the signature type */
            switch (pSpec.getSignatureType()) {
                case ISO9796D2:
                    return new ISO9796d2Signer(new RSABlindedEngine(), myDigest.getDigest(), ISOTrailers.noTrailerAvailable(myDigest.getDigest()));
                case X931:
                    return new X931Signer(new RSABlindedEngine(), myDigest.getDigest(), ISOTrailers.noTrailerAvailable(myDigest.getDigest()));
                case PREHASH:
                    return new RSADigestSigner(myDigest.getDigest());
                case PSS128:
                    return new PSSSigner(new RSABlindedEngine(), myDigest.getDigest(),
                            pFactory.getDigestFactory().createDigest(GordianDigestSpecBuilder.shake128()).getDigest(), mySaltLength);
                case PSS256:
                    return new PSSSigner(new RSABlindedEngine(), myDigest.getDigest(),
                            pFactory.getDigestFactory().createDigest(GordianDigestSpecBuilder.shake256()).getDigest(), mySaltLength);
                case PSSMGF1:
                default:
                    if (myDigest.getDigestSpec().getDigestType() == GordianDigestType.SKEIN) {
                        int i = 0;
                    }
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
            BouncyKeyPair.checkKeyPair(pKeyPair);
            super.initForSigning(pKeyPair);

            /* Initialise and set the signer */
            final BouncyRSAPrivateKey myPrivate = (BouncyRSAPrivateKey) getKeyPair().getPrivateKey();
            final CipherParameters myParms = getSignatureSpec().getSignatureType().isPSS()
                                             ? new ParametersWithRandom(myPrivate.getPrivateKey(), getRandom())
                                             : myPrivate.getPrivateKey();
            getSigner().init(true, myParms);
        }

        @Override
        public void initForVerify(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            BouncyKeyPair.checkKeyPair(pKeyPair);
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
            extends GordianCoreAnonymousAgreement {
        /**
         * Key Length.
         */
        private static final int KEYLEN = 32;

        /**
         * The generator.
         */
        private final RSAKEMGenerator theGenerator;

        /**
         * Derivation function.
         */
        private final DerivationFunction theDerivation;

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
            theDerivation = newDerivationFunction();
            theGenerator = new RSAKEMGenerator(KEYLEN, theDerivation, getRandom());
        }

        @Override
        public GordianAgreementMessageASN1 createClientHelloASN1(final GordianKeyPair pServer) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check keyPair */
                BouncyKeyPair.checkKeyPair(pServer);
                checkKeyPair(pServer);

                /* Create encapsulation */
                final BouncyRSAPublicKey myPublic = (BouncyRSAPublicKey) getPublicKey(pServer);
                final SecretWithEncapsulation myResult = theGenerator.generateEncapsulated(myPublic.getPublicKey());

                /* Create message */
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

            /* Initialise Key Encapsulation */
            final BouncyRSAPrivateKey myPrivate = (BouncyRSAPrivateKey) getPrivateKey(pServer);
            final RSAKEMExtractor myExtractor = new RSAKEMExtractor(myPrivate.getPrivateKey(), KEYLEN, theDerivation);

            /* Parse clientHello message and store secret */
            final byte[] myMessage = pClientHello.getEncapsulated();
            storeSecret(myExtractor.extractSecret(myMessage));
        }
    }

    /**
     * RSA Encryptor.
     */
    public static class BouncyRSAEncryptor
            extends BouncyCoreEncryptor {
        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the encryptorSpec
         * @throws OceanusException on error
         */
        BouncyRSAEncryptor(final BouncyFactory pFactory,
                           final GordianEncryptorSpec pSpec) throws OceanusException {
            /* Initialise underlying cipher */
            super(pFactory, pSpec, new RSABlindedEngine());
        }

        @Override
        protected BouncyRSAPublicKey getPublicKey() {
            return (BouncyRSAPublicKey) super.getPublicKey();
        }

        @Override
        protected BouncyRSAPrivateKey getPrivateKey() {
            return (BouncyRSAPrivateKey) super.getPrivateKey();
        }
    }

    /**
     * RSA Encryptor.
     */
    public static class BouncyCoreEncryptor
            extends GordianCoreEncryptor {
        /**
         * The underlying encryptor.
         */
        private final AsymmetricBlockCipher theEncryptor;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the encryptorSpec
         * @param pEngine the underlying engine
         * @throws OceanusException on error
         */
        protected BouncyCoreEncryptor(final BouncyFactory pFactory,
                                      final GordianEncryptorSpec pSpec,
                                      final AsymmetricBlockCipher pEngine) throws OceanusException {
            /* Initialise underlying cipher */
            super(pFactory, pSpec);
            final BouncyDigest myDigest = pFactory.getDigestFactory().createDigest(pSpec.getDigestSpec());
            theEncryptor = new OAEPEncoding(pEngine, myDigest.getDigest(), PSource.PSpecified.DEFAULT.getValue());
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
            final ParametersWithRandom myParms = new ParametersWithRandom(getPublicKey().getPublicKey(), getRandom());
            theEncryptor.init(true, myParms);
        }

        @Override
        public void initForDecrypt(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise underlying cipher */
            BouncyKeyPair.checkKeyPair(pKeyPair);
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
                    final int myLen = Math.min(myInLen, myInBlockLength);
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
