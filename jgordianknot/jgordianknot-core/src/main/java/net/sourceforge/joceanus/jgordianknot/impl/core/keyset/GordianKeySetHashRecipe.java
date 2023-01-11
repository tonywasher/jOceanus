/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.keyset;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIdManager;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianPersonalisation;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianPersonalisation.GordianPersonalId;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Class for assembling/disassembling KeySetHashes.
 */
public final class GordianKeySetHashRecipe {
    /**
     * Number of digests.
     */
    private static final int NUM_DIGESTS = 3;

    /**
     * Recipe length (Integer).
     */
    private static final int RECIPELEN = Integer.BYTES;

    /**
     * Salt length.
     */
    private static final int SALTLEN = GordianLength.LEN_256.getByteLength();

    /**
     * HashSize.
     */
    static final int HASHLEN = RECIPELEN + SALTLEN + GordianLength.LEN_512.getByteLength();

    /**
     * Hash margins.
     */
    private static final int HASH_MARGIN = 4;

    /**
     * The KeySetHashSpec.
     */
    private final GordianKeySetHashSpec theSpec;

    /**
     * The Recipe.
     */
    private final byte[] theRecipe;

    /**
     * The Salt.
     */
    private final byte[] theSalt;

    /**
     * The Initialisation Vector.
     */
    private final byte[] theInitVector;

    /**
     * The Hash.
     */
    private final byte[] theHash;

    /**
     * The Hash Parameters.
     */
    private final GordianHashParameters theParams;

    /**
     * Constructor for random choices.
     * @param pFactory the factory
     * @param pSpec the keySetHashSpec
     */
    GordianKeySetHashRecipe(final GordianCoreFactory pFactory,
                            final GordianKeySetHashSpec pSpec) {
        /* Access the secureRandom */
        final SecureRandom myRandom = pFactory.getRandomSource().getRandom();

        /* Create the Salt vector */
        theSalt = new byte[SALTLEN];
        myRandom.nextBytes(theSalt);

        /* Calculate the initVector */
        final GordianPersonalisation myPersonal = pFactory.getPersonalisation();
        theInitVector = myPersonal.adjustIV(theSalt);

        /* Allocate new set of parameters */
        theParams = new GordianHashParameters(pFactory);
        theRecipe = theParams.getRecipe();
        theSpec = pSpec;
        theHash = null;
    }

    /**
     * Constructor for external form parse.
     * @param pFactory the factory
     * @param pPassLength the password length
     * @param pExternal the external form
     * @throws OceanusException on error
     */
    GordianKeySetHashRecipe(final GordianCoreFactory pFactory,
                            final int pPassLength,
                            final byte[] pExternal) throws OceanusException  {
        /* Parse the ASN1 external form */
        final GordianKeySetHashASN1 myASN1 = GordianKeySetHashASN1.getInstance(pExternal);
        final byte[] myExternal = myASN1.getHashBytes();
        theSpec = myASN1.getSpec();

        /* Determine hash length */
        final int myLen = myExternal.length;
        final int myHashLen = myLen
                - RECIPELEN
                - SALTLEN;

        /* Create the byte arrays */
        theRecipe = new byte[RECIPELEN];
        theSalt = new byte[SALTLEN];
        theHash = new byte[myHashLen];

        /* Determine offset position */
        int myOffSet = Math.max(pPassLength, HASH_MARGIN);
        myOffSet = Math.min(myOffSet, myHashLen
                - HASH_MARGIN);

        /* Copy Data into buffers */
        System.arraycopy(myExternal, 0, theHash, 0, myOffSet);
        System.arraycopy(myExternal, myOffSet, theRecipe, 0, RECIPELEN);
        System.arraycopy(myExternal, myOffSet
                + RECIPELEN, theSalt, 0, SALTLEN);
        System.arraycopy(myExternal, myOffSet
                + RECIPELEN
                + SALTLEN, theHash, myOffSet, myHashLen
                - myOffSet);

        /* Calculate the initVector */
        final GordianPersonalisation myPersonal = pFactory.getPersonalisation();
        theInitVector = myPersonal.adjustIV(theSalt);

        /* Allocate new set of parameters */
        theParams = new GordianHashParameters(pFactory, theRecipe);
    }

    /**
     * Obtain the Spec.
     * @return the spec
     */
    GordianKeySetHashSpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain the Prime Digest type.
     * @return the digest type
     */
    GordianDigestType getPrimeDigest() {
        return theParams.getPrimeDigest();
    }

    /**
     * Obtain the Alternate Digest type.
     * @return the digest type
     */
    GordianDigestType getAlternateDigest() {
        return theParams.getAlternateDigest();
    }

    /**
     * Obtain the Secret Digest type.
     * @return the digest type
     */
    GordianDigestType getSecretDigest() {
        return theParams.getSecretDigest();
    }

    /**
     * Obtain the External Digest type.
     * @return the digest type
     */
    GordianDigestType getExternalDigest() {
        return theParams.getExternalDigest();
    }

    /**
     * Obtain the Adjustment.
     * @return the adjustment
     */
    int getAdjustment() {
        return theParams.getAdjustment();
    }

    /**
     * Obtain the Initialisation vector.
     * @return the initialisation vector
     */
    byte[] getInitVector() {
        return theInitVector == null
               ? null
               : Arrays.copyOf(theInitVector, theInitVector.length);
    }

    /**
     * Obtain the Hash.
     * @return the Hash
     */
    public byte[] getHash() {
        return theHash == null
               ? null
               : Arrays.copyOf(theHash, theHash.length);
    }

    /**
     * Build External Format for hash and password length.
     * @param pPassLength the password length
     * @param pHash the calculated hash
     * @return the external form
     * @throws OceanusException on error
     */
    byte[] buildExternal(final int pPassLength,
                         final byte[] pHash) throws OceanusException {
        /* Allocate the new buffer */
        final int myHashLen = pHash.length;
        final int myLen = RECIPELEN
                + SALTLEN
                + myHashLen;
        final byte[] myBuffer = new byte[myLen];

        /* Determine offset position */
        int myOffSet = Math.max(pPassLength, HASH_MARGIN);
        myOffSet = Math.min(myOffSet, myHashLen
                - HASH_MARGIN);

        /* Copy Data into buffer */
        System.arraycopy(pHash, 0, myBuffer, 0, myOffSet);
        System.arraycopy(theRecipe, 0, myBuffer, myOffSet, RECIPELEN);
        System.arraycopy(theSalt, 0, myBuffer, myOffSet
                + RECIPELEN, SALTLEN);
        System.arraycopy(pHash, myOffSet, myBuffer, myOffSet
                + RECIPELEN
                + SALTLEN, myHashLen
                - myOffSet);

        /* Build the ASN1 form */
        final GordianKeySetHashASN1 myASN1 = new GordianKeySetHashASN1(theSpec, myBuffer);
        return myASN1.getEncodedBytes();
    }

    /**
     * The parameters class.
     */
    private static final class GordianHashParameters {
        /**
         * The Recipe.
         */
        private final byte[] theRecipe;

        /**
         * The hMac types.
         */
        private final GordianDigestType[] theDigests;

        /**
         * The external Digest type.
         */
        private final GordianDigestType theExternalDigest;

        /**
         * The Adjustment.
         */
        private final int theAdjust;

        /**
         * Construct the parameters from random.
         * @param pFactory the factory
         */
        GordianHashParameters(final GordianCoreFactory pFactory) {
            /* Obtain Id manager and random */
            final GordianIdManager myManager = pFactory.getIdManager();
            final GordianPersonalisation myPersonal = pFactory.getPersonalisation();
            final SecureRandom myRandom = pFactory.getRandomSource().getRandom();

            /* Generate recipe and derive digestTypes */
            final int mySeed = myRandom.nextInt();
            theRecipe = TethysDataConverter.integerToByteArray(mySeed);
            final Random mySeededRandom = myPersonal.getSeededRandom(GordianPersonalId.HASHRANDOM, theRecipe);
            theDigests = myManager.deriveKeyHashDigestTypesFromSeed(mySeededRandom, NUM_DIGESTS);
            theExternalDigest = myManager.deriveExternalDigestTypeFromSeed(mySeededRandom);

            /* Derive random adjustment value */
            theAdjust = mySeededRandom.nextInt(TethysDataConverter.NYBBLE_MASK + 1);
        }

        /**
         * Construct the parameters from recipe.
         * @param pFactory the factory
         * @param pRecipe the recipe bytes
         */
        GordianHashParameters(final GordianCoreFactory pFactory,
                              final byte[] pRecipe) {
            /* Obtain Id manager */
            final GordianIdManager myManager = pFactory.getIdManager();
            final GordianPersonalisation myPersonal = pFactory.getPersonalisation();

            /* Store recipe and derive digestTypes */
            theRecipe = pRecipe;
            final Random mySeededRandom = myPersonal.getSeededRandom(GordianPersonalId.HASHRANDOM, theRecipe);
            theDigests = myManager.deriveKeyHashDigestTypesFromSeed(mySeededRandom, NUM_DIGESTS);
            theExternalDigest = myManager.deriveExternalDigestTypeFromSeed(mySeededRandom);

            /* Derive random adjustment value */
            theAdjust = mySeededRandom.nextInt(TethysDataConverter.NYBBLE_MASK + 1);
        }

        /**
         * Obtain the Recipe.
         * @return the recipe
         */
        byte[] getRecipe() {
            return theRecipe;
        }

        /**
         * Obtain the Prime Digest type.
         * @return the digest type
         */
        GordianDigestType getPrimeDigest() {
            return theDigests[0];
        }

        /**
         * Obtain the Alternate Digest type.
         * @return the digest type
         */
        GordianDigestType getAlternateDigest() {
            return theDigests[1];
        }

        /**
         * Obtain the Secret Digest type.
         * @return the digest type
         */
        GordianDigestType getSecretDigest() {
            return theDigests[2];
        }

        /**
         * Obtain the external Digest type.
         * @return the digest type
         */
        GordianDigestType getExternalDigest() {
            return theExternalDigest;
        }

        /**
         * Obtain the Adjustment.
         * @return the adjustment
         */
        int getAdjustment() {
            return theAdjust;
        }
    }
}
