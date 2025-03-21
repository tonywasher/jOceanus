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
package net.sourceforge.joceanus.gordianknot.impl.core.base;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianCryptoException;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * SecureRandom source.
 */
public class GordianRandomSource {
    /**
     * The number of entropy bits required.
     */
    private static final int NUM_ENTROPY_BITS_REQUIRED = 256;

    /**
     * The number of entropy bits required.
     */
    private static final int NUM_ENTROPY_BYTES_REQUIRED = NUM_ENTROPY_BITS_REQUIRED / Byte.SIZE;

    /**
     * The Initial strongRandom.
     */
    private static SecureRandom theStrongEntropy;

    /**
     * The random source.
     */
    private SecureRandom theRandom;

    /**
     * Constructor.
     * @throws GordianException on error
     */
    GordianRandomSource() throws GordianException {
        theRandom = getStrongRandom();
    }

    /**
     * Obtain the random.
     * @return the random
     */
    public SecureRandom getRandom() {
        return theRandom;
    }

    /**
     * Set the random.
     * @param pRandom the random
     */
    public void setRandom(final SecureRandom pRandom) {
        /* Seed the Random */
        pRandom.setSeed(defaultPersonalisation());

        /* Store the new random */
        theRandom = pRandom;
    }

    /**
     * Access the strong Secure Random.
     * @return the secure random
     * @throws GordianException on error
     */
    public static SecureRandom getStrongRandom() throws GordianException {
        /* Return the entropy if it has been created */
        SecureRandom myStrong = theStrongEntropy;
        if (myStrong != null) {
            return myStrong;
        }

        /* Synchronize the attempts */
        synchronized (GordianRandomSource.class) {
            /* If we have not yet created the strong entropy */
            myStrong = theStrongEntropy;
            if (myStrong == null) {
                /* Protect against exceptions */
                try {
                    /* Handle differently for Windows and *nix */
                    final boolean isWindows = System.getProperty("os.name").startsWith("Windows");
                    myStrong = isWindows
                                   ? SecureRandom.getInstanceStrong()
                                   : SecureRandom.getInstance("NativePRNGNonBlocking");

                    /* Seed the Entropy */
                    myStrong.setSeed(createPersonalisation(null));
                    theStrongEntropy = myStrong;

                } catch (NoSuchAlgorithmException e) {
                    throw new GordianCryptoException("No strong random", e);
                }
            }
            return myStrong;
        }
    }

    /**
     * Create a personalisation Vector.
     * @return initVector.
     */
    public byte[] defaultPersonalisation() {
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
        final byte[] myThread = GordianDataConverter.longToByteArray(Thread.currentThread().getId());
        final byte[] myTime = GordianDataConverter.longToByteArray(System.currentTimeMillis());
        final byte[] myNanos = GordianDataConverter.longToByteArray(System.nanoTime());

        /* Create the final initVector */
        int myLen = myThread.length + myTime.length + myNanos.length;
        if (pSeed != null) {
            myLen += pSeed.length;
        }
        final byte[] myVector = new byte[myLen];

        /* Build the vector */
        System.arraycopy(myThread, 0, myVector, 0, myThread.length);
        System.arraycopy(myTime, 0, myVector, myThread.length, myTime.length);
        System.arraycopy(myNanos, 0, myVector, myThread.length + myTime.length, myNanos.length);
        if (pSeed != null) {
            System.arraycopy(pSeed, 0, myVector, myThread.length + myTime.length + myNanos.length, pSeed.length);
        }

        /* return it */
        return myVector;
    }
}
