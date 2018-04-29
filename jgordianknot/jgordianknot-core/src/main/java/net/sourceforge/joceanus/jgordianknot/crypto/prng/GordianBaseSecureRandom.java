/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto.prng;

import java.security.SecureRandom;

import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropyUtil;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianSecureRandom;

/**
 * SecureRandom wrapper class.
 */
public class GordianBaseSecureRandom
        extends SecureRandom
        implements GordianSecureRandom {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -5438084775827341109L;

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
     * Constructor.
     * @param pGenerator the random generator
     * @param pRandom the secure random instance
     * @param pEntropy the entropy source
     * @param isPredictionResistant true/false
     */
    protected GordianBaseSecureRandom(final GordianDRBGenerator pGenerator,
                                      final SecureRandom pRandom,
                                      final EntropySource pEntropy,
                                      final boolean isPredictionResistant) {
        /* Store parameters */
        theGenerator = pGenerator;
        theRandom = pRandom;
        theEntropy = pEntropy;
        predictionResistant = isPredictionResistant;
    }

    @Override
    public SecureRandom getRandom() {
        return this;
    }

    @Override
    public void setSeed(final byte[] seed) {
        synchronized (this) {
            /* Ensure that the random generator is seeded if it exists */
            theRandom.setSeed(seed);
        }
    }

    @Override
    public void setSeed(final long seed) {
        synchronized (this) {
            /* this will happen when SecureRandom() is created */
            if (theRandom != null) {
                theRandom.setSeed(seed);
            }
        }
    }

    @Override
    public void nextBytes(final byte[] bytes) {
        synchronized (this) {
            /* Generate, checking for reSeed request */
            if (theGenerator.generate(bytes, null, predictionResistant) < 0) {
                /* ReSeed and regenerate */
                theGenerator.reseed(null);
                theGenerator.generate(bytes, null, predictionResistant);
            }
        }
    }

    @Override
    public byte[] generateSeed(final int numBytes) {
        return EntropyUtil.generateSeed(theEntropy, numBytes);
    }

    @Override
    public String getAlgorithm() {
        return theGenerator.getAlgorithm();
    }

    /**
     * Force a reSeed of the DRBG.
     * @param pXtraInput optional additional input
     */
    public void reseed(final byte[] pXtraInput) {
        synchronized (this) {
            theGenerator.reseed(pXtraInput);
        }
    }

    /**
     * Simple secureRandom.
     */
    protected static class GordianSimpleSecureRandom
            implements GordianSecureRandom {
        /**
         * The underlying secureRandom.
         */
        private final SecureRandom theRandom;

        /**
         * Constructor.
         * @param pRandom the underlying random
         */
        protected GordianSimpleSecureRandom(final SecureRandom pRandom) {
            theRandom = pRandom;
        }

        @Override
        public byte[] generateSeed(final int pLength) {
            return theRandom.generateSeed(pLength);
        }

        @Override
        public void setSeed(final byte[] pSeed) {
            theRandom.setSeed(pSeed);
        }

        @Override
        public void reseed(final byte[] pXtraBytes) {
            setSeed(pXtraBytes);
        }

        @Override
        public SecureRandom getRandom() {
            return theRandom;
        }
    }
}
