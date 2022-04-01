/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2022 Tony Washer
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
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPublicKey;
import org.bouncycastle.jcajce.spec.DHDomainParameterSpec;
import org.bouncycastle.jcajce.spec.EdDSAParameterSpec;
import org.bouncycastle.jcajce.spec.XDHParameterSpec;
import org.bouncycastle.jce.spec.ElGamalParameterSpec;
import org.bouncycastle.pqc.crypto.lms.LMSParameters;
import org.bouncycastle.pqc.jcajce.spec.CMCEParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.FrodoParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.LMSHSSKeyGenParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.LMSKeyGenParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.McElieceCCA2KeyGenParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.McElieceKeyGenParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.SABERParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.SPHINCS256KeyGenParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.SPHINCSPlusParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.XMSSMTParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.XMSSParameterSpec;

import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianDHGroup;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianDSAKeyType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianLMSKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianLMSKeySpec.GordianHSSKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianMcElieceKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianMcElieceKeySpec.GordianMcElieceDigestType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianRSAModulus;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianSPHINCSDigestType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianXMSSKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianXMSSKeySpec.GordianXMSSDigestType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCoreKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianKeyPairValidity;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaKeyPair.JcaDHPrivateKey;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaKeyPair.JcaDHPublicKey;
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
                        final GordianKeyPairSpec pKeySpec) {
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
        /* Check the keyPair */
        JcaKeyPair.checkKeyPair(pKeyPair);

        /* derive the encoding */
        final JcaPrivateKey myPrivateKey = (JcaPrivateKey) getPrivateKey(pKeyPair);
        return new PKCS8EncodedKeySpec(myPrivateKey.getPrivateKey().getEncoded());
    }

    @Override
    public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Check the keyPair */
        JcaKeyPair.checkKeyPair(pKeyPair);

        /* derive the encoding */
        final JcaPublicKey myPublicKey = (JcaPublicKey) getPublicKey(pKeyPair);
        return new X509EncodedKeySpec(myPublicKey.getPublicKey().getEncoded());
    }

    @Override
    public JcaKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                    final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Check the keySpec */
            checkKeySpec(pPrivateKey);

            /* derive the keyPair */
            final JcaPublicKey myPublic = derivePublicKey(pPublicKey);
            final JcaPrivateKey myPrivate = createPrivate(theFactory.generatePrivate(pPrivateKey));
            final JcaKeyPair myPair = new JcaKeyPair(myPublic, myPrivate);

            /* Check that we have a matching pair */
            GordianKeyPairValidity.checkValidity(getFactory(), myPair);

            /* Return the keyPair */
            return myPair;

        } catch (InvalidKeySpecException e) {
            throw new GordianCryptoException(PARSE_ERROR, e);
        }
    }

    /**
     * Create private key.
     * @param pPrivateKey the private key
     * @return the private key
     */
    protected JcaPrivateKey createPrivate(final PrivateKey pPrivateKey) {
        return new JcaPrivateKey(getKeySpec(), pPrivateKey);
    }

    /**
     * Create public key.
     * @param pPublicKey the public key
     * @return the public key
     */
    protected JcaPublicKey createPublic(final PublicKey pPublicKey) {
        return new JcaPublicKey(getKeySpec(), pPublicKey);
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
        /* Protect against exceptions */
        try {
            /* Check the keySpec */
            checkKeySpec(pEncodedKey);

            /* derive the key */
            return createPublic(theFactory.generatePublic(pEncodedKey));

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
                               final GordianKeyPairSpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = JcaKeyPairFactory.getJavaKeyPairGenerator(RSA_ALGO, false);
            theGenerator.initialize(pKeySpec.getRSAModulus().getLength(), getRandom());

            /* Create the factory */
            setKeyFactory(JcaKeyPairFactory.getJavaKeyFactory(RSA_ALGO, false));
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final KeyPair myPair = theGenerator.generateKeyPair();
            final JcaPublicKey myPublic = createPublic(myPair.getPublic());
            final JcaPrivateKey myPrivate = createPrivate(myPair.getPrivate());
            return new JcaKeyPair(myPublic, myPrivate);
        }
    }

    /**
     * Jca ElGamal KeyPair generator.
     */
    public static class JcaElGamalKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * RSA algorithm.
         */
        private static final String ELGAMAL_ALGO = "ELGAMAL";

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
        JcaElGamalKeyPairGenerator(final JcaFactory pFactory,
                                   final GordianKeyPairSpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create the parameter generator */
                final GordianDHGroup myGroup = pKeySpec.getDHGroup();
                final DHParameters myParms = myGroup.getParameters();
                final ElGamalParameterSpec mySpec = new ElGamalParameterSpec(myParms.getP(), myParms.getQ());

                /* Create and initialise the generator */
                theGenerator = JcaKeyPairFactory.getJavaKeyPairGenerator(ELGAMAL_ALGO, false);
                theGenerator.initialize(mySpec, getRandom());

                /* Create the factory */
                setKeyFactory(JcaKeyPairFactory.getJavaKeyFactory(ELGAMAL_ALGO, false));

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create ElGamalGenerator", e);
            }
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final KeyPair myPair = theGenerator.generateKeyPair();
            final JcaPublicKey myPublic = createPublic(myPair.getPublic());
            final JcaPrivateKey myPrivate = createPrivate(myPair.getPrivate());
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
                              final GordianKeyPairSpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create and initialise the generator */
                final String myAlgo = getAlgorithm();
                theGenerator = JcaKeyPairFactory.getJavaKeyPairGenerator(myAlgo, false);
                final ECGenParameterSpec myParms = new ECGenParameterSpec(pKeySpec.getElliptic().getCurveName());
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(JcaKeyPairFactory.getJavaKeyFactory(myAlgo, false));

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create ECgenerator for:  " + pKeySpec, e);
            }
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final KeyPair myPair = theGenerator.generateKeyPair();
            final JcaPublicKey myPublic = createPublic(myPair.getPublic());
            final JcaPrivateKey myPrivate = createPrivate(myPair.getPrivate());
            return new JcaKeyPair(myPublic, myPrivate);
        }

        /**
         * Obtain algorithm for keySpec.
         * @return the algorithm
         */
        private String getAlgorithm() {
            switch (this.getKeySpec().getKeyPairType()) {
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
                               final GordianKeyPairSpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            final GordianDSAKeyType myKeyType = pKeySpec.getDSAKeyType();
            theGenerator = JcaKeyPairFactory.getJavaKeyPairGenerator(DSA_ALGO, false);
            theGenerator.initialize(myKeyType.getKeySize(), getRandom());

            /* Create the factory */
            setKeyFactory(JcaKeyPairFactory.getJavaKeyFactory(DSA_ALGO, false));
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final KeyPair myPair = theGenerator.generateKeyPair();
            final JcaPublicKey myPublic = createPublic(myPair.getPublic());
            final JcaPrivateKey myPrivate = createPrivate(myPair.getPrivate());
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
                              final GordianKeyPairSpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create the parameter generator */
                final GordianDHGroup myGroup = pKeySpec.getDHGroup();
                final DHParameters myParms = myGroup.getParameters();
                final DHDomainParameterSpec mySpec = new DHDomainParameterSpec(myParms);

                /* Create and initialise the generator */
                theGenerator = JcaKeyPairFactory.getJavaKeyPairGenerator(DH_ALGO, false);
                theGenerator.initialize(mySpec, getRandom());

                /* Create the factory */
                setKeyFactory(JcaKeyPairFactory.getJavaKeyFactory(DH_ALGO, false));

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create DHgenerator", e);
            }
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final KeyPair myPair = theGenerator.generateKeyPair();
            final JcaPublicKey myPublic = createPublic(myPair.getPublic());
            final JcaPrivateKey myPrivate = createPrivate(myPair.getPrivate());
            return new JcaKeyPair(myPublic, myPrivate);
        }

        @Override
        protected JcaPrivateKey createPrivate(final PrivateKey pPrivateKey) {
            return new JcaDHPrivateKey(getKeySpec(), (BCDHPrivateKey) pPrivateKey);
        }

        @Override
        protected JcaPublicKey createPublic(final PublicKey pPublicKey) {
            return new JcaDHPublicKey(getKeySpec(), (BCDHPublicKey) pPublicKey);
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
                                   final GordianKeyPairSpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Determine the type */
                final String myType = GordianSPHINCSDigestType.SHA3.equals(pKeySpec.getSPHINCSDigestType())
                                      ? SPHINCS256KeyGenParameterSpec.SHA3_256
                                      : SPHINCS256KeyGenParameterSpec.SHA512_256;

                /* Create and initialise the generator */
                theGenerator = JcaKeyPairFactory.getJavaKeyPairGenerator(SPHINCS_ALGO, true);
                final SPHINCS256KeyGenParameterSpec myParms = new SPHINCS256KeyGenParameterSpec(myType);
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(JcaKeyPairFactory.getJavaKeyFactory(SPHINCS_ALGO, true));

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create SPHINCSgenerator", e);
            }
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final KeyPair myPair = theGenerator.generateKeyPair();
            final JcaPublicKey myPublic = createPublic(myPair.getPublic());
            final JcaPrivateKey myPrivate = createPrivate(myPair.getPrivate());
            return new JcaKeyPair(myPublic, myPrivate);
        }
    }

    /**
     * Jca SPHINCSPlus KeyPair generator.
     */
    public static class JcaSPHINCSPlusKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * SPHINCS algorithm.
         */
        private static final String SPHINCS_ALGO = "SPHINCS+";

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
        JcaSPHINCSPlusKeyPairGenerator(final JcaFactory pFactory,
                                       final GordianKeyPairSpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create and initialise the generator */
                theGenerator = JcaKeyPairFactory.getJavaKeyPairGenerator(SPHINCS_ALGO, true);
                final SPHINCSPlusParameterSpec myParms = pKeySpec.getSPHINCSPlusKeySpec().getParameterSpec();
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(JcaKeyPairFactory.getJavaKeyFactory(SPHINCS_ALGO, true));

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create SPHINCS+generator", e);
            }
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final KeyPair myPair = theGenerator.generateKeyPair();
            final JcaPublicKey myPublic = createPublic(myPair.getPublic());
            final JcaPrivateKey myPrivate = createPrivate(myPair.getPrivate());
            return new JcaKeyPair(myPublic, myPrivate);
        }
    }

    /**
     * Jca CMCE KeyPair generator.
     */
    public static class JcaCMCEKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * FRODO algorithm.
         */
        private static final String CMCE_ALGO = "CMCE";

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
        JcaCMCEKeyPairGenerator(final JcaFactory pFactory,
                                final GordianKeyPairSpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create and initialise the generator */
                theGenerator = JcaKeyPairFactory.getJavaKeyPairGenerator(CMCE_ALGO, true);
                final CMCEParameterSpec myParms = pKeySpec.getCMCEKeySpec().getParameterSpec();
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(JcaKeyPairFactory.getJavaKeyFactory(CMCE_ALGO, true));

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create CMCEgenerator", e);
            }
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final KeyPair myPair = theGenerator.generateKeyPair();
            final JcaPublicKey myPublic = createPublic(myPair.getPublic());
            final JcaPrivateKey myPrivate = createPrivate(myPair.getPrivate());
            return new JcaKeyPair(myPublic, myPrivate);
        }
    }

    /**
     * Jca Frodo KeyPair generator.
     */
    public static class JcaFrodoKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * FRODO algorithm.
         */
        private static final String FRODO_ALGO = "FRODO";

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
        JcaFrodoKeyPairGenerator(final JcaFactory pFactory,
                                 final GordianKeyPairSpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create and initialise the generator */
                theGenerator = JcaKeyPairFactory.getJavaKeyPairGenerator(FRODO_ALGO, true);
                final FrodoParameterSpec myParms = pKeySpec.getFRODOKeySpec().getParameterSpec();
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(JcaKeyPairFactory.getJavaKeyFactory(FRODO_ALGO, true));

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create FRODOgenerator", e);
            }
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final KeyPair myPair = theGenerator.generateKeyPair();
            final JcaPublicKey myPublic = createPublic(myPair.getPublic());
            final JcaPrivateKey myPrivate = createPrivate(myPair.getPrivate());
            return new JcaKeyPair(myPublic, myPrivate);
        }
    }

    /**
     * Jca SABER KeyPair generator.
     */
    public static class JcaSABERKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * FRODO algorithm.
         */
        private static final String SABER_ALGO = "SABER";

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
        JcaSABERKeyPairGenerator(final JcaFactory pFactory,
                                 final GordianKeyPairSpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create and initialise the generator */
                theGenerator = JcaKeyPairFactory.getJavaKeyPairGenerator(SABER_ALGO, true);
                final SABERParameterSpec myParms = pKeySpec.getSABERKeySpec().getParameterSpec();
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(JcaKeyPairFactory.getJavaKeyFactory(SABER_ALGO, true));

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create SABERgenerator", e);
            }
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final KeyPair myPair = theGenerator.generateKeyPair();
            final JcaPublicKey myPublic = createPublic(myPair.getPublic());
            final JcaPrivateKey myPrivate = createPrivate(myPair.getPrivate());
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
                                   final GordianKeyPairSpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = JcaKeyPairFactory.getJavaKeyPairGenerator(RAINBOW_ALGO, true);
            theGenerator.initialize(GordianRSAModulus.MOD1024.getLength(), getRandom());

            /* Create the factory */
            setKeyFactory(JcaKeyPairFactory.getJavaKeyFactory(RAINBOW_ALGO, true));
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final KeyPair myPair = theGenerator.generateKeyPair();
            final JcaPublicKey myPublic = createPublic(myPair.getPublic());
            final JcaPrivateKey myPrivate = createPrivate(myPair.getPrivate());
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
                                    final GordianKeyPairSpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            final GordianMcElieceKeySpec myKeyType = pKeySpec.getMcElieceKeySpec();
            final String myAlgo = myKeyType.isCCA2()
                                  ? MCELIECECCA2_ALGO
                                  : MCELIECE_ALGO;
            theGenerator = JcaKeyPairFactory.getJavaKeyPairGenerator(myAlgo, true);

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
            setKeyFactory(JcaKeyPairFactory.getJavaKeyFactory(myAlgo, true));
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final KeyPair myPair = theGenerator.generateKeyPair();
            final JcaPublicKey myPublic = createPublic(myPair.getPublic());
            final JcaPrivateKey myPrivate = createPrivate(myPair.getPrivate());
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
                                   final GordianKeyPairSpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            theGenerator = JcaKeyPairFactory.getJavaKeyPairGenerator(NEWHOPE_ALGO, true);
            theGenerator.initialize(GordianRSAModulus.MOD1024.getLength(), getRandom());

            /* Create the factory */
            setKeyFactory(JcaKeyPairFactory.getJavaKeyFactory(NEWHOPE_ALGO, true));
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final KeyPair myPair = theGenerator.generateKeyPair();
            final JcaPublicKey myPublic = createPublic(myPair.getPublic());
            final JcaPrivateKey myPrivate = createPrivate(myPair.getPrivate());
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
                                final GordianKeyPairSpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Access the algorithm */
                final GordianXMSSKeySpec myXMSSKeySpec = pKeySpec.getXMSSKeySpec();
                final boolean isXMSSMT = myXMSSKeySpec.isMT();
                final GordianXMSSDigestType myType = myXMSSKeySpec.getDigestType();

                /* Create the parameters */
                final AlgorithmParameterSpec myAlgo = isXMSSMT
                                                      ? new XMSSMTParameterSpec(myXMSSKeySpec.getHeight().getHeight(),
                                                                                myXMSSKeySpec.getLayers().getLayers(), myType.name())
                                                      : new XMSSParameterSpec(myXMSSKeySpec.getHeight().getHeight(), myType.name());

                /* Create and initialise the generator */
                final String myJavaType = myXMSSKeySpec.getKeyType().name();
                theGenerator = JcaKeyPairFactory.getJavaKeyPairGenerator(myJavaType, true);
                theGenerator.initialize(myAlgo, getRandom());

                /* Create the factory */
                setKeyFactory(JcaKeyPairFactory.getJavaKeyFactory(myJavaType, true));

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create XMSSgenerator", e);
            }
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final KeyPair myPair = theGenerator.generateKeyPair();
            final JcaPublicKey myPublic = createPublic(myPair.getPublic());
            final JcaStateAwarePrivateKey myPrivate = createPrivate(myPair.getPrivate());
            return new JcaStateAwareKeyPair(myPublic, myPrivate);
        }

        @Override
        protected JcaStateAwarePrivateKey createPrivate(final PrivateKey pPrivateKey) {
            return new JcaStateAwarePrivateKey(getKeySpec(), pPrivateKey);
        }

        @Override
        public JcaKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                        final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pPrivateKey);

                /* derive keyPair */
                final JcaPublicKey myPublic = derivePublicKey(pPublicKey);
                JcaStateAwarePrivateKey myPrivate = createPrivate(getKeyFactory().generatePrivate(pPrivateKey));
                final JcaKeyPair myPair = new JcaStateAwareKeyPair(myPublic, myPrivate);

                /* Check that we have a matching pair */
                GordianKeyPairValidity.checkValidity(getFactory(), myPair);

                /* Rebuild and return the keyPair to avoid incrementing usage count */
                myPrivate = createPrivate(getKeyFactory().generatePrivate(pPrivateKey));
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
                                        final GordianKeyPairSpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create the parameters */
                final AlgorithmParameterSpec myAlgo;
                final boolean is25519 = pKeySpec.getEdwardsElliptic().is25519();
                switch (pKeySpec.getKeyPairType()) {
                    case XDH:
                        myAlgo = is25519
                                 ? new XDHParameterSpec(XDHParameterSpec.X25519)
                                 : new XDHParameterSpec(XDHParameterSpec.X448);
                        break;
                    case EDDSA:
                        myAlgo = is25519
                                 ? new EdDSAParameterSpec(EdDSAParameterSpec.Ed25519)
                                 : new EdDSAParameterSpec(EdDSAParameterSpec.Ed448);
                        break;
                    default:
                        throw new GordianLogicException("Invalid KeySpec" + pKeySpec);
                }

                /* Create and initialise the generator */
                final String myJavaType = pKeySpec.toString();
                theGenerator = JcaKeyPairFactory.getJavaKeyPairGenerator(myJavaType, false);
                theGenerator.initialize(myAlgo, getRandom());

                /* Create the factory */
                setKeyFactory(JcaKeyPairFactory.getJavaKeyFactory(myJavaType, false));

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create EdwardsGenerator", e);
            }
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final KeyPair myPair = theGenerator.generateKeyPair();
            final JcaPublicKey myPublic = createPublic(myPair.getPublic());
            final JcaPrivateKey myPrivate = createPrivate(myPair.getPrivate());
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
                               final GordianKeyPairSpec pKeySpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            final String myJavaType = pKeySpec.getKeyPairType().toString();
            theGenerator = JcaKeyPairFactory.getJavaKeyPairGenerator(myJavaType, true);

            /* Protect against exceptions */
            try {
                final AlgorithmParameterSpec myParms = pKeySpec.getSubKeyType() instanceof GordianHSSKeySpec
                        ? deriveParameters(pKeySpec.getHSSKeySpec())
                        : deriveParameters(pKeySpec.getLMSKeySpec());
                theGenerator.initialize(myParms, getRandom());

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to initialise generator", e);
            }

            /* Create the factory */
            setKeyFactory(JcaKeyPairFactory.getJavaKeyFactory(myJavaType, true));
        }

        /**
         * Derive the parameters.
         * @param pKeySpec the keySPec
         * @return the parameters.
         */
        private static LMSHSSKeyGenParameterSpec deriveParameters(final GordianHSSKeySpec pKeySpec) {
            /* Generate and return the keyPair */
            final GordianLMSKeySpec myKeySpec = pKeySpec.getKeySpec();
            final LMSKeyGenParameterSpec[] myParams = new LMSKeyGenParameterSpec[pKeySpec.getTreeDepth()];
            Arrays.fill(myParams, deriveParameters(myKeySpec));
            return new LMSHSSKeyGenParameterSpec(myParams);
        }

        /**
         * Derive the parameters.
         * @param pKeySpec the keySpec
         * @return the parameters.
         */
        private static LMSKeyGenParameterSpec deriveParameters(final GordianLMSKeySpec pKeySpec) {
            final LMSParameters myParms = pKeySpec.getParameters();
            return new LMSKeyGenParameterSpec(myParms.getLMSigParam(), myParms.getLMOTSParam());
        }

        @Override
        public JcaKeyPair generateKeyPair() {
            /* Generate and return the keyPair */
            final KeyPair myPair = theGenerator.generateKeyPair();
            final JcaPublicKey myPublic = createPublic(myPair.getPublic());
            final JcaStateAwarePrivateKey myPrivate = createPrivate(myPair.getPrivate());
            return new JcaStateAwareKeyPair(myPublic, myPrivate);
        }

        @Override
        protected JcaStateAwarePrivateKey createPrivate(final PrivateKey pPrivateKey) {
            return new JcaStateAwarePrivateKey(getKeySpec(), pPrivateKey);
        }

        @Override
        public JcaKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                        final PKCS8EncodedKeySpec pPrivateKey) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check the keySpecs */
                checkKeySpec(pPrivateKey);

                /* derive keyPair */
                final JcaPublicKey myPublic = derivePublicKey(pPublicKey);
                JcaStateAwarePrivateKey myPrivate = createPrivate(getKeyFactory().generatePrivate(pPrivateKey));
                final JcaKeyPair myPair = new JcaStateAwareKeyPair(myPublic, myPrivate);

                /* Check that we have a matching pair */
                GordianKeyPairValidity.checkValidity(getFactory(), myPair);

                /* Rebuild and return the keyPair to avoid incrementing usage count */
                myPrivate = createPrivate(getKeyFactory().generatePrivate(pPrivateKey));
                return new JcaStateAwareKeyPair(myPublic, myPrivate);

            } catch (InvalidKeySpecException e) {
                throw new GordianCryptoException(PARSE_ERROR, e);
            }
        }
    }
}
