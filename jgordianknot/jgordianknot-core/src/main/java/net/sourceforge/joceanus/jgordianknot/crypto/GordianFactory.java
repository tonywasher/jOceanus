/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * GordianKnot base for Factory.
 */
public abstract class GordianFactory {
    /**
     * The Base personalisation.
     */
    private static final String BASE_PERSONAL = "jG0rd1anKn0t";

    /**
     * The Hash Prime.
     */
    public static final int HASH_PRIME = 37;

    /**
     * Restricted key length.
     */
    private static final int SMALL_KEYLEN = 128;

    /**
     * Unlimited key length.
     */
    protected static final int BIG_KEYLEN = 256;

    /**
     * The number of seed bytes.
     */
    private static final int SEED_SIZE = 32;

    /**
     * Parameters.
     */
    private final GordianParameters theParameters;

    /**
     * Do we use restricted keys?
     */
    private final boolean isRestricted;

    /**
     * Personalisation.
     */
    private final byte[] thePersonalisation;

    /**
     * SecureRandom instance.
     */
    private SecureRandom theRandom;

    /**
     * The Id Manager.
     */
    private GordianIdManager theIdManager;

    /**
     * Constructor.
     * @param pParameters the parameters
     * @throws OceanusException on error
     */
    protected GordianFactory(final GordianParameters pParameters) throws OceanusException {
        /* Store parameters */
        theParameters = pParameters;
        isRestricted = theParameters.useRestricted();

        /* Calculate personalisation bytes */
        String myPhrase = theParameters.getSecurityPhrase();
        GordianDigest myDigest = createDigest(getDefaultDigest());
        myDigest.update(TethysDataConverter.stringToByteArray(BASE_PERSONAL));
        if (myPhrase != null) {
            myDigest.update(TethysDataConverter.stringToByteArray(myPhrase));
        }
        thePersonalisation = myDigest.finish();

        /* Create the Id Manager */
        theIdManager = new GordianIdManager(this);
    }

    /**
     * Is the factory restricted?
     * @return true/false
     */
    public boolean isRestricted() {
        return isRestricted;
    }

    /**
     * Obtain Default Digest.
     * @return the default digest
     */
    protected GordianDigestType getDefaultDigest() {
        return theParameters.getBaseHashAlgorithm();
    }

    /**
     * Obtain Default SP800.
     * @return the default SP800
     */
    protected GordianSP800Type getDefaultSP800() {
        return theParameters.getSP800Type();
    }

    /**
     * Obtain Number of iterations.
     * @return the # of iterations
     */
    protected int getNumIterations() {
        return theParameters.getNumHashIterations();
    }

    /**
     * Obtain Number of steps.
     * @return the # of steps
     */
    protected int getNumCipherSteps() {
        return theParameters.getNumCipherSteps();
    }

    /**
     * Obtain the secureRandom instance.
     * @return the secureRandom instance
     */
    protected SecureRandom getRandom() {
        return theRandom;
    }

    /**
     * Obtain the idManager.
     * @return the idManager
     */
    protected GordianIdManager getIdManager() {
        return theIdManager;
    }

    /**
     * Obtain the personalisation bytes.
     * @return the personalisation
     */
    protected byte[] getPersonalisation() {
        return thePersonalisation;
    }

    /**
     * Set the secureRandom instance.
     * @param pRandom the secureRandom instance
     */
    protected void setSecureRandom(final SecureRandom pRandom) {
        theRandom = pRandom;
        theIdManager.setSecureRandom(pRandom);
    }

    /**
     * ReSeed the random number generator.
     */
    public void reSeedRandom() {
        /* Generate and apply the new seed */
        byte[] mySeed = theRandom.generateSeed(SEED_SIZE);
        theRandom.setSeed(mySeed);
    }

    /**
     * Obtain keyLength.
     * @return the keyLength
     */
    public int getKeyLength() {
        return isRestricted
                            ? SMALL_KEYLEN
                            : BIG_KEYLEN;
    }

    /**
     * Obtain keyAlgorithm.
     * @param <X> the key class
     * @param pKeyType the keyType
     * @return the keyLength
     * @throws OceanusException on error
     */
    public <X> String getKeyAlgorithm(final X pKeyType) throws OceanusException {
        return pKeyType.toString();
    }

    /**
     * create KeySet.
     * @return the new keySet
     */
    public GordianKeySet createKeySet() {
        return new GordianKeySet(this);
    }

    /**
     * Generate a keySetHash for the given password.
     * @param pPassword the password
     * @return the Password hash
     * @throws OceanusException on error
     */
    public GordianKeySetHash generateKeySetHash(final char[] pPassword) throws OceanusException {
        return new GordianKeySetHash(this, pPassword);
    }

    /**
     * Derive a keySetHash for the given hash and password.
     * @param pHashBytes the hash bytes
     * @param pPassword the password
     * @return the Password hash
     * @throws OceanusException on error
     * @throws GordianBadCredentialsException if password does not match
     */
    public GordianKeySetHash deriveKeySetHash(final byte[] pHashBytes,
                                              final char[] pPassword) throws OceanusException {
        return new GordianKeySetHash(this, pHashBytes, pPassword);
    }

    /**
     * create SecureRandom.
     * @param pRandomType the SP800 RandomType
     * @return the new SecureRandom
     * @throws OceanusException on error
     */
    public abstract SecureRandom createRandom(final GordianSP800Type pRandomType) throws OceanusException;

    /**
     * generate random GordianDigest.
     * @return the new Digest
     * @throws OceanusException on error
     */
    public GordianDigest generateRandomDigest() throws OceanusException {
        GordianDigestType myType = theIdManager.generateRandomDigestType(supportedDigests());
        return createDigest(myType);
    }

    /**
     * create GordianDigest.
     * @param pDigestType the DigestType
     * @return the new Digest
     * @throws OceanusException on error
     */
    public abstract GordianDigest createDigest(final GordianDigestType pDigestType) throws OceanusException;

    /**
     * Obtain predicate for supported digestTypes.
     * @return the predicate
     */
    public abstract Predicate<GordianDigestType> supportedDigests();

    /**
     * generate random GordianMac.
     * @return the new MAC
     * @throws OceanusException on error
     */
    public GordianMac generateRandomMac() throws OceanusException {
        /* Determine a random specification */
        GordianMacSpec mySpec = generateRandomMacSpec();

        /* Determine a random key */
        GordianKeyGenerator<GordianMacSpec> myGenerator = getKeyGenerator(mySpec);
        GordianKey<GordianMacSpec> myKey = myGenerator.generateKey();

        /* Create and initialise the MAC */
        GordianMac myMac = createMac(mySpec);
        myMac.initMac(myKey);

        /* Return it */
        return myMac;
    }

    /**
     * generate random GordianMacSpec.
     * @return the new MacSpec
     */
    private GordianMacSpec generateRandomMacSpec() {
        GordianMacType myMacType = theIdManager.generateRandomMacType(supportedMacs());
        switch (myMacType) {
            case HMAC:
                GordianDigestType myDigestType = theIdManager.generateRandomDigestType(supportedDigests());
                return new GordianMacSpec(myMacType, myDigestType);
            case POLY1305:
            case GMAC:
                GordianSymKeyType mySymType = theIdManager.generateRandomSymKeyType(standardSymKeys());
                return new GordianMacSpec(myMacType, mySymType);
            default:
                return new GordianMacSpec(myMacType);
        }
    }

    /**
     * create GordianMac.
     * @param pMacSpec the MacSpec
     * @return the new MAC
     * @throws OceanusException on error
     */
    public abstract GordianMac createMac(final GordianMacSpec pMacSpec) throws OceanusException;

    /**
     * Obtain predicate for supported macTypes.
     * @return the predicate
     */
    public abstract Predicate<GordianMacType> supportedMacs();

    /**
     * obtain GordianKeyGenerator.
     * @param <T> the keyClass
     * @param pKeyType the KeyType
     * @return the new KeyGenerator
     * @throws OceanusException on error
     */
    public abstract <T> GordianKeyGenerator<T> getKeyGenerator(final T pKeyType) throws OceanusException;

    /**
     * Obtain keyPair generator.
     * @param pKeyType the key type
     * @return the generator
     * @throws OceanusException on error
     */
    public abstract GordianKeyPairGenerator getKeyPairGenerator(final GordianAsymKeyType pKeyType) throws OceanusException;

    /**
     * generate random SymKey.
     * @return the new key
     * @throws OceanusException on error
     */
    public GordianKey<GordianSymKeyType> generateRandomSymKey() throws OceanusException {
        /* Determine a random keyType */
        GordianSymKeyType myType = theIdManager.generateRandomSymKeyType(supportedSymKeys());

        /* Generate a random key */
        GordianKeyGenerator<GordianSymKeyType> myGenerator = getKeyGenerator(myType);
        return myGenerator.generateKey();
    }

    /**
     * generate random SymKeyList.
     * @return the list of keys
     * @throws OceanusException on error
     */
    public List<GordianKey<GordianSymKeyType>> generateRandomSymKeyList() throws OceanusException {
        /* Determine a random set of keyType */
        int myCount = getNumCipherSteps();
        GordianSymKeyType[] myTypes = theIdManager.generateRandomSymKeyTypes(myCount, supportedSymKeys());

        /* Loop through the keys */
        List<GordianKey<GordianSymKeyType>> myKeyList = new ArrayList<>();
        for (int i = 0; i < myCount; i++) {
            /* Generate a random key */
            GordianSymKeyType myType = myTypes[i];
            GordianKeyGenerator<GordianSymKeyType> myGenerator = getKeyGenerator(myType);
            myKeyList.add(myGenerator.generateKey());
        }

        /* Return the list */
        return myKeyList;
    }

    /**
     * create GordianSymCipher.
     * @param pKeyType the KeyType
     * @param pMode the cipher mode
     * @param pPadding use padding true/false
     * @return the new Cipher
     * @throws OceanusException on error
     */
    public abstract GordianCipher<GordianSymKeyType> createSymKeyCipher(final GordianSymKeyType pKeyType,
                                                                        final GordianCipherMode pMode,
                                                                        final boolean pPadding) throws OceanusException;

    /**
     * Obtain predicate for supported SymKeyTypes.
     * @return the predicate
     */
    public abstract Predicate<GordianSymKeyType> supportedSymKeys();

    /**
     * Obtain predicate for standard SymKeyTypes.
     * @return the predicate
     */
    public abstract Predicate<GordianSymKeyType> standardSymKeys();

    /**
     * generate random GordianStreamKeyType.
     * @return the new StreamKeyType
     */
    public GordianStreamKeyType generateRandomStreamKeyType() {
        return theIdManager.generateRandomStreamKeyType(supportedStreamKeys());
    }

    /**
     * generate random StreamKey.
     * @return the new key
     * @throws OceanusException on error
     */
    public GordianKey<GordianStreamKeyType> generateRandomStreamKey() throws OceanusException {
        /* Determine a random keyType */
        GordianStreamKeyType myType = theIdManager.generateRandomStreamKeyType(supportedStreamKeys());

        /* Generate a random key */
        GordianKeyGenerator<GordianStreamKeyType> myGenerator = getKeyGenerator(myType);
        return myGenerator.generateKey();
    }

    /**
     * create GordianStreamCipher.
     * @param pKeyType the KeyType
     * @return the new Cipher
     * @throws OceanusException on error
     */
    public abstract GordianCipher<GordianStreamKeyType> createStreamKeyCipher(final GordianStreamKeyType pKeyType) throws OceanusException;

    /**
     * Obtain predicate for supported StreamKeyTypes.
     * @return the predicate
     */
    public abstract Predicate<GordianStreamKeyType> supportedStreamKeys();

    /**
     * create GordianWrapCipher.
     * @param pKeyType the KeyType
     * @return the new Cipher
     * @throws OceanusException on error
     */
    public abstract GordianWrapCipher createWrapCipher(final GordianSymKeyType pKeyType) throws OceanusException;

    /**
     * Create signer.
     * @param pPrivateKey the privateKey
     * @param pDigestType the digest type
     * @return the signer
     * @throws OceanusException on error
     */
    public abstract GordianSigner createSigner(final GordianPrivateKey pPrivateKey,
                                               final GordianDigestType pDigestType) throws OceanusException;

    /**
     * Create validator.
     * @param pPublicKey the publicKey
     * @param pDigestType the digest type
     * @return the validator
     * @throws OceanusException on error
     */
    public abstract GordianValidator createValidator(final GordianPublicKey pPublicKey,
                                                     final GordianDigestType pDigestType) throws OceanusException;

    /**
     * Build Invalid text string.
     * @param pValue the parameter
     * @return the text
     */
    protected static String getInvalidText(final Object pValue) {
        /* Create initial string */
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append("Invalid ");

        /* Build details */
        if (pValue != null) {
            myBuilder.append(pValue.getClass().getSimpleName());
            myBuilder.append(" :- ");
            myBuilder.append(pValue.toString());
        } else {
            myBuilder.append("null value");
        }

        /* Return the string */
        return myBuilder.toString();
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
        if (!(pThat instanceof GordianFactory)) {
            return false;
        }

        /* Access the target field */
        GordianFactory myThat = (GordianFactory) pThat;

        /* Check Differences */
        return theParameters.equals(myThat.theParameters);
    }

    @Override
    public int hashCode() {
        return theParameters.hashCode();
    }
}
