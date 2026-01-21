/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.gordianknot.impl.bc;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignParams;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyKeyPair.BouncyPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncyKeyPair.BouncyPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.BouncySignature.BouncyDigestSignature;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.GordianKeyPairValidity;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.snova.SnovaKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaKeyPairGenerator;
import org.bouncycastle.pqc.crypto.snova.SnovaParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaPublicKeyParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaSigner;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory;

import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

/**
 * Snova KeyPair classes.
 */
public final class BouncySnovaKeyPair {
    /**
     * Private constructor.
     */
    private BouncySnovaKeyPair() {
    }

    /**
     * Bouncy Snova PublicKey.
     */
    public static class BouncySnovaPublicKey
            extends BouncyPublicKey<SnovaPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncySnovaPublicKey(final GordianKeyPairSpec pKeySpec,
                             final SnovaPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SnovaPublicKeyParameters myThis = getPublicKey();
            final SnovaPublicKeyParameters myThat = (SnovaPublicKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
        }

        /**
         * CompareKeys.
         *
         * @param pFirst  the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final SnovaPublicKeyParameters pFirst,
                                           final SnovaPublicKeyParameters pSecond) {
            return Arrays.equals(pFirst.getEncoded(), pSecond.getEncoded());
        }
    }

    /**
     * Bouncy Snova PrivateKey.
     */
    public static class BouncySnovaPrivateKey
            extends BouncyPrivateKey<SnovaPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncySnovaPrivateKey(final GordianKeyPairSpec pKeySpec,
                              final SnovaPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SnovaPrivateKeyParameters myThis = getPrivateKey();
            final SnovaPrivateKeyParameters myThat = (SnovaPrivateKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
        }

        /**
         * CompareKeys.
         *
         * @param pFirst  the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final SnovaPrivateKeyParameters pFirst,
                                           final SnovaPrivateKeyParameters pSecond) {
            return Arrays.equals(pFirst.getEncoded(), pSecond.getEncoded());
        }
    }

    /**
     * BouncyCastle Snova KeyPair generator.
     */
    public static class BouncySnovaKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final SnovaKeyPairGenerator theGenerator;

        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncySnovaKeyPairGenerator(final GordianBaseFactory pFactory,
                                    final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final SnovaParameters myParms = pKeySpec.getSnovaKeySpec().getParameters();

            /* Create and initialise the generator */
            theGenerator = new SnovaKeyPairGenerator();
            final SnovaKeyGenerationParameters myParams = new SnovaKeyGenerationParameters(getRandom(), myParms);
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncySnovaPublicKey myPublic = new BouncySnovaPublicKey(getKeySpec(), (SnovaPublicKeyParameters) myPair.getPublic());
            final BouncySnovaPrivateKey myPrivate = new BouncySnovaPrivateKey(getKeySpec(), (SnovaPrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Check the keyPair type and keySpecs */
                BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

                /* build and return the encoding */
                final BouncySnovaPrivateKey myPrivateKey = (BouncySnovaPrivateKey) getPrivateKey(pKeyPair);
                final SnovaPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
                final PrivateKeyInfo myInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(myParms, null);
                return new PKCS8EncodedKeySpec(myInfo.getEncoded());

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                           final PKCS8EncodedKeySpec pPrivateKey) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pPrivateKey);

                /* derive keyPair */
                final BouncySnovaPublicKey myPublic = derivePublicKey(pPublicKey);
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final SnovaPrivateKeyParameters myParms = (SnovaPrivateKeyParameters) PrivateKeyFactory.createKey(myInfo);
                final BouncySnovaPrivateKey myPrivate = new BouncySnovaPrivateKey(getKeySpec(), myParms);
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
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Check the keyPair type and keySpecs */
                BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

                /* build and return the encoding */
                final BouncySnovaPublicKey myPublicKey = (BouncySnovaPublicKey) getPublicKey(pKeyPair);
                final SnovaPublicKeyParameters myParms = myPublicKey.getPublicKey();
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(myParms);
                return new X509EncodedKeySpec(myInfo.getEncoded());

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws GordianException {
            final BouncySnovaPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         *
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws GordianException on error
         */
        private BouncySnovaPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pEncodedKey);

                /* derive publicKey */
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final SnovaPublicKeyParameters myParms = (SnovaPublicKeyParameters) PublicKeyFactory.createKey(myInfo);
                return new BouncySnovaPublicKey(getKeySpec(), myParms);

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * Snova signer.
     */
    public static class BouncySnovaSignature
            extends BouncyDigestSignature {
        /**
         * The Snova Signer.
         */
        private final SnovaSigner theSigner;

        /**
         * Constructor.
         *
         * @param pFactory the factory
         * @param pSpec    the signatureSpec.
         * @throws GordianException on error
         */
        BouncySnovaSignature(final GordianBaseFactory pFactory,
                             final GordianSignatureSpec pSpec) throws GordianException {
            /* Initialise underlying class */
            super(pFactory, pSpec);
            theSigner = new SnovaSigner();
        }

        @Override
        public void initForSigning(final GordianSignParams pParams) throws GordianException {
            /* Initialise detail */
            super.initForSigning(pParams);
            final BouncyKeyPair myPair = getKeyPair();
            BouncyKeyPair.checkKeyPair(myPair);

            /* Initialise and set the signer */
            final BouncySnovaPrivateKey myPrivate = (BouncySnovaPrivateKey) myPair.getPrivateKey();
            final CipherParameters myParms = new ParametersWithRandom(myPrivate.getPrivateKey(), getRandom());
            theSigner.init(true, myParms);
        }

        @Override
        public void initForVerify(final GordianSignParams pParams) throws GordianException {
            /* Initialise detail */
            super.initForVerify(pParams);
            final BouncyKeyPair myPair = getKeyPair();
            BouncyKeyPair.checkKeyPair(myPair);

            /* Initialise and set the signer */
            final BouncySnovaPublicKey myPublic = (BouncySnovaPublicKey) myPair.getPublicKey();
            theSigner.init(false, myPublic.getPublicKey());
        }

        @Override
        public byte[] sign() throws GordianException {
            /* Check that we are in signing mode */
            checkMode(GordianSignatureMode.SIGN);

            /* Sign the message */
            return theSigner.generateSignature(getDigest());
        }

        @Override
        public boolean verify(final byte[] pSignature) throws GordianException {
            /* Check that we are in verify mode */
            checkMode(GordianSignatureMode.VERIFY);

            /* Verify the message */
            return theSigner.verifySignature(getDigest(), pSignature);
        }
    }
}
