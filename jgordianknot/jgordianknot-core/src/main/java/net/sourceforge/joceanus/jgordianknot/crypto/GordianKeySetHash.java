/*********************************-=--=**********************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

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
    public static final int HASHLEN = GordianKeySetHashRecipe.HASHLEN;

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
    private byte[] theHash;

    /**
     * Encrypted password.
     */
    private byte[] thePassword;

    /**
     * CipherSet.
     */
    private GordianKeySet theKeySet;

    /**
     * Constructor for a completely new keySetHash.
     * @param pFactory the factory
     * @param pPassword the password (cleared after usage)
     * @throws OceanusException on error
     */
    protected GordianKeySetHash(final GordianFactory pFactory,
                                final char[] pPassword) throws OceanusException {
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
     * @throws OceanusException on error
     */
    protected GordianKeySetHash(final GordianFactory pFactory,
                                final byte[] pHashBytes,
                                final char[] pPassword) throws OceanusException {
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
     * @throws OceanusException on error
     */
    private GordianKeySetHash(final GordianKeySetHash pSource) throws OceanusException {
        /* Build the encryption cipher */
        final GordianKeySet mySet = pSource.theKeySet;

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
            myPassword = TethysDataConverter.bytesToCharArray(myBytes);

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
     * @throws OceanusException on error
     */
    public GordianKeySetHash similarHash() throws OceanusException {
        /* Return the similar hash */
        return new GordianKeySetHash(this);
    }

    /**
     * Build the password hash from the password.
     * @param pPassword the password
     * @throws OceanusException on error
     */
    private void setPassword(final char[] pPassword) throws OceanusException {
        /* Generate the Hash */
        final byte[][] myResults = generateHash(pPassword);
        theHash = myResults[0];

        /* Create the Key Set */
        theKeySet = new GordianKeySet(theFactory);
        theKeySet.buildFromSecret(myResults[1], myResults[2]);

        /* Protect against exceptions */
        byte[] myBytes = null;
        try {
            /* Encrypt the password */
            myBytes = TethysDataConverter.charsToByteArray(pPassword);
            thePassword = theKeySet.encryptBytes(myBytes);
        } finally {
            /* Clear out the password bytes */
            if (myBytes != null) {
                Arrays.fill(myBytes, (byte) 0);
            }
        }
    }

    /**
     * Attempt to match the password hash with the password.
     * @param pPassword the password
     * @throws GordianBadCredentialsException on wrong password
     * @throws OceanusException on error
     */
    private void attemptPassword(final char[] pPassword) throws OceanusException {
        /* Generate the HashBytes */
        final byte[][] myResults = generateHash(pPassword);

        /* Check that the arrays match */
        if (!Arrays.equals(theHash, myResults[0])) {
            /* Fail the password attempt */
            throw new GordianBadCredentialsException("Invalid Password");
        }

        /* Create the Key Set */
        theKeySet = new GordianKeySet(theFactory);
        theKeySet.buildFromSecret(myResults[1], myResults[2]);

        /* Protect against exceptions */
        byte[] myBytes = null;
        try {
            /* Encrypt the password */
            myBytes = TethysDataConverter.charsToByteArray(pPassword);
            thePassword = theKeySet.encryptBytes(myBytes);
        } finally {
            /* Clear out the password bytes */
            if (myBytes != null) {
                Arrays.fill(myBytes, (byte) 0);
            }
        }
    }

    /**
     * Generate Hash.
     * @param pPassword the password for the keys
     * @return the Salt and Hash array
     * @throws OceanusException on error
     */
    private byte[][] generateHash(final char[] pPassword) throws OceanusException {
        byte[] myPassBytes = null;

        /* Protect against exceptions */
        try {
            /* Obtain configuration details */
            final GordianPersonalisation myPersonal = theFactory.getPersonalisation();
            final int iIterations = theFactory.getNumIterations();
            final int iFinal = theRecipe.getAdjustment()
                               + iIterations;

            /* Create a byte array of the iterations */
            final byte[] myLoops = TethysDataConverter.integerToByteArray(iFinal);

            /* Convert password to bytes */
            myPassBytes = TethysDataConverter.charsToByteArray(pPassword);

            /* Create the primeMac */
            GordianMacSpec myMacSpec = GordianMacSpec.hMac(theRecipe.getPrimeDigest());
            final GordianMac myPrimeMac = theFactory.createMac(myMacSpec);
            myPrimeMac.initMac(myPassBytes);

            /* Create the alternateMac */
            myMacSpec = GordianMacSpec.hMac(theRecipe.getAlternateDigest());
            final GordianMac myAlternateMac = theFactory.createMac(myMacSpec);
            myAlternateMac.initMac(myPassBytes);

            /* Create the secretMac */
            myMacSpec = GordianMacSpec.hMac(theRecipe.getSecretDigest());
            final GordianMac mySecretMac = theFactory.createMac(myMacSpec);
            mySecretMac.initMac(myPassBytes);

            /* Initialise hash bytes and counter */
            final byte[] myPrimeBytes = new byte[myPrimeMac.getMacSize()];
            final byte[] myAlternateBytes = new byte[myAlternateMac.getMacSize()];
            final byte[] mySecretBytes = new byte[mySecretMac.getMacSize()];
            final byte[] myPrimeHash = new byte[myPrimeMac.getMacSize()];
            final byte[] myAlternateHash = new byte[myAlternateMac.getMacSize()];
            final byte[] mySecretHash = new byte[mySecretMac.getMacSize()];

            /* Access final digest */
            final GordianDigestSpec myDigestSpec = new GordianDigestSpec(theRecipe.getExternalDigest(), GordianLength.LEN_512);
            final GordianDigest myDigest = theFactory.createDigest(myDigestSpec);

            /* Initialise the hash input values as the salt bytes */
            final byte[] mySaltBytes = theRecipe.getInitVector();
            byte[] myPrimeInput = mySaltBytes;
            byte[] myAlternateInput = mySaltBytes;
            byte[] mySecretInput = mySaltBytes;

            /* Update each Hash with the personalisation */
            myPersonal.updateMac(myPrimeMac);
            myPersonal.updateMac(myAlternateMac);
            myPersonal.updateMac(mySecretMac);

            /* Update each Hash with the loops */
            myPrimeMac.update(myLoops);
            myAlternateMac.update(myLoops);
            mySecretMac.update(myLoops);

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
                TethysDataConverter.buildHashResult(myPrimeBytes, myPrimeHash);
                myAlternateMac.finish(myAlternateHash, 0);
                TethysDataConverter.buildHashResult(myAlternateBytes, myAlternateHash);
                mySecretMac.finish(mySecretHash, 0);
                TethysDataConverter.buildHashResult(mySecretBytes, mySecretHash);
            }

            /* Combine the Primary and Alternate hashes to form the initVector */
            myDigest.update(myPrimeHash);
            myDigest.update(myAlternateHash);
            final byte[] myInitVector = myDigest.finish();

            /* Combine the Primary and Alternate bytes to form the external hash */
            myDigest.update(myPrimeBytes);
            myDigest.update(myAlternateBytes);
            final byte[] myExternalHash = myDigest.finish();

            /* Create the external hash */
            final byte[] myHashBytes = theRecipe.buildExternal(pPassword.length, myExternalHash);

            /* Check whether the HashBytes is too large */
            if (myHashBytes.length > HASHLEN) {
                throw new GordianDataException("Password Hash too large: "
                                               + myHashBytes.length);
            }

            /* Return to caller */
            return new byte[][]
            { myHashBytes, mySecretBytes, myInitVector };

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
            myPassword = TethysDataConverter.bytesToCharArray(myBytes);

            /* Try to initialise the hash and return it */
            return new GordianKeySetHash(theFactory, pHashBytes, myPassword);

            /* Catch Exceptions */
        } catch (OceanusException e) {
            LOGGER.error("Password attempt failed", e);
            return null;
        } catch (GordianBadCredentialsException e) {
            return null;
        } finally {
            /* Clear out password and bytes */
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
        final GordianKeySetHash myThat = (GordianKeySetHash) pThat;

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
