/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package org.bouncycastle.crypto.newengines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.eu.ecrypt.SosemanukFast;

/**
 * StreamCipher implementation based on the SosemanukFast java implementation.
 */
public class SosemanukEngine
        implements StreamCipher {
    /**
     * Advanced stream length. This must Match BufferLength in the Sosemanuk engine, in order to
     * always leave an empty buffer in the underlying engine. This is because on reInit we can reset
     * all registers, but we cannot reset internal buffer, hence we must always leave it empty.
     */
    private static final int STREAM_LEN = 80;

    /**
     * Underlying engine.
     */
    private final SosemanukFast theEngine = new SosemanukFast();

    /**
     * Are we initialised?
     */
    private boolean isInitialised;

    /**
     * index of next byte in keyStream.
     */
    private int theIndex;

    /**
     * Advanced stream.
     */
    private byte[] keyStream = new byte[STREAM_LEN];

    /**
     * Active key.
     */
    private byte[] theKey;

    /**
     * Active initVector.
     */
    private byte[] theInitVector;

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

        /* Must have a key if we have never been initialised */
        if (newKey == null && !isInitialised) {
            throw new IllegalStateException(getAlgorithmName() + " KeyParameter can not be null for first initialisation");
        }

        /* Store key and initVector */
        if (newKey != null) {
            theKey = newKey.clone();
        }
        theInitVector = newIV == null
                                      ? null
                                      : newIV.clone();

        /* Reset cipher and mark as initialised */
        reset();
        isInitialised = true;
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
        if (!isInitialised) {
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
            out[i + outOff] = (byte) (keyStream[theIndex] ^ in[i + inOff]);
            theIndex = (theIndex + 1) & STREAM_LEN - 1;

            if (theIndex == 0) {
                theEngine.makeStream(keyStream, 0, STREAM_LEN);
            }
        }
        return len;
    }

    @Override
    public void reset() {
        theIndex = 0;
        theEngine.setKey(theKey);
        theEngine.setIV(theInitVector);
        theEngine.makeStream(keyStream, 0, STREAM_LEN);
    }

    @Override
    public byte returnByte(final byte in) {
        final byte out = (byte) (keyStream[theIndex] ^ in);
        theIndex = (theIndex + 1) & STREAM_LEN - 1;

        if (theIndex == 0) {
            theEngine.makeStream(keyStream, 0, STREAM_LEN);
        }
        return out;
    }
}
