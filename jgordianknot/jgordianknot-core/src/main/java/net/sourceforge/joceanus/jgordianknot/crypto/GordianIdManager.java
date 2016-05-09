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
     * The Standard Symmetric personalisation location.
     */
    private static final int LOC_STDSYM = 7;

    /**
     * The Stream personalisation location.
     */
    private static final int LOC_STREAM = 11;

    /**
     * The Digest personalisation location.
     */
    private static final int LOC_DIGEST = 13;

    /**
     * The Mac personalisation location.
     */
    private static final int LOC_MAC = 17;

    /**
     * The Cipher personalisation location.
     */
    private static final int LOC_CIPHER = 19;

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
     * The list of Standard Symmetric Keys.
     */
    private final GordianSymKeyType[] theStdSymKeys;

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

        /* Create shuffled and filtered lists */
        theSymKeys = shuffleTypes(GordianSymKeyType.values(), LOC_SYM, pFactory.supportedSymKeys());
        theStdSymKeys = shuffleTypes(GordianSymKeyType.values(), LOC_STDSYM, pFactory.standardSymKeys());
        theStreamKeys = shuffleTypes(GordianStreamKeyType.values(), LOC_STREAM, pFactory.supportedStreamKeys());
        theDigests = shuffleTypes(GordianDigestType.values(), LOC_DIGEST, pFactory.supportedDigests());
        theMacs = shuffleTypes(GordianMacType.values(), LOC_MAC, pFactory.supportedMacs());

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
     * @return the random symKeyType
     */
    protected GordianSymKeyType generateRandomSymKeyType() {
        /* Determine a random symKey */
        GordianSymKeyType[] mySymKey = getRandomTypes(theSymKeys, 1);

        /* Return the single SymKeyType */
        return mySymKey[0];
    }

    /**
     * Obtain random standard SymKeyType.
     * @return the random symKeyType
     */
    private GordianSymKeyType generateRandomStdSymKeyType() {
        /* Determine a random symKey */
        GordianSymKeyType[] mySymKey = getRandomTypes(theStdSymKeys, 1);

        /* Return the single SymKeyType */
        return mySymKey[0];
    }

    /**
     * Obtain set of random SymKeyTypes.
     * @param pCount the count
     * @return the random symKeyTypes
     */
    protected GordianSymKeyType[] generateRandomSymKeyTypes(final int pCount) {
        /* Determine random symKeyTypes */
        return getRandomTypes(theSymKeys, pCount);
    }

    /**
     * Derive set of standard SymKeyTypes from seed.
     * @param pSeed the seed
     * @param pCount the count
     * @return the symKeyTypes
     */
    protected GordianSymKeyType[] deriveSymKeyTypesFromSeed(final int pSeed,
                                                            final int pCount) {
        GordianSymKeyType[] myResult = Arrays.copyOf(theStdSymKeys, pCount);
        getSeededTypes(theStdSymKeys, myResult, pSeed);
        return myResult;
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
     * Obtain symKeyType from external standard SymKeyId.
     * @param pId the external id
     * @return the symKeyType
     * @throws OceanusException on error
     */
    private GordianSymKeyType deriveStdSymKeyTypeFromExternalId(final int pId) throws OceanusException {
        return deriveTypeFromExternalId(pId, theStdSymKeys);
    }

    /**
     * Obtain external SymKeyId.
     * @param pKey the symKeyType
     * @return the external id
     * @throws OceanusException on error
     */
    private int deriveExternalIdFromStdSymKeyType(final GordianSymKeyType pKey) throws OceanusException {
        return deriveExternalIdFromType(pKey, theStdSymKeys);
    }

    /**
     * Obtain random StreamKeyType.
     * @return the random streamKeyType
     */
    protected GordianStreamKeyType generateRandomStreamKeyType() {
        /* Determine a random streamKeyType */
        GordianStreamKeyType[] myStreamKey = getRandomTypes(theStreamKeys, 1);

        /* Return the single StreamKeyType */
        return myStreamKey[0];
    }

    /**
     * Obtain streamKeyType from external StreamKeyId.
     * @param pId the external id
     * @return the streamKeyType
     * @throws OceanusException on error
     */
    private GordianStreamKeyType deriveStreamKeyTypeFromExternalId(final int pId) throws OceanusException {
        return deriveTypeFromExternalId(pId, theStreamKeys);
    }

    /**
     * Obtain external StreamKeyId.
     * @param pKey the streamKeyType
     * @return the external id
     * @throws OceanusException on error
     */
    private int deriveExternalIdFromStreamKeyType(final GordianStreamKeyType pKey) throws OceanusException {
        return deriveExternalIdFromType(pKey, theStreamKeys);
    }

    /**
     * Obtain random DigestType.
     * @return the random digestType
     */
    protected GordianDigestType generateRandomDigestType() {
        /* Determine a random digestType */
        GordianDigestType[] myDigest = getRandomTypes(theDigests, 1);

        /* Return the single digestType */
        return myDigest[0];
    }

    /**
     * Derive set of standard DigestTypes from seed.
     * @param pSeed the seed
     * @param pCount the count
     * @return the digestTypes
     */
    protected GordianDigestType[] deriveDigestTypesFromSeed(final int pSeed,
                                                            final int pCount) {
        GordianDigestType[] myResult = Arrays.copyOf(theDigests, pCount);
        getSeededTypes(theDigests, myResult, pSeed);
        return myResult;
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
     * generate random GordianMacSpec.
     * @return the new MacSpec
     */
    protected GordianMacSpec generateRandomMacSpec() {
        GordianMacType myMacType = generateRandomMacType();
        switch (myMacType) {
            case HMAC:
                GordianDigestType myDigestType = generateRandomDigestType();
                return new GordianMacSpec(myMacType, myDigestType);
            case POLY1305:
            case GMAC:
                GordianSymKeyType mySymType = generateRandomStdSymKeyType();
                return new GordianMacSpec(myMacType, mySymType);
            default:
                return new GordianMacSpec(myMacType);
        }
    }

    /**
     * Obtain random MacType.
     * @return the random macType
     */
    private GordianMacType generateRandomMacType() {
        /* Determine a random digest */
        GordianMacType[] myMac = getRandomTypes(theMacs, 1);

        /* Return the single macType */
        return myMac[0];
    }

    /**
     * Obtain macSpec from external MacSpecId.
     * @param pId the external id
     * @return the macSpec
     * @throws OceanusException on error
     */
    private GordianMacSpec deriveMacSpecFromExternalId(final int pId) throws OceanusException {
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
                return new GordianMacSpec(myMacType, deriveStdSymKeyTypeFromExternalId(myCode));
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
    private int deriveExternalIdFromMacSpec(final GordianMacSpec pMacSpec) throws OceanusException {
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
                myCode += deriveExternalIdFromStdSymKeyType(pMacSpec.getKeyType()) << TethysDataConverter.NYBBLE_SHIFT;
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
     * Obtain cipherMode from external ModeId.
     * @param pId the external id
     * @return the mode
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
     * Obtain external Padding.
     * @param pPadding the padding
     * @return the external id
     * @throws OceanusException on error
     */
    protected int deriveExternalIdFromPadding(final GordianPadding pPadding) throws OceanusException {
        return pPadding.ordinal();
    }

    /**
     * Obtain padding from external PaddingId.
     * @param pId the external id
     * @return the padding
     * @throws OceanusException on error
     */
    private static GordianPadding derivePaddingFromExternalId(final int pId) throws OceanusException {
        for (GordianPadding myPadding : GordianPadding.values()) {
            if (myPadding.ordinal() == pId) {
                return myPadding;
            }
        }
        throw new GordianDataException("Invalid paddingId: " + pId);
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
        if (GordianPadding.class.equals(pTypeClass)) {
            return pTypeClass.cast(derivePaddingFromExternalId(pId));
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
        if (GordianPadding.class.isInstance(pType)) {
            return deriveExternalIdFromPadding((GordianPadding) pType);
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
     * @param pFilter the filter
     * @return the shuffled types
     */
    private <E extends Enum<E>> E[] shuffleTypes(final E[] pTypes,
                                                 final int pIndex,
                                                 final Predicate<E> pFilter) {
        /* Access input length */
        int myNumTypes = pTypes.length;
        int myLen = myNumTypes;

        /* Allocate a copy of the types */
        E[] myTypes = Arrays.copyOf(pTypes, myNumTypes);

        /* Obtain the personalised integer */
        int mySeed = getPersonalisedInteger(pIndex);

        /* Loop through the types */
        int myNumAvailable = 0;
        for (int i = 0; i < myLen; i++) {
            /* Access the next element index */
            int myIndex = mySeed % myNumTypes;

            /* Access the value */
            int myLoc = myNumAvailable + myIndex;
            E myType = myTypes[myLoc];

            /* If this is a valid selection */
            if (pFilter.test(myType)) {
                /* If we need to shift the item */
                if (myIndex != 0) {
                    /* Swap value into place */
                    E myCurr = myTypes[myNumAvailable];
                    myTypes[myNumAvailable] = myType;
                    myTypes[myLoc] = myCurr;
                }

                /* Increment available count */
                myNumAvailable++;

                /* else we are not interested in this item */
            } else {
                /* If we need to shift the item */
                if (myIndex != myNumTypes - 1) {
                    /* Swap value out to end */
                    int myLast = myNumAvailable + myNumTypes - 1;
                    E myCurr = myTypes[myLast];
                    myTypes[myLast] = myType;
                    myTypes[myLoc] = myCurr;
                }
            }

            /* Adjust for next iteration */
            mySeed /= myNumTypes;
            myNumTypes--;
        }

        /* Return the shuffled and filtered types */
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
        E[] myResult = Arrays.copyOf(pTypes, pCount);
        getSeededTypes(pTypes, myResult, theRandom.nextInt());
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
        int myNumTypes = pSelected.length;

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

            /* If we need to shift the item */
            if (iIndex != 0) {
                /* Swap value into place */
                E myCurr = mySelection[myNumSelected];
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
