/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
import java.math.BigInteger;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.pkcs.DHParameter;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.DomainParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.agreement.DHBasicAgreement;
import org.bouncycastle.crypto.agreement.DHUnifiedAgreement;
import org.bouncycastle.crypto.agreement.MQVBasicAgreement;
import org.bouncycastle.crypto.generators.DHKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DHKeyGenerationParameters;
import org.bouncycastle.crypto.params.DHMQVPrivateParameters;
import org.bouncycastle.crypto.params.DHMQVPublicParameters;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.crypto.params.DHUPrivateParameters;
import org.bouncycastle.crypto.params.DHUPublicParameters;
import org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.util.BigIntegers;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianDHGroup;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianAsymFactory;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.jgordianknot.impl.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreBasicAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreEphemeralAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianAsymAlgId.GordianDHEncodedParser;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * DiffieHellman AsymKey classes.
 */
public final class BouncyDHAsymKey {
    /**
     * Private constructor.
     */
    private BouncyDHAsymKey() {
    }

    /**
     * Bouncy DH PublicKey.
     */
    public static class BouncyDHPublicKey
            extends BouncyPublicKey<DHPublicKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        BouncyDHPublicKey(final GordianAsymKeySpec pKeySpec,
                          final DHPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final DHPublicKeyParameters myThis = getPublicKey();
            final DHPublicKeyParameters myThat = (DHPublicKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
        }

        /**
         * Is the private key valid for this public key?
         * @param pPrivate the private key
         * @return true/false
         */
        public boolean validPrivate(final BouncyDHPrivateKey pPrivate) {
            final DHPrivateKeyParameters myPrivate = pPrivate.getPrivateKey();
            return getPublicKey().getParameters().equals(myPrivate.getParameters());
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final DHPublicKeyParameters pFirst,
                                           final DHPublicKeyParameters pSecond) {
            return pFirst.getY().equals(pSecond.getY())
                    && pFirst.getParameters().equals(pSecond.getParameters());
        }
    }

    /**
     * Bouncy DH PrivateKey.
     */
    public static class BouncyDHPrivateKey
            extends BouncyPrivateKey<DHPrivateKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        BouncyDHPrivateKey(final GordianAsymKeySpec pKeySpec,
                           final DHPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final DHPrivateKeyParameters myThis = getPrivateKey();
            final DHPrivateKeyParameters myThat = (DHPrivateKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final DHPrivateKeyParameters pFirst,
                                           final DHPrivateKeyParameters pSecond) {
            return pFirst.getX().equals(pSecond.getX())
                    && pFirst.getParameters().equals(pSecond.getParameters());
        }
    }

    /**
     * BouncyCastle DH KeyPair generator.
     */
    public static class BouncyDHKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final DHKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyDHKeyPairGenerator(final BouncyFactory pFactory,
                                 final GordianAsymKeySpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create the parameter generator */
            final GordianDHGroup myGroup = pKeySpec.getDHGroup();
            final DHParameters myParms = myGroup.getParameters();
            final DHKeyGenerationParameters myParams = new DHKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialise the generator */
            theGenerator = new DHKeyPairGenerator();
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyDHPublicKey myPublic = new BouncyDHPublicKey(getKeySpec(), (DHPublicKeyParameters) myPair.getPublic());
            final BouncyDHPrivateKey myPrivate = new BouncyDHPrivateKey(getKeySpec(), (DHPrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws GordianCryptoException {
            try {
                final BouncyDHPrivateKey myPrivateKey = (BouncyDHPrivateKey) getPrivateKey(pKeyPair);
                final DHPrivateKeyParameters myKey = myPrivateKey.getPrivateKey();
                final DHParameters myParms = myKey.getParameters();
                final AlgorithmIdentifier myId = getAlgorithmIdentifier(myParms);
                final PrivateKeyInfo myInfo = new PrivateKeyInfo(myId, new ASN1Integer(myKey.getX()));
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
                final BouncyDHPublicKey myPublic = derivePublicKey(pPublicKey);
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final BCDHPrivateKey myKey = new BCDHPrivateKey(myInfo);
                final DHParameters myParms = GordianDHEncodedParser.determineParameters(myInfo.getPrivateKeyAlgorithm());
                final BouncyDHPrivateKey myPrivate = new BouncyDHPrivateKey(getKeySpec(), new DHPrivateKeyParameters(myKey.getX(), myParms));
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) {
            final BouncyDHPublicKey myPublicKey = (BouncyDHPublicKey) getPublicKey(pKeyPair);
            final DHPublicKeyParameters myKey = myPublicKey.getPublicKey();
            final DHParameters myParms = myKey.getParameters();
            final AlgorithmIdentifier myId = getAlgorithmIdentifier(myParms);
            final byte[] myBytes = KeyUtil.getEncodedSubjectPublicKeyInfo(myId, new ASN1Integer(myKey.getY()));
            return new X509EncodedKeySpec(myBytes);
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            final BouncyDHPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws OceanusException on error
         */
        private BouncyDHPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            checkKeySpec(pEncodedKey);
            final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
            final BCDHPublicKey myKey = new BCDHPublicKey(myInfo);
            return new BouncyDHPublicKey(getKeySpec(), myKey.engineGetKeyParameters());
        }

        /**
         * Obtain algorithm Id from DHParameters.
         * @param pParams the parameters
         * @return the algorithmId.
         */
        private static AlgorithmIdentifier getAlgorithmIdentifier(final DHParameters pParams) {
            /* If this is a public # algorithm */
            return pParams.getQ() != null
                   ? new AlgorithmIdentifier(X9ObjectIdentifiers.dhpublicnumber,
                    new DomainParameters(pParams.getP(), pParams.getG(), pParams.getQ(), pParams.getJ(), null).toASN1Primitive())
                   : new AlgorithmIdentifier(PKCSObjectIdentifiers.dhKeyAgreement,
                           new DHParameter(pParams.getP(), pParams.getG(), pParams.getL()).toASN1Primitive());
        }
    }

    /**
     * DH Anonymous.
     */
    public static class BouncyDHAnonymousAgreement
            extends GordianCoreAnonymousAgreement {
        /**
         * The agreement.
         */
        private final DHBasicAgreement theAgreement;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         */
        BouncyDHAnonymousAgreement(final BouncyFactory pFactory,
                                   final GordianAgreementSpec pSpec) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the agreement */
            theAgreement = new DHBasicAgreement();
            enableDerivation();
        }

        @Override
        public byte[] initiateAgreement(final GordianKeyPair pTarget) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pTarget);

            /* Create an ephemeral keyPair */
            final GordianAsymFactory myAsym = getFactory().getAsymmetricFactory();
            final GordianKeyPairGenerator myGenerator = myAsym.getKeyPairGenerator(pTarget.getKeySpec());
            final GordianKeyPair myPair = myGenerator.generateKeyPair();
            final BouncyDHPrivateKey myPrivate = (BouncyDHPrivateKey) getPrivateKey(myPair);

            /* Create the message  */
            final X509EncodedKeySpec myKeySpec = myGenerator.getX509Encoding(myPair);
            final byte[] myKeyBytes = myKeySpec.getEncoded();
            final byte[] myMessage = createMessage(myKeyBytes);

            /* Derive the secret */
            theAgreement.init(myPrivate.getPrivateKey());
            final BouncyDHPublicKey myTarget = (BouncyDHPublicKey) getPublicKey(pTarget);
            final BigInteger mySecret = theAgreement.calculateAgreement(myTarget.getPublicKey());
            storeSecret(BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(), mySecret));
            return myMessage;
        }

        @Override
        public void acceptAgreement(final GordianKeyPair pSelf,
                                    final byte[] pMessage) throws OceanusException {
            /* Check keyPair */
            checkKeyPair(pSelf);

            /* Obtain source keySpec */
            final byte[] myKeyBytes = parseMessage(pMessage);
            final X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(myKeyBytes);
            final GordianAsymFactory myAsym = getFactory().getAsymmetricFactory();
            final GordianKeyPairGenerator myGenerator = myAsym.getKeyPairGenerator(pSelf.getKeySpec());
            final GordianKeyPair myPartner = myGenerator.derivePublicOnlyKeyPair(myKeySpec);
            final BouncyDHPrivateKey myPrivate = (BouncyDHPrivateKey) getPrivateKey(pSelf);
            final BouncyDHPublicKey myPublic = (BouncyDHPublicKey) getPublicKey(myPartner);

            /* Derive the secret */
            theAgreement.init(myPrivate.getPrivateKey());
            final BigInteger mySecret = theAgreement.calculateAgreement(myPublic.getPublicKey());

            /* Store secret */
            storeSecret(BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(), mySecret));
        }
    }

    /**
     * DH Basic Agreement.
     */
    public static class BouncyDHBasicAgreement
            extends GordianCoreBasicAgreement {
        /**
         * The agreement.
         */
        private final DHBasicAgreement theAgreement;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         */
        BouncyDHBasicAgreement(final BouncyFactory pFactory,
                               final GordianAgreementSpec pSpec) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the agreement */
            theAgreement = new DHBasicAgreement();
            enableDerivation();
        }

        @Override
        public byte[] initiateAgreement(final GordianKeyPair pSource,
                                        final GordianKeyPair pTarget) throws OceanusException {
            /* Check keyPairs */
            checkKeyPair(pSource);
            checkKeyPair(pTarget);

            /* Build the init Message */
            final byte[] myMessage = createMessage();

            /* Derive the secret */
            final BouncyDHPrivateKey myPrivate = (BouncyDHPrivateKey) getPrivateKey(pSource);
            theAgreement.init(myPrivate.getPrivateKey());
            final BouncyDHPublicKey myTarget = (BouncyDHPublicKey) getPublicKey(pTarget);
            final BigInteger mySecret = theAgreement.calculateAgreement(myTarget.getPublicKey());
            storeSecret(BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(), mySecret));

            /* Return the message  */
            return myMessage;
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
            final BouncyDHPrivateKey myPrivate = (BouncyDHPrivateKey) getPrivateKey(pSelf);
            final BouncyDHPublicKey myPublic = (BouncyDHPublicKey) getPublicKey(pSource);

            /* Derive the secret */
            theAgreement.init(myPrivate.getPrivateKey());
            final BigInteger mySecret = theAgreement.calculateAgreement(myPublic.getPublicKey());

            /* Store secret */
            storeSecret(BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(), mySecret));
        }
    }

    /**
     * DH Unified Agreement.
     */
    public static class BouncyDHUnifiedAgreement
            extends GordianCoreEphemeralAgreement {
        /**
         * The agreement.
         */
        private final DHUnifiedAgreement theAgreement;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         */
        BouncyDHUnifiedAgreement(final BouncyFactory pFactory,
                                 final GordianAgreementSpec pSpec) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create Key Agreement */
            theAgreement = new DHUnifiedAgreement();
            enableDerivation();
        }

        @Override
        public byte[] acceptAgreement(final GordianKeyPair pSource,
                                      final GordianKeyPair pResponder,
                                      final byte[] pMessage) throws OceanusException {
            /* process message */
            final byte[] myResponse = parseMessage(pResponder, pMessage);

            /* Initialise agreement */
            final BouncyDHPrivateKey myPrivate = (BouncyDHPrivateKey) getPrivateKey(pResponder);
            final BouncyDHPrivateKey myEphPrivate = (BouncyDHPrivateKey) getPrivateKey(getEphemeralKeyPair());
            final BouncyDHPublicKey myEphPublic = (BouncyDHPublicKey) getPublicKey(getEphemeralKeyPair());
            final DHUPrivateParameters myPrivParams = new DHUPrivateParameters(myPrivate.getPrivateKey(),
                    myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
            theAgreement.init(myPrivParams);

            /* Calculate agreement */
            final BouncyDHPublicKey mySrcPublic = (BouncyDHPublicKey) getPublicKey(pSource);
            final BouncyDHPublicKey mySrcEphPublic = (BouncyDHPublicKey) getPublicKey(getPartnerEphemeralKeyPair());
            final DHUPublicParameters myPubParams = new DHUPublicParameters(mySrcPublic.getPublicKey(),
                    mySrcEphPublic.getPublicKey());
            storeSecret(theAgreement.calculateAgreement(myPubParams));

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
            final BouncyDHPrivateKey myPrivate = (BouncyDHPrivateKey) getPrivateKey(getOwnerKeyPair());
            final BouncyDHPrivateKey myEphPrivate = (BouncyDHPrivateKey) getPrivateKey(getEphemeralKeyPair());
            final BouncyDHPublicKey myEphPublic = (BouncyDHPublicKey) getPublicKey(getEphemeralKeyPair());
            final DHUPrivateParameters myPrivParams = new DHUPrivateParameters(myPrivate.getPrivateKey(),
                    myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
            theAgreement.init(myPrivParams);

            /* Calculate agreement */
            final BouncyDHPublicKey mySrcPublic = (BouncyDHPublicKey) getPublicKey(pResponder);
            final BouncyDHPublicKey mySrcEphPublic = (BouncyDHPublicKey) getPublicKey(getPartnerEphemeralKeyPair());
            final DHUPublicParameters myPubParams = new DHUPublicParameters(mySrcPublic.getPublicKey(),
                    mySrcEphPublic.getPublicKey());
            storeSecret(theAgreement.calculateAgreement(myPubParams));
        }
    }

    /**
     * DH MQV Agreement.
     */
    public static class BouncyDHMQVAgreement
            extends GordianCoreEphemeralAgreement {
        /**
         * The agreement.
         */
        private final MQVBasicAgreement theAgreement;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         */
        BouncyDHMQVAgreement(final BouncyFactory pFactory,
                             final GordianAgreementSpec pSpec) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create Key Agreement */
            theAgreement = new MQVBasicAgreement();
            enableDerivation();
        }

        @Override
        public byte[] acceptAgreement(final GordianKeyPair pSource,
                                      final GordianKeyPair pResponder,
                                      final byte[] pMessage) throws OceanusException {
            /* process message */
            final byte[] myResponse = parseMessage(pResponder, pMessage);

            /* Initialise agreement */
            final BouncyDHPrivateKey myPrivate = (BouncyDHPrivateKey) getPrivateKey(pResponder);
            final BouncyDHPrivateKey myEphPrivate = (BouncyDHPrivateKey) getPrivateKey(getEphemeralKeyPair());
            final BouncyDHPublicKey myEphPublic = (BouncyDHPublicKey) getPublicKey(getEphemeralKeyPair());
            final DHMQVPrivateParameters myPrivParams = new DHMQVPrivateParameters(myPrivate.getPrivateKey(),
                    myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
            theAgreement.init(myPrivParams);

            /* Calculate agreement */
            final BouncyDHPublicKey mySrcPublic = (BouncyDHPublicKey) getPublicKey(pSource);
            final BouncyDHPublicKey mySrcEphPublic = (BouncyDHPublicKey) getPublicKey(getPartnerEphemeralKeyPair());
            final DHMQVPublicParameters myPubParams = new DHMQVPublicParameters(mySrcPublic.getPublicKey(),
                    mySrcEphPublic.getPublicKey());
            storeSecret(BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(),
                    theAgreement.calculateAgreement(myPubParams)));

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
            final BouncyDHPrivateKey myPrivate = (BouncyDHPrivateKey) getPrivateKey(getOwnerKeyPair());
            final BouncyDHPrivateKey myEphPrivate = (BouncyDHPrivateKey) getPrivateKey(getEphemeralKeyPair());
            final BouncyDHPublicKey myEphPublic = (BouncyDHPublicKey) getPublicKey(getEphemeralKeyPair());
            final DHMQVPrivateParameters myPrivParams = new DHMQVPrivateParameters(myPrivate.getPrivateKey(),
                    myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
            theAgreement.init(myPrivParams);

            /* Calculate agreement */
            final BouncyDHPublicKey mySrcPublic = (BouncyDHPublicKey) getPublicKey(pResponder);
            final BouncyDHPublicKey mySrcEphPublic = (BouncyDHPublicKey) getPublicKey(getPartnerEphemeralKeyPair());
            final DHMQVPublicParameters myPubParams = new DHMQVPublicParameters(mySrcPublic.getPublicKey(),
                    mySrcEphPublic.getPublicKey());
            storeSecret(BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(),
                    theAgreement.calculateAgreement(myPubParams)));
        }
    }
}
