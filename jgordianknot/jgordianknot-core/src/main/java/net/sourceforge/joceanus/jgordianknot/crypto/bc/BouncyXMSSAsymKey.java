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

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.asn1.XMSSKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTPrivateKey;
import org.bouncycastle.pqc.asn1.XMSSMTPublicKey;
import org.bouncycastle.pqc.asn1.XMSSPrivateKey;
import org.bouncycastle.pqc.asn1.XMSSPublicKey;
import org.bouncycastle.pqc.crypto.xmss.BDS;
import org.bouncycastle.pqc.crypto.xmss.BDSStateMap;
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
import org.bouncycastle.pqc.crypto.xmss.XMSSUtil;
import org.bouncycastle.pqc.jcajce.provider.xmss.BCXMSSMTPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.xmss.BCXMSSMTPublicKey;
import org.bouncycastle.pqc.jcajce.provider.xmss.BCXMSSPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.xmss.BCXMSSPublicKey;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianLength;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianXMSSKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncySignature.BouncyDigestSignature;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * XMSS AsymKey classes.
 */
public final class BouncyXMSSAsymKey {
    /**
     * Private constructor.
     */
    private BouncyXMSSAsymKey() {
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
        BouncyXMSSPublicKey(final GordianAsymKeySpec pKeySpec,
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
            return Arrays.equals(pFirst.toByteArray(), pSecond.toByteArray());
        }
    }

    /**
     * Bouncy XMSS PrivateKey.
     */
    public static class BouncyXMSSPrivateKey
            extends BouncyPrivateKey<XMSSPrivateKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        BouncyXMSSPrivateKey(final GordianAsymKeySpec pKeySpec,
                             final XMSSPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
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
            return Arrays.equals(pFirst.toByteArray(), pSecond.toByteArray());
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
                                   final GordianAsymKeySpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = new XMSSKeyPairGenerator();
            final KeyGenerationParameters myParams = new XMSSKeyGenerationParameters(
                    new XMSSParameters(GordianXMSSKeyType.DEFAULT_HEIGHT, createDigest(getKeyType())), getRandom());
            theGenerator.init(myParams);
        }

        /**
         * Obtain the keyTypeType.
         * @return the keyTypeType
         */
        private GordianXMSSKeyType getKeyType() {
            return getKeySpec().getXMSSKeyType();
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyXMSSPublicKey myPublic = new BouncyXMSSPublicKey(getKeySpec(), (XMSSPublicKeyParameters) myPair.getPublic());
            final BouncyXMSSPrivateKey myPrivate = new BouncyXMSSPrivateKey(getKeySpec(), (XMSSPrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) {
            final BouncyXMSSPrivateKey myPrivateKey = (BouncyXMSSPrivateKey) getPrivateKey(pKeyPair);
            final XMSSPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
            final BCXMSSPrivateKey myKey = new BCXMSSPrivateKey(getOID(getKeyType()), myParms);
            return new PKCS8EncodedKeySpec(myKey.getEncoded());
        }

        @Override
        public BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                           final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            try {
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final XMSSKeyParams myParams = XMSSKeyParams.getInstance(myInfo.getPrivateKeyAlgorithm().getParameters());
                final XMSSPrivateKey myKey = XMSSPrivateKey.getInstance(myInfo.parsePrivateKey());
                final XMSSPrivateKeyParameters.Builder myBuilder = new XMSSPrivateKeyParameters.Builder(
                        new XMSSParameters(myParams.getHeight(), createDigest(getKeyType())))
                                .withIndex(myKey.getIndex())
                                .withSecretKeySeed(myKey.getSecretKeySeed())
                                .withSecretKeyPRF(myKey.getSecretKeyPRF())
                                .withPublicSeed(myKey.getPublicSeed())
                                .withRoot(myKey.getRoot());
                if (myKey.getBdsState() != null) {
                    final BDS myImport = (BDS) XMSSUtil.deserialize(myKey.getBdsState(), BDS.class);
                    myBuilder.withBDSState(myImport.withWOTSDigest(myParams.getTreeDigest().getAlgorithm()));
                }
                final XMSSPrivateKeyParameters myPrivateParms = myBuilder.build();

                final BouncyXMSSPrivateKey myPrivate = new BouncyXMSSPrivateKey(getKeySpec(), myPrivateParms);
                final BouncyXMSSPublicKey myPublic = derivePublicKey(pPublicKey);
                return new BouncyKeyPair(myPublic, myPrivate);

            } catch (IOException
                    | ClassNotFoundException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) {
            final BouncyXMSSPublicKey myPublicKey = (BouncyXMSSPublicKey) getPublicKey(pKeyPair);
            final XMSSPublicKeyParameters myParms = myPublicKey.getPublicKey();
            final BCXMSSPublicKey myKey = new BCXMSSPublicKey(getOID(getKeyType()), myParms);
            return new X509EncodedKeySpec(myKey.getEncoded());
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
            try {
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final XMSSKeyParams myParams = XMSSKeyParams.getInstance(myInfo.getAlgorithm().getParameters());
                final XMSSPublicKey myPublicKey = XMSSPublicKey.getInstance(myInfo.parsePublicKey());
                final XMSSPublicKeyParameters myParms = new XMSSPublicKeyParameters.Builder(
                        new XMSSParameters(myParams.getHeight(), createDigest(getKeyType())))
                                .withPublicSeed(myPublicKey.getPublicSeed())
                                .withRoot(myPublicKey.getRoot()).build();
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
    static Digest createDigest(final GordianXMSSKeyType pKeyType) {
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
     * Obtain digest OID for XMSSKeyType.
     * @param pKeyType the keyType
     * @return the OIDt
     */
    static ASN1ObjectIdentifier getOID(final GordianXMSSKeyType pKeyType) {
        switch (pKeyType) {
            case SHAKE128:
                return NISTObjectIdentifiers.id_shake128;
            case SHAKE256:
                return NISTObjectIdentifiers.id_shake256;
            case SHA256:
                return NISTObjectIdentifiers.id_sha256;
            case SHA512:
            default:
                return NISTObjectIdentifiers.id_sha512;
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
        BouncyXMSSMTPublicKey(final GordianAsymKeySpec pKeySpec,
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
            return Arrays.equals(pFirst.toByteArray(), pSecond.toByteArray());
        }
    }

    /**
     * Bouncy XMSSMT PrivateKey.
     */
    public static class BouncyXMSSMTPrivateKey
            extends BouncyPrivateKey<XMSSMTPrivateKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        BouncyXMSSMTPrivateKey(final GordianAsymKeySpec pKeySpec,
                               final XMSSMTPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
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
            return Arrays.equals(pFirst.toByteArray(), pSecond.toByteArray());
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
                                     final GordianAsymKeySpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = new XMSSMTKeyPairGenerator();
            final KeyGenerationParameters myParams = new XMSSMTKeyGenerationParameters(
                    new XMSSMTParameters(GordianXMSSKeyType.DEFAULT_HEIGHT, GordianXMSSKeyType.DEFAULT_LAYERS,
                            createDigest(getKeyType())), getRandom());
            theGenerator.init(myParams);
        }

        /**
         * Obtain the digestType.
         * @return the digestType
         */
        private GordianXMSSKeyType getKeyType() {
            return getKeySpec().getXMSSKeyType();
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyXMSSMTPublicKey myPublic = new BouncyXMSSMTPublicKey(getKeySpec(), (XMSSMTPublicKeyParameters) myPair.getPublic());
            final BouncyXMSSMTPrivateKey myPrivate = new BouncyXMSSMTPrivateKey(getKeySpec(), (XMSSMTPrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) {
            final BouncyXMSSMTPrivateKey myPrivateKey = (BouncyXMSSMTPrivateKey) getPrivateKey(pKeyPair);
            final XMSSMTPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
            final BCXMSSMTPrivateKey myKey = new BCXMSSMTPrivateKey(getOID(getKeyType()), myParms);
            return new PKCS8EncodedKeySpec(myKey.getEncoded());
        }

        @Override
        public BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                           final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            try {
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final XMSSMTKeyParams myParams = XMSSMTKeyParams.getInstance(myInfo.getPrivateKeyAlgorithm().getParameters());
                final XMSSMTPrivateKey myKey = XMSSMTPrivateKey.getInstance(myInfo.parsePrivateKey());
                final XMSSMTPrivateKeyParameters.Builder myBuilder = new XMSSMTPrivateKeyParameters.Builder(
                        new XMSSMTParameters(myParams.getHeight(), myParams.getLayers(), createDigest(getKeyType())))
                                .withIndex(myKey.getIndex())
                                .withSecretKeySeed(myKey.getSecretKeySeed())
                                .withSecretKeyPRF(myKey.getSecretKeyPRF())
                                .withPublicSeed(myKey.getPublicSeed())
                                .withRoot(myKey.getRoot());
                if (myKey.getBdsState() != null) {
                    final BDSStateMap myImport = (BDSStateMap) XMSSUtil.deserialize(myKey.getBdsState(), BDSStateMap.class);
                    myBuilder.withBDSState(myImport.withWOTSDigest(myParams.getTreeDigest().getAlgorithm()));
                }
                final XMSSMTPrivateKeyParameters myPrivateParms = myBuilder.build();

                final BouncyXMSSMTPrivateKey myPrivate = new BouncyXMSSMTPrivateKey(getKeySpec(), myPrivateParms);
                final BouncyXMSSMTPublicKey myPublic = derivePublicKey(pPublicKey);
                return new BouncyKeyPair(myPublic, myPrivate);

            } catch (IOException
                    | ClassNotFoundException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) {
            final BouncyXMSSMTPublicKey myPublicKey = (BouncyXMSSMTPublicKey) getPublicKey(pKeyPair);
            final XMSSMTPublicKeyParameters myParms = myPublicKey.getPublicKey();
            final BCXMSSMTPublicKey myKey = new BCXMSSMTPublicKey(getOID(getKeyType()), myParms);
            return new X509EncodedKeySpec(myKey.getEncoded());
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
            try {
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final XMSSMTKeyParams myParams = XMSSMTKeyParams.getInstance(myInfo.getAlgorithm().getParameters());
                final XMSSMTPublicKey myPublicKey = XMSSMTPublicKey.getInstance(myInfo.parsePublicKey());
                final XMSSMTPublicKeyParameters myParms = new XMSSMTPublicKeyParameters.Builder(
                        new XMSSMTParameters(myParams.getHeight(), myParams.getLayers(), createDigest(getKeyType())))
                                .withPublicSeed(myPublicKey.getPublicSeed())
                                .withRoot(myPublicKey.getRoot()).build();
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
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error
         */
        BouncyXMSSSignature(final BouncyFactory pFactory,
                            final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the signer */
            theSigner = new XMSSSigner();

            /* Determine preHash */
            preHash = GordianSignatureType.PREHASH.equals(pSpec.getSignatureType());
        }


        @Override
        public void initForSigning(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            super.initForSigning(pKeyPair);

            /* Set the digest */
            final GordianDigestSpec myDigestSpec = pKeyPair.getKeySpec().getXMSSKeyType().getDigestSpec();
            setDigest(preHash ? myDigestSpec : null);

            /* Initialise and set the signer */
            final BouncyXMSSPrivateKey myPrivate = (BouncyXMSSPrivateKey) getKeyPair().getPrivateKey();
            theSigner.init(true, myPrivate.getPrivateKey());
        }

        @Override
        public void initForVerify(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            super.initForVerify(pKeyPair);

            /* Set the digest */
            final GordianDigestSpec myDigestSpec = pKeyPair.getKeySpec().getXMSSKeyType().getDigestSpec();
            setDigest(preHash ? myDigestSpec : null);

            /* Initialise and set the signer */
            final BouncyXMSSPublicKey myPublic = (BouncyXMSSPublicKey) getKeyPair().getPublicKey();
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

    /**
     * XMSSMT signature.
     */
    public static class BouncyXMSSMTSignature
            extends BouncyDigestSignature {
        /**
         * Is this a preHash signature?
         */
        private final boolean preHash;

        /**
         * The XMSS Signer.
         */
        private final XMSSMTSigner theSigner;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error
         */
        BouncyXMSSMTSignature(final BouncyFactory pFactory,
                              final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the signer */
            theSigner = new XMSSMTSigner();

            /* Determine preHash */
            preHash = GordianSignatureType.PREHASH.equals(pSpec.getSignatureType());
       }

        @Override
        public void initForSigning(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            super.initForSigning(pKeyPair);

            /* Set the digest */
            final GordianDigestSpec myDigestSpec = pKeyPair.getKeySpec().getXMSSKeyType().getDigestSpec();
            setDigest(preHash ? myDigestSpec : null);

            /* Initialise and set the signer */
            final BouncyXMSSMTPrivateKey myPrivate = (BouncyXMSSMTPrivateKey) getKeyPair().getPrivateKey();
            theSigner.init(true, myPrivate.getPrivateKey());
        }

        @Override
        public void initForVerify(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Initialise detail */
            super.initForVerify(pKeyPair);

            /* Set the digest */
            final GordianDigestSpec myDigestSpec = pKeyPair.getKeySpec().getXMSSKeyType().getDigestSpec();
            setDigest(preHash ? myDigestSpec : null);

            /* Initialise and set the signer */
            final BouncyXMSSMTPublicKey myPublic = (BouncyXMSSMTPublicKey) getKeyPair().getPublicKey();
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
