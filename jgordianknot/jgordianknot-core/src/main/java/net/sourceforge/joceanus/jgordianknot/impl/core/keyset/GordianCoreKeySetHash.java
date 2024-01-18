/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2023 Tony Washer
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
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianBadCredentialsException;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianPersonalisation;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

import java.util.Arrays;

/**
 * Hash from which to derive KeySet.
 */
public final class GordianCoreKeySetHash
    implements GordianKeySetHash {
    /**
     * Hash Recipe.
     */
    private final GordianKeySetHashRecipe theRecipe;

    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * keySetHashSpec.
     */
    private final GordianKeySetHashSpec theSpec;

    /**
     * The Hash.
     */
    private byte[] theHash;

    /**
     * CipherSet.
     */
    private GordianCoreKeySet theKeySet;

    /**
     * Constructor for a completely new keySetHash.
     *
     * @param pFactory the factory
     * @param pSpec the keySetHashSpec
     */
    private GordianCoreKeySetHash(final GordianCoreFactory pFactory,
                                  final GordianKeySetHashSpec pSpec) {
        /* Store the factory */
        theFactory = pFactory;
        theSpec = pSpec;

        /* Create a random HashRecipe */
        theRecipe = new GordianKeySetHashRecipe(theFactory, pSpec);
    }

    /**
     * Constructor for a password hash whose hash is known.
     *
     * @param pFactory   the factory
     * @param pHashBytes the Hash bytes
     * @param pPassLen   the password length
     * @throws OceanusException on error
     */
    private GordianCoreKeySetHash(final GordianCoreFactory pFactory,
                                  final byte[] pHashBytes,
                                  final int pPassLen) throws OceanusException {
        /* Store the factory */
        theFactory = pFactory;

        /* Store the hash bytes and extract the mode */
        theHash = Arrays.copyOf(pHashBytes, pHashBytes.length);

        /* Parse the hash */
        theRecipe = new GordianKeySetHashRecipe(theFactory, pPassLen, pHashBytes);
        theSpec = theRecipe.getSpec();
    }

    /**
     * Create a new keySetHash for password.
     *
     * @param pFactory  the factory
     * @param pSpec the keySetSpec
     * @param pPassword the password
     * @return the new keySetHash
     * @throws OceanusException on error
     */
    static GordianKeySetHash newKeySetHash(final GordianCoreFactory pFactory,
                                           final GordianKeySetHashSpec pSpec,
                                           final char[] pPassword) throws OceanusException {
        /* Protect against exceptions */
        byte[] myPassword = null;
        try {
            /* Access bytes of password */
            myPassword = TethysDataConverter.charsToByteArray(pPassword);
            return newKeySetHash(pFactory, pSpec, myPassword);

            /* Ensure intermediate password is reset */
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
     * @param pSpec the keySetHashSpec
     * @param pPassword the password
     * @return the new keySetHash
     * @throws OceanusException on error
     */
    static GordianKeySetHash newKeySetHash(final GordianFactory pFactory,
                                           final GordianKeySetHashSpec pSpec,
                                           final byte[] pPassword) throws OceanusException {
        /* Create a new keySetHash */
        final GordianCoreKeySetHash myHash = new GordianCoreKeySetHash((GordianCoreFactory) pFactory, pSpec);

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
     * @throws OceanusException on error
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
     * @throws OceanusException on error
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
    public GordianFactory getFactory() {
        return theFactory;
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
            theKeySet = new GordianCoreKeySet(theFactory, theSpec.getKeySetSpec());
            theKeySet.buildFromSecret(myResults[iIndex]);

        } finally {
            /* Clear out results */
            if (myResults != null) {
                Arrays.fill(myResults[1], (byte) 0);
            }
        }
    }

    /**
     * Attempt to match the password hash with the password.
     *
     * @param pPassword the password
     * @throws OceanusException on error
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
            theKeySet = new GordianCoreKeySet(theFactory, theSpec.getKeySetSpec());
            theKeySet.buildFromSecret(myResults[iIndex]);

        } finally {
            /* Clear out results */
            if (myResults != null) {
                int iIndex = 1;
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
        final GordianPersonalisation myPersonal = theFactory.getPersonalisation();
        final int iIterations = theRecipe.getSpec().getNumIterations();
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
        myPrimeMac.initKeyBytes(pPassword);

        /* Create the alternateMac */
        myMacSpec = GordianMacSpec.hMac(theRecipe.getSecondaryDigest());
        final GordianMac mySecondaryMac = myMacs.createMac(myMacSpec);
        mySecondaryMac.initKeyBytes(pPassword);

        /* Create the alternateMac */
        myMacSpec = GordianMacSpec.hMac(theRecipe.getTertiaryDigest());
        final GordianMac myTertiaryMac = myMacs.createMac(myMacSpec);
        myTertiaryMac.initKeyBytes(pPassword);

        /* Create the secretMac */
        myMacSpec = GordianMacSpec.hMac(new GordianDigestSpec(theRecipe.getSecretDigest(), GordianLength.LEN_512));
        final GordianMac mySecretMac = myMacs.createMac(myMacSpec);
        mySecretMac.initKeyBytes(pPassword);

        /* Initialise hash bytes and counter */
        final byte[] myPrimeBytes = new byte[myPrimeMac.getMacSize()];
        final byte[] mySecondaryBytes = new byte[mySecondaryMac.getMacSize()];
        final byte[] myTertiaryBytes = new byte[myTertiaryMac.getMacSize()];
        final byte[] mySecretBytes = new byte[mySecretMac.getMacSize()];
        final byte[] myPrimeHash = new byte[myPrimeMac.getMacSize()];
        final byte[] mySecondaryHash = new byte[mySecondaryMac.getMacSize()];
        final byte[] myTertiaryHash = new byte[myTertiaryMac.getMacSize()];
        final byte[] mySecretHash = new byte[mySecretMac.getMacSize()];

        /* Access final digest */
        final GordianDigestSpec myDigestSpec = new GordianDigestSpec(theRecipe.getExternalDigest(), GordianLength.LEN_512);
        final GordianDigest myDigest = myDigests.createDigest(myDigestSpec);

        /* Initialise the hash input values as the salt bytes */
        final byte[] mySaltBytes = theRecipe.getInitVector();
        byte[] myPrimeInput = mySaltBytes;
        byte[] mySecondaryInput = mySaltBytes;
        byte[] myTertiaryInput = mySaltBytes;
        byte[] mySecretInput = mySaltBytes;

        /* Protect from exceptions */
        try {
            /* Update each Hash with the personalisation */
            myPersonal.updateMac(myPrimeMac);
            myPersonal.updateMac(mySecondaryMac);
            myPersonal.updateMac(myTertiaryMac);
            myPersonal.updateMac(mySecretMac);

            /* Update each Hash with the loops */
            myPrimeMac.update(myLoops);
            mySecondaryMac.update(myLoops);
            myTertiaryMac.update(myLoops);
            mySecretMac.update(myLoops);

            /* Loop through the iterations */
            for (int iPass = 0; iPass < iFinal; iPass++) {
                /* Update the prime Mac */
                myPrimeMac.update(mySecondaryInput);
                myPrimeMac.update(myTertiaryInput);

                /* Update the secondary Mac */
                mySecondaryMac.update(myPrimeInput);
                mySecondaryMac.update(myTertiaryInput);

                /* Update the tertiary Mac */
                myTertiaryMac.update(myPrimeInput);
                myTertiaryMac.update(mySecondaryInput);

                /* Update the secret Mac */
                mySecretMac.update(mySecretInput);
                mySecretMac.update(myPrimeInput);
                mySecretMac.update(mySecondaryInput);
                mySecretMac.update(myTertiaryInput);

                /* Update inputs */
                myPrimeInput = myPrimeHash;
                mySecondaryInput = mySecondaryHash;
                myTertiaryInput = myTertiaryHash;
                mySecretInput = mySecretHash;

                /* Recalculate hashes and combine them */
                myPrimeMac.finish(myPrimeHash, 0);
                GordianPersonalisation.buildHashResult(myPrimeBytes, myPrimeHash);
                mySecondaryMac.finish(mySecondaryHash, 0);
                GordianPersonalisation.buildHashResult(mySecondaryBytes, mySecondaryHash);
                myTertiaryMac.finish(myTertiaryHash, 0);
                GordianPersonalisation.buildHashResult(myTertiaryBytes, myTertiaryHash);
                mySecretMac.finish(mySecretHash, 0);
                GordianPersonalisation.buildHashResult(mySecretBytes, mySecretHash);
            }

            /* Combine the Primary, Secondary and Tertiary bytes to form the external hash */
            myDigest.update(myPrimeBytes);
            myDigest.update(mySecondaryBytes);
            myDigest.update(myTertiaryBytes);
            final byte[] myExternalHash = myDigest.finish();

            /* Create the external hash */
            final byte[] myHashBytes = theRecipe.buildExternal(pPassword.length, myExternalHash);

            /* Return to caller */
            return new byte[][]
                    {myHashBytes, mySecretBytes};

            /* Clear intermediate arrays */
        } finally {
            Arrays.fill(myPrimeHash, (byte) 0);
            Arrays.fill(myPrimeBytes, (byte) 0);
            Arrays.fill(mySecondaryHash, (byte) 0);
            Arrays.fill(mySecondaryBytes, (byte) 0);
            Arrays.fill(myTertiaryHash, (byte) 0);
            Arrays.fill(myTertiaryBytes, (byte) 0);
            Arrays.fill(mySecretHash, (byte) 0);
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
        return GordianCoreFactory.HASH_PRIME * theFactory.hashCode()
                + Arrays.hashCode(theHash);
    }
}
