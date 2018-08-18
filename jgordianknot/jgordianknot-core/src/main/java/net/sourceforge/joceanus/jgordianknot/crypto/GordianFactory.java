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

import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherSpec.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherSpec.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation.GordianKEMSender;
import net.sourceforge.joceanus.jtethys.OceanusException;

import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * GordianKnot base for Factory.
 */
public abstract class GordianFactory
        implements GordianFactoryGenerator {
    /**
     * The Hash Prime.
     */
    public static final int HASH_PRIME = 37;

    /**
     * RC5 rounds.
     */
    public static final int RC5_ROUNDS = 12;

    /**
     * Restricted key length.
     */
    private static final int SMALL_KEYLEN = GordianLength.LEN_128.getLength();

    /**
     * Unlimited key length.
     */
    protected static final int BIG_KEYLEN = GordianLength.LEN_256.getLength();

    /**
     * The number of seed bytes.
     */
    private static final int SEED_SIZE = GordianLength.LEN_256.getByteLength();

    /**
     * Array for Max Cipher Steps.
     */
    private static final int[] MAX_CIPHER_STEPS;

    /**
     * Static Constructor.
     */
    static {
        /* Calculate max cipher Steps */
        MAX_CIPHER_STEPS = new int[2];
        MAX_CIPHER_STEPS[0] = determineMaximumCipherSteps(false);
        MAX_CIPHER_STEPS[1] = determineMaximumCipherSteps(true);
    }

    /**
     * Parameters.
     */
    private final GordianParameters theParameters;

    /**
     * FactoryGenerator.
     */
    private final GordianFactoryGenerator theGenerator;

    /**
     * Do we use restricted keys?
     */
    private final boolean isRestricted;

    /**
     * Personalisation.
     */
    private final GordianPersonalisation thePersonalisation;

    /**
     * SignatureId.
     */
    private GordianSignatureAlgId theSignatureId;

    /**
     * AsymAlgId.
     */
    private GordianAsymAlgId theAsymAlgId;

    /**
     * Obfuscater.
     */
    private GordianKnuthObfuscater theKnuth;

    /**
     * SecureRandom instance.
     */
    private GordianSecureRandom theRandom;

    /**
     * The Id Manager.
     */
    private GordianIdManager theIdManager;

    /**
     * Constructor.
     * @param pParameters the parameters
     * @param pGenerator the factoryGenerator
     * @throws OceanusException on error
     */
    protected GordianFactory(final GordianParameters pParameters,
                             final GordianFactoryGenerator pGenerator) throws OceanusException {
        /* Store parameters */
        theParameters = pParameters;
        theGenerator = pGenerator;
        isRestricted = theParameters.useRestricted();

        /* Calculate personalisation bytes */
        thePersonalisation = new GordianPersonalisation(this, theParameters);
    }

    @Override
    public GordianFactory newFactory(final GordianParameters pParams) throws OceanusException {
        return theGenerator.newFactory(pParams);
    }

    /**
     * Is the factory restricted?
     * @return true/false
     */
    public boolean isRestricted() {
        return isRestricted;
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
        return theRandom.getRandom();
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
     * Obtain the signatureIdManager.
     * @return the signatureIdManager
     */
    protected GordianSignatureAlgId getSignatureIdManager() {
        if (theSignatureId == null) {
            theSignatureId = new GordianSignatureAlgId(this);
        }
        return theSignatureId;
    }

    /**
     * Obtain the asymAlgorithmIdManager.
     * @return the algorithmIdManager
     */
    GordianAsymAlgId getAlgorithmIdManager() {
        if (theAsymAlgId == null) {
            theAsymAlgId = new GordianAsymAlgId();
        }
        return theAsymAlgId;
    }

    /**
     * Obtain the obfuscater.
     * @return the obfuscater
     */
    public GordianKnuthObfuscater getObfuscater() {
        if (theKnuth == null) {
            theKnuth = new GordianKnuthObfuscater(getIdManager(), thePersonalisation);
        }
        return theKnuth;
    }

    /**
     * Obtain the personalisation bytes.
     * @return the personalisation
     */
    protected GordianPersonalisation getPersonalisation() {
        return thePersonalisation;
    }

    /**
     * Create a random randomSpec.
     * @return the randomSpec
     */
    public GordianRandomSpec generateRandomSpec() {
        /* Access the random generator */
        final SecureRandom myRandom = getRandom();

        /* Determine the type of random generator */
        final boolean isHMac = myRandom.nextBoolean();
        final GordianRandomType myType = isHMac
                                                ? GordianRandomType.HMAC
                                                : GordianRandomType.HASH;
        final Predicate<GordianDigestSpec> myPredicate = isHMac
                                                                ? supportedHMacDigestSpecs()
                                                                : supportedDigestSpecs();

        /* Access the digestTypes */
        final GordianDigestType[] myDigestTypes = GordianDigestType.values();

        /* Keep looping until we find a valid digest */
        for (;;) {
            /* Obtain the candidate DigestSpec */
            final int myInt = myRandom.nextInt(myDigestTypes.length);
            final GordianDigestType myDigestType = myDigestTypes[myInt];
            final GordianDigestSpec mySpec = new GordianDigestSpec(myDigestType, GordianLength.LEN_512);

            /* If this is a valid digestSpec, return it */
            if (myPredicate.test(mySpec)) {
                return new GordianRandomSpec(myType, new GordianDigestSpec(myDigestTypes[myInt]));
            }
        }
    }

    /**
     * Set the secureRandom instance.
     * @param pRandom the secureRandom instance
     */
    protected void setSecureRandom(final GordianSecureRandom pRandom) {
        theRandom = pRandom;
        getIdManager().setSecureRandom(pRandom.getRandom());
    }

    /**
     * ReSeed the random number generator.
     */
    public void reSeedRandom() {
        /* Generate and apply the new seed */
        final byte[] mySeed = theRandom.generateSeed(SEED_SIZE);
        theRandom.setSeed(mySeed);
        theRandom.reseed((byte[]) null);
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
    public abstract <X> String getKeyAlgorithm(X pKeyType) throws OceanusException;

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
        return GordianKeySetHash.newKeySetHash(this, pPassword);
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
        return GordianKeySetHash.resolveKeySetHash(this, pHashBytes, pPassword);
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
        /* Keep looping until we find a valid digest */
        for (;;) {
            final GordianDigestType myType = getIdManager().generateRandomDigestType();
            final GordianDigestSpec mySpec = new GordianDigestSpec(myType);
            if (supportedDigestSpecs().test(mySpec)) {
                return createDigest(new GordianDigestSpec(myType));
            }
        }
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
    public Predicate<GordianDigestType> supportedDigestTypes() {
        return this::validDigestType;
    }

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
     * generate random GordianMac.
     * @return the new MAC
     * @throws OceanusException on error
     */
    public GordianMac generateRandomMac() throws OceanusException {
        /* Determine a random specification */
        final GordianMacSpec mySpec = getIdManager().generateRandomMacSpec();

        /* Determine a random key */
        final GordianKeyGenerator<GordianMacSpec> myGenerator = getKeyGenerator(mySpec);
        final GordianKey<GordianMacSpec> myKey = myGenerator.generateKey();

        /* Create and initialise the MAC */
        final GordianMac myMac = createMac(mySpec);
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
    public Predicate<GordianMacType> supportedMacTypes() {
        return this::validMacType;
    }

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
    public Predicate<GordianDigestType> supportedHMacDigestTypes() {
        return this::validHMacDigestType;
    }

    /**
     * Obtain predicate for supported poly1305 symKeySpecs.
     * @return the predicate
     */
    public Predicate<GordianSymKeySpec> supportedPoly1305SymKeySpecs() {
        return p -> validPoly1305SymKeySpec(p)
                    && p.getBlockLength() == GordianLength.LEN_128;
    }

    /**
     * Obtain predicate for supported gMac symKeySpecs.
     * @return the predicate
     */
    public Predicate<GordianSymKeySpec> supportedGMacSymKeySpecs() {
        return p -> validGMacSymKeySpec(p)
                    && p.getBlockLength() == GordianLength.LEN_128;
    }

    /**
     * Obtain predicate for supported cMac symKeyTypes.
     * @return the predicate
     */
    public Predicate<GordianSymKeySpec> supportedCMacSymKeySpecs() {
        return this::validCMacSymKeySpec;
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
     * Determine KeySpec from PKCS8EncodedKeySpec.
     * @param pEncoded the encodedKeySpec
     * @return the keySpec
     * @throws OceanusException on error
     */
    GordianAsymKeySpec determineKeySpec(final PKCS8EncodedKeySpec pEncoded) throws OceanusException {
        final GordianAsymAlgId myAlgId = getAlgorithmIdManager();
        return myAlgId.determineKeySpec(pEncoded);
    }

    /**
     * Determine KeySpec from X509EncodedKeySpec.
     * @param pEncoded the encodedKeySpec
     * @return the keySpec
     * @throws OceanusException on error
     */
    public  GordianAsymKeySpec determineKeySpec(final X509EncodedKeySpec pEncoded) throws OceanusException {
        final GordianAsymAlgId myAlgId = getAlgorithmIdManager();
        return myAlgId.determineKeySpec(pEncoded);
    }

    /**
     * generate random SymKey.
     * @return the new key
     * @throws OceanusException on error
     */
    public GordianKey<GordianSymKeySpec> generateRandomSymKey() throws OceanusException {
        /* Determine a random keyType */
        final GordianSymKeyType myType = getIdManager().generateRandomSymKeyType();

        /* Generate a random key */
        final GordianKeyGenerator<GordianSymKeySpec> myGenerator = getKeyGenerator(new GordianSymKeySpec(myType));
        return myGenerator.generateKey();
    }

    /**
     * generate random SymKeyList.
     * @return the list of keys
     * @throws OceanusException on error
     */
    public List<GordianKey<GordianSymKeySpec>> generateRandomSymKeyList() throws OceanusException {
        /* Determine a random set of keyType */
        final int myCount = getNumCipherSteps() - 1;
        final GordianSymKeyType[] myTypes = getIdManager().generateRandomKeySetSymKeyTypes(myCount);

        /* Loop through the keys */
        final List<GordianKey<GordianSymKeySpec>> myKeyList = new ArrayList<>();
        for (int i = 0; i < myCount; i++) {
            /* Generate a random key */
            final GordianSymKeyType myType = myTypes[i];
            final GordianKeyGenerator<GordianSymKeySpec> myGenerator = getKeyGenerator(new GordianSymKeySpec(myType));
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
    public abstract GordianCipher<GordianSymKeySpec> createSymKeyCipher(GordianSymCipherSpec pCipherSpec) throws OceanusException;

    /**
     * Obtain predicate for supported symKeySpecs.
     * @return the predicate
     */
    public Predicate<GordianSymKeySpec> supportedSymKeySpecs() {
        return this::validSymKeySpec;
    }

    /**
     * Obtain predicate for supported sumCipherSpecs.
     * @return the predicate
     */
    public BiPredicate<GordianSymCipherSpec, Boolean> supportedSymCipherSpecs() {
        return this::validSymCipherSpec;
    }

    /**
     * Obtain predicate for supported SymKeyTypes.
     * @return the predicate
     */
    public Predicate<GordianSymKeyType> supportedSymKeyTypes() {
        return this::validSymKeyType;
    }

    /**
     * Obtain predicate for keySet SymKeyTypes.
     * @return the predicate
     */
    public Predicate<GordianSymKeyType> supportedKeySetSymKeyTypes() {
        return this::validKeySetSymKeyType;
    }

    /**
     * Obtain predicate for supported poly1305 symKeySpecs.
     * @return the predicate
     */
    public Predicate<GordianSymKeySpec> supportedKeySetSymKeySpecs() {
        return p -> supportedKeySetSymKeyTypes().test(p.getSymKeyType())
                    && p.getBlockLength() == GordianLength.LEN_128;
    }

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
        final GordianStreamKeyType myType = generateRandomStreamKeyType();

        /* Generate a random key */
        final GordianKeyGenerator<GordianStreamKeyType> myGenerator = getKeyGenerator(myType);
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
    public Predicate<GordianStreamKeyType> supportedStreamKeyTypes() {
        return this::validStreamKeyType;
    }

    /**
     * create GordianWrapCipher.
     * @param pKeySpec the KeySpec
     * @return the new Cipher
     * @throws OceanusException on error
     */
    public abstract GordianWrapCipher createWrapCipher(GordianSymKeySpec pKeySpec) throws OceanusException;

    /**
     * Create a wrapCipher.
     * @param pBlockCipher the underlying block cipher
     * @return the wrapCipher
     */
    protected GordianWrapCipher createWrapCipher(final GordianCipher<GordianSymKeySpec> pBlockCipher) {
        return new GordianWrapCipher(this, pBlockCipher);
    }

    /**
     * Obtain predicate for signatures.
     * @return the predicate
     */
    public abstract Predicate<GordianSignatureSpec> supportedSignatureSpec();

    /**
     * Create signer.
     * @param pSignatureSpec the signatureSpec
     * @return the signer
     * @throws OceanusException on error
     */
    public abstract GordianSignature createSigner(GordianSignatureSpec pSignatureSpec) throws OceanusException;

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
        final StringBuilder myBuilder = new StringBuilder();
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
        final GordianFactory myThat = (GordianFactory) pThat;

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
        final GordianRandomType myType = pRandomSpec.getRandomType();
        final GordianDigestSpec mySpec = pRandomSpec.getDigestSpec();

        /* Check that the randomType is supported */
        return GordianRandomType.HASH.equals(myType)
                                                     ? validDigestSpec(mySpec)
                                                     : validHMacSpec(mySpec);
    }

    /**
     * Check DigestType.
     * @param pDigestType the digestType
     * @return true/false
     */
    protected boolean validDigestType(final GordianDigestType pDigestType) {
        return true;
    }

    /**
     * Check MacType.
     * @param pMacType the macType
     * @return true/false
     */
    protected boolean validMacType(final GordianMacType pMacType) {
        return true;
    }

    /**
     * Check HMacDigestType.
     * @param pDigestType the digestType
     * @return true/false
     */
    protected boolean validHMacDigestType(final GordianDigestType pDigestType) {
        return validDigestType(pDigestType);
    }

    /**
     * Check DigestSpec.
     * @param pDigestSpec the digestSpec
     * @return true/false
     */
    protected boolean validDigestSpec(final GordianDigestSpec pDigestSpec) {
        /* Access details */
        final GordianDigestType myType = pDigestSpec.getDigestType();
        final GordianLength myStateLen = pDigestSpec.getStateLength();
        final GordianLength myLen = pDigestSpec.getDigestLength();

        /* Check validity */
        return supportedDigestTypes().test(myType)
               && myType.isLengthValid(myLen)
               && myType.isStateValidForLength(myStateLen, myLen);
    }

    /**
     * Check SignatureDigestSpec.
     * @param pDigestSpec the digestSpec
     * @return true/false
     */
    protected boolean validSignatureDigestSpec(final GordianDigestSpec pDigestSpec) {
        return validDigestSpec(pDigestSpec);
    }

    /**
     * Check HMacSpec.
     * @param pDigestSpec the digestSpec
     * @return true/false
     */
    protected boolean validHMacSpec(final GordianDigestSpec pDigestSpec) {
        /* Access details */
        final GordianDigestType myType = pDigestSpec.getDigestType();

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
        final GordianMacType myType = pMacSpec.getMacType();

        /* Check that the macType is supported */
        if (!supportedMacTypes().test(myType)) {
            return false;
        }

        /* Switch on MacType */
        final GordianDigestSpec mySpec = pMacSpec.getDigestSpec();
        final GordianSymKeySpec mySymSpec = pMacSpec.getKeySpec();
        switch (myType) {
            case HMAC:
                return supportedHMacDigestSpecs().test(mySpec);
            case GMAC:
                return supportedGMacSymKeySpecs().test(mySymSpec);
            case CMAC:
                return supportedCMacSymKeySpecs().test(mySymSpec);
            case POLY1305:
                return supportedPoly1305SymKeySpecs().test(mySymSpec);
            case SKEIN:
                return GordianDigestType.SKEIN.equals(mySpec.getDigestType())
                       && supportedDigestSpecs().test(mySpec);
            case BLAKE:
                return GordianDigestType.BLAKE.equals(mySpec.getDigestType())
                       && supportedDigestSpecs().test(mySpec);
            case KUPYNA:
                return GordianDigestType.KUPYNA.equals(mySpec.getDigestType())
                       && supportedDigestSpecs().test(mySpec);
            case KALYNA:
                return GordianSymKeyType.KALYNA.equals(mySymSpec.getSymKeyType())
                       && validSymKeySpec(mySymSpec);
            case VMPC:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine supported Poly1305 algorithms.
     * @param pKeySpec the keySpec
     * @return true/false
     */
    protected boolean validPoly1305SymKeySpec(final GordianSymKeySpec pKeySpec) {
        switch (pKeySpec.getSymKeyType()) {
            case KUZNYECHIK:
            case RC5:
                return false;
            default:
                return validSymKeySpec(pKeySpec);
        }
    }

    /**
     * Determine supported GMAC algorithms.
     * @param pKeySpec the keySpec
     * @return true/false
     */
    protected boolean validGMacSymKeySpec(final GordianSymKeySpec pKeySpec) {
        return validCMacSymKeySpec(pKeySpec);
    }

    /**
     * Determine supported CMAC algorithms.
     * @param pKeySpec the keySpec
     * @return true/false
     */
    protected boolean validCMacSymKeySpec(final GordianSymKeySpec pKeySpec) {
        if (GordianSymKeyType.RC5.equals(pKeySpec.getSymKeyType())) {
            return false;
        }
        return validSymKeySpec(pKeySpec);
    }

    /**
     * Check SymKeySpec.
     * @param pSymKeySpec the symKeySpec
     * @return true/false
     */
    protected boolean validSymKeySpec(final GordianSymKeySpec pSymKeySpec) {
        /* Access details */
        final GordianLength myLen = pSymKeySpec.getBlockLength();

        /* Reject restrictedSpecs where the block length is too large */
        if (isRestricted
            && myLen.getLength() > GordianLength.LEN_128.getLength()) {
            return false;
        }

        /* Reject Speck-64 for unrestricted */
        if (!isRestricted
            && GordianSymKeyType.SPECK.equals(pSymKeySpec.getSymKeyType())
            && GordianLength.LEN_64.equals(myLen)) {
            return false;
        }

        /* Check validity */
        final GordianSymKeyType myType = pSymKeySpec.getSymKeyType();
        return supportedSymKeyTypes().test(myType)
               && myType.isLengthValid(myLen);
    }

    /**
     * Check SymKeyType.
     * @param pKeyType the symKeyType
     * @return true/false
     */
    protected boolean validSymKeyType(final GordianSymKeyType pKeyType) {
        return validSymKeyTypeForRestriction(pKeyType, isRestricted);
    }

    /**
     * Check SymKeyType.
     * @param pKeyType the symKeyType
     * @param pRestricted is the symKeyType restricted?
     * @return true/false
     */
    protected static boolean validSymKeyTypeForRestriction(final GordianSymKeyType pKeyType,
                                                           final boolean pRestricted) {
        return pKeyType.validForRestriction(pRestricted);
    }

    /**
     * Generate keySet symKeyType.
     * @param pKeyType the symKeyType
     * @return true/false
     */
    protected boolean validKeySetSymKeyType(final GordianSymKeyType pKeyType) {
        return validSymKeyType(pKeyType) && validKeySetSymKeyTypeForRestriction(pKeyType, isRestricted);
    }

    /**
     * Generate keySet symKeyType.
     * @param pKeyType the symKeyType
     * @param pRestricted is the symKeyType restricted?
     * @return true/false
     */
    protected static boolean validKeySetSymKeyTypeForRestriction(final GordianSymKeyType pKeyType,
                                                                 final boolean pRestricted) {
        return validSymKeyTypeForRestriction(pKeyType, pRestricted)
               && pKeyType.getDefaultLength().equals(GordianLength.LEN_128);
    }

    /**
     * Obtain maximum cipherSteps.
     * @param pRestricted are keys restricted
     * @return the maximum
     */
    public static int getMaximumCipherSteps(final boolean pRestricted) {
        return MAX_CIPHER_STEPS[pRestricted
                                            ? 1
                                            : 0];
    }

    /**
     * Determine maximum cipherSteps.
     * @param pRestricted are keys restricted?
     * @return the maximum
     */
    private static int determineMaximumCipherSteps(final boolean pRestricted) {
        /* Count valid values */
        int myCount = 0;
        for (final GordianSymKeyType myType : GordianSymKeyType.values()) {
            if (validKeySetSymKeyTypeForRestriction(myType, pRestricted)) {
                myCount++;
            }
        }

        /* Maximum is 1 less than the count */
        return myCount - 1;
    }

    /**
     * Check StreamKeyType.
     * @param pKeyType the streamKeyType
     * @return true/false
     */
    protected boolean validStreamKeyType(final GordianStreamKeyType pKeyType) {
        return pKeyType.validForRestriction(isRestricted);
    }

    /**
     * Check SignatureSpec and KeyPair combination.
     * @param pKeyPair the keyPair
     * @param pSignSpec the macSpec
     * @return true/false
     */
    public boolean validSignatureSpecForKeyPair(final GordianKeyPair pKeyPair,
                                                final GordianSignatureSpec pSignSpec) {
        /* Check signature matches keyPair */
        if (pSignSpec.getAsymKeyType() != pKeyPair.getKeySpec().getKeyType()) {
            return false;
        }


        /* Check that the signatureSpec is supported */
        if (!validSignatureSpec(pSignSpec)) {
            return false;
        }

        /* Disallow ECNR if keySize is smaller than digestSize */
        final GordianAsymKeySpec myKeySpec = pKeyPair.getKeySpec();
        if (GordianSignatureType.NR.equals(pSignSpec.getSignatureType())) {
            return myKeySpec.getElliptic().getKeySize() >= pSignSpec.getDigestSpec().getDigestLength().getLength();
        }

        /* Disallow incorrectly sized digest for GOST */
        if (GordianAsymKeyType.GOST2012.equals(myKeySpec.getKeyType())) {
            final int myDigestLen = pSignSpec.getDigestSpec().getDigestLength().getLength();
            return myKeySpec.getElliptic().getKeySize() == myDigestLen;
        }

        /* OK */
        return true;
    }

    /**
     * Check SignatureSpec.
     * @param pSignSpec the macSpec
     * @return true/false
     */
    protected boolean validSignatureSpec(final GordianSignatureSpec pSignSpec) {
        /* Check that the signatureType is supported */
        final GordianAsymKeyType myType = pSignSpec.getAsymKeyType();
        final GordianSignatureType mySignType = pSignSpec.getSignatureType();
        if (!myType.isSignatureAvailable(mySignType)) {
            return false;
        }

        /* Check that the digestSpec is supported */
        final GordianDigestSpec mySpec = pSignSpec.getDigestSpec();
        if (!validSignatureDigestSpec(mySpec)) {
            return false;
        }

        /* Only allow SM3 for SM2 signature */
        if (GordianAsymKeyType.SM2.equals(myType)) {
            return GordianDigestType.SM3.equals(mySpec.getDigestType());
        }

        /* Only allow GOST for DSTU signature */
        if (GordianAsymKeyType.DSTU4145.equals(myType)) {
            return GordianDigestType.GOST.equals(mySpec.getDigestType());
        }

        /* Only allow STREEBOG for GOST signature */
        if (GordianAsymKeyType.GOST2012.equals(myType)) {
            return GordianDigestType.STREEBOG.equals(mySpec.getDigestType());
        }

        /* OK */
        return true;
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

    /**
     * validate the symCipherSpec.
     * @param pCipherSpec the cipherSpec.
     * @param isAAD is this cipherSpec for an AADCipher?
     * @return true/false
     */
    protected boolean validSymCipherSpec(final GordianSymCipherSpec pCipherSpec,
                                         final Boolean isAAD) {
        /* Reject null modes and wrong AAD modes */
        final GordianCipherMode myMode = pCipherSpec.getCipherMode();
        if (myMode == null
            || isAAD != myMode.isAAD()) {
            return false;
        }

        /* Check that the mode is valid for the keyType */
        final GordianSymKeySpec myKeySpec = pCipherSpec.getKeyType();
        final GordianSymKeyType myKeyType = myKeySpec.getSymKeyType();
        if (!myMode.validForSymKey(myKeyType)) {
            return false;
        }

        /* Disallow AAD for RC5-64 */
        if (GordianSymKeyType.RC5.equals(myKeyType)
            && GordianLength.LEN_128.equals(myKeySpec.getBlockLength())
            && myMode.isAAD()) {
            return false;
        }

        /* Determine whether we have a short block length */
        final int myLen = myKeySpec.getBlockLength().getLength();
        final boolean shortBlock = myLen < GordianLength.LEN_128.getLength();

        /* Reject modes which do not allow short blocks */
        if (shortBlock && !myMode.allowShortBlock()) {
            return false;
        }

        /* Reject modes which do not allow non-standard blocks */
        final boolean stdBlock = myLen == GordianLength.LEN_128.getLength();
        if (!stdBlock && myMode.needsStdBlock()) {
            return false;
        }

        /* Reject bad padding */
        final GordianPadding myPadding = pCipherSpec.getPadding();
        return myMode.hasPadding()
                                   ? myPadding != null
                                   : GordianPadding.NONE.equals(myPadding);
    }
}
