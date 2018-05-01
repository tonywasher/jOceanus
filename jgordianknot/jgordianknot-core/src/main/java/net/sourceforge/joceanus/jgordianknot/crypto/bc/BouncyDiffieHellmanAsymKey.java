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

import javax.crypto.spec.DHParameterSpec;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.pkcs.DHParameter;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.agreement.DHBasicAgreement;
import org.bouncycastle.crypto.generators.DHKeyPairGenerator;
import org.bouncycastle.crypto.params.DHKeyGenerationParameters;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation.GordianKEMSender;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianModulus;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * DiffieHellman AsymKey classes.
 */
public final class BouncyDiffieHellmanAsymKey {
    /**
     * Private constructor.
     */
    private BouncyDiffieHellmanAsymKey() {
    }

    /**
     * Bouncy DiffieHellman PublicKey.
     */
    public static class BouncyDiffieHellmanPublicKey
            extends BouncyPublicKey {
        /**
         * Public Key details.
         */
        private final DHPublicKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        protected BouncyDiffieHellmanPublicKey(final GordianAsymKeySpec pKeySpec,
                                               final DHPublicKeyParameters pPublicKey) {
            super(pKeySpec);
            theKey = pPublicKey;
        }

        /**
         * Obtain the public key.
         * @return the key
         */
        protected DHPublicKeyParameters getPublicKey() {
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
            if (!(pThat instanceof BouncyDiffieHellmanPublicKey)) {
                return false;
            }

            /* Access the target field */
            final BouncyDiffieHellmanPublicKey myThat = (BouncyDiffieHellmanPublicKey) pThat;

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
        public boolean validPrivate(final BouncyDiffieHellmanPrivateKey pPrivate) {
            final DHPrivateKeyParameters myPrivate = pPrivate.getPrivateKey();
            return theKey.getParameters().equals(myPrivate.getParameters());
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final DHPublicKeyParameters pFirst,
                                           final DHPublicKeyParameters pSecond) {
            return pFirst.getY().equals(pSecond.getY())
                   && pFirst.getParameters().equals(pSecond.getParameters());
        }
    }

    /**
     * Bouncy DiffieHellman PrivateKey.
     */
    public static class BouncyDiffieHellmanPrivateKey
            extends BouncyPrivateKey {
        /**
         * Private Key details.
         */
        private final DHPrivateKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        protected BouncyDiffieHellmanPrivateKey(final GordianAsymKeySpec pKeySpec,
                                                final DHPrivateKeyParameters pPrivateKey) {
            super(pKeySpec);
            theKey = pPrivateKey;
        }

        /**
         * Obtain the private key.
         * @return the key
         */
        protected DHPrivateKeyParameters getPrivateKey() {
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
            if (!(pThat instanceof BouncyDiffieHellmanPrivateKey)) {
                return false;
            }

            /* Access the target field */
            final BouncyDiffieHellmanPrivateKey myThat = (BouncyDiffieHellmanPrivateKey) pThat;

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
        private static boolean compareKeys(final DHPrivateKeyParameters pFirst,
                                           final DHPrivateKeyParameters pSecond) {
            return pFirst.getX().equals(pSecond.getX())
                   && pFirst.getParameters().equals(pSecond.getParameters());
        }
    }

    /**
     * BouncyCastle DiffieHellman KeyPair generator.
     */
    public static class BouncyDiffieHellmanKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final DHKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        protected BouncyDiffieHellmanKeyPairGenerator(final BouncyFactory pFactory,
                                                      final GordianAsymKeySpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create the parameter generator */
            final GordianModulus myModulus = pKeySpec.getModulus();
            final DHParameters myParms = myModulus.getDHParameters();
            final DHKeyGenerationParameters myParams = new DHKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            theGenerator = new DHKeyPairGenerator();
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyDiffieHellmanPublicKey myPublic = new BouncyDiffieHellmanPublicKey(getKeySpec(), DHPublicKeyParameters.class.cast(myPair.getPublic()));
            final BouncyDiffieHellmanPrivateKey myPrivate = new BouncyDiffieHellmanPrivateKey(getKeySpec(), DHPrivateKeyParameters.class.cast(myPair.getPrivate()));
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        protected PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws GordianCryptoException {
            try {
                final BouncyDiffieHellmanPrivateKey myPrivateKey = BouncyDiffieHellmanPrivateKey.class.cast(getPrivateKey(pKeyPair));
                final DHPrivateKeyParameters myKey = myPrivateKey.getPrivateKey();
                final DHParameters myParms = myKey.getParameters();
                final PrivateKeyInfo myInfo = new PrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.dhKeyAgreement,
                        new DHParameter(myParms.getP(), myParms.getG(), myParms.getL()).toASN1Primitive()), new ASN1Integer(myKey.getX()));
                return new PKCS8EncodedKeySpec(myInfo.getEncoded());
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        protected BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                              final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            try {
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final BCDHPrivateKey myKey = new BCDHPrivateKey(myInfo);
                final DHParameterSpec mySpec = myKey.getParams();
                final DHParameters myParms = new DHParameters(mySpec.getP(), mySpec.getG());
                final BouncyDiffieHellmanPrivateKey myPrivate = new BouncyDiffieHellmanPrivateKey(getKeySpec(), new DHPrivateKeyParameters(myKey.getX(), myParms));
                final BouncyDiffieHellmanPublicKey myPublic = derivePublicKey(pPublicKey);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) {
            final BouncyDiffieHellmanPublicKey myPublicKey = BouncyDiffieHellmanPublicKey.class.cast(getPublicKey(pKeyPair));
            final DHPublicKeyParameters myKey = myPublicKey.getPublicKey();
            final DHParameters myParms = myKey.getParameters();
            final byte[] myBytes = KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.dhKeyAgreement,
                    new DHParameter(myParms.getP(), myParms.getG(), myParms.getL()).toASN1Primitive()), new ASN1Integer(myKey.getY()));
            return new X509EncodedKeySpec(myBytes);
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) {
            final BouncyDiffieHellmanPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         */
        private BouncyDiffieHellmanPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) {
            final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
            final BCDHPublicKey myKey = new BCDHPublicKey(myInfo);
            return new BouncyDiffieHellmanPublicKey(getKeySpec(), myKey.engineGetKeyParameters());
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
            final byte[] myInitVector = new byte[INITLEN];
            getRandom().nextBytes(myInitVector);

            /* Create an ephemeral New Hope key */
            final BouncyKeyPairGenerator myGenerator = pFactory.getKeyPairGenerator(pPublicKey.getKeySpec());
            final GordianKeyPair myPair = myGenerator.generateKeyPair();
            final BouncyDiffieHellmanPrivateKey myPrivate = BouncyDiffieHellmanPrivateKey.class.cast(getPrivateKey(myPair));
            final BouncyDiffieHellmanPublicKey myPublic = BouncyDiffieHellmanPublicKey.class.cast(getPublicKey(myPair));

            /* Derive the secret */
            final DHBasicAgreement myAgreement = new DHBasicAgreement();
            myAgreement.init(myPrivate.getPrivateKey());
            final BigInteger mySecret = myAgreement.calculateAgreement(pPublicKey.getPublicKey());

            /* Obtain the encoded keySpec of the public key */
            final byte[] myY = myPublic.getPublicKey().getY().toByteArray();

            /* Create cipherText */
            final int myLen = myY.length;
            final byte[] myCipherText = new byte[myLen + INITLEN];
            System.arraycopy(myInitVector, 0, myCipherText, 0, INITLEN);
            System.arraycopy(myY, 0, myCipherText, INITLEN, myLen);

            /* Store secret and cipherText */
            storeSecret(BouncyKeyEncapsulation.hashSecret(mySecret.toByteArray(), getDigest(pDigestSpec)), myInitVector);
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
            final byte[] myInitVector = new byte[INITLEN];
            System.arraycopy(pCipherText, 0, myInitVector, 0, INITLEN);

            /* Obtain ephemeral PublicKeySpec */
            final int myYLen = pCipherText.length - INITLEN;
            final byte[] myYbytes = new byte[myYLen];
            System.arraycopy(pCipherText, INITLEN, myYbytes, 0, myYLen);
            final BigInteger myY = new BigInteger(myYbytes);
            final DHParameters myParms = pPrivateKey.getPrivateKey().getParameters();
            final DHPublicKeyParameters myPublicKey = new DHPublicKeyParameters(myY, myParms);

            /* Derive the secret */
            final DHBasicAgreement myAgreement = new DHBasicAgreement();
            myAgreement.init(pPrivateKey.getPrivateKey());
            final BigInteger mySecret = myAgreement.calculateAgreement(myPublicKey);

            /* Store secret */
            storeSecret(BouncyKeyEncapsulation.hashSecret(mySecret.toByteArray(), getDigest(pDigestSpec)), myInitVector);
        }
    }
}
