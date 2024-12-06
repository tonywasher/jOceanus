/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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

import java.util.Arrays;

/**
 * StreamCipher implementation based on the SosemanukFast java implementation.
 * <p>
 * Based on http://www.ecrypt.eu.org/stream/e2-rabbit.html.
 */
@SuppressWarnings("checkstyle:MagicNumber")
public class GordianRabbitEngine
        implements StreamCipher, Memoable {
    /**
     * Number of variables.
     */
    private static final int NUM_VARS = 8;

    /**
     * Advanced stream length.
     */
    private static final int STREAM_LEN = 80;

    /**
     * context.
     */
    static class GordianRabbitContext {
        /**
         * X state.
         */
        private int[] x = new int[NUM_VARS];

        /**
         * c stat.
         */
        private int[] c = new int[NUM_VARS];

        /**
         * carry state.
         */
        private int carry;

        /**
         * Copy from.
         * @param pSource the source context
         */
        void copyFrom(final GordianRabbitContext pSource) {
            System.arraycopy(pSource.x, 0, x, 0, NUM_VARS);
            System.arraycopy(pSource.c, 0, c, 0, NUM_VARS);
            carry = pSource.carry;
        }
    }

    /**
     * Work context.
     */
    private GordianRabbitContext work = new GordianRabbitContext();

    /**
     * master context.
     */
    private GordianRabbitContext master = new GordianRabbitContext();

    /**
     * index of next byte in keyStream.
     */
    private int theIndex;

    /**
     * Advanced stream.
     */
    private final byte[] keyStream = new byte[STREAM_LEN];

    /**
     * Reset state.
     */
    private GordianRabbitEngine theResetState;

    /**
     * Constructor.
     */
    public GordianRabbitEngine() {
    }

    /**
     * Constructor.
     * @param pSource the source engine
     */
    private GordianRabbitEngine(final GordianRabbitEngine pSource) {
        reset(pSource);
    }

    /**
     * initialise a Rabbit cipher.
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
        setKey(newKey);
        setIV(newIV);
        makeKeyStream();

        /* Save reset state */
        theResetState = copy();
    }

    @Override
    public String getAlgorithmName() {
        return "Rabbit";
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
        final byte out = (byte) (keyStream[theIndex] ^ in);
        theIndex = (theIndex + 1) % STREAM_LEN;

        if (theIndex == 0) {
            makeKeyStream();
        }
        return out;
    }

    /**
     * Decode a 32-bit value from a buffer (little-endian).
     *
     * @param buf the input buffer
     * @param off the input offset
     * @return the decoded value
     */
    private static int decode32le(final byte[] buf, final int off) {
        return (buf[off] & 0xFF)
                | ((buf[off + 1] & 0xFF) << 8)
                | ((buf[off + 2] & 0xFF) << 16)
                | ((buf[off + 3] & 0xFF) << 24);
    }

    /**
     * Encode a 32-bit value into a buffer (little-endian).
     *
     * @param val the value to encode
     * @param buf the output buffer
     * @param off the output offset
     */
    private static void encode32le(final int val, final byte[] buf, final int off) {
        buf[off] = (byte) val;
        buf[off + 1] = (byte) (val >> 8);
        buf[off + 2] = (byte) (val >> 16);
        buf[off + 3] = (byte) (val >> 24);
    }

    /* Square a 32-bit unsigned integer to obtain the 64-bit result and return */
    /* the upper 32 bits XOR the lower 32 bits */
    static int rabbitGFunc(final int x) {
        /* Construct high and low argument for squaring */
        final int a = x & 0xFFFF;
        final int b = x >>> 16;

        /* Calculate high and low result of squaring */
        final int h = ((((a * a) >>> 17) + (a * b)) >>> 15) + b * b;
        final int l = x * x;

        /* Return high XOR low */
        return (h ^ l);
    }

    /* Calculate the next internal state */
    static void rabbitNextState(final GordianRabbitContext pContext) {
        /* Save old counter values */
        final int[] cOld = Arrays.copyOf(pContext.c, NUM_VARS);
        final int[] g = new int[NUM_VARS];

        /* Calculate new counter values */
        pContext.c[0] = (pContext.c[0] + 0x4D34D34D + pContext.carry);
        pContext.c[1] = (pContext.c[1] + 0xD34D34D3 + (Integer.compareUnsigned(pContext.c[0], cOld[0]) < 0 ? 1 : 0));
        pContext.c[2] = (pContext.c[2] + 0x34D34D34 + (Integer.compareUnsigned(pContext.c[1], cOld[1]) < 0 ? 1 : 0));
        pContext.c[3] = (pContext.c[3] + 0x4D34D34D + (Integer.compareUnsigned(pContext.c[2], cOld[2]) < 0 ? 1 : 0));
        pContext.c[4] = (pContext.c[4] + 0xD34D34D3 + (Integer.compareUnsigned(pContext.c[3], cOld[3]) < 0 ? 1 : 0));
        pContext.c[5] = (pContext.c[5] + 0x34D34D34 + (Integer.compareUnsigned(pContext.c[4], cOld[4]) < 0 ? 1 : 0));
        pContext.c[6] = (pContext.c[6] + 0x4D34D34D + (Integer.compareUnsigned(pContext.c[5], cOld[5]) < 0 ? 1 : 0));
        pContext.c[7] = (pContext.c[7] + 0xD34D34D3 + (Integer.compareUnsigned(pContext.c[6], cOld[6]) < 0 ? 1 : 0));
        pContext.carry = Integer.compareUnsigned(pContext.c[7], cOld[7]) < 0 ? 1 : 0;

        /* Calculate the g-values */
        for (int i = 0; i < 8; i++) {
            g[i] = rabbitGFunc((int) (pContext.x[i] + pContext.c[i]));
        }

        /* Calculate new state values */
        pContext.x[0] = (g[0] + rotl32(g[7], 16) + rotl32(g[6], 16));
        pContext.x[1] = (g[1] + rotl32(g[0], 8) + g[7]);
        pContext.x[2] = (g[2] + rotl32(g[1], 16) + rotl32(g[0], 16));
        pContext.x[3] = (g[3] + rotl32(g[2], 8) + g[1]);
        pContext.x[4] = (g[4] + rotl32(g[3], 16) + rotl32(g[2], 16));
        pContext.x[5] = (g[5] + rotl32(g[4], 8) + g[3]);
        pContext.x[6] = (g[6] + rotl32(g[5], 16) + rotl32(g[4], 16));
        pContext.x[7] = (g[7] + rotl32(g[6], 8) + g[5]);
    }

    static int rotl32(final int v, final int n) {
        return (((v) << (n)) | ((v) >>> (32 - (n))));
    }

    /* Key setup */
    void setKey(final byte[] key) {
        /* Check lengths */
        if (key == null || key.length != 16) {
            throw new IllegalArgumentException("A key of 16 bytes is needed");
        }

        /* Generate four subkeys */
        final int k0 = decode32le(key, 0);
        final int k1 = decode32le(key, 4);
        final int k2 = decode32le(key, 8);
        final int k3 = decode32le(key, 12);

        /* Generate initial state variables */
        master.x[0] = k0;
        master.x[2] = k1;
        master.x[4] = k2;
        master.x[6] = k3;
        master.x[1] = (k3 << 16) | (k2 >>> 16);
        master.x[3] = (k0 << 16) | (k3 >>> 16);
        master.x[5] = (k1 << 16) | (k0 >>> 16);
        master.x[7] = (k2 << 16) | (k1 >>> 16);

        /* Generate initial counter values */
        master.c[0] = rotl32(k2, 16);
        master.c[2] = rotl32(k3, 16);
        master.c[4] = rotl32(k0, 16);
        master.c[6] = rotl32(k1, 16);
        master.c[1] = (k0 & 0xFFFF0000) | (k1 & 0xFFFF);
        master.c[3] = (k1 & 0xFFFF0000) | (k2 & 0xFFFF);
        master.c[5] = (k2 & 0xFFFF0000) | (k3 & 0xFFFF);
        master.c[7] = (k3 & 0xFFFF0000) | (k0 & 0xFFFF);

        /* Clear carry bit */
        master.carry = 0;

        /* Iterate the system four times */
        for (int i = 0; i < 4; i++) {
            rabbitNextState(master);
        }

        /* Modify the counters */
        for (int i = 0; i < NUM_VARS; i++) {
            master.c[i] ^= master.x[(i + 4) & 0x7];
        }

        /* Copy master instance to work instance */
        for (int i = 0; i < NUM_VARS; i++) {
            work.x[i] = master.x[i];
            work.c[i] = master.c[i];
        }
        work.carry = master.carry;
    }

    /* ------------------------------------------------------------------------- */

    /* IV setup */
    void setIV(final byte[] iv) {
        /* Check lengths */
        if (iv == null || iv.length != 8) {
            throw new IllegalArgumentException("An IV of 8 bytes is needed");
        }

        /* Generate four subvectors */
        final int i0 = decode32le(iv, 0);
        final int i2 = decode32le(iv, 4);
        final int i1 = (i0 >>> 16) | (i2 & 0xFFFF0000);
        final int i3 = (i2 << 16) | (i0 & 0x0000FFFF);

        /* Modify counter values */
        work.c[0] = master.c[0] ^ i0;
        work.c[1] = master.c[1] ^ i1;
        work.c[2] = master.c[2] ^ i2;
        work.c[3] = master.c[3] ^ i3;
        work.c[4] = master.c[4] ^ i0;
        work.c[5] = master.c[5] ^ i1;
        work.c[6] = master.c[6] ^ i2;
        work.c[7] = master.c[7] ^ i3;

        /* Copy state variables */
        for (int i = 0; i < NUM_VARS; i++) {
            work.x[i] = master.x[i];
        }
        work.carry = master.carry;

        /* Iterate the system four times */
        for (int i = 0; i < 4; i++) {
            rabbitNextState(work);
        }
    }

    /* Generate keystream */
    void makeKeyStream() {
        /* Generate five blocks */
        for (int i = 0; i < STREAM_LEN; i += 16) {
            /* Iterate the system */
            rabbitNextState(work);

            /* Generate 16 bytes of pseudo-random data */
            encode32le(work.x[0] ^ (work.x[5] >>> 16) ^ (work.x[3] << 16), keyStream, i + 0);
            encode32le(work.x[2] ^ (work.x[7] >>> 16) ^ (work.x[5] << 16), keyStream, i + 4);
            encode32le(work.x[4] ^ (work.x[1] >>> 16) ^ (work.x[7] << 16), keyStream, i + 8);
            encode32le(work.x[6] ^ (work.x[3] >>> 16) ^ (work.x[1] << 16), keyStream, i + 12);
        }
    }

    @Override
    public GordianRabbitEngine copy() {
        return new GordianRabbitEngine(this);
    }

    @Override
    public void reset(final Memoable pState) {
        final GordianRabbitEngine e = (GordianRabbitEngine) pState;
        work.copyFrom(e.work);
        master.copyFrom(e.master);
        System.arraycopy(e.keyStream, 0, keyStream, 0, STREAM_LEN);
        theIndex = e.theIndex;
    }
}
