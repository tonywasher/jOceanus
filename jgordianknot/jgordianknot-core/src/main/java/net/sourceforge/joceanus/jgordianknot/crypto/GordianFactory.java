/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
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
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherSpec.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherSpec.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation.GordianKEMSender;
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
     * Initialisation Vector size (128/8).
     */
    protected static final int IVSIZE = 16;

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
        char[] myPhrase = theParameters.getSecurityPhrase();
        GordianDigest myDigest = createDigest(getDefaultDigest());
        myDigest.update(TethysDataConverter.stringToByteArray(BASE_PERSONAL));
        if (myPhrase != null) {
            myDigest.update(TethysDataConverter.charsToByteArray(myPhrase));
        }
        thePersonalisation = myDigest.finish();
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
    protected GordianDigestSpec getDefaultDigest() {
        return new GordianDigestSpec(theParameters.getBaseHashAlgorithm());
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
        if (theIdManager == null) {
            theIdManager = new GordianIdManager(this);
        }
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
     * Obtain the default randomSpec.
     * @return the default randomSpec
     */
    protected GordianRandomSpec defaultRandomSpec() {
        return new GordianRandomSpec(getDefaultSP800(), getDefaultDigest());
    }

    /**
     * Set the secureRandom instance.
     * @param pRandom the secureRandom instance
     */
    protected void setSecureRandom(final SecureRandom pRandom) {
        theRandom = pRandom;
        getIdManager().setSecureRandom(pRandom);
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
     * @param isRestricted is the factory restricted?
     * @return the keyLength
     */
    public static int getKeyLength(final boolean isRestricted) {
        return isRestricted
                            ? SMALL_KEYLEN
                            : BIG_KEYLEN;
    }

    /**
     * Obtain keyLength.
     * @return the keyLength
     */
    public int getKeyLength() {
        return getKeyLength(isRestricted);
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
     * @param pRandomSpec the randomSpec
     * @return the new SecureRandom
     * @throws OceanusException on error
     */
    public abstract SecureRandom createRandom(GordianRandomSpec pRandomSpec) throws OceanusException;

    /**
     * Obtain predicate for supported randomSpecs.
     * @return the predicate
     */
    public Predicate<GordianRandomSpec> supportedRandomSpecs() {
        return this::validRandomSpec;
    }

    /**
     * generate random GordianDigest.
     * @return the new Digest
     * @throws OceanusException on error
     */
    public GordianDigest generateRandomDigest() throws OceanusException {
        GordianDigestType myType = getIdManager().generateRandomDigestType();
        return createDigest(new GordianDigestSpec(myType));
    }

    /**
     * create GordianDigest.
     * @param pDigestSpec the DigestSpec
     * @return the new Digest
     * @throws OceanusException on error
     */
    public abstract GordianDigest createDigest(GordianDigestSpec pDigestSpec) throws OceanusException;

    /**
     * Obtain predicate for supported digestSpecs.
     * @return the predicate
     */
    public Predicate<GordianDigestSpec> supportedDigestSpecs() {
        return this::validDigestSpec;
    }

    /**
     * Obtain predicate for supported digestTypes.
     * @return the predicate
     */
    public abstract Predicate<GordianDigestType> supportedDigestTypes();

    /**
     * generate random GordianMac.
     * @return the new MAC
     * @throws OceanusException on error
     */
    public GordianMac generateRandomMac() throws OceanusException {
        /* Determine a random specification */
        GordianMacSpec mySpec = getIdManager().generateRandomMacSpec();

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
     * create GordianMac.
     * @param pMacSpec the MacSpec
     * @return the new MAC
     * @throws OceanusException on error
     */
    public abstract GordianMac createMac(GordianMacSpec pMacSpec) throws OceanusException;

    /**
     * Obtain predicate for supported macSpecs.
     * @return the predicate
     */
    public Predicate<GordianMacSpec> supportedMacSpecs() {
        return this::validMacSpec;
    }

    /**
     * Obtain predicate for supported macTypes.
     * @return the predicate
     */
    public abstract Predicate<GordianMacType> supportedMacTypes();

    /**
     * Obtain predicate for supported hMac digestSpecs.
     * @return the predicate
     */
    public Predicate<GordianDigestSpec> supportedHMacDigestSpecs() {
        return this::validHMacSpec;
    }

    /**
     * Obtain predicate for supported hMac digestTypes.
     * @return the predicate
     */
    public abstract Predicate<GordianDigestType> supportedHMacDigestTypes();

    /**
     * Obtain predicate for supported poly1305 symKeyTypes.
     * @return the predicate
     */
    public abstract Predicate<GordianSymKeyType> supportedPoly1305SymKeyTypes();

    /**
     * Obtain predicate for supported gMac symKeyTypes.
     * @return the predicate
     */
    public abstract Predicate<GordianSymKeyType> supportedGMacSymKeyTypes();

    /**
     * Obtain predicate for supported cMac symKeyTypes.
     * @return the predicate
     */
    public abstract Predicate<GordianSymKeyType> supportedCMacSymKeyTypes();

    /**
     * Obtain predicate for supported KeyHash digests.
     * @return the predicate
     */
    public Predicate<GordianDigestType> supportedKeySetDigestTypes() {
        return supportedHMacDigestTypes().and(GordianDigestType::isCombinedHashDigest);
    }

    /**
     * Obtain predicate for supported external digests.
     * @return the predicate
     */
    public Predicate<GordianDigestType> supportedExternalDigestTypes() {
        return supportedHMacDigestTypes().and(GordianDigestType::isExternalHashDigest);
    }

    /**
     * obtain GordianKeyGenerator.
     * @param <T> the keyClass
     * @param pKeyType the KeyType
     * @return the new KeyGenerator
     * @throws OceanusException on error
     */
    public abstract <T> GordianKeyGenerator<T> getKeyGenerator(T pKeyType) throws OceanusException;

    /**
     * Obtain keyPair generator.
     * @param pKeySpec the keySpec
     * @return the generator
     * @throws OceanusException on error
     */
    public abstract GordianKeyPairGenerator getKeyPairGenerator(GordianAsymKeySpec pKeySpec) throws OceanusException;

    /**
     * generate random SymKey.
     * @return the new key
     * @throws OceanusException on error
     */
    public GordianKey<GordianSymKeyType> generateRandomSymKey() throws OceanusException {
        /* Determine a random keyType */
        GordianSymKeyType myType = getIdManager().generateRandomSymKeyType();

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
        GordianSymKeyType[] myTypes = getIdManager().generateRandomSymKeyTypes(myCount);

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
     * @param pCipherSpec the cipherSpec
     * @return the new Cipher
     * @throws OceanusException on error
     */
    public abstract GordianCipher<GordianSymKeyType> createSymKeyCipher(GordianSymCipherSpec pCipherSpec) throws OceanusException;

    /**
     * Obtain predicate for supported SymKeyTypes.
     * @return the predicate
     */
    public abstract Predicate<GordianSymKeyType> supportedSymKeyTypes();

    /**
     * Obtain predicate for keySet SymKeyTypes.
     * @return the predicate
     */
    public abstract Predicate<GordianSymKeyType> supportedKeySetSymKeyTypes();

    /**
     * create GordianAADCipher.
     * @param pCipherSpec the cipherSpec
     * @return the new Cipher
     * @throws OceanusException on error
     */
    public abstract GordianAADCipher createAADCipher(GordianSymCipherSpec pCipherSpec) throws OceanusException;

    /**
     * generate random GordianStreamKeyType.
     * @return the new StreamKeyType
     */
    public GordianStreamKeyType generateRandomStreamKeyType() {
        return getIdManager().generateRandomStreamKeyType();
    }

    /**
     * generate random StreamKey.
     * @return the new key
     * @throws OceanusException on error
     */
    public GordianKey<GordianStreamKeyType> generateRandomStreamKey() throws OceanusException {
        /* Determine a random keyType */
        GordianStreamKeyType myType = generateRandomStreamKeyType();

        /* Generate a random key */
        GordianKeyGenerator<GordianStreamKeyType> myGenerator = getKeyGenerator(myType);
        return myGenerator.generateKey();
    }

    /**
     * create GordianStreamCipher.
     * @param pCipherSpec the cipherSpec
     * @return the new Cipher
     * @throws OceanusException on error
     */
    public abstract GordianCipher<GordianStreamKeyType> createStreamKeyCipher(GordianStreamCipherSpec pCipherSpec) throws OceanusException;

    /**
     * Obtain predicate for supported StreamKeyTypes.
     * @return the predicate
     */
    public abstract Predicate<GordianStreamKeyType> supportedStreamKeyTypes();

    /**
     * Obtain predicate for keySet SymKeyTypes.
     * @return the predicate
     */
    public Predicate<GordianStreamKeyType> supportedKeySetStreamKeyTypes() {
        return p -> supportedStreamKeyTypes().test(p) && p.getIVLength() > 0;
    }

    /**
     * create GordianWrapCipher.
     * @param pKeyType the KeyType
     * @return the new Cipher
     * @throws OceanusException on error
     */
    protected abstract GordianWrapCipher createWrapCipher(GordianSymKeyType pKeyType) throws OceanusException;

    /**
     * Create a wrapCipher.
     * @param pBlockCipher the underlying block cipher
     * @return the wrapCipher
     * @throws OceanusException on error
     */
    protected GordianWrapCipher createWrapCipher(final GordianCipher<GordianSymKeyType> pBlockCipher) throws OceanusException {
        return new GordianWrapCipher(this, pBlockCipher);
    }

    /**
     * Obtain predicate for signatures.
     * @return the predicate
     */
    public abstract BiPredicate<GordianKeyPair, GordianSignatureSpec> supportedSignatures();

    /**
     * Create signer.
     * @param pKeyPair the keyPair
     * @param pSignatureSpec the signatureSpec
     * @return the signer
     * @throws OceanusException on error
     */
    public abstract GordianSigner createSigner(GordianKeyPair pKeyPair,
                                               GordianSignatureSpec pSignatureSpec) throws OceanusException;

    /**
     * Create validator.
     * @param pKeyPair the keyPair
     * @param pSignatureSpec the signatureSpec
     * @return the validator
     * @throws OceanusException on error
     */
    public abstract GordianValidator createValidator(GordianKeyPair pKeyPair,
                                                     GordianSignatureSpec pSignatureSpec) throws OceanusException;

    /**
     * Obtain predicate for keyExchange.
     * @return the predicate
     */
    public BiPredicate<GordianKeyPair, GordianDigestSpec> supportedKeyExchanges() {
        return this::validExchangeSpec;
    }

    /**
     * Create KEMessage.
     * @param pKeyPair the keyPair
     * @param pDigestSpec the digestSpec
     * @return the KEMSender
     * @throws OceanusException on error
     */
    public abstract GordianKEMSender createKEMessage(GordianKeyPair pKeyPair,
                                                     GordianDigestSpec pDigestSpec) throws OceanusException;

    /**
     * Parse KEMessage.
     * @param pKeyPair the keyPair
     * @param pDigestSpec the digestSpec
     * @param pMessage the cipherText
     * @return the parsed KEMessage
     * @throws OceanusException on error
     */
    public abstract GordianKeyEncapsulation parseKEMessage(GordianKeyPair pKeyPair,
                                                           GordianDigestSpec pDigestSpec,
                                                           byte[] pMessage) throws OceanusException;

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

    /**
     * Check RandomSpec.
     * @param pRandomSpec the randomSpec
     * @return true/false
     */
    private boolean validRandomSpec(final GordianRandomSpec pRandomSpec) {
        /* Access details */
        GordianSP800Type myType = pRandomSpec.getRandomType();
        GordianDigestSpec mySpec = pRandomSpec.getDigestSpec();

        /* Check that the randomType is supported */
        return GordianSP800Type.HASH.equals(myType)
                                                    ? validDigestSpec(mySpec)
                                                    : validHMacSpec(mySpec);
    }

    /**
     * Check DigestSpec.
     * @param pDigestSpec the digestSpec
     * @return true/false
     */
    private boolean validDigestSpec(final GordianDigestSpec pDigestSpec) {
        /* Access details */
        GordianDigestType myType = pDigestSpec.getDigestType();
        GordianLength myStateLen = pDigestSpec.getStateLength();
        GordianLength myLen = pDigestSpec.getDigestLength();

        /* Check validity */
        return supportedDigestTypes().test(myType)
               && myType.isLengthValid(myLen)
               && myType.isStateValidForLength(myStateLen, myLen);
    }

    /**
     * Check HMacSpec.
     * @param pDigestSpec the digestSpec
     * @return true/false
     */
    private boolean validHMacSpec(final GordianDigestSpec pDigestSpec) {
        /* Access details */
        GordianDigestType myType = pDigestSpec.getDigestType();

        /* Check validity */
        return supportedHMacDigestTypes().test(myType)
               && supportedDigestSpecs().test(pDigestSpec);
    }

    /**
     * Check MacSpec.
     * @param pMacSpec the macSpec
     * @return true/false
     */
    private boolean validMacSpec(final GordianMacSpec pMacSpec) {
        /* Access details */
        GordianMacType myType = pMacSpec.getMacType();
        GordianDigestSpec mySpec = pMacSpec.getDigestSpec();
        GordianSymKeyType mySymKey = pMacSpec.getKeyType();

        /* Check that the macType is supported */
        if (!supportedMacTypes().test(myType)) {
            return false;
        }

        /* Switch on MacType */
        switch (myType) {
            case HMAC:
                return supportedHMacDigestSpecs().test(mySpec);
            case GMAC:
                return supportedGMacSymKeyTypes().test(mySymKey);
            case CMAC:
                return supportedCMacSymKeyTypes().test(mySymKey);
            case POLY1305:
                return supportedPoly1305SymKeyTypes().test(mySymKey);
            case SKEIN:
                return supportedDigestSpecs().test(mySpec)
                       && GordianDigestType.SKEIN.equals(mySpec.getDigestType());
            case VMPC:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check SignatureSpec.
     * @param pKeyPair the keyPair
     * @param pSignSpec the macSpec
     * @return true/false
     */
    protected boolean validSignatureSpec(final GordianKeyPair pKeyPair,
                                         final GordianSignatureSpec pSignSpec) {
        /* Access details */
        GordianAsymKeyType myType = pSignSpec.getAsymKeyType();
        GordianSignatureType mySignType = pSignSpec.getSignatureType();
        GordianDigestSpec mySpec = pSignSpec.getDigestSpec();

        /* Check signature matches keyPair */
        if (pSignSpec.getAsymKeyType() != pKeyPair.getKeySpec().getKeyType()) {
            return false;
        }

        /* Check that the signatureType is supported */
        if (!myType.isSignatureAvailable(mySignType)) {
            return false;
        }

        /* Check that the digestSpec is supported */
        if (!validDigestSpec(mySpec)) {
            return false;
        }

        /* Only allow SM3 for SM2 signature */
        if (GordianAsymKeyType.SM2.equals(myType)) {
            return GordianDigestType.SM3.equals(mySpec.getDigestType());
        }

        /* Disallow ECNR if keySize is smaller than digestSize */
        GordianAsymKeySpec myKeySpec = pKeyPair.getKeySpec();
        return !GordianSignatureType.NR.equals(mySignType)
               || myKeySpec.getElliptic().getKeySize() >= mySpec.getDigestLength().getLength();
    }

    /**
     * Check ExchangeSpec.
     * @param pKeyPair the keyPair
     * @param pDigestSpec the digestSpec
     * @return true/false
     */
    protected boolean validExchangeSpec(final GordianKeyPair pKeyPair,
                                        final GordianDigestSpec pDigestSpec) {
        /* Switch on KeyType */
        switch (pKeyPair.getKeySpec().getKeyType()) {
            case RSA:
            case EC:
            case SM2:
            case DIFFIEHELLMAN:
            case NEWHOPE:
                return supportedDigestSpecs().test(pDigestSpec);
            default:
                return false;
        }
    }
}
