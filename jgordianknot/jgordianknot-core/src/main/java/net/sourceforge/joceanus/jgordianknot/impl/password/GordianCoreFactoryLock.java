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
package net.sourceforge.joceanus.jgordianknot.impl.password;

import java.security.SecureRandom;
import java.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianBadCredentialsException;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianFactoryLock;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianFactoryGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianPersonalisation;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySet;
import net.sourceforge.joceanus.jgordianknot.impl.password.GordianBuilder.GordianUtilGenerator;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Factory Lock implementation.
 */
public class GordianCoreFactoryLock
    implements GordianFactoryLock {
    /**
     * The number of iterations.
     */
    private static final int NUM_ITERATIONS = 4096;

    /**
     * The initVector length.
     */
    private static final GordianLength INITVECTOR_LEN = GordianLength.LEN_128;

    /**
     * The seed length.
     */
    private static final GordianLength SEED_LEN = GordianParameters.SECRET_LEN;

    /**
     * The external length.
     */
    private static final int EXTERNAL_LEN = INITVECTOR_LEN.getByteLength() + 3 * SEED_LEN.getByteLength();

    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The initVector.
     */
    private final byte[] theInitVector;

    /**
     * The lock.
     */
    private final byte[] theLock;

    /**
     * Locking constructor.
     * @param pFactory the factory to lock
     * @param pPassword the password
     * @throws OceanusException on error
     */
    public GordianCoreFactoryLock(final GordianCoreFactory pFactory,
                                  final char[] pPassword) throws OceanusException {
        /* Protect from exceptions */
        byte[] myPassword = null;
        try {
            /* Store the Factory */
            theFactory = pFactory;

            /* Reject the operation if not a random factory */
            if (!pFactory.isRandom()) {
                throw new GordianDataException("attempt to lock non-Random factory");
            }

            /* Create an initVector */
            final SecureRandom myRandom = pFactory.getRandomSource().getRandom();
            theInitVector = new byte[INITVECTOR_LEN.getByteLength()];
            myRandom.nextBytes(theInitVector);

            /* Generate the hash */
            myPassword = TethysDataConverter.charsToByteArray(pPassword);
            final byte[][] myResults = processPassword(pFactory, myPassword);
            theLock = createLock(myResults);

        } finally {
            if (myPassword != null) {
                Arrays.fill(myPassword, (byte) 0);
            }
        }
    }

    /**
     * UnLocking constructor.
     * @param pLock the external hash
     * @param pFactory the factory to lock
     * @param pPassword the password
     * @throws OceanusException on error
     */
    public GordianCoreFactoryLock(final GordianCoreFactory pFactory,
                                  final byte[] pLock,
                                  final char[] pPassword) throws OceanusException {
        /* Protect from exceptions */
        byte[] myPassword = null;
        try {
            /* Store the Lock */
            theLock = pLock;

            /* Reject the operation if not a random factory */
            if (pLock.length != EXTERNAL_LEN) {
                throw new GordianDataException("invalid locked factory");
            }

            /* Access component parts */
            theInitVector = Arrays.copyOf(pLock, INITVECTOR_LEN.getByteLength());

            /* Generate the hash and create parameters */
            myPassword = TethysDataConverter.charsToByteArray(pPassword);
            final byte[][] myResults = processPassword(pFactory, myPassword);
            final GordianParameters myParameters = buildParameters(myResults);
            final GordianFactoryGenerator myGenerator = new GordianUtilGenerator();
            theFactory = (GordianCoreFactory) myGenerator.newFactory(myParameters);

        } finally {
            if (myPassword != null) {
                Arrays.fill(myPassword, (byte) 0);
            }
        }
    }

    @Override
    public GordianFactory getLockedObject() {
        return theFactory;
    }

    @Override
    public byte[] getLockBytes() {
        return theLock;
    }

    /**
     * Create the lock.
     * @param pResults the processed results
     * @return the external hash
     * @throws OceanusException on error
     */
    private byte[] createLock(final byte[][] pResults) throws OceanusException {
        try {
            /* Access lengths */
            final int myInitLen = INITVECTOR_LEN.getByteLength();
            final int mySeedLen = SEED_LEN.getByteLength();

            /* Access buffers */
            final GordianParameters myParams = theFactory.getParameters();
            final byte[] myBuffer = new byte[EXTERNAL_LEN];
            final byte[] mySecSeed = Arrays.copyOf(myParams.getSecuritySeed(), mySeedLen);
            final byte[] myKeySetSeed = Arrays.copyOf(myParams.getKeySetSeed(), mySeedLen);

            /* Encode the seeds */
            GordianPersonalisation.buildHashResult(mySecSeed, pResults[1]);
            GordianPersonalisation.buildHashResult(myKeySetSeed, pResults[1]);

            /* Build buffer and return it */
            System.arraycopy(theInitVector, 0, myBuffer, 0, myInitLen);
            System.arraycopy(pResults[0], 0, myBuffer, myInitLen, mySeedLen);
            System.arraycopy(mySecSeed, 0, myBuffer, myInitLen + mySeedLen, mySeedLen);
            System.arraycopy(myKeySetSeed, 0, myBuffer, myInitLen + mySeedLen + mySeedLen, mySeedLen);
            return myBuffer;

        } finally {
            Arrays.fill(pResults[0], (byte) 0);
            Arrays.fill(pResults[1], (byte) 0);
        }
    }

    /**
     * Build the parameters.
     * @param pResults the processed results
     * @return the parameters
     * @throws OceanusException on error
     */
    private GordianParameters buildParameters(final byte[][] pResults) throws OceanusException {
        try {
            /* Access lengths */
            final int myInitLen = INITVECTOR_LEN.getByteLength();
            final int mySeedLen = SEED_LEN.getByteLength();

            /* Access buffers */
            final byte[] myHash = new byte[mySeedLen];
            final byte[] mySecSeed = new byte[mySeedLen];
            final byte[] myKeySetSeed = new byte[mySeedLen];

            /* Extract from buffer */
            System.arraycopy(theLock, myInitLen, myHash, 0, mySeedLen);
            System.arraycopy(theLock,myInitLen + mySeedLen, mySecSeed, 0, mySeedLen);
            System.arraycopy(theLock, myInitLen + mySeedLen + mySeedLen, myKeySetSeed, 0, mySeedLen);
            if (!Arrays.equals(myHash, pResults[0])) {
                /* Fail the password attempt */
                throw new GordianBadCredentialsException("Invalid Password");
            }

            /* Encode the seeds */
            GordianPersonalisation.buildHashResult(mySecSeed, pResults[1]);
            GordianPersonalisation.buildHashResult(myKeySetSeed, pResults[1]);

            /* Create the parameters  */
            final GordianParameters myParameters = new GordianParameters(GordianFactoryType.BC);
            myParameters.setSecuritySeeds(mySecSeed, myKeySetSeed);
            myParameters.setInternal();
            return myParameters;

        } finally {
            Arrays.fill(pResults[0], (byte) 0);
            Arrays.fill(pResults[1], (byte) 0);
        }
    }

    /**
     * Process the password.
     *
     * @param pPassword the password for the lock
     * @return the results array
     * @throws OceanusException on error
     */
    private byte[][] processPassword(final GordianCoreFactory pFactory,
                                     final byte[] pPassword) throws OceanusException {
        /* Access factories */
        final GordianDigestFactory myDigests = pFactory.getDigestFactory();
        final GordianMacFactory myMacs = pFactory.getMacFactory();

        /* Create the primeMac */
        GordianMacSpec myMacSpec = GordianMacSpec.hMac(GordianDigestType.BLAKE2);
        final GordianMac myPrimeMac = myMacs.createMac(myMacSpec);
        myPrimeMac.initKeyBytes(pPassword);

        /* Create the alternateMac */
        myMacSpec = GordianMacSpec.hMac(GordianDigestType.KUPYNA);
        final GordianMac mySecondaryMac = myMacs.createMac(myMacSpec);
        mySecondaryMac.initKeyBytes(pPassword);

        /* Create the alternateMac */
        myMacSpec = GordianMacSpec.hMac(GordianDigestType.STREEBOG);
        final GordianMac myTertiaryMac = myMacs.createMac(myMacSpec);
        myTertiaryMac.initKeyBytes(pPassword);

        /* Create the secretMac */
        myMacSpec = GordianMacSpec.hMac(new GordianDigestSpec(GordianDigestType.SHA3, SEED_LEN));
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
        final GordianDigestSpec myDigestSpec = new GordianDigestSpec(GordianDigestType.SKEIN, SEED_LEN);
        final GordianDigest myDigest = myDigests.createDigest(myDigestSpec);

        /* Initialise the hash input values as the salt bytes */
        byte[] myPrimeInput = theInitVector;
        byte[] mySecondaryInput = theInitVector;
        byte[] myTertiaryInput = theInitVector;
        byte[] mySecretInput = theInitVector;

        /* Protect from exceptions */
        try {
            /* Loop through the iterations */
            for (int iPass = 0; iPass < NUM_ITERATIONS; iPass++) {
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

            /* Return to caller */
            return new byte[][]
                    {myExternalHash, mySecretBytes};

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
        if (!(pThat instanceof GordianCoreFactoryLock)) {
            return false;
        }

        /* Access the target field */
        final GordianCoreFactoryLock myThat = (GordianCoreFactoryLock) pThat;

        /* Check differences */
        return theFactory.equals(myThat.getLockedObject())
                && Arrays.equals(theLock, myThat.getLockBytes());
    }

    @Override
    public int hashCode() {
        return GordianCoreFactory.HASH_PRIME * theFactory.hashCode()
                + Arrays.hashCode(theLock);
    }
}
