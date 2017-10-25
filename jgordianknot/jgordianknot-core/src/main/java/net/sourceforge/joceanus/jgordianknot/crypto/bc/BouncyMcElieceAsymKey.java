/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
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
import org.bouncycastle.pqc.asn1.McElieceCCA2PrivateKey;
import org.bouncycastle.pqc.asn1.McElieceCCA2PublicKey;
import org.bouncycastle.pqc.asn1.McEliecePrivateKey;
import org.bouncycastle.pqc.asn1.McEliecePublicKey;
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
import org.bouncycastle.pqc.jcajce.provider.mceliece.BCMcElieceCCA2PrivateKey;
import org.bouncycastle.pqc.jcajce.provider.mceliece.BCMcElieceCCA2PublicKey;
import org.bouncycastle.pqc.jcajce.provider.mceliece.BCMcEliecePrivateKey;
import org.bouncycastle.pqc.jcajce.provider.mceliece.BCMcEliecePublicKey;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
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
            extends BouncyPublicKey {
        /**
         * Public Key details.
         */
        private final McEliecePublicKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        protected BouncyMcEliecePublicKey(final GordianAsymKeySpec pKeySpec,
                                          final McEliecePublicKeyParameters pPublicKey) {
            super(pKeySpec);
            theKey = pPublicKey;
        }

        /**
         * Obtain the public key.
         * @return the key
         */
        protected McEliecePublicKeyParameters getPublicKey() {
            return theKey;
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle the trivial cases */
            if (pThat == this) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Make sure that the object is the same class */
            if (!(pThat instanceof BouncyMcEliecePublicKey)) {
                return false;
            }

            /* Access the target field */
            final BouncyMcEliecePublicKey myThat = (BouncyMcEliecePublicKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(myThat);
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
        }

        /**
         * CompareKeys.
         * @param pThat the key to compare with
         * @return true/false
         */
        private boolean compareKeys(final BouncyMcEliecePublicKey pThat) {
            return compareKeys(getPublicKey(), pThat.getPublicKey());
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
            extends BouncyPrivateKey {
        /**
         * Private Key details.
         */
        private final McEliecePrivateKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        protected BouncyMcEliecePrivateKey(final GordianAsymKeySpec pKeySpec,
                                           final McEliecePrivateKeyParameters pPrivateKey) {
            super(pKeySpec);
            theKey = pPrivateKey;
        }

        /**
         * Obtain the private key.
         * @return the key
         */
        protected McEliecePrivateKeyParameters getPrivateKey() {
            return theKey;
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle the trivial cases */
            if (pThat == this) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Make sure that the object is the same class */
            if (!(pThat instanceof BouncyMcEliecePrivateKey)) {
                return false;
            }

            /* Access the target field */
            final BouncyMcEliecePrivateKey myThat = (BouncyMcEliecePrivateKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(myThat);
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
        }

        /**
         * CompareKeys.
         * @param pThat the key to compare with
         * @return true/false
         */
        private boolean compareKeys(final BouncyMcEliecePrivateKey pThat) {
            return compareKeys(getPrivateKey(), pThat.getPrivateKey());
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
        protected BouncyMcElieceKeyPairGenerator(final BouncyFactory pFactory,
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
            final BouncyMcEliecePublicKey myPublic = new BouncyMcEliecePublicKey(getKeySpec(), McEliecePublicKeyParameters.class.cast(myPair.getPublic()));
            final BouncyMcEliecePrivateKey myPrivate = new BouncyMcEliecePrivateKey(getKeySpec(), McEliecePrivateKeyParameters.class.cast(myPair.getPrivate()));
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        protected PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) {
            final BouncyMcEliecePrivateKey myPrivateKey = BouncyMcEliecePrivateKey.class.cast(getPrivateKey(pKeyPair));
            final McEliecePrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
            final BCMcEliecePrivateKey myKey = new BCMcEliecePrivateKey(myParms);
            return new PKCS8EncodedKeySpec(myKey.getEncoded());
        }

        @Override
        protected BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                              final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            try {
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final McEliecePrivateKey myKey = McEliecePrivateKey.getInstance(myInfo.parsePrivateKey());
                final BouncyMcEliecePrivateKey myPrivate = new BouncyMcEliecePrivateKey(getKeySpec(),
                        new McEliecePrivateKeyParameters(myKey.getN(), myKey.getK(), myKey.getField(), myKey.getGoppaPoly(),
                                myKey.getP1(), myKey.getP2(), myKey.getSInv()));
                final BouncyMcEliecePublicKey myPublic = derivePublicKey(pPublicKey);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) {
            final BouncyMcEliecePublicKey myPublicKey = BouncyMcEliecePublicKey.class.cast(getPublicKey(pKeyPair));
            final McEliecePublicKeyParameters myParms = myPublicKey.getPublicKey();
            final BCMcEliecePublicKey myKey = new BCMcEliecePublicKey(myParms);
            return new X509EncodedKeySpec(myKey.getEncoded());
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
                final McEliecePublicKey myKey = McEliecePublicKey.getInstance(myInfo.parsePublicKey());
                final McEliecePublicKeyParameters myParms = new McEliecePublicKeyParameters(myKey.getN(),
                        myKey.getT(), myKey.getG());
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
            extends BouncyPublicKey {
        /**
         * Public Key details.
         */
        private final McElieceCCA2PublicKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        protected BouncyMcElieceCCA2PublicKey(final GordianAsymKeySpec pKeySpec,
                                              final McElieceCCA2PublicKeyParameters pPublicKey) {
            super(pKeySpec);
            theKey = pPublicKey;
        }

        /**
         * Obtain the public key.
         * @return the key
         */
        protected McElieceCCA2PublicKeyParameters getPublicKey() {
            return theKey;
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle the trivial cases */
            if (pThat == this) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Make sure that the object is the same class */
            if (!(pThat instanceof BouncyMcElieceCCA2PublicKey)) {
                return false;
            }

            /* Access the target field */
            final BouncyMcElieceCCA2PublicKey myThat = (BouncyMcElieceCCA2PublicKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(myThat);
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
        }

        /**
         * CompareKeys.
         * @param pThat the key to compare with
         * @return true/false
         */
        private boolean compareKeys(final BouncyMcElieceCCA2PublicKey pThat) {
            return compareKeys(getPublicKey(), pThat.getPublicKey());
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
            extends BouncyPrivateKey {
        /**
         * Private Key details.
         */
        private final McElieceCCA2PrivateKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        protected BouncyMcElieceCCA2PrivateKey(final GordianAsymKeySpec pKeySpec,
                                               final McElieceCCA2PrivateKeyParameters pPrivateKey) {
            super(pKeySpec);
            theKey = pPrivateKey;
        }

        /**
         * Obtain the private key.
         * @return the key
         */
        protected McElieceCCA2PrivateKeyParameters getPrivateKey() {
            return theKey;
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle the trivial cases */
            if (pThat == this) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Make sure that the object is the same class */
            if (!(pThat instanceof BouncyMcElieceCCA2PrivateKey)) {
                return false;
            }

            /* Access the target field */
            final BouncyMcElieceCCA2PrivateKey myThat = (BouncyMcElieceCCA2PrivateKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(myThat);
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
        }

        /**
         * CompareKeys.
         * @param pThat the key to compare with
         * @return true/false
         */
        private boolean compareKeys(final BouncyMcElieceCCA2PrivateKey pThat) {
            return compareKeys(getPrivateKey(), pThat.getPrivateKey());
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
        protected BouncyMcElieceCCA2KeyPairGenerator(final BouncyFactory pFactory,
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
            final BouncyMcElieceCCA2PublicKey myPublic = new BouncyMcElieceCCA2PublicKey(getKeySpec(), McElieceCCA2PublicKeyParameters.class.cast(myPair.getPublic()));
            final BouncyMcElieceCCA2PrivateKey myPrivate = new BouncyMcElieceCCA2PrivateKey(getKeySpec(), McElieceCCA2PrivateKeyParameters.class.cast(myPair.getPrivate()));
            return new BouncyKeyPair(myPublic, myPrivate);
        }

        @Override
        protected PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) {
            final BouncyMcElieceCCA2PrivateKey myPrivateKey = BouncyMcElieceCCA2PrivateKey.class.cast(getPrivateKey(pKeyPair));
            final McElieceCCA2PrivateKeyParameters myParms = myPrivateKey.getPrivateKey();
            final BCMcElieceCCA2PrivateKey myKey = new BCMcElieceCCA2PrivateKey(myParms);
            return new PKCS8EncodedKeySpec(myKey.getEncoded());
        }

        @Override
        protected BouncyKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                              final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            try {
                final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
                final McElieceCCA2PrivateKey myKey = McElieceCCA2PrivateKey.getInstance(myInfo.parsePrivateKey());
                final BouncyMcElieceCCA2PrivateKey myPrivate = new BouncyMcElieceCCA2PrivateKey(getKeySpec(),
                        new McElieceCCA2PrivateKeyParameters(myKey.getN(), myKey.getK(), myKey.getField(), myKey.getGoppaPoly(),
                                myKey.getP(), getDigest()));
                final BouncyMcElieceCCA2PublicKey myPublic = derivePublicKey(pPublicKey);
                return new BouncyKeyPair(myPublic, myPrivate);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }

        @Override
        public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) {
            final BouncyMcElieceCCA2PublicKey myPublicKey = BouncyMcElieceCCA2PublicKey.class.cast(getPublicKey(pKeyPair));
            final McElieceCCA2PublicKeyParameters myParms = myPublicKey.getPublicKey();
            final BCMcElieceCCA2PublicKey myKey = new BCMcElieceCCA2PublicKey(myParms);
            return new X509EncodedKeySpec(myKey.getEncoded());
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
                final McElieceCCA2PublicKey myKey = McElieceCCA2PublicKey.getInstance(myInfo.parsePublicKey());
                final McElieceCCA2PublicKeyParameters myParms = new McElieceCCA2PublicKeyParameters(myKey.getN(),
                        myKey.getT(), myKey.getG(), getDigest());
                return new BouncyMcElieceCCA2PublicKey(getKeySpec(), myParms);
            } catch (IOException e) {
                throw new GordianCryptoException(ERROR_PARSE, e);
            }
        }
    }
}
