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

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.ECGenParameterSpec;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPair.JcaPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPair.JcaPublicKey;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Jca KeyPair generator.
 */
public abstract class JcaKeyPairGenerator
        extends GordianKeyPairGenerator {
    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pKeyType the keyType
     */
    protected JcaKeyPairGenerator(final JcaFactory pFactory,
                                  final GordianAsymKeyType pKeyType) {
        super(pFactory, pKeyType);
    }

    /**
     * Jca RSA KeyPair generator.
     */
    public static class JcaRSAKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * RSA strength.
         */
        private static final int RSA_STRENGTH = 2048;

        /**
         * Generator.
         */
        private KeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @throws OceanusException on error
         */
        protected JcaRSAKeyPairGenerator(final JcaFactory pFactory) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, GordianAsymKeyType.RSA);

            /* Create and initialise the generator */
            try {
                theGenerator = KeyPairGenerator.getInstance("RSA");
                theGenerator.initialize(RSA_STRENGTH, getRandom());
            } catch (NoSuchAlgorithmException e) {
                throw new GordianCryptoException("Failed to create RSAgenerator", e);
            }
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            KeyPair myPair = theGenerator.generateKeyPair();
            JcaPublicKey myPublic = new JcaPublicKey(GordianAsymKeyType.RSA, myPair.getPublic());
            JcaPrivateKey myPrivate = new JcaPrivateKey(GordianAsymKeyType.RSA, myPair.getPrivate());
            return new JcaKeyPair(myPublic, myPrivate);
        }
    }

    /**
     * Jca Elliptic KeyPair generator.
     */
    public static class JcaECKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * Generator.
         */
        private KeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeyType the keyType
         * @throws OceanusException on error
         */
        protected JcaECKeyPairGenerator(final JcaFactory pFactory,
                                        final GordianAsymKeyType pKeyType) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeyType);

            /* Create and initialise the generator */
            try {
                theGenerator = KeyPairGenerator.getInstance("EC");
                ECGenParameterSpec myParms = new ECGenParameterSpec(pKeyType.getCurve());
                theGenerator.initialize(myParms, getRandom());
            } catch (NoSuchAlgorithmException
                    | InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create ECgenerator", e);
            }
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            KeyPair myPair = theGenerator.generateKeyPair();
            JcaPublicKey myPublic = new JcaPublicKey(getKeyType(), myPair.getPublic());
            JcaPrivateKey myPrivate = new JcaPrivateKey(getKeyType(), myPair.getPrivate());
            return new JcaKeyPair(myPublic, myPrivate);
        }
    }
}
