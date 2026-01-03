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
import org.bouncycastle.pqc.crypto.saber.SABERKEMExtractor;
import org.bouncycastle.pqc.crypto.saber.SABERKEMGenerator;
import org.bouncycastle.pqc.crypto.saber.SABERKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.saber.SABERKeyPairGenerator;
import org.bouncycastle.pqc.crypto.saber.SABERParameters;
import org.bouncycastle.pqc.crypto.saber.SABERPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.saber.SABERPublicKeyParameters;
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
 * SABER KeyPair classes.
 */
public final class BouncySABERKeyPair {
    /**
     * Private constructor.
     */
    private BouncySABERKeyPair() {
    }

    /**
     * Bouncy SABER PublicKey.
     */
    public static class BouncySABERPublicKey
            extends BouncyPublicKey<SABERPublicKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        BouncySABERPublicKey(final GordianKeyPairSpec pKeySpec,
                             final SABERPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SABERPublicKeyParameters myThis = getPublicKey();
            final SABERPublicKeyParameters myThat = (SABERPublicKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * Bouncy SABER PrivateKey.
     */
    public static class BouncySABERPrivateKey
            extends BouncyPrivateKey<SABERPrivateKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        BouncySABERPrivateKey(final GordianKeyPairSpec pKeySpec,
                              final SABERPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final SABERPrivateKeyParameters myThis = getPrivateKey();
            final SABERPrivateKeyParameters myThat = (SABERPrivateKeyParameters) pThat;

            /* Compare keys */
            return Arrays.equals(myThis.getEncoded(), myThat.getEncoded());
        }
    }

    /**
     * BouncyCastle SABER KeyPair generator.
     */
    public static class BouncySABERKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final SABERKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws GordianException on error
         */
        BouncySABERKeyPairGenerator(final BouncyFactory pFactory,
                                    final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create the generator */
            theGenerator = new SABERKeyPairGenerator();

            /* Determine the parameters */
            final SABERParameters myParms = pKeySpec.getSABERKeySpec().getParameters();

            /* Initialise the generator */
            final SABERKeyGenerationParameters myParams = new SABERKeyGenerationParameters(getRandom(), myParms);
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncySABERPublicKey myPublic = new BouncySABERPublicKey(getKeySpec(), (SABERPublicKeyParameters) myPair.getPublic());
            final BouncySABERPrivateKey myPrivate = new BouncySABERPrivateKey(getKeySpec(), (SABERPrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Check the keyPair type and keySpecs */
                BouncyKeyPair.checkKeyPair(pKeyPair, getKeySpec());

                /* build and return the encoding */
                final BouncySABERPrivateKey myPrivateKey = (BouncySABERPrivateKey) getPrivateKey(pKeyPair);
                final SABERPrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
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
                final BouncySABERPublicKey myPublic = derivePublicKey(pPublicKey);
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final SABERPrivateKeyParameters myParms = (SABERPrivateKeyParameters) PrivateKeyFactory.createKey(myInfo);
                final BouncySABERPrivateKey myPrivate = new BouncySABERPrivateKey(getKeySpec(), myParms);
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
                final BouncySABERPublicKey myPublicKey = (BouncySABERPublicKey) getPublicKey(pKeyPair);
                final SABERPublicKeyParameters myParms = myPublicKey.getPublicKey();
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(myParms);
                final byte[] myBytes = myInfo.getEncoded(ASN1Encoding.DER);
                return new X509EncodedKeySpec(myBytes);

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws GordianException {
            final BouncySABERPublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws GordianException on error
         */
        private BouncySABERPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pEncodedKey);

                /* derive publicKey */
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final SABERPublicKeyParameters myParms = (SABERPublicKeyParameters) PublicKeyFactory.createKey(myInfo);
                return new BouncySABERPublicKey(getKeySpec(), myParms);

            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * SABER Agreement.
     */
    public static class BouncySABERAgreement
            extends GordianCoreAnonymousAgreement {
        /**
         * The generator.
         */
        private final SABERKEMGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         */
        BouncySABERAgreement(final BouncyFactory pFactory,
                             final GordianAgreementSpec pSpec) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create Agreement */
            theGenerator = new SABERKEMGenerator(getRandom());
        }

        @Override
        public GordianAgreementMessageASN1 createClientHelloASN1(final GordianKeyPair pServer) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Check keyPair */
                BouncyKeyPair.checkKeyPair(pServer);
                checkKeyPair(pServer);

                /* Create encapsulation */
                final BouncySABERPublicKey myPublic = (BouncySABERPublicKey) getPublicKey(pServer);
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
            final BouncySABERPrivateKey myPrivate = (BouncySABERPrivateKey) getPrivateKey(pServer);
            final SABERKEMExtractor myExtractor = new SABERKEMExtractor(myPrivate.getPrivateKey());

            /* Parse clientHello message and store secret */
            final byte[] myMessage = pClientHello.getEncapsulated();
            storeSecret(myExtractor.extractSecret(myMessage));
        }
    }

    /**
     * SABER XAgreement Engine.
     */
    public static class BouncySABERXAgreementEngine
            extends BouncyXAgreementBase {
        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         * @throws GordianException on error
         */
        BouncySABERXAgreementEngine(final GordianXCoreAgreementFactory pFactory,
                                    final GordianAgreementSpec pSpec) throws GordianException {
            /* Initialize underlying class */
            super(pFactory, pSpec);
        }

        @Override
        public void buildClientHello() throws GordianException {
            /* Protect against exceptions */
            try {
                /* Create encapsulation */
                final BouncySABERPublicKey myPublic = (BouncySABERPublicKey) getPublicKey(getServerKeyPair());
                final SABERKEMGenerator myGenerator = new SABERKEMGenerator(getRandom());
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
            final BouncySABERPrivateKey myPrivate = (BouncySABERPrivateKey) getPrivateKey(getServerKeyPair());
            final SABERKEMExtractor myExtractor = new SABERKEMExtractor(myPrivate.getPrivateKey());

            /* Parse encapsulated message and store secret */
            final byte[] myMessage = getEncapsulated();
            storeSecret(myExtractor.extractSecret(myMessage));
        }
    }
}
