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
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianLength;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSigner;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianValidator;
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
            extends BouncyPublicKey {
        /**
         * Public Key details.
         */
        private final XMSSPublicKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        protected BouncyXMSSPublicKey(final GordianAsymKeySpec pKeySpec,
                                      final XMSSPublicKeyParameters pPublicKey) {
            super(pKeySpec);
            theKey = pPublicKey;
        }

        /**
         * Obtain the public key.
         * @return the key
         */
        protected XMSSPublicKeyParameters getPublicKey() {
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
            if (!(pThat instanceof BouncyXMSSPublicKey)) {
                return false;
            }

            /* Access the target field */
            final BouncyXMSSPublicKey myThat = (BouncyXMSSPublicKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(myThat);
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
        }

        /**
         * CompareKeys.
         * @param pThat the key to compare with
         * @return true/false
         */
        private boolean compareKeys(final BouncyXMSSPublicKey pThat) {
            return compareKeys(getPublicKey(), pThat.getPublicKey());
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
            extends BouncyPrivateKey {
        /**
         * Private Key details.
         */
        private final XMSSPrivateKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        protected BouncyXMSSPrivateKey(final GordianAsymKeySpec pKeySpec,
                                       final XMSSPrivateKeyParameters pPrivateKey) {
            super(pKeySpec);
            theKey = pPrivateKey;
        }

        /**
         * Obtain the private key.
         * @return the key
         */
        protected XMSSPrivateKeyParameters getPrivateKey() {
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
            if (!(pThat instanceof BouncyXMSSPrivateKey)) {
                return false;
            }

            /* Access the target field */
            final BouncyXMSSPrivateKey myThat = (BouncyXMSSPrivateKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(myThat);
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
        }

        /**
         * CompareKeys.
         * @param pThat the key to compare with
         * @return true/false
         */
        private boolean compareKeys(final BouncyXMSSPrivateKey pThat) {
            return compareKeys(getPrivateKey(), pThat.getPrivateKey());
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
        protected BouncyXMSSKeyPairGenerator(final BouncyFactory pFactory,
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
            final BouncyXMSSPublicKey myPublic = new BouncyXMSSPublicKey(getKeySpec(), XMSSPublicKeyParameters.class.cast(myPair.getPublic()));
            final BouncyXMSSPrivateKey myPrivate = new BouncyXMSSPrivateKey(getKeySpec(), XMSSPrivateKeyParameters.class.cast(myPair.getPrivate()));
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        protected PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) {
            final BouncyXMSSPrivateKey myPrivateKey = BouncyXMSSPrivateKey.class.cast(getPrivateKey(pKeyPair));
            final XMSSPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
            final BCXMSSPrivateKey myKey = new BCXMSSPrivateKey(getOID(getKeyType()), myParms);
            return new PKCS8EncodedKeySpec(myKey.getEncoded());
        }

        @Override
        protected BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
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
                    myBuilder.withBDSState((BDS) XMSSUtil.deserialize(myKey.getBdsState()));
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
            final BouncyXMSSPublicKey myPublicKey = BouncyXMSSPublicKey.class.cast(getPublicKey(pKeyPair));
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
            extends BouncyPublicKey {
        /**
         * Public Key details.
         */
        private final XMSSMTPublicKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        protected BouncyXMSSMTPublicKey(final GordianAsymKeySpec pKeySpec,
                                        final XMSSMTPublicKeyParameters pPublicKey) {
            super(pKeySpec);
            theKey = pPublicKey;
        }

        /**
         * Obtain the public key.
         * @return the key
         */
        protected XMSSMTPublicKeyParameters getPublicKey() {
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
            if (!(pThat instanceof BouncyXMSSMTPublicKey)) {
                return false;
            }

            /* Access the target field */
            final BouncyXMSSMTPublicKey myThat = (BouncyXMSSMTPublicKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(myThat);
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
        }

        /**
         * CompareKeys.
         * @param pThat the key to compare with
         * @return true/false
         */
        private boolean compareKeys(final BouncyXMSSMTPublicKey pThat) {
            return compareKeys(getPublicKey(), pThat.getPublicKey());
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
            extends BouncyPrivateKey {
        /**
         * Private Key details.
         */
        private final XMSSMTPrivateKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        protected BouncyXMSSMTPrivateKey(final GordianAsymKeySpec pKeySpec,
                                         final XMSSMTPrivateKeyParameters pPrivateKey) {
            super(pKeySpec);
            theKey = pPrivateKey;
        }

        /**
         * Obtain the private key.
         * @return the key
         */
        protected XMSSMTPrivateKeyParameters getPrivateKey() {
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
            if (!(pThat instanceof BouncyXMSSMTPrivateKey)) {
                return false;
            }

            /* Access the target field */
            final BouncyXMSSMTPrivateKey myThat = (BouncyXMSSMTPrivateKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(myThat);
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
        }

        /**
         * CompareKeys.
         * @param pThat the key to compare with
         * @return true/false
         */
        private boolean compareKeys(final BouncyXMSSMTPrivateKey pThat) {
            return compareKeys(getPrivateKey(), pThat.getPrivateKey());
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
        protected BouncyXMSSMTKeyPairGenerator(final BouncyFactory pFactory,
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
            final BouncyXMSSMTPublicKey myPublic = new BouncyXMSSMTPublicKey(getKeySpec(), XMSSMTPublicKeyParameters.class.cast(myPair.getPublic()));
            final BouncyXMSSMTPrivateKey myPrivate = new BouncyXMSSMTPrivateKey(getKeySpec(), XMSSMTPrivateKeyParameters.class.cast(myPair.getPrivate()));
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        protected PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) {
            final BouncyXMSSMTPrivateKey myPrivateKey = BouncyXMSSMTPrivateKey.class.cast(getPrivateKey(pKeyPair));
            final XMSSMTPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
            final BCXMSSMTPrivateKey myKey = new BCXMSSMTPrivateKey(getOID(getKeyType()), myParms);
            return new PKCS8EncodedKeySpec(myKey.getEncoded());
        }

        @Override
        protected BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
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
                    myBuilder.withBDSState((BDSStateMap) XMSSUtil.deserialize(myKey.getBdsState()));
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
            final BouncyXMSSMTPublicKey myPublicKey = BouncyXMSSMTPublicKey.class.cast(getPublicKey(pKeyPair));
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
     * XMSS signer.
     */
    public static class BouncyXMSSSigner
            extends BouncyDigestSignature
            implements GordianSigner {
        /**
         * The XMSS Signer.
         */
        private final XMSSSigner theSigner;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pPrivateKey the private key
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error
         */
        protected BouncyXMSSSigner(final BouncyFactory pFactory,
                                   final BouncyXMSSPrivateKey pPrivateKey,
                                   final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the signer */
            theSigner = new XMSSSigner();

            /* Initialise and set the signer */
            theSigner.init(true, pPrivateKey.getPrivateKey());
        }

        @Override
        public byte[] sign() throws OceanusException {
            /* Sign the message */
            return theSigner.generateSignature(getDigest());
        }
    }

    /**
     * XMSS validator.
     */
    public static class BouncyXMSSValidator
            extends BouncyDigestSignature
            implements GordianValidator {
        /**
         * The XMSS Signer.
         */
        private final XMSSSigner theSigner;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pPublicKey the public key
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error
         */
        protected BouncyXMSSValidator(final BouncyFactory pFactory,
                                      final BouncyXMSSPublicKey pPublicKey,
                                      final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the signer */
            theSigner = new XMSSSigner();

            /* Initialise and set the signer */
            theSigner.init(false, pPublicKey.getPublicKey());
        }

        @Override
        public boolean verify(final byte[] pSignature) throws OceanusException {
            return theSigner.verifySignature(getDigest(), pSignature);
        }
    }

    /**
     * XMSSMT signer.
     */
    public static class BouncyXMSSMTSigner
            extends BouncyDigestSignature
            implements GordianSigner {
        /**
         * The XMSS Signer.
         */
        private final XMSSMTSigner theSigner;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pPrivateKey the private key
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error
         */
        protected BouncyXMSSMTSigner(final BouncyFactory pFactory,
                                     final BouncyXMSSMTPrivateKey pPrivateKey,
                                     final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the signer */
            theSigner = new XMSSMTSigner();

            /* Initialise and set the signer */
            theSigner.init(true, pPrivateKey.getPrivateKey());
        }

        @Override
        public byte[] sign() throws OceanusException {
            /* Sign the message */
            return theSigner.generateSignature(getDigest());
        }
    }

    /**
     * XMSSMT validator.
     */
    public static class BouncyXMSSMTValidator
            extends BouncyDigestSignature
            implements GordianValidator {
        /**
         * The XMSS Signer.
         */
        private final XMSSMTSigner theSigner;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pPublicKey the public key
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error
         */
        protected BouncyXMSSMTValidator(final BouncyFactory pFactory,
                                        final BouncyXMSSMTPublicKey pPublicKey,
                                        final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the signer */
            theSigner = new XMSSMTSigner();

            /* Initialise and set the signer */
            theSigner.init(false, pPublicKey.getPublicKey());
        }

        @Override
        public boolean verify(final byte[] pSignature) throws OceanusException {
            return theSigner.verifySignature(getDigest(), pSignature);
        }
    }
}
