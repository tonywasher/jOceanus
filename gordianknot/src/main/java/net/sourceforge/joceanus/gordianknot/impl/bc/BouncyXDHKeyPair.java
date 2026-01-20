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
package net.sourceforge.joceanus.gordianknot.impl.bc;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianKeyPairValidity;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.RawAgreement;
import org.bouncycastle.crypto.agreement.X25519Agreement;
import org.bouncycastle.crypto.agreement.X448Agreement;
import org.bouncycastle.crypto.agreement.XDHUnifiedAgreement;
import org.bouncycastle.crypto.generators.X25519KeyPairGenerator;
import org.bouncycastle.crypto.generators.X448KeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448KeyGenerationParameters;
import org.bouncycastle.crypto.params.X448PrivateKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.crypto.params.XDHUPrivateParameters;
import org.bouncycastle.crypto.params.XDHUPublicParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;

import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

/**
 * EdwardsCurve XDH KeyPair classes.
 */
public final class BouncyXDHKeyPair {
    /**
     * Private constructor.
     */
    private BouncyXDHKeyPair() {
    }

    /**
     * Bouncy EdwardsX25519 PublicKey.
     */
    public static class BouncyX25519PublicKey
            extends BouncyPublicKey<X25519PublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyX25519PublicKey(final GordianKeyPairSpec pKeySpec,
                              final X25519PublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final X25519PublicKeyParameters myThis = getPublicKey();
            final X25519PublicKeyParameters myThat = (X25519PublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy EdwardsX25519 PrivateKey.
     */
    public static class BouncyX25519PrivateKey
            extends BouncyPrivateKey<X25519PrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyX25519PrivateKey(final GordianKeyPairSpec pKeySpec,
                               final X25519PrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final X25519PrivateKeyParameters myThis = getPrivateKey();
            final X25519PrivateKeyParameters myThat = (X25519PrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy EdwardsX448 PublicKey.
     */
    public static class BouncyX448PublicKey
            extends BouncyPublicKey<X448PublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyX448PublicKey(final GordianKeyPairSpec pKeySpec,
                            final X448PublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final X448PublicKeyParameters myThis = getPublicKey();
            final X448PublicKeyParameters myThat = (X448PublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy EdwardsX448 PrivateKey.
     */
    public static class BouncyX448PrivateKey
            extends BouncyPrivateKey<X448PrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyX448PrivateKey(final GordianKeyPairSpec pKeySpec,
                             final X448PrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final X448PrivateKeyParameters myThis = getPrivateKey();
            final X448PrivateKeyParameters myThat = (X448PrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle EdwardsX25519 KeyPair generator.
     */
    public static class BouncyX25519KeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final X25519KeyPairGenerator theGenerator;

        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyX25519KeyPairGenerator(final GordianBaseFactory pFactory,
                                     final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create the generator */
            theGenerator = new X25519KeyPairGenerator();

            /* Initialise the generator */
            final X25519KeyGenerationParameters myParams = new X25519KeyGenerationParameters(getRandom());
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyX25519PublicKey myPublic = new BouncyX25519PublicKey(getKeySpec(), (X25519PublicKeyParameters) myPair.getPublic());
            final BouncyX25519PrivateKey myPrivate = new BouncyX25519PrivateKey(getKeySpec(), (X25519PrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Check the keyPair type and keySpecs */
                BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

                /* build and return the encoding */
                final BouncyX25519PrivateKey myPrivateKey = (BouncyX25519PrivateKey) getPrivateKey(pKeyPair);
                final X25519PrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
                final PrivateKeyInfo myInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(myParms);
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
                final BouncyX25519PublicKey myPublic = derivePublicKey(pPublicKey);
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final X25519PrivateKeyParameters myParms = (X25519PrivateKeyParameters) PrivateKeyFactory.createKey(myInfo);
                final BouncyX25519PrivateKey myPrivate = new BouncyX25519PrivateKey(getKeySpec(), myParms);
                final BouncyKeyPair myPair = new BouncyKeyPair(myPublic, myPrivate);

                /* Check that we have a matching pair */
                GordianKeyPairValidity.checkValidity(getFactory(), myPair);

                /* Return the derived keyPair */
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
                final BouncyX25519PublicKey myPublicKey = (BouncyX25519PublicKey) getPublicKey(pKeyPair);
                final X25519PublicKeyParameters myParms = myPublicKey.getPublicKey();
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(myParms);
                final byte[] myBytes = myInfo.getEncoded(ASN1Encoding.DER);
                return new X509EncodedKeySpec(myBytes);

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws GordianException {
            final BouncyX25519PublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         *
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws GordianException on error
         */
        private BouncyX25519PublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pEncodedKey);

                /* derive publicKey */
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final X25519PublicKeyParameters myParms = (X25519PublicKeyParameters) PublicKeyFactory.createKey(myInfo);
                return new BouncyX25519PublicKey(getKeySpec(), myParms);

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * BouncyCastle EdwardsX448 KeyPair generator.
     */
    public static class BouncyX448KeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final X448KeyPairGenerator theGenerator;

        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyX448KeyPairGenerator(final GordianBaseFactory pFactory,
                                   final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create the generator */
            theGenerator = new X448KeyPairGenerator();

            /* Initialise the generator */
            final X448KeyGenerationParameters myParams = new X448KeyGenerationParameters(getRandom());
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyX448PublicKey myPublic = new BouncyX448PublicKey(getKeySpec(), (X448PublicKeyParameters) myPair.getPublic());
            final BouncyX448PrivateKey myPrivate = new BouncyX448PrivateKey(getKeySpec(), (X448PrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Check the keyPair type and keySpecs */
                BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

                /* build and return the encoding */
                final BouncyX448PrivateKey myPrivateKey = (BouncyX448PrivateKey) getPrivateKey(pKeyPair);
                final X448PrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
                final PrivateKeyInfo myInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(myParms);
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
                final BouncyX448PublicKey myPublic = derivePublicKey(pPublicKey);
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final X448PrivateKeyParameters myParms = (X448PrivateKeyParameters) PrivateKeyFactory.createKey(myInfo);
                final BouncyX448PrivateKey myPrivate = new BouncyX448PrivateKey(getKeySpec(), myParms);
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
                final BouncyX448PublicKey myPublicKey = (BouncyX448PublicKey) getPublicKey(pKeyPair);
                final X448PublicKeyParameters myParms = myPublicKey.getPublicKey();
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(myParms);
                final byte[] myBytes = myInfo.getEncoded(ASN1Encoding.DER);
                return new X509EncodedKeySpec(myBytes);

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws GordianException {
            final BouncyX448PublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         *
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws GordianException on error
         */
        private BouncyX448PublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pEncodedKey);

                /* derive publicKey */
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final X448PublicKeyParameters myParms = (X448PublicKeyParameters) PublicKeyFactory.createKey(myInfo);
                return new BouncyX448PublicKey(getKeySpec(), myParms);

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * Establish the agreement.
     *
     * @param pSpec the keyPairSpec
     * @return the agreement
     */
    private static RawAgreement establishAgreement(final GordianKeyPairSpec pSpec) {
        return pSpec.getEdwardsElliptic().is25519()
                ? new X25519Agreement()
                : new X448Agreement();
    }

    /**
     * XDH Anonymous Agreement Engine.
     */
    public static class BouncyXDHAnonAgreementEngine
            extends BouncyAgreementBase {
        /**
         * The agreement.
         */
        private final RawAgreement theAgreement;

        /**
         * Constructor.
         *
         * @param pFactory the security factory
         * @param pSpec    the agreementSpec
         * @throws GordianException on error
         */
        BouncyXDHAnonAgreementEngine(final GordianCoreAgreementFactory pFactory,
                                     final GordianAgreementSpec pSpec) throws GordianException {
            /* Initialize underlying class */
            super(pFactory, pSpec);

            /* Create the agreement */
            theAgreement = establishAgreement(pSpec.getKeyPairSpec());
        }

        @Override
        public void buildClientHello() throws GordianException {
            /* Access keys */
            final BouncyPublicKey<?> myPublic = (BouncyPublicKey<?>) getPublicKey(getServerKeyPair());
            final BouncyPrivateKey<?> myPrivate = (BouncyPrivateKey<?>) getPrivateKey(getClientEphemeral());

            /* Derive the secret */
            theAgreement.init(myPrivate.getPrivateKey());
            final byte[] mySecret = new byte[theAgreement.getAgreementSize()];
            theAgreement.calculateAgreement(myPublic.getPublicKey(), mySecret, 0);

            /* Store secret */
            storeSecret(mySecret);
        }

        @Override
        public void processClientHello() throws GordianException {
            /* Access keys */
            final BouncyPublicKey<?> myPublic = (BouncyPublicKey<?>) getPublicKey(getClientEphemeral());
            final BouncyPrivateKey<?> myPrivate = (BouncyPrivateKey<?>) getPrivateKey(getServerKeyPair());

            /* Derive the secret */
            theAgreement.init(myPrivate.getPrivateKey());
            final byte[] mySecret = new byte[theAgreement.getAgreementSize()];
            theAgreement.calculateAgreement(myPublic.getPublicKey(), mySecret, 0);

            /* Store secret */
            storeSecret(mySecret);
        }
    }

    /**
     * DH Basic Agreement Engine.
     */
    public static class BouncyXDHBasicAgreementEngine
            extends BouncyAgreementBase {
        /**
         * The agreement.
         */
        private final RawAgreement theAgreement;

        /**
         * Constructor.
         *
         * @param pFactory the security factory
         * @param pSpec    the agreementSpec
         * @throws GordianException on error
         */
        BouncyXDHBasicAgreementEngine(final GordianCoreAgreementFactory pFactory,
                                      final GordianAgreementSpec pSpec) throws GordianException {
            /* Initialize underlying class */
            super(pFactory, pSpec);

            /* Create the agreement */
            theAgreement = establishAgreement(pSpec.getKeyPairSpec());
        }

        @Override
        public void processClientHello() throws GordianException {
            /* Access keys */
            final BouncyPublicKey<?> myPublic = (BouncyPublicKey<?>) getPublicKey(getClientKeyPair());
            final BouncyPrivateKey<?> myPrivate = (BouncyPrivateKey<?>) getPrivateKey(getServerKeyPair());

            /* Derive the secret */
            theAgreement.init(myPrivate.getPrivateKey());
            final byte[] mySecret = new byte[theAgreement.getAgreementSize()];
            theAgreement.calculateAgreement(myPublic.getPublicKey(), mySecret, 0);

            /* Store secret */
            storeSecret(mySecret);
        }

        @Override
        public void processServerHello() throws GordianException {
            /* Access keys */
            final BouncyPublicKey<?> myPublic = (BouncyPublicKey<?>) getPublicKey(getServerKeyPair());
            final BouncyPrivateKey<?> myPrivate = (BouncyPrivateKey<?>) getPrivateKey(getClientKeyPair());

            /* Derive the secret */
            theAgreement.init(myPrivate.getPrivateKey());
            final byte[] mySecret = new byte[theAgreement.getAgreementSize()];
            theAgreement.calculateAgreement(myPublic.getPublicKey(), mySecret, 0);

            /* Store secret */
            storeSecret(mySecret);
        }
    }

    /**
     * XDH Unified Agreement Engine.
     */
    public static class BouncyXDHUnifiedAgreementEngine
            extends BouncyAgreementBase {
        /**
         * The agreement.
         */
        private final XDHUnifiedAgreement theAgreement;

        /**
         * Constructor.
         *
         * @param pFactory the security factory
         * @param pSpec    the agreementSpec
         * @throws GordianException on error
         */
        BouncyXDHUnifiedAgreementEngine(final GordianCoreAgreementFactory pFactory,
                                        final GordianAgreementSpec pSpec) throws GordianException {
            /* Initialize underlying class */
            super(pFactory, pSpec);

            /* Create the agreement */
            theAgreement = new XDHUnifiedAgreement(establishAgreement(pSpec.getKeyPairSpec()));
        }

        @Override
        public void processClientHello() throws GordianException {
            /* Access keys */
            final BouncyPublicKey<?> myClientPublic = (BouncyPublicKey<?>) getPublicKey(getClientKeyPair());
            final BouncyPublicKey<?> myClientEphPublic = (BouncyPublicKey<?>) getPublicKey(getClientEphemeral());
            final BouncyPrivateKey<?> myPrivate = (BouncyPrivateKey<?>) getPrivateKey(getServerKeyPair());
            final BouncyPublicKey<?> myEphPublic = (BouncyPublicKey<?>) getPublicKey(getServerEphemeral());
            final BouncyPrivateKey<?> myEphPrivate = (BouncyPrivateKey<?>) getPrivateKey(getServerEphemeral());

            /* Derive the secret */
            final XDHUPrivateParameters myPrivParams
                    = new XDHUPrivateParameters(myPrivate.getPrivateKey(), myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
            theAgreement.init(myPrivParams);
            final byte[] mySecret = new byte[theAgreement.getAgreementSize()];
            final XDHUPublicParameters myPubParams
                    = new XDHUPublicParameters(myClientPublic.getPublicKey(), myClientEphPublic.getPublicKey());
            theAgreement.calculateAgreement(myPubParams, mySecret, 0);

            /* Store secret */
            storeSecret(mySecret);
        }

        @Override
        public void processServerHello() throws GordianException {
            /* Access keys */
            final BouncyPublicKey<?> myServerPublic = (BouncyPublicKey<?>) getPublicKey(getServerKeyPair());
            final BouncyPublicKey<?> myServerEphPublic = (BouncyPublicKey<?>) getPublicKey(getServerEphemeral());
            final BouncyPrivateKey<?> myPrivate = (BouncyPrivateKey<?>) getPrivateKey(getClientKeyPair());
            final BouncyPublicKey<?> myEphPublic = (BouncyPublicKey<?>) getPublicKey(getClientEphemeral());
            final BouncyPrivateKey<?> myEphPrivate = (BouncyPrivateKey<?>) getPrivateKey(getClientEphemeral());

            /* Derive the secret */
            final XDHUPrivateParameters myPrivParams
                    = new XDHUPrivateParameters(myPrivate.getPrivateKey(), myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
            theAgreement.init(myPrivParams);
            final byte[] mySecret = new byte[theAgreement.getAgreementSize()];
            final XDHUPublicParameters myPubParams
                    = new XDHUPublicParameters(myServerPublic.getPublicKey(), myServerEphPublic.getPublicKey());
            theAgreement.calculateAgreement(myPubParams, mySecret, 0);

            /* Store secret */
            storeSecret(mySecret);
        }
    }
}
