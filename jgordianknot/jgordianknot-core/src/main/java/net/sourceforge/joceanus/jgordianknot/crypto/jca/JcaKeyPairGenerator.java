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
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPair.JcaPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPair.JcaPublicKey;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Jca KeyPair generator.
 */
public abstract class JcaKeyPairGenerator
        extends GordianKeyPairGenerator {
    /**
     * Factory.
     */
    private KeyFactory theFactory;

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
     * Set the key factory.
     * @param pFactory the keyFactory
     */
    protected void setKeyFactory(final KeyFactory pFactory) {
        theFactory = pFactory;
    }

    /**
     * Extract the PKCS8 encoding for the private key
     * @param pPrivateKey the private key
     * @return the PKCS8 encoding
     * @throws OceanusException on error
     */
    @Override
    protected PKCS8EncodedKeySpec getPKCS8Encoding(final GordianPrivateKey pPrivateKey) throws OceanusException {
        try {
            JcaPrivateKey myPrivateKey = JcaPrivateKey.class.cast(pPrivateKey);
            return theFactory.getKeySpec(myPrivateKey.getPrivateKey(), PKCS8EncodedKeySpec.class);
        } catch (InvalidKeySpecException e) {
            throw new GordianCryptoException("Failed to generate encoding", e);
        }
    }

    @Override
    public X509EncodedKeySpec getX509Encoding(final GordianPublicKey pPublicKey) throws OceanusException {
        try {
            JcaPublicKey myPublicKey = JcaPublicKey.class.cast(pPublicKey);
            return theFactory.getKeySpec(myPublicKey.getPublicKey(), X509EncodedKeySpec.class);
        } catch (InvalidKeySpecException e) {
            throw new GordianCryptoException("Failed to generate encoding", e);
        }
    }

    @Override
    protected JcaPrivateKey derivePrivateKey(final PKCS8EncodedKeySpec pEncodedKey) throws OceanusException {
        try {
            return new JcaPrivateKey(getKeyType(), theFactory.generatePrivate(pEncodedKey));
        } catch (InvalidKeySpecException e) {
            throw new GordianCryptoException("Failed to parse encoding", e);
        }
    }

    @Override
    public JcaPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
        try {
            return new JcaPublicKey(getKeyType(), theFactory.generatePublic(pEncodedKey));
        } catch (InvalidKeySpecException e) {
            throw new GordianCryptoException("Failed to parse encoding", e);
        }
    }

    /**
     * Jca RSA KeyPair generator.
     */
    public static class JcaRSAKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * RSA algorithm.
         */
        private static final String RSA_ALGO = "RSA";

        /**
         * RSA strength.
         */
        private static final int RSA_STRENGTH = 2048;

        /**
         * Generator.
         */
        private final KeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @throws OceanusException on error
         */
        protected JcaRSAKeyPairGenerator(final JcaFactory pFactory) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, GordianAsymKeyType.RSA);

            /* Create and initialise the generator */
            theGenerator = JcaFactory.getJavaKeyPairGenerator(RSA_ALGO);
            theGenerator.initialize(RSA_STRENGTH, getRandom());

            /* Create the factory */
            setKeyFactory(JcaFactory.getJavaKeyFactory(RSA_ALGO));
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
         * EC algorithm.
         */
        private static final String EC_ALGO = "EC";

        /**
         * Generator.
         */
        private final KeyPairGenerator theGenerator;

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

            /* Protect against exceptions */
            try {
                /* Create and initialise the generator */
                theGenerator = JcaFactory.getJavaKeyPairGenerator(EC_ALGO);
                ECGenParameterSpec myParms = new ECGenParameterSpec(pKeyType.getCurve());
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(JcaFactory.getJavaKeyFactory(EC_ALGO));
            } catch (InvalidAlgorithmParameterException e) {
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
