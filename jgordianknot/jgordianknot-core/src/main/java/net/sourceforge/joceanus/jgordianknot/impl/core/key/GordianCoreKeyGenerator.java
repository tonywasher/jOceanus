/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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

import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianByteArrayInteger;
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

    @Override
    public int getKeyLength() {
        return theKeyLength;
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
     * @return the new Secret Key
     * @throws OceanusException on error
     */
    public GordianKey<T> generateKeyFromSecret(final byte[] pSecret,
                                               final byte[] pInitVector) throws OceanusException {
        /* Determine the key length in bytes */
        final int myKeyLen = theKeyLength
                / Byte.SIZE;

        /* Create a buffer to hold the resulting key and # of bytes built */
        final byte[] myKeyBytes = new byte[myKeyLen];
        int myBuilt = 0;

        /* Determine an initialSeed and work out hash type and iterations */
        int mySeed = determineSecretSeed(pSecret, pInitVector);
        mySeed = theFactory.getPersonalisation().convertRecipe(mySeed);
        final GordianDigestType[] myDigestType = new GordianDigestType[2];
        theFactory.getIdManager().deriveKeyHashDigestTypesFromSeed(mySeed, myDigestType);

        /* Create the MACs and initialise them */
        final GordianMacFactory myFactory = theFactory.getMacFactory();
        final GordianMac[] myMacs = new GordianMac[2];
        GordianMacSpec myMacSpec = GordianMacSpec.hMac(myDigestType[0]);
        GordianMac myMac = myFactory.createMac(myMacSpec);
        initMacKeyBytes(myMac, pSecret);
        myMacs[0] = myMac;
        myMacSpec = GordianMacSpec.hMac(myDigestType[1]);
        myMac =  myFactory.createMac(myMacSpec);
        initMacKeyBytes(myMac, pSecret);
        myMacs[1] = myMac;

        /* while we need to generate more bytes */
        final GordianByteArrayInteger mySection = new GordianByteArrayInteger();
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
        }

        /* Return the new key */
        return buildKeyFromBytes(myKeyBytes);
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
            TethysDataConverter.buildHashResult(myResult, myPrimeHash);
        }

        /* Return the result */
        return myResult;
    }

    /**
     * Determine initialSeed.
     * @param pSecret the secret
     * @param pInitVector the initialisation vector
     * @return the seed
     * @throws OceanusException on error
     */
    private int determineSecretSeed(final byte[] pSecret,
                                    final byte[] pInitVector) throws OceanusException {
        /* Determine a digestType to use based on the first four bytes of the initVector */
        int mySeed = TethysDataConverter.byteArrayToInteger(Arrays.copyOf(pInitVector, Integer.SIZE));
        final GordianPersonalisation myPersonal = theFactory.getPersonalisation();
        mySeed = myPersonal.convertRecipe(mySeed);
        final GordianDigestType[] myDigestType = new GordianDigestType[1];
        theFactory.getIdManager().deriveKeyHashDigestTypesFromSeed(mySeed, myDigestType);

        /* Create the MAC and initialise it */
        final GordianMacSpec myMacSpec = GordianMacSpec.hMac(myDigestType[0]);
        final GordianMacFactory myFactory = theFactory.getMacFactory();
        final GordianMac myMac = myFactory.createMac(myMacSpec);
        initMacKeyBytes(myMac, pSecret);

        /* Create the standard data */
        final byte[] myAlgo = TethysDataConverter.stringToByteArray(theKeyType.toString());

        /* Update with personalisation, algorithm and initVector */
        myPersonal.updateMac(myMac);
        myMac.update(myAlgo);
        myMac.update(pInitVector);

        /* Determine result */
        final byte[] myResult = new byte[myMac.getMacSize()];
        myMac.finish(myResult, 0);

        /* Return the first few bytes as the seed */
        return TethysDataConverter.byteArrayToInteger(Arrays.copyOf(myResult, Integer.SIZE));
    }
}
