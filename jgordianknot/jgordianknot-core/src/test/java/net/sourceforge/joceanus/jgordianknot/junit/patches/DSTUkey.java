package net.sourceforge.joceanus.jgordianknot.junit.patches;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.digests.DSTU7564Digest;
import org.bouncycastle.crypto.macs.DSTU7564Mac;
import org.bouncycastle.crypto.params.KeyParameter;

public class DSTUkey {
    /**
     * Run Tests.
     * @param pArgs arguments
     */
    public static void main(final String[] pArgs) {
        TestDSTUkey(new DSTU7564Mac(512));
    }

    /**
     * Test DSTUreset.
     * @param pMac the mac
     */
    private static void TestDSTUkey(final Mac pMac) {
        /* Define message */
        final byte[] myMessage = "1234567890123456".getBytes();

        /* Determine underlying engine size */
        final int myEngineSize = pMac.getMacSize() == 32 ? 64 : 128;

        /* Define key (can be any multiple of engineSize) */
        final byte[] myKey = new byte[myEngineSize];

        /* Initialise Mac (will fail with ArrayIndexOutOfBoundsException) */
        pMac.init(new KeyParameter(myKey));
        final int myOutSize = pMac.getMacSize();
        final byte[] myOut = new byte[myOutSize];
        pMac.update(myMessage, 0, myMessage.length);
        pMac.doFinal(myOut, 0);
    }

    /**
     * Implementation of DSTU7564 MAC mode
     */
    public static class DSTU7564XMac
            implements Mac {
        private static final int BITS_IN_BYTE = 8;

        private DSTU7564Digest engine;

        private int macSize;

        private byte[] paddedKey;
        private byte[] invertedKey;

        private long inputLength;

        public DSTU7564XMac(int macBitSize) {
            /* Mac size can be only 256 / 384 / 512. Same as hash size for DSTU7654Digest */
            this.engine = new DSTU7564Digest(macBitSize);
            this.macSize = macBitSize / BITS_IN_BYTE;

            this.paddedKey = null;
            this.invertedKey = null;
        }

        public void init(CipherParameters params)
                throws IllegalArgumentException {
            if (params instanceof KeyParameter) {
                byte[] key = ((KeyParameter) params).getKey();

                invertedKey = new byte[key.length];

                paddedKey = padKey(key);

                for (int byteIndex = 0; byteIndex < invertedKey.length; byteIndex++) {
                    invertedKey[byteIndex] = (byte) (key[byteIndex] ^ (byte) 0xFF);
                }
                reset();
            } else {
                throw new IllegalArgumentException("Bad parameter passed");
            }

            //engine.update(paddedKey, 0, paddedKey.length);
        }

        public String getAlgorithmName() {
            return "DSTU7564Mac";
        }

        public int getMacSize() {
            return macSize;
        }

        public void update(byte in)
                throws IllegalStateException {
            engine.update(in);
            inputLength++;
        }

        public void update(byte[] in, int inOff, int len)
                throws DataLengthException, IllegalStateException {
            if (in.length - inOff < len) {
                throw new DataLengthException("Input buffer too short");
            }

            if (paddedKey == null) {
                throw new IllegalStateException(getAlgorithmName() + " not initialised");
            }

            engine.update(in, inOff, len);
            inputLength += len;
        }

        public int doFinal(byte[] out, int outOff)
                throws DataLengthException, IllegalStateException {
            if (paddedKey == null) {
                throw new IllegalStateException(getAlgorithmName() + " not initialised");
            }
            if (out.length - outOff < macSize) {
                throw new OutputLengthException("Output buffer too short");
            }

            pad();

            engine.update(invertedKey, 0, invertedKey.length);

            inputLength = 0;

            int result = engine.doFinal(out, outOff);
            reset();
            return result;
        }

        public void reset() {
            inputLength = 0;
            engine.reset();
            if (paddedKey != null) {
                engine.update(paddedKey, 0, paddedKey.length);
            }
        }

        private void pad() {
            int extra = engine.getByteLength() - (int) (inputLength % engine.getByteLength());
            if (extra < 13)  // terminator byte + 96 bits of length
            {
                extra += engine.getByteLength();
            }

            byte[] padded = new byte[extra];

            padded[0] = (byte) 0x80; // Defined in standard;

            // Defined in standard;
            longToLittleEndian(inputLength * BITS_IN_BYTE, padded, padded.length - 12);

            engine.update(padded, 0, padded.length);
        }

        private byte[] padKey(byte[] in) {
            int paddedLen = ((in.length + engine.getByteLength() - 1) / engine.getByteLength()) * engine.getByteLength();

            int lastBlk = (int) (in.length % engine.getByteLength());
            int extra = lastBlk == 0 ? 0 : engine.getByteLength() - lastBlk;
            if (extra < 13)  // terminator byte + 96 bits of length
            {
                paddedLen += engine.getByteLength();
            }

            byte[] padded = new byte[paddedLen];

            System.arraycopy(in, 0, padded, 0, in.length);

            padded[in.length] = (byte) 0x80; // Defined in standard;
            intToLittleEndian(in.length * BITS_IN_BYTE, padded, padded.length - 12); // Defined in standard;

            return padded;
        }

        public static void longToLittleEndian(long n, byte[] bs, int off)
        {
            intToLittleEndian((int)(n & 0xffffffffL), bs, off);
            intToLittleEndian((int)(n >>> 32), bs, off + 4);
        }

        public static void intToLittleEndian(int n, byte[] bs, int off)
        {
            bs[  off] = (byte)(n       );
            bs[++off] = (byte)(n >>>  8);
            bs[++off] = (byte)(n >>> 16);
            bs[++off] = (byte)(n >>> 24);
        }
    }
}
