package net.sourceforge.joceanus.jgordianknot.junit.patches;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.digests.TigerDigest;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.security.SecureRandom;

public class ECNRBoundary {
    /**
     * Main entry.
     * @param pArgs the arguments
     */
    public static void main(final String[] pArgs) {
        /* Create the generator */
        ECKeyPairGenerator myGenerator = new ECKeyPairGenerator();
        SecureRandom myRandom = new SecureRandom();
        String myCurve = "brainpoolP192t1";

        /* Lookup the parameters */
        final X9ECParameters x9 = ECNamedCurveTable.getByName(myCurve);

        /* Initialise the generator */
        final ASN1ObjectIdentifier myOid = ECUtil.getNamedCurveOid(myCurve);
        ECNamedDomainParameters myDomain = new ECNamedDomainParameters(myOid, x9.getCurve(), x9.getG(), x9.getN(), x9.getH(), x9.getSeed());
        ECKeyGenerationParameters myParams = new ECKeyGenerationParameters(myDomain, myRandom);
        myGenerator.init(myParams);

        /* Create the key Pair */
        AsymmetricCipherKeyPair myPair = myGenerator.generateKeyPair();

        /* Create the digest and the output buffer */
        Digest myDigest = new TigerDigest();
        byte[] myArtifact = new byte[myDigest.getDigestSize()];
        final byte[] myMessage = "Hello there. How is life treating you?".getBytes();
        myDigest.update(myMessage, 0, myMessage.length);
        myDigest.doFinal(myArtifact, 0);

        /* Create signer */
        ECNRSigner mySigner = new ECNRSigner();
        mySigner.init(true, myPair.getPrivate());
        BigInteger[] mySignature = mySigner.generateSignature(myArtifact);

        /* verify signature */
        mySigner.init(false, myPair.getPublic());
        if (!mySigner.verifySignature(myArtifact, mySignature[0], mySignature[1])) {
            System.out.println("ECNR failed");
        }
    }

    public static class ECNRSigner
            implements DSAExt
    {
        private boolean             forSigning;
        private ECKeyParameters key;
        private SecureRandom random;

        public void init(
                boolean          forSigning,
                CipherParameters param)
        {
            this.forSigning = forSigning;

            if (forSigning)
            {
                if (param instanceof ParametersWithRandom)
                {
                    ParametersWithRandom    rParam = (ParametersWithRandom)param;

                    this.random = rParam.getRandom();
                    this.key = (ECPrivateKeyParameters)rParam.getParameters();
                }
                else
                {
                    this.random = CryptoServicesRegistrar.getSecureRandom();
                    this.key = (ECPrivateKeyParameters)param;
                }
            }
            else
            {
                this.key = (ECPublicKeyParameters)param;
            }
        }

        public BigInteger getOrder()
        {
            return key.getParameters().getN();
        }

        // Section 7.2.5 ECSP-NR, pg 34
        /**
         * generate a signature for the given message using the key we were
         * initialised with.  Generally, the order of the curve should be at
         * least as long as the hash of the message of interest, and with
         * ECNR it *must* be at least as long.
         *
         * @param digest  the digest to be signed.
         * @exception DataLengthException if the digest is longer than the key allows
         */
        public BigInteger[] generateSignature(
                byte[] digest)
        {
            if (!this.forSigning)
            {
                throw new IllegalStateException("not initialised for signing");
            }

            BigInteger n = getOrder();
            int nBitLength = n.bitLength();

            BigInteger e = new BigInteger(1, digest);
            int eBitLength = e.bitLength();

            ECPrivateKeyParameters  privKey = (ECPrivateKeyParameters)key;

            if (eBitLength > nBitLength)
            {
                throw new DataLengthException("input too large for ECNR key.");
            }

            BigInteger r = null;
            BigInteger s = null;

            AsymmetricCipherKeyPair tempPair;
            do // generate r
            {
                // generate another, but very temporary, key pair using
                // the same EC parameters
                ECKeyPairGenerator keyGen = new ECKeyPairGenerator();

                keyGen.init(new ECKeyGenerationParameters(privKey.getParameters(), this.random));

                tempPair = keyGen.generateKeyPair();

                //    BigInteger Vx = tempPair.getPublic().getW().getAffineX();
                ECPublicKeyParameters V = (ECPublicKeyParameters)tempPair.getPublic();        // get temp's public key
                BigInteger Vx = V.getQ().getAffineXCoord().toBigInteger();                    // get the point's x coordinate

                r = Vx.add(e).mod(n);
            }
            while (r.equals(ECConstants.ZERO));

            // generate s
            BigInteger x = privKey.getD();                // private key value
            BigInteger u = ((ECPrivateKeyParameters)tempPair.getPrivate()).getD();    // temp's private key value
            s = u.subtract(r.multiply(x)).mod(n);

            BigInteger[]  res = new BigInteger[2];
            res[0] = r;
            res[1] = s;

            return res;
        }

        // Section 7.2.6 ECVP-NR, pg 35
        /**
         * return true if the value r and s represent a signature for the
         * message passed in. Generally, the order of the curve should be at
         * least as long as the hash of the message of interest, and with
         * ECNR, it *must* be at least as long.  But just in case the signer
         * applied mod(n) to the longer digest, this implementation will
         * apply mod(n) during verification.
         *
         * @param digest  the digest to be verified.
         * @param r       the r value of the signature.
         * @param s       the s value of the signature.
         * @exception DataLengthException if the digest is longer than the key allows
         */
        public boolean verifySignature(
                byte[]      digest,
                BigInteger  r,
                BigInteger  s)
        {
            if (this.forSigning)
            {
                throw new IllegalStateException("not initialised for verifying");
            }

            ECPublicKeyParameters pubKey = (ECPublicKeyParameters)key;
            BigInteger n = pubKey.getParameters().getN();
            int nBitLength = n.bitLength();

            BigInteger e = new BigInteger(1, digest);
            int eBitLength = e.bitLength();

            if (eBitLength > nBitLength)
            {
                throw new DataLengthException("input too large for ECNR key.");
            }

            // r in the range [1,n-1]
            if (r.compareTo(ECConstants.ONE) < 0 || r.compareTo(n) >= 0)
            {
                return false;
            }

            // s in the range [0,n-1]           NB: ECNR spec says 0
            if (s.compareTo(ECConstants.ZERO) < 0 || s.compareTo(n) >= 0)
            {
                return false;
            }

            // compute P = sG + rW

            ECPoint G = pubKey.getParameters().getG();
            ECPoint W = pubKey.getQ();
            // calculate P using Bouncy math
            ECPoint P = ECAlgorithms.sumOfTwoMultiplies(G, s, W, r).normalize();

            // components must be bogus.
            if (P.isInfinity())
            {
                return false;
            }

            BigInteger x = P.getAffineXCoord().toBigInteger();
            BigInteger t = r.subtract(x).mod(n);

            return t.equals(e);
        }
    }
}
