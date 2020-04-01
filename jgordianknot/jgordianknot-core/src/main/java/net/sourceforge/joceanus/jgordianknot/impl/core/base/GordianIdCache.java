/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.base;

import java.util.Arrays;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianPersonalisation.GordianPersonalId;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Security Id Cache.
 */
public class GordianIdCache {
    /**
     * The personalisation.
     */
    private final GordianPersonalisation thePersonalisation;

    /**
     * The list of keySet Symmetric Keys.
     */
    private final GordianSymKeyType[] theKeySetSymKeys;

    /**
     * The list of external Digests.
     */
    private final GordianDigestType[] theExternalDigests;

    /**
     * The list of keySetDigests.
     */
    private final GordianDigestType[] theKeySetDigests;

    /**
     * Constructor.
     * @param pFactory the security factory
     * @param pKeyLen the keyLength
     * @throws OceanusException on error
     */
    GordianIdCache(final GordianCoreFactory pFactory,
                   final GordianLength pKeyLen) throws OceanusException {
        /* Store the details */
        thePersonalisation = pFactory.getPersonalisation();

        /* Access factories */
        final GordianDigestFactory myDigests = pFactory.getDigestFactory();

        /* Create shuffled and filtered lists */
        int myIndex = 0;
        theKeySetSymKeys = shuffleTypes(GordianSymKeyType.values(), GordianPersonalId.SYMKEY, myIndex++,
                pFactory.supportedKeySetSymKeyTypes(pKeyLen));

        /* Create shuffled digest lists */
        theExternalDigests = shuffleTypes(GordianDigestType.values(), GordianPersonalId.DIGEST, myIndex++,
                myDigests.supportedExternalDigestTypes());
        theKeySetDigests = shuffleTypes(GordianDigestType.values(), GordianPersonalId.DIGEST, myIndex,
                pFactory.supportedKeySetDigestTypes());
    }

    /**
     * Derive set of keySet SymKeyTypes from seed.
     * @param pSeed the seed
     * @param pKeyTypes the array of symKeyTypes to be filled in
     * @return the remaining seed
     */
    int deriveKeySetSymKeyTypesFromSeed(final int pSeed,
                                        final GordianSymKeyType[] pKeyTypes) {
        return getSeededTypes(theKeySetSymKeys, pKeyTypes, pSeed);
    }

    /**
     * Derive set of keyHashDigestTypes from seed.
     * @param pSeed the seed
     * @param pDigestTypes the array of digestTypes to be filled in
     * @return the remaining seed
     */
    int deriveKeyHashDigestTypesFromSeed(final int pSeed,
                                         final GordianDigestType[] pDigestTypes) {
        return getSeededTypes(theKeySetDigests, pDigestTypes, pSeed);
    }

    /**
     * Derive set of standard externalDigestTypes from seed.
     * @param pSeed the seed
     * @param pDigestTypes the array of digestTypes to be filled in
     * @return the remaining seed
     */
    int deriveExternalDigestTypesFromSeed(final int pSeed,
                                          final GordianDigestType[] pDigestTypes) {
        return getSeededTypes(theExternalDigests, pDigestTypes, pSeed);
    }

    /**
     * Generate personalised shuffle of Types.
     * @param <E> the data type
     * @param pTypes the types to be shuffled.
     * @param pId the relevant id
     * @param pIndex the index
     * @param pFilter the filter
     * @return the shuffled types
     */
    private <E extends Enum<E>> E[] shuffleTypes(final E[] pTypes,
                                                 final GordianPersonalId pId,
                                                 final int pIndex,
                                                 final Predicate<E> pFilter) {
        /* Filter the types */
        final E[] myTypes = filterTypes(pTypes, pFilter);

        /* Access input length */
        final int myLen = myTypes.length;
        int myNumTypes = myLen;

        /* Obtain the personalised integer and modify it according to the index to obtain differing seeds for id re-use */
        int mySeed = thePersonalisation.getPersonalisedInteger(pId) * (GordianCoreFactory.HASH_PRIME + pIndex);
        mySeed = GordianPersonalisation.sanitiseValue(mySeed);

        /* Loop through the types */
        for (int i = 0; i < myLen - 1; i++) {
            /* Access the next element index */
            final int myIndex = mySeed % myNumTypes;

            /* If we have not chosen the first index */
            if (myIndex > 0) {
                /* Access the value */
                final int myLoc = i + myIndex;
                final E myType = myTypes[myLoc];

                /* Swap value into place */
                final E myCurr = myTypes[i];
                myTypes[i] = myType;
                myTypes[myLoc] = myCurr;
            }

            /* Adjust for next iteration */
            mySeed /= myNumTypes;
            myNumTypes--;
        }

        /* Return the shuffled and filtered types */
        return myTypes;
    }

    /**
     * Filter Types.
     * @param <E> the data type
     * @param pTypes the types to be shuffled.
     * @param pFilter the filter
     * @return the filtered types
     */
    private static <E extends Enum<E>> E[] filterTypes(final E[] pTypes,
                                                       final Predicate<E> pFilter) {
        /* Access input length */
        final int myNumTypes = pTypes.length;
        int myNumAvailable = 0;

        /* Allocate a copy of the types */
        final E[] myTypes = Arrays.copyOf(pTypes, myNumTypes);

        /* Loop through the types */
        for (int i = 0; i < myNumTypes; i++) {
            /* Access the value */
            final E myType = myTypes[i];

            /* If this is a valid selection */
            if (pFilter.test(myType)) {
                /* Copy it to the target types */
                myTypes[myNumAvailable++] = myType;
            }
        }

        /* Return the filtered types as a new array */
        return Arrays.copyOf(myTypes, myNumAvailable);
    }

    /**
     * Obtain seeded subSet of Types.
     * @param <E> the data type
     * @param pTypes the source types.
     * @param pSelected the array of selected types to be filled in
     * @param pSeed the seed for the types
     * @return the remaining seed
     */
    private static <E extends Enum<E>> int getSeededTypes(final E[] pTypes,
                                                          final E[] pSelected,
                                                          final int pSeed) {
        /* Access lengths */
        int myTotalTypes = pTypes.length;
        final int myNumTypes = pSelected.length;

        /* Allocate a copy of the types */
        final E[] mySelection = Arrays.copyOf(pTypes, myTotalTypes);

        /* Ensure that seed is positive */
        int mySeed = pSeed < 0
                     ? -pSeed
                     : pSeed;

        /* Loop through the types */
        int myNumSelected = 0;
        while (myNumSelected < myNumTypes) {
            /* Access the next element index */
            final int iIndex = mySeed % myTotalTypes;

            /* Access the value */
            final int myLoc = myNumSelected + iIndex;
            final E myType = mySelection[myLoc];

            /* If we need to shift the item */
            if (iIndex != 0) {
                /* Swap value into place */
                final E myCurr = mySelection[myNumSelected];
                mySelection[myNumSelected] = myType;
                mySelection[myLoc] = myCurr;
            }

            /* Increment selection count */
            myNumSelected++;

            /* Adjust for next iteration */
            mySeed /= myTotalTypes;
            myTotalTypes--;

            /* Break loop if we have finished */
            if (myTotalTypes == 0) {
                break;
            }
        }

        /* Handle insufficient available types */
        if (myNumSelected < myNumTypes) {
            /* Can't get here */
            throw new IllegalStateException("Insufficient available types");
        }

        /* Fill in the results array */
        System.arraycopy(mySelection, 0, pSelected, 0, myNumTypes);

        /* Return the results */
        return mySeed;
    }
}
