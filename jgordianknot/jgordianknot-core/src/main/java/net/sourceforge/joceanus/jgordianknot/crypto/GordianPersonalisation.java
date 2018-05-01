/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto;

import java.util.Arrays;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Personalisation.
 */
public class GordianPersonalisation {
    /**
     * The Base personalisation.
     */
    private static final String BASE_PERSONAL = "jG0rd1anKn0t";

    /**
     * The hash length.
     */
    private static final GordianLength HASH_LEN = GordianLength.LEN_512;

    /**
     * The Mask to see whether adjustment is needed for a small Recipe/Personalised Integer.
     */
    private static final int VALUE_CHECK = 0x7F000000;

    /**
     * The Adjustment to apply to a small Recipe/Personalised Integer.
     */
    private static final int VALUE_ADJUST = 0x7000000;

    /**
     * Personalisation bytes.
     */
    private final byte[] thePersonalisation;

    /**
     * InitVector bytes.
     */
    private final byte[] theInitVector;

    /**
     * The personalisation length.
     */
    private final int thePersonalLen;

    /**
     * The recipe mask.
     */
    private final int theRecipeMask;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pParameters the parameters
     * @throws OceanusException on error
     */
    protected GordianPersonalisation(final GordianFactory pFactory,
                                     final GordianParameters pParameters) throws OceanusException {
        /* Calculate personalisation bytes */
        final byte[][] myArrays = personalise(pFactory, pParameters);
        thePersonalisation = myArrays[0];
        theInitVector = myArrays[1];
        thePersonalLen = thePersonalisation.length;

        /* Obtain the recipe mask */
        theRecipeMask = getPersonalisedInteger(GordianPersonalId.RECIPE);
    }

    /**
     * Obtain an array of digests for personalisation.
     * @param pFactory the factory
     * @return the digests
     * @throws OceanusException on error
     */
    private static GordianDigest[] determineDigests(final GordianFactory pFactory) throws OceanusException {
        /* Initialise variables */
        final GordianDigestType[] myTypes = GordianDigestType.values();
        final GordianDigest[] myDigests = new GordianDigest[myTypes.length];
        int myLen = 0;

        /* Loop through the digestTypes */
        for (final GordianDigestType myType : GordianDigestType.values()) {
            /* Add the digest if it is relevant */
            if (pFactory.supportedExternalDigestTypes().test(myType)) {
                myDigests[myLen++] = pFactory.createDigest(new GordianDigestSpec(myType, HASH_LEN));
            }
        }

        /* Return the array */
        return Arrays.copyOf(myDigests, myLen);
    }

    /**
     * Create an array of hashes from personalisation.
     * @param pFactory the factory
     * @param pParameters the parameters
     * @return the hashes
     * @throws OceanusException on error
     */
    private static byte[][] personalise(final GordianFactory pFactory,
                                        final GordianParameters pParameters) throws OceanusException {
        /* Determine the digests */
        final GordianDigest[] myDigests = determineDigests(pFactory);
        final byte[][] myHashes = new byte[myDigests.length][];

        /* Obtain configuration */
        final char[] myPhrase = pParameters.getSecurityPhrase();
        final byte[] myPersonalBytes = TethysDataConverter.stringToByteArray(BASE_PERSONAL);
        final byte[] myPhraseBytes = myPhrase == null
                                                      ? null
                                                      : TethysDataConverter.charsToByteArray(myPhrase);

        /* Initialise hashes */
        final byte[] myConfig = new byte[HASH_LEN.getByteLength()];
        for (int i = 0; i < myDigests.length; i++) {
            /* Initialise the digests */
            final GordianDigest myDigest = myDigests[i];
            myDigest.update(myPersonalBytes);
            if (myPhraseBytes != null) {
                myDigest.update(myPhraseBytes);
            }

            /* Finish the update and store the buffer */
            final byte[] myResult = myDigest.finish();
            TethysDataConverter.buildHashResult(myConfig, myResult);
            myHashes[i] = myResult;
        }

        /* Loop the configured amount of times to cross-fertilise */
        for (int i = 0; i < pParameters.getNumHashIterations(); i++) {
            /* Update all the digests */
            for (int j = 0; j < myDigests.length; j++) {
                /* Update with the results */
                final GordianDigest myDigest = myDigests[j];
                for (int k = 0; k < myDigests.length; k++) {
                    myDigest.update(myHashes[k]);
                }
            }

            /* Finish all the digests */
            for (int j = 0; j < myDigests.length; j++) {
                /* Update with the results */
                final GordianDigest myDigest = myDigests[j];
                final byte[] myResult = myHashes[j];
                myDigest.finish(myResult, 0);
                TethysDataConverter.buildHashResult(myConfig, myResult);
            }
        }

        /* Finally build the initVector mask */
        final byte[] myInitVec = new byte[HASH_LEN.getByteLength()];
        for (int i = 0; i < myDigests.length; i++) {
            TethysDataConverter.buildHashResult(myConfig, myHashes[i]);
        }

        /* Return the array */
        return new byte[][]
        { myConfig, myInitVec };
    }

    /**
     * Adjust an IV.
     * @param pIV the input IV
     * @return the adjusted IV
     */
    protected byte[] adjustIV(final byte[] pIV) {
        return TethysDataConverter.combineHashes(pIV, theInitVector);
    }

    /**
     * Update a MAC with personalisation.
     * @param pMac the MAC
     */
    protected void updateMac(final GordianMac pMac) {
        pMac.update(thePersonalisation);
        pMac.update(theInitVector);
    }

    /**
     * Convert the recipe.
     * @param pRecipe the recipe
     * @return the converted recipe
     */
    protected int convertRecipe(final int pRecipe) {
        return sanitiseValue(pRecipe ^ theRecipeMask);
    }

    /**
     * Obtain integer from personalisation.
     * @param pId the id of the integer
     * @return the result
     */
    protected int getPersonalisedInteger(final GordianPersonalId pId) {
        return sanitiseValue(getPersonalisedMask(getOffsetForId(pId)));
    }

    /**
     * Obtain byte from personalisation.
     * @param pId the id of the byte
     * @return the result
     */
    protected int getPersonalisedByte(final GordianPersonalId pId) {
        return getPersonalisedByte(getOffsetForId(pId));
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
            myVal |= getPersonalisedByte(myOffSet);
        }

        /* Return the value */
        return myVal;
    }

    /**
     * Obtain byte from personalisation.
     * @param pOffSet the offset within the array
     * @return the result
     */
    private int getPersonalisedByte(final int pOffSet) {
        int myOffSet = pOffSet;
        if (myOffSet >= thePersonalLen) {
            myOffSet %= thePersonalLen;
        }
        return thePersonalisation[myOffSet] & TethysDataConverter.BYTE_MASK;
    }

    /**
     * Sanitise an integer value.
     * @param pValue the value to sanitise
     * @return the sanitised value
     */
    private static int sanitiseValue(final int pValue) {
        /* Ensure that the value is positive */
        int myVal = pValue < 0
                               ? -pValue
                               : pValue;

        /* If the value is insufficiently large */
        if ((myVal & VALUE_CHECK) == 0) {
            /* Set some top bits */
            myVal |= VALUE_ADJUST;
        }

        /* Return the sanitised value */
        return myVal;
    }

    /**
     * Personalisation IDs.
     */
    protected enum GordianPersonalId {
        /**
         * SymKey.
         */
        SYMKEY,

        /**
         * KeySetSymKey.
         */
        KEYSETSYMKEY,

        /**
         * StreamKey.
         */
        STREAMKEY,

        /**
         * Digest.
         */
        DIGEST,

        /**
         * ExternalDigest.
         */
        XTERNDIGEST,

        /**
         * KeySetDigest.
         */
        KEYSETDIGEST,

        /**
         * HMac.
         */
        HMAC,

        /**
         * MAC.
         */
        MAC,

        /**
         * Recipe.
         */
        RECIPE,

        /**
         * KnuthPrime.
         */
        KNUTHPRIME,

        /**
         * KnuthMask.
         */
        KNUTHMASK;
    }
}
