/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.impl.core.keyset;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianBadCredentialsException;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;

import java.util.Arrays;

/**
 * Hash from which to derive KeySet.
 */
public final class GordianCoreKeySetHash
    implements GordianKeySetHash {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(GordianKeySetHash.class);

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
    private final GordianCoreFactory theFactory;

    /**
     * The Hash.
     */
    private byte[] theHash;

    /**
     * Encrypted password.
     */
    private byte[] thePassword;

    /**
     * Encrypted child password.
     */
    private byte[] theChildPassword;

    /**
     * CipherSet.
     */
    private GordianCoreKeySet theKeySet;

    /**
     * Constructor for a completely new keySetHash.
     *
     * @param pFactory the factory
     */
    private GordianCoreKeySetHash(final GordianCoreFactory pFactory) {
        /* Store the factory */
        theFactory = pFactory;

        /* Create a random HashRecipe */
        theRecipe = new GordianKeySetHashRecipe(theFactory);
    }

    /**
     * Constructor for a password hash whose hash is known.
     *
     * @param pFactory   the factory
     * @param pHashBytes the Hash bytes
     * @param pPassLen   the password length
     */
    private GordianCoreKeySetHash(final GordianCoreFactory pFactory,
                                  final byte[] pHashBytes,
                                  final int pPassLen) {
        /* Store the factory */
        theFactory = pFactory;

        /* Store the hash bytes and extract the mode */
        theHash = Arrays.copyOf(pHashBytes, pHashBytes.length);

        /* Parse the hash */
        theRecipe = new GordianKeySetHashRecipe(theFactory, pPassLen, pHashBytes);
    }

    /**
     * Create a new keySetHash for password.
     *
     * @param pFactory  the factory
     * @param pPassword the password
     * @return the new keySetHash
     * @throws OceanusException on error
     */
    static GordianKeySetHash newKeySetHash(final GordianCoreFactory pFactory,
                                           final char[] pPassword) throws OceanusException {
        /* Protect against exceptions */
        byte[] myPassword = null;
        try {
            /* Access bytes of password */
            myPassword = TethysDataConverter.charsToByteArray(pPassword);
            return newKeySetHash(pFactory, myPassword);

            /* Ensure inetrmediate password is reset */
        } finally {
            if (myPassword != null) {
                Arrays.fill(myPassword, (byte) 0);
            }
        }
    }

    /**
     * Create a new keySetHash for password.
     *
     * @param pFactory  the factory
     * @param pPassword the password
     * @return the new keySetHash
     * @throws OceanusException on error
     */
    private static GordianKeySetHash newKeySetHash(final GordianFactory pFactory,
                                                   final byte[] pPassword) throws OceanusException {
        /* Create a new keySetHash */
        final GordianCoreKeySetHash myHash = new GordianCoreKeySetHash((GordianCoreFactory) pFactory);

        /* Build hash from password */
        myHash.setPassword(pPassword);
        return myHash;
    }

    /**
     * resolve a keySetHash with password.
     *
     * @param pFactory  the factory
     * @param pHash     the hash
     * @param pPassword the password
     * @return the resolved keySetHash
     * @throws GordianBadCredentialsException if wrong password is given
     * @throws OceanusException               on error
     */
    static GordianKeySetHash resolveKeySetHash(final GordianCoreFactory pFactory,
                                               final byte[] pHash,
                                               final char[] pPassword) throws OceanusException {
        /* Protect against exceptions */
        byte[] myPassword = null;
        try {
            /* Access bytes of password */
            myPassword = TethysDataConverter.charsToByteArray(pPassword);
            return resolveKeySetHash(pFactory, pHash, myPassword);

            /* Ensure intermediate password is reset */
        } finally {
            if (myPassword != null) {
                Arrays.fill(myPassword, (byte) 0);
            }
        }
    }

    /**
     * resolve a keySetHash with password.
     *
     * @param pFactory  the factory
     * @param pHash     the hash
     * @param pPassword the password
     * @return the new keySetHash
     * @throws GordianBadCredentialsException if wrong password is given
     * @throws OceanusException               on error
     */
    private static GordianKeySetHash resolveKeySetHash(final GordianCoreFactory pFactory,
                                                       final byte[] pHash,
                                                       final byte[] pPassword) throws OceanusException {
        /* Create a new keySetHash */
        final GordianCoreKeySetHash myHash = new GordianCoreKeySetHash(pFactory, pHash, pPassword.length);

        /* Resolve hash with password */
        myHash.attemptPassword(pPassword);
        return myHash;
    }

    @Override
    public byte[] getHash() {
        return Arrays.copyOf(theHash, theHash.length);
    }

    @Override
    public GordianCoreKeySet getKeySet() {
        return theKeySet;
    }

    /**
     * Get Factory.
     *
     * @return the Factory
     */
    private GordianFactory getFactory() {
        return theFactory;
    }

    @Override
    public GordianCoreKeySetHash similarHash() throws OceanusException {
        /* Protect against exceptions */
        byte[] myPassword = null;
        try {
            /* Create a new keySetHash */
            final GordianCoreKeySetHash myHash = new GordianCoreKeySetHash(theFactory);

            /* Access the original password */
            myPassword = theKeySet.decryptBytes(thePassword);

            /* Build hash from password */
            myHash.setPassword(myPassword);
            return myHash;

            /* Ensure password is reset */
        } finally {
            if (myPassword != null) {
                Arrays.fill(myPassword, (byte) 0);
            }
        }
    }

    @Override
    public GordianCoreKeySetHash childHash() throws OceanusException {
        /* Protect against exceptions */
        byte[] myPassword = null;
        try {
            /* Create a new keySetHash */
            final GordianCoreKeySetHash myHash = new GordianCoreKeySetHash(theFactory);

            /* Access the child password */
            myPassword = theKeySet.decryptBytes(theChildPassword);

            /* Build hash from password */
            myHash.setPassword(myPassword);
            return myHash;

            /* Ensure password is reset */
        } finally {
            if (myPassword != null) {
                Arrays.fill(myPassword, (byte) 0);
            }
        }
    }

    @Override
    public GordianKeySetHash resolveChildHash(final byte[] pHash) throws OceanusException {
        /* Protect against exceptions */
        byte[] myPassword = null;
        try {
            /* Access the child password */
            myPassword = theKeySet.decryptBytes(theChildPassword);

            /* Resolve hash */
            return resolveKeySetHash(theFactory, pHash, myPassword);

            /* Ensure password is reset */
        } finally {
            if (myPassword != null) {
                Arrays.fill(myPassword, (byte) 0);
            }
        }
    }

    /**
     * Build the password hash from the password.
     *
     * @param pPassword the password
     * @throws OceanusException on error
     */
    private void setPassword(final byte[] pPassword) throws OceanusException {
        /* Protect against exceptions */
        byte[][] myResults = null;
        try {
            /* Generate the Hash */
            myResults = generateHash(pPassword);
            int iIndex = 0;
            theHash = myResults[iIndex++];

            /* Create the Key Set */
            theKeySet = new GordianCoreKeySet(theFactory);
            theKeySet.buildFromSecret(myResults[iIndex++], myResults[iIndex++]);

            /* Encrypt the passwords */
            thePassword = theKeySet.encryptBytes(pPassword);
            theChildPassword = theKeySet.encryptBytes(myResults[iIndex]);

        } finally {
            /* Clear out results */
            if (myResults != null) {
                int iIndex = 1;
                Arrays.fill(myResults[iIndex++], (byte) 0);
                Arrays.fill(myResults[iIndex++], (byte) 0);
                Arrays.fill(myResults[iIndex], (byte) 0);
            }
        }
    }

    /**
     * Attempt to match the password hash with the password.
     *
     * @param pPassword the password
     * @throws GordianBadCredentialsException on wrong password
     * @throws OceanusException               on error
     */
    private void attemptPassword(final byte[] pPassword) throws OceanusException {
        /* Protect against exceptions */
        byte[][] myResults = null;
        try {
            /* Generate the HashBytes */
            myResults = generateHash(pPassword);

            /* Check that the arrays match */
            int iIndex = 0;
            if (!Arrays.equals(theHash, myResults[iIndex++])) {
                /* Fail the password attempt */
                throw new GordianBadCredentialsException("Invalid Password");
            }

            /* Create the Key Set */
            theKeySet = new GordianCoreKeySet(theFactory);
            theKeySet.buildFromSecret(myResults[iIndex++], myResults[iIndex++]);

            /* Encrypt the passwords*/
            thePassword = theKeySet.encryptBytes(pPassword);
            theChildPassword = theKeySet.encryptBytes(myResults[iIndex]);

        } finally {
            /* Clear out results */
            if (myResults != null) {
                int iIndex = 1;
                Arrays.fill(myResults[iIndex++], (byte) 0);
                Arrays.fill(myResults[iIndex++], (byte) 0);
                Arrays.fill(myResults[iIndex], (byte) 0);
            }
        }
    }

    /**
     * Generate Hash.
     *
     * @param pPassword the password for the keys
     * @return the Salt and Hash array
     * @throws OceanusException on error
     */
    private byte[][] generateHash(final byte[] pPassword) throws OceanusException {
        /* Obtain configuration details */
        final GordianCoreKeySetFactory myFactory = (GordianCoreKeySetFactory) theFactory.getKeySetFactory();
        final GordianPersonalisation myPersonal = myFactory.getPersonalisation();
        final int iIterations = theFactory.getNumIterations();
        final int iFinal = theRecipe.getAdjustment()
                + iIterations;

        /* Create a byte array of the iterations */
        final byte[] myLoops = TethysDataConverter.integerToByteArray(iFinal);

        /* Access factories */
        final GordianDigestFactory myDigests = theFactory.getDigestFactory();
        final GordianMacFactory myMacs = theFactory.getMacFactory();

        /* Create the primeMac */
        GordianMacSpec myMacSpec = GordianMacSpec.hMac(theRecipe.getPrimeDigest());
        final GordianMac myPrimeMac = myMacs.createMac(myMacSpec);
        myPrimeMac.initMac(pPassword);

        /* Create the alternateMac */
        myMacSpec = GordianMacSpec.hMac(theRecipe.getAlternateDigest());
        final GordianMac myAlternateMac = myMacs.createMac(myMacSpec);
        myAlternateMac.initMac(pPassword);

        /* Create the secretMac */
        myMacSpec = GordianMacSpec.hMac(theRecipe.getSecretDigest());
        final GordianMac mySecretMac = myMacs.createMac(myMacSpec);
        mySecretMac.initMac(pPassword);

        /* Initialise hash bytes and counter */
        final byte[] myPrimeBytes = new byte[myPrimeMac.getMacSize()];
        final byte[] myAlternateBytes = new byte[myAlternateMac.getMacSize()];
        final byte[] mySecretBytes = new byte[mySecretMac.getMacSize()];
        final byte[] myPrimeHash = new byte[myPrimeMac.getMacSize()];
        final byte[] myAlternateHash = new byte[myAlternateMac.getMacSize()];
        final byte[] mySecretHash = new byte[mySecretMac.getMacSize()];

        /* Access final digest */
        final GordianDigestSpec myDigestSpec = new GordianDigestSpec(theRecipe.getExternalDigest(), GordianLength.LEN_512);
        final GordianDigest myDigest = myDigests.createDigest(myDigestSpec);

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
        myDigest.update(mySecretHash);
        myDigest.update(myPrimeBytes);
        myDigest.update(myAlternateBytes);
        myDigest.update(mySecretBytes);
        final byte[] myChildPassword = myDigest.finish();

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
                {myHashBytes, mySecretBytes, myInitVector, myChildPassword};
    }

    /**
     * Attempt the cached password against the passed hash.
     *
     * @param pHashBytes the Hash to test against
     * @return the new PasswordHash if successful, otherwise null
     */
    public GordianKeySetHash attemptPasswordForHash(final byte[] pHashBytes) {
        /* Protect against exceptions */
        byte[] myPassword = null;
        try {
            /* Access the original password */
            myPassword = theKeySet.decryptBytes(thePassword);

            /* Try to resolve the hash and return it */
            return resolveKeySetHash(theFactory, pHashBytes, myPassword);

            /* Catch Exceptions */
        } catch (OceanusException e) {
            LOGGER.error("Password attempt failed", e);
            return null;
        } catch (GordianBadCredentialsException e) {
            return null;
        } finally {
            /* Clear out password */
            if (myPassword != null) {
                Arrays.fill(myPassword, (byte) 0);
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
        if (!(pThat instanceof GordianCoreKeySetHash)) {
            return false;
        }

        /* Access the target field */
        final GordianCoreKeySetHash myThat = (GordianCoreKeySetHash) pThat;

        /* Check differences */
        return theFactory.equals(myThat.getFactory())
                && Arrays.equals(theHash, myThat.getHash());
    }

    @Override
    public int hashCode() {
        return GordianParameters.HASH_PRIME * theFactory.hashCode()
                + Arrays.hashCode(theHash);
    }
}
