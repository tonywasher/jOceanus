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
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianDHGroup;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianKeyPairAlgId.GordianDHEncodedParser;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianKeyPairValidity;
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

import java.io.IOException;
import java.math.BigInteger;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * DiffieHellman KeyPair classes.
 */
public final class BouncyDHKeyPair {
    /**
     * Private constructor.
     */
    private BouncyDHKeyPair() {
    }

    /**
     * Bouncy DH PublicKey.
     */
    public static class BouncyDHPublicKey
            extends BouncyPublicKey<DHPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyDHPublicKey(final GordianKeyPairSpec pKeySpec,
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
         *
         * @param pPrivate the private key
         * @return true/false
         */
        public boolean validPrivate(final BouncyDHPrivateKey pPrivate) {
            final DHPrivateKeyParameters myPrivate = pPrivate.getPrivateKey();
            return getPublicKey().getParameters().equals(myPrivate.getParameters());
        }

        /**
         * CompareKeys.
         *
         * @param pFirst  the first key
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
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyDHPrivateKey(final GordianKeyPairSpec pKeySpec,
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
         *
         * @param pFirst  the first key
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
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyDHKeyPairGenerator(final GordianBaseFactory pFactory,
                                 final GordianKeyPairSpec pKeySpec) {
            /* Initialize underlying class */
            super(pFactory, pKeySpec);

            /* Create the parameter generator */
            final GordianDHGroup myGroup = pKeySpec.getDHGroup();
            final DHParameters myParms = myGroup.getParameters();
            final DHKeyGenerationParameters myParams = new DHKeyGenerationParameters(getRandom(), myParms);

            /* Create and initialize the generator */
            theGenerator = new DHKeyPairGenerator();
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyDHPublicKey myPublic = new BouncyDHPublicKey(getKeySpec(), (DHPublicKeyParameters) myPair.getPublic());
            final BouncyDHPrivateKey myPrivate = new BouncyDHPrivateKey(getKeySpec(), (DHPrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Check the keyPair type and keySpecs */
                BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

                /* build and return the encoding */
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
                                           final PKCS8EncodedKeySpec pPrivateKey) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pPrivateKey);

                /* derive keyPair */
                final BouncyDHPublicKey myPublic = derivePublicKey(pPublicKey);
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final BCDHPrivateKey myKey = new BCDHPrivateKey(myInfo);
                final DHParameters myParms = GordianDHEncodedParser.determineParameters(myInfo.getPrivateKeyAlgorithm());
                final BouncyDHPrivateKey myPrivate = new BouncyDHPrivateKey(getKeySpec(), new DHPrivateKeyParameters(myKey.getX(), myParms));
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
            /* Check the keyPair type and keySpecs */
            BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

            /* build and return the encoding */
            final BouncyDHPublicKey myPublicKey = (BouncyDHPublicKey) getPublicKey(pKeyPair);
            final DHPublicKeyParameters myKey = myPublicKey.getPublicKey();
            final DHParameters myParms = myKey.getParameters();
            final AlgorithmIdentifier myId = getAlgorithmIdentifier(myParms);
            final byte[] myBytes = KeyUtil.getEncodedSubjectPublicKeyInfo(myId, new ASN1Integer(myKey.getY()));
            return new X509EncodedKeySpec(myBytes);
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws GordianException {
            final BouncyDHPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         *
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws GordianException on error
         */
        private BouncyDHPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws GordianException {
            /* Check the keySpecs */
            checkKeySpec(pEncodedKey);

            /* derive publicKey */
            final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
            final BCDHPublicKey myKey = new BCDHPublicKey(myInfo);
            return new BouncyDHPublicKey(getKeySpec(), myKey.engineGetKeyParameters());
        }

        /**
         * Obtain algorithm Id from DHParameters.
         *
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
     * DH Anonymous Agreement Engine.
     */
    public static class BouncyDHAnonAgreementEngine
            extends BouncyAgreementBase {
        /**
         * The agreement.
         */
        private final DHBasicAgreement theAgreement;

        /**
         * Constructor.
         *
         * @param pFactory the security factory
         * @param pSpec    the agreementSpec
         * @throws GordianException on error
         */
        BouncyDHAnonAgreementEngine(final GordianCoreAgreementFactory pFactory,
                                    final GordianAgreementSpec pSpec) throws GordianException {
            /* Initialize underlying class */
            super(pFactory, pSpec);

            /* Create the agreement */
            theAgreement = new DHBasicAgreement();
        }

        @Override
        public void buildClientHello() throws GordianException {
            /* Access keys */
            final BouncyDHPublicKey myPublic = (BouncyDHPublicKey) getPublicKey(getServerKeyPair());
            final BouncyDHPrivateKey myPrivate = (BouncyDHPrivateKey) getPrivateKey(getClientEphemeral());

            /* Derive the secret */
            theAgreement.init(myPrivate.getPrivateKey());
            final BigInteger mySecretInt = theAgreement.calculateAgreement(myPublic.getPublicKey());
            final byte[] mySecret = BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(), mySecretInt);

            /* Store secret */
            storeSecret(mySecret);
        }

        @Override
        public void processClientHello() throws GordianException {
            /* Access keys */
            final BouncyDHPublicKey myPublic = (BouncyDHPublicKey) getPublicKey(getClientEphemeral());
            final BouncyDHPrivateKey myPrivate = (BouncyDHPrivateKey) getPrivateKey(getServerKeyPair());

            /* Derive the secret */
            theAgreement.init(myPrivate.getPrivateKey());
            final BigInteger mySecretInt = theAgreement.calculateAgreement(myPublic.getPublicKey());
            final byte[] mySecret = BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(), mySecretInt);

            /* Store secret */
            storeSecret(mySecret);
        }
    }

    /**
     * DH Basic Agreement Engine.
     */
    public static class BouncyDHBasicAgreementEngine
            extends BouncyAgreementBase {
        /**
         * The agreement.
         */
        private final DHBasicAgreement theAgreement;

        /**
         * Constructor.
         *
         * @param pFactory the security factory
         * @param pSpec    the agreementSpec
         * @throws GordianException on error
         */
        BouncyDHBasicAgreementEngine(final GordianCoreAgreementFactory pFactory,
                                     final GordianAgreementSpec pSpec) throws GordianException {
            /* Initialize underlying class */
            super(pFactory, pSpec);

            /* Create the agreement */
            theAgreement = new DHBasicAgreement();
        }

        @Override
        public void processClientHello() throws GordianException {
            /* Access keys */
            final BouncyDHPublicKey myPublic = (BouncyDHPublicKey) getPublicKey(getClientKeyPair());
            final BouncyDHPrivateKey myPrivate = (BouncyDHPrivateKey) getPrivateKey(getServerKeyPair());

            /* Derive the secret */
            theAgreement.init(myPrivate.getPrivateKey());
            final BigInteger mySecretInt = theAgreement.calculateAgreement(myPublic.getPublicKey());
            final byte[] mySecret = BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(), mySecretInt);

            /* Store secret */
            storeSecret(mySecret);
        }

        @Override
        public void processServerHello() throws GordianException {
            /* Access keys */
            final BouncyDHPublicKey myPublic = (BouncyDHPublicKey) getPublicKey(getServerKeyPair());
            final BouncyDHPrivateKey myPrivate = (BouncyDHPrivateKey) getPrivateKey(getClientKeyPair());

            /* Derive the secret */
            theAgreement.init(myPrivate.getPrivateKey());
            final BigInteger mySecretInt = theAgreement.calculateAgreement(myPublic.getPublicKey());
            final byte[] mySecret = BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(), mySecretInt);

            /* Store secret */
            storeSecret(mySecret);
        }
    }

    /**
     * DH Unified Agreement Engine.
     */
    public static class BouncyDHUnifiedAgreementEngine
            extends BouncyAgreementBase {
        /**
         * The agreement.
         */
        private final DHUnifiedAgreement theAgreement;

        /**
         * Constructor.
         *
         * @param pFactory the security factory
         * @param pSpec    the agreementSpec
         * @throws GordianException on error
         */
        BouncyDHUnifiedAgreementEngine(final GordianCoreAgreementFactory pFactory,
                                       final GordianAgreementSpec pSpec) throws GordianException {
            /* Initialize underlying class */
            super(pFactory, pSpec);

            /* Create the agreement */
            theAgreement = new DHUnifiedAgreement();
        }

        @Override
        public void processClientHello() throws GordianException {
            /* Access keys */
            final BouncyDHPublicKey myClientPublic = (BouncyDHPublicKey) getPublicKey(getClientKeyPair());
            final BouncyDHPublicKey myClientEphPublic = (BouncyDHPublicKey) getPublicKey(getClientEphemeral());
            final BouncyDHPrivateKey myPrivate = (BouncyDHPrivateKey) getPrivateKey(getServerKeyPair());
            final BouncyDHPublicKey myEphPublic = (BouncyDHPublicKey) getPublicKey(getServerEphemeral());
            final BouncyDHPrivateKey myEphPrivate = (BouncyDHPrivateKey) getPrivateKey(getServerEphemeral());

            /* Derive the secret */
            final DHUPrivateParameters myPrivParams
                    = new DHUPrivateParameters(myPrivate.getPrivateKey(), myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
            theAgreement.init(myPrivParams);

            /* Store secret */
            final DHUPublicParameters myPubParams
                    = new DHUPublicParameters(myClientPublic.getPublicKey(), myClientEphPublic.getPublicKey());
            storeSecret(theAgreement.calculateAgreement(myPubParams));
        }

        @Override
        public void processServerHello() throws GordianException {
            /* Access keys */
            final BouncyDHPublicKey myServerPublic = (BouncyDHPublicKey) getPublicKey(getServerKeyPair());
            final BouncyDHPublicKey myServerEphPublic = (BouncyDHPublicKey) getPublicKey(getServerEphemeral());
            final BouncyDHPrivateKey myPrivate = (BouncyDHPrivateKey) getPrivateKey(getClientKeyPair());
            final BouncyDHPublicKey myEphPublic = (BouncyDHPublicKey) getPublicKey(getClientEphemeral());
            final BouncyDHPrivateKey myEphPrivate = (BouncyDHPrivateKey) getPrivateKey(getClientEphemeral());

            /* Derive the secret */
            final DHUPrivateParameters myPrivParams
                    = new DHUPrivateParameters(myPrivate.getPrivateKey(), myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
            theAgreement.init(myPrivParams);

            /* Store secret */
            final DHUPublicParameters myPubParams
                    = new DHUPublicParameters(myServerPublic.getPublicKey(), myServerEphPublic.getPublicKey());
            storeSecret(theAgreement.calculateAgreement(myPubParams));
        }
    }

    /**
     * DH MQV XAgreement Engine.
     */
    public static class BouncyDHMQVAgreementEngine
            extends BouncyAgreementBase {
        /**
         * The agreement.
         */
        private final MQVBasicAgreement theAgreement;

        /**
         * Constructor.
         *
         * @param pFactory the security factory
         * @param pSpec    the agreementSpec
         * @throws GordianException on error
         */
        BouncyDHMQVAgreementEngine(final GordianCoreAgreementFactory pFactory,
                                   final GordianAgreementSpec pSpec) throws GordianException {
            /* Initialize underlying class */
            super(pFactory, pSpec);

            /* Create the agreement */
            theAgreement = new MQVBasicAgreement();
        }

        @Override
        public void processClientHello() throws GordianException {
            /* Access keys */
            final BouncyDHPublicKey myClientPublic = (BouncyDHPublicKey) getPublicKey(getClientKeyPair());
            final BouncyDHPublicKey myClientEphPublic = (BouncyDHPublicKey) getPublicKey(getClientEphemeral());
            final BouncyDHPrivateKey myPrivate = (BouncyDHPrivateKey) getPrivateKey(getServerKeyPair());
            final BouncyDHPublicKey myEphPublic = (BouncyDHPublicKey) getPublicKey(getServerEphemeral());
            final BouncyDHPrivateKey myEphPrivate = (BouncyDHPrivateKey) getPrivateKey(getServerEphemeral());

            /* Derive the secret */
            final DHMQVPrivateParameters myPrivParams
                    = new DHMQVPrivateParameters(myPrivate.getPrivateKey(), myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
            theAgreement.init(myPrivParams);

            /* Store secret */
            final DHMQVPublicParameters myPubParams
                    = new DHMQVPublicParameters(myClientPublic.getPublicKey(), myClientEphPublic.getPublicKey());
            storeSecret(BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(),
                    theAgreement.calculateAgreement(myPubParams)));
        }

        @Override
        public void processServerHello() throws GordianException {
            /* Access keys */
            final BouncyDHPublicKey myServerPublic = (BouncyDHPublicKey) getPublicKey(getServerKeyPair());
            final BouncyDHPublicKey myServerEphPublic = (BouncyDHPublicKey) getPublicKey(getServerEphemeral());
            final BouncyDHPrivateKey myPrivate = (BouncyDHPrivateKey) getPrivateKey(getClientKeyPair());
            final BouncyDHPublicKey myEphPublic = (BouncyDHPublicKey) getPublicKey(getClientEphemeral());
            final BouncyDHPrivateKey myEphPrivate = (BouncyDHPrivateKey) getPrivateKey(getClientEphemeral());

            /* Derive the secret */
            final DHMQVPrivateParameters myPrivParams
                    = new DHMQVPrivateParameters(myPrivate.getPrivateKey(), myEphPrivate.getPrivateKey(), myEphPublic.getPublicKey());
            theAgreement.init(myPrivParams);

            /* Store secret */
            final DHMQVPublicParameters myPubParams
                    = new DHMQVPublicParameters(myServerPublic.getPublicKey(), myServerEphPublic.getPublicKey());
            storeSecret(BigIntegers.asUnsignedByteArray(theAgreement.getFieldSize(),
                    theAgreement.calculateAgreement(myPubParams)));
        }
    }
}
