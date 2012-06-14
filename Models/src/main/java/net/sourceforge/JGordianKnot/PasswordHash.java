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

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;

import net.sourceforge.JDataManager.DataConverter;
import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject.JDataContents;
import net.sourceforge.JGordianKnot.DataHayStack.HashModeNeedle;

/**
 * Password Hash implementation.
 * @author Tony Washer
 */
public class PasswordHash implements JDataContents {
    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(PasswordHash.class.getSimpleName());

    /**
     * Field ID for mode.
     */
    public static final JDataField FIELD_MODE = FIELD_DEFS.declareLocalField("Mode");

    /**
     * Field ID for hash.
     */
    public static final JDataField FIELD_HASH = FIELD_DEFS.declareLocalField("Hash");

    /**
     * Field ID for cipher set.
     */
    public static final JDataField FIELD_CIPHER = FIELD_DEFS.declareLocalField("CipherSet");

    /**
     * Field ID for symKey map.
     */
    public static final JDataField FIELD_SYMKEYMAP = FIELD_DEFS.declareLocalField("SymKeyMap");

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_MODE.equals(pField)) {
            return theHashMode;
        }
        if (FIELD_HASH.equals(pField)) {
            return theHashBytes;
        }
        if (FIELD_CIPHER.equals(pField)) {
            return theCipherSet;
        }
        if (FIELD_SYMKEYMAP.equals(pField)) {
            return theSymKeyMap;
        }
        return null;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    /**
     * Salt length for passwords.
     */
    private static final int SALTLENGTH = 32;

    /**
     * Hash size for password hash.
     */
    public static final int HASHSIZE = 128;

    /**
     * Sample point for prime hash.
     */
    public static final int SAMPLE_PRIME = 3;

    /**
     * Sample point for alternate hash.
     */
    public static final int SAMPLE_ALT = 5;

    /**
     * Sample point for secret hash.
     */
    public static final int SAMPLE_SECRET = 7;

    /**
     * Hash Mode.
     */
    private final HashMode theHashMode;

    /**
     * Salt Bytes.
     */
    private final byte[] theSaltBytes;

    /**
     * The security generator.
     */
    private final SecurityGenerator theGenerator;

    /**
     * The secure random generator.
     */
    private final SecureRandom theRandom;

    /**
     * The Symmetric Key Map.
     */
    private final Map<SymmetricKey, byte[]> theSymKeyMap;

    /**
     * Hash Bytes.
     */
    private byte[] theHashBytes = null;

    /**
     * Secret hash.
     */
    private byte[] theSecretHash = null;

    /**
     * Encrypted password.
     */
    private byte[] thePassword = null;

    /**
     * CipherSet.
     */
    private CipherSet theCipherSet = null;

    /**
     * Obtain the HashBytes.
     * @return the HashBytes
     */
    public byte[] getHashBytes() {
        return theHashBytes;
    }

    /**
     * Obtain the HashMode.
     * @return the HashMode
     */
    public HashMode getHashMode() {
        return theHashMode;
    }

    /**
     * Obtain the SecurityGenerator.
     * @return the SecurityGenerator
     */
    public SecurityGenerator getSecurityGenerator() {
        return theGenerator;
    }

    /**
     * Get CipherSet.
     * @return the CipherSet
     */
    public CipherSet getCipherSet() {
        return theCipherSet;
    }

    /**
     * Constructor for a completely new password hash.
     * @param pGenerator the security generator
     * @param pPassword the password (cleared after usage)
     * @throws JDataException on error
     */
    protected PasswordHash(final SecurityGenerator pGenerator,
                           final char[] pPassword) throws JDataException {
        /* Store the secure random generator */
        theGenerator = pGenerator;
        theRandom = theGenerator.getRandom();

        /* Create a random HashMode */
        theHashMode = new HashMode(theGenerator.useRestricted(), theRandom);

        /* Generate a new salt */
        theSaltBytes = new byte[SALTLENGTH];
        theRandom.nextBytes(theSaltBytes);

        /* Build hash from password */
        setPassword(pPassword);

        /* Build the SymmetricKey map */
        theSymKeyMap = new HashMap<SymmetricKey, byte[]>();
    }

    /**
     * Constructor for a password hash whose hash is known.
     * @param pGenerator the security generator
     * @param pHashBytes the Hash bytes
     * @param pPassword the password (cleared after usage)
     * @throws WrongPasswordException if wrong password is given
     * @throws JDataException on error
     */
    protected PasswordHash(final SecurityGenerator pGenerator,
                           final byte[] pHashBytes,
                           final char[] pPassword) throws WrongPasswordException, JDataException {
        /* Store the hash bytes and extract the mode */
        theHashBytes = pHashBytes;

        /* Parse the hash */
        HashModeNeedle myNeedle = new HashModeNeedle(theHashBytes);
        theSaltBytes = myNeedle.getSalt();
        theHashMode = myNeedle.getHashMode();

        /* Store the secure random generator */
        theGenerator = pGenerator;
        theRandom = theGenerator.getRandom();

        /* Validate the password */
        attemptPassword(pPassword);

        /* Build the SymmetricKey map */
        theSymKeyMap = new HashMap<SymmetricKey, byte[]>();
    }

    /**
     * Constructor for alternate password hash sharing same password.
     * @param pSource the source hash
     * @throws JDataException on error
     */
    private PasswordHash(final PasswordHash pSource) throws JDataException {
        char[] myPassword = null;

        /* Build the encryption cipher */
        CipherSet mySet = pSource.theCipherSet;

        /* Store the secure random generator */
        theGenerator = pSource.theGenerator;
        theRandom = pSource.theRandom;

        /* Create a random HashMode */
        theHashMode = new HashMode(theGenerator.useRestricted(), theRandom);

        /* Generate a new salt */
        theSaltBytes = new byte[SALTLENGTH];
        theRandom.nextBytes(theSaltBytes);

        /* Protect against exceptions */
        try {
            /* Access the original password */
            myPassword = mySet.decryptChars(pSource.thePassword);

            /* Build hash from password */
            setPassword(myPassword);

            /* Build the SymmetricKey map */
            theSymKeyMap = new HashMap<SymmetricKey, byte[]>();

            /* Catch Exceptions */
        } catch (JDataException e) {
            throw e;
        } finally {
            if (myPassword != null) {
                Arrays.fill(myPassword, (char) 0);
            }
        }
    }

    /**
     * Clone this password hash.
     * @return the cloned hash
     * @throws JDataException on error
     */
    public PasswordHash cloneHash() throws JDataException {
        /* Return the cloned hash */
        return new PasswordHash(this);
    }

    @Override
    public int hashCode() {
        /* Calculate and return the hashCode for this password key */
        return Arrays.hashCode(theHashBytes);
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

        /* Make sure that the object is a Password Hash */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target Hash */
        PasswordHash myThat = (PasswordHash) pThat;

        /* Compare the two */
        return Arrays.equals(theHashBytes, myThat.getHashBytes());
    }

    /**
     * Build the password hash from the password.
     * @param pPassword the password (cleared after usage)
     * @throws JDataException on error
     */
    private void setPassword(final char[] pPassword) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Generate the HashBytes */
            theHashBytes = generateHashBytes(pPassword);

            /* Create the Cipher Set */
            theCipherSet = new CipherSet(theGenerator, theHashMode);
            theCipherSet.buildCiphers(theSecretHash);

            /* Encrypt the password */
            thePassword = theCipherSet.encryptChars(pPassword);

            /* Clear out the password */
            Arrays.fill(pPassword, (char) 0);
        } catch (Exception e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to initialise using password", e);
        }
    }

    /**
     * Attempt to match the password hash with the password.
     * @param pPassword the password (cleared after usage)
     * @throws WrongPasswordException n wrong password
     * @throws JDataException on error
     */
    private void attemptPassword(final char[] pPassword) throws WrongPasswordException, JDataException {
        byte[] myHashBytes;

        /* Generate the HashBytes */
        myHashBytes = generateHashBytes(pPassword);

        /* Check that the arrays match */
        if (!Arrays.equals(theHashBytes, myHashBytes)) {
            /* Fail the password attempt */
            throw new WrongPasswordException("Invalid Password");
        }

        /* Create the Cipher Set */
        theCipherSet = new CipherSet(theGenerator, theHashMode);
        theCipherSet.buildCiphers(theSecretHash);

        /* Encrypt the password */
        thePassword = theCipherSet.encryptChars(pPassword);

        /* Clear out the password */
        Arrays.fill(pPassword, (char) 0);
    }

    /**
     * Generate Hash bytes.
     * @param pPassword the password for the keys
     * @return the Salt and Hash array
     * @throws JDataException on error
     */
    private byte[] generateHashBytes(final char[] pPassword) throws JDataException {
        byte[] myHashBytes;
        byte[] myPrimeBytes = null;
        byte[] myAlternateBytes = null;
        byte[] mySecretBytes = null;
        byte[] myPrimeHash;
        byte[] myAlternateHash;
        byte[] mySecretHash;
        byte[] myPassBytes = null;
        IterationCounter myCounter = new IterationCounter();
        byte[] mySeed = theGenerator.getSecurityBytes();
        Mac myPrimeMac;
        Mac myAlternateMac;
        Mac mySecretMac;
        int iIterations = theGenerator.getNumHashIterations();
        int iSwitch = theHashMode.getSwitchAdjust() + (iIterations / 2);
        int iFinal = theHashMode.getFinalAdjust() + iIterations;

        /* Protect against exceptions */
        try {
            /* Convert password to bytes */
            myPassBytes = DataConverter.charsToByteArray(pPassword);

            /* Access the MACs */
            myPrimeMac = theGenerator.accessMac(theHashMode.getPrimeDigest(), myPassBytes);
            myAlternateMac = theGenerator.accessMac(theHashMode.getAlternateDigest(), myPassBytes);
            mySecretMac = theGenerator.accessMac(theHashMode.getSecretDigest(), myPassBytes);

            /* Initialise the hash values as the salt bytes */
            myPrimeHash = theSaltBytes;
            myAlternateHash = theSaltBytes;
            mySecretHash = theSaltBytes;

            /* Loop through the iterations */
            for (int i = 0; i < iFinal; i++) {
                /* Note the final pass */
                int iPass = i + 1;
                boolean bFinalPass = (iPass == iFinal);

                /* Iterate the counter */
                byte[] myCountBuffer = myCounter.iterate();

                /* Update the prime Mac */
                myPrimeMac.update(myPrimeHash);
                myPrimeMac.update(myCountBuffer);
                myPrimeMac.update(mySeed);

                /* Recalculate the prime hash skipping every third time */
                if ((bFinalPass) || ((iPass % SAMPLE_PRIME) != 0)) {
                    myPrimeHash = myPrimeMac.doFinal();
                    myPrimeBytes = DataConverter.combineHashes(myPrimeBytes, myPrimeHash);
                }

                /* Update the alternate Mac */
                myAlternateMac.update(myAlternateHash);
                myAlternateMac.update(myCountBuffer);
                myAlternateMac.update(mySeed);

                /* Recalculate the alternate hash skipping every fifth time */
                if ((bFinalPass) || ((iPass % SAMPLE_ALT) != 0)) {
                    myAlternateHash = myAlternateMac.doFinal();
                    myAlternateBytes = DataConverter.combineHashes(myAlternateBytes, myAlternateHash);
                }

                /* Update the secret Mac */
                mySecretMac.update(mySecretHash);
                mySecretMac.update(myCountBuffer);
                mySecretMac.update(mySeed);

                /* Recalculate the secret hash skipping every seventh time */
                if ((bFinalPass) || ((iPass % SAMPLE_SECRET) != 0)) {
                    mySecretHash = mySecretMac.doFinal();
                    mySecretBytes = DataConverter.combineHashes(mySecretBytes, mySecretHash);

                    /* Every seventh time */
                } else {
                    /* Add in the Prime and Alternate hashes */
                    mySecretMac.update(myPrimeHash);
                    mySecretMac.update(myAlternateHash);
                }

                /* If we have hit the switch point */
                if (iPass == iSwitch) {
                    /* Save the alternate hash value */
                    byte[] myAlt = myAlternateHash;

                    /* Switch the hashes */
                    myAlternateHash = myPrimeHash;
                    myPrimeHash = myAlt;
                }
            }

            /* Combine the Primary and Alternate hashes */
            byte[] myExternalHash = DataConverter.combineHashes(myPrimeBytes, myAlternateBytes);

            /* Store the Secret Hash */
            theSecretHash = mySecretBytes;

            /* Create the external hash */
            HashModeNeedle myNeedle = new HashModeNeedle(theHashMode, theSaltBytes, myExternalHash);
            myHashBytes = myNeedle.getExternal();
        } catch (Exception e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to generate salt and hash", e);
        } finally {
            if (myPassBytes != null) {
                Arrays.fill(myPassBytes, (byte) 0);
            }
        }

        /* Check whether the HashBytes is too large */
        if (myHashBytes.length > HASHSIZE) {
            throw new JDataException(ExceptionClass.DATA, "Password Hash too large: " + myHashBytes.length);
        }

        /* Return to caller */
        return myHashBytes;
    }

    /**
     * Get the secured private key definition from an Asymmetric Key.
     * @param pKey the AsymmetricKey whose private key is to be secured
     * @return the secured key
     * @throws JDataException on error
     */
    public byte[] securePrivateKey(final AsymmetricKey pKey) throws JDataException {
        /* Secure the key */
        return theCipherSet.securePrivateKey(pKey);
    }

    /**
     * Derive an AsymmetricKey from its definition.
     * @param pSecuredPrivateKeyDef the Secured Private Key definition
     * @param pPublicKeyDef the Public KeyDef
     * @return the asymmetric key
     * @throws JDataException on error
     */
    public AsymmetricKey deriveAsymmetricKey(final byte[] pSecuredPrivateKeyDef,
                                             final byte[] pPublicKeyDef) throws JDataException {
        /* derive the Asymmetric Key */
        return theCipherSet.deriveAsymmetricKey(pSecuredPrivateKeyDef, pPublicKeyDef);
    }

    /**
     * derive a SymmetricKey from secured key definition.
     * @param pSecuredKeyDef the secured key definition
     * @return the Symmetric key
     * @throws JDataException on error
     */
    public SymmetricKey deriveSymmetricKey(final byte[] pSecuredKeyDef) throws JDataException {
        /* Derive the symmetric key */
        SymmetricKey mySymKey = theCipherSet.deriveSymmetricKey(pSecuredKeyDef);

        /* Add the key definition to the map */
        theSymKeyMap.put(mySymKey, pSecuredKeyDef);

        /* Return the new key */
        return mySymKey;
    }

    /**
     * Get the Secured Key Definition for a Symmetric Key.
     * @param pKey the Symmetric Key to secure
     * @return the secured key definition
     * @throws JDataException on error
     */
    public byte[] secureSymmetricKey(final SymmetricKey pKey) throws JDataException {
        byte[] myWrappedKey;

        /* Look for an entry in the map and return it if found */
        myWrappedKey = theSymKeyMap.get(pKey);
        if (myWrappedKey != null) {
            return myWrappedKey;
        }

        /* Wrap the Key */
        myWrappedKey = theCipherSet.secureSymmetricKey(pKey);

        /* Add the key definition to the map */
        theSymKeyMap.put(pKey, myWrappedKey);

        /* Return to caller */
        return myWrappedKey;
    }

    /**
     * Encrypt string.
     * @param pString the string to encrypt
     * @return the encrypted bytes
     * @throws JDataException on error
     */
    public byte[] encryptString(final String pString) throws JDataException {
        /* Encrypt the string */
        return theCipherSet.encryptString(pString);
    }

    /**
     * Decrypt string.
     * @param pBytes the string to decrypt
     * @return the decrypted string
     * @throws JDataException on error
     */
    public String decryptString(final byte[] pBytes) throws JDataException {
        /* Decrypt the bytes */
        return theCipherSet.decryptString(pBytes);
    }

    /**
     * Attempt the cached password against the passed hash.
     * @param pHashBytes the Hash to test against
     * @return the new PasswordHash if successful, otherwise null
     */
    protected PasswordHash attemptPassword(final byte[] pHashBytes) {
        char[] myPassword = null;

        /* Protect against exceptions */
        try {
            /* Access the original password */
            myPassword = theCipherSet.decryptChars(thePassword);

            /* Try to initialise the hash */
            PasswordHash myHash = new PasswordHash(theGenerator, pHashBytes, myPassword);

            /* Return the new hash */
            return myHash;

            /* Catch Exceptions */
        } catch (JDataException e) {
            return null;
        } catch (WrongPasswordException e) {
            return null;
        } finally {
            if (myPassword != null) {
                Arrays.fill(myPassword, (char) 0);
            }
        }
    }
}
