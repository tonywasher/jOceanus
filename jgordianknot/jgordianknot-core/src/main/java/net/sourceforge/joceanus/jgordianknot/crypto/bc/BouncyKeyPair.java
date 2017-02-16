/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2016 Tony Washer
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

import java.util.Arrays;

import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2PrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2PublicKeyParameters;
import org.bouncycastle.pqc.crypto.newhope.NHPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPublicKeyParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPublicKeyParameters;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
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
    public abstract static class BouncyPublicKey
            extends GordianPublicKey {
        /**
         * Constructor.
         * @param pKeySpec the key spec
         */
        protected BouncyPublicKey(final GordianAsymKeySpec pKeySpec) {
            super(pKeySpec);
        }
    }

    /**
     * Bouncy PrivateKey.
     */
    public abstract static class BouncyPrivateKey
            extends GordianPrivateKey {
        /**
         * Constructor.
         * @param pKeySpec the key spec
         */
        protected BouncyPrivateKey(final GordianAsymKeySpec pKeySpec) {
            super(pKeySpec);
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
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        protected BouncyRSAPublicKey(final GordianAsymKeySpec pKeySpec,
                                     final RSAKeyParameters pPublicKey) {
            super(pKeySpec);
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
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(theKey, myThat.getPublicKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
        }

        /**
         * Is the private key valid for this public key?
         * @param pPrivate the private key
         * @return true/false
         */
        public boolean validPrivate(final BouncyRSAPrivateKey pPrivate) {
            RSAPrivateCrtKeyParameters myPrivate = pPrivate.getPrivateKey();
            return theKey.getExponent().equals(myPrivate.getExponent())
                   && theKey.getModulus().equals(myPrivate.getModulus());

        }

        /**
         * CompareKeys.
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
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        protected BouncyRSAPrivateKey(final GordianAsymKeySpec pKeySpec,
                                      final RSAPrivateCrtKeyParameters pPrivateKey) {
            super(pKeySpec);
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
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(theKey, myThat.getPrivateKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
        }

        /**
         * CompareKeys.
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
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        protected BouncyECPublicKey(final GordianAsymKeySpec pKeySpec,
                                    final ECPublicKeyParameters pPublicKey) {
            super(pKeySpec);
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
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(theKey, myThat.getPublicKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
        }

        /**
         * Is the private key valid for this public key?
         * @param pPrivate the private key
         * @return true/false
         */
        public boolean validPrivate(final BouncyECPrivateKey pPrivate) {
            ECPrivateKeyParameters myPrivate = pPrivate.getPrivateKey();
            return theKey.getParameters().equals(myPrivate.getParameters());
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final ECPublicKeyParameters pFirst,
                                           final ECPublicKeyParameters pSecond) {
            return pFirst.getQ().equals(pSecond.getQ())
                   && pFirst.getParameters().equals(pSecond.getParameters());
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
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        protected BouncyECPrivateKey(final GordianAsymKeySpec pKeySpec,
                                     final ECPrivateKeyParameters pPrivateKey) {
            super(pKeySpec);
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
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(theKey, myThat.getPrivateKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final ECPrivateKeyParameters pFirst,
                                           final ECPrivateKeyParameters pSecond) {
            return pFirst.getD().equals(pSecond.getD())
                   && pFirst.getParameters().equals(pSecond.getParameters());
        }
    }

    /**
     * Bouncy ElGamal PublicKey.
     */
    public static class BouncyElGamalPublicKey
            extends BouncyPublicKey {
        /**
         * Public Key details.
         */
        private final ElGamalPublicKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        protected BouncyElGamalPublicKey(final GordianAsymKeySpec pKeySpec,
                                         final ElGamalPublicKeyParameters pPublicKey) {
            super(pKeySpec);
            theKey = pPublicKey;
        }

        /**
         * Obtain the public key.
         * @return the key
         */
        protected ElGamalPublicKeyParameters getPublicKey() {
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
            if (!(pThat instanceof BouncyElGamalPublicKey)) {
                return false;
            }

            /* Access the target field */
            BouncyElGamalPublicKey myThat = (BouncyElGamalPublicKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(theKey, myThat.getPublicKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
        }

        /**
         * Is the private key valid for this public key?
         * @param pPrivate the private key
         * @return true/false
         */
        public boolean validPrivate(final BouncyElGamalPrivateKey pPrivate) {
            ElGamalPrivateKeyParameters myPrivate = pPrivate.getPrivateKey();
            return theKey.getParameters().equals(myPrivate.getParameters());
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final ElGamalPublicKeyParameters pFirst,
                                           final ElGamalPublicKeyParameters pSecond) {
            return pFirst.getY().equals(pSecond.getY())
                   && pFirst.getParameters().equals(pSecond.getParameters());
        }
    }

    /**
     * Bouncy ElGamal PrivateKey.
     */
    public static class BouncyElGamalPrivateKey
            extends BouncyPrivateKey {
        /**
         * Private Key details.
         */
        private final ElGamalPrivateKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        protected BouncyElGamalPrivateKey(final GordianAsymKeySpec pKeySpec,
                                          final ElGamalPrivateKeyParameters pPrivateKey) {
            super(pKeySpec);
            theKey = pPrivateKey;
        }

        /**
         * Obtain the private key.
         * @return the key
         */
        protected ElGamalPrivateKeyParameters getPrivateKey() {
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
            if (!(pThat instanceof BouncyElGamalPrivateKey)) {
                return false;
            }

            /* Access the target field */
            BouncyElGamalPrivateKey myThat = (BouncyElGamalPrivateKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(theKey, myThat.getPrivateKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final ElGamalPrivateKeyParameters pFirst,
                                           final ElGamalPrivateKeyParameters pSecond) {
            return pFirst.getX().equals(pSecond.getX())
                   && pFirst.getParameters().equals(pSecond.getParameters());
        }
    }

    /**
     * Bouncy DiffieHellman PublicKey.
     */
    public static class BouncyDiffieHellmanPublicKey
            extends BouncyPublicKey {
        /**
         * Public Key details.
         */
        private final DHPublicKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        protected BouncyDiffieHellmanPublicKey(final GordianAsymKeySpec pKeySpec,
                                               final DHPublicKeyParameters pPublicKey) {
            super(pKeySpec);
            theKey = pPublicKey;
        }

        /**
         * Obtain the public key.
         * @return the key
         */
        protected DHPublicKeyParameters getPublicKey() {
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
            if (!(pThat instanceof BouncyDiffieHellmanPublicKey)) {
                return false;
            }

            /* Access the target field */
            BouncyDiffieHellmanPublicKey myThat = (BouncyDiffieHellmanPublicKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(theKey, myThat.getPublicKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
        }

        /**
         * Is the private key valid for this public key?
         * @param pPrivate the private key
         * @return true/false
         */
        public boolean validPrivate(final BouncyDiffieHellmanPrivateKey pPrivate) {
            DHPrivateKeyParameters myPrivate = pPrivate.getPrivateKey();
            return theKey.getParameters().equals(myPrivate.getParameters());
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
     * Bouncy DiffieHellman PrivateKey.
     */
    public static class BouncyDiffieHellmanPrivateKey
            extends BouncyPrivateKey {
        /**
         * Private Key details.
         */
        private final DHPrivateKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        protected BouncyDiffieHellmanPrivateKey(final GordianAsymKeySpec pKeySpec,
                                                final DHPrivateKeyParameters pPrivateKey) {
            super(pKeySpec);
            theKey = pPrivateKey;
        }

        /**
         * Obtain the private key.
         * @return the key
         */
        protected DHPrivateKeyParameters getPrivateKey() {
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
            if (!(pThat instanceof BouncyDiffieHellmanPrivateKey)) {
                return false;
            }

            /* Access the target field */
            BouncyDiffieHellmanPrivateKey myThat = (BouncyDiffieHellmanPrivateKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(theKey, myThat.getPrivateKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
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
     * Bouncy SPHINCS PublicKey.
     */
    public static class BouncySPHINCSPublicKey
            extends BouncyPublicKey {
        /**
         * Public Key details.
         */
        private final SPHINCSPublicKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        protected BouncySPHINCSPublicKey(final GordianAsymKeySpec pKeySpec,
                                         final SPHINCSPublicKeyParameters pPublicKey) {
            super(pKeySpec);
            theKey = pPublicKey;
        }

        /**
         * Obtain the public key.
         * @return the key
         */
        protected SPHINCSPublicKeyParameters getPublicKey() {
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
            if (!(pThat instanceof BouncySPHINCSPublicKey)) {
                return false;
            }

            /* Access the target field */
            BouncySPHINCSPublicKey myThat = (BouncySPHINCSPublicKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(theKey, myThat.getPublicKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final SPHINCSPublicKeyParameters pFirst,
                                           final SPHINCSPublicKeyParameters pSecond) {
            return Arrays.equals(pFirst.getKeyData(), pSecond.getKeyData());
        }
    }

    /**
     * Bouncy SPHINCS PrivateKey.
     */
    public static class BouncySPHINCSPrivateKey
            extends BouncyPrivateKey {
        /**
         * Private Key details.
         */
        private final SPHINCSPrivateKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        protected BouncySPHINCSPrivateKey(final GordianAsymKeySpec pKeySpec,
                                          final SPHINCSPrivateKeyParameters pPrivateKey) {
            super(pKeySpec);
            theKey = pPrivateKey;
        }

        /**
         * Obtain the private key.
         * @return the key
         */
        protected SPHINCSPrivateKeyParameters getPrivateKey() {
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
            if (!(pThat instanceof BouncySPHINCSPrivateKey)) {
                return false;
            }

            /* Access the target field */
            BouncySPHINCSPrivateKey myThat = (BouncySPHINCSPrivateKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(theKey, myThat.getPrivateKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final SPHINCSPrivateKeyParameters pFirst,
                                           final SPHINCSPrivateKeyParameters pSecond) {
            return Arrays.equals(pFirst.getKeyData(), pSecond.getKeyData());
        }
    }

    /**
     * Bouncy NewHope PublicKey.
     */
    public static class BouncyNewHopePublicKey
            extends BouncyPublicKey {
        /**
         * Public Key details.
         */
        private final NHPublicKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        protected BouncyNewHopePublicKey(final GordianAsymKeySpec pKeySpec,
                                         final NHPublicKeyParameters pPublicKey) {
            super(pKeySpec);
            theKey = pPublicKey;
        }

        /**
         * Obtain the public key.
         * @return the key
         */
        protected NHPublicKeyParameters getPublicKey() {
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
            if (!(pThat instanceof BouncyNewHopePublicKey)) {
                return false;
            }

            /* Access the target field */
            BouncyNewHopePublicKey myThat = (BouncyNewHopePublicKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(theKey, myThat.getPublicKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final NHPublicKeyParameters pFirst,
                                           final NHPublicKeyParameters pSecond) {
            return Arrays.equals(pFirst.getPubData(), pSecond.getPubData());
        }
    }

    /**
     * Bouncy NewHope PrivateKey.
     */
    public static class BouncyNewHopePrivateKey
            extends BouncyPrivateKey {
        /**
         * Private Key details.
         */
        private final NHPrivateKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        protected BouncyNewHopePrivateKey(final GordianAsymKeySpec pKeySpec,
                                          final NHPrivateKeyParameters pPrivateKey) {
            super(pKeySpec);
            theKey = pPrivateKey;
        }

        /**
         * Obtain the private key.
         * @return the key
         */
        protected NHPrivateKeyParameters getPrivateKey() {
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
            if (!(pThat instanceof BouncyNewHopePrivateKey)) {
                return false;
            }

            /* Access the target field */
            BouncyNewHopePrivateKey myThat = (BouncyNewHopePrivateKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(theKey, myThat.getPrivateKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final NHPrivateKeyParameters pFirst,
                                           final NHPrivateKeyParameters pSecond) {
            return Arrays.equals(pFirst.getSecData(), pSecond.getSecData());
        }
    }

    /**
     * Bouncy Rainbow PublicKey.
     */
    public static class BouncyRainbowPublicKey
            extends BouncyPublicKey {
        /**
         * Public Key details.
         */
        private final RainbowPublicKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        protected BouncyRainbowPublicKey(final GordianAsymKeySpec pKeySpec,
                                         final RainbowPublicKeyParameters pPublicKey) {
            super(pKeySpec);
            theKey = pPublicKey;
        }

        /**
         * Obtain the public key.
         * @return the key
         */
        protected RainbowPublicKeyParameters getPublicKey() {
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
            if (!(pThat instanceof BouncyRainbowPublicKey)) {
                return false;
            }

            /* Access the target field */
            BouncyRainbowPublicKey myThat = (BouncyRainbowPublicKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(theKey, myThat.getPublicKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final RainbowPublicKeyParameters pFirst,
                                           final RainbowPublicKeyParameters pSecond) {
            return Arrays.equals(pFirst.getCoeffScalar(), pSecond.getCoeffScalar())
                   && Arrays.deepEquals(pFirst.getCoeffSingular(), pSecond.getCoeffSingular())
                   && Arrays.deepEquals(pFirst.getCoeffQuadratic(), pSecond.getCoeffQuadratic());
        }
    }

    /**
     * Bouncy Rainbow PrivateKey.
     */
    public static class BouncyRainbowPrivateKey
            extends BouncyPrivateKey {
        /**
         * Private Key details.
         */
        private final RainbowPrivateKeyParameters theKey;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        protected BouncyRainbowPrivateKey(final GordianAsymKeySpec pKeySpec,
                                          final RainbowPrivateKeyParameters pPrivateKey) {
            super(pKeySpec);
            theKey = pPrivateKey;
        }

        /**
         * Obtain the private key.
         * @return the key
         */
        protected RainbowPrivateKeyParameters getPrivateKey() {
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
            if (!(pThat instanceof BouncyRainbowPrivateKey)) {
                return false;
            }

            /* Access the target field */
            BouncyRainbowPrivateKey myThat = (BouncyRainbowPrivateKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(theKey, myThat.getPrivateKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
        }

        /**
         * CompareKeys.
         * @param pFirst the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final RainbowPrivateKeyParameters pFirst,
                                           final RainbowPrivateKeyParameters pSecond) {
            if (!Arrays.equals(pFirst.getB1(), pSecond.getB1())
                || !Arrays.equals(pFirst.getB2(), pSecond.getB2())) {
                return false;
            }
            if (!Arrays.deepEquals(pFirst.getInvA1(), pSecond.getInvA1())
                || !Arrays.deepEquals(pFirst.getInvA2(), pSecond.getInvA2())) {
                return false;
            }
            return Arrays.equals(pFirst.getVi(), pSecond.getVi())
                   && Arrays.equals(pFirst.getLayers(), pSecond.getLayers());
        }
    }

    /**
     * Bouncy McEliece PublicKey.
     */
    public static class BouncyMcEliecePublicKey
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
        protected BouncyMcEliecePublicKey(final GordianAsymKeySpec pKeySpec,
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
            if (!(pThat instanceof BouncyMcEliecePublicKey)) {
                return false;
            }

            /* Access the target field */
            BouncyMcEliecePublicKey myThat = (BouncyMcEliecePublicKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(theKey, myThat.getPublicKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
        }

        /**
         * Is the private key valid for this public key?
         * @param pPrivate the private key
         * @return true/false
         */
        public boolean validPrivate(final BouncyMcEliecePrivateKey pPrivate) {
            McElieceCCA2PrivateKeyParameters myPrivate = pPrivate.getPrivateKey();
            return theKey.getN() == myPrivate.getN();
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
     * Bouncy McEliece PrivateKey.
     */
    public static class BouncyMcEliecePrivateKey
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
        protected BouncyMcEliecePrivateKey(final GordianAsymKeySpec pKeySpec,
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
            if (!(pThat instanceof BouncyMcEliecePrivateKey)) {
                return false;
            }

            /* Access the target field */
            BouncyMcEliecePrivateKey myThat = (BouncyMcEliecePrivateKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                   && compareKeys(theKey, myThat.getPrivateKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                   + theKey.hashCode();
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
}
