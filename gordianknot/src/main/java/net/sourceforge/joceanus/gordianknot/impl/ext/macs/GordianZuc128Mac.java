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
package net.sourceforge.joceanus.gordianknot.impl.ext.macs;

import net.sourceforge.joceanus.gordianknot.impl.ext.engines.GordianZuc128Engine;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;

/**
 * Zuc128Mac implementation.
 * Based on <a href="http://www.qtc.jp/3GPP/Specs/eea3eia3specificationv16.pdf">Zuc Specification</a>
 * Donated to BouncyCastle
 */
public class GordianZuc128Mac
        implements Mac {
    /**
     * The Maximum Bit Mask.
     */
    private static final int TOPBIT = 0x80;

    /**
     * The Zuc128 Engine.
     */
    private final GordianZuc128Engine theEngine;

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
    private GordianZuc128Engine theState;

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
    public GordianZuc128Mac() {
        theEngine = new GordianZuc128Engine();
        theKeyStream = new int[2];
    }

    /**
     * Obtain Algorithm Name.
     * @return the name
     */
    public String getAlgorithmName() {
        return "Zuc128Mac";
    }

    /**
     * Obtain Mac Size.
     * @return the size in Bytes
     */
    public int getMacSize() {
        return Integer.BYTES;
    }

    /**
     * Initialise the Mac.
     * @param pParams the parameters
     */
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
        theMac = 0;

        /* Initialise the KeyStream */
        for (int i = 0; i < theKeyStream.length - 1; i++) {
            theKeyStream[i] = theEngine.makeKeyStreamWord();
        }
        theWordIndex = theKeyStream.length - 1;
        theByteIndex = Integer.BYTES - 1;
    }

    /**
     * Update the mac with a single byte.
     * @param in the byte to update with
     */
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

    /**
     * Update the mac.
     * @param in the input buffer
     * @param inOff the starting offset in the input buffer
     * @param len the length of data to process
     */
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

    /**
     * Finalize the mac.
     * @param out the output buffer
     * @param outOff the starting offset in the input buffer
     * @return the size of the mac
     */
    public int doFinal(final byte[] out, final int outOff) {
        /* Finish the Mac and output it */
        shift4NextByte();
        theMac ^= getKeyStreamWord(theByteIndex * Byte.SIZE);
        theMac ^= getFinalWord();
        GordianZuc128Engine.encode32be(theMac, out, outOff);

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
