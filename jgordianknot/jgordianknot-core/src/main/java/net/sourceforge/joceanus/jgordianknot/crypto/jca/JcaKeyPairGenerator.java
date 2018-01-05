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
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.spec.DHParameterSpec;

import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.pqc.jcajce.spec.McElieceKeyGenParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.SPHINCS256KeyGenParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.XMSSMTParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.XMSSParameterSpec;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDSAKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMcElieceKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianModulus;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSPHINCSKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianXMSSKeyType;
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
            final JcaPrivateKey myPrivateKey = JcaPrivateKey.class.cast(getPrivateKey(pKeyPair));
            return theFactory.getKeySpec(myPrivateKey.getPrivateKey(), PKCS8EncodedKeySpec.class);
        } catch (InvalidKeySpecException e) {
            throw new GordianCryptoException("Failed to generate encoding", e);
        }
    }

    @Override
    public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
        try {
            final JcaPublicKey myPublicKey = JcaPublicKey.class.cast(getPublicKey(pKeyPair));
            return theFactory.getKeySpec(myPublicKey.getPublicKey(), X509EncodedKeySpec.class);
        } catch (InvalidKeySpecException e) {
            throw new GordianCryptoException("Failed to generate encoding", e);
        }
    }

    @Override
    protected JcaKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                       final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
        try {
            final JcaPrivateKey myPrivate = new JcaPrivateKey(getKeySpec(), theFactory.generatePrivate(pPrivateKey));
            final JcaPublicKey myPublic = derivePublicKey(pPublicKey);
            return new JcaKeyPair(myPublic, myPrivate);
        } catch (InvalidKeySpecException e) {
            throw new GordianCryptoException("Failed to parse encoding", e);
        }
    }

    @Override
    public JcaKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pPublicKey) throws OceanusException {
        final JcaPublicKey myPublic = derivePublicKey(pPublicKey);
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
            final KeyPair myPair = theGenerator.generateKeyPair();
            final JcaPublicKey myPublic = new JcaPublicKey(getKeySpec(), myPair.getPublic());
            final JcaPrivateKey myPrivate = new JcaPrivateKey(getKeySpec(), myPair.getPrivate());
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
                final String myAlgo = getAlgorithm();
                theGenerator = JcaFactory.getJavaKeyPairGenerator(myAlgo, false);
                final ECGenParameterSpec myParms = new ECGenParameterSpec(pKeySpec.getElliptic().getCurveName());
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(JcaFactory.getJavaKeyFactory(myAlgo, false));
            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create ECgenerator for:  " + pKeySpec, e);
            }
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            final KeyPair myPair = theGenerator.generateKeyPair();
            final JcaPublicKey myPublic = new JcaPublicKey(getKeySpec(), myPair.getPublic());
            final JcaPrivateKey myPrivate = new JcaPrivateKey(getKeySpec(), myPair.getPrivate());
            return new JcaKeyPair(myPublic, myPrivate);
        }

        /**
         * Obtain algorithm for keySpec.
         * @return the algorithm
         */
        private String getAlgorithm() {
            switch (this.getKeySpec().getKeyType()) {
                case DSTU4145:
                    return "DSTU4145";
                case GOST2012:
                    return "ECGOST3410-2012";
                default:
                    return "EC";
            }
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
            final GordianDSAKeyType myKeyType = pKeySpec.getDSAKeyType();
            theGenerator = JcaFactory.getJavaKeyPairGenerator(DSA_ALGO, false);
            theGenerator.initialize(myKeyType.getKeySize(), getRandom());

            /* Create the factory */
            setKeyFactory(JcaFactory.getJavaKeyFactory(DSA_ALGO, false));
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            final KeyPair myPair = theGenerator.generateKeyPair();
            final JcaPublicKey myPublic = new JcaPublicKey(getKeySpec(), myPair.getPublic());
            final JcaPrivateKey myPrivate = new JcaPrivateKey(getKeySpec(), myPair.getPrivate());
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
                final GordianModulus myModulus = pKeySpec.getModulus();
                final DHParameters myParms = myModulus.getDHParameters();
                final DHParameterSpec mySpec = new DHParameterSpec(myParms.getP(), myParms.getG());

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
            final KeyPair myPair = theGenerator.generateKeyPair();
            final JcaPublicKey myPublic = new JcaPublicKey(getKeySpec(), myPair.getPublic());
            final JcaPrivateKey myPrivate = new JcaPrivateKey(getKeySpec(), myPair.getPrivate());
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
                final String myType = GordianSPHINCSKeyType.SHA3.equals(pKeySpec.getSPHINCSType())
                                                                                                   ? SPHINCS256KeyGenParameterSpec.SHA3_256
                                                                                                   : SPHINCS256KeyGenParameterSpec.SHA512_256;

                /* Create and initialise the generator */
                theGenerator = JcaFactory.getJavaKeyPairGenerator(SPHINCS_ALGO, true);
                final SPHINCS256KeyGenParameterSpec myParms = new SPHINCS256KeyGenParameterSpec(myType);
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(JcaFactory.getJavaKeyFactory(SPHINCS_ALGO, true));
            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create SPHINCSgenerator", e);
            }
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            final KeyPair myPair = theGenerator.generateKeyPair();
            final JcaPublicKey myPublic = new JcaPublicKey(getKeySpec(), myPair.getPublic());
            final JcaPrivateKey myPrivate = new JcaPrivateKey(getKeySpec(), myPair.getPrivate());
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
            final KeyPair myPair = theGenerator.generateKeyPair();
            final JcaPublicKey myPublic = new JcaPublicKey(getKeySpec(), myPair.getPublic());
            final JcaPrivateKey myPrivate = new JcaPrivateKey(getKeySpec(), myPair.getPrivate());
            return new JcaKeyPair(myPublic, myPrivate);
        }
    }

    /**
     * Jca McEliece KeyPair generator.
     */
    public static class JcaMcElieceKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * McEliece algorithm.
         */
        private static final String MCELIECE_ALGO = "McEliece";

        /**
         * McEliece-CCA2 algorithm.
         */
        private static final String MCELIECECCA2_ALGO = "McEliece-CCA2";

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
            final GordianMcElieceKeySpec myKeyType = pKeySpec.getMcElieceSpec();
            theGenerator = JcaFactory.getJavaKeyPairGenerator(myKeyType.isCCA2()
                                                                                 ? MCELIECECCA2_ALGO
                                                                                 : MCELIECE_ALGO, true);

            /*
             * Note that we should create McEliece parameters and initialise using them, but that
             * call is not yet supported and is a No-OP leading to a NullPointer exception when we
             * try to generate the keyPair. Note also that obtaining PKCS8 and X509 encoding also
             * leads to NullPointer exceptions, since this is also not yet supported.
             */
            theGenerator.initialize(McElieceKeyGenParameterSpec.DEFAULT_M, getRandom());

            /* Create the factory */
            setKeyFactory(JcaFactory.getJavaKeyFactory(MCELIECE_ALGO, true));
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            final KeyPair myPair = theGenerator.generateKeyPair();
            final JcaPublicKey myPublic = new JcaPublicKey(getKeySpec(), myPair.getPublic());
            final JcaPrivateKey myPrivate = new JcaPrivateKey(getKeySpec(), myPair.getPrivate());
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
            final KeyPair myPair = theGenerator.generateKeyPair();
            final JcaPublicKey myPublic = new JcaPublicKey(getKeySpec(), myPair.getPublic());
            final JcaPrivateKey myPrivate = new JcaPrivateKey(getKeySpec(), myPair.getPrivate());
            return new JcaKeyPair(myPublic, myPrivate);
        }
    }

    /**
     * Jca XMSS KeyPair generator.
     */
    public static class JcaXMSSKeyPairGenerator
            extends JcaKeyPairGenerator {
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
        protected JcaXMSSKeyPairGenerator(final JcaFactory pFactory,
                                          final GordianAsymKeySpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Access the algorithm */
                final boolean isXMSSMT = GordianAsymKeyType.XMSSMT.equals(pKeySpec.getKeyType());
                final GordianXMSSKeyType myType = pKeySpec.getXMSSKeyType();

                /* Create the parameters */
                final AlgorithmParameterSpec myAlgo = isXMSSMT
                                                               ? new XMSSMTParameterSpec(GordianXMSSKeyType.DEFAULT_HEIGHT,
                                                                       GordianXMSSKeyType.DEFAULT_LAYERS, myType.name())
                                                               : new XMSSParameterSpec(GordianXMSSKeyType.DEFAULT_HEIGHT, myType.name());

                /* Create and initialise the generator */
                final String myJavaType = pKeySpec.getKeyType().toString();
                theGenerator = JcaFactory.getJavaKeyPairGenerator(myJavaType, true);
                theGenerator.initialize(myAlgo, getRandom());

                /* Create the factory */
                setKeyFactory(JcaFactory.getJavaKeyFactory(myJavaType, true));
            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create DHgenerator", e);
            }
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            final KeyPair myPair = theGenerator.generateKeyPair();
            final JcaPublicKey myPublic = new JcaPublicKey(getKeySpec(), myPair.getPublic());
            final JcaPrivateKey myPrivate = new JcaPrivateKey(getKeySpec(), myPair.getPrivate());
            return new JcaKeyPair(myPublic, myPrivate);
        }
    }
}
