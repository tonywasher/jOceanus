/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2022 Tony Washer
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
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumKeyPairGenerator;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPublicKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumSigner;
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
 * Dilithium KeyPair classes.
 */
public final class BouncyDILITHIUMKeyPair {
    /**
     * Private constructor.
     */
    private BouncyDILITHIUMKeyPair() {
    }

    /**
     * Bouncy Dilithium PublicKey.
     */
    public static class BouncyDILITHIUMPublicKey
            extends BouncyPublicKey<DilithiumPublicKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        BouncyDILITHIUMPublicKey(final GordianKeyPairSpec pKeySpec,
                                 final DilithiumPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final DilithiumPublicKeyParameters myThis = getPublicKey();
            final DilithiumPublicKeyParameters myThat = (DilithiumPublicKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final DilithiumPublicKeyParameters pFirst,
                                           final DilithiumPublicKeyParameters pSecond) {
            return Arrays.equals(pFirst.getEncoded(), pSecond.getEncoded());
        }
    }

    /**
     * Bouncy Dilithium PrivateKey.
     */
    public static class BouncyDILITHIUMPrivateKey
            extends BouncyPrivateKey<DilithiumPrivateKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        BouncyDILITHIUMPrivateKey(final GordianKeyPairSpec pKeySpec,
                                  final DilithiumPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final DilithiumPrivateKeyParameters myThis = getPrivateKey();
            final DilithiumPrivateKeyParameters myThat = (DilithiumPrivateKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final DilithiumPrivateKeyParameters pFirst,
                                           final DilithiumPrivateKeyParameters pSecond) {
            return Arrays.equals(pFirst.getEncoded(), pSecond.getEncoded());
        }
    }

    /**
     * BouncyCastle Dilithium KeyPair generator.
     */
    public static class BouncyDILITHIUMKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final DilithiumKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyDILITHIUMKeyPairGenerator(final BouncyFactory pFactory,
                                        final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final DilithiumParameters myParms = pKeySpec.getDilithiumKeySpec().getParameters();

            /* Create and initialise the generator */
            theGenerator = new DilithiumKeyPairGenerator();
            final DilithiumKeyGenerationParameters myParams = new DilithiumKeyGenerationParameters(getRandom(), myParms);
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyDILITHIUMPublicKey myPublic = new BouncyDILITHIUMPublicKey(getKeySpec(), (DilithiumPublicKeyParameters) myPair.getPublic());
            final BouncyDILITHIUMPrivateKey myPrivate = new BouncyDILITHIUMPrivateKey(getKeySpec(), (DilithiumPrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check the keyPair type and keySpecs */
                BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

                /* build and return the encoding */
                final BouncyDILITHIUMPrivateKey myPrivateKey = (BouncyDILITHIUMPrivateKey) getPrivateKey(pKeyPair);
                final DilithiumPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
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
                final BouncyDILITHIUMPublicKey myPublic = derivePublicKey(pPublicKey);
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final DilithiumPrivateKeyParameters myParms = (DilithiumPrivateKeyParameters) PrivateKeyFactory.createKey(myInfo);
                final BouncyDILITHIUMPrivateKey myPrivate = new BouncyDILITHIUMPrivateKey(getKeySpec(), myParms);
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
                final BouncyDILITHIUMPublicKey myPublicKey = (BouncyDILITHIUMPublicKey) getPublicKey(pKeyPair);
                final DilithiumPublicKeyParameters myParms = myPublicKey.getPublicKey();
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(myParms);
                return new X509EncodedKeySpec(myInfo.getEncoded());

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            final BouncyDILITHIUMPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws OceanusException on error
         */
        private BouncyDILITHIUMPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pEncodedKey);

                /* derive publicKey */
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final DilithiumPublicKeyParameters myParms = (DilithiumPublicKeyParameters) PublicKeyFactory.createKey(myInfo);
                return new BouncyDILITHIUMPublicKey(getKeySpec(), myParms);

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * Dilithium signer.
     */
    public static class BouncyDILITHIUMSignature
            extends BouncyDigestSignature {
        /**
         * The SPHINCSPlus Signer.
         */
        private final DilithiumSigner theSigner;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error
         */
        BouncyDILITHIUMSignature(final BouncyFactory pFactory,
                              final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);
            theSigner = new DilithiumSigner();
        }

        @Override
        public void initForSigning(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            BouncyKeyPair.checkKeyPair(pKeyPair);
            super.initForSigning(pKeyPair);

            /* Initialise and set the signer */
            final BouncyDILITHIUMPrivateKey myPrivate = (BouncyDILITHIUMPrivateKey) getKeyPair().getPrivateKey();
            final CipherParameters myParms = new ParametersWithRandom(myPrivate.getPrivateKey(), getRandom());
            theSigner.init(true, myParms);
        }

        @Override
        public void initForVerify(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            BouncyKeyPair.checkKeyPair(pKeyPair);
            super.initForVerify(pKeyPair);

            /* Initialise and set the signer */
            final BouncyDILITHIUMPublicKey myPublic = (BouncyDILITHIUMPublicKey) getKeyPair().getPublicKey();
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
