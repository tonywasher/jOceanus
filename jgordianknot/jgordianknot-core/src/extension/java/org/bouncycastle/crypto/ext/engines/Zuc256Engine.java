/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
package org.bouncycastle.crypto.ext.engines;

import org.bouncycastle.util.Memoable;

/**
 * Zuc256Mac implementation.
 * Based on http://www.is.cas.cn/ztzl2016/zouchongzhi/201801/W020180126529970733243.pdf
 */
public class Zuc256Engine extends Zuc128Engine {
    /* the constants D */
    private static final byte[] EK_d = new byte[] {
            0b0100010, 0b0101111, 0b0100100, 0b0101010, 0b1101101, 0b1000000, 0b1000000, 0b1000000,
            0b1000000, 0b1000000, 0b1000000, 0b1000000, 0b1000000, 0b1010010, 0b0010000, 0b0110000
    };

    /* the constants D for 32 bit Mac*/
    private static final byte[] EK_d32  = new byte[] {
            0b0100010, 0b0101111, 0b0100101, 0b0101010, 0b1101101, 0b1000000, 0b1000000, 0b1000000,
            0b1000000, 0b1000000, 0b1000000, 0b1000000, 0b1000000, 0b1010010, 0b0010000, 0b0110000
    };

    /* the constants D for 64 bit Mac */
    private static final byte[] EK_d64 = new byte[] {
            0b0100011, 0b0101111, 0b0100100, 0b0101010, 0b1101101, 0b1000000, 0b1000000, 0b1000000,
            0b1000000, 0b1000000, 0b1000000, 0b1000000, 0b1000000, 0b1010010, 0b0010000, 0b0110000
    };

    /* the constants D for 128 bit Mac */
    private static final byte[] EK_d128 = new byte[] {
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
    public Zuc256Engine(){
        theD = EK_d;
    }

    /**
     * Constructor for Mac.
     */
    public Zuc256Engine(int pLength){
        switch (pLength) {
            case 32:
                theD = EK_d32;
                break;
            case 64:
                theD = EK_d64;
                break;
            case 128:
                theD = EK_d128;
                break;
            default:
                throw new IllegalArgumentException("Unsupported length: " + pLength);
        }
    }

    /**
     * Constructor for Memoable.
     * @param pSource the source engine
     */
    private Zuc256Engine(Zuc256Engine pSource){
        super(pSource);
    }

    @Override
    protected int getMaxIterations() {
        return 625;
    }

    @Override
    public String getAlgorithmName() {
        return "Zuc-256";
    }

    static int MAKEU31(byte a, byte b, byte c, byte d) {
        return (((a & 0xFF) << 23) | ((b & 0xFF) << 16) | ((c & 0xFF) << 8) | (d & 0xFF));
    }

    /* initialize */
    protected void setKeyAndIV(int[] pLFSR, byte[] k, byte[] iv)
    {
        /* Check lengths */
        if (k == null || k.length != 32) {
            throw new IllegalArgumentException("A key of 32 bytes is needed");
        }
        if (iv == null || iv.length != 23) {
            throw new IllegalArgumentException("An IV of 23 bytes is needed");
        }

        /* Expand the 6bit part of the IV from 6 bytes to 8 bytes */
        byte iv17 = (byte) ((iv[17] >>> 2) & 0x3F);
        byte iv18 = (byte) (((iv[17] << 4) & 0x3F) | ((iv[18] >>> 4) & 0xF));
        byte iv19 = (byte) (((iv[18] << 2) & 0x3F) | ((iv[19] >>> 6) & 0x3));
        byte iv20 = (byte) (iv[19] & 0x3F);
        byte iv21 = (byte) ((iv[20] >>> 2) & 0x3F);
        byte iv22 = (byte) (((iv[20] << 4) & 0x3F) | ((iv[21] >>> 4) & 0xF));
        byte iv23 = (byte) (((iv[21] << 2) & 0x3F) | ((iv[22] >>> 6) & 0x3));
        byte iv24 = (byte) (iv[22] & 0x3F);

        /* expand key and IV */
        pLFSR[0] = MAKEU31(k[0], theD[0], k[21], k[16]);
        pLFSR[1] = MAKEU31(k[1], theD[1], k[22], k[17]);
        pLFSR[2] = MAKEU31(k[2], theD[2], k[23], k[18]);
        pLFSR[3] = MAKEU31(k[3], theD[3], k[24], k[19]);
        pLFSR[4] = MAKEU31(k[4], theD[4], k[25], k[20]);
        pLFSR[5] = MAKEU31(iv[0], (byte)(theD[5] | iv17), k[5], k[26]);
        pLFSR[6] = MAKEU31(iv[1], (byte)(theD[6] | iv18), k[6], k[27]);
        pLFSR[7] = MAKEU31(iv[10], (byte)(theD[7] | iv19), k[7], iv[2]);
        pLFSR[8] = MAKEU31(k[8], (byte)(theD[8] | iv20), iv[3], iv[11]);
        pLFSR[9] = MAKEU31(k[9], (byte)(theD[9] | iv21), iv[12], iv[4]);
        pLFSR[10] = MAKEU31(iv[5], (byte)(theD[10] | iv22), k[10], k[28]);
        pLFSR[11] = MAKEU31(k[11], (byte)(theD[11] | iv23), iv[6], iv[13]);
        pLFSR[12] = MAKEU31(k[12], (byte)(theD[12] | iv24), iv[7], iv[14]);
        pLFSR[13] = MAKEU31(k[13], theD[13], iv[15], iv[8]);
        pLFSR[14] = MAKEU31(k[14], (byte)(theD[14] | ((k[31] >>> 4) & 0xF)), iv[16], iv[9]);
        pLFSR[15] = MAKEU31(k[15], (byte)(theD[15] | (k[31] & 0xF)), k[30], k[29]);
    }

    @Override
    public Zuc256Engine copy() {
        return new Zuc256Engine(this);
    }

    @Override
    public void reset(final Memoable pState) {
        final Zuc256Engine e = (Zuc256Engine) pState;
        super.reset(pState);
        theD = e.theD;
    }
}
