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

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.agreement.SM2KeyExchange;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.engines.SM2Engine.Mode;
import org.bouncycastle.crypto.generators.SM2KeyPairGenerator;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.SM2KeyExchangePrivateParameters;
import org.bouncycastle.crypto.params.SM2KeyExchangePublicParameters;
import org.bouncycastle.crypto.signers.SM2Signer;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianSM2EncryptionSpec;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianSM2EncryptionSpec.GordianSM2EncryptionType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECPrivateKey;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyEllipticKeyPair.BouncyECPublicKey;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianAgreementMessageASN1;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreEphemeralAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.encrypt.GordianCoreEncryptor;
import net.sourceforge.joceanus.jgordianknot.impl.core.sign.GordianCoreSignature;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SM2 KeyPair classes.
 */
public final class BouncySM2KeyPair {
    /**
     * Private constructor.
     */
    private BouncySM2KeyPair() {
    }

    /**
     * BouncyCastle Elliptic KeyPair generator.
     */
    public static class BouncySM2KeyPairGenerator
            extends BouncyECKeyPairGenerator {
        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws OceanusException on error
         */
        BouncySM2KeyPairGenerator(final BouncyFactory pFactory,
                                  final GordianKeyPairSpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);
        }

        @Override
        SM2KeyPairGenerator newGenerator() {
            return new SM2KeyPairGenerator();
        }
    }

    /**
     * SM2 signature.
     */
    public static class BouncySM2Signature
            extends GordianCoreSignature {
        /**
         * The Signer.
         */
        private final SM2Signer theSigner;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the signatureSpec.
         */
        BouncySM2Signature(final BouncyFactory pFactory,
                           final GordianSignatureSpec pSpec) {
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
     * EC SM2 Agreement.
     */
    public static class BouncyECSM2Agreement
            extends GordianCoreEphemeralAgreement {
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
            final SM2KeyExchangePrivateParameters myPrivParams = new SM2KeyExchangePrivateParameters(false,
                    myPrivate.getPrivateKey(), myEphPrivate.getPrivateKey());
            theAgreement.init(myPrivParams);

            /* Prepare for agreement */
            final BouncyECPublicKey mySrcPublic = (BouncyECPublicKey) getPublicKey(pClient);
            final BouncyECPublicKey mySrcEphPublic = (BouncyECPublicKey) getPublicKey(getClientEphemeralKeyPair());
            final SM2KeyExchangePublicParameters myPubParams = new SM2KeyExchangePublicParameters(mySrcPublic.getPublicKey(),
                    mySrcEphPublic.getPublicKey());

            /* If we are confirming */
            if (Boolean.TRUE.equals(getAgreementSpec().withConfirm())) {
                /* Create agreement and confirmation tags */
                final byte[][] myResults = theAgreement.calculateKeyWithConfirmation(KEYLEN, null, myPubParams);

                /* Store the confirmationTags */
                storeConfirmationTags(myResults[1], myResults[2]);

                /* Store the secret */
                storeSecret(myResults[0]);

                /* else standard agreement */
            } else {
                /* Calculate and store the secret */
                storeSecret(theAgreement.calculateKey(KEYLEN, myPubParams));
            }

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
            final SM2KeyExchangePrivateParameters myPrivParams = new SM2KeyExchangePrivateParameters(true,
                    myPrivate.getPrivateKey(), myEphPrivate.getPrivateKey());
            theAgreement.init(myPrivParams);

            /* Calculate agreement */
            final BouncyECPublicKey mySrcPublic = (BouncyECPublicKey) getPublicKey(pServer);
            final BouncyECPublicKey mySrcEphPublic = (BouncyECPublicKey) getPublicKey(getServerEphemeralKeyPair());
            final SM2KeyExchangePublicParameters myPubParams = new SM2KeyExchangePublicParameters(mySrcPublic.getPublicKey(),
                    mySrcEphPublic.getPublicKey());

            /* If we are confirming */
            if (Boolean.TRUE.equals(getAgreementSpec().withConfirm())) {
                /* Obtain confirmationTag in serverHello */
                final byte[] myConfirm = getServerConfirmationTag();

                /* Protect against exception */
                try {
                    /* Create agreement and confirmation tags */
                    final byte[][] myResults = theAgreement.calculateKeyWithConfirmation(KEYLEN, myConfirm, myPubParams);

                    /* Store the confirmationTag */
                    storeConfirmationTag(myResults[1]);

                    /* Store the secret */
                    storeSecret(myResults[0]);

                    /* Catch mismatch on confirmation tag */
                } catch (IllegalStateException e) {
                    throw new GordianIOException("Confirmation failed", e);
                }

                /* else standard agreement */
            } else {
                /* Calculate and store the secret */
                storeSecret(theAgreement.calculateKey(KEYLEN, myPubParams));
            }

            /* Return confirmation if needed */
            return buildClientConfirmASN1();
        }
    }

    /**
     * SM2 Encryptor.
     */
    public static class BouncySM2Encryptor
            extends GordianCoreEncryptor {
        /**
         * The underlying encryptor.
         */
        private final SM2Engine theEncryptor;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the encryptorSpec
         * @throws OceanusException on error
         */
        BouncySM2Encryptor(final BouncyFactory pFactory,
                           final GordianEncryptorSpec pSpec) throws OceanusException {
            /* Initialise underlying cipher */
            super(pFactory, pSpec);
            final BouncyDigestFactory myFactory = pFactory.getDigestFactory();
            final GordianSM2EncryptionSpec mySpec = pSpec.getSM2EncryptionSpec();
            final BouncyDigest myDigest = myFactory.createDigest(mySpec.getDigestSpec());
            final Mode mySM2Mode = mySpec.getEncryptionType() == GordianSM2EncryptionType.C1C2C3
                                   ? Mode.C1C2C3 : Mode.C1C3C2;
            theEncryptor = new SM2Engine(myDigest.getDigest(), mySM2Mode);
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
