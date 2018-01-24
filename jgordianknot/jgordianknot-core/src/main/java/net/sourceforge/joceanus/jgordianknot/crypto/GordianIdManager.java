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

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherSpec.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherSpec.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianPersonalisation.GordianPersonalId;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Security Id Manager.
 */
public class GordianIdManager {
    /**
     * The factory.
     */
    private final GordianFactory theFactory;

    /**
     * The SecureRandom.
     */
    private SecureRandom theRandom;

    /**
     * The personalisation.
     */
    private final GordianPersonalisation thePersonalisation;

    /**
     * The cipherSet indent.
     */
    private final int theCipherIndent;

    /**
     * The list of Symmetric Keys.
     */
    private final GordianSymKeyType[] theSymKeys;

    /**
     * The list of keySet Symmetric Keys.
     */
    private final GordianSymKeyType[] theKeySetSymKeys;

    /**
     * The list of Stream Keys.
     */
    private final GordianStreamKeyType[] theStreamKeys;

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
     * Constructor.
     * @param pFactory the security factory
     */
    protected GordianIdManager(final GordianFactory pFactory) {
        /* Store the factory */
        theFactory = pFactory;

        /* Access personalisation */
        thePersonalisation = pFactory.getPersonalisation();

        /* Create shuffled and filtered lists */
        theSymKeys = shuffleTypes(GordianSymKeyType.values(), GordianPersonalId.SYMKEY, pFactory.supportedSymKeyTypes());
        theKeySetSymKeys = shuffleTypes(GordianSymKeyType.values(), GordianPersonalId.KEYSETSYMKEY, pFactory.supportedKeySetSymKeyTypes());
        theStreamKeys = shuffleTypes(GordianStreamKeyType.values(), GordianPersonalId.STREAMKEY, pFactory.supportedStreamKeyTypes());
        theDigests = shuffleTypes(GordianDigestType.values(), GordianPersonalId.DIGEST, pFactory.supportedDigestTypes());
        theExternalDigests = shuffleTypes(GordianDigestType.values(), GordianPersonalId.XTERNDIGEST, pFactory.supportedExternalDigestTypes());
        theKeySetDigests = shuffleTypes(GordianDigestType.values(), GordianPersonalId.KEYSETDIGEST, pFactory.supportedKeySetDigestTypes());
        theHMacDigests = shuffleTypes(GordianDigestType.values(), GordianPersonalId.HMAC, pFactory.supportedHMacDigestTypes());
        theMacs = shuffleTypes(GordianMacType.values(), GordianPersonalId.MAC, pFactory.supportedMacTypes());

        /* Determine the cipher indentation */
        theCipherIndent = thePersonalisation.getPersonalisedByte(GordianPersonalId.CIPHER) & TethysDataConverter.NYBBLE_MASK;
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
        final GordianSymKeyType[] mySymKey = getRandomTypes(theSymKeys, 1);

        /* Return the single SymKeyType */
        return mySymKey[0];
    }

    /**
     * Obtain random keySet SymKeyType.
     * @return the random symKeyType
     */
    private GordianSymKeyType generateRandomKeySetSymKeyType() {
        /* Determine a random symKey */
        final GordianSymKeyType[] mySymKey = getRandomTypes(theKeySetSymKeys, 1);

        /* Return the single SymKeyType */
        return mySymKey[0];
    }

    /**
     * Obtain set of random keySet SymKeyTypes.
     * @param pCount the count
     * @return the random symKeyTypes
     */
    protected GordianSymKeyType[] generateRandomKeySetSymKeyTypes(final int pCount) {
        /* Determine random symKeyTypes */
        return getRandomTypes(theKeySetSymKeys, pCount);
    }

    /**
     * Derive set of keySet SymKeyTypes from seed.
     * @param pSeed the seed
     * @param pKeyTypes the array of symKeyTypes to be filled in
     * @return the remaining seed
     */
    protected int deriveKeySetSymKeyTypesFromSeed(final int pSeed,
                                                  final GordianSymKeyType[] pKeyTypes) {
        return getSeededTypes(theKeySetSymKeys, pKeyTypes, pSeed);
    }

    /**
     * Obtain digestSpec from encodedId.
     * @param pEncodedId the encoded id
     * @return the digestSpec
     * @throws OceanusException on error
     */
    protected GordianSymKeySpec deriveSymKeySpecFromEncodedId(final int pEncodedId) throws OceanusException {
        /* Isolate id Components */
        final int myLenCode = pEncodedId & TethysDataConverter.NYBBLE_MASK;
        final int myId = pEncodedId >> TethysDataConverter.NYBBLE_SHIFT;

        /* Translate components */
        final GordianSymKeyType myType = deriveSymKeyTypeFromEncodedId(myId);
        final GordianLength myLength = deriveLengthFromEncodedId(myLenCode);

        /* Create SymKeySpec */
        return new GordianSymKeySpec(myType, myLength);
    }

    /**
     * Obtain symKeyType from encoded SymKeyId.
     * @param pEncodedId the encoded id
     * @return the symKeyType
     * @throws OceanusException on error
     */
    private GordianSymKeyType deriveSymKeyTypeFromEncodedId(final int pEncodedId) throws OceanusException {
        return deriveTypeFromEncodedId(pEncodedId, theSymKeys);
    }

    /**
     * Obtain encoded SymKeySpecId.
     * @param pSymKeySpec the digestSpec
     * @return the encoded id
     * @throws OceanusException on error
     */
    protected int deriveEncodedIdFromSymKeySpec(final GordianSymKeySpec pSymKeySpec) throws OceanusException {
        int myCode = deriveEncodedIdFromSymKeyType(pSymKeySpec.getSymKeyType());
        myCode <<= TethysDataConverter.NYBBLE_SHIFT;
        myCode += deriveEncodedIdFromLength(pSymKeySpec.getBlockLength());
        return myCode;
    }

    /**
     * Obtain encoded SymKeyId.
     * @param pKey the symKeyType
     * @return the encoded id
     * @throws OceanusException on error
     */
    private int deriveEncodedIdFromSymKeyType(final GordianSymKeyType pKey) throws OceanusException {
        return deriveEncodedIdFromType(pKey, theSymKeys);
    }

    /**
     * Obtain random StreamKeyType.
     * @return the random streamKeyType
     */
    protected GordianStreamKeyType generateRandomStreamKeyType() {
        /* Determine a random streamKeyType */
        final GordianStreamKeyType[] myStreamKey = getRandomTypes(theStreamKeys, 1);

        /* Return the single StreamKeyType */
        return myStreamKey[0];
    }

    /**
     * Obtain streamKeyType from encoded StreamKeyId.
     * @param pEncodedId the encoded id
     * @return the streamKeyType
     * @throws OceanusException on error
     */
    protected GordianStreamKeyType deriveStreamKeyTypeFromEncodedId(final int pEncodedId) throws OceanusException {
        return deriveTypeFromEncodedId(pEncodedId, theStreamKeys);
    }

    /**
     * Obtain encoded StreamKeyId.
     * @param pKey the streamKeyType
     * @return the encoded id
     * @throws OceanusException on error
     */
    protected int deriveEncodedIdFromStreamKeyType(final GordianStreamKeyType pKey) throws OceanusException {
        return deriveEncodedIdFromType(pKey, theStreamKeys);
    }

    /**
     * Obtain random DigestType.
     * @return the random digestType
     */
    protected GordianDigestType generateRandomDigestType() {
        /* Determine a random digestType */
        final GordianDigestType[] myDigest = getRandomTypes(theDigests, 1);

        /* Return the single digestType */
        return myDigest[0];
    }

    /**
     * Obtain random hMacDigestType.
     * @return the random digestType
     */
    protected GordianDigestType generateRandomHMacDigestType() {
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
    protected int deriveKeyHashDigestTypesFromSeed(final int pSeed,
                                                   final GordianDigestType[] pDigestTypes) {
        return getSeededTypes(theKeySetDigests, pDigestTypes, pSeed);
    }

    /**
     * Derive set of standard externalDigestTypes from seed.
     * @param pSeed the seed
     * @param pDigestTypes the array of digestTypes to be filled in
     * @return the remaining seed
     */
    protected int deriveExternalDigestTypesFromSeed(final int pSeed,
                                                    final GordianDigestType[] pDigestTypes) {
        return getSeededTypes(theExternalDigests, pDigestTypes, pSeed);
    }

    /**
     * Obtain digestSpec from encodedId.
     * @param pEncodedId the encoded id
     * @return the digestSpec
     * @throws OceanusException on error
     */
    protected GordianDigestSpec deriveDigestSpecFromEncodedId(final int pEncodedId) throws OceanusException {
        /* Isolate id Components */
        final int myLenCode = pEncodedId & TethysDataConverter.NYBBLE_MASK;
        final int myCode = pEncodedId >> TethysDataConverter.NYBBLE_SHIFT;
        final int myStateCode = myCode & TethysDataConverter.NYBBLE_MASK;
        final int myId = myCode >> TethysDataConverter.NYBBLE_SHIFT;

        /* Translate components */
        final GordianDigestType myType = deriveDigestTypeFromEncodedId(myId);
        final GordianLength myLength = deriveLengthFromEncodedId(myLenCode);
        final GordianLength myState = myStateCode == 0
                                                       ? null
                                                       : deriveLengthFromEncodedId(myStateCode);

        /* Create DigestSpec */
        return new GordianDigestSpec(myType, myState, myLength);
    }

    /**
     * Obtain encoded DigestSpecId.
     * @param pDigestSpec the digestSpec
     * @return the encoded id
     * @throws OceanusException on error
     */
    protected int deriveEncodedIdFromDigestSpec(final GordianDigestSpec pDigestSpec) throws OceanusException {
        int myCode = deriveEncodedIdFromDigestType(pDigestSpec.getDigestType());
        final GordianLength myState = pDigestSpec.getStateLength();
        myCode <<= TethysDataConverter.NYBBLE_SHIFT;
        myCode += myState == null
                                  ? 0
                                  : deriveEncodedIdFromLength(myState);
        myCode <<= TethysDataConverter.NYBBLE_SHIFT;
        myCode += deriveEncodedIdFromLength(pDigestSpec.getDigestLength());
        return myCode;
    }

    /**
     * Obtain encoded DigestTypeId.
     * @param pDigest the digestType
     * @return the encoded id
     * @throws OceanusException on error
     */
    private int deriveEncodedIdFromDigestType(final GordianDigestType pDigest) throws OceanusException {
        return deriveEncodedIdFromType(pDigest, theDigests);
    }

    /**
     * Obtain digestType from encoded Id.
     * @param pEncodedId the encoded id
     * @return the digestType
     * @throws OceanusException on error
     */
    private GordianDigestType deriveDigestTypeFromEncodedId(final int pEncodedId) throws OceanusException {
        return deriveTypeFromEncodedId(pEncodedId, theDigests);
    }

    /**
     * generate random GordianMacSpec.
     * @return the new MacSpec
     */
    protected GordianMacSpec generateRandomMacSpec() {
        final GordianMacType myMacType = generateRandomMacType();
        switch (myMacType) {
            case HMAC:
                final GordianDigestType myDigestType = generateRandomHMacDigestType();
                return GordianMacSpec.hMac(myDigestType);
            case POLY1305:
            case GMAC:
            case CMAC:
                return generateRandomSymKeyMacSpec(myMacType);
            case SKEIN:
                return GordianMacSpec.skeinMac();
            case BLAKE:
                return GordianMacSpec.blakeMac();
            case KUPYNA:
                return GordianMacSpec.kupynaMac();
            case KALYNA:
                return GordianMacSpec.kalynaMac();
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
        final GordianMacType[] myMac = getRandomTypes(theMacs, 1);

        /* Return the single macType */
        return myMac[0];
    }

    /**
     * Obtain random symKey MacSpec.
     * @param pMacType the macType
     * @return the random symKey MacSpec
     */
    private GordianMacSpec generateRandomSymKeyMacSpec(final GordianMacType pMacType) {
        /* Loop until we get a valid Spec */
        for (;;) {
            final GordianSymKeyType mySymType = generateRandomKeySetSymKeyType();
            final GordianMacSpec myMacSpec = new GordianMacSpec(pMacType, new GordianSymKeySpec(mySymType));
            if (theFactory.supportedMacSpecs().test(myMacSpec)) {
                return myMacSpec;
            }
        }
    }

    /**
     * Obtain macSpec from encoded macSpecId.
     * @param pEncodedId the encoded id
     * @return the macSpec
     * @throws OceanusException on error
     */
    protected GordianMacSpec deriveMacSpecFromEncodedId(final int pEncodedId) throws OceanusException {
        /* Isolate id Components */
        final int myId = pEncodedId & TethysDataConverter.NYBBLE_MASK;
        final int myCode = pEncodedId >> TethysDataConverter.NYBBLE_SHIFT;

        /* Determine MacType */
        final GordianMacType myMacType = deriveMacTypeFromEncodedId(myId);

        /* Switch on the MacType */
        switch (myMacType) {
            case HMAC:
                return GordianMacSpec.hMac(deriveDigestSpecFromEncodedId(myCode));
            case GMAC:
            case CMAC:
            case POLY1305:
            case KALYNA:
                return new GordianMacSpec(myMacType, deriveSymKeySpecFromEncodedId(myCode));
            case SKEIN:
                GordianDigestSpec mySpec = deriveDigestSpecFromEncodedId(myCode);
                return GordianMacSpec.skeinMac(mySpec.getStateLength(), mySpec.getDigestLength());
            case BLAKE:
                mySpec = deriveDigestSpecFromEncodedId(myCode);
                return GordianMacSpec.blakeMac(mySpec.getStateLength(), mySpec.getDigestLength());
            case KUPYNA:
                mySpec = deriveDigestSpecFromEncodedId(myCode);
                return GordianMacSpec.kupynaMac(mySpec.getDigestLength());
            default:
                return new GordianMacSpec(myMacType);
        }
    }

    /**
     * Obtain encoded macSpecId.
     * @param pMacSpec the macSpec
     * @return the external id
     * @throws OceanusException on error
     */
    protected int deriveEncodedIdFromMacSpec(final GordianMacSpec pMacSpec) throws OceanusException {
        /* Determine base code */
        final GordianMacType myMacType = pMacSpec.getMacType();
        int myCode = deriveEncodedIdFromMacType(myMacType);

        /* Switch on MacType */
        switch (myMacType) {
            case HMAC:
            case SKEIN:
            case BLAKE:
            case KUPYNA:
                myCode += deriveEncodedIdFromDigestSpec(pMacSpec.getDigestSpec()) << TethysDataConverter.NYBBLE_SHIFT;
                break;
            case GMAC:
            case CMAC:
            case POLY1305:
            case KALYNA:
                myCode += deriveEncodedIdFromSymKeySpec(pMacSpec.getKeySpec()) << TethysDataConverter.NYBBLE_SHIFT;
                break;
            default:
                break;
        }

        /* Return the code */
        return myCode;
    }

    /**
     * Obtain macType from encoded Id.
     * @param pEncodedId the encoded id
     * @return the macType
     * @throws OceanusException on error
     */
    private GordianMacType deriveMacTypeFromEncodedId(final int pEncodedId) throws OceanusException {
        return deriveTypeFromEncodedId(pEncodedId, theMacs);
    }

    /**
     * Obtain encoded MacId.
     * @param pMac the macType
     * @return the encoded id
     * @throws OceanusException on error
     */
    private int deriveEncodedIdFromMacType(final GordianMacType pMac) throws OceanusException {
        return deriveEncodedIdFromType(pMac, theMacs);
    }

    /**
     * Obtain cipherSpec from encoded cipherSpecId.
     * @param pEncodedId the encoded id
     * @return the cipherSpec
     * @throws OceanusException on error
     */
    protected GordianSymCipherSpec deriveSymCipherSpecFromEncodedId(final int pEncodedId) throws OceanusException {
        /* Isolate id Components */
        final int myPaddingCode = pEncodedId & TethysDataConverter.NYBBLE_MASK;
        final int myCode = pEncodedId >> TethysDataConverter.NYBBLE_SHIFT;
        final int myModeCode = myCode & TethysDataConverter.BYTE_MASK;
        final int myId = myCode >> TethysDataConverter.BYTE_SHIFT;

        /* Determine KeyType */
        final GordianSymKeySpec mySpec = deriveSymKeySpecFromEncodedId(myId);
        final GordianCipherMode myMode = deriveCipherModeFromEncodedId(myModeCode);
        final GordianPadding myPadding = derivePaddingFromEncodedId(myPaddingCode);

        /* Switch on the MacType */
        return new GordianSymCipherSpec(mySpec, myMode, myPadding);
    }

    /**
     * Obtain cipherSpec from encoded cipherSpecId.
     * @param pEncodedId the encoded id
     * @return the cipherSpec
     * @throws OceanusException on error
     */
    protected GordianStreamCipherSpec deriveStreamCipherSpecFromEncodedId(final int pEncodedId) throws OceanusException {
        /* Derive StreamCipherSpec */
        final GordianStreamKeyType myType = deriveStreamKeyTypeFromEncodedId(pEncodedId);
        return GordianStreamCipherSpec.stream(myType);
    }

    /**
     * Obtain encoded cipherSpecId.
     * @param pCipherSpec the cipherSpec
     * @return the external id
     * @throws OceanusException on error
     */
    protected int deriveEncodedIdFromCipherSpec(final GordianSymCipherSpec pCipherSpec) throws OceanusException {
        /* Derive the encoded id */
        int myCode = deriveEncodedIdFromSymKeySpec(pCipherSpec.getKeyType());
        myCode <<= TethysDataConverter.BYTE_SHIFT;
        myCode += deriveEncodedIdFromCipherMode(pCipherSpec.getCipherMode());
        myCode <<= TethysDataConverter.NYBBLE_SHIFT;
        myCode += deriveEncodedIdFromPadding(pCipherSpec.getPadding());

        /* Return the code */
        return myCode;
    }

    /**
     * Obtain external cipherSpecId.
     * @param pCipherSpec the cipherSpec
     * @return the external id
     * @throws OceanusException on error
     */
    protected int deriveEncodedIdFromCipherSpec(final GordianStreamCipherSpec pCipherSpec) throws OceanusException {
        /* Derive the encoded id */
        return deriveEncodedIdFromStreamKeyType(pCipherSpec.getKeyType());
    }

    /**
     * Obtain encoded CipherMode.
     * @param pMode the cipherMode
     * @return the encoded id
     */
    private static int deriveEncodedIdFromCipherMode(final GordianCipherMode pMode) {
        return pMode.ordinal() + 1;
    }

    /**
     * Obtain cipherMode from encoded id.
     * @param pEncodedId the encoded id
     * @return the mode
     * @throws OceanusException on error
     */
    private static GordianCipherMode deriveCipherModeFromEncodedId(final int pEncodedId) throws OceanusException {
        final int myId = pEncodedId - 1;
        for (final GordianCipherMode myMode : GordianCipherMode.values()) {
            if (myMode.ordinal() == myId) {
                return myMode;
            }
        }
        throw new GordianDataException("Invalid modeId: " + pEncodedId);
    }

    /**
     * Obtain encoded Padding.
     * @param pPadding the padding
     * @return the encodedId
     */
    private static int deriveEncodedIdFromPadding(final GordianPadding pPadding) {
        return pPadding.ordinal() + 1;
    }

    /**
     * Obtain padding from encoded id.
     * @param pEncodedId the encoded id
     * @return the padding
     * @throws OceanusException on error
     */
    private static GordianPadding derivePaddingFromEncodedId(final int pEncodedId) throws OceanusException {
        final int myId = pEncodedId - 1;
        for (final GordianPadding myPadding : GordianPadding.values()) {
            if (myPadding.ordinal() == myId) {
                return myPadding;
            }
        }
        throw new GordianDataException("Invalid paddingId: " + pEncodedId);
    }

    /**
     * Obtain encoded Length.
     * @param pLength the length
     * @return the encoded id
     */
    private static int deriveEncodedIdFromLength(final GordianLength pLength) {
        return pLength.ordinal() + 1;
    }

    /**
     * Obtain length from encoded id.
     * @param pEncodedId the encoded id
     * @return the length
     * @throws OceanusException on error
     */
    private static GordianLength deriveLengthFromEncodedId(final int pEncodedId) throws OceanusException {
        final int myId = pEncodedId - 1;
        for (final GordianLength myLength : GordianLength.values()) {
            if (myLength.ordinal() == myId) {
                return myLength;
            }
        }
        throw new GordianDataException("Invalid lengthId: " + pEncodedId);
    }

    /**
     * Obtain external Id for type.
     * @param <E> the data type
     * @param pType the type.
     * @param pTypes the type list
     * @return the external Id
     * @throws OceanusException on error
     */

    private static <E extends Enum<E>> int deriveEncodedIdFromType(final E pType,
                                                                   final E[] pTypes) throws OceanusException {
        /* Loop through the types */
        final int myNumTypes = pTypes.length;
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
     * Obtain type from encoded Id.
     * @param <E> the data type
     * @param pId the Id.
     * @param pTypes the type list
     * @return the type
     * @throws OceanusException on error
     */
    private static <E extends Enum<E>> E deriveTypeFromEncodedId(final int pId,
                                                                 final E[] pTypes) throws OceanusException {
        /* If the item is in range */
        final int myNumTypes = pTypes.length;
        if (pId >= 0
            && pId < myNumTypes) {
            return pTypes[pId];
        }

        /* Can't get here */
        throw new GordianDataException("Invalid id: " + pId);
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
