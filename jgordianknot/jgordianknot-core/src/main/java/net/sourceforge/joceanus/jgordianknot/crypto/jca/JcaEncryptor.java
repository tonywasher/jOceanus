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
package net.sourceforge.joceanus.jgordianknot.crypto.jca;

import java.security.InvalidKeyException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import org.bouncycastle.pqc.jcajce.spec.McElieceKeyGenParameterSpec;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianEncryptor;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPair.JcaPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPair.JcaPublicKey;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
            extends GordianEncryptor {
        /**
         * McEliece cipher length.
         */
        private static final int MCELIECE_CIPHERLEN = (1 << McElieceKeyGenParameterSpec.DEFAULT_M) / Byte.SIZE;

        /**
         * The underlying encryptor.
         */
        private final Cipher theEncryptor;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the encryptorSpec
         * @throws OceanusException on error
         */
        JcaBlockEncryptor(final JcaFactory pFactory,
                          final GordianEncryptorSpec pSpec) throws OceanusException {
            /* Initialise underlying cipher */
            super(pFactory, pSpec);
            theEncryptor = JcaFactory.getJavaEncryptor(getAlgorithmName(pSpec), GordianAsymKeyType.MCELIECE.equals(pSpec.getKeyType()));
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
        public void initForEncrypt(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
                /* Initialise underlying cipher */
                super.initForEncrypt(pKeyPair);

                /* Initialise for encryption */
                theEncryptor.init(Cipher.ENCRYPT_MODE, getPublicKey().getPublicKey(), getRandom());
            } catch (InvalidKeyException e) {
                throw new GordianCryptoException(ERROR_INIT, e);
            }
        }

        @Override
        public void initForDecrypt(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
                /* Initialise underlying cipher */
                super.initForDecrypt(pKeyPair);

                /* Initialise for decryption */
                theEncryptor.init(Cipher.DECRYPT_MODE, getPrivateKey().getPrivateKey());
            } catch (InvalidKeyException e) {
                throw new GordianCryptoException(ERROR_INIT, e);
            }
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
                /* Access input block length */
                int myInLen = pData.length;
                final int myInBlockLength = theEncryptor.getBlockSize();
                final int myNumBlocks = getNumBlocks(myInLen, myInBlockLength);
                final int myOutBlockLength = GordianAsymKeyType.MCELIECE.equals(getEncryptorSpec().getKeyType())
                                             ? MCELIECE_CIPHERLEN
                                             : theEncryptor.getOutputSize(myInBlockLength);

                /* Create the output buffer */
                final byte[] myOutput = new byte[myOutBlockLength * myNumBlocks];

                /* Loop encrypting the blocks */
                int myInOff = 0;
                int myOutOff = 0;
                while (myInLen > 0) {
                    /* Process the data */
                    final int myLen = myInLen >= myInBlockLength
                                      ? myInBlockLength
                                      : myInLen;
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
         * @param pLength the length of clear data
         * @param pBlockLength the blockLength
         * @return the number of blocks.
         */
        private static int getNumBlocks(final int pLength, final int pBlockLength) {
            return (pLength + pBlockLength - 1) / pBlockLength;
        }

        /**
         * Obtain the algorithmName.
         * @param pSpec the Spec
         * @return the algorithm name
         */
        private static String getAlgorithmName(final GordianEncryptorSpec pSpec) {
            /* If this is a McEliece encryptor */
            if (GordianAsymKeyType.MCELIECE.equals(pSpec.getKeyType())) {
                return "McEliece";
            }

            /* Switch on encryptor type */
            switch (pSpec.getDigestSpec().getDigestLength()) {
                case LEN_224:
                    return "RSA/ECB/OAEPWITHSHA-224ANDMGF1PADDING";
                case LEN_256:
                    return "RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING";
                case LEN_384:
                    return "RSA/ECB/OAEPWITHSHA-384ANDMGF1PADDING";
                case LEN_512:
                default:
                    return "RSA/ECB/OAEPWITHSHA-512ANDMGF1PADDING";
            }
        }
    }

    /**
     * Hybrid Encryptor.
     */
    public static class JcaHybridEncryptor
            extends GordianEncryptor {
        /**
         * The underlying encryptor.
         */
        private final Cipher theEncryptor;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the encryptorSpec
         * @throws OceanusException on error
         */
        JcaHybridEncryptor(final JcaFactory pFactory,
                           final GordianEncryptorSpec pSpec) throws OceanusException {
            /* Initialise underlying cipher */
            super(pFactory, pSpec);
            theEncryptor = JcaFactory.getJavaEncryptor(getAlgorithmName(pSpec), true);
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
        public void initForEncrypt(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
                /* Initialise underlying cipher */
                super.initForEncrypt(pKeyPair);

                /* Initialise for encryption */
                theEncryptor.init(Cipher.ENCRYPT_MODE, getPublicKey().getPublicKey(), getRandom());
            } catch (InvalidKeyException e) {
                throw new GordianCryptoException(ERROR_INIT, e);
            }
        }

        @Override
        public void initForDecrypt(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
                /* Initialise underlying cipher */
                super.initForDecrypt(pKeyPair);

                /* Initialise for decryption */
                theEncryptor.init(Cipher.DECRYPT_MODE, getPrivateKey().getPrivateKey());
            } catch (InvalidKeyException e) {
                throw new GordianCryptoException(ERROR_INIT, e);
            }
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
                return theEncryptor.doFinal(pData, 0, pData.length);
            } catch (IllegalBlockSizeException
                    | BadPaddingException e) {
                throw new GordianCryptoException("Failed to process data", e);
            }
        }

        /**
         * Obtain the algorithmName.
         * @param pSpec the Spec
         * @return the algorithm name
         */
        private static String getAlgorithmName(final GordianEncryptorSpec pSpec) {
            /* Switch on encryptor type */
            switch (pSpec.getMcElieceType()) {
                case FUJISAKI:
                    return "McElieceFujisaki";
                case KOBARAIMAI:
                    return "McElieceKobaraImai";
                case POINTCHEVAL:
                default:
                    return "McEliecePointcheval";
            }
        }
    }
}
