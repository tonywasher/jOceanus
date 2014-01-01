/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot;

import java.security.SecureRandom;
import java.util.Arrays;

import org.bouncycastle.crypto.prng.BasicEntropySourceProvider;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropySourceProvider;
import org.bouncycastle.crypto.prng.drbg.SP80090DRBG;

/**
 * Builder class for SP800 DRBG SecureRandom instances, based on the BouncyCastle Code.
 */
public class SP800SecureRandomBuilder {
    /**
     * The number of entropy bits required.
     */
    private static final int NUM_ENTROPY_BITS_REQUIRED = 256;

    /**
     * The power of 2 for RESEED calculation.
     */
    private static final int RESEED_POWER = 48;

    /**
     * The length of time before a reSeed is required.
     */
    protected static final long RESEED_MAX = 1L << (RESEED_POWER - 1);

    /**
     * The power of 2 for BITS calculation.
     */
    private static final int BITS_POWER = 19;

    /**
     * The maximum # of bits that can be requested.
     */
    protected static final int MAX_BITS_REQUEST = 1 << (BITS_POWER - 1);

    /**
     * The Basic Secure Random instance.
     */
    private final SecureRandom theRandom;

    /**
     * The Entropy Source Provider.
     */
    private final EntropySourceProvider theEntropyProvider;

    /**
     * The Security Bytes.
     */
    private byte[] theSecurityBytes = null;

    /**
     * Access the Secure Random.
     * @return the secure random
     */
    protected SecureRandom getRandom() {
        return theRandom;
    }

    /**
     * Basic constructor, creates a builder using an EntropySourceProvider based on the default SecureRandom with predictionResistant set to false.
     * <p>
     * Any SecureRandom created from a builder constructed like this will make use of input passed to SecureRandom.setSeed() if the default SecureRandom does
     * for its generateSeed() call.
     * </p>
     */
    public SP800SecureRandomBuilder() {
        /* Use the default Secure Random and no prediction resistance */
        this(new SecureRandom(), false);
    }

    /**
     * Construct a builder with an EntropySourceProvider based on the passed in SecureRandom and the passed in value for prediction resistance.
     * <p>
     * Any SecureRandom created from a builder constructed like this will make use of input passed to SecureRandom.setSeed() if the passed in SecureRandom does
     * for its generateSeed() call.
     * </p>
     * @param pEntropySource the entropy source
     * @param isPredictionResistant is the random generator to be prediction resistant?
     */
    public SP800SecureRandomBuilder(final SecureRandom pEntropySource,
                                    final boolean isPredictionResistant) {
        /* Store parameters and create an entropy provider */
        theRandom = pEntropySource;
        theEntropyProvider = new BasicEntropySourceProvider(theRandom, isPredictionResistant);
    }

    /**
     * Create a builder which makes creates the SecureRandom objects from a specified entropy source provider.
     * <p>
     * <b>Note:</b> If this constructor is used any calls to setSeed() in the resulting SecureRandom will be ignored.
     * </p>
     * @param pEntropy the provider of entropy
     */
    public SP800SecureRandomBuilder(final EntropySourceProvider pEntropy) {
        theRandom = null;
        theEntropyProvider = pEntropy;
    }

    /**
     * Set the personalisation string for DRBG SecureRandoms created by this builder.
     * @param pSecurityBytes the personalisation string for the underlying DRBG.
     */
    public void setSecurityBytes(final byte[] pSecurityBytes) {
        theSecurityBytes = (pSecurityBytes == null)
                ? null
                : Arrays.copyOf(pSecurityBytes, pSecurityBytes.length);
    }

    /**
     * Build a SecureRandom based on a SP 800-90A Hash DRBG.
     * @param pDigest digest to use in the DRBG underneath the SecureRandom.
     * @param pInitVector nonce value to use in DRBG construction.
     * @param isPredictionResistant specify whether the underlying DRBG in the resulting SecureRandom should reseed on each request for bytes.
     * @return a SecureRandom supported by a Hash DRBG.
     */
    protected SP800SecureRandom buildHash(final DataDigest pDigest,
                                          final byte[] pInitVector,
                                          final boolean isPredictionResistant) {
        EntropySource myEntropy = theEntropyProvider.get(NUM_ENTROPY_BITS_REQUIRED);
        HashSP800DRBG myProvider = new HashSP800DRBG(pDigest, myEntropy, theSecurityBytes, pInitVector);
        return new SP800SecureRandom(myProvider, myEntropy, isPredictionResistant);
    }

    /**
     * Build a SecureRandom based on a SP 800-90A HMAC DRBG.
     * @param hMac HMAC algorithm to use in the DRBG underneath the SecureRandom.
     * @param pInitVector nonce value to use in DRBG construction.
     * @param isPredictionResistant specify whether the underlying DRBG in the resulting SecureRandom should reseed on each request for bytes.
     * @return a SecureRandom supported by a HMAC DRBG.
     */
    protected SP800SecureRandom buildHMAC(final DataMac hMac,
                                          final byte[] pInitVector,
                                          final boolean isPredictionResistant) {
        EntropySource myEntropy = theEntropyProvider.get(NUM_ENTROPY_BITS_REQUIRED);
        HMacSP800DRBG myProvider = new HMacSP800DRBG(hMac, myEntropy, theSecurityBytes, pInitVector);
        return new SP800SecureRandom(myProvider, myEntropy, isPredictionResistant);
    }

    /**
     * SecureRandom wrapper class.
     */
    protected final class SP800SecureRandom
            extends SecureRandom {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 781744191004794480L;

        /**
         * The DRBG generator.
         */
        private final SP80090DRBG theGenerator;

        /**
         * The DRBG provider.
         */
        private final EntropySource theEntropy;

        /**
         * Is this instance prediction resistant?
         */
        private final boolean predictionResistant;

        /**
         * Constructor.
         * @param pGenerator the random generator
         * @param pEntropy the entropy source
         * @param isPredictionResistant true/false
         */
        private SP800SecureRandom(final SP80090DRBG pGenerator,
                                  final EntropySource pEntropy,
                                  final boolean isPredictionResistant) {
            /* Store parameters */
            theGenerator = pGenerator;
            theEntropy = pEntropy;
            predictionResistant = isPredictionResistant;
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
                    theGenerator.reseed(theEntropy.getEntropy());
                    theGenerator.generate(bytes, null, predictionResistant);
                }
            }
        }

        @Override
        public byte[] generateSeed(final int numBytes) {
            /* Generate a new seed */
            byte[] bytes = new byte[numBytes];
            nextBytes(bytes);
            return bytes;
        }
    }
}
