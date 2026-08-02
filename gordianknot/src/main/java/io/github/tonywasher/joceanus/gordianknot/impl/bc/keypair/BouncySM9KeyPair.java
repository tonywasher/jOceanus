/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSM9Spec.GordianSM9EncryptType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSM9Spec.GordianSM9SignType;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPairGenerator.BouncyKeyFactorySet;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.GordianCoreIdAwareKeyPair.GordianIdAwarePrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.GordianCoreIdAwareKeyPair.GordianIdAwarePublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpecBuilder;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.generators.SM9EncMasterKeyPairGenerator;
import org.bouncycastle.crypto.generators.SM9SigMasterKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.SM9EncMasterPrivateKeyParameters;
import org.bouncycastle.crypto.params.SM9EncMasterPublicKeyParameters;
import org.bouncycastle.crypto.params.SM9EncPrivateKeyParameters;
import org.bouncycastle.crypto.params.SM9EncPublicKeyParameters;
import org.bouncycastle.crypto.params.SM9SigMasterPrivateKeyParameters;
import org.bouncycastle.crypto.params.SM9SigMasterPublicKeyParameters;
import org.bouncycastle.crypto.params.SM9SigPrivateKeyParameters;
import org.bouncycastle.util.Arrays;

import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;
//import org.bouncycastle.util.Objects;

/**
 * SM9 KeyPair classes.
 */
public final class BouncySM9KeyPair {
    /**
     * The builder.
     */
    private static final GordianCoreKeyPairSpecBuilder BUILDER = GordianCoreKeyPairSpecBuilder.newInstance();

    /**
     * The Encrypt keySpec.
     */
    private static final GordianKeyPairSpec ENCRYPYT = BUILDER.sm9(GordianSM9EncryptType.ENCRYPT);

    /**
     * The Exchange keySpec.
     */
    private static final GordianKeyPairSpec EXCHANGE = BUILDER.sm9(GordianSM9EncryptType.EXCHANGE);

    /**
     * The Sign keySpec.
     */
    private static final GordianKeyPairSpec SIGN = BUILDER.sm9(GordianSM9SignType.SIGN);

    /**
     * Private constructor.
     */
    private BouncySM9KeyPair() {
    }

    /**
     * Bouncy SM9EncMaster PublicKey.
     */
    public static class BouncySM9EncMasterPublicKey
            extends BouncyPublicKey<SM9EncMasterPublicKeyParameters>
            implements GordianIdAwarePublicKey<GordianSM9EncryptType> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncySM9EncMasterPublicKey(final GordianKeyPairSpec pKeySpec,
                                    final SM9EncMasterPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SM9EncMasterPublicKeyParameters myThis = getPublicKey();
            final SM9EncMasterPublicKeyParameters myThat = (SM9EncMasterPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.areEqual(myThis.getEncoded(), myThat.getEncoded());
        }

        @Override
        public GordianSM9EncryptType getSubKeyType() {
            return GordianSM9EncryptType.ENCMASTER;
        }

        @Override
        public GordianIdAwarePublicKey<GordianSM9EncryptType> deriveUserPublicKey(final GordianSM9EncryptType pKeyType,
                                                                                  final byte[] pIdentity) {
            return switch (pKeyType) {
                case ENCRYPT -> {
                    final SM9EncPublicKeyParameters myParms = getPublicKey().getUserPublicKey(pIdentity,
                            SM9EncMasterPrivateKeyParameters.HID);
                    yield new BouncySM9EncUserPublicKey(ENCRYPYT, myParms);
                }
                case EXCHANGE -> {
                    final SM9EncPublicKeyParameters myParms = getPublicKey().getUserPublicKey(pIdentity,
                            SM9EncMasterPrivateKeyParameters.HID_EXCHANGE);
                    yield new BouncySM9EncUserPublicKey(EXCHANGE, myParms);
                }
                default -> null;
            };
        }
    }

    /**
     * Bouncy SM9EncMaster PrivateKey.
     */
    public static class BouncySM9EncMasterPrivateKey
            extends BouncyPrivateKey<SM9EncMasterPrivateKeyParameters>
            implements GordianIdAwarePrivateKey<GordianSM9EncryptType> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncySM9EncMasterPrivateKey(final GordianKeyPairSpec pKeySpec,
                                     final SM9EncMasterPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SM9EncMasterPrivateKeyParameters myThis = getPrivateKey();
            final SM9EncMasterPrivateKeyParameters myThat = (SM9EncMasterPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.areEqual(myThis.getEncoded(), myThat.getEncoded());
        }

        @Override
        public GordianSM9EncryptType getSubKeyType() {
            return GordianSM9EncryptType.ENCMASTER;
        }

        @Override
        public BouncySM9EncUserPrivateKey newUserPrivateKey(final GordianSM9EncryptType pKeyType,
                                                            final byte[] pIdentity) {
            return switch (pKeyType) {
                case ENCRYPT -> {
                    final SM9EncPrivateKeyParameters myParms = getPrivateKey().generateUserKey(pIdentity,
                            SM9EncMasterPrivateKeyParameters.HID);
                    yield new BouncySM9EncUserPrivateKey(ENCRYPYT, myParms);
                }
                case EXCHANGE -> {
                    final SM9EncPrivateKeyParameters myParms = getPrivateKey().generateExchangeKey(pIdentity);
                    yield new BouncySM9EncUserPrivateKey(EXCHANGE, myParms);
                }
                default -> null;
            };
        }
    }

    /**
     * Bouncy SM9EncUser PublicKey.
     */
    public static class BouncySM9EncUserPublicKey
            extends BouncyPublicKey<SM9EncPublicKeyParameters>
            implements GordianIdAwarePublicKey<GordianSM9EncryptType> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncySM9EncUserPublicKey(final GordianKeyPairSpec pKeySpec,
                                  final SM9EncPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SM9EncPublicKeyParameters myThis = getPublicKey();
            final SM9EncPublicKeyParameters myThat = (SM9EncPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.areEqual(myThis.getIdentity(), myThat.getIdentity())
                    && Arrays.areEqual(myThis.getMasterPublicKey().getEncoded(), myThat.getMasterPublicKey().getEncoded());
        }

        @Override
        public GordianSM9EncryptType getSubKeyType() {
            return (GordianSM9EncryptType) getKeySpec().getSubSpec();
        }

        @Override
        public byte[] getIdentity() {
            return Arrays.clone(getPublicKey().getIdentity());
        }
    }

    /**
     * Bouncy SM9EncUser PrivateKey.
     */
    public static class BouncySM9EncUserPrivateKey
            extends BouncyPrivateKey<SM9EncPrivateKeyParameters>
            implements GordianIdAwarePrivateKey<GordianSM9EncryptType> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncySM9EncUserPrivateKey(final GordianKeyPairSpec pKeySpec,
                                   final SM9EncPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SM9EncPrivateKeyParameters myThis = getPrivateKey();
            final SM9EncPrivateKeyParameters myThat = (SM9EncPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.areEqual(myThis.getEncoded(), myThat.getEncoded())
                    && Arrays.areEqual(myThis.getIdentity(), myThat.getIdentity())
                    && Objects.equals(myThis.getHid(), myThat.getHid())
                    && Arrays.areEqual(myThis.getMasterPublicKey().getEncoded(), myThat.getMasterPublicKey().getEncoded());
        }

        @Override
        public GordianSM9EncryptType getSubKeyType() {
            return (GordianSM9EncryptType) getKeySpec().getSubSpec();
        }

        @Override
        public byte[] getIdentity() {
            return Arrays.clone(getPrivateKey().getIdentity());
        }
    }

    /**
     * Bouncy SM9SignMaster PublicKey.
     */
    public static class BouncySM9SignMasterPublicKey
            extends BouncyPublicKey<SM9SigMasterPublicKeyParameters>
            implements GordianIdAwarePublicKey<GordianSM9SignType> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncySM9SignMasterPublicKey(final GordianKeyPairSpec pKeySpec,
                                     final SM9SigMasterPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SM9SigMasterPublicKeyParameters myThis = getPublicKey();
            final SM9SigMasterPublicKeyParameters myThat = (SM9SigMasterPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.areEqual(myThis.getEncoded(), myThat.getEncoded());
        }

        @Override
        public GordianSM9SignType getSubKeyType() {
            return GordianSM9SignType.SIGNMASTER;
        }

        @Override
        public GordianIdAwarePublicKey<GordianSM9SignType> deriveUserPublicKey(final GordianSM9SignType pKeyType,
                                                                               final byte[] pIdentity) {
            return new BouncySM9SignUserPublicKey(SIGN, getPublicKey(), pIdentity);
        }
    }

    /**
     * Bouncy SM9SignMaster PrivateKey.
     */
    public static class BouncySM9SignMasterPrivateKey
            extends BouncyPrivateKey<SM9SigMasterPrivateKeyParameters>
            implements GordianIdAwarePrivateKey<GordianSM9SignType> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncySM9SignMasterPrivateKey(final GordianKeyPairSpec pKeySpec,
                                      final SM9SigMasterPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SM9SigMasterPrivateKeyParameters myThis = getPrivateKey();
            final SM9SigMasterPrivateKeyParameters myThat = (SM9SigMasterPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.areEqual(myThis.getEncoded(), myThat.getEncoded());
        }

        @Override
        public GordianSM9SignType getSubKeyType() {
            return GordianSM9SignType.SIGNMASTER;
        }

        @Override
        public GordianIdAwarePrivateKey<GordianSM9SignType> newUserPrivateKey(final GordianSM9SignType pKeyType,
                                                                              final byte[] pIdentity) {
            final SM9SigPrivateKeyParameters myParms = getPrivateKey().generateUserKey(pIdentity);
            return new BouncySM9SignUserPrivateKey(SIGN, myParms, pIdentity);
        }
    }

    /**
     * Bouncy SM9SignUser PublicKey.
     */
    public static class BouncySM9SignUserPublicKey
            extends BouncyPublicKey<SM9SigMasterPublicKeyParameters>
            implements GordianIdAwarePublicKey<GordianSM9SignType> {
        /**
         * The identity.
         */
        private final byte[] theIdentity;

        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncySM9SignUserPublicKey(final GordianKeyPairSpec pKeySpec,
                                   final SM9SigMasterPublicKeyParameters pPublicKey,
                                   final byte[] pIdentity) {
            super(pKeySpec, pPublicKey);
            theIdentity = pIdentity;
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SM9SigMasterPublicKeyParameters myThis = getPublicKey();
            final SM9SigMasterPublicKeyParameters myThat = (SM9SigMasterPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.areEqual(myThis.getEncoded(), myThat.getEncoded());
        }

        @Override
        public GordianSM9SignType getSubKeyType() {
            return GordianSM9SignType.SIGN;
        }

        @Override
        public byte[] getIdentity() {
            return Arrays.clone(theIdentity);
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
            if (!(pThat instanceof BouncySM9SignUserPublicKey myThat)) {
                return false;
            }

            /* Check differences */
            return Arrays.areEqual(theIdentity, myThat.getIdentity())
                    && super.equals(myThat);
        }

        @Override
        public int hashCode() {
            return Objects.hash(Arrays.hashCode(theIdentity), super.hashCode());
        }
    }

    /**
     * Bouncy SM9SignUser PrivateKey.
     */
    public static class BouncySM9SignUserPrivateKey
            extends BouncyPrivateKey<SM9SigPrivateKeyParameters>
            implements GordianIdAwarePrivateKey<GordianSM9SignType> {
        /**
         * The identity.
         */
        private final byte[] theIdentity;

        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncySM9SignUserPrivateKey(final GordianKeyPairSpec pKeySpec,
                                    final SM9SigPrivateKeyParameters pPrivateKey,
                                    final byte[] pIdentity) {
            super(pKeySpec, pPrivateKey);
            theIdentity = pIdentity;
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SM9SigPrivateKeyParameters myThis = getPrivateKey();
            final SM9SigPrivateKeyParameters myThat = (SM9SigPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.areEqual(myThis.getEncoded(), myThat.getEncoded()) &&
                    Arrays.areEqual(myThis.getMasterPublicKey().getEncoded(), myThat.getMasterPublicKey().getEncoded());
        }

        @Override
        public GordianSM9SignType getSubKeyType() {
            return GordianSM9SignType.SIGN;
        }

        @Override
        public byte[] getIdentity() {
            return Arrays.clone(theIdentity);
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
            if (!(pThat instanceof BouncySM9SignUserPrivateKey myThat)) {
                return false;
            }

            /* Check differences */
            return Arrays.areEqual(theIdentity, myThat.getIdentity())
                    && super.equals(myThat);
        }

        @Override
        public int hashCode() {
            return Objects.hash(Arrays.hashCode(theIdentity), super.hashCode());
        }
    }

    /**
     * BouncyCastle SM9EncMaster KeyPair generator.
     */
    public static class BouncySM9EncKeyPairGenerator
            extends BouncyKeyPairGenerator<SM9EncMasterPrivateKeyParameters, SM9EncMasterPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncySM9EncKeyPairGenerator(final GordianBaseFactory pFactory,
                                     final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final KeyGenerationParameters myParams = new KeyGenerationParameters(getRandom(), GordianLength.LEN_256.getLength());

            /* Create and initialise the generator */
            setGenerator(new SM9EncMasterKeyPairGenerator(), myParams);
            setFactorySet(BouncySM9EncKeyFactorySet.INSTANCE);
        }

        @Override
        BouncySM9EncMasterPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncySM9EncMasterPrivateKey(getKeySpec(), (SM9EncMasterPrivateKeyParameters) pThat);
        }

        @Override
        BouncySM9EncMasterPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncySM9EncMasterPublicKey(getKeySpec(), (SM9EncMasterPublicKeyParameters) pThat);
        }
    }

    /**
     * SM9Enc KeyFactorySet.
     */
    private enum BouncySM9EncKeyFactorySet
            implements BouncyKeyFactorySet {
        /**
         * Instance.
         */
        INSTANCE;

        @Override
        public AsymmetricKeyParameter parsePKCS8EncodedKeySpec(final PKCS8EncodedKeySpec pEncodedKey) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Parse the encoded keySpec */
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pEncodedKey.getEncoded());
                final byte[] myData = ASN1OctetString.getInstance(myInfo.parsePrivateKey()).getOctets();
                return SM9EncMasterPrivateKeyParameters.fromEncoded(myData);

            } catch (IOException e) {
                throw new GordianCryptoException(BouncyKeyPairGenerator.ERROR_PARSE, e);
            }
        }

        @Override
        public PKCS8EncodedKeySpec createPKCS8EncodedKeySpec(final AsymmetricKeyParameter pParams) throws GordianException {
            /* Protect against exceptions */
            try {
                /* build and return the encoding */
                final SM9EncMasterPrivateKeyParameters myParams = (SM9EncMasterPrivateKeyParameters) pParams;
                final AlgorithmIdentifier myId = new AlgorithmIdentifier(GMObjectIdentifiers.sm9encrypt);
                final PrivateKeyInfo myInfo = new PrivateKeyInfo(myId, new DEROctetString(myParams.getEncoded()));
                return new PKCS8EncodedKeySpec(myInfo.getEncoded(ASN1Encoding.DER));

            } catch (IOException e) {
                throw new GordianCryptoException(BouncyKeyPairGenerator.ERROR_PARSE, e);
            }
        }

        @Override
        public AsymmetricKeyParameter parseX509EncodedKeySpec(final X509EncodedKeySpec pEncodedKey) {
            /* Parse the encoded keySpec */
            final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
            return SM9EncMasterPublicKeyParameters.fromEncoded(myInfo.getPublicKeyData().getOctets());
        }

        @Override
        public X509EncodedKeySpec createX509EncodedKeySpec(final AsymmetricKeyParameter pParams) throws GordianException {
            /* build and return the encoding */
            try {
                final SM9EncMasterPublicKeyParameters myParams = (SM9EncMasterPublicKeyParameters) pParams;
                final AlgorithmIdentifier myId = new AlgorithmIdentifier(GMObjectIdentifiers.sm9encrypt);
                final SubjectPublicKeyInfo myInfo = new SubjectPublicKeyInfo(myId, new DEROctetString(myParams.getEncoded()));
                return new X509EncodedKeySpec(myInfo.getEncoded(ASN1Encoding.DER));

            } catch (IOException e) {
                throw new GordianCryptoException(BouncyKeyPairGenerator.ERROR_PARSE, e);
            }
        }
    }

    /**
     * BouncyCastle SM9SignMaster KeyPair generator.
     */
    public static class BouncySM9SignKeyPairGenerator
            extends BouncyKeyPairGenerator<SM9SigMasterPrivateKeyParameters, SM9SigMasterPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncySM9SignKeyPairGenerator(final GordianBaseFactory pFactory,
                                      final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Determine the parameters */
            final KeyGenerationParameters myParams = new KeyGenerationParameters(getRandom(), GordianLength.LEN_256.getLength());

            /* Create and initialise the generator */
            setGenerator(new SM9SigMasterKeyPairGenerator(), myParams);
            setFactorySet(BouncySM9SignKeyFactorySet.INSTANCE);
        }

        @Override
        BouncySM9SignMasterPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncySM9SignMasterPrivateKey(getKeySpec(), (SM9SigMasterPrivateKeyParameters) pThat);
        }

        @Override
        BouncySM9SignMasterPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncySM9SignMasterPublicKey(getKeySpec(), (SM9SigMasterPublicKeyParameters) pThat);
        }
    }

    /**
     * SM9Sig KeyFactorySet.
     */
    private enum BouncySM9SignKeyFactorySet
            implements BouncyKeyFactorySet {
        /**
         * Instance.
         */
        INSTANCE;

        @Override
        public AsymmetricKeyParameter parsePKCS8EncodedKeySpec(final PKCS8EncodedKeySpec pEncodedKey) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Parse the encoded keySpec */
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pEncodedKey.getEncoded());
                final byte[] myData = ASN1OctetString.getInstance(myInfo.parsePrivateKey()).getOctets();
                return SM9SigMasterPrivateKeyParameters.fromEncoded(myData);

            } catch (IOException e) {
                throw new GordianCryptoException(BouncyKeyPairGenerator.ERROR_PARSE, e);
            }
        }

        @Override
        public PKCS8EncodedKeySpec createPKCS8EncodedKeySpec(final AsymmetricKeyParameter pParams) throws GordianException {
            /* Protect against exceptions */
            try {
                /* build and return the encoding */
                final SM9SigMasterPrivateKeyParameters myParams = (SM9SigMasterPrivateKeyParameters) pParams;
                final AlgorithmIdentifier myId = new AlgorithmIdentifier(GMObjectIdentifiers.sm9sign);
                final PrivateKeyInfo myInfo = new PrivateKeyInfo(myId, new DEROctetString(myParams.getEncoded()));
                return new PKCS8EncodedKeySpec(myInfo.getEncoded(ASN1Encoding.DER));

            } catch (IOException e) {
                throw new GordianCryptoException(BouncyKeyPairGenerator.ERROR_PARSE, e);
            }
        }

        @Override
        public AsymmetricKeyParameter parseX509EncodedKeySpec(final X509EncodedKeySpec pEncodedKey) {
            /* Parse the encoded keySpec */
            final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
            return SM9SigMasterPublicKeyParameters.fromEncoded(myInfo.getPublicKeyData().getOctets());
        }

        @Override
        public X509EncodedKeySpec createX509EncodedKeySpec(final AsymmetricKeyParameter pParams) throws GordianException {
            /* build and return the encoding */
            try {
                final SM9SigMasterPublicKeyParameters myParams = (SM9SigMasterPublicKeyParameters) pParams;
                final AlgorithmIdentifier myId = new AlgorithmIdentifier(GMObjectIdentifiers.sm9sign);
                final SubjectPublicKeyInfo myInfo = new SubjectPublicKeyInfo(myId, new DEROctetString(myParams.getEncoded()));
                return new X509EncodedKeySpec(myInfo.getEncoded(ASN1Encoding.DER));

            } catch (IOException e) {
                throw new GordianCryptoException(BouncyKeyPairGenerator.ERROR_PARSE, e);
            }
        }
    }
}
