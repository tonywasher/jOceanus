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
package net.sourceforge.joceanus.gordianknot.impl.ext.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Memoable;

/**
 * Snow3GEngine implementation.
 * Based on https://www.gsma.com/aboutus/wp-content/uploads/2014/12/snow3gspec.pdf
 */
@SuppressWarnings("checkstyle:MagicNumber")
public class GordianSnow3GEngine
        implements StreamCipher, Memoable {
    /**
     * Rijndael S-box SR.
     */
    private static final byte[] SR = {
            (byte) 0x63, (byte) 0x7C, (byte) 0x77, (byte) 0x7B, (byte) 0xF2, (byte) 0x6B, (byte) 0x6F, (byte) 0xC5,
            (byte) 0x30, (byte) 0x01, (byte) 0x67, (byte) 0x2B, (byte) 0xFE, (byte) 0xD7, (byte) 0xAB, (byte) 0x76,
            (byte) 0xCA, (byte) 0x82, (byte) 0xC9, (byte) 0x7D, (byte) 0xFA, (byte) 0x59, (byte) 0x47, (byte) 0xF0,
            (byte) 0xAD, (byte) 0xD4, (byte) 0xA2, (byte) 0xAF, (byte) 0x9C, (byte) 0xA4, (byte) 0x72, (byte) 0xC0,
            (byte) 0xB7, (byte) 0xFD, (byte) 0x93, (byte) 0x26, (byte) 0x36, (byte) 0x3F, (byte) 0xF7, (byte) 0xCC,
            (byte) 0x34, (byte) 0xA5, (byte) 0xE5, (byte) 0xF1, (byte) 0x71, (byte) 0xD8, (byte) 0x31, (byte) 0x15,
            (byte) 0x04, (byte) 0xC7, (byte) 0x23, (byte) 0xC3, (byte) 0x18, (byte) 0x96, (byte) 0x05, (byte) 0x9A,
            (byte) 0x07, (byte) 0x12, (byte) 0x80, (byte) 0xE2, (byte) 0xEB, (byte) 0x27, (byte) 0xB2, (byte) 0x75,
            (byte) 0x09, (byte) 0x83, (byte) 0x2C, (byte) 0x1A, (byte) 0x1B, (byte) 0x6E, (byte) 0x5A, (byte) 0xA0,
            (byte) 0x52, (byte) 0x3B, (byte) 0xD6, (byte) 0xB3, (byte) 0x29, (byte) 0xE3, (byte) 0x2F, (byte) 0x84,
            (byte) 0x53, (byte) 0xD1, (byte) 0x00, (byte) 0xED, (byte) 0x20, (byte) 0xFC, (byte) 0xB1, (byte) 0x5B,
            (byte) 0x6A, (byte) 0xCB, (byte) 0xBE, (byte) 0x39, (byte) 0x4A, (byte) 0x4C, (byte) 0x58, (byte) 0xCF,
            (byte) 0xD0, (byte) 0xEF, (byte) 0xAA, (byte) 0xFB, (byte) 0x43, (byte) 0x4D, (byte) 0x33, (byte) 0x85,
            (byte) 0x45, (byte) 0xF9, (byte) 0x02, (byte) 0x7F, (byte) 0x50, (byte) 0x3C, (byte) 0x9F, (byte) 0xA8,
            (byte) 0x51, (byte) 0xA3, (byte) 0x40, (byte) 0x8F, (byte) 0x92, (byte) 0x9D, (byte) 0x38, (byte) 0xF5,
            (byte) 0xBC, (byte) 0xB6, (byte) 0xDA, (byte) 0x21, (byte) 0x10, (byte) 0xFF, (byte) 0xF3, (byte) 0xD2,
            (byte) 0xCD, (byte) 0x0C, (byte) 0x13, (byte) 0xEC, (byte) 0x5F, (byte) 0x97, (byte) 0x44, (byte) 0x17,
            (byte) 0xC4, (byte) 0xA7, (byte) 0x7E, (byte) 0x3D, (byte) 0x64, (byte) 0x5D, (byte) 0x19, (byte) 0x73,
            (byte) 0x60, (byte) 0x81, (byte) 0x4F, (byte) 0xDC, (byte) 0x22, (byte) 0x2A, (byte) 0x90, (byte) 0x88,
            (byte) 0x46, (byte) 0xEE, (byte) 0xB8, (byte) 0x14, (byte) 0xDE, (byte) 0x5E, (byte) 0x0B, (byte) 0xDB,
            (byte) 0xE0, (byte) 0x32, (byte) 0x3A, (byte) 0x0A, (byte) 0x49, (byte) 0x06, (byte) 0x24, (byte) 0x5C,
            (byte) 0xC2, (byte) 0xD3, (byte) 0xAC, (byte) 0x62, (byte) 0x91, (byte) 0x95, (byte) 0xE4, (byte) 0x79,
            (byte) 0xE7, (byte) 0xC8, (byte) 0x37, (byte) 0x6D, (byte) 0x8D, (byte) 0xD5, (byte) 0x4E, (byte) 0xA9,
            (byte) 0x6C, (byte) 0x56, (byte) 0xF4, (byte) 0xEA, (byte) 0x65, (byte) 0x7A, (byte) 0xAE, (byte) 0x08,
            (byte) 0xBA, (byte) 0x78, (byte) 0x25, (byte) 0x2E, (byte) 0x1C, (byte) 0xA6, (byte) 0xB4, (byte) 0xC6,
            (byte) 0xE8, (byte) 0xDD, (byte) 0x74, (byte) 0x1F, (byte) 0x4B, (byte) 0xBD, (byte) 0x8B, (byte) 0x8A,
            (byte) 0x70, (byte) 0x3E, (byte) 0xB5, (byte) 0x66, (byte) 0x48, (byte) 0x03, (byte) 0xF6, (byte) 0x0E,
            (byte) 0x61, (byte) 0x35, (byte) 0x57, (byte) 0xB9, (byte) 0x86, (byte) 0xC1, (byte) 0x1D, (byte) 0x9E,
            (byte) 0xE1, (byte) 0xF8, (byte) 0x98, (byte) 0x11, (byte) 0x69, (byte) 0xD9, (byte) 0x8E, (byte) 0x94,
            (byte) 0x9B, (byte) 0x1E, (byte) 0x87, (byte) 0xE9, (byte) 0xCE, (byte) 0x55, (byte) 0x28, (byte) 0xDF,
            (byte) 0x8C, (byte) 0xA1, (byte) 0x89, (byte) 0x0D, (byte) 0xBF, (byte) 0xE6, (byte) 0x42, (byte) 0x68,
            (byte) 0x41, (byte) 0x99, (byte) 0x2D, (byte) 0x0F, (byte) 0xB0, (byte) 0x54, (byte) 0xBB, (byte) 0x16
    };

    /**
     * S-box SQ.
     */
    private static final byte[] SQ = {
            (byte) 0x25, (byte) 0x24, (byte) 0x73, (byte) 0x67, (byte) 0xD7, (byte) 0xAE, (byte) 0x5C, (byte) 0x30,
            (byte) 0xA4, (byte) 0xEE, (byte) 0x6E, (byte) 0xCB, (byte) 0x7D, (byte) 0xB5, (byte) 0x82, (byte) 0xDB,
            (byte) 0xE4, (byte) 0x8E, (byte) 0x48, (byte) 0x49, (byte) 0x4F, (byte) 0x5D, (byte) 0x6A, (byte) 0x78,
            (byte) 0x70, (byte) 0x88, (byte) 0xE8, (byte) 0x5F, (byte) 0x5E, (byte) 0x84, (byte) 0x65, (byte) 0xE2,
            (byte) 0xD8, (byte) 0xE9, (byte) 0xCC, (byte) 0xED, (byte) 0x40, (byte) 0x2F, (byte) 0x11, (byte) 0x28,
            (byte) 0x57, (byte) 0xD2, (byte) 0xAC, (byte) 0xE3, (byte) 0x4A, (byte) 0x15, (byte) 0x1B, (byte) 0xB9,
            (byte) 0xB2, (byte) 0x80, (byte) 0x85, (byte) 0xA6, (byte) 0x2E, (byte) 0x02, (byte) 0x47, (byte) 0x29,
            (byte) 0x07, (byte) 0x4B, (byte) 0x0E, (byte) 0xC1, (byte) 0x51, (byte) 0xAA, (byte) 0x89, (byte) 0xD4,
            (byte) 0xCA, (byte) 0x01, (byte) 0x46, (byte) 0xB3, (byte) 0xEF, (byte) 0xDD, (byte) 0x44, (byte) 0x7B,
            (byte) 0xC2, (byte) 0x7F, (byte) 0xBE, (byte) 0xC3, (byte) 0x9F, (byte) 0x20, (byte) 0x4C, (byte) 0x64,
            (byte) 0x83, (byte) 0xA2, (byte) 0x68, (byte) 0x42, (byte) 0x13, (byte) 0xB4, (byte) 0x41, (byte) 0xCD,
            (byte) 0xBA, (byte) 0xC6, (byte) 0xBB, (byte) 0x6D, (byte) 0x4D, (byte) 0x71, (byte) 0x21, (byte) 0xF4,
            (byte) 0x8D, (byte) 0xB0, (byte) 0xE5, (byte) 0x93, (byte) 0xFE, (byte) 0x8F, (byte) 0xE6, (byte) 0xCF,
            (byte) 0x43, (byte) 0x45, (byte) 0x31, (byte) 0x22, (byte) 0x37, (byte) 0x36, (byte) 0x96, (byte) 0xFA,
            (byte) 0xBC, (byte) 0x0F, (byte) 0x08, (byte) 0x52, (byte) 0x1D, (byte) 0x55, (byte) 0x1A, (byte) 0xC5,
            (byte) 0x4E, (byte) 0x23, (byte) 0x69, (byte) 0x7A, (byte) 0x92, (byte) 0xFF, (byte) 0x5B, (byte) 0x5A,
            (byte) 0xEB, (byte) 0x9A, (byte) 0x1C, (byte) 0xA9, (byte) 0xD1, (byte) 0x7E, (byte) 0x0D, (byte) 0xFC,
            (byte) 0x50, (byte) 0x8A, (byte) 0xB6, (byte) 0x62, (byte) 0xF5, (byte) 0x0A, (byte) 0xF8, (byte) 0xDC,
            (byte) 0x03, (byte) 0x3C, (byte) 0x0C, (byte) 0x39, (byte) 0xF1, (byte) 0xB8, (byte) 0xF3, (byte) 0x3D,
            (byte) 0xF2, (byte) 0xD5, (byte) 0x97, (byte) 0x66, (byte) 0x81, (byte) 0x32, (byte) 0xA0, (byte) 0x00,
            (byte) 0x06, (byte) 0xCE, (byte) 0xF6, (byte) 0xEA, (byte) 0xB7, (byte) 0x17, (byte) 0xF7, (byte) 0x8C,
            (byte) 0x79, (byte) 0xD6, (byte) 0xA7, (byte) 0xBF, (byte) 0x8B, (byte) 0x3F, (byte) 0x1F, (byte) 0x53,
            (byte) 0x63, (byte) 0x75, (byte) 0x35, (byte) 0x2C, (byte) 0x60, (byte) 0xFD, (byte) 0x27, (byte) 0xD3,
            (byte) 0x94, (byte) 0xA5, (byte) 0x7C, (byte) 0xA1, (byte) 0x05, (byte) 0x58, (byte) 0x2D, (byte) 0xBD,
            (byte) 0xD9, (byte) 0xC7, (byte) 0xAF, (byte) 0x6B, (byte) 0x54, (byte) 0x0B, (byte) 0xE0, (byte) 0x38,
            (byte) 0x04, (byte) 0xC8, (byte) 0x9D, (byte) 0xE7, (byte) 0x14, (byte) 0xB1, (byte) 0x87, (byte) 0x9C,
            (byte) 0xDF, (byte) 0x6F, (byte) 0xF9, (byte) 0xDA, (byte) 0x2A, (byte) 0xC4, (byte) 0x59, (byte) 0x16,
            (byte) 0x74, (byte) 0x91, (byte) 0xAB, (byte) 0x26, (byte) 0x61, (byte) 0x76, (byte) 0x34, (byte) 0x2B,
            (byte) 0xAD, (byte) 0x99, (byte) 0xFB, (byte) 0x72, (byte) 0xEC, (byte) 0x33, (byte) 0x12, (byte) 0xDE,
            (byte) 0x98, (byte) 0x3B, (byte) 0xC0, (byte) 0x9B, (byte) 0x3E, (byte) 0x18, (byte) 0x10, (byte) 0x3A,
            (byte) 0x56, (byte) 0xE1, (byte) 0x77, (byte) 0xC9, (byte) 0x1E, (byte) 0x9E, (byte) 0x95, (byte) 0xA3,
            (byte) 0x90, (byte) 0x19, (byte) 0xA8, (byte) 0x6C, (byte) 0x09, (byte) 0xD0, (byte) 0xF0, (byte) 0x86
    };

    /**
     * LFSR State.
     */
    private final int[] lfsrState = new int[16];

    /**
     * FSM State.
     */
    private final int[] fsmState = new int[3];

    /**
     * index of next byte in keyStream.
     */
    private int theIndex;

    /**
     * Advanced stream.
     */
    private final byte[] keyStream = new byte[Integer.BYTES];

    /**
     * The iterations.
     */
    private int theIterations;

    /**
     * Reset state.
     */
    private GordianSnow3GEngine theResetState;

    /**
     * Constructor.
     */
    public GordianSnow3GEngine() {
    }

    /**
     * Constructor.
     * @param pSource the source engine
     */
    private GordianSnow3GEngine(final GordianSnow3GEngine pSource) {
        reset(pSource);
    }

    /**
     * initialise a Snow3G cipher.
     * @param forEncryption whether or not we are for encryption.
     * @param params the parameters required to set up the cipher.
     * @exception IllegalArgumentException if the params argument is inappropriate.
     */
    public void init(final boolean forEncryption,
                     final CipherParameters params) {
        /*
         * encryption and decryption is completely symmetrical, so the 'forEncryption' is
         * irrelevant. (Like 90% of stream ciphers)
         */

        /* Determine parameters */
        CipherParameters myParams = params;
        byte[] newKey = null;
        byte[] newIV = null;
        if ((myParams instanceof ParametersWithIV)) {
            final ParametersWithIV ivParams = (ParametersWithIV) myParams;
            newIV = ivParams.getIV();
            myParams = ivParams.getParameters();
        }
        if (myParams instanceof KeyParameter) {
            final KeyParameter keyParam = (KeyParameter) myParams;
            newKey = keyParam.getKey();
        }

        /* Initialise engine and mark as initialised */
        theIndex = 0;
        theIterations = 0;
        setKeyAndIV(newKey, newIV);

        /* Save reset state */
        theResetState = copy();
    }

    /**
     * Obtain Max iterations.
     * @return the maximum iterations
     */
    protected int getMaxIterations() {
        return 625;
    }

    @Override
    public String getAlgorithmName() {
        return "Snow3G";
    }

    @Override
    public int processBytes(final byte[] in,
                            final int inOff,
                            final int len,
                            final byte[] out,
                            final int outOff) {
        /* Check for errors */
        if (theResetState == null) {
            throw new IllegalStateException(getAlgorithmName() + " not initialised");
        }
        if ((inOff + len) > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if ((outOff + len) > out.length) {
            throw new OutputLengthException("output buffer too short");
        }

        /* Loop through the input bytes */
        for (int i = 0; i < len; i++) {
            out[i + outOff] = returnByte(in[i + inOff]);
        }
        return len;
    }

    @Override
    public void reset() {
        if (theResetState != null) {
            reset(theResetState);
        }
    }

    @Override
    public byte returnByte(final byte in) {
        /* Make the keyStream if required */
        if (theIndex == 0) {
            makeKeyStream();
        }

        /* Map the next byte and adjust index */
        final byte out = (byte) (keyStream[theIndex] ^ in);
        theIndex = (theIndex + 1) % Integer.BYTES;

        /* Return the mapped character */
        return out;
    }

    /**
     * Decode a 32-bit value from a buffer (little-endian).
     *
     * @param buf the input buffer
     * @param off the input offset
     * @return the decoded value
     */
    private static int decode32be(final byte[] buf, final int off) {
        return ((buf[off] & 0xFF) << 24)
                | ((buf[off + 1] & 0xFF) << 16)
                | ((buf[off + 2] & 0xFF) << 8)
                | (buf[off + 3] & 0xFF);
    }

    /**
     * Encode a 32-bit value into a buffer (little-endian).
     *
     * @param val the value to encode
     * @param buf the output buffer
     * @param off the output offset
     */
    private static void encode32be(final int val, final byte[] buf, final int off) {
        buf[off] = (byte) (val >> 24);
        buf[off + 1] = (byte) (val >> 16);
        buf[off + 2] = (byte) (val >> 8);
        buf[off + 3] = (byte) (val);
    }

    /* MULx.
     * Input v: an 8-bit input.
     * Input c: an 8-bit input.
     * Output : an 8-bit output.
     * See section 3.1.1 for details.
     */
    int mulX(final byte v, final int c) {
        if ((v & 0x80) != 0) {
            return ((v << 1) ^ c) & 0xFF;
        } else {
            return (v << 1) & 0xFF;
        }
    }

    /* MULxPOW.
     * Input v: an 8-bit input.
     * Input i: a positive integer.
     * Input c: an 8-bit input.
     * Output : an 8-bit output.
     * See section 3.1.2 for details.
     */
    int mulXpow(final byte v, final int i, final int c) {
        if (i == 0) {
            return v & 0xFF;
        } else {
            return mulX((byte) mulXpow(v, i - 1, c), c);
        }
    }

    /* The function MUL alpha.
     * Input c: 8-bit input.
     * Output : 32-bit output.
     * See section 3.4.2 for details.
     */
    int mulAlpha(final byte c) {
        return ((mulXpow(c, 23, 0xa9) << 24)
                | (mulXpow(c, 245, 0xa9) << 16)
                | (mulXpow(c, 48, 0xa9) << 8)
                | (mulXpow(c, 239, 0xa9)));
    }

    /* The function DIV alpha.
     * Input c: 8-bit input.
     * Output : 32-bit output.
     * See section 3.4.3 for details.
     */
    int divAlpha(final byte c) {
        return ((mulXpow(c, 16, 0xa9) << 24)
                | (mulXpow(c, 39, 0xa9) << 16)
                | (mulXpow(c, 6, 0xa9) << 8)
                | (mulXpow(c, 64, 0xa9)));
    }

    /* The 32x32-bit S-Box S1
     * Input: a 32-bit input.
     * Output: a 32-bit output of S1 box.
     * See section 3.3.1.
     */
    int s1(final int w) {
        final byte srw0 = SR[((w >> 24) & 0xff)];
        final byte srw1 = SR[((w >> 16) & 0xff)];
        final byte srw2 = SR[((w >> 8) & 0xff)];
        final byte srw3 = SR[((w) & 0xff)];
        final int r0 = ((mulX(srw0, 0x1b))
                ^ (srw1)
                ^ (srw2)
                ^ ((mulX(srw3, 0x1b)) ^ srw3)
        ) & 0xFF;
        final int r1 = (((mulX(srw0, 0x1b)) ^ srw0)
                ^ (mulX(srw1, 0x1b))
                ^ (srw2)
                ^ (srw3)
        ) & 0xFF;
        final int r2 = ((srw0)
                ^ ((mulX(srw1, 0x1b)) ^ srw1)
                ^ (mulX(srw2, 0x1b))
                ^ (srw3)
        ) & 0xFF;
        final int r3 = ((srw0)
                ^ (srw1)
                ^ ((mulX(srw2, 0x1b)) ^ srw2)
                ^ (mulX(srw3, 0x1b))
        ) & 0xFF;
        return (((r0) << 24) | ((r1) << 16) | ((r2) << 8)
                | (r3));
    }

    /* The 32x32-bit S-Box S2
     * Input: a 32-bit input.
     * Output: a 32-bit output of S2 box.
     * See section 3.3.2.
     */
    int s2(final int w) {
        final byte sqw0 = SQ[((w >> 24) & 0xff)];
        final byte sqw1 = SQ[((w >> 16) & 0xff)];
        final byte sqw2 = SQ[((w >> 8) & 0xff)];
        final byte sqw3 = SQ[((w) & 0xff)];
        final int r0 = ((mulX(sqw0, 0x69))
                ^ (sqw1)
                ^ (sqw2)
                ^ ((mulX(sqw3, 0x69)) ^ sqw3)
        ) & 0xFF;
        final int r1 = (((mulX(sqw0, 0x69)) ^ sqw0)
                ^ (mulX(sqw1, 0x69))
                ^ (sqw2)
                ^ (sqw3)
        ) & 0xFF;
        final int r2 = ((sqw0)
                ^ ((mulX(sqw1, 0x69)) ^ sqw1)
                ^ (mulX(sqw2, 0x69))
                ^ (sqw3)
        ) & 0xFF;
        final int r3 = ((sqw0)
                ^ (sqw1)
                ^ ((mulX(sqw2, 0x69)) ^ sqw2)
                ^ (mulX(sqw3, 0x69))
        ) & 0xFF;
        return (((r0) << 24) | ((r1) << 16) | ((r2) << 8)
                | (r3));
    }

    /* Clocking LFSR in initialization mode.
     * LFSR Registers S0 to S15 are updated as the LFSR receives a single clock.
     * Input f: a 32-bit word comes from output of FSM.
     * See section 3.4.4.
     */
    void clockLFSRInitializationMode(final int f) {
        final int v = (((lfsrState[0] << 8) & 0xffffff00)
                ^ (mulAlpha((byte) ((lfsrState[0] >>> 24) & 0xff)))
                ^ (lfsrState[2])
                ^ ((lfsrState[11] >>> 8) & 0x00ffffff)
                ^ (divAlpha((byte) ((lfsrState[11]) & 0xff)))
                ^ (f)
        );
        lfsrState[0] = lfsrState[1];
        lfsrState[1] = lfsrState[2];
        lfsrState[2] = lfsrState[3];
        lfsrState[3] = lfsrState[4];
        lfsrState[4] = lfsrState[5];
        lfsrState[5] = lfsrState[6];
        lfsrState[6] = lfsrState[7];
        lfsrState[7] = lfsrState[8];
        lfsrState[8] = lfsrState[9];
        lfsrState[9] = lfsrState[10];
        lfsrState[10] = lfsrState[11];
        lfsrState[11] = lfsrState[12];
        lfsrState[12] = lfsrState[13];
        lfsrState[13] = lfsrState[14];
        lfsrState[14] = lfsrState[15];
        lfsrState[15] = v;
    }

    /* Clocking LFSR in keystream mode.
     * LFSR Registers S0 to S15 are updated as the LFSR receives a single clock.
     * See section 3.4.5.
     */
    void clockLFSRKeyStreamMode() {
        final int v = (((lfsrState[0] << 8) & 0xffffff00)
                ^ (mulAlpha((byte) ((lfsrState[0] >>> 24) & 0xff)))
                ^ (lfsrState[2])
                ^ ((lfsrState[11] >>> 8) & 0x00ffffff)
                ^ (divAlpha((byte) ((lfsrState[11]) & 0xff)))
        );
        lfsrState[0] = lfsrState[1];
        lfsrState[1] = lfsrState[2];
        lfsrState[2] = lfsrState[3];
        lfsrState[3] = lfsrState[4];
        lfsrState[4] = lfsrState[5];
        lfsrState[5] = lfsrState[6];
        lfsrState[6] = lfsrState[7];
        lfsrState[7] = lfsrState[8];
        lfsrState[8] = lfsrState[9];
        lfsrState[9] = lfsrState[10];
        lfsrState[10] = lfsrState[11];
        lfsrState[11] = lfsrState[12];
        lfsrState[12] = lfsrState[13];
        lfsrState[13] = lfsrState[14];
        lfsrState[14] = lfsrState[15];
        lfsrState[15] = v;
    }

    /* Clocking FSM.
     * Produces a 32-bit word F.
     * Updates FSM registers R1, R2, R3.
     * See Section 3.4.6.
     */
    int clockFSM() {
        final int f = ((lfsrState[15] + fsmState[0]) & 0xffffffff) ^ fsmState[1];
        final int r = (fsmState[1] + (fsmState[2] ^ lfsrState[5])) & 0xffffffff;
        fsmState[2] = s2(fsmState[1]);
        fsmState[1] = s1(fsmState[0]);
        fsmState[0] = r;
        return f;
    }

    /* Initialization.
     * Input k[4]: Four 32-bit words making up 128-bit key.
     * Input IV[4]: Four 32-bit words making 128-bit initialization variable.
     * Output: All the LFSRs and FSM are initialized for key generation.
     * See Section 4.1.
     */
    void setKeyAndIV(final byte[] key, final byte[] iv) {
        /* Check lengths */
        if (key == null || key.length != 16) {
            throw new IllegalArgumentException("A key of 16 bytes is needed");
        }
        if (iv == null || iv.length != 16) {
            throw new IllegalArgumentException("An IV of 16 bytes is needed");
        }

        /* Generate four subkeys */
        final int k0 = decode32be(key, 12);
        final int k1 = decode32be(key, 8);
        final int k2 = decode32be(key, 4);
        final int k3 = decode32be(key, 0);

        /* Generate four subvectors */
        final int i0 = decode32be(iv, 12);
        final int i1 = decode32be(iv, 8);
        final int i2 = decode32be(iv, 4);
        final int i3 = decode32be(iv, 0);

        lfsrState[15] = k3 ^ i0;
        lfsrState[14] = k2;
        lfsrState[13] = k1;
        lfsrState[12] = k0 ^ i1;
        lfsrState[11] = k3 ^ 0xffffffff;
        lfsrState[10] = k2 ^ 0xffffffff ^ i2;
        lfsrState[9] = k1 ^ 0xffffffff ^ i3;
        lfsrState[8] = k0 ^ 0xffffffff;
        lfsrState[7] = k3;
        lfsrState[6] = k2;
        lfsrState[5] = k1;
        lfsrState[4] = k0;
        lfsrState[3] = k3 ^ 0xffffffff;
        lfsrState[2] = k2 ^ 0xffffffff;
        lfsrState[1] = k1 ^ 0xffffffff;
        lfsrState[0] = k0 ^ 0xffffffff;
        fsmState[0] = 0x0;
        fsmState[1] = 0x0;
        fsmState[2] = 0x0;
        for (int i = 0; i < 32; i++) {
            final int f = clockFSM();
            clockLFSRInitializationMode(f);
        }
        /* Clock FSM once. Discard the output. */
        clockFSM();
        /* Clock LFSR in keystream mode once. */
        clockLFSRKeyStreamMode();
     }

    /* Generation of Keystream.
     * input n: number of 32-bit words of keystream.
     * input z: space for the generated keystream, assumes
     * memory is allocated already.
     * output: generated keystream which is filled in z
     * See section 4.2.
     */
    void makeKeyStream() {
        if (theIterations++ >= getMaxIterations()) {
            throw new IllegalStateException("Too much data processed by singleKey/IV");
        }

        final int f = clockFSM(); // STEP 1 */
        encode32be(f ^ lfsrState[0], keyStream, 0); // STEP 2 */
        /* Note that ks[t] corresponds to z_{t+1} in section 4.2 */
        clockLFSRKeyStreamMode(); // STEP 3 */
    }

    @Override
    public GordianSnow3GEngine copy() {
        return new GordianSnow3GEngine(this);
    }

    @Override
    public void reset(final Memoable pState) {
        final GordianSnow3GEngine e = (GordianSnow3GEngine) pState;
        System.arraycopy(e.lfsrState, 0, lfsrState, 0, lfsrState.length);
        System.arraycopy(e.fsmState, 0, fsmState, 0, fsmState.length);
        System.arraycopy(e.keyStream, 0, keyStream, 0, keyStream.length);
        theIterations = e.theIterations;
        theIndex = e.theIndex;
    }
}
