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
package net.sourceforge.joceanus.jgordianknot.impl.core.key;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianByteArrayInteger;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianPersonalisation;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianRandomSource;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * GordianKnot interface for Key Generators.
 * @param <T> the keyType
 */
public abstract class GordianCoreKeyGenerator<T extends GordianKeySpec>
    implements GordianKeyGenerator<T> {
    /**
     * iterations for buildCipher.
     */
    private static final int BUILD_ITERATIONS = 16;

    /**
     * The Key Type.
     */
    private final T theKeyType;

    /**
     * The Key Length.
     */
    private final int theKeyLength;

    /**
     * The Security Factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The Random Source.
     */
    private final GordianRandomSource theRandom;

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pKeyType the keyType
     */
    protected GordianCoreKeyGenerator(final GordianCoreFactory pFactory,
                                      final T pKeyType) {
        /* Store parameters */
        theKeyType = pKeyType;
        theFactory = pFactory;

        /* Cache some values */
        theKeyLength = pKeyType.getKeyLength().getLength();
        theRandom = pFactory.getRandomSource();
    }

    @Override
    public T getKeyType() {
        return theKeyType;
    }

    /**
     * Obtain random generator.
     * @return the generator
     */
    protected SecureRandom getRandom() {
        return theRandom.getRandom();
    }

    /**
     * Obtain factory.
     * @return the factory
     */
    protected GordianCoreFactory getFactory() {
        return theFactory;
    }

    /**
     * Generate a new Key.
     * @param pBytes the bytes for the key.
     * @return the new Key
     */
    public abstract GordianKey<T> buildKeyFromBytes(byte[] pBytes);

    /**
     * Generate a Key from a Secret.
     * @param pSecret the derived Secret
     * @param pInitVector the initialisation vector
     * @param pSeededRandom the deterministic random
     * @return the new Secret Key
     * @throws OceanusException on error
     */
    public GordianKey<T> generateKeyFromSecret(final byte[] pSecret,
                                               final byte[] pInitVector,
                                               final Random pSeededRandom) throws OceanusException {
        /* Determine the key length in bytes */
        final int myKeyLen = theKeyLength
                / Byte.SIZE;

        /* Create a buffer to hold the resulting key and # of bytes built */
        final byte[] myKeyBytes = new byte[myKeyLen];
        int myBuilt = 0;

        /* Derive the two digestTypes from the seededRandom */
        final GordianDigestType[] myDigestTypes = theFactory.getIdManager().deriveKeyHashDigestTypesFromSeed(pSeededRandom, 2);

        /* Create the MACs and initialise them */
        final GordianMacFactory myFactory = theFactory.getMacFactory();
        final GordianMac[] myMacs = new GordianMac[2];
        GordianMacSpec myMacSpec = GordianMacSpec.hMac(myDigestTypes[0]);
        GordianMac myMac = myFactory.createMac(myMacSpec);
        initMacKeyBytes(myMac, pSecret);
        myMacs[0] = myMac;
        myMacSpec = GordianMacSpec.hMac(myDigestTypes[1]);
        myMac =  myFactory.createMac(myMacSpec);
        initMacKeyBytes(myMac, pSecret);
        myMacs[1] = myMac;

        /* Protect against exceptions */
        try {
            /* Create section count */
            final GordianByteArrayInteger mySection = new GordianByteArrayInteger();
            final byte[] myAdjust = new byte[Integer.BYTES];
            pSeededRandom.nextBytes(myAdjust);
            mySection.addTo(myAdjust);

            /* while we need to generate more bytes */
            while (myBuilt < myKeyLen) {
                /* Build the key part */
                final byte[] myKeyPart = buildCipherSection(myMacs, mySection.iterate(), pInitVector);

                /* Determine how many bytes of this hash should be used */
                int myNeeded = myKeyLen
                        - myBuilt;
                if (myNeeded > myKeyPart.length) {
                    myNeeded = myKeyPart.length;
                }

                /* Copy bytes across */
                System.arraycopy(myKeyPart, 0, myKeyBytes, myBuilt, myNeeded);
                myBuilt += myNeeded;
                Arrays.fill(myKeyPart, (byte) 0);
            }

            /* Return the new key */
            return buildKeyFromBytes(myKeyBytes);

            /* Clear build buffer */
        } finally {
            Arrays.fill(myKeyBytes, (byte) 0);
        }
    }

    @Override
    public <X extends GordianKeySpec> GordianKey<T> translateKey(final GordianKey<X> pSource) throws OceanusException {
        /* Check that the keyLengths are compatible */
        if (pSource.getKeyType().getKeyLength() != theKeyType.getKeyLength()) {
            throw new GordianDataException("Incorrect length for key");
        }

        /* Build the key */
        final GordianCoreKey<X> mySource = (GordianCoreKey<X>) pSource;
        return buildKeyFromBytes(mySource.getKeyBytes());
    }

    /**
     * Init Mac keyBytes.
     * @param pMac the Mac.
     * @param pKeyBytes the keyBytes
     * @throws OceanusException on error
     */
    public abstract void initMacKeyBytes(GordianMac pMac, byte[] pKeyBytes) throws OceanusException;

    /**
     * Build Secret Key section (based on PBKDF2).
     * @param pMacs the MACs to utilise
     * @param pSection the section count
     * @param pInitVector the initialisation vector
     * @return the section
     * @throws OceanusException on error
     */
    private byte[] buildCipherSection(final GordianMac[] pMacs,
                                      final byte[] pSection,
                                      final byte[] pInitVector) throws OceanusException {
        /* Access the two MACs */
        final GordianMac myPrime = pMacs[0];
        final GordianMac myAlt = pMacs[1];

        /* Declare initial value */
        final byte[] myResult = new byte[myPrime.getMacSize()];
        final byte[] myPrimeHash = new byte[myPrime.getMacSize()];
        final byte[] myAltHash = new byte[myAlt.getMacSize()];
        byte[] myPrimeInput = pInitVector;
        byte[] myAltInput = pInitVector;

        /* Create the standard data */
        final byte[] myAlgo = TethysDataConverter.stringToByteArray(theKeyType.toString());
        final GordianPersonalisation myPersonal = theFactory.getPersonalisation();
        final byte[] myKeyLen = TethysDataConverter.integerToByteArray(theKeyLength);

        /* Protect against exceptions */
        try {
            /* Update prime with personalisation, algorithm and section */
            myPersonal.updateMac(myPrime);
            myPrime.update(myAlgo);
            myPrime.update(myKeyLen);
            myPrime.update(pSection);

            /* Update alt with personalisation, algorithm and section */
            myPersonal.updateMac(myAlt);
            myAlt.update(myAlgo);
            myAlt.update(myKeyLen);
            myAlt.update(pSection);

            /* Loop through the iterations */
            for (int i = 0; i < BUILD_ITERATIONS; i++) {
                /* Calculate alternate hash */
                myAlt.update(myAltInput);
                myAltInput = myAltHash;
                myAlt.finish(myAltHash, 0);

                /* Add the existing result to hash */
                myPrime.update(myPrimeInput);
                myPrime.update(myAltHash);
                myPrimeInput = myPrimeHash;

                /* Calculate MAC and place results into hash */
                myPrime.finish(myPrimeHash, 0);

                /* Fold into results */
                GordianPersonalisation.buildHashResult(myResult, myPrimeHash);
            }

            /* Return the result */
            return myResult;

            /* Clear the intermediate buffers */
        } finally {
            Arrays.fill(myPrimeHash, (byte) 0);
            Arrays.fill(myAltHash, (byte) 0);
        }
    }
}
