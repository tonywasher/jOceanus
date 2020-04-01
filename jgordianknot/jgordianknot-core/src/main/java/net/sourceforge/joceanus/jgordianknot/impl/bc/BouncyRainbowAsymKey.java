/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.patch.utils.PqcPrivateKeyFactory;
import org.bouncycastle.crypto.patch.utils.PqcPrivateKeyInfoFactory;
import org.bouncycastle.crypto.patch.utils.PqcPublicKeyFactory;
import org.bouncycastle.crypto.patch.utils.PqcSubjectPublicKeyInfoFactory;
import org.bouncycastle.pqc.crypto.rainbow.RainbowKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowKeyPairGenerator;
import org.bouncycastle.pqc.crypto.rainbow.RainbowParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPublicKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowSigner;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncySignature.BouncyDigestSignature;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Rainbow AsymKey classes.
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
            extends BouncyPublicKey<RainbowPublicKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        BouncyRainbowPublicKey(final GordianAsymKeySpec pKeySpec,
                               final RainbowPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final RainbowPublicKeyParameters myThis = getPublicKey();
            final RainbowPublicKeyParameters myThat = (RainbowPublicKeyParameters) pThat;

            /* Check equality */
            return Arrays.equals(myThis.getCoeffScalar(), myThat.getCoeffScalar())
                    && Arrays.deepEquals(myThis.getCoeffSingular(), myThat.getCoeffSingular())
                    && Arrays.deepEquals(myThis.getCoeffQuadratic(), myThat.getCoeffQuadratic());
        }
    }

    /**
     * Bouncy Rainbow PrivateKey.
     */
    public static class BouncyRainbowPrivateKey
            extends BouncyPrivateKey<RainbowPrivateKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        BouncyRainbowPrivateKey(final GordianAsymKeySpec pKeySpec,
                                final RainbowPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final RainbowPrivateKeyParameters myThis = getPrivateKey();
            final RainbowPrivateKeyParameters myThat = (RainbowPrivateKeyParameters) pThat;

            /* Check equality */
            if (!Arrays.equals(myThis.getB1(), myThat.getB1())
                    || !Arrays.equals(myThis.getB2(), myThat.getB2())) {
                return false;
            }
            if (!Arrays.deepEquals(myThis.getInvA1(), myThat.getInvA1())
                    || !Arrays.deepEquals(myThis.getInvA2(), myThat.getInvA2())) {
                return false;
            }
            return Arrays.equals(myThis.getVi(), myThat.getVi())
                    && Arrays.equals(myThis.getLayers(), myThat.getLayers());
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
        BouncyRainbowKeyPairGenerator(final BouncyFactory pFactory,
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
            final BouncyRainbowPublicKey myPublic = new BouncyRainbowPublicKey(getKeySpec(), (RainbowPublicKeyParameters) myPair.getPublic());
            final BouncyRainbowPrivateKey myPrivate = new BouncyRainbowPrivateKey(getKeySpec(), (RainbowPrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
                final BouncyRainbowPrivateKey myPrivateKey = (BouncyRainbowPrivateKey) getPrivateKey(pKeyPair);
                final RainbowPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
                final PrivateKeyInfo myInfo = PqcPrivateKeyInfoFactory.createPrivateKeyInfo(myParms, null);
                return new PKCS8EncodedKeySpec(myInfo.getEncoded());
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                           final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            try {
                checkKeySpec(pPrivateKey);
                final BouncyRainbowPublicKey myPublic = derivePublicKey(pPublicKey);
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final RainbowPrivateKeyParameters myParms = (RainbowPrivateKeyParameters) PqcPrivateKeyFactory.createKey(myInfo);
                final BouncyRainbowPrivateKey myPrivate = new BouncyRainbowPrivateKey(getKeySpec(), myParms);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
                final BouncyRainbowPublicKey myPublicKey = (BouncyRainbowPublicKey) getPublicKey(pKeyPair);
                final RainbowPublicKeyParameters myParms = myPublicKey.getPublicKey();
                final SubjectPublicKeyInfo myInfo = PqcSubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(myParms);
                return new X509EncodedKeySpec(myInfo.getEncoded());
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
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
                checkKeySpec(pEncodedKey);
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final RainbowPublicKeyParameters myParms = (RainbowPublicKeyParameters) PqcPublicKeyFactory.createKey(myInfo);
                return new BouncyRainbowPublicKey(getKeySpec(), myParms);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * Rainbow signature.
     */
    public static class BouncyRainbowSignature
            extends BouncyDigestSignature {
        /**
         * The Rainbow Signer.
         */
        private final RainbowSigner theSigner;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error
         */
        BouncyRainbowSignature(final BouncyFactory pFactory,
                               final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the signer */
            theSigner = new RainbowSigner();
        }

        @Override
        public void initForSigning(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            super.initForSigning(pKeyPair);

            /* Initialise and set the signer */
            final BouncyRainbowPrivateKey myPrivate = (BouncyRainbowPrivateKey) getKeyPair().getPrivateKey();
            final ParametersWithRandom myParms = new ParametersWithRandom(myPrivate.getPrivateKey(), getRandom());
            theSigner.init(true, myParms);
        }

        @Override
        public void initForVerify(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            super.initForVerify(pKeyPair);

            /* Initialise and set the signer */
            final BouncyRainbowPublicKey myPublic = (BouncyRainbowPublicKey) getKeyPair().getPublicKey();
            theSigner.init(false, myPublic.getPublicKey());
        }

        @Override
        public byte[] sign() throws OceanusException {
            /* Check that we are in signing mode */
            checkMode(GordianSignatureMode.SIGN);

            /* Sign the message */
            return theSigner.generateSignature(getDigest());
        }

        @Override
        public boolean verify(final byte[] pSignature) throws OceanusException {
            /* Check that we are in verify mode */
            checkMode(GordianSignatureMode.VERIFY);

            /* Verify the message */
            return theSigner.verifySignature(getDigest(), pSignature);
        }
    }
}
