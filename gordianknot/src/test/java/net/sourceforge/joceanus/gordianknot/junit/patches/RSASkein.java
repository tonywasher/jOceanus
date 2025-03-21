package net.sourceforge.joceanus.gordianknot.junit.patches;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.SignerWithRecovery;
import org.bouncycastle.crypto.digests.SkeinDigest;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.ISOTrailers;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * RSASkein test.
 */
public class RSASkein {
    /**
     * Main entry.
     * @param pArgs the arguments
     */
    public static void main(final String[] pArgs) {
        /* Create the generator */
        RSAKeyPairGenerator myGenerator = new RSAKeyPairGenerator();
        final SecureRandom myRandom = new SecureRandom();
        final BigInteger RSA_EXPONENT = new BigInteger("10001", 16);
        final RSAKeyGenerationParameters myParams = new RSAKeyGenerationParameters(RSA_EXPONENT, myRandom, 1024, 128);
        myGenerator.init(myParams);

        /* Create the key Pair */
        AsymmetricCipherKeyPair myPair = myGenerator.generateKeyPair();

        /* Create digest and signer */
        Digest myDigest = new SkeinDigest(1024, 1024);
        Signer mySigner1 = new ISO9796d2Signer(new RSABlindedEngine(), myDigest, ISOTrailers.noTrailerAvailable(myDigest));
        Signer mySigner2 = new X931Signer(new RSABlindedEngine(), myDigest, ISOTrailers.noTrailerAvailable(myDigest));
        final byte[] myMessage = "Hello there. How is life treating you?".getBytes();

        /* Initialise signer */
        try {
            mySigner1.init(true, myPair.getPrivate());
            mySigner1.update(myMessage, 0, myMessage.length);
            byte[] mySign1 = mySigner1.generateSignature();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* Initialise signer */
        try {
            mySigner2.init(true, myPair.getPrivate());
            mySigner2.update(myMessage, 0, myMessage.length);
            byte[] mySign2 = mySigner1.generateSignature();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ISO9796-2 - mechanism using a hash function with recovery (scheme 1)
     */
    public static class ISO9796d2Signer
            implements SignerWithRecovery {
        private Digest digest;
        private AsymmetricBlockCipher cipher;

        private int trailer;
        private int keyBits;
        private byte[] block;
        private byte[] mBuf;
        private int messageLength;
        private boolean fullMessage;
        private byte[] recoveredMessage;

        private byte[] preSig;
        private byte[] preBlock;

        /**
         * Generate a signer with either implicit or explicit trailers for ISO9796-2.
         *
         * @param cipher   base cipher to use for signature creation/verification
         * @param digest   digest to use.
         * @param implicit whether or not the trailer is implicit or gives the hash.
         */
        public ISO9796d2Signer(
                AsymmetricBlockCipher cipher,
                Digest digest,
                boolean implicit) {
            this.cipher = cipher;
            this.digest = digest;

            if (implicit) {
                trailer = ISOTrailers.TRAILER_IMPLICIT;
            } else {
                Integer trailerObj = ISOTrailers.getTrailer(digest);

                if (trailerObj != null) {
                    trailer = trailerObj.intValue();
                } else {
                    throw new IllegalArgumentException("no valid trailer for digest: " + digest.getAlgorithmName());
                }
            }
        }

        /**
         * Constructor for a signer with an explicit digest trailer.
         *
         * @param cipher cipher to use.
         * @param digest digest to sign with.
         */
        public ISO9796d2Signer(
                AsymmetricBlockCipher cipher,
                Digest digest) {
            this(cipher, digest, false);
        }

        public void init(
                boolean forSigning,
                CipherParameters param) {
            RSAKeyParameters kParam = (RSAKeyParameters) param;

            cipher.init(forSigning, kParam);

            keyBits = kParam.getModulus().bitLength();

            block = new byte[(keyBits + 7) / 8];

            int blockLen = block.length - digest.getDigestSize() - 2;
            if (trailer == ISOTrailers.TRAILER_IMPLICIT) {
                blockLen--;
            }
            if (blockLen < 0) {
                throw new IllegalArgumentException("key too small for specified hash");
            }

            mBuf = new byte[blockLen];

            reset();
        }

        /**
         * compare two byte arrays - constant time
         */
        private boolean isSameAs(
                byte[] a,
                byte[] b) {
            boolean isOkay = true;

            if (messageLength > mBuf.length) {
                if (mBuf.length > b.length) {
                    isOkay = false;
                }

                for (int i = 0; i != mBuf.length; i++) {
                    if (a[i] != b[i]) {
                        isOkay = false;
                    }
                }
            } else {
                if (messageLength != b.length) {
                    isOkay = false;
                }

                for (int i = 0; i != b.length; i++) {
                    if (a[i] != b[i]) {
                        isOkay = false;
                    }
                }
            }

            return isOkay;
        }

        /**
         * clear possible sensitive data
         */
        private void clearBlock(
                byte[] block) {
            for (int i = 0; i != block.length; i++) {
                block[i] = 0;
            }
        }

        public void updateWithRecoveredMessage(byte[] signature)
                throws InvalidCipherTextException {
            byte[] block = cipher.processBlock(signature, 0, signature.length);

            if (((block[0] & 0xC0) ^ 0x40) != 0) {
                throw new InvalidCipherTextException("malformed signature");
            }

            if (((block[block.length - 1] & 0xF) ^ 0xC) != 0) {
                throw new InvalidCipherTextException("malformed signature");
            }

            int delta = 0;

            if (((block[block.length - 1] & 0xFF) ^ 0xBC) == 0) {
                delta = 1;
            } else {
                int sigTrail = ((block[block.length - 2] & 0xFF) << 8) | (block[block.length - 1] & 0xFF);
                Integer trailerObj = ISOTrailers.getTrailer(digest);

                if (trailerObj != null) {
                    int trailer = trailerObj.intValue();
                    if (sigTrail != trailer) {
                        if (!(trailer == ISOTrailers.TRAILER_SHA512_256 && sigTrail == 0x40CC)) {
                            throw new IllegalStateException("signer initialised with wrong digest for trailer " + sigTrail);
                        }
                    }
                } else {
                    throw new IllegalArgumentException("unrecognised hash in signature");
                }

                delta = 2;
            }

            //
            // find out how much padding we've got
            //
            int mStart = 0;

            for (mStart = 0; mStart != block.length; mStart++) {
                if (((block[mStart] & 0x0f) ^ 0x0a) == 0) {
                    break;
                }
            }

            mStart++;

            int off = block.length - delta - digest.getDigestSize();

            //
            // there must be at least one byte of message string
            //
            if ((off - mStart) <= 0) {
                throw new InvalidCipherTextException("malformed block");
            }

            //
            // if we contain the whole message as well, check the hash of that.
            //
            if ((block[0] & 0x20) == 0) {
                fullMessage = true;

                recoveredMessage = new byte[off - mStart];
                System.arraycopy(block, mStart, recoveredMessage, 0, recoveredMessage.length);
            } else {
                fullMessage = false;

                recoveredMessage = new byte[off - mStart];
                System.arraycopy(block, mStart, recoveredMessage, 0, recoveredMessage.length);
            }

            preSig = signature;
            preBlock = block;

            digest.update(recoveredMessage, 0, recoveredMessage.length);
            messageLength = recoveredMessage.length;
            System.arraycopy(recoveredMessage, 0, mBuf, 0, recoveredMessage.length);
        }

        /**
         * update the internal digest with the byte b
         */
        public void update(
                byte b) {
            digest.update(b);

            if (messageLength < mBuf.length) {
                mBuf[messageLength] = b;
            }

            messageLength++;
        }

        /**
         * update the internal digest with the byte array in
         */
        public void update(
                byte[] in,
                int off,
                int len) {
            while (len > 0 && messageLength < mBuf.length) {
                this.update(in[off]);
                off++;
                len--;
            }

            digest.update(in, off, len);
            messageLength += len;
        }

        /**
         * reset the internal state
         */
        public void reset() {
            digest.reset();
            messageLength = 0;
            clearBlock(mBuf);

            if (recoveredMessage != null) {
                clearBlock(recoveredMessage);
            }

            recoveredMessage = null;
            fullMessage = false;

            if (preSig != null) {
                preSig = null;
                clearBlock(preBlock);
                preBlock = null;
            }
        }

        /**
         * generate a signature for the loaded message using the key we were
         * initialised with.
         */
        public byte[] generateSignature()
                throws CryptoException {
            int digSize = digest.getDigestSize();

            int t = 0;
            int delta = 0;

            if (trailer == ISOTrailers.TRAILER_IMPLICIT) {
                t = 8;
                delta = block.length - digSize - 1;
                digest.doFinal(block, delta);
                block[block.length - 1] = (byte) ISOTrailers.TRAILER_IMPLICIT;
            } else {
                t = 16;
                delta = block.length - digSize - 2;
                digest.doFinal(block, delta);
                block[block.length - 2] = (byte) (trailer >>> 8);
                block[block.length - 1] = (byte) trailer;
            }

            byte header = 0;
            int x = (digSize + messageLength) * 8 + t + 4 - keyBits;

            if (x > 0) {
                int mR = messageLength - ((x + 7) / 8);
                header = 0x60;

                delta -= mR;

                System.arraycopy(mBuf, 0, block, delta, mR);

                recoveredMessage = new byte[mR];
            } else {
                header = 0x40;
                delta -= messageLength;

                System.arraycopy(mBuf, 0, block, delta, messageLength);

                recoveredMessage = new byte[messageLength];
            }

            if ((delta - 1) > 0) {
                for (int i = delta - 1; i != 0; i--) {
                    block[i] = (byte) 0xbb;
                }
                block[delta - 1] ^= (byte) 0x01;
                block[0] = (byte) 0x0b;
                block[0] |= header;
            } else {
                block[0] = (byte) 0x0a;
                block[0] |= header;
            }

            byte[] b = cipher.processBlock(block, 0, block.length);

            fullMessage = (header & 0x20) == 0;
            System.arraycopy(mBuf, 0, recoveredMessage, 0, recoveredMessage.length);

            messageLength = 0;

            clearBlock(mBuf);
            clearBlock(block);

            return b;
        }

        /**
         * return true if the signature represents a ISO9796-2 signature
         * for the passed in message.
         */
        public boolean verifySignature(
                byte[] signature) {
            byte[] block = null;

            if (preSig == null) {
                try {
                    block = cipher.processBlock(signature, 0, signature.length);
                } catch (Exception e) {
                    return false;
                }
            } else {
                if (!Arrays.areEqual(preSig, signature)) {
                    throw new IllegalStateException("updateWithRecoveredMessage called on different signature");
                }

                block = preBlock;

                preSig = null;
                preBlock = null;
            }

            if (((block[0] & 0xC0) ^ 0x40) != 0) {
                return returnFalse(block);
            }

            if (((block[block.length - 1] & 0xF) ^ 0xC) != 0) {
                return returnFalse(block);
            }

            int delta = 0;

            if (((block[block.length - 1] & 0xFF) ^ 0xBC) == 0) {
                delta = 1;
            } else {
                int sigTrail = ((block[block.length - 2] & 0xFF) << 8) | (block[block.length - 1] & 0xFF);
                Integer trailerObj = ISOTrailers.getTrailer(digest);

                if (trailerObj != null) {
                    int trailer = trailerObj.intValue();
                    if (sigTrail != trailer) {
                        if (!(trailer == ISOTrailers.TRAILER_SHA512_256 && sigTrail == 0x40CC)) {
                            throw new IllegalStateException("signer initialised with wrong digest for trailer " + sigTrail);
                        }
                    }
                } else {
                    throw new IllegalArgumentException("unrecognised hash in signature");
                }

                delta = 2;
            }

            //
            // find out how much padding we've got
            //
            int mStart = 0;

            for (mStart = 0; mStart != block.length; mStart++) {
                if (((block[mStart] & 0x0f) ^ 0x0a) == 0) {
                    break;
                }
            }

            mStart++;

            //
            // check the hashes
            //
            byte[] hash = new byte[digest.getDigestSize()];

            int off = block.length - delta - hash.length;

            //
            // there must be at least one byte of message string
            //
            if ((off - mStart) <= 0) {
                return returnFalse(block);
            }

            //
            // if we contain the whole message as well, check the hash of that.
            //
            if ((block[0] & 0x20) == 0) {
                fullMessage = true;

                // check right number of bytes passed in.
                if (messageLength > off - mStart) {
                    return returnFalse(block);
                }

                digest.reset();
                digest.update(block, mStart, off - mStart);
                digest.doFinal(hash, 0);

                boolean isOkay = true;

                for (int i = 0; i != hash.length; i++) {
                    block[off + i] ^= hash[i];
                    if (block[off + i] != 0) {
                        isOkay = false;
                    }
                }

                if (!isOkay) {
                    return returnFalse(block);
                }

                recoveredMessage = new byte[off - mStart];
                System.arraycopy(block, mStart, recoveredMessage, 0, recoveredMessage.length);
            } else {
                fullMessage = false;

                digest.doFinal(hash, 0);

                boolean isOkay = true;

                for (int i = 0; i != hash.length; i++) {
                    block[off + i] ^= hash[i];
                    if (block[off + i] != 0) {
                        isOkay = false;
                    }
                }

                if (!isOkay) {
                    return returnFalse(block);
                }

                recoveredMessage = new byte[off - mStart];
                System.arraycopy(block, mStart, recoveredMessage, 0, recoveredMessage.length);
            }

            //
            // if they've input a message check what we've recovered against
            // what was input.
            //
            if (messageLength != 0) {
                if (!isSameAs(mBuf, recoveredMessage)) {
                    return returnFalse(block);
                }
            }

            clearBlock(mBuf);
            clearBlock(block);

            messageLength = 0;

            return true;
        }

        private boolean returnFalse(byte[] block) {
            messageLength = 0;

            clearBlock(mBuf);
            clearBlock(block);

            return false;
        }

        /**
         * Return true if the full message was recoveredMessage.
         *
         * @return true on full message recovery, false otherwise.
         * @see org.bouncycastle.crypto.SignerWithRecovery#hasFullMessage()
         */
        public boolean hasFullMessage() {
            return fullMessage;
        }

        /**
         * Return a reference to the recoveredMessage message, either as it was added
         * to a just generated signature, or extracted from a verified one.
         *
         * @return the full/partial recoveredMessage message.
         * @see org.bouncycastle.crypto.SignerWithRecovery#getRecoveredMessage()
         */
        public byte[] getRecoveredMessage() {
            return recoveredMessage;
        }
    }
    /**
     * X9.31-1998 - signing using a hash.
     * <p>
     * The message digest hash, H, is encapsulated to form a byte string as follows
     * <pre>
     * EB = 06 || PS || 0xBA || H || TRAILER
     * </pre>
     * where PS is a string of bytes all of value 0xBB of length such that |EB|=|n|, and TRAILER is the ISO/IEC 10118 part number for the digest. The byte string, EB, is converted to an integer value, the message representative, f.
     */
    public static class X931Signer
            implements Signer
    {
        private Digest                      digest;
        private AsymmetricBlockCipher       cipher;
        private RSAKeyParameters            kParam;

        private int         trailer;
        private int         keyBits;
        private byte[]      block;

        /**
         * Generate a signer with either implicit or explicit trailers for X9.31
         *
         * @param cipher base cipher to use for signature creation/verification
         * @param digest digest to use.
         * @param implicit whether or not the trailer is implicit or gives the hash.
         */
        public X931Signer(
                AsymmetricBlockCipher cipher,
                Digest digest,
                boolean implicit)
        {
            this.cipher = cipher;
            this.digest = digest;

            if (implicit)
            {
                trailer = ISOTrailers.TRAILER_IMPLICIT;
            }
            else
            {
                Integer trailerObj = ISOTrailers.getTrailer(digest);

                if (trailerObj != null)
                {
                    trailer = trailerObj.intValue();
                }
                else
                {
                    throw new IllegalArgumentException("no valid trailer for digest: " + digest.getAlgorithmName());
                }
            }
        }

        /**
         * Constructor for a signer with an explicit digest trailer.
         *
         * @param cipher cipher to use.
         * @param digest digest to sign with.
         */
        public X931Signer(
                AsymmetricBlockCipher cipher,
                Digest digest)
        {
            this(cipher, digest, false);
        }

        public void init(
                boolean                 forSigning,
                CipherParameters        param)
        {
            kParam = (RSAKeyParameters)param;

            cipher.init(forSigning, kParam);

            keyBits = kParam.getModulus().bitLength();

            block = new byte[(keyBits + 7) / 8];

            int blockLen = block.length - digest.getDigestSize() - 3;
            if (trailer == ISOTrailers.TRAILER_IMPLICIT) {
                blockLen--;
            }
            if (blockLen < 0) {
                throw new IllegalArgumentException("key too small for specified hash");
            }

            reset();
        }

        /**
         * clear possible sensitive data
         */
        private void clearBlock(
                byte[]  block)
        {
            for (int i = 0; i != block.length; i++)
            {
                block[i] = 0;
            }
        }

        /**
         * update the internal digest with the byte b
         */
        public void update(
                byte    b)
        {
            digest.update(b);
        }

        /**
         * update the internal digest with the byte array in
         */
        public void update(
                byte[]  in,
                int     off,
                int     len)
        {
            digest.update(in, off, len);
        }

        /**
         * reset the internal state
         */
        public void reset()
        {
            digest.reset();
        }

        /**
         * generate a signature for the loaded message using the key we were
         * initialised with.
         */
        public byte[] generateSignature()
                throws CryptoException
        {
            createSignatureBlock(trailer);

            BigInteger t = new BigInteger(1, cipher.processBlock(block, 0, block.length));
            clearBlock(block);

            t = t.min(kParam.getModulus().subtract(t));

            int size = BigIntegers.getUnsignedByteLength(kParam.getModulus());
            return BigIntegers.asUnsignedByteArray(size, t);
        }

        private void createSignatureBlock(int trailer)
        {
            int     digSize = digest.getDigestSize();

            int delta;

            if (trailer == ISOTrailers.TRAILER_IMPLICIT)
            {
                delta = block.length - digSize - 1;
                digest.doFinal(block, delta);
                block[block.length - 1] = (byte)ISOTrailers.TRAILER_IMPLICIT;
            }
            else
            {
                delta = block.length - digSize - 2;
                digest.doFinal(block, delta);
                block[block.length - 2] = (byte)(trailer >>> 8);
                block[block.length - 1] = (byte)trailer;
            }

            block[0] = 0x6b;
            for (int i = delta - 2; i != 0; i--)
            {
                block[i] = (byte)0xbb;
            }
            block[delta - 1] = (byte)0xba;
        }

        /**
         * return true if the signature represents a X9.31 signature
         * for the passed in message.
         */
        public boolean verifySignature(
                byte[]      signature)
        {
            try
            {
                block = cipher.processBlock(signature, 0, signature.length);
            }
            catch (Exception e)
            {
                return false;
            }

            BigInteger t = new BigInteger(1, block);
            BigInteger f;

            if ((t.intValue() & 15) == 12)
            {
                f = t;
            }
            else
            {
                t = kParam.getModulus().subtract(t);
                if ((t.intValue() & 15) == 12)
                {
                    f = t;
                }
                else
                {
                    return false;
                }
            }

            createSignatureBlock(trailer);

            byte[] fBlock = BigIntegers.asUnsignedByteArray(block.length, f);

            boolean rv = Arrays.constantTimeAreEqual(block, fBlock);

            // check for old NIST tool value
            if (trailer == ISOTrailers.TRAILER_SHA512_256 && !rv)
            {
                block[block.length - 2] = (byte)0x40;   // old NIST CAVP tool value
                rv = Arrays.constantTimeAreEqual(block, fBlock);
            }

            clearBlock(block);
            clearBlock(fBlock);

            return rv;
        }
    }
}
