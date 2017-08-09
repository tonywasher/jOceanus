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

import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianConsumer;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSigner;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianValidator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPair.JcaPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPair.JcaPublicKey;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Jca implementation of signature.
 */
public abstract class JcaSignature
        implements GordianConsumer {
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
    private static final String SPHINCS_ALGOBASE = "withSPHINCS256";

    /**
     * The Rainbow Signature.
     */
    private static final String RAINBOW_ALGOBASE = "withRainbow";

    /**
     * The RSA Signer.
     */
    private Signature theSigner;

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

    /**
     * RSA signer.
     */
    public static class JcaRSASigner
            extends JcaSignature
            implements GordianSigner {
        /**
         * Constructor.
         * @param pPrivateKey the private key
         * @param pSignatureSpec the signatureSpec
         * @param pRandom the secure random
         * @throws OceanusException on error
         */
        protected JcaRSASigner(final JcaPrivateKey pPrivateKey,
                               final GordianSignatureSpec pSignatureSpec,
                               final SecureRandom pRandom) throws OceanusException {
            /* Create the PSSParameterSpec */
            try {
                final String myDigest = JcaDigest.getSignAlgorithm(pSignatureSpec.getDigestSpec());
                setSigner(JcaFactory.getJavaSignature(myDigest + getSignatureBase(pPrivateKey.getKeySpec(), pSignatureSpec), false));

                /* Initialise and set the signer */
                getSigner().initSign(pPrivateKey.getPrivateKey(), pRandom);

                /* Catch exceptions */
            } catch (InvalidKeyException e) {
                throw new GordianCryptoException(SIG_ERROR, e);
            }
        }

        @Override
        public byte[] sign() throws OceanusException {
            /* Protect against exception */
            try {
                return getSigner().sign();
            } catch (SignatureException e) {
                throw new GordianCryptoException(SIG_ERROR, e);
            }
        }
    }

    /**
     * RSA Validator.
     */
    public static class JcaRSAValidator
            extends JcaSignature
            implements GordianValidator {
        /**
         * Constructor.
         * @param pPublicKey the public key
         * @param pSignatureSpec the signatureSpec
         * @throws OceanusException on error
         */
        protected JcaRSAValidator(final JcaPublicKey pPublicKey,
                                  final GordianSignatureSpec pSignatureSpec) throws OceanusException {
            /* Create the PSSParameterSpec */
            try {
                final String myDigest = JcaDigest.getSignAlgorithm(pSignatureSpec.getDigestSpec());
                setSigner(JcaFactory.getJavaSignature(myDigest + getSignatureBase(pPublicKey.getKeySpec(), pSignatureSpec), false));

                /* Initialise and set the signer */
                getSigner().initVerify(pPublicKey.getPublicKey());

                /* Catch exceptions */
            } catch (InvalidKeyException e) {
                throw new GordianCryptoException(SIG_ERROR, e);
            }
        }

        @Override
        public boolean verify(final byte[] pSignature) throws OceanusException {
            /* Protect against exception */
            try {
                return getSigner().verify(pSignature);
            } catch (SignatureException e) {
                throw new GordianCryptoException(SIG_ERROR, e);
            }
        }
    }

    /**
     * Obtain Signer base.
     * @param pKeySpec the keySpec
     * @param pSignatureSpec the signatureSpec
     * @return the base
     */
    private static String getSignatureBase(final GordianAsymKeySpec pKeySpec,
                                           final GordianSignatureSpec pSignatureSpec) {
        /* Handle SM2 explicitly */
        if (GordianAsymKeyType.SM2.equals(pKeySpec.getKeyType())) {
            return EC_SM2_ALGOBASE;
        }

        /* Note if we are DSA */
        final boolean isDSA = GordianAsymKeyType.DSA.equals(pKeySpec.getKeyType());

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
    public static class JcaDSASigner
            extends JcaSignature
            implements GordianSigner {
        /**
         * Constructor.
         * @param pPrivateKey the private key
         * @param pSignatureSpec the signatureSpec
         * @param pRandom the secure random
         * @throws OceanusException on error
         */
        protected JcaDSASigner(final JcaPrivateKey pPrivateKey,
                               final GordianSignatureSpec pSignatureSpec,
                               final SecureRandom pRandom) throws OceanusException {
            /* Create the Signer */
            try {
                final String myDigest = JcaDigest.getAlgorithm(pSignatureSpec.getDigestSpec());
                setSigner(JcaFactory.getJavaSignature(myDigest + getSignatureBase(pPrivateKey.getKeySpec(), pSignatureSpec), false));

                /* Initialise and set the signer */
                getSigner().initSign(pPrivateKey.getPrivateKey(), pRandom);

                /* Catch exceptions */
            } catch (InvalidKeyException e) {
                throw new GordianCryptoException(SIG_ERROR, e);
            }
        }

        @Override
        public byte[] sign() throws OceanusException {
            /* Protect against exception */
            try {
                return getSigner().sign();
            } catch (SignatureException e) {
                throw new GordianCryptoException(SIG_ERROR, e);
            }
        }
    }

    /**
     * DSA Validator.
     */
    public static class JcaDSAValidator
            extends JcaSignature
            implements GordianValidator {
        /**
         * Constructor.
         * @param pPublicKey the public key
         * @param pSignatureSpec the signatureSpec
         * @throws OceanusException on error
         */
        protected JcaDSAValidator(final JcaPublicKey pPublicKey,
                                  final GordianSignatureSpec pSignatureSpec) throws OceanusException {
            /* Create the PSSParameterSpec */
            try {
                final String myDigest = JcaDigest.getAlgorithm(pSignatureSpec.getDigestSpec());
                setSigner(JcaFactory.getJavaSignature(myDigest + getSignatureBase(pPublicKey.getKeySpec(), pSignatureSpec), false));

                /* Initialise and set the signer */
                getSigner().initVerify(pPublicKey.getPublicKey());

                /* Catch exceptions */
            } catch (InvalidKeyException e) {
                throw new GordianCryptoException(SIG_ERROR, e);
            }
        }

        @Override
        public boolean verify(final byte[] pSignature) throws OceanusException {
            /* Protect against exception */
            try {
                return getSigner().verify(pSignature);
            } catch (SignatureException e) {
                throw new GordianCryptoException(SIG_ERROR, e);
            }
        }
    }

    /**
     * SPHINCS signer.
     */
    public static class JcaSPHINCSSigner
            extends JcaSignature
            implements GordianSigner {
        /**
         * Constructor.
         * @param pPrivateKey the private key
         * @param pSignatureSpec the signatureSpec
         * @throws OceanusException on error
         */
        protected JcaSPHINCSSigner(final JcaPrivateKey pPrivateKey,
                                   final GordianSignatureSpec pSignatureSpec) throws OceanusException {
            /* Create the Signer */
            try {
                final String myDigest = JcaDigest.getAlgorithm(pSignatureSpec.getDigestSpec());
                setSigner(JcaFactory.getJavaSignature(myDigest + SPHINCS_ALGOBASE, true));

                /* Initialise and set the signer */
                getSigner().initSign(pPrivateKey.getPrivateKey());

                /* Catch exceptions */
            } catch (InvalidKeyException e) {
                throw new GordianCryptoException(SIG_ERROR, e);
            }
        }

        @Override
        public byte[] sign() throws OceanusException {
            /* Protect against exception */
            try {
                return getSigner().sign();
            } catch (SignatureException e) {
                throw new GordianCryptoException(SIG_ERROR, e);
            }
        }
    }

    /**
     * SPHINCS Validator.
     */
    public static class JcaSPHINCSValidator
            extends JcaSignature
            implements GordianValidator {
        /**
         * Constructor.
         * @param pPublicKey the public key
         * @param pSignatureSpec the signatureSpec
         * @throws OceanusException on error
         */
        protected JcaSPHINCSValidator(final JcaPublicKey pPublicKey,
                                      final GordianSignatureSpec pSignatureSpec) throws OceanusException {
            /* Create the PSSParameterSpec */
            try {
                final String myDigest = JcaDigest.getAlgorithm(pSignatureSpec.getDigestSpec());
                setSigner(JcaFactory.getJavaSignature(myDigest + SPHINCS_ALGOBASE, true));

                /* Initialise and set the signer */
                getSigner().initVerify(pPublicKey.getPublicKey());

                /* Catch exceptions */
            } catch (InvalidKeyException e) {
                throw new GordianCryptoException(SIG_ERROR, e);
            }
        }

        @Override
        public boolean verify(final byte[] pSignature) throws OceanusException {
            /* Protect against exception */
            try {
                return getSigner().verify(pSignature);
            } catch (SignatureException e) {
                throw new GordianCryptoException(SIG_ERROR, e);
            }
        }
    }

    /**
     * Rainbow signer.
     */
    public static class JcaRainbowSigner
            extends JcaSignature
            implements GordianSigner {
        /**
         * Constructor.
         * @param pPrivateKey the private key
         * @param pSignatureSpec the signatureSpec
         * @param pRandom the secure random
         * @throws OceanusException on error
         */
        protected JcaRainbowSigner(final JcaPrivateKey pPrivateKey,
                                   final GordianSignatureSpec pSignatureSpec,
                                   final SecureRandom pRandom) throws OceanusException {
            /* Create the Signer */
            try {
                final String myDigest = JcaDigest.getAlgorithm(pSignatureSpec.getDigestSpec());
                setSigner(JcaFactory.getJavaSignature(myDigest + RAINBOW_ALGOBASE, true));

                /* Initialise and set the signer */
                getSigner().initSign(pPrivateKey.getPrivateKey(), pRandom);

                /* Catch exceptions */
            } catch (InvalidKeyException e) {
                throw new GordianCryptoException(SIG_ERROR, e);
            }
        }

        @Override
        public byte[] sign() throws OceanusException {
            /* Protect against exception */
            try {
                return getSigner().sign();
            } catch (SignatureException e) {
                throw new GordianCryptoException(SIG_ERROR, e);
            }
        }
    }

    /**
     * Rainbow Validator.
     */
    public static class JcaRainbowValidator
            extends JcaSignature
            implements GordianValidator {
        /**
         * Constructor.
         * @param pPublicKey the public key
         * @param pSignatureSpec the signatureSpec
         * @throws OceanusException on error
         */
        protected JcaRainbowValidator(final JcaPublicKey pPublicKey,
                                      final GordianSignatureSpec pSignatureSpec) throws OceanusException {
            /* Create the PSSParameterSpec */
            try {
                final String myDigest = JcaDigest.getAlgorithm(pSignatureSpec.getDigestSpec());
                setSigner(JcaFactory.getJavaSignature(myDigest + RAINBOW_ALGOBASE, true));

                /* Initialise and set the signer */
                getSigner().initVerify(pPublicKey.getPublicKey());

                /* Catch exceptions */
            } catch (InvalidKeyException e) {
                throw new GordianCryptoException(SIG_ERROR, e);
            }
        }

        @Override
        public boolean verify(final byte[] pSignature) throws OceanusException {
            /* Protect against exception */
            try {
                return getSigner().verify(pSignature);
            } catch (SignatureException e) {
                throw new GordianCryptoException(SIG_ERROR, e);
            }
        }
    }
}
