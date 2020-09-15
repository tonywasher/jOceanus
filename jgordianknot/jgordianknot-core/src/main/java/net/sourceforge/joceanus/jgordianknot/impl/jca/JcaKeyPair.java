/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.jca;

import java.security.PrivateKey;
import java.security.PublicKey;

import org.bouncycastle.pqc.jcajce.interfaces.LMSPrivateKey;
import org.bouncycastle.pqc.jcajce.interfaces.XMSSMTPrivateKey;
import org.bouncycastle.pqc.jcajce.interfaces.XMSSPrivateKey;

import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianStateAwareKeyPair;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCoreKeyPair;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianPrivateKey;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianPublicKey;

/**
 * BouncyCastle Asymmetric KeyPair.
 */
public class JcaKeyPair
        extends GordianCoreKeyPair {
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

    @Override
    public JcaKeyPair getPublicOnly() {
        return new JcaKeyPair(getPublicKey());
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
         * @param pKeySpec the keySpec
         * @param pPublicKey the public key
         */
        protected JcaPublicKey(final GordianKeyPairSpec pKeySpec,
                               final PublicKey pPublicKey) {
            super(pKeySpec);
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
            final JcaPublicKey myThat = (JcaPublicKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                    && theKey.equals(myThat.getPublicKey());
        }

        @Override
        public int hashCode() {
            return GordianCoreFactory.HASH_PRIME * getKeySpec().hashCode()
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
         * @param pKeySpec the keySpec
         * @param pPrivateKey the private key
         */
        protected JcaPrivateKey(final GordianKeyPairSpec pKeySpec,
                                final PrivateKey pPrivateKey) {
            super(pKeySpec);
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
            final JcaPrivateKey myThat = (JcaPrivateKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                    && theKey.equals(myThat.getPrivateKey());
        }

        @Override
        public int hashCode() {
            return GordianCoreFactory.HASH_PRIME * getKeySpec().hashCode()
                    + theKey.hashCode();
        }
    }

    /**
     * Bouncy StateAware PrivateKey.
     */
    public static class JcaStateAwarePrivateKey
            extends JcaPrivateKey {
        /**
         * The private key.
         */
        private PrivateKey thePrivateKey;

        /**
         * Constructor.
         * @param pKeySpec the key spec
         * @param pKey the key
         */
        JcaStateAwarePrivateKey(final GordianKeyPairSpec pKeySpec,
                                final PrivateKey pKey) {
            super(pKeySpec, pKey);
            thePrivateKey = pKey;
        }

        @Override
        public PrivateKey getPrivateKey() {
            return thePrivateKey;
        }

        /**
         * Obtain number of signatures remaining.
         * @return the number of signatures remaining
         */
        public long getUsagesRemaining() {
            if (thePrivateKey instanceof LMSPrivateKey) {
                return ((LMSPrivateKey) getPrivateKey()).getUsagesRemaining();
            }
            if (thePrivateKey instanceof XMSSMTPrivateKey) {
                return ((XMSSMTPrivateKey) getPrivateKey()).getUsagesRemaining();
            }
            return thePrivateKey instanceof XMSSPrivateKey
                    ? ((XMSSPrivateKey) getPrivateKey()).getUsagesRemaining()
                    : 0;
        }

        /**
         * Obtain a keyShard from the number of usages.
         * @param pNumUsages the number of usage for the shard
         * @return the keyShard
         */
        public JcaStateAwarePrivateKey getKeyShard(final int pNumUsages) {
            if (thePrivateKey instanceof LMSPrivateKey) {
                return new JcaStateAwarePrivateKey(getKeySpec(), ((LMSPrivateKey) getPrivateKey()).extractKeyShard(pNumUsages));
            }
            if (thePrivateKey instanceof XMSSMTPrivateKey) {
                return new JcaStateAwarePrivateKey(getKeySpec(), ((XMSSMTPrivateKey) getPrivateKey()).extractKeyShard(pNumUsages));
            }
            return thePrivateKey instanceof XMSSPrivateKey
                   ? new JcaStateAwarePrivateKey(getKeySpec(), ((XMSSPrivateKey) getPrivateKey()).extractKeyShard(pNumUsages))
                   : null;
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
            if (!(pThat instanceof JcaStateAwarePrivateKey)) {
                return false;
            }

            /* Access the target field */
            final JcaStateAwarePrivateKey myThat = (JcaStateAwarePrivateKey) pThat;

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                    && thePrivateKey.equals(myThat.getPrivateKey());
        }

        @Override
        public int hashCode() {
            return GordianCoreFactory.HASH_PRIME * getKeySpec().hashCode()
                    + thePrivateKey.hashCode();
        }
    }

    /**
     * Jca StateAware KeyPair.
     */
    public static class JcaStateAwareKeyPair
            extends JcaKeyPair
            implements GordianStateAwareKeyPair {
        /**
         * Constructor.
         * @param pPublic the public key
         * @param pPrivate the private key
         */
        JcaStateAwareKeyPair(final JcaPublicKey pPublic,
                             final JcaStateAwarePrivateKey pPrivate) {
            super(pPublic, pPrivate);
        }

        @Override
        public JcaStateAwarePrivateKey getPrivateKey() {
            return (JcaStateAwarePrivateKey) super.getPrivateKey();
        }

        @Override
        public long getUsagesRemaining() {
            return getPrivateKey().getUsagesRemaining();
        }

        @Override
        public JcaStateAwareKeyPair getKeyPairShard(final int pNumUsages) {
            return new JcaStateAwareKeyPair(getPublicKey(), getPrivateKey().getKeyShard(pNumUsages));
        }
    }
}
