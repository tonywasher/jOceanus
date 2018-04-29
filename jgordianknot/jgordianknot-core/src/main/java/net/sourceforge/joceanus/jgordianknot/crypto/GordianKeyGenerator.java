/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto;

import java.security.SecureRandom;
import java.util.Arrays;

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
     * Secure key.
     * @param pKeyToSecure the Key to secure
     * @param pKeySet the keySet to use to secure Key
     * @return the securedKey
     * @throws OceanusException on error
     */
    public byte[] secureKey(final GordianKey<?> pKeyToSecure,
                            final GordianKeySet pKeySet) throws OceanusException {
        return pKeySet.secureKey(pKeyToSecure);
    }

    /**
     * Secure Key.
     * @param pKeyToSecure the Key to secure
     * @param pKey the key to use to secure Key
     * @return the securedKey
     * @throws OceanusException on error
     */
    public byte[] secureKey(final GordianKey<?> pKeyToSecure,
                            final GordianKey<GordianSymKeySpec> pKey) throws OceanusException {
        final GordianWrapCipher myCipher = theFactory.createWrapCipher(pKey.getKeyType());
        return myCipher.secureKey(pKey, pKeyToSecure);
    }

    /**
     * Derive the key.
     * @param pSecuredKey the secured key
     * @param pKeySet the keySet to use to derive privateKey
     * @return the derived key
     * @throws OceanusException on error
     */
    public GordianKey<T> deriveKey(final byte[] pSecuredKey,
                                   final GordianKeySet pKeySet) throws OceanusException {
        return pKeySet.deriveKey(pSecuredKey, theKeyType);
    }

    /**
     * Create the keyPair from the PKCS8/X509 encodings.
     * @param pSecuredKey the secured key
     * @param pKey the key to use to derive Key
     * @return the derived key
     * @throws OceanusException on error
     */
    public GordianKey<T> deriveKey(final byte[] pSecuredKey,
                                   final GordianKey<GordianSymKeySpec> pKey) throws OceanusException {
        final GordianWrapCipher myCipher = theFactory.createWrapCipher(pKey.getKeyType());
        return myCipher.deriveKey(pKey, pSecuredKey, theKeyType);
    }

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
        mySeed = theFactory.getIdManager().deriveKeyHashDigestTypesFromSeed(mySeed, myDigestType);
        final int myIterations = mySeed & TethysDataConverter.NYBBLE_MASK;

        /* Create the MACs and initialise them */
        final GordianMac[] myMacs = new GordianMac[2];
        GordianMacSpec myMacSpec = GordianMacSpec.hMac(myDigestType[0]);
        GordianMac myMac = theFactory.createMac(myMacSpec);
        myMac.initMac(pSecret);
        myMacs[0] = myMac;
        myMacSpec = GordianMacSpec.hMac(myDigestType[1]);
        myMac = theFactory.createMac(myMacSpec);
        myMac.initMac(pSecret);
        myMacs[1] = myMac;

        /* while we need to generate more bytes */
        final GordianByteArrayInteger mySection = new GordianByteArrayInteger();
        while (myBuilt < myKeyLen) {
            /* Build the key part */
            final byte[] myKeyPart = buildCipherSection(myMacs, mySection.iterate(), pInitVector, myIterations);

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
     * @param pMacs the MACs to utilise
     * @param pSection the section count
     * @param pInitVector the initialisation vector
     * @param pIterations the adjustment to iterations
     * @return the section
     * @throws OceanusException on error
     */
    private byte[] buildCipherSection(final GordianMac[] pMacs,
                                      final byte[] pSection,
                                      final byte[] pInitVector,
                                      final int pIterations) throws OceanusException {
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

        /* Update with personalisation, algorithm and section */
        myPersonal.updateMac(myPrime);
        myPrime.update(myAlgo);
        myPrime.update(myKeyLen);
        myPrime.update(pSection);

        /* Loop through the iterations */
        final int myNumIterations = theFactory.getNumIterations() + pIterations;
        for (int i = 0; i < myNumIterations; i++) {
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
        mySeed = theFactory.getPersonalisation().convertRecipe(mySeed);
        final GordianDigestType[] myDigestType = new GordianDigestType[1];
        theFactory.getIdManager().deriveKeyHashDigestTypesFromSeed(mySeed, myDigestType);

        /* Create the MAC and initialise it */
        final GordianMacSpec myMacSpec = GordianMacSpec.hMac(myDigestType[0]);
        final GordianMac myMac = theFactory.createMac(myMacSpec);
        myMac.initMac(pSecret);

        /* Create the standard data */
        final byte[] myAlgo = TethysDataConverter.stringToByteArray(theKeyType.toString());
        final GordianPersonalisation myPersonal = theFactory.getPersonalisation();

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
