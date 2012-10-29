/*******************************************************************************
 * jGordianKnot: Security Suite
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
package net.sourceforge.jOceanus.jGordianKnot;

import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import net.sourceforge.jOceanus.jDataManager.DataConverter;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jGordianKnot.DataHayStack.EncryptModeNeedle;
import net.sourceforge.jOceanus.jGordianKnot.DataHayStack.SymKeyNeedle;

/**
 * Set of DataCiphers used for encryption.
 * @author Tony Washer
 */
public class CipherSet implements JDataContents {
    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(CipherSet.class.getSimpleName());

    /**
     * Field IDs for number of steps.
     */
    public static final JDataField FIELD_STEPS = FIELD_DEFS.declareLocalField("NumSteps");

    /**
     * Field IDs for data key map.
     */
    public static final JDataField FIELD_MAP = FIELD_DEFS.declareLocalField("DataKeyMap");

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_STEPS.equals(pField)) {
            return theNumSteps;
        }
        if (FIELD_MAP.equals(pField)) {
            return theMap;
        }
        return JDataFieldValue.UnknownField;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    /**
     * Maximum number of encryption steps.
     */
    public static final int MAXSTEPS = SymKeyType.values().length - 1;

    /**
     * Key Id byte allowance.
     */
    public static final int KEYIDLEN = numKeyBytes(MAXSTEPS);

    /**
     * Multiplier to obtain IV from vector.
     */
    private static final int VECTOR_SHIFT = 7;

    /**
     * The Number of Steps.
     */
    private final int theNumSteps;

    /**
     * Use restricted keys.
     */
    private final boolean useRestricted;

    /**
     * Cipher digest.
     */
    private final DigestType theDigest;

    /**
     * The Random Generator.
     */
    private final SecureRandom theRandom;

    /**
     * The DataKey Map.
     */
    private final Map<SymKeyType, DataCipher> theMap;

    /**
     * The security generator.
     */
    private final SecurityGenerator theGenerator;

    /**
     * Constructor.
     * @param pGenerator the security generator
     * @param pKeyMode the Asymmetric Key Mode
     */
    public CipherSet(final SecurityGenerator pGenerator,
                     final AsymKeyMode pKeyMode) {
        /* Store parameters */
        theGenerator = pGenerator;
        theRandom = theGenerator.getRandom();
        useRestricted = pKeyMode.useRestricted();
        theDigest = pKeyMode.getCipherDigest();

        /* Determine the number of cipher steps */
        theNumSteps = pGenerator.getNumCipherSteps();

        /* Build the Map */
        theMap = new EnumMap<SymKeyType, DataCipher>(SymKeyType.class);
    }

    /**
     * Constructor.
     * @param pGenerator the security generator
     * @param pHashMode the Hash Mode
     */
    public CipherSet(final SecurityGenerator pGenerator,
                     final HashMode pHashMode) {
        /* Store parameters */
        theGenerator = pGenerator;
        theRandom = theGenerator.getRandom();
        useRestricted = pHashMode.useRestricted();
        theDigest = pHashMode.getCipherDigest();

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
        int myHash = SecurityGenerator.HASH_PRIME * theNumSteps;
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
     * @param pSecret the Secret Key
     * @throws JDataException on error
     */
    private void buildCipher(final SymKeyType pKeyType,
                             final byte[] pSecret) throws JDataException {
        /* Determine the key length in bytes */
        int myKeyLen = SymmetricKey.getKeyLen(useRestricted) / Byte.SIZE;

        /* Create a buffer to hold the resulting key and # of bytes built */
        byte[] myKeyBytes = new byte[myKeyLen];
        int myBuilt = 0;

        /* Create the Mac and standard data */
        IterationCounter myCount = new IterationCounter();
        Mac myMac = theGenerator.accessMac(theDigest, pSecret);

        /* while we need to generate more bytes */
        while (myBuilt < myKeyLen) {
            /* Build the cipher section */
            byte[] mySection = buildCipherSection(myMac, myCount.iterate(), pKeyType);

            /* Determine how many bytes of this hash should be used */
            int myNeeded = myKeyLen - myBuilt;
            if (myNeeded > mySection.length) {
                myNeeded = mySection.length;
            }

            /* Copy bytes across */
            System.arraycopy(mySection, 0, myKeyBytes, myBuilt, myNeeded);
            myBuilt += myNeeded;
        }

        /* Build the secret key specification */
        SecretKey myKeyDef = new SecretKeySpec(myKeyBytes, pKeyType.getAlgorithm());

        /* Create the Symmetric Key */
        SymmetricKey myKey = new SymmetricKey(theGenerator, myKeyDef, pKeyType);

        /* Access a Cipher */
        DataCipher myCipher = myKey.initDataCipher();

        /* Store into map */
        theMap.put(pKeyType, myCipher);
    }

    /**
     * Build Secret Key section.
     * @param pMac the Mac to utilise
     * @param pSection the section count
     * @param pKeyType the Key type
     * @return the section
     * @throws JDataException on error
     */
    private byte[] buildCipherSection(final Mac pMac,
                                      final byte[] pSection,
                                      final SymKeyType pKeyType) throws JDataException {
        /* Declare initial value */
        byte[] myResult = null;

        /* Access number of iterations */
        int iIterations = theGenerator.getNumHashIterations() >>> 1;

        /* Create the standard data */
        IterationCounter myCount = new IterationCounter();
        byte[] myAlgo = DataConverter.stringToByteArray(pKeyType.getAlgorithm());
        byte[] mySeed = theGenerator.getSecurityBytes();

        /* Loop through the iterations */
        for (int i = 0; i < iIterations; i++) {
            /* Add section number to hash */
            pMac.update(pSection);

            /* Update with algorithm */
            pMac.update(myAlgo);

            /* Increment count and add to hash */
            pMac.update(myCount.iterate());

            /* Update with security bytes */
            pMac.update(mySeed);

            /* Calculate Mac */
            myResult = DataConverter.combineHashes(pMac.doFinal(), myResult);
        }

        /* Return the result */
        return myResult;
    }

    /**
     * Encrypt item.
     * @param pBytes the bytes to encrypt
     * @return the encrypted bytes
     * @throws JDataException on error
     */
    public byte[] encryptBytes(final byte[] pBytes) throws JDataException {
        /* Allocate a new initialisation vector */
        byte[] myVector = new byte[SymmetricKey.IVSIZE];
        theRandom.nextBytes(myVector);

        /* Access the current set of bytes */
        byte[] myCurBytes = pBytes;

        /* Determine the encryption mode */
        EncryptionMode myMode = new EncryptionMode(theNumSteps, theRandom);
        SymKeyType[] myKeyTypes = myMode.getSymKeyTypes();

        /* Loop through the SymKeyTypes */
        for (int i = 0; i < myKeyTypes.length; i++) {
            /* Access the Key Type */
            SymKeyType myType = myKeyTypes[i];

            /* Access the DataCipher */
            DataCipher myCipher = theMap.get(myType);

            /* Access the shifted vector */
            byte[] myShift = getShiftedVector(myType, myVector);

            /* Encrypt the bytes */
            myCurBytes = myCipher.encryptBytes(myCurBytes, myShift);
        }

        /* hide the encryptionMode */
        EncryptModeNeedle myNeedle = new EncryptModeNeedle(myMode, myVector, myCurBytes);

        /* Return the encrypted bytes */
        return myNeedle.getExternal();
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
        EncryptModeNeedle myNeedle = new EncryptModeNeedle(pBytes);
        byte[] myVector = myNeedle.getInitVector();
        byte[] myBytes = myNeedle.getEncryptedBytes();
        EncryptionMode myMode = myNeedle.getEncryptionMode();
        SymKeyType[] myTypes = myMode.getSymKeyTypes();

        /* Loop through the SymKeyTypes */
        for (int i = myTypes.length - 1; i >= 0; i--) {
            /* Access the Key Type */
            SymKeyType myType = myTypes[i];

            /* Access the DataCipher */
            DataCipher myCipher = theMap.get(myType);

            /* Access the shifted vector */
            byte[] myShift = getShiftedVector(myType, myVector);

            /* Decrypt the bytes */
            myBytes = myCipher.decryptBytes(myBytes, myShift);
        }

        /* Return the decrypted bytes */
        return myBytes;
    }

    /**
     * Obtain shifted Initialisation vector.
     * @param pKeyType the Symmetric Key Type
     * @param pVector the initialisation vector
     * @return the shifted vector
     */
    private static byte[] getShiftedVector(final SymKeyType pKeyType,
                                           final byte[] pVector) {
        /* Determine index into array for Key Type */
        byte[] myNew = new byte[pVector.length];
        int myIndex = VECTOR_SHIFT * pKeyType.getId();
        myIndex %= pVector.length;

        /* Access current vector */
        byte[] myVector = pVector;

        /* If we need to shift the array */
        if (myIndex > 0) {
            /* Access shifted array */
            System.arraycopy(myVector, myIndex, myNew, 0, myVector.length - myIndex);
            System.arraycopy(myVector, 0, myNew, myVector.length - myIndex, myIndex);
            myVector = myNew;
        }

        /* return the shifted vector */
        return myVector;
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
        byte[] myBytes;
        byte[] myRawBytes;

        /* Convert the characters to a byte array */
        myRawBytes = DataConverter.charsToByteArray(pChars);

        /* Encrypt the characters */
        myBytes = encryptBytes(myRawBytes);

        /* Return to caller */
        return myBytes;
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

        /* Decrypt the bytes */
        myBytes = decryptBytes(pBytes);

        /* Convert the bytes to characters */
        myChars = DataConverter.bytesToCharArray(myBytes);

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
        SymmetricKey myKey = new SymmetricKey(theGenerator, mySecret, myType);

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
        PrivateKey myPrivate = pKey.getPrivateKey();

        /* Return null if there is no PrivateKey */
        if (myPrivate == null) {
            throw new JDataException(ExceptionClass.DATA, "No PrivateKey");
        }

        /* Extract the encoded version of the key */
        byte[] myEncoded = myPrivate.getEncoded();

        /* Encode the key */
        byte[] myEncrypted = encryptBytes(myEncoded);

        /* Check whether the SecuredKey is too large */
        if (myEncrypted.length > AsymmetricKey.PRIVATESIZE) {
            throw new JDataException(ExceptionClass.DATA, "PrivateKey too large: " + myEncrypted.length);
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
        /* Decrypt the encoded bytes */
        byte[] myEncoded = (pEncrypted == null) ? null : decryptBytes(pEncrypted);

        /* Create the Asymmetric Key */
        AsymmetricKey myKey = new AsymmetricKey(theGenerator, myEncoded, pPublicKey);

        /* Return the key */
        return myKey;
    }
}
