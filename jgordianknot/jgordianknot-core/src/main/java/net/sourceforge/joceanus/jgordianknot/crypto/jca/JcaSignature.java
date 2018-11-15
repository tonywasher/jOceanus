/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto.jca;

import java.security.InvalidKeyException;
import java.security.Signature;
import java.security.SignatureException;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianLength;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignature;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureType;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Jca implementation of signature.
 */
public abstract class JcaSignature
        extends GordianSignature {
    /**
     * The Signature error.
     */
    private static final String SIG_ERROR = "Signature error";

    /**
     * The RSA PSS Algorithm.
     */
    private static final String RSA_PSS_ALGOBASE = "withRSAandMGF1";

    /**
     * The RSA X9.31 Algorithm.
     */
    private static final String RSA_X931_ALGOBASE = "withRSA/X9.31";

    /**
     * The RSA ISO9796d2 Algorithm.
     */
    private static final String RSA_ISO9796D2_ALGOBASE = "withRSA/ISO9796-2";

    /**
     * The ECDSA Signature.
     */
    private static final String EC_DSA_ALGOBASE = "withECDSA";

    /**
     * The ECDDSA Signature.
     */
    private static final String EC_DDSA_ALGOBASE = "withECDDSA";

    /**
     * The DSA Signature.
     */
    private static final String DSA_ALGOBASE = "withDSA";

    /**
     * The DDSA Signature.
     */
    private static final String DDSA_ALGOBASE = "withDDSA";

    /**
     * The ECNR Signature.
     */
    private static final String EC_NR_ALGOBASE = "withECNR";

    /**
     * The SM2 Signature.
     */
    private static final String EC_SM2_ALGOBASE = "WITHSM2";

    /**
     * The SPHINCS Signature.
     */
    static final String SPHINCS_ALGOBASE = "withSPHINCS256";

    /**
     * The Rainbow Signature.
     */
    static final String RAINBOW_ALGOBASE = "withRainbow";

    /**
     * The DSTU Signature.
     */
    private static final String DSTU_SIGN = "DSTU4145";

    /**
     * The RSA Signer.
     */
    private Signature theSigner;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSpec the signature Spec
     */
    JcaSignature(final GordianFactory pFactory,
                 final GordianSignatureSpec pSpec) {
        super(pFactory, pSpec);
    }

    /**
     * Set the signer.
     * @param pSigner the signer.
     */
    protected void setSigner(final Signature pSigner) {
        theSigner = pSigner;
    }

    /**
     * Obtain the signer.
     * @return the signer.
     */
    protected Signature getSigner() {
        return theSigner;
    }

    @Override
    public void initForSigning(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Initialise detail */
        super.initForSigning(pKeyPair);

        /* Initialise for signing */
        try {
            /* Determine whether we should use random for signatures */
            final boolean useRandom = getSignatureSpec().getAsymKeyType().useRandomForSignatures();

            /* Initialise the signing */
            if (useRandom) {
                getSigner().initSign(getKeyPair().getPrivateKey().getPrivateKey(), getRandom());
            } else {
                getSigner().initSign(getKeyPair().getPrivateKey().getPrivateKey());
            }

            /* Catch exceptions */
        } catch (InvalidKeyException e) {
            throw new GordianCryptoException(SIG_ERROR, e);
        }
    }

    @Override
    public void initForVerify(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Initialise detail */
        super.initForVerify(pKeyPair);

        /* Initialise for signing */
        try {
            /* Initialise for verification */
            getSigner().initVerify(getKeyPair().getPublicKey().getPublicKey());

            /* Catch exceptions */
        } catch (InvalidKeyException e) {
            throw new GordianCryptoException(SIG_ERROR, e);
        }
    }

    @Override
    public void update(final byte[] pBytes,
                       final int pOffset,
                       final int pLength) {
        try {
            theSigner.update(pBytes, pOffset, pLength);
        } catch (SignatureException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void update(final byte pByte) {
        try {
            theSigner.update(pByte);
        } catch (SignatureException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void update(final byte[] pBytes) {
        try {
            theSigner.update(pBytes, 0, pBytes.length);
        } catch (SignatureException e) {
            throw new IllegalArgumentException(e);
        }
    }


    @Override
    public byte[] sign() throws OceanusException {
        /* Check that we are in signing mode */
        checkMode(GordianSignatureMode.SIGN);

        /* Protect against exception */
        try {
            return getSigner().sign();
        } catch (SignatureException e) {
            throw new GordianCryptoException(SIG_ERROR, e);
        }
    }

    @Override
    public boolean verify(final byte[] pSignature) throws OceanusException {
        /* Check that we are in verify mode */
        checkMode(GordianSignatureMode.VERIFY);

        /* Protect against exception */
        try {
            return getSigner().verify(pSignature);
        } catch (SignatureException e) {
            throw new GordianCryptoException(SIG_ERROR, e);
        }
    }

    @Override
    public void reset() {
        /* NoOp */
    }

    @Override
    protected JcaKeyPair getKeyPair() {
        return (JcaKeyPair) super.getKeyPair();
    }

    /**
     * RSA signature.
     */
    static class JcaRSASignature
            extends JcaSignature {
        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSignatureSpec the signatureSpec
         * @throws OceanusException on error
         */
        JcaRSASignature(final GordianFactory pFactory,
                        final GordianSignatureSpec pSignatureSpec) throws OceanusException {
            /* Initialise class */
            super(pFactory, pSignatureSpec);

            /* Create the signature class */
            final String myDigest = JcaDigest.getSignAlgorithm(pSignatureSpec.getDigestSpec());
            setSigner(JcaFactory.getJavaSignature(myDigest + getSignatureBase(pSignatureSpec), false));
        }
    }

    /**
     * Obtain Signer base.
     * @param pSignatureSpec the signatureSpec
     * @return the base
     */
    static String getSignatureBase(final GordianSignatureSpec pSignatureSpec) {
        /* Handle SM2 explicitly */
        if (GordianAsymKeyType.SM2.equals(pSignatureSpec.getAsymKeyType())) {
            return EC_SM2_ALGOBASE;
        }

        /* Note if we are DSA */
        final boolean isDSA = GordianAsymKeyType.DSA.equals(pSignatureSpec.getAsymKeyType());

        /* Switch on signature type */
        switch (pSignatureSpec.getSignatureType()) {
            case PSS:
                return RSA_PSS_ALGOBASE;
            case X931:
                return RSA_X931_ALGOBASE;
            case ISO9796D2:
                return RSA_ISO9796D2_ALGOBASE;
            case DSA:
                return isDSA
                             ? DSA_ALGOBASE
                             : EC_DSA_ALGOBASE;
            case DDSA:
                return isDSA
                             ? DDSA_ALGOBASE
                             : EC_DDSA_ALGOBASE;
            case NR:
                return EC_NR_ALGOBASE;
            default:
                return null;
        }
    }

    /**
     * DSA signer.
     */
    static class JcaDSASignature
            extends JcaSignature {
        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSignatureSpec the signatureSpec
         * @throws OceanusException on error
         */
        JcaDSASignature(final GordianFactory pFactory,
                        final GordianSignatureSpec pSignatureSpec) throws OceanusException {
            /* Initialise class */
            super(pFactory, pSignatureSpec);

            /* Create the signature class */
            final String myDigest = JcaDigest.getSignAlgorithm(pSignatureSpec.getDigestSpec());
            setSigner(JcaFactory.getJavaSignature(myDigest + getSignatureBase(pSignatureSpec), false));
        }
    }

    /**
     * GOST signature.
     */
    static class JcaGOSTSignature
            extends JcaSignature {
        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSignatureSpec the signatureSpec
         * @throws OceanusException on error
         */
        JcaGOSTSignature(final GordianFactory pFactory,
                         final GordianSignatureSpec pSignatureSpec) throws OceanusException {
            /* Initialise class */
            super(pFactory, pSignatureSpec);

            /* Create the signature class */
            setSigner(JcaFactory.getJavaSignature(getSignature(pSignatureSpec), false));
        }

        /**
         * Obtain Signer base.
         * @param pSignatureSpec the signatureSpec
         * @return the base
         */
        private static String getSignature(final GordianSignatureSpec pSignatureSpec) {
            /* Handle DSTU explicitly */
            if (GordianAsymKeyType.DSTU4145.equals(pSignatureSpec.getAsymKeyType())) {
                return DSTU_SIGN;
            }

            /* Obtain the digest length */
            final GordianLength myLength = pSignatureSpec.getDigestSpec().getDigestLength();

            /* Build the algorithm */
            final StringBuilder myBuilder = new StringBuilder();
            myBuilder.append("GOST3411-2012-")
               .append(myLength.getLength())
               .append("withECGOST3410-2012-")
               .append(myLength.getLength());
            return myBuilder.toString();
        }
    }

    /**
     * SPHINCS signature.
     */
    static class JcaSPHINCSSignature
            extends JcaSignature {
        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSignatureSpec the signatureSpec
         * @throws OceanusException on error
         */
        JcaSPHINCSSignature(final GordianFactory pFactory,
                            final GordianSignatureSpec pSignatureSpec) throws OceanusException {
            /* Initialise class */
            super(pFactory, pSignatureSpec);
        }
        @Override
        public void initForSigning(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Determine the required signer */
            final String mySignName = getAlgorithmForKeyPair(pKeyPair);
            setSigner(JcaFactory.getJavaSignature(mySignName, true));

            /* pass on call */
            super.initForSigning(pKeyPair);
        }

        @Override
        public void initForVerify(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Determine the required signer */
            final String mySignName = getAlgorithmForKeyPair(pKeyPair);
            setSigner(JcaFactory.getJavaSignature(mySignName, true));

            /* pass on call */
            super.initForVerify(pKeyPair);
        }

        /**
         * Obtain algorithmName for keyPair.
         * @param pKeyPair the keyPair
         * @return the name
         * @throws OceanusException on error
         */
        private static String getAlgorithmForKeyPair(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Determine the required signer */
            final GordianDigestSpec myDigestSpec = pKeyPair.getKeySpec().getSPHINCSType().getDigestSpec();
            final String myDigest = JcaDigest.getAlgorithm(myDigestSpec);

            /* Create builder */
            final StringBuilder myBuilder = new StringBuilder();
            myBuilder.append(myDigest)
                    .append(SPHINCS_ALGOBASE);

            /* Build the algorithm */
            return myBuilder.toString();
        }
    }

    /**
     * Rainbow signature.
     */
    static class JcaRainbowSignature
            extends JcaSignature {
        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSignatureSpec the signatureSpec
        * @throws OceanusException on error
         */
        JcaRainbowSignature(final GordianFactory pFactory,
                            final GordianSignatureSpec pSignatureSpec) throws OceanusException {
            /* Initialise class */
            super(pFactory, pSignatureSpec);

            /* Create the signature class */
            final String myDigest = JcaDigest.getAlgorithm(pSignatureSpec.getDigestSpec());
            setSigner(JcaFactory.getJavaSignature(myDigest + RAINBOW_ALGOBASE, true));
        }
    }

    /**
     * XMSS signature.
     */
    static class JcaXMSSSignature
            extends JcaSignature {
        /**
         * Is this a preHash signature?
         */
        private final boolean preHash;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSignatureSpec the signatureSpec
         * @throws OceanusException on error
         */
        JcaXMSSSignature(final GordianFactory pFactory,
                         final GordianSignatureSpec pSignatureSpec) throws OceanusException {
            /* Initialise class */
            super(pFactory, pSignatureSpec);

            /* Determine preHash */
            preHash = GordianSignatureType.PREHASH.equals(pSignatureSpec.getSignatureType());
        }

        @Override
        public void initForSigning(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Determine the required signer */
            final String mySignName = getAlgorithmForKeyPair(pKeyPair);
            setSigner(JcaFactory.getJavaSignature(mySignName, true));

            /* pass on call */
            super.initForSigning(pKeyPair);
        }

        @Override
        public void initForVerify(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Determine the required signer */
            final String mySignName = getAlgorithmForKeyPair(pKeyPair);
            setSigner(JcaFactory.getJavaSignature(mySignName, true));

            /* pass on call */
            super.initForVerify(pKeyPair);
        }

        /**
         * Obtain algorithmName for keyPair.
         * @param pKeyPair the keyPair
         * @return the name
         * @throws OceanusException on error
         */
        private String getAlgorithmForKeyPair(final GordianKeyPair pKeyPair) throws OceanusException {
            /* Determine the required signer */
            final GordianDigestSpec myDigestSpec = pKeyPair.getKeySpec().getXMSSKeyType().getDigestSpec();
            final String myDigest = JcaDigest.getAlgorithm(myDigestSpec);

            /* Create builder */
            final GordianAsymKeySpec myKeySpec = pKeyPair.getKeySpec();
            final StringBuilder myBuilder = new StringBuilder();
            myBuilder.append(myKeySpec.getKeyType())
                    .append('-')
                    .append(myDigest);
            if (preHash) {
                myBuilder.insert(0, "with")
                        .insert(0, myDigest);
            }

            /* Build the algorithm */
            return myBuilder.toString();
        }
    }

    /**
     * EdDSA signature.
     */
    static class JcaEdDSASignature
            extends JcaSignature {
        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSignatureSpec the signatureSpec
         * @throws OceanusException on error
         */
        JcaEdDSASignature(final GordianFactory pFactory,
                          final GordianSignatureSpec pSignatureSpec) throws OceanusException {
            /* Initialise class */
            super(pFactory, pSignatureSpec);

            /* Create the signature class */
            final boolean is25519 = GordianAsymKeyType.ED25519.equals(pSignatureSpec.getAsymKeyType());
            final String myAlgo;
            switch (pSignatureSpec.getSignatureType()) {
                case PREHASH:
                    myAlgo = is25519
                             ? "Ed25519ph"
                             : "Ed448ph";
                    break;
                case PURE:
                    myAlgo = is25519
                             ? "Ed25519ctx"
                             : "Ed448";
                    break;
                default:
                    throw new IllegalArgumentException("Invalid Signature Type: " + pSignatureSpec.getSignatureType());
            }
            setSigner(JcaFactory.getJavaSignature(myAlgo, false));
        }
    }

    /**
     * qTESLA signature.
     */
    static class JcaQTESLASignature
            extends JcaSignature {
        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSignatureSpec the signatureSpec
         * @throws OceanusException on error
         */
        JcaQTESLASignature(final GordianFactory pFactory,
                           final GordianSignatureSpec pSignatureSpec) throws OceanusException {
            /* Initialise class */
            super(pFactory, pSignatureSpec);

            /* Create the signature class */
            setSigner(JcaFactory.getJavaSignature("QTESLA", true));
        }
    }
}
