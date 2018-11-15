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
import org.bouncycastle.crypto.newutils.PqcPrivateKeyFactory;
import org.bouncycastle.crypto.newutils.PqcPrivateKeyInfoFactory;
import org.bouncycastle.crypto.newutils.PqcPublicKeyFactory;
import org.bouncycastle.crypto.newutils.PqcSubjectPublicKeyInfoFactory;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.qtesla.QTESLAKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.qtesla.QTESLAKeyPairGenerator;
import org.bouncycastle.pqc.crypto.qtesla.QTESLAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.qtesla.QTESLAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.qtesla.QTESLASigner;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncySignature.BouncyDigestSignature;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * qTESLA AsymKey classes.
 */
public final class BouncyQTESLAAsymKey {
    /**
     * Private constructor.
     */
    private BouncyQTESLAAsymKey() {
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
        BouncyQTESLAPublicKey(final GordianAsymKeySpec pKeySpec,
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
        BouncyQTESLAPrivateKey(final GordianAsymKeySpec pKeySpec,
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
                                     final GordianAsymKeySpec pKeySpec) {
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
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyQTESLAPublicKey myPublic = new BouncyQTESLAPublicKey(getKeySpec(), (QTESLAPublicKeyParameters) myPair.getPublic());
            final BouncyQTESLAPrivateKey myPrivate = new BouncyQTESLAPrivateKey(getKeySpec(), (QTESLAPrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
                final BouncyQTESLAPrivateKey myPrivateKey = (BouncyQTESLAPrivateKey) getPrivateKey(pKeyPair);
                final QTESLAPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
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
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final QTESLAPrivateKeyParameters myParms = (QTESLAPrivateKeyParameters) PqcPrivateKeyFactory.createKey(myInfo);
                final BouncyQTESLAPrivateKey myPrivate = new BouncyQTESLAPrivateKey(getKeySpec(), myParms);
                final BouncyQTESLAPublicKey myPublic = derivePublicKey(pPublicKey);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
                final BouncyQTESLAPublicKey myPublicKey = (BouncyQTESLAPublicKey) getPublicKey(pKeyPair);
                final QTESLAPublicKeyParameters myParms = myPublicKey.getPublicKey();
                final SubjectPublicKeyInfo myInfo = PqcSubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(myParms);
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
            try {
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final QTESLAPublicKeyParameters myParms = (QTESLAPublicKeyParameters) PqcPublicKeyFactory.createKey(myInfo);
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
            super.initForSigning(pKeyPair);

            /* Initialise and set the signer */
            final BouncyQTESLAPrivateKey myPrivate = (BouncyQTESLAPrivateKey) getKeyPair().getPrivateKey();
            theSigner.init(true, myPrivate.getPrivateKey());
        }

        @Override
        public void initForVerify(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
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
