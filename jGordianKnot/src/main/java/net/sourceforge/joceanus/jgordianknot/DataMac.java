/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;

/**
 * Encapsulation of a Mac.
 */
public class DataMac {
    /**
     * Creation failure error text.
     */
    private static final String ERROR_CREATE = "failed to create Mac";

    /**
     * Creation failure error text.
     */
    private static final String ERROR_CALC = "failed to calculate MAC";

    /**
     * The Mac.
     */
    private final Mac theMac;

    /**
     * The MacType.
     */
    private final MacType theMacType;

    /**
     * The Algorithm.
     */
    private final String theAlgo;

    /**
     * The DigestType.
     */
    private final DigestType theDigestType;

    /**
     * The SymmetricKey.
     */
    private final SymmetricKey theKey;

    /**
     * The Initialisation vector.
     */
    private final byte[] theInitVector;

    /**
     * Obtain the mac type.
     * @return the mac type
     */
    public MacType getMacType() {
        return theMacType;
    }

    /**
     * Obtain the digest type.
     * @return the digest type
     */
    public DigestType getDigestType() {
        return theDigestType;
    }

    /**
     * Obtain the symmetric key.
     * @return the symmetric key
     */
    public SymmetricKey getSymmetricKey() {
        return theKey;
    }

    /**
     * Obtain the initialisation vector.
     * @return the initialisation vector
     */
    public byte[] getInitVector() {
        return theInitVector;
    }

    /**
     * Obtain the mac length.
     * @return the mac length
     */
    public int getMacLength() {
        return theMac.getMacLength();
    }

    /**
     * Constructor for a new HMac digest of specified parameters.
     * @param pGenerator the security generator
     * @param pDigestType DigestType
     * @param pVector the initialisation vector
     * @throws JDataException on error
     */
    protected DataMac(final SecurityGenerator pGenerator,
                      final DigestType pDigestType,
                      final byte[] pVector) throws JDataException {
        /* Store the KeyType and the Generator */
        theMacType = MacType.HMAC;
        theDigestType = pDigestType;
        theKey = null;
        theInitVector = (pVector == null)
                ? null
                : Arrays.copyOf(pVector, pVector.length);

        /* Determine the algorithm */
        boolean useLongHash = pGenerator.useLongHash();
        theAlgo = pDigestType.getMacAlgorithm(useLongHash);

        /* Protect against exceptions */
        try {
            String myProviderName = pGenerator.getProvider().getProvider();
            theMac = Mac.getInstance(theAlgo, myProviderName);
            if (theInitVector != null) {
                initialiseMac(pVector);
            }

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            /* Throw the exception */
            throw new JDataException(ExceptionClass.CRYPTO, ERROR_CREATE, e);
        }
    }

    /**
     * Constructor for a new HMac digest of specified digest type.
     * @param pGenerator the security generator
     * @param pDigestType DigestType
     * @throws JDataException on error
     */
    protected DataMac(final SecurityGenerator pGenerator,
                      final DigestType pDigestType) throws JDataException {
        /* Create digest with random initialisation vector */
        this(pGenerator, pDigestType, pGenerator.getRandomBytes(pGenerator.getKeyLen()
                                                                / Byte.SIZE));
    }

    /**
     * Constructor for a new hMac of random type.
     * @param pGenerator the security generator
     * @throws JDataException on error
     */
    protected DataMac(final SecurityGenerator pGenerator) throws JDataException {
        /* Create digest for random digest type */
        this(pGenerator, DigestType.getRandomTypes(1, pGenerator.getRandom())[0]);
    }

    /**
     * Constructor for a new hMac of random type.
     * @param pGenerator the security generator
     * @param pMacType the mac type
     * @param pKey the symmetric key to use for Mac
     * @param pVector the initialisation vector
     * @throws JDataException on error
     */
    protected DataMac(final SecurityGenerator pGenerator,
                      final MacType pMacType,
                      final SymmetricKey pKey,
                      final byte[] pVector) throws JDataException {
        /* Store the KeyType and the Generator */
        theKey = pKey;
        SymKeyType myKeyType = theKey.getKeyType();
        theMacType = myKeyType.adjustMacType(pMacType);
        theDigestType = null;
        theInitVector = (pVector == null)
                ? null
                : Arrays.copyOf(pVector, pVector.length);
        theAlgo = myKeyType.getMacAlgorithm(theMacType);

        /* Protect against exceptions */
        try {
            /* Return a digest for the algorithm */
            String myProviderName = pGenerator.getProvider().getProvider();
            theMac = Mac.getInstance(theAlgo, myProviderName);
            if (pVector != null) {
                theMac.init(theKey.getSecretKey(), new IvParameterSpec(pVector));
            } else {
                theMac.init(theKey.getSecretKey());
            }

            /* Catch exceptions */
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            /* Throw the exception */
            throw new JDataException(ExceptionClass.CRYPTO, ERROR_CREATE, e);
        }
    }

    /**
     * Initialise a Mac object.
     * @param pVector the initialisation vector
     * @throws JDataException on error
     */
    public void initialiseMac(final byte[] pVector) throws JDataException {
        /* Protect against exceptions */
        try {
            SecretKey myKey = new SecretKeySpec(pVector, theAlgo);
            theMac.init(myKey);

            /* Catch exceptions */
        } catch (InvalidKeyException e) {
            /* Throw the exception */
            throw new JDataException(ExceptionClass.CRYPTO, ERROR_CREATE, e);
        }
    }

    /**
     * Update the mac with a portion of a byte array.
     * @param pBytes the bytes to update with.
     * @param pOffset the offset of the data within the byte array
     * @param pLength the length of the data to use
     */
    public void update(final byte[] pBytes,
                       final int pOffset,
                       final int pLength) {
        /* Update the mac */
        theMac.update(pBytes, pOffset, pLength);
    }

    /**
     * Update the mac with a byte array.
     * @param pBytes the bytes to update with.
     */
    public void update(final byte[] pBytes) {
        /* Update the mac */
        theMac.update(pBytes);
    }

    /**
     * Update the mac with a single byte.
     * @param pByte the byte to update with.
     */
    public void update(final byte pByte) {
        /* Update the mac */
        theMac.update(pByte);
    }

    /**
     * Update the mac with a byteBuffer.
     * @param pBuffer the buffer to update with.
     */
    public void update(final ByteBuffer pBuffer) {
        /* Update the mac */
        theMac.update(pBuffer);
    }

    /**
     * Reset the mac.
     */
    public void reset() {
        /* Reset the mac */
        theMac.reset();
    }

    /**
     * Calculate the mac and reset it.
     * @return the code
     */
    public byte[] finish() {
        /* Calculate the mac */
        return theMac.doFinal();
    }

    /**
     * Update the MAC, calculate and reset it.
     * @param pBytes the bytes to update with.
     * @return the code
     */
    public byte[] finish(final byte[] pBytes) {
        /* Calculate the mac */
        return theMac.doFinal(pBytes);
    }

    /**
     * Calculate the MAC, and return it in the buffer provided.
     * @param pBuffer the buffer to return the mac in.
     * @param pOffset the offset in the buffer to store the MAC.
     * @throws JDataException if buffer too short
     */
    public void finish(final byte[] pBuffer,
                       final int pOffset) throws JDataException {
        /* Calculate the mac */
        try {
            theMac.doFinal(pBuffer, pOffset);
        } catch (ShortBufferException | IllegalStateException e) {
            /* Throw the exception */
            throw new JDataException(ExceptionClass.CRYPTO, ERROR_CALC, e);
        }
    }
}
