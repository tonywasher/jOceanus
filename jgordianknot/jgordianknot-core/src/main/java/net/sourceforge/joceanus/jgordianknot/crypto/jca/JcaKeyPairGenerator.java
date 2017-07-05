/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
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

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.spec.DHParameterSpec;

import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.pqc.jcajce.spec.SPHINCS256KeyGenParameterSpec;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianModulus;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSPHINCSKeyType;
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
     * @param pKeySpec the keySpec
     */
    protected JcaKeyPairGenerator(final JcaFactory pFactory,
                                  final GordianAsymKeySpec pKeySpec) {
        super(pFactory, pKeySpec);
    }

    /**
     * Set the key factory.
     * @param pFactory the keyFactory
     */
    protected void setKeyFactory(final KeyFactory pFactory) {
        theFactory = pFactory;
    }

    @Override
    protected PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
        try {
            JcaPrivateKey myPrivateKey = JcaPrivateKey.class.cast(getPrivateKey(pKeyPair));
            return theFactory.getKeySpec(myPrivateKey.getPrivateKey(), PKCS8EncodedKeySpec.class);
        } catch (InvalidKeySpecException e) {
            throw new GordianCryptoException("Failed to generate encoding", e);
        }
    }

    @Override
    public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
        try {
            JcaPublicKey myPublicKey = JcaPublicKey.class.cast(getPublicKey(pKeyPair));
            return theFactory.getKeySpec(myPublicKey.getPublicKey(), X509EncodedKeySpec.class);
        } catch (InvalidKeySpecException e) {
            throw new GordianCryptoException("Failed to generate encoding", e);
        }
    }

    @Override
    protected JcaKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                       final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
        try {
            JcaPrivateKey myPrivate = new JcaPrivateKey(getKeySpec(), theFactory.generatePrivate(pPrivateKey));
            JcaPublicKey myPublic = derivePublicKey(pPublicKey);
            return new JcaKeyPair(myPublic, myPrivate);
        } catch (InvalidKeySpecException e) {
            throw new GordianCryptoException("Failed to parse encoding", e);
        }
    }

    @Override
    public JcaKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pPublicKey) throws OceanusException {
        JcaPublicKey myPublic = derivePublicKey(pPublicKey);
        return new JcaKeyPair(myPublic);
    }

    /**
     * Derive the public key.
     * @param pEncodedKey the encoded public key
     * @return the public key
     * @throws OceanusException on error
     */
    private JcaPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
        try {
            return new JcaPublicKey(getKeySpec(), theFactory.generatePublic(pEncodedKey));
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
         * Generator.
         */
        private final KeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws OceanusException on error
         */
        protected JcaRSAKeyPairGenerator(final JcaFactory pFactory,
                                         final GordianAsymKeySpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = JcaFactory.getJavaKeyPairGenerator(RSA_ALGO, false);
            theGenerator.initialize(pKeySpec.getModulus().getModulus(), getRandom());

            /* Create the factory */
            setKeyFactory(JcaFactory.getJavaKeyFactory(RSA_ALGO, false));
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            KeyPair myPair = theGenerator.generateKeyPair();
            JcaPublicKey myPublic = new JcaPublicKey(getKeySpec(), myPair.getPublic());
            JcaPrivateKey myPrivate = new JcaPrivateKey(getKeySpec(), myPair.getPrivate());
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
         * @param pKeySpec the keySpec
         * @throws OceanusException on error
         */
        protected JcaECKeyPairGenerator(final JcaFactory pFactory,
                                        final GordianAsymKeySpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create and initialise the generator */
                theGenerator = JcaFactory.getJavaKeyPairGenerator(EC_ALGO, false);
                ECGenParameterSpec myParms = new ECGenParameterSpec(pKeySpec.getElliptic().getCurveName());
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(JcaFactory.getJavaKeyFactory(EC_ALGO, false));
            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create ECgenerator", e);
            }
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            KeyPair myPair = theGenerator.generateKeyPair();
            JcaPublicKey myPublic = new JcaPublicKey(getKeySpec(), myPair.getPublic());
            JcaPrivateKey myPrivate = new JcaPrivateKey(getKeySpec(), myPair.getPrivate());
            return new JcaKeyPair(myPublic, myPrivate);
        }
    }

    /**
     * Jca DSA KeyPair generator.
     */
    public static class JcaDSAKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * DSA algorithm.
         */
        private static final String DSA_ALGO = "DSA";

        /**
         * Generator.
         */
        private final KeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws OceanusException on error
         */
        protected JcaDSAKeyPairGenerator(final JcaFactory pFactory,
                                         final GordianAsymKeySpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = JcaFactory.getJavaKeyPairGenerator(DSA_ALGO, false);
            theGenerator.initialize(GordianModulus.MOD1024.getModulus(), getRandom());

            /* Create the factory */
            setKeyFactory(JcaFactory.getJavaKeyFactory(DSA_ALGO, false));
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            KeyPair myPair = theGenerator.generateKeyPair();
            JcaPublicKey myPublic = new JcaPublicKey(getKeySpec(), myPair.getPublic());
            JcaPrivateKey myPrivate = new JcaPrivateKey(getKeySpec(), myPair.getPrivate());
            return new JcaKeyPair(myPublic, myPrivate);
        }
    }

    /**
     * Jca DiffieHellman KeyPair generator.
     */
    public static class JcaDiffieHellmanKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * DH algorithm.
         */
        private static final String DH_ALGO = "DH";

        /**
         * Generator.
         */
        private final KeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws OceanusException on error
         */
        protected JcaDiffieHellmanKeyPairGenerator(final JcaFactory pFactory,
                                                   final GordianAsymKeySpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create the parameter generator */
                GordianModulus myModulus = pKeySpec.getModulus();
                DHParameters myParms = myModulus.getDHParameters();
                DHParameterSpec mySpec = new DHParameterSpec(myParms.getP(), myParms.getG());

                /* Create and initialise the generator */
                theGenerator = JcaFactory.getJavaKeyPairGenerator(DH_ALGO, false);
                theGenerator.initialize(mySpec, getRandom());

                /* Create the factory */
                setKeyFactory(JcaFactory.getJavaKeyFactory(DH_ALGO, false));
            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create DHgenerator", e);
            }
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            KeyPair myPair = theGenerator.generateKeyPair();
            JcaPublicKey myPublic = new JcaPublicKey(getKeySpec(), myPair.getPublic());
            JcaPrivateKey myPrivate = new JcaPrivateKey(getKeySpec(), myPair.getPrivate());
            return new JcaKeyPair(myPublic, myPrivate);
        }
    }

    /**
     * Jca SPHINCS KeyPair generator.
     */
    public static class JcaSPHINCSKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * SPHINCS algorithm.
         */
        private static final String SPHINCS_ALGO = "SPHINCS256";

        /**
         * Generator.
         */
        private final KeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws OceanusException on error
         */
        protected JcaSPHINCSKeyPairGenerator(final JcaFactory pFactory,
                                             final GordianAsymKeySpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Determine the type */
                String myType = GordianSPHINCSKeyType.SHA3.equals(pKeySpec.getSPHINCSType())
                                                                                             ? SPHINCS256KeyGenParameterSpec.SHA3_256
                                                                                             : SPHINCS256KeyGenParameterSpec.SHA512_256;

                /* Create and initialise the generator */
                theGenerator = JcaFactory.getJavaKeyPairGenerator(SPHINCS_ALGO, true);
                SPHINCS256KeyGenParameterSpec myParms = new SPHINCS256KeyGenParameterSpec(myType);
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(JcaFactory.getJavaKeyFactory(SPHINCS_ALGO, true));
            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create SPHINCSgenerator", e);
            }
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            KeyPair myPair = theGenerator.generateKeyPair();
            JcaPublicKey myPublic = new JcaPublicKey(getKeySpec(), myPair.getPublic());
            JcaPrivateKey myPrivate = new JcaPrivateKey(getKeySpec(), myPair.getPrivate());
            return new JcaKeyPair(myPublic, myPrivate);
        }
    }

    /**
     * Jca Rainbow KeyPair generator.
     */
    public static class JcaRainbowKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * NewHope algorithm.
         */
        private static final String RAINBOW_ALGO = "Rainbow";

        /**
         * Generator.
         */
        private final KeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws OceanusException on error
         */
        protected JcaRainbowKeyPairGenerator(final JcaFactory pFactory,
                                             final GordianAsymKeySpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = JcaFactory.getJavaKeyPairGenerator(RAINBOW_ALGO, true);
            theGenerator.initialize(GordianModulus.MOD1024.getModulus(), getRandom());

            /* Create the factory */
            setKeyFactory(JcaFactory.getJavaKeyFactory(RAINBOW_ALGO, true));
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            KeyPair myPair = theGenerator.generateKeyPair();
            JcaPublicKey myPublic = new JcaPublicKey(getKeySpec(), myPair.getPublic());
            JcaPrivateKey myPrivate = new JcaPrivateKey(getKeySpec(), myPair.getPrivate());
            return new JcaKeyPair(myPublic, myPrivate);
        }
    }

    /**
     * Jca McEliece KeyPair generator.
     */
    public static class JcaMcElieceKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * NewHope algorithm.
         */
        private static final String MCELIECE_ALGO = "McEliece-CCA2";

        /**
         * Generator.
         */
        private final KeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws OceanusException on error
         */
        protected JcaMcElieceKeyPairGenerator(final JcaFactory pFactory,
                                              final GordianAsymKeySpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = JcaFactory.getJavaKeyPairGenerator(MCELIECE_ALGO, true);

            /*
             * Note that we should create McEliece parameters and initialise using them, but that
             * call is not yet supported and is a No-OP leading to a NullPointer exception when we
             * try to generate the keyPair. Note also that obtaining PKCS8 and X509 encoding also
             * leads to NullPointer exceptions, since this is also not yet supported.
             */
            theGenerator.initialize(GordianModulus.MOD1024.getModulus(), getRandom());

            /* Create the factory */
            setKeyFactory(JcaFactory.getJavaKeyFactory(MCELIECE_ALGO, true));
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            KeyPair myPair = theGenerator.generateKeyPair();
            JcaPublicKey myPublic = new JcaPublicKey(getKeySpec(), myPair.getPublic());
            JcaPrivateKey myPrivate = new JcaPrivateKey(getKeySpec(), myPair.getPrivate());
            return new JcaKeyPair(myPublic, myPrivate);
        }
    }

    /**
     * Jca NewHope KeyPair generator.
     */
    public static class JcaNewHopeKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * NewHope algorithm.
         */
        private static final String NEWHOPE_ALGO = "NH";

        /**
         * Generator.
         */
        private final KeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws OceanusException on error
         */
        protected JcaNewHopeKeyPairGenerator(final JcaFactory pFactory,
                                             final GordianAsymKeySpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = JcaFactory.getJavaKeyPairGenerator(NEWHOPE_ALGO, true);
            theGenerator.initialize(GordianModulus.MOD1024.getModulus(), getRandom());

            /* Create the factory */
            setKeyFactory(JcaFactory.getJavaKeyFactory(NEWHOPE_ALGO, true));
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            KeyPair myPair = theGenerator.generateKeyPair();
            JcaPublicKey myPublic = new JcaPublicKey(getKeySpec(), myPair.getPublic());
            JcaPrivateKey myPrivate = new JcaPrivateKey(getKeySpec(), myPair.getPrivate());
            return new JcaKeyPair(myPublic, myPrivate);
        }
    }
}
