/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2016 Tony Washer
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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianConsumer;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
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
     * The RSA Algorithm.
     */
    private static final String RSA_ALGO = "SHA512withRSAandMGF1";

    /**
     * The ECDSA Signature.
     */
    private static final String ECDSA_ALGO = "SHA512withECDSA";

    /**
     * The MGF1 Signature.
     */
    private static final String MGF1_ALGO = "MGF1";

    /**
     * The SHA2 Signature.
     */
    private static final String SHA2_ALGO = "SHA512";

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
         * @param pRandom the secure random
         * @throws OceanusException on error
         */
        protected JcaRSASigner(final JcaPrivateKey pPrivateKey,
                               final SecureRandom pRandom) throws OceanusException {
            /* Create the PSSParameterSpec */
            try {
                setSigner(Signature.getInstance(RSA_ALGO));
                PSSParameterSpec myPSSParms = new PSSParameterSpec(SHA2_ALGO, MGF1_ALGO,
                        MGF1ParameterSpec.SHA512, GordianKeyPair.MGF1_SALTLEN, 1);
                getSigner().setParameter(myPSSParms);

                /* Initialise and set the signer */
                getSigner().initSign(pPrivateKey.getPrivateKey(), pRandom);

                /* Catch exceptions */
            } catch (NoSuchAlgorithmException
                    | InvalidAlgorithmParameterException
                    | InvalidKeyException e) {
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
         * @throws OceanusException on error
         */
        protected JcaRSAValidator(final JcaPublicKey pPublicKey) throws OceanusException {
            /* Create the PSSParameterSpec */
            try {
                setSigner(Signature.getInstance(RSA_ALGO));
                PSSParameterSpec myPSSParms = new PSSParameterSpec(SHA2_ALGO, MGF1_ALGO,
                        MGF1ParameterSpec.SHA512, GordianKeyPair.MGF1_SALTLEN, 1);
                getSigner().setParameter(myPSSParms);

                /* Initialise and set the signer */
                getSigner().initVerify(pPublicKey.getPublicKey());

                /* Catch exceptions */
            } catch (NoSuchAlgorithmException
                    | InvalidAlgorithmParameterException
                    | InvalidKeyException e) {
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
     * ECDSA signer.
     */
    public static class JcaECDSASigner
            extends JcaSignature
            implements GordianSigner {
        /**
         * Constructor.
         * @param pPrivateKey the private key
         * @param pRandom the secure random
         * @throws OceanusException on error
         */
        protected JcaECDSASigner(final JcaPrivateKey pPrivateKey,
                                 final SecureRandom pRandom) throws OceanusException {
            /* Create the Signer */
            try {
                setSigner(Signature.getInstance(ECDSA_ALGO));

                /* Initialise and set the signer */
                getSigner().initSign(pPrivateKey.getPrivateKey(), pRandom);

                /* Catch exceptions */
            } catch (NoSuchAlgorithmException
                    | InvalidKeyException e) {
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
     * ECDSA Validator.
     */
    public static class JcaECDSAValidator
            extends JcaSignature
            implements GordianValidator {
        /**
         * Constructor.
         * @param pPublicKey the public key
         * @throws OceanusException on error
         */
        protected JcaECDSAValidator(final JcaPublicKey pPublicKey) throws OceanusException {
            /* Create the PSSParameterSpec */
            try {
                setSigner(Signature.getInstance(ECDSA_ALGO));

                /* Initialise and set the signer */
                getSigner().initVerify(pPublicKey.getPublicKey());

                /* Catch exceptions */
            } catch (NoSuchAlgorithmException
                    | InvalidKeyException e) {
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
