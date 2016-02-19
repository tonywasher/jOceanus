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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jgordianknot/jgordianknot-core/src/main/java/net/sourceforge/joceanus/jgordianknot/crypto/CipherSetRecipe.java $
 * $Revision: 647 $
 * $Author: Tony $
 * $Date: 2015-11-04 08:58:02 +0000 (Wed, 04 Nov 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto.bc;

import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeyType;
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
    }
}
