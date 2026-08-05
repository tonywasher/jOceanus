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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianIdAwareKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSM9Spec.GordianSM9EncryptType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSM9Spec.GordianSM9SignType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.GordianCoreIdAwareKeyPair.GordianIdAwarePrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.GordianCoreIdAwareKeyPair.GordianIdAwarePublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair.JcaIdAwareKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair.JcaPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair.JcaPublicKey;
import org.bouncycastle.jcajce.interfaces.SM9EncMasterPrivateKey;
import org.bouncycastle.jcajce.interfaces.SM9EncMasterPublicKey;
import org.bouncycastle.jcajce.interfaces.SM9EncUserKeyGenerator;
import org.bouncycastle.jcajce.interfaces.SM9SigMasterPrivateKey;
import org.bouncycastle.jcajce.interfaces.SM9SigMasterPublicKey;
import org.bouncycastle.util.Arrays;

import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

/**
 * Jca SM9 KeyPair generator.
 */
public final class JcaSM9KeyPairGenerator {
    /**
     * The builder.
     */
    private static final GordianCoreKeyPairSpecBuilder BUILDER = GordianCoreKeyPairSpecBuilder.newInstance();

    /**
     * The Encrypt keySpec.
     */
    private static final GordianKeyPairSpec ENCRYPT = BUILDER.sm9(GordianSM9EncryptType.ENCRYPT);

    /**
     * The Exchange keySpec.
     */
    private static final GordianKeyPairSpec EXCHANGE = BUILDER.sm9(GordianSM9EncryptType.EXCHANGE);

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
     * @param pPublicKey the master public key
     * @param pKeyType   the keyType
     * @param pIdentity  the identity
     * @return the public key
     */
    private static JcaSM9EncUserPublicKey deriveUserPublicKey(final SM9EncMasterPublicKey pPublicKey,
                                                              final GordianIdAwareKeyType pKeyType,
                                                              final byte[] pIdentity) {
        return switch ((GordianSM9EncryptType) pKeyType) {
            case ENCRYPT -> {
                final PublicKey myUserPublic = pPublicKey.getUserPublicKey(pIdentity,
                        SM9EncMasterPublicKey.HID);
                yield new JcaSM9EncUserPublicKey(ENCRYPT, myUserPublic, pPublicKey, pIdentity);
            }
            case EXCHANGE -> {
                final PublicKey myUserPublic = pPublicKey.getUserPublicKey(pIdentity,
                        SM9EncMasterPublicKey.HID_EXCHANGE);
                yield new JcaSM9EncUserPublicKey(EXCHANGE, myUserPublic, pPublicKey, pIdentity);
            }
            default -> null;
        };
    }

    /**
     * Jca SM9EncMaster PublicKey.
     */
    public static class JcaSM9EncMasterPublicKey
            extends JcaPublicKey
            implements GordianIdAwarePublicKey {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        JcaSM9EncMasterPublicKey(final GordianKeyPairSpec pKeySpec,
                                 final PublicKey pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        public GordianSM9EncryptType getSubKeyType() {
            return GordianSM9EncryptType.ENCMASTER;
        }

        @Override
        public JcaSM9EncUserPublicKey deriveUserPublicKey(final GordianIdAwareKeyType pKeyType,
                                                          final byte[] pIdentity) {
            final SM9EncMasterPublicKey myPublic = (SM9EncMasterPublicKey) getPublicKey();
            return JcaSM9KeyPairGenerator.deriveUserPublicKey(myPublic, pKeyType, pIdentity);
        }
    }

    /**
     * Jca SM9EncMaster PrivateKey.
     */
    public static class JcaSM9EncMasterPrivateKey
            extends JcaPrivateKey
            implements GordianIdAwarePrivateKey {
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
                    yield new JcaSM9EncUserPrivateKey(ENCRYPT, myUserPrivate, pIdentity);
                }
                case EXCHANGE -> {
                    final PrivateKey myUserPrivate = myPrivate.generateExchangeKeyPair(pIdentity).getPrivate();
                    yield new JcaSM9EncUserPrivateKey(EXCHANGE, myUserPrivate, pIdentity);
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
            implements GordianIdAwarePublicKey {
        /**
         * The master publicKey.
         */
        private final SM9EncMasterPublicKey theMasterPublic;

        /**
         * The identity.
         */
        private final byte[] theIdentity;

        /**
         * Constructor.
         *
         * @param pKeySpec      the keySpec
         * @param pPublicKey    the public key
         * @param pMasterPublic the master publicKey
         * @param pIdentity     the identity
         */
        JcaSM9EncUserPublicKey(final GordianKeyPairSpec pKeySpec,
                               final PublicKey pPublicKey,
                               final SM9EncMasterPublicKey pMasterPublic,
                               final byte[] pIdentity) {
            super(pKeySpec, pPublicKey);
            theMasterPublic = pMasterPublic;
            theIdentity = pIdentity.clone();
        }

        @Override
        public GordianSM9EncryptType getSubKeyType() {
            return (GordianSM9EncryptType) getKeySpec().getSubSpec();
        }

        @Override
        public byte[] getIdentity() {
            return Arrays.clone(theIdentity);
        }

        @Override
        public JcaSM9EncUserPublicKey deriveUserPublicKey(final GordianIdAwareKeyType pKeyType,
                                                          final byte[] pIdentity) {
            return JcaSM9KeyPairGenerator.deriveUserPublicKey(theMasterPublic, pKeyType, pIdentity);
        }

        /**
         * Obtain the master publicKey
         *
         * @return the master public key
         */
        public SM9EncMasterPublicKey getMasterPublicKey() {
            return theMasterPublic;
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
            if (!(pThat instanceof JcaSM9EncUserPublicKey myThat)) {
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
     * Jca SM9EncUser PrivateKey.
     */
    public static class JcaSM9EncUserPrivateKey
            extends JcaPrivateKey
            implements GordianIdAwarePrivateKey {
        /**
         * The identity.
         */
        private final byte[] theIdentity;

        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         * @param pIdentity   the identity
         */
        JcaSM9EncUserPrivateKey(final GordianKeyPairSpec pKeySpec,
                                final PrivateKey pPrivateKey,
                                final byte[] pIdentity) {
            super(pKeySpec, pPrivateKey);
            theIdentity = pIdentity.clone();
        }

        @Override
        public GordianSM9EncryptType getSubKeyType() {
            return (GordianSM9EncryptType) getKeySpec().getSubSpec();
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
            if (!(pThat instanceof JcaSM9EncUserPrivateKey myThat)) {
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
     * Jca SM9SignMaster PublicKey.
     */
    public static class JcaSM9SignMasterPublicKey
            extends JcaPublicKey
            implements GordianIdAwarePublicKey {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        JcaSM9SignMasterPublicKey(final GordianKeyPairSpec pKeySpec,
                                  final PublicKey pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        public GordianSM9SignType getSubKeyType() {
            return GordianSM9SignType.SIGNMASTER;
        }

        @Override
        public JcaSM9SignUserPublicKey deriveUserPublicKey(final GordianIdAwareKeyType pKeyType,
                                                           final byte[] pIdentity) {
            final SM9SigMasterPublicKey myPublic = (SM9SigMasterPublicKey) getPublicKey();
            return new JcaSM9SignUserPublicKey(SIGN, myPublic, pIdentity);
        }
    }

    /**
     * Jca SM9SignMaster PrivateKey.
     */
    public static class JcaSM9SignMasterPrivateKey
            extends JcaPrivateKey
            implements GordianIdAwarePrivateKey {
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
            return new JcaSM9SignUserPrivateKey(SIGN, myUserPrivate, pIdentity);
        }
    }

    /**
     * Jca SM9SignUser PublicKey.
     */
    public static class JcaSM9SignUserPublicKey
            extends JcaPublicKey
            implements GordianIdAwarePublicKey {
        /**
         * The master publicKey.
         */
        private final SM9SigMasterPublicKey theMasterPublic;

        /**
         * The identity.
         */
        private final byte[] theIdentity;

        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the master public key
         * @param pIdentity  the identity
         */
        JcaSM9SignUserPublicKey(final GordianKeyPairSpec pKeySpec,
                                final SM9SigMasterPublicKey pPublicKey,
                                final byte[] pIdentity) {
            super(pKeySpec, pPublicKey.getUserPublicKey(pIdentity));
            theMasterPublic = pPublicKey;
            theIdentity = pIdentity.clone();
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
        public JcaSM9SignUserPublicKey deriveUserPublicKey(final GordianIdAwareKeyType pKeyType,
                                                           final byte[] pIdentity) {
            return new JcaSM9SignUserPublicKey(SIGN, theMasterPublic, pIdentity);
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
            if (!(pThat instanceof JcaSM9SignUserPublicKey myThat)) {
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
     * Jca SM9SignUser PrivateKey.
     */
    public static class JcaSM9SignUserPrivateKey
            extends JcaPrivateKey
            implements GordianIdAwarePrivateKey {
        /**
         * The identity.
         */
        private final byte[] theIdentity;

        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         * @param pIdentity   the identity
         */
        JcaSM9SignUserPrivateKey(final GordianKeyPairSpec pKeySpec,
                                 final PrivateKey pPrivateKey,
                                 final byte[] pIdentity) {
            super(pKeySpec, pPrivateKey);
            theIdentity = pIdentity.clone();
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
            if (!(pThat instanceof JcaSM9SignUserPrivateKey myThat)) {
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
            return new JcaSM9EncMasterPublicKey(getKeySpec(), pThat);
        }

        @Override
        public JcaKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws GordianException {
            final JcaPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new JcaIdAwareKeyPair(myPublic, null);
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
            return new JcaSM9SignMasterPublicKey(getKeySpec(), pThat);
        }

        @Override
        public JcaKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws GordianException {
            final JcaPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new JcaIdAwareKeyPair(myPublic, null);
        }
    }
}
