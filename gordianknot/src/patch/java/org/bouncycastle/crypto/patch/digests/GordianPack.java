package org.bouncycastle.crypto.patch.digests;

public class GordianPack {
    public static long littleEndianToLong(byte[] input, int off, int len)
    {
        long result = 0;
        for (int i = 0; i < len; ++i)
        {
            result |= (input[off + i] & 0xFFL) << (i << 3);
        }
        return result;
    }

    public static long littleEndianToLong(byte[] bs, int off) {
        int lo = littleEndianToInt(bs, off);
        int hi = littleEndianToInt(bs, off + 4);
        return ((long) (hi & 0xffffffffL) << 32) | (long) (lo & 0xffffffffL);
    }

    public static int littleEndianToInt(byte[] bs, int off)
    {
        int n = bs[off] & 0xff;
        n |= (bs[++off] & 0xff) << 8;
        n |= (bs[++off] & 0xff) << 16;
        n |= bs[++off] << 24;
        return n;
    }

    public static void intToLittleEndian(int n, byte[] bs, int off)
    {
        bs[off] = (byte)(n);
        bs[++off] = (byte)(n >>> 8);
        bs[++off] = (byte)(n >>> 16);
        bs[++off] = (byte)(n >>> 24);
    }

    public static void longToLittleEndian(long n, byte[] bs, int off) {
        intToLittleEndian((int) (n & 0xffffffffL), bs, off);
        intToLittleEndian((int) (n >>> 32), bs, off + 4);
    }

    public static void longToLittleEndian(long n, byte[] bs, int off, int len)
    {
        for (int i = 0; i < len; ++i)
        {
            bs[off + i] = (byte)(n >>> (i << 3));
        }
    }
}
