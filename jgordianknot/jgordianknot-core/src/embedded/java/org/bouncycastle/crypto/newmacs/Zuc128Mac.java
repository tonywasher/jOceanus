package org.bouncycastle.crypto.newmacs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.newengines.Zuc128Engine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/**
 * Zuc128Mac implementation.
 * Based on http://www.qtc.jp/3GPP/Specs/eea3eia3specificationv16.pdf
 */
public class Zuc128Mac implements Mac {
    /**
     * The Maximum Bit Mask.
     */
    private static final int TOPBIT = 0x80;

    /**
     * The Zuc128 Engine.
     */
    private final Zuc128Engine theEngine;

    /**
     * The calculated Mac in words.
     */
    private int theMac;

    /**
     * The active keyStream.
     */
    private final int[] theKeyStream;

    /**
     * The initialised state.
     */
    private Zuc128Engine theState;

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
     */
    public Zuc128Mac() {
        theEngine = new Zuc128Engine();
        theKeyStream = new int[2];
    }

    @Override
    public String getAlgorithmName() {
        return "Zuc128Mac";
    }

    @Override
    public int getMacSize() {
        return Integer.BYTES;
    }

    @Override
    public void init(final CipherParameters pParams) {
        CipherParameters myParams = pParams;
        if (!(myParams instanceof ParametersWithIV)) {
            throw new IllegalArgumentException(getAlgorithmName() + " requires an IV.");
        }
        final ParametersWithIV ivParams = (ParametersWithIV) myParams;
        final byte[] myIV = ivParams.getIV();
        myParams = ivParams.getParameters();

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
        theMac = 0;

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
        /* Update the Mac */
        theMac ^= getKeyStreamWord(bitNo);
    }

    /**
     * Obtain the keyStreamWord.
     * @param bitNo the bitNumber
     * @return the word
     */
    private int getKeyStreamWord(final int bitNo) {
        /* Access the first word and return it if this is bit 0 */
        final int myFirst = theKeyStream[theWordIndex];
        if (bitNo == 0) {
            return myFirst;
        }

        /* Access the second word */
        final int mySecond = theKeyStream[(theWordIndex + 1) % theKeyStream.length];
        return (myFirst << bitNo) | (mySecond >>> (Integer.SIZE - bitNo));
    }

    @Override
    public void update(final byte[] in, final int inOff, final int len) {
        for (int byteNo = 0; byteNo < len; byteNo++) {
            update(in[inOff + byteNo]);
        }
    }

    /**
     * Obtain the final word.
     * @return the final word
     */
    private int getFinalWord() {
        if (theByteIndex != 0) {
            return theEngine.makeKeyStreamWord();
        }
        theWordIndex = (theWordIndex + 1) % theKeyStream.length;
        return theKeyStream[theWordIndex];
    }

    @Override
    public int doFinal(final byte[] out, final int outOff) {
        /* Finish the Mac and output it */
        shift4NextByte();
        theMac ^= getKeyStreamWord(theByteIndex * Byte.SIZE);
        theMac ^= getFinalWord();
        Zuc128Engine.encode32be(theMac, out, outOff);

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
