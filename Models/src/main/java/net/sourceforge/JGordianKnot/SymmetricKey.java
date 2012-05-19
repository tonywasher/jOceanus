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
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.ModelException.ExceptionClass;
import net.sourceforge.JDataManager.ReportFields;
import net.sourceforge.JDataManager.ReportFields.ReportField;
import net.sourceforge.JDataManager.ReportObject.ReportDetail;
import net.sourceforge.JGordianKnot.ZipFile.StreamCipher;

public class SymmetricKey implements ReportDetail {
    /**
     * Report fields
     */
    protected static final ReportFields theFields = new ReportFields(SymmetricKey.class.getSimpleName());

    /* Field IDs */
    public static final ReportField FIELD_KEYTYPE = theFields.declareLocalField("KeyType");
    public static final ReportField FIELD_KEYLEN = theFields.declareLocalField("KeyLength");
    public static final ReportField FIELD_IVLEN = theFields.declareLocalField("IVLength");

    @Override
    public ReportFields getReportFields() {
        return theFields;
    }

    @Override
    public Object getFieldValue(ReportField pField) {
        if (pField == FIELD_KEYTYPE)
            return theKeyType;
        if (pField == FIELD_KEYLEN)
            return theKeyLen;
        if (pField == FIELD_IVLEN)
            return IVSIZE;
        return null;
    }

    @Override
    public String getObjectSummary() {
        return "SymmetricKey(" + theKeyType + ")";
    }

    /**
     * Encrypted ID Key Size
     */
    public final static int IDSIZE = 128;

    /**
     * Initialisation Vector size
     */
    public final static int IVSIZE = 16;

    /**
     * Restricted key length
     */
    private final static int smallKEYLEN = 128;

    /**
     * Unlimited key length
     */
    private final static int bigKEYLEN = 256;

    /**
     * The Secret Key
     */
    private SecretKey theKey = null;

    /**
     * The Key Type
     */
    private SymKeyType theKeyType = null;

    /**
     * The security generator
     */
    private final SecurityGenerator theGenerator;

    /**
     * The Key Length
     */
    private int theKeyLen = bigKEYLEN;

    /**
     * The Encoded KeyDef
     */
    private byte[] theEncodedKeyDef = null;

    /**
     * Obtain the secret key
     * @return the secret key
     */
    protected SecretKey getSecretKey() {
        return theKey;
    }

    /**
     * Obtain the secret key type
     * @return the secret key type
     */
    public SymKeyType getKeyType() {
        return theKeyType;
    }

    /**
     * Obtain the key length
     * @return the secret key length
     */
    public int getKeyLength() {
        return theKeyLen;
    }

    /**
     * Determine key length
     * @param useRestricted restricted mode?
     * @return key length
     */
    protected static int getKeyLen(boolean useRestricted) {
        return useRestricted ? smallKEYLEN : bigKEYLEN;
    }

    /**
     * Encryption length
     * @param pDataLength the length of data to be encrypted
     * @return the length of encrypted data
     */
    public static int getEncryptionLength(int pDataLength) {
        int iBlocks = 1 + ((pDataLength - 1) % IVSIZE);
        return iBlocks * IVSIZE;
    }

    /**
     * Constructor for a new randomly generated key
     * @param pGenerator the security generator
     * @param pKeyType Symmetric KeyType
     * @param useRestricted use restricted keys
     * @throws ModelException
     */
    public SymmetricKey(SecurityGenerator pGenerator,
                        SymKeyType pKeyType,
                        boolean useRestricted) throws ModelException {
        /* Store the KeyType and the Generator */
        theKeyType = pKeyType;
        theKeyLen = getKeyLen(useRestricted);
        theGenerator = pGenerator;

        /* Generate the new key */
        theKey = theGenerator.generateSecretKey(theKeyType, theKeyLen);
        theEncodedKeyDef = theKey.getEncoded();
    }

    /**
     * Constructor for a decoded symmetric key
     * @param pGenerator the security generator
     * @param pKey Secret Key for algorithm
     * @param pKeyType Symmetric KeyType
     * @throws ModelException
     */
    protected SymmetricKey(SecurityGenerator pGenerator,
                           SecretKey pKey,
                           SymKeyType pKeyType) throws ModelException {
        /* Store the KeyType and the Generator */
        theKeyType = pKeyType;
        theKeyLen = pKey.getEncoded().length;
        theGenerator = pGenerator;
        theKey = pKey;
        theEncodedKeyDef = theKey.getEncoded();
    }

    @Override
    public int hashCode() {
        /* Calculate and return the hashCode for this symmetric key */
        int hashCode = 19 * theEncodedKeyDef.hashCode();
        hashCode += theKeyType.getId();
        return hashCode;
    }

    @Override
    public boolean equals(Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat)
            return true;
        if (pThat == null)
            return false;

        /* Make sure that the object is a Symmetric Key */
        if (pThat.getClass() != this.getClass())
            return false;

        /* Access the target Key */
        SymmetricKey myThat = (SymmetricKey) pThat;

        /* Not equal if different key-types */
        if (myThat.theKeyType != theKeyType)
            return false;

        /* Ensure that the secret key is identical */
        return Arrays.equals(myThat.theEncodedKeyDef, theEncodedKeyDef);
    }

    /**
     * Initialise data cipher for encryption/decryption
     * @return the Data Cipher
     * @throws ModelException
     */
    public DataCipher initDataCipher() throws ModelException {
        Cipher myCipher;

        /* Protect against exceptions */
        try {
            /* Create a new cipher */
            myCipher = theGenerator.accessCipher(theKeyType.getCipher());

            /* Return the Data Cipher */
            return new DataCipher(myCipher, this);
        }

        /* catch exceptions */
        catch (Exception e) {
            throw new ModelException(ExceptionClass.CRYPTO, "Failed to initialise cipher", e);
        }
    }

    /**
     * Initialise stream cipher for encryption with random initialisation vector
     * @return the Stream Cipher
     * @throws ModelException
     */
    public StreamCipher initEncryptionStream() throws ModelException {
        Cipher myCipher;

        /* Protect against exceptions */
        try {
            /* Create a new cipher */
            myCipher = theGenerator.accessCipher(theKeyType.getCipher());

            /* Initialise the cipher generating a random Initialisation vector */
            myCipher.init(Cipher.ENCRYPT_MODE, theKey, theGenerator.getRandom());

            /* Return the Stream Cipher */
            return new StreamCipher(myCipher, myCipher.getIV());
        }

        /* catch exceptions */
        catch (Exception e) {
            throw new ModelException(ExceptionClass.CRYPTO, "Failed to initialise cipher", e);
        }
    }

    /**
     * Initialise Stream cipher for decryption with initialisation vector
     * @param pInitVector Initialisation vector for cipher
     * @return the Stream Cipher
     * @throws ModelException
     */
    public StreamCipher initDecryptionStream(byte[] pInitVector) throws ModelException {
        AlgorithmParameterSpec myParms;
        Cipher myCipher;

        /* Protect against exceptions */
        try {
            /* Create a new cipher */
            myCipher = theGenerator.accessCipher(theKeyType.getCipher());

            /* Initialise the cipher using the password */
            myParms = new IvParameterSpec(pInitVector);
            myCipher.init(Cipher.DECRYPT_MODE, theKey, myParms);

            /* Return the Stream Cipher */
            return new StreamCipher(myCipher, pInitVector);
        }

        /* catch exceptions */
        catch (Exception e) {
            throw new ModelException(ExceptionClass.CRYPTO, "Failed to initialise cipher", e);
        }
    }
}
