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

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianIdSpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherMode;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianPadding;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipherSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianBlakeXofKey;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianChaCha20Key;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianElephantKey;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianISAPKey;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianSalsa20Key;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianSkeinXofKey;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianSparkleKey;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianStreamSubKeyType;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianVMPCKey;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeyType;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSubSpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSubSpec.GordianDigestState;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianKnuthObfuscater;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacType;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianSipHashSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianPersonalisation.GordianPersonalId;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Knuth Obfuscater.
 */
public class GordianCoreKnuthObfuscater
    implements GordianKnuthObfuscater {
    /**
     * Make sure that the top positive bit is set for the Knuth Prime.
     */
    private static final int VALUE_MASK = 0x40000000;

    /**
     * Knuth Prime.
     */
    private final int thePrime;

    /**
     * Knuth Inverse.
     */
    private final int theInverse;

    /**
     * Knuth Mask.
     */
    private final int theMask;

    /**
     * Constructor.
     * @param pFactory the factory
     * @throws GordianException on error
     */
    GordianCoreKnuthObfuscater(final GordianCoreFactory pFactory) throws GordianException {
        /* Generate Knuth Prime/Inverse */
        final GordianPersonalisation myPersonal = pFactory.getPersonalisation();
        final BigInteger[] myKnuth = generatePrime(myPersonal.getPersonalisedInteger(GordianPersonalId.KNUTHPRIME));
        thePrime = myKnuth[0].intValue();
        theInverse = myKnuth[1].intValue();
        theMask = myPersonal.getPersonalisedInteger(GordianPersonalId.KNUTHMASK);
    }

    /**
     * Obtain a large integer prime based on the supplied value.
     * @param pBase the base value
     * @return the encoded value
     */
    private static BigInteger[] generatePrime(final int pBase) {
        /* Ensure that the value is positive */
        int myVal = pBase < 0
                      ? -pBase
                      : pBase;

        /* Ensure that the top positive bit is set */
        myVal |= VALUE_MASK;

        /* Make sure that the value is prime */
        BigInteger myValue = BigInteger.valueOf(myVal);
        if (!myValue.isProbablePrime(Integer.SIZE)) {
            myValue = myValue.nextProbablePrime();
        }

        /* Calculate the inverse */
        final BigInteger myMax = BigInteger.valueOf(1).shiftLeft(Integer.SIZE);
        final BigInteger myInverse = myValue.modInverse(myMax);

        /* Return the pair of values */
        return new BigInteger[]
                { myValue, myInverse };
    }

    /**
     * Encode an integer value via Knuth Multiplication.
     * @param pInput the input
     * @return the encoded value
     */
    public int knuthEncodeInteger(final int pInput) {
        return (int) ((pInput ^ theMask) * (long) thePrime);
    }

    /**
     * Encode an integer value via Knuth Multiplication.
     * @param pInput the input
     * @param pAdjustment the adjustment
     * @return the encoded value
     */
    public int knuthEncodeInteger(final int pInput,
                                  final int pAdjustment) {
        final int myId = pInput + pAdjustment;
        return knuthEncodeInteger(myId);
    }

    /**
     * Decode a Knuth Encoded integer value.
     * @param pEncoded the encoded value
     * @return the original input
     */
    public int knuthDecodeInteger(final int pEncoded) {
        return theMask ^ (int) (pEncoded * (long) theInverse);
    }

    /**
     * Decode a Knuth Encoded integer value.
     * @param pEncoded the encoded value
     * @param pAdjustment the adjustment
     * @return the original input
     */
    public int knuthDecodeInteger(final int pEncoded,
                                  final int pAdjustment) {
        final int myId = knuthDecodeInteger(pEncoded);
        return myId - pAdjustment;
    }

    /**
     * Encode a long value via Knuth Multiplication.
     * @param pInput the input
     * @return the encoded value
     */
    public long knuthEncodeLong(final long pInput) {
        final long myHigh = knuthEncodeInteger((int) (pInput >>> Integer.SIZE));
        final int myLow = knuthEncodeInteger((int) pInput);
        return (myHigh << Integer.SIZE) | Integer.toUnsignedLong(myLow);
    }

    /**
     * Encode a long value via Knuth Multiplication.
     * @param pInput the input
     * @param pAdjustment the adjustment
     * @return the encoded value
     */
    public long knuthEncodeLong(final long pInput,
                                final int pAdjustment) {
        final long myHigh = knuthEncodeInteger((int) (pInput >>> Integer.SIZE), pAdjustment);
        final int myLow = knuthEncodeInteger((int) pInput, pAdjustment);
        return (myHigh << Integer.SIZE) | Integer.toUnsignedLong(myLow);
    }

    /**
     * Decode a Knuth Encoded long value.
     * @param pEncoded the encoded value
     * @return the original input
     */
    public long knuthDecodeLong(final long pEncoded) {
        final long myHigh = knuthDecodeInteger((int) (pEncoded >>> Integer.SIZE));
        final int myLow = knuthDecodeInteger((int) pEncoded);
        return (myHigh << Integer.SIZE) | Integer.toUnsignedLong(myLow);
    }

    /**
     * Decode a Knuth Encoded long value.
     * @param pEncoded the encoded value
     * @param pAdjustment the adjustment
     * @return the original input
     */
    public long knuthDecodeLong(final long pEncoded,
                                final int pAdjustment) {
        final long myHigh = knuthDecodeInteger((int) (pEncoded >>> Integer.SIZE), pAdjustment);
        final int myLow = knuthDecodeInteger((int) pEncoded, pAdjustment);
        return (myHigh << Integer.SIZE) | Integer.toUnsignedLong(myLow);
    }

    @Override
    public int deriveExternalIdFromType(final GordianIdSpec pType,
                                        final int pAdjustment) throws GordianException {
        return knuthEncodeInteger(deriveEncodedIdFromType(pType), pAdjustment);
    }

    @Override
    public int deriveExternalIdFromType(final GordianIdSpec pType) throws GordianException {
        return knuthEncodeInteger(deriveEncodedIdFromType(pType));
    }

    /**
     * Obtain external Id from Type.
     * @param <T> the type class
     * @param pType the type
     * @return the externalId
     * @throws GordianException on error
     */
    private static <T extends GordianIdSpec> int deriveEncodedIdFromType(final T pType) throws GordianException {
        if (pType instanceof GordianDigestSpec) {
            final int myId = deriveEncodedIdFromDigestSpec((GordianDigestSpec) pType);
            return GordianIdMarker.DIGEST.applyMarker(myId);
        }
        if (pType instanceof GordianSymCipherSpec) {
            final int myId = deriveEncodedIdFromSymCipherSpec((GordianSymCipherSpec) pType);
            return GordianIdMarker.SYMCIPHER.applyMarker(myId);
        }
        if (pType instanceof GordianStreamCipherSpec) {
            final int myId = deriveEncodedIdFromStreamCipherSpec((GordianStreamCipherSpec) pType);
            return GordianIdMarker.STREAMCIPHER.applyMarker(myId);
        }
        if (pType instanceof GordianMacSpec) {
            final int myId = deriveEncodedIdFromMacSpec((GordianMacSpec) pType);
            return GordianIdMarker.MACKEY.applyMarker(myId);
        }
        if (pType instanceof GordianSymKeySpec) {
            final int myId = deriveEncodedIdFromSymKeySpec((GordianSymKeySpec) pType);
            return GordianIdMarker.SYMKEY.applyMarker(myId);
        }
        if (pType instanceof GordianStreamKeySpec) {
            final int myId = deriveEncodedIdFromStreamKeySpec((GordianStreamKeySpec) pType);
            return GordianIdMarker.STREAMKEY.applyMarker(myId);
        }
        throw new GordianDataException("Invalid type: " + pType.getClass().getCanonicalName());
    }

    @Override
    public GordianIdSpec deriveTypeFromExternalId(final int pId,
                                                  final int pAdjustment) throws GordianException {
        return deriveTypeFromEncodedId(knuthDecodeInteger(pId, pAdjustment));
    }

    @Override
    public GordianIdSpec deriveTypeFromExternalId(final int pId) throws GordianException {
        return deriveTypeFromEncodedId(knuthDecodeInteger(pId));
    }

    /**
     * Obtain Type from external Id.
     * @param pId the external id
     * @return the Type
     * @throws GordianException on error
     */
    private static GordianIdSpec deriveTypeFromEncodedId(final int pId) throws GordianException {
        final GordianIdMarker myMarker = GordianIdMarker.determine(pId);
        final int myId = GordianIdMarker.removeMarker(pId);
        switch (myMarker) {
            case DIGEST:
                return deriveDigestSpecFromEncodedId(myId);
            case SYMCIPHER:
                return deriveSymCipherSpecFromEncodedId(myId);
            case STREAMCIPHER:
                return deriveStreamCipherSpecFromEncodedId(myId);
            case MACKEY:
                return deriveMacSpecFromEncodedId(myId);
            case SYMKEY:
                return deriveSymKeySpecFromEncodedId(myId);
            case STREAMKEY:
                return deriveStreamKeySpecFromEncodedId(myId);
            default:
                throw new GordianDataException("Unsupported encoding");
        }
    }

    /**
     * Obtain encoded DigestSpecId.
     * @param pDigestSpec the digestSpec
     * @return the encoded id
     */
    private static int deriveEncodedIdFromDigestSpec(final GordianDigestSpec pDigestSpec) {
        /* Build the encoded id */
        int myCode = deriveEncodedIdFromDigestType(pDigestSpec.getDigestType());
        final GordianDigestState myState = pDigestSpec.getDigestState();
        myCode <<= determineShiftForDigestSubSpec();
        if (myState != null) {
            myCode += deriveEncodedIdFromDigestState(myState);
        }
        myCode <<= determineShiftForEnum(GordianLength.class);
        myCode += deriveEncodedIdFromLength(pDigestSpec.getDigestLength());
        myCode <<= 1;
        myCode += pDigestSpec.isXofMode() ? 1 : 0;

        /* return the code */
        return myCode;
    }

    /**
     * Obtain digestSpec from encodedId.
     * @param pEncodedId the encoded id
     * @return the digestSpec
     * @throws GordianException on error
     */
    private static GordianDigestSpec deriveDigestSpecFromEncodedId(final int pEncodedId) throws GordianException {
        /* Isolate id Components */
        final boolean isXof = (pEncodedId & 1) == 1;
        int myCode = pEncodedId >> 1;
        final int myLenCode = myCode & determineMaskForEnum(GordianLength.class);
        myCode = myCode >> determineShiftForEnum(GordianLength.class);
        final int mySubSpecCode = myCode & determineMaskForDigestSubSpec();
        final int myId = myCode >> determineShiftForDigestSubSpec();

        /* Translate components */
        final GordianDigestType myType = deriveDigestTypeFromEncodedId(myId);
        final GordianLength myLength = deriveLengthFromEncodedId(myLenCode);
        GordianDigestSubSpec mySubSpec = null;
        if (mySubSpecCode != 0) {
            mySubSpec = deriveDigestStateFromEncodedId(mySubSpecCode);
        }

        /* Create DigestSpec */
        return new GordianDigestSpec(myType, mySubSpec, myLength, isXof);
    }

    /**
     * Obtain encoded SymKeySpecId.
     * @param pSymKeySpec the symKeySpec
     * @return the encoded id
     */
    private static int deriveEncodedIdFromSymKeySpec(final GordianSymKeySpec pSymKeySpec) {
        /* Build the encoded id */
        int myCode = deriveEncodedIdFromSymKeyType(pSymKeySpec.getSymKeyType());
        myCode <<= determineShiftForEnum(GordianLength.class);
        myCode += deriveEncodedIdFromLength(pSymKeySpec.getBlockLength());
        myCode <<= determineShiftForEnum(GordianLength.class);
        myCode += deriveEncodedIdFromLength(pSymKeySpec.getKeyLength());

        /* return the code */
        return myCode;
    }

    /**
     * Obtain symKeySpec from encodedId.
     * @param pEncodedId the encoded id
     * @return the symKeySpec
     * @throws GordianException on error
     */
    private static GordianSymKeySpec deriveSymKeySpecFromEncodedId(final int pEncodedId) throws GordianException {
        /* Isolate id Components */
        final int myKeyLenCode = pEncodedId & determineMaskForEnum(GordianLength.class);
        final int myCode = pEncodedId >> determineShiftForEnum(GordianLength.class);
        final int myBlkLenCode = myCode & determineMaskForEnum(GordianLength.class);
        final int myId = myCode >> determineShiftForEnum(GordianLength.class);

        /* Translate components */
        final GordianSymKeyType myType = deriveSymKeyTypeFromEncodedId(myId);
        final GordianLength myBlkLength = deriveLengthFromEncodedId(myBlkLenCode);
        final GordianLength myKeyLength = deriveLengthFromEncodedId(myKeyLenCode);

        /* Create SymKeySpec */
        return new GordianSymKeySpec(myType, myBlkLength, myKeyLength);
    }

    /**
     * Obtain encoded symCipherSpecId.
     * @param pCipherSpec the symCipherSpec
     * @return the external id
     */
    private static int deriveEncodedIdFromSymCipherSpec(final GordianSymCipherSpec pCipherSpec) {
        /* Derive the encoded id */
        int myCode = deriveEncodedIdFromSymKeySpec(pCipherSpec.getKeyType());
        myCode <<= determineShiftForEnum(GordianCipherMode.class);
        myCode += deriveEncodedIdFromCipherMode(pCipherSpec.getCipherMode());
        myCode <<= determineShiftForEnum(GordianPadding.class);
        myCode += deriveEncodedIdFromPadding(pCipherSpec.getPadding());

        /* Return the code */
        return myCode;
    }

    /**
     * Obtain cipherSpec from encoded symCipherSpecId.
     * @param pEncodedId the encoded id
     * @return the symCipherSpec
     * @throws GordianException on error
     */
    private static GordianSymCipherSpec deriveSymCipherSpecFromEncodedId(final int pEncodedId) throws GordianException {
        /* Isolate id Components */
        final int myPaddingCode = pEncodedId & determineMaskForEnum(GordianPadding.class);
        final int myCode = pEncodedId >> determineShiftForEnum(GordianPadding.class);
        final int myModeCode = myCode & determineMaskForEnum(GordianCipherMode.class);
        final int myId = myCode >> determineShiftForEnum(GordianCipherMode.class);

        /* Determine KeyType */
        final GordianSymKeySpec mySpec = deriveSymKeySpecFromEncodedId(myId);
        final GordianCipherMode myMode = deriveCipherModeFromEncodedId(myModeCode);
        final GordianPadding myPadding = derivePaddingFromEncodedId(myPaddingCode);

        /* Create the cipherSpec */
        return new GordianSymCipherSpec(mySpec, myMode, myPadding);
    }

    /**
     * Obtain encoded StreamKeySpecId.
     * @param pStreamKeySpec the streamKeySpec
     * @return the encoded id
     */
    private static int deriveEncodedIdFromStreamKeySpec(final GordianStreamKeySpec pStreamKeySpec) {
        /* Build the encoded id */
        int myCode = deriveEncodedIdFromStreamKeyType(pStreamKeySpec.getStreamKeyType());
        myCode <<= determineShiftForEnum(GordianLength.class);
        myCode += deriveEncodedIdFromLength(pStreamKeySpec.getKeyLength());
        myCode <<= determineShiftForStreamKeySubType();
        myCode += deriveEncodedIdFromStreamKeySubType(pStreamKeySpec);

        /* return the code */
        return myCode;
    }

    /**
     * Obtain streamKeySpec from encodedId.
     * @param pEncodedId the encoded id
     * @return the streamKeySpec
     * @throws GordianException on error
     */
    private static GordianStreamKeySpec deriveStreamKeySpecFromEncodedId(final int pEncodedId) throws GordianException {
        /* Isolate id Components */
        final int mySubKeyCode = pEncodedId & determineMaskForStreamKeySubType();
        final int myCode = pEncodedId >> determineShiftForStreamKeySubType();
        final int myKeyLenCode = myCode & determineMaskForEnum(GordianLength.class);
        final int myId = myCode >> determineShiftForEnum(GordianLength.class);

        /* Translate components */
        final GordianStreamKeyType myType = deriveStreamKeyTypeFromEncodedId(myId);
        final GordianLength myKeyLength = deriveLengthFromEncodedId(myKeyLenCode);
        final GordianStreamSubKeyType mySubKeyType = deriveStreamSubKeyTypeFromEncodedId(myType, mySubKeyCode);

        /* Create StreamKeySpec */
        return new GordianStreamKeySpec(myType, myKeyLength, mySubKeyType);
    }

    /**
     * Obtain encoded symCipherSpecId.
     * @param pCipherSpec the symCipherSpec
     * @return the external id
     */
    private static int deriveEncodedIdFromStreamCipherSpec(final GordianStreamCipherSpec pCipherSpec) {
        /* Build the encoded id */
        int myCode = deriveEncodedIdFromStreamKeySpec(pCipherSpec.getKeyType());
        myCode <<= 1;
        myCode += (pCipherSpec.isAEADMode() ? 1 : 0);

        /* Return the encoded id */
        return myCode;
    }

    /**
     * Obtain cipherSpec from encoded symCipherSpecId.
     * @param pEncodedId the encoded id
     * @return the symCipherSpec
     * @throws GordianException on error
     */
    private static GordianStreamCipherSpec deriveStreamCipherSpecFromEncodedId(final int pEncodedId) throws GordianException {
        /* Determine KeySpec */
        final int myAAD = pEncodedId & 1;
        final int myCode = pEncodedId >> 1;
        final GordianStreamKeySpec mySpec = deriveStreamKeySpecFromEncodedId(myCode);

        /* Create the cipherSpec */
        return GordianStreamCipherSpecBuilder.stream(mySpec, myAAD != 0);
    }

    /**
     * Obtain encoded StreamKeySpecId.
     * @param pStreamKeySpec the streamKeySpec
     * @return the encoded id
     */
    private static int deriveEncodedIdFromStreamKeySubType(final GordianStreamKeySpec pStreamKeySpec) {
        /* Switch on keyType */
        switch (pStreamKeySpec.getStreamKeyType()) {
            case CHACHA20:
                return deriveEncodedIdFromEnum((GordianChaCha20Key) pStreamKeySpec.getSubKeyType());
            case SALSA20:
                return deriveEncodedIdFromEnum((GordianSalsa20Key) pStreamKeySpec.getSubKeyType());
            case VMPC:
                return deriveEncodedIdFromEnum((GordianVMPCKey) pStreamKeySpec.getSubKeyType());
            case SKEINXOF:
                return deriveEncodedIdFromEnum((GordianSkeinXofKey) pStreamKeySpec.getSubKeyType());
            case BLAKE2XOF:
                return deriveEncodedIdFromEnum((GordianBlakeXofKey) pStreamKeySpec.getSubKeyType());
            case ELEPHANT:
                return deriveEncodedIdFromEnum((GordianElephantKey) pStreamKeySpec.getSubKeyType());
            case ISAP:
                return deriveEncodedIdFromEnum((GordianISAPKey) pStreamKeySpec.getSubKeyType());
            case SPARKLE:
                return deriveEncodedIdFromEnum((GordianSparkleKey) pStreamKeySpec.getSubKeyType());
            default:
                return 0;
        }
    }

    /**
     * Obtain subKeyType from encoded streamSubKeyType.
     * @param pKeyType the keyType
     * @param pEncodedId the encodedId
     * @return the subKeyType
     * @throws GordianException on error
     */
    private static GordianStreamSubKeyType deriveStreamSubKeyTypeFromEncodedId(final GordianStreamKeyType pKeyType,
                                                                               final int pEncodedId) throws GordianException {
        /* Switch on keyType */
        switch (pKeyType) {
            case CHACHA20:
                return deriveEnumFromEncodedId(pEncodedId, GordianChaCha20Key.class);
            case SALSA20:
                return deriveEnumFromEncodedId(pEncodedId, GordianSalsa20Key.class);
            case VMPC:
                return deriveEnumFromEncodedId(pEncodedId, GordianVMPCKey.class);
            case SKEINXOF:
                return deriveEnumFromEncodedId(pEncodedId, GordianSkeinXofKey.class);
            case BLAKE2XOF:
                return deriveEnumFromEncodedId(pEncodedId, GordianBlakeXofKey.class);
            case ELEPHANT:
                return deriveEnumFromEncodedId(pEncodedId, GordianElephantKey.class);
            case ISAP:
                return deriveEnumFromEncodedId(pEncodedId, GordianISAPKey.class);
            case SPARKLE:
                return deriveEnumFromEncodedId(pEncodedId, GordianSparkleKey.class);
            default:
                return null;
        }
    }

    /**
     * Obtain mask for DigestSubSpec.
     * @return the mask
     */
    private static int determineMaskForDigestSubSpec() {
        return ~(-1 << determineShiftForDigestSubSpec());
    }

    /**
     * Obtain shift for StreamKeySubType.
     * @return the bit shift
     */
    private static int determineShiftForDigestSubSpec() {
        return determineShiftForEnum(GordianDigestState.class);
    }

    /**
     * Obtain mask for StreamKeySubType.
     * @return the mask
     */
    private static int determineMaskForStreamKeySubType() {
        return ~(-1 << determineShiftForStreamKeySubType());
    }

    /**
     * Obtain shift for StreamKeySubType.
     * @return the bit shift
     */
    private static int determineShiftForStreamKeySubType() {
        int myShift = determineShiftForEnum(GordianVMPCKey.class);
        myShift = Math.max(myShift, determineShiftForEnum(GordianSalsa20Key.class));
        myShift = Math.max(myShift, determineShiftForEnum(GordianChaCha20Key.class));
        myShift = Math.max(myShift, determineShiftForEnum(GordianSkeinXofKey.class));
        myShift = Math.max(myShift, determineShiftForEnum(GordianBlakeXofKey.class));
        myShift = Math.max(myShift, determineShiftForEnum(GordianElephantKey.class));
        myShift = Math.max(myShift, determineShiftForEnum(GordianISAPKey.class));
        return Math.max(myShift, determineShiftForEnum(GordianSparkleKey.class));
    }

    /**
     * Obtain encoded macSpecId.
     * @param pMacSpec the macSpec
     * @return the external id
     */
    private static int deriveEncodedIdFromMacSpec(final GordianMacSpec pMacSpec) {
        /* Build the encoded macId */
        final GordianMacType myMacType = pMacSpec.getMacType();
        int myCode = deriveEncodedIdFromMacType(myMacType);
        int myShift = determineShiftForEnum(GordianMacType.class);
        myCode += deriveEncodedIdFromLength(pMacSpec.getKeyLength()) << myShift;
        myShift += determineShiftForEnum(GordianLength.class);

        /* Switch on MacType */
        switch (myMacType) {
            case HMAC:
            case SKEIN:
            case BLAKE2:
            case BLAKE3:
            case KUPYNA:
            case KMAC:
                myCode += deriveEncodedIdFromDigestSpec(Objects.requireNonNull(pMacSpec.getDigestSpec())) << myShift;
                break;
            case GMAC:
            case CMAC:
            case KALYNA:
            case CBCMAC:
            case CFBMAC:
                myCode += deriveEncodedIdFromSymKeySpec(Objects.requireNonNull(pMacSpec.getSymKeySpec())) << myShift;
                break;
            case POLY1305:
                if (pMacSpec.getSymKeySpec() != null) {
                    myCode += deriveEncodedIdFromSymKeySpec(pMacSpec.getSymKeySpec()) << myShift;
                }
                break;
            case ZUC:
                myCode += deriveEncodedIdFromLength(pMacSpec.getMacLength()) << myShift;
                break;
            case SIPHASH:
                myCode += deriveEncodedIdFromSipHashSpec(pMacSpec.getSipHashSpec()) << myShift;
                break;
            default:
                break;
        }

        /* Return the code */
        return myCode;
    }

    /**
     * Obtain macSpec from encoded macSpecId.
     * @param pEncodedId the encoded id
     * @return the macSpec
     * @throws GordianException on error
     */
    private static GordianMacSpec deriveMacSpecFromEncodedId(final int pEncodedId) throws GordianException {
        /* Isolate id Components */
        final int myMacId = pEncodedId & determineMaskForEnum(GordianMacType.class);
        final int myCode = pEncodedId >> determineShiftForEnum(GordianMacType.class);
        final int myKeyLenId = myCode & determineMaskForEnum(GordianLength.class);
        final int myId = myCode >> determineShiftForEnum(GordianLength.class);

        /* Determine MacType and keyLength */
        final GordianMacType myMacType = deriveMacTypeFromEncodedId(myMacId);
        final GordianLength myKeyLen = deriveLengthFromEncodedId(myKeyLenId);

        /* Switch on the MacType */
        switch (myMacType) {
            case HMAC:
                return GordianMacSpecBuilder.hMac(deriveDigestSpecFromEncodedId(myId), myKeyLen);
            case GMAC:
            case CMAC:
            case KALYNA:
            case CFBMAC:
            case CBCMAC:
                return new GordianMacSpec(myMacType, deriveSymKeySpecFromEncodedId(myId));
            case POLY1305:
                return myId == 0
                            ? GordianMacSpecBuilder.poly1305Mac()
                            : new GordianMacSpec(myMacType, deriveSymKeySpecFromEncodedId(myId));
            case SKEIN:
                final GordianDigestSpec mySkeinSpec = deriveDigestSpecFromEncodedId(myId);
                return GordianMacSpecBuilder.skeinMac(myKeyLen, mySkeinSpec);
            case BLAKE2:
                final GordianDigestSpec myBlake2Spec = deriveDigestSpecFromEncodedId(myId);
                return GordianMacSpecBuilder.blake2Mac(myKeyLen, myBlake2Spec);
            case BLAKE3:
                final GordianDigestSpec myBlake3Spec = deriveDigestSpecFromEncodedId(myId);
                return GordianMacSpecBuilder.blake3Mac(myBlake3Spec.getDigestLength());
            case KMAC:
                final GordianDigestSpec myKMACSpec = deriveDigestSpecFromEncodedId(myId);
                return GordianMacSpecBuilder.kMac(myKeyLen, myKMACSpec);
            case KUPYNA:
                final GordianDigestSpec myKupynaSpec = deriveDigestSpecFromEncodedId(myId);
                return GordianMacSpecBuilder.kupynaMac(myKeyLen, myKupynaSpec.getDigestLength());
            case ZUC:
                final GordianLength myLength = deriveLengthFromEncodedId(myId);
                return GordianMacSpecBuilder.zucMac(myKeyLen, myLength);
            case SIPHASH:
                return new GordianMacSpec(GordianMacType.SIPHASH, deriveSipHashSpecFromEncodedId(myId));
            default:
                return new GordianMacSpec(myMacType, myKeyLen);
        }
    }

    /**
     * Obtain encoded SipHashId.
     * @param pSpec the sipHashSpec
     * @return the encoded id
     */
    private static int deriveEncodedIdFromSipHashSpec(final GordianSipHashSpec pSpec) {
        return deriveEncodedIdFromEnum(pSpec);
    }

    /**
     * Obtain sipHashSpec from encoded Id.
     * @param pEncodedId the encoded id
     * @return the sipHashSpec
     * @throws GordianException on error
     */
    private static GordianSipHashSpec deriveSipHashSpecFromEncodedId(final int pEncodedId) throws GordianException {
        return deriveEnumFromEncodedId(pEncodedId, GordianSipHashSpec.class);
    }

    /**
     * Obtain encoded DigestId.
     * @param pDigest the digestType
     * @return the encoded id
     */
    private static int deriveEncodedIdFromDigestType(final GordianDigestType pDigest) {
        return deriveEncodedIdFromEnum(pDigest);
    }

    /**
     * Obtain digestType from encoded Id.
     * @param pEncodedId the encoded id
     * @return the digestType
     * @throws GordianException on error
     */
    private static GordianDigestType deriveDigestTypeFromEncodedId(final int pEncodedId) throws GordianException {
        return deriveEnumFromEncodedId(pEncodedId, GordianDigestType.class);
    }

    /**
     * Obtain encoded symKeyId.
     * @param pSymKey the symKeyType
     * @return the encoded id
     */
    private static int deriveEncodedIdFromSymKeyType(final GordianSymKeyType pSymKey) {
        return deriveEncodedIdFromEnum(pSymKey);
    }

    /**
     * Obtain symKeyType from encoded Id.
     * @param pEncodedId the encoded id
     * @return the symKeyType
     * @throws GordianException on error
     */
    private static GordianSymKeyType deriveSymKeyTypeFromEncodedId(final int pEncodedId) throws GordianException {
        return deriveEnumFromEncodedId(pEncodedId, GordianSymKeyType.class);
    }

    /**
     * Obtain encoded streamKeyId.
     * @param pStreamKey the streamKeyType
     * @return the encoded id
     */
    private static int deriveEncodedIdFromStreamKeyType(final GordianStreamKeyType pStreamKey) {
        return deriveEncodedIdFromEnum(pStreamKey);
    }

    /**
     * Obtain streamKeyType from encoded Id.
     * @param pEncodedId the encoded id
     * @return the streamKeyType
     * @throws GordianException on error
     */
    private static GordianStreamKeyType deriveStreamKeyTypeFromEncodedId(final int pEncodedId) throws GordianException {
        return deriveEnumFromEncodedId(pEncodedId, GordianStreamKeyType.class);
    }

    /**
     * Obtain encoded MacId.
     * @param pMac the macType
     * @return the encoded id
     */
    private static int deriveEncodedIdFromMacType(final GordianMacType pMac) {
        return deriveEncodedIdFromEnum(pMac);
    }

    /**
     * Obtain macType from encoded Id.
     * @param pEncodedId the encoded id
     * @return the macType
     * @throws GordianException on error
     */
    private static GordianMacType deriveMacTypeFromEncodedId(final int pEncodedId) throws GordianException {
        return deriveEnumFromEncodedId(pEncodedId, GordianMacType.class);
    }

    /**
     * Obtain encoded Length.
     * @param pLength the length
     * @return the encoded id
     */
    private static int deriveEncodedIdFromLength(final GordianLength pLength) {
        return deriveEncodedIdFromEnum(pLength);
    }

    /**
     * Obtain length from encoded Id.
     * @param pEncodedId the encoded id
     * @return the length
     * @throws GordianException on error
     */
    private static GordianLength deriveLengthFromEncodedId(final int pEncodedId) throws GordianException {
        return deriveEnumFromEncodedId(pEncodedId, GordianLength.class);
    }

    /**
     * Obtain encoded digestState.
     * @param pState the state
     * @return the encoded id
     */
    private static int deriveEncodedIdFromDigestState(final GordianDigestState pState) {
        return deriveEncodedIdFromEnum(pState);
    }

    /**
     * Obtain digestState from encoded Id.
     * @param pEncodedId the encoded id
     * @return the state
     * @throws GordianException on error
     */
    private static GordianDigestState deriveDigestStateFromEncodedId(final int pEncodedId) throws GordianException {
        return deriveEnumFromEncodedId(pEncodedId, GordianDigestState.class);
    }

    /**
     * Obtain encoded CipherMode.
     * @param pMode the cipherMode
     * @return the encoded id
     */
    private static int deriveEncodedIdFromCipherMode(final GordianCipherMode pMode) {
        return deriveEncodedIdFromEnum(pMode);
    }

    /**
     * Obtain cipherMode from encoded Id.
     * @param pEncodedId the encoded id
     * @return the cipherMode
     * @throws GordianException on error
     */
    private static GordianCipherMode deriveCipherModeFromEncodedId(final int pEncodedId) throws GordianException {
        return deriveEnumFromEncodedId(pEncodedId, GordianCipherMode.class);
    }

    /**
     * Obtain encoded Padding.
     * @param pPadding the padding
     * @return the encoded id
     */
    private static int deriveEncodedIdFromPadding(final GordianPadding pPadding) {
        return deriveEncodedIdFromEnum(pPadding);
    }

    /**
     * Obtain padding from encoded Id.
     * @param pEncodedId the encoded id
     * @return the padding
     * @throws GordianException on error
     */
    private static GordianPadding derivePaddingFromEncodedId(final int pEncodedId) throws GordianException {
        return deriveEnumFromEncodedId(pEncodedId, GordianPadding.class);
    }

    /**
     * Obtain encodedId from enum.
     * @param <E> the Enum type
     * @param pEnum the enum
     * @return the encoded id
     */
    private static <E extends Enum<E>> int deriveEncodedIdFromEnum(final E pEnum) {
        return pEnum.ordinal() + 1;
    }

    /**
     * Obtain enum from encoded id.
     * @param <E> the enum type
     * @param pEncodedId the encoded id
     * @param pClazz the Enum class
     * @return the padding
     * @throws GordianException on error
     */
    private static <E extends Enum<E>> E deriveEnumFromEncodedId(final int pEncodedId,
                                                                 final Class<E> pClazz) throws GordianException {
        final int myId = pEncodedId - 1;
        for (final E myEnum : pClazz.getEnumConstants()) {
            if (myEnum.ordinal() == myId) {
                return myEnum;
            }
        }
        throw new GordianDataException("Invalid enumId: " + pEncodedId + " for class: " + pClazz.getSimpleName());
    }

    /**
     * Obtain mask for enum.
     * @param <E> the Enum type
     * @param pClazz the enum class
     * @return the mask
     */
    private static <E extends Enum<E>> int determineMaskForEnum(final Class<E> pClazz) {
        return ~(-1 << determineShiftForEnum(pClazz));
    }

    /**
     * Obtain shift for enum.
     * @param <E> the Enum type
     * @param pClazz the enum class
     * @return the bit shift
     */
    private static <E extends Enum<E>> int determineShiftForEnum(final Class<E> pClazz) {
        return Integer.SIZE - Integer.numberOfLeadingZeros(pClazz.getEnumConstants().length);
    }

    /**
     * GordianIdSpec markers.
     */
    private enum GordianIdMarker {
        /**
         * SymKey.
         */
        SYMKEY(1),

        /**
         * StreamKey.
         */
        STREAMKEY(2),

        /**
         * MacKey.
         */
        MACKEY(3),

        /**
         * Digest.
         */
        DIGEST(4),

        /**
         * SymKeyCipher.
         */
        SYMCIPHER(5),

        /**
         * StreamCipher.
         */
        STREAMCIPHER(6);

        /**
         * The marker mask.
         */
        private static final int MASK = 0x70000000;

        /**
         * The marker shift.
         */
        private static final int SHIFT = 28;

        /**
         * The marker.
         */
        private final int theMarker;

        /**
         * Constructor.
         * @param pMarker the marker
         */
        GordianIdMarker(final int pMarker) {
            theMarker = pMarker;
        }

        /**
         * Apply marker.
         * @param pId the encoded id
         * @return the marked and encoded id
         */
        int applyMarker(final int pId) {
            if ((pId & MASK) != 0) {
                throw new IllegalArgumentException();
            }

            return pId | (theMarker << SHIFT);
        }

        /**
         * Remove marker.
         * @param pId the merked encoded id
         * @return the marked and encoded id
         */
        static int removeMarker(final int pId) {
            return pId & ~MASK;
        }

        /**
         * Determine marker.
         * @param pId the merked encoded id
         * @return the marker
         */
        static GordianIdMarker determine(final int pId) {
            final int myMark = (pId & MASK) >> SHIFT;
            for (GordianIdMarker myMarker : values()) {
                if (myMarker.theMarker == myMark) {
                    return myMarker;
                }
            }
            throw new IllegalArgumentException();
        }
    }
}
