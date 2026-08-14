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

package io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianIdAwareKeyPair.GordianIdAwareUserKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianIdAwareKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSM9Spec.GordianSM9EncryptType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSM9Spec.GordianSM9SignType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.GordianCoreIdAwareKeyPair.GordianIdAwareMasterPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.GordianCoreIdAwareKeyPair.GordianIdAwareMasterPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.GordianCoreIdAwareKeyPair.GordianIdAwareUserPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.GordianCoreIdAwareKeyPair.GordianIdAwareUserPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair.JcaIdAwareMasterKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair.JcaIdAwareUserKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair.JcaPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair.JcaPublicKey;
import org.bouncycastle.crypto.params.SM9EncMasterPrivateKeyParameters;
import org.bouncycastle.jcajce.interfaces.SM9EncMasterPrivateKey;
import org.bouncycastle.jcajce.interfaces.SM9EncMasterPublicKey;
import org.bouncycastle.jcajce.interfaces.SM9EncUserKeyGenerator;
import org.bouncycastle.jcajce.interfaces.SM9EncUserPrivateKey;
import org.bouncycastle.jcajce.interfaces.SM9EncUserPublicKey;
import org.bouncycastle.jcajce.interfaces.SM9SigMasterPrivateKey;
import org.bouncycastle.jcajce.interfaces.SM9SigMasterPublicKey;
import org.bouncycastle.jcajce.interfaces.SM9SigUserPrivateKey;
import org.bouncycastle.jcajce.interfaces.SM9SigUserPublicKey;
import org.bouncycastle.jcajce.spec.SM9EncUserPrivateKeySpec;
import org.bouncycastle.jcajce.spec.SM9SigUserPrivateKeySpec;
import org.bouncycastle.util.Arrays;

import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Jca SM9 KeyPair generator.
 */
public final class JcaSM9KeyPairGenerator {
    /**
     * The builder.
     */
    private static final GordianCoreKeyPairSpecBuilder BUILDER = GordianCoreKeyPairSpecBuilder.newInstance();

    /**
     * The EncryptMaster keySpec.
     */
    private static final GordianKeyPairSpec ENCMASTER = BUILDER.sm9(GordianSM9EncryptType.ENCMASTER);

    /**
     * The Encrypt keySpec.
     */
    private static final GordianKeyPairSpec ENCRYPT = BUILDER.sm9(GordianSM9EncryptType.ENCRYPT);

    /**
     * The Exchange keySpec.
     */
    private static final GordianKeyPairSpec EXCHANGE = BUILDER.sm9(GordianSM9EncryptType.EXCHANGE);

    /**
     * The SignMaster keySpec.
     */
    private static final GordianKeyPairSpec SIGNMASTER = BUILDER.sm9(GordianSM9SignType.SIGNMASTER);

    /**
     * The Sign keySpec.
     */
    private static final GordianKeyPairSpec SIGN = BUILDER.sm9(GordianSM9SignType.SIGN);

    /**
     * Private Constructor.
     */
    private JcaSM9KeyPairGenerator() {
    }

    /**
     * Derive User Encoding public key.
     *
     * @param pPublicKey  the master public key
     * @param pKeyType    the keyType
     * @param pIdentity   the identity
     * @param pKeyFactory the keyFactory
     * @return the public key
     */
    private static JcaSM9EncUserPublicKey deriveUserPublicKey(final SM9EncMasterPublicKey pPublicKey,
                                                              final GordianIdAwareKeyType pKeyType,
                                                              final byte[] pIdentity,
                                                              final KeyFactory pKeyFactory) {
        return switch ((GordianSM9EncryptType) pKeyType) {
            case ENCRYPT -> {
                final PublicKey myUserPublic = pPublicKey.getUserPublicKey(pIdentity,
                        SM9EncMasterPublicKey.HID);
                yield new JcaSM9EncUserPublicKey(ENCRYPT, myUserPublic, pKeyFactory);
            }
            case EXCHANGE -> {
                final PublicKey myUserPublic = pPublicKey.getUserPublicKey(pIdentity,
                        SM9EncMasterPublicKey.HID_EXCHANGE);
                yield new JcaSM9EncUserPublicKey(EXCHANGE, myUserPublic, pKeyFactory);
            }
            default -> null;
        };
    }

    /**
     * Jca SM9EncMaster PublicKey.
     */
    public static class JcaSM9EncMasterPublicKey
            extends JcaPublicKey
            implements GordianIdAwareMasterPublicKey {
        /**
         * The factory.
         */
        private final KeyFactory theKeyFactory;

        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPublicKey  the public key
         * @param pKeyFactory the keyFactory
         */
        JcaSM9EncMasterPublicKey(final GordianKeyPairSpec pKeySpec,
                                 final PublicKey pPublicKey,
                                 final KeyFactory pKeyFactory) {
            super(pKeySpec, pPublicKey);
            theKeyFactory = pKeyFactory;
        }

        @Override
        public GordianSM9EncryptType getSubKeyType() {
            return GordianSM9EncryptType.ENCMASTER;
        }

        @Override
        public JcaSM9EncUserPublicKey deriveUserPublicKey(final GordianIdAwareKeyType pKeyType,
                                                          final byte[] pIdentity) {
            final SM9EncMasterPublicKey myPublic = (SM9EncMasterPublicKey) getPublicKey();
            return JcaSM9KeyPairGenerator.deriveUserPublicKey(myPublic, pKeyType, pIdentity, theKeyFactory);
        }

        @Override
        public JcaIdAwareMasterKeyPair deriveMasterPublicKey() {
            return new JcaIdAwareMasterKeyPair(this, null);
        }

        @Override
        public GordianIdAwareUserKeyPair deriveUserKeyPairFromEncoding(final PKCS8EncodedKeySpec pEncoding,
                                                                       final GordianIdAwareKeyType pKeyType,
                                                                       final byte[] pIdentity) throws GordianException {
            /* Can't derive EXCHANGE key from Encoding */
            if (GordianSM9EncryptType.EXCHANGE.equals(pKeyType)) {
                throw new GordianDataException("Can't derive EXCHANGE keyPairs from encoding");
            }

            /* Protect against exceptions */
            try {
                /* Build the private key from encoded */
                final SM9EncMasterPublicKey myMasterPublic = (SM9EncMasterPublicKey) getPublicKey();
                final SM9EncUserPrivateKeySpec mySpec =
                        new SM9EncUserPrivateKeySpec(pEncoding.getEncoded(), myMasterPublic, pIdentity, SM9EncMasterPrivateKeyParameters.HID);
                final PrivateKey myDerived = theKeyFactory.generatePrivate(mySpec);
                final JcaSM9EncUserPrivateKey myPrivate = new JcaSM9EncUserPrivateKey(ENCRYPT, myDerived);
                final JcaSM9EncUserPublicKey myPublic = deriveUserPublicKey(pKeyType, pIdentity);
                return new JcaIdAwareUserKeyPair(myPublic, myPrivate);
            } catch (InvalidKeySpecException e) {
                throw new GordianDataException("Invalid encoded data", e);
            }
        }

        @Override
        public boolean equals(final Object pThat) {
            return super.equals(pThat);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    /**
     * Jca SM9EncMaster PrivateKey.
     */
    public static class JcaSM9EncMasterPrivateKey
            extends JcaPrivateKey
            implements GordianIdAwareMasterPrivateKey {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        JcaSM9EncMasterPrivateKey(final GordianKeyPairSpec pKeySpec,
                                  final PrivateKey pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        public GordianSM9EncryptType getSubKeyType() {
            return GordianSM9EncryptType.ENCMASTER;
        }

        @Override
        public JcaSM9EncUserPrivateKey newUserPrivateKey(final GordianIdAwareKeyType pKeyType,
                                                         final byte[] pIdentity) {
            final SM9EncMasterPrivateKey myPrivate = (SM9EncMasterPrivateKey) getPrivateKey();
            return switch ((GordianSM9EncryptType) pKeyType) {
                case ENCRYPT -> {
                    final PrivateKey myUserPrivate = myPrivate.generateUserKeyPair(pIdentity,
                            SM9EncUserKeyGenerator.HID).getPrivate();
                    yield new JcaSM9EncUserPrivateKey(ENCRYPT, myUserPrivate);
                }
                case EXCHANGE -> {
                    final PrivateKey myUserPrivate = myPrivate.generateExchangeKeyPair(pIdentity).getPrivate();
                    yield new JcaSM9EncUserPrivateKey(EXCHANGE, myUserPrivate);
                }
                default -> null;
            };
        }
    }

    /**
     * Jca SM9EncUser PublicKey.
     */
    public static class JcaSM9EncUserPublicKey
            extends JcaPublicKey
            implements GordianIdAwareUserPublicKey {
        /**
         * The factory.
         */
        private final KeyFactory theKeyFactory;

        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPublicKey  the public key
         * @param pKeyFactory the keyFactory
         */
        JcaSM9EncUserPublicKey(final GordianKeyPairSpec pKeySpec,
                               final PublicKey pPublicKey,
                               final KeyFactory pKeyFactory) {
            super(pKeySpec, pPublicKey);
            theKeyFactory = pKeyFactory;
        }

        @Override
        public GordianSM9EncryptType getSubKeyType() {
            return (GordianSM9EncryptType) getKeySpec().getSubSpec();
        }

        @Override
        public byte[] getIdentity() {
            final SM9EncUserPublicKey myPublic = (SM9EncUserPublicKey) getPublicKey();
            return Arrays.clone(myPublic.getIdentity());
        }

        @Override
        public JcaSM9EncUserPublicKey deriveUserPublicKey(final GordianIdAwareKeyType pKeyType,
                                                          final byte[] pIdentity) {
            return JcaSM9KeyPairGenerator.deriveUserPublicKey(getMasterPublicKey(), pKeyType, pIdentity, theKeyFactory);
        }

        /**
         * Obtain the master publicKey
         *
         * @return the master public key
         */
        public SM9EncMasterPublicKey getMasterPublicKey() {
            final SM9EncUserPublicKey myPublic = (SM9EncUserPublicKey) getPublicKey();
            return myPublic.getMasterPublicKey();
        }

        @Override
        public JcaIdAwareMasterKeyPair deriveMasterPublicKey() {
            final JcaSM9EncMasterPublicKey myPublic = new JcaSM9EncMasterPublicKey(ENCMASTER, getMasterPublicKey(), theKeyFactory);
            return new JcaIdAwareMasterKeyPair(myPublic, null);
        }

        @Override
        public boolean equals(final Object pThat) {
            return super.equals(pThat);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    /**
     * Jca SM9EncUser PrivateKey.
     */
    public static class JcaSM9EncUserPrivateKey
            extends JcaPrivateKey
            implements GordianIdAwareUserPrivateKey {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        JcaSM9EncUserPrivateKey(final GordianKeyPairSpec pKeySpec,
                                final PrivateKey pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        public GordianSM9EncryptType getSubKeyType() {
            return (GordianSM9EncryptType) getKeySpec().getSubSpec();
        }

        @Override
        public byte[] getIdentity() {
            final SM9EncUserPrivateKey myPrivate = (SM9EncUserPrivateKey) getPrivateKey();
            return Arrays.clone(myPrivate.getIdentity());
        }

        @Override
        public PKCS8EncodedKeySpec getPartialEncoding() throws GordianException {
            /* Can't derive EXCHANGE key from Encoding */
            if (GordianSM9EncryptType.EXCHANGE.equals(getSubKeyType())) {
                throw new GordianDataException("Can't obtain EXCHANGE privateKey encoding");
            }
            return new PKCS8EncodedKeySpec(getPrivateKey().getEncoded());
        }
    }

    /**
     * Jca SM9SignMaster PublicKey.
     */
    public static class JcaSM9SignMasterPublicKey
            extends JcaPublicKey
            implements GordianIdAwareMasterPublicKey {
        /**
         * The factory.
         */
        private final KeyFactory theKeyFactory;

        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPublicKey  the public key
         * @param pKeyFactory the keyFactory
         */
        JcaSM9SignMasterPublicKey(final GordianKeyPairSpec pKeySpec,
                                  final PublicKey pPublicKey,
                                  final KeyFactory pKeyFactory) {
            super(pKeySpec, pPublicKey);
            theKeyFactory = pKeyFactory;
        }

        @Override
        public GordianSM9SignType getSubKeyType() {
            return GordianSM9SignType.SIGNMASTER;
        }

        @Override
        public JcaSM9SignUserPublicKey deriveUserPublicKey(final GordianIdAwareKeyType pKeyType,
                                                           final byte[] pIdentity) {
            final SM9SigMasterPublicKey myPublic = (SM9SigMasterPublicKey) getPublicKey();
            final SM9SigUserPublicKey myUserPublic = (SM9SigUserPublicKey) myPublic.getUserPublicKey(pIdentity);
            return new JcaSM9SignUserPublicKey(SIGN, myUserPublic, theKeyFactory);
        }

        @Override
        public JcaIdAwareMasterKeyPair deriveMasterPublicKey() {
            return new JcaIdAwareMasterKeyPair(this, null);
        }

        @Override
        public GordianIdAwareUserKeyPair deriveUserKeyPairFromEncoding(final PKCS8EncodedKeySpec pEncoding,
                                                                       final GordianIdAwareKeyType pKeyType,
                                                                       final byte[] pIdentity) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Build the private key from encoded */
                final SM9SigMasterPublicKey myMasterPublic = (SM9SigMasterPublicKey) getPublicKey();
                final SM9SigUserPrivateKeySpec mySpec = new SM9SigUserPrivateKeySpec(pEncoding.getEncoded(), myMasterPublic, pIdentity);
                final PrivateKey myDerived = theKeyFactory.generatePrivate(mySpec);
                final JcaSM9SignUserPrivateKey myPrivate = new JcaSM9SignUserPrivateKey(SIGN, myDerived);
                final JcaSM9SignUserPublicKey myPublic = deriveUserPublicKey(pKeyType, pIdentity);
                return new JcaIdAwareUserKeyPair(myPublic, myPrivate);
            } catch (InvalidKeySpecException e) {
                throw new GordianDataException("Invalid encoded data", e);
            }
        }

        @Override
        public boolean equals(final Object pThat) {
            return super.equals(pThat);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    /**
     * Jca SM9SignMaster PrivateKey.
     */
    public static class JcaSM9SignMasterPrivateKey
            extends JcaPrivateKey
            implements GordianIdAwareMasterPrivateKey {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        JcaSM9SignMasterPrivateKey(final GordianKeyPairSpec pKeySpec,
                                   final PrivateKey pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        public GordianSM9SignType getSubKeyType() {
            return GordianSM9SignType.SIGNMASTER;
        }

        @Override
        public JcaSM9SignUserPrivateKey newUserPrivateKey(final GordianIdAwareKeyType pKeyType,
                                                          final byte[] pIdentity) {
            final SM9SigMasterPrivateKey myPrivate = (SM9SigMasterPrivateKey) getPrivateKey();
            final PrivateKey myUserPrivate = myPrivate.generateUserKeyPair(pIdentity).getPrivate();
            return new JcaSM9SignUserPrivateKey(SIGN, myUserPrivate);
        }
    }

    /**
     * Jca SM9SignUser PublicKey.
     */
    public static class JcaSM9SignUserPublicKey
            extends JcaPublicKey
            implements GordianIdAwareUserPublicKey {
        /**
         * The factory.
         */
        private final KeyFactory theKeyFactory;

        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPublicKey  the master public key
         * @param pKeyFactory the keyFactory
         */
        JcaSM9SignUserPublicKey(final GordianKeyPairSpec pKeySpec,
                                final SM9SigUserPublicKey pPublicKey,
                                final KeyFactory pKeyFactory) {
            super(pKeySpec, pPublicKey);
            theKeyFactory = pKeyFactory;
        }

        @Override
        public GordianSM9SignType getSubKeyType() {
            return GordianSM9SignType.SIGN;
        }

        @Override
        public byte[] getIdentity() {
            final SM9SigUserPublicKey myPublic = (SM9SigUserPublicKey) getPublicKey();
            return Arrays.clone(myPublic.getIdentity());
        }

        @Override
        public JcaSM9SignUserPublicKey deriveUserPublicKey(final GordianIdAwareKeyType pKeyType,
                                                           final byte[] pIdentity) {
            final SM9SigMasterPublicKey myPublic = getMasterPublicKey();
            final SM9SigUserPublicKey myUserPublic = (SM9SigUserPublicKey) myPublic.getUserPublicKey(pIdentity);
            return new JcaSM9SignUserPublicKey(SIGN, myUserPublic, theKeyFactory);
        }

        @Override
        public JcaIdAwareMasterKeyPair deriveMasterPublicKey() {
            final JcaSM9SignMasterPublicKey myPublic = new JcaSM9SignMasterPublicKey(SIGNMASTER, getMasterPublicKey(), theKeyFactory);
            return new JcaIdAwareMasterKeyPair(myPublic, null);
        }

        /**
         * Obtain the master publicKey
         *
         * @return the master public key
         */
        public SM9SigMasterPublicKey getMasterPublicKey() {
            final SM9SigUserPublicKey myPublic = (SM9SigUserPublicKey) getPublicKey();
            return myPublic.getMasterPublicKey();
        }

        @Override
        public boolean equals(final Object pThat) {
            return super.equals(pThat);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    /**
     * Jca SM9SignUser PrivateKey.
     */
    public static class JcaSM9SignUserPrivateKey
            extends JcaPrivateKey
            implements GordianIdAwareUserPrivateKey {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        JcaSM9SignUserPrivateKey(final GordianKeyPairSpec pKeySpec,
                                 final PrivateKey pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        public GordianSM9SignType getSubKeyType() {
            return GordianSM9SignType.SIGN;
        }

        @Override
        public byte[] getIdentity() {
            final SM9SigUserPrivateKey myPrivate = (SM9SigUserPrivateKey) getPrivateKey();
            return Arrays.clone(myPrivate.getIdentity());
        }

        @Override
        public PKCS8EncodedKeySpec getPartialEncoding() {
            return new PKCS8EncodedKeySpec(getPrivateKey().getEncoded());
        }
    }

    /**
     * Jca SM9EncMaster KeyPair generator.
     */
    public static class JcaSM9EncKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws GordianException on error
         */
        JcaSM9EncKeyPairGenerator(final GordianBaseFactory pFactory,
                                  final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialize the generator */
            final KeyPairGenerator myGenerator = getJavaKeyPairGenerator("SM9-ENC", false);
            setKeyPairGenerator(myGenerator);
            setKeyFactory(getJavaKeyFactory("SM9", false));
            getGenerator().initialize(GordianLength.LEN_256.getLength(), getRandom());
        }

        @Override
        protected JcaSM9EncMasterPrivateKey createPrivate(final PrivateKey pThat) {
            return new JcaSM9EncMasterPrivateKey(getKeySpec(), pThat);
        }

        @Override
        protected JcaSM9EncMasterPublicKey createPublic(final PublicKey pThat) {
            return new JcaSM9EncMasterPublicKey(getKeySpec(), pThat, getKeyFactory());
        }
    }

    /**
     * Jca SM9SignMaster KeyPair generator.
     */
    public static class JcaSM9SignKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws GordianException on error
         */
        JcaSM9SignKeyPairGenerator(final GordianBaseFactory pFactory,
                                   final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialize the generator */
            final KeyPairGenerator myGenerator = getJavaKeyPairGenerator("SM9-SIGN", false);
            setKeyPairGenerator(myGenerator);
            setKeyFactory(getJavaKeyFactory("SM9", false));
            getGenerator().initialize(GordianLength.LEN_256.getLength(), getRandom());
        }

        @Override
        protected JcaSM9SignMasterPrivateKey createPrivate(final PrivateKey pThat) {
            return new JcaSM9SignMasterPrivateKey(getKeySpec(), pThat);
        }

        @Override
        protected JcaSM9SignMasterPublicKey createPublic(final PublicKey pThat) {
            return new JcaSM9SignMasterPublicKey(getKeySpec(), pThat, getKeyFactory());
        }
    }
}
