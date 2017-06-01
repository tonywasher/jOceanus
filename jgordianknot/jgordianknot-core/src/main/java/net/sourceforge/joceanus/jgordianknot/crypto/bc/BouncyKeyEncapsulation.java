/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto.bc;

import java.math.BigInteger;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.agreement.DHBasicAgreement;
import org.bouncycastle.crypto.kems.ECIESKeyEncapsulation;
import org.bouncycastle.crypto.kems.RSAKeyEncapsulation;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.pqc.crypto.ExchangePair;
import org.bouncycastle.pqc.crypto.newhope.NHAgreement;
import org.bouncycastle.pqc.crypto.newhope.NHExchangePairGenerator;
import org.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.newhope.BCNHPublicKey;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation.GordianKEMSender;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianModulus;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyDiffieHellmanPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyDiffieHellmanPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyECPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyECPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyNewHopePrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyNewHopePublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyRSAPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyRSAPublicKey;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * BouncyCastle Key Encapsulation.
 */
public abstract class BouncyKeyEncapsulation {
    /**
     * InitVectorLength.
     */
    private static final int INITLEN = 16;

    /**
     * Private Constructor.
     */
    private BouncyKeyEncapsulation() {
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
            BouncyKeyDerivation myKDF = new BouncyKeyDerivation(getDigest(pDigestSpec));
            RSAKeyEncapsulation myKEMS = new RSAKeyEncapsulation(myKDF, getRandom());

            /* Initialise the encapsulation */
            myKEMS.init(pPublicKey.getPublicKey());

            /* Create initVector */
            byte[] myInitVector = new byte[INITLEN];
            getRandom().nextBytes(myInitVector);

            /* Create cipherText */
            GordianModulus myModulus = pPublicKey.getKeySpec().getModulus();
            int myLen = myModulus.getModulus() / Byte.SIZE;
            byte[] myCipherText = new byte[myLen + INITLEN];
            KeyParameter myParms = (KeyParameter) myKEMS.encrypt(myCipherText, INITLEN, myKDF.getKeyLen());
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
            BouncyKeyDerivation myKDF = new BouncyKeyDerivation(getDigest(pDigestSpec));
            RSAKeyEncapsulation myKEMS = new RSAKeyEncapsulation(myKDF, null);

            /* Initialise the encapsulation */
            myKEMS.init(pPrivateKey.getPrivateKey());

            /* Obtain initVector */
            byte[] myInitVector = new byte[INITLEN];
            System.arraycopy(pCipherText, 0, myInitVector, 0, INITLEN);

            /* Parse cipherText */
            KeyParameter myParms = (KeyParameter) myKEMS.decrypt(pCipherText, INITLEN, pCipherText.length - INITLEN, myKDF.getKeyLen());

            /* Store secret */
            storeSecret(myParms.getKey(), myInitVector);
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
            BouncyKeyDerivation myKDF = new BouncyKeyDerivation(getDigest(pDigestSpec));
            ECIESKeyEncapsulation myKEMS = new ECIESKeyEncapsulation(myKDF, getRandom());

            /* Initialise the encapsulation */
            myKEMS.init(pPublicKey.getPublicKey());

            /* Create initVector */
            byte[] myInitVector = new byte[INITLEN];
            getRandom().nextBytes(myInitVector);

            /* Determine cipher text length */
            int myFieldSize = pPublicKey.getPublicKey().getParameters().getCurve().getFieldSize();
            myFieldSize = (myFieldSize + Byte.SIZE - 1) / Byte.SIZE;
            int myLen = (2 * myFieldSize) + 1;

            /* Create cipherText */
            byte[] myCipherText = new byte[myLen + INITLEN];
            KeyParameter myParms = (KeyParameter) myKEMS.encrypt(myCipherText, INITLEN, myKDF.getKeyLen());
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
            BouncyKeyDerivation myKDF = new BouncyKeyDerivation(getDigest(pDigestSpec));
            ECIESKeyEncapsulation myKEMS = new ECIESKeyEncapsulation(myKDF, null);

            /* Initialise the encapsulation */
            myKEMS.init(pPrivateKey.getPrivateKey());

            /* Obtain initVector */
            byte[] myInitVector = new byte[INITLEN];
            System.arraycopy(pCipherText, 0, myInitVector, 0, INITLEN);

            /* Parse cipherText */
            KeyParameter myParms = (KeyParameter) myKEMS.decrypt(pCipherText, INITLEN, pCipherText.length - INITLEN, myKDF.getKeyLen());

            /* Store secret */
            storeSecret(myParms.getKey(), myInitVector);
        }
    }

    /**
     * ClientDiffieHellman Encapsulation.
     */
    public static class BouncyDiffieHellmanSender
            extends GordianKEMSender {
        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pPublicKey the target publicKey
         * @param pDigestSpec the digestSpec
         * @throws OceanusException on error
         */
        protected BouncyDiffieHellmanSender(final BouncyFactory pFactory,
                                            final BouncyDiffieHellmanPublicKey pPublicKey,
                                            final GordianDigestSpec pDigestSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory);

            /* Create initVector */
            byte[] myInitVector = new byte[INITLEN];
            getRandom().nextBytes(myInitVector);

            /* Create an ephemeral New Hope key */
            BouncyKeyPairGenerator myGenerator = pFactory.getKeyPairGenerator(pPublicKey.getKeySpec());
            GordianKeyPair myPair = myGenerator.generateKeyPair();
            BouncyDiffieHellmanPrivateKey myPrivate = BouncyDiffieHellmanPrivateKey.class.cast(getPrivateKey(myPair));
            BouncyDiffieHellmanPublicKey myPublic = BouncyDiffieHellmanPublicKey.class.cast(getPublicKey(myPair));

            /* Derive the secret */
            DHBasicAgreement myAgreement = new DHBasicAgreement();
            myAgreement.init(myPrivate.getPrivateKey());
            BigInteger mySecret = myAgreement.calculateAgreement(pPublicKey.getPublicKey());

            /* Obtain the encoded keySpec of the public key */
            byte[] myY = myPublic.getPublicKey().getY().toByteArray();

            /* Create cipherText */
            int myLen = myY.length;
            byte[] myCipherText = new byte[myLen + INITLEN];
            System.arraycopy(myInitVector, 0, myCipherText, 0, INITLEN);
            System.arraycopy(myY, 0, myCipherText, INITLEN, myLen);

            /* Store secret and cipherText */
            storeSecret(hashSecret(mySecret.toByteArray(), getDigest(pDigestSpec)), myInitVector);
            storeCipherText(myCipherText);
        }
    }

    /**
     * ServerDiffieHellman Encapsulation.
     */
    public static class BouncyDiffieHellmanReceiver
            extends GordianKeyEncapsulation {
        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pPrivateKey the target privateKey
         * @param pDigestSpec the digestSpec
         * @param pCipherText the cipherText
         * @throws OceanusException on error
         */
        protected BouncyDiffieHellmanReceiver(final BouncyFactory pFactory,
                                              final BouncyDiffieHellmanPrivateKey pPrivateKey,
                                              final GordianDigestSpec pDigestSpec,
                                              final byte[] pCipherText) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory);

            /* Obtain initVector */
            byte[] myInitVector = new byte[INITLEN];
            System.arraycopy(pCipherText, 0, myInitVector, 0, INITLEN);

            /* Obtain ephemeral PublicKeySpec */
            int myYLen = pCipherText.length - INITLEN;
            byte[] myYbytes = new byte[myYLen];
            System.arraycopy(pCipherText, INITLEN, myYbytes, 0, myYLen);
            BigInteger myY = new BigInteger(myYbytes);
            DHParameters myParms = pPrivateKey.getPrivateKey().getParameters();
            DHPublicKeyParameters myPublicKey = new DHPublicKeyParameters(myY, myParms);

            /* Derive the secret */
            DHBasicAgreement myAgreement = new DHBasicAgreement();
            myAgreement.init(pPrivateKey.getPrivateKey());
            BigInteger mySecret = myAgreement.calculateAgreement(myPublicKey);

            /* Store secret */
            storeSecret(hashSecret(mySecret.toByteArray(), getDigest(pDigestSpec)), myInitVector);
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
            byte[] myInitVector = new byte[INITLEN];
            getRandom().nextBytes(myInitVector);

            /* Generate an Exchange KeyPair */
            NHExchangePairGenerator myGenerator = new NHExchangePairGenerator(getRandom());
            ExchangePair myPair = myGenerator.GenerateExchange(pPublicKey.getPublicKey());

            /* Derive the secret */
            byte[] mySecret = myPair.getSharedValue();

            /* Obtain the encoded keySpec of the public key */
            BCNHPublicKey myPublic = new BCNHPublicKey((NHPublicKeyParameters) myPair.getPublicKey());
            X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(myPublic.getEncoded());
            byte[] myKeySpecBytes = myKeySpec.getEncoded();

            /* Create cipherText */
            int myLen = myKeySpecBytes.length;
            byte[] myCipherText = new byte[myLen + INITLEN];
            System.arraycopy(myInitVector, 0, myCipherText, 0, INITLEN);
            System.arraycopy(myKeySpecBytes, 0, myCipherText, INITLEN, myLen);

            /* Store secret and cipherText */
            storeSecret(hashSecret(mySecret, getDigest(pDigestSpec)), myInitVector);
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
            byte[] myInitVector = new byte[INITLEN];
            System.arraycopy(pCipherText, 0, myInitVector, 0, INITLEN);

            /* Obtain ephemeral PublicKeySpec */
            int myX509Len = pCipherText.length - INITLEN;
            byte[] myX509bytes = new byte[myX509Len];
            System.arraycopy(pCipherText, INITLEN, myX509bytes, 0, myX509Len);
            X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(myX509bytes);

            /* Derive ephemeral Public key */
            BouncyKeyPairGenerator myGenerator = pFactory.getKeyPairGenerator(pPrivateKey.getKeySpec());
            GordianKeyPair myPair = myGenerator.derivePublicOnlyKeyPair(myKeySpec);
            BouncyNewHopePublicKey myPublic = BouncyNewHopePublicKey.class.cast(getPublicKey(myPair));

            /* Derive the secret */
            NHAgreement myAgreement = new NHAgreement();
            myAgreement.init(pPrivateKey.getPrivateKey());
            byte[] mySecret = myAgreement.calculateAgreement(myPublic.getPublicKey());

            /* Store secret */
            storeSecret(hashSecret(mySecret, getDigest(pDigestSpec)), myInitVector);
        }
    }

    /**
     * Hash secret.
     * @param pSecret the sharedSecret
     * @param pDigest the digest
     * @return the hashed secret
     */
    private static byte[] hashSecret(final byte[] pSecret,
                                     final GordianDigest pDigest) {
        pDigest.update(pSecret);
        return pDigest.finish();
    }

    /**
     * KeyDerivation.
     */
    private static final class BouncyKeyDerivation
            implements DerivationFunction {
        /**
         * Digest.
         */
        private final GordianDigest theDigest;

        /**
         * Constructor.
         * @param pDigest the security digest
         */
        private BouncyKeyDerivation(final GordianDigest pDigest) {
            theDigest = pDigest;
        }

        /**
         * Obtain the key length.
         * @return the keyLen
         */
        private int getKeyLen() {
            return theDigest.getDigestSize();
        }

        @Override
        public int generateBytes(final byte[] pBuffer,
                                 final int pOffset,
                                 final int pLength) {
            /* Protect against exceptions */
            try {
                return theDigest.finish(pBuffer, pOffset);
            } catch (OceanusException e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public void init(final DerivationParameters pParms) {
            theDigest.update(((KDFParameters) pParms).getSharedSecret());
        }
    }
}
