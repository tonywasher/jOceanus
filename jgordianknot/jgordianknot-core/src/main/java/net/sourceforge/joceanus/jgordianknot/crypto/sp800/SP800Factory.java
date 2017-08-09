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
package net.sourceforge.joceanus.jgordianknot.crypto.sp800;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import org.bouncycastle.crypto.prng.BasicEntropySourceProvider;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropySourceProvider;
import org.bouncycastle.crypto.prng.drbg.SP80090DRBG;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMac;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Builder class for SP800 DRBG SecureRandom instances, based on the BouncyCastle Code.
 */
public final class SP800Factory {
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
     * The Entropy Secure Random instance.
     */
    private static SecureRandom theStrongEntropy;

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
    private byte[] theSecurityBytes;

    /**
     * Basic constructor, creates a builder using an EntropySourceProvider based on the default
     * SecureRandom with predictionResistant set to false.
     * <p>
     * Any SecureRandom created from a builder constructed like this will make use of input passed
     * to SecureRandom.setSeed() if the default SecureRandom does for its generateSeed() call.
     * </p>
     * @throws OceanusException on error
     */
    public SP800Factory() throws OceanusException {
        this(getStrongRandom(), false);
    }

    /**
     * Construct a builder with an EntropySourceProvider based on the passed in SecureRandom and the
     * passed in value for prediction resistance.
     * <p>
     * Any SecureRandom created from a builder constructed like this will make use of input passed
     * to SecureRandom.setSeed() if the passed in SecureRandom does for its generateSeed() call.
     * </p>
     * @param pEntropySource the entropy source
     * @param isPredictionResistant is the random generator to be prediction resistant?
     */
    public SP800Factory(final SecureRandom pEntropySource,
                        final boolean isPredictionResistant) {
        /* Store parameters and create an entropy provider */
        theRandom = pEntropySource;
        theEntropyProvider = new BasicEntropySourceProvider(theRandom, isPredictionResistant);
    }

    /**
     * Create a builder which makes creates the SecureRandom objects from a specified entropy source
     * provider.
     * <p>
     * <b>Note:</b> If this constructor is used any calls to setSeed() in the resulting SecureRandom
     * will be ignored.
     * </p>
     * @param pEntropy the provider of entropy
     */
    public SP800Factory(final EntropySourceProvider pEntropy) {
        theRandom = null;
        theEntropyProvider = pEntropy;
    }

    /**
     * Access the Secure Random.
     * @return the secure random
     */
    protected SecureRandom getRandom() {
        return theRandom;
    }

    /**
     * Access the strong Secure Random.
     * @return the secure random
     * @throws OceanusException on error
     */
    private static synchronized SecureRandom getStrongRandom() throws OceanusException {
        /* If we have not yet created the string entropy */
        if (theStrongEntropy == null) {
            /* Protect against exceptions */
            try {
                /* Handle differently for Windows and *nix */
                final boolean isWindows = System.getProperty("os.name").startsWith("Windows");
                theStrongEntropy = isWindows
                                             ? SecureRandom.getInstanceStrong()
                                             : SecureRandom.getInstance("NativePRNGNonBlocking");

                /* Seed the Entropy */
                theStrongEntropy.nextBoolean();

            } catch (NoSuchAlgorithmException e) {
                throw new GordianCryptoException("No strong random", e);
            }
        }
        return theStrongEntropy;
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
     * Create a default initVector.
     * @return initVector.
     */
    private static byte[] defaultInitVector() {
        return TethysDataConverter.longToByteArray(System.currentTimeMillis());
    }

    /**
     * Build a SecureRandom based on a SP 800-90A Hash DRBG.
     * @param pDigest digest to use in the DRBG underneath the SecureRandom.
     * @param isPredictionResistant specify whether the underlying DRBG in the resulting
     * SecureRandom should re-seed on each request for bytes.
     * @return a SecureRandom supported by a Hash DRBG.
     */
    public SP800SecureRandom buildHash(final GordianDigest pDigest,
                                       final boolean isPredictionResistant) {
        return buildHash(pDigest, defaultInitVector(), isPredictionResistant);
    }

    /**
     * Build a SecureRandom based on a SP 800-90A Hash DRBG.
     * @param pDigest digest to use in the DRBG underneath the SecureRandom.
     * @param pInitVector nonce value to use in DRBG construction.
     * @param isPredictionResistant specify whether the underlying DRBG in the resulting
     * SecureRandom should re-seed on each request for bytes.
     * @return a SecureRandom supported by a Hash DRBG.
     */
    public SP800SecureRandom buildHash(final GordianDigest pDigest,
                                       final byte[] pInitVector,
                                       final boolean isPredictionResistant) {
        /* Build DRBG */
        final EntropySource myEntropy = theEntropyProvider.get(NUM_ENTROPY_BITS_REQUIRED);
        final SP800HashDRBG myProvider = new SP800HashDRBG(pDigest, myEntropy, theSecurityBytes, pInitVector);
        return new SP800SecureRandom(myProvider, theRandom, myEntropy, isPredictionResistant);
    }

    /**
     * Build a SecureRandom based on a SP 800-90A HMAC DRBG.
     * @param hMac HMAC algorithm to use in the DRBG underneath the SecureRandom.
     * @param isPredictionResistant specify whether the underlying DRBG in the resulting
     * SecureRandom should re-seed on each request for bytes.
     * @return a SecureRandom supported by a HMAC DRBG.
     */
    public SP800SecureRandom buildHMAC(final GordianMac hMac,
                                       final boolean isPredictionResistant) {
        return buildHMAC(hMac, defaultInitVector(), isPredictionResistant);
    }

    /**
     * Build a SecureRandom based on a SP 800-90A HMAC DRBG.
     * @param hMac HMAC algorithm to use in the DRBG underneath the SecureRandom.
     * @param pInitVector nonce value to use in DRBG construction.
     * @param isPredictionResistant specify whether the underlying DRBG in the resulting
     * SecureRandom should re-seed on each request for bytes.
     * @return a SecureRandom supported by a HMAC DRBG.
     */
    public SP800SecureRandom buildHMAC(final GordianMac hMac,
                                       final byte[] pInitVector,
                                       final boolean isPredictionResistant) {
        /* Create initVector if required */
        final byte[] myInit = pInitVector == null
                                                  ? TethysDataConverter.longToByteArray(System.currentTimeMillis())
                                                  : pInitVector;

        /* Build DRBG */
        final EntropySource myEntropy = theEntropyProvider.get(NUM_ENTROPY_BITS_REQUIRED);
        final SP800HMacDRBG myProvider = new SP800HMacDRBG(hMac, myEntropy, theSecurityBytes, myInit);
        return new SP800SecureRandom(myProvider, theRandom, myEntropy, isPredictionResistant);
    }

    /**
     * SecureRandom wrapper class.
     */
    protected static final class SP800SecureRandom
            extends
            SecureRandom {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 781744191004794480L;

        /**
         * The Basic Secure Random instance.
         */
        private final SecureRandom theRandom;

        /**
         * The DRBG generator.
         */
        private final transient SP80090DRBG theGenerator;

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
        private SP800SecureRandom(final SP80090DRBG pGenerator,
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
            final byte[] bytes = new byte[numBytes];
            nextBytes(bytes);
            return bytes;
        }
    }
}
