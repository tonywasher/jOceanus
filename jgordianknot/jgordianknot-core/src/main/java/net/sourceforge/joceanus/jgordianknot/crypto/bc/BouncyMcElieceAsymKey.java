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

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.newutils.PqcPrivateKeyFactory;
import org.bouncycastle.crypto.newutils.PqcPrivateKeyInfoFactory;
import org.bouncycastle.crypto.newutils.PqcPublicKeyFactory;
import org.bouncycastle.crypto.newutils.PqcSubjectPublicKeyInfoFactory;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2KeyPairGenerator;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2Parameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2PrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2PublicKeyParameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceKeyPairGenerator;
import org.bouncycastle.pqc.crypto.mceliece.McElieceParameters;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePublicKeyParameters;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * McEliece AsymKey classes.
 */
public final class BouncyMcElieceAsymKey {
    /**
     * Private constructor.
     */
    private BouncyMcElieceAsymKey() {
    }

    /**
     * Bouncy McEliece PublicKey.
     */
    public static class BouncyMcEliecePublicKey
            extends BouncyPublicKey<McEliecePublicKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        BouncyMcEliecePublicKey(final GordianAsymKeySpec pKeySpec,
                                final McEliecePublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
         }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final McEliecePublicKeyParameters myThis = getPublicKey();
            final McEliecePublicKeyParameters myThat = (McEliecePublicKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
        }

        /**
         * Is the private key valid for this public key?
         * @param pPrivate the private key
         * @return true/false
         */
        public boolean validPrivate(final BouncyMcEliecePrivateKey pPrivate) {
            final McEliecePrivateKeyParameters myPrivate = pPrivate.getPrivateKey();
            return getPublicKey().getN() == myPrivate.getN();
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final McEliecePublicKeyParameters pFirst,
                                           final McEliecePublicKeyParameters pSecond) {
            if (pFirst.getN() != pSecond.getN()
                || pFirst.getT() != pSecond.getT()) {
                return false;
            }
            return pFirst.getG().equals(pSecond.getG());
        }
    }

    /**
     * Bouncy McEliece PrivateKey.
     */
    public static class BouncyMcEliecePrivateKey
            extends BouncyPrivateKey<McEliecePrivateKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        BouncyMcEliecePrivateKey(final GordianAsymKeySpec pKeySpec,
                                 final McEliecePrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final McEliecePrivateKeyParameters myThis = getPrivateKey();
            final McEliecePrivateKeyParameters myThat = (McEliecePrivateKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final McEliecePrivateKeyParameters pFirst,
                                           final McEliecePrivateKeyParameters pSecond) {
            if (pFirst.getN() != pSecond.getN()
                || pFirst.getK() != pSecond.getK()) {
                return false;
            }
            if (!pFirst.getP1().equals(pSecond.getP1())
                || !pFirst.getP2().equals(pSecond.getP2())) {
                return false;
            }
            return pFirst.getField().equals(pSecond.getField())
                   && pFirst.getGoppaPoly().equals(pSecond.getGoppaPoly());
        }
    }

    /**
     * BouncyCastle McEliece KeyPair generator.
     */
    public static class BouncyMcElieceKeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final McElieceKeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyMcElieceKeyPairGenerator(final BouncyFactory pFactory,
                                       final GordianAsymKeySpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = new McElieceKeyPairGenerator();
            final KeyGenerationParameters myParams = new McElieceKeyGenerationParameters(getRandom(), new McElieceParameters(new SHA256Digest()));
            theGenerator.init(myParams);
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyMcEliecePublicKey myPublic = new BouncyMcEliecePublicKey(getKeySpec(), (McEliecePublicKeyParameters) myPair.getPublic());
            final BouncyMcEliecePrivateKey myPrivate = new BouncyMcEliecePrivateKey(getKeySpec(), (McEliecePrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
                final BouncyMcEliecePrivateKey myPrivateKey = (BouncyMcEliecePrivateKey) getPrivateKey(pKeyPair);
                final McEliecePrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
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
                final McEliecePrivateKeyParameters myParms = (McEliecePrivateKeyParameters) PqcPrivateKeyFactory.createKey(myInfo);
                final BouncyMcEliecePrivateKey myPrivate = new BouncyMcEliecePrivateKey(getKeySpec(), myParms);
                final BouncyMcEliecePublicKey myPublic = derivePublicKey(pPublicKey);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
                final BouncyMcEliecePublicKey myPublicKey = (BouncyMcEliecePublicKey) getPublicKey(pKeyPair);
                final McEliecePublicKeyParameters myParms = myPublicKey.getPublicKey();
                final SubjectPublicKeyInfo myInfo = PqcSubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(myParms);
                return new X509EncodedKeySpec(myInfo.getEncoded());
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            final BouncyMcEliecePublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws OceanusException on error
         */
        private BouncyMcEliecePublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            try {
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final McEliecePublicKeyParameters myParms = (McEliecePublicKeyParameters) PqcPublicKeyFactory.createKey(myInfo);
                return new BouncyMcEliecePublicKey(getKeySpec(), myParms);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }

    /**
     * Bouncy McElieceCCA2 PublicKey.
     */
    public static class BouncyMcElieceCCA2PublicKey
            extends BouncyPublicKey<McElieceCCA2PublicKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        BouncyMcElieceCCA2PublicKey(final GordianAsymKeySpec pKeySpec,
                                    final McElieceCCA2PublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final McElieceCCA2PublicKeyParameters myThis = getPublicKey();
            final McElieceCCA2PublicKeyParameters myThat = (McElieceCCA2PublicKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
        }

        /**
         * Is the private key valid for this public key?
         * @param pPrivate the private key
         * @return true/false
         */
        public boolean validPrivate(final BouncyMcElieceCCA2PrivateKey pPrivate) {
            final McElieceCCA2PrivateKeyParameters myPrivate = pPrivate.getPrivateKey();
            return getPublicKey().getN() == myPrivate.getN();
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final McElieceCCA2PublicKeyParameters pFirst,
                                           final McElieceCCA2PublicKeyParameters pSecond) {
            if (pFirst.getN() != pSecond.getN()
                || pFirst.getT() != pSecond.getT()) {
                return false;
            }
            return pFirst.getDigest().equals(pSecond.getDigest())
                   && pFirst.getG().equals(pSecond.getG());
        }
    }

    /**
     * Bouncy McElieceCCA2 PrivateKey.
     */
    public static class BouncyMcElieceCCA2PrivateKey
            extends BouncyPrivateKey<McElieceCCA2PrivateKeyParameters> {
        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        BouncyMcElieceCCA2PrivateKey(final GordianAsymKeySpec pKeySpec,
                                     final McElieceCCA2PrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final McElieceCCA2PrivateKeyParameters myThis = getPrivateKey();
            final McElieceCCA2PrivateKeyParameters myThat = (McElieceCCA2PrivateKeyParameters) pThat;

            /* Compare keys */
            return compareKeys(myThis, myThat);
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final McElieceCCA2PrivateKeyParameters pFirst,
                                           final McElieceCCA2PrivateKeyParameters pSecond) {
            if (pFirst.getN() != pSecond.getN()
                || pFirst.getK() != pSecond.getK()) {
                return false;
            }
            if (!pFirst.getP().equals(pSecond.getP())
                || !pFirst.getDigest().equals(pSecond.getDigest())) {
                return false;
            }
            return pFirst.getField().equals(pSecond.getField())
                   && pFirst.getGoppaPoly().equals(pSecond.getGoppaPoly());
        }
    }

    /**
     * BouncyCastle McElieceCCA2 KeyPair generator.
     */
    public static class BouncyMcElieceCCA2KeyPairGenerator
            extends BouncyKeyPairGenerator {
        /**
         * Generator.
         */
        private final McElieceCCA2KeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyMcElieceCCA2KeyPairGenerator(final BouncyFactory pFactory,
                                           final GordianAsymKeySpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = new McElieceCCA2KeyPairGenerator();
            final KeyGenerationParameters myParams = new McElieceCCA2KeyGenerationParameters(getRandom(),
                    new McElieceCCA2Parameters(getDigest()));
            theGenerator.init(myParams);
        }

        /**
         * Obtain the digest string.
         * @return the digest
         */
        private String getDigest() {
            return getKeySpec().getMcElieceSpec().getDigestType().getParameter();
        }

        @Override
        public BouncyKeyPair generateKeyPair() {
            final AsymmetricCipherKeyPair myPair = theGenerator.generateKeyPair();
            final BouncyMcElieceCCA2PublicKey myPublic = new BouncyMcElieceCCA2PublicKey(getKeySpec(), (McElieceCCA2PublicKeyParameters) myPair.getPublic());
            final BouncyMcElieceCCA2PrivateKey myPrivate = new BouncyMcElieceCCA2PrivateKey(getKeySpec(), (McElieceCCA2PrivateKeyParameters) myPair.getPrivate());
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
                final BouncyMcElieceCCA2PrivateKey myPrivateKey = (BouncyMcElieceCCA2PrivateKey) getPrivateKey(pKeyPair);
                final McElieceCCA2PrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
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
                final McElieceCCA2PrivateKeyParameters myParms = (McElieceCCA2PrivateKeyParameters) PqcPrivateKeyFactory.createKey(myInfo);
                final BouncyMcElieceCCA2PrivateKey myPrivate = new BouncyMcElieceCCA2PrivateKey(getKeySpec(), myParms);
                final BouncyMcElieceCCA2PublicKey myPublic = derivePublicKey(pPublicKey);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
            try {
                final BouncyMcElieceCCA2PublicKey myPublicKey = (BouncyMcElieceCCA2PublicKey) getPublicKey(pKeyPair);
                final McElieceCCA2PublicKeyParameters myParms = myPublicKey.getPublicKey();
                final SubjectPublicKeyInfo myInfo = PqcSubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(myParms);
                return new X509EncodedKeySpec(myInfo.getEncoded());
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public BouncyKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            final BouncyMcElieceCCA2PublicKey myPublic = derivePublicKey(pEncodedKey);
            return new BouncyKeyPair(myPublic);
        }

        /**
         * Derive public key from encoded.
         * @param pEncodedKey the encoded key
         * @return the public key
         * @throws OceanusException on error
         */
        private BouncyMcElieceCCA2PublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
            try {
                final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncodedKey.getEncoded());
                final McElieceCCA2PublicKeyParameters myParms = (McElieceCCA2PublicKeyParameters) PqcPublicKeyFactory.createKey(myInfo);
                return new BouncyMcElieceCCA2PublicKey(getKeySpec(), myParms);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }
}
