/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.lock;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianBadCredentialsException;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.api.lock.GordianPasswordLockSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIdManager;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianPersonalisation;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianPersonalisation.GordianPersonalId;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySet;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySetFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Class for assembling/disassembling PasswordLocks.
 */
public final class GordianPasswordLockRecipe {
    /**
     * Number of digests.
     */
    private static final int NUM_DIGESTS = 3;

    /**
     * Recipe length (Integer).
     */
    private static final int RECIPELEN = Integer.BYTES;

    /**
     * Salt length.
     */
    private static final int SALTLEN = GordianLength.LEN_256.getByteLength();

    /**
     * Hash Length.
     */
    private static final int HASHLEN = GordianLength.LEN_512.getByteLength();

    /**
     * HashSize.
     */
    static final int HASHSIZE = RECIPELEN + SALTLEN + HASHLEN;

    /**
     * Hash margins.
     */
    private static final int HASH_MARGIN = 4;

    /**
     * The PasswordLockSpec.
     */
    private final GordianPasswordLockSpec theLockSpec;

    /**
     * The Recipe.
     */
    private final byte[] theRecipe;

    /**
     * The Salt.
     */
    private final byte[] theSalt;

    /**
     * The Initialisation Vector.
     */
    private final byte[] theInitVector;

    /**
     * The Hash.
     */
    private byte[] theHashBytes;

    /**
     * The Payload.
     */
    private final byte[] thePayload;

    /**
     * The Lock Parameters.
     */
    private final GordianPasswordLockParams theParams;

    /**
     * Constructor for random choices.
     * @param pFactory the factory
     * @param pLockSpec the passwordLockSpec
     */
    GordianPasswordLockRecipe(final GordianCoreFactory pFactory,
                              final GordianPasswordLockSpec pLockSpec) {
        /* Access the secureRandom */
        final SecureRandom myRandom = pFactory.getRandomSource().getRandom();

        /* Create the Salt vector */
        theSalt = new byte[SALTLEN];
        myRandom.nextBytes(theSalt);

        /* Calculate the initVector */
        final GordianPersonalisation myPersonal = pFactory.getPersonalisation();
        theInitVector = myPersonal.adjustIV(theSalt);

        /* Allocate new set of parameters */
        theParams = new GordianPasswordLockParams(pFactory);
        theRecipe = theParams.getRecipe();
        theLockSpec = pLockSpec;
        theHashBytes = null;
        thePayload = null;
    }

    /**
     * Constructor for external form parse.
     * @param pFactory the factory
     * @param pPassLength the password length
     * @param pLockASN1 the lockASN1
     */
    GordianPasswordLockRecipe(final GordianCoreFactory pFactory,
                              final int pPassLength,
                              final GordianPasswordLockASN1 pLockASN1)  {
        /* Parse the ASN1 external form */
        final byte[] myHashBytes = pLockASN1.getHashBytes();
        theLockSpec = pLockASN1.getLockSpec();
        thePayload = pLockASN1.getPayload();

        /* Create the byte arrays */
        theRecipe = new byte[RECIPELEN];
        theSalt = new byte[SALTLEN];
        theHashBytes = new byte[HASHLEN];

        /* Determine offset position */
        int myOffSet = Math.max(pPassLength, HASH_MARGIN);
        myOffSet = Math.min(myOffSet, HASHLEN
                - HASH_MARGIN);

        /* Copy Data into buffers */
        System.arraycopy(myHashBytes, 0, theHashBytes, 0, myOffSet);
        System.arraycopy(myHashBytes, myOffSet, theRecipe, 0, RECIPELEN);
        System.arraycopy(myHashBytes, myOffSet
                + RECIPELEN, theSalt, 0, SALTLEN);
        System.arraycopy(myHashBytes, myOffSet
                + RECIPELEN
                + SALTLEN, theHashBytes, myOffSet, HASHLEN
                - myOffSet);

        /* Calculate the initVector */
        final GordianPersonalisation myPersonal = pFactory.getPersonalisation();
        theInitVector = myPersonal.adjustIV(theSalt);

        /* Allocate new set of parameters */
        theParams = new GordianPasswordLockParams(pFactory, theRecipe);
    }

    /**
     * Obtain the payload.
     * @return the payLoad
     */
    byte[] getPayload() {
        return thePayload;
    }

    /**
     * Build lockBytes for hash and password length.
     * @param pPassLength the password length
     * @param pPayload the payload
     * @return the lockBytes
     */
    GordianPasswordLockASN1 buildLockASN1(final int pPassLength,
                                          final byte[] pPayload) {
        /* Allocate the new buffer */
        final int myHashLen = theHashBytes.length;
        final int myLen = RECIPELEN
                + SALTLEN
                + myHashLen;
        final byte[] myBuffer = new byte[myLen];

        /* Determine offset position */
        int myOffSet = Math.max(pPassLength, HASH_MARGIN);
        myOffSet = Math.min(myOffSet, myHashLen
                - HASH_MARGIN);

        /* Copy Data into buffer */
        System.arraycopy(theHashBytes, 0, myBuffer, 0, myOffSet);
        System.arraycopy(theRecipe, 0, myBuffer, myOffSet, RECIPELEN);
        System.arraycopy(theSalt, 0, myBuffer, myOffSet
                + RECIPELEN, SALTLEN);
        System.arraycopy(theHashBytes, myOffSet, myBuffer, myOffSet
                + RECIPELEN
                + SALTLEN, myHashLen
                - myOffSet);

        /* Build the ASN1 form */
        return new GordianPasswordLockASN1(theLockSpec, myBuffer, pPayload);
    }

    /**
     * Process password.
     *
     * @param pFactory the factory
     * @param pPassword the password for the keys
     * @return the locking KeySet
     * @throws OceanusException on error
     */
    GordianCoreKeySet processPassword(final GordianCoreFactory pFactory,
                                      final byte[] pPassword) throws OceanusException {
        /* Obtain configuration details */
        final GordianPersonalisation myPersonal = pFactory.getPersonalisation();
        final int iIterations = theLockSpec.getNumIterations();
        final int iFinal = theParams.getAdjustment()
                + iIterations;

        /* Create a byte array of the iterations */
        final byte[] myLoops = TethysDataConverter.integerToByteArray(iFinal);

        /* Access factories */
        final GordianDigestFactory myDigests = pFactory.getDigestFactory();
        final GordianMacFactory myMacs = pFactory.getMacFactory();

        /* Create the primeMac */
        GordianMacSpec myMacSpec = GordianMacSpec.hMac(theParams.getPrimeDigest());
        final GordianMac myPrimeMac = myMacs.createMac(myMacSpec);
        myPrimeMac.initKeyBytes(pPassword);

        /* Create the alternateMac */
        myMacSpec = GordianMacSpec.hMac(theParams.getSecondaryDigest());
        final GordianMac mySecondaryMac = myMacs.createMac(myMacSpec);
        mySecondaryMac.initKeyBytes(pPassword);

        /* Create the alternateMac */
        myMacSpec = GordianMacSpec.hMac(theParams.getTertiaryDigest());
        final GordianMac myTertiaryMac = myMacs.createMac(myMacSpec);
        myTertiaryMac.initKeyBytes(pPassword);

        /* Create the secretMac */
        myMacSpec = GordianMacSpec.hMac(new GordianDigestSpec(theParams.getSecretDigest(), GordianLength.LEN_512));
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
        final GordianDigestSpec myDigestSpec = new GordianDigestSpec(theParams.getExternalDigest(), GordianLength.LEN_512);
        final GordianDigest myDigest = myDigests.createDigest(myDigestSpec);

        /* Initialise the hash input values as the salt bytes */
        final byte[] mySaltBytes = theInitVector;
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
            final byte[] myHashBytes = myDigest.finish();

            /* If we are resolving the lock, check the hash */
            if (theHashBytes != null
                    && !Arrays.equals(theHashBytes, myHashBytes)) {
                /* Fail the password attempt */
                throw new GordianBadCredentialsException("Invalid Password");
            }
            theHashBytes = myHashBytes;

            /* Create the Key Set */
            final GordianCoreKeySet myKeySet = ((GordianCoreKeySetFactory) pFactory.getKeySetFactory()).createKeySet(theLockSpec.getKeySetSpec());
            myKeySet.buildFromSecret(mySecretBytes);

            /* Return to caller */
            return myKeySet;

            /* Clear intermediate arrays */
        } finally {
            Arrays.fill(myPrimeHash, (byte) 0);
            Arrays.fill(myPrimeBytes, (byte) 0);
            Arrays.fill(mySecondaryHash, (byte) 0);
            Arrays.fill(mySecondaryBytes, (byte) 0);
            Arrays.fill(myTertiaryHash, (byte) 0);
            Arrays.fill(myTertiaryBytes, (byte) 0);
            Arrays.fill(mySecretHash, (byte) 0);
            Arrays.fill(mySecretBytes, (byte) 0);
        }
    }

    /**
     * The parameters class.
     */
    private static final class GordianPasswordLockParams {
        /**
         * The Recipe.
         */
        private final byte[] theRecipe;

        /**
         * The secret hMac type.
         */
        private final GordianDigestType theSecretDigest;

        /**
         * The hMac types.
         */
        private final GordianDigestType[] theDigests;

        /**
         * The external Digest type.
         */
        private final GordianDigestType theExternalDigest;

        /**
         * The Adjustment.
         */
        private final int theAdjust;

        /**
         * Construct the parameters from random.
         * @param pFactory the factory
         */
        GordianPasswordLockParams(final GordianCoreFactory pFactory) {
            /* Obtain Id manager and random */
            final GordianIdManager myManager = pFactory.getIdManager();
            final GordianPersonalisation myPersonal = pFactory.getPersonalisation();
            final SecureRandom myRandom = pFactory.getRandomSource().getRandom();

            /* Generate recipe and derive digestTypes */
            final int mySeed = myRandom.nextInt();
            theRecipe = TethysDataConverter.integerToByteArray(mySeed);
            final Random mySeededRandom = myPersonal.getSeededRandom(GordianPersonalId.HASHRANDOM, theRecipe);
            theSecretDigest = myManager.deriveKeyHashSecretTypeFromSeed(mySeededRandom);
            theDigests = myManager.deriveKeyHashDigestTypesFromSeed(mySeededRandom, NUM_DIGESTS);
            theExternalDigest = myManager.deriveExternalDigestTypeFromSeed(mySeededRandom);

            /* Derive random adjustment value */
            theAdjust = mySeededRandom.nextInt(TethysDataConverter.NYBBLE_MASK + 1);
        }

        /**
         * Construct the parameters from recipe.
         * @param pFactory the factory
         * @param pRecipe the recipe bytes
         */
        GordianPasswordLockParams(final GordianCoreFactory pFactory,
                                  final byte[] pRecipe) {
            /* Obtain Id manager */
            final GordianIdManager myManager = pFactory.getIdManager();
            final GordianPersonalisation myPersonal = pFactory.getPersonalisation();

            /* Store recipe and derive digestTypes */
            theRecipe = pRecipe;
            final Random mySeededRandom = myPersonal.getSeededRandom(GordianPersonalId.HASHRANDOM, theRecipe);
            theSecretDigest = myManager.deriveKeyHashSecretTypeFromSeed(mySeededRandom);
            theDigests = myManager.deriveKeyHashDigestTypesFromSeed(mySeededRandom, NUM_DIGESTS);
            theExternalDigest = myManager.deriveExternalDigestTypeFromSeed(mySeededRandom);

            /* Derive random adjustment value */
            theAdjust = mySeededRandom.nextInt(TethysDataConverter.NYBBLE_MASK + 1);
        }

        /**
         * Obtain the Recipe.
         * @return the recipe
         */
        byte[] getRecipe() {
            return theRecipe;
        }

        /**
         * Obtain the Prime Digest type.
         * @return the digest type
         */
        GordianDigestType getPrimeDigest() {
            return theDigests[0];
        }

        /**
         * Obtain the Secondary Digest type.
         * @return the digest type
         */
        GordianDigestType getSecondaryDigest() {
            return theDigests[1];
        }

        /**
         * Obtain the Tertiary Digest type.
         * @return the digest type
         */
        GordianDigestType getTertiaryDigest() {
            return theDigests[2];
        }

        /**
         * Obtain the Secret Digest type.
         * @return the digest type
         */
        GordianDigestType getSecretDigest() {
            return theSecretDigest;
        }

        /**
         * Obtain the external Digest type.
         * @return the digest type
         */
        GordianDigestType getExternalDigest() {
            return theExternalDigest;
        }

        /**
         * Obtain the Adjustment.
         * @return the adjustment
         */
        int getAdjustment() {
            return theAdjust;
        }
    }
}
