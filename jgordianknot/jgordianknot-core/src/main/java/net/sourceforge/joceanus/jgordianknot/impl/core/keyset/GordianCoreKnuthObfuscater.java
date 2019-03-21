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

import java.math.BigInteger;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianIdSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKnuthObfuscater;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianPersonalisation.GordianPersonalId;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Knuth Obfuscater.
 */
public class GordianCoreKnuthObfuscater
    implements GordianKnuthObfuscater {
    /**
     * The Id Manager.
     */
    private final GordianIdManager theIdManager;

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
     * @param pKeySetFactory the keySet factory
     */
    GordianCoreKnuthObfuscater(final GordianCoreKeySetFactory pKeySetFactory) {
        /* Store the manager */
        theIdManager = pKeySetFactory.getIdManager();

        /* Generate Knuth Prime/Inverse */
        final GordianPersonalisation myPersonal = pKeySetFactory.getPersonalisation();
        final BigInteger[] myKnuth = generatePrime(myPersonal.getPersonalisedInteger(GordianPersonalId.KNUTHPRIME));
        thePrime = myKnuth[0].intValue();
        theInverse = myKnuth[1].intValue();
        theMask = myPersonal.getPersonalisedInteger(GordianPersonalId.KNUTHMASK);
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
                                        final int pAdjustment) throws OceanusException {
        return knuthEncodeInteger(deriveEncodedIdFromType(pType), pAdjustment);
    }

    @Override
    public int deriveExternalIdFromType(final GordianIdSpec pType) throws OceanusException {
        return knuthEncodeInteger(deriveEncodedIdFromType(pType));
    }

    /**
     * Obtain external Id from Type.
     * @param <T> the type class
     * @param pType the type
     * @return the externalId
     * @throws OceanusException on error
     */
    private <T extends GordianIdSpec> int deriveEncodedIdFromType(final T pType) throws OceanusException {
        if (pType instanceof GordianDigestSpec) {
            final int myId = theIdManager.deriveEncodedIdFromDigestSpec((GordianDigestSpec) pType);
            return GordianIdMarker.DIGEST.applyMarker(myId);
        }
        if (pType instanceof GordianSymCipherSpec) {
            final int myId = theIdManager.deriveEncodedIdFromCipherSpec((GordianSymCipherSpec) pType);
            return GordianIdMarker.SYMCIPHER.applyMarker(myId);
        }
        if (pType instanceof GordianStreamCipherSpec) {
            final int myId = theIdManager.deriveEncodedIdFromCipherSpec((GordianStreamCipherSpec) pType);
            return GordianIdMarker.STREAMCIPHER.applyMarker(myId);
        }
        if (pType instanceof GordianMacSpec) {
            final int myId = theIdManager.deriveEncodedIdFromMacSpec((GordianMacSpec) pType);
            return GordianIdMarker.MACKEY.applyMarker(myId);
        }
        if (pType instanceof GordianSymKeySpec) {
            final int myId = theIdManager.deriveEncodedIdFromSymKeySpec((GordianSymKeySpec) pType);
            return GordianIdMarker.SYMKEY.applyMarker(myId);
        }
        if (pType instanceof GordianStreamKeyType) {
            final int myId = theIdManager.deriveEncodedIdFromStreamKeyType((GordianStreamKeyType) pType);
            return GordianIdMarker.STREAMKEY.applyMarker(myId);
        }
        throw new GordianDataException("Invalid type: " + pType.getClass().getCanonicalName());
    }

    @Override
    public GordianIdSpec deriveTypeFromExternalId(final int pId,
                                                  final int pAdjustment) throws OceanusException {
        return deriveTypeFromEncodedId(knuthDecodeInteger(pId, pAdjustment));
    }

    @Override
    public GordianIdSpec deriveTypeFromExternalId(final int pId) throws OceanusException {
        return deriveTypeFromEncodedId(knuthDecodeInteger(pId));
    }

    /**
     * Obtain Type from external Id.
     * @param pId the external id
     * @return the Type
     * @throws OceanusException on error
     */
    private GordianIdSpec deriveTypeFromEncodedId(final int pId) throws OceanusException {
        final GordianIdMarker myMarker = GordianIdMarker.determine(pId);
        final int myId = GordianIdMarker.removeMarker(pId);
        switch (myMarker) {
            case DIGEST:
                return theIdManager.deriveDigestSpecFromEncodedId(myId);
            case SYMCIPHER:
                return theIdManager.deriveSymCipherSpecFromEncodedId(myId);
            case STREAMCIPHER:
                return theIdManager.deriveStreamCipherSpecFromEncodedId(myId);
            case MACKEY:
                return theIdManager.deriveMacSpecFromEncodedId(myId);
            case SYMKEY:
                return theIdManager.deriveSymKeySpecFromEncodedId(myId);
            case STREAMKEY:
                return theIdManager.deriveStreamKeyTypeFromEncodedId(myId);
            default:
                throw new GordianDataException("Unsupported encoding");
        }
    }

    /**
     * Obtain a large integer prime based on the supplied value.
     * @param pBase the base value
     * @return the encoded value
     */
    private static BigInteger[] generatePrime(final int pBase) {
        /* Make sure that the value is prime */
        BigInteger myValue = BigInteger.valueOf(pBase);
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
