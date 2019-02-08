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
     * The Zuc256 Engine.
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
     * The current index.
     */
    private int theIndex;

    /**
     * Constructor.
     * @param pLength the bit length of the Mac
     */
    Zuc128Mac(int pLength) {
        theEngine = new Zuc128Engine();
        theKeyStream = new int[2];
    }

    @Override
    public String getAlgorithmName() {
        return "Zuc128Mac";
    }

    @Override
    public int getMacSize() {
        return 4;
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
        theMac = 0;

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
        /* Update the Mac */
        theMac ^= getKeyStreamWord(bitNo);
     }

    /**
     * Obtain the keyStreamWord
     * @param bitNo the bitNumber
     * @return the word
     */
    private int getKeyStreamWord(int bitNo) {
        /* Access the first word and return it if this is bit 0 */
        int myFirst = theKeyStream[theIndex];
        if (bitNo == 0) {
            return myFirst;
        }

        /* Access the second word */
        int mySecond = theKeyStream[(theIndex + 1) % theKeyStream.length];
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
        theMac ^= theEngine.makeKeyStreamWord();
        theMac ^= theEngine.makeKeyStreamWord();
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
