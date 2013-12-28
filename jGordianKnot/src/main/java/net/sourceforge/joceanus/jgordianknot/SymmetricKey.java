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
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;

/**
 * Symmetric Key implementation.
 */
public class SymmetricKey {
    /**
     * Cipher initialisation failure.
     */
    private static final String ERROR_CIPHER = "Failed to initialise Cipher";

    /**
     * Encrypted ID Key Size.
     */
    public static final int IDSIZE = 128;

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
     * Obtain the generator.
     * @return the generator
     */
    protected SecurityGenerator getGenerator() {
        return theGenerator;
    }

    /**
     * Obtain the random generator.
     * @return the random generator
     */
    public SecureRandom getRandom() {
        return theGenerator.getRandom();
    }

    /**
     * Obtain the secret key.
     * @return the secret key
     */
    protected SecretKey getSecretKey() {
        return theKey;
    }

    /**
     * Obtain the symmetric key type.
     * @return the symmetric key type
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
     * Constructor for a symmetric key of specified type.
     * @param pGenerator the security generator
     * @param pKeyType Symmetric KeyType
     * @throws JDataException on error
     */
    protected SymmetricKey(final SecurityGenerator pGenerator,
                           final SymKeyType pKeyType) throws JDataException {
        /* Store the KeyType and the Generator */
        theKeyType = pKeyType;
        theKeyLen = pGenerator.getKeyLen();
        theGenerator = pGenerator;

        /* Generate the new key */
        theKey = theGenerator.generateSecretKey(theKeyType, theKeyLen);
        theEncodedKeyDef = theKey.getEncoded();
    }

    /**
     * Constructor for a symmetric key of random type.
     * @param pGenerator the security generator
     * @throws JDataException on error
     */
    protected SymmetricKey(final SecurityGenerator pGenerator) throws JDataException {
        /* Create StreamKey for random key type */
        this(pGenerator, SymKeyType.getRandomTypes(1, pGenerator.getRandom())[0]);
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
        int hashCode = SecurityGenerator.HASH_PRIME
                       * Arrays.hashCode(theEncodedKeyDef);
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
     * Obtain data cipher for encryption/decryption.
     * @return the Data Cipher
     * @throws JDataException on error
     */
    public DataCipher getDataCipher() throws JDataException {
        /* Create the Data Cipher */
        return new DataCipher(this);
    }

    /**
     * Obtain stream cipher for encryption/decryption.
     * @return the Stream Cipher
     * @throws JDataException on error
     */
    public StreamCipher getStreamCipher() throws JDataException {
        /* Create the Stream Cipher */
        return new StreamCipher(this);
    }

    /**
     * Obtain cipher.
     * @return the Stream Cipher
     * @throws JDataException on error
     */
    private Cipher getCipher() throws JDataException {
        /* Protect against exceptions */
        try {
            /* Create a new cipher */
            return Cipher.getInstance(theKeyType.getCipher(), theGenerator.getProvider().getProvider());

            /* catch exceptions */
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
            throw new JDataException(ExceptionClass.CRYPTO, ERROR_CIPHER, e);
        }
    }

    /**
     * Initialise stream cipher for encryption with random initialisation vector.
     * @return the Stream Cipher
     * @throws JDataException on error
     */
    public Cipher initEncryptionStream() throws JDataException {
        /* Create a new cipher */
        Cipher myCipher = getCipher();

        /* Protect against exceptions */
        try {
            /* Initialise the cipher generating a random Initialisation vector */
            myCipher.init(Cipher.ENCRYPT_MODE, theKey, theGenerator.getRandom());

            /* catch exceptions */
        } catch (InvalidKeyException e) {
            throw new JDataException(ExceptionClass.CRYPTO, ERROR_CIPHER, e);
        }

        /* Return the Stream Cipher */
        return myCipher;
    }

    /**
     * Initialise Stream cipher for decryption with initialisation vector.
     * @param pInitVector Initialisation vector for cipher
     * @return the Stream Cipher
     * @throws JDataException on error
     */
    public Cipher initDecryptionStream(final byte[] pInitVector) throws JDataException {
        /* Create a new cipher */
        Cipher myCipher = getCipher();

        /* Protect against exceptions */
        try {
            /* Initialise the cipher using the password */
            AlgorithmParameterSpec myParms = new IvParameterSpec(pInitVector);
            myCipher.init(Cipher.DECRYPT_MODE, theKey, myParms);

            /* catch exceptions */
        } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new JDataException(ExceptionClass.CRYPTO, ERROR_CIPHER, e);
        }

        /* Return the Stream Cipher */
        return myCipher;
    }
}
