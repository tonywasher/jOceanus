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

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import net.sourceforge.joceanus.jdatamanager.DataConverter;
import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jgordianknot.DataHayStack.SymKeyNeedle;
import net.sourceforge.joceanus.jgordianknot.SecurityRegister.SymmetricRegister;

/**
 * Set of DataCiphers used for encryption.
 */
public class CipherSet {
    /**
     * Initialisation Vector size.
     */
    public static final int IVSIZE = 32;

    /**
     * Maximum number of encryption steps.
     */
    public static final int MAXSTEPS = SymKeyType.values().length - 1;

    /**
     * Key Id byte allowance.
     */
    public static final int KEYIDLEN = numKeyBytes(MAXSTEPS);

    /**
     * The Number of Steps.
     */
    private final int theNumSteps;

    /**
     * The Salt bytes.
     */
    private final byte[] theSaltBytes;

    /**
     * The Block Size.
     */
    private int theBlockSize = 0;

    /**
     * The DataKey Map.
     */
    private final Map<SymKeyType, DataCipher> theMap;

    /**
     * The security generator.
     */
    private final SecurityGenerator theGenerator;

    /**
     * Encryption length.
     * @param pDataLength the length of data to be encrypted
     * @return the length of encrypted data
     */
    public static int getEncryptionLength(final int pDataLength) {
        int iBlocks = 1 + ((pDataLength - 1) % IVSIZE);
        return iBlocks
               * IVSIZE;
    }

    /**
     * Constructor.
     * @param pGenerator the security generator
     * @param pSaltBytes the salt bytes
     */
    public CipherSet(final SecurityGenerator pGenerator,
                     final byte[] pSaltBytes) {
        /* Store parameters */
        theGenerator = pGenerator;
        theSaltBytes = Arrays.copyOf(pSaltBytes, pSaltBytes.length);

        /* Determine the number of cipher steps */
        theNumSteps = pGenerator.getNumCipherSteps();

        /* Build the Map */
        theMap = new EnumMap<SymKeyType, DataCipher>(SymKeyType.class);
    }

    /**
     * Constructor.
     * @param pGenerator the security generator
     * @param pHashKey the Hash Key
     */
    public CipherSet(final SecurityGenerator pGenerator,
                     final HashKey pHashKey) {
        /* Store parameters */
        theGenerator = pGenerator;
        byte[] myIV = pHashKey.getInitVector();
        theSaltBytes = Arrays.copyOf(myIV, myIV.length);

        /* Determine the number of cipher steps */
        theNumSteps = pGenerator.getNumCipherSteps();

        /* Build the Map */
        theMap = new EnumMap<SymKeyType, DataCipher>(SymKeyType.class);
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

        /* Make sure that the object is a CipherSet */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target Set */
        CipherSet myThat = (CipherSet) pThat;

        /* Check mode and steps */
        if (theNumSteps != myThat.theNumSteps) {
            return false;
        }

        /* Check the map */
        return theMap.equals(myThat.theMap);
    }

    @Override
    public int hashCode() {
        int myHash = SecurityGenerator.HASH_PRIME
                     * theNumSteps;
        myHash += theMap.hashCode();
        return myHash;
    }

    /**
     * Add a Cipher.
     * @param pCipher the Cipher
     */
    public void addCipher(final DataCipher pCipher) {
        /* Store into map */
        theMap.put(pCipher.getSymKeyType(), pCipher);
        adjustBlockLength(pCipher);
    }

    /**
     * Adjust IV length.
     * @param pCipher the Cipher to adjust with
     */
    private void adjustBlockLength(final DataCipher pCipher) {
        /* Obtain block size */
        int mySize = pCipher.getBlockSize();
        if (mySize > theBlockSize) {
            theBlockSize = mySize;
        }
    }

    /**
     * Build Secret Ciphers.
     * @param pSecret the Secret bytes
     * @throws JDataException on error
     */
    public void buildCiphers(final byte[] pSecret) throws JDataException {
        /* Loop through the Cipher values */
        for (SymKeyType myType : SymKeyType.values()) {
            /* Build the Cipher */
            buildCipher(myType, pSecret);
        }
    }

    /**
     * Build Secret Cipher for a Key Type.
     * @param pKeyType the Key type
     * @param pSecret the derived Secret
     * @throws JDataException on error
     */
    private void buildCipher(final SymKeyType pKeyType,
                             final byte[] pSecret) throws JDataException {
        /* Generate a new Secret Key from the secret */
        SecurityRegister myRegister = theGenerator.getRegister();
        SymmetricRegister myReg = myRegister.getSymRegistration(pKeyType, theGenerator.getKeyLen());
        SecretKey myKeyDef = myReg.buildSecretKey(pSecret, theSaltBytes);

        /* Create the Symmetric Key */
        SymmetricKey myKey = new SymmetricKey(theGenerator, pKeyType, myKeyDef);

        /* Obtain a Cipher */
        DataCipher myCipher = myKey.getDataCipher();

        /* Store into map */
        theMap.put(pKeyType, myCipher);

        /* adjust the block length */
        adjustBlockLength(myCipher);
    }

    /**
     * Encrypt item.
     * @param pBytes the bytes to encrypt
     * @return the encrypted bytes
     * @throws JDataException on error
     */
    public byte[] encryptBytes(final byte[] pBytes) throws JDataException {
        /* Access the current set of bytes */
        byte[] myCurBytes = pBytes;

        /* Determine the encryption mode */
        CipherSetKey myKey = new CipherSetKey(theGenerator);
        SymKeyType[] myKeyTypes = myKey.getSymKeyTypes();
        byte[] myVector = myKey.getInitVector();

        /* Loop through the SymKeyTypes */
        for (int i = 0; i < myKeyTypes.length; i++) {
            /* Access the Key Type */
            SymKeyType myType = myKeyTypes[i];

            /* Access the DataCipher */
            DataCipher myCipher = theMap.get(myType);

            /* Encrypt the bytes */
            myCurBytes = myCipher.encryptBytes(myCurBytes, myVector);
        }

        /* Return the encrypted bytes */
        return myKey.buildExternal(myCurBytes);
    }

    /**
     * Determine length of bytes to encode the number of keys.
     * @param pNumKeys the number of keys
     * @return the number of key bytes
     */
    private static int numKeyBytes(final int pNumKeys) {
        /* Determine the number of bytes */
        return 1 + (pNumKeys / 2);
    }

    /**
     * Decrypt item.
     * @param pBytes the bytes to decrypt
     * @return the decrypted bytes
     * @throws JDataException on error
     */
    public byte[] decryptBytes(final byte[] pBytes) throws JDataException {
        /* Parse the bytes into the separate parts */
        CipherSetKey myKey = new CipherSetKey(pBytes);
        SymKeyType[] myTypes = myKey.getSymKeyTypes();
        byte[] myVector = myKey.getInitVector();
        byte[] myBytes = myKey.getBytes();

        /* Loop through the SymKeyTypes */
        for (int i = myTypes.length - 1; i >= 0; i--) {
            /* Access the Key Type */
            SymKeyType myType = myTypes[i];

            /* Access the DataCipher */
            DataCipher myCipher = theMap.get(myType);

            /* Decrypt the bytes */
            myBytes = myCipher.decryptBytes(myBytes, myVector);
        }

        /* Return the decrypted bytes */
        return myBytes;
    }

    /**
     * Wrap bytes.
     * @param pBytes the bytes to wrap
     * @return the wrapped bytes
     * @throws JDataException on error
     */
    public byte[] wrapBytes(final byte[] pBytes) throws JDataException {
        /* Access the current set of bytes */
        byte[] myCurBytes = pBytes;

        /* Determine the encryption mode */
        CipherSetKey myKey = new CipherSetKey(theGenerator);
        SymKeyType[] myKeyTypes = myKey.getSymKeyTypes();
        byte[] myVector = myKey.getInitVector();

        /* Loop through the SymKeyTypes */
        for (int i = 0; i < myKeyTypes.length; i++) {
            /* Access the Key Type */
            SymKeyType myType = myKeyTypes[i];

            /* Access the DataCipher */
            DataCipher myCipher = theMap.get(myType);

            /* Wrap the bytes */
            myCurBytes = myCipher.wrapBytes(myCurBytes, myVector);
        }

        /* Return the wrapped bytes */
        return myKey.buildExternal(myCurBytes);
    }

    /**
     * Unwrap Bytes.
     * @param pBytes the bytes to unwrap
     * @return the unwrapped bytes
     * @throws JDataException on error
     */
    public byte[] unwrapBytes(final byte[] pBytes) throws JDataException {
        /* Parse the bytes into the separate parts */
        CipherSetKey myKey = new CipherSetKey(pBytes);
        SymKeyType[] myTypes = myKey.getSymKeyTypes();
        byte[] myVector = myKey.getInitVector();
        byte[] myBytes = myKey.getBytes();

        /* Loop through the SymKeyTypes */
        for (int i = myTypes.length - 1; i >= 0; i--) {
            /* Access the Key Type */
            SymKeyType myType = myTypes[i];

            /* Access the DataCipher */
            DataCipher myCipher = theMap.get(myType);

            /* unwrap the bytes */
            myBytes = myCipher.unwrapBytes(myBytes, myVector);
        }

        /* Return the decrypted bytes */
        return myBytes;
    }

    /**
     * Encrypt string.
     * @param pString the string to encrypt
     * @return the encrypted bytes
     * @throws JDataException on error
     */
    public byte[] encryptString(final String pString) throws JDataException {
        /* Access the bytes */
        byte[] myBytes = DataConverter.stringToByteArray(pString);

        /* Encrypt the bytes */
        return encryptBytes(myBytes);
    }

    /**
     * Decrypt string.
     * @param pBytes the string to decrypt
     * @return the decrypted string
     * @throws JDataException on error
     */
    public String decryptString(final byte[] pBytes) throws JDataException {
        /* Decrypt the bytes */
        byte[] myBytes = decryptBytes(pBytes);

        /* ReBuild the string */
        return DataConverter.byteArrayToString(myBytes);
    }

    /**
     * Encrypt character array.
     * @param pChars Characters to encrypt
     * @return Encrypted bytes
     * @throws JDataException on error
     */
    public byte[] encryptChars(final char[] pChars) throws JDataException {
        /* Convert the characters to a byte array */
        byte[] myRawBytes = DataConverter.charsToByteArray(pChars);

        /* Encrypt the characters */
        return encryptBytes(myRawBytes);
    }

    /**
     * Decrypt bytes into a character array.
     * @param pBytes Bytes to decrypt
     * @return Decrypted character array
     * @throws JDataException on error
     */
    public char[] decryptChars(final byte[] pBytes) throws JDataException {
        /* Decrypt the bytes */
        byte[] myBytes = decryptBytes(pBytes);

        /* Convert the bytes to characters */
        char[] myChars = DataConverter.bytesToCharArray(myBytes);

        /* Clear out the bytes */
        Arrays.fill(myBytes, (byte) 0);

        /* Return to caller */
        return myChars;
    }

    /**
     * secure SymmetricKey.
     * @param pKey the key to wrap
     * @return the wrapped symmetric key
     * @throws JDataException on error
     */
    public byte[] secureSymmetricKey(final SymmetricKey pKey) throws JDataException {
        /* Extract the encoded version of the key */
        byte[] myEncoded = pKey.getSecretKey().getEncoded();

        /* Encode the key */
        byte[] myEncrypted = encryptBytes(myEncoded);

        /* Determine the external definition */
        SymKeyNeedle myNeedle = new SymKeyNeedle(pKey.getKeyType(), myEncrypted);
        return myNeedle.getExternal();
    }

    /**
     * derive SymmetricKey.
     * @param pKeySpec the wrapped symmetric key
     * @return the symmetric key
     * @throws JDataException on error
     */
    public SymmetricKey deriveSymmetricKey(final byte[] pKeySpec) throws JDataException {
        /* Parse the KeySpec */
        SymKeyNeedle myNeedle = new SymKeyNeedle(pKeySpec);

        /* Decrypt the encoded bytes */
        byte[] myEncoded = decryptBytes(myNeedle.getEncodedKey());
        SymKeyType myType = myNeedle.getSymKeyType();

        /* Create the Secret Key */
        SecretKey mySecret = new SecretKeySpec(myEncoded, myType.getAlgorithm());

        /* Create the Symmetric Key */
        SymmetricKey myKey = new SymmetricKey(theGenerator, myType, mySecret);

        /* Return the key */
        return myKey;
    }

    /**
     * secure AsymmetricKey (privateKey).
     * @param pKey the key to wrap
     * @return the wrapped Asymmetric key
     * @throws JDataException on error
     */
    public byte[] securePrivateKey(final AsymmetricKey pKey) throws JDataException {
        /* Access the Private Key */
        byte[] myPrivate = pKey.getExternalPrivate();

        /* Reject if there is no PrivateKey */
        if (myPrivate == null) {
            throw new JDataException(ExceptionClass.DATA, "No PrivateKey");
        }

        /* Encode the key */
        byte[] myEncrypted = encryptBytes(myPrivate);

        /* Check whether the SecuredKey is too large */
        if (myEncrypted.length > AsymmetricKey.PRIVATESIZE) {
            throw new JDataException(ExceptionClass.DATA, "PrivateKey too large: "
                                                          + myEncrypted.length);
        }

        /* Return the wrapped key */
        return myEncrypted;
    }

    /**
     * derive AsymmetricKey.
     * @param pEncrypted the wrapped private key
     * @param pPublicKey the public key
     * @return the Asymmetric key
     * @throws JDataException on error
     */
    public AsymmetricKey deriveAsymmetricKey(final byte[] pEncrypted,
                                             final byte[] pPublicKey) throws JDataException {
        /* Create the Asymmetric Key */
        byte[] myEncoded = decryptBytes(pEncrypted);
        return new AsymmetricKey(theGenerator, myEncoded, pPublicKey);
    }

    /**
     * derive AsymmetricKey.
     * @param pPublicKey the public key
     * @return the Asymmetric key
     * @throws JDataException on error
     */
    public AsymmetricKey deriveAsymmetricKey(final byte[] pPublicKey) throws JDataException {
        /* Create the Asymmetric Key */
        return new AsymmetricKey(theGenerator, pPublicKey);
    }
}
