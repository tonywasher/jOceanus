/*******************************************************************************
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JGordianKnot;

import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import net.sourceforge.JDataManager.DataConverter;
import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.ModelException.ExceptionClass;
import net.sourceforge.JDataManager.ReportFields;
import net.sourceforge.JDataManager.ReportFields.ReportField;
import net.sourceforge.JDataManager.ReportObject.ReportDetail;

public class DataCipher implements ReportDetail {
    /**
     * Report fields
     */
    protected static final ReportFields theFields = new ReportFields(DataCipher.class.getSimpleName());

    /* Field IDs */
    public static final ReportField FIELD_SYMKEY = theFields.declareLocalField("SymmetricKey");

    @Override
    public ReportFields getReportFields() {
        return theFields;
    }

    @Override
    public Object getFieldValue(ReportField pField) {
        if (pField == FIELD_SYMKEY)
            return theSymKey;
        return null;
    }

    @Override
    public String getObjectSummary() {
        return "DataCipher(" + getSymKeyType() + ")";
    }

    /**
     * The cipher
     */
    private Cipher theCipher = null;

    /**
     * The SymmetricKey (if used)
     */
    private SymmetricKey theSymKey = null;

    /**
     * Get Symmetric Key Type
     * @return the key type
     */
    protected SymKeyType getSymKeyType() {
        return (theSymKey == null) ? null : theSymKey.getKeyType();
    }

    /**
     * Constructor
     * @param pCipher the initialised cipher
     * @param pKey the Symmetric Key
     */
    protected DataCipher(Cipher pCipher,
                         SymmetricKey pKey) {
        theCipher = pCipher;
        theSymKey = pKey;
    }

    @Override
    public boolean equals(Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat)
            return true;
        if (pThat == null)
            return false;

        /* Make sure that the object is a DataCipher */
        if (pThat.getClass() != this.getClass())
            return false;

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
     * Encrypt bytes
     * @param pBytes bytes to encrypt
     * @param pVector initialisation vector
     * @return Encrypted bytes
     * @throws ModelException
     */
    public byte[] encryptBytes(byte[] pBytes,
                               byte[] pVector) throws ModelException {
        byte[] myBytes;

        /* Protect against exceptions */
        try {
            /* Initialise the cipher using the vector */
            initialiseEncryption(pVector);

            /* Encrypt the byte array */
            myBytes = theCipher.doFinal(pBytes);
        } catch (Exception e) {
            throw new ModelException(ExceptionClass.CRYPTO, "Failed to encrypt bytes", e);
        }

        /* Return to caller */
        return myBytes;
    }

    /**
     * Decrypt bytes
     * @param pBytes bytes to decrypt
     * @param pVector initialisation vector
     * @return Decrypted bytes
     * @throws ModelException
     */
    public byte[] decryptBytes(byte[] pBytes,
                               byte[] pVector) throws ModelException {
        byte[] myBytes;

        /* Protect against exceptions */
        try {
            /* Initialise the cipher using the vector */
            initialiseDecryption(pVector);

            /* Encrypt the byte array */
            myBytes = theCipher.doFinal(pBytes);
        } catch (Exception e) {
            throw new ModelException(ExceptionClass.CRYPTO, "Failed to decrypt bytes", e);
        }

        /* Return to caller */
        return myBytes;
    }

    /**
     * Encrypt string
     * @param pString string to encrypt
     * @return Encrypted bytes
     * @throws ModelException
     */
    public byte[] encryptString(String pString) throws ModelException {
        byte[] myBytes;

        /* Protect against exceptions */
        try {
            /* Convert the string to a byte array */
            myBytes = DataConverter.stringToByteArray(pString);

            /* Encrypt the byte array */
            myBytes = theCipher.doFinal(myBytes);
        } catch (Exception e) {
            throw new ModelException(ExceptionClass.CRYPTO, "Failed to encrypt string", e);
        }

        /* Return to caller */
        return myBytes;
    }

    /**
     * Encrypt character array
     * @param pChars Characters to encrypt
     * @return Encrypted bytes
     * @throws ModelException
     */
    public byte[] encryptChars(char[] pChars) throws ModelException {
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
        } catch (Exception e) {
            throw new ModelException(ExceptionClass.CRYPTO, "Failed to encrypt character array", e);
        }

        /* Return to caller */
        return myBytes;
    }

    /**
     * Decrypt bytes into a string
     * @param pBytes bytes to decrypt
     * @return Decrypted string
     * @throws ModelException
     */
    public String decryptString(byte[] pBytes) throws ModelException {
        byte[] myBytes;
        String myString;

        /* Protect against exceptions */
        try {
            /* Decrypt the bytes */
            myBytes = theCipher.doFinal(pBytes);

            /* Convert the bytes to a string */
            myString = DataConverter.byteArrayToString(myBytes);
        } catch (Exception e) {
            throw new ModelException(ExceptionClass.CRYPTO, "Failed to decrypt string", e);
        }

        /* Return to caller */
        return myString;
    }

    /**
     * Decrypt bytes into a character array
     * @param pBytes Bytes to decrypt
     * @return Decrypted character array
     * @throws ModelException
     */
    public char[] decryptChars(byte[] pBytes) throws ModelException {
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
        } catch (Exception e) {
            throw new ModelException(ExceptionClass.CRYPTO, "Failed to decrypt character array", e);
        }

        /* Return to caller */
        return myChars;
    }

    /**
     * Initialise encryption
     * @param pVector initialisation vector
     * @throws Exception
     */
    private void initialiseEncryption(byte[] pVector) throws Exception {
        AlgorithmParameterSpec myParms;

        /* If we have a symmetric key */
        if (theSymKey != null) {
            /* Initialise the cipher using the vector */
            myParms = new IvParameterSpec(pVector);
            theCipher.init(Cipher.ENCRYPT_MODE, theSymKey.getSecretKey(), myParms);
        }
    }

    /**
     * Initialise decryption
     * @param pVector initialisation vector
     * @throws Exception
     */
    private void initialiseDecryption(byte[] pVector) throws Exception {
        AlgorithmParameterSpec myParms;

        /* If we have a symmetric key */
        if (theSymKey != null) {
            /* Initialise the cipher using the vector */
            myParms = new IvParameterSpec(pVector);
            theCipher.init(Cipher.DECRYPT_MODE, theSymKey.getSecretKey(), myParms);
        }
    }
}
