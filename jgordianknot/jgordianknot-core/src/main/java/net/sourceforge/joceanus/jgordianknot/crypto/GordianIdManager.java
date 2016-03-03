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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Security Id Manager.
 */
public class GordianIdManager {
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
     * The Cipher personalisation location.
     */
    private static final int LOC_CIPHER = 17;

    /**
     * The SecureRandom.
     */
    private SecureRandom theRandom;

    /**
     * The personalisation hash.
     */
    private final byte[] thePersonalisation;

    /**
     * The personalisation length.
     */
    private final int thePersonalLen;

    /**
     * The cipherSet indent.
     */
    private final int theCipherIndent;

    /**
     * The list of Symmetric Keys.
     */
    private final GordianSymKeyType[] theSymKeys;

    /**
     * The list of Stream Keys.
     */
    private final GordianStreamKeyType[] theStreamKeys;

    /**
     * The list of Digests.
     */
    private final GordianDigestType[] theDigests;

    /**
     * The list of MACs.
     */
    private final GordianMacType[] theMacs;

    /**
     * Constructor.
     * @param pFactory the security factory
     */
    protected GordianIdManager(final GordianFactory pFactory) {
        /* Access personalisation */
        thePersonalisation = pFactory.getPersonalisation();
        thePersonalLen = thePersonalisation.length;

        /* Create shuffled lists */
        theSymKeys = shuffleTypes(GordianSymKeyType.values(), LOC_SYM);
        theStreamKeys = shuffleTypes(GordianStreamKeyType.values(), LOC_STREAM);
        theDigests = shuffleTypes(GordianDigestType.values(), LOC_DIGEST);
        theMacs = shuffleTypes(GordianMacType.values(), LOC_MAC);

        /* Determine the cipher indentation */
        theCipherIndent = getPersonalisedByte(LOC_CIPHER) & TethysDataConverter.NYBBLE_MASK;
    }

    /**
     * Obtain cipher indentation.
     * @return the cipher indentation
     */
    protected int getCipherIndentation() {
        return theCipherIndent;
    }

    /**
     * Set the secureRandom instance.
     * @param pRandom the secureRandom instance
     */
    protected void setSecureRandom(final SecureRandom pRandom) {
        theRandom = pRandom;
    }

    /**
     * Obtain random SymKeyType.
     * @param pPredicate the predicate to determine whether a type is valid
     * @return the random symKeyType
     */
    protected GordianSymKeyType generateRandomSymKeyType(final Predicate<GordianSymKeyType> pPredicate) {
        /* Determine a random symKey */
        GordianSymKeyType[] mySymKey = getRandomTypes(theSymKeys, 1, pPredicate);

        /* Return the single SymKeyType */
        return mySymKey[0];
    }

    /**
     * Obtain set of random SymKeyTypes.
     * @param pCount the count
     * @param pPredicate the predicate to determine whether a type is valid
     * @return the random symKeyTypes
     */
    protected GordianSymKeyType[] generateRandomSymKeyTypes(final int pCount,
                                                            final Predicate<GordianSymKeyType> pPredicate) {
        /* Determine random symKeyTypes */
        return getRandomTypes(theSymKeys, pCount, pPredicate);
    }

    /**
     * Obtain symKeyType from external SymKeyId.
     * @param pId the external id
     * @return the symKeyType
     * @throws OceanusException on error
     */
    protected GordianSymKeyType deriveSymKeyTypeFromExternalId(final int pId) throws OceanusException {
        return deriveTypeFromExternalId(pId, theSymKeys);
    }

    /**
     * Obtain external SymKeyId.
     * @param pKey the symKeyType
     * @return the external id
     * @throws OceanusException on error
     */
    protected int deriveExternalIdFromSymKeyType(final GordianSymKeyType pKey) throws OceanusException {
        return deriveExternalIdFromType(pKey, theSymKeys);
    }

    /**
     * Obtain random StreamKeyType.
     * @param pPredicate the predicate to determine whether a type is valid
     * @return the random streamKeyType
     */
    protected GordianStreamKeyType generateRandomStreamKeyType(final Predicate<GordianStreamKeyType> pPredicate) {
        /* Determine a random streamKeyType */
        GordianStreamKeyType[] myStreamKey = getRandomTypes(theStreamKeys, 1, pPredicate);

        /* Return the single StreamKeyType */
        return myStreamKey[0];
    }

    /**
     * Obtain streamKeyType from external StreamKeyId.
     * @param pId the external id
     * @return the streamKeyType
     * @throws OceanusException on error
     */
    protected GordianStreamKeyType deriveStreamKeyTypeFromExternalId(final int pId) throws OceanusException {
        return deriveTypeFromExternalId(pId, theStreamKeys);
    }

    /**
     * Obtain external StreamKeyId.
     * @param pKey the streamKeyType
     * @return the external id
     * @throws OceanusException on error
     */
    protected int deriveExternalIdFromStreamKeyType(final GordianStreamKeyType pKey) throws OceanusException {
        return deriveExternalIdFromType(pKey, theStreamKeys);
    }

    /**
     * Obtain random DigestType.
     * @param pPredicate the predicate to determine whether a type is valid
     * @return the random digestType
     */
    protected GordianDigestType generateRandomDigestType(final Predicate<GordianDigestType> pPredicate) {
        /* Determine a random digestType */
        GordianDigestType[] myDigest = getRandomTypes(theDigests, 1, pPredicate);

        /* Return the single digestType */
        return myDigest[0];
    }

    /**
     * Obtain seeded DigestType.
     * @param pSeed the seed for the digest type
     * @param pPredicate the predicate to determine whether a type is valid
     * @return the random digestType
     */
    protected GordianDigestType getSeededDigestType(final int pSeed,
                                                    final Predicate<GordianDigestType> pPredicate) {
        /* Determine the seeded digestType */
        GordianDigestType[] myDigest = getSeededTypes(theDigests, 1, pSeed, pPredicate);

        /* Return the single digestType */
        return myDigest[0];
    }

    /**
     * Obtain set of random DigestTypes.
     * @param pCount the count
     * @param pPredicate the predicate to determine whether a type is valid
     * @return the random digestTypes
     */
    protected GordianDigestType[] generateRandomDigestTypes(final int pCount,
                                                            final Predicate<GordianDigestType> pPredicate) {
        return getRandomTypes(theDigests, pCount, pPredicate);
    }

    /**
     * Obtain digestType from external DigestId.
     * @param pId the external id
     * @return the digestType
     * @throws OceanusException on error
     */
    protected GordianDigestType deriveDigestTypeFromExternalId(final int pId) throws OceanusException {
        return deriveTypeFromExternalId(pId, theDigests);
    }

    /**
     * Obtain external DigestId.
     * @param pDigest the digestType
     * @return the external id
     * @throws OceanusException on error
     */
    protected int deriveExternalIdFromDigestType(final GordianDigestType pDigest) throws OceanusException {
        return deriveExternalIdFromType(pDigest, theDigests);
    }

    /**
     * Obtain random MacType.
     * @param pPredicate the predicate to determine whether a type is valid
     * @return the random macType
     */
    protected GordianMacType generateRandomMacType(final Predicate<GordianMacType> pPredicate) {
        /* Determine a random digest */
        GordianMacType[] myMac = getRandomTypes(theMacs, 1, pPredicate);

        /* Return the single macType */
        return myMac[0];
    }

    /**
     * Obtain macSpec from external MacSpecId.
     * @param pId the external id
     * @return the macSpec
     * @throws OceanusException on error
     */
    protected GordianMacSpec deriveMacSpecFromExternalId(final int pId) throws OceanusException {
        /* Isolate id Components */
        int myId = pId & TethysDataConverter.NYBBLE_MASK;
        int myCode = pId >> TethysDataConverter.NYBBLE_SHIFT;

        /* Determine MacType */
        GordianMacType myMacType = deriveMacTypeFromExternalId(myId);

        /* Switch on the MacType */
        switch (myMacType) {
            case HMAC:
                return new GordianMacSpec(myMacType, deriveDigestTypeFromExternalId(myCode));
            case GMAC:
            case POLY1305:
                return new GordianMacSpec(myMacType, deriveSymKeyTypeFromExternalId(myCode));
            default:
                return new GordianMacSpec(myMacType);
        }
    }

    /**
     * Obtain macType from external MacId.
     * @param pId the external id
     * @return the macType
     * @throws OceanusException on error
     */
    private GordianMacType deriveMacTypeFromExternalId(final int pId) throws OceanusException {
        return deriveTypeFromExternalId(pId, theMacs);
    }

    /**
     * Obtain external MacSpecId.
     * @param pMacSpec the macSpec
     * @return the external id
     * @throws OceanusException on error
     */
    protected int deriveExternalIdFromMacSpec(final GordianMacSpec pMacSpec) throws OceanusException {
        /* Determine base code */
        GordianMacType myMacType = pMacSpec.getMacType();
        int myCode = deriveExternalIdFromMacType(myMacType);

        /* Switch on MacType */
        switch (myMacType) {
            case HMAC:
                myCode += deriveExternalIdFromDigestType(pMacSpec.getDigestType()) << TethysDataConverter.NYBBLE_SHIFT;
                break;
            case GMAC:
            case POLY1305:
                myCode += deriveExternalIdFromSymKeyType(pMacSpec.getKeyType()) << TethysDataConverter.NYBBLE_SHIFT;
                break;
            default:
                break;
        }

        /* Return the code */
        return myCode;
    }

    /**
     * Obtain external MacId.
     * @param pMac the macType
     * @return the external id
     * @throws OceanusException on error
     */
    private int deriveExternalIdFromMacType(final GordianMacType pMac) throws OceanusException {
        return deriveExternalIdFromType(pMac, theMacs);
    }

    /**
     * Obtain external CipherMode.
     * @param pMode the cipherMode
     * @return the external id
     * @throws OceanusException on error
     */
    protected int deriveExternalIdFromCipherMode(final GordianCipherMode pMode) throws OceanusException {
        return pMode.ordinal();
    }

    /**
     * Obtain macType from external MacId.
     * @param pId the external id
     * @return the macType
     * @throws OceanusException on error
     */
    private static GordianCipherMode deriveCipherModeFromExternalId(final int pId) throws OceanusException {
        for (GordianCipherMode myMode : GordianCipherMode.values()) {
            if (myMode.ordinal() == pId) {
                return myMode;
            }
        }
        throw new GordianDataException("Invalid modeId: " + pId);
    }

    /**
     * Obtain Type from external Id.
     * @param <T> the type class
     * @param pId the external id
     * @param pTypeClass the type class
     * @return the Type
     * @throws OceanusException on error
     */
    protected <T> T deriveTypeFromExternalId(final int pId,
                                             final Class<T> pTypeClass) throws OceanusException {
        if (GordianDigestType.class.equals(pTypeClass)) {
            return pTypeClass.cast(deriveDigestTypeFromExternalId(pId));
        }
        if (GordianSymKeyType.class.equals(pTypeClass)) {
            return pTypeClass.cast(deriveSymKeyTypeFromExternalId(pId));
        }
        if (GordianStreamKeyType.class.equals(pTypeClass)) {
            return pTypeClass.cast(deriveStreamKeyTypeFromExternalId(pId));
        }
        if (GordianMacSpec.class.equals(pTypeClass)) {
            return pTypeClass.cast(deriveMacSpecFromExternalId(pId));
        }
        if (GordianCipherMode.class.equals(pTypeClass)) {
            return pTypeClass.cast(deriveCipherModeFromExternalId(pId));
        }
        throw new GordianDataException("Invalid class: " + pTypeClass.getCanonicalName());
    }

    /**
     * Obtain external Id from Type.
     * @param <T> the type class
     * @param pType the type
     * @return the externalId
     * @throws OceanusException on error
     */
    protected <T> int deriveExternalIdFromType(final T pType) throws OceanusException {
        if (GordianDigestType.class.isInstance(pType)) {
            return deriveExternalIdFromDigestType((GordianDigestType) pType);
        }
        if (GordianSymKeyType.class.isInstance(pType)) {
            return deriveExternalIdFromSymKeyType((GordianSymKeyType) pType);
        }
        if (GordianStreamKeyType.class.isInstance(pType)) {
            return deriveExternalIdFromStreamKeyType((GordianStreamKeyType) pType);
        }
        if (GordianMacSpec.class.isInstance(pType)) {
            return deriveExternalIdFromMacSpec((GordianMacSpec) pType);
        }
        if (GordianCipherMode.class.isInstance(pType)) {
            return deriveExternalIdFromCipherMode((GordianCipherMode) pType);
        }
        throw new GordianDataException("Invalid type: " + pType);
    }

    /**
     * Obtain external Id for type.
     * @param <E> the data type
     * @param pType the type.
     * @param pTypes the type list
     * @return the external Id
     * @throws OceanusException on error
     */

    private static <E extends Enum<E>> int deriveExternalIdFromType(final E pType,
                                                                    final E[] pTypes) throws OceanusException {
        /* Loop through the types */
        int myNumTypes = pTypes.length;
        for (int i = 0; i < myNumTypes; i++) {
            /* return the match */
            if (pType == pTypes[i]) {
                return i;
            }
        }

        /* Can't get here */
        throw new GordianDataException("Invalid item");
    }

    /**
     * Obtain type by Id.
     * @param <E> the data type
     * @param pId the Id.
     * @param pTypes the type list
     * @return the type
     * @throws OceanusException on error
     */
    private static <E extends Enum<E>> E deriveTypeFromExternalId(final int pId,
                                                                  final E[] pTypes) throws OceanusException {
        /* If the item is in range */
        int myNumTypes = pTypes.length;
        if ((pId >= 0)
            && (pId < myNumTypes)) {
            return pTypes[pId];
        }

        /* Can't get here */
        throw new GordianDataException("Invalid id: " + pId);
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
     * @param pSeed the seed for the types
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
                    int myLast = myNumSelected + myTotalTypes - 1;
                    E myCurr = mySelection[myLast];
                    mySelection[myLast] = myType;
                    mySelection[myLoc] = myCurr;
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
        return thePersonalisation[myOffSet] & TethysDataConverter.BYTE_MASK;
    }
}
