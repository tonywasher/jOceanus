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
 * StreamCipher implementation based on the SosemanukFast java implementation.
 * <p>
 * Copied from http://www.ecrypt.eu.org/stream/e2-sosemanuk.html.
 */
@SuppressWarnings("checkstyle:MagicNumber")
public class GordianSosemanukEngine
        implements StreamCipher, Memoable {
    /**
     * Advanced stream length.
     */
    private static final int STREAM_LEN = 80;

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
    private GordianSosemanukEngine theResetState;

    /**
     * Constructor.
     */
    public GordianSosemanukEngine() {
    }

    /**
     * Constructor.
     * @param pSource the source engine
     */
    private GordianSosemanukEngine(final GordianSosemanukEngine pSource) {
        reset(pSource);
    }

    /**
     * initialise a Susemanuk cipher.
     * @param forEncryption whether or not we are for encryption.
     * @param params the parameters required to set up the cipher.
     * @exception IllegalArgumentException if the params argument is inappropriate.
     */
    public void init(final boolean forEncryption,
                     final CipherParameters params) {
        /*
         * Sosemanuk encryption and decryption is completely symmetrical, so the 'forEncryption' is
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
        makeStreamBlock(keyStream, 0);

        /* Save reset state */
        theResetState = copy();
    }

    @Override
    public String getAlgorithmName() {
        return "Sosemanuk";
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
            makeStreamBlock(keyStream, 0);
        }
        return out;
    }


    @Override
    public GordianSosemanukEngine copy() {
        return new GordianSosemanukEngine(this);
    }

    @Override
    public void reset(final Memoable pState) {
        final GordianSosemanukEngine e = (GordianSosemanukEngine) pState;
        lfsr0 = e.lfsr0;
        lfsr1 = e.lfsr1;
        lfsr2 = e.lfsr2;
        lfsr3 = e.lfsr3;
        lfsr4 = e.lfsr4;
        lfsr5 = e.lfsr5;
        lfsr6 = e.lfsr6;
        lfsr7 = e.lfsr7;
        lfsr8 = e.lfsr8;
        lfsr9 = e.lfsr9;
        fsmR1 = e.fsmR1;
        fsmR2 = e.fsmR2;
        System.arraycopy(e.keyStream, 0, keyStream, 0, STREAM_LEN);
        theIndex = e.theIndex;
    }

    /**
     * LFSR0 State.
     */
    private int lfsr0;

    /**
     * LFSR1 State.
     */
    private int lfsr1;

    /**
     * LFSR2 State.
     */
    private int lfsr2;

    /**
     * LFSR3 State.
     */
    private int lfsr3;

    /**
     * LFSR4 State.
     */
    private int lfsr4;

    /**
     * LFSR5 State.
     */
    private int lfsr5;

    /**
     * LFSR6 State.
     */
    private int lfsr6;

    /**
     * LFSR7 State.
     */
    private int lfsr7;

    /**
     * LFSR8 State.
     */
    private int lfsr8;

    /**
     * LFSR9 State.
     */
    private int lfsr9;

    /**
     * FSMR1 State.
     */
    private int fsmR1;

    /**
     * FSMR2 State.
     */
    private int fsmR2;

    /*
     * The code internals for the SERPENT-derived functions have been
     * semi-automatically generated, using a mixture of C, C
     * preprocessor, vi macros and Forth. The base circuits for
     * the SERPENT S-boxes have been published by Dag Arne Osvik
     * ("Speeding up Serpent", at the 3rd AES Candidate Conference).
     */

    /**
     * Decode a 32-bit value from a buffer (little-endian).
     *
     * @param buf   the input buffer
     * @param off   the input offset
     * @return  the decoded value
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
     * @param val   the value to encode
     * @param buf   the output buffer
     * @param off   the output offset
     */
    private static void encode32le(final int val, final byte[] buf, final int off) {
        buf[off] = (byte) val;
        buf[off + 1] = (byte) (val >> 8);
        buf[off + 2] = (byte) (val >> 16);
        buf[off + 3] = (byte) (val >> 24);
    }

    /**
     * Left-rotate a 32-bit value by some bit.
     *
     * @param val   the value to rotate
     * @param n     the rotation count (between 1 and 31)
     * @return rotated value
     */
    private static int rotateLeft(final int val, final int n) {
        return (val << n) | (val >>> (32 - n));
    }

    /** Subkeys for Serpent24: 100 32-bit words. */
    private final int[] serpent24SubKeys = new int[100];

    /**
     * Set the private key. The key length must be between 1
     * and 32 bytes.
     *
     * @param key   the private key
     */
    @SuppressWarnings("checkstyle:MethodLength")
    public void setKey(final byte[] key) {
        if (key.length < 1 || key.length > 32) {
            throw new IllegalArgumentException("bad key length: " + key.length);
        }
        final byte[] lkey;
        if (key.length == 32) {
            lkey = key;
        } else {
            lkey = new byte[32];
            System.arraycopy(key, 0, lkey, 0, key.length);
            lkey[key.length] = 0x01;
            for (int i = key.length + 1; i < lkey.length; i++) {
                lkey[i] = 0x00;
            }
        }

        int i = 0;

        int w0 = decode32le(lkey, 0);
        int w1 = decode32le(lkey, 4);
        int w2 = decode32le(lkey, 8);
        int w3 = decode32le(lkey, 12);
        int w4 = decode32le(lkey, 16);
        int w5 = decode32le(lkey, 20);
        int w6 = decode32le(lkey, 24);
        int w7 = decode32le(lkey, 28);
        int tt = w0 ^ w3 ^ w5 ^ w7 ^ (0x9E3779B9 ^ (0));
        w0 = rotateLeft(tt, 11);
        tt = w1 ^ w4 ^ w6 ^ w0 ^ (0x9E3779B9 ^ (0 + 1));
        w1 = rotateLeft(tt, 11);
        tt = w2 ^ w5 ^ w7 ^ w1 ^ (0x9E3779B9 ^ (0 + 2));
        w2 = rotateLeft(tt, 11);
        tt = w3 ^ w6 ^ w0 ^ w2 ^ (0x9E3779B9 ^ (0 + 3));
        w3 = rotateLeft(tt, 11);
        int r0 = w0;
        int r1 = w1;
        int r2 = w2;
        int r3 = w3;
        int r4 = r0;
        r0 |= r3;
        r3 ^= r1;
        r1 &= r4;
        r4 ^= r2;
        r2 ^= r3;
        r3 &= r0;
        r4 |= r1;
        r3 ^= r4;
        r0 ^= r1;
        r4 &= r0;
        r1 ^= r3;
        r4 ^= r2;
        r1 |= r0;
        r1 ^= r2;
        r0 ^= r3;
        r2 = r1;
        r1 |= r3;
        r1 ^= r0;
        serpent24SubKeys[i++] = r1;
        serpent24SubKeys[i++] = r2;
        serpent24SubKeys[i++] = r3;
        serpent24SubKeys[i++] = r4;
        tt = w4 ^ w7 ^ w1 ^ w3 ^ (0x9E3779B9 ^ (4));
        w4 = rotateLeft(tt, 11);
        tt = w5 ^ w0 ^ w2 ^ w4 ^ (0x9E3779B9 ^ (4 + 1));
        w5 = rotateLeft(tt, 11);
        tt = w6 ^ w1 ^ w3 ^ w5 ^ (0x9E3779B9 ^ (4 + 2));
        w6 = rotateLeft(tt, 11);
        tt = w7 ^ w2 ^ w4 ^ w6 ^ (0x9E3779B9 ^ (4 + 3));
        w7 = rotateLeft(tt, 11);
        r0 = w4;
        r1 = w5;
        r2 = w6;
        r3 = w7;
        r4 = r0;
        r0 &= r2;
        r0 ^= r3;
        r2 ^= r1;
        r2 ^= r0;
        r3 |= r4;
        r3 ^= r1;
        r4 ^= r2;
        r1 = r3;
        r3 |= r4;
        r3 ^= r0;
        r0 &= r1;
        r4 ^= r0;
        r1 ^= r3;
        r1 ^= r4;
        r4 = ~r4;
        serpent24SubKeys[i++] = r2;
        serpent24SubKeys[i++] = r3;
        serpent24SubKeys[i++] = r1;
        serpent24SubKeys[i++] = r4;
        tt = w0 ^ w3 ^ w5 ^ w7 ^ (0x9E3779B9 ^ (8));
        w0 = rotateLeft(tt, 11);
        tt = w1 ^ w4 ^ w6 ^ w0 ^ (0x9E3779B9 ^ (8 + 1));
        w1 = rotateLeft(tt, 11);
        tt = w2 ^ w5 ^ w7 ^ w1 ^ (0x9E3779B9 ^ (8 + 2));
        w2 = rotateLeft(tt, 11);
        tt = w3 ^ w6 ^ w0 ^ w2 ^ (0x9E3779B9 ^ (8 + 3));
        w3 = rotateLeft(tt, 11);
        r0 = w0;
        r1 = w1;
        r2 = w2;
        r3 = w3;
        r0 = ~r0;
        r2 = ~r2;
        r4 = r0;
        r0 &= r1;
        r2 ^= r0;
        r0 |= r3;
        r3 ^= r2;
        r1 ^= r0;
        r0 ^= r4;
        r4 |= r1;
        r1 ^= r3;
        r2 |= r0;
        r2 &= r4;
        r0 ^= r1;
        r1 &= r2;
        r1 ^= r0;
        r0 &= r2;
        r0 ^= r4;
        serpent24SubKeys[i++] = r2;
        serpent24SubKeys[i++] = r0;
        serpent24SubKeys[i++] = r3;
        serpent24SubKeys[i++] = r1;
        tt = w4 ^ w7 ^ w1 ^ w3 ^ (0x9E3779B9 ^ (12));
        w4 = rotateLeft(tt, 11);
        tt = w5 ^ w0 ^ w2 ^ w4 ^ (0x9E3779B9 ^ (12 + 1));
        w5 = rotateLeft(tt, 11);
        tt = w6 ^ w1 ^ w3 ^ w5 ^ (0x9E3779B9 ^ (12 + 2));
        w6 = rotateLeft(tt, 11);
        tt = w7 ^ w2 ^ w4 ^ w6 ^ (0x9E3779B9 ^ (12 + 3));
        w7 = rotateLeft(tt, 11);
        r0 = w4;
        r1 = w5;
        r2 = w6;
        r3 = w7;
        r3 ^= r0;
        r4 = r1;
        r1 &= r3;
        r4 ^= r2;
        r1 ^= r0;
        r0 |= r3;
        r0 ^= r4;
        r4 ^= r3;
        r3 ^= r2;
        r2 |= r1;
        r2 ^= r4;
        r4 = ~r4;
        r4 |= r1;
        r1 ^= r3;
        r1 ^= r4;
        r3 |= r0;
        r1 ^= r3;
        r4 ^= r3;
        serpent24SubKeys[i++] = r1;
        serpent24SubKeys[i++] = r4;
        serpent24SubKeys[i++] = r2;
        serpent24SubKeys[i++] = r0;
        tt = w0 ^ w3 ^ w5 ^ w7 ^ (0x9E3779B9 ^ (16));
        w0 = rotateLeft(tt, 11);
        tt = w1 ^ w4 ^ w6 ^ w0 ^ (0x9E3779B9 ^ (16 + 1));
        w1 = rotateLeft(tt, 11);
        tt = w2 ^ w5 ^ w7 ^ w1 ^ (0x9E3779B9 ^ (16 + 2));
        w2 = rotateLeft(tt, 11);
        tt = w3 ^ w6 ^ w0 ^ w2 ^ (0x9E3779B9 ^ (16 + 3));
        w3 = rotateLeft(tt, 11);
        r0 = w0;
        r1 = w1;
        r2 = w2;
        r3 = w3;
        r4 = r1;
        r1 |= r2;
        r1 ^= r3;
        r4 ^= r2;
        r2 ^= r1;
        r3 |= r4;
        r3 &= r0;
        r4 ^= r2;
        r3 ^= r1;
        r1 |= r4;
        r1 ^= r0;
        r0 |= r4;
        r0 ^= r2;
        r1 ^= r4;
        r2 ^= r1;
        r1 &= r0;
        r1 ^= r4;
        r2 = ~r2;
        r2 |= r0;
        r4 ^= r2;
        serpent24SubKeys[i++] = r4;
        serpent24SubKeys[i++] = r3;
        serpent24SubKeys[i++] = r1;
        serpent24SubKeys[i++] = r0;
        tt = w4 ^ w7 ^ w1 ^ w3 ^ (0x9E3779B9 ^ (20));
        w4 = rotateLeft(tt, 11);
        tt = w5 ^ w0 ^ w2 ^ w4 ^ (0x9E3779B9 ^ (20 + 1));
        w5 = rotateLeft(tt, 11);
        tt = w6 ^ w1 ^ w3 ^ w5 ^ (0x9E3779B9 ^ (20 + 2));
        w6 = rotateLeft(tt, 11);
        tt = w7 ^ w2 ^ w4 ^ w6 ^ (0x9E3779B9 ^ (20 + 3));
        w7 = rotateLeft(tt, 11);
        r0 = w4;
        r1 = w5;
        r2 = w6;
        r3 = w7;
        r2 = ~r2;
        r4 = r3;
        r3 &= r0;
        r0 ^= r4;
        r3 ^= r2;
        r2 |= r4;
        r1 ^= r3;
        r2 ^= r0;
        r0 |= r1;
        r2 ^= r1;
        r4 ^= r0;
        r0 |= r3;
        r0 ^= r2;
        r4 ^= r3;
        r4 ^= r0;
        r3 = ~r3;
        r2 &= r4;
        r2 ^= r3;
        serpent24SubKeys[i++] = r0;
        serpent24SubKeys[i++] = r1;
        serpent24SubKeys[i++] = r4;
        serpent24SubKeys[i++] = r2;
        tt = w0 ^ w3 ^ w5 ^ w7 ^ (0x9E3779B9 ^ (24));
        w0 = rotateLeft(tt, 11);
        tt = w1 ^ w4 ^ w6 ^ w0 ^ (0x9E3779B9 ^ (24 + 1));
        w1 = rotateLeft(tt, 11);
        tt = w2 ^ w5 ^ w7 ^ w1 ^ (0x9E3779B9 ^ (24 + 2));
        w2 = rotateLeft(tt, 11);
        tt = w3 ^ w6 ^ w0 ^ w2 ^ (0x9E3779B9 ^ (24 + 3));
        w3 = rotateLeft(tt, 11);
        r0 = w0;
        r1 = w1;
        r2 = w2;
        r3 = w3;
        r0 ^= r1;
        r1 ^= r3;
        r3 = ~r3;
        r4 = r1;
        r1 &= r0;
        r2 ^= r3;
        r1 ^= r2;
        r2 |= r4;
        r4 ^= r3;
        r3 &= r1;
        r3 ^= r0;
        r4 ^= r1;
        r4 ^= r2;
        r2 ^= r0;
        r0 &= r3;
        r2 = ~r2;
        r0 ^= r4;
        r4 |= r3;
        r2 ^= r4;
        serpent24SubKeys[i++] = r1;
        serpent24SubKeys[i++] = r3;
        serpent24SubKeys[i++] = r0;
        serpent24SubKeys[i++] = r2;
        tt = w4 ^ w7 ^ w1 ^ w3 ^ (0x9E3779B9 ^ (28));
        w4 = rotateLeft(tt, 11);
        tt = w5 ^ w0 ^ w2 ^ w4 ^ (0x9E3779B9 ^ (28 + 1));
        w5 = rotateLeft(tt, 11);
        tt = w6 ^ w1 ^ w3 ^ w5 ^ (0x9E3779B9 ^ (28 + 2));
        w6 = rotateLeft(tt, 11);
        tt = w7 ^ w2 ^ w4 ^ w6 ^ (0x9E3779B9 ^ (28 + 3));
        w7 = rotateLeft(tt, 11);
        r0 = w4;
        r1 = w5;
        r2 = w6;
        r3 = w7;
        r1 ^= r3;
        r3 = ~r3;
        r2 ^= r3;
        r3 ^= r0;
        r4 = r1;
        r1 &= r3;
        r1 ^= r2;
        r4 ^= r3;
        r0 ^= r4;
        r2 &= r4;
        r2 ^= r0;
        r0 &= r1;
        r3 ^= r0;
        r4 |= r1;
        r4 ^= r0;
        r0 |= r3;
        r0 ^= r2;
        r2 &= r3;
        r0 = ~r0;
        r4 ^= r2;
        serpent24SubKeys[i++] = r1;
        serpent24SubKeys[i++] = r4;
        serpent24SubKeys[i++] = r0;
        serpent24SubKeys[i++] = r3;
        tt = w0 ^ w3 ^ w5 ^ w7 ^ (0x9E3779B9 ^ (32));
        w0 = rotateLeft(tt, 11);
        tt = w1 ^ w4 ^ w6 ^ w0 ^ (0x9E3779B9 ^ (32 + 1));
        w1 = rotateLeft(tt, 11);
        tt = w2 ^ w5 ^ w7 ^ w1 ^ (0x9E3779B9 ^ (32 + 2));
        w2 = rotateLeft(tt, 11);
        tt = w3 ^ w6 ^ w0 ^ w2 ^ (0x9E3779B9 ^ (32 + 3));
        w3 = rotateLeft(tt, 11);
        r0 = w0;
        r1 = w1;
        r2 = w2;
        r3 = w3;
        r4 = r0;
        r0 |= r3;
        r3 ^= r1;
        r1 &= r4;
        r4 ^= r2;
        r2 ^= r3;
        r3 &= r0;
        r4 |= r1;
        r3 ^= r4;
        r0 ^= r1;
        r4 &= r0;
        r1 ^= r3;
        r4 ^= r2;
        r1 |= r0;
        r1 ^= r2;
        r0 ^= r3;
        r2 = r1;
        r1 |= r3;
        r1 ^= r0;
        serpent24SubKeys[i++] = r1;
        serpent24SubKeys[i++] = r2;
        serpent24SubKeys[i++] = r3;
        serpent24SubKeys[i++] = r4;
        tt = w4 ^ w7 ^ w1 ^ w3 ^ (0x9E3779B9 ^ (36));
        w4 = rotateLeft(tt, 11);
        tt = w5 ^ w0 ^ w2 ^ w4 ^ (0x9E3779B9 ^ (36 + 1));
        w5 = rotateLeft(tt, 11);
        tt = w6 ^ w1 ^ w3 ^ w5 ^ (0x9E3779B9 ^ (36 + 2));
        w6 = rotateLeft(tt, 11);
        tt = w7 ^ w2 ^ w4 ^ w6 ^ (0x9E3779B9 ^ (36 + 3));
        w7 = rotateLeft(tt, 11);
        r0 = w4;
        r1 = w5;
        r2 = w6;
        r3 = w7;
        r4 = r0;
        r0 &= r2;
        r0 ^= r3;
        r2 ^= r1;
        r2 ^= r0;
        r3 |= r4;
        r3 ^= r1;
        r4 ^= r2;
        r1 = r3;
        r3 |= r4;
        r3 ^= r0;
        r0 &= r1;
        r4 ^= r0;
        r1 ^= r3;
        r1 ^= r4;
        r4 = ~r4;
        serpent24SubKeys[i++] = r2;
        serpent24SubKeys[i++] = r3;
        serpent24SubKeys[i++] = r1;
        serpent24SubKeys[i++] = r4;
        tt = w0 ^ w3 ^ w5 ^ w7 ^ (0x9E3779B9 ^ (40));
        w0 = rotateLeft(tt, 11);
        tt = w1 ^ w4 ^ w6 ^ w0 ^ (0x9E3779B9 ^ (40 + 1));
        w1 = rotateLeft(tt, 11);
        tt = w2 ^ w5 ^ w7 ^ w1 ^ (0x9E3779B9 ^ (40 + 2));
        w2 = rotateLeft(tt, 11);
        tt = w3 ^ w6 ^ w0 ^ w2 ^ (0x9E3779B9 ^ (40 + 3));
        w3 = rotateLeft(tt, 11);
        r0 = w0;
        r1 = w1;
        r2 = w2;
        r3 = w3;
        r0 = ~r0;
        r2 = ~r2;
        r4 = r0;
        r0 &= r1;
        r2 ^= r0;
        r0 |= r3;
        r3 ^= r2;
        r1 ^= r0;
        r0 ^= r4;
        r4 |= r1;
        r1 ^= r3;
        r2 |= r0;
        r2 &= r4;
        r0 ^= r1;
        r1 &= r2;
        r1 ^= r0;
        r0 &= r2;
        r0 ^= r4;
        serpent24SubKeys[i++] = r2;
        serpent24SubKeys[i++] = r0;
        serpent24SubKeys[i++] = r3;
        serpent24SubKeys[i++] = r1;
        tt = w4 ^ w7 ^ w1 ^ w3 ^ (0x9E3779B9 ^ (44));
        w4 = rotateLeft(tt, 11);
        tt = w5 ^ w0 ^ w2 ^ w4 ^ (0x9E3779B9 ^ (44 + 1));
        w5 = rotateLeft(tt, 11);
        tt = w6 ^ w1 ^ w3 ^ w5 ^ (0x9E3779B9 ^ (44 + 2));
        w6 = rotateLeft(tt, 11);
        tt = w7 ^ w2 ^ w4 ^ w6 ^ (0x9E3779B9 ^ (44 + 3));
        w7 = rotateLeft(tt, 11);
        r0 = w4;
        r1 = w5;
        r2 = w6;
        r3 = w7;
        r3 ^= r0;
        r4 = r1;
        r1 &= r3;
        r4 ^= r2;
        r1 ^= r0;
        r0 |= r3;
        r0 ^= r4;
        r4 ^= r3;
        r3 ^= r2;
        r2 |= r1;
        r2 ^= r4;
        r4 = ~r4;
        r4 |= r1;
        r1 ^= r3;
        r1 ^= r4;
        r3 |= r0;
        r1 ^= r3;
        r4 ^= r3;
        serpent24SubKeys[i++] = r1;
        serpent24SubKeys[i++] = r4;
        serpent24SubKeys[i++] = r2;
        serpent24SubKeys[i++] = r0;
        tt = w0 ^ w3 ^ w5 ^ w7 ^ (0x9E3779B9 ^ (48));
        w0 = rotateLeft(tt, 11);
        tt = w1 ^ w4 ^ w6 ^ w0 ^ (0x9E3779B9 ^ (48 + 1));
        w1 = rotateLeft(tt, 11);
        tt = w2 ^ w5 ^ w7 ^ w1 ^ (0x9E3779B9 ^ (48 + 2));
        w2 = rotateLeft(tt, 11);
        tt = w3 ^ w6 ^ w0 ^ w2 ^ (0x9E3779B9 ^ (48 + 3));
        w3 = rotateLeft(tt, 11);
        r0 = w0;
        r1 = w1;
        r2 = w2;
        r3 = w3;
        r4 = r1;
        r1 |= r2;
        r1 ^= r3;
        r4 ^= r2;
        r2 ^= r1;
        r3 |= r4;
        r3 &= r0;
        r4 ^= r2;
        r3 ^= r1;
        r1 |= r4;
        r1 ^= r0;
        r0 |= r4;
        r0 ^= r2;
        r1 ^= r4;
        r2 ^= r1;
        r1 &= r0;
        r1 ^= r4;
        r2 = ~r2;
        r2 |= r0;
        r4 ^= r2;
        serpent24SubKeys[i++] = r4;
        serpent24SubKeys[i++] = r3;
        serpent24SubKeys[i++] = r1;
        serpent24SubKeys[i++] = r0;
        tt = w4 ^ w7 ^ w1 ^ w3 ^ (0x9E3779B9 ^ (52));
        w4 = rotateLeft(tt, 11);
        tt = w5 ^ w0 ^ w2 ^ w4 ^ (0x9E3779B9 ^ (52 + 1));
        w5 = rotateLeft(tt, 11);
        tt = w6 ^ w1 ^ w3 ^ w5 ^ (0x9E3779B9 ^ (52 + 2));
        w6 = rotateLeft(tt, 11);
        tt = w7 ^ w2 ^ w4 ^ w6 ^ (0x9E3779B9 ^ (52 + 3));
        w7 = rotateLeft(tt, 11);
        r0 = w4;
        r1 = w5;
        r2 = w6;
        r3 = w7;
        r2 = ~r2;
        r4 = r3;
        r3 &= r0;
        r0 ^= r4;
        r3 ^= r2;
        r2 |= r4;
        r1 ^= r3;
        r2 ^= r0;
        r0 |= r1;
        r2 ^= r1;
        r4 ^= r0;
        r0 |= r3;
        r0 ^= r2;
        r4 ^= r3;
        r4 ^= r0;
        r3 = ~r3;
        r2 &= r4;
        r2 ^= r3;
        serpent24SubKeys[i++] = r0;
        serpent24SubKeys[i++] = r1;
        serpent24SubKeys[i++] = r4;
        serpent24SubKeys[i++] = r2;
        tt = w0 ^ w3 ^ w5 ^ w7 ^ (0x9E3779B9 ^ (56));
        w0 = rotateLeft(tt, 11);
        tt = w1 ^ w4 ^ w6 ^ w0 ^ (0x9E3779B9 ^ (56 + 1));
        w1 = rotateLeft(tt, 11);
        tt = w2 ^ w5 ^ w7 ^ w1 ^ (0x9E3779B9 ^ (56 + 2));
        w2 = rotateLeft(tt, 11);
        tt = w3 ^ w6 ^ w0 ^ w2 ^ (0x9E3779B9 ^ (56 + 3));
        w3 = rotateLeft(tt, 11);
        r0 = w0;
        r1 = w1;
        r2 = w2;
        r3 = w3;
        r0 ^= r1;
        r1 ^= r3;
        r3 = ~r3;
        r4 = r1;
        r1 &= r0;
        r2 ^= r3;
        r1 ^= r2;
        r2 |= r4;
        r4 ^= r3;
        r3 &= r1;
        r3 ^= r0;
        r4 ^= r1;
        r4 ^= r2;
        r2 ^= r0;
        r0 &= r3;
        r2 = ~r2;
        r0 ^= r4;
        r4 |= r3;
        r2 ^= r4;
        serpent24SubKeys[i++] = r1;
        serpent24SubKeys[i++] = r3;
        serpent24SubKeys[i++] = r0;
        serpent24SubKeys[i++] = r2;
        tt = w4 ^ w7 ^ w1 ^ w3 ^ (0x9E3779B9 ^ (60));
        w4 = rotateLeft(tt, 11);
        tt = w5 ^ w0 ^ w2 ^ w4 ^ (0x9E3779B9 ^ (60 + 1));
        w5 = rotateLeft(tt, 11);
        tt = w6 ^ w1 ^ w3 ^ w5 ^ (0x9E3779B9 ^ (60 + 2));
        w6 = rotateLeft(tt, 11);
        tt = w7 ^ w2 ^ w4 ^ w6 ^ (0x9E3779B9 ^ (60 + 3));
        w7 = rotateLeft(tt, 11);
        r0 = w4;
        r1 = w5;
        r2 = w6;
        r3 = w7;
        r1 ^= r3;
        r3 = ~r3;
        r2 ^= r3;
        r3 ^= r0;
        r4 = r1;
        r1 &= r3;
        r1 ^= r2;
        r4 ^= r3;
        r0 ^= r4;
        r2 &= r4;
        r2 ^= r0;
        r0 &= r1;
        r3 ^= r0;
        r4 |= r1;
        r4 ^= r0;
        r0 |= r3;
        r0 ^= r2;
        r2 &= r3;
        r0 = ~r0;
        r4 ^= r2;
        serpent24SubKeys[i++] = r1;
        serpent24SubKeys[i++] = r4;
        serpent24SubKeys[i++] = r0;
        serpent24SubKeys[i++] = r3;
        tt = w0 ^ w3 ^ w5 ^ w7 ^ (0x9E3779B9 ^ (64));
        w0 = rotateLeft(tt, 11);
        tt = w1 ^ w4 ^ w6 ^ w0 ^ (0x9E3779B9 ^ (64 + 1));
        w1 = rotateLeft(tt, 11);
        tt = w2 ^ w5 ^ w7 ^ w1 ^ (0x9E3779B9 ^ (64 + 2));
        w2 = rotateLeft(tt, 11);
        tt = w3 ^ w6 ^ w0 ^ w2 ^ (0x9E3779B9 ^ (64 + 3));
        w3 = rotateLeft(tt, 11);
        r0 = w0;
        r1 = w1;
        r2 = w2;
        r3 = w3;
        r4 = r0;
        r0 |= r3;
        r3 ^= r1;
        r1 &= r4;
        r4 ^= r2;
        r2 ^= r3;
        r3 &= r0;
        r4 |= r1;
        r3 ^= r4;
        r0 ^= r1;
        r4 &= r0;
        r1 ^= r3;
        r4 ^= r2;
        r1 |= r0;
        r1 ^= r2;
        r0 ^= r3;
        r2 = r1;
        r1 |= r3;
        r1 ^= r0;
        serpent24SubKeys[i++] = r1;
        serpent24SubKeys[i++] = r2;
        serpent24SubKeys[i++] = r3;
        serpent24SubKeys[i++] = r4;
        tt = w4 ^ w7 ^ w1 ^ w3 ^ (0x9E3779B9 ^ (68));
        w4 = rotateLeft(tt, 11);
        tt = w5 ^ w0 ^ w2 ^ w4 ^ (0x9E3779B9 ^ (68 + 1));
        w5 = rotateLeft(tt, 11);
        tt = w6 ^ w1 ^ w3 ^ w5 ^ (0x9E3779B9 ^ (68 + 2));
        w6 = rotateLeft(tt, 11);
        tt = w7 ^ w2 ^ w4 ^ w6 ^ (0x9E3779B9 ^ (68 + 3));
        w7 = rotateLeft(tt, 11);
        r0 = w4;
        r1 = w5;
        r2 = w6;
        r3 = w7;
        r4 = r0;
        r0 &= r2;
        r0 ^= r3;
        r2 ^= r1;
        r2 ^= r0;
        r3 |= r4;
        r3 ^= r1;
        r4 ^= r2;
        r1 = r3;
        r3 |= r4;
        r3 ^= r0;
        r0 &= r1;
        r4 ^= r0;
        r1 ^= r3;
        r1 ^= r4;
        r4 = ~r4;
        serpent24SubKeys[i++] = r2;
        serpent24SubKeys[i++] = r3;
        serpent24SubKeys[i++] = r1;
        serpent24SubKeys[i++] = r4;
        tt = w0 ^ w3 ^ w5 ^ w7 ^ (0x9E3779B9 ^ (72));
        w0 = rotateLeft(tt, 11);
        tt = w1 ^ w4 ^ w6 ^ w0 ^ (0x9E3779B9 ^ (72 + 1));
        w1 = rotateLeft(tt, 11);
        tt = w2 ^ w5 ^ w7 ^ w1 ^ (0x9E3779B9 ^ (72 + 2));
        w2 = rotateLeft(tt, 11);
        tt = w3 ^ w6 ^ w0 ^ w2 ^ (0x9E3779B9 ^ (72 + 3));
        w3 = rotateLeft(tt, 11);
        r0 = w0;
        r1 = w1;
        r2 = w2;
        r3 = w3;
        r0 = ~r0;
        r2 = ~r2;
        r4 = r0;
        r0 &= r1;
        r2 ^= r0;
        r0 |= r3;
        r3 ^= r2;
        r1 ^= r0;
        r0 ^= r4;
        r4 |= r1;
        r1 ^= r3;
        r2 |= r0;
        r2 &= r4;
        r0 ^= r1;
        r1 &= r2;
        r1 ^= r0;
        r0 &= r2;
        r0 ^= r4;
        serpent24SubKeys[i++] = r2;
        serpent24SubKeys[i++] = r0;
        serpent24SubKeys[i++] = r3;
        serpent24SubKeys[i++] = r1;
        tt = w4 ^ w7 ^ w1 ^ w3 ^ (0x9E3779B9 ^ (76));
        w4 = rotateLeft(tt, 11);
        tt = w5 ^ w0 ^ w2 ^ w4 ^ (0x9E3779B9 ^ (76 + 1));
        w5 = rotateLeft(tt, 11);
        tt = w6 ^ w1 ^ w3 ^ w5 ^ (0x9E3779B9 ^ (76 + 2));
        w6 = rotateLeft(tt, 11);
        tt = w7 ^ w2 ^ w4 ^ w6 ^ (0x9E3779B9 ^ (76 + 3));
        w7 = rotateLeft(tt, 11);
        r0 = w4;
        r1 = w5;
        r2 = w6;
        r3 = w7;
        r3 ^= r0;
        r4 = r1;
        r1 &= r3;
        r4 ^= r2;
        r1 ^= r0;
        r0 |= r3;
        r0 ^= r4;
        r4 ^= r3;
        r3 ^= r2;
        r2 |= r1;
        r2 ^= r4;
        r4 = ~r4;
        r4 |= r1;
        r1 ^= r3;
        r1 ^= r4;
        r3 |= r0;
        r1 ^= r3;
        r4 ^= r3;
        serpent24SubKeys[i++] = r1;
        serpent24SubKeys[i++] = r4;
        serpent24SubKeys[i++] = r2;
        serpent24SubKeys[i++] = r0;
        tt = w0 ^ w3 ^ w5 ^ w7 ^ (0x9E3779B9 ^ (80));
        w0 = rotateLeft(tt, 11);
        tt = w1 ^ w4 ^ w6 ^ w0 ^ (0x9E3779B9 ^ (80 + 1));
        w1 = rotateLeft(tt, 11);
        tt = w2 ^ w5 ^ w7 ^ w1 ^ (0x9E3779B9 ^ (80 + 2));
        w2 = rotateLeft(tt, 11);
        tt = w3 ^ w6 ^ w0 ^ w2 ^ (0x9E3779B9 ^ (80 + 3));
        w3 = rotateLeft(tt, 11);
        r0 = w0;
        r1 = w1;
        r2 = w2;
        r3 = w3;
        r4 = r1;
        r1 |= r2;
        r1 ^= r3;
        r4 ^= r2;
        r2 ^= r1;
        r3 |= r4;
        r3 &= r0;
        r4 ^= r2;
        r3 ^= r1;
        r1 |= r4;
        r1 ^= r0;
        r0 |= r4;
        r0 ^= r2;
        r1 ^= r4;
        r2 ^= r1;
        r1 &= r0;
        r1 ^= r4;
        r2 = ~r2;
        r2 |= r0;
        r4 ^= r2;
        serpent24SubKeys[i++] = r4;
        serpent24SubKeys[i++] = r3;
        serpent24SubKeys[i++] = r1;
        serpent24SubKeys[i++] = r0;
        tt = w4 ^ w7 ^ w1 ^ w3 ^ (0x9E3779B9 ^ (84));
        w4 = rotateLeft(tt, 11);
        tt = w5 ^ w0 ^ w2 ^ w4 ^ (0x9E3779B9 ^ (84 + 1));
        w5 = rotateLeft(tt, 11);
        tt = w6 ^ w1 ^ w3 ^ w5 ^ (0x9E3779B9 ^ (84 + 2));
        w6 = rotateLeft(tt, 11);
        tt = w7 ^ w2 ^ w4 ^ w6 ^ (0x9E3779B9 ^ (84 + 3));
        w7 = rotateLeft(tt, 11);
        r0 = w4;
        r1 = w5;
        r2 = w6;
        r3 = w7;
        r2 = ~r2;
        r4 = r3;
        r3 &= r0;
        r0 ^= r4;
        r3 ^= r2;
        r2 |= r4;
        r1 ^= r3;
        r2 ^= r0;
        r0 |= r1;
        r2 ^= r1;
        r4 ^= r0;
        r0 |= r3;
        r0 ^= r2;
        r4 ^= r3;
        r4 ^= r0;
        r3 = ~r3;
        r2 &= r4;
        r2 ^= r3;
        serpent24SubKeys[i++] = r0;
        serpent24SubKeys[i++] = r1;
        serpent24SubKeys[i++] = r4;
        serpent24SubKeys[i++] = r2;
        tt = w0 ^ w3 ^ w5 ^ w7 ^ (0x9E3779B9 ^ (88));
        w0 = rotateLeft(tt, 11);
        tt = w1 ^ w4 ^ w6 ^ w0 ^ (0x9E3779B9 ^ (88 + 1));
        w1 = rotateLeft(tt, 11);
        tt = w2 ^ w5 ^ w7 ^ w1 ^ (0x9E3779B9 ^ (88 + 2));
        w2 = rotateLeft(tt, 11);
        tt = w3 ^ w6 ^ w0 ^ w2 ^ (0x9E3779B9 ^ (88 + 3));
        w3 = rotateLeft(tt, 11);
        r0 = w0;
        r1 = w1;
        r2 = w2;
        r3 = w3;
        r0 ^= r1;
        r1 ^= r3;
        r3 = ~r3;
        r4 = r1;
        r1 &= r0;
        r2 ^= r3;
        r1 ^= r2;
        r2 |= r4;
        r4 ^= r3;
        r3 &= r1;
        r3 ^= r0;
        r4 ^= r1;
        r4 ^= r2;
        r2 ^= r0;
        r0 &= r3;
        r2 = ~r2;
        r0 ^= r4;
        r4 |= r3;
        r2 ^= r4;
        serpent24SubKeys[i++] = r1;
        serpent24SubKeys[i++] = r3;
        serpent24SubKeys[i++] = r0;
        serpent24SubKeys[i++] = r2;
        tt = w4 ^ w7 ^ w1 ^ w3 ^ (0x9E3779B9 ^ (92));
        w4 = rotateLeft(tt, 11);
        tt = w5 ^ w0 ^ w2 ^ w4 ^ (0x9E3779B9 ^ (92 + 1));
        w5 = rotateLeft(tt, 11);
        tt = w6 ^ w1 ^ w3 ^ w5 ^ (0x9E3779B9 ^ (92 + 2));
        w6 = rotateLeft(tt, 11);
        tt = w7 ^ w2 ^ w4 ^ w6 ^ (0x9E3779B9 ^ (92 + 3));
        w7 = rotateLeft(tt, 11);
        r0 = w4;
        r1 = w5;
        r2 = w6;
        r3 = w7;
        r1 ^= r3;
        r3 = ~r3;
        r2 ^= r3;
        r3 ^= r0;
        r4 = r1;
        r1 &= r3;
        r1 ^= r2;
        r4 ^= r3;
        r0 ^= r4;
        r2 &= r4;
        r2 ^= r0;
        r0 &= r1;
        r3 ^= r0;
        r4 |= r1;
        r4 ^= r0;
        r0 |= r3;
        r0 ^= r2;
        r2 &= r3;
        r0 = ~r0;
        r4 ^= r2;
        serpent24SubKeys[i++] = r1;
        serpent24SubKeys[i++] = r4;
        serpent24SubKeys[i++] = r0;
        serpent24SubKeys[i++] = r3;
        tt = w0 ^ w3 ^ w5 ^ w7 ^ (0x9E3779B9 ^ (96));
        w0 = rotateLeft(tt, 11);
        tt = w1 ^ w4 ^ w6 ^ w0 ^ (0x9E3779B9 ^ (96 + 1));
        w1 = rotateLeft(tt, 11);
        tt = w2 ^ w5 ^ w7 ^ w1 ^ (0x9E3779B9 ^ (96 + 2));
        w2 = rotateLeft(tt, 11);
        tt = w3 ^ w6 ^ w0 ^ w2 ^ (0x9E3779B9 ^ (96 + 3));
        w3 = rotateLeft(tt, 11);
        r0 = w0;
        r1 = w1;
        r2 = w2;
        r3 = w3;
        r4 = r0;
        r0 |= r3;
        r3 ^= r1;
        r1 &= r4;
        r4 ^= r2;
        r2 ^= r3;
        r3 &= r0;
        r4 |= r1;
        r3 ^= r4;
        r0 ^= r1;
        r4 &= r0;
        r1 ^= r3;
        r4 ^= r2;
        r1 |= r0;
        r1 ^= r2;
        r0 ^= r3;
        r2 = r1;
        r1 |= r3;
        r1 ^= r0;
        serpent24SubKeys[i++] = r1;
        serpent24SubKeys[i++] = r2;
        serpent24SubKeys[i++] = r3;
        serpent24SubKeys[i++] = r4;
    }

    /**
     * Set the IV. The IV length must lie between 0 and 16 (inclusive).
     * <code>null</code> is accepted, and yields the same result
     * than an IV of length 0.
     *
     * @param iv   the IV (or <code>null</code>)
     */
    @SuppressWarnings("checkstyle:MethodLength")
    public void setIV(final byte[] iv) {
        byte[] myIV = iv;
        if (myIV == null) {
            myIV = new byte[0];
        }
        if (myIV.length > 16) {
            throw new IllegalArgumentException("bad IV length: " + myIV.length);
        }
        final byte[] piv;
        if (myIV.length == 16) {
            piv = myIV;
        } else {
            piv = new byte[16];
            System.arraycopy(myIV, 0, piv, 0, myIV.length);
            for (int i = myIV.length; i < piv.length; i++) {
                piv[i] = 0x00;
            }
        }

        int r0 = decode32le(piv, 0);
        int r1 = decode32le(piv, 4);
        int r2 = decode32le(piv, 8);
        int r3 = decode32le(piv, 12);
        int r4;

        r0 ^= serpent24SubKeys[0];
        r1 ^= serpent24SubKeys[0 + 1];
        r2 ^= serpent24SubKeys[0 + 2];
        r3 ^= serpent24SubKeys[0 + 3];
        r3 ^= r0;
        r4 = r1;
        r1 &= r3;
        r4 ^= r2;
        r1 ^= r0;
        r0 |= r3;
        r0 ^= r4;
        r4 ^= r3;
        r3 ^= r2;
        r2 |= r1;
        r2 ^= r4;
        r4 = ~r4;
        r4 |= r1;
        r1 ^= r3;
        r1 ^= r4;
        r3 |= r0;
        r1 ^= r3;
        r4 ^= r3;
        r1 = rotateLeft(r1, 13);
        r2 = rotateLeft(r2, 3);
        r4 = r4 ^ r1 ^ r2;
        r0 = r0 ^ r2 ^ (r1 << 3);
        r4 = rotateLeft(r4, 1);
        r0 = rotateLeft(r0, 7);
        r1 = r1 ^ r4 ^ r0;
        r2 = r2 ^ r0 ^ (r4 << 7);
        r1 = rotateLeft(r1, 5);
        r2 = rotateLeft(r2, 22);
        r1 ^= serpent24SubKeys[4];
        r4 ^= serpent24SubKeys[4 + 1];
        r2 ^= serpent24SubKeys[4 + 2];
        r0 ^= serpent24SubKeys[4 + 3];
        r1 = ~r1;
        r2 = ~r2;
        r3 = r1;
        r1 &= r4;
        r2 ^= r1;
        r1 |= r0;
        r0 ^= r2;
        r4 ^= r1;
        r1 ^= r3;
        r3 |= r4;
        r4 ^= r0;
        r2 |= r1;
        r2 &= r3;
        r1 ^= r4;
        r4 &= r2;
        r4 ^= r1;
        r1 &= r2;
        r1 ^= r3;
        r2 = rotateLeft(r2, 13);
        r0 = rotateLeft(r0, 3);
        r1 = r1 ^ r2 ^ r0;
        r4 = r4 ^ r0 ^ (r2 << 3);
        r1 = rotateLeft(r1, 1);
        r4 = rotateLeft(r4, 7);
        r2 = r2 ^ r1 ^ r4;
        r0 = r0 ^ r4 ^ (r1 << 7);
        r2 = rotateLeft(r2, 5);
        r0 = rotateLeft(r0, 22);
        r2 ^= serpent24SubKeys[8];
        r1 ^= serpent24SubKeys[8 + 1];
        r0 ^= serpent24SubKeys[8 + 2];
        r4 ^= serpent24SubKeys[8 + 3];
        r3 = r2;
        r2 &= r0;
        r2 ^= r4;
        r0 ^= r1;
        r0 ^= r2;
        r4 |= r3;
        r4 ^= r1;
        r3 ^= r0;
        r1 = r4;
        r4 |= r3;
        r4 ^= r2;
        r2 &= r1;
        r3 ^= r2;
        r1 ^= r4;
        r1 ^= r3;
        r3 = ~r3;
        r0 = rotateLeft(r0, 13);
        r1 = rotateLeft(r1, 3);
        r4 = r4 ^ r0 ^ r1;
        r3 = r3 ^ r1 ^ (r0 << 3);
        r4 = rotateLeft(r4, 1);
        r3 = rotateLeft(r3, 7);
        r0 = r0 ^ r4 ^ r3;
        r1 = r1 ^ r3 ^ (r4 << 7);
        r0 = rotateLeft(r0, 5);
        r1 = rotateLeft(r1, 22);
        r0 ^= serpent24SubKeys[12];
        r4 ^= serpent24SubKeys[12 + 1];
        r1 ^= serpent24SubKeys[12 + 2];
        r3 ^= serpent24SubKeys[12 + 3];
        r2 = r0;
        r0 |= r3;
        r3 ^= r4;
        r4 &= r2;
        r2 ^= r1;
        r1 ^= r3;
        r3 &= r0;
        r2 |= r4;
        r3 ^= r2;
        r0 ^= r4;
        r2 &= r0;
        r4 ^= r3;
        r2 ^= r1;
        r4 |= r0;
        r4 ^= r1;
        r0 ^= r3;
        r1 = r4;
        r4 |= r3;
        r4 ^= r0;
        r4 = rotateLeft(r4, 13);
        r3 = rotateLeft(r3, 3);
        r1 = r1 ^ r4 ^ r3;
        r2 = r2 ^ r3 ^ (r4 << 3);
        r1 = rotateLeft(r1, 1);
        r2 = rotateLeft(r2, 7);
        r4 = r4 ^ r1 ^ r2;
        r3 = r3 ^ r2 ^ (r1 << 7);
        r4 = rotateLeft(r4, 5);
        r3 = rotateLeft(r3, 22);
        r4 ^= serpent24SubKeys[16];
        r1 ^= serpent24SubKeys[16 + 1];
        r3 ^= serpent24SubKeys[16 + 2];
        r2 ^= serpent24SubKeys[16 + 3];
        r1 ^= r2;
        r2 = ~r2;
        r3 ^= r2;
        r2 ^= r4;
        r0 = r1;
        r1 &= r2;
        r1 ^= r3;
        r0 ^= r2;
        r4 ^= r0;
        r3 &= r0;
        r3 ^= r4;
        r4 &= r1;
        r2 ^= r4;
        r0 |= r1;
        r0 ^= r4;
        r4 |= r2;
        r4 ^= r3;
        r3 &= r2;
        r4 = ~r4;
        r0 ^= r3;
        r1 = rotateLeft(r1, 13);
        r4 = rotateLeft(r4, 3);
        r0 = r0 ^ r1 ^ r4;
        r2 = r2 ^ r4 ^ (r1 << 3);
        r0 = rotateLeft(r0, 1);
        r2 = rotateLeft(r2, 7);
        r1 = r1 ^ r0 ^ r2;
        r4 = r4 ^ r2 ^ (r0 << 7);
        r1 = rotateLeft(r1, 5);
        r4 = rotateLeft(r4, 22);
        r1 ^= serpent24SubKeys[20];
        r0 ^= serpent24SubKeys[20 + 1];
        r4 ^= serpent24SubKeys[20 + 2];
        r2 ^= serpent24SubKeys[20 + 3];
        r1 ^= r0;
        r0 ^= r2;
        r2 = ~r2;
        r3 = r0;
        r0 &= r1;
        r4 ^= r2;
        r0 ^= r4;
        r4 |= r3;
        r3 ^= r2;
        r2 &= r0;
        r2 ^= r1;
        r3 ^= r0;
        r3 ^= r4;
        r4 ^= r1;
        r1 &= r2;
        r4 = ~r4;
        r1 ^= r3;
        r3 |= r2;
        r4 ^= r3;
        r0 = rotateLeft(r0, 13);
        r1 = rotateLeft(r1, 3);
        r2 = r2 ^ r0 ^ r1;
        r4 = r4 ^ r1 ^ (r0 << 3);
        r2 = rotateLeft(r2, 1);
        r4 = rotateLeft(r4, 7);
        r0 = r0 ^ r2 ^ r4;
        r1 = r1 ^ r4 ^ (r2 << 7);
        r0 = rotateLeft(r0, 5);
        r1 = rotateLeft(r1, 22);
        r0 ^= serpent24SubKeys[24];
        r2 ^= serpent24SubKeys[24 + 1];
        r1 ^= serpent24SubKeys[24 + 2];
        r4 ^= serpent24SubKeys[24 + 3];
        r1 = ~r1;
        r3 = r4;
        r4 &= r0;
        r0 ^= r3;
        r4 ^= r1;
        r1 |= r3;
        r2 ^= r4;
        r1 ^= r0;
        r0 |= r2;
        r1 ^= r2;
        r3 ^= r0;
        r0 |= r4;
        r0 ^= r1;
        r3 ^= r4;
        r3 ^= r0;
        r4 = ~r4;
        r1 &= r3;
        r1 ^= r4;
        r0 = rotateLeft(r0, 13);
        r3 = rotateLeft(r3, 3);
        r2 = r2 ^ r0 ^ r3;
        r1 = r1 ^ r3 ^ (r0 << 3);
        r2 = rotateLeft(r2, 1);
        r1 = rotateLeft(r1, 7);
        r0 = r0 ^ r2 ^ r1;
        r3 = r3 ^ r1 ^ (r2 << 7);
        r0 = rotateLeft(r0, 5);
        r3 = rotateLeft(r3, 22);
        r0 ^= serpent24SubKeys[28];
        r2 ^= serpent24SubKeys[28 + 1];
        r3 ^= serpent24SubKeys[28 + 2];
        r1 ^= serpent24SubKeys[28 + 3];
        r4 = r2;
        r2 |= r3;
        r2 ^= r1;
        r4 ^= r3;
        r3 ^= r2;
        r1 |= r4;
        r1 &= r0;
        r4 ^= r3;
        r1 ^= r2;
        r2 |= r4;
        r2 ^= r0;
        r0 |= r4;
        r0 ^= r3;
        r2 ^= r4;
        r3 ^= r2;
        r2 &= r0;
        r2 ^= r4;
        r3 = ~r3;
        r3 |= r0;
        r4 ^= r3;
        r4 = rotateLeft(r4, 13);
        r2 = rotateLeft(r2, 3);
        r1 = r1 ^ r4 ^ r2;
        r0 = r0 ^ r2 ^ (r4 << 3);
        r1 = rotateLeft(r1, 1);
        r0 = rotateLeft(r0, 7);
        r4 = r4 ^ r1 ^ r0;
        r2 = r2 ^ r0 ^ (r1 << 7);
        r4 = rotateLeft(r4, 5);
        r2 = rotateLeft(r2, 22);
        r4 ^= serpent24SubKeys[32];
        r1 ^= serpent24SubKeys[32 + 1];
        r2 ^= serpent24SubKeys[32 + 2];
        r0 ^= serpent24SubKeys[32 + 3];
        r0 ^= r4;
        r3 = r1;
        r1 &= r0;
        r3 ^= r2;
        r1 ^= r4;
        r4 |= r0;
        r4 ^= r3;
        r3 ^= r0;
        r0 ^= r2;
        r2 |= r1;
        r2 ^= r3;
        r3 = ~r3;
        r3 |= r1;
        r1 ^= r0;
        r1 ^= r3;
        r0 |= r4;
        r1 ^= r0;
        r3 ^= r0;
        r1 = rotateLeft(r1, 13);
        r2 = rotateLeft(r2, 3);
        r3 = r3 ^ r1 ^ r2;
        r4 = r4 ^ r2 ^ (r1 << 3);
        r3 = rotateLeft(r3, 1);
        r4 = rotateLeft(r4, 7);
        r1 = r1 ^ r3 ^ r4;
        r2 = r2 ^ r4 ^ (r3 << 7);
        r1 = rotateLeft(r1, 5);
        r2 = rotateLeft(r2, 22);
        r1 ^= serpent24SubKeys[36];
        r3 ^= serpent24SubKeys[36 + 1];
        r2 ^= serpent24SubKeys[36 + 2];
        r4 ^= serpent24SubKeys[36 + 3];
        r1 = ~r1;
        r2 = ~r2;
        r0 = r1;
        r1 &= r3;
        r2 ^= r1;
        r1 |= r4;
        r4 ^= r2;
        r3 ^= r1;
        r1 ^= r0;
        r0 |= r3;
        r3 ^= r4;
        r2 |= r1;
        r2 &= r0;
        r1 ^= r3;
        r3 &= r2;
        r3 ^= r1;
        r1 &= r2;
        r1 ^= r0;
        r2 = rotateLeft(r2, 13);
        r4 = rotateLeft(r4, 3);
        r1 = r1 ^ r2 ^ r4;
        r3 = r3 ^ r4 ^ (r2 << 3);
        r1 = rotateLeft(r1, 1);
        r3 = rotateLeft(r3, 7);
        r2 = r2 ^ r1 ^ r3;
        r4 = r4 ^ r3 ^ (r1 << 7);
        r2 = rotateLeft(r2, 5);
        r4 = rotateLeft(r4, 22);
        r2 ^= serpent24SubKeys[40];
        r1 ^= serpent24SubKeys[40 + 1];
        r4 ^= serpent24SubKeys[40 + 2];
        r3 ^= serpent24SubKeys[40 + 3];
        r0 = r2;
        r2 &= r4;
        r2 ^= r3;
        r4 ^= r1;
        r4 ^= r2;
        r3 |= r0;
        r3 ^= r1;
        r0 ^= r4;
        r1 = r3;
        r3 |= r0;
        r3 ^= r2;
        r2 &= r1;
        r0 ^= r2;
        r1 ^= r3;
        r1 ^= r0;
        r0 = ~r0;
        r4 = rotateLeft(r4, 13);
        r1 = rotateLeft(r1, 3);
        r3 = r3 ^ r4 ^ r1;
        r0 = r0 ^ r1 ^ (r4 << 3);
        r3 = rotateLeft(r3, 1);
        r0 = rotateLeft(r0, 7);
        r4 = r4 ^ r3 ^ r0;
        r1 = r1 ^ r0 ^ (r3 << 7);
        r4 = rotateLeft(r4, 5);
        r1 = rotateLeft(r1, 22);
        r4 ^= serpent24SubKeys[44];
        r3 ^= serpent24SubKeys[44 + 1];
        r1 ^= serpent24SubKeys[44 + 2];
        r0 ^= serpent24SubKeys[44 + 3];
        r2 = r4;
        r4 |= r0;
        r0 ^= r3;
        r3 &= r2;
        r2 ^= r1;
        r1 ^= r0;
        r0 &= r4;
        r2 |= r3;
        r0 ^= r2;
        r4 ^= r3;
        r2 &= r4;
        r3 ^= r0;
        r2 ^= r1;
        r3 |= r4;
        r3 ^= r1;
        r4 ^= r0;
        r1 = r3;
        r3 |= r0;
        r3 ^= r4;
        r3 = rotateLeft(r3, 13);
        r0 = rotateLeft(r0, 3);
        r1 = r1 ^ r3 ^ r0;
        r2 = r2 ^ r0 ^ (r3 << 3);
        r1 = rotateLeft(r1, 1);
        r2 = rotateLeft(r2, 7);
        r3 = r3 ^ r1 ^ r2;
        r0 = r0 ^ r2 ^ (r1 << 7);
        r3 = rotateLeft(r3, 5);
        r0 = rotateLeft(r0, 22);
        lfsr9 = r3;
        lfsr8 = r1;
        lfsr7 = r0;
        lfsr6 = r2;
        r3 ^= serpent24SubKeys[48];
        r1 ^= serpent24SubKeys[48 + 1];
        r0 ^= serpent24SubKeys[48 + 2];
        r2 ^= serpent24SubKeys[48 + 3];
        r1 ^= r2;
        r2 = ~r2;
        r0 ^= r2;
        r2 ^= r3;
        r4 = r1;
        r1 &= r2;
        r1 ^= r0;
        r4 ^= r2;
        r3 ^= r4;
        r0 &= r4;
        r0 ^= r3;
        r3 &= r1;
        r2 ^= r3;
        r4 |= r1;
        r4 ^= r3;
        r3 |= r2;
        r3 ^= r0;
        r0 &= r2;
        r3 = ~r3;
        r4 ^= r0;
        r1 = rotateLeft(r1, 13);
        r3 = rotateLeft(r3, 3);
        r4 = r4 ^ r1 ^ r3;
        r2 = r2 ^ r3 ^ (r1 << 3);
        r4 = rotateLeft(r4, 1);
        r2 = rotateLeft(r2, 7);
        r1 = r1 ^ r4 ^ r2;
        r3 = r3 ^ r2 ^ (r4 << 7);
        r1 = rotateLeft(r1, 5);
        r3 = rotateLeft(r3, 22);
        r1 ^= serpent24SubKeys[52];
        r4 ^= serpent24SubKeys[52 + 1];
        r3 ^= serpent24SubKeys[52 + 2];
        r2 ^= serpent24SubKeys[52 + 3];
        r1 ^= r4;
        r4 ^= r2;
        r2 = ~r2;
        r0 = r4;
        r4 &= r1;
        r3 ^= r2;
        r4 ^= r3;
        r3 |= r0;
        r0 ^= r2;
        r2 &= r4;
        r2 ^= r1;
        r0 ^= r4;
        r0 ^= r3;
        r3 ^= r1;
        r1 &= r2;
        r3 = ~r3;
        r1 ^= r0;
        r0 |= r2;
        r3 ^= r0;
        r4 = rotateLeft(r4, 13);
        r1 = rotateLeft(r1, 3);
        r2 = r2 ^ r4 ^ r1;
        r3 = r3 ^ r1 ^ (r4 << 3);
        r2 = rotateLeft(r2, 1);
        r3 = rotateLeft(r3, 7);
        r4 = r4 ^ r2 ^ r3;
        r1 = r1 ^ r3 ^ (r2 << 7);
        r4 = rotateLeft(r4, 5);
        r1 = rotateLeft(r1, 22);
        r4 ^= serpent24SubKeys[56];
        r2 ^= serpent24SubKeys[56 + 1];
        r1 ^= serpent24SubKeys[56 + 2];
        r3 ^= serpent24SubKeys[56 + 3];
        r1 = ~r1;
        r0 = r3;
        r3 &= r4;
        r4 ^= r0;
        r3 ^= r1;
        r1 |= r0;
        r2 ^= r3;
        r1 ^= r4;
        r4 |= r2;
        r1 ^= r2;
        r0 ^= r4;
        r4 |= r3;
        r4 ^= r1;
        r0 ^= r3;
        r0 ^= r4;
        r3 = ~r3;
        r1 &= r0;
        r1 ^= r3;
        r4 = rotateLeft(r4, 13);
        r0 = rotateLeft(r0, 3);
        r2 = r2 ^ r4 ^ r0;
        r1 = r1 ^ r0 ^ (r4 << 3);
        r2 = rotateLeft(r2, 1);
        r1 = rotateLeft(r1, 7);
        r4 = r4 ^ r2 ^ r1;
        r0 = r0 ^ r1 ^ (r2 << 7);
        r4 = rotateLeft(r4, 5);
        r0 = rotateLeft(r0, 22);
        r4 ^= serpent24SubKeys[60];
        r2 ^= serpent24SubKeys[60 + 1];
        r0 ^= serpent24SubKeys[60 + 2];
        r1 ^= serpent24SubKeys[60 + 3];
        r3 = r2;
        r2 |= r0;
        r2 ^= r1;
        r3 ^= r0;
        r0 ^= r2;
        r1 |= r3;
        r1 &= r4;
        r3 ^= r0;
        r1 ^= r2;
        r2 |= r3;
        r2 ^= r4;
        r4 |= r3;
        r4 ^= r0;
        r2 ^= r3;
        r0 ^= r2;
        r2 &= r4;
        r2 ^= r3;
        r0 = ~r0;
        r0 |= r4;
        r3 ^= r0;
        r3 = rotateLeft(r3, 13);
        r2 = rotateLeft(r2, 3);
        r1 = r1 ^ r3 ^ r2;
        r4 = r4 ^ r2 ^ (r3 << 3);
        r1 = rotateLeft(r1, 1);
        r4 = rotateLeft(r4, 7);
        r3 = r3 ^ r1 ^ r4;
        r2 = r2 ^ r4 ^ (r1 << 7);
        r3 = rotateLeft(r3, 5);
        r2 = rotateLeft(r2, 22);
        r3 ^= serpent24SubKeys[64];
        r1 ^= serpent24SubKeys[64 + 1];
        r2 ^= serpent24SubKeys[64 + 2];
        r4 ^= serpent24SubKeys[64 + 3];
        r4 ^= r3;
        r0 = r1;
        r1 &= r4;
        r0 ^= r2;
        r1 ^= r3;
        r3 |= r4;
        r3 ^= r0;
        r0 ^= r4;
        r4 ^= r2;
        r2 |= r1;
        r2 ^= r0;
        r0 = ~r0;
        r0 |= r1;
        r1 ^= r4;
        r1 ^= r0;
        r4 |= r3;
        r1 ^= r4;
        r0 ^= r4;
        r1 = rotateLeft(r1, 13);
        r2 = rotateLeft(r2, 3);
        r0 = r0 ^ r1 ^ r2;
        r3 = r3 ^ r2 ^ (r1 << 3);
        r0 = rotateLeft(r0, 1);
        r3 = rotateLeft(r3, 7);
        r1 = r1 ^ r0 ^ r3;
        r2 = r2 ^ r3 ^ (r0 << 7);
        r1 = rotateLeft(r1, 5);
        r2 = rotateLeft(r2, 22);
        r1 ^= serpent24SubKeys[68];
        r0 ^= serpent24SubKeys[68 + 1];
        r2 ^= serpent24SubKeys[68 + 2];
        r3 ^= serpent24SubKeys[68 + 3];
        r1 = ~r1;
        r2 = ~r2;
        r4 = r1;
        r1 &= r0;
        r2 ^= r1;
        r1 |= r3;
        r3 ^= r2;
        r0 ^= r1;
        r1 ^= r4;
        r4 |= r0;
        r0 ^= r3;
        r2 |= r1;
        r2 &= r4;
        r1 ^= r0;
        r0 &= r2;
        r0 ^= r1;
        r1 &= r2;
        r1 ^= r4;
        r2 = rotateLeft(r2, 13);
        r3 = rotateLeft(r3, 3);
        r1 = r1 ^ r2 ^ r3;
        r0 = r0 ^ r3 ^ (r2 << 3);
        r1 = rotateLeft(r1, 1);
        r0 = rotateLeft(r0, 7);
        r2 = r2 ^ r1 ^ r0;
        r3 = r3 ^ r0 ^ (r1 << 7);
        r2 = rotateLeft(r2, 5);
        r3 = rotateLeft(r3, 22);
        fsmR1 = r2;
        lfsr4 = r1;
        fsmR2 = r3;
        lfsr5 = r0;
        r2 ^= serpent24SubKeys[72];
        r1 ^= serpent24SubKeys[72 + 1];
        r3 ^= serpent24SubKeys[72 + 2];
        r0 ^= serpent24SubKeys[72 + 3];
        r4 = r2;
        r2 &= r3;
        r2 ^= r0;
        r3 ^= r1;
        r3 ^= r2;
        r0 |= r4;
        r0 ^= r1;
        r4 ^= r3;
        r1 = r0;
        r0 |= r4;
        r0 ^= r2;
        r2 &= r1;
        r4 ^= r2;
        r1 ^= r0;
        r1 ^= r4;
        r4 = ~r4;
        r3 = rotateLeft(r3, 13);
        r1 = rotateLeft(r1, 3);
        r0 = r0 ^ r3 ^ r1;
        r4 = r4 ^ r1 ^ (r3 << 3);
        r0 = rotateLeft(r0, 1);
        r4 = rotateLeft(r4, 7);
        r3 = r3 ^ r0 ^ r4;
        r1 = r1 ^ r4 ^ (r0 << 7);
        r3 = rotateLeft(r3, 5);
        r1 = rotateLeft(r1, 22);
        r3 ^= serpent24SubKeys[76];
        r0 ^= serpent24SubKeys[76 + 1];
        r1 ^= serpent24SubKeys[76 + 2];
        r4 ^= serpent24SubKeys[76 + 3];
        r2 = r3;
        r3 |= r4;
        r4 ^= r0;
        r0 &= r2;
        r2 ^= r1;
        r1 ^= r4;
        r4 &= r3;
        r2 |= r0;
        r4 ^= r2;
        r3 ^= r0;
        r2 &= r3;
        r0 ^= r4;
        r2 ^= r1;
        r0 |= r3;
        r0 ^= r1;
        r3 ^= r4;
        r1 = r0;
        r0 |= r4;
        r0 ^= r3;
        r0 = rotateLeft(r0, 13);
        r4 = rotateLeft(r4, 3);
        r1 = r1 ^ r0 ^ r4;
        r2 = r2 ^ r4 ^ (r0 << 3);
        r1 = rotateLeft(r1, 1);
        r2 = rotateLeft(r2, 7);
        r0 = r0 ^ r1 ^ r2;
        r4 = r4 ^ r2 ^ (r1 << 7);
        r0 = rotateLeft(r0, 5);
        r4 = rotateLeft(r4, 22);
        r0 ^= serpent24SubKeys[80];
        r1 ^= serpent24SubKeys[80 + 1];
        r4 ^= serpent24SubKeys[80 + 2];
        r2 ^= serpent24SubKeys[80 + 3];
        r1 ^= r2;
        r2 = ~r2;
        r4 ^= r2;
        r2 ^= r0;
        r3 = r1;
        r1 &= r2;
        r1 ^= r4;
        r3 ^= r2;
        r0 ^= r3;
        r4 &= r3;
        r4 ^= r0;
        r0 &= r1;
        r2 ^= r0;
        r3 |= r1;
        r3 ^= r0;
        r0 |= r2;
        r0 ^= r4;
        r4 &= r2;
        r0 = ~r0;
        r3 ^= r4;
        r1 = rotateLeft(r1, 13);
        r0 = rotateLeft(r0, 3);
        r3 = r3 ^ r1 ^ r0;
        r2 = r2 ^ r0 ^ (r1 << 3);
        r3 = rotateLeft(r3, 1);
        r2 = rotateLeft(r2, 7);
        r1 = r1 ^ r3 ^ r2;
        r0 = r0 ^ r2 ^ (r3 << 7);
        r1 = rotateLeft(r1, 5);
        r0 = rotateLeft(r0, 22);
        r1 ^= serpent24SubKeys[84];
        r3 ^= serpent24SubKeys[84 + 1];
        r0 ^= serpent24SubKeys[84 + 2];
        r2 ^= serpent24SubKeys[84 + 3];
        r1 ^= r3;
        r3 ^= r2;
        r2 = ~r2;
        r4 = r3;
        r3 &= r1;
        r0 ^= r2;
        r3 ^= r0;
        r0 |= r4;
        r4 ^= r2;
        r2 &= r3;
        r2 ^= r1;
        r4 ^= r3;
        r4 ^= r0;
        r0 ^= r1;
        r1 &= r2;
        r0 = ~r0;
        r1 ^= r4;
        r4 |= r2;
        r0 ^= r4;
        r3 = rotateLeft(r3, 13);
        r1 = rotateLeft(r1, 3);
        r2 = r2 ^ r3 ^ r1;
        r0 = r0 ^ r1 ^ (r3 << 3);
        r2 = rotateLeft(r2, 1);
        r0 = rotateLeft(r0, 7);
        r3 = r3 ^ r2 ^ r0;
        r1 = r1 ^ r0 ^ (r2 << 7);
        r3 = rotateLeft(r3, 5);
        r1 = rotateLeft(r1, 22);
        r3 ^= serpent24SubKeys[88];
        r2 ^= serpent24SubKeys[88 + 1];
        r1 ^= serpent24SubKeys[88 + 2];
        r0 ^= serpent24SubKeys[88 + 3];
        r1 = ~r1;
        r4 = r0;
        r0 &= r3;
        r3 ^= r4;
        r0 ^= r1;
        r1 |= r4;
        r2 ^= r0;
        r1 ^= r3;
        r3 |= r2;
        r1 ^= r2;
        r4 ^= r3;
        r3 |= r0;
        r3 ^= r1;
        r4 ^= r0;
        r4 ^= r3;
        r0 = ~r0;
        r1 &= r4;
        r1 ^= r0;
        r3 = rotateLeft(r3, 13);
        r4 = rotateLeft(r4, 3);
        r2 = r2 ^ r3 ^ r4;
        r1 = r1 ^ r4 ^ (r3 << 3);
        r2 = rotateLeft(r2, 1);
        r1 = rotateLeft(r1, 7);
        r3 = r3 ^ r2 ^ r1;
        r4 = r4 ^ r1 ^ (r2 << 7);
        r3 = rotateLeft(r3, 5);
        r4 = rotateLeft(r4, 22);
        r3 ^= serpent24SubKeys[92];
        r2 ^= serpent24SubKeys[92 + 1];
        r4 ^= serpent24SubKeys[92 + 2];
        r1 ^= serpent24SubKeys[92 + 3];
        r0 = r2;
        r2 |= r4;
        r2 ^= r1;
        r0 ^= r4;
        r4 ^= r2;
        r1 |= r0;
        r1 &= r3;
        r0 ^= r4;
        r1 ^= r2;
        r2 |= r0;
        r2 ^= r3;
        r3 |= r0;
        r3 ^= r4;
        r2 ^= r0;
        r4 ^= r2;
        r2 &= r3;
        r2 ^= r0;
        r4 = ~r4;
        r4 |= r3;
        r0 ^= r4;
        r0 = rotateLeft(r0, 13);
        r2 = rotateLeft(r2, 3);
        r1 = r1 ^ r0 ^ r2;
        r3 = r3 ^ r2 ^ (r0 << 3);
        r1 = rotateLeft(r1, 1);
        r3 = rotateLeft(r3, 7);
        r0 = r0 ^ r1 ^ r3;
        r2 = r2 ^ r3 ^ (r1 << 7);
        r0 = rotateLeft(r0, 5);
        r2 = rotateLeft(r2, 22);
        r0 ^= serpent24SubKeys[96];
        r1 ^= serpent24SubKeys[96 + 1];
        r2 ^= serpent24SubKeys[96 + 2];
        r3 ^= serpent24SubKeys[96 + 3];
        lfsr3 = r0;
        lfsr2 = r1;
        lfsr1 = r2;
        lfsr0 = r3;
    }

    /**
     * mulAlpha[] is used to multiply a word by alpha; mulAlpha[x]
     * is equal to x * alpha^4.
     */
    private static final int[] MUL_ALPHA = new int[256];

    /**
     * divAlpha[] is used to divide a word by alpha; divAlpha[x]
     * is equal to x / alpha.
     */
    private static final int[] DIV_ALPHA = new int[256];

    static {
        /*
         * We first build exponential and logarithm tables
         * relatively to beta in F_{2^8}. We set log(0x00) = 0xFF
         * conventionaly, but this is actually not used in our
         * computations.
         */
        final int[] expb = new int[256];
        for (int i = 0, x = 0x01; i < 0xFF; i++) {
            expb[i] = x;
            x <<= 1;
            if (x > 0xFF) {
                x ^= 0x1A9;
            }
        }
        expb[0xFF] = 0x00;
        final int[] logb = new int[256];
        for (int i = 0; i < 0x100; i++) {
            logb[expb[i]] = i;
        }

        /*
         * We now compute mulAlpha[] and divAlpha[]. For all
         * x != 0, we work with invertible numbers, which are
         * as such powers of beta. Multiplication (in F_{2^8})
         * is then implemented as integer addition modulo 255,
         * over the exponents computed by the logb[] table.
         *
         * We have the following equations:
         * alpha^4 = beta^23 * alpha^3 + beta^245 * alpha^2
         *           + beta^48 * alpha + beta^239
         * 1/alpha = beta^16 * alpha^3 + beta^39 * alpha^2
         *           + beta^6 * alpha + beta^64
         */
        MUL_ALPHA[0x00] = 0x00000000;
        DIV_ALPHA[0x00] = 0x00000000;
        for (int x = 1; x < 0x100; x++) {
            final int ex = logb[x];
            MUL_ALPHA[x] = (expb[(ex + 23) % 255] << 24)
                    | (expb[(ex + 245) % 255] << 16)
                    | (expb[(ex + 48) % 255] << 8)
                    | expb[(ex + 239) % 255];
            DIV_ALPHA[x] = (expb[(ex + 16) % 255] << 24)
                    | (expb[(ex + 39) % 255] << 16)
                    | (expb[(ex + 6) % 255] << 8)
                    | expb[(ex + 64) % 255];
        }
    }

    /**
     * Produce 80 bytes of output stream into the provided buffer.
     *
     * @param buf   the output buffer
     * @param off   the output offset
     */
    @SuppressWarnings("checkstyle:MethodLength")
    private void makeStreamBlock(final byte[] buf, final int off) {
        int s0 = lfsr0;
        int s1 = lfsr1;
        int s2 = lfsr2;
        int s3 = lfsr3;
        int s4 = lfsr4;
        int s5 = lfsr5;
        int s6 = lfsr6;
        int s7 = lfsr7;
        int s8 = lfsr8;
        int s9 = lfsr9;
        int r1 = fsmR1;
        int r2 = fsmR2;

        int tt = r1;
        r1 = r2 + (s1 ^ ((r1 & 0x01) != 0 ? s8 : 0));
        r2 = rotateLeft(tt * 0x54655307, 7);
        int v0 = s0;
        s0 = ((s0 << 8) ^ MUL_ALPHA[s0 >>> 24])
                ^ ((s3 >>> 8) ^ DIV_ALPHA[s3 & 0xFF]) ^ s9;
        int f0 = (s9 + r1) ^ r2;

        tt = r1;
        r1 = r2 + (s2 ^ ((r1 & 0x01) != 0 ? s9 : 0));
        r2 = rotateLeft(tt * 0x54655307, 7);
        int v1 = s1;
        s1 = ((s1 << 8) ^ MUL_ALPHA[s1 >>> 24])
                ^ ((s4 >>> 8) ^ DIV_ALPHA[s4 & 0xFF]) ^ s0;
        int f1 = (s0 + r1) ^ r2;

        tt = r1;
        r1 = r2 + (s3 ^ ((r1 & 0x01) != 0 ? s0 : 0));
        r2 = rotateLeft(tt * 0x54655307, 7);
        int v2 = s2;
        s2 = ((s2 << 8) ^ MUL_ALPHA[s2 >>> 24])
                ^ ((s5 >>> 8) ^ DIV_ALPHA[s5 & 0xFF]) ^ s1;
        int f2 = (s1 + r1) ^ r2;

        tt = r1;
        r1 = r2 + (s4 ^ ((r1 & 0x01) != 0 ? s1 : 0));
        r2 = rotateLeft(tt * 0x54655307, 7);
        int v3 = s3;
        s3 = ((s3 << 8) ^ MUL_ALPHA[s3 >>> 24])
                ^ ((s6 >>> 8) ^ DIV_ALPHA[s6 & 0xFF]) ^ s2;
        int f3 = (s2 + r1) ^ r2;

        /*
         * Apply the third S-box (number 2) on (f3, f2, f1, f0).
         */
        int f4 = f0;
        f0 &= f2;
        f0 ^= f3;
        f2 ^= f1;
        f2 ^= f0;
        f3 |= f4;
        f3 ^= f1;
        f4 ^= f2;
        f1 = f3;
        f3 |= f4;
        f3 ^= f0;
        f0 &= f1;
        f4 ^= f0;
        f1 ^= f3;
        f1 ^= f4;
        f4 = ~f4;

        /*
         * S-box result is in (f2, f3, f1, f4).
         */
        encode32le(f2 ^ v0, buf, off);
        encode32le(f3 ^ v1, buf, off + 4);
        encode32le(f1 ^ v2, buf, off + 8);
        encode32le(f4 ^ v3, buf, off + 12);

        tt = r1;
        r1 = r2 + (s5 ^ ((r1 & 0x01) != 0 ? s2 : 0));
        r2 = rotateLeft(tt * 0x54655307, 7);
        v0 = s4;
        s4 = ((s4 << 8) ^ MUL_ALPHA[s4 >>> 24])
                ^ ((s7 >>> 8) ^ DIV_ALPHA[s7 & 0xFF]) ^ s3;
        f0 = (s3 + r1) ^ r2;

        tt = r1;
        r1 = r2 + (s6 ^ ((r1 & 0x01) != 0 ? s3 : 0));
        r2 = rotateLeft(tt * 0x54655307, 7);
        v1 = s5;
        s5 = ((s5 << 8) ^ MUL_ALPHA[s5 >>> 24])
                ^ ((s8 >>> 8) ^ DIV_ALPHA[s8 & 0xFF]) ^ s4;
        f1 = (s4 + r1) ^ r2;

        tt = r1;
        r1 = r2 + (s7 ^ ((r1 & 0x01) != 0 ? s4 : 0));
        r2 = rotateLeft(tt * 0x54655307, 7);
        v2 = s6;
        s6 = ((s6 << 8) ^ MUL_ALPHA[s6 >>> 24])
                ^ ((s9 >>> 8) ^ DIV_ALPHA[s9 & 0xFF]) ^ s5;
        f2 = (s5 + r1) ^ r2;

        tt = r1;
        r1 = r2 + (s8 ^ ((r1 & 0x01) != 0 ? s5 : 0));
        r2 = rotateLeft(tt * 0x54655307, 7);
        v3 = s7;
        s7 = ((s7 << 8) ^ MUL_ALPHA[s7 >>> 24])
                ^ ((s0 >>> 8) ^ DIV_ALPHA[s0 & 0xFF]) ^ s6;
        f3 = (s6 + r1) ^ r2;

        /*
         * Apply the third S-box (number 2) on (f3, f2, f1, f0).
         */
        f4 = f0;
        f0 &= f2;
        f0 ^= f3;
        f2 ^= f1;
        f2 ^= f0;
        f3 |= f4;
        f3 ^= f1;
        f4 ^= f2;
        f1 = f3;
        f3 |= f4;
        f3 ^= f0;
        f0 &= f1;
        f4 ^= f0;
        f1 ^= f3;
        f1 ^= f4;
        f4 = ~f4;

        /*
         * S-box result is in (f2, f3, f1, f4).
         */
        encode32le(f2 ^ v0, buf, off + 16);
        encode32le(f3 ^ v1, buf, off + 20);
        encode32le(f1 ^ v2, buf, off + 24);
        encode32le(f4 ^ v3, buf, off + 28);

        tt = r1;
        r1 = r2 + (s9 ^ ((r1 & 0x01) != 0 ? s6 : 0));
        r2 = rotateLeft(tt * 0x54655307, 7);
        v0 = s8;
        s8 = ((s8 << 8) ^ MUL_ALPHA[s8 >>> 24])
                ^ ((s1 >>> 8) ^ DIV_ALPHA[s1 & 0xFF]) ^ s7;
        f0 = (s7 + r1) ^ r2;

        tt = r1;
        r1 = r2 + (s0 ^ ((r1 & 0x01) != 0 ? s7 : 0));
        r2 = rotateLeft(tt * 0x54655307, 7);
        v1 = s9;
        s9 = ((s9 << 8) ^ MUL_ALPHA[s9 >>> 24])
                ^ ((s2 >>> 8) ^ DIV_ALPHA[s2 & 0xFF]) ^ s8;
        f1 = (s8 + r1) ^ r2;

        tt = r1;
        r1 = r2 + (s1 ^ ((r1 & 0x01) != 0 ? s8 : 0));
        r2 = rotateLeft(tt * 0x54655307, 7);
        v2 = s0;
        s0 = ((s0 << 8) ^ MUL_ALPHA[s0 >>> 24])
                ^ ((s3 >>> 8) ^ DIV_ALPHA[s3 & 0xFF]) ^ s9;
        f2 = (s9 + r1) ^ r2;

        tt = r1;
        r1 = r2 + (s2 ^ ((r1 & 0x01) != 0 ? s9 : 0));
        r2 = rotateLeft(tt * 0x54655307, 7);
        v3 = s1;
        s1 = ((s1 << 8) ^ MUL_ALPHA[s1 >>> 24])
                ^ ((s4 >>> 8) ^ DIV_ALPHA[s4 & 0xFF]) ^ s0;
        f3 = (s0 + r1) ^ r2;

        /*
         * Apply the third S-box (number 2) on (f3, f2, f1, f0).
         */
        f4 = f0;
        f0 &= f2;
        f0 ^= f3;
        f2 ^= f1;
        f2 ^= f0;
        f3 |= f4;
        f3 ^= f1;
        f4 ^= f2;
        f1 = f3;
        f3 |= f4;
        f3 ^= f0;
        f0 &= f1;
        f4 ^= f0;
        f1 ^= f3;
        f1 ^= f4;
        f4 = ~f4;

        /*
         * S-box result is in (f2, f3, f1, f4).
         */
        encode32le(f2 ^ v0, buf, off + 32);
        encode32le(f3 ^ v1, buf, off + 36);
        encode32le(f1 ^ v2, buf, off + 40);
        encode32le(f4 ^ v3, buf, off + 44);

        tt = r1;
        r1 = r2 + (s3 ^ ((r1 & 0x01) != 0 ? s0 : 0));
        r2 = rotateLeft(tt * 0x54655307, 7);
        v0 = s2;
        s2 = ((s2 << 8) ^ MUL_ALPHA[s2 >>> 24])
                ^ ((s5 >>> 8) ^ DIV_ALPHA[s5 & 0xFF]) ^ s1;
        f0 = (s1 + r1) ^ r2;

        tt = r1;
        r1 = r2 + (s4 ^ ((r1 & 0x01) != 0 ? s1 : 0));
        r2 = rotateLeft(tt * 0x54655307, 7);
        v1 = s3;
        s3 = ((s3 << 8) ^ MUL_ALPHA[s3 >>> 24])
                ^ ((s6 >>> 8) ^ DIV_ALPHA[s6 & 0xFF]) ^ s2;
        f1 = (s2 + r1) ^ r2;

        tt = r1;
        r1 = r2 + (s5 ^ ((r1 & 0x01) != 0 ? s2 : 0));
        r2 = rotateLeft(tt * 0x54655307, 7);
        v2 = s4;
        s4 = ((s4 << 8) ^ MUL_ALPHA[s4 >>> 24])
                ^ ((s7 >>> 8) ^ DIV_ALPHA[s7 & 0xFF]) ^ s3;
        f2 = (s3 + r1) ^ r2;

        tt = r1;
        r1 = r2 + (s6 ^ ((r1 & 0x01) != 0 ? s3 : 0));
        r2 = rotateLeft(tt * 0x54655307, 7);
        v3 = s5;
        s5 = ((s5 << 8) ^ MUL_ALPHA[s5 >>> 24])
                ^ ((s8 >>> 8) ^ DIV_ALPHA[s8 & 0xFF]) ^ s4;
        f3 = (s4 + r1) ^ r2;

        /*
         * Apply the third S-box (number 2) on (f3, f2, f1, f0).
         */
        f4 = f0;
        f0 &= f2;
        f0 ^= f3;
        f2 ^= f1;
        f2 ^= f0;
        f3 |= f4;
        f3 ^= f1;
        f4 ^= f2;
        f1 = f3;
        f3 |= f4;
        f3 ^= f0;
        f0 &= f1;
        f4 ^= f0;
        f1 ^= f3;
        f1 ^= f4;
        f4 = ~f4;

        /*
         * S-box result is in (f2, f3, f1, f4).
         */
        encode32le(f2 ^ v0, buf, off + 48);
        encode32le(f3 ^ v1, buf, off + 52);
        encode32le(f1 ^ v2, buf, off + 56);
        encode32le(f4 ^ v3, buf, off + 60);

        tt = r1;
        r1 = r2 + (s7 ^ ((r1 & 0x01) != 0 ? s4 : 0));
        r2 = rotateLeft(tt * 0x54655307, 7);
        v0 = s6;
        s6 = ((s6 << 8) ^ MUL_ALPHA[s6 >>> 24])
                ^ ((s9 >>> 8) ^ DIV_ALPHA[s9 & 0xFF]) ^ s5;
        f0 = (s5 + r1) ^ r2;

        tt = r1;
        r1 = r2 + (s8 ^ ((r1 & 0x01) != 0 ? s5 : 0));
        r2 = rotateLeft(tt * 0x54655307, 7);
        v1 = s7;
        s7 = ((s7 << 8) ^ MUL_ALPHA[s7 >>> 24])
                ^ ((s0 >>> 8) ^ DIV_ALPHA[s0 & 0xFF]) ^ s6;
        f1 = (s6 + r1) ^ r2;

        tt = r1;
        r1 = r2 + (s9 ^ ((r1 & 0x01) != 0 ? s6 : 0));
        r2 = rotateLeft(tt * 0x54655307, 7);
        v2 = s8;
        s8 = ((s8 << 8) ^ MUL_ALPHA[s8 >>> 24])
                ^ ((s1 >>> 8) ^ DIV_ALPHA[s1 & 0xFF]) ^ s7;
        f2 = (s7 + r1) ^ r2;

        tt = r1;
        r1 = r2 + (s0 ^ ((r1 & 0x01) != 0 ? s7 : 0));
        r2 = rotateLeft(tt * 0x54655307, 7);
        v3 = s9;
        s9 = ((s9 << 8) ^ MUL_ALPHA[s9 >>> 24])
                ^ ((s2 >>> 8) ^ DIV_ALPHA[s2 & 0xFF]) ^ s8;
        f3 = (s8 + r1) ^ r2;

        /*
         * Apply the third S-box (number 2) on (f3, f2, f1, f0).
         */
        f4 = f0;
        f0 &= f2;
        f0 ^= f3;
        f2 ^= f1;
        f2 ^= f0;
        f3 |= f4;
        f3 ^= f1;
        f4 ^= f2;
        f1 = f3;
        f3 |= f4;
        f3 ^= f0;
        f0 &= f1;
        f4 ^= f0;
        f1 ^= f3;
        f1 ^= f4;
        f4 = ~f4;

        /*
         * S-box result is in (f2, f3, f1, f4).
         */
        encode32le(f2 ^ v0, buf, off + 64);
        encode32le(f3 ^ v1, buf, off + 68);
        encode32le(f1 ^ v2, buf, off + 72);
        encode32le(f4 ^ v3, buf, off + 76);

        lfsr0 = s0;
        lfsr1 = s1;
        lfsr2 = s2;
        lfsr3 = s3;
        lfsr4 = s4;
        lfsr5 = s5;
        lfsr6 = s6;
        lfsr7 = s7;
        lfsr8 = s8;
        lfsr9 = s9;
        fsmR1 = r1;
        fsmR2 = r2;
    }
}
