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

import java.math.BigInteger;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherSpec.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherSpec.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianPersonalisation.GordianPersonalId;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Knuth Obfuscater.
 */
public class GordianKnuthObfuscater {
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
     * Constructor.
     * @param pIdManager the Id Manager
     * @param pPersonalisation pPersonalisation
     */
    protected GordianKnuthObfuscater(final GordianIdManager pIdManager,
                                     final GordianPersonalisation pPersonalisation) {
        /* Store the manager */
        theIdManager = pIdManager;

        /* Generate Knuth Prime/Inverse */
        final BigInteger[] myKnuth = generatePrime(pPersonalisation.getPersonalisedInteger(GordianPersonalId.KNUTH));
        thePrime = myKnuth[0].intValue();
        theInverse = myKnuth[1].intValue();
    }

    /**
     * Encode a value via Knuth Multiplication.
     * @param pInput the input
     * @return the encoded value
     */
    public long knuthEncode(final int pInput) {
        return pInput * (long) thePrime;
    }

    /**
     * Encode a value via Knuth Multiplication.
     * @param pInput the input
     * @param pAdjustment the adjustment
     * @return the encoded value
     */
    public long knuthEncode(final int pInput,
                            final int pAdjustment) {
        final int myId = pInput + pAdjustment;
        return knuthEncode(myId);
    }

    /**
     * Decode a Knuth Encoded value.
     * @param pEncoded the encoded value
     * @return the original input
     */
    public int knuthDecode(final int pEncoded) {
        return (int) (pEncoded * (long) theInverse);
    }

    /**
     * Decode a Knuth Encoded value.
     * @param pEncoded the encoded value
     * @param pAdjustment the adjustment
     * @return the original input
     */
    public int knuthDecode(final int pEncoded,
                           final int pAdjustment) {
        final int myId = knuthDecode(pEncoded);
        return myId - pAdjustment;
    }

    /**
     * Create a Knuth hash.
     * @param pInput the input
     * @return the output hash
     */
    public int knuthHash(final int pInput) {
        final long myHash = knuthEncode(pInput);
        final int myBitLength = Integer.SIZE - (pInput >= 0
                                                            ? Integer.numberOfLeadingZeros(pInput)
                                                            : Integer.numberOfLeadingZeros(-pInput));
        return (int) (myHash >> myBitLength);
    }

    /**
     * Obtain external Id from Type.
     * @param <T> the type class
     * @param pType the type
     * @param pAdjustment the adjustment
     * @return the externalId
     * @throws OceanusException on error
     */
    public <T> int deriveExternalIdFromType(final T pType,
                                            final int pAdjustment) throws OceanusException {
        return (int) knuthEncode(deriveEncodedIdFromType(pType), pAdjustment);
    }

    /**
     * Obtain external Id from Type.
     * @param <T> the type class
     * @param pType the type
     * @return the externalId
     * @throws OceanusException on error
     */
    public <T> int deriveExternalIdFromType(final T pType) throws OceanusException {
        return (int) knuthEncode(deriveEncodedIdFromType(pType));
    }

    /**
     * Obtain external Id from Type.
     * @param <T> the type class
     * @param pType the type
     * @return the externalId
     * @throws OceanusException on error
     */
    private <T> int deriveEncodedIdFromType(final T pType) throws OceanusException {
        if (GordianDigestSpec.class.isInstance(pType)) {
            return theIdManager.deriveEncodedIdFromDigestSpec((GordianDigestSpec) pType);
        }
        if (GordianSymCipherSpec.class.isInstance(pType)) {
            return theIdManager.deriveEncodedIdFromCipherSpec((GordianSymCipherSpec) pType);
        }
        if (GordianStreamCipherSpec.class.isInstance(pType)) {
            return theIdManager.deriveEncodedIdFromCipherSpec((GordianStreamCipherSpec) pType);
        }
        if (GordianMacSpec.class.isInstance(pType)) {
            return theIdManager.deriveEncodedIdFromMacSpec((GordianMacSpec) pType);
        }
        if (GordianSymKeySpec.class.isInstance(pType)) {
            return theIdManager.deriveEncodedIdFromSymKeySpec((GordianSymKeySpec) pType);
        }
        if (GordianStreamKeyType.class.isInstance(pType)) {
            return theIdManager.deriveEncodedIdFromStreamKeyType((GordianStreamKeyType) pType);
        }
        throw new GordianDataException("Invalid type: " + pType.getClass().getCanonicalName());
    }

    /**
     * Obtain external Id from Type.
     * @param <T> the type class
     * @param pId the externalId
     * @param pAdjustment the adjustment
     * @param pClazz the class of the type
     * @return the derived Type
     * @throws OceanusException on error
     */
    public <T> T deriveTypeFromExternalId(final int pId,
                                          final int pAdjustment,
                                          final Class<T> pClazz) throws OceanusException {
        return deriveTypeFromEncodedId(knuthDecode(pId, pAdjustment), pClazz);
    }

    /**
     * Obtain external Id from Type.
     * @param <T> the type class
     * @param pId the externalId
     * @param pClazz the class of the type
     * @return the derived Type
     * @throws OceanusException on error
     */
    public <T> T deriveTypeFromExternalId(final int pId,
                                          final Class<T> pClazz) throws OceanusException {
        return deriveTypeFromEncodedId(knuthDecode(pId), pClazz);
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
    public BigInteger[] generatePrime(final int pBase) {
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
