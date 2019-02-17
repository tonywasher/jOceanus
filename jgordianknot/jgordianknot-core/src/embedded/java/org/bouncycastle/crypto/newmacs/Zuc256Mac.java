package org.bouncycastle.crypto.newmacs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.newengines.Zuc256Engine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/**
 * Zuc128Engine implementation.
 * Based on http://www.is.cas.cn/ztzl2016/zouchongzhi/201801/W020180126529970733243.pdf
 */
public class Zuc256Mac implements Mac {
    /**
     * The Maximum Bit Mask.
     */
    private static final int TOPBIT = 0x80;

    /**
     * The Zuc256 Engine.
     */
    private final Zuc256Engine theEngine;

    /**
     * The mac length.
     */
    private final int theMacLength;

    /**
     * The calculated Mac in words.
     */
    private final int[] theMac;

    /**
     * The active keyStream.
     */
    private final int[] theKeyStream;

    /**
     * The initialised state.
     */
    private Zuc256Engine theState;

    /**
     * The current word index.
     */
    private int theWordIndex;

    /**
     * The current byte index.
     */
    private int theByteIndex;

    /**
     * Constructor.
     * @param pLength the bit length of the Mac
     */
    public Zuc256Mac(final int pLength) {
        theEngine = new Zuc256Engine(pLength);
        theMacLength = pLength;
        final int numWords = pLength / Integer.SIZE;
        theMac = new int[numWords];
        theKeyStream = new int[numWords + 1];
    }

    @Override
    public String getAlgorithmName() {
        return "Zuc256Mac-" + theMacLength;
    }

    @Override
    public int getMacSize() {
        return theMacLength / Byte.SIZE;
    }

    @Override
    public void init(final CipherParameters pParams) {
        /* Initialise the engine */
        theEngine.init(true, pParams);
        theState = theEngine.copy();
        initKeyStream();
    }

    /**
     * Initialise the keyStream.
     */
    private void initKeyStream() {
        /* Initialise the Mac */
        for (int i = 0; i < theMac.length; i++) {
            theMac[i] = theEngine.makeKeyStreamWord();
        }

        /* Initialise the KeyStream */
        for (int i = 0; i < theKeyStream.length - 1; i++) {
            theKeyStream[i] = theEngine.makeKeyStreamWord();
        }
        theWordIndex = theKeyStream.length - 1;
        theByteIndex = Integer.BYTES - 1;
    }

    @Override
    public void update(final byte in) {
        /* shift for next byte */
        shift4NextByte();

        /* Loop through the bits */
        final int bitBase = theByteIndex * Byte.SIZE;
        for (int bitMask = TOPBIT, bitNo = 0; bitMask > 0; bitMask >>= 1, bitNo++) {
            /* If the bit is set */
            if ((in & bitMask) != 0) {
                /* update theMac */
                updateMac(bitBase + bitNo);
            }
        }
    }

    /**
     * Shift for next byte.
     */
    private void shift4NextByte() {
        /* Adjust the byte index */
        theByteIndex = (theByteIndex + 1) % Integer.BYTES;

        /* Adjust keyStream if required */
        if (theByteIndex == 0) {
            theKeyStream[theWordIndex] = theEngine.makeKeyStreamWord();
            theWordIndex = (theWordIndex + 1) % theKeyStream.length;
        }
    }

    /**
     * Update the Mac.
     * @param bitNo the bit number
     */
    private void updateMac(final int bitNo) {
        /* Loop through the Mac */
        for (int wordNo = 0; wordNo < theMac.length; wordNo++) {
            theMac[wordNo] ^= getKeyStreamWord(wordNo, bitNo);
        }
    }

    /**
     * Obtain the keyStreamWord.
     * @param wordNo the wordNumber
     * @param bitNo the bitNumber
     * @return the word
     */
    private int getKeyStreamWord(final int wordNo, final int bitNo) {
        /* Access the first word and return it if this is bit 0 */
        final int myFirst = theKeyStream[(theWordIndex + wordNo) % theKeyStream.length];
        if (bitNo == 0) {
            return myFirst;
        }

        /* Access the second word */
        final int mySecond = theKeyStream[(theWordIndex + wordNo + 1) % theKeyStream.length];
        return (myFirst << bitNo) | (mySecond >>> (Integer.SIZE - bitNo));
    }

    @Override
    public void update(final byte[] in, final int inOff, final int len) {
        for (int byteNo = 0; byteNo < len; byteNo++) {
            update(in[inOff + byteNo]);
        }
    }

    @Override
    public int doFinal(final byte[] out, final int outOff) {
        /* Finish the Mac and output it */
        shift4NextByte();
        updateMac(theByteIndex * Byte.SIZE);
        for (int i = 0; i < theMac.length; i++) {
            Zuc256Engine.encode32be(theMac[i], out, outOff + i * Integer.BYTES);
        }

        /* Reset the Mac */
        reset();
        return getMacSize();
    }

    @Override
    public void reset() {
        theEngine.reset(theState);
        initKeyStream();
    }
}
