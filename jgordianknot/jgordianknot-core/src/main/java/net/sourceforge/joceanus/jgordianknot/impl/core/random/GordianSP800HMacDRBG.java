/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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

import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianByteArrayInteger;
import net.sourceforge.joceanus.jgordianknot.impl.core.mac.GordianCoreMac;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Implementation of HMacSP800DRBG based on the BouncyCastle Code.
 * <p>
 * This implementation is modified so that it accepts any GordianMac.
 */
public final class GordianSP800HMacDRBG
        implements GordianDRBGenerator {
    /**
     * The Update Id.
     */
    private static final byte[] UPDATE_ID = { 0 };

    /**
     * The ReSeed Id.
     */
    private static final byte[] SEED_ID = { 1 };

    /**
     * The HMac.
     */
    private final GordianCoreMac theHMac;

    /**
     * The Entropy Source.
     */
    private final EntropySource theEntropy;

    /**
     * The ReSeed Counter.
     */
    private final GordianByteArrayInteger theReseedCounter;

    /**
     * The Key.
     */
    private final byte[] theKey;

    /**
     * The Hash.
     */
    private final byte[] theHash;

    /**
     * Construct a SP800-90A Hash DRBG.
     * @param pHMac Hash MAC to base the DRBG on.
     * @param pEntropy source of entropy to use for seeding/reSeeding.
     * @param pSecurityBytes personalisation string to distinguish this DRBG (may be null).
     * @param pInitVector nonce to further distinguish this DRBG (may be null).
     */
    public GordianSP800HMacDRBG(final GordianCoreMac pHMac,
                                final EntropySource pEntropy,
                                final byte[] pSecurityBytes,
                                final byte[] pInitVector) {
        /* Store hMac and entropy source */
        theHMac = pHMac;
        theEntropy = pEntropy;

        /* Create Seed Material */
        final byte[] myEntropy = theEntropy.getEntropy();
        final byte[] mySeed = Arrays.concatenate(myEntropy, pInitVector, pSecurityBytes);

        /* Initialise buffers */
        final int myLen = theHMac.getMacSize();
        theKey = new byte[myLen];
        theHash = new byte[myLen];
        Arrays.fill(theHash, (byte) 1);

        /* Update the state */
        updateState(mySeed);

        /* Initialise reSeed counter */
        theReseedCounter = new GordianByteArrayInteger(TethysDataConverter.BYTES_LONG);
        theReseedCounter.iterate();
    }

    /**
     * Update the state (HMAC_DRBG_Update).
     * @param pSeed the extra seed material
     */
    private void updateState(final byte[] pSeed) {
        try {
            updateState(pSeed, UPDATE_ID);
            if (pSeed != null) {
                updateState(pSeed, SEED_ID);
            }
        } catch (OceanusException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Update the state.
     * @param pSeed optional seed material
     * @param pCycle the cycle id
     * @throws OceanusException on error
     */
    private void updateState(final byte[] pSeed,
                             final byte[] pCycle) throws OceanusException {

        /* Initialise the hMac */
        theHMac.initKeyBytes(theKey);

        /* Update with hash and cycle id */
        theHMac.update(theHash);
        theHMac.update(pCycle);

        /* Add any seed material */
        if (pSeed != null) {
            theHMac.update(pSeed);
        }

        /* Generate new key */
        theHMac.finish(theKey, 0);

        /* Calculate new hash */
        theHMac.initKeyBytes(theKey);
        theHMac.update(theHash);
        theHMac.finish(theHash, 0);
    }

    @Override
    public void reseed(final byte[] pXtraBytes) {
        /* Create seed material */
        final byte[] myEntropy = theEntropy.getEntropy();
        final byte[] mySeed = Arrays.concatenate(myEntropy, pXtraBytes);

        /* Update the state */
        updateState(mySeed);

        /* re-initialise reSeed counter */
        theReseedCounter.reset();
        theReseedCounter.iterate();
    }

    @Override
    public int generate(final byte[] pOutput,
                        final byte[] pXtraBytes,
                        final boolean isPredictionResistant) {
        /* Check valid # of bits */
        final int myLen = pOutput.length;
        final int myNumBits = myLen << GordianCoreRandomFactory.BIT_SHIFT;
        if (myNumBits > GordianCoreRandomFactory.MAX_BITS_REQUEST) {
            throw new IllegalArgumentException("Number of bits per request limited to "
                    + GordianCoreRandomFactory.MAX_BITS_REQUEST);
        }

        /* Check for reSeed required */
        if (theReseedCounter.compareLimit(GordianCoreRandomFactory.RESEED_MAX)) {
            return -1;
        }

        /* Access XtraBytes */
        byte[] myXtraBytes = pXtraBytes;

        /* If we are prediction resistant */
        if (isPredictionResistant) {
            /* ReSeed and discard xtraBytes */
            reseed(myXtraBytes);
            myXtraBytes = null;

            /* else if we have extra bytes */
        } else if (myXtraBytes != null) {
            /* Update the state */
            updateState(myXtraBytes);
        }

        /* Allocate output buffer */
        final byte[] myResult = new byte[myLen];

        /* Protect against exceptions */
        try {
            /* Initialise the hMac */
            theHMac.initKeyBytes(theKey);
            final int mySize = theHMac.getMacSize();

            /* while we need to generate more bytes */
            int myBuilt = 0;
            while (myBuilt < myLen) {
                /* Update the mac */
                theHMac.update(theHash);
                theHMac.finish(theHash, 0);

                /* Determine how many bytes of this hash should be used */
                int myNeeded = myLen
                        - myBuilt;
                if (myNeeded > mySize) {
                    myNeeded = mySize;
                }

                /* Copy bytes across */
                System.arraycopy(theHash, 0, myResult, myBuilt, myNeeded);
                myBuilt += myNeeded;
            }
        } catch (OceanusException e) {
            throw new IllegalStateException(e);
        }

        /* Update the state */
        updateState(myXtraBytes);

        /* Iterate the reSeed counter */
        theReseedCounter.iterate();

        /* Return the bytes */
        System.arraycopy(myResult, 0, pOutput, 0, pOutput.length);

        /* Return the number of bits generated */
        return myNumBits;
    }

    @Override
    public int getBlockSize() {
        return theHMac.getMacSize();
    }

    @Override
    public String getAlgorithm() {
        return GordianCoreRandomFactory.SP800_PREFIX + theHMac.getMacSpec().toString();
    }
}
