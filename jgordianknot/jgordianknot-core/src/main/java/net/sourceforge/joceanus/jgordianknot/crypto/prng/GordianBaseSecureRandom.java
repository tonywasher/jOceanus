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
 * -----------------------------------------------------------------------------
 * Derived from code under the following license
 * -----------------------------------------------------------------------------
 * The Bouncy Castle License
 *
 * Copyright (c) 2000-2012 The Legion Of The Bouncy Castle (http://www.bouncycastle.org)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
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
}
