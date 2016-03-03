/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
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

import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianPublicKey;

/**
 * BouncyCastle Asymmetric KeyPair.
 */
public class BouncyKeyPair
        extends GordianKeyPair {
    /**
     * Constructor.
     * @param pPublic the public key
     */
    protected BouncyKeyPair(final BouncyPublicKey pPublic) {
        this(pPublic, null);
    }

    /**
     * Constructor.
     * @param pPublic the public key
     * @param pPrivate the private key
     */
    protected BouncyKeyPair(final BouncyPublicKey pPublic,
                            final BouncyPrivateKey pPrivate) {
        super(pPublic, pPrivate);
    }

    @Override
    public BouncyPublicKey getPublicKey() {
        return (BouncyPublicKey) super.getPublicKey();
    }

    @Override
    public BouncyPrivateKey getPrivateKey() {
        return (BouncyPrivateKey) super.getPrivateKey();
    }

    /**
     * Bouncy PublicKey.
     */
    public static class BouncyPublicKey
            extends GordianPublicKey {
        /**
         * Constructor.
         * @param pKeyType the key type
         */
        protected BouncyPublicKey(final GordianAsymKeyType pKeyType) {
            super(pKeyType);
        }
    }

    /**
     * Bouncy PrivateKey.
     */
    public static class BouncyPrivateKey
            extends GordianPrivateKey {
        /**
         * Constructor.
         * @param pKeyType the key type
         */
        protected BouncyPrivateKey(final GordianAsymKeyType pKeyType) {
            super(pKeyType);
        }
    }

    /**
     * Bouncy RSA PublicKey.
     */
    public static class BouncyRSAPublicKey
            extends BouncyPublicKey {
        /**
         * Public Key details.
         */
        private final RSAKeyParameters theKey;

        /**
         * Constructor.
         * @param pPublicKey the public key
         */
        protected BouncyRSAPublicKey(final RSAKeyParameters pPublicKey) {
            super(GordianAsymKeyType.RSA);
            theKey = pPublicKey;
        }

        /**
         * Obtain the public key.
         * @return the key
         */
        protected RSAKeyParameters getPublicKey() {
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
            if (!(pThat instanceof BouncyRSAPublicKey)) {
                return false;
            }

            /* Access the target field */
            BouncyRSAPublicKey myThat = (BouncyRSAPublicKey) pThat;

            /* Check differences */
            return compareKeys(theKey, myThat.getPublicKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeyType().hashCode()
                   + theKey.hashCode();
        }

        /**
         * CompareKeys
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final RSAKeyParameters pFirst,
                                           final RSAKeyParameters pSecond) {
            return pFirst.getExponent().equals(pSecond.getExponent())
                   && pFirst.getModulus().equals(pSecond.getModulus());
        }
    }

    /**
     * Bouncy RSA PrivateKey.
     */
    public static class BouncyRSAPrivateKey
            extends BouncyPrivateKey {
        /**
         * Private Key details.
         */
        private final RSAPrivateCrtKeyParameters theKey;

        /**
         * Constructor.
         * @param pPrivateKey the private key
         */
        protected BouncyRSAPrivateKey(final RSAPrivateCrtKeyParameters pPrivateKey) {
            super(GordianAsymKeyType.RSA);
            theKey = pPrivateKey;
        }

        /**
         * Obtain the private key.
         * @return the key
         */
        protected RSAPrivateCrtKeyParameters getPrivateKey() {
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
            if (!(pThat instanceof BouncyRSAPrivateKey)) {
                return false;
            }

            /* Access the target field */
            BouncyRSAPrivateKey myThat = (BouncyRSAPrivateKey) pThat;

            /* Check differences */
            return compareKeys(theKey, myThat.getPrivateKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeyType().hashCode()
                   + theKey.hashCode();
        }

        /**
         * CompareKeys
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final RSAPrivateCrtKeyParameters pFirst,
                                           final RSAPrivateCrtKeyParameters pSecond) {
            if (!pFirst.getExponent().equals(pSecond.getExponent())
                || !pFirst.getModulus().equals(pSecond.getModulus())) {
                return false;
            }

            if (!pFirst.getP().equals(pSecond.getP())
                || !pFirst.getQ().equals(pSecond.getQ())) {
                return false;
            }

            if (!pFirst.getDP().equals(pSecond.getDP())
                || !pFirst.getDQ().equals(pSecond.getDQ())) {
                return false;
            }

            return pFirst.getPublicExponent().equals(pSecond.getPublicExponent())
                   && pFirst.getQInv().equals(pSecond.getQInv());
        }
    }

    /**
     * Bouncy Elliptic PublicKey.
     */
    public static class BouncyECPublicKey
            extends BouncyPublicKey {
        /**
         * Public Key details.
         */
        private final ECPublicKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeyType the key type
         * @param pPublicKey the public key
         */
        protected BouncyECPublicKey(final GordianAsymKeyType pKeyType,
                                    final ECPublicKeyParameters pPublicKey) {
            super(pKeyType);
            theKey = pPublicKey;
        }

        /**
         * Obtain the public key.
         * @return the key
         */
        protected ECPublicKeyParameters getPublicKey() {
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
            if (!(pThat instanceof BouncyECPublicKey)) {
                return false;
            }

            /* Access the target field */
            BouncyECPublicKey myThat = (BouncyECPublicKey) pThat;

            /* Check differences */
            return getKeyType().equals(myThat.getKeyType())
                   && compareKeys(theKey, myThat.getPublicKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeyType().hashCode()
                   + theKey.hashCode();
        }

        /**
         * CompareKeys
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final ECPublicKeyParameters pFirst,
                                           final ECPublicKeyParameters pSecond) {
            return pFirst.getQ().equals(pSecond.getQ());
        }
    }

    /**
     * Bouncy Elliptic PrivateKey.
     */
    public static class BouncyECPrivateKey
            extends BouncyPrivateKey {
        /**
         * Private Key details.
         */
        private final ECPrivateKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeyType the key type
         * @param pPrivateKey the private key
         */
        protected BouncyECPrivateKey(final GordianAsymKeyType pKeyType,
                                     final ECPrivateKeyParameters pPrivateKey) {
            super(pKeyType);
            theKey = pPrivateKey;
        }

        /**
         * Obtain the private key.
         * @return the key
         */
        protected ECPrivateKeyParameters getPrivateKey() {
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
            if (!(pThat instanceof BouncyECPrivateKey)) {
                return false;
            }

            /* Access the target field */
            BouncyECPrivateKey myThat = (BouncyECPrivateKey) pThat;

            /* Check differences */
            return getKeyType().equals(myThat.getKeyType())
                   && compareKeys(theKey, myThat.getPrivateKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeyType().hashCode()
                   + theKey.hashCode();
        }

        /**
         * CompareKeys
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final ECPrivateKeyParameters pFirst,
                                           final ECPrivateKeyParameters pSecond) {
            return pFirst.getD().equals(pSecond.getD());
        }
    }
}
