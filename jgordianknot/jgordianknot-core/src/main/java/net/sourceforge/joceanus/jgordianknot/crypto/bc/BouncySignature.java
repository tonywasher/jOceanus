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
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.DSASigner;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.ECNRSigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.crypto.signers.ISO9796d2Signer;
import org.bouncycastle.crypto.signers.PSSSigner;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.crypto.signers.X931Signer;
import org.bouncycastle.pqc.crypto.rainbow.RainbowSigner;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCS256Signer;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianConsumer;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianLength;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSigner;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianValidator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyDSAPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyDSAPublicKey;
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
     * Signature generation error.
     */
    private static final String ERROR_SIGGEN = "Failed to generate signature";

    /**
     * Signature validation error.
     */
    private static final String ERROR_SIGPARSE = "Failed to parse signature";

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
        private final Signer theSigner;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error
         */
        protected BouncyPSSSignature(final BouncyFactory pFactory,
                                     final GordianSignatureSpec pSpec) throws OceanusException {
            theSigner = getRSASigner(pFactory, pSpec);
        }

        /**
         * Obtain the signer.
         * @return the signer.
         */
        protected Signer getSigner() {
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

        /**
         * Obtain RSASigner.
         * @param pFactory the factory
         * @param pSpec the signatureSpec
         * @return the RSASigner
         * @throws OceanusException on error
         */
        private static Signer getRSASigner(final BouncyFactory pFactory,
                                           final GordianSignatureSpec pSpec) throws OceanusException {
            /* Create the digest */
            BouncyDigest myDigest = pFactory.createDigest(pSpec.getDigestSpec());

            /* Access the signature type */
            switch (pSpec.getSignatureType()) {
                case ISO9796D2:
                    return new ISO9796d2Signer(new RSABlindedEngine(), myDigest.getDigest(), true);
                case X931:
                    return new X931Signer(new RSABlindedEngine(), myDigest.getDigest(), true);
                case PSS:
                default:
                    return new PSSSigner(new RSAEngine(), myDigest.getDigest(), MGF1_SALTLEN);
            }
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
         * @param pFactory the factory
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error
         */
        protected BouncyDigestSignature(final BouncyFactory pFactory,
                                        final GordianSignatureSpec pSpec) throws OceanusException {
            theDigest = pFactory.createDigest(pSpec.getDigestSpec());
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
         * @param pFactory the factory
         * @param pPrivateKey the private key
         * @param pSpec the signatureSpec
         * @param pRandom the secure Random
         * @throws OceanusException on error
         */
        protected BouncyRSASigner(final BouncyFactory pFactory,
                                  final BouncyRSAPrivateKey pPrivateKey,
                                  final GordianSignatureSpec pSpec,
                                  final SecureRandom pRandom) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Initialise and set the signer */
            CipherParameters myParms = GordianSignatureType.PSS.equals(pSpec.getSignatureType())
                                                                                                 ? new ParametersWithRandom(pPrivateKey.getPrivateKey(), pRandom)
                                                                                                 : pPrivateKey.getPrivateKey();
            getSigner().init(true, myParms);
        }

        @Override
        public byte[] sign() throws OceanusException {
            /* Protect against exception */
            try {
                return getSigner().generateSignature();
            } catch (DataLengthException
                    | CryptoException e) {
                throw new GordianCryptoException(ERROR_SIGGEN, e);
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
         * @param pFactory the factory
         * @param pPublicKey the public key
         * @param pSpec the signatureSpec
         * @throws OceanusException on error
         */
        protected BouncyRSAValidator(final BouncyFactory pFactory,
                                     final BouncyRSAPublicKey pPublicKey,
                                     final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Initialise and set the Validator */
            getSigner().init(false, pPublicKey.getPublicKey());
        }

        @Override
        public boolean verify(final byte[] pSignature) {
            return getSigner().verifySignature(pSignature);
        }
    }

    /**
     * EC signer.
     */
    public static class BouncyECSigner
            extends BouncyDigestSignature
            implements GordianSigner {
        /**
         * The Signer.
         */
        private final DSA theSigner;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pPrivateKey the private key
         * @param pSpec the signatureSpec.
         * @param pRandom the random generator
         * @throws OceanusException on error
         */
        protected BouncyECSigner(final BouncyFactory pFactory,
                                 final BouncyECPrivateKey pPrivateKey,
                                 final GordianSignatureSpec pSpec,
                                 final SecureRandom pRandom) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the signer */
            theSigner = getDSASigner(pFactory, pPrivateKey.getKeySpec(), pSpec);

            /* Initialise and set the signer */
            ParametersWithRandom myParms = new ParametersWithRandom(pPrivateKey.getPrivateKey(), pRandom);
            theSigner.init(true, myParms);
        }

        @Override
        public byte[] sign() throws OceanusException {
            BigInteger[] myValues = theSigner.generateSignature(getDigest());
            return dsaEncode(myValues[0], myValues[1]);
        }
    }

    /**
     * EC validator.
     */
    public static class BouncyECValidator
            extends BouncyDigestSignature
            implements GordianValidator {
        /**
         * The EC Signer.
         */
        private final DSA theSigner;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pPublicKey the public key
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error-
         */
        protected BouncyECValidator(final BouncyFactory pFactory,
                                    final BouncyECPublicKey pPublicKey,
                                    final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the signer */
            theSigner = getDSASigner(pFactory, pPublicKey.getKeySpec(), pSpec);

            /* Initialise and set the signer */
            theSigner.init(false, pPublicKey.getPublicKey());
        }

        @Override
        public boolean verify(final byte[] pSignature) throws OceanusException {
            BigInteger[] myValues = dsaDecode(pSignature);
            return theSigner.verifySignature(getDigest(), myValues[0], myValues[1]);
        }
    }

    /**
     * BouncyCastle DSA Encoder. Copied from SignatureSpi.java
     * @param r first integer
     * @param s second integer
     * @return encoded set
     * @throws OceanusException on error
     */
    private static byte[] dsaEncode(final BigInteger r,
                                    final BigInteger s) throws OceanusException {
        try {
            ASN1EncodableVector v = new ASN1EncodableVector();

            v.add(new ASN1Integer(r));
            v.add(new ASN1Integer(s));

            return new DERSequence(v).getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            throw new GordianCryptoException(ERROR_SIGGEN, e);
        }
    }

    /**
     * BouncyCastle DSA Decoder. Copied from SignatureSpi.java
     * @param pEncoded the encode set
     * @return array of integers
     * @throws OceanusException on error
     */
    private static BigInteger[] dsaDecode(final byte[] pEncoded) throws OceanusException {
        try {
            ASN1Sequence s = (ASN1Sequence) ASN1Primitive.fromByteArray(pEncoded);
            BigInteger[] sig = new BigInteger[2];

            sig[0] = ASN1Integer.getInstance(s.getObjectAt(0)).getValue();
            sig[1] = ASN1Integer.getInstance(s.getObjectAt(1)).getValue();

            return sig;
        } catch (IOException e) {
            throw new GordianCryptoException(ERROR_SIGPARSE, e);
        }
    }

    /**
     * Obtain DSASigner.
     * @param pFactory the factory
     * @param pKeySpec the keySpec
     * @param pSpec the signatureSpec
     * @return the ECSigner
     * @throws OceanusException on error
     */
    private static DSA getDSASigner(final BouncyFactory pFactory,
                                    final GordianAsymKeySpec pKeySpec,
                                    final GordianSignatureSpec pSpec) throws OceanusException {
        /* Handle SM2 explicitly */
        if (GordianAsymKeyType.SM2.equals(pKeySpec.getKeyType())) {
            return new SM2Signer();
        }

        /* Note if we are DSA */
        boolean isDSA = GordianAsymKeyType.DSA.equals(pKeySpec.getKeyType());

        /* Switch on signature type */
        switch (pSpec.getSignatureType()) {
            case DDSA:
                BouncyDigest myDigest = pFactory.createDigest(pSpec.getDigestSpec());
                HMacDSAKCalculator myCalc = new HMacDSAKCalculator(myDigest.getDigest());
                return isDSA
                             ? new DSASigner(myCalc)
                             : new ECDSASigner(myCalc);
            case NR:
                return new ECNRSigner();
            case DSA:
            default:
                return isDSA
                             ? new DSASigner()
                             : new ECDSASigner();
        }
    }

    /**
     * DSA signer.
     */
    public static class BouncyDSASigner
            extends BouncyDigestSignature
            implements GordianSigner {
        /**
         * The Signer.
         */
        private final DSA theSigner;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pPrivateKey the private key
         * @param pSpec the signatureSpec.
         * @param pRandom the random generator
         * @throws OceanusException on error
         */
        protected BouncyDSASigner(final BouncyFactory pFactory,
                                  final BouncyDSAPrivateKey pPrivateKey,
                                  final GordianSignatureSpec pSpec,
                                  final SecureRandom pRandom) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the signer */
            theSigner = getDSASigner(pFactory, pPrivateKey.getKeySpec(), pSpec);

            /* Initialise and set the signer */
            ParametersWithRandom myParms = new ParametersWithRandom(pPrivateKey.getPrivateKey(), pRandom);
            theSigner.init(true, myParms);
        }

        @Override
        public byte[] sign() throws OceanusException {
            BigInteger[] myValues = theSigner.generateSignature(getDigest());
            return dsaEncode(myValues[0], myValues[1]);
        }
    }

    /**
     * DSA validator.
     */
    public static class BouncyDSAValidator
            extends BouncyDigestSignature
            implements GordianValidator {
        /**
         * The EC Signer.
         */
        private final DSA theSigner;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pPublicKey the public key
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error-
         */
        protected BouncyDSAValidator(final BouncyFactory pFactory,
                                     final BouncyDSAPublicKey pPublicKey,
                                     final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the signer */
            theSigner = getDSASigner(pFactory, pPublicKey.getKeySpec(), pSpec);

            /* Initialise and set the signer */
            theSigner.init(false, pPublicKey.getPublicKey());
        }

        @Override
        public boolean verify(final byte[] pSignature) throws OceanusException {
            BigInteger[] myValues = dsaDecode(pSignature);
            return theSigner.verifySignature(getDigest(), myValues[0], myValues[1]);
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
         * @param pFactory the factory
         * @param pPrivateKey the private key
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error
         */
        protected BouncySPHINCSSigner(final BouncyFactory pFactory,
                                      final BouncySPHINCSPrivateKey pPrivateKey,
                                      final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the internal digests */
            BouncyDigest myTreeDigest = pFactory.createDigest(GordianDigestSpec.sha3(GordianLength.LEN_256));
            BouncyDigest myMsgDigest = pFactory.createDigest(GordianDigestSpec.sha3(GordianLength.LEN_512));

            /* Create the signer */
            theSigner = new SPHINCS256Signer(myTreeDigest.getDigest(), myMsgDigest.getDigest());

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
         * @param pFactory the factory
         * @param pPublicKey the public key
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error
         */
        protected BouncySPHINCSValidator(final BouncyFactory pFactory,
                                         final BouncySPHINCSPublicKey pPublicKey,
                                         final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Create the internal digests */
            BouncyDigest myTreeDigest = pFactory.createDigest(GordianDigestSpec.sha3(GordianLength.LEN_256));
            BouncyDigest myMsgDigest = pFactory.createDigest(GordianDigestSpec.sha3(GordianLength.LEN_512));

            /* Create the signer */
            theSigner = new SPHINCS256Signer(myTreeDigest.getDigest(), myMsgDigest.getDigest());

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
         * @param pFactory the factory
         * @param pPrivateKey the private key
         * @param pSpec the signatureSpec.
         * @param pRandom the secure Random
         * @throws OceanusException on error
         */
        protected BouncyRainbowSigner(final BouncyFactory pFactory,
                                      final BouncyRainbowPrivateKey pPrivateKey,
                                      final GordianSignatureSpec pSpec,
                                      final SecureRandom pRandom) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

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
         * @param pFactory the factory
         * @param pPublicKey the public key
         * @param pSpec the signatureSpec.
         * @throws OceanusException on error
         */
        protected BouncyRainbowValidator(final BouncyFactory pFactory,
                                         final BouncyRainbowPublicKey pPublicKey,
                                         final GordianSignatureSpec pSpec) throws OceanusException {
            /* Initialise underlying class */
            super(pFactory, pSpec);

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
