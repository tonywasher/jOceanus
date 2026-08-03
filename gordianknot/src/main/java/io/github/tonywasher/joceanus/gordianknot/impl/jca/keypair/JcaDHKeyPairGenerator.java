/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreDHSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair.JcaPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair.JcaPublicKey;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPublicKey;
import org.bouncycastle.jcajce.spec.DHDomainParameterSpec;

import javax.crypto.spec.DHParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Objects;

/**
 * Jca DiffieHellman KeyPair generator.
 */
public class JcaDHKeyPairGenerator
        extends JcaKeyPairGenerator {
    /**
     * DH algorithm.
     */
    private static final String DH_ALGO = "DH";

    /**
     * Constructor.
     *
     * @param pFactory the Security Factory
     * @param pKeySpec the keySpec
     * @throws GordianException on error
     */
    JcaDHKeyPairGenerator(final GordianBaseFactory pFactory,
                          final GordianKeyPairSpec pKeySpec) throws GordianException {
        /* initialize underlying class */
        super(pFactory, pKeySpec);

        /* Protect against exceptions */
        try {
            /* Create the parameter generator */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final GordianCoreDHSpec myGroup = myKeySpec.getDHSpec();
            final DHParameters myParms = myGroup.getParameters();
            final DHDomainParameterSpec mySpec = new DHDomainParameterSpec(myParms);

            /* Create and initialize the generator */
            createFactories(DH_ALGO, false);
            getGenerator().initialize(mySpec, getRandom());

        } catch (InvalidAlgorithmParameterException e) {
            throw new GordianCryptoException("Failed to create DHgenerator", e);
        }
    }

    @Override
    protected JcaPrivateKey createPrivate(final PrivateKey pPrivateKey) {
        return new JcaDHPrivateKey(getKeySpec(), (BCDHPrivateKey) pPrivateKey);
    }

    @Override
    protected JcaPublicKey createPublic(final PublicKey pPublicKey) {
        return new JcaDHPublicKey(getKeySpec(), (BCDHPublicKey) pPublicKey);
    }

    /**
     * Jca DH PublicKey.
     */
    public static class JcaDHPublicKey
            extends JcaPublicKey {
        /**
         * Public Key details.
         */
        private final BCDHPublicKey theKey;

        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        protected JcaDHPublicKey(final GordianKeyPairSpec pKeySpec,
                                 final BCDHPublicKey pPublicKey) {
            super(pKeySpec, pPublicKey);
            theKey = pPublicKey;
        }

        @Override
        public BCDHPublicKey getPublicKey() {
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
            if (!(pThat instanceof JcaDHPublicKey myThat)) {
                return false;
            }

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                    && theKey.getY().equals(myThat.getPublicKey().getY())
                    && dhParamsAreEqual(theKey.getParams(), myThat.getPublicKey().getParams());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getKeySpec(), theKey);
        }
    }

    /**
     * check DH Parameters are equal (ignoring L!!).
     *
     * @param pFirst  the first parameters
     * @param pSecond the second parameters
     * @return true/false
     */
    private static boolean dhParamsAreEqual(final DHParameterSpec pFirst,
                                            final DHParameterSpec pSecond) {
        final DHDomainParameterSpec myFirst = (DHDomainParameterSpec) pFirst;
        final DHDomainParameterSpec mySecond = (DHDomainParameterSpec) pSecond;
        return myFirst.getP().equals(mySecond.getP())
                && myFirst.getG().equals(mySecond.getG())
                && myFirst.getQ().equals(mySecond.getQ());
    }

    /**
     * Jca DH PrivateKey.
     */
    public static class JcaDHPrivateKey
            extends JcaPrivateKey {
        /**
         * The private key.
         */
        private final BCDHPrivateKey thePrivateKey;

        /**
         * Constructor.
         *
         * @param pKeySpec the key spec
         * @param pKey     the key
         */
        JcaDHPrivateKey(final GordianKeyPairSpec pKeySpec,
                        final BCDHPrivateKey pKey) {
            super(pKeySpec, pKey);
            thePrivateKey = pKey;
        }

        @Override
        public BCDHPrivateKey getPrivateKey() {
            return thePrivateKey;
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
            if (!(pThat instanceof JcaDHPrivateKey myThat)) {
                return false;
            }

            /* Check differences */
            return getKeySpec().equals(myThat.getKeySpec())
                    && thePrivateKey.getX().equals(myThat.getPrivateKey().getX())
                    && dhParamsAreEqual(thePrivateKey.getParams(), myThat.getPrivateKey().getParams());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getKeySpec(), thePrivateKey);
        }
    }
}
