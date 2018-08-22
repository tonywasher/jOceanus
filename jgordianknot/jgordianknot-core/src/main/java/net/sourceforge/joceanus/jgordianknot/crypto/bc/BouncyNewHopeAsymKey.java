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
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.ExchangePair;
import org.bouncycastle.pqc.crypto.newhope.NHAgreement;
import org.bouncycastle.pqc.crypto.newhope.NHExchangePairGenerator;
import org.bouncycastle.pqc.crypto.newhope.NHKeyPairGenerator;
import org.bouncycastle.pqc.crypto.newhope.NHPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.newhope.BCNHPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.newhope.BCNHPublicKey;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreement.GordianEncapsulationAgreement;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation.GordianKEMSender;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianModulus;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * NewHope AsymKey classes.
 */
public final class BouncyNewHopeAsymKey {
    /**
     * Private constructor.
     */
    private BouncyNewHopeAsymKey() {
    }

    /**
     * Bouncy NewHope PublicKey.
     */
    public static class BouncyNewHopePublicKey
            extends BouncyPublicKey {
        /**
         * Public Key details.
         */
        private final NHPublicKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        protected BouncyNewHopePublicKey(final GordianAsymKeySpec pKeySpec,
                                         final NHPublicKeyParameters pPublicKey) {
            super(pKeySpec);
            theKey = pPublicKey;
        }

        /**
         * Obtain the public key.
         * @return the key
         */
        protected NHPublicKeyParameters getPublicKey() {
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
            if (!(pThat instanceof BouncyNewHopePublicKey)) {
                return false;
            }

            /* Access the target field */
            final BouncyNewHopePublicKey myThat = (BouncyNewHopePublicKey) pThat;

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
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final NHPublicKeyParameters pFirst,
                                           final NHPublicKeyParameters pSecond) {
            return Arrays.equals(pFirst.getPubData(), pSecond.getPubData());
        }
    }

    /**
     * Bouncy NewHope PrivateKey.
     */
    public static class BouncyNewHopePrivateKey
            extends BouncyPrivateKey {
        /**
         * Private Key details.
         */
        private final NHPrivateKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        protected BouncyNewHopePrivateKey(final GordianAsymKeySpec pKeySpec,
                                          final NHPrivateKeyParameters pPrivateKey) {
            super(pKeySpec);
            theKey = pPrivateKey;
        }

        /**
         * Obtain the private key.
         * @return the key
         */
        protected NHPrivateKeyParameters getPrivateKey() {
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
            if (!(pThat instanceof BouncyNewHopePrivateKey)) {
                return false;
            }

            /* Access the target field */
            final BouncyNewHopePrivateKey myThat = (BouncyNewHopePrivateKey) pThat;

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
        private static boolean compareKeys(final NHPrivateKeyParameters pFirst,
                                           final NHPrivateKeyParameters pSecond) {
            return Arrays.equals(pFirst.getSecData(), pSecond.getSecData());
        }
    }

    /**
     * BouncyCastle NewHope KeyPair generator.
     */
    public static class BouncyNewHopeKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final NHKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        protected BouncyNewHopeKeyPairGenerator(final BouncyFactory pFactory,
                                                final GordianAsymKeySpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = new NHKeyPairGenerator();
            final KeyGenerationParameters myParams = new KeyGenerationParameters(getRandom(), GordianModulus.MOD1024.getModulus());
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyNewHopePublicKey myPublic = new BouncyNewHopePublicKey(getKeySpec(), NHPublicKeyParameters.class.cast(myPair.getPublic()));
            final BouncyNewHopePrivateKey myPrivate = new BouncyNewHopePrivateKey(getKeySpec(), NHPrivateKeyParameters.class.cast(myPair.getPrivate()));
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) {
            final BouncyNewHopePrivateKey myPrivateKey = BouncyNewHopePrivateKey.class.cast(getPrivateKey(pKeyPair));
            final NHPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
            final BCNHPrivateKey myKey = new BCNHPrivateKey(myParms);
            return new PKCS8EncodedKeySpec(myKey.getEncoded());
        }

        @Override
        public BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                           final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            try {
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final BCNHPrivateKey myKey = new BCNHPrivateKey(myInfo);
                final BouncyNewHopePrivateKey myPrivate = new BouncyNewHopePrivateKey(getKeySpec(), new NHPrivateKeyParameters(myKey.getSecretData()));
                final BouncyNewHopePublicKey myPublic = derivePublicKey(pPublicKey);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) {
            final BouncyNewHopePublicKey myPublicKey = BouncyNewHopePublicKey.class.cast(getPublicKey(pKeyPair));
            final NHPublicKeyParameters myParms = myPublicKey.getPublicKey();
            final BCNHPublicKey myKey = new BCNHPublicKey(myParms);
            return new X509EncodedKeySpec(myKey.getEncoded());
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) {
            final BouncyNewHopePublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         */
        private BouncyNewHopePublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) {
            final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
            final NHPublicKeyParameters myParms = new NHPublicKeyParameters(myInfo.getPublicKeyData().getBytes());
            return new BouncyNewHopePublicKey(getKeySpec(), myParms);
        }
    }

    /**
     * ClientNewHope Encapsulation.
     */
    public static class BouncyNewHopeSender
            extends GordianKEMSender {
        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pPublicKey the target publicKey
         * @param pDigestSpec the digestSpec
         * @throws OceanusException on error
         */
        protected BouncyNewHopeSender(final BouncyFactory pFactory,
                                      final BouncyNewHopePublicKey pPublicKey,
                                      final GordianDigestSpec pDigestSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory);

            /* Create initVector */
            final byte[] myInitVector = new byte[INITLEN];
            getRandom().nextBytes(myInitVector);

            /* Generate an Exchange KeyPair */
            final NHExchangePairGenerator myGenerator = new NHExchangePairGenerator(getRandom());
            final ExchangePair myPair = myGenerator.GenerateExchange(pPublicKey.getPublicKey());

            /* Derive the secret */
            final byte[] mySecret = myPair.getSharedValue();

            /* Obtain the encoded keySpec of the public key */
            final BCNHPublicKey myPublic = new BCNHPublicKey((NHPublicKeyParameters) myPair.getPublicKey());
            final X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(myPublic.getEncoded());
            final byte[] myKeySpecBytes = myKeySpec.getEncoded();

            /* Create cipherText */
            final int myLen = myKeySpecBytes.length;
            final byte[] myCipherText = new byte[myLen + INITLEN];
            System.arraycopy(myInitVector, 0, myCipherText, 0, INITLEN);
            System.arraycopy(myKeySpecBytes, 0, myCipherText, INITLEN, myLen);

            /* Store secret and cipherText */
            storeSecret(BouncyKeyEncapsulation.hashSecret(mySecret, getDigest(pDigestSpec)), myInitVector);
            storeCipherText(myCipherText);
        }
    }

    /**
     * ServerNewHope Encapsulation.
     */
    public static class BouncyNewHopeReceiver
            extends GordianKeyEncapsulation {
        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pPrivateKey the target privateKey
         * @param pDigestSpec the digestSpec
         * @param pCipherText the cipherText
         * @throws OceanusException on error
         */
        protected BouncyNewHopeReceiver(final BouncyFactory pFactory,
                                        final BouncyNewHopePrivateKey pPrivateKey,
                                        final GordianDigestSpec pDigestSpec,
                                        final byte[] pCipherText) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory);

            /* Obtain initVector */
            final byte[] myInitVector = new byte[INITLEN];
            System.arraycopy(pCipherText, 0, myInitVector, 0, INITLEN);

            /* Obtain ephemeral PublicKeySpec */
            final int myX509Len = pCipherText.length - INITLEN;
            final byte[] myX509bytes = new byte[myX509Len];
            System.arraycopy(pCipherText, INITLEN, myX509bytes, 0, myX509Len);
            final X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(myX509bytes);

            /* Derive ephemeral Public key */
            final BouncyKeyPairGenerator myGenerator = pFactory.getKeyPairGenerator(pPrivateKey.getKeySpec());
            final GordianKeyPair myPair = myGenerator.derivePublicOnlyKeyPair(myKeySpec);
            final BouncyNewHopePublicKey myPublic = BouncyNewHopePublicKey.class.cast(getPublicKey(myPair));

            /* Derive the secret */
            final NHAgreement myAgreement = new NHAgreement();
            myAgreement.init(pPrivateKey.getPrivateKey());
            final byte[] mySecret = myAgreement.calculateAgreement(myPublic.getPublicKey());

            /* Store secret */
            storeSecret(BouncyKeyEncapsulation.hashSecret(mySecret, getDigest(pDigestSpec)), myInitVector);
        }
    }

    /**
     * NewHope Encapsulation.
     */
    public static class BouncyNewHopeAgreement
            extends GordianEncapsulationAgreement {
        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the digestSpec
         */
        BouncyNewHopeAgreement(final BouncyFactory pFactory,
                               final GordianAgreementSpec pSpec) {
            /* Initialise underlying class */
            super(pFactory, pSpec);
        }

        @Override
        public byte[] initiateAgreement(final GordianKeyPair pTarget) throws OceanusException {
             /* Check keyPair */
            checkKeyPair(pTarget);

            /* Generate an Exchange KeyPair */
            final NHExchangePairGenerator myGenerator = new NHExchangePairGenerator(getRandom());
            final BouncyNewHopePublicKey myTarget = (BouncyNewHopePublicKey) getPublicKey(pTarget);
            final ExchangePair myPair = myGenerator.GenerateExchange(myTarget.getPublicKey());

            /* Derive the secret */
            final byte[] mySecret = myPair.getSharedValue();
            storeSecret(mySecret);

            /* Obtain the encoded keySpec of the public key */
            final BCNHPublicKey myPublic = new BCNHPublicKey((NHPublicKeyParameters) myPair.getPublicKey());
            final X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(myPublic.getEncoded());
            final byte[] myKeySpecBytes = myKeySpec.getEncoded();

            /* Create the message  */
            return createMessage(myKeySpecBytes);
        }

        @Override
        public void acceptAgreement(final GordianKeyPair pSelf,
                                    final byte[] pMessage) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pSelf);

            /* Obtain keySpec */
            final byte[] myX509bytes = parseMessage(pMessage);
            final X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(myX509bytes);

            /* Derive ephemeral Public key */
            final GordianKeyPairGenerator myGenerator = getFactory().getKeyPairGenerator(pSelf.getKeySpec());
            final GordianKeyPair myPair = myGenerator.derivePublicOnlyKeyPair(myKeySpec);
            final BouncyNewHopePublicKey myPublic = (BouncyNewHopePublicKey) getPublicKey(myPair);

            /* Derive the secret */
            final BouncyNewHopePrivateKey myPrivate = (BouncyNewHopePrivateKey) getPrivateKey(pSelf);
            final NHAgreement myAgreement = new NHAgreement();
            myAgreement.init(myPrivate.getPrivateKey());
            final byte[] mySecret = myAgreement.calculateAgreement(myPublic.getPublicKey());

            /* Store secret */
            storeSecret(mySecret);
        }
    }
}
