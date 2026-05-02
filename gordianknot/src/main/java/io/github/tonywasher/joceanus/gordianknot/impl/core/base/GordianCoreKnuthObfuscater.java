/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.gordianknot.impl.core.base;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianIdSpec;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianCipherMode;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianPadding;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianBlakeXofKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianChaCha20Key;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianElephantKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianISAPKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianRomulusKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianSalsa20Key;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianSkeinXofKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianSparkleKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianVMPCKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSubSpec.GordianDigestState;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestType;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianKnuthObfuscater;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianMacSpec;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianMacSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianMacType;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianSipHashType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianPersonalisation.GordianPersonalId;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreStreamCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreStreamKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.mac.GordianCoreMacSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.mac.GordianCoreMacSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.mac.GordianCoreSipHashType;

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
     *
     * @param pFactory the factory
     * @throws GordianException on error
     */
    public GordianCoreKnuthObfuscater(final GordianBaseFactory pFactory) throws GordianException {
        /* Generate Knuth Prime/Inverse */
        final GordianPersonalisation myPersonal = pFactory.getPersonalisation();
        final BigInteger[] myKnuth = generatePrime(myPersonal.getPersonalisedInteger(GordianPersonalId.KNUTHPRIME));
        thePrime = myKnuth[0].intValue();
        theInverse = myKnuth[1].intValue();
        theMask = myPersonal.getPersonalisedInteger(GordianPersonalId.KNUTHMASK);
    }

    /**
     * Obtain a large integer prime based on the supplied value.
     *
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
                {myValue, myInverse};
    }

    /**
     * Encode an integer value via Knuth Multiplication.
     *
     * @param pInput the input
     * @return the encoded value
     */
    public int knuthEncodeInteger(final int pInput) {
        return (int) ((pInput ^ theMask) * (long) thePrime);
    }

    /**
     * Encode an integer value via Knuth Multiplication.
     *
     * @param pInput      the input
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
     *
     * @param pEncoded the encoded value
     * @return the original input
     */
    public int knuthDecodeInteger(final int pEncoded) {
        return theMask ^ (int) (pEncoded * (long) theInverse);
    }

    /**
     * Decode a Knuth Encoded integer value.
     *
     * @param pEncoded    the encoded value
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
     *
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
     *
     * @param pInput      the input
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
     *
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
     *
     * @param pEncoded    the encoded value
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
     *
     * @param <T>   the type class
     * @param pType the type
     * @return the externalId
     * @throws GordianException on error
     */
    private static <T extends GordianIdSpec> int deriveEncodedIdFromType(final T pType) throws GordianException {
        if (pType instanceof GordianDigestSpec mySpec) {
            final int myId = deriveEncodedIdFromDigestSpec(mySpec);
            return GordianIdMarker.DIGEST.applyMarker(myId);
        }
        if (pType instanceof GordianSymCipherSpec mySpec) {
            final int myId = deriveEncodedIdFromSymCipherSpec(mySpec);
            return GordianIdMarker.SYMCIPHER.applyMarker(myId);
        }
        if (pType instanceof GordianStreamCipherSpec mySpec) {
            final int myId = deriveEncodedIdFromStreamCipherSpec(mySpec);
            return GordianIdMarker.STREAMCIPHER.applyMarker(myId);
        }
        if (pType instanceof GordianMacSpec mySpec) {
            final int myId = deriveEncodedIdFromMacSpec(mySpec);
            return GordianIdMarker.MACKEY.applyMarker(myId);
        }
        if (pType instanceof GordianSymKeySpec mySpec) {
            final int myId = deriveEncodedIdFromSymKeySpec(mySpec);
            return GordianIdMarker.SYMKEY.applyMarker(myId);
        }
        if (pType instanceof GordianStreamKeySpec mySpec) {
            final int myId = deriveEncodedIdFromStreamKeySpec(mySpec);
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
     *
     * @param pId the external id
     * @return the Type
     * @throws GordianException on error
     */
    private static GordianIdSpec deriveTypeFromEncodedId(final int pId) throws GordianException {
        final GordianIdMarker myMarker = GordianIdMarker.determine(pId);
        final int myId = GordianIdMarker.removeMarker(pId);
        return switch (myMarker) {
            case DIGEST -> deriveDigestSpecFromEncodedId(myId);
            case SYMCIPHER -> deriveSymCipherSpecFromEncodedId(myId);
            case STREAMCIPHER -> deriveStreamCipherSpecFromEncodedId(myId);
            case MACKEY -> deriveMacSpecFromEncodedId(myId);
            case SYMKEY -> deriveSymKeySpecFromEncodedId(myId);
            case STREAMKEY -> deriveStreamKeySpecFromEncodedId(myId);
            default -> throw new GordianDataException("Unsupported encoding");
        };
    }

    /**
     * Obtain encoded DigestSpecId.
     *
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
     *
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
        GordianDigestState mySubSpec = null;
        if (mySubSpecCode != 0) {
            mySubSpec = deriveDigestStateFromEncodedId(mySubSpecCode);
        }

        /* Create DigestSpec */
        final GordianDigestSpecBuilder myBuilder = GordianCoreDigestSpecBuilder.newInstance();
        return myBuilder.digest(myType, mySubSpec, myLength, isXof);
    }

    /**
     * Obtain encoded SymKeySpecId.
     *
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
     *
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
        final GordianSymKeySpecBuilder myBuilder = GordianCoreSymKeySpecBuilder.newInstance();
        return myBuilder.symKey(myType, myBlkLength, myKeyLength);
    }

    /**
     * Obtain encoded symCipherSpecId.
     *
     * @param pCipherSpec the symCipherSpec
     * @return the external id
     */
    private static int deriveEncodedIdFromSymCipherSpec(final GordianSymCipherSpec pCipherSpec) {
        /* Derive the encoded id */
        int myCode = deriveEncodedIdFromSymKeySpec(pCipherSpec.getKeySpec());
        myCode <<= determineShiftForEnum(GordianCipherMode.class);
        myCode += deriveEncodedIdFromCipherMode(pCipherSpec.getCipherMode());
        myCode <<= determineShiftForEnum(GordianPadding.class);
        myCode += deriveEncodedIdFromPadding(pCipherSpec.getPadding());

        /* Return the code */
        return myCode;
    }

    /**
     * Obtain cipherSpec from encoded symCipherSpecId.
     *
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
        final GordianSymCipherSpecBuilder myBuilder = GordianCoreSymCipherSpecBuilder.newInstance();
        return myBuilder.symCipher(mySpec, myMode, myPadding);
    }

    /**
     * Obtain encoded StreamKeySpecId.
     *
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
     *
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
        final GordianStreamKeySubType mySubKeyType = deriveStreamSubKeyTypeFromEncodedId(myType, mySubKeyCode);

        /* Create StreamKeySpec */
        final GordianStreamKeySpecBuilder myBuilder = GordianCoreStreamKeySpecBuilder.newInstance();
        return myBuilder.streamKey(myType, myKeyLength, mySubKeyType);
    }

    /**
     * Obtain encoded symCipherSpecId.
     *
     * @param pCipherSpec the symCipherSpec
     * @return the external id
     */
    private static int deriveEncodedIdFromStreamCipherSpec(final GordianStreamCipherSpec pCipherSpec) {
        /* Build the encoded id */
        int myCode = deriveEncodedIdFromStreamKeySpec(pCipherSpec.getKeySpec());
        myCode <<= 1;
        myCode += (pCipherSpec.asAEAD() ? 1 : 0);

        /* Return the encoded id */
        return myCode;
    }

    /**
     * Obtain cipherSpec from encoded symCipherSpecId.
     *
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
        final GordianStreamCipherSpecBuilder myBuilder = GordianCoreStreamCipherSpecBuilder.newInstance();
        return myBuilder.streamCipher(mySpec, myAAD != 0);
    }

    /**
     * Obtain encoded StreamKeySpecId.
     *
     * @param pStreamKeySpec the streamKeySpec
     * @return the encoded id
     */
    private static int deriveEncodedIdFromStreamKeySubType(final GordianStreamKeySpec pStreamKeySpec) {
        /* Switch on keyType */
        return switch (pStreamKeySpec.getStreamKeyType()) {
            case CHACHA20 -> deriveEncodedIdFromEnum((GordianChaCha20Key) pStreamKeySpec.getSubKeyType());
            case SALSA20 -> deriveEncodedIdFromEnum((GordianSalsa20Key) pStreamKeySpec.getSubKeyType());
            case VMPC -> deriveEncodedIdFromEnum((GordianVMPCKey) pStreamKeySpec.getSubKeyType());
            case SKEINXOF -> deriveEncodedIdFromEnum((GordianSkeinXofKey) pStreamKeySpec.getSubKeyType());
            case BLAKE2XOF -> deriveEncodedIdFromEnum((GordianBlakeXofKey) pStreamKeySpec.getSubKeyType());
            case ELEPHANT -> deriveEncodedIdFromEnum((GordianElephantKey) pStreamKeySpec.getSubKeyType());
            case ISAP -> deriveEncodedIdFromEnum((GordianISAPKey) pStreamKeySpec.getSubKeyType());
            case ROMULUS -> deriveEncodedIdFromEnum((GordianRomulusKey) pStreamKeySpec.getSubKeyType());
            case SPARKLE -> deriveEncodedIdFromEnum((GordianSparkleKey) pStreamKeySpec.getSubKeyType());
            default -> 0;
        };
    }

    /**
     * Obtain subKeyType from encoded streamSubKeyType.
     *
     * @param pKeyType   the keyType
     * @param pEncodedId the encodedId
     * @return the subKeyType
     * @throws GordianException on error
     */
    private static GordianStreamKeySubType deriveStreamSubKeyTypeFromEncodedId(final GordianStreamKeyType pKeyType,
                                                                               final int pEncodedId) throws GordianException {
        /* Switch on keyType */
        return switch (pKeyType) {
            case CHACHA20 -> deriveEnumFromEncodedId(pEncodedId, GordianChaCha20Key.class);
            case SALSA20 -> deriveEnumFromEncodedId(pEncodedId, GordianSalsa20Key.class);
            case VMPC -> deriveEnumFromEncodedId(pEncodedId, GordianVMPCKey.class);
            case SKEINXOF -> deriveEnumFromEncodedId(pEncodedId, GordianSkeinXofKey.class);
            case BLAKE2XOF -> deriveEnumFromEncodedId(pEncodedId, GordianBlakeXofKey.class);
            case ELEPHANT -> deriveEnumFromEncodedId(pEncodedId, GordianElephantKey.class);
            case ISAP -> deriveEnumFromEncodedId(pEncodedId, GordianISAPKey.class);
            case ROMULUS -> deriveEnumFromEncodedId(pEncodedId, GordianRomulusKey.class);
            case SPARKLE -> deriveEnumFromEncodedId(pEncodedId, GordianSparkleKey.class);
            default -> null;
        };
    }

    /**
     * Obtain mask for DigestSubSpec.
     *
     * @return the mask
     */
    private static int determineMaskForDigestSubSpec() {
        return ~(-1 << determineShiftForDigestSubSpec());
    }

    /**
     * Obtain shift for StreamKeySubType.
     *
     * @return the bit shift
     */
    private static int determineShiftForDigestSubSpec() {
        return determineShiftForEnum(GordianDigestState.class);
    }

    /**
     * Obtain mask for StreamKeySubType.
     *
     * @return the mask
     */
    private static int determineMaskForStreamKeySubType() {
        return ~(-1 << determineShiftForStreamKeySubType());
    }

    /**
     * Obtain shift for StreamKeySubType.
     *
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
        myShift = Math.max(myShift, determineShiftForEnum(GordianRomulusKey.class));
        return Math.max(myShift, determineShiftForEnum(GordianSparkleKey.class));
    }

    /**
     * Obtain encoded macSpecId.
     *
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
        final GordianCoreMacSpec mySpec = (GordianCoreMacSpec) pMacSpec;
        switch (myMacType) {
            case HMAC, SKEIN, BLAKE2, BLAKE3, KUPYNA, KMAC:
                myCode += deriveEncodedIdFromDigestSpec(Objects.requireNonNull(mySpec.getDigestSpec())) << myShift;
                break;
            case GMAC, CMAC, KALYNA, CBCMAC, CFBMAC:
                myCode += deriveEncodedIdFromSymKeySpec(Objects.requireNonNull(mySpec.getSymKeySpec())) << myShift;
                break;
            case POLY1305:
                if (mySpec.getSymKeySpec() != null) {
                    myCode += deriveEncodedIdFromSymKeySpec(mySpec.getSymKeySpec()) << myShift;
                }
                break;
            case ZUC:
                myCode += deriveEncodedIdFromLength(mySpec.getMacLength()) << myShift;
                break;
            case SIPHASH:
                myCode += deriveEncodedIdFromSipHashType(mySpec.getSipHashSpec()) << myShift;
                break;
            default:
                break;
        }

        /* Return the code */
        return myCode;
    }

    /**
     * Obtain macSpec from encoded macSpecId.
     *
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
        final GordianMacSpecBuilder myBuilder = GordianCoreMacSpecBuilder.newInstance();

        /* Switch on the MacType */
        return switch (myMacType) {
            case HMAC -> myBuilder.hMac(deriveDigestSpecFromEncodedId(myId), myKeyLen);
            case GMAC, CMAC, KALYNA, CFBMAC, CBCMAC -> myBuilder.mac(myMacType, deriveSymKeySpecFromEncodedId(myId));
            case POLY1305 -> myId == 0
                    ? myBuilder.poly1305Mac()
                    : myBuilder.mac(myMacType, deriveSymKeySpecFromEncodedId(myId));
            case SKEIN -> {
                final GordianDigestSpec mySkeinSpec = deriveDigestSpecFromEncodedId(myId);
                yield myBuilder.skeinMac(myKeyLen, mySkeinSpec);
            }
            case BLAKE2 -> {
                final GordianDigestSpec myBlake2Spec = deriveDigestSpecFromEncodedId(myId);
                yield myBuilder.blake2Mac(myKeyLen, myBlake2Spec);
            }
            case BLAKE3 -> {
                final GordianDigestSpec myBlake3Spec = deriveDigestSpecFromEncodedId(myId);
                yield myBuilder.blake3Mac(myBlake3Spec.getDigestLength());
            }
            case KMAC -> {
                final GordianDigestSpec myKMACSpec = deriveDigestSpecFromEncodedId(myId);
                yield myBuilder.kMac(myKeyLen, myKMACSpec);
            }
            case KUPYNA -> {
                final GordianDigestSpec myKupynaSpec = deriveDigestSpecFromEncodedId(myId);
                yield myBuilder.kupynaMac(myKeyLen, myKupynaSpec.getDigestLength());
            }
            case ZUC -> {
                final GordianLength myLength = deriveLengthFromEncodedId(myId);
                yield myBuilder.zucMac(myKeyLen, myLength);
            }
            case SIPHASH -> myBuilder.sipHash(deriveSipHashTypeFromEncodedId(myId));
            default -> myBuilder.mac(myMacType, myKeyLen);
        };
    }

    /**
     * Obtain encoded SipHashId.
     *
     * @param pType the sipHashType
     * @return the encoded id
     */
    private static int deriveEncodedIdFromSipHashType(final GordianCoreSipHashType pType) {
        return deriveEncodedIdFromEnum(pType.getType());
    }

    /**
     * Obtain sipHashSpec from encoded Id.
     *
     * @param pEncodedId the encoded id
     * @return the sipHashSpec
     * @throws GordianException on error
     */
    private static GordianSipHashType deriveSipHashTypeFromEncodedId(final int pEncodedId) throws GordianException {
        return deriveEnumFromEncodedId(pEncodedId, GordianSipHashType.class);
    }

    /**
     * Obtain encoded DigestId.
     *
     * @param pDigest the digestType
     * @return the encoded id
     */
    private static int deriveEncodedIdFromDigestType(final GordianDigestType pDigest) {
        return deriveEncodedIdFromEnum(pDigest);
    }

    /**
     * Obtain digestType from encoded Id.
     *
     * @param pEncodedId the encoded id
     * @return the digestType
     * @throws GordianException on error
     */
    private static GordianDigestType deriveDigestTypeFromEncodedId(final int pEncodedId) throws GordianException {
        return deriveEnumFromEncodedId(pEncodedId, GordianDigestType.class);
    }

    /**
     * Obtain encoded symKeyId.
     *
     * @param pSymKey the symKeyType
     * @return the encoded id
     */
    private static int deriveEncodedIdFromSymKeyType(final GordianSymKeyType pSymKey) {
        return deriveEncodedIdFromEnum(pSymKey);
    }

    /**
     * Obtain symKeyType from encoded Id.
     *
     * @param pEncodedId the encoded id
     * @return the symKeyType
     * @throws GordianException on error
     */
    private static GordianSymKeyType deriveSymKeyTypeFromEncodedId(final int pEncodedId) throws GordianException {
        return deriveEnumFromEncodedId(pEncodedId, GordianSymKeyType.class);
    }

    /**
     * Obtain encoded streamKeyId.
     *
     * @param pStreamKey the streamKeyType
     * @return the encoded id
     */
    private static int deriveEncodedIdFromStreamKeyType(final GordianStreamKeyType pStreamKey) {
        return deriveEncodedIdFromEnum(pStreamKey);
    }

    /**
     * Obtain streamKeyType from encoded Id.
     *
     * @param pEncodedId the encoded id
     * @return the streamKeyType
     * @throws GordianException on error
     */
    private static GordianStreamKeyType deriveStreamKeyTypeFromEncodedId(final int pEncodedId) throws GordianException {
        return deriveEnumFromEncodedId(pEncodedId, GordianStreamKeyType.class);
    }

    /**
     * Obtain encoded MacId.
     *
     * @param pMac the macType
     * @return the encoded id
     */
    private static int deriveEncodedIdFromMacType(final GordianMacType pMac) {
        return deriveEncodedIdFromEnum(pMac);
    }

    /**
     * Obtain macType from encoded Id.
     *
     * @param pEncodedId the encoded id
     * @return the macType
     * @throws GordianException on error
     */
    private static GordianMacType deriveMacTypeFromEncodedId(final int pEncodedId) throws GordianException {
        return deriveEnumFromEncodedId(pEncodedId, GordianMacType.class);
    }

    /**
     * Obtain encoded Length.
     *
     * @param pLength the length
     * @return the encoded id
     */
    private static int deriveEncodedIdFromLength(final GordianLength pLength) {
        return deriveEncodedIdFromEnum(pLength);
    }

    /**
     * Obtain length from encoded Id.
     *
     * @param pEncodedId the encoded id
     * @return the length
     * @throws GordianException on error
     */
    private static GordianLength deriveLengthFromEncodedId(final int pEncodedId) throws GordianException {
        return deriveEnumFromEncodedId(pEncodedId, GordianLength.class);
    }

    /**
     * Obtain encoded digestState.
     *
     * @param pState the state
     * @return the encoded id
     */
    private static int deriveEncodedIdFromDigestState(final GordianDigestState pState) {
        return deriveEncodedIdFromEnum(pState);
    }

    /**
     * Obtain digestState from encoded Id.
     *
     * @param pEncodedId the encoded id
     * @return the state
     * @throws GordianException on error
     */
    private static GordianDigestState deriveDigestStateFromEncodedId(final int pEncodedId) throws GordianException {
        return deriveEnumFromEncodedId(pEncodedId, GordianDigestState.class);
    }

    /**
     * Obtain encoded CipherMode.
     *
     * @param pMode the cipherMode
     * @return the encoded id
     */
    private static int deriveEncodedIdFromCipherMode(final GordianCipherMode pMode) {
        return deriveEncodedIdFromEnum(pMode);
    }

    /**
     * Obtain cipherMode from encoded Id.
     *
     * @param pEncodedId the encoded id
     * @return the cipherMode
     * @throws GordianException on error
     */
    private static GordianCipherMode deriveCipherModeFromEncodedId(final int pEncodedId) throws GordianException {
        return deriveEnumFromEncodedId(pEncodedId, GordianCipherMode.class);
    }

    /**
     * Obtain encoded Padding.
     *
     * @param pPadding the padding
     * @return the encoded id
     */
    private static int deriveEncodedIdFromPadding(final GordianPadding pPadding) {
        return deriveEncodedIdFromEnum(pPadding);
    }

    /**
     * Obtain padding from encoded Id.
     *
     * @param pEncodedId the encoded id
     * @return the padding
     * @throws GordianException on error
     */
    private static GordianPadding derivePaddingFromEncodedId(final int pEncodedId) throws GordianException {
        return deriveEnumFromEncodedId(pEncodedId, GordianPadding.class);
    }

    /**
     * Obtain encodedId from enum.
     *
     * @param <E>   the Enum type
     * @param pEnum the enum
     * @return the encoded id
     */
    private static <E extends Enum<E>> int deriveEncodedIdFromEnum(final E pEnum) {
        return pEnum.ordinal() + 1;
    }

    /**
     * Obtain enum from encoded id.
     *
     * @param <E>        the enum type
     * @param pEncodedId the encoded id
     * @param pClazz     the Enum class
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
     *
     * @param <E>    the Enum type
     * @param pClazz the enum class
     * @return the mask
     */
    private static <E extends Enum<E>> int determineMaskForEnum(final Class<E> pClazz) {
        return ~(-1 << determineShiftForEnum(pClazz));
    }

    /**
     * Obtain shift for enum.
     *
     * @param <E>    the Enum type
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
         *
         * @param pMarker the marker
         */
        GordianIdMarker(final int pMarker) {
            theMarker = pMarker;
        }

        /**
         * Apply marker.
         *
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
         *
         * @param pId the merked encoded id
         * @return the marked and encoded id
         */
        static int removeMarker(final int pId) {
            return pId & ~MASK;
        }

        /**
         * Determine marker.
         *
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
