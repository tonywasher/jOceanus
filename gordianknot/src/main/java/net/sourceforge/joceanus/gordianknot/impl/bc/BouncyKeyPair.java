/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianStateAwareKeyPair;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianCoreKeyPair;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianPrivateKey;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianPrivateKey.GordianStateAwarePrivateKey;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianPublicKey;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

import java.util.Objects;

/**
 * BouncyCastle Asymmetric KeyPair.
 */
public class BouncyKeyPair
        extends GordianCoreKeyPair {
    /**
     * Constructor.
     * @param pPublic the public key
     */
    protected BouncyKeyPair(final BouncyPublicKey<?> pPublic) {
        this(pPublic, null);
    }

    /**
     * Constructor.
     * @param pPublic the public key
     * @param pPrivate the private key
     */
    protected BouncyKeyPair(final BouncyPublicKey<?> pPublic,
                            final BouncyPrivateKey<?> pPrivate) {
        super(pPublic, pPrivate);
    }

    @Override
    public BouncyPublicKey<?> getPublicKey() {
        return (BouncyPublicKey<?>) super.getPublicKey();
    }

    @Override
    public BouncyPrivateKey<?> getPrivateKey() {
        return (BouncyPrivateKey<?>) super.getPrivateKey();
    }

    @Override
    public BouncyKeyPair getPublicOnly() {
        return new BouncyKeyPair(getPublicKey());
    }

    /**
     * Check for bouncyKeyPair.
     * @param pKeyPair the keyPair to check
     * @throws GordianException on error
     */
    public static void checkKeyPair(final GordianKeyPair pKeyPair) throws GordianException {
        /* Check that it is a BouncyKeyPair */
        if (!(pKeyPair instanceof BouncyKeyPair)) {
            /* Reject keyPair */
            throw new GordianDataException("Invalid KeyPair");
        }
    }

    /**
     * Check for bouncyKeyPair.
     * @param pKeyPair the keyPair to check
     * @param pSpec the required keySpec
     * @throws GordianException on error
     */
    public static void checkKeyPair(final GordianKeyPair pKeyPair,
                                    final GordianKeyPairSpec pSpec) throws GordianException {
        /* Check the keyPair */
        checkKeyPair(pKeyPair);

        /* Check that it the correct key type */
        if (!pSpec.equals(pKeyPair.getKeyPairSpec())) {
            /* Reject keyPair */
            throw new GordianDataException("Invalid KeyPairType");
        }
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
        protected BouncyPublicKey(final GordianKeyPairSpec pKeySpec,
                                  final T pKey) {
            super(pKeySpec);
            theKey = pKey;
        }

        /**
         * Obtain the public key.
         * @return the key
         */
        public T getPublicKey() {
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
            return Objects.hash(getKeySpec().hashCode(), theKey);
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
        protected BouncyPrivateKey(final GordianKeyPairSpec pKeySpec,
                                   final T pKey) {
            super(pKeySpec);
            theKey = pKey;
        }

        /**
         * Obtain the private key.
         * @return the key
         */
        public T getPrivateKey() {
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
            return Objects.hash(getKeySpec(), theKey);
        }
    }

    /**
     * Bouncy StateAware PrivateKey.
     * @param <T> parameter type
     */
    public abstract static class BouncyStateAwarePrivateKey<T extends AsymmetricKeyParameter>
            extends BouncyPrivateKey<T>
            implements GordianStateAwarePrivateKey {
        /**
         * The private key.
         */
        private final T thePrivateKey;

        /**
         * Constructor.
         * @param pKeySpec the key spec
         * @param pKey the key
         */
        protected BouncyStateAwarePrivateKey(final GordianKeyPairSpec pKeySpec,
                                             final T pKey) {
            super(pKeySpec, pKey);
            thePrivateKey = pKey;
        }

        @Override
        public T getPrivateKey() {
            return thePrivateKey;
        }

        @Override
        public abstract BouncyStateAwarePrivateKey<T> getKeyShard(int pNumUsages);

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
            if (!(pThat instanceof BouncyStateAwarePrivateKey)) {
                return false;
            }

            /* Access the target field */
            final BouncyStateAwarePrivateKey<?> myThat = (BouncyStateAwarePrivateKey<?>) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                    && matchKey(myThat.getPrivateKey());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getKeySpec(), thePrivateKey);
        }
    }

    /**
     * Bouncy StateAware KeyPair.
      */
    public static class BouncyStateAwareKeyPair
            extends BouncyKeyPair
            implements GordianStateAwareKeyPair {
        /**
         * Constructor.
         * @param pPublic the public key
         * @param pPrivate the private key
         */
        BouncyStateAwareKeyPair(final BouncyPublicKey<?> pPublic,
                                final BouncyStateAwarePrivateKey<?> pPrivate) {
            super(pPublic, pPrivate);
        }

        @Override
        public BouncyStateAwarePrivateKey<?> getPrivateKey() {
            return (BouncyStateAwarePrivateKey<?>) super.getPrivateKey();
        }

        @Override
        public long getUsagesRemaining() {
            return getPrivateKey().getUsagesRemaining();
        }

        @Override
        public BouncyStateAwareKeyPair getKeyPairShard(final int pNumUsages) {
            return new BouncyStateAwareKeyPair(getPublicKey(), getPrivateKey().getKeyShard(pNumUsages));
        }
    }
}
