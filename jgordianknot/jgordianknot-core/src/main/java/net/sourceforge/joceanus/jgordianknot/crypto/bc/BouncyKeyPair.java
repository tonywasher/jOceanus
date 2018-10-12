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

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

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
     * @param <T> parameter type
     */
    public abstract static class BouncyPublicKey<T extends AsymmetricKeyParameter>
            extends GordianPublicKey {
        /**
         * Public Key details.
         */
        private final T theKey;

        /**
         * Constructor.
         * @param pKeySpec the key spec
         * @param pKey the key
         */
        protected BouncyPublicKey(final GordianAsymKeySpec pKeySpec,
                                  final T pKey) {
            super(pKeySpec);
            theKey = pKey;
        }

        /**
         * Obtain the public key.
         * @return the key
         */
        protected T getPublicKey() {
            return theKey;
        }

        /**
         * matchKey.
         * @param pThat the key to match
         * @return true/false
         */
        protected abstract boolean matchKey(AsymmetricKeyParameter pThat);

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
            if (!(pThat instanceof BouncyPublicKey)) {
                return false;
            }

            /* Access the target field */
            final BouncyPublicKey<?> myThat = (BouncyPublicKey<?>) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                    && matchKey(myThat.getPublicKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                    + theKey.hashCode();
        }
    }

    /**
     * Bouncy PrivateKey.
     * @param <T> parameter type
     */
    public abstract static class BouncyPrivateKey<T extends AsymmetricKeyParameter>
            extends GordianPrivateKey {
        /**
         * Private Key details.
         */
        private final T theKey;

        /**
         * Constructor.
         * @param pKeySpec the key spec
         * @param pKey the key
         */
        protected BouncyPrivateKey(final GordianAsymKeySpec pKeySpec,
                                   final T pKey) {
            super(pKeySpec);
            theKey = pKey;
        }

        /**
         * Obtain the private key.
         * @return the key
         */
        protected T getPrivateKey() {
            return theKey;
        }

        /**
         * matchKey.
         * @param pThat the key to match
         * @return true/false
         */
        protected abstract boolean matchKey(AsymmetricKeyParameter pThat);

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
            if (!(pThat instanceof BouncyPrivateKey)) {
                return false;
            }

            /* Access the target field */
            final BouncyPrivateKey<?> myThat = (BouncyPrivateKey<?>) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                    && matchKey(myThat.getPrivateKey());
        }

        @Override
        public int hashCode() {
            return GordianFactory.HASH_PRIME * getKeySpec().hashCode()
                    + theKey.hashCode();
        }
    }
}
