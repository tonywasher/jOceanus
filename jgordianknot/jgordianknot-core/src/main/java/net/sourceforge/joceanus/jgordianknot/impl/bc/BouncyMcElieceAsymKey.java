/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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

import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.patch.utils.PqcPrivateKeyFactory;
import org.bouncycastle.crypto.patch.utils.PqcPrivateKeyInfoFactory;
import org.bouncycastle.crypto.patch.utils.PqcPublicKeyFactory;
import org.bouncycastle.crypto.patch.utils.PqcSubjectPublicKeyInfoFactory;
import org.bouncycastle.pqc.crypto.MessageEncryptor;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2KeyPairGenerator;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2Parameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2PrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2PublicKeyParameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCipher;
import org.bouncycastle.pqc.crypto.mceliece.McElieceFujisakiCipher;
import org.bouncycastle.pqc.crypto.mceliece.McElieceKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceKeyPairGenerator;
import org.bouncycastle.pqc.crypto.mceliece.McElieceKobaraImaiCipher;
import org.bouncycastle.pqc.crypto.mceliece.McElieceParameters;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePointchevalCipher;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePublicKeyParameters;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianMcElieceKeySpec.GordianMcElieceDigestType;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.encrypt.GordianCoreEncryptor;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * McEliece AsymKey classes.
 */
public final class BouncyMcElieceAsymKey {
    /**
     * Private constructor.
     */
    private BouncyMcElieceAsymKey() {
    }

    /**
     * Bouncy McEliece PublicKey.
     */
    public static class BouncyMcEliecePublicKey
            extends BouncyPublicKey<McEliecePublicKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        BouncyMcEliecePublicKey(final GordianAsymKeySpec pKeySpec,
                                final McEliecePublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final McEliecePublicKeyParameters myThis = getPublicKey();
            final McEliecePublicKeyParameters myThat = (McEliecePublicKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
        }

        /**
         * Is the private key valid for this public key?
         * @param pPrivate the private key
         * @return true/false
         */
        public boolean validPrivate(final BouncyMcEliecePrivateKey pPrivate) {
            final McEliecePrivateKeyParameters myPrivate = pPrivate.getPrivateKey();
            return getPublicKey().getN() == myPrivate.getN();
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final McEliecePublicKeyParameters pFirst,
                                           final McEliecePublicKeyParameters pSecond) {
            if (pFirst.getN() != pSecond.getN()
                    || pFirst.getT() != pSecond.getT()) {
                return false;
            }
            return pFirst.getG().equals(pSecond.getG());
        }
    }

    /**
     * Bouncy McEliece PrivateKey.
     */
    public static class BouncyMcEliecePrivateKey
            extends BouncyPrivateKey<McEliecePrivateKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        BouncyMcEliecePrivateKey(final GordianAsymKeySpec pKeySpec,
                                 final McEliecePrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final McEliecePrivateKeyParameters myThis = getPrivateKey();
            final McEliecePrivateKeyParameters myThat = (McEliecePrivateKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final McEliecePrivateKeyParameters pFirst,
                                           final McEliecePrivateKeyParameters pSecond) {
            if (pFirst.getN() != pSecond.getN()
                    || pFirst.getK() != pSecond.getK()) {
                return false;
            }
            if (!pFirst.getP1().equals(pSecond.getP1())
                    || !pFirst.getP2().equals(pSecond.getP2())) {
                return false;
            }
            return pFirst.getField().equals(pSecond.getField())
                    && pFirst.getGoppaPoly().equals(pSecond.getGoppaPoly());
        }
    }

    /**
     * BouncyCastle McEliece KeyPair generator.
     */
    public static class BouncyMcElieceKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final McElieceKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyMcElieceKeyPairGenerator(final BouncyFactory pFactory,
                                       final GordianAsymKeySpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = new McElieceKeyPairGenerator();
            final KeyGenerationParameters myParams = new McElieceKeyGenerationParameters(getRandom(), new McElieceParameters(new SHA256Digest()));
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyMcEliecePublicKey myPublic = new BouncyMcEliecePublicKey(getKeySpec(), (McEliecePublicKeyParameters) myPair.getPublic());
            final BouncyMcEliecePrivateKey myPrivate = new BouncyMcEliecePrivateKey(getKeySpec(), (McEliecePrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
                final BouncyMcEliecePrivateKey myPrivateKey = (BouncyMcEliecePrivateKey) getPrivateKey(pKeyPair);
                final McEliecePrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
                final PrivateKeyInfo myInfo = PqcPrivateKeyInfoFactory.createPrivateKeyInfo(myParms, null);
                return new PKCS8EncodedKeySpec(myInfo.getEncoded());
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                           final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            try {
                checkKeySpec(pPrivateKey);
                final BouncyMcEliecePublicKey myPublic = derivePublicKey(pPublicKey);
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final McEliecePrivateKeyParameters myParms = (McEliecePrivateKeyParameters) PqcPrivateKeyFactory.createKey(myInfo);
                final BouncyMcEliecePrivateKey myPrivate = new BouncyMcEliecePrivateKey(getKeySpec(), myParms);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
                final BouncyMcEliecePublicKey myPublicKey = (BouncyMcEliecePublicKey) getPublicKey(pKeyPair);
                final McEliecePublicKeyParameters myParms = myPublicKey.getPublicKey();
                final SubjectPublicKeyInfo myInfo = PqcSubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(myParms);
                return new X509EncodedKeySpec(myInfo.getEncoded());
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            final BouncyMcEliecePublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws OceanusException on error
         */
        private BouncyMcEliecePublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            try {
                checkKeySpec(pEncodedKey);
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final McEliecePublicKeyParameters myParms = (McEliecePublicKeyParameters) PqcPublicKeyFactory.createKey(myInfo);
                return new BouncyMcEliecePublicKey(getKeySpec(), myParms);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * Bouncy McElieceCCA2 PublicKey.
     */
    public static class BouncyMcElieceCCA2PublicKey
            extends BouncyPublicKey<McElieceCCA2PublicKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        BouncyMcElieceCCA2PublicKey(final GordianAsymKeySpec pKeySpec,
                                    final McElieceCCA2PublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final McElieceCCA2PublicKeyParameters myThis = getPublicKey();
            final McElieceCCA2PublicKeyParameters myThat = (McElieceCCA2PublicKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
        }

        /**
         * Is the private key valid for this public key?
         * @param pPrivate the private key
         * @return true/false
         */
        public boolean validPrivate(final BouncyMcElieceCCA2PrivateKey pPrivate) {
            final McElieceCCA2PrivateKeyParameters myPrivate = pPrivate.getPrivateKey();
            return getPublicKey().getN() == myPrivate.getN();
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final McElieceCCA2PublicKeyParameters pFirst,
                                           final McElieceCCA2PublicKeyParameters pSecond) {
            if (pFirst.getN() != pSecond.getN()
                    || pFirst.getT() != pSecond.getT()) {
                return false;
            }
            return pFirst.getDigest().equals(pSecond.getDigest())
                    && pFirst.getG().equals(pSecond.getG());
        }
    }

    /**
     * Bouncy McElieceCCA2 PrivateKey.
     */
    public static class BouncyMcElieceCCA2PrivateKey
            extends BouncyPrivateKey<McElieceCCA2PrivateKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        BouncyMcElieceCCA2PrivateKey(final GordianAsymKeySpec pKeySpec,
                                     final McElieceCCA2PrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final McElieceCCA2PrivateKeyParameters myThis = getPrivateKey();
            final McElieceCCA2PrivateKeyParameters myThat = (McElieceCCA2PrivateKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final McElieceCCA2PrivateKeyParameters pFirst,
                                           final McElieceCCA2PrivateKeyParameters pSecond) {
            if (pFirst.getN() != pSecond.getN()
                    || pFirst.getK() != pSecond.getK()) {
                return false;
            }
            if (!pFirst.getP().equals(pSecond.getP())
                    || !pFirst.getDigest().equals(pSecond.getDigest())) {
                return false;
            }
            return pFirst.getField().equals(pSecond.getField())
                    && pFirst.getGoppaPoly().equals(pSecond.getGoppaPoly());
        }
    }

    /**
     * BouncyCastle McElieceCCA2 KeyPair generator.
     */
    public static class BouncyMcElieceCCA2KeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final McElieceCCA2KeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyMcElieceCCA2KeyPairGenerator(final BouncyFactory pFactory,
                                           final GordianAsymKeySpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            final GordianMcElieceDigestType myType = pKeySpec.getMcElieceKeySpec().getDigestType();
            theGenerator = new McElieceCCA2KeyPairGenerator();
            final KeyGenerationParameters myParams = new McElieceCCA2KeyGenerationParameters(getRandom(),
                    new McElieceCCA2Parameters(myType.getM(), McElieceParameters.DEFAULT_T, myType.getParameter()));
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyMcElieceCCA2PublicKey myPublic = new BouncyMcElieceCCA2PublicKey(getKeySpec(), (McElieceCCA2PublicKeyParameters) myPair.getPublic());
            final BouncyMcElieceCCA2PrivateKey myPrivate = new BouncyMcElieceCCA2PrivateKey(getKeySpec(), (McElieceCCA2PrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
                final BouncyMcElieceCCA2PrivateKey myPrivateKey = (BouncyMcElieceCCA2PrivateKey) getPrivateKey(pKeyPair);
                final McElieceCCA2PrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
                final PrivateKeyInfo myInfo = PqcPrivateKeyInfoFactory.createPrivateKeyInfo(myParms, null);
                return new PKCS8EncodedKeySpec(myInfo.getEncoded());
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                           final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            try {
                checkKeySpec(pPrivateKey);
                final BouncyMcElieceCCA2PublicKey myPublic = derivePublicKey(pPublicKey);
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final McElieceCCA2PrivateKeyParameters myParms = (McElieceCCA2PrivateKeyParameters) PqcPrivateKeyFactory.createKey(myInfo);
                final BouncyMcElieceCCA2PrivateKey myPrivate = new BouncyMcElieceCCA2PrivateKey(getKeySpec(), myParms);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
                final BouncyMcElieceCCA2PublicKey myPublicKey = (BouncyMcElieceCCA2PublicKey) getPublicKey(pKeyPair);
                final McElieceCCA2PublicKeyParameters myParms = myPublicKey.getPublicKey();
                final SubjectPublicKeyInfo myInfo = PqcSubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(myParms);
                return new X509EncodedKeySpec(myInfo.getEncoded());
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            final BouncyMcElieceCCA2PublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws OceanusException on error
         */
        private BouncyMcElieceCCA2PublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            try {
                checkKeySpec(pEncodedKey);
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final McElieceCCA2PublicKeyParameters myParms = (McElieceCCA2PublicKeyParameters) PqcPublicKeyFactory.createKey(myInfo);
                return new BouncyMcElieceCCA2PublicKey(getKeySpec(), myParms);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * McEliece Encryptor.
     */
    public static class BouncyMcElieceEncryptor
            extends GordianCoreEncryptor {
        /**
         * The underlying encryptor.
         */
        private final MessageEncryptor theEncryptor;

        /**
         * Are we encrypting?
         */
        private int theInputBlockLen;

        /**
         * Output blockSize.
         */
        private int theOutputBlockLen;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the encryptorSpec
         */
        BouncyMcElieceEncryptor(final BouncyFactory pFactory,
                                final GordianEncryptorSpec pSpec) {
            /* Initialise underlying cipher */
            super(pFactory, pSpec);

            /* Create the encryptor */
            theEncryptor = new McElieceCipher();
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

            /* Determine the block lengths */
            final McEliecePublicKeyParameters myParams = (McEliecePublicKeyParameters) getPublicKey().getPublicKey();
            theInputBlockLen = myParams.getK() / Byte.SIZE;
            theOutputBlockLen = myParams.getN() / Byte.SIZE;
        }

        @Override
        public void initForDecrypt(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise underlying cipher */
            super.initForDecrypt(pKeyPair);

            /* Initialise for decryption */
            theEncryptor.init(false, getPrivateKey().getPrivateKey());

            /* Determine the block lengths */
            final McEliecePublicKeyParameters myParams = (McEliecePublicKeyParameters) getPublicKey().getPublicKey();
            theInputBlockLen = myParams.getN() / Byte.SIZE;
            theOutputBlockLen = myParams.getK() / Byte.SIZE;
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

                /* Loop encrypting the blocks */
                int myInOff = 0;
                int myOutOff = 0;
                while (myInLen > 0) {
                    /* Process the data */
                    final int myLen = myInLen >= theInputBlockLen
                                      ? theInputBlockLen
                                      : myInLen;
                    final byte[] myData = new byte[myLen];
                    System.arraycopy(pData, myInOff, myData, 0, myLen);
                    final byte[] myBlock = isEncrypting()
                                           ? theEncryptor.messageEncrypt(myData)
                                           : theEncryptor.messageDecrypt(myData);

                    /* Copy to the output buffer */
                    final int myOutLen = myBlock.length;
                    System.arraycopy(myBlock, 0, myOutput, myOutOff, myOutLen);
                    myOutOff += myOutLen;

                    /* Move to next block */
                    myInOff += theInputBlockLen;
                    myInLen -= theInputBlockLen;
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
            return theOutputBlockLen * getNumBlocks(pLength, theInputBlockLen);
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

    /**
     * McEliece CCA2 Encryptor.
     */
    public static class BouncyMcElieceCCA2Encryptor
            extends GordianCoreEncryptor {
        /**
         * The underlying encryptor.
         */
        private final MessageEncryptor theEncryptor;

        /**
         * Are we padding?
         */
        private final boolean doPad;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the encryptorSpec
         */
        BouncyMcElieceCCA2Encryptor(final BouncyFactory pFactory,
                                    final GordianEncryptorSpec pSpec) {
            /* Initialise underlying cipher */
            super(pFactory, pSpec);

            /* Switch on encryptor type */
            switch (pSpec.getMcElieceType()) {
                case FUJISAKI:
                    theEncryptor = new McElieceFujisakiCipher();
                    doPad = false;
                    break;
                case KOBARAIMAI:
                    theEncryptor = new McElieceKobaraImaiCipher();
                    doPad = true;
                    break;
                case POINTCHEVAL:
                default:
                    theEncryptor = new McEliecePointchevalCipher();
                    doPad = false;
                    break;
            }
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

            /* Handle padding if required */
            byte[] myData = pBytes;
            if (doPad) {
                final int myLen = pBytes.length;
                myData = new byte[myLen + 1];
                System.arraycopy(pBytes, 0, myData, 0, myLen);
                myData[myLen] = 1;
            }

            /* Encrypt the message */
            return theEncryptor.messageEncrypt(myData);
        }

        @Override
        public byte[] decrypt(final byte[] pBytes) throws OceanusException {
            try {
                /* Check that we are in decryption mode */
                checkMode(GordianEncryptMode.DECRYPT);

                /* Decrypt the message */
                byte[] myData = theEncryptor.messageDecrypt(pBytes);

                /* Handle padding if required */
                if (doPad) {
                    /* find first non-zero byte */
                    int myIndex = myData.length - 1;
                    while (myIndex >= 0 && myData[myIndex] == 0) {
                        myIndex--;
                    }

                    /* check if padding byte is valid */
                    if (myIndex < 0 || myData[myIndex] != 0x01) {
                        throw new GordianDataException("Failed to decrypt data");
                    }

                    /* extract message */
                    final byte[] myBytes = new byte[myIndex];
                    System.arraycopy(myData, 0, myBytes, 0, myIndex);
                    myData = myBytes;
                }

                /* Return the data */
                return myData;

            } catch (InvalidCipherTextException e) {
                throw new GordianCryptoException("Failed to decrypt data", e);
            }
        }
    }
}
