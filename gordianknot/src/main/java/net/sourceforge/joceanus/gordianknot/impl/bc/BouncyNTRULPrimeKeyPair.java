/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.bc;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.gordianknot.impl.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.gordianknot.impl.core.agree.GordianAgreementMessageASN1;
import net.sourceforge.joceanus.gordianknot.impl.core.agree.GordianCoreAnonymousAgreement;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianIOException;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianKeyPairValidity;
import net.sourceforge.joceanus.gordianknot.impl.core.xagree.GordianXCoreAgreementFactory;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeKEMExtractor;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeKEMGenerator;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeKeyPairGenerator;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimePublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory;

import javax.security.auth.DestroyFailedException;
import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

/**
 * NTRULPrime KeyPair classes.
 */
public final class BouncyNTRULPrimeKeyPair {
    /**
     * Private constructor.
     */
    private BouncyNTRULPrimeKeyPair() {
    }

    /**
     * Bouncy NTRULPrime PublicKey.
     */
    public static class BouncyNTRULPrimePublicKey
            extends BouncyPublicKey<NTRULPRimePublicKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        BouncyNTRULPrimePublicKey(final GordianKeyPairSpec pKeySpec,
                                  final NTRULPRimePublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final NTRULPRimePublicKeyParameters myThis = getPublicKey();
            final NTRULPRimePublicKeyParameters myThat = (NTRULPRimePublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy NTRULPrime PrivateKey.
     */
    public static class BouncyNTRULPrimePrivateKey
            extends BouncyPrivateKey<NTRULPRimePrivateKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        BouncyNTRULPrimePrivateKey(final GordianKeyPairSpec pKeySpec,
                                   final NTRULPRimePrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final NTRULPRimePrivateKeyParameters myThis = getPrivateKey();
            final NTRULPRimePrivateKeyParameters myThat = (NTRULPRimePrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle NTRULPrime KeyPair generator.
     */
    public static class BouncyNTRULPrimeKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final NTRULPRimeKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws GordianException on error
         */
        BouncyNTRULPrimeKeyPairGenerator(final BouncyFactory pFactory,
                                         final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create the generator */
            theGenerator = new NTRULPRimeKeyPairGenerator();

            /* Determine the parameters */
            final NTRULPRimeParameters myParms = pKeySpec.getNTRUPrimeKeySpec().getParams().getNTRULParameters();

            /* Initialise the generator */
            final NTRULPRimeKeyGenerationParameters myParams = new NTRULPRimeKeyGenerationParameters(getRandom(), myParms);
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyNTRULPrimePublicKey myPublic = new BouncyNTRULPrimePublicKey(getKeySpec(), (NTRULPRimePublicKeyParameters) myPair.getPublic());
            final BouncyNTRULPrimePrivateKey myPrivate = new BouncyNTRULPrimePrivateKey(getKeySpec(), (NTRULPRimePrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Check the keyPair type and keySpecs */
                BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

                /* build and return the encoding */
                final BouncyNTRULPrimePrivateKey myPrivateKey = (BouncyNTRULPrimePrivateKey) getPrivateKey(pKeyPair);
                final NTRULPRimePrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
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
                final BouncyNTRULPrimePublicKey myPublic = derivePublicKey(pPublicKey);
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final NTRULPRimePrivateKeyParameters myParms = (NTRULPRimePrivateKeyParameters) PrivateKeyFactory.createKey(myInfo);
                final BouncyNTRULPrimePrivateKey myPrivate = new BouncyNTRULPrimePrivateKey(getKeySpec(), myParms);
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
                final BouncyNTRULPrimePublicKey myPublicKey = (BouncyNTRULPrimePublicKey) getPublicKey(pKeyPair);
                final NTRULPRimePublicKeyParameters myParms = myPublicKey.getPublicKey();
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(myParms);
                final byte[] myBytes = myInfo.getEncoded(ASN1Encoding.DER);
                return new X509EncodedKeySpec(myBytes);

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws GordianException {
            final BouncyNTRULPrimePublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws GordianException on error
         */
        private BouncyNTRULPrimePublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pEncodedKey);

                /* derive publicKey */
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final NTRULPRimePublicKeyParameters myParms = (NTRULPRimePublicKeyParameters) PublicKeyFactory.createKey(myInfo);
                return new BouncyNTRULPrimePublicKey(getKeySpec(), myParms);

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * NTRULPrime Agreement.
     */
    public static class BouncyNTRULPrimeAgreement
            extends GordianCoreAnonymousAgreement {
        /**
         * The generator.
         */
        private final NTRULPRimeKEMGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         */
        BouncyNTRULPrimeAgreement(final BouncyFactory pFactory,
                                  final GordianAgreementSpec pSpec) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create Agreement */
            theGenerator = new NTRULPRimeKEMGenerator(getRandom());
        }

        @Override
        public GordianAgreementMessageASN1 createClientHelloASN1(final GordianKeyPair pServer) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Check keyPair */
                BouncyKeyPair.checkKeyPair(pServer);
                checkKeyPair(pServer);

                /* Create encapsulation */
                final BouncyNTRULPrimePublicKey myPublic = (BouncyNTRULPrimePublicKey) getPublicKey(pServer);
                final SecretWithEncapsulation myResult = theGenerator.generateEncapsulated(myPublic.getPublicKey());

                /* Build the clientHello Message */
                final GordianAgreementMessageASN1 myClientHello = buildClientHelloASN1(myResult.getEncapsulation());

                /* Store secret and create initVector */
                storeSecret(myResult.getSecret());
                myResult.destroy();

                /* Return the message  */
                return myClientHello;
            } catch (DestroyFailedException e) {
                throw new GordianIOException("Failed to destroy secret", e);
            }
        }

        @Override
        public void acceptClientHelloASN1(final GordianKeyPair pServer,
                                          final GordianAgreementMessageASN1 pClientHello) throws GordianException {
            /* Check keyPair */
            BouncyKeyPair.checkKeyPair(pServer);
            checkKeyPair(pServer);

            /* Initialise Key Encapsulation */
            final BouncyNTRULPrimePrivateKey myPrivate = (BouncyNTRULPrimePrivateKey) getPrivateKey(pServer);
            final NTRULPRimeKEMExtractor myExtractor = new NTRULPRimeKEMExtractor(myPrivate.getPrivateKey());

            /* Parse clientHello message and store secret */
            final byte[] myMessage = pClientHello.getEncapsulated();
            storeSecret(myExtractor.extractSecret(myMessage));
        }
    }

    /**
     * NTRULPrime XAgreement Engine.
     */
    public static class BouncyNTRULPrimeXAgreementEngine
            extends BouncyXAgreementBase {
        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         * @throws GordianException on error
         */
        BouncyNTRULPrimeXAgreementEngine(final GordianXCoreAgreementFactory pFactory,
                                         final GordianAgreementSpec pSpec) throws GordianException {
            /* Initialize underlying class */
            super(pFactory, pSpec);
        }

        @Override
        public void buildClientHello() throws GordianException {
            /* Protect against exceptions */
            try {
                /* Create encapsulation */
                final BouncyNTRULPrimePublicKey myPublic = (BouncyNTRULPrimePublicKey) getPublicKey(getServerKeyPair());
                final NTRULPRimeKEMGenerator myGenerator = new NTRULPRimeKEMGenerator(getRandom());
                final SecretWithEncapsulation myResult = myGenerator.generateEncapsulated(myPublic.getPublicKey());

                /* Store the encapsulation */
                setEncapsulated(myResult.getEncapsulation());

                /* Store secret and create initVector */
                storeSecret(myResult.getSecret());
                myResult.destroy();

            } catch (DestroyFailedException e) {
                throw new GordianIOException("Failed to destroy secret", e);
            }
        }

        @Override
        public void processClientHello() throws GordianException {
            /* Create encapsulation */
            final BouncyNTRULPrimePrivateKey myPrivate = (BouncyNTRULPrimePrivateKey) getPrivateKey(getServerKeyPair());
            final NTRULPRimeKEMExtractor myExtractor = new NTRULPRimeKEMExtractor(myPrivate.getPrivateKey());

            /* Parse encapsulated message and store secret */
            final byte[] myMessage = getEncapsulated();
            storeSecret(myExtractor.extractSecret(myMessage));
        }
    }
}
