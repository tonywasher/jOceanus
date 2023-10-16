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
package net.sourceforge.joceanus.jgordianknot.impl.core.base;

import java.security.SecureRandom;
import java.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryLock;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianBadCredentialsException;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.mac.GordianCoreMac;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * FactoryLock class.
 */
public class GordianCoreFactoryLock
    implements GordianFactoryLock {
    /**
     * Mask.
     */
    private static final byte[] MASK = { 'F', 'A', 'C', 'T' };

    /**
     * Mask length.
     */
    public static final int MASK_LEN = MASK.length;

    /**
     * InitVector length.
     */
    public static final int INIT_LEN = GordianLength.LEN_128.getByteLength();

    /**
     * Hash length.
     */
    public static final GordianLength HASH_LEN = GordianLength.LEN_512;

    /**
     * The number of hash iterations.
     */
    private static final int NUM_ITERATIONS = 1024;

    /**
     * The work buffer.
     */
    private final byte[] theWork;

    /**
     * The initVector.
     */
    private final byte[] theInit;

    /**
     * The parameters.
     */
    private final GordianParameters theParameters;

    /**
     * The factory.
     */
    private GordianFactory theFactory;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pPassword the password
     * @throws OceanusException on error
     */
    public GordianCoreFactoryLock(final GordianFactory pFactory,
                                  final char[] pPassword) throws OceanusException {
        /* Access parameters */
        final GordianCoreFactory myFactory = (GordianCoreFactory) pFactory;
        theParameters = myFactory.getParameters();
        theFactory = pFactory;

        /* Reject request if this is not a randomFactory */
        if (theParameters.getKeySetSeed() == null) {
            throw new GordianDataException("Unable to lock non-Random factory");
        }

        /* Create the workBuffer */
        theWork = buildWorkBuffer();

        /* Create the initVector */
        theInit = new byte[INIT_LEN];
        final SecureRandom myRandom = myFactory.getRandomSource().getRandom();
        myRandom.nextBytes(theInit);

        /* Process the work buffer */
        processWorkBuffer(pFactory, pPassword);
    }

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pExternal the external buffer
     * @param pPassword the password
     * @throws OceanusException on error
     */
    public GordianCoreFactoryLock(final GordianFactory pFactory,
                                  final byte[] pExternal,
                                  final char[] pPassword) throws OceanusException {
        /* Check that the buffer is the correct length */
        if (pExternal == null
            || pExternal.length != INIT_LEN + HASH_LEN.getByteLength()) {
            throw new IllegalArgumentException("Invalid external buffer");
        }

        /* Extract the init and work buffers */
        theInit = Arrays.copyOf(pExternal, INIT_LEN);
        theWork = Arrays.copyOfRange(pExternal, INIT_LEN, pExternal.length);

        /* Process the work buffer */
        processWorkBuffer(pFactory, pPassword);

        /* Check that the MASK is correct */
        final byte[] myMask = Arrays.copyOf(theWork, MASK_LEN);
        if (!Arrays.equals(myMask, MASK)) {
            throw new GordianBadCredentialsException("Invalid Password");
        }

        /* Access the various parts */
        final int mySeedLen = GordianParameters.SEED_LEN;
        final byte[] mySecSeed = Arrays.copyOfRange(theWork, MASK_LEN, mySeedLen + MASK_LEN);
        final byte[] myKeySetSeed = Arrays.copyOfRange(theWork, mySeedLen + MASK_LEN, theWork.length);
        Arrays.fill(theWork, (byte) 0);

        /* Create the parameters */
        theParameters = new GordianParameters(GordianFactoryType.BC);
        theParameters.setSecuritySeeds(mySecSeed, myKeySetSeed);
        theParameters.setInternal();
    }

    /**
     * Obtain parameters.
     * @return the parameters
     */
    public GordianParameters getParameters() {
        return theParameters;
    }

    @Override
    public GordianFactory getFactory() {
        return theFactory;
    }

    @Override
    public GordianKeySet getKeySet() {
        return ((GordianCoreFactory) theFactory).getEmbeddedKeySet();
    }

    /**
     * Record the factory.
     * @param pFactory the factory
     */
    public void setFactory(final GordianFactory pFactory) {
        theFactory = pFactory;
    }

    @Override
    public byte[] getExternalBuffer() {
        /* Create buffer */
        final byte[] myBuffer = new byte[theInit.length + theWork.length];

        /* Build buffer and return it */
        System.arraycopy(theInit, 0, myBuffer, 0, theInit.length);
        System.arraycopy(theWork, 0, myBuffer, theInit.length, theWork.length);
        return myBuffer;
    }

    /**
     * Build the work buffer.
     * @return the buffer
     */
    private byte[] buildWorkBuffer() {
        /* Access buffers */
        final byte[] myBuffer = new byte[HASH_LEN.getByteLength()];
        final byte[] mySecSeed = theParameters.getSecuritySeed();
        final byte[] myKeySetSeed = theParameters.getKeySetSeed();

        /* Build buffer and return it */
        System.arraycopy(MASK, 0, myBuffer, 0, MASK_LEN);
        System.arraycopy(mySecSeed, 0, myBuffer, MASK_LEN, mySecSeed.length);
        System.arraycopy(myKeySetSeed, 0, myBuffer, MASK_LEN + mySecSeed.length, myKeySetSeed.length);
        return myBuffer;
    }

    /**
     * Process the work buffer
     * @param pFactory the factory
     * @param pPassword the password
     * @throws OceanusException on error
     */
    private void processWorkBuffer(final GordianFactory pFactory,
                                   final char[] pPassword) throws OceanusException {
        /* Access password as bytes */
        final byte[] myPassword = TethysDataConverter.charsToByteArray(pPassword);

        /* Determine the macs */
        final GordianMac[] myMacs = determineMacs(pFactory);
        final byte[][] myHashes = new byte[myMacs.length][];

        /* Initialise hashes */
        for (int i = 0; i < myMacs.length; i++) {
            /* Initialise the macs */
            final GordianCoreMac myMac = (GordianCoreMac) myMacs[i];
            myMac.initKeyBytes(myPassword);
            myMac.update(theInit);

            /* Finish the update and store the buffer */
            final byte[] myResult = myMac.finish();
            myHashes[i] = myResult;
        }

        /* Loop iterations times */
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            /* Cross-fertilise the hashes and apply them */
            iterateHashes(myMacs, myHashes);
            applyHashes(myHashes);
        }
    }

    /**
     * Apply the result hashes.
     * @param pHashes the result Hashes
     */
    public void applyHashes(final byte[][] pHashes) {
        /* Loop through the hashes */
        final int myLen = HASH_LEN.getByteLength();
        for (byte[] myHash : pHashes) {
            /* Loop through the array bytes */
            for (int i = 0; i < myLen; i++) {
                /* Combine the bytes */
                theWork[i] ^= myHash[i];
            }
        }
    }

    /**
     * Iterate the hashes.
     * @param pMacs the mac array
     * @param pHashes the hashes array
     * @throws OceanusException on error
     */
    private static void iterateHashes(final GordianMac[] pMacs,
                                      final byte[][] pHashes) throws OceanusException {
        /* Update all the Macs */
        for (final GordianMac myMac : pMacs) {
            /* Update with the results */
            for (int k = 0; k < pMacs.length; k++) {
                myMac.update(pHashes[k]);
            }
        }

        /* Finish all the macs */
        for (int j = 0; j < pMacs.length; j++) {
            /* Update with the results */
            final GordianMac myMac = pMacs[j];
            final byte[] myResult = pHashes[j];
            myMac.finish(myResult, 0);
        }
    }

    /**
     * Obtain an array of macs for masking.
     * @param pFactory the factory
     * @return the macs
     * @throws OceanusException on error
     */
    private static GordianMac[] determineMacs(final GordianFactory pFactory) throws OceanusException {
        /* Access mac factory */
        final GordianMacFactory myFactory = pFactory.getMacFactory();

        /* Initialise variables */
        final GordianDigestType[] myTypes = GordianDigestType.values();
        final GordianMac[] myMacs = new GordianMac[myTypes.length];
        int myLen = 0;

        /* Loop through the digestTypes */
        for (final GordianDigestType myType : myTypes) {
            /* If we can generate HASH_LEN as the result */
            if (myType.isLengthValid(HASH_LEN)
                && myFactory.supportedHMacDigestTypes().test(myType)) {
                final GordianMacSpec myMacSpec = GordianMacSpec.hMac(new GordianDigestSpec(myType, HASH_LEN));
                myMacs[myLen++] = myFactory.createMac(myMacSpec);
            }
        }

        /* Return the array */
        return Arrays.copyOf(myMacs, myLen);
    }
}
