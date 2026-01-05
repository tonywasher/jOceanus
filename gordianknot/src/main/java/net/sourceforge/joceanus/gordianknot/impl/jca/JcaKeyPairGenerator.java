/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.jca;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianDHGroup;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianDSAKeyType;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianLMSKeySpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianLMSKeySpec.GordianHSSKeySpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianRSAModulus;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianXMSSKeySpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianXMSSKeySpec.GordianXMSSDigestType;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianLogicException;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianCoreKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianKeyPairValidity;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPair.JcaDHPrivateKey;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPair.JcaDHPublicKey;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPair.JcaPrivateKey;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPair.JcaPublicKey;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPair.JcaStateAwareKeyPair;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPair.JcaStateAwarePrivateKey;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPublicKey;
import org.bouncycastle.jcajce.spec.DHDomainParameterSpec;
import org.bouncycastle.jcajce.spec.EdDSAParameterSpec;
import org.bouncycastle.jcajce.spec.MLDSAParameterSpec;
import org.bouncycastle.jcajce.spec.MLKEMParameterSpec;
import org.bouncycastle.jcajce.spec.SLHDSAParameterSpec;
import org.bouncycastle.jcajce.spec.XDHParameterSpec;
import org.bouncycastle.jce.spec.ElGamalParameterSpec;
import org.bouncycastle.pqc.crypto.lms.LMSParameters;
import org.bouncycastle.pqc.jcajce.spec.BIKEParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.CMCEParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.FalconParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.FrodoParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.HQCParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.LMSHSSKeyGenParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.LMSKeyGenParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.MayoParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.NTRULPRimeParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.NTRUParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.PicnicParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.SABERParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.SNTRUPrimeParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.SnovaParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.XMSSMTParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.XMSSParameterSpec;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

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
    JcaKeyPairGenerator(final GordianCoreFactory pFactory,
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
    public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws GordianException {
        /* Check the keyPair */
        JcaKeyPair.checkKeyPair(pKeyPair);

        /* derive the encoding */
        final JcaPrivateKey myPrivateKey = (JcaPrivateKey) getPrivateKey(pKeyPair);
        return new PKCS8EncodedKeySpec(myPrivateKey.getPrivateKey().getEncoded());
    }

    @Override
    public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws GordianException {
        /* Check the keyPair */
        JcaKeyPair.checkKeyPair(pKeyPair);

        /* derive the encoding */
        final JcaPublicKey myPublicKey = (JcaPublicKey) getPublicKey(pKeyPair);
        return new X509EncodedKeySpec(myPublicKey.getPublicKey().getEncoded());
    }

    @Override
    public JcaKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                    final PKCS8EncodedKeySpec pPrivateKey) throws GordianException {
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
    public JcaKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pPublicKey) throws GordianException {
        final JcaPublicKey myPublic = derivePublicKey(pPublicKey);
        return new JcaKeyPair(myPublic);
    }

    /**
     * Derive the public key.
     * @param pEncodedKey the encoded public key
     * @return the public key
     * @throws GordianException on error
     */
    protected JcaPublicKey derivePublicKey(final X509EncodedKeySpec pEncodedKey) throws GordianException {
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
         * @throws GordianException on error
         */
        JcaRSAKeyPairGenerator(final GordianCoreFactory pFactory,
                               final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* initialize underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialize the generator */
            theGenerator = getJavaKeyPairGenerator(RSA_ALGO, false);
            theGenerator.initialize(pKeySpec.getRSAModulus().getLength(), getRandom());

            /* Create the factory */
            setKeyFactory(getJavaKeyFactory(RSA_ALGO, false));
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
         * @throws GordianException on error
         */
        JcaElGamalKeyPairGenerator(final GordianCoreFactory pFactory,
                                   final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* initialize underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create the parameter generator */
                final GordianDHGroup myGroup = pKeySpec.getDHGroup();
                final DHParameters myParms = myGroup.getParameters();
                final ElGamalParameterSpec mySpec = new ElGamalParameterSpec(myParms.getP(), myParms.getQ());

                /* Create and initialize the generator */
                theGenerator = getJavaKeyPairGenerator(ELGAMAL_ALGO, false);
                theGenerator.initialize(mySpec, getRandom());

                /* Create the factory */
                setKeyFactory(getJavaKeyFactory(ELGAMAL_ALGO, false));

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
         * @throws GordianException on error
         */
        JcaECKeyPairGenerator(final GordianCoreFactory pFactory,
                              final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* initialize underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create and initialize the generator */
                final String myAlgo = getAlgorithm();
                theGenerator = getJavaKeyPairGenerator(myAlgo, false);
                final ECGenParameterSpec myParms = new ECGenParameterSpec(pKeySpec.getElliptic().getCurveName());
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(getJavaKeyFactory(myAlgo, false));

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
         * @throws GordianException on error
         */
        JcaDSAKeyPairGenerator(final GordianCoreFactory pFactory,
                               final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* initialize underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialize the generator */
            final GordianDSAKeyType myKeyType = pKeySpec.getDSAKeyType();
            theGenerator = getJavaKeyPairGenerator(DSA_ALGO, false);
            theGenerator.initialize(myKeyType.getKeySize(), getRandom());

            /* Create the factory */
            setKeyFactory(getJavaKeyFactory(DSA_ALGO, false));
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
         * @throws GordianException on error
         */
        JcaDHKeyPairGenerator(final GordianCoreFactory pFactory,
                              final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* initialize underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create the parameter generator */
                final GordianDHGroup myGroup = pKeySpec.getDHGroup();
                final DHParameters myParms = myGroup.getParameters();
                final DHDomainParameterSpec mySpec = new DHDomainParameterSpec(myParms);

                /* Create and initialize the generator */
                theGenerator = getJavaKeyPairGenerator(DH_ALGO, false);
                theGenerator.initialize(mySpec, getRandom());

                /* Create the factory */
                setKeyFactory(getJavaKeyFactory(DH_ALGO, false));

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
     * Jca SLHDSA KeyPair generator.
     */
    public static class JcaSLHDSAKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * SLHDSA algorithm.
         */
        private static final String SLHDSA_ALGO = "SLH-DSA";

        /**
         * HASH indication.
         */
        private static final String SLHDSA_HASH = "HASH-" + SLHDSA_ALGO;

        /**
         * Generator.
         */
        private final KeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws GordianException on error
         */
        JcaSLHDSAKeyPairGenerator(final GordianCoreFactory pFactory,
                                  final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* initialize underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Determine algorithm */
                final String myAlgo = pKeySpec.getSLHDSAKeySpec().isHash() ? SLHDSA_HASH : SLHDSA_ALGO;

                /* Create and initialize the generator */
                theGenerator = getJavaKeyPairGenerator(myAlgo, false);
                final SLHDSAParameterSpec myParms = pKeySpec.getSLHDSAKeySpec().getParameterSpec();
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(getJavaKeyFactory(myAlgo, false));

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create SLHDSAgenerator", e);
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
         * @throws GordianException on error
         */
        JcaCMCEKeyPairGenerator(final GordianCoreFactory pFactory,
                                final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* initialize underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create and initialize the generator */
                theGenerator = getJavaKeyPairGenerator(CMCE_ALGO, true);
                final CMCEParameterSpec myParms = pKeySpec.getCMCEKeySpec().getParameterSpec();
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(getJavaKeyFactory(CMCE_ALGO, true));

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
         * @throws GordianException on error
         */
        JcaFrodoKeyPairGenerator(final GordianCoreFactory pFactory,
                                 final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* initialize underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create and initialize the generator */
                theGenerator = getJavaKeyPairGenerator(FRODO_ALGO, true);
                final FrodoParameterSpec myParms = pKeySpec.getFRODOKeySpec().getParameterSpec();
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(getJavaKeyFactory(FRODO_ALGO, true));

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
         * SABER algorithm.
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
         * @throws GordianException on error
         */
        JcaSABERKeyPairGenerator(final GordianCoreFactory pFactory,
                                 final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* initialize underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create and initialize the generator */
                theGenerator = getJavaKeyPairGenerator(SABER_ALGO, true);
                final SABERParameterSpec myParms = pKeySpec.getSABERKeySpec().getParameterSpec();
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(getJavaKeyFactory(SABER_ALGO, true));

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
     * Jca MLKEM KeyPair generator.
     */
    public static class JcaMLKEMKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * MLKEM algorithm.
         */
        private static final String MLKEM_ALGO = "MLKEM";

        /**
         * Generator.
         */
        private final KeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws GordianException on error
         */
        JcaMLKEMKeyPairGenerator(final GordianCoreFactory pFactory,
                                 final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* initialize underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create and initialize the generator */
                theGenerator = getJavaKeyPairGenerator(MLKEM_ALGO, false);
                final MLKEMParameterSpec myParms = pKeySpec.getMLKEMKeySpec().getParameterSpec();
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(getJavaKeyFactory(MLKEM_ALGO, false));

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create MLKEMgenerator", e);
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
     * Jca MLDSA KeyPair generator.
     */
    public static class JcaMLDSAKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * MLDSA algorithm.
         */
        private static final String MLDSA_ALGO = "ML-DSA";

        /**
         * HASH indication.
         */
        private static final String MLDSA_HASH = "HASH-" + MLDSA_ALGO;

        /**
         * Generator.
         */
        private final KeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws GordianException on error
         */
        JcaMLDSAKeyPairGenerator(final GordianCoreFactory pFactory,
                                 final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* initialize underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Determine algorithm */
                final String myAlgo = pKeySpec.getMLDSAKeySpec().isHash() ? MLDSA_HASH : MLDSA_ALGO;

                /* Create and initialize the generator */
                theGenerator = getJavaKeyPairGenerator(myAlgo, false);
                final MLDSAParameterSpec myParms = pKeySpec.getMLDSAKeySpec().getParameterSpec();
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(getJavaKeyFactory(myAlgo, false));

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create MLDSAGenerator", e);
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
     * Jca HQC KeyPair generator.
     */
    public static class JcaHQCKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * BIKE algorithm.
         */
        private static final String HQC_ALGO = "HQC";

        /**
         * Generator.
         */
        private final KeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws GordianException on error
         */
        JcaHQCKeyPairGenerator(final GordianCoreFactory pFactory,
                               final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* initialize underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create and initialize the generator */
                theGenerator = getJavaKeyPairGenerator(HQC_ALGO, true);
                final HQCParameterSpec myParms = pKeySpec.getHQCKeySpec().getParameterSpec();
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(getJavaKeyFactory(HQC_ALGO, true));

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create HQCgenerator", e);
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
     * Jca BIKE KeyPair generator.
     */
    public static class JcaBIKEKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * BIKE algorithm.
         */
        private static final String BIKE_ALGO = "BIKE";

        /**
         * Generator.
         */
        private final KeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws GordianException on error
         */
        JcaBIKEKeyPairGenerator(final GordianCoreFactory pFactory,
                                final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* initialize underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create and initialize the generator */
                theGenerator = getJavaKeyPairGenerator(BIKE_ALGO, true);
                final BIKEParameterSpec myParms = pKeySpec.getBIKEKeySpec().getParameterSpec();
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(getJavaKeyFactory(BIKE_ALGO, true));

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create BIKEgenerator", e);
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
     * Jca NTRU KeyPair generator.
     */
    public static class JcaNTRUKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * NTRU algorithm.
         */
        private static final String NTRU_ALGO = "NTRU";

        /**
         * Generator.
         */
        private final KeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws GordianException on error
         */
        JcaNTRUKeyPairGenerator(final GordianCoreFactory pFactory,
                                final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* initialize underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create and initialize the generator */
                theGenerator = getJavaKeyPairGenerator(NTRU_ALGO, true);
                final NTRUParameterSpec myParms = pKeySpec.getNTRUKeySpec().getParameterSpec();
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(getJavaKeyFactory(NTRU_ALGO, true));

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create NTRUgenerator", e);
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
     * Jca Falcon KeyPair generator.
     */
    public static class JcaFalconKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * FALCON algorithm.
         */
        private static final String FALCON_ALGO = "FALCON";

        /**
         * Generator.
         */
        private final KeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws GordianException on error
         */
        JcaFalconKeyPairGenerator(final GordianCoreFactory pFactory,
                                  final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* initialize underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create and initialize the generator */
                theGenerator = getJavaKeyPairGenerator(FALCON_ALGO, true);
                final FalconParameterSpec myParms = pKeySpec.getFalconKeySpec().getParameterSpec();
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(getJavaKeyFactory(FALCON_ALGO, true));

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create FALCONgenerator", e);
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
     * Jca Mayo KeyPair generator.
     */
    public static class JcaMayoKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * Mayo algorithm.
         */
        private static final String MAYO_ALGO = "MAYO";

        /**
         * Generator.
         */
        private final KeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws GordianException on error
         */
        JcaMayoKeyPairGenerator(final GordianCoreFactory pFactory,
                                final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* initialize underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create and initialize the generator */
                theGenerator = getJavaKeyPairGenerator(MAYO_ALGO, true);
                final MayoParameterSpec myParms = pKeySpec.getMayoKeySpec().getParameterSpec();
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(getJavaKeyFactory(MAYO_ALGO, true));

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create MayoGenerator", e);
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
     * Jca Snova KeyPair generator.
     */
    public static class JcaSnovaKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * Snova algorithm.
         */
        private static final String SNOVA_ALGO = "SNOVA";

        /**
         * Generator.
         */
        private final KeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws GordianException on error
         */
        JcaSnovaKeyPairGenerator(final GordianCoreFactory pFactory,
                                 final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* initialize underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create and initialize the generator */
                theGenerator = getJavaKeyPairGenerator(SNOVA_ALGO, true);
                final SnovaParameterSpec myParms = pKeySpec.getSnovaKeySpec().getParameterSpec();
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(getJavaKeyFactory(SNOVA_ALGO, true));

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create SnovaGenerator", e);
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
     * Jca NTRULPrime KeyPair generator.
     */
    public static class JcaNTRULPrimeKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * NTRULPrime algorithm.
         */
        private static final String NTRU_ALGO = "NTRULPRIME";

        /**
         * Generator.
         */
        private final KeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws GordianException on error
         */
        JcaNTRULPrimeKeyPairGenerator(final GordianCoreFactory pFactory,
                                      final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* initialize underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create and initialize the generator */
                theGenerator = getJavaKeyPairGenerator(NTRU_ALGO, true);
                final NTRULPRimeParameterSpec myParms = pKeySpec.getNTRUPrimeKeySpec().getParams().getNTRULParameterSpec();
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(getJavaKeyFactory(NTRU_ALGO, true));

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create NTRULPrimeGenerator", e);
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
     * Jca SNTRUPrime KeyPair generator.
     */
    public static class JcaSNTRUPrimeKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * NTRULPrime algorithm.
         */
        private static final String NTRU_ALGO = "SNTRUPRIME";

        /**
         * Generator.
         */
        private final KeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws GordianException on error
         */
        JcaSNTRUPrimeKeyPairGenerator(final GordianCoreFactory pFactory,
                                      final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* initialize underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create and initialize the generator */
                theGenerator = getJavaKeyPairGenerator(NTRU_ALGO, true);
                final SNTRUPrimeParameterSpec myParms = pKeySpec.getNTRUPrimeKeySpec().getParams().getSNTRUParameterSpec();
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(getJavaKeyFactory(NTRU_ALGO, true));

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create SNTRUPrimeGenerator", e);
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
     * Jca Picnic KeyPair generator.
     */
    public static class JcaPicnicKeyPairGenerator
            extends JcaKeyPairGenerator {
        /**
         * Picnic algorithm.
         */
        private static final String PICNIC_ALGO = "PICNIC";

        /**
         * Generator.
         */
        private final KeyPairGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         * @throws GordianException on error
         */
        JcaPicnicKeyPairGenerator(final GordianCoreFactory pFactory,
                                  final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* initialize underlying class */
            super(pFactory, pKeySpec);

            /* Protect against exceptions */
            try {
                /* Create and initialize the generator */
                theGenerator = getJavaKeyPairGenerator(PICNIC_ALGO, true);
                final PicnicParameterSpec myParms = pKeySpec.getPicnicKeySpec().getParameterSpec();
                theGenerator.initialize(myParms, getRandom());

                /* Create the factory */
                setKeyFactory(getJavaKeyFactory(PICNIC_ALGO, true));

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to create PICNICgenerator", e);
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
         * @throws GordianException on error
         */
        JcaXMSSKeyPairGenerator(final GordianCoreFactory pFactory,
                                final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* initialize underlying class */
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

                /* Create and initialize the generator */
                final String myJavaType = myXMSSKeySpec.getKeyType().name();
                theGenerator = getJavaKeyPairGenerator(myJavaType, true);
                theGenerator.initialize(myAlgo, getRandom());

                /* Create the factory */
                setKeyFactory(getJavaKeyFactory(myJavaType, true));

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
                                        final PKCS8EncodedKeySpec pPrivateKey) throws GordianException {
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
         * @throws GordianException on error
         */
        protected JcaEdKeyPairGenerator(final GordianCoreFactory pFactory,
                                        final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* initialize underlying class */
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

                /* Create and initialize the generator */
                final String myJavaType = pKeySpec.toString();
                theGenerator = getJavaKeyPairGenerator(myJavaType, false);
                theGenerator.initialize(myAlgo, getRandom());

                /* Create the factory */
                setKeyFactory(getJavaKeyFactory(myJavaType, false));

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
         * @throws GordianException on error
         */
        JcaNewHopeKeyPairGenerator(final GordianCoreFactory pFactory,
                                   final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* initialize underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialize the generator */
            theGenerator = getJavaKeyPairGenerator(NEWHOPE_ALGO, true);
            theGenerator.initialize(GordianRSAModulus.MOD1024.getLength(), getRandom());

            /* Create the factory */
            setKeyFactory(getJavaKeyFactory(NEWHOPE_ALGO, true));
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
         * @throws GordianException on error
         */
        JcaLMSKeyPairGenerator(final GordianCoreFactory pFactory,
                               final GordianKeyPairSpec pKeySpec) throws GordianException {
            /* initialize underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialize the generator */
            final String myJavaType = pKeySpec.getKeyPairType().toString();
            theGenerator = getJavaKeyPairGenerator(myJavaType, true);

            /* Protect against exceptions */
            try {
                final AlgorithmParameterSpec myParms = pKeySpec.getSubKeyType() instanceof GordianHSSKeySpec
                        ? deriveParameters(pKeySpec.getHSSKeySpec())
                        : deriveParameters(pKeySpec.getLMSKeySpec());
                theGenerator.initialize(myParms, getRandom());

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException("Failed to initialize generator", e);
            }

            /* Create the factory */
            setKeyFactory(getJavaKeyFactory(myJavaType, true));
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
                                        final PKCS8EncodedKeySpec pPrivateKey) throws GordianException {
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
     * Create the BouncyCastle KeyFactory via JCA.
     * @param pAlgorithm the Algorithm
     * @param postQuantum is this a postQuantum algorithm?
     * @return the KeyFactory
     * @throws GordianException on error
     */
    static KeyFactory getJavaKeyFactory(final String pAlgorithm,
                                        final boolean postQuantum) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Return a KeyFactory for the algorithm */
            return KeyFactory.getInstance(pAlgorithm, postQuantum
                    ? JcaProvider.BCPQPROV
                    : JcaProvider.BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create KeyFactory", e);
        }
    }

    /**
     * Create the BouncyCastle KeyPairGenerator via JCA.
     * @param pAlgorithm the Algorithm
     * @param postQuantum is this a postQuantum algorithm?
     * @return the KeyPairGenerator
     * @throws GordianException on error
     */
    static KeyPairGenerator getJavaKeyPairGenerator(final String pAlgorithm,
                                                    final boolean postQuantum) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Return a KeyPairGenerator for the algorithm */
            return KeyPairGenerator.getInstance(pAlgorithm, postQuantum
                    ? JcaProvider.BCPQPROV
                    : JcaProvider.BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create KeyPairGenerator", e);
        }
    }
}
