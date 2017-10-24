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

import java.io.IOException;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.asn1.RainbowPrivateKey;
import org.bouncycastle.pqc.asn1.RainbowPublicKey;
import org.bouncycastle.pqc.crypto.rainbow.RainbowKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowKeyPairGenerator;
import org.bouncycastle.pqc.crypto.rainbow.RainbowParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPublicKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowSigner;
import org.bouncycastle.pqc.jcajce.provider.rainbow.BCRainbowPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.rainbow.BCRainbowPublicKey;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSigner;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianValidator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncySignature.BouncyDigestSignature;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * RSA AsymKey classes.
 */
public final class BouncyRainbowAsymKey {
    /**
     * Private constructor.
     */
    private BouncyRainbowAsymKey() {
    }

    /**
     * Bouncy Rainbow PublicKey.
     */
    public static class BouncyRainbowPublicKey
            extends BouncyPublicKey {
        /**
         * Public Key details.
         */
        private final RainbowPublicKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        protected BouncyRainbowPublicKey(final GordianAsymKeySpec pKeySpec,
                                         final RainbowPublicKeyParameters pPublicKey) {
            super(pKeySpec);
            theKey = pPublicKey;
        }

        /**
         * Obtain the public key.
         * @return the key
         */
        protected RainbowPublicKeyParameters getPublicKey() {
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
            if (!(pThat instanceof BouncyRainbowPublicKey)) {
                return false;
            }

            /* Access the target field */
            final BouncyRainbowPublicKey myThat = (BouncyRainbowPublicKey) pThat;

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
        private static boolean compareKeys(final RainbowPublicKeyParameters pFirst,
                                           final RainbowPublicKeyParameters pSecond) {
            return Arrays.equals(pFirst.getCoeffScalar(), pSecond.getCoeffScalar())
                   && Arrays.deepEquals(pFirst.getCoeffSingular(), pSecond.getCoeffSingular())
                   && Arrays.deepEquals(pFirst.getCoeffQuadratic(), pSecond.getCoeffQuadratic());
        }
    }

    /**
     * Bouncy Rainbow PrivateKey.
     */
    public static class BouncyRainbowPrivateKey
            extends BouncyPrivateKey {
        /**
         * Private Key details.
         */
        private final RainbowPrivateKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        protected BouncyRainbowPrivateKey(final GordianAsymKeySpec pKeySpec,
                                          final RainbowPrivateKeyParameters pPrivateKey) {
            super(pKeySpec);
            theKey = pPrivateKey;
        }

        /**
         * Obtain the private key.
         * @return the key
         */
        protected RainbowPrivateKeyParameters getPrivateKey() {
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
            if (!(pThat instanceof BouncyRainbowPrivateKey)) {
                return false;
            }

            /* Access the target field */
            final BouncyRainbowPrivateKey myThat = (BouncyRainbowPrivateKey) pThat;

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
        private static boolean compareKeys(final RainbowPrivateKeyParameters pFirst,
                                           final RainbowPrivateKeyParameters pSecond) {
            if (!Arrays.equals(pFirst.getB1(), pSecond.getB1())
                || !Arrays.equals(pFirst.getB2(), pSecond.getB2())) {
                return false;
            }
            if (!Arrays.deepEquals(pFirst.getInvA1(), pSecond.getInvA1())
                || !Arrays.deepEquals(pFirst.getInvA2(), pSecond.getInvA2())) {
                return false;
            }
            return Arrays.equals(pFirst.getVi(), pSecond.getVi())
                   && Arrays.equals(pFirst.getLayers(), pSecond.getLayers());
        }
    }

    /**
     * BouncyCastle Rainbow KeyPair generator.
     */
    public static class BouncyRainbowKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final RainbowKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        protected BouncyRainbowKeyPairGenerator(final BouncyFactory pFactory,
                                                final GordianAsymKeySpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = new RainbowKeyPairGenerator();
            final KeyGenerationParameters myParams = new RainbowKeyGenerationParameters(getRandom(), new RainbowParameters());
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyRainbowPublicKey myPublic = new BouncyRainbowPublicKey(getKeySpec(), RainbowPublicKeyParameters.class.cast(myPair.getPublic()));
            final BouncyRainbowPrivateKey myPrivate = new BouncyRainbowPrivateKey(getKeySpec(), RainbowPrivateKeyParameters.class.cast(myPair.getPrivate()));
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        protected PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) {
            final BouncyRainbowPrivateKey myPrivateKey = BouncyRainbowPrivateKey.class.cast(getPrivateKey(pKeyPair));
            final RainbowPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
            final BCRainbowPrivateKey myKey = new BCRainbowPrivateKey(myParms);
            return new PKCS8EncodedKeySpec(myKey.getEncoded());
        }

        @Override
        protected BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                              final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            try {
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final RainbowPrivateKey myKey = RainbowPrivateKey.getInstance(myInfo.parsePrivateKey());
                final BouncyRainbowPrivateKey myPrivate = new BouncyRainbowPrivateKey(getKeySpec(),
                        new RainbowPrivateKeyParameters(myKey.getInvA1(), myKey.getB1(), myKey.getInvA2(), myKey.getB2(), myKey.getVi(), myKey.getLayers()));
                final BouncyRainbowPublicKey myPublic = derivePublicKey(pPublicKey);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) {
            final BouncyRainbowPublicKey myPublicKey = BouncyRainbowPublicKey.class.cast(getPublicKey(pKeyPair));
            final RainbowPublicKeyParameters myParms = myPublicKey.getPublicKey();
            final BCRainbowPublicKey myKey = new BCRainbowPublicKey(myParms);
            return new X509EncodedKeySpec(myKey.getEncoded());
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            final BouncyRainbowPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws OceanusException on error
         */
        private BouncyRainbowPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            try {
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final RainbowPublicKey myKey = RainbowPublicKey.getInstance(myInfo.parsePublicKey());
                final RainbowPublicKeyParameters myParms = new RainbowPublicKeyParameters(myKey.getDocLength(),
                        myKey.getCoeffQuadratic(), myKey.getCoeffSingular(), myKey.getCoeffScalar());
                return new BouncyRainbowPublicKey(getKeySpec(), myParms);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * Rainbow signer.
     */
    public static class BouncyRainbowSigner
            extends BouncyDigestSignature
            implements GordianSigner {
        /**
         * The Rainbow Signer.
         */
        private final RainbowSigner theSigner;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pPrivateKey the private key
         * @param pSpec the signatureSpec.
         * @param pRandom the secure Random
         * @throws OceanusException on error
         */
        protected BouncyRainbowSigner(final BouncyFactory pFactory,
                                      final BouncyRainbowPrivateKey pPrivateKey,
                                      final GordianSignatureSpec pSpec,
                                      final SecureRandom pRandom) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the signer */
            theSigner = new RainbowSigner();

            /* Initialise and set the signer */
            final ParametersWithRandom myParms = new ParametersWithRandom(pPrivateKey.getPrivateKey(), pRandom);
            theSigner.init(true, myParms);
        }

        @Override
        public byte[] sign() throws OceanusException {
            /* Sign the message */
            return theSigner.generateSignature(getDigest());
        }
    }

    /**
     * Rainbow validator.
     */
    public static class BouncyRainbowValidator
            extends BouncyDigestSignature
            implements GordianValidator {
        /**
         * The Rainbow Signer.
         */
        private final RainbowSigner theSigner;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pPublicKey the public key
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error
         */
        protected BouncyRainbowValidator(final BouncyFactory pFactory,
                                         final BouncyRainbowPublicKey pPublicKey,
                                         final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the signer */
            theSigner = new RainbowSigner();

            /* Initialise and set the signer */
            theSigner.init(false, pPublicKey.getPublicKey());
        }

        @Override
        public boolean verify(final byte[] pSignature) throws OceanusException {
            return theSigner.verifySignature(getDigest(), pSignature);
        }
    }
}
