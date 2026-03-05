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
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewCipherMode;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPadding;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewBlakeXofKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewChaCha20Key;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewElephantKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewISAPKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewRomulusKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewSalsa20Key;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewSkeinXofKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewSparkleKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySubType.GordianNewVMPCKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSubSpec.GordianNewDigestState;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestType;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianKnuthObfuscater;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewMacSpec;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewMacSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewMacType;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewSipHashType;
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
        if (pType instanceof GordianNewDigestSpec mySpec) {
            final int myId = deriveEncodedIdFromDigestSpec(mySpec);
            return GordianIdMarker.DIGEST.applyMarker(myId);
        }
        if (pType instanceof GordianNewSymCipherSpec mySpec) {
            final int myId = deriveEncodedIdFromSymCipherSpec(mySpec);
            return GordianIdMarker.SYMCIPHER.applyMarker(myId);
        }
        if (pType instanceof GordianNewStreamCipherSpec mySpec) {
            final int myId = deriveEncodedIdFromStreamCipherSpec(mySpec);
            return GordianIdMarker.STREAMCIPHER.applyMarker(myId);
        }
        if (pType instanceof GordianNewMacSpec mySpec) {
            final int myId = deriveEncodedIdFromMacSpec(mySpec);
            return GordianIdMarker.MACKEY.applyMarker(myId);
        }
        if (pType instanceof GordianNewSymKeySpec mySpec) {
            final int myId = deriveEncodedIdFromSymKeySpec(mySpec);
            return GordianIdMarker.SYMKEY.applyMarker(myId);
        }
        if (pType instanceof GordianNewStreamKeySpec mySpec) {
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
     *
     * @param pDigestSpec the digestSpec
     * @return the encoded id
     */
    private static int deriveEncodedIdFromDigestSpec(final GordianNewDigestSpec pDigestSpec) {
        /* Build the encoded id */
        int myCode = deriveEncodedIdFromDigestType(pDigestSpec.getDigestType());
        final GordianNewDigestState myState = pDigestSpec.getDigestState();
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
    private static GordianNewDigestSpec deriveDigestSpecFromEncodedId(final int pEncodedId) throws GordianException {
        /* Isolate id Components */
        final boolean isXof = (pEncodedId & 1) == 1;
        int myCode = pEncodedId >> 1;
        final int myLenCode = myCode & determineMaskForEnum(GordianLength.class);
        myCode = myCode >> determineShiftForEnum(GordianLength.class);
        final int mySubSpecCode = myCode & determineMaskForDigestSubSpec();
        final int myId = myCode >> determineShiftForDigestSubSpec();

        /* Translate components */
        final GordianNewDigestType myType = deriveDigestTypeFromEncodedId(myId);
        final GordianLength myLength = deriveLengthFromEncodedId(myLenCode);
        GordianNewDigestState mySubSpec = null;
        if (mySubSpecCode != 0) {
            mySubSpec = deriveDigestStateFromEncodedId(mySubSpecCode);
        }

        /* Create DigestSpec */
        final GordianNewDigestSpecBuilder myBuilder = GordianCoreDigestSpecBuilder.newInstance();
        return myBuilder.digest(myType, mySubSpec, myLength, isXof);
    }

    /**
     * Obtain encoded SymKeySpecId.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the encoded id
     */
    private static int deriveEncodedIdFromSymKeySpec(final GordianNewSymKeySpec pSymKeySpec) {
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
    private static GordianNewSymKeySpec deriveSymKeySpecFromEncodedId(final int pEncodedId) throws GordianException {
        /* Isolate id Components */
        final int myKeyLenCode = pEncodedId & determineMaskForEnum(GordianLength.class);
        final int myCode = pEncodedId >> determineShiftForEnum(GordianLength.class);
        final int myBlkLenCode = myCode & determineMaskForEnum(GordianLength.class);
        final int myId = myCode >> determineShiftForEnum(GordianLength.class);

        /* Translate components */
        final GordianNewSymKeyType myType = deriveSymKeyTypeFromEncodedId(myId);
        final GordianLength myBlkLength = deriveLengthFromEncodedId(myBlkLenCode);
        final GordianLength myKeyLength = deriveLengthFromEncodedId(myKeyLenCode);

        /* Create SymKeySpec */
        final GordianNewSymKeySpecBuilder myBuilder = GordianCoreSymKeySpecBuilder.newInstance();
        return myBuilder.symKey(myType, myBlkLength, myKeyLength);
    }

    /**
     * Obtain encoded symCipherSpecId.
     *
     * @param pCipherSpec the symCipherSpec
     * @return the external id
     */
    private static int deriveEncodedIdFromSymCipherSpec(final GordianNewSymCipherSpec pCipherSpec) {
        /* Derive the encoded id */
        int myCode = deriveEncodedIdFromSymKeySpec(pCipherSpec.getKeySpec());
        myCode <<= determineShiftForEnum(GordianNewCipherMode.class);
        myCode += deriveEncodedIdFromCipherMode(pCipherSpec.getCipherMode());
        myCode <<= determineShiftForEnum(GordianNewPadding.class);
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
    private static GordianNewSymCipherSpec deriveSymCipherSpecFromEncodedId(final int pEncodedId) throws GordianException {
        /* Isolate id Components */
        final int myPaddingCode = pEncodedId & determineMaskForEnum(GordianNewPadding.class);
        final int myCode = pEncodedId >> determineShiftForEnum(GordianNewPadding.class);
        final int myModeCode = myCode & determineMaskForEnum(GordianNewCipherMode.class);
        final int myId = myCode >> determineShiftForEnum(GordianNewCipherMode.class);

        /* Determine KeyType */
        final GordianNewSymKeySpec mySpec = deriveSymKeySpecFromEncodedId(myId);
        final GordianNewCipherMode myMode = deriveCipherModeFromEncodedId(myModeCode);
        final GordianNewPadding myPadding = derivePaddingFromEncodedId(myPaddingCode);

        /* Create the cipherSpec */
        final GordianNewSymCipherSpecBuilder myBuilder = GordianCoreSymCipherSpecBuilder.newInstance();
        return myBuilder.symCipher(mySpec, myMode, myPadding);
    }

    /**
     * Obtain encoded StreamKeySpecId.
     *
     * @param pStreamKeySpec the streamKeySpec
     * @return the encoded id
     */
    private static int deriveEncodedIdFromStreamKeySpec(final GordianNewStreamKeySpec pStreamKeySpec) {
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
    private static GordianNewStreamKeySpec deriveStreamKeySpecFromEncodedId(final int pEncodedId) throws GordianException {
        /* Isolate id Components */
        final int mySubKeyCode = pEncodedId & determineMaskForStreamKeySubType();
        final int myCode = pEncodedId >> determineShiftForStreamKeySubType();
        final int myKeyLenCode = myCode & determineMaskForEnum(GordianLength.class);
        final int myId = myCode >> determineShiftForEnum(GordianLength.class);

        /* Translate components */
        final GordianNewStreamKeyType myType = deriveStreamKeyTypeFromEncodedId(myId);
        final GordianLength myKeyLength = deriveLengthFromEncodedId(myKeyLenCode);
        final GordianNewStreamKeySubType mySubKeyType = deriveStreamSubKeyTypeFromEncodedId(myType, mySubKeyCode);

        /* Create StreamKeySpec */
        final GordianNewStreamKeySpecBuilder myBuilder = GordianCoreStreamKeySpecBuilder.newInstance();
        return myBuilder.streamKey(myType, myKeyLength, mySubKeyType);
    }

    /**
     * Obtain encoded symCipherSpecId.
     *
     * @param pCipherSpec the symCipherSpec
     * @return the external id
     */
    private static int deriveEncodedIdFromStreamCipherSpec(final GordianNewStreamCipherSpec pCipherSpec) {
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
    private static GordianNewStreamCipherSpec deriveStreamCipherSpecFromEncodedId(final int pEncodedId) throws GordianException {
        /* Determine KeySpec */
        final int myAAD = pEncodedId & 1;
        final int myCode = pEncodedId >> 1;
        final GordianNewStreamKeySpec mySpec = deriveStreamKeySpecFromEncodedId(myCode);

        /* Create the cipherSpec */
        final GordianNewStreamCipherSpecBuilder myBuilder = GordianCoreStreamCipherSpecBuilder.newInstance();
        return myBuilder.streamCipher(mySpec, myAAD != 0);
    }

    /**
     * Obtain encoded StreamKeySpecId.
     *
     * @param pStreamKeySpec the streamKeySpec
     * @return the encoded id
     */
    private static int deriveEncodedIdFromStreamKeySubType(final GordianNewStreamKeySpec pStreamKeySpec) {
        /* Switch on keyType */
        switch (pStreamKeySpec.getStreamKeyType()) {
            case CHACHA20:
                return deriveEncodedIdFromEnum((GordianNewChaCha20Key) pStreamKeySpec.getSubKeyType());
            case SALSA20:
                return deriveEncodedIdFromEnum((GordianNewSalsa20Key) pStreamKeySpec.getSubKeyType());
            case VMPC:
                return deriveEncodedIdFromEnum((GordianNewVMPCKey) pStreamKeySpec.getSubKeyType());
            case SKEINXOF:
                return deriveEncodedIdFromEnum((GordianNewSkeinXofKey) pStreamKeySpec.getSubKeyType());
            case BLAKE2XOF:
                return deriveEncodedIdFromEnum((GordianNewBlakeXofKey) pStreamKeySpec.getSubKeyType());
            case ELEPHANT:
                return deriveEncodedIdFromEnum((GordianNewElephantKey) pStreamKeySpec.getSubKeyType());
            case ISAP:
                return deriveEncodedIdFromEnum((GordianNewISAPKey) pStreamKeySpec.getSubKeyType());
            case ROMULUS:
                return deriveEncodedIdFromEnum((GordianNewRomulusKey) pStreamKeySpec.getSubKeyType());
            case SPARKLE:
                return deriveEncodedIdFromEnum((GordianNewSparkleKey) pStreamKeySpec.getSubKeyType());
            default:
                return 0;
        }
    }

    /**
     * Obtain subKeyType from encoded streamSubKeyType.
     *
     * @param pKeyType   the keyType
     * @param pEncodedId the encodedId
     * @return the subKeyType
     * @throws GordianException on error
     */
    private static GordianNewStreamKeySubType deriveStreamSubKeyTypeFromEncodedId(final GordianNewStreamKeyType pKeyType,
                                                                                  final int pEncodedId) throws GordianException {
        /* Switch on keyType */
        switch (pKeyType) {
            case CHACHA20:
                return deriveEnumFromEncodedId(pEncodedId, GordianNewChaCha20Key.class);
            case SALSA20:
                return deriveEnumFromEncodedId(pEncodedId, GordianNewSalsa20Key.class);
            case VMPC:
                return deriveEnumFromEncodedId(pEncodedId, GordianNewVMPCKey.class);
            case SKEINXOF:
                return deriveEnumFromEncodedId(pEncodedId, GordianNewSkeinXofKey.class);
            case BLAKE2XOF:
                return deriveEnumFromEncodedId(pEncodedId, GordianNewBlakeXofKey.class);
            case ELEPHANT:
                return deriveEnumFromEncodedId(pEncodedId, GordianNewElephantKey.class);
            case ISAP:
                return deriveEnumFromEncodedId(pEncodedId, GordianNewISAPKey.class);
            case ROMULUS:
                return deriveEnumFromEncodedId(pEncodedId, GordianNewRomulusKey.class);
            case SPARKLE:
                return deriveEnumFromEncodedId(pEncodedId, GordianNewSparkleKey.class);
            default:
                return null;
        }
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
        return determineShiftForEnum(GordianNewDigestState.class);
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
        int myShift = determineShiftForEnum(GordianNewVMPCKey.class);
        myShift = Math.max(myShift, determineShiftForEnum(GordianNewSalsa20Key.class));
        myShift = Math.max(myShift, determineShiftForEnum(GordianNewChaCha20Key.class));
        myShift = Math.max(myShift, determineShiftForEnum(GordianNewSkeinXofKey.class));
        myShift = Math.max(myShift, determineShiftForEnum(GordianNewBlakeXofKey.class));
        myShift = Math.max(myShift, determineShiftForEnum(GordianNewElephantKey.class));
        myShift = Math.max(myShift, determineShiftForEnum(GordianNewISAPKey.class));
        myShift = Math.max(myShift, determineShiftForEnum(GordianNewRomulusKey.class));
        return Math.max(myShift, determineShiftForEnum(GordianNewSparkleKey.class));
    }

    /**
     * Obtain encoded macSpecId.
     *
     * @param pMacSpec the macSpec
     * @return the external id
     */
    private static int deriveEncodedIdFromMacSpec(final GordianNewMacSpec pMacSpec) {
        /* Build the encoded macId */
        final GordianNewMacType myMacType = pMacSpec.getMacType();
        int myCode = deriveEncodedIdFromMacType(myMacType);
        int myShift = determineShiftForEnum(GordianNewMacType.class);
        myCode += deriveEncodedIdFromLength(pMacSpec.getKeyLength()) << myShift;
        myShift += determineShiftForEnum(GordianLength.class);

        /* Switch on MacType */
        final GordianCoreMacSpec mySpec = (GordianCoreMacSpec) pMacSpec;
        switch (myMacType) {
            case HMAC:
            case SKEIN:
            case BLAKE2:
            case BLAKE3:
            case KUPYNA:
            case KMAC:
                myCode += deriveEncodedIdFromDigestSpec(Objects.requireNonNull(mySpec.getDigestSpec())) << myShift;
                break;
            case GMAC:
            case CMAC:
            case KALYNA:
            case CBCMAC:
            case CFBMAC:
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
    private static GordianNewMacSpec deriveMacSpecFromEncodedId(final int pEncodedId) throws GordianException {
        /* Isolate id Components */
        final int myMacId = pEncodedId & determineMaskForEnum(GordianNewMacType.class);
        final int myCode = pEncodedId >> determineShiftForEnum(GordianNewMacType.class);
        final int myKeyLenId = myCode & determineMaskForEnum(GordianLength.class);
        final int myId = myCode >> determineShiftForEnum(GordianLength.class);

        /* Determine MacType and keyLength */
        final GordianNewMacType myMacType = deriveMacTypeFromEncodedId(myMacId);
        final GordianLength myKeyLen = deriveLengthFromEncodedId(myKeyLenId);
        final GordianNewMacSpecBuilder myBuilder = GordianCoreMacSpecBuilder.newInstance();

        /* Switch on the MacType */
        switch (myMacType) {
            case HMAC:
                return myBuilder.hMac(deriveDigestSpecFromEncodedId(myId), myKeyLen);
            case GMAC:
            case CMAC:
            case KALYNA:
            case CFBMAC:
            case CBCMAC:
                return myBuilder.mac(myMacType, deriveSymKeySpecFromEncodedId(myId));
            case POLY1305:
                return myId == 0
                        ? myBuilder.poly1305Mac()
                        : myBuilder.mac(myMacType, deriveSymKeySpecFromEncodedId(myId));
            case SKEIN:
                final GordianNewDigestSpec mySkeinSpec = deriveDigestSpecFromEncodedId(myId);
                return myBuilder.skeinMac(myKeyLen, mySkeinSpec);
            case BLAKE2:
                final GordianNewDigestSpec myBlake2Spec = deriveDigestSpecFromEncodedId(myId);
                return myBuilder.blake2Mac(myKeyLen, myBlake2Spec);
            case BLAKE3:
                final GordianNewDigestSpec myBlake3Spec = deriveDigestSpecFromEncodedId(myId);
                return myBuilder.blake3Mac(myBlake3Spec.getDigestLength());
            case KMAC:
                final GordianNewDigestSpec myKMACSpec = deriveDigestSpecFromEncodedId(myId);
                return myBuilder.kMac(myKeyLen, myKMACSpec);
            case KUPYNA:
                final GordianNewDigestSpec myKupynaSpec = deriveDigestSpecFromEncodedId(myId);
                return myBuilder.kupynaMac(myKeyLen, myKupynaSpec.getDigestLength());
            case ZUC:
                final GordianLength myLength = deriveLengthFromEncodedId(myId);
                return myBuilder.zucMac(myKeyLen, myLength);
            case SIPHASH:
                return myBuilder.sipHash(deriveSipHashTypeFromEncodedId(myId));
            default:
                return myBuilder.mac(myMacType, myKeyLen);
        }
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
    private static GordianNewSipHashType deriveSipHashTypeFromEncodedId(final int pEncodedId) throws GordianException {
        return deriveEnumFromEncodedId(pEncodedId, GordianNewSipHashType.class);
    }

    /**
     * Obtain encoded DigestId.
     *
     * @param pDigest the digestType
     * @return the encoded id
     */
    private static int deriveEncodedIdFromDigestType(final GordianNewDigestType pDigest) {
        return deriveEncodedIdFromEnum(pDigest);
    }

    /**
     * Obtain digestType from encoded Id.
     *
     * @param pEncodedId the encoded id
     * @return the digestType
     * @throws GordianException on error
     */
    private static GordianNewDigestType deriveDigestTypeFromEncodedId(final int pEncodedId) throws GordianException {
        return deriveEnumFromEncodedId(pEncodedId, GordianNewDigestType.class);
    }

    /**
     * Obtain encoded symKeyId.
     *
     * @param pSymKey the symKeyType
     * @return the encoded id
     */
    private static int deriveEncodedIdFromSymKeyType(final GordianNewSymKeyType pSymKey) {
        return deriveEncodedIdFromEnum(pSymKey);
    }

    /**
     * Obtain symKeyType from encoded Id.
     *
     * @param pEncodedId the encoded id
     * @return the symKeyType
     * @throws GordianException on error
     */
    private static GordianNewSymKeyType deriveSymKeyTypeFromEncodedId(final int pEncodedId) throws GordianException {
        return deriveEnumFromEncodedId(pEncodedId, GordianNewSymKeyType.class);
    }

    /**
     * Obtain encoded streamKeyId.
     *
     * @param pStreamKey the streamKeyType
     * @return the encoded id
     */
    private static int deriveEncodedIdFromStreamKeyType(final GordianNewStreamKeyType pStreamKey) {
        return deriveEncodedIdFromEnum(pStreamKey);
    }

    /**
     * Obtain streamKeyType from encoded Id.
     *
     * @param pEncodedId the encoded id
     * @return the streamKeyType
     * @throws GordianException on error
     */
    private static GordianNewStreamKeyType deriveStreamKeyTypeFromEncodedId(final int pEncodedId) throws GordianException {
        return deriveEnumFromEncodedId(pEncodedId, GordianNewStreamKeyType.class);
    }

    /**
     * Obtain encoded MacId.
     *
     * @param pMac the macType
     * @return the encoded id
     */
    private static int deriveEncodedIdFromMacType(final GordianNewMacType pMac) {
        return deriveEncodedIdFromEnum(pMac);
    }

    /**
     * Obtain macType from encoded Id.
     *
     * @param pEncodedId the encoded id
     * @return the macType
     * @throws GordianException on error
     */
    private static GordianNewMacType deriveMacTypeFromEncodedId(final int pEncodedId) throws GordianException {
        return deriveEnumFromEncodedId(pEncodedId, GordianNewMacType.class);
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
    private static int deriveEncodedIdFromDigestState(final GordianNewDigestState pState) {
        return deriveEncodedIdFromEnum(pState);
    }

    /**
     * Obtain digestState from encoded Id.
     *
     * @param pEncodedId the encoded id
     * @return the state
     * @throws GordianException on error
     */
    private static GordianNewDigestState deriveDigestStateFromEncodedId(final int pEncodedId) throws GordianException {
        return deriveEnumFromEncodedId(pEncodedId, GordianNewDigestState.class);
    }

    /**
     * Obtain encoded CipherMode.
     *
     * @param pMode the cipherMode
     * @return the encoded id
     */
    private static int deriveEncodedIdFromCipherMode(final GordianNewCipherMode pMode) {
        return deriveEncodedIdFromEnum(pMode);
    }

    /**
     * Obtain cipherMode from encoded Id.
     *
     * @param pEncodedId the encoded id
     * @return the cipherMode
     * @throws GordianException on error
     */
    private static GordianNewCipherMode deriveCipherModeFromEncodedId(final int pEncodedId) throws GordianException {
        return deriveEnumFromEncodedId(pEncodedId, GordianNewCipherMode.class);
    }

    /**
     * Obtain encoded Padding.
     *
     * @param pPadding the padding
     * @return the encoded id
     */
    private static int deriveEncodedIdFromPadding(final GordianNewPadding pPadding) {
        return deriveEncodedIdFromEnum(pPadding);
    }

    /**
     * Obtain padding from encoded Id.
     *
     * @param pEncodedId the encoded id
     * @return the padding
     * @throws GordianException on error
     */
    private static GordianNewPadding derivePaddingFromEncodedId(final int pEncodedId) throws GordianException {
        return deriveEnumFromEncodedId(pEncodedId, GordianNewPadding.class);
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
