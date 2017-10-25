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

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.signers.DSASigner;
import org.bouncycastle.crypto.signers.DSTU4145Signer;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.ECGOST3410_2012Signer;
import org.bouncycastle.crypto.signers.ECNRSigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.crypto.signers.SM2Signer;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianConsumer;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureSpec;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * BouncyCastle implementation of signature.
 */
public final class BouncySignature {
    /**
     * Signature generation error.
     */
    static final String ERROR_SIGGEN = "Failed to generate signature";

    /**
     * Signature validation error.
     */
    static final String ERROR_SIGPARSE = "Failed to parse signature";

    /**
     * Private constructor.
     */
    private BouncySignature() {
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
     * BouncyCastle DSA Encoder. Copied from SignatureSpi.java
     * @param r first integer
     * @param s second integer
     * @return encoded set
     * @throws OceanusException on error
     */
    static byte[] dsaEncode(final BigInteger r,
                            final BigInteger s) throws OceanusException {
        try {
            final ASN1EncodableVector v = new ASN1EncodableVector();

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
    static BigInteger[] dsaDecode(final byte[] pEncoded) throws OceanusException {
        try {
            final ASN1Sequence s = (ASN1Sequence) ASN1Primitive.fromByteArray(pEncoded);
            final BigInteger[] sig = new BigInteger[2];

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
    static DSA getDSASigner(final BouncyFactory pFactory,
                            final GordianAsymKeySpec pKeySpec,
                            final GordianSignatureSpec pSpec) throws OceanusException {
        /* Handle SM2/DSTU/GOST explicitly */
        if (GordianAsymKeyType.SM2.equals(pKeySpec.getKeyType())) {
            return new SM2Signer();
        }
        if (GordianAsymKeyType.DSTU4145.equals(pKeySpec.getKeyType())) {
            return new DSTU4145Signer();
        }
        if (GordianAsymKeyType.GOST2012.equals(pKeySpec.getKeyType())) {
            return new ECGOST3410_2012Signer();
        }

        /* Note if we are DSA */
        final boolean isDSA = GordianAsymKeyType.DSA.equals(pKeySpec.getKeyType());

        /* Switch on signature type */
        switch (pSpec.getSignatureType()) {
            case DDSA:
                final BouncyDigest myDigest = pFactory.createDigest(pSpec.getDigestSpec());
                final HMacDSAKCalculator myCalc = new HMacDSAKCalculator(myDigest.getDigest());
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
}
