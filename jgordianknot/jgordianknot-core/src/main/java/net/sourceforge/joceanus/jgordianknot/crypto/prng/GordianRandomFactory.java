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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.bouncycastle.crypto.prng.BasicEntropySourceProvider;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropySourceProvider;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMac;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianRandomSpec;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Builder class for SP800 DRBG SecureRandom instances, based on the BouncyCastle Code.
 */
public final class GordianRandomFactory {
    /**
     * The number of entropy bits required.
     */
    private static final int NUM_ENTROPY_BITS_REQUIRED = 256;

    /**
     * The number of entropy bits required.
     */
    private static final int NUM_ENTROPY_BYTES_REQUIRED = NUM_ENTROPY_BITS_REQUIRED / Byte.SIZE;

    /**
     * The power of 2 for RESEED calculation.
     */
    private static final int RESEED_POWER = 48;

    /**
     * The length of time before a reSeed is required.
     */
    static final long RESEED_MAX = 1L << (RESEED_POWER - 1);

    /**
     * The power of 2 for BITS calculation.
     */
    private static final int BITS_POWER = 19;

    /**
     * The maximum # of bits that can be requested.
     */
    static final int MAX_BITS_REQUEST = 1 << (BITS_POWER - 1);

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
     * Basic constructor, creates a builder using an EntropySourceProvider based on the default
     * SecureRandom with predictionResistant set to false.
     * <p>
     * Any SecureRandom created from a builder constructed like this will make use of input passed
     * to SecureRandom.setSeed() if the default SecureRandom does for its generateSeed() call.
     * </p>
     * @throws OceanusException on error
     */
    public GordianRandomFactory() throws OceanusException {
        this(null, false);
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
     * @throws OceanusException on error
     */
    public GordianRandomFactory(final SecureRandom pEntropySource,
                                final boolean isPredictionResistant) throws OceanusException {
        /* If the EntropySource is null use StrongRandom */
        theRandom = pEntropySource == null
                                           ? getStrongRandom()
                                           : pEntropySource;

        /* Store parameters and create an entropy provider */
        theEntropyProvider = new BasicEntropySourceProvider(theRandom, isPredictionResistant);
    }

    /**
     * Access the Secure Random.
     * @return the secure random
     */
    SecureRandom getRandom() {
        return theRandom;
    }

    /**
     * Create a random randomSpec.
     * @param pFactory the factory
     * @return the randomSpec
     */
    public GordianRandomSpec generateRandomSpec(final GordianFactory pFactory) {
        return pFactory.generateRandomSpec(theRandom);
    }

    /**
     * Access the strong Secure Random.
     * @return the secure random
     * @throws OceanusException on error
     */
    private static SecureRandom getStrongRandom() throws OceanusException {
        synchronized (GordianRandomFactory.class) {
            /* If we have not yet created the strong entropy */
            if (theStrongEntropy == null) {
                /* Protect against exceptions */
                try {
                    /* Handle differently for Windows and *nix */
                    final boolean isWindows = System.getProperty("os.name").startsWith("Windows");
                    theStrongEntropy = isWindows
                                                 ? SecureRandom.getInstanceStrong()
                                                 : SecureRandom.getInstance("NativePRNGNonBlocking");

                    /* Seed the Entropy */
                    theStrongEntropy.setSeed(createPersonalisation(null));

                } catch (NoSuchAlgorithmException e) {
                    throw new GordianCryptoException("No strong random", e);
                }
            }
            return theStrongEntropy;
        }
    }

    /**
     * Create a personalisation Vector.
     * @return initVector.
     */
    private byte[] defaultPersonalisation() {
        /* Obtain some underlying entropy */
        final byte[] mySeed = new byte[NUM_ENTROPY_BYTES_REQUIRED];
        theRandom.nextBytes(mySeed);

        /* Create the personalisation */
        return createPersonalisation(mySeed);
    }

    /**
     * Create a personalisation Vector.
     * @param pSeed the seed (or null)
     * @return initVector.
     */
    private static byte[] createPersonalisation(final byte[] pSeed) {
        /* Create the source arrays */
        final byte[] myThread = TethysDataConverter.longToByteArray(Thread.currentThread().getId());
        final byte[] myTime = TethysDataConverter.longToByteArray(System.currentTimeMillis());

        /* Create the final initVector */
        int myLen = myThread.length + myTime.length;
        if (pSeed != null) {
            myLen += pSeed.length;
        }
        final byte[] myVector = new byte[myLen];

        /* Build the vector */
        System.arraycopy(myThread, 0, myVector, 0, myThread.length);
        System.arraycopy(myTime, 0, myVector, myThread.length, myTime.length);
        if (pSeed != null) {
            System.arraycopy(pSeed, 0, myVector, 0, pSeed.length);
        }

        /* return it */
        return myVector;
    }

    /**
     * Build a SecureRandom based on a SP 800-90A Hash DRBG.
     * @param pDigest digest to use in the DRBG underneath the SecureRandom.
     * @param isPredictionResistant specify whether the underlying DRBG in the resulting
     * SecureRandom should re-seed on each request for bytes.
     * @return a SecureRandom supported by a Hash DRBG.
     */
    public GordianBaseSecureRandom buildHash(final GordianDigest pDigest,
                                         final boolean isPredictionResistant) {
        return buildHash(pDigest, null, isPredictionResistant);
    }

    /**
     * Build a SecureRandom based on a SP 800-90A Hash DRBG.
     * @param pDigest digest to use in the DRBG underneath the SecureRandom.
     * @param pInitVector nonce value to use in DRBG construction.
     * @param isPredictionResistant specify whether the underlying DRBG in the resulting
     * SecureRandom should re-seed on each request for bytes.
     * @return a SecureRandom supported by a Hash DRBG.
     */
    public GordianBaseSecureRandom buildHash(final GordianDigest pDigest,
                                         final byte[] pInitVector,
                                         final boolean isPredictionResistant) {
        /* Create initVector if required */
        final byte[] myInit = pInitVector == null
                                                  ? theRandom.generateSeed(NUM_ENTROPY_BYTES_REQUIRED)
                                                  : pInitVector;

        /* Build DRBG */
        final EntropySource myEntropy = theEntropyProvider.get(NUM_ENTROPY_BITS_REQUIRED);
        final GordianSP800HashDRBG myProvider = new GordianSP800HashDRBG(pDigest, myEntropy, defaultPersonalisation(), myInit);
        return new GordianBaseSecureRandom(myProvider, theRandom, myEntropy, isPredictionResistant);
    }

    /**
     * Build a SecureRandom based on a SP 800-90A HMAC DRBG.
     * @param hMac HMAC algorithm to use in the DRBG underneath the SecureRandom.
     * @param isPredictionResistant specify whether the underlying DRBG in the resulting
     * SecureRandom should re-seed on each request for bytes.
     * @return a SecureRandom supported by a HMAC DRBG.
     */
    public GordianBaseSecureRandom buildHMAC(final GordianMac hMac,
                                         final boolean isPredictionResistant) {
        return buildHMAC(hMac, null, isPredictionResistant);
    }

    /**
     * Build a SecureRandom based on a SP 800-90A HMAC DRBG.
     * @param hMac HMAC algorithm to use in the DRBG underneath the SecureRandom.
     * @param pInitVector nonce value to use in DRBG construction.
     * @param isPredictionResistant specify whether the underlying DRBG in the resulting
     * SecureRandom should re-seed on each request for bytes.
     * @return a SecureRandom supported by a HMAC DRBG.
     */
    public GordianBaseSecureRandom buildHMAC(final GordianMac hMac,
                                         final byte[] pInitVector,
                                         final boolean isPredictionResistant) {
        /* Create initVector if required */
        final byte[] myInit = pInitVector == null
                                                  ? theRandom.generateSeed(NUM_ENTROPY_BYTES_REQUIRED)
                                                  : pInitVector;

        /* Build DRBG */
        final EntropySource myEntropy = theEntropyProvider.get(NUM_ENTROPY_BITS_REQUIRED);
        final GordianSP800HMacDRBG myProvider = new GordianSP800HMacDRBG(hMac, myEntropy, defaultPersonalisation(), myInit);
        return new GordianBaseSecureRandom(myProvider, theRandom, myEntropy, isPredictionResistant);
    }
}
