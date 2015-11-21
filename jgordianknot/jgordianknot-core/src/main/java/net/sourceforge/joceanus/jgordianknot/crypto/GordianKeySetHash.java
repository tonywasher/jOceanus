/*********************************-=--=**********************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jgordianknot/jgordianknot-core/src/main/java/net/sourceforge/joceanus/jgordianknot/crypto/MacType.java $
 * $Revision: 647 $
 * $Author: Tony $
 * $Date: 2015-11-04 08:58:02 +0000 (Wed, 04 Nov 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jtethys.DataConverter;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Hash from which to derive KeySet.
 */
public final class GordianKeySetHash {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GordianKeySetHash.class);

    /**
     * Hash size for password hash.
     */
    public static final int HASHSIZE = 98;

    /**
     * Hash Recipe.
     */
    private final GordianKeySetHashRecipe theRecipe;

    /**
     * The factory.
     */
    private final GordianFactory theFactory;

    /**
     * The Hash.
     */
    private byte[] theHash = null;

    /**
     * The Secret.
     */
    private byte[] theSecret = null;

    /**
     * Encrypted password.
     */
    private byte[] thePassword = null;

    /**
     * CipherSet.
     */
    private GordianKeySet theKeySet = null;

    /**
     * Constructor for a completely new keySetHash.
     * @param pFactory the factory
     * @param pPassword the password (cleared after usage)
     * @throws JOceanusException on error
     */
    protected GordianKeySetHash(final GordianFactory pFactory,
                                final char[] pPassword) throws JOceanusException {
        /* Store the factory */
        theFactory = pFactory;

        /* Create a random HashRecipe */
        theRecipe = new GordianKeySetHashRecipe(theFactory);

        /* Build hash from password */
        setPassword(pPassword);
    }

    /**
     * Constructor for a password hash whose hash is known.
     * @param pFactory the factory
     * @param pHashBytes the Hash bytes
     * @param pPassword the password (cleared after usage)
     * @throws GordianBadCredentialsException if wrong password is given
     * @throws JOceanusException on error
     */
    protected GordianKeySetHash(final GordianFactory pFactory,
                                final byte[] pHashBytes,
                                final char[] pPassword) throws JOceanusException {
        /* Store the factory */
        theFactory = pFactory;

        /* Store the hash bytes and extract the mode */
        theHash = Arrays.copyOf(pHashBytes, pHashBytes.length);

        /* Parse the hash */
        theRecipe = new GordianKeySetHashRecipe(theFactory, pPassword.length, pHashBytes);

        /* Validate the password */
        attemptPassword(pPassword);
    }

    /**
     * Constructor for alternate hash sharing same password.
     * @param pSource the source hash
     * @throws JOceanusException on error
     */
    private GordianKeySetHash(final GordianKeySetHash pSource) throws JOceanusException {
        /* Build the encryption cipher */
        GordianKeySet mySet = pSource.theKeySet;

        /* Store the secure random generator */
        theFactory = pSource.theFactory;

        /* Create a random HashRecipe */
        theRecipe = new GordianKeySetHashRecipe(theFactory);

        /* Protect against exceptions */
        char[] myPassword = null;
        byte[] myBytes = null;
        try {
            /* Access the original password */
            myBytes = mySet.decryptBytes(pSource.thePassword);
            myPassword = DataConverter.bytesToCharArray(myBytes);

            /* Build hash from password */
            setPassword(myPassword);
        } finally {
            if (myPassword != null) {
                Arrays.fill(myPassword, (char) 0);
            }
            if (myBytes != null) {
                Arrays.fill(myBytes, (byte) 0);
            }
        }
    }

    /**
     * Obtain the Hash.
     * @return the Hash
     */
    public byte[] getHash() {
        return Arrays.copyOf(theHash, theHash.length);
    }

    /**
     * Get CipherSet.
     * @return the CipherSet
     */
    public GordianKeySet getKeySet() {
        return theKeySet;
    }

    /**
     * Get Factory.
     * @return the Factory
     */
    private GordianFactory getFactory() {
        return theFactory;
    }

    /**
     * obtain similar keySetHash (same password).
     * @return the similar hash
     * @throws JOceanusException on error
     */
    public GordianKeySetHash similarHash() throws JOceanusException {
        /* Return the similar hash */
        return new GordianKeySetHash(this);
    }

    /**
     * Build the password hash from the password.
     * @param pPassword the password (cleared after usage)
     * @throws JOceanusException on error
     */
    private void setPassword(final char[] pPassword) throws JOceanusException {
        /* Generate the Hash */
        theHash = generateHash(pPassword);

        /* Create the Key Set */
        theKeySet = new GordianKeySet(theFactory);
        theKeySet.buildFromSecret(theSecret, theRecipe.getInitVector());

        /* Protect against exceptions */
        byte[] myBytes = null;
        try {
            /* Encrypt the password */
            myBytes = DataConverter.charsToByteArray(pPassword);
            thePassword = theKeySet.encryptBytes(myBytes);
        } finally {
            /* Clear out the password */
            Arrays.fill(pPassword, (char) 0);
            if (myBytes != null) {
                Arrays.fill(myBytes, (byte) 0);
            }
        }
    }

    /**
     * Attempt to match the password hash with the password.
     * @param pPassword the password (cleared after usage)
     * @throws GordianBadCredentialsException on wrong password
     * @throws JOceanusException on error
     */
    private void attemptPassword(final char[] pPassword) throws JOceanusException {
        /* Generate the HashBytes */
        byte[] myHash = generateHash(pPassword);

        /* Check that the arrays match */
        if (!Arrays.equals(theHash, myHash)) {
            /* Fail the password attempt */
            throw new GordianBadCredentialsException("Invalid Password");
        }

        /* Create the Key Set */
        theKeySet = new GordianKeySet(theFactory);
        theKeySet.buildFromSecret(theSecret, theRecipe.getInitVector());

        /* Protect against exceptions */
        byte[] myBytes = null;
        try {
            /* Encrypt the password */
            myBytes = DataConverter.charsToByteArray(pPassword);
            thePassword = theKeySet.encryptBytes(myBytes);
        } finally {
            /* Clear out the password */
            Arrays.fill(pPassword, (char) 0);
            if (myBytes != null) {
                Arrays.fill(myBytes, (byte) 0);
            }
        }
    }

    /**
     * Generate Hash.
     * @param pPassword the password for the keys
     * @return the Salt and Hash array
     * @throws JOceanusException on error
     */
    private byte[] generateHash(final char[] pPassword) throws JOceanusException {
        byte[] myPassBytes = null;

        /* Protect against exceptions */
        try {
            /* Obtain configuration details */
            byte[] mySeed = theFactory.getPersonalisation();
            int iIterations = theFactory.getNumIterations();
            int iFinal = theRecipe.getAdjustment()
                         + iIterations;

            /* Convert password to bytes */
            myPassBytes = DataConverter.charsToByteArray(pPassword);

            /* Create the primeMac */
            GordianMacSpec mySpec = new GordianMacSpec(GordianMacType.HMAC, theRecipe.getPrimeDigest());
            GordianMac myPrimeMac = theFactory.createMac(mySpec);
            myPrimeMac.initMac(myPassBytes);

            /* Create the alternateMac */
            mySpec = new GordianMacSpec(GordianMacType.HMAC, theRecipe.getAlternateDigest());
            GordianMac myAlternateMac = theFactory.createMac(mySpec);
            myAlternateMac.initMac(myPassBytes);

            /* Create the secretMac */
            mySpec = new GordianMacSpec(GordianMacType.HMAC, theRecipe.getSecretDigest());
            GordianMac mySecretMac = theFactory.createMac(mySpec);
            mySecretMac.initMac(myPassBytes);

            /* Initialise hash bytes and counter */
            byte[] myPrimeBytes = new byte[myPrimeMac.getMacSize()];
            byte[] myAlternateBytes = new byte[myAlternateMac.getMacSize()];
            byte[] mySecretBytes = new byte[mySecretMac.getMacSize()];
            byte[] myPrimeHash = new byte[myPrimeMac.getMacSize()];
            byte[] myAlternateHash = new byte[myAlternateMac.getMacSize()];
            byte[] mySecretHash = new byte[mySecretMac.getMacSize()];

            /* Access final digest */
            GordianDigest myDigest = theFactory.createDigest(theFactory.getDefaultDigest());

            /* Initialise the hash input values as the salt bytes */
            byte[] mySaltBytes = theRecipe.getInitVector();
            byte[] myPrimeInput = mySaltBytes;
            byte[] myAlternateInput = mySaltBytes;
            byte[] mySecretInput = mySaltBytes;

            /* Update each Hash with the seed */
            myPrimeMac.update(mySeed);
            myAlternateMac.update(mySeed);
            mySecretMac.update(mySeed);

            /* Loop through the iterations */
            for (int iPass = 0; iPass < iFinal; iPass++) {
                /* Update the prime Mac */
                myPrimeMac.update(myPrimeInput);

                /* Update the alternate Mac */
                myAlternateMac.update(myAlternateInput);

                /* Update the secret Mac */
                mySecretMac.update(mySecretInput);
                mySecretMac.update(myPrimeInput);
                mySecretMac.update(myAlternateInput);

                /* Update inputs */
                myPrimeInput = myPrimeHash;
                myAlternateInput = myAlternateHash;
                mySecretInput = mySecretHash;

                /* Recalculate hashes and combine them */
                myPrimeMac.finish(myPrimeHash, 0);
                DataConverter.buildHashResult(myPrimeBytes, myPrimeHash);
                myAlternateMac.finish(myAlternateHash, 0);
                DataConverter.buildHashResult(myAlternateBytes, myAlternateHash);
                mySecretMac.finish(mySecretHash, 0);
                DataConverter.buildHashResult(mySecretBytes, mySecretHash);
            }

            /* Combine the Primary and Alternate hashes */
            myDigest.update(myPrimeBytes);
            myDigest.update(myAlternateBytes);
            byte[] myExternalHash = myDigest.finish();

            /* Store the Secret Hash */
            theSecret = mySecretBytes;

            /* Create the external hash */
            byte[] myHashBytes = theRecipe.buildExternal(pPassword.length, myExternalHash);

            /* Check whether the HashBytes is too large */
            if (myHashBytes.length > HASHSIZE) {
                throw new GordianDataException("Password Hash too large: "
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
     * Attempt the cached password against the passed hash.
     * @param pHashBytes the Hash to test against
     * @return the new PasswordHash if successful, otherwise null
     */
    public GordianKeySetHash attemptPassword(final byte[] pHashBytes) {
        /* Protect against exceptions */
        char[] myPassword = null;
        byte[] myBytes = null;
        try {
            /* Access the original password */
            myBytes = theKeySet.decryptBytes(thePassword);
            myPassword = DataConverter.bytesToCharArray(myBytes);

            /* Try to initialise the hash and return it */
            return new GordianKeySetHash(theFactory, pHashBytes, myPassword);

            /* Catch Exceptions */
        } catch (JOceanusException e) {
            LOGGER.error("Password attempt failed", e);
            return null;
        } catch (GordianBadCredentialsException e) {
            return null;
        } finally {
            if (myPassword != null) {
                Arrays.fill(myPassword, (char) 0);
            }
            if (myBytes != null) {
                Arrays.fill(myBytes, (byte) 0);
            }
        }
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (pThat == this) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is the same class */
        if (!(pThat instanceof GordianKeySetHash)) {
            return false;
        }

        /* Access the target field */
        GordianKeySetHash myThat = (GordianKeySetHash) pThat;

        /* Check differences */
        return theFactory.equals(myThat.getFactory())
               && Arrays.equals(theHash, myThat.getHash());
    }

    @Override
    public int hashCode() {
        return GordianFactory.HASH_PRIME * theFactory.hashCode()
               + Arrays.hashCode(theHash);
    }
}
