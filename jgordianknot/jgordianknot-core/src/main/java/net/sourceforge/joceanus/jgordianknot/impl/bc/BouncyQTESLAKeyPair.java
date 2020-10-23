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
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.qtesla.QTESLAKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.qtesla.QTESLAKeyPairGenerator;
import org.bouncycastle.pqc.crypto.qtesla.QTESLAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.qtesla.QTESLAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.qtesla.QTESLASigner;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory;

import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncySignature.BouncyDigestSignature;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianKeyPairValidity;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * qTESLA KeyPair classes.
 */
public final class BouncyQTESLAKeyPair {
    /**
     * Private constructor.
     */
    private BouncyQTESLAKeyPair() {
    }

    /**
     * Bouncy QTESLA PublicKey.
     */
    public static class BouncyQTESLAPublicKey
            extends BouncyPublicKey<QTESLAPublicKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        BouncyQTESLAPublicKey(final GordianKeyPairSpec pKeySpec,
                              final QTESLAPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final QTESLAPublicKeyParameters myThis = getPublicKey();
            final QTESLAPublicKeyParameters myThat = (QTESLAPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getPublicData(), myThat.getPublicData());
        }
    }

    /**
     * Bouncy QTESLA PrivateKey.
     */
    public static class BouncyQTESLAPrivateKey
            extends BouncyPrivateKey<QTESLAPrivateKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        BouncyQTESLAPrivateKey(final GordianKeyPairSpec pKeySpec,
                               final QTESLAPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final QTESLAPrivateKeyParameters myThis = getPrivateKey();
            final QTESLAPrivateKeyParameters myThat = (QTESLAPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getSecret(), myThat.getSecret());
        }
    }

    /**
     * BouncyCastle QTESLA KeyPair generator.
     */
    public static class BouncyQTESLAKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final QTESLAKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyQTESLAKeyPairGenerator(final BouncyFactory pFactory,
                                     final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = new QTESLAKeyPairGenerator();
            final int myCategory = pKeySpec.getQTESLACategory();
            final QTESLAKeyGenerationParameters myParams = new QTESLAKeyGenerationParameters(myCategory, getRandom());
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyQTESLAPublicKey myPublic = new BouncyQTESLAPublicKey(getKeySpec(), (QTESLAPublicKeyParameters) myPair.getPublic());
            final BouncyQTESLAPrivateKey myPrivate = new BouncyQTESLAPrivateKey(getKeySpec(), (QTESLAPrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check the keyPair type and keySpecs */
                BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

                /* build and return the encoding */
                final BouncyQTESLAPrivateKey myPrivateKey = (BouncyQTESLAPrivateKey) getPrivateKey(pKeyPair);
                final QTESLAPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
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
                final BouncyQTESLAPublicKey myPublic = derivePublicKey(pPublicKey);
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final QTESLAPrivateKeyParameters myParms = (QTESLAPrivateKeyParameters) PrivateKeyFactory.createKey(myInfo);
                final BouncyQTESLAPrivateKey myPrivate = new BouncyQTESLAPrivateKey(getKeySpec(), myParms);
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
                final BouncyQTESLAPublicKey myPublicKey = (BouncyQTESLAPublicKey) getPublicKey(pKeyPair);
                final QTESLAPublicKeyParameters myParms = myPublicKey.getPublicKey();
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(myParms);
                return new X509EncodedKeySpec(myInfo.getEncoded());

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            final BouncyQTESLAPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws OceanusException on error
         */
        private BouncyQTESLAPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pEncodedKey);

                /* derive publicKey */
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final QTESLAPublicKeyParameters myParms = (QTESLAPublicKeyParameters) PublicKeyFactory.createKey(myInfo);
                return new BouncyQTESLAPublicKey(getKeySpec(), myParms);

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * QTESLA signature.
     */
    public static class BouncyQTESLASignature
            extends BouncyDigestSignature {
        /**
         * The XMSS Signer.
         */
        private final QTESLASigner theSigner;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error
         */
        BouncyQTESLASignature(final BouncyFactory pFactory,
                              final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the signer */
            theSigner = new QTESLASigner();
        }


        @Override
        public void initForSigning(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            BouncyKeyPair.checkKeyPair(pKeyPair);
            super.initForSigning(pKeyPair);

            /* Initialise and set the signer */
            final BouncyQTESLAPrivateKey myPrivate = (BouncyQTESLAPrivateKey) getKeyPair().getPrivateKey();
            final ParametersWithRandom myParms = new ParametersWithRandom(myPrivate.getPrivateKey(), getRandom());
            theSigner.init(true, myParms);
        }

        @Override
        public void initForVerify(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            BouncyKeyPair.checkKeyPair(pKeyPair);
            super.initForVerify(pKeyPair);

            /* Initialise and set the signer */
            final BouncyQTESLAPublicKey myPublic = (BouncyQTESLAPublicKey) getKeyPair().getPublicKey();
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
