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
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianEdwardsElliptic;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianXMSSKeySpec;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignParams;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureType;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import net.sourceforge.joceanus.gordianknot.impl.core.sign.GordianCoreSignature;
import org.bouncycastle.jcajce.spec.ContextParameterSpec;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Signature;
import java.security.SignatureException;

/**
 * Jca implementation of signature.
 */
public abstract class JcaSignature
        extends GordianCoreSignature {
    /**
     * The Signature error.
     */
    private static final String SIG_ERROR = "Signature error";

    /**
     * The RSA PSS MGF1 Algorithm.
     */
    private static final String RSA_PSSMGF1_ALGOBASE = "withRSAandMGF1";

    /**
     * The RSA PSS SHAKE128 Algorithm.
     */
    private static final String RSA_PSS128_ALGOBASE = "withRSAandSHAKE128";

    /**
     * The RSA PSS SHAKE256 Algorithm.
     */
    private static final String RSA_PSS256_ALGOBASE = "withRSAandSHAKE256";

    /**
     * The RSA PSS PureSHAKE Algorithm.
     */
    private static final String RSA_PSSSHAKE_ALGOBASE = "withRSA/PSS";

    /**
     * The RSA X9.31 Algorithm.
     */
    private static final String RSA_X931_ALGOBASE = "withRSA/X9.31";

    /**
     * The RSA ISO9796d2 Algorithm.
     */
    private static final String RSA_ISO9796D2_ALGOBASE = "withRSA/ISO9796-2";

    /**
     * The RSA preHash Algorithm.
     */
    private static final String RSA_PREHASH_ALGOBASE = "withRSAEncryption";

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
     * The DSTU Signature.
     */
    private static final String DSTU_SIGN = "DSTU4145";

    /**
     * The PQC Hash prefix.
     */
    private static final String PQC_HASH_PFX = "HASH-";

    /**
     * The RSA Signer.
     */
    private Signature theSigner;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSpec the signature Spec
     */
    JcaSignature(final GordianCoreFactory pFactory,
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
    public void initForSigning(final GordianSignParams pParams) throws GordianException {
        /* Initialise detail */
        super.initForSigning(pParams);
        final JcaKeyPair myPair = getKeyPair();
        final byte[] myContext = getContext();
        JcaKeyPair.checkKeyPair(myPair);

        /* Initialise for signing */
        try {
            /* Determine whether we should use random for signatures */
            final boolean useRandom = getSignatureSpec().getKeyPairType().useRandomForSignatures();

            /* Initialise the signing */
            if (useRandom) {
                getSigner().initSign(myPair.getPrivateKey().getPrivateKey(), getRandom());
            } else {
                getSigner().initSign(myPair.getPrivateKey().getPrivateKey());
            }

            /* If we support context */
            if (getSignatureSpec().supportsContext()) {
                /* Declare the context to the signer */
                final ContextParameterSpec mySpec = myContext == null ? null : new ContextParameterSpec(myContext);
                getSigner().setParameter(mySpec);
            }

            /* Catch exceptions */
        } catch (InvalidKeyException
                 | InvalidAlgorithmParameterException e) {
            throw new GordianCryptoException(SIG_ERROR, e);
        }
    }

    @Override
    public void initForVerify(final GordianSignParams pParams) throws GordianException {
        /* Initialise detail */
        super.initForVerify(pParams);
        final JcaKeyPair myPair = getKeyPair();
        final byte[] myContext = getContext();
        JcaKeyPair.checkKeyPair(myPair);

        /* Initialise for signing */
        try {
            /* Initialise for verification */
            getSigner().initVerify(myPair.getPublicKey().getPublicKey());

            /* If we support context */
            if (getSignatureSpec().supportsContext()) {
                /* Declare the context to the signer */
                final ContextParameterSpec mySpec = myContext == null ? null : new ContextParameterSpec(myContext);
                getSigner().setParameter(mySpec);
            }

            /* Catch exceptions */
        } catch (InvalidKeyException
                 | InvalidAlgorithmParameterException e) {
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
    public byte[] sign() throws GordianException {
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
    public boolean verify(final byte[] pSignature) throws GordianException {
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
         * @throws GordianException on error
         */
        JcaRSASignature(final GordianCoreFactory pFactory,
                        final GordianSignatureSpec pSignatureSpec) throws GordianException {
            /* Initialise class */
            super(pFactory, pSignatureSpec);

            /* Create the signature class */
            final String myDigest = JcaDigest.getSignAlgorithm(pSignatureSpec.getDigestSpec());
            setSigner(JcaSignatureFactory.getJavaSignature(myDigest + getSignatureBase(pSignatureSpec), false));
        }
    }

    /**
     * Obtain Signer base.
     * @param pSignatureSpec the signatureSpec
     * @return the base
     */
    static String getSignatureBase(final GordianSignatureSpec pSignatureSpec) {
        /* Handle SM2 explicitly */
        if (GordianKeyPairType.SM2.equals(pSignatureSpec.getKeyPairType())) {
            return EC_SM2_ALGOBASE;
        }

        /* Note if we are DSA */
        final boolean isDSA = GordianKeyPairType.DSA.equals(pSignatureSpec.getKeyPairType());
        final boolean isSHAKE = GordianDigestType.SHAKE.equals(pSignatureSpec.getDigestSpec().getDigestType());

        /* Switch on signature type */
        switch (pSignatureSpec.getSignatureType()) {
            case PSSMGF1:
                return RSA_PSSMGF1_ALGOBASE;
            case PSS128:
                return isSHAKE ? RSA_PSSSHAKE_ALGOBASE : RSA_PSS128_ALGOBASE;
            case PSS256:
                return isSHAKE ? RSA_PSSSHAKE_ALGOBASE : RSA_PSS256_ALGOBASE;
            case X931:
                return RSA_X931_ALGOBASE;
            case ISO9796D2:
                return RSA_ISO9796D2_ALGOBASE;
            case PREHASH:
                return RSA_PREHASH_ALGOBASE;
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
         * @throws GordianException on error
         */
        JcaDSASignature(final GordianCoreFactory pFactory,
                        final GordianSignatureSpec pSignatureSpec) throws GordianException {
            /* Initialise class */
            super(pFactory, pSignatureSpec);

            /* Create the signature class */
            final String myDigest = JcaDigest.getSignAlgorithm(pSignatureSpec.getDigestSpec());
            setSigner(JcaSignatureFactory.getJavaSignature(myDigest + getSignatureBase(pSignatureSpec), false));
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
         * @throws GordianException on error
         */
        JcaGOSTSignature(final GordianCoreFactory pFactory,
                         final GordianSignatureSpec pSignatureSpec) throws GordianException {
            /* Initialise class */
            super(pFactory, pSignatureSpec);

            /* Create the signature class */
            setSigner(JcaSignatureFactory.getJavaSignature(getSignature(pSignatureSpec), false));
        }

        /**
         * Obtain Signer base.
         * @param pSignatureSpec the signatureSpec
         * @return the base
         */
        private static String getSignature(final GordianSignatureSpec pSignatureSpec) {
            /* Handle DSTU explicitly */
            if (GordianKeyPairType.DSTU4145.equals(pSignatureSpec.getKeyPairType())) {
                return DSTU_SIGN;
            }

            /* Obtain the digest length */
            final GordianLength myLength = pSignatureSpec.getDigestSpec().getDigestLength();

            /* Build the algorithm */
            return "GOST3411-2012-"
                    + myLength.getLength()
                    + "withECGOST3410-2012-"
                    + myLength.getLength();
        }
    }

    /**
     * SLHDSA signature.
     */
    static class JcaSLHDSASignature
            extends JcaSignature {
        /**
         * Base name.
         */
        private static final String BASE_NAME = "SLH-DSA";

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSignatureSpec the signatureSpec
        */
        JcaSLHDSASignature(final GordianCoreFactory pFactory,
                           final GordianSignatureSpec pSignatureSpec) {
            /* Initialise class */
            super(pFactory, pSignatureSpec);
        }

        @Override
        public void initForSigning(final GordianSignParams pParams) throws GordianException {
            /* Determine the required signer */
            final GordianKeyPair myPair = pParams.getKeyPair();
            JcaKeyPair.checkKeyPair(myPair);
            final String mySignName = getAlgorithmForKeyPair(myPair);
            setSigner(JcaSignatureFactory.getJavaSignature(mySignName, false));

            /* pass on call */
            super.initForSigning(pParams);
        }

        @Override
        public void initForVerify(final GordianSignParams pParams) throws GordianException {
            /* Determine the required signer */
            final GordianKeyPair myPair = pParams.getKeyPair();
            JcaKeyPair.checkKeyPair(myPair);
            final String mySignName = getAlgorithmForKeyPair(myPair);
            setSigner(JcaSignatureFactory.getJavaSignature(mySignName, false));

            /* pass on call */
            super.initForVerify(pParams);
        }

        /**
         * Obtain algorithmName for keyPair.
         * @param pKeyPair the keyPair
         * @return the name
         */
        private static String getAlgorithmForKeyPair(final GordianKeyPair pKeyPair) {
            /* Build the algorithm */
            final boolean isHash = pKeyPair.getKeyPairSpec().getSLHDSAKeySpec().isHash();
            return isHash ? PQC_HASH_PFX + BASE_NAME : BASE_NAME;
        }
    }

    /**
     * MLDSA signature.
     */
    static class JcaMLDSASignature
            extends JcaSignature {
        /**
         * Base name.
         */
        private static final String BASE_NAME = "ML-DSA";

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSignatureSpec the signatureSpec
         */
        JcaMLDSASignature(final GordianCoreFactory pFactory,
                          final GordianSignatureSpec pSignatureSpec) {
            /* Initialise class */
            super(pFactory, pSignatureSpec);
        }

        @Override
        public void initForSigning(final GordianSignParams pParams) throws GordianException {
            /* Determine the required signer */
            final GordianKeyPair myPair = pParams.getKeyPair();
            JcaKeyPair.checkKeyPair(myPair);
            final String mySignName = getAlgorithmForKeyPair(myPair);
            setSigner(JcaSignatureFactory.getJavaSignature(mySignName, false));

            /* pass on call */
            super.initForSigning(pParams);
        }

        @Override
        public void initForVerify(final GordianSignParams pParams) throws GordianException {
            /* Determine the required signer */
            final GordianKeyPair myPair = pParams.getKeyPair();
            JcaKeyPair.checkKeyPair(myPair);
            final String mySignName = getAlgorithmForKeyPair(myPair);
            setSigner(JcaSignatureFactory.getJavaSignature(mySignName, false));

            /* pass on call */
            super.initForVerify(pParams);
        }

        /**
         * Obtain algorithmName for keyPair.
         * @param pKeyPair the keyPair
         * @return the name
         */
        private static String getAlgorithmForKeyPair(final GordianKeyPair pKeyPair) {
            /* Build the algorithm */
            final boolean isHash = pKeyPair.getKeyPairSpec().getMLDSAKeySpec().isHash();
            return isHash ? PQC_HASH_PFX + BASE_NAME : BASE_NAME;
        }
    }

    /**
     * Falcon signature.
     */
    static class JcaFalconSignature
            extends JcaSignature {
        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSignatureSpec the signatureSpec
         * @throws GordianException on error
         */
        JcaFalconSignature(final GordianCoreFactory pFactory,
                           final GordianSignatureSpec pSignatureSpec) throws GordianException {
            /* Initialise class */
            super(pFactory, pSignatureSpec);

            /* Create the signature class */
            setSigner(JcaSignatureFactory.getJavaSignature("FALCON", true));
        }
    }

    /**
     * Mayo signature.
     */
    static class JcaMayoSignature
            extends JcaSignature {
        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSignatureSpec the signatureSpec
         * @throws GordianException on error
         */
        JcaMayoSignature(final GordianCoreFactory pFactory,
                         final GordianSignatureSpec pSignatureSpec) throws GordianException {
            /* Initialise class */
            super(pFactory, pSignatureSpec);

            /* Create the signature class */
            setSigner(JcaSignatureFactory.getJavaSignature("MAYO", true));
        }
    }

    /**
     * Snova signature.
     */
    static class JcaSnovaSignature
            extends JcaSignature {
        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSignatureSpec the signatureSpec
         * @throws GordianException on error
         */
        JcaSnovaSignature(final GordianCoreFactory pFactory,
                          final GordianSignatureSpec pSignatureSpec) throws GordianException {
            /* Initialise class */
            super(pFactory, pSignatureSpec);

            /* Create the signature class */
            setSigner(JcaSignatureFactory.getJavaSignature("SNOVA", true));
        }
    }

    /**
     * Picnic signature.
     */
    static class JcaPicnicSignature
            extends JcaSignature {
        /**
         * SIgnature base.
         */
        private static final String BASE_NAME = "PICNIC";

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSignatureSpec the signatureSpec
         * @throws GordianException on error
         */
        JcaPicnicSignature(final GordianCoreFactory pFactory,
                           final GordianSignatureSpec pSignatureSpec) throws GordianException {
            /* Initialise class */
            super(pFactory, pSignatureSpec);

            /* Create the signature class */
            final String myName = determineSignatureName(pSignatureSpec);
            setSigner(JcaSignatureFactory.getJavaSignature(myName, true));
        }

        /**
         * Determine signatureName.
         * @param pSignatureSpec the signatureSpec
         * @return the algorithm name
         */
        private static String determineSignatureName(final GordianSignatureSpec pSignatureSpec) {
            /* If we do not have a digest */
            if (pSignatureSpec.getSignatureSpec() == null) {
                return BASE_NAME;
            }

            /* Switch on digest Type */
            switch (pSignatureSpec.getDigestSpec().getDigestType()) {
                case SHA2:
                    return "SHA512With" + BASE_NAME;
                case SHA3:
                    return "SHA3-512With" + BASE_NAME;
                case SHAKE:
                    return "SHAKE256With" + BASE_NAME;
                default:
                    throw new IllegalArgumentException("Bad SignatureSpec");
            }
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
         */
        JcaXMSSSignature(final GordianCoreFactory pFactory,
                         final GordianSignatureSpec pSignatureSpec) {
            /* Initialise class */
            super(pFactory, pSignatureSpec);

            /* Determine preHash */
            preHash = GordianSignatureType.PREHASH.equals(pSignatureSpec.getSignatureType());
        }

        @Override
        public void initForSigning(final GordianSignParams pParams) throws GordianException {
            /* Determine the required signer */
            final GordianKeyPair myPair = pParams.getKeyPair();
            JcaKeyPair.checkKeyPair(myPair);
            final String mySignName = getAlgorithmForKeyPair(myPair);
            setSigner(JcaSignatureFactory.getJavaSignature(mySignName, true));

            /* pass on call */
            super.initForSigning(pParams);
        }

        @Override
        public void initForVerify(final GordianSignParams pParams) throws GordianException {
            /* Determine the required signer */
            final GordianKeyPair myPair = pParams.getKeyPair();
            JcaKeyPair.checkKeyPair(myPair);
            final String mySignName = getAlgorithmForKeyPair(myPair);
            setSigner(JcaSignatureFactory.getJavaSignature(mySignName, true));

            /* pass on call */
            super.initForVerify(pParams);
        }

        /**
         * Obtain algorithmName for keyPair.
         * @param pKeyPair the keyPair
         * @return the name
         * @throws GordianException on error
         */
        private String getAlgorithmForKeyPair(final GordianKeyPair pKeyPair) throws GordianException {
            /* Determine the required signer */
            final GordianXMSSKeySpec myXMSSKeySpec = pKeyPair.getKeyPairSpec().getXMSSKeySpec();
            final GordianDigestSpec myDigestSpec = myXMSSKeySpec.getDigestType().getDigestSpec();
            final String myDigest = JcaDigest.getAlgorithm(myDigestSpec);

            /* Create builder */
            final StringBuilder myBuilder = new StringBuilder();
            myBuilder.append(myXMSSKeySpec.getKeyType().name())
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
         */
        JcaEdDSASignature(final GordianCoreFactory pFactory,
                          final GordianSignatureSpec pSignatureSpec) {
            /* Initialise class */
            super(pFactory, pSignatureSpec);
        }

        @Override
        public void initForSigning(final GordianSignParams pParams) throws GordianException {
            /* Determine the required signer */
            final GordianKeyPair myPair = pParams.getKeyPair();
            JcaKeyPair.checkKeyPair(myPair);
            final String mySignName = getAlgorithmForKeyPair(myPair);
            setSigner(JcaSignatureFactory.getJavaSignature(mySignName, false));

            /* pass on call */
            super.initForSigning(pParams);
        }

        @Override
        public void initForVerify(final GordianSignParams pParams) throws GordianException {
            /* Determine the required signer */
            final GordianKeyPair myPair = pParams.getKeyPair();
            JcaKeyPair.checkKeyPair(myPair);
            final String mySignName = getAlgorithmForKeyPair(myPair);
            setSigner(JcaSignatureFactory.getJavaSignature(mySignName, false));

            /* pass on call */
            super.initForVerify(pParams);
        }

        /**
         * Obtain algorithmName for keyPair.
         * @param pKeyPair the keyPair
         * @return the name
         */
        private static String getAlgorithmForKeyPair(final GordianKeyPair pKeyPair) {
            /* Determine the required signer */
            final GordianEdwardsElliptic myEdwards = pKeyPair.getKeyPairSpec().getEdwardsElliptic();
            final boolean is25519 = myEdwards.is25519();

            /* Build the algorithm */
            return is25519 ? "Ed25519" : "Ed448";
        }
    }

    /**
     * LMS signature.
     */
    static class JcaLMSSignature
            extends JcaSignature {
        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSignatureSpec the signatureSpec
         * @throws GordianException on error
         */
        JcaLMSSignature(final GordianCoreFactory pFactory,
                        final GordianSignatureSpec pSignatureSpec) throws GordianException {
            /* Initialise class */
            super(pFactory, pSignatureSpec);

            /* Create the signature class */
            setSigner(JcaSignatureFactory.getJavaSignature("LMS", true));
        }
    }
}
