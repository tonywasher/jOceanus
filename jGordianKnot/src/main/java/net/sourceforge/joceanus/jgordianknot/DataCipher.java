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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;

import org.bouncycastle.util.Arrays;

/**
 * Wrapper class for Cipher used to encryption data objects.
 */
public class DataCipher {
    /**
     * Data length error text.
     */
    private static final String ERROR_DATALEN = "Invalid data length";

    /**
     * Wrap block length (128-bits) in bytes.
     * <p>
     * This needs to be the half the block size to ensure that the wrap encryption is always performed on a multiple of the block
     */
    private static final int WRAP_BLOCKLEN = CipherSet.IVSIZE >> 1;

    /**
     * Wrap repeat count.
     */
    private static final int WRAP_COUNT = 6;

    /**
     * Multiplier to obtain IV from vector.
     */
    private static final int VECTOR_SHIFT = 7;

    /**
     * The cipher.
     */
    private final Cipher theCipher;

    /**
     * The SymmetricKey.
     */
    private final SymmetricKey theSymKey;

    /**
     * The SecretKey.
     */
    private final SecretKey theSecretKey;

    /**
     * Get Symmetric Key Type.
     * @return the key type
     */
    protected SymKeyType getSymKeyType() {
        return theSymKey.getKeyType();
    }

    /**
     * Get Block Size.
     * @return the block size
     */
    protected int getBlockSize() {
        return theCipher.getBlockSize();
    }

    /**
     * Constructor.
     * @param pKey the Symmetric Key
     * @throws JDataException on error
     */
    protected DataCipher(final SymmetricKey pKey) throws JDataException {
        /* Record keys */
        theSymKey = pKey;
        theSecretKey = theSymKey.getSecretKey();

        /* protect against exceptions */
        try {
            /* Create a new cipher */
            SecurityGenerator myGenerator = theSymKey.getGenerator();
            SecurityProvider myProvider = myGenerator.getProvider();
            SymKeyType myKeyType = theSymKey.getKeyType();
            theCipher = Cipher.getInstance(myKeyType.getCipher(), myProvider.getProvider());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to create cipher", e);
        }
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is a DataCipher */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target Cipher */
        DataCipher myThat = (DataCipher) pThat;

        /* Check the symmetric key */
        return theSymKey.equals(myThat.theSymKey);
    }

    @Override
    public int hashCode() {
        return theSymKey.hashCode();
    }

    /**
     * Encrypt bytes.
     * @param pBytes bytes to encrypt
     * @param pVector initialisation vector
     * @return Encrypted bytes
     * @throws JDataException on error
     */
    public byte[] encryptBytes(final byte[] pBytes,
                               final byte[] pVector) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Access the shifted vector */
            byte[] myShift = getShiftedVector(pVector);

            /* Initialise the cipher using the shifted vector */
            initialiseEncryption(myShift);

            /* Encrypt the byte array */
            return theCipher.doFinal(pBytes);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to encrypt bytes", e);
        }
    }

    /**
     * Decrypt bytes.
     * @param pBytes bytes to decrypt
     * @param pVector initialisation vector
     * @return Decrypted bytes
     * @throws JDataException on error
     */
    public byte[] decryptBytes(final byte[] pBytes,
                               final byte[] pVector) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Access the shifted vector */
            byte[] myShift = getShiftedVector(pVector);

            /* Initialise the cipher using the vector */
            initialiseDecryption(myShift);

            /* Encrypt the byte array */
            return theCipher.doFinal(pBytes);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to decrypt bytes", e);
        }
    }

    /**
     * Wrap bytes.
     * @param pBytes the bytes to wrap
     * @param pVector initialisation vector
     * @return the wrapped bytes
     * @throws JDataException on error
     */
    protected byte[] wrapBytes(final byte[] pBytes,
                               final byte[] pVector) throws JDataException {
        /* Access the shifted vector */
        byte[] myShift = getShiftedVector(pVector);

        /* Determine number of blocks */
        int myDataLen = pBytes.length;
        int myNumBlocks = myDataLen
                          / WRAP_BLOCKLEN;

        /* Data must be a multiple of WRAP_BLOCKLEN */
        if ((myNumBlocks * WRAP_BLOCKLEN) != myDataLen) {
            throw new IllegalArgumentException(ERROR_DATALEN);
        }

        /* Allocate buffer for data and encryption */
        byte[] myData = new byte[myDataLen
                                 + WRAP_BLOCKLEN];
        byte[] myBuffer = new byte[WRAP_BLOCKLEN << 1];

        /* Build the data block */
        System.arraycopy(myShift, 0, myData, 0, WRAP_BLOCKLEN);
        System.arraycopy(pBytes, 0, myData, WRAP_BLOCKLEN, myDataLen);

        /* Initialise the cipher using the shifted vector */
        initialiseEncryption(myShift);

        /* Loop WRAP_COUNT times */
        for (int myCycle = 0, myCount = 1; myCycle < WRAP_COUNT; myCycle++) {
            /* Loop through the data blocks */
            for (int myBlock = 1, myOffset = WRAP_BLOCKLEN; myBlock <= myNumBlocks; myBlock++, myOffset += WRAP_BLOCKLEN) {
                /* Build the data to be encrypted */
                System.arraycopy(myData, 0, myBuffer, 0, WRAP_BLOCKLEN);
                System.arraycopy(myData, myOffset, myBuffer, WRAP_BLOCKLEN, WRAP_BLOCKLEN);

                /* Encrypt the byte array */
                byte[] myResult = theCipher.update(myBuffer);

                /* Adjust the result using the count as a mask */
                for (int myMask = myCount++, myIndex = WRAP_BLOCKLEN - 1; myMask != 0; myMask >>>= Byte.SIZE) {
                    myResult[myIndex--] ^= (byte) myMask;
                }

                /* Restore encrypted data */
                System.arraycopy(myResult, 0, myData, 0, WRAP_BLOCKLEN);
                System.arraycopy(myResult, WRAP_BLOCKLEN, myData, myOffset, WRAP_BLOCKLEN);
            }
        }

        /* Return the wrapped data */
        return myData;
    }

    /**
     * unWrap bytes.
     * @param pBytes the bytes to unwrap
     * @param pVector initialisation vector
     * @return the unwrapped bytes
     * @throws JDataException on error
     */
    protected byte[] unwrapBytes(final byte[] pBytes,
                                 final byte[] pVector) throws JDataException {
        /* Access the shifted vector */
        byte[] myShift = getShiftedVector(pVector);
        byte[] myCheck = Arrays.copyOf(pVector, WRAP_BLOCKLEN);

        /* Determine number of blocks */
        int myDataLen = pBytes.length
                        - WRAP_BLOCKLEN;
        int myNumBlocks = myDataLen
                          / WRAP_BLOCKLEN;

        /* Data must be a multiple of WRAP_BLOCKLEN */
        if ((myNumBlocks * WRAP_BLOCKLEN) != myDataLen) {
            throw new IllegalArgumentException(ERROR_DATALEN);
        }

        /* Allocate buffers for data and encryption */
        byte[] myIV = new byte[WRAP_BLOCKLEN];
        byte[] myData = new byte[myDataLen];
        byte[] myBuffer = new byte[WRAP_BLOCKLEN << 1];

        /* Build the data block */
        System.arraycopy(pBytes, 0, myIV, 0, WRAP_BLOCKLEN);
        System.arraycopy(pBytes, WRAP_BLOCKLEN, myData, 0, myDataLen);

        /* Initialise the cipher using the shifted vector */
        initialiseDecryption(myShift);

        /* Loop WRAP_COUNT times */
        for (int myCycle = WRAP_COUNT - 1, myCount = myNumBlocks
                                                     * (myCycle + 1); myCycle >= 0; myCycle--) {
            /* Loop through the data blocks */
            for (int myBlock = myNumBlocks, myOffset = WRAP_BLOCKLEN
                                                       * (myBlock - 1); myBlock >= 1; myBlock--, myOffset -= WRAP_BLOCKLEN) {
                /* Build the data to be decrypted */
                System.arraycopy(myIV, 0, myBuffer, 0, WRAP_BLOCKLEN);
                System.arraycopy(myData, myOffset, myBuffer, WRAP_BLOCKLEN, WRAP_BLOCKLEN);

                /* Adjust the buffer using the count as a mask */
                for (int myMask = myCount--, myIndex = WRAP_BLOCKLEN - 1; myMask != 0; myMask >>>= Byte.SIZE) {
                    myBuffer[myIndex--] ^= (byte) myMask;
                }

                /* Decrypt the byte array */
                byte[] myResult = theCipher.update(myBuffer);

                /* Restore encrypted data */
                System.arraycopy(myResult, 0, myIV, 0, WRAP_BLOCKLEN);
                System.arraycopy(myResult, WRAP_BLOCKLEN, myData, myOffset, WRAP_BLOCKLEN);
            }
        }

        /* Perform integrity check */
        if (!Arrays.areEqual(myIV, myCheck)) {
            throw new JDataException(ExceptionClass.CRYPTO, "Checksum failed");
        }

        /* Return unwrapped data */
        return myData;
    }

    /**
     * Initialise encryption.
     * @param pVector initialisation vector
     * @throws JDataException on error
     */
    private void initialiseEncryption(final byte[] pVector) throws JDataException {
        try {
            /* Initialise the cipher using the vector */
            AlgorithmParameterSpec myParms = new IvParameterSpec(pVector);
            theCipher.init(Cipher.ENCRYPT_MODE, theSecretKey, myParms);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to initialise encryption", e);
        }
    }

    /**
     * Initialise decryption.
     * @param pVector initialisation vector
     * @throws JDataException on error
     */
    private void initialiseDecryption(final byte[] pVector) throws JDataException {
        try {
            /* Initialise the cipher using the vector */
            AlgorithmParameterSpec myParms = new IvParameterSpec(pVector);
            theCipher.init(Cipher.DECRYPT_MODE, theSecretKey, myParms);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to initialise decryption", e);
        }
    }

    /**
     * Obtain shifted Initialisation vector.
     * @param pVector the initialisation vector
     * @return the shifted vector
     */
    private byte[] getShiftedVector(final byte[] pVector) {
        /* Determine length of input and output vectors */
        int myVectorLen = pVector.length;
        int myLen = theCipher.getBlockSize();
        SymKeyType myType = theSymKey.getKeyType();
        byte[] myNew = new byte[myLen];

        /* Determine index into array for Key Type */
        int myIndex = VECTOR_SHIFT
                      * myType.getId();
        myIndex %= myVectorLen;

        /* Determine remaining data length in vector */
        int myRemainder = myVectorLen
                          - myIndex;

        /* If we need a single copy */
        if (myRemainder >= myLen) {
            /* Copy whole part */
            System.arraycopy(pVector, myIndex, myNew, 0, myLen);
        } else {
            /* Build in two parts */
            System.arraycopy(pVector, myIndex, myNew, 0, myRemainder);
            System.arraycopy(pVector, 0, myNew, myRemainder, myLen
                                                             - myRemainder);
        }

        /* return the shifted vector */
        return myNew;
    }
}
