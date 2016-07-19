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
package net.sourceforge.joceanus.jgordianknot.crypto.jca;

import java.security.PrivateKey;
import java.security.PublicKey;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
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
     */
    protected JcaKeyPair(final JcaPublicKey pPublic) {
        this(pPublic, null);
    }

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
            if (!(pThat instanceof JcaPublicKey)) {
                return false;
            }

            /* Access the target field */
            JcaPublicKey myThat = (JcaPublicKey) pThat;

            /* Check differences */
            return theKey.equals(myThat.getPublicKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeyType().hashCode()
                   + theKey.hashCode();
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
            if (!(pThat instanceof JcaPrivateKey)) {
                return false;
            }

            /* Access the target field */
            JcaPrivateKey myThat = (JcaPrivateKey) pThat;

            /* Check differences */
            return theKey.equals(myThat.getPrivateKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeyType().hashCode()
                   + theKey.hashCode();
        }
    }
}
