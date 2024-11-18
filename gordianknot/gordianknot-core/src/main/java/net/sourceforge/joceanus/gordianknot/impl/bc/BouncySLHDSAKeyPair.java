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
package net.sourceforge.joceanus.gordianknot.impl.bc;

import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncySignature.BouncyDigestSignature;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianKeyPairValidity;
import net.sourceforge.joceanus.jtethys.OceanusException;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.slhdsa.HashSLHDSASigner;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAKeyPairGenerator;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSASigner;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory;

import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

/**
 * SPHINCSPlus KeyPair classes.
 */
public final class BouncySLHDSAKeyPair {
    /**
     * Private constructor.
     */
    private BouncySLHDSAKeyPair() {
    }

    /**
     * Bouncy SLHDSA PublicKey.
     */
    public static class BouncySLHDSAPublicKey
            extends BouncyPublicKey<SLHDSAPublicKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        BouncySLHDSAPublicKey(final GordianKeyPairSpec pKeySpec,
                              final SLHDSAPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SLHDSAPublicKeyParameters myThis = getPublicKey();
            final SLHDSAPublicKeyParameters myThat = (SLHDSAPublicKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final SLHDSAPublicKeyParameters pFirst,
                                           final SLHDSAPublicKeyParameters pSecond) {
            return Arrays.equals(pFirst.getSeed(), pSecond.getSeed())
                    && Arrays.equals(pFirst.getRoot(), pSecond.getRoot());
        }
    }

    /**
     * Bouncy SLHDSA PrivateKey.
     */
    public static class BouncySLHDSAPrivateKey
            extends BouncyPrivateKey<SLHDSAPrivateKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        BouncySLHDSAPrivateKey(final GordianKeyPairSpec pKeySpec,
                               final SLHDSAPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SLHDSAPrivateKeyParameters myThis = getPrivateKey();
            final SLHDSAPrivateKeyParameters myThat = (SLHDSAPrivateKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final SLHDSAPrivateKeyParameters pFirst,
                                           final SLHDSAPrivateKeyParameters pSecond) {
            return Arrays.equals(pFirst.getSeed(), pSecond.getSeed())
                   && Arrays.equals(pFirst.getPrf(), pSecond.getPrf());
        }
    }

    /**
     * BouncyCastle SLHDSA KeyPair generator.
     */
    public static class BouncySLHDSAKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final SLHDSAKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncySLHDSAKeyPairGenerator(final BouncyFactory pFactory,
                                     final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final SLHDSAParameters myParms = pKeySpec.getSLHDSAKeySpec().getParameters();

            /* Create and initialise the generator */
            theGenerator = new SLHDSAKeyPairGenerator();
            final SLHDSAKeyGenerationParameters myParams = new SLHDSAKeyGenerationParameters(getRandom(), myParms);
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncySLHDSAPublicKey myPublic = new BouncySLHDSAPublicKey(getKeySpec(), (SLHDSAPublicKeyParameters) myPair.getPublic());
            final BouncySLHDSAPrivateKey myPrivate = new BouncySLHDSAPrivateKey(getKeySpec(), (SLHDSAPrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check the keyPair type and keySpecs */
                BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

                /* build and return the encoding */
                final BouncySLHDSAPrivateKey myPrivateKey = (BouncySLHDSAPrivateKey) getPrivateKey(pKeyPair);
                final SLHDSAPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
                final PrivateKeyInfo myInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(myParms, null);
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
                final BouncySLHDSAPublicKey myPublic = derivePublicKey(pPublicKey);
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final SLHDSAPrivateKeyParameters myParms = (SLHDSAPrivateKeyParameters) PrivateKeyFactory.createKey(myInfo);
                final BouncySLHDSAPrivateKey myPrivate = new BouncySLHDSAPrivateKey(getKeySpec(), myParms);
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
                final BouncySLHDSAPublicKey myPublicKey = (BouncySLHDSAPublicKey) getPublicKey(pKeyPair);
                final SLHDSAPublicKeyParameters myParms = myPublicKey.getPublicKey();
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(myParms);
                return new X509EncodedKeySpec(myInfo.getEncoded());

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            final BouncySLHDSAPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws OceanusException on error
         */
        private BouncySLHDSAPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pEncodedKey);

                /* derive publicKey */
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final SLHDSAPublicKeyParameters myParms = (SLHDSAPublicKeyParameters) PublicKeyFactory.createKey(myInfo);
                return new BouncySLHDSAPublicKey(getKeySpec(), myParms);

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * SLHDSA signer.
     */
    public static class BouncySLHDSASignature
            extends BouncyDigestSignature {
        /**
         * The SLHDSA Signer.
         */
        private final SLHDSASigner theSigner;

        /**
         * The SLHDSAHash Signer.
         */
        private final HashSLHDSASigner theHashSigner;

        /**
         * Is this a hash signer?
         */
        private boolean isHash;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error
         */
        BouncySLHDSASignature(final BouncyFactory pFactory,
                              final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);
            theSigner = new SLHDSASigner();
            theHashSigner = new HashSLHDSASigner();
        }

        @Override
        public void initForSigning(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            BouncyKeyPair.checkKeyPair(pKeyPair);
            super.initForSigning(pKeyPair);

            /* Determine whether this is a hashSigner */
            isHash = pKeyPair.getKeyPairSpec().getSLHDSAKeySpec().isHash();

            /* Initialise and set the signer */
            final BouncySLHDSAPrivateKey myPrivate = (BouncySLHDSAPrivateKey) getKeyPair().getPrivateKey();
            CipherParameters myParms = new ParametersWithRandom(myPrivate.getPrivateKey(), getRandom());
            if (isHash) {
                theHashSigner.init(true, myParms);
            } else {
                theSigner.init(true, myParms);
            }
        }

        @Override
        public void initForVerify(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            BouncyKeyPair.checkKeyPair(pKeyPair);
            super.initForVerify(pKeyPair);

            /* Determine whether this is a hashSigner */
            isHash = pKeyPair.getKeyPairSpec().getSLHDSAKeySpec().isHash();

            /* Initialise and set the signer */
            final BouncySLHDSAPublicKey myPublic = (BouncySLHDSAPublicKey) getKeyPair().getPublicKey();
            if (isHash) {
                theHashSigner.init(false, myPublic.getPublicKey());
            } else {
                theSigner.init(false, myPublic.getPublicKey());
            }
        }

        @Override
        public void update(final byte[] pBytes,
                           final int pOffset,
                           final int pLength) {
            if (isHash) {
                theHashSigner.update(pBytes, pOffset, pLength);
            } else {
                super.update(pBytes, pOffset, pLength);
            }
        }

        @Override
        public void update(final byte pByte) {
            if (isHash) {
                theHashSigner.update(pByte);
            } else {
                super.update(pByte);
            }
        }

        @Override
        public void update(final byte[] pBytes) {
            if (isHash) {
                theHashSigner.update(pBytes, 0, pBytes.length);
            } else {
                super.update(pBytes);
            }
        }

        @Override
        public void reset() {
            if (isHash) {
                theHashSigner.reset();
            } else {
                super.reset();
            }
        }

        @Override
        public byte[] sign() throws OceanusException {
            /* Check that we are in signing mode */
            checkMode(GordianSignatureMode.SIGN);

            /* Sign the message */
            try {
                return isHash
                        ? theHashSigner.generateSignature()
                        : theSigner.generateSignature(getDigest());
            } catch (CryptoException e) {
                throw new GordianCryptoException("Failed to sign message", e);
            }
         }

        @Override
        public boolean verify(final byte[] pSignature) throws OceanusException {
            /* Check that we are in verify mode */
            checkMode(GordianSignatureMode.VERIFY);

            /* Verify the message */
            return isHash
                    ? theHashSigner.verifySignature(pSignature)
                    : theSigner.verifySignature(getDigest(), pSignature);
        }
    }
}
