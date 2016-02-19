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
package net.sourceforge.joceanus.jgordianknot.crypto.jca;

import java.security.PrivateKey;
import java.security.PublicKey;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianPublicKey;

/**
 * BouncyCastle Asymmetric KeyPair.
 */
public class JcaKeyPair
        extends GordianKeyPair {
    /**
     * Constructor.
     * @param pPublic the public key
     * @param pPrivate the private key
     */
    protected JcaKeyPair(final JcaPublicKey pPublic,
                         final JcaPrivateKey pPrivate) {
        super(pPublic, pPrivate);
    }

    @Override
    public JcaPublicKey getPublicKey() {
        return (JcaPublicKey) super.getPublicKey();
    }

    @Override
    public JcaPrivateKey getPrivateKey() {
        return (JcaPrivateKey) super.getPrivateKey();
    }

    /**
     * Jca PublicKey.
     */
    public static class JcaPublicKey
            extends GordianPublicKey {
        /**
         * Public Key details.
         */
        private final PublicKey theKey;

        /**
         * Constructor.
         * @param pKeyType the key type
         * @param pPublicKey the public key
         */
        protected JcaPublicKey(final GordianAsymKeyType pKeyType,
                               final PublicKey pPublicKey) {
            super(pKeyType);
            theKey = pPublicKey;
        }

        /**
         * Obtain the public key.
         * @return the key
         */
        protected PublicKey getPublicKey() {
            return theKey;
        }
    }

    /**
     * Jca PrivateKey.
     */
    public static class JcaPrivateKey
            extends GordianPrivateKey {
        /**
         * Private Key details.
         */
        private final PrivateKey theKey;

        /**
         * Constructor.
         * @param pKeyType the key type
         * @param pPrivateKey the private key
         */
        protected JcaPrivateKey(final GordianAsymKeyType pKeyType,
                                final PrivateKey pPrivateKey) {
            super(pKeyType);
            theKey = pPrivateKey;
        }

        /**
         * Obtain the private key.
         * @return the key
         */
        protected PrivateKey getPrivateKey() {
            return theKey;
        }
    }
}
