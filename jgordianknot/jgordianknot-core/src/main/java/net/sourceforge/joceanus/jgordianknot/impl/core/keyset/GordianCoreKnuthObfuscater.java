/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
    public int deriveExternalIdFromType(final Object pType,
                                        final int pAdjustment) throws OceanusException {
        return knuthEncodeInteger(deriveEncodedIdFromType(pType), pAdjustment);
    }

    @Override
    public int deriveExternalIdFromType(final Object pType) throws OceanusException {
        return knuthEncodeInteger(deriveEncodedIdFromType(pType));
    }

    /**
     * Obtain external Id from Type.
     * @param <T> the type class
     * @param pType the type
     * @return the externalId
     * @throws OceanusException on error
     */
    private <T> int deriveEncodedIdFromType(final T pType) throws OceanusException {
        if (pType instanceof GordianDigestSpec) {
            return theIdManager.deriveEncodedIdFromDigestSpec((GordianDigestSpec) pType);
        }
        if (pType instanceof GordianSymCipherSpec) {
            return theIdManager.deriveEncodedIdFromCipherSpec((GordianSymCipherSpec) pType);
        }
        if (pType instanceof GordianStreamCipherSpec) {
            return theIdManager.deriveEncodedIdFromCipherSpec((GordianStreamCipherSpec) pType);
        }
        if (pType instanceof GordianMacSpec) {
            return theIdManager.deriveEncodedIdFromMacSpec((GordianMacSpec) pType);
        }
        if (pType instanceof GordianSymKeySpec) {
            return theIdManager.deriveEncodedIdFromSymKeySpec((GordianSymKeySpec) pType);
        }
        if (pType instanceof GordianStreamKeyType) {
            return theIdManager.deriveEncodedIdFromStreamKeyType((GordianStreamKeyType) pType);
        }
        throw new GordianDataException("Invalid type: " + pType.getClass().getCanonicalName());
    }

    @Override
    public <T> T deriveTypeFromExternalId(final int pId,
                                          final int pAdjustment,
                                          final Class<T> pClazz) throws OceanusException {
        return deriveTypeFromEncodedId(knuthDecodeInteger(pId, pAdjustment), pClazz);
    }

    @Override
    public <T> T deriveTypeFromExternalId(final int pId,
                                          final Class<T> pClazz) throws OceanusException {
        return deriveTypeFromEncodedId(knuthDecodeInteger(pId), pClazz);
    }

    /**
     * Obtain Type from external Id.
     * @param <T> the type class
     * @param pId the external id
     * @param pClazz the type class
     * @return the Type
     * @throws OceanusException on error
     */
    private <T> T deriveTypeFromEncodedId(final int pId,
                                          final Class<T> pClazz) throws OceanusException {
        if (GordianDigestSpec.class.equals(pClazz)) {
            return pClazz.cast(theIdManager.deriveDigestSpecFromEncodedId(pId));
        }
        if (GordianSymCipherSpec.class.equals(pClazz)) {
            return pClazz.cast(theIdManager.deriveSymCipherSpecFromEncodedId(pId));
        }
        if (GordianStreamCipherSpec.class.equals(pClazz)) {
            return pClazz.cast(theIdManager.deriveStreamCipherSpecFromEncodedId(pId));
        }
        if (GordianMacSpec.class.equals(pClazz)) {
            return pClazz.cast(theIdManager.deriveMacSpecFromEncodedId(pId));
        }
        if (GordianSymKeySpec.class.equals(pClazz)) {
            return pClazz.cast(theIdManager.deriveSymKeySpecFromEncodedId(pId));
        }
        if (GordianStreamKeyType.class.equals(pClazz)) {
            return pClazz.cast(theIdManager.deriveStreamKeyTypeFromEncodedId(pId));
        }
        throw new GordianDataException("Invalid class: " + pClazz.getCanonicalName());
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
}
