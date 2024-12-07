/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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

import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.kdf.GordianHKDFEngine;
import net.sourceforge.joceanus.gordianknot.impl.core.kdf.GordianHKDFParams;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Personalisation.
 */
public class GordianPersonalisation {
    /**
     * The Base personalisation.
     */
    private static final byte[] BASE_PERSONAL = "G0rd1anKn0t".getBytes(StandardCharsets.UTF_8);

    /**
     * Number of iterations.
     */
    public static final Integer NUM_ITERATIONS = 128;

    /**
     * Phrase multiplier.
     */
    public static final Integer PHRASE_SHIFT = 4;

    /**
     * The hash length.
     */
    private static final GordianLength HASH_LEN = GordianLength.LEN_256;

    /**
     * The result length.
     */
    private static final int RESULT_LEN = GordianLength.LEN_512.getByteLength();

    /**
     * The # of results.
     */
    private static final int NUM_RESULTS = 3;

    /**
     * Personalisation bytes.
     */
    private final byte[] thePersonalisation;

    /**
     * InitVector bytes.
     */
    private final byte[] theInitVector;

    /**
     * InitVector bytes.
     */
    private final byte[] theKeySetVector;

    /**
     * Constructor.
     * @param pFactory the factory
     * @throws OceanusException on error
     */
    GordianPersonalisation(final GordianCoreFactory pFactory) throws OceanusException {
        /* Calculate personalisation bytes */
        final byte[][] myArrays = personalise(pFactory);
        thePersonalisation = myArrays[0];
        theInitVector = myArrays[1];
        theKeySetVector = myArrays[2];
    }

    /**
     * Obtain an array of digests for personalisation.
     * @param pFactory the factory
     * @return the digests
     * @throws OceanusException on error
     */
    private static GordianDigest[] determineDigests(final GordianFactory pFactory) throws OceanusException {
        /* Access digest factory */
        final GordianDigestFactory myFactory = pFactory.getDigestFactory();

        /* Initialise variables */
        final GordianDigestType[] myTypes = GordianDigestType.values();
        final GordianDigest[] myDigests = new GordianDigest[myTypes.length];
        int myLen = 0;

        /* Loop through the digestTypes */
        for (final GordianDigestType myType : GordianDigestType.values()) {
            /* Add the digest if it is available as 256-bit and supports largeData */
            final GordianDigestSpec mySpec = new GordianDigestSpec(myType, HASH_LEN);
            if (myType.supportsLargeData()
                    && myFactory.supportedDigestSpecs().test(mySpec)) {
                myDigests[myLen++] = myFactory.createDigest(mySpec);
            }
        }

        /* Return the array */
        return Arrays.copyOf(myDigests, myLen);
    }

    /**
     * Obtain the keySetVector.
     * @return the keySetVector
     */
    byte[] getKeySetVector() {
        return theKeySetVector;
    }

    /**
     * determine hostName.
     * @return the hostName
     */
    private static String getHostName() {
        /* Protect against exceptions */
        try {
            final InetAddress myAddr = InetAddress.getLocalHost();
            return myAddr.getHostName();

        } catch (UnknownHostException e) {
            return "localhost";
        }
    }

    /**
     * Create an array of hashes from personalisation.
     * @param pFactory the factory
     * @return the hashes
     * @throws OceanusException on error
     */
    private static byte[][] personalise(final GordianCoreFactory pFactory) throws OceanusException {
        /* Determine the digests */
        final GordianDigest[] myDigests = determineDigests(pFactory);

        /* Allocate buffers */
        final byte[][] myHashes = new byte[myDigests.length][];
        final byte[] myConfig = new byte[HASH_LEN.getByteLength()];
        byte[] myExpanded = null;
        GordianHKDFParams myParams = null;
        final byte[] myKeySetVec = pFactory.getKeySetSeed();

        /* Obtain configuration */
        byte[] myPhraseBytes = pFactory.getSecuritySeed();
        if (myPhraseBytes == null) {
            myPhraseBytes = GordianDataConverter.stringToByteArray(getHostName());
        }

        /* Protect against exceptions */
        try {
            /* Initialise hashes */
            for (int i = 0; i < myDigests.length; i++) {
                /* Initialise the digests */
                final GordianDigest myDigest = myDigests[i];
                myDigest.update(BASE_PERSONAL);
                myDigest.update(myPhraseBytes);

                /* Finish the update and store the buffer */
                final byte[] myResult = myDigest.finish();
                buildHashResult(myConfig, myResult);
                myHashes[i] = myResult;
            }

            /* Determine the number of iterations */
            final int myIterations = pFactory.isInternal()
                    ? NUM_ITERATIONS
                    : NUM_ITERATIONS << PHRASE_SHIFT;

            /* Loop the required amount of times to cross-fertilise */
            for (int i = 0; i < myIterations; i++) {
                iterateHashes(myDigests, myHashes, myConfig);
            }

            /* Determine the number of results that we want from HKDF */
            final int numResults = myKeySetVec != null ? NUM_RESULTS - 1 : NUM_RESULTS;

            /* Use HKDF to expand to the required length */
            final GordianDigestSpec mySpec = determineHKDFDigestSpec(pFactory, myConfig);
            final GordianHKDFEngine myEngine = new GordianHKDFEngine(pFactory, mySpec);
            myParams = GordianHKDFParams.expandOnly(myConfig, RESULT_LEN * numResults).withInfo(BASE_PERSONAL);
            myExpanded = myEngine.deriveBytes(myParams);

            /* Extract the results */
            final byte[][] myResults = new byte[NUM_RESULTS][];
            myResults[0] = new byte[RESULT_LEN];
            System.arraycopy(myExpanded, 0, myResults[0], 0, RESULT_LEN);
            myResults[1] = new byte[RESULT_LEN];
            System.arraycopy(myExpanded, RESULT_LEN, myResults[1], 0, RESULT_LEN);

            /* Handle the keySetVector */
            if (myKeySetVec != null) {
                myResults[2] = myKeySetVec;
            } else {
                myResults[2] = new byte[RESULT_LEN];
                System.arraycopy(myExpanded, RESULT_LEN << 1, myResults[2], 0, RESULT_LEN);
            }

            /* Return the array */
            return myResults;

            /* Clear intermediate arrays */
        } finally {
            /* Clear intermediate hashes */
            for (int i = 0; i < myDigests.length; i++) {
                if (myHashes[i] != null) {
                    Arrays.fill(myHashes[i], (byte) 0);
                }
            }

            /* Clear intermediate result */
            Arrays.fill(myConfig, (byte) 0);

            /* Clear temporary output buffer */
            if (myExpanded != null) {
                Arrays.fill(myExpanded, (byte) 0);
            }

            /* Clear HKDF parameters */
            if (myParams != null) {
                myParams.clearParameters();
            }
        }
    }

    /**
     * Iterate the hashes.
     * @param pDigests the digest array
     * @param pHashes the hashes array
     * @param pResult the result array
     * @throws OceanusException on error
     */
    private static void iterateHashes(final GordianDigest[] pDigests,
                                      final byte[][] pHashes,
                                      final byte[] pResult) throws OceanusException {
        /* Update all the digests */
        for (final GordianDigest myDigest : pDigests) {
            /* Update with the results */
            for (int k = 0; k < pDigests.length; k++) {
                myDigest.update(pHashes[k]);
            }
        }

        /* Finish all the digests */
        for (int j = 0; j < pDigests.length; j++) {
            /* Update with the results */
            final GordianDigest myDigest = pDigests[j];
            final byte[] myResult = pHashes[j];
            myDigest.finish(myResult, 0);
            buildHashResult(pResult, myResult);
        }
    }

    /**
     * Adjust an IV.
     * @param pIV the input IV
     * @return the adjusted IV
     */
    public byte[] adjustIV(final byte[] pIV) {
        return combineHashes(pIV, theInitVector);
    }

    /**
     * Update a MAC with personalisation.
     * @param pMac the MAC
     */
    public void updateMac(final GordianMac pMac) {
        pMac.update(thePersonalisation);
        pMac.update(theInitVector);
    }

    /**
     * Update HKDFInfo with personalisation.
     * @param pParams the params
     */
    public void updateInfo(final GordianHKDFParams pParams) {
        pParams.withInfo(BASE_PERSONAL);
        pParams.withInfo(thePersonalisation);
        pParams.withInfo(theInitVector);
    }

    /**
     * Obtain integer from personalisation.
     * @param pId the id of the integer
     * @return the result
     */
    public int getPersonalisedInteger(final GordianPersonalId pId) {
        return getPersonalisedMask(getOffsetForId(pId));
    }

    /**
     * Determine offset for Id.
     * @param pId the id of the value
     * @return the offset
     */
    private static int getOffsetForId(final GordianPersonalId pId) {
        return pId.ordinal() << 2;
    }

    /**
     * Obtain mask from personalisation.
     * @param pOffSet the offset within the array
     * @return the result
     */
    private int getPersonalisedMask(final int pOffSet) {
        /* Loop to obtain the personalised byte */
        int myVal = 0;
        for (int i = 0, myOffSet = pOffSet; i < Integer.BYTES; i++, myOffSet++) {
            myVal <<= Byte.SIZE;
            myVal |= thePersonalisation[myOffSet] & GordianDataConverter.BYTE_MASK;
        }

        /* Return the value */
        return myVal;
    }

    /**
     * Obtain seeded random.
     * @param pPrefixId the prefixId
     * @param pBaseSeed the seed.
     * @return the random
     */
    public Random getSeededRandom(final GordianPersonalId pPrefixId,
                                  final byte[] pBaseSeed) {
        /* Build the 48-bit seed and return the seeded random */
        final long myPrefix = ((long) getPersonalisedInteger(pPrefixId)) << Short.SIZE;
        final long myBaseSeed = Integer.toUnsignedLong(GordianDataConverter.byteArrayToInteger(pBaseSeed));
        final long mySeed = myPrefix ^ myBaseSeed;
        return new Random(mySeed);
    }

    /**
     * Detremine the HKDFDigestSpec.
     * @param pFactory the factory
     * @param pBaseSeed the seed.
     * @return the digestSpec
     */
    private static GordianDigestSpec determineHKDFDigestSpec(final GordianFactory pFactory,
                                                             final byte[] pBaseSeed) {
        /* Build the 64-bit seed and create the seeded random */
        final long mySeed = GordianDataConverter.byteArrayToLong(pBaseSeed);
        final Random myRandom = new Random(mySeed);

        /* Access the list to select from */
        final GordianValidator myValidator = ((GordianCoreFactory) pFactory).getValidator();
        final List<GordianDigestType> myTypes = myValidator.listAllExternalDigestTypes();

        /* Select from the list */
        final int myIndex = myRandom.nextInt(myTypes.size());
        final GordianDigestType myType = myTypes.get(myIndex);

        /* return the selected digestSpec */
        return new GordianDigestSpec(myType, GordianLength.LEN_512);
    }

    /**
     * Simple function to combine hashes. Hashes are simply XORed together.
     * @param pFirst the first Hash
     * @param pSecond the second Hash
     * @return the combined hash
     */
    public static byte[] combineHashes(final byte[] pFirst,
                                       final byte[] pSecond) {
        /* Handle nulls */
        if (pFirst == null) {
            return pSecond;
        }
        if (pSecond == null) {
            return pFirst;
        }

        /* If the target is smaller than the source */
        byte[] myTarget = pSecond;
        byte[] mySource = pFirst;
        if (myTarget.length < mySource.length) {
            /* Reverse the order to make use of all bits */
            myTarget = pFirst;
            mySource = pSecond;
        }

        /* Allocate the target as a copy of the input */
        myTarget = Arrays.copyOf(myTarget, myTarget.length);

        /* Determine length of operation */
        final int myLen = mySource.length;

        /* Loop through the array bytes */
        for (int i = 0; i < myTarget.length; i++) {
            /* Combine the bytes */
            myTarget[i] ^= mySource[i
                    % myLen];
        }

        /* return the array */
        return myTarget;
    }

    /**
     * Simple function to build a hash result.
     * @param pResult the result Hash
     * @param pHash the calculated Hash
     * @throws OceanusException on error
     */
    public static void buildHashResult(final byte[] pResult,
                                       final byte[] pHash) throws OceanusException {
        /* If the target is smaller than the source */
        final int myLen = pResult.length;
        if (myLen != pHash.length) {
            throw new GordianDataException("Hashes are different lengths");
        }
        /* Loop through the array bytes */
        for (int i = 0; i < myLen; i++) {
            /* Combine the bytes */
            pResult[i] ^= pHash[i];
        }
    }

    /**
     * Personalisation IDs.
     */
    public enum GordianPersonalId {
        /**
         * KeySetGenRandom Prefix.
         */
        KEYSETGENRANDOM,

        /**
         * KeySetRandom Prefix.
         */
        KEYSETRANDOM,

        /**
         * LockRandom Prefix.
         */
        LOCKRANDOM,

        /**
         * KnuthPrime.
         */
        KNUTHPRIME,

        /**
         * KnuthMask.
         */
        KNUTHMASK
    }
}
