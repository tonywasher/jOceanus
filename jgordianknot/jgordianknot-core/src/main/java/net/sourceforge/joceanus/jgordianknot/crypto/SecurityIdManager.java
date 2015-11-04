/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jgordianknot/src/main/java/net/sourceforge/joceanus/jgordianknot/crypto/SecurityGenerator.java $
 * $Revision: 570 $
 * $Author: Tony $
 * $Date: 2015-02-14 06:54:38 +0000 (Sat, 14 Feb 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jtethys.DataConverter;

/**
 * Security Id Manager.
 */
public class SecurityIdManager {
    /**
     * The Asymmetric personalisation location.
     */
    private static final int LOC_ASYM = 3;

    /**
     * The Symmetric personalisation location.
     */
    private static final int LOC_SYM = 5;

    /**
     * The Stream personalisation location.
     */
    private static final int LOC_STREAM = 7;

    /**
     * The Digest personalisation location.
     */
    private static final int LOC_DIGEST = 11;

    /**
     * The Mac personalisation location.
     */
    private static final int LOC_MAC = 13;

    /**
     * The SecureRandom.
     */
    private final SecureRandom theRandom;

    /**
     * The personalisation hash.
     */
    private final byte[] thePersonalisation;

    /**
     * The personalisation length.
     */
    private final int thePersonalLen;

    /**
     * The list of ASymmetric Keys.
     */
    private final AsymKeyType[] theAsymKeys;

    /**
     * The list of Symmetric Keys.
     */
    private final SymKeyType[] theSymKeys;

    /**
     * The list of Stream Keys.
     */
    private final StreamKeyType[] theStreamKeys;

    /**
     * The list of Digests.
     */
    private final DigestType[] theDigests;

    /**
     * The list of Macs.
     */
    private final MacType[] theMacs;

    /**
     * Constructor.
     * @param pGenerator the security generator
     */
    protected SecurityIdManager(final SecurityGenerator pGenerator) {
        /* Access random generator */
        theRandom = pGenerator.getRandom();

        /* Access personalisation */
        thePersonalisation = pGenerator.getPersonalisation();
        thePersonalLen = thePersonalisation.length;

        /* Create shuffled lists */
        theAsymKeys = shuffleTypes(AsymKeyType.values(), LOC_ASYM);
        theSymKeys = shuffleTypes(SymKeyType.values(), LOC_SYM);
        theStreamKeys = shuffleTypes(StreamKeyType.values(), LOC_STREAM);
        theDigests = shuffleTypes(DigestType.values(), LOC_DIGEST);
        theMacs = shuffleTypes(MacType.values(), LOC_MAC);
    }

    /**
     * Obtain random ASymKeyType.
     * @param pPredicate the predicate to determine whether a type is valid
     * @return the random AsymKeyType
     */
    protected AsymKeyType getRandomASymKeyType(final Predicate<AsymKeyType> pPredicate) {
        /* Determine a random AsymKey */
        AsymKeyType[] myASymKey = getRandomTypes(theAsymKeys, 1, pPredicate);

        /* Return the single ASymKeyType */
        return myASymKey[0];
    }

    /**
     * Obtain asymKeyType from external AsymKeyId.
     * @param pId the external id
     * @param pPredicate the predicate to determine whether a type is valid
     * @return the asymKeyType
     */
    protected AsymKeyType deriveAsymKeyTypeFromExternalId(final int pId,
                                                          final Predicate<AsymKeyType> pPredicate) {
        /* derive the asymKeyType */
        return deriveTypeFromId(pId, theAsymKeys, pPredicate);
    }

    /**
     * Obtain external AsymKeyId.
     * @param pKey the asymKeyType
     * @return the external id
     */
    protected int getExternalId(final AsymKeyType pKey) {
        /* Return the external id */
        return getExternalId(pKey, theAsymKeys);
    }

    /**
     * Obtain random SymKeyType.
     * @param pPredicate the predicate to determine whether a type is valid
     * @return the random symKeyType
     */
    protected SymKeyType getRandomSymKeyType(final Predicate<SymKeyType> pPredicate) {
        /* Determine a random symKey */
        SymKeyType[] mySymKey = getRandomTypes(theSymKeys, 1, pPredicate);

        /* Return the single SymKeyType */
        return mySymKey[0];
    }

    /**
     * Obtain set of random SymKeyTypes.
     * @param pCount the count
     * @param pPredicate the predicate to determine whether a type is valid
     * @return the random symKeyTypes
     */
    protected SymKeyType[] getRandomSymKeyTypes(final int pCount,
                                                final Predicate<SymKeyType> pPredicate) {
        /* Determine random symKeyTypes */
        return getRandomTypes(theSymKeys, pCount, pPredicate);
    }

    /**
     * Obtain symKeyType from external SymKeyId.
     * @param pId the external id
     * @param pPredicate the predicate to determine whether a type is valid
     * @return the symKeyType
     */
    protected SymKeyType deriveSymKeyTypeFromExternalId(final int pId,
                                                        final Predicate<SymKeyType> pPredicate) {
        /* derive the symKeyType */
        return deriveTypeFromId(pId, theSymKeys, pPredicate);
    }

    /**
     * Obtain external SymKeyId.
     * @param pKey the symKeyType
     * @return the external id
     */
    protected int getExternalId(final SymKeyType pKey) {
        /* Return the external id */
        return getExternalId(pKey, theSymKeys);
    }

    /**
     * Obtain random StreamKeyType.
     * @param pPredicate the predicate to determine whether a type is valid
     * @return the random streamKeyType
     */
    protected StreamKeyType getRandomStreamKeyType(final Predicate<StreamKeyType> pPredicate) {
        /* Determine a random streamKeyType */
        StreamKeyType[] myStreamKey = getRandomTypes(theStreamKeys, 1, pPredicate);

        /* Return the single StreamKeyType */
        return myStreamKey[0];
    }

    /**
     * Obtain streamKeyType from external StreamKeyId.
     * @param pId the external id
     * @param pPredicate the predicate to determine whether a type is valid
     * @return the streamKeyType
     */
    protected StreamKeyType deriveStreamKeyTypeFromExternalId(final int pId,
                                                              final Predicate<StreamKeyType> pPredicate) {
        /* derive the streamKeyType */
        return deriveTypeFromId(pId, theStreamKeys, pPredicate);
    }

    /**
     * Obtain external StreamKeyId.
     * @param pKey the streamKeyType
     * @return the external id
     */
    protected int getExternalId(final StreamKeyType pKey) {
        /* Return the external id */
        return getExternalId(pKey, theStreamKeys);
    }

    /**
     * Obtain random DigestType.
     * @param pPredicate the predicate to determine whether a type is valid
     * @return the random digestType
     */
    protected DigestType getRandomDigestType(final Predicate<DigestType> pPredicate) {
        /* Determine a random digestType */
        DigestType[] myDigest = getRandomTypes(theDigests, 1, pPredicate);

        /* Return the single digestType */
        return myDigest[0];
    }

    /**
     * Obtain seeded DigestType.
     * @param pSeed the seed for the digest type
     * @param pPredicate the predicate to determine whether a type is valid
     * @return the random digestType
     */
    protected DigestType getSeededDigestType(final int pSeed,
                                             final Predicate<DigestType> pPredicate) {
        /* Determine the seeded digestType */
        DigestType[] myDigest = getSeededTypes(theDigests, 1, pSeed, pPredicate);

        /* Return the single digestType */
        return myDigest[0];
    }

    /**
     * Obtain set of random DigestTypes.
     * @param pCount the count
     * @param pPredicate the predicate to determine whether a type is valid
     * @return the random digestTypes
     */
    protected DigestType[] getRandomDigestTypes(final int pCount,
                                                final Predicate<DigestType> pPredicate) {
        /* Determine random digestTypes */
        return getRandomTypes(theDigests, pCount, pPredicate);
    }

    /**
     * Obtain digestType from external DigestId.
     * @param pId the external id
     * @param pPredicate the predicate to determine whether a type is valid
     * @return the digestType
     */
    protected DigestType deriveDigestTypeFromExternalId(final int pId,
                                                        final Predicate<DigestType> pPredicate) {
        /* derive the digestType */
        return deriveTypeFromId(pId, theDigests, pPredicate);
    }

    /**
     * Obtain external DigestId.
     * @param pDigest the digestType
     * @return the external id
     */
    protected int getExternalId(final DigestType pDigest) {
        /* Return the external id */
        return getExternalId(pDigest, theDigests);
    }

    /**
     * Obtain random MacType.
     * @param pPredicate the predicate to determine whether a type is valid
     * @return the random macType
     */
    protected MacType getRandomMacType(final Predicate<MacType> pPredicate) {
        /* Determine a random digest */
        MacType[] myMac = getRandomTypes(theMacs, 1, pPredicate);

        /* Return the single macType */
        return myMac[0];
    }

    /**
     * Obtain macType from external MacId.
     * @param pId the external id
     * @param pPredicate the predicate to determine whether a type is valid
     * @return the macType
     */
    protected MacType deriveMacTypeFromExternalId(final int pId,
                                                  final Predicate<MacType> pPredicate) {
        /* derive the macType */
        return deriveTypeFromId(pId, theMacs, pPredicate);
    }

    /**
     * Obtain external MacId.
     * @param pMac the macType
     * @return the external id
     */
    protected int getExternalId(final MacType pMac) {
        /* Return the external id */
        return getExternalId(pMac, theMacs);
    }

    /**
     * Obtain external Id for type.
     * @param <E> the data type
     * @param pType the type.
     * @param pTypes the type list
     * @return the external Id
     */

    private static <E extends Enum<E>> int getExternalId(final E pType,
                                                         final E[] pTypes) {
        /* Loop through the types */
        int myNumTypes = pTypes.length;
        for (int i = 0; i < myNumTypes; i++) {
            /* return the match */
            if (pType == pTypes[i]) {
                return i;
            }
        }

        /* Can't get here */
        throw new IllegalStateException("Invalid item");
    }

    /**
     * Obtain type by Id.
     * @param <E> the data type
     * @param pId the Id.
     * @param pTypes the type list
     * @param pPredicate the predicate to determine whether a type is valid
     * @return the type
     */
    private static <E extends Enum<E>> E deriveTypeFromId(final int pId,
                                                          final E[] pTypes,
                                                          final Predicate<E> pPredicate) {
        /* If the item is in range */
        int myNumTypes = pTypes.length;
        if ((pId >= 0)
            && (pId < myNumTypes)) {
            /* Ensure that it passes the test */
            E myType = pTypes[pId];
            if (pPredicate.test(myType)) {
                return myType;
            }
        }

        /* Can't get here */
        throw new IllegalStateException("Invalid id: " + pId);
    }

    /**
     * Generate personalised shuffle of Types.
     * @param <E> the data type
     * @param pTypes the types to be shuffled.
     * @param pIndex the relevant index
     * @return the shuffled types
     */
    private <E extends Enum<E>> E[] shuffleTypes(final E[] pTypes,
                                                 final int pIndex) {
        /* Access input length */
        int myNumTypes = pTypes.length;
        int myLen = myNumTypes;

        /* Allocate a copy of the types */
        E[] myTypes = Arrays.copyOf(pTypes, myNumTypes);

        /* Obtain the personalised integer */
        int mySeed = getPersonalisedInteger(pIndex);

        /* Loop through the types */
        for (int i = 0; i < myLen; i++) {
            /* Access the next element index */
            int myIndex = mySeed % myNumTypes;

            /* If we need to shift the item */
            if (myIndex != 0) {
                /* Access items to swap */
                int myLoc = i + myIndex;
                E myType = myTypes[myLoc];
                E myBase = myTypes[i];

                /* Swap them */
                myTypes[i] = myType;
                myTypes[myLoc] = myBase;
            }

            /* Adjust for next iteration */
            mySeed /= myNumTypes;
            myNumTypes--;
        }

        /* Return the shuffled types */
        return myTypes;
    }

    /**
     * Generate random subSet of Types.
     * @param <E> the data type
     * @param pTypes the source types.
     * @param pCount the count of types to be returned
     * @param pPredicate the predicate to determine whether a type is valid
     * @return the subSet of types
     */
    private <E extends Enum<E>> E[] getRandomTypes(final E[] pTypes,
                                                   final int pCount,
                                                   final Predicate<E> pPredicate) {
        /* Use a random seed */
        return getSeededTypes(pTypes, pCount, theRandom.nextInt(), pPredicate);
    }

    /**
     * Obtain seeded subSet of Types.
     * @param <E> the data type
     * @param pTypes the source types.
     * @param pCount the count of types to be returned
     * @param pSeed the seed for the digest type
     * @param pPredicate the predicate to determine whether a type is valid
     * @return the subSet of types
     */
    private static <E extends Enum<E>> E[] getSeededTypes(final E[] pTypes,
                                                          final int pCount,
                                                          final int pSeed,
                                                          final Predicate<E> pPredicate) {
        /* Access lengths */
        int myTotalTypes = pTypes.length;
        int myNumTypes = pCount;

        /* Allocate a copy of the types */
        E[] mySelection = Arrays.copyOf(pTypes, myTotalTypes);

        /* Ensure that seed is positive */
        int mySeed = (pSeed < 0)
                                 ? -pSeed
                                 : pSeed;

        /* Loop through the types */
        int myNumSelected = 0;
        while (myNumSelected < myNumTypes) {
            /* Access the next element index */
            int iIndex = mySeed % myTotalTypes;

            /* Access the value */
            int myLoc = myNumSelected + iIndex;
            E myType = mySelection[myLoc];

            /* If this is a valid selection */
            if (pPredicate.test(myType)) {
                /* If we need to shift the item */
                if (iIndex != 0) {
                    /* Swap value into place */
                    E myCurr = mySelection[myNumSelected];
                    mySelection[myNumSelected] = myType;
                    mySelection[myLoc] = myCurr;
                }

                /* Increment selection count */
                myNumSelected++;

                /* else we are not interested in this item */
            } else {
                /* If we need to shift the item */
                if (iIndex != myTotalTypes - 1) {
                    /* Swap value out to end */
                    E myCurr = mySelection[myTotalTypes - 1];
                    mySelection[myTotalTypes - 1] = myType;
                    mySelection[myNumSelected] = myCurr;
                }
            }

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

        /* Return the results */
        return Arrays.copyOf(mySelection, myNumTypes);
    }

    /**
     * Obtain integer from personalisation.
     * @param pOffSet the offset within the array
     * @return the result
     */
    private int getPersonalisedInteger(final int pOffSet) {
        /* Loop to obtain the personalised byte */
        int myVal = 0;
        for (int i = 0, myOffSet = pOffSet; i < Integer.BYTES; i++, myOffSet++) {
            myVal <<= Byte.SIZE;
            myVal |= getPersonalisedByte(myOffSet);
        }

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
    private int getPersonalisedByte(final int pOffSet) {
        int myOffSet = pOffSet;
        if (myOffSet >= thePersonalLen) {
            myOffSet %= thePersonalLen;
        }
        return thePersonalisation[myOffSet] & DataConverter.BYTE_MASK;
    }
}
