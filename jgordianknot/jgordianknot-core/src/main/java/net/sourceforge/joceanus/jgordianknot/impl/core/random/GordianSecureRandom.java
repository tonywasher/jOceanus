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
package net.sourceforge.joceanus.jgordianknot.impl.core.random;

import java.security.SecureRandom;

import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropyUtil;

import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianSeededRandom;

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
    }

    @Override
    public SecureRandom getRandom() {
        return this;
    }

    @Override
    public void setSeed(final byte[] seed) {
        synchronized (this) {
            /* Ensure that the random generator is seeded if it exists */
            if (theRandom != null) {
                theRandom.setSeed(seed);
            }
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

    @Override
    public String toString() {
        return getAlgorithm();
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
}
