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

import org.bouncycastle.util.Memoable;

/**
 * Zuc256Mac implementation.
 * Based on http://www.is.cas.cn/ztzl2016/zouchongzhi/201801/W020180126529970733243.pdf
 */
@SuppressWarnings("checkstyle:MagicNumber")
public class GordianZuc256Engine
        extends GordianZuc128Engine {
    /**
     * the constants D.
     */
    private static final byte[] EK_D = {
            0b0100010, 0b0101111, 0b0100100, 0b0101010, 0b1101101, 0b1000000, 0b1000000, 0b1000000,
            0b1000000, 0b1000000, 0b1000000, 0b1000000, 0b1000000, 0b1010010, 0b0010000, 0b0110000
    };

    /**
     * the constants D for 32 bit Mac.
     */
    private static final byte[] EK_D32 = {
            0b0100010, 0b0101111, 0b0100101, 0b0101010, 0b1101101, 0b1000000, 0b1000000, 0b1000000,
            0b1000000, 0b1000000, 0b1000000, 0b1000000, 0b1000000, 0b1010010, 0b0010000, 0b0110000
    };

    /**
     * the constants D for 64 bit Mac.
     */
    private static final byte[] EK_D64 = {
            0b0100011, 0b0101111, 0b0100100, 0b0101010, 0b1101101, 0b1000000, 0b1000000, 0b1000000,
            0b1000000, 0b1000000, 0b1000000, 0b1000000, 0b1000000, 0b1010010, 0b0010000, 0b0110000
    };

    /**
     * the constants D for 128 bit Mac.
     */
    private static final byte[] EK_D128 = {
            0b0100011, 0b0101111, 0b0100101, 0b0101010, 0b1101101, 0b1000000, 0b1000000, 0b1000000,
            0b1000000, 0b1000000, 0b1000000, 0b1000000, 0b1000000, 0b1010010, 0b0010000, 0b0110000
    };

    /**
     * The selected D constants.
     */
    private byte[] theD;

    /**
     * Constructor for streamCipher.
     */
    public GordianZuc256Engine() {
        theD = EK_D;
    }

    /**
     * Constructor for Mac.
     * @param pLength the Mac length
     */
    public GordianZuc256Engine(final int pLength) {
        switch (pLength) {
            case 32:
                theD = EK_D32;
                break;
            case 64:
                theD = EK_D64;
                break;
            case 128:
                theD = EK_D128;
                break;
            default:
                throw new IllegalArgumentException("Unsupported length: " + pLength);
        }
    }

    /**
     * Constructor for Memoable.
     * @param pSource the source engine
     */
    private GordianZuc256Engine(final GordianZuc256Engine pSource) {
        super(pSource);
    }

    /**
     * Obtain Max iterations.
     * @return the maximum iterations
     */
    protected int getMaxIterations() {
        return 625;
    }

    /**
     * Obtain Algorithm Name.
     * @return the name
     */
    public String getAlgorithmName() {
        return "Zuc-256";
    }

    /**
     * Build a 31-bit integer from constituent parts.
     * @param a part A
     * @param b part B
     * @param c part C
     * @param d part D
     * @return the built integer
     */
    private static int makeU31(final byte a, final byte b, final byte c, final byte d) {
        return (((a & 0xFF) << 23) | ((b & 0xFF) << 16) | ((c & 0xFF) << 8) | (d & 0xFF));
    }

    /**
     * Process key and IV into LFSR.
     * @param pLFSR the LFSR
     * @param k the key
     * @param iv the iv
     */
    protected void setKeyAndIV(final int[] pLFSR,
                               final byte[] k,
                               final byte[] iv) {
        /* Check lengths */
        if (k == null || k.length != 32) {
            throw new IllegalArgumentException("A key of 32 bytes is needed");
        }
        if (iv == null || iv.length != 25) {
            throw new IllegalArgumentException("An IV of 25 bytes is needed");
        }

        /* expand key and IV */
        pLFSR[0] = makeU31(k[0], theD[0], k[21], k[16]);
        pLFSR[1] = makeU31(k[1], theD[1], k[22], k[17]);
        pLFSR[2] = makeU31(k[2], theD[2], k[23], k[18]);
        pLFSR[3] = makeU31(k[3], theD[3], k[24], k[19]);
        pLFSR[4] = makeU31(k[4], theD[4], k[25], k[20]);
        pLFSR[5] = makeU31(iv[0], (byte) (theD[5] | (iv[17] & 0x3F)), k[5], k[26]);
        pLFSR[6] = makeU31(iv[1], (byte) (theD[6] | (iv[18] & 0x3F)), k[6], k[27]);
        pLFSR[7] = makeU31(iv[10], (byte) (theD[7] | (iv[19] & 0x3F)), k[7], iv[2]);
        pLFSR[8] = makeU31(k[8], (byte) (theD[8] | (iv[20] & 0x3F)), iv[3], iv[11]);
        pLFSR[9] = makeU31(k[9], (byte) (theD[9] | (iv[21] & 0x3F)), iv[12], iv[4]);
        pLFSR[10] = makeU31(iv[5], (byte) (theD[10] | (iv[22] & 0x3F)), k[10], k[28]);
        pLFSR[11] = makeU31(k[11], (byte) (theD[11] | (iv[23] & 0x3F)), iv[6], iv[13]);
        pLFSR[12] = makeU31(k[12], (byte) (theD[12] | (iv[24] & 0x3F)), iv[7], iv[14]);
        pLFSR[13] = makeU31(k[13], theD[13], iv[15], iv[8]);
        pLFSR[14] = makeU31(k[14], (byte) (theD[14] | ((k[31] >>> 4) & 0xF)), iv[16], iv[9]);
        pLFSR[15] = makeU31(k[15], (byte) (theD[15] | (k[31] & 0xF)), k[30], k[29]);
    }

    /**
     * Create a copy of the engine.
     * @return the copy
     */
    public GordianZuc256Engine copy() {
        return new GordianZuc256Engine(this);
    }

    /**
     * Reset from saved engine state.
     * @param pState teh state to restore
     */
    public void reset(final Memoable pState) {
        final GordianZuc256Engine e = (GordianZuc256Engine) pState;
        super.reset(pState);
        theD = e.theD;
    }
}
