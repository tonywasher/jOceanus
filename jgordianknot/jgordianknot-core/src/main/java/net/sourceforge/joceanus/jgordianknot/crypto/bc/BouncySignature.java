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
package net.sourceforge.joceanus.jgordianknot.crypto.bc;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.PSSSigner;
import org.bouncycastle.pqc.crypto.rainbow.RainbowSigner;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCS256Signer;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianConsumer;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSigner;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianValidator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyECPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyECPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyRSAPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyRSAPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyRainbowPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyRainbowPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncySPHINCSPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncySPHINCSPublicKey;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * BouncyCastle implementation of signature.
 */
public final class BouncySignature {
    /**
     * MGF1 Salt length.
     */
    private static final int MGF1_SALTLEN = 64;

    /**
     * Private constructor.
     */
    private BouncySignature() {
    }

    /**
     * PSS signature base.
     */
    public abstract static class BouncyPSSSignature
            implements GordianConsumer {
        /**
         * The RSA Signer.
         */
        private PSSSigner theSigner;

        /**
         * Set the signer.
         * @param pSigner the signer.
         */
        protected void setSigner(final PSSSigner pSigner) {
            theSigner = pSigner;
        }

        /**
         * Obtain the signer.
         * @return the signer.
         */
        protected PSSSigner getSigner() {
            return theSigner;
        }

        @Override
        public void update(final byte[] pBytes,
                           final int pOffset,
                           final int pLength) {
            theSigner.update(pBytes, pOffset, pLength);
        }

        @Override
        public void update(final byte pByte) {
            theSigner.update(pByte);
        }

        @Override
        public void update(final byte[] pBytes) {
            theSigner.update(pBytes, 0, pBytes.length);
        }
    }

    /**
     * Digest signature base.
     */
    public abstract static class BouncyDigestSignature
            implements GordianConsumer {
        /**
         * The Digest.
         */
        private final BouncyDigest theDigest;

        /**
         * Constructor.
         * @param pDigest the digest.
         */
        protected BouncyDigestSignature(final BouncyDigest pDigest) {
            theDigest = pDigest;
        }

        @Override
        public void update(final byte[] pBytes,
                           final int pOffset,
                           final int pLength) {
            theDigest.update(pBytes, pOffset, pLength);
        }

        @Override
        public void update(final byte pByte) {
            theDigest.update(pByte);
        }

        @Override
        public void update(final byte[] pBytes) {
            theDigest.update(pBytes, 0, pBytes.length);
        }

        /**
         * Obtain the calculated digest.
         * @return the digest.
         */
        protected byte[] getDigest() {
            return theDigest.finish();
        }
    }

    /**
     * RSA signer.
     */
    public static class BouncyRSASigner
            extends BouncyPSSSignature
            implements GordianSigner {
        /**
         * Constructor.
         * @param pPrivateKey the private key
         * @param pDigest the digest.
         * @param pRandom the secure Random
         */
        protected BouncyRSASigner(final BouncyRSAPrivateKey pPrivateKey,
                                  final BouncyDigest pDigest,
                                  final SecureRandom pRandom) {
            /* Create the RSAEngine and the signer */
            RSAEngine myEngine = new RSAEngine();
            setSigner(new PSSSigner(myEngine, pDigest.getDigest(), MGF1_SALTLEN));

            /* Initialise and set the signer */
            ParametersWithRandom myParms = new ParametersWithRandom(pPrivateKey.getPrivateKey(), pRandom);
            getSigner().init(true, myParms);
        }

        @Override
        public byte[] sign() throws OceanusException {
            /* Protect against exception */
            try {
                return getSigner().generateSignature();
            } catch (DataLengthException
                    | CryptoException e) {
                throw new GordianCryptoException("Failed to generate signature", e);
            }
        }
    }

    /**
     * RSA Validator.
     */
    public static class BouncyRSAValidator
            extends BouncyPSSSignature
            implements GordianValidator {
        /**
         * Constructor.
         * @param pPublicKey the public key
         * @param pDigest the digest.
         */
        protected BouncyRSAValidator(final BouncyRSAPublicKey pPublicKey,
                                     final BouncyDigest pDigest) {
            /* Create the RSAEngine and the signer */
            RSAEngine myEngine = new RSAEngine();
            setSigner(new PSSSigner(myEngine, pDigest.getDigest(), MGF1_SALTLEN));

            /* Initialise and set the Validator */
            getSigner().init(false, pPublicKey.getPublicKey());
        }

        @Override
        public boolean verify(final byte[] pSignature) {
            return getSigner().verifySignature(pSignature);
        }
    }

    /**
     * ECDSA signer.
     */
    public static class BouncyECDSASigner
            extends BouncyDigestSignature
            implements GordianSigner {
        /**
         * The ECDSA Signer.
         */
        private final ECDSASigner theSigner;

        /**
         * Constructor.
         * @param pPrivateKey the private key
         * @param pDigest the digest.
         * @param pRandom the secure Random
         */
        protected BouncyECDSASigner(final BouncyECPrivateKey pPrivateKey,
                                    final BouncyDigest pDigest,
                                    final SecureRandom pRandom) {
            /* Initialise underlying class */
            super(pDigest);

            /* Create the signer */
            theSigner = new ECDSASigner();

            /* Initialise and set the signer */
            ParametersWithRandom myParms = new ParametersWithRandom(pPrivateKey.getPrivateKey(), pRandom);
            theSigner.init(true, myParms);
        }

        @Override
        public byte[] sign() throws OceanusException {
            /* Protect against exceptions */
            try {
                BigInteger[] myValues = theSigner.generateSignature(getDigest());
                return dsaEncode(myValues[0], myValues[1]);

                /* Handle exceptions */
            } catch (IOException e) {
                throw new GordianCryptoException("Failed to generate signature", e);
            }
        }

        /**
         * BouncyCastle DSA Encoder. Copied from SignatureSpi.java
         * @param r first integer
         * @param s second integer
         * @return encoded set
         * @throws IOException on error
         */
        private static byte[] dsaEncode(final BigInteger r,
                                        final BigInteger s) throws IOException {
            ASN1EncodableVector v = new ASN1EncodableVector();

            v.add(new ASN1Integer(r));
            v.add(new ASN1Integer(s));

            return new DERSequence(v).getEncoded(ASN1Encoding.DER);
        }
    }

    /**
     * ECDSA validator.
     */
    public static class BouncyECDSAValidator
            extends BouncyDigestSignature
            implements GordianValidator {
        /**
         * The ECDSA Signer.
         */
        private final ECDSASigner theSigner;

        /**
         * Constructor.
         * @param pPublicKey the public key
         * @param pDigest the digest.
         */
        protected BouncyECDSAValidator(final BouncyECPublicKey pPublicKey,
                                       final BouncyDigest pDigest) {
            /* Initialise underlying class */
            super(pDigest);

            /* Create the signer */
            theSigner = new ECDSASigner();

            /* Initialise and set the signer */
            theSigner.init(false, pPublicKey.getPublicKey());
        }

        @Override
        public boolean verify(final byte[] pSignature) throws OceanusException {
            /* Protect against exceptions */
            try {
                BigInteger[] myValues = dsaDecode(pSignature);
                return theSigner.verifySignature(getDigest(), myValues[0], myValues[1]);

                /* Handle exceptions */
            } catch (IOException e) {
                throw new GordianCryptoException("Failed to parse signature", e);
            }
        }

        /**
         * BouncyCastle DSA Decoder. Copied from SignatureSpi.java
         * @param pEncoded the encode set
         * @return array of integers
         * @throws IOException on error
         */
        private static BigInteger[] dsaDecode(final byte[] pEncoded) throws IOException {
            ASN1Sequence s = (ASN1Sequence) ASN1Primitive.fromByteArray(pEncoded);
            BigInteger[] sig = new BigInteger[2];

            sig[0] = ASN1Integer.getInstance(s.getObjectAt(0)).getValue();
            sig[1] = ASN1Integer.getInstance(s.getObjectAt(1)).getValue();

            return sig;
        }
    }

    /**
     * SPHINCS signer.
     */
    public static class BouncySPHINCSSigner
            extends BouncyDigestSignature
            implements GordianSigner {
        /**
         * The SPHINCS Signer.
         */
        private final SPHINCS256Signer theSigner;

        /**
         * Constructor.
         * @param pPrivateKey the private key
         * @param pBaseDigest the tree digest.
         * @param pTreeDigest the tree digest.
         * @param pMsgDigest the message digest.
         * @param pRandom the secure Random
         */
        protected BouncySPHINCSSigner(final BouncySPHINCSPrivateKey pPrivateKey,
                                      final BouncyDigest pBaseDigest,
                                      final BouncyDigest pTreeDigest,
                                      final BouncyDigest pMsgDigest,
                                      final SecureRandom pRandom) {
            /* Initialise underlying class */
            super(pBaseDigest);

            /* Create the signer */
            theSigner = new SPHINCS256Signer(pTreeDigest.getDigest(), pMsgDigest.getDigest());

            /* Initialise and set the signer */
            theSigner.init(true, pPrivateKey.getPrivateKey());
        }

        @Override
        public byte[] sign() throws OceanusException {
            /* Sign the message */
            return theSigner.generateSignature(getDigest());
        }
    }

    /**
     * SPHINCS validator.
     */
    public static class BouncySPHINCSValidator
            extends BouncyDigestSignature
            implements GordianValidator {
        /**
         * The SPHINCS Signer.
         */
        private final SPHINCS256Signer theSigner;

        /**
         * Constructor.
         * @param pPublicKey the public key
         * @param pBaseDigest the base digest.
         * @param pTreeDigest the tree digest.
         * @param pMsgDigest the message digest.
         */
        protected BouncySPHINCSValidator(final BouncySPHINCSPublicKey pPublicKey,
                                         final BouncyDigest pBaseDigest,
                                         final BouncyDigest pTreeDigest,
                                         final BouncyDigest pMsgDigest) {
            /* Initialise underlying class */
            super(pBaseDigest);

            /* Create the signer */
            theSigner = new SPHINCS256Signer(pTreeDigest.getDigest(), pMsgDigest.getDigest());

            /* Initialise and set the signer */
            theSigner.init(false, pPublicKey.getPublicKey());
        }

        @Override
        public boolean verify(final byte[] pSignature) throws OceanusException {
            return theSigner.verifySignature(getDigest(), pSignature);
        }
    }

    /**
     * Rainbow signer.
     */
    public static class BouncyRainbowSigner
            extends BouncyDigestSignature
            implements GordianSigner {
        /**
         * The Rainbow Signer.
         */
        private final RainbowSigner theSigner;

        /**
         * Constructor.
         * @param pPrivateKey the private key
         * @param pDigest the digest.
         * @param pRandom the secure Random
         */
        protected BouncyRainbowSigner(final BouncyRainbowPrivateKey pPrivateKey,
                                      final BouncyDigest pDigest,
                                      final SecureRandom pRandom) {
            /* Initialise underlying class */
            super(pDigest);

            /* Create the signer */
            theSigner = new RainbowSigner();

            /* Initialise and set the signer */
            ParametersWithRandom myParms = new ParametersWithRandom(pPrivateKey.getPrivateKey(), pRandom);
            theSigner.init(true, myParms);
        }

        @Override
        public byte[] sign() throws OceanusException {
            /* Sign the message */
            return theSigner.generateSignature(getDigest());
        }
    }

    /**
     * Rainbow validator.
     */
    public static class BouncyRainbowValidator
            extends BouncyDigestSignature
            implements GordianValidator {
        /**
         * The Rainbow Signer.
         */
        private final RainbowSigner theSigner;

        /**
         * Constructor.
         * @param pPublicKey the public key
         * @param pDigest the digest.
         */
        protected BouncyRainbowValidator(final BouncyRainbowPublicKey pPublicKey,
                                         final BouncyDigest pDigest) {
            /* Initialise underlying class */
            super(pDigest);

            /* Create the signer */
            theSigner = new RainbowSigner();

            /* Initialise and set the signer */
            theSigner.init(false, pPublicKey.getPublicKey());
        }

        @Override
        public boolean verify(final byte[] pSignature) throws OceanusException {
            return theSigner.verifySignature(getDigest(), pSignature);
        }
    }
}
