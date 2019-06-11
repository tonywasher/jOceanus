package net.sourceforge.joceanus.jgordianknot.junit.patches;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.digests.DSTU7564Digest;
import org.bouncycastle.crypto.engines.DSTU7624Engine;
import org.bouncycastle.crypto.macs.DSTU7564Mac;
import org.bouncycastle.crypto.macs.DSTU7624Mac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;

import java.security.SecureRandom;

public class DSTUreset {
    /**
     * Run Tests.
     * @param pArgs arguments
     */
    public static void main(final String[] pArgs) {
        TestDSTUreset(new DSTU7564XMac(512));
        TestDSTUreset(new DSTU7624XMac(128, 128));
    }

    /**
     * Test DSTUreset.
     * @param pMac the mac
     */
    private static void TestDSTUreset(final Mac pMac) {
        /* Define message and key */
        final byte[] myMessage = "1234567890123456".getBytes();
        final byte[] myKey = "A123456789B12345".getBytes();

        /* Define buffers */
        final int myOutSize = pMac.getMacSize();
        final byte[] myFirst = new byte[myOutSize];
        final byte[] mySecond = new byte[myOutSize];

        /* Run the Mac twice without reInitialising */
        pMac.init(new KeyParameter(myKey));
        pMac.update(myMessage, 0, myMessage.length);
        pMac.doFinal(myFirst, 0);
        pMac.update(myMessage, 0, myMessage.length);
        pMac.doFinal(mySecond, 0);
        if (!Arrays.areEqual(myFirst, mySecond)) {
            System.out.println(pMac.getAlgorithmName() + " did not reset after doFinal() properly");
        }

        /* Manually perform the reset */
        pMac.reset();

        /* Initialise the mac twice */
        pMac.init(new KeyParameter(myKey));
        pMac.init(new KeyParameter(myKey));
        pMac.update(myMessage, 0, myMessage.length);
        pMac.doFinal(mySecond, 0);
        if (!Arrays.areEqual(myFirst, mySecond)) {
            System.out.println(pMac.getAlgorithmName() + " did not re-init properly");
        }
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

            int extra = engine.getByteLength() - (int) (in.length % engine.getByteLength());
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
    /**
     * Implementation of DSTU7624 MAC mode
     */
    public static class DSTU7624XMac
            implements Mac
    {
        private final static int BITS_IN_BYTE = 8;

        private byte[]              buf;
        private int                 bufOff;

        private int macSize;
        private int blockSize;
        private DSTU7624Engine engine;

        private byte[] c, cTemp, kDelta;

        public DSTU7624XMac(int blockBitLength, int q)
        {
            this.engine = new DSTU7624Engine(blockBitLength);
            this.blockSize = blockBitLength / BITS_IN_BYTE;
            this.macSize = q / BITS_IN_BYTE;
            this.c = new byte[blockSize];
            this.kDelta = new byte[blockSize];
            this.cTemp = new byte[blockSize];
            this.buf = new byte[blockSize];
        }

        public void init(CipherParameters params)
                throws IllegalArgumentException
        {
            if (params instanceof KeyParameter)
            {
                engine.init(true, params);
                reset();
            }
            else
            {
                throw new IllegalArgumentException("Invalid parameter passed to DSTU7624Mac");
            }
        }

        public String getAlgorithmName()
        {
            return "DSTU7624Mac";
        }

        public int getMacSize()
        {
            return macSize;
        }

        public void update(byte in)
        {
            if (bufOff == buf.length)
            {
                processBlock(buf, 0);
                bufOff = 0;
            }

            buf[bufOff++] = in;
        }

        public void update(byte[] in, int inOff, int len)
        {
            if (len < 0)
            {
                throw new IllegalArgumentException(
                        "can't have a negative input length!");
            }

            int blockSize = engine.getBlockSize();
            int gapLen = blockSize - bufOff;

            if (len > gapLen)
            {
                System.arraycopy(in, inOff, buf, bufOff, gapLen);

                processBlock(buf, 0);

                bufOff = 0;
                len -= gapLen;
                inOff += gapLen;

                while (len > blockSize)
                {
                    processBlock(in, inOff);

                    len -= blockSize;
                    inOff += blockSize;
                }
            }

            System.arraycopy(in, inOff, buf, bufOff, len);

            bufOff += len;
        }

        private void processBlock(byte[] in, int inOff)
        {
            xor(c, 0, in, inOff, cTemp);

            engine.processBlock(cTemp, 0, c, 0);
        }

        public int doFinal(byte[] out, int outOff)
                throws DataLengthException, IllegalStateException
        {
            if (bufOff % buf.length != 0)
            {
                throw new DataLengthException("input must be a multiple of blocksize");
            }

            //Last block
            xor(c, 0, buf, 0, cTemp);
            xor(cTemp, 0, kDelta, 0, c);
            engine.processBlock(c, 0, c, 0);

            if (macSize + outOff > out.length)
            {
                throw new OutputLengthException("output buffer too short");
            }

            System.arraycopy(c, 0, out, outOff, macSize);
            reset();

            return macSize;
        }

        public void reset()
        {
            Arrays.fill(c, (byte)0x00);
            Arrays.fill(cTemp, (byte)0x00);
            Arrays.fill(kDelta, (byte)0x00);
            Arrays.fill(buf, (byte)0x00);
            engine.reset();
            engine.processBlock(kDelta, 0, kDelta, 0);
            bufOff = 0;
        }

        private void xor(byte[] x, int xOff, byte[] y, int yOff, byte[] x_xor_y)
        {

            if (x.length - xOff < blockSize || y.length - yOff < blockSize || x_xor_y.length < blockSize)
            {
                throw new IllegalArgumentException("some of input buffers too short");
            }
            for (int byteIndex = 0; byteIndex < blockSize; byteIndex++)
            {
                x_xor_y[byteIndex] = (byte)(x[byteIndex + xOff] ^ y[byteIndex + yOff]);
            }
        }

    }
}
