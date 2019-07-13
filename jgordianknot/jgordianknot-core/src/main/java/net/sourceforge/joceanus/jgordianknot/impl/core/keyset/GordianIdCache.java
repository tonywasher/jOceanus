/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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

import java.util.Arrays;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianRandomSource;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreCipherFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianPersonalisation.GordianPersonalId;

/**
 * Security Id Cache.
 */
public class GordianIdCache {
    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The keyLength.
     */
    private final GordianLength theKeyLength;

    /**
     * The SecureRandom.
     */
    private final GordianRandomSource theRandom;

    /**
     * The personalisation.
     */
    private final GordianPersonalisation thePersonalisation;

    /**
     * The list of Symmetric Keys.
     */
    private final GordianSymKeyType[] theSymKeys;

    /**
     * The list of keySet Symmetric Keys.
     */
    private final GordianSymKeyType[] theKeySetSymKeys;

    /**
     * The list of CMac Symmetric Keys.
     */
    private final GordianSymKeyType[] theCMacSymKeys;

    /**
     * The list of CMac Symmetric Keys.
     */
    private final GordianSymKeyType[] theGMacSymKeys;

    /**
     * The list of Poly1305 Symmetric Keys.
     */
    private final GordianSymKeyType[] thePolySymKeys;

    /**
     * The list of CBCMac Symmetric Keys.
     */
    private final GordianSymKeyType[] theCBCMacSymKeys;

    /**
     * The list of CFBMac Symmetric Keys.
     */
    private final GordianSymKeyType[] theCFBMacSymKeys;

    /**
     * The list of Stream Keys.
     */
    private final GordianStreamKeyType[] theStreamKeys;

    /**
     * The list of largeData Stream Keys.
     */
    private final GordianStreamKeyType[] theLargeDataStreamKeys;

    /**
     * The list of Digests.
     */
    private final GordianDigestType[] theDigests;

    /**
     * The list of external Digests.
     */
    private final GordianDigestType[] theExternalDigests;

    /**
     * The list of keySetDigests.
     */
    private final GordianDigestType[] theKeySetDigests;

    /**
     * The list of hMacDigests.
     */
    private final GordianDigestType[] theHMacDigests;

    /**
     * The list of MACs.
     */
    private final GordianMacType[] theMacs;

    /**
     * The list of LargeData MACs.
     */
    private final GordianMacType[] theLargeDataMacs;

    /**
     * Constructor.
     * @param pFactory the security factory
     * @param pKeyLen the keyLength
     * @param pKeySetFactory the keySet factory
     */
    GordianIdCache(final GordianCoreFactory pFactory,
                   final GordianLength pKeyLen,
                   final GordianCoreKeySetFactory pKeySetFactory) {
        /* Store the factory */
        theFactory = pFactory;
        theKeyLength = pKeyLen;
        theRandom = pFactory.getRandomSource();
        thePersonalisation = pKeySetFactory.getPersonalisation();

        /* Access factories */
        final GordianDigestFactory myDigests = theFactory.getDigestFactory();
        final GordianCipherFactory myCiphers = theFactory.getCipherFactory();
        final GordianMacFactory myMacs = theFactory.getMacFactory();

        /* Create shuffled and filtered lists */

        theSymKeys = shuffleTypes(GordianSymKeyType.values(), GordianPersonalId.SYMKEY,
                myCiphers.supportedSymKeyTypes().and(t -> GordianCoreCipherFactory.validSymKeyTypeForKeyLength(t, theKeyLength)));
        theKeySetSymKeys = shuffleTypes(GordianSymKeyType.values(), GordianPersonalId.SYMKEY,
                pKeySetFactory.supportedKeySetSymKeyTypes(theKeyLength));
        theCMacSymKeys = shuffleTypes(GordianSymKeyType.values(), GordianPersonalId.SYMKEY,
                t -> validSymKeyTypeForMacType(GordianMacType.CMAC, t));
        theGMacSymKeys = shuffleTypes(GordianSymKeyType.values(), GordianPersonalId.SYMKEY,
                t -> validSymKeyTypeForMacType(GordianMacType.GMAC, t));
        thePolySymKeys = shuffleTypes(GordianSymKeyType.values(), GordianPersonalId.SYMKEY,
                this::validSymKeyTypeForPoly1305);
        theCBCMacSymKeys = shuffleTypes(GordianSymKeyType.values(), GordianPersonalId.SYMKEY,
                t -> validSymKeyTypeForMacType(GordianMacType.CBCMAC, t));
        theCFBMacSymKeys = shuffleTypes(GordianSymKeyType.values(), GordianPersonalId.SYMKEY,
                t -> validSymKeyTypeForMacType(GordianMacType.CFBMAC, t));

        /* Create shuffled streamKey lists */
        theStreamKeys = shuffleTypes(GordianStreamKeyType.values(), GordianPersonalId.STREAMKEY,
                myCiphers.supportedStreamKeyTypes().and(t -> t.validForKeyLength(theKeyLength)));
        theLargeDataStreamKeys = shuffleTypes(GordianStreamKeyType.values(), GordianPersonalId.STREAMKEY,
                myCiphers.supportedStreamKeyTypes().and(t -> t.validForKeyLength(theKeyLength)).and(GordianStreamKeyType::supportsLargeData));

        /* Create shuffled digest lists */
        theDigests = shuffleTypes(GordianDigestType.values(), GordianPersonalId.DIGEST, myDigests.supportedDigestTypes());
        theExternalDigests = shuffleTypes(GordianDigestType.values(), GordianPersonalId.DIGEST, myDigests.supportedExternalDigestTypes());
        theKeySetDigests = shuffleTypes(GordianDigestType.values(), GordianPersonalId.DIGEST, pKeySetFactory.supportedKeySetDigestTypes());
        theHMacDigests = shuffleTypes(GordianDigestType.values(), GordianPersonalId.DIGEST, myMacs.supportedHMacDigestTypes());

        /* Create shuffled MacType lists */
        theMacs = shuffleTypes(GordianMacType.values(), GordianPersonalId.MAC,
                myMacs.supportedMacTypes().and(t -> t.validForKeyLength(theKeyLength)));
        theLargeDataMacs = shuffleTypes(GordianMacType.values(), GordianPersonalId.MAC,
                myMacs.supportedMacTypes().and(t -> t.validForKeyLength(theKeyLength)).and(GordianMacType::supportsLargeData));
    }

    /**
     * Obtain random SymKeyType.
     * @return the random symKeyType
     */
    GordianSymKeyType generateRandomSymKeyType() {
        /* Determine a random symKey */
        final GordianSymKeyType[] mySymKey = getRandomTypes(theSymKeys, 1);

        /* Return the single SymKeyType */
        return mySymKey[0];
    }

    /**
     * Obtain set of random keySet SymKeyTypes.
     * @param pCount the count
     * @return the random symKeyTypes
     */
    GordianSymKeyType[] generateRandomKeySetSymKeyTypes(final int pCount) {
        /* Determine random symKeyTypes */
        return getRandomTypes(theKeySetSymKeys, pCount);
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
     * Obtain random StreamKeyType.
     * @param pLargeData only generate a Mac that is suitable for for parsing large amounts of data
     * @return the random streamKeyType
     */
    GordianStreamKeyType generateRandomStreamKeyType(final boolean pLargeData) {
        return getRandomTypes(pLargeData ? theLargeDataStreamKeys : theStreamKeys, 1)[0];
    }

    /**
     * Obtain random DigestType.
     * @return the random digestType
     */
    GordianDigestType generateRandomDigestType() {
        /* Determine a random digestType */
        final GordianDigestType[] myDigest = getRandomTypes(theDigests, 1);

        /* Return the single digestType */
        return myDigest[0];
    }

    /**
     * Obtain random hMacDigestType.
     * @return the random digestType
     */
    GordianDigestType generateRandomHMacDigestType() {
        /* Determine a random digestType */
        final GordianDigestType[] myDigest = getRandomTypes(theHMacDigests, 1);

        /* Return the single digestType */
        return myDigest[0];
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
     * generate random GordianMacSpec.
     * @param pLargeData only generate a Mac that is suitable for for parsing large amounts of data
     * @return the new MacSpec
     */
    GordianMacSpec generateRandomMacSpec(final boolean pLargeData) {
        final GordianMacType myMacType = generateRandomMacType(pLargeData);
        switch (myMacType) {
            case HMAC:
                final GordianDigestType myDigestType = generateRandomHMacDigestType();
                return GordianMacSpec.hMac(myDigestType, theKeyLength);
            case POLY1305:
            case GMAC:
            case CMAC:
            case CBCMAC:
            case CFBMAC:
                return generateRandomSymKeyMacSpec(myMacType);
            case SKEIN:
                return GordianMacSpec.skeinMac(theKeyLength);
            case KMAC:
                return GordianMacSpec.kMac(theKeyLength);
            case BLAKE:
                return GordianMacSpec.blakeMac(theKeyLength);
            case KUPYNA:
                return GordianMacSpec.kupynaMac(theKeyLength);
            case KALYNA:
                return GordianMacSpec.kalynaMac(GordianSymKeySpec.kalyna(theKeyLength));
            case SIPHASH:
                return GordianMacSpec.sipHash();
            case ZUC:
                return GordianMacSpec.zucMac(theKeyLength, GordianLength.LEN_32);
            default:
                return new GordianMacSpec(myMacType, theKeyLength);
        }
    }

    /**
     * Obtain random MacType.
     * @param pLargeData only generate a Mac that is suitable for for parsing large amounts of data
     * @return the random macType
     */
    private GordianMacType generateRandomMacType(final boolean pLargeData) {
        return getRandomTypes(pLargeData ? theLargeDataMacs : theMacs, 1)[0];
    }

    /**
     * Is the symKeyType valid for the MacType.
     * @param pMacType the macType
     * @param pSymKeyType the symKey Type
     * @return true/false
     */
    private boolean validSymKeyTypeForMacType(final GordianMacType pMacType,
                                              final GordianSymKeyType pSymKeyType) {
        final GordianMacFactory myMacs = theFactory.getMacFactory();
        final GordianMacSpec myMacSpec = new GordianMacSpec(pMacType, new GordianSymKeySpec(pSymKeyType, theKeyLength));
        return myMacs.supportedMacSpecs().test(myMacSpec);
    }

    /**
     * Is the symKeyType valid for Poly1305.
     * @param pSymKeyType the symKey Type
     * @return true/false
     */
    private boolean validSymKeyTypeForPoly1305(final GordianSymKeyType pSymKeyType) {
        final GordianMacFactory myMacs = theFactory.getMacFactory();
        final GordianMacSpec myMacSpec = new GordianMacSpec(GordianMacType.POLY1305, new GordianSymKeySpec(pSymKeyType, GordianLength.LEN_128));
        return myMacs.supportedMacSpecs().test(myMacSpec);
    }

    /**
     * Obtain random symKey MacSpec.
     * @param pMacType the macType
     * @return the random symKey MacSpec
     */
    private GordianMacSpec generateRandomSymKeyMacSpec(final GordianMacType pMacType) {
        final GordianSymKeyType mySymType = getRandomTypes(determineSymKeySetForMacType(pMacType), 1)[0];
        return GordianMacType.POLY1305 == pMacType
               ? new GordianMacSpec(GordianMacType.POLY1305, new GordianSymKeySpec(mySymType, GordianLength.LEN_128))
               : new GordianMacSpec(pMacType, new GordianSymKeySpec(mySymType, theKeyLength));
    }

    /**
     * Obtain random symKey MacSpec.
     * @param pMacType the macType
     * @return the random symKey MacSpec
     */
    private GordianSymKeyType[] determineSymKeySetForMacType(final GordianMacType pMacType) {
        /* Obtain the correct symKeySet */
        switch (pMacType) {
            case CMAC:
                return theCMacSymKeys;
            case GMAC:
                return theGMacSymKeys;
            case POLY1305:
                return thePolySymKeys;
            case CBCMAC:
                return theCBCMacSymKeys;
            case CFBMAC:
                return theCFBMacSymKeys;
            default:
                throw new IllegalArgumentException("IllegalMacType: " + pMacType);
        }
    }

    /**
     * Generate personalised shuffle of Types.
     * @param <E> the data type
     * @param pTypes the types to be shuffled.
     * @param pId the relevant id
     * @param pFilter the filter
     * @return the shuffled types
     */
    private <E extends Enum<E>> E[] shuffleTypes(final E[] pTypes,
                                                 final GordianPersonalId pId,
                                                 final Predicate<E> pFilter) {
        /* Filter the types */
        final E[] myTypes = filterTypes(pTypes, pFilter);

        /* Access input length */
        final int myLen = myTypes.length;
        int myNumTypes = myLen;

        /* Obtain the personalised integer */
        int mySeed = thePersonalisation.getPersonalisedInteger(pId);

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
     * Generate random subSet of Types.
     * @param <E> the data type
     * @param pTypes the source types.
     * @param pCount the count of types to be returned
     * @return the subSet of types
     */
    private <E extends Enum<E>> E[] getRandomTypes(final E[] pTypes,
                                                   final int pCount) {
        /* Use a random seed */
        final E[] myResult = Arrays.copyOf(pTypes, pCount);
        getSeededTypes(pTypes, myResult, theRandom.getRandom().nextInt());
        return myResult;
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
