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
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;

import net.sourceforge.joceanus.jdatamanager.DataConverter;
import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;

/**
 * Wrapper class for Cipher used to encryption data objects.
 * @author Tony Washer
 */
public class DataCipher {
    /**
     * The cipher.
     */
    private Cipher theCipher = null;

    /**
     * The SymmetricKey.
     */
    private SymmetricKey theSymKey = null;

    /**
     * Get Symmetric Key Type.
     * @return the key type
     */
    protected SymKeyType getSymKeyType() {
        return theSymKey.getKeyType();
    }

    /**
     * Constructor.
     * @param pCipher the initialised cipher
     * @param pKey the Symmetric Key
     */
    protected DataCipher(final Cipher pCipher,
                         final SymmetricKey pKey) {
        theCipher = pCipher;
        theSymKey = pKey;
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
        byte[] myBytes;

        /* Protect against exceptions */
        try {
            /* Initialise the cipher using the vector */
            initialiseEncryption(pVector);

            /* Encrypt the byte array */
            myBytes = theCipher.doFinal(pBytes);
        } catch (IllegalBlockSizeException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to encrypt bytes", e);
        } catch (BadPaddingException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to encrypt bytes", e);
        }

        /* Return to caller */
        return myBytes;
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
        byte[] myBytes;

        /* Protect against exceptions */
        try {
            /* Initialise the cipher using the vector */
            initialiseDecryption(pVector);

            /* Encrypt the byte array */
            myBytes = theCipher.doFinal(pBytes);
        } catch (IllegalBlockSizeException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to decrypt bytes", e);
        } catch (BadPaddingException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to decrypt bytes", e);
        }

        /* Return to caller */
        return myBytes;
    }

    /**
     * Encrypt string.
     * @param pString string to encrypt
     * @return Encrypted bytes
     * @throws JDataException on error
     */
    public byte[] encryptString(final String pString) throws JDataException {
        byte[] myBytes;

        /* Protect against exceptions */
        try {
            /* Convert the string to a byte array */
            myBytes = DataConverter.stringToByteArray(pString);

            /* Encrypt the byte array */
            myBytes = theCipher.doFinal(myBytes);
        } catch (IllegalBlockSizeException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to encrypt string", e);
        } catch (BadPaddingException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to encrypt string", e);
        }

        /* Return to caller */
        return myBytes;
    }

    /**
     * Encrypt character array.
     * @param pChars Characters to encrypt
     * @return Encrypted bytes
     * @throws JDataException on error
     */
    public byte[] encryptChars(final char[] pChars) throws JDataException {
        byte[] myBytes;
        byte[] myRawBytes;

        /* Protect against exceptions */
        try {
            /* Convert the characters to a byte array */
            myRawBytes = DataConverter.charsToByteArray(pChars);

            /* Encrypt the characters */
            myBytes = theCipher.doFinal(myRawBytes);

            /* Clear out the bytes */
            Arrays.fill(myRawBytes, (byte) 0);
        } catch (IllegalBlockSizeException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to encrypt character array", e);
        } catch (BadPaddingException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to encrypt character array", e);
        }

        /* Return to caller */
        return myBytes;
    }

    /**
     * Decrypt bytes into a string.
     * @param pBytes bytes to decrypt
     * @return Decrypted string
     * @throws JDataException on error
     */
    public String decryptString(final byte[] pBytes) throws JDataException {
        byte[] myBytes;
        String myString;

        /* Protect against exceptions */
        try {
            /* Decrypt the bytes */
            myBytes = theCipher.doFinal(pBytes);

            /* Convert the bytes to a string */
            myString = DataConverter.byteArrayToString(myBytes);
        } catch (IllegalBlockSizeException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to decrypt string", e);
        } catch (BadPaddingException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to decrypt string", e);
        }

        /* Return to caller */
        return myString;
    }

    /**
     * Decrypt bytes into a character array.
     * @param pBytes Bytes to decrypt
     * @return Decrypted character array
     * @throws JDataException on error
     */
    public char[] decryptChars(final byte[] pBytes) throws JDataException {
        byte[] myBytes;
        char[] myChars;

        /* Protect against exceptions */
        try {
            /* Decrypt the bytes */
            myBytes = theCipher.doFinal(pBytes);

            /* Convert the bytes to characters */
            myChars = DataConverter.bytesToCharArray(myBytes);

            /* Clear out the bytes */
            Arrays.fill(myBytes, (byte) 0);
        } catch (IllegalBlockSizeException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to decrypt character array", e);
        } catch (BadPaddingException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to decrypt character array", e);
        }

        /* Return to caller */
        return myChars;
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
            theCipher.init(Cipher.ENCRYPT_MODE, theSymKey.getSecretKey(), myParms);
        } catch (InvalidAlgorithmParameterException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to initialise encryption", e);
        } catch (InvalidKeyException e) {
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
            theCipher.init(Cipher.DECRYPT_MODE, theSymKey.getSecretKey(), myParms);
        } catch (InvalidAlgorithmParameterException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to initialise decryption", e);
        } catch (InvalidKeyException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to initialise decryption", e);
        }
    }
}
