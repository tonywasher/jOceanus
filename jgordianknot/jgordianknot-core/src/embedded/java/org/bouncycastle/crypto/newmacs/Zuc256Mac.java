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
     * The current index.
     */
    private int theIndex;

    /**
     * Constructor.
     * @param pLength the bit length of the Mac
     */
    Zuc256Mac(int pLength) {
        theEngine = new Zuc256Engine(pLength);
        theMacLength = pLength;
        int numWords = pLength / 32;
        theMac = new int[numWords];
        theKeyStream = new int[numWords + 1];
    }

    @Override
    public String getAlgorithmName() {
        return "Zuc256Mac-" + theMacLength;
    }

    @Override
    public int getMacSize() {
        return theMacLength / 8;
    }

    @Override
    public void init(final CipherParameters pParams) {
        CipherParameters myParams = pParams;
        byte[] myIV = null;
        if (myParams instanceof ParametersWithIV) {
            final ParametersWithIV ivParams = (ParametersWithIV) myParams;
            myIV = ivParams.getIV();
            myParams = ivParams.getParameters();
        } else {
            throw new IllegalArgumentException(getAlgorithmName() + " requires an IV.");
        }

        /* Access the key */
        if (!(myParams instanceof KeyParameter)) {
            throw new IllegalArgumentException(getAlgorithmName() + " requires a key.");
        }
        final KeyParameter keyParams = (KeyParameter) myParams;
        final byte[] myKey = keyParams.getKey();

        /* Initialise the engine */
        theEngine.setKeyAndIV(myKey, myIV);
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
        theIndex = theKeyStream.length - 1;
    }

    @Override
    public void update(final byte in) {
        /* Read in the next keyStream word */
        theKeyStream[theIndex] = theEngine.makeKeyStreamWord();
        theIndex = (theIndex + 1) % theKeyStream.length;

        /* Loop through the bits */
        for (int bitMask = 128, bitNo = 0; bitMask > 0; bitMask >>= 1, bitNo++) {
            /* If the bit is set */
            if ((in & bitMask) != 0) {
                /* update theMac */
                updateMac(bitNo);
            }
        }
    }

    /**
     * Update the Mac
     * @param bitNo the bit number
     */
    private void updateMac(int bitNo) {
        /* Loop through the Mac */
        for (int wordNo = 0; wordNo < theMac.length; wordNo++) {
            theMac[wordNo] ^= getKeyStreamWord(wordNo, bitNo);
        }
    }

    /**
     * Obtain the keyStreamWord
     * @param wordNo the wordNumber
     * @param bitNo the bitNumber
     * @return the word
     */
    private int getKeyStreamWord(int wordNo, int bitNo) {
        /* Access the first word and return it if this is bit 0 */
        int myFirst = theKeyStream[(theIndex + wordNo) % theKeyStream.length];
        if (bitNo == 0) {
            return myFirst;
        }

        /* Access the second word */
        int mySecond = theKeyStream[(theIndex + wordNo + 1) % theKeyStream.length];
        return (myFirst << bitNo) | (mySecond >> (32 - bitNo));
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
        for (int i = 0; i < theMac.length; i++) {
            theMac[i] ^= theEngine.makeKeyStreamWord();
            Zuc256Engine.encode32be(theMac[i], out, outOff + i * 4);
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
