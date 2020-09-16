/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreBasicAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreEphemeralAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreSignedAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
         * @param pKeySpec the keySpec
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
         * @param pKeySpec the keySpec
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
         * @param pKeySpec the keySpec
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
         * @param pKeySpec the keySpec
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
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws OceanusException on error
         */
        BouncyX25519KeyPairGenerator(final BouncyFactory pFactory,
                                     final GordianKeyPairSpec pKeySpec) throws OceanusException {
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
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyX25519PublicKey myPublic = new BouncyX25519PublicKey(getKeySpec(), (X25519PublicKeyParameters) myPair.getPublic());
            final BouncyX25519PrivateKey myPrivate = new BouncyX25519PrivateKey(getKeySpec(), (X25519PrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
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
                                           final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            try {
                checkKeySpec(pPrivateKey);
                final BouncyX25519PublicKey myPublic = derivePublicKey(pPublicKey);
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final X25519PrivateKeyParameters myParms = (X25519PrivateKeyParameters) PrivateKeyFactory.createKey(myInfo);
                final BouncyX25519PrivateKey myPrivate = new BouncyX25519PrivateKey(getKeySpec(), myParms);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
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
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            final BouncyX25519PublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws OceanusException on error
         */
        private BouncyX25519PublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            try {
                checkKeySpec(pEncodedKey);
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
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws OceanusException on error
         */
        BouncyX448KeyPairGenerator(final BouncyFactory pFactory,
                                   final GordianKeyPairSpec pKeySpec) throws OceanusException {
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
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyX448PublicKey myPublic = new BouncyX448PublicKey(getKeySpec(), (X448PublicKeyParameters) myPair.getPublic());
            final BouncyX448PrivateKey myPrivate = new BouncyX448PrivateKey(getKeySpec(), (X448PrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
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
                                           final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            try {
                checkKeySpec(pPrivateKey);
                final BouncyX448PublicKey myPublic = derivePublicKey(pPublicKey);
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final X448PrivateKeyParameters myParms = (X448PrivateKeyParameters) PrivateKeyFactory.createKey(myInfo);
                final BouncyX448PrivateKey myPrivate = new BouncyX448PrivateKey(getKeySpec(), myParms);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
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
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            final BouncyX448PublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws OceanusException on error
         */
        private BouncyX448PublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            try {
                checkKeySpec(pEncodedKey);
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
     * @param pKeyPair the keyPair
     * @return the agreement
     */
    private static RawAgreement establishAgreement(final GordianKeyPair pKeyPair) {
        return pKeyPair.getKeyPairSpec().getEdwardsElliptic().is25519()
                       ? new X25519Agreement()
                       : new X448Agreement();
    }

    /**
     * XDH Anonymous.
     */
    public static class BouncyXDHAnonymousAgreement
            extends GordianCoreAnonymousAgreement {
        /**
         * The agreement.
         */
        private RawAgreement theAgreement;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         */
        BouncyXDHAnonymousAgreement(final BouncyFactory pFactory,
                                    final GordianKeyPairAgreementSpec pSpec) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Enable derivation */
            enableDerivation();
        }

        @Override
        public byte[] createClientHello(final GordianKeyPair pServer) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pServer);

            /* Establish agreement */
            theAgreement = establishAgreement(pServer);

            /* Create an ephemeral keyPair */
            final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
            final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(pServer.getKeyPairSpec());
            final GordianKeyPair myPair = myGenerator.generateKeyPair();
            final BouncyPrivateKey<?> myPrivate = (BouncyPrivateKey<?>) getPrivateKey(myPair);

            /* Create the request  */
            final X509EncodedKeySpec myKeySpec = myGenerator.getX509Encoding(myPair);
            final byte[] myKeyBytes = myKeySpec.getEncoded();
            final byte[] myClientHello = buildClientHello(myKeyBytes);

            /* Derive the secret */
            theAgreement.init(myPrivate.getPrivateKey());
            final BouncyPublicKey<?> myTarget = (BouncyPublicKey<?>) getPublicKey(pServer);
            final byte[] mySecret = new byte[theAgreement.getAgreementSize()];
            theAgreement.calculateAgreement(myTarget.getPublicKey(), mySecret, 0);
            storeSecret(mySecret);
            return myClientHello;
        }

        @Override
        public void acceptClientHello(final GordianKeyPair pServer,
                                      final byte[] pClientHello) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pServer);

            /* Establish agreement */
            theAgreement = establishAgreement(pServer);

            /* Parse request */
            final byte[] myKeyBytes = parseClientHello(pClientHello);
            final BouncyPrivateKey<?> myPrivate = (BouncyPrivateKey<?>) getPrivateKey(pServer);
            final X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(myKeyBytes);
            final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
            final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(pServer.getKeyPairSpec());

            /* Derive partner key */
            final GordianKeyPair myPartner = myGenerator.derivePublicOnlyKeyPair(myKeySpec);
            final BouncyPublicKey<?> myPublic = (BouncyPublicKey<?>) getPublicKey(myPartner);

            /* Derive the secret */
            theAgreement.init(myPrivate.getPrivateKey());
            final byte[] mySecret = new byte[theAgreement.getAgreementSize()];
            theAgreement.calculateAgreement(myPublic.getPublicKey(), mySecret, 0);
            storeSecret(mySecret);
        }
    }

    /**
     * XDH Basic Agreement.
     */
    public static class BouncyXDHBasicAgreement
            extends GordianCoreBasicAgreement {
        /**
         * Agreement.
         */
        private RawAgreement theAgreement;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         */
        BouncyXDHBasicAgreement(final BouncyFactory pFactory,
                                final GordianKeyPairAgreementSpec pSpec) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the agreement */
            enableDerivation();
        }

        @Override
        public byte[] acceptClientHello(final GordianKeyPair pClient,
                                        final GordianKeyPair pServer,
                                        final byte[] pClientHello) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pClient);
            checkKeyPair(pServer);

            /* Establish agreement */
            theAgreement = establishAgreement(pServer);

            /* Process clientHello */
            processClientHello(pServer, pClientHello);
            final BouncyPrivateKey<?> myPrivate = (BouncyPrivateKey<?>) getPrivateKey(pServer);
            final BouncyPublicKey<?> myPublic = (BouncyPublicKey<?>) getPublicKey(pClient);

            /* Derive the secret */
            theAgreement.init(myPrivate.getPrivateKey());
            final byte[] mySecret = new byte[theAgreement.getAgreementSize()];
            theAgreement.calculateAgreement(myPublic.getPublicKey(), mySecret, 0);
            storeSecret(mySecret);

            /* Return the serverHello */
            return buildServerHello();
        }

        @Override
        public byte[] acceptServerHello(final GordianKeyPair pServer,
                                        final byte[] pServerHello) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pServer);

            /* Establish agreement */
            theAgreement = establishAgreement(pServer);

            /* process the serverHello */
            processServerHello(pServerHello);
            final BouncyPrivateKey<?> myPrivate = (BouncyPrivateKey<?>) getPrivateKey(getClientKeyPair());

            /* Calculate agreement */
            theAgreement.init(myPrivate.getPrivateKey());
            final BouncyPublicKey<?> mySrcPublic = (BouncyPublicKey<?>) getPublicKey(pServer);
            final byte[] mySecret = new byte[theAgreement.getAgreementSize()];
            theAgreement.calculateAgreement(mySrcPublic.getPublicKey(), mySecret, 0);
            storeSecret(mySecret);

            /* Return confirmation if needed */
            return buildClientConfirm();
        }
    }

    /**
     * XDH Signed Agreement.
     */
    public static class BouncyXDHSignedAgreement
            extends GordianCoreSignedAgreement {
        /**
         * Agreement.
         */
        private RawAgreement theAgreement;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         */
        BouncyXDHSignedAgreement(final BouncyFactory pFactory,
                                final GordianKeyPairAgreementSpec pSpec) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the agreement */
            enableDerivation();
        }

        @Override
        public byte[] acceptClientHello(final GordianKeyPair pServer,
                                        final byte[] pClientHello) throws OceanusException {
            /* Process clientHello */
            processClientHello(pClientHello);
            final BouncyPrivateKey<?> myPrivate = (BouncyPrivateKey<?>) getPrivateKey(getServerEphemeralKeyPair());
            final BouncyPublicKey<?> myPublic = (BouncyPublicKey<?>) getPublicKey(getClientEphemeralKeyPair());

            /* Establish agreement */
            theAgreement = establishAgreement(getServerEphemeralKeyPair());

            /* Derive the secret */
            theAgreement.init(myPrivate.getPrivateKey());
            final byte[] mySecret = new byte[theAgreement.getAgreementSize()];
            theAgreement.calculateAgreement(myPublic.getPublicKey(), mySecret, 0);
            storeSecret(mySecret);

            /* Return the serverHello */
            return buildServerHello(pServer);
        }

        @Override
        public void acceptServerHello(final GordianKeyPair pServer,
                                      final byte[] pServerHello) throws OceanusException {
            /* process the serverHello */
            processServerHello(pServer, pServerHello);
            final BouncyPrivateKey<?> myPrivate = (BouncyPrivateKey<?>) getPrivateKey(getClientEphemeralKeyPair());
            final BouncyPublicKey<?> mySrcPublic = (BouncyPublicKey<?>) getPublicKey(getServerEphemeralKeyPair());

            /* Establish agreement */
            theAgreement = establishAgreement(getServerEphemeralKeyPair());

            /* Calculate agreement */
            theAgreement.init(myPrivate.getPrivateKey());
            final byte[] mySecret = new byte[theAgreement.getAgreementSize()];
            theAgreement.calculateAgreement(mySrcPublic.getPublicKey(), mySecret, 0);
            storeSecret(mySecret);
        }
    }

    /**
     * XDH Unified Agreement.
     */
    public static class BouncyXDHUnifiedAgreement
            extends GordianCoreEphemeralAgreement {
        /**
         * Agreement.
         */
        private XDHUnifiedAgreement theAgreement;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         */
        BouncyXDHUnifiedAgreement(final BouncyFactory pFactory,
                                  final GordianKeyPairAgreementSpec pSpec) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Add in the derivation function */
            enableDerivation();
        }

        @Override
        public byte[] acceptClientHello(final GordianKeyPair pClient,
                                        final GordianKeyPair pServer,
                                        final byte[] pClientHello) throws OceanusException {
            /* Establish agreement */
            theAgreement = new XDHUnifiedAgreement(establishAgreement(pServer));

            /* process clientHello */
            processClientHello(pClient, pServer, pClientHello);

            /* Initialise agreement */
            final BouncyPrivateKey<?> myPrivate = (BouncyPrivateKey<?>) getPrivateKey(pServer);
            final BouncyPrivateKey<?> myEphPrivate = (BouncyPrivateKey<?>) getPrivateKey(getServerEphemeralKeyPair());
            final BouncyPublicKey<?> myEphPublic = (BouncyPublicKey<?>) getPublicKey(getServerEphemeralKeyPair());
            final XDHUPrivateParameters myPrivParams = new XDHUPrivateParameters(myPrivate.getPrivateKey(),
                    myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
            theAgreement.init(myPrivParams);

            /* Calculate agreement */
            final BouncyPublicKey<?> mySrcPublic = (BouncyPublicKey<?>) getPublicKey(pClient);
            final BouncyPublicKey<?> mySrcEphPublic = (BouncyPublicKey<?>) getPublicKey(getClientEphemeralKeyPair());
            final XDHUPublicParameters myPubParams = new XDHUPublicParameters(mySrcPublic.getPublicKey(),
                    mySrcEphPublic.getPublicKey());
            final byte[] mySecret = new byte[theAgreement.getAgreementSize()];
            theAgreement.calculateAgreement(myPubParams, mySecret, 0);
            storeSecret(mySecret);

            /* Return the serverHello */
            return buildServerHello();
        }

        @Override
        public byte[] acceptServerHello(final GordianKeyPair pServer,
                                        final byte[] pServerHello) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pServer);

            /* Establish agreement */
            theAgreement = new XDHUnifiedAgreement(establishAgreement(pServer));

            /* process the serverHello */
            processServerHello(pServer, pServerHello);

            /* Initialise agreement */
            final BouncyPrivateKey<?> myPrivate = (BouncyPrivateKey<?>) getPrivateKey(getClientKeyPair());
            final BouncyPrivateKey<?> myEphPrivate = (BouncyPrivateKey<?>) getPrivateKey(getClientEphemeralKeyPair());
            final BouncyPublicKey<?> myEphPublic = (BouncyPublicKey<?>) getPublicKey(getClientEphemeralKeyPair());
            final XDHUPrivateParameters myPrivParams = new XDHUPrivateParameters(myPrivate.getPrivateKey(),
                    myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
            theAgreement.init(myPrivParams);

            /* Calculate agreement */
            final BouncyPublicKey<?> mySrcPublic = (BouncyPublicKey<?>) getPublicKey(pServer);
            final BouncyPublicKey<?> mySrcEphPublic = (BouncyPublicKey<?>) getPublicKey(getServerEphemeralKeyPair());
            final XDHUPublicParameters myPubParams = new XDHUPublicParameters(mySrcPublic.getPublicKey(),
                    mySrcEphPublic.getPublicKey());
            final byte[] mySecret = new byte[theAgreement.getAgreementSize()];
            theAgreement.calculateAgreement(myPubParams, mySecret, 0);
            storeSecret(mySecret);

            /* Return confirmation if needed */
            return buildClientConfirm();
        }
    }
}
