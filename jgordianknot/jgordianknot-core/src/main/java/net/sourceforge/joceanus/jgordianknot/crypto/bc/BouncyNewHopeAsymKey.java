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

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.newutils.PqcPrivateKeyFactory;
import org.bouncycastle.crypto.newutils.PqcPrivateKeyInfoFactory;
import org.bouncycastle.crypto.newutils.PqcPublicKeyFactory;
import org.bouncycastle.crypto.newutils.PqcSubjectPublicKeyInfoFactory;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.ExchangePair;
import org.bouncycastle.pqc.crypto.newhope.NHAgreement;
import org.bouncycastle.pqc.crypto.newhope.NHExchangePairGenerator;
import org.bouncycastle.pqc.crypto.newhope.NHKeyPairGenerator;
import org.bouncycastle.pqc.crypto.newhope.NHPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreement.GordianEncapsulationAgreement;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianRSAModulus;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.jtethys.OceanusException;

import static net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPairGenerator.ERROR_PARSE;

/**
 * NewHope AsymKey classes.
 */
public final class BouncyNewHopeAsymKey {
    /**
     * Private constructor.
     */
    private BouncyNewHopeAsymKey() {
    }

    /**
     * Bouncy NewHope PublicKey.
     */
    public static class BouncyNewHopePublicKey
            extends BouncyPublicKey<NHPublicKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        BouncyNewHopePublicKey(final GordianAsymKeySpec pKeySpec,
                               final NHPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final NHPublicKeyParameters myThis = getPublicKey();
            final NHPublicKeyParameters myThat = (NHPublicKeyParameters) pThat;

            /* Check equality */
            return Arrays.equals(myThis.getPubData(), myThat.getPubData());
        }
    }

    /**
     * Bouncy NewHope PrivateKey.
     */
    public static class BouncyNewHopePrivateKey
            extends BouncyPrivateKey<NHPrivateKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        BouncyNewHopePrivateKey(final GordianAsymKeySpec pKeySpec,
                                final NHPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final NHPrivateKeyParameters myThis = getPrivateKey();
            final NHPrivateKeyParameters myThat = (NHPrivateKeyParameters) pThat;

            /* Check equality */
            return Arrays.equals(myThis.getSecData(), myThat.getSecData());
        }
    }

    /**
     * BouncyCastle NewHope KeyPair generator.
     */
    public static class BouncyNewHopeKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final NHKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyNewHopeKeyPairGenerator(final BouncyFactory pFactory,
                                      final GordianAsymKeySpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = new NHKeyPairGenerator();
            final KeyGenerationParameters myParams = new KeyGenerationParameters(getRandom(), GordianRSAModulus.MOD1024.getLength());
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyNewHopePublicKey myPublic = new BouncyNewHopePublicKey(getKeySpec(), (NHPublicKeyParameters) myPair.getPublic());
            final BouncyNewHopePrivateKey myPrivate = new BouncyNewHopePrivateKey(getKeySpec(), (NHPrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
                final BouncyNewHopePrivateKey myPrivateKey = (BouncyNewHopePrivateKey) getPrivateKey(pKeyPair);
                final NHPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
                final PrivateKeyInfo myInfo = PqcPrivateKeyInfoFactory.createPrivateKeyInfo(myParms, null);
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
                final NHPrivateKeyParameters myParms = (NHPrivateKeyParameters) PqcPrivateKeyFactory.createKey(myInfo);
                final BouncyNewHopePrivateKey myPrivate = new BouncyNewHopePrivateKey(getKeySpec(), myParms);
                final BouncyNewHopePublicKey myPublic = derivePublicKey(pPublicKey);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
                final BouncyNewHopePublicKey myPublicKey = (BouncyNewHopePublicKey) getPublicKey(pKeyPair);
                final NHPublicKeyParameters myParms = myPublicKey.getPublicKey();
                final SubjectPublicKeyInfo myInfo = PqcSubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(myParms);
                return new X509EncodedKeySpec(myInfo.getEncoded());
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            final BouncyNewHopePublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws OceanusException on error
         */
        private BouncyNewHopePublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            try {
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final NHPublicKeyParameters myParms = (NHPublicKeyParameters) PqcPublicKeyFactory.createKey(myInfo);
                return new BouncyNewHopePublicKey(getKeySpec(), myParms);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * NewHope Encapsulation.
     */
    public static class BouncyNewHopeAgreement
            extends GordianEncapsulationAgreement {
        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         */
        BouncyNewHopeAgreement(final BouncyFactory pFactory,
                               final GordianAgreementSpec pSpec) {
            /* Initialise underlying class */
            super(pFactory, pSpec);
        }

        @Override
        public byte[] initiateAgreement(final GordianKeyPair pTarget) throws OceanusException {
            try {
                /* Check keyPair */
                checkKeyPair(pTarget);

                /* Generate an Exchange KeyPair */
                final NHExchangePairGenerator myGenerator = new NHExchangePairGenerator(getRandom());
                final BouncyNewHopePublicKey myTarget = (BouncyNewHopePublicKey) getPublicKey(pTarget);
                final ExchangePair myPair = myGenerator.GenerateExchange(myTarget.getPublicKey());

                /* Derive the secret */
                final byte[] mySecret = myPair.getSharedValue();
                storeSecret(mySecret);

                /* Obtain the encoded keySpec of the public key */
                final NHPublicKeyParameters myParms = (NHPublicKeyParameters) myPair.getPublicKey();
                final SubjectPublicKeyInfo myInfo = PqcSubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(myParms);
                final X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(myInfo.getEncoded());
                final byte[] myKeySpecBytes = myKeySpec.getEncoded();

                /* Create the message  */
                return createMessage(myKeySpecBytes);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public void acceptAgreement(final GordianKeyPair pSelf,
                                    final byte[] pMessage) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pSelf);

            /* Obtain keySpec */
            final byte[] myX509bytes = parseMessage(pMessage);
            final X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(myX509bytes);

            /* Derive ephemeral Public key */
            final GordianKeyPairGenerator myGenerator = getFactory().getKeyPairGenerator(pSelf.getKeySpec());
            final GordianKeyPair myPair = myGenerator.derivePublicOnlyKeyPair(myKeySpec);
            final BouncyNewHopePublicKey myPublic = (BouncyNewHopePublicKey) getPublicKey(myPair);

            /* Derive the secret */
            final BouncyNewHopePrivateKey myPrivate = (BouncyNewHopePrivateKey) getPrivateKey(pSelf);
            final NHAgreement myAgreement = new NHAgreement();
            myAgreement.init(myPrivate.getPrivateKey());
            final byte[] mySecret = myAgreement.calculateAgreement(myPublic.getPublicKey());

            /* Store secret */
            storeSecret(mySecret);
        }
    }
}
