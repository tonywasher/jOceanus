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
package net.sourceforge.joceanus.gordianknot.impl.bc;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import net.sourceforge.joceanus.gordianknot.impl.core.sign.GordianCoreSignature;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.crypto.signers.DSASigner;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.ECNRSigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;

import java.io.IOException;
import java.math.BigInteger;

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
            extends GordianCoreSignature {
        /**
         * The Digest.
         */
        private BouncyDigest theDigest;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the signatureSpec.
         * @throws GordianException on error
         */
        BouncyDigestSignature(final GordianCoreFactory pFactory,
                              final GordianSignatureSpec pSpec) throws GordianException {
            super(pFactory, pSpec);
            theDigest = pSpec.getSignatureSpec() == null
                        ? new BouncyDigest(null, new NullDigest())
                        : (BouncyDigest) getDigestFactory().createDigest(pSpec.getDigestSpec());
        }

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the signatureSpec.
         * @param pDigest the digest
         */
        BouncyDigestSignature(final GordianCoreFactory pFactory,
                              final GordianSignatureSpec pSpec,
                              final Digest pDigest) {
            super(pFactory, pSpec);
            theDigest = new BouncyDigest(pSpec.getDigestSpec(), pDigest);
        }

        /**
         * Set the digest.
         * @param pSpec the digestSpec.
         * @throws GordianException on error
         */
        protected void setDigest(final GordianDigestSpec pSpec) throws GordianException {
            theDigest = pSpec == null
                        ? new BouncyDigest(null, new NullDigest())
                        : (BouncyDigest) getDigestFactory().createDigest(pSpec);
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

        @Override
        protected BouncyKeyPair getKeyPair() {
            return (BouncyKeyPair) super.getKeyPair();
        }

        @Override
        public GordianCoreFactory getFactory() {
            return (GordianCoreFactory) super.getFactory();
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
         * @throws GordianException on error
         */
        byte[] dsaEncode(BigInteger r,
                         BigInteger s) throws GordianException;

        /**
         * Decode byte array into integersBouncyCastle DSA Decoder. Copied from SignatureSpi.java
         * @param pEncoded the encode set
         * @return array of integers
         * @throws GordianException on error
         */
        BigInteger[] dsaDecode(byte[] pEncoded) throws GordianException;
    }

    /**
     * DER encoder.
     */
    protected static final class BouncyDERCoder implements BouncyDSACoder {
        @Override
        public byte[] dsaEncode(final BigInteger r,
                                final BigInteger s) throws GordianException {
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
        public BigInteger[] dsaDecode(final byte[] pEncoded) throws GordianException {
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
     * Obtain DSASigner.
     * @param pFactory the factory
     * @param pSpec the signatureSpec
     * @return the ECSigner
     * @throws GordianException on error
     */
    static DSA getDSASigner(final GordianCoreFactory pFactory,
                            final GordianSignatureSpec pSpec) throws GordianException {
        /* Note if we are DSA */
        final boolean isDSA = GordianKeyPairType.DSA.equals(pSpec.getKeyPairType());

        /* Switch on signature type */
        switch (pSpec.getSignatureType()) {
            case DDSA:
                final BouncyDigest myDigest = (BouncyDigest) pFactory.getDigestFactory().createDigest(pSpec.getDigestSpec());
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
