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

import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.pqc.crypto.xmss.XMSSKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSKeyPairGenerator;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTKeyPairGenerator;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPublicKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTSigner;
import org.bouncycastle.pqc.crypto.xmss.XMSSParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSSigner;

import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianXMSSKeySpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianXMSSKeySpec.GordianXMSSDigestType;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureType;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyKeyPair.BouncyStateAwareKeyPair;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyKeyPair.BouncyStateAwarePrivateKey;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncySignature.BouncyDigestSignature;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianKeyPairValidity;
import net.sourceforge.joceanus.tethys.OceanusException;

/**
 * XMSS KeyPair classes.
 */
public final class BouncyXMSSKeyPair {
    /**
     * Private constructor.
     */
    private BouncyXMSSKeyPair() {
    }

    /**
     * Bouncy XMSS PublicKey.
     */
    public static class BouncyXMSSPublicKey
            extends BouncyPublicKey<XMSSPublicKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        BouncyXMSSPublicKey(final GordianKeyPairSpec pKeySpec,
                            final XMSSPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final XMSSPublicKeyParameters myThis = getPublicKey();
            final XMSSPublicKeyParameters myThat = (XMSSPublicKeyParameters) pThat;

            /* Check equality */
            return compareKeys(myThis, myThat);
        }

        /**
         * Is the private key valid for this public key?
         * @param pPrivate the private key
         * @return true/false
         */
        public boolean validPrivate(final BouncyXMSSPrivateKey pPrivate) {
            final XMSSPrivateKeyParameters myPrivate = pPrivate.getPrivateKey();
            return getPublicKey().getParameters().equals(myPrivate.getParameters());
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final XMSSPublicKeyParameters pFirst,
                                           final XMSSPublicKeyParameters pSecond) {
            try {
                return Arrays.equals(pFirst.getEncoded(), pSecond.getEncoded());
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    /**
     * Bouncy XMSS PrivateKey.
     */
    public static class BouncyXMSSPrivateKey
            extends BouncyStateAwarePrivateKey<XMSSPrivateKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        BouncyXMSSPrivateKey(final GordianKeyPairSpec pKeySpec,
                             final XMSSPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        public long getUsagesRemaining() {
            return getPrivateKey().getUsagesRemaining();
        }

        @Override
        public BouncyXMSSPrivateKey getKeyShard(final int pNumUsages) {
            return new BouncyXMSSPrivateKey(getKeySpec(), getPrivateKey().extractKeyShard(pNumUsages));
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final XMSSPrivateKeyParameters myThis = getPrivateKey();
            final XMSSPrivateKeyParameters myThat = (XMSSPrivateKeyParameters) pThat;

            /* Check equality */
            return compareKeys(myThis, myThat);
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final XMSSPrivateKeyParameters pFirst,
                                           final XMSSPrivateKeyParameters pSecond) {
            try {
                return Arrays.equals(pFirst.getEncoded(), pSecond.getEncoded());
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    /**
     * BouncyCastle XMSS KeyPair generator.
     */
    public static class BouncyXMSSKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final XMSSKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyXMSSKeyPairGenerator(final BouncyFactory pFactory,
                                   final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = new XMSSKeyPairGenerator();
            final GordianXMSSKeySpec mySpec = pKeySpec.getXMSSKeySpec();
            final KeyGenerationParameters myParams = new XMSSKeyGenerationParameters(
                    new XMSSParameters(mySpec.getHeight().getHeight(), createDigest(getKeyType())), getRandom());
            theGenerator.init(myParams);
        }

        /**
         * Obtain the keyTypeType.
         * @return the keyTypeType
         */
        private GordianXMSSDigestType getKeyType() {
            return getKeySpec().getXMSSDigestType();
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyXMSSPublicKey myPublic = new BouncyXMSSPublicKey(getKeySpec(), (XMSSPublicKeyParameters) myPair.getPublic());
            final BouncyXMSSPrivateKey myPrivate = new BouncyXMSSPrivateKey(getKeySpec(), (XMSSPrivateKeyParameters) myPair.getPrivate());
            return new BouncyStateAwareKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check the keyPair type and keySpecs */
                BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

                /* build and return the encoding */
                final BouncyXMSSPrivateKey myPrivateKey = (BouncyXMSSPrivateKey) getPrivateKey(pKeyPair);
                final XMSSPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
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
                final BouncyXMSSPublicKey myPublic = derivePublicKey(pPublicKey);
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                XMSSPrivateKeyParameters myParms = (XMSSPrivateKeyParameters) PrivateKeyFactory.createKey(myInfo);
                BouncyXMSSPrivateKey myPrivate = new BouncyXMSSPrivateKey(getKeySpec(), myParms);
                final BouncyKeyPair myPair = new BouncyStateAwareKeyPair(myPublic, myPrivate);

                /* Check that we have a matching pair */
                GordianKeyPairValidity.checkValidity(getFactory(), myPair);

                /* Rebuild and return the keyPair to avoid incrementing usage count */
                myParms = (XMSSPrivateKeyParameters) PrivateKeyFactory.createKey(myInfo);
                myPrivate = new BouncyXMSSPrivateKey(getKeySpec(), myParms);
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
                final BouncyXMSSPublicKey myPublicKey = (BouncyXMSSPublicKey) getPublicKey(pKeyPair);
                final XMSSPublicKeyParameters myParms = myPublicKey.getPublicKey();
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(myParms);
                return new X509EncodedKeySpec(myInfo.getEncoded());

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            final BouncyXMSSPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws OceanusException on error
         */
        private BouncyXMSSPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pEncodedKey);

                /* derive publicKey */
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final XMSSPublicKeyParameters myParms = (XMSSPublicKeyParameters) PublicKeyFactory.createKey(myInfo);
                return new BouncyXMSSPublicKey(getKeySpec(), myParms);

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * Create digest for XMSSKeyType.
     * @param pKeyType the key type
     * @return the digest
     */
    static Digest createDigest(final GordianXMSSDigestType pKeyType) {
        switch (pKeyType) {
            case SHAKE128:
                return new SHAKEDigest(GordianLength.LEN_128.getLength());
            case SHAKE256:
                return new SHAKEDigest(GordianLength.LEN_256.getLength());
            case SHA256:
                return new SHA256Digest();
            case SHA512:
            default:
                return new SHA512Digest();
        }
    }

    /**
     * Bouncy XMSSMT PublicKey.
     */
    public static class BouncyXMSSMTPublicKey
            extends BouncyPublicKey<XMSSMTPublicKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        BouncyXMSSMTPublicKey(final GordianKeyPairSpec pKeySpec,
                              final XMSSMTPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final XMSSMTPublicKeyParameters myThis = getPublicKey();
            final XMSSMTPublicKeyParameters myThat = (XMSSMTPublicKeyParameters) pThat;

            /* Check equality */
            return compareKeys(myThis, myThat);
        }

        /**
         * Is the private key valid for this public key?
         * @param pPrivate the private key
         * @return true/false
         */
        public boolean validPrivate(final BouncyXMSSMTPrivateKey pPrivate) {
            final XMSSMTPrivateKeyParameters myPrivate = pPrivate.getPrivateKey();
            return getPublicKey().getParameters().equals(myPrivate.getParameters());
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final XMSSMTPublicKeyParameters pFirst,
                                           final XMSSMTPublicKeyParameters pSecond) {
            try {
                return Arrays.equals(pFirst.getEncoded(), pSecond.getEncoded());
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    /**
     * Bouncy XMSSMT PrivateKey.
     */
    public static class BouncyXMSSMTPrivateKey
            extends BouncyStateAwarePrivateKey<XMSSMTPrivateKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        BouncyXMSSMTPrivateKey(final GordianKeyPairSpec pKeySpec,
                               final XMSSMTPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        public long getUsagesRemaining() {
            return getPrivateKey().getUsagesRemaining();
        }

        @Override
        public BouncyXMSSMTPrivateKey getKeyShard(final int pNumUsages) {
            return new BouncyXMSSMTPrivateKey(getKeySpec(), getPrivateKey().extractKeyShard(pNumUsages));
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final XMSSMTPrivateKeyParameters myThis = getPrivateKey();
            final XMSSMTPrivateKeyParameters myThat = (XMSSMTPrivateKeyParameters) pThat;

            /* Check equality */
            return compareKeys(myThis, myThat);
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final XMSSMTPrivateKeyParameters pFirst,
                                           final XMSSMTPrivateKeyParameters pSecond) {
            try {
                return Arrays.equals(pFirst.getEncoded(), pSecond.getEncoded());
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    /**
     * BouncyCastle XMSSMT KeyPair generator.
     */
    public static class BouncyXMSSMTKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final XMSSMTKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyXMSSMTKeyPairGenerator(final BouncyFactory pFactory,
                                     final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = new XMSSMTKeyPairGenerator();
            final GordianXMSSKeySpec mySpec = pKeySpec.getXMSSKeySpec();
            final KeyGenerationParameters myParams = new XMSSMTKeyGenerationParameters(
                    new XMSSMTParameters(mySpec.getHeight().getHeight(), mySpec.getLayers().getLayers(),
                            createDigest(getKeyType())), getRandom());
            theGenerator.init(myParams);
        }

        /**
         * Obtain the digestType.
         * @return the digestType
         */
        private GordianXMSSDigestType getKeyType() {
            return getKeySpec().getXMSSDigestType();
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyXMSSMTPublicKey myPublic = new BouncyXMSSMTPublicKey(getKeySpec(), (XMSSMTPublicKeyParameters) myPair.getPublic());
            final BouncyXMSSMTPrivateKey myPrivate = new BouncyXMSSMTPrivateKey(getKeySpec(), (XMSSMTPrivateKeyParameters) myPair.getPrivate());
            return new BouncyStateAwareKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check the keyPair type and keySpecs */
                BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

                /* build and return the encoding */
                final BouncyXMSSMTPrivateKey myPrivateKey = (BouncyXMSSMTPrivateKey) getPrivateKey(pKeyPair);
                final XMSSMTPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
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
                final BouncyXMSSMTPublicKey myPublic = derivePublicKey(pPublicKey);
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                XMSSMTPrivateKeyParameters myParms = (XMSSMTPrivateKeyParameters) PrivateKeyFactory.createKey(myInfo);
                BouncyXMSSMTPrivateKey myPrivate = new BouncyXMSSMTPrivateKey(getKeySpec(), myParms);
                final BouncyKeyPair myPair = new BouncyStateAwareKeyPair(myPublic, myPrivate);

                /* Check that we have a matching pair */
                GordianKeyPairValidity.checkValidity(getFactory(), myPair);

                /* Rebuild and return the keyPair to avoid incrementing usage count */
                myParms = (XMSSMTPrivateKeyParameters) PrivateKeyFactory.createKey(myInfo);
                myPrivate = new BouncyXMSSMTPrivateKey(getKeySpec(), myParms);
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
                final BouncyXMSSMTPublicKey myPublicKey = (BouncyXMSSMTPublicKey) getPublicKey(pKeyPair);
                final XMSSMTPublicKeyParameters myParms = myPublicKey.getPublicKey();
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(myParms);
                return new X509EncodedKeySpec(myInfo.getEncoded());

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            final BouncyXMSSMTPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws OceanusException on error
         */
        private BouncyXMSSMTPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pEncodedKey);

                /* derive publicKey */
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final XMSSMTPublicKeyParameters myParms = (XMSSMTPublicKeyParameters) PublicKeyFactory.createKey(myInfo);
                return new BouncyXMSSMTPublicKey(getKeySpec(), myParms);

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * XMSS signature.
     */
    public static class BouncyXMSSSignature
            extends BouncyDigestSignature {
        /**
         * Is this a preHash signature?
         */
        private final boolean preHash;

        /**
         * The XMSS Signer.
         */
        private final XMSSSigner theSigner;

        /**
         * The XMSSMT Signer.
         */
        private final XMSSMTSigner theMTSigner;

        /**
         * Are we using the MT signer?
         */
        private boolean isMT;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error
         */
        BouncyXMSSSignature(final BouncyFactory pFactory,
                            final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the signers */
            theSigner = new XMSSSigner();
            theMTSigner = new XMSSMTSigner();

            /* Determine preHash */
            preHash = GordianSignatureType.PREHASH.equals(pSpec.getSignatureType());
        }

        @Override
        public void initForSigning(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            BouncyKeyPair.checkKeyPair(pKeyPair);
            super.initForSigning(pKeyPair);

            /* Set the digest */
            final GordianXMSSKeySpec myKeySpec = pKeyPair.getKeyPairSpec().getXMSSKeySpec();
            final GordianDigestSpec myDigestSpec = myKeySpec.getDigestType().getDigestSpec();
            setDigest(preHash ? myDigestSpec : null);

            /* Initialise and set the signer */
            isMT = myKeySpec.isMT();
            if (isMT) {
                final BouncyXMSSMTPrivateKey myPrivate = (BouncyXMSSMTPrivateKey) getKeyPair().getPrivateKey();
                theMTSigner.init(true, myPrivate.getPrivateKey());
            } else {
                final BouncyXMSSPrivateKey myPrivate = (BouncyXMSSPrivateKey) getKeyPair().getPrivateKey();
                theSigner.init(true, myPrivate.getPrivateKey());
            }
        }

        @Override
        public void initForVerify(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            BouncyKeyPair.checkKeyPair(pKeyPair);
            super.initForVerify(pKeyPair);

            /* Set the digest */
            final GordianXMSSKeySpec myKeySpec = pKeyPair.getKeyPairSpec().getXMSSKeySpec();
            final GordianDigestSpec myDigestSpec = myKeySpec.getDigestType().getDigestSpec();
            setDigest(preHash ? myDigestSpec : null);

            /* Initialise and set the signer */
            isMT = myKeySpec.isMT();
            if (isMT) {
                final BouncyXMSSMTPublicKey myPublic = (BouncyXMSSMTPublicKey) getKeyPair().getPublicKey();
                theMTSigner.init(false, myPublic.getPublicKey());
            } else {
                final BouncyXMSSPublicKey myPublic = (BouncyXMSSPublicKey) getKeyPair().getPublicKey();
                theSigner.init(false, myPublic.getPublicKey());
            }
        }

        @Override
        public byte[] sign() throws OceanusException {
            /* Check that we are in signing mode */
            checkMode(GordianSignatureMode.SIGN);

            /* Sign the message */
            return isMT
                   ? theMTSigner.generateSignature(getDigest())
                   : theSigner.generateSignature(getDigest());
        }

        @Override
        public boolean verify(final byte[] pSignature) throws OceanusException {
            /* Check that we are in verify mode */
            checkMode(GordianSignatureMode.VERIFY);

            /* Verify the message */
            return isMT
                   ? theMTSigner.verifySignature(getDigest(), pSignature)
                   : theSigner.verifySignature(getDigest(), pSignature);
        }
    }
}
