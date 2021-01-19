/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.lms.HSSKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.lms.HSSKeyPairGenerator;
import org.bouncycastle.pqc.crypto.lms.HSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.HSSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.HSSSigner;
import org.bouncycastle.pqc.crypto.lms.LMSKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.lms.LMSKeyPairGenerator;
import org.bouncycastle.pqc.crypto.lms.LMSParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSSigner;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory;

import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianLMSKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianLMSKeySpec.GordianHSSKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyKeyPair.BouncyStateAwareKeyPair;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyKeyPair.BouncyStateAwarePrivateKey;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncySignature.BouncyDigestSignature;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianKeyPairValidity;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * LMS KeyPair classes.
 */
public final class BouncyLMSKeyPair {
    /**
     * Private constructor.
     */
    private BouncyLMSKeyPair() {
    }

    /**
     * Bouncy LMS PublicKey.
     */
    public static class BouncyLMSPublicKey
            extends BouncyPublicKey<LMSPublicKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        BouncyLMSPublicKey(final GordianKeyPairSpec pKeySpec,
                           final LMSPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final LMSPublicKeyParameters myThis = getPublicKey();
            final LMSPublicKeyParameters myThat = (LMSPublicKeyParameters) pThat;

            /* Check equality */
            return myThis.equals(myThat);
        }
    }

    /**
     * Bouncy LMS PrivateKey.
     */
    public static class BouncyLMSPrivateKey
            extends BouncyStateAwarePrivateKey<LMSPrivateKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        BouncyLMSPrivateKey(final GordianKeyPairSpec pKeySpec,
                            final LMSPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        public long getUsagesRemaining() {
            return getPrivateKey().getUsagesRemaining();
        }

        @Override
        public BouncyLMSPrivateKey getKeyShard(final int pNumUsages) {
            return new BouncyLMSPrivateKey(getKeySpec(), getPrivateKey().extractKeyShard(pNumUsages));
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final LMSPrivateKeyParameters myThis = getPrivateKey();
            final LMSPrivateKeyParameters myThat = (LMSPrivateKeyParameters) pThat;

            /* Check equality */
            return myThis.equals(myThat);
        }
    }

    /**
     * BouncyCastle LMS KeyPair generator.
     */
    public static class BouncyLMSKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final LMSKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyLMSKeyPairGenerator(final BouncyFactory pFactory,
                                  final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = new LMSKeyPairGenerator();
            final GordianLMSKeySpec mySpec = pKeySpec.getLMSKeySpec();
            final KeyGenerationParameters myParams = new LMSKeyGenerationParameters(mySpec.getParameters(), getRandom());
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyLMSPublicKey myPublic = new BouncyLMSPublicKey(getKeySpec(), (LMSPublicKeyParameters) myPair.getPublic());
            final BouncyLMSPrivateKey myPrivate = new BouncyLMSPrivateKey(getKeySpec(), (LMSPrivateKeyParameters) myPair.getPrivate());
            return new BouncyStateAwareKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check the keyPair type and keySpecs */
                BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

                /* build and return the encoding */
                final BouncyLMSPrivateKey myPrivateKey = (BouncyLMSPrivateKey) getPrivateKey(pKeyPair);
                final LMSPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
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
                final BouncyLMSPublicKey myPublic = derivePublicKey(pPublicKey);
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                LMSPrivateKeyParameters myParms = (LMSPrivateKeyParameters) PrivateKeyFactory.createKey(myInfo);
                BouncyLMSPrivateKey myPrivate = new BouncyLMSPrivateKey(getKeySpec(), myParms);
                final BouncyKeyPair myPair = new BouncyStateAwareKeyPair(myPublic, myPrivate);

                /* Check that we have a matching pair */
                GordianKeyPairValidity.checkValidity(getFactory(), myPair);

                /* Rebuild and return the keyPair to avoid incrementing usage count */
                myParms = (LMSPrivateKeyParameters) PrivateKeyFactory.createKey(myInfo);
                myPrivate = new BouncyLMSPrivateKey(getKeySpec(), myParms);
                return new BouncyStateAwareKeyPair(myPublic, myPrivate);

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
                final BouncyLMSPublicKey myPublicKey = (BouncyLMSPublicKey) getPublicKey(pKeyPair);
                final LMSPublicKeyParameters myParms = myPublicKey.getPublicKey();
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(myParms);
                return new X509EncodedKeySpec(myInfo.getEncoded());

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            final BouncyLMSPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws OceanusException on error
         */
        private BouncyLMSPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pEncodedKey);

                /* derive publicKey */
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final LMSPublicKeyParameters myParms = (LMSPublicKeyParameters) PublicKeyFactory.createKey(myInfo);
                return new BouncyLMSPublicKey(getKeySpec(), myParms);

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * Bouncy HSS PublicKey.
     */
    public static class BouncyHSSPublicKey
            extends BouncyPublicKey<HSSPublicKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        BouncyHSSPublicKey(final GordianKeyPairSpec pKeySpec,
                           final HSSPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final HSSPublicKeyParameters myThis = getPublicKey();
            final HSSPublicKeyParameters myThat = (HSSPublicKeyParameters) pThat;

            /* Check equality */
            return myThis.equals(myThat);
        }
    }

    /**
     * Bouncy HSS PrivateKey.
     */
    public static class BouncyHSSPrivateKey
            extends BouncyStateAwarePrivateKey<HSSPrivateKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        BouncyHSSPrivateKey(final GordianKeyPairSpec pKeySpec,
                            final HSSPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        public long getUsagesRemaining() {
            return getPrivateKey().getUsagesRemaining();
        }

        @Override
        public BouncyHSSPrivateKey getKeyShard(final int pNumUsages) {
            return new BouncyHSSPrivateKey(getKeySpec(), getPrivateKey().extractKeyShard(pNumUsages));
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final HSSPrivateKeyParameters myThis = getPrivateKey();
            final HSSPrivateKeyParameters myThat = (HSSPrivateKeyParameters) pThat;

            /* Check equality */
            return myThis.equals(myThat);
        }
    }

    /**
     * BouncyCastle HSS KeyPair generator.
     */
    public static class BouncyHSSKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final HSSKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyHSSKeyPairGenerator(final BouncyFactory pFactory,
                                  final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = new HSSKeyPairGenerator();
            final GordianHSSKeySpec myKeySpec = pKeySpec.getHSSKeySpec();
            final KeyGenerationParameters myParams = new HSSKeyGenerationParameters(deriveParameters(myKeySpec), getRandom());
            theGenerator.init(myParams);
        }

        /**
         * Derive the parameters.
         * @param pKeySpec the keySPec
         * @return the parameters.
         */
        private static LMSParameters[] deriveParameters(final GordianHSSKeySpec pKeySpec) {
            final GordianLMSKeySpec myKeySpec = pKeySpec.getKeySpec();
            final LMSParameters[] myParams = new LMSParameters[pKeySpec.getTreeDepth()];
            Arrays.fill(myParams, myKeySpec.getParameters());
            return myParams;
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyHSSPublicKey myPublic = new BouncyHSSPublicKey(getKeySpec(), (HSSPublicKeyParameters) myPair.getPublic());
            final BouncyHSSPrivateKey myPrivate = new BouncyHSSPrivateKey(getKeySpec(), (HSSPrivateKeyParameters) myPair.getPrivate());
            return new BouncyStateAwareKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check the keyPair type and keySpecs */
                BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

                /* build and return the encoding */
                final BouncyHSSPrivateKey myPrivateKey = (BouncyHSSPrivateKey) getPrivateKey(pKeyPair);
                final HSSPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
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
                final BouncyHSSPublicKey myPublic = derivePublicKey(pPublicKey);
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                HSSPrivateKeyParameters myParms = (HSSPrivateKeyParameters) PrivateKeyFactory.createKey(myInfo);
                BouncyHSSPrivateKey myPrivate = new BouncyHSSPrivateKey(getKeySpec(), myParms);
                final BouncyKeyPair myPair = new BouncyStateAwareKeyPair(myPublic, myPrivate);

                /* Check that we have a matching pair */
                GordianKeyPairValidity.checkValidity(getFactory(), myPair);

                /* Rebuild and return the keyPair to avoid incrementing usage count */
                myParms = (HSSPrivateKeyParameters) PrivateKeyFactory.createKey(myInfo);
                myPrivate = new BouncyHSSPrivateKey(getKeySpec(), myParms);
                return new BouncyStateAwareKeyPair(myPublic, myPrivate);
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
                final BouncyHSSPublicKey myPublicKey = (BouncyHSSPublicKey) getPublicKey(pKeyPair);
                final HSSPublicKeyParameters myParms = myPublicKey.getPublicKey();
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(myParms);
                return new X509EncodedKeySpec(myInfo.getEncoded());

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            final BouncyHSSPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws OceanusException on error
         */
        private BouncyHSSPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pEncodedKey);

                /* derive publicKey */
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final HSSPublicKeyParameters myParms = (HSSPublicKeyParameters) PublicKeyFactory.createKey(myInfo);
                return new BouncyHSSPublicKey(getKeySpec(), myParms);

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * LMS signature.
     */
    public static class BouncyLMSSignature
            extends BouncyDigestSignature {
        /**
         * The LMS Signer.
         */
        private final LMSSigner theSigner;

        /**
         * The HSS Signer.
         */
        private final HSSSigner theHSSSigner;

        /**
         * Are we using the HSS signer?
         */
        private boolean isHSS;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error
         */
        BouncyLMSSignature(final BouncyFactory pFactory,
                           final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the signer */
            theSigner = new LMSSigner();
            theHSSSigner = new HSSSigner();
        }


        @Override
        public void initForSigning(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            BouncyKeyPair.checkKeyPair(pKeyPair);
            super.initForSigning(pKeyPair);

            /* Initialise and set the signer */
            isHSS = pKeyPair.getKeyPairSpec().getSubKeyType() instanceof GordianHSSKeySpec;
            if (isHSS) {
                final BouncyHSSPrivateKey myPrivate = (BouncyHSSPrivateKey) getKeyPair().getPrivateKey();
                theHSSSigner.init(true, myPrivate.getPrivateKey());
            } else {
                final BouncyLMSPrivateKey myPrivate = (BouncyLMSPrivateKey) getKeyPair().getPrivateKey();
                theSigner.init(true, myPrivate.getPrivateKey());
            }
        }

        @Override
        public void initForVerify(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            BouncyKeyPair.checkKeyPair(pKeyPair);
            super.initForVerify(pKeyPair);

            /* Initialise and set the signer */
            isHSS = pKeyPair.getKeyPairSpec().getSubKeyType() instanceof GordianHSSKeySpec;
            if (isHSS) {
                final BouncyHSSPublicKey myPublic = (BouncyHSSPublicKey) getKeyPair().getPublicKey();
                theHSSSigner.init(false, myPublic.getPublicKey());
            } else {
                final BouncyLMSPublicKey myPublic = (BouncyLMSPublicKey) getKeyPair().getPublicKey();
                theSigner.init(false, myPublic.getPublicKey());
            }
        }

        @Override
        public byte[] sign() throws OceanusException {
            /* Check that we are in signing mode */
            checkMode(GordianSignatureMode.SIGN);

            /* Sign the message */
            return isHSS
                   ? theHSSSigner.generateSignature(getDigest())
                   : theSigner.generateSignature(getDigest());
        }

        @Override
        public boolean verify(final byte[] pSignature) throws OceanusException {
            /* Check that we are in verify mode */
            checkMode(GordianSignatureMode.VERIFY);

            /* Verify the message */
            return  isHSS
                    ? theHSSSigner.verifySignature(getDigest(), pSignature)
                    : theSigner.verifySignature(getDigest(), pSignature);
        }
    }
}
