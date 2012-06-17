/*******************************************************************************
 * JGordianKnot: Security Suite
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

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject.JDataContents;
import net.sourceforge.JDataManager.JDataObject.JDataFieldValue;

/**
 * Symmetric Key implementation.
 * @author Tony Washer
 */
public class SymmetricKey implements JDataContents {
    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(SymmetricKey.class.getSimpleName());

    /**
     * KeyType Field ID.
     */
    public static final JDataField FIELD_KEYTYPE = FIELD_DEFS.declareLocalField("KeyType");

    /**
     * KeyLength Field ID.
     */
    public static final JDataField FIELD_KEYLEN = FIELD_DEFS.declareLocalField("KeyLength");

    /**
     * InitVector Length Field ID.
     */
    public static final JDataField FIELD_IVLEN = FIELD_DEFS.declareLocalField("IVLength");

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_KEYTYPE.equals(pField)) {
            return theKeyType;
        }
        if (FIELD_KEYLEN.equals(pField)) {
            return theKeyLen;
        }
        if (FIELD_IVLEN.equals(pField)) {
            return IVSIZE;
        }
        return JDataFieldValue.UnknownField;
    }

    @Override
    public String formatObject() {
        return "SymmetricKey(" + theKeyType + ")";
    }

    /**
     * Encrypted ID Key Size.
     */
    public static final int IDSIZE = 128;

    /**
     * Initialisation Vector size.
     */
    public static final int IVSIZE = 16;

    /**
     * Restricted key length.
     */
    private static final int SMALL_KEYLEN = 128;

    /**
     * Unlimited key length.
     */
    private static final int BIG_KEYLEN = 256;

    /**
     * The Secret Key.
     */
    private final SecretKey theKey;

    /**
     * The Key Type.
     */
    private final SymKeyType theKeyType;

    /**
     * The security generator.
     */
    private final SecurityGenerator theGenerator;

    /**
     * The Key Length.
     */
    private final int theKeyLen;

    /**
     * The Encoded KeyDef.
     */
    private final byte[] theEncodedKeyDef;

    /**
     * Obtain the secret key.
     * @return the secret key
     */
    protected SecretKey getSecretKey() {
        return theKey;
    }

    /**
     * Obtain the secret key type.
     * @return the secret key type
     */
    public SymKeyType getKeyType() {
        return theKeyType;
    }

    /**
     * Obtain the key length.
     * @return the secret key length
     */
    public int getKeyLength() {
        return theKeyLen;
    }

    /**
     * Determine key length.
     * @param useRestricted restricted mode?
     * @return key length
     */
    protected static int getKeyLen(final boolean useRestricted) {
        return useRestricted ? SMALL_KEYLEN : BIG_KEYLEN;
    }

    /**
     * Encryption length.
     * @param pDataLength the length of data to be encrypted
     * @return the length of encrypted data
     */
    public static int getEncryptionLength(final int pDataLength) {
        int iBlocks = 1 + ((pDataLength - 1) % IVSIZE);
        return iBlocks * IVSIZE;
    }

    /**
     * Constructor for a new randomly generated key.
     * @param pGenerator the security generator
     * @param pKeyType Symmetric KeyType
     * @param useRestricted use restricted keys
     * @throws JDataException on error
     */
    public SymmetricKey(final SecurityGenerator pGenerator,
                        final SymKeyType pKeyType,
                        final boolean useRestricted) throws JDataException {
        /* Store the KeyType and the Generator */
        theKeyType = pKeyType;
        theKeyLen = getKeyLen(useRestricted);
        theGenerator = pGenerator;

        /* Generate the new key */
        theKey = theGenerator.generateSecretKey(theKeyType, theKeyLen);
        theEncodedKeyDef = theKey.getEncoded();
    }

    /**
     * Constructor for a decoded symmetric key.
     * @param pGenerator the security generator
     * @param pKey Secret Key for algorithm
     * @param pKeyType Symmetric KeyType
     * @throws JDataException on error
     */
    protected SymmetricKey(final SecurityGenerator pGenerator,
                           final SecretKey pKey,
                           final SymKeyType pKeyType) throws JDataException {
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
        int hashCode = SecurityGenerator.HASH_PRIME * theEncodedKeyDef.hashCode();
        hashCode += theKeyType.hashCode();
        return hashCode;
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

        /* Make sure that the object is a Symmetric Key */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target Key */
        SymmetricKey myThat = (SymmetricKey) pThat;

        /* Not equal if different key-types */
        if (myThat.theKeyType != theKeyType) {
            return false;
        }

        /* Ensure that the secret key is identical */
        return Arrays.equals(myThat.theEncodedKeyDef, theEncodedKeyDef);
    }

    /**
     * Initialise data cipher for encryption/decryption.
     * @return the Data Cipher
     * @throws JDataException on error
     */
    public DataCipher initDataCipher() throws JDataException {
        /* Protect against exceptions */
        try {
            /* Create a new cipher */
            Cipher myCipher = theGenerator.accessCipher(theKeyType.getCipher());

            /* Return the Data Cipher */
            return new DataCipher(myCipher, this);

            /* catch exceptions */
        } catch (Exception e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to initialise cipher", e);
        }
    }

    /**
     * Initialise stream cipher for encryption with random initialisation vector.
     * @return the Stream Cipher
     * @throws JDataException on error
     */
    public StreamCipher initEncryptionStream() throws JDataException {
        /* Protect against exceptions */
        try {
            /* Create a new cipher */
            Cipher myCipher = theGenerator.accessCipher(theKeyType.getCipher());

            /* Initialise the cipher generating a random Initialisation vector */
            myCipher.init(Cipher.ENCRYPT_MODE, theKey, theGenerator.getRandom());

            /* Return the Stream Cipher */
            return new StreamCipher(myCipher, myCipher.getIV());

            /* catch exceptions */
        } catch (Exception e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to initialise cipher", e);
        }
    }

    /**
     * Initialise Stream cipher for decryption with initialisation vector.
     * @param pInitVector Initialisation vector for cipher
     * @return the Stream Cipher
     * @throws JDataException on error
     */
    public StreamCipher initDecryptionStream(final byte[] pInitVector) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Create a new cipher */
            Cipher myCipher = theGenerator.accessCipher(theKeyType.getCipher());

            /* Initialise the cipher using the password */
            AlgorithmParameterSpec myParms = new IvParameterSpec(pInitVector);
            myCipher.init(Cipher.DECRYPT_MODE, theKey, myParms);

            /* Return the Stream Cipher */
            return new StreamCipher(myCipher, pInitVector);

            /* catch exceptions */
        } catch (Exception e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to initialise cipher", e);
        }
    }
}
