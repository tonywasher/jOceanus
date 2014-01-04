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
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jgordianknot.SecurityRegister.MacRegister;

/**
 * Encapsulation of a Mac.
 */
public class DataMac {
    /**
     * Creation failure error text.
     */
    private static final String ERROR_CREATE = "failed to create Mac";

    /**
     * Initialisation failure error text.
     */
    private static final String ERROR_INIT = "failed to initialise Mac";

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
     * The SymKeyType.
     */
    private final SymKeyType theKeyType;

    /**
     * The SecretKey.
     */
    private SecretKey theKey;

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
     * Obtain the SymKey Type.
     * @return the SymKey type
     */
    public SymKeyType getSymKeyType() {
        return theKeyType;
    }

    /**
     * Obtain the secret key.
     * @return the secret key
     */
    protected SecretKey getSecretKey() {
        return theKey;
    }

    /**
     * Obtain the initialisation vector.
     * @return the initialisation vector
     */
    public byte[] getInitVector() {
        return (theInitVector == null)
                ? null
                : Arrays.copyOf(theInitVector, theInitVector.length);
    }

    /**
     * Obtain the mac length.
     * @return the mac length
     */
    public int getMacLength() {
        return theMac.getMacLength();
    }

    /**
     * DataMac Generator.
     * @param pGenerator the security generator
     * @return the new Mac
     * @throws JDataException on error
     */
    protected static DataMac generateRandomMac(final SecurityGenerator pGenerator) throws JDataException {
        /* Access random generator */
        SecureRandom myRandom = pGenerator.getRandom();
        MacType[] myType = MacType.getRandomTypes(1, myRandom);

        /* Generate a random Mac for the Mac type */
        return generateRandomMac(pGenerator, myType[0]);
    }

    /**
     * DataMac Generator.
     * @param pGenerator the security generator
     * @param pMacType the MacType
     * @return the new Mac
     * @throws JDataException on error
     */
    protected static DataMac generateRandomMac(final SecurityGenerator pGenerator,
                                               final MacType pMacType) throws JDataException {
        /* Switch on MacType */
        switch (pMacType) {
            case HMAC:
                return generateRandomDigestMac(pGenerator);
            case GMAC:
            case POLY1305:
                return generateRandomSymKeyMac(pGenerator, pMacType);
            default:
                return generateRandomOtherMac(pGenerator, pMacType);
        }
    }

    /**
     * DataMac Generator.
     * @param pGenerator the security generator
     * @return the new Mac
     * @throws JDataException on error
     */
    protected static DataMac generateRandomDigestMac(final SecurityGenerator pGenerator) throws JDataException {
        /* Access random generator */
        SecureRandom myRandom = pGenerator.getRandom();
        DigestType[] myType = DigestType.getRandomTypes(1, myRandom);

        /* Generate a random Mac for the Mac type */
        return generateRandomDigestMac(pGenerator, myType[0]);
    }

    /**
     * DataMac Generator.
     * @param pGenerator the security generator
     * @param pDigestType the digest type
     * @return the new Mac
     * @throws JDataException on error
     */
    protected static DataMac generateRandomDigestMac(final SecurityGenerator pGenerator,
                                                     final DigestType pDigestType) throws JDataException {
        /* Generate a new Secret Key */
        SecurityRegister myRegister = pGenerator.getRegister();
        MacRegister myReg = myRegister.getMacRegistration(pDigestType, pGenerator.getKeyLen());
        SecretKey myKey = myReg.generateKey();

        /* Generate the Mac */
        return new DataMac(pGenerator, pDigestType, myKey);
    }

    /**
     * DataMac Generator.
     * @param pGenerator the security generator
     * @param pMacType the MacType
     * @return the new Mac
     * @throws JDataException on error
     */
    protected static DataMac generateRandomSymKeyMac(final SecurityGenerator pGenerator,
                                                     final MacType pMacType) throws JDataException {
        /* Access random generator */
        SecureRandom myRandom = pGenerator.getRandom();
        SymKeyType[] myType = SymKeyType.getRandomTypes(1, myRandom, true);

        /* Generate a random Mac for the Mac type */
        return generateRandomSymKeyMac(pGenerator, pMacType, myType[0]);
    }

    /**
     * DataMac Generator.
     * @param pGenerator the security generator
     * @param pMacType the MacType
     * @return the new Mac
     * @throws JDataException on error
     */
    protected static DataMac generateRandomOtherMac(final SecurityGenerator pGenerator,
                                                    final MacType pMacType) throws JDataException {
        /* Generate a new Secret Key */
        SecurityRegister myRegister = pGenerator.getRegister();
        MacRegister myReg = myRegister.getMacRegistration(pMacType, pGenerator.getKeyLen());
        SecretKey myKey = myReg.generateKey();

        /* Generate an initialisation vector */
        int myIVLen = pMacType.getIVLen();
        byte[] myInitVector = (myIVLen != 0)
                ? pGenerator.getRandomBytes(myIVLen)
                : null;

        /* Generate the Mac */
        return new DataMac(pGenerator, pMacType, myKey, myInitVector);
    }

    /**
     * DataMac Generator.
     * @param pGenerator the security generator
     * @param pMacType the MacType
     * @param pKeyType the SymKey type
     * @return the new Mac
     * @throws JDataException on error
     */
    protected static DataMac generateRandomSymKeyMac(final SecurityGenerator pGenerator,
                                                     final MacType pMacType,
                                                     final SymKeyType pKeyType) throws JDataException {
        /* Generate a new Secret Key */
        SecurityRegister myRegister = pGenerator.getRegister();
        MacRegister myReg = myRegister.getMacRegistration(pMacType, pKeyType, pGenerator.getKeyLen());
        SecretKey myKey = myReg.generateKey();

        /* Generate an initialisation vector */
        byte[] myInitVector = pGenerator.getRandomBytes(pMacType.getIVLen());

        /* Generate the Mac */
        return new DataMac(pGenerator, pMacType, pKeyType, myKey, myInitVector);
    }

    /**
     * Constructor for a new HMac digest of specified parameters.
     * @param pGenerator the security generator
     * @param pDigestType DigestType
     * @param pKey the secret Key (or null)
     * @throws JDataException on error
     */
    protected DataMac(final SecurityGenerator pGenerator,
                      final DigestType pDigestType,
                      final SecretKey pKey) throws JDataException {
        /* Store the KeyType and the Generator */
        theMacType = MacType.HMAC;
        theDigestType = pDigestType;
        theKey = pKey;
        theInitVector = null;
        theKeyType = null;

        /* Determine the algorithm */
        boolean useLongHash = pGenerator.useLongHash();
        theAlgo = pDigestType.getMacAlgorithm(useLongHash);

        /* Protect against exceptions */
        try {
            theMac = Mac.getInstance(theAlgo, pGenerator.getProviderName());
            if (theKey != null) {
                theMac.init(theKey);
            }

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException e) {
            /* Throw the exception */
            throw new JDataException(ExceptionClass.CRYPTO, ERROR_CREATE, e);
        }
    }

    /**
     * Constructor for a new GMAC/Poly1305Mac of specified parameters.
     * @param pGenerator the security generator
     * @param pMacType the mac type
     * @param pKeyType the key type
     * @param pKey the secret key
     * @param pVector the initialisation vector
     * @throws JDataException on error
     */
    private DataMac(final SecurityGenerator pGenerator,
                    final MacType pMacType,
                    final SymKeyType pKeyType,
                    final SecretKey pKey,
                    final byte[] pVector) throws JDataException {
        /* Not allowed for non standard block size */
        if (!pKeyType.isStdBlock()) {
            throw new UnsupportedOperationException();
        }

        /* Store the KeyType and the Generator */
        theKey = pKey;
        theMacType = pMacType;
        theDigestType = null;
        theKeyType = pKeyType;
        theInitVector = Arrays.copyOf(pVector, pVector.length);
        theAlgo = pMacType.getAlgorithm(theKeyType);

        /* Protect against exceptions */
        try {
            /* Return a Mac for the algorithm */
            theMac = Mac.getInstance(theAlgo, pGenerator.getProviderName());
            theMac.init(theKey, new IvParameterSpec(pVector));

            /* Catch exceptions */
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            /* Throw the exception */
            throw new JDataException(ExceptionClass.CRYPTO, ERROR_CREATE, e);
        }
    }

    /**
     * Constructor for a new Skein/VMPCMac of specified parameters.
     * @param pGenerator the security generator
     * @param pMacType the mac type
     * @param pKey the secret key
     * @param pVector the initialisation vector
     * @throws JDataException on error
     */
    private DataMac(final SecurityGenerator pGenerator,
                    final MacType pMacType,
                    final SecretKey pKey,
                    final byte[] pVector) throws JDataException {
        /* Store the KeyType and the Generator */
        theKey = pKey;
        theMacType = pMacType;
        theDigestType = null;
        theKeyType = null;
        theInitVector = (pVector == null)
                ? null
                : Arrays.copyOf(pVector, pVector.length);

        /* Determine the algorithm */
        boolean useLongHash = pGenerator.useLongHash();
        theAlgo = pMacType.getAlgorithm(useLongHash);

        /* Protect against exceptions */
        try {
            /* Return a Mac for the algorithm */
            theMac = Mac.getInstance(theAlgo, pGenerator.getProviderName());
            if (pVector != null) {
                theMac.init(theKey, new IvParameterSpec(pVector));
            } else {
                theMac.init(theKey);
            }

            /* Catch exceptions */
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            /* Throw the exception */
            throw new JDataException(ExceptionClass.CRYPTO, ERROR_CREATE, e);
        }
    }

    /**
     * Initialise an HMac object.
     * @param pKeyBytes the key bytes
     * @throws JDataException on error
     */
    public void setSecretKey(final byte[] pKeyBytes) throws JDataException {
        /* Only allowed for HMac */
        if (theMacType != MacType.HMAC) {
            throw new UnsupportedOperationException();
        }

        /* Protect against exceptions */
        try {
            SecretKey myKey = new SecretKeySpec(pKeyBytes, theAlgo);
            theMac.init(myKey);

            /* Catch exceptions */
        } catch (InvalidKeyException e) {
            /* Throw the exception */
            throw new JDataException(ExceptionClass.CRYPTO, ERROR_INIT, e);
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
