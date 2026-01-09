/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.random;

import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianSeededRandom;

import java.security.SecureRandom;

/**
 * Combined Random.
 */
public class GordianCombinedRandom
        extends SecureRandom
        implements GordianSeededRandom {
    /**
     * SerialId.
     */
    private static final long serialVersionUID = -7229182850521659615L;

    /**
     * The ctr random.
     */
    private final GordianSecureRandom theCtrRandom;

    /**
     * The hash Random.
     */
    private final GordianSecureRandom theHashRandom;

    /**
     * The pre-calculated buffer.
     */
    private final byte[] theBuffer;

    /**
     * Bytes available in buffer.
     */
    private int bytesAvailable;

    /**
     * Constructor.
     * @param pCtr the ctrRandom
     * @param pHash the hashRandom
     */
    GordianCombinedRandom(final GordianSecureRandom pCtr,
                          final GordianSecureRandom pHash) {
        theHashRandom = pHash;
        theCtrRandom = pCtr;
        theBuffer = new byte[GordianLength.LEN_512.getByteLength()];
    }

    @Override
    public SecureRandom getRandom() {
        return this;
    }

    @Override
    public void setSeed(final byte[] seed) {
        synchronized (this) {
            /* Pass the call on to the ctrRandom */
            theCtrRandom.setSeed(seed);
            bytesAvailable = 0;
        }
    }

    @Override
    public void setSeed(final long seed) {
        synchronized (this) {
            if (theCtrRandom != null) {
                /* Pass the call on to the ctrRandom */
                theCtrRandom.setSeed(seed);
                bytesAvailable = 0;
            }
        }
    }

    @Override
    public void nextBytes(final byte[] bytes) {
        synchronized (this) {
            /* Obtain the required random bytes from the hashRandom */
            theHashRandom.nextBytes(bytes);

            /* Determine how many bytes are needed */
            int bytesNeeded = bytes.length;
            int bytesBuilt = 0;

            /* If we have bytes available */
            if (bytesAvailable > 0) {
                /* Fulfil the request from the buffer as much as possible */
                final int bytesToTransfer = Math.min(bytesNeeded, bytesAvailable);
                processBuffer(theBuffer.length - bytesAvailable, bytes, 0, bytesToTransfer);
                bytesAvailable -= bytesToTransfer;
                bytesNeeded -= bytesToTransfer;
                bytesBuilt += bytesToTransfer;
            }

            /* Loop to fulfil remaining bytes */
            while (bytesNeeded > 0) {
                /* Fill the buffer again */
                theHashRandom.nextBytes(theBuffer);
                bytesAvailable = theBuffer.length;

                /* Fulfil the request from the buffer as much as possible */
                final int bytesToTransfer = Math.min(bytesNeeded, bytesAvailable);
                processBuffer(0, bytes, bytesBuilt, bytesToTransfer);
                bytesAvailable -= bytesToTransfer;
                bytesNeeded -= bytesToTransfer;
                bytesBuilt += bytesToTransfer;
            }
        }
    }

    /**
     * Xor bytes from buffer into output bytes.
     * @param pBufPos the starting position in the buffer
     * @param pOutput the output buffer,
     * @param pOutPos the starting position in the output buffer
     * @param pNumBytes the number of bytes to process
     */
    private void processBuffer(final int pBufPos,
                               final byte[] pOutput,
                               final int pOutPos,
                               final int pNumBytes) {
        /* Loop through the bytes */
        for (int i = 0; i < pNumBytes; i++) {
            pOutput[i + pOutPos] ^= theBuffer[i + pBufPos];
        }
    }

    @Override
    public byte[] generateSeed(final int numBytes) {
        return theCtrRandom.generateSeed(numBytes);
    }

    @Override
    public String getAlgorithm() {
        return theCtrRandom.getAlgorithm()
                + "-" + theHashRandom.getAlgorithm();
    }

    @Override
    public String toString() {
        return getAlgorithm();
    }

    @Override
    public void reseed(final byte[] pXtraInput) {
        synchronized (this) {
            theCtrRandom.reseed(pXtraInput);
            theHashRandom.reseed(pXtraInput);
            bytesAvailable = 0;
        }
    }
}
