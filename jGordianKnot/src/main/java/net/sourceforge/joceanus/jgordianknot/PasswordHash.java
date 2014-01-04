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

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import net.sourceforge.joceanus.jdatamanager.DataConverter;
import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;

/**
 * Password Hash implementation.
 */
public class PasswordHash {
    /**
     * Hash size for password hash.
     */
    public static final int HASHSIZE = 128;

    /**
     * Sample point for prime hash.
     */
    public static final int SAMPLE_PRIME = 7;

    /**
     * Sample point for alternate hash.
     */
    public static final int SAMPLE_ALT = 5;

    /**
     * Sample point for secret hash.
     */
    public static final int SAMPLE_SECRET = 3;

    /**
     * Hash Key.
     */
    private final HashKey theHashKey;

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
        return Arrays.copyOf(theHashBytes, theHashBytes.length);
    }

    /**
     * Obtain the HashKey.
     * @return the HashKey
     */
    public HashKey getHashKey() {
        return theHashKey;
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

        /* Create a random HashKey */
        theHashKey = new HashKey(theGenerator);

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
     * @throws InvalidCredentialsException if wrong password is given
     * @throws JDataException on error
     */
    protected PasswordHash(final SecurityGenerator pGenerator,
                           final byte[] pHashBytes,
                           final char[] pPassword) throws JDataException {
        /* Store the hash bytes and extract the mode */
        theHashBytes = Arrays.copyOf(pHashBytes, pHashBytes.length);

        /* Parse the hash */
        theHashKey = new HashKey(pPassword.length, pHashBytes);

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

        /* Build the encryption cipher */
        CipherSet mySet = pSource.theCipherSet;

        /* Store the secure random generator */
        theGenerator = pSource.theGenerator;
        theRandom = pSource.theRandom;

        /* Create a random HashKey */
        theHashKey = new HashKey(theGenerator);

        /* Protect against exceptions */
        char[] myPassword = null;
        try {
            /* Access the original password */
            myPassword = mySet.decryptChars(pSource.thePassword);

            /* Build hash from password */
            setPassword(myPassword);

            /* Build the SymmetricKey map */
            theSymKeyMap = new HashMap<SymmetricKey, byte[]>();

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
        /* Generate the HashBytes */
        theHashBytes = generateHashBytes(pPassword);

        /* Create the Cipher Set */
        theCipherSet = new CipherSet(theGenerator, theHashKey);
        theCipherSet.buildCiphers(theSecretHash);

        /* Encrypt the password */
        thePassword = theCipherSet.encryptChars(pPassword);

        /* Clear out the password */
        Arrays.fill(pPassword, (char) 0);
    }

    /**
     * Attempt to match the password hash with the password.
     * @param pPassword the password (cleared after usage)
     * @throws InvalidCredentialsException on wrong password
     * @throws JDataException on error
     */
    private void attemptPassword(final char[] pPassword) throws JDataException {
        /* Generate the HashBytes */
        byte[] myHashBytes = generateHashBytes(pPassword);

        /* Check that the arrays match */
        if (!Arrays.equals(theHashBytes, myHashBytes)) {
            /* Fail the password attempt */
            throw new InvalidCredentialsException("Invalid Password");
        }

        /* Create the Cipher Set */
        theCipherSet = new CipherSet(theGenerator, theHashKey);
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
        byte[] myPassBytes = null;

        /* Protect against exceptions */
        try {
            /* Initialise hash bytes and counter */
            byte[] myPrimeBytes = null;
            byte[] myAlternateBytes = null;
            byte[] mySecretBytes = null;

            /* Obtain configuration details */
            byte[] mySeed = theGenerator.getSecurityBytes();
            int iIterations = theGenerator.getNumHashIterations();
            int iFinal = theHashKey.getAdjustment()
                         + iIterations;

            /* Convert password to bytes */
            myPassBytes = DataConverter.charsToByteArray(pPassword);

            /* Access the MACs */
            DataMac myPrimeMac = theGenerator.generateMac(theHashKey.getPrimeDigest(), myPassBytes);
            DataMac myAlternateMac = theGenerator.generateMac(theHashKey.getAlternateDigest(), myPassBytes);
            DataMac mySecretMac = theGenerator.generateMac(theHashKey.getSecretDigest(), myPassBytes);

            /* Initialise the hash values as the salt bytes */
            byte[] mySaltBytes = theHashKey.getInitVector();
            byte[] myPrimeHash = mySaltBytes;
            byte[] myAlternateHash = mySaltBytes;
            byte[] mySecretHash = mySaltBytes;

            /* Update each Hash with the seed */
            myPrimeMac.update(mySeed);
            myAlternateMac.update(mySeed);
            mySecretMac.update(mySeed);

            /* Loop through the iterations */
            for (int iPass = 1; iPass <= iFinal; iPass++) {
                /* Update the prime Mac */
                myPrimeMac.update(myPrimeHash);

                /* Add in Alternate Hash every so often */
                if ((iPass % SAMPLE_PRIME) == 0) {
                    /* Add in the Alternate hash */
                    myPrimeMac.update(myAlternateHash);
                }

                /* Update the alternate Mac */
                myAlternateMac.update(myAlternateHash);

                /* Add in prime hash every so often */
                if ((iPass % SAMPLE_ALT) == 0) {
                    /* Add in the Prime hash */
                    myAlternateMac.update(myPrimeHash);
                }

                /* Update the secret Mac */
                mySecretMac.update(mySecretHash);

                /* Add in prime/alternate hashes every so often */
                if ((iPass % SAMPLE_SECRET) == 0) {
                    /* Add in the Prime and Alternate hashes */
                    mySecretMac.update(myPrimeHash);
                    mySecretMac.update(myAlternateHash);
                }

                /* Recalculate hashes and combine them */
                myPrimeHash = myPrimeMac.finish();
                myPrimeBytes = DataConverter.combineHashes(myPrimeBytes, myPrimeHash);
                myAlternateHash = myAlternateMac.finish();
                myAlternateBytes = DataConverter.combineHashes(myAlternateBytes, myAlternateHash);
                mySecretHash = mySecretMac.finish();
                mySecretBytes = DataConverter.combineHashes(mySecretBytes, mySecretHash);
            }

            /* Combine the Primary and Alternate hashes */
            byte[] myExternalHash = DataConverter.combineHashes(myPrimeBytes, myAlternateBytes);

            /* Store the Secret Hash */
            theSecretHash = mySecretBytes;

            /* Create the external hash */
            byte[] myHashBytes = theHashKey.buildExternal(pPassword.length, myExternalHash);

            /* Check whether the HashBytes is too large */
            if (myHashBytes.length > HASHSIZE) {
                throw new JDataException(ExceptionClass.DATA, "Password Hash too large: "
                                                              + myHashBytes.length);
            }

            /* Return to caller */
            return myHashBytes;

        } finally {
            if (myPassBytes != null) {
                Arrays.fill(myPassBytes, (byte) 0);
            }
        }
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
        theSymKeyMap.put(mySymKey, Arrays.copyOf(pSecuredKeyDef, pSecuredKeyDef.length));

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
            return Arrays.copyOf(myWrappedKey, myWrappedKey.length);
        }

        /* Wrap the Key */
        myWrappedKey = theCipherSet.secureSymmetricKey(pKey);

        /* Add the key definition to the map */
        theSymKeyMap.put(pKey, Arrays.copyOf(myWrappedKey, myWrappedKey.length));

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
    protected final PasswordHash attemptPassword(final byte[] pHashBytes) {
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
            theGenerator.getLogger().log(Level.SEVERE, "Password attempt failed", e);
            return null;
        } catch (InvalidCredentialsException e) {
            return null;
        } finally {
            if (myPassword != null) {
                Arrays.fill(myPassword, (char) 0);
            }
        }
    }
}
