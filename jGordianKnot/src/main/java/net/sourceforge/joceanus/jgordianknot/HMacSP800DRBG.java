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
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot;

import java.security.InvalidKeyException;

import javax.crypto.Mac;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

import net.sourceforge.joceanus.jdatamanager.DataConverter;

import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.drbg.SP80090DRBG;
import org.bouncycastle.util.Arrays;

/**
 * Implementation of HMacSP800DRBG based on the BouncyCastle Code.
 * <p>
 * This implementation is modified so that it accepts any JCE HMac.
 */
public class HMacSP800DRBG
        implements SP80090DRBG {
    /**
     * The bit shift.
     */
    private static final int BIT_SHIFT = 3;

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
    private final Mac theHMac;

    /**
     * The HMac algorithm .
     */
    private final String theAlgo;

    /**
     * The Entropy Source.
     */
    private final EntropySource theEntropy;

    /**
     * The ReSeed Counter.
     */
    private ByteArrayInteger theReseedCounter;

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
    protected HMacSP800DRBG(final Mac pHMac,
                            final EntropySource pEntropy,
                            final byte[] pSecurityBytes,
                            final byte[] pInitVector) {
        /* Store hMac and entropy source */
        theHMac = pHMac;
        theEntropy = pEntropy;
        theAlgo = theHMac.getAlgorithm();

        /* Create Seed Material */
        byte[] myEntropy = theEntropy.getEntropy();
        byte[] mySeed = Arrays.concatenate(myEntropy, pInitVector, pSecurityBytes);

        /* Initialise buffers */
        int myLen = theHMac.getMacLength();
        theKey = new byte[myLen];
        theHash = new byte[myLen];
        Arrays.fill(theHash, (byte) 1);

        /* Update the state */
        updateState(mySeed);

        /* Initialise reSeed counter */
        theReseedCounter = new ByteArrayInteger(DataConverter.BYTES_LONG);
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
        } catch (ShortBufferException e) {
            throw new IllegalStateException(e);
        } catch (InvalidKeyException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Update the state.
     * @param pSeed optional seed material
     * @param pCycle the cycle id
     * @throws InvalidKeyException on invalid key
     * @throws ShortBufferException on invalid buffer
     */
    private void updateState(final byte[] pSeed,
                             final byte[] pCycle) throws InvalidKeyException, ShortBufferException {

        /* Initialise the hMac */
        theHMac.init(new SecretKeySpec(theKey, theAlgo));

        /* Update with hash and cycle id */
        theHMac.update(theHash);
        theHMac.update(pCycle);

        /* Add any seed material */
        if (pSeed != null) {
            theHMac.update(pSeed);
        }

        /* Generate new key */
        theHMac.doFinal(theKey, 0);

        /* Calculate new hash */
        theHMac.init(new SecretKeySpec(theKey, theAlgo));
        theHMac.update(theHash);
        theHMac.doFinal(theHash, 0);
    }

    @Override
    public void reseed(final byte[] pXtraBytes) {
        /* Create seed material */
        byte[] myEntropy = theEntropy.getEntropy();
        byte[] mySeed = Arrays.concatenate(myEntropy, pXtraBytes);

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
        int myLen = pOutput.length;
        int myNumBits = myLen << BIT_SHIFT;
        if (myNumBits > SP800SecureRandomBuilder.MAX_BITS_REQUEST) {
            throw new IllegalArgumentException("Number of bits per request limited to "
                                               + SP800SecureRandomBuilder.MAX_BITS_REQUEST);
        }

        /* Access XtraBytes */
        byte[] myXtraBytes = pXtraBytes;

        /* Check for reSeed required */
        if (theReseedCounter.compareLimit(SP800SecureRandomBuilder.RESEED_MAX)) {
            return -1;
        }

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
        byte[] myResult = new byte[myLen];

        /* Protect against exceptions */
        try {
            /* Initialise the hMac */
            theHMac.init(new SecretKeySpec(theKey, theAlgo));
            int mySize = theHMac.getMacLength();

            /* while we need to generate more bytes */
            int myBuilt = 0;
            while (myBuilt < myLen) {
                /* Update the mac */
                theHMac.update(theHash);
                theHMac.doFinal(theHash, 0);

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
        } catch (ShortBufferException e) {
            throw new IllegalStateException(e);
        } catch (InvalidKeyException e) {
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
}
