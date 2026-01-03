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
import org.bouncycastle.pqc.crypto.mlkem.MLKEMExtractor;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMGenerator;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMKeyPairGenerator;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
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
 * MLKEM KeyPair classes.
 */
public final class BouncyMLKEMKeyPair {
    /**
     * Private constructor.
     */
    private BouncyMLKEMKeyPair() {
    }

    /**
     * Bouncy KYBER PublicKey.
     */
    public static class BouncyMLKEMPublicKey
            extends BouncyPublicKey<MLKEMPublicKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        BouncyMLKEMPublicKey(final GordianKeyPairSpec pKeySpec,
                             final MLKEMPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final MLKEMPublicKeyParameters myThis = getPublicKey();
            final MLKEMPublicKeyParameters myThat = (MLKEMPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy MLKEM PrivateKey.
     */
    public static class BouncyMLKEMPrivateKey
            extends BouncyPrivateKey<MLKEMPrivateKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        BouncyMLKEMPrivateKey(final GordianKeyPairSpec pKeySpec,
                              final MLKEMPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final MLKEMPrivateKeyParameters myThis = getPrivateKey();
            final MLKEMPrivateKeyParameters myThat = (MLKEMPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle MLKEM KeyPair generator.
     */
    public static class BouncyMLKEMKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final MLKEMKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws GordianException on error
         */
        BouncyMLKEMKeyPairGenerator(final BouncyFactory pFactory,
                                    final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create the generator */
            theGenerator = new MLKEMKeyPairGenerator();

            /* Determine the parameters */
            final MLKEMParameters myParms = pKeySpec.getMLKEMKeySpec().getParameters();

            /* Initialise the generator */
            final MLKEMKeyGenerationParameters myParams = new MLKEMKeyGenerationParameters(getRandom(), myParms);
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyMLKEMPublicKey myPublic = new BouncyMLKEMPublicKey(getKeySpec(), (MLKEMPublicKeyParameters) myPair.getPublic());
            final BouncyMLKEMPrivateKey myPrivate = new BouncyMLKEMPrivateKey(getKeySpec(), (MLKEMPrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Check the keyPair type and keySpecs */
                BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

                /* build and return the encoding */
                final BouncyMLKEMPrivateKey myPrivateKey = (BouncyMLKEMPrivateKey) getPrivateKey(pKeyPair);
                final MLKEMPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
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
                final BouncyMLKEMPublicKey myPublic = derivePublicKey(pPublicKey);
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final MLKEMPrivateKeyParameters myParms = (MLKEMPrivateKeyParameters) PrivateKeyFactory.createKey(myInfo);
                final BouncyMLKEMPrivateKey myPrivate = new BouncyMLKEMPrivateKey(getKeySpec(), myParms);
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
                final BouncyMLKEMPublicKey myPublicKey = (BouncyMLKEMPublicKey) getPublicKey(pKeyPair);
                final MLKEMPublicKeyParameters myParms = myPublicKey.getPublicKey();
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(myParms);
                final byte[] myBytes = myInfo.getEncoded(ASN1Encoding.DER);
                return new X509EncodedKeySpec(myBytes);

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws GordianException {
            final BouncyMLKEMPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws GordianException on error
         */
        private BouncyMLKEMPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pEncodedKey);

                /* derive publicKey */
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final MLKEMPublicKeyParameters myParms = (MLKEMPublicKeyParameters) PublicKeyFactory.createKey(myInfo);
                return new BouncyMLKEMPublicKey(getKeySpec(), myParms);

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * MLKEM Agreement.
     */
    public static class BouncyMLKEMAgreement
            extends GordianCoreAnonymousAgreement {
        /**
         * The generator.
         */
        private final MLKEMGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         */
        BouncyMLKEMAgreement(final BouncyFactory pFactory,
                             final GordianAgreementSpec pSpec) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create Agreement */
            theGenerator = new MLKEMGenerator(getRandom());
        }

        @Override
        public GordianAgreementMessageASN1 createClientHelloASN1(final GordianKeyPair pServer) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Check keyPair */
                BouncyKeyPair.checkKeyPair(pServer);
                checkKeyPair(pServer);

                /* Create encapsulation */
                final BouncyMLKEMPublicKey myPublic = (BouncyMLKEMPublicKey) getPublicKey(pServer);
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
            final BouncyMLKEMPrivateKey myPrivate = (BouncyMLKEMPrivateKey) getPrivateKey(pServer);
            final MLKEMExtractor myExtractor = new MLKEMExtractor(myPrivate.getPrivateKey());

            /* Parse clientHello message and store secret */
            final byte[] myMessage = pClientHello.getEncapsulated();
            storeSecret(myExtractor.extractSecret(myMessage));
        }
    }

    /**
     * MLKEM XAgreement Engine.
     */
    public static class BouncyMLKEMXAgreementEngine
            extends BouncyXAgreementBase {
        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         * @throws GordianException on error
         */
        BouncyMLKEMXAgreementEngine(final GordianXCoreAgreementFactory pFactory,
                                    final GordianAgreementSpec pSpec) throws GordianException {
            /* Initialize underlying class */
            super(pFactory, pSpec);
        }

        @Override
        public void buildClientHello() throws GordianException {
            /* Protect against exceptions */
            try {
                /* Create encapsulation */
                final BouncyMLKEMPublicKey myPublic = (BouncyMLKEMPublicKey) getPublicKey(getServerKeyPair());
                final MLKEMGenerator myGenerator = new MLKEMGenerator(getRandom());
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
            final BouncyMLKEMPrivateKey myPrivate = (BouncyMLKEMPrivateKey) getPrivateKey(getServerKeyPair());
            final MLKEMExtractor myExtractor = new MLKEMExtractor(myPrivate.getPrivateKey());

            /* Parse encapsulated message and store secret */
            final byte[] myMessage = getEncapsulated();
            storeSecret(myExtractor.extractSecret(myMessage));
        }
    }
}
