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
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
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
     * The Recipe personalisation location.
     */
    private static final int LOC_RECIPE = 37;

    /**
     * Personalisation bytes.
     */
    private final byte[][] thePersonalisation;

    /**
     * The personalisation length.
     */
    private final int thePersonalLen;

    /**
     * The number of hashes.
     */
    private final int theNumHashes;

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
        thePersonalisation = personalise(pFactory, pParameters);
        theNumHashes = thePersonalisation.length - 1;
        thePersonalLen = HASH_LEN.getByteLength();

        /* Obtain the recipe mask */
        theRecipeMask = getPersonalisedInteger(LOC_RECIPE);
    }

    /**
     * Obtain an array of digests for personalisation.
     * @param pFactory the factory
     * @return the digests
     * @throws OceanusException on error
     */
    protected GordianDigest[] determineDigests(final GordianFactory pFactory) throws OceanusException {
        /* Initialise variables */
        final GordianDigestType[] myTypes = GordianDigestType.values();
        final GordianDigest[] myDigests = new GordianDigest[myTypes.length];
        int myLen = 0;

        /* Loop through the digestTypes */
        for (GordianDigestType myType : GordianDigestType.values()) {
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
    protected byte[][] personalise(final GordianFactory pFactory,
                                   final GordianParameters pParameters) throws OceanusException {
        /* Determine the digests */
        final GordianDigest[] myDigests = determineDigests(pFactory);
        final byte[][] myHashes = new byte[myDigests.length + 1][];

        /* Obtain configuration */
        final char[] myPhrase = pParameters.getSecurityPhrase();
        final byte[] myPersonalBytes = TethysDataConverter.stringToByteArray(BASE_PERSONAL);
        final byte[] myPhraseBytes = myPhrase == null
                                                      ? null
                                                      : TethysDataConverter.charsToByteArray(myPhrase);

        /* Initialise hashes */
        final byte[] myResults = new byte[HASH_LEN.getByteLength()];
        myHashes[myDigests.length] = myResults;
        for (int i = 0; i < myDigests.length; i++) {
            /* Initialise the digests */
            final GordianDigest myDigest = myDigests[i];
            myDigest.update(myPersonalBytes);
            if (myPhraseBytes != null) {
                myDigest.update(myPhraseBytes);
            }

            /* Finish the update and store the buffer */
            final byte[] myResult = myDigest.finish();
            TethysDataConverter.buildHashResult(myResults, myResult);
            myHashes[i] = myResult;
        }

        /* Loop several times */
        for (int i = 0; i < myDigests.length; i++) {
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
                TethysDataConverter.buildHashResult(myResults, myResult);
            }
        }

        /* Return the array */
        return myHashes;
    }

    /**
     * Update a MAC with personalisation.
     * @param pMac the MAC
     */
    protected void updateMac(final GordianMac pMac) {
        for (byte[] myArray : thePersonalisation) {
            pMac.update(myArray);
        }
    }

    /**
     * Convert the recipe.
     * @param pRecipe the recipe
     * @return the converted recipe
     */
    protected int convertRecipe(final int pRecipe) {
        return pRecipe ^ theRecipeMask;
    }

    /**
     * Obtain mask from personalisation.
     * @param pOffSet the offset within the array
     * @return the result
     */
    protected int getPersonalisedMask(final int pOffSet) {
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
     * Obtain integer from personalisation.
     * @param pOffSet the offset within the array
     * @return the result
     */
    protected int getPersonalisedInteger(final int pOffSet) {
        /* Loop to obtain the personalised byte */
        final int myVal = getPersonalisedMask(pOffSet);

        /* Ensure that the value is positive */
        return myVal < 0
                         ? -myVal
                         : myVal;
    }

    /**
     * Obtain byte from personalisation.
     * @param pOffSet the offset within the array
     * @return the result
     */
    protected int getPersonalisedByte(final int pOffSet) {
        int myOffSet = pOffSet;
        if (myOffSet >= thePersonalLen) {
            myOffSet %= thePersonalLen;
        }
        return thePersonalisation[theNumHashes][myOffSet] & TethysDataConverter.BYTE_MASK;
    }
}
