/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2016 Tony Washer
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

import java.security.SecureRandom;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * GordianKnot interface for Key Generators.
 * @param <T> the keyType
 */
public abstract class GordianKeyGenerator<T> {
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
    private final GordianFactory theFactory;

    /**
     * The Random Generator.
     */
    private final SecureRandom theRandom;

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pKeyType the keyType
     */
    protected GordianKeyGenerator(final GordianFactory pFactory,
                                  final T pKeyType) {
        /* Store parameters */
        theKeyType = pKeyType;
        theFactory = pFactory;

        /* Cache some values */
        theKeyLength = pFactory.getKeyLength();
        theRandom = pFactory.getRandom();
    }

    /**
     * Obtain keyType.
     * @return the keyType
     */
    public T getKeyType() {
        return theKeyType;
    }

    /**
     * Obtain keyLength.
     * @return the keyLength
     */
    public int getKeyLength() {
        return theKeyLength;
    }

    /**
     * Obtain random generator.
     * @return the generator
     */
    protected SecureRandom getRandom() {
        return theRandom;
    }

    /**
     * Obtain factory.
     * @return the factory
     */
    protected GordianFactory getFactory() {
        return theFactory;
    }

    /**
     * Generate a new Key.
     * @return the new Key
     */
    public abstract GordianKey<T> generateKey();

    /**
     * Translate a Key.
     * @param pSource the source key.
     * @return the new Key
     * @throws OceanusException on error
     */
    protected abstract GordianKey<T> translateKey(GordianKey<?> pSource) throws OceanusException;

    /**
     * Generate a new Key.
     * @param pBytes the bytes for the key.
     * @return the new Key
     * @throws OceanusException on error
     */
    protected abstract GordianKey<T> buildKeyFromBytes(byte[] pBytes);

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
        int myKeyLen = theKeyLength
                       / Byte.SIZE;

        /* Create a buffer to hold the resulting key and # of bytes built */
        byte[] myKeyBytes = new byte[myKeyLen];
        int myBuilt = 0;

        /* Create the MAC and initialise it */
        GordianMacSpec myMacSpec = GordianMacSpec.hMac(theFactory.getDefaultDigest());
        GordianMac myMac = theFactory.createMac(myMacSpec);
        myMac.initMac(pSecret);

        /* while we need to generate more bytes */
        GordianByteArrayInteger mySection = new GordianByteArrayInteger();
        while (myBuilt < myKeyLen) {
            /* Build the key part */
            byte[] myKeyPart = buildCipherSection(myMac, mySection.iterate(), pInitVector);

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
     * Build Secret Key section (based on PBKDF2).
     * @param pMac the MAC to utilise
     * @param pSection the section count
     * @param pInitVector the initialisation vector
     * @return the section
     * @throws OceanusException on error
     */
    private byte[] buildCipherSection(final GordianMac pMac,
                                      final byte[] pSection,
                                      final byte[] pInitVector) throws OceanusException {
        /* Declare initial value */
        byte[] myResult = new byte[pMac.getMacSize()];
        byte[] myHash = new byte[pMac.getMacSize()];
        byte[] myInput = pInitVector;

        /* Create the standard data */
        byte[] myAlgo = TethysDataConverter.stringToByteArray(theKeyType.toString());
        byte[] myPersonal = theFactory.getPersonalisation();

        /* Update with personalisation, algorithm and section */
        pMac.update(myPersonal);
        pMac.update(myAlgo);
        pMac.update(pSection);

        /* Loop through the iterations */
        int myNumIterations = theFactory.getNumIterations() << 1;
        for (int i = 0; i < myNumIterations; i++) {
            /* Add the existing result to hash */
            pMac.update(myInput);
            myInput = myHash;

            /* Calculate MAC and place results into hash */
            pMac.finish(myHash, 0);

            /* Fold into results */
            TethysDataConverter.buildHashResult(myResult, myHash);
        }

        /* Return the result */
        return myResult;
    }
}
