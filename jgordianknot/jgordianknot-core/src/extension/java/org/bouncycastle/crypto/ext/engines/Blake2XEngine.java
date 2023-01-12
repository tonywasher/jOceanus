/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2023 Tony Washer
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

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.ext.digests.Blake2;
import org.bouncycastle.crypto.ext.digests.Blake2X;
import org.bouncycastle.crypto.ext.params.Blake2Parameters.Builder;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Memoable;

/**
 * Blake2X used as a stream Cipher.
 */
public class Blake2XEngine
        implements StreamCipher, Memoable {
    /**
     * index of next byte in keyStream.
     */
    private int theIndex;

    /**
     * Advanced stream.
     */
    private final byte[] theKeyStream;

    /**
     * Underlying digest.
     */
    private final Blake2X theBlake2X;

    /**
     * Reset state.
     */
    private Blake2X theResetState;

    /**
     * Constructor.
     * @param pDigest the underlying digest
     */
    public Blake2XEngine(final Blake2 pDigest) {
        theBlake2X = new Blake2X(pDigest);
        theKeyStream = new byte[pDigest.getDigestSize()];
    }

    /**
     * Constructor.
     * @param pSource the source engine
     */
    private Blake2XEngine(final Blake2XEngine pSource) {
        theBlake2X = new Blake2X(pSource.theBlake2X);
        theKeyStream = new byte[theBlake2X.getByteLength() >> 1];
        reset(pSource);
    }

    /**
     * initialise a Blake2X cipher.
     * @param forEncryption whether or not we are for encryption.
     * @param params the parameters required to set up the cipher.
     * @exception IllegalArgumentException if the params argument is inappropriate.
     */
    public void init(final boolean forEncryption,
                     final CipherParameters params) {
        /*
         * Blake2X encryption and decryption is completely symmetrical, so the 'forEncryption' is
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
        if (newKey == null || newIV == null) {
            throw new IllegalArgumentException("A key and IV must be provided");
        }

        /* Initialise engine and mark as initialised */
        final Builder myBuilder = new Builder()
                .setKey(newKey)
                .setSalt(newIV)
                .setMaxOutputLen(-1);
        theBlake2X.init(myBuilder.build());

        /* Save reset state */
        theResetState = theBlake2X.copy();

        /* Initialise the stream block */
        theIndex = 0;
        makeStreamBlock();
    }

    @Override
    public String getAlgorithmName() {
        return theBlake2X.getAlgorithmName();
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
            theBlake2X.reset(theResetState);
            theIndex = 0;
            makeStreamBlock();
        }
    }

    @Override
    public byte returnByte(final byte in) {
        final byte out = (byte) (theKeyStream[theIndex] ^ in);
        theIndex = (theIndex + 1) % theKeyStream.length;

        if (theIndex == 0) {
            makeStreamBlock();
        }
        return out;
    }

    /**
     * Generate keystream.
     */
    private void makeStreamBlock() {
        /* Generate next output block */
        theBlake2X.doOutput(theKeyStream, 0, theKeyStream.length);
    }

    @Override
    public Blake2XEngine copy() {
        return new Blake2XEngine(this);
    }

    @Override
    public void reset(final Memoable pState) {
        final Blake2XEngine e = (Blake2XEngine) pState;
        if (theKeyStream.length != e.theKeyStream.length) {
            throw new IllegalArgumentException();
        }
        theBlake2X.reset(e.theBlake2X);
        System.arraycopy(e.theKeyStream, 0, theKeyStream, 0, theKeyStream.length);
        theIndex = e.theIndex;
        theResetState = e.theResetState;
    }
}

