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

import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import net.sourceforge.JDataWalker.DataConverter;
import net.sourceforge.JDataWalker.ModelException;
import net.sourceforge.JDataWalker.ModelException.ExceptionClass;
import net.sourceforge.JDataWalker.ReportFields;
import net.sourceforge.JDataWalker.ReportFields.ReportField;
import net.sourceforge.JDataWalker.ReportObject.ReportDetail;
import net.sourceforge.JGordianKnot.DataHayStack.SymKeyNeedle;

public class CipherSet implements ReportDetail {
    /**
     * Report fields
     */
    protected static final ReportFields theFields = new ReportFields(CipherSet.class.getSimpleName());

    /* Field IDs */
    public static final ReportField FIELD_STEPS = theFields.declareLocalField("NumSteps");
    public static final ReportField FIELD_MAP = theFields.declareLocalField("Map");

    @Override
    public ReportFields getReportFields() {
        return theFields;
    }

    @Override
    public Object getFieldValue(ReportField pField) {
        if (pField == FIELD_STEPS)
            return theNumSteps;
        if (pField == FIELD_MAP)
            return theMap;
        return null;
    }

    @Override
    public String getObjectSummary() {
        return theFields.getName();
    }

    /**
     * Maximum number of encryption steps
     */
    public final static int MAXSTEPS = SymKeyType.values().length - 1;

    /**
     * Key Id byte allowance
     */
    public final static int KEYIDLEN = numKeyBytes(MAXSTEPS);

    /**
     * Multiplier to obtain IV from vector
     */
    private final static int vectMULT = 7;

    /**
     * The Number of Steps
     */
    private final int theNumSteps;

    /**
     * Use restricted keys
     */
    protected final boolean useRestricted;

    /**
     * Cipher digest
     */
    protected final DigestType theDigest;

    /**
     * The Random Generator
     */
    private final SecureRandom theRandom;

    /**
     * The DataKey Map
     */
    private final Map<SymKeyType, DataCipher> theMap;

    /**
     * The security generator
     */
    private final SecurityGenerator theGenerator;

    /**
     * Constructor
     * @param pGenerator the security generator
     * @param pKeyMode the Asymmetric Key Mode
     */
    public CipherSet(SecurityGenerator pGenerator,
                     AsymKeyMode pKeyMode) {
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
     * Constructor
     * @param pGenerator the security generator
     * @param pHashMode the Hash Mode
     */
    public CipherSet(SecurityGenerator pGenerator,
                     HashMode pHashMode) {
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
    public boolean equals(Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat)
            return true;
        if (pThat == null)
            return false;

        /* Make sure that the object is a CipherSet */
        if (pThat.getClass() != this.getClass())
            return false;

        /* Access the target Set */
        CipherSet myThat = (CipherSet) pThat;

        /* Check mode and steps */
        if (theNumSteps != myThat.theNumSteps)
            return false;

        /* Check the map */
        return theMap.equals(myThat.theMap);
    }

    @Override
    public int hashCode() {
        int myHash = 17 * theNumSteps;
        myHash += theMap.hashCode();
        return myHash;
    }

    /**
     * Add a Cipher
     * @param pCipher the Cipher
     */
    public void addCipher(DataCipher pCipher) {
        /* Store into map */
        theMap.put(pCipher.getSymKeyType(), pCipher);
    }

    /**
     * Build Secret Ciphers
     * @param pSecret the Secret bytes
     * @throws ModelException
     */
    public void buildCiphers(byte[] pSecret) throws ModelException {
        /* Loop through the Cipher values */
        for (SymKeyType myType : SymKeyType.values()) {
            /* Build the Cipher */
            buildCipher(myType, pSecret);
        }
    }

    /**
     * Build Secret Cipher for a Key Type
     * @param pKeyType the Key type
     * @param pSecret the Secret Key
     * @throws ModelException
     */
    private void buildCipher(SymKeyType pKeyType,
                             byte[] pSecret) throws ModelException {
        /* Determine the key length in bytes */
        int myKeyLen = SymmetricKey.getKeyLen(useRestricted) / 8;

        /* Create a buffer to hold the resulting key and # of bytes built */
        byte[] myKeyBytes = new byte[myKeyLen];
        int myBuilt = 0;

        /* Protect against exceptions */
        try {
            /* Create the MessageDigest and standard data */
            IterationCounter myCount = new IterationCounter();
            byte[] myAlgo = DataConverter.stringToByteArray(pKeyType.getAlgorithm());
            MessageDigest myDigest = theGenerator.accessDigest(theDigest);
            byte[] mySeed = theGenerator.getSecurityBytes();

            /* while we need to generate more bytes */
            while (myBuilt < myKeyLen) {
                /* Increment count and add to hash */
                myDigest.update(myCount.iterate());

                /* Update with secret and algorithm */
                myDigest.update(pSecret);
                myDigest.update(myAlgo);

                /* Update with security bytes */
                myDigest.update(mySeed);

                /* Obtain the calculated hash */
                byte[] myHash = myDigest.digest();

                /* Determine how many bytes of this hash should be used */
                int myNeeded = myKeyLen - myBuilt;
                if (myNeeded > myHash.length)
                    myNeeded = myHash.length;

                /* Copy bytes across */
                System.arraycopy(myHash, 0, myKeyBytes, myBuilt, myNeeded);
                myBuilt += myNeeded;
            }
        }

        /* Catch exceptions */
        catch (Exception e) {
            /* Throw exception */
            throw new ModelException(ExceptionClass.CRYPTO, "Failed to Derive KeyDefinition", e);
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
     * Encrypt item
     * @param pBytes the bytes to encrypt
     * @return the encrypted bytes
     * @throws ModelException
     */
    public byte[] encryptBytes(byte[] pBytes) throws ModelException {
        /* Allocate a new initialisation vector */
        byte[] myVector = new byte[SymmetricKey.IVSIZE];
        theRandom.nextBytes(myVector);

        /* Access the current set of bytes */
        byte[] myCurBytes = pBytes;

        /* Determine the SymKeyTypes to use */
        SymKeyType[] myKeyTypes = SymKeyType.getRandomTypes(theNumSteps, theRandom);

        /* Encode the array */
        byte[] myKeyBytes = encodeSymKeyTypes(myKeyTypes);

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

        /* Allocate the bytes */
        byte[] myEncrypt = new byte[SymmetricKey.IVSIZE + myKeyBytes.length + myCurBytes.length];
        System.arraycopy(myVector, 0, myEncrypt, 0, SymmetricKey.IVSIZE);
        System.arraycopy(myKeyBytes, 0, myEncrypt, SymmetricKey.IVSIZE, myKeyBytes.length);
        System.arraycopy(myCurBytes, 0, myEncrypt, SymmetricKey.IVSIZE + myKeyBytes.length, myCurBytes.length);

        /* Return the encrypted bytes */
        return myEncrypt;
    }

    /**
     * Determine length of bytes to encode the number of keys
     * @param pNumKeys the number of keys
     * @return the number of key bytes
     */
    private static int numKeyBytes(int pNumKeys) {
        /* Determine the number of bytes */
        return 1 + (pNumKeys / 2);
    }

    /**
     * Encode SymKeyTypes
     * @param pTypes the types to encode
     * @return the encoded types
     * @throws ModelException
     */
    private static byte[] encodeSymKeyTypes(SymKeyType[] pTypes) throws ModelException {
        /* Determine the number of bytes */
        int myNumBytes = numKeyBytes(pTypes.length);

        /* Allocate the bytes */
        byte[] myBytes = new byte[myNumBytes];
        Arrays.fill(myBytes, (byte) 0);

        /* Loop through the keys */
        for (int i = 0, j = 0; i < pTypes.length; i++) {
            /* Access the id of the Symmetric Key */
            int myId = pTypes[i].getId();

            /* Access the id */
            if ((i % 2) == 1) {
                myId <<= 4;
                j++;
            }
            myBytes[j] |= myId;
        }

        /* Encode the number of keys */
        myBytes[0] |= (pTypes.length << 4);

        /* Return the bytes */
        return myBytes;
    }

    /**
     * Decrypt item
     * @param pBytes the bytes to decrypt
     * @return the decrypted bytes
     * @throws ModelException
     */
    public byte[] decryptBytes(byte[] pBytes) throws ModelException {
        /* Split the bytes into the separate parts */
        byte[] myVector = Arrays.copyOf(pBytes, SymmetricKey.IVSIZE);
        SymKeyType[] myTypes = decodeSymKeyTypes(pBytes);
        byte[] myBytes = Arrays.copyOfRange(pBytes, SymmetricKey.IVSIZE + numKeyBytes(myTypes.length),
                                            pBytes.length);

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
     * Obtain shifted Initialisation vector
     * @param pKeyType the Symmetric Key Type
     * @param pVector the initialisation vector
     * @return the shifted vector
     */
    private static byte[] getShiftedVector(SymKeyType pKeyType,
                                           byte[] pVector) {
        /* Determine index into array for Key Type */
        byte[] myNew = new byte[pVector.length];
        int myIndex = vectMULT * pKeyType.getId();
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
     * Decode SymKeyTypes
     * @param pBytes the encrypted bytes
     * @return the array of SymKeyTypes
     * @throws ModelException
     */
    private static SymKeyType[] decodeSymKeyTypes(byte[] pBytes) throws ModelException {
        /* Extract the number of SymKeys */
        int myNumKeys = (pBytes[SymmetricKey.IVSIZE] >> 4) & 0xF;

        /* Allocate the array */
        SymKeyType[] myTypes = new SymKeyType[myNumKeys];

        /* Loop through the keys */
        for (int i = 0, j = SymmetricKey.IVSIZE; i < myNumKeys; i++) {
            /* Access the id of the Symmetric Key */
            int myId = pBytes[j];

            /* Isolate the id */
            if ((i % 2) == 1)
                myId >>= 4;
            else
                j++;
            myId &= 0xF;

            /* Determine the SymKeyType */
            myTypes[i] = SymKeyType.fromId(myId);
        }

        /* Return the array */
        return myTypes;
    }

    /**
     * Encrypt string
     * @param pString the string to encrypt
     * @return the encrypted bytes
     * @throws ModelException
     */
    public byte[] encryptString(String pString) throws ModelException {
        /* Access the bytes */
        byte[] myBytes = DataConverter.stringToByteArray(pString);

        /* Encrypt the bytes */
        return encryptBytes(myBytes);
    }

    /**
     * Decrypt string
     * @param pBytes the string to decrypt
     * @return the decrypted string
     * @throws ModelException
     */
    public String decryptString(byte[] pBytes) throws ModelException {
        /* Decrypt the bytes */
        byte[] myBytes = decryptBytes(pBytes);

        /* ReBuild the string */
        return DataConverter.byteArrayToString(myBytes);
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

        /* Convert the characters to a byte array */
        myRawBytes = DataConverter.charsToByteArray(pChars);

        /* Encrypt the characters */
        myBytes = encryptBytes(myRawBytes);

        /* Return to caller */
        return myBytes;
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
     * secure SymmetricKey
     * @param pKey the key to wrap
     * @return the wrapped symmetric key
     * @throws ModelException
     */
    public byte[] secureSymmetricKey(SymmetricKey pKey) throws ModelException {
        /* Extract the encoded version of the key */
        byte[] myEncoded = pKey.getSecretKey().getEncoded();

        /* Encode the key */
        byte[] myEncrypted = encryptBytes(myEncoded);

        /* Determine the external definition */
        SymKeyNeedle myNeedle = new SymKeyNeedle(pKey.getKeyType(), myEncrypted);
        return myNeedle.getExternal();
    }

    /**
     * derive SymmetricKey
     * @param pKeySpec the wrapped symmetric key
     * @return the symmetric key
     * @throws ModelException
     */
    public SymmetricKey deriveSymmetricKey(byte[] pKeySpec) throws ModelException {
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
     * secure AsymmetricKey (privateKey)
     * @param pKey the key to wrap
     * @return the wrapped Asymmetric key
     * @throws ModelException
     */
    public byte[] securePrivateKey(AsymmetricKey pKey) throws ModelException {
        /* Access the Private Key */
        PrivateKey myPrivate = pKey.getPrivateKey();

        /* Return null if there is no PrivateKey */
        if (myPrivate == null)
            return null;

        /* Extract the encoded version of the key */
        byte[] myEncoded = myPrivate.getEncoded();

        /* Encode the key */
        byte[] myEncrypted = encryptBytes(myEncoded);

        /* Check whether the SecuredKey is too large */
        if (myEncrypted.length > AsymmetricKey.PRIVATESIZE)
            throw new ModelException(ExceptionClass.DATA, "PrivateKey too large: " + myEncrypted.length);

        /* Return the wrapped key */
        return myEncrypted;
    }

    /**
     * derive AsymmetricKey
     * @param pEncrypted the wrapped private key
     * @param pPublicKey the public key
     * @return the Asymmetric key
     * @throws ModelException
     */
    public AsymmetricKey deriveAsymmetricKey(byte[] pEncrypted,
                                             byte[] pPublicKey) throws ModelException {
        /* Decrypt the encoded bytes */
        byte[] myEncoded = (pEncrypted == null) ? null : decryptBytes(pEncrypted);

        /* Create the Asymmetric Key */
        AsymmetricKey myKey = new AsymmetricKey(theGenerator, myEncoded, pPublicKey);

        /* Return the key */
        return myKey;
    }
}
