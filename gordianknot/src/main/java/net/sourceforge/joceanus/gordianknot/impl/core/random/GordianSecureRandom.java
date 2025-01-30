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
package net.sourceforge.joceanus.gordianknot.impl.core.random;

import java.security.SecureRandom;

import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropyUtil;

import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianSeededRandom;

/**
 * SecureRandom wrapper class.
 */
public class GordianSecureRandom
        extends SecureRandom
        implements GordianSeededRandom {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -6422187120154720941L;

    /**
     * The Basic Secure Random instance.
     */
    private final SecureRandom theRandom;

    /**
     * The DRBG generator.
     */
    private final transient GordianDRBGenerator theGenerator;

    /**
     * The DRBG provider.
     */
    private final transient EntropySource theEntropy;

    /**
     * Is this instance prediction resistant?
     */
    private final boolean predictionResistant;

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
     * @param pGenerator the random generator
     * @param pRandom the secure random instance
     * @param pEntropy the entropy source
     * @param isPredictionResistant true/false
     */
    GordianSecureRandom(final GordianDRBGenerator pGenerator,
                        final SecureRandom pRandom,
                        final EntropySource pEntropy,
                        final boolean isPredictionResistant) {
        /* Store parameters */
        theGenerator = pGenerator;
        theRandom = pRandom;
        theEntropy = pEntropy;
        predictionResistant = isPredictionResistant;
        theBuffer = new byte[pGenerator.getBlockSize()];
    }

    @Override
    public SecureRandom getRandom() {
        return this;
    }

    @Override
    public void setSeed(final byte[] seed) {
        synchronized (this) {
            /* Pass the call on */
            theRandom.setSeed(seed);
            bytesAvailable = 0;
        }
    }

    @Override
    public void setSeed(final long seed) {
        synchronized (this) {
            if (theRandom != null) {
                theRandom.setSeed(seed);
                bytesAvailable = 0;
            }
        }
    }

    @Override
    public void nextBytes(final byte[] bytes) {
        synchronized (this) {
            /* Fill buffer directly if we are prediction resistant */
            if (predictionResistant) {
                fillBuffer(bytes);
                return;
            }

            /* Determine how many bytes are needed */
            int bytesNeeded = bytes.length;
            int bytesBuilt = 0;

            /* If we have bytes available */
            if (bytesAvailable > 0) {
                /* Fulfil the request from the buffer as much as possible */
                final int bytesToTransfer = Math.min(bytesNeeded, bytesAvailable);
                System.arraycopy(theBuffer, theBuffer.length - bytesAvailable, bytes, 0, bytesToTransfer);
                bytesAvailable -= bytesToTransfer;
                bytesNeeded -= bytesToTransfer;
                bytesBuilt += bytesToTransfer;
            }

            /* Loop to fulfil remaining bytes */
            while (bytesNeeded > 0) {
                /* Fill the buffer again */
                nextBuffer();

                /* Fulfil the request from the buffer as much as possible */
                final int bytesToTransfer = Math.min(bytesNeeded, bytesAvailable);
                System.arraycopy(theBuffer, 0, bytes, bytesBuilt, bytesToTransfer);
                bytesAvailable -= bytesToTransfer;
                bytesNeeded -= bytesToTransfer;
                bytesBuilt += bytesToTransfer;
            }
        }
    }

    /**
     * Next buffer of random data.
     */
    private void nextBuffer() {
        /* Generate, checking for reSeed request */
        if (theGenerator.generate(theBuffer, null, predictionResistant) < 0) {
            /* ReSeed and regenerate */
            theGenerator.reseed(null);
            theGenerator.generate(theBuffer, null, predictionResistant);
        }
        bytesAvailable = theBuffer.length;
    }

    /**
     * Next buffer of random data.
     * @param pBuffer the buffer to fill
     */
    private void fillBuffer(final byte[] pBuffer) {
        /* Generate, checking for reSeed request */
        if (theGenerator.generate(pBuffer, null, predictionResistant) < 0) {
            /* ReSeed and regenerate */
            theGenerator.reseed(null);
            theGenerator.generate(pBuffer, null, predictionResistant);
        }
        bytesAvailable = 0;
    }

    @Override
    public byte[] generateSeed(final int numBytes) {
        return EntropyUtil.generateSeed(theEntropy, numBytes);
    }

    @Override
    public String getAlgorithm() {
        return theGenerator.getAlgorithm();
    }

    @Override
    public String toString() {
        return getAlgorithm();
    }

    @Override
    public void reseed(final byte[] pXtraInput) {
        synchronized (this) {
            theGenerator.reseed(pXtraInput);
            bytesAvailable = 0;
        }
    }
}
