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
package org.bouncycastle.crypto.ext.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.CSHAKEDigest;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;

/**
 * Bouncy implementation of KMAC.
 */
public class KMAC
        implements Mac, Xof {
    /**
     * Not initialised error.
     */
    private static final String INIT_ERROR = "Not initialised";

    /**
     * Digest.
     */
    private final CSHAKEDigest theDigest;

    /**
     * The Rate.
     */
    private final int theRate;

    /**
     * Minimum key length.
     */
    private final int minKeyLen;

    /**
     * Current key.
     */
    private byte[] theKey;

    /**
     * Have we started output?
     */
    private boolean startedOutput;

    /**
     * Create a KMAC.
     * @param bitLength bit length of the underlying SHAKE function, 128 or 256.
     * @param customS the customization string - available for local use.
     */
    public KMAC(final int bitLength,
                final byte[] customS) {
        /* Store the digest */
        theDigest = new CSHAKEDigest(bitLength, "KMAC".getBytes(), customS);
        minKeyLen = bitLength / Byte.SIZE;
        theRate =  (1600 - (bitLength << 1)) / Byte.SIZE;
    }

    @Override
    public String getAlgorithmName() {
        return "KMAC" + minKeyLen * Byte.SIZE;
    }

    @Override
    public void init(final CipherParameters pParams) {
        /* Access the key */
        if (!(pParams instanceof KeyParameter)) {
            throw new IllegalArgumentException(getAlgorithmName() + " requires a key.");
        }
        final KeyParameter keyParams = (KeyParameter) pParams;
        final byte[] myKey = keyParams.getKey();

        /* Check that the key is of sufficient strength */
        if (myKey == null || myKey.length < minKeyLen) {
            throw new IllegalArgumentException(getAlgorithmName() + " requires a key of at least " + minKeyLen + " bytes.");
        }

        /* Build the key details */
        theKey = bytepad(encodeString(myKey));

        /* Reset */
        reset();
    }

    @Override
    public int getMacSize() {
        return getDigestSize();
    }

    @Override
    public int getDigestSize() {
        return theDigest.getDigestSize();
    }

    @Override
    public int getByteLength() {
        return theDigest.getByteLength();
    }

    @Override
    public void update(final byte pIn) {
        /* Check that we have a key */
        if (theKey == null) {
            throw new IllegalStateException(INIT_ERROR);
        }

        /* Update the digest */
        theDigest.update(pIn);
    }

    @Override
    public void update(final byte[] pIn,
                       final int pInOff,
                       final int pLen) {
        /* Check that we have a key */
        if (theKey == null) {
            throw new IllegalStateException(INIT_ERROR);
        }

        /* Update the digest */
        theDigest.update(pIn, pInOff, pLen);
    }

    @Override
    public int doFinal(final byte[] pOut,
                       final int pOutOff) {
        /* finalise the digest */
        return doFinal(pOut, pOutOff, getDigestSize());
    }

    @Override
    public int doFinal(final byte[] pOut,
                       final int pOutOff,
                       final int pOutLen) {
        /* Check that we have a key */
        if (theKey == null) {
            throw new IllegalStateException(INIT_ERROR);
        }

        /* If we have not started output */
        if (!startedOutput) {
            /* Write trailer for known length */
            writeTrailer(pOutLen);
        }

        /* Perform the output */
        final int myResult = doOutput(pOut, pOutOff, pOutLen);

        /* Reset the digest and return */
        reset();
        return myResult;
    }

    @Override
    public int doOutput(final byte[] pOut,
                        final int pOutOff,
                        final int pOutLen) {
        /* If we have not started output */
        if (!startedOutput) {
            /* Write trailer for unknown length */
            writeTrailer(0);
        }

        /* Generate bytes */
        return theDigest.doOutput(pOut, pOutOff, pOutLen);
    }

    @Override
    public void reset() {
        /* Reset the underlying digest */
        theDigest.reset();

        /* Initialise the key */
        if (theKey != null) {
            theDigest.update(theKey, 0, theKey.length);
        }

        /* Clear flag */
        startedOutput = false;
    }

    /**
     * Write trailer.
     * @param pLen the length
     */
    private void writeTrailer(final int pLen) {
        final byte[] myTrailer = rightEncode(pLen * Byte.SIZE);
        theDigest.update(myTrailer, 0, myTrailer.length);
        startedOutput = true;
    }

    /**
     * Encode a string.
     * @param str the string to encode
     * @return the encoded string
     */
    private byte[] bytepad(final byte[] str) {
        /* Left encode the rate */
        final byte[] myRate = leftEncode(theRate);

        /* Determine length of buffer */
        int myLen = myRate.length + str.length;
        final int myRemainder = myLen % theRate;
        if (myRemainder != 0)  {
            myLen += theRate - myRemainder;
        }

        /* Build the buffer and return it */
        final byte[] myBuffer = Arrays.copyOf(myRate, myLen);
        System.arraycopy(str, 0, myBuffer, myRate.length, str.length);
        return myBuffer;
    }

    /**
     * Encode a string.
     * @param str the string to encode
     * @return the encoded string
     */
    private static byte[] encodeString(final byte[] str) {
        /* Handle null or zero length string */
        if (str == null || str.length == 0) {
            return leftEncode(0);
        }

        /* Concatenate the encoded length and the string */
        return Arrays.concatenate(leftEncode(str.length * (long) Byte.SIZE), str);
    }

    /**
     * left Encode a length.
     * @param strLen the length to encode
     * @return the encoded length
     */
    private static byte[] leftEncode(final long strLen) {
        /* Calculate # of bytes required to hold length */
        byte n = 1;
        long v = strLen;
        while ((v >>= Byte.SIZE) != 0) {
            n++;
        }

        /* Allocate byte array and store length */
        final byte[] b = new byte[n + 1];
        b[0] = n;

        /* Encode the length */
        for (int i = 1; i <= n; i++) {
            b[i] = (byte) (strLen >> (Byte.SIZE * (n - i)));
        }

        /* Return the encoded length */
        return b;
    }

    /**
     * right Encode a length.
     * @param strLen the length to encode
     * @return the encoded length
     */
    private static byte[] rightEncode(final long strLen) {
        /* Calculate # of bytes required to hold length */
        byte n = 1;
        long v = strLen;
        while ((v >>= Byte.SIZE) != 0) {
            n++;
        }

        /* Allocate byte array and store length */
        final byte[] b = new byte[n + 1];
        b[n] = n;

        /* Encode the length */
        for (int i = 0; i < n; i++) {
            b[i] = (byte) (strLen >> (Byte.SIZE * (n - i - 1)));
        }

        /* Return the encoded length */
        return b;
    }
}
