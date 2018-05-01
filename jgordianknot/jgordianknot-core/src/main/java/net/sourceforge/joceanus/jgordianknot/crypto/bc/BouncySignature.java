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

        @Override
        public void reset() {
            theDigest.reset();
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
     * DSACoder interface.
     */
    interface BouncyDSACoder {
        /**
         * Encode integers into byte array.
         * @param r first integer
         * @param s second integer
         * @return encoded set
         * @throws OceanusException on error
         */
        byte[] dsaEncode(BigInteger r,
                         BigInteger s) throws OceanusException;

        /**
         * Decode byte array into integersBouncyCastle DSA Decoder. Copied from SignatureSpi.java
         * @param pEncoded the encode set
         * @return array of integers
         * @throws OceanusException on error
         */
        BigInteger[] dsaDecode(byte[] pEncoded) throws OceanusException;
    }

    /**
     * DER encoder.
     */
    private static final class BouncyDERCoder implements BouncyDSACoder {
        @Override
        public byte[] dsaEncode(final BigInteger r,
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

        @Override
        public BigInteger[] dsaDecode(final byte[] pEncoded) throws OceanusException {
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
    }

    /**
     * Plain encoder.
     */
    private static final class BouncyPlainCoder implements BouncyDSACoder {
        @Override
        public byte[] dsaEncode(final BigInteger r,
                                final BigInteger s) throws OceanusException {
            /* Access byteArrays */
            final byte[] myFirst = makeUnsigned(r);
            final byte[] mySecond = makeUnsigned(s);
            final byte[] myResult = myFirst.length > mySecond.length
                                                                     ? new byte[myFirst.length * 2]
                                                                     : new byte[mySecond.length * 2];

            /* Build array and return */
            System.arraycopy(myFirst, 0, myResult, myResult.length / 2 - myFirst.length, myFirst.length);
            System.arraycopy(mySecond, 0, myResult, myResult.length - mySecond.length, mySecond.length);
            return myResult;
        }

        /**
         * Make the value unsigned.
         * @param pValue the value
         * @return the unsigned value
         */
        private static byte[] makeUnsigned(final BigInteger pValue) {
            /* Convert to byteArray and return if OK */
            final byte[] myResult = pValue.toByteArray();
            if (myResult[0] != 0) {
                return myResult;
            }

            /* Shorten the array */
            final byte[] myTmp = new byte[myResult.length - 1];
            System.arraycopy(myResult, 1, myTmp, 0, myTmp.length);
            return myTmp;
        }

        @Override
        public BigInteger[] dsaDecode(final byte[] pEncoded) throws OceanusException {
            /* Build the value arrays */
            final byte[] myFirst = new byte[pEncoded.length / 2];
            final byte[] mySecond = new byte[pEncoded.length / 2];
            System.arraycopy(pEncoded, 0, myFirst, 0, myFirst.length);
            System.arraycopy(pEncoded, myFirst.length, mySecond, 0, mySecond.length);

            /* Create the signature values and return */
            final BigInteger[] sig = new BigInteger[2];
            sig[0] = new BigInteger(1, myFirst);
            sig[1] = new BigInteger(1, mySecond);
            return sig;
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
        /* Handle DSTU/GOST explicitly */
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

    /**
     * Obtain DSACoder.
     * @param pKeySpec the keySpec
     * @return the ECCoder
     */
    static BouncyDSACoder getDSACoder(final GordianAsymKeySpec pKeySpec) {
        switch (pKeySpec.getKeyType()) {
            case DSTU4145:
            case GOST2012:
                return new BouncyPlainCoder();
            case EC:
            case DSA:
            case SM2:
            default:
                return new BouncyDERCoder();
        }
    }
}
