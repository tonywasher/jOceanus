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
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.DSAKeyPairGenerator;
import org.bouncycastle.crypto.generators.DSAParametersGenerator;
import org.bouncycastle.crypto.params.DSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.DSAParameterGenerationParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDSAKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSigner;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianValidator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncySignature.BouncyDERCoder;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncySignature.BouncyDigestSignature;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * DSA AsymKey classes.
 */
public final class BouncyDSAAsymKey {
    /**
     * Private constructor.
     */
    private BouncyDSAAsymKey() {
    }

    /**
     * Bouncy DSA PublicKey.
     */
    public static class BouncyDSAPublicKey
            extends BouncyPublicKey {
        /**
         * Public Key details.
         */
        private final DSAPublicKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        protected BouncyDSAPublicKey(final GordianAsymKeySpec pKeySpec,
                                     final DSAPublicKeyParameters pPublicKey) {
            super(pKeySpec);
            theKey = pPublicKey;
        }

        /**
         * Obtain the public key.
         * @return the key
         */
        protected DSAPublicKeyParameters getPublicKey() {
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
            if (!(pThat instanceof BouncyDSAPublicKey)) {
                return false;
            }

            /* Access the target field */
            final BouncyDSAPublicKey myThat = (BouncyDSAPublicKey) pThat;

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
        public boolean validPrivate(final BouncyDSAPrivateKey pPrivate) {
            final DSAPrivateKeyParameters myPrivate = pPrivate.getPrivateKey();
            return theKey.getParameters().equals(myPrivate.getParameters());
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final DSAPublicKeyParameters pFirst,
                                           final DSAPublicKeyParameters pSecond) {
            return pFirst.getY().equals(pSecond.getY())
                   && pFirst.getParameters().equals(pSecond.getParameters());
        }
    }

    /**
     * Bouncy DSA PrivateKey.
     */
    public static class BouncyDSAPrivateKey
            extends BouncyPrivateKey {
        /**
         * Private Key details.
         */
        private final DSAPrivateKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        protected BouncyDSAPrivateKey(final GordianAsymKeySpec pKeySpec,
                                      final DSAPrivateKeyParameters pPrivateKey) {
            super(pKeySpec);
            theKey = pPrivateKey;
        }

        /**
         * Obtain the private key.
         * @return the key
         */
        protected DSAPrivateKeyParameters getPrivateKey() {
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
            if (!(pThat instanceof BouncyDSAPrivateKey)) {
                return false;
            }

            /* Access the target field */
            final BouncyDSAPrivateKey myThat = (BouncyDSAPrivateKey) pThat;

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
        private static boolean compareKeys(final DSAPrivateKeyParameters pFirst,
                                           final DSAPrivateKeyParameters pSecond) {
            return pFirst.getX().equals(pSecond.getX())
                   && pFirst.getParameters().equals(pSecond.getParameters());
        }
    }

    /**
     * BouncyCastle DSA KeyPair generator.
     */
    public static class BouncyDSAKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final DSAKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        protected BouncyDSAKeyPairGenerator(final BouncyFactory pFactory,
                                            final GordianAsymKeySpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create the parameter generator */
            final GordianDSAKeyType myKeyType = pKeySpec.getDSAKeyType();
            final DSAParameterGenerationParameters myGenParms = new DSAParameterGenerationParameters(myKeyType.getKeySize(),
                    myKeyType.getHashSize(), PRIME_CERTAINTY, getRandom());
            final DSAParametersGenerator myParmGenerator = new DSAParametersGenerator(new SHA256Digest());
            myParmGenerator.init(myGenParms);

            /* Create and initialise the generator */
            theGenerator = new DSAKeyPairGenerator();
            final DSAKeyGenerationParameters myParams = new DSAKeyGenerationParameters(getRandom(), myParmGenerator.generateParameters());
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyDSAPublicKey myPublic = new BouncyDSAPublicKey(getKeySpec(), DSAPublicKeyParameters.class.cast(myPair.getPublic()));
            final BouncyDSAPrivateKey myPrivate = new BouncyDSAPrivateKey(getKeySpec(), DSAPrivateKeyParameters.class.cast(myPair.getPrivate()));
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws GordianCryptoException {
            try {
                final BouncyDSAPrivateKey myPrivateKey = BouncyDSAPrivateKey.class.cast(getPrivateKey(pKeyPair));
                final DSAPrivateKeyParameters myKey = myPrivateKey.getPrivateKey();
                final DSAParameters myParms = myKey.getParameters();
                final PrivateKeyInfo myInfo = new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, new DSAParameter(myParms.getP(), myParms.getQ(), myParms.getG())
                        .toASN1Primitive()), new ASN1Integer(myKey.getX()));
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
                final DSAParameter myParams = DSAParameter.getInstance(myInfo.getPrivateKeyAlgorithm().getParameters());
                final ASN1Integer myX = ASN1Integer.getInstance(myInfo.parsePrivateKey());
                final DSAParameters myParms = new DSAParameters(myParams.getP(), myParams.getQ(), myParams.getG());
                final BouncyDSAPrivateKey myPrivate = new BouncyDSAPrivateKey(getKeySpec(), new DSAPrivateKeyParameters(myX.getValue(), myParms));
                final BouncyDSAPublicKey myPublic = derivePublicKey(pPublicKey);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) {
            final BouncyDSAPublicKey myPublicKey = BouncyDSAPublicKey.class.cast(getPublicKey(pKeyPair));
            final DSAPublicKeyParameters myKey = myPublicKey.getPublicKey();
            final DSAParameters myParms = myKey.getParameters();
            final byte[] myBytes = KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa,
                    new DSAParameter(myParms.getP(), myParms.getQ(), myParms.getG()).toASN1Primitive()), new ASN1Integer(myKey.getY()));
            return new X509EncodedKeySpec(myBytes);
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            final BouncyDSAPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws OceanusException on error
         */
        private BouncyDSAPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            try {
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final DSAParameter myParams = DSAParameter.getInstance(myInfo.getAlgorithm().getParameters());
                final ASN1Integer myY = ASN1Integer.getInstance(myInfo.parsePublicKey());
                final DSAParameters myParms = new DSAParameters(myParams.getP(), myParams.getQ(), myParams.getG());
                return new BouncyDSAPublicKey(getKeySpec(), new DSAPublicKeyParameters(myY.getValue(), myParms));
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * DSA signer.
     */
    public static class BouncyDSASigner
            extends BouncyDigestSignature
            implements GordianSigner {
        /**
         * The Signer.
         */
        private final DSA theSigner;

        /**
         * The Coder.
         */
        private final BouncyDERCoder theCoder;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pPrivateKey the private key
         * @param pSpec the signatureSpec.
         * @param pRandom the random generator
         * @throws OceanusException on error
         */
        protected BouncyDSASigner(final BouncyFactory pFactory,
                                  final BouncyDSAPrivateKey pPrivateKey,
                                  final GordianSignatureSpec pSpec,
                                  final SecureRandom pRandom) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the signer */
            theSigner = BouncySignature.getDSASigner(pFactory, pPrivateKey.getKeySpec(), pSpec);
            theCoder = new BouncyDERCoder();

            /* Initialise and set the signer */
            final ParametersWithRandom myParms = new ParametersWithRandom(pPrivateKey.getPrivateKey(), pRandom);
            theSigner.init(true, myParms);
        }

        @Override
        public byte[] sign() throws OceanusException {
            final BigInteger[] myValues = theSigner.generateSignature(getDigest());
            return theCoder.dsaEncode(myValues[0], myValues[1]);
        }
    }

    /**
     * DSA validator.
     */
    public static class BouncyDSAValidator
            extends BouncyDigestSignature
            implements GordianValidator {
        /**
         * The EC Signer.
         */
        private final DSA theSigner;

        /**
         * The Coder.
         */
        private final BouncyDERCoder theCoder;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pPublicKey the public key
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error-
         */
        protected BouncyDSAValidator(final BouncyFactory pFactory,
                                     final BouncyDSAPublicKey pPublicKey,
                                     final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the signer */
            theSigner = BouncySignature.getDSASigner(pFactory, pPublicKey.getKeySpec(), pSpec);
            theCoder = new BouncyDERCoder();

            /* Initialise and set the signer */
            theSigner.init(false, pPublicKey.getPublicKey());
        }

        @Override
        public boolean verify(final byte[] pSignature) throws OceanusException {
            final BigInteger[] myValues = theCoder.dsaDecode(pSignature);
            return theSigner.verifySignature(getDigest(), myValues[0], myValues[1]);
        }
    }
}
