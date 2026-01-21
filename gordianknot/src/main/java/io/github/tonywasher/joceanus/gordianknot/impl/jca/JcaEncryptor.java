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
package io.github.tonywasher.joceanus.gordianknot.impl.jca;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigestType;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.GordianEncryptorSpec;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.GordianSM2EncryptionSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.encrypt.GordianCoreEncryptor;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPair.JcaPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaKeyPair.JcaPublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * JCA Encryptor classes.
 */
public final class JcaEncryptor {
    /**
     * Error string.
     */
    private static final String ERROR_INIT = "Failed to initialise";

    /**
     * Private constructor.
     */
    private JcaEncryptor() {
    }

    /**
     * Block Encryptor.
     */
    public static class JcaBlockEncryptor
            extends GordianCoreEncryptor {
        /**
         * The underlying encryptor.
         */
        private final Cipher theEncryptor;

        /**
         * Constructor.
         *
         * @param pFactory the factory
         * @param pSpec    the encryptorSpec
         * @throws GordianException on error
         */
        JcaBlockEncryptor(final GordianBaseFactory pFactory,
                          final GordianEncryptorSpec pSpec) throws GordianException {
            /* Initialise underlying cipher */
            super(pFactory, pSpec);
            theEncryptor = getJavaEncryptor(getAlgorithmName(pSpec), false);
        }

        @Override
        protected JcaPublicKey getPublicKey() {
            return (JcaPublicKey) super.getPublicKey();
        }

        @Override
        protected JcaPrivateKey getPrivateKey() {
            return (JcaPrivateKey) super.getPrivateKey();
        }

        @Override
        public void initForEncrypt(final GordianKeyPair pKeyPair) throws GordianException {
            try {
                /* Initialise underlying cipher */
                JcaKeyPair.checkKeyPair(pKeyPair);
                super.initForEncrypt(pKeyPair);

                /* Initialise for encryption */
                theEncryptor.init(Cipher.ENCRYPT_MODE, getPublicKey().getPublicKey(), getRandom());
            } catch (InvalidKeyException e) {
                throw new GordianCryptoException(ERROR_INIT, e);
            }
        }

        @Override
        public void initForDecrypt(final GordianKeyPair pKeyPair) throws GordianException {
            try {
                /* Initialise underlying cipher */
                JcaKeyPair.checkKeyPair(pKeyPair);
                super.initForDecrypt(pKeyPair);

                /* Initialise for decryption */
                theEncryptor.init(Cipher.DECRYPT_MODE, getPrivateKey().getPrivateKey());
            } catch (InvalidKeyException e) {
                throw new GordianCryptoException(ERROR_INIT, e);
            }
        }

        @Override
        public byte[] encrypt(final byte[] pBytes) throws GordianException {
            /* Check that we are in encryption mode */
            checkMode(GordianEncryptMode.ENCRYPT);

            /* Encrypt the message */
            return processData(pBytes);
        }

        @Override
        public byte[] decrypt(final byte[] pBytes) throws GordianException {
            /* Check that we are in decryption mode */
            checkMode(GordianEncryptMode.DECRYPT);

            /* Decrypt the message */
            return processData(pBytes);
        }

        /**
         * Process a data buffer.
         *
         * @param pData the buffer to process
         * @return the processed buffer
         * @throws GordianException on error
         */
        private byte[] processData(final byte[] pData) throws GordianException {
            try {
                /* Access input block length */
                int myInLen = pData.length;
                final int myInBlockLength = theEncryptor.getBlockSize();
                final int myNumBlocks = getNumBlocks(myInLen, myInBlockLength);
                final int myOutBlockLength = theEncryptor.getOutputSize(myInBlockLength);

                /* Create the output buffer */
                final byte[] myOutput = new byte[myOutBlockLength * myNumBlocks];

                /* Loop encrypting the blocks */
                int myInOff = 0;
                int myOutOff = 0;
                while (myInLen > 0) {
                    /* Process the data */
                    final int myLen = Math.min(myInLen, myInBlockLength);
                    final byte[] myBlock = theEncryptor.doFinal(pData, myInOff, myLen);

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

            } catch (IllegalBlockSizeException
                     | BadPaddingException e) {
                throw new GordianCryptoException("Failed to process data", e);
            }
        }

        /**
         * Obtain the number of blocks required for the length in terms of blocks.
         *
         * @param pLength      the length of clear data
         * @param pBlockLength the blockLength
         * @return the number of blocks.
         */
        private static int getNumBlocks(final int pLength, final int pBlockLength) {
            return (pLength + pBlockLength - 1) / pBlockLength;
        }

        /**
         * Obtain the algorithmName.
         *
         * @param pSpec the Spec
         * @return the algorithm name
         */
        private static String getAlgorithmName(final GordianEncryptorSpec pSpec) {
            /* Determine the base algorithm */
            final String myBase = pSpec.getKeyPairType().name();

            /* Switch on encryptor type */
            switch (pSpec.getDigestSpec().getDigestLength()) {
                case LEN_224:
                    return myBase + "/ECB/OAEPWITHSHA224ANDMGF1PADDING";
                case LEN_256:
                    return myBase + "/ECB/OAEPWITHSHA256ANDMGF1PADDING";
                case LEN_384:
                    return myBase + "/ECB/OAEPWITHSHA384ANDMGF1PADDING";
                case LEN_512:
                default:
                    return myBase + "/ECB/OAEPWITHSHA512ANDMGF1PADDING";
            }
        }
    }

    /**
     * Hybrid Encryptor.
     */
    public static class JcaHybridEncryptor
            extends GordianCoreEncryptor {
        /**
         * The underlying encryptor.
         */
        private final Cipher theEncryptor;

        /**
         * Constructor.
         *
         * @param pFactory the factory
         * @param pSpec    the encryptorSpec
         * @throws GordianException on error
         */
        JcaHybridEncryptor(final GordianBaseFactory pFactory,
                           final GordianEncryptorSpec pSpec) throws GordianException {
            /* Initialise underlying cipher */
            super(pFactory, pSpec);
            theEncryptor = getJavaEncryptor(getAlgorithmName(pSpec), false);
        }

        @Override
        protected JcaPublicKey getPublicKey() {
            return (JcaPublicKey) super.getPublicKey();
        }

        @Override
        protected JcaPrivateKey getPrivateKey() {
            return (JcaPrivateKey) super.getPrivateKey();
        }

        @Override
        public void initForEncrypt(final GordianKeyPair pKeyPair) throws GordianException {
            try {
                /* Initialise underlying cipher */
                JcaKeyPair.checkKeyPair(pKeyPair);
                super.initForEncrypt(pKeyPair);

                /* Initialise for encryption */
                theEncryptor.init(Cipher.ENCRYPT_MODE, getPublicKey().getPublicKey(), getRandom());
            } catch (InvalidKeyException e) {
                throw new GordianCryptoException(ERROR_INIT, e);
            }
        }

        @Override
        public void initForDecrypt(final GordianKeyPair pKeyPair) throws GordianException {
            try {
                /* Initialise underlying cipher */
                JcaKeyPair.checkKeyPair(pKeyPair);
                super.initForDecrypt(pKeyPair);

                /* Initialise for decryption */
                theEncryptor.init(Cipher.DECRYPT_MODE, getPrivateKey().getPrivateKey());
            } catch (InvalidKeyException e) {
                throw new GordianCryptoException(ERROR_INIT, e);
            }
        }

        @Override
        public byte[] encrypt(final byte[] pBytes) throws GordianException {
            /* Check that we are in encryption mode */
            checkMode(GordianEncryptMode.ENCRYPT);

            /* Encrypt the message */
            return processData(pBytes);
        }

        @Override
        public byte[] decrypt(final byte[] pBytes) throws GordianException {
            /* Check that we are in decryption mode */
            checkMode(GordianEncryptMode.DECRYPT);

            /* Decrypt the message */
            return processData(pBytes);
        }

        /**
         * Process a data buffer.
         *
         * @param pData the buffer to process
         * @return the processed buffer
         * @throws GordianException on error
         */
        private byte[] processData(final byte[] pData) throws GordianException {
            try {
                return theEncryptor.doFinal(pData, 0, pData.length);
            } catch (IllegalBlockSizeException
                     | BadPaddingException e) {
                throw new GordianCryptoException("Failed to process data", e);
            }
        }

        /**
         * Obtain the algorithmName.
         *
         * @param pSpec the Spec
         * @return the algorithm name
         */
        private static String getAlgorithmName(final GordianEncryptorSpec pSpec) {
            /* Switch on encryptor type */
            final GordianSM2EncryptionSpec mySpec = pSpec.getSM2EncryptionSpec();
            final GordianDigestSpec myDigestSpec = mySpec.getDigestSpec();
            final GordianDigestType myDigestType = myDigestSpec.getDigestType();
            switch (myDigestType) {
                case SHA2:
                    return "SM2withSHA" + myDigestSpec.getDigestLength();
                case BLAKE2:
                    return "SM2withBlake2" + (GordianLength.LEN_512.equals(myDigestSpec.getDigestLength()) ? "b" : "s");
                case WHIRLPOOL:
                case SM3:
                default:
                    return "SM2with" + myDigestType;
            }
        }
    }

    /**
     * Create the BouncyCastle KeyFactory via JCA.
     *
     * @param pAlgorithm  the Algorithm
     * @param postQuantum is this a postQuantum algorithm?
     * @return the KeyFactory
     * @throws GordianException on error
     */
    private static Cipher getJavaEncryptor(final String pAlgorithm,
                                           final boolean postQuantum) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Return a Cipher for the algorithm */
            return Cipher.getInstance(pAlgorithm, postQuantum
                    ? JcaProvider.BCPQPROV
                    : JcaProvider.BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException
                 | NoSuchPaddingException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create Cipher", e);
        }
    }
}
