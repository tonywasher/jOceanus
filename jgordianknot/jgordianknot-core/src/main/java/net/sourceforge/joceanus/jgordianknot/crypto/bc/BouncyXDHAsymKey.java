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

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreement.GordianBasicAgreement;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreement.GordianEphemeralAgreement;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * EdwardsCurve XDH AsymKey classes.
 */
public final class BouncyXDHAsymKey {
    /**
     * Private constructor.
     */
    private BouncyXDHAsymKey() {
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
        BouncyX25519PublicKey(final GordianAsymKeySpec pKeySpec,
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
        BouncyX25519PrivateKey(final GordianAsymKeySpec pKeySpec,
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
        BouncyX448PublicKey(final GordianAsymKeySpec pKeySpec,
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
        BouncyX448PrivateKey(final GordianAsymKeySpec pKeySpec,
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
                                     final GordianAsymKeySpec pKeySpec) throws OceanusException {
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
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final X25519PrivateKeyParameters myParms = (X25519PrivateKeyParameters) PrivateKeyFactory.createKey(myInfo);
                final BouncyX25519PrivateKey myPrivate = new BouncyX25519PrivateKey(getKeySpec(), myParms);
                final BouncyX25519PublicKey myPublic = derivePublicKey(pPublicKey);
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
                                   final GordianAsymKeySpec pKeySpec) throws OceanusException {
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
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final X448PrivateKeyParameters myParms = (X448PrivateKeyParameters) PrivateKeyFactory.createKey(myInfo);
                final BouncyX448PrivateKey myPrivate = new BouncyX448PrivateKey(getKeySpec(), myParms);
                final BouncyX448PublicKey myPublic = derivePublicKey(pPublicKey);
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
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final X448PublicKeyParameters myParms = (X448PublicKeyParameters) PublicKeyFactory.createKey(myInfo);
                return new BouncyX448PublicKey(getKeySpec(), myParms);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * XDH Basic Agreement.
     */
    public static class BouncyXDHBasicAgreement
            extends GordianBasicAgreement {
        /**
         * Agreement.
         */
        private final RawAgreement theAgreement;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         */
        BouncyXDHBasicAgreement(final BouncyFactory pFactory,
                                final GordianAgreementSpec pSpec) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the agreement */
            theAgreement = GordianAsymKeyType.X25519.equals(pSpec.getAsymKeyType())
                                  ? new X25519Agreement()
                                  : new X448Agreement();
            enableDerivation();
        }

        @Override
        public byte[] initiateAgreement(final GordianKeyPair pSource,
                                        final GordianKeyPair pTarget) throws OceanusException {
            /* Check keyPairs */
            checkKeyPair(pSource);
            checkKeyPair(pTarget);

            /* Derive the secret */
            final BouncyPrivateKey myPrivate = (BouncyPrivateKey) getPrivateKey(pSource);
            theAgreement.init(myPrivate.getPrivateKey());
            final BouncyPublicKey myTarget = (BouncyPublicKey) getPublicKey(pTarget);
            final byte[] mySecret = new byte[theAgreement.getAgreementSize()];
            theAgreement.calculateAgreement(myTarget.getPublicKey(), mySecret, 0);
            storeSecret(mySecret);

            /* Create the message  */
            return createMessage();
        }

        @Override
        public void acceptAgreement(final GordianKeyPair pSource,
                                    final GordianKeyPair pSelf,
                                    final byte[] pMessage) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pSource);
            checkKeyPair(pSelf);

            /* Determine initVector */
            parseMessage(pMessage);
            final BouncyPrivateKey myPrivate = (BouncyPrivateKey) getPrivateKey(pSelf);
            final BouncyPublicKey myPublic = (BouncyPublicKey) getPublicKey(pSource);

            /* Derive the secret */
            theAgreement.init(myPrivate.getPrivateKey());
            final byte[] mySecret = new byte[theAgreement.getAgreementSize()];
            theAgreement.calculateAgreement(myPublic.getPublicKey(), mySecret, 0);
            storeSecret(mySecret);
        }
    }

    /**
     * XDH Unified Agreement.
     */
    public static class BouncyXDHUnifiedAgreement
            extends GordianEphemeralAgreement {
        /**
         * Agreement.
         */
        private final XDHUnifiedAgreement theAgreement;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         */
        BouncyXDHUnifiedAgreement(final BouncyFactory pFactory,
                                  final GordianAgreementSpec pSpec) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the agreement */
            final RawAgreement myAgreement = GordianAsymKeyType.X25519.equals(pSpec.getAsymKeyType())
                    ? new X25519Agreement()
                    : new X448Agreement();
            theAgreement = new XDHUnifiedAgreement(myAgreement);
            enableDerivation();
        }

        @Override
        public byte[] acceptAgreement(final GordianKeyPair pSource,
                                      final GordianKeyPair pResponder,
                                      final byte[] pMessage) throws OceanusException {
            /* process message */
            final byte[] myResponse = parseMessage(pResponder, pMessage);

            /* Initialise agreement */
            final BouncyPrivateKey myPrivate = (BouncyPrivateKey) getPrivateKey(pResponder);
            final BouncyPrivateKey myEphPrivate = (BouncyPrivateKey) getPrivateKey(getEphemeralKeyPair());
            final BouncyPublicKey myEphPublic = (BouncyPublicKey) getPublicKey(getEphemeralKeyPair());
            final XDHUPrivateParameters myPrivParams = new XDHUPrivateParameters(myPrivate.getPrivateKey(),
                    myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
            theAgreement.init(myPrivParams);

            /* Calculate agreement */
            final BouncyPublicKey mySrcPublic = (BouncyPublicKey) getPublicKey(pSource);
            final BouncyPublicKey mySrcEphPublic = (BouncyPublicKey) getPublicKey(getPartnerEphemeralKeyPair());
            final XDHUPublicParameters myPubParams = new XDHUPublicParameters(mySrcPublic.getPublicKey(),
                    mySrcEphPublic.getPublicKey());
            final byte[] mySecret = new byte[theAgreement.getAgreementSize()];
            theAgreement.calculateAgreement(myPubParams, mySecret, 0);
            storeSecret(mySecret);

            /* Return the response */
            return myResponse;
        }

        @Override
        public void confirmAgreement(final GordianKeyPair pResponder,
                                     final byte[] pMessage) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pResponder);

            /* parse the ephemeral message */
            parseEphemeral(pMessage);

            /* Initialise agreement */
            final BouncyPrivateKey myPrivate = (BouncyPrivateKey) getPrivateKey(getOwnerKeyPair());
            final BouncyPrivateKey myEphPrivate = (BouncyPrivateKey) getPrivateKey(getEphemeralKeyPair());
            final BouncyPublicKey myEphPublic = (BouncyPublicKey) getPublicKey(getEphemeralKeyPair());
            final XDHUPrivateParameters myPrivParams = new XDHUPrivateParameters(myPrivate.getPrivateKey(),
                    myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
            theAgreement.init(myPrivParams);

            /* Calculate agreement */
            final BouncyPublicKey mySrcPublic = (BouncyPublicKey) getPublicKey(pResponder);
            final BouncyPublicKey mySrcEphPublic = (BouncyPublicKey) getPublicKey(getPartnerEphemeralKeyPair());
            final XDHUPublicParameters myPubParams = new XDHUPublicParameters(mySrcPublic.getPublicKey(),
                    mySrcEphPublic.getPublicKey());
            final byte[] mySecret = new byte[theAgreement.getAgreementSize()];
            theAgreement.calculateAgreement(myPubParams, mySecret, 0);
            storeSecret(mySecret);
        }
    }
}
