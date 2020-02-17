/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.jcajce.spec.DHDomainParameterSpec;
import org.bouncycastle.jcajce.spec.EdDSAParameterSpec;
import org.bouncycastle.jcajce.spec.XDHParameterSpec;
import org.bouncycastle.pqc.crypto.lms.LMSParameters;
import org.bouncycastle.pqc.jcajce.spec.LMSParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.McElieceCCA2KeyGenParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.McElieceKeyGenParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.QTESLAParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.SPHINCS256KeyGenParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.XMSSMTParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.XMSSParameterSpec;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianDHGroup;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianDSAKeyType;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianLMSKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianMcElieceKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianMcElieceKeySpec.GordianMcElieceDigestType;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianRSAModulus;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianSPHINCSKeyType;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianXMSSKeyType;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCoreKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaKeyPair.JcaPrivateKey;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaKeyPair.JcaPublicKey;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaKeyPair.JcaStateAwareKeyPair;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaKeyPair.JcaStateAwarePrivateKey;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Jca KeyPair generator.
 */
public abstract class JcaKeyPairGenerator
        extends GordianCoreKeyPairGenerator {
    /**
     * Parse error.
     */
    private static final String PARSE_ERROR = "Failed to parse encoding";
    /**
     * Factory.
     */
    private KeyFactory theFactory;

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pKeySpec the keySpec
     */
    JcaKeyPairGenerator(final JcaFactory pFactory,
                        final GordianAsymKeySpec pKeySpec) {
        super(pFactory, pKeySpec);
    }

    /**
     * Obtain Set the key factory.
     * @return the keyFactory
     */
    KeyFactory getKeyFactory() {
        return theFactory;
    }

    /**
     * Set the key factory.
     * @param pFactory the keyFactory
     */
    void setKeyFactory(final KeyFactory pFactory) {
        theFactory = pFactory;
    }

    @Override
    public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
        final JcaPrivateKey myPrivateKey = (JcaPrivateKey) getPrivateKey(pKeyPair);
        return new PKCS8EncodedKeySpec(myPrivateKey.getPrivateKey().getEncoded());
    }

    @Override
    public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
        final JcaPublicKey myPublicKey = (JcaPublicKey) getPublicKey(pKeyPair);
        return new X509EncodedKeySpec(myPublicKey.getPublicKey().getEncoded());
    }

    @Override
    public JcaKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                    final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
        try {
            final JcaPublicKey myPublic = derivePublicKey(pPublicKey);
            checkKeySpec(pPrivateKey);
            final JcaPrivateKey myPrivate = new JcaPrivateKey(getKeySpec(), theFactory.generatePrivate(pPrivateKey));
            return new JcaKeyPair(myPublic, myPrivate);
        } catch (InvalidKeySpecException e) {
            throw new GordianCryptoException(PARSE_ERROR, e);
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
    protected JcaPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws OceanusException {
        try {
            checkKeySpec(pEncodedKey);
            return new JcaPublicKey(getKeySpec(), theFactory.generatePublic(pEncodedKey));
        } catch (InvalidKeySpecException e) {
            throw new GordianCryptoException(PARSE_ERROR, e);
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
        JcaRSAKeyPairGenerator(final JcaFactory pFactory,
                               final GordianAsymKeySpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = JcaAsymFactory.getJavaKeyPairGenerator(RSA_ALGO, false);
            theGenerator.initialize(pKeySpec.getModulus().getLength(), getRandom());

            /* Create the factory */
            setKeyFactory(JcaAsymFactory.getJavaKeyFactory(RSA_ALGO, false));
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
        JcaECKeyPairGenerator(final JcaFactory pFactory,
                              final GordianAsymKeySpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create and initialise the generator */
                final String myAlgo = getAlgorithm();
                theGenerator = JcaAsymFactory.getJavaKeyPairGenerator(myAlgo, false);
                final ECGenParameterSpec myParms = new ECGenParameterSpec(pKeySpec.getElliptic().getCurveName());
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(JcaAsymFactory.getJavaKeyFactory(myAlgo, false));
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
        JcaDSAKeyPairGenerator(final JcaFactory pFactory,
                               final GordianAsymKeySpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            final GordianDSAKeyType myKeyType = pKeySpec.getDSAKeyType();
            theGenerator = JcaAsymFactory.getJavaKeyPairGenerator(DSA_ALGO, false);
            theGenerator.initialize(myKeyType.getKeySize(), getRandom());

            /* Create the factory */
            setKeyFactory(JcaAsymFactory.getJavaKeyFactory(DSA_ALGO, false));
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
    public static class JcaDHKeyPairGenerator
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
        JcaDHKeyPairGenerator(final JcaFactory pFactory,
                              final GordianAsymKeySpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create the parameter generator */
                final GordianDHGroup myGroup = pKeySpec.getDHGroup();
                final DHParameters myParms = myGroup.getParameters();
                final DHDomainParameterSpec mySpec = new DHDomainParameterSpec(myParms);

                /* Create and initialise the generator */
                theGenerator = JcaAsymFactory.getJavaKeyPairGenerator(DH_ALGO, false);
                theGenerator.initialize(mySpec, getRandom());

                /* Create the factory */
                setKeyFactory(JcaAsymFactory.getJavaKeyFactory(DH_ALGO, false));
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
        JcaSPHINCSKeyPairGenerator(final JcaFactory pFactory,
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
                theGenerator = JcaAsymFactory.getJavaKeyPairGenerator(SPHINCS_ALGO, true);
                final SPHINCS256KeyGenParameterSpec myParms = new SPHINCS256KeyGenParameterSpec(myType);
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(JcaAsymFactory.getJavaKeyFactory(SPHINCS_ALGO, true));
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
        JcaRainbowKeyPairGenerator(final JcaFactory pFactory,
                                   final GordianAsymKeySpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = JcaAsymFactory.getJavaKeyPairGenerator(RAINBOW_ALGO, true);
            theGenerator.initialize(GordianRSAModulus.MOD1024.getLength(), getRandom());

            /* Create the factory */
            setKeyFactory(JcaAsymFactory.getJavaKeyFactory(RAINBOW_ALGO, true));
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
        JcaMcElieceKeyPairGenerator(final JcaFactory pFactory,
                                    final GordianAsymKeySpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            final GordianMcElieceKeySpec myKeyType = pKeySpec.getMcElieceSpec();
            final String myAlgo = myKeyType.isCCA2()
                                  ? MCELIECECCA2_ALGO
                                  : MCELIECE_ALGO;
            theGenerator = JcaAsymFactory.getJavaKeyPairGenerator(myAlgo, true);

            /* Protect against exceptions */
            try {
                if (myKeyType.isCCA2()) {
                    final GordianMcElieceDigestType myDigestType = myKeyType.getDigestType();
                    theGenerator.initialize(new McElieceCCA2KeyGenParameterSpec(myDigestType.getM(), McElieceCCA2KeyGenParameterSpec.DEFAULT_T,
                            myDigestType.getParameter()), getRandom());
                } else {
                    theGenerator.initialize(new McElieceKeyGenParameterSpec(), getRandom());
                }
            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to initialise generator", e);
            }

            /* Create the factory */
            setKeyFactory(JcaAsymFactory.getJavaKeyFactory(myAlgo, true));
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
        JcaNewHopeKeyPairGenerator(final JcaFactory pFactory,
                                   final GordianAsymKeySpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = JcaAsymFactory.getJavaKeyPairGenerator(NEWHOPE_ALGO, true);
            theGenerator.initialize(GordianRSAModulus.MOD1024.getLength(), getRandom());

            /* Create the factory */
            setKeyFactory(JcaAsymFactory.getJavaKeyFactory(NEWHOPE_ALGO, true));
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
        JcaXMSSKeyPairGenerator(final JcaFactory pFactory,
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
                theGenerator = JcaAsymFactory.getJavaKeyPairGenerator(myJavaType, true);
                theGenerator.initialize(myAlgo, getRandom());

                /* Create the factory */
                setKeyFactory(JcaAsymFactory.getJavaKeyFactory(myJavaType, true));
            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create XMSSgenerator", e);
            }
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            final KeyPair myPair = theGenerator.generateKeyPair();
            final JcaPublicKey myPublic = new JcaPublicKey(getKeySpec(), myPair.getPublic());
            final JcaStateAwarePrivateKey myPrivate = new JcaStateAwarePrivateKey(getKeySpec(), myPair.getPrivate());
            return new JcaStateAwareKeyPair(myPublic, myPrivate);
        }

        @Override
        public JcaKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                        final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            try {
                final JcaPublicKey myPublic = derivePublicKey(pPublicKey);
                final JcaStateAwarePrivateKey myPrivate = new JcaStateAwarePrivateKey(getKeySpec(), getKeyFactory().generatePrivate(pPrivateKey));
                return new JcaStateAwareKeyPair(myPublic, myPrivate);
            } catch (InvalidKeySpecException e) {
                throw new GordianCryptoException(PARSE_ERROR, e);
            }
        }
    }

    /**
     * Jca Edwards KeyPair generator.
     */
    public static class JcaEdKeyPairGenerator
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
        protected JcaEdKeyPairGenerator(final JcaFactory pFactory,
                                        final GordianAsymKeySpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create the parameters */
                final AlgorithmParameterSpec myAlgo;
                switch (pKeySpec.getKeyType()) {
                    case X25519:
                        myAlgo = new XDHParameterSpec(XDHParameterSpec.X25519);
                        break;
                    case X448:
                        myAlgo = new XDHParameterSpec(XDHParameterSpec.X448);
                        break;
                    case ED25519:
                        myAlgo = new EdDSAParameterSpec(EdDSAParameterSpec.Ed25519);
                        break;
                    case ED448:
                        myAlgo = new EdDSAParameterSpec(EdDSAParameterSpec.Ed448);
                        break;
                    default:
                        throw new GordianLogicException("Invalid KeySpec" + pKeySpec);
                }

                /* Create and initialise the generator */
                final String myJavaType = pKeySpec.getKeyType().toString();
                theGenerator = JcaAsymFactory.getJavaKeyPairGenerator(myJavaType, false);
                theGenerator.initialize(myAlgo, getRandom());

                /* Create the factory */
                setKeyFactory(JcaAsymFactory.getJavaKeyFactory(myJavaType, false));
            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create EdwardsGenerator", e);
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
     * Jca QTESLA KeyPair generator.
     */
    public static class JcaQTESLAKeyPairGenerator
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
        JcaQTESLAKeyPairGenerator(final JcaFactory pFactory,
                                  final GordianAsymKeySpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create the parameters */
                final String myCategory;
                switch (pKeySpec.getQTESLAKeyType()) {
                    case PROVABLY_SECURE_I:
                        myCategory = QTESLAParameterSpec.PROVABLY_SECURE_I;
                        break;
                    case PROVABLY_SECURE_III:
                        myCategory = QTESLAParameterSpec.PROVABLY_SECURE_III;
                        break;
                    default:
                        throw new GordianLogicException("Invalid KeySpec" + pKeySpec);
                }

                /* Create and initialise the generator */
                final String myJavaType = pKeySpec.getKeyType().toString();
                theGenerator = JcaAsymFactory.getJavaKeyPairGenerator(myJavaType, true);
                final AlgorithmParameterSpec myAlgo = new QTESLAParameterSpec(myCategory);
                theGenerator.initialize(myAlgo, getRandom());

                /* Create the factory */
                setKeyFactory(JcaAsymFactory.getJavaKeyFactory(myJavaType, true));
            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create qTESLAgenerator", e);
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
     * Jca LMS KeyPair generator.
     */
    public static class JcaLMSKeyPairGenerator
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
        JcaLMSKeyPairGenerator(final JcaFactory pFactory,
                               final GordianAsymKeySpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            final String myJavaType = pKeySpec.getKeyType().toString();
            theGenerator = JcaAsymFactory.getJavaKeyPairGenerator(myJavaType, true);

            /* Protect against exceptions */
            try {
                final GordianLMSKeySpec myKeySpec = pKeySpec.getLMSSpec();
                final LMSParameters myParms = myKeySpec.getParameters();
                final LMSParameterSpec mySpec = new LMSParameterSpec(myParms.getLmsParam(), myParms.getLmOTSParam());
                theGenerator.initialize(mySpec, getRandom());
             } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to initialise generator", e);
            }

            /* Create the factory */
            setKeyFactory(JcaAsymFactory.getJavaKeyFactory(myJavaType, true));
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
