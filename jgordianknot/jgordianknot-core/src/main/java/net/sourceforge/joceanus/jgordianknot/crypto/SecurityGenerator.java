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
import java.util.function.Predicate;

import net.sourceforge.joceanus.jtethys.DataConverter;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Generator class for various security primitives.
 */
public class SecurityGenerator {
    /**
     * The Base personalisation.
     */
    protected static final String BASE_PERSONAL = "jG0rd1anKn0t";

    /**
     * The Hash prime.
     */
    protected static final int HASH_PRIME = 19;

    /**
     * The number of seed bytes.
     */
    protected static final int SEED_SIZE = 32;

    /**
     * Restricted key length.
     */
    protected static final int SMALL_KEYLEN = 128;

    /**
     * Unlimited key length.
     */
    protected static final int BIG_KEYLEN = 256;

    /**
     * The Security provider.
     */
    private final SecurityProvider theProvider;

    /**
     * Do we use restricted keys?
     */
    private final boolean useRestricted;

    /**
     * The Number of cipher steps.
     */
    private final int theCipherSteps;

    /**
     * The Number of hash iterations.
     */
    private final int theIterations;

    /**
     * The Number of Active KeySets.
     */
    private final int theNumActiveKeySets;

    /**
     * The Personalisation bytes.
     */
    private final byte[] thePersonalisation;

    /**
     * The Security provider name.
     */
    private final String theProviderName;

    /**
     * The Base Hash algorithm.
     */
    private final DigestType theHashAlgorithm;

    /**
     * The Secure Random builder.
     */
    private final SP800SecureRandomBuilder theRandomBuilder;

    /**
     * The Secure Random generator.
     */
    private final SecureRandom theRandom;

    /**
     * Security Id Manager.
     */
    private final SecurityIdManager theIdManager;

    /**
     * Security Register.
     */
    private final SecurityRegister theRegister;

    /**
     * The SymKey predicate.
     */
    private final Predicate<SymKeyType> theSymPredicate;

    /**
     * The MacSymKey predicate.
     */
    private final Predicate<SymKeyType> theMacSymPredicate;

    /**
     * The StreamKey predicate.
     */
    private final Predicate<StreamKeyType> theStreamPredicate;

    /**
     * The Digest predicate.
     */
    private final Predicate<DigestType> theDigestPredicate;

    /**
     * Default Constructor.
     * @throws JOceanusException on error
     */
    public SecurityGenerator() throws JOceanusException {
        this(new SecurityParameters());
    }

    /**
     * Constructor for explicit provider.
     * @param pParameters the Security parameters
     * @throws JOceanusException on error
     */
    public SecurityGenerator(final SecurityParameters pParameters) throws JOceanusException {
        /* Store the provider */
        theProvider = pParameters.getProvider();
        theProviderName = theProvider.getProvider();

        /* Ensure that the provider is installed */
        theProvider.ensureInstalled();

        /* Store parameters */
        useRestricted = pParameters.useRestricted();
        theHashAlgorithm = pParameters.getBaseHashAlgorithm();
        theCipherSteps = pParameters.getNumCipherSteps();
        theIterations = pParameters.getNumHashIterations();
        theNumActiveKeySets = pParameters.getNumActiveKeySets();

        /* Calculate personalisation bytes */
        DataDigest myDigest = generateDigest(theHashAlgorithm);
        String myPhrase = pParameters.getSecurityPhrase();
        myDigest.update(DataConverter.stringToByteArray(BASE_PERSONAL));
        if (myPhrase != null) {
            myDigest.update(DataConverter.stringToByteArray(myPhrase));
        }
        thePersonalisation = myDigest.finish();

        /* Create the random builder */
        theRandomBuilder = new SP800SecureRandomBuilder();
        theRandomBuilder.setSecurityBytes(thePersonalisation);

        /* Create a new secure random generator */
        theRandom = generateHashSecureRandom(theHashAlgorithm, false);

        /* Create the register */
        theRegister = new SecurityRegister(this);

        /* Create the id manager */
        theIdManager = new SecurityIdManager(this);

        /* Determine the Predicates */
        theSymPredicate = SymKeyType.allForKeyLen(!useRestricted);
        theMacSymPredicate = SymKeyType.allMacForKeyLen(!useRestricted);
        theStreamPredicate = StreamKeyType.allForKeyLen(!useRestricted);
        theDigestPredicate = DigestType.allSupported();
    }

    /**
     * Access the Security provider.
     * @return the security provider
     */
    protected SecurityProvider getProvider() {
        return theProvider;
    }

    /**
     * Access the Security provider name.
     * @return the security provider name
     */
    protected String getProviderName() {
        return theProviderName;
    }

    /**
     * Access the Secure Random.
     * @return the secure random
     */
    public SecureRandom getRandom() {
        return theRandom;
    }

    /**
     * Access the base hash algorithm.
     * @return the hash algorithm
     */
    protected DigestType getBaseHashAlgorithm() {
        return theHashAlgorithm;
    }

    /**
     * Access the Security Id Manager.
     * @return the idManager
     */
    protected SecurityIdManager getIdManager() {
        return theIdManager;
    }

    /**
     * Access the Security Register.
     * @return the register
     */
    protected SecurityRegister getRegister() {
        return theRegister;
    }

    /**
     * Access the number of Hash Iterations.
     * @return the number of hash iterations
     */
    protected int getNumHashIterations() {
        return theIterations;
    }

    /**
     * Access the personalisation bytes.
     * @return the personalisation bytes
     */
    protected byte[] getPersonalisation() {
        return thePersonalisation;
    }

    /**
     * Access the number of Cipher Steps.
     * @return the number of cipher steps
     */
    public int getNumCipherSteps() {
        return theCipherSteps;
    }

    /**
     * Access the number of Active KeySets.
     * @return the number of active KeySets
     */
    public int getNumActiveKeySets() {
        return theNumActiveKeySets;
    }

    /**
     * Do we use restricted security.
     * @return true/false
     */
    protected boolean useRestricted() {
        return useRestricted;
    }

    /**
     * Obtain SymKeyPredicate.
     * @return the predicate
     */
    public Predicate<SymKeyType> getSymKeyPredicate() {
        return theSymPredicate;
    }

    /**
     * Obtain MacSymKeyPredicate.
     * @return the predicate
     */
    public Predicate<SymKeyType> getMacSymKeyPredicate() {
        return theMacSymPredicate;
    }

    /**
     * Obtain StreamKeyPredicate.
     * @return the predicate
     */
    public Predicate<StreamKeyType> getStreamKeyPredicate() {
        return theStreamPredicate;
    }

    /**
     * Obtain DigestPredicate.
     * @return the predicate
     */
    public Predicate<DigestType> getDigestPredicate() {
        return theDigestPredicate;
    }

    /**
     * Determine key length.
     * @return key length
     */
    protected int getKeyLen() {
        return useRestricted
                             ? SMALL_KEYLEN
                             : BIG_KEYLEN;
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
     * Obtain random bytes.
     * @param pNumBytes the number of bytes to obtain
     * @return the random bytes
     */
    public byte[] getRandomBytes(final int pNumBytes) {
        /* Generate and apply the new seed */
        byte[] myBytes = new byte[pNumBytes];
        theRandom.nextBytes(myBytes);
        return myBytes;
    }

    /**
     * Generate an SP800HashDRBG SecureRandom.
     * @param pType the digest type
     * @param isPredictionResistant true/false
     * @return the SecureRandom
     * @throws JOceanusException on error
     */
    public final SecureRandom generateHashSecureRandom(final DigestType pType,
                                                       final boolean isPredictionResistant) throws JOceanusException {
        /* Create the digest */
        DataDigest myDigest = generateDigest(pType);

        /* Create the new SecureRandom */
        return theRandomBuilder.buildHash(myDigest, null, isPredictionResistant);
    }

    /**
     * Generate an SP800HMacDRBG SecureRandom.
     * @param pType the digest type
     * @param pPassword the password in byte format
     * @param isPredictionResistant true/false
     * @return the SecureRandom
     * @throws JOceanusException on error
     */
    public final SecureRandom generateHMacSecureRandom(final DigestType pType,
                                                       final byte[] pPassword,
                                                       final boolean isPredictionResistant) throws JOceanusException {
        /* Create the mac */
        DataMac myMac = generateMac(pType, pPassword);

        /* Create the new SecureRandom */
        return theRandomBuilder.buildHMAC(myMac, null, isPredictionResistant);
    }

    /**
     * Generate a password Hash for the given password.
     * @param pPassword the password
     * @return the Password hash
     * @throws JOceanusException on error
     */
    public PasswordHash generatePasswordHash(final char[] pPassword) throws JOceanusException {
        /* Create the new Password Hash */
        return new PasswordHash(this, pPassword);
    }

    /**
     * Derive a password Hash for the given hash and password.
     * @param pHashBytes the hash bytes
     * @param pPassword the password
     * @return the Password hash
     * @throws JOceanusException on error
     * @throws InvalidCredentialsException if password does not match
     */
    public PasswordHash derivePasswordHash(final byte[] pHashBytes,
                                           final char[] pPassword) throws JOceanusException {
        /* Create the new Password Hash */
        return new PasswordHash(this, pHashBytes, pPassword);
    }

    /**
     * Determine a list of random symmetric key types.
     * @return the Symmetric Key types
     * @throws JOceanusException on error
     */
    public SymKeyType[] generateSymKeyTypes() throws JOceanusException {
        /* Determine new SymKeyTypes */
        return theIdManager.getRandomSymKeyTypes(getNumCipherSteps(), theSymPredicate);
    }

    /**
     * Generate a new Symmetric Key for the required KeyType.
     * @param pKeyType the Symmetric Key type
     * @return the newly created Symmetric Key
     * @throws JOceanusException on error
     */
    public SymmetricKey generateSymmetricKey(final SymKeyType pKeyType) throws JOceanusException {
        /* Create the new Symmetric Key */
        return SymmetricKey.generateSymmetricKey(this, pKeyType);
    }

    /**
     * Generate a new Symmetric Key of a random type.
     * @return the newly created Symmetric Key
     * @throws JOceanusException on error
     */
    public SymmetricKey generateSymmetricKey() throws JOceanusException {
        /* Create the new Symmetric Key */
        return SymmetricKey.generateSymmetricKey(this);
    }

    /**
     * Obtain external SymKeyId.
     * @param pKey the symKeyType
     * @return the external id
     */
    public int getExternalId(final SymKeyType pKey) {
        return theIdManager.getExternalId(pKey);
    }

    /**
     * Obtain symKeyType from external SymKeyId.
     * @param pId the external id
     * @return the symKeyType
     */
    public SymKeyType deriveSymKeyTypeFromExternalId(final int pId) {
        return theIdManager.deriveSymKeyTypeFromExternalId(pId, theSymPredicate);
    }

    /**
     * Generate a new Stream Key for the required KeyType.
     * @param pKeyType the Stream Key type
     * @return the newly created Stream Key
     * @throws JOceanusException on error
     */
    public StreamKey generateStreamKey(final StreamKeyType pKeyType) throws JOceanusException {
        /* Create the new Stream Key */
        return StreamKey.generateStreamKey(this, pKeyType);
    }

    /**
     * Generate a new Stream Key of a random type.
     * @return the newly created Stream Key
     * @throws JOceanusException on error
     */
    public StreamKey generateStreamKey() throws JOceanusException {
        /* Create the new Stream Key */
        return StreamKey.generateStreamKey(this);
    }

    /**
     * Obtain external StreamKeyId.
     * @param pKey the streamKeyType
     * @return the external id
     */
    public int getExternalId(final StreamKeyType pKey) {
        return theIdManager.getExternalId(pKey);
    }

    /**
     * Obtain streamKeyType from external StreamKeyId.
     * @param pId the external id
     * @return the streamKeyType
     */
    public StreamKeyType deriveStreamKeyTypeFromExternalId(final int pId) {
        return theIdManager.deriveStreamKeyTypeFromExternalId(pId, theStreamPredicate);
    }

    /**
     * Generate a new Asymmetric Key of a specified type.
     * @param pKeyType the Asymmetric Key type
     * @return the newly created Asymmetric Key
     * @throws JOceanusException on error
     */
    public AsymmetricKey generateAsymmetricKey(final AsymKeyType pKeyType) throws JOceanusException {
        /* Create the new Asymmetric Key */
        return AsymmetricKey.generateAsymmetricKey(this, pKeyType);
    }

    /**
     * Generate a new Asymmetric Key of a random type.
     * @return the newly created Asymmetric Key
     * @throws JOceanusException on error
     */
    public AsymmetricKey generateAsymmetricKey() throws JOceanusException {
        /* Create the new Asymmetric Key */
        return AsymmetricKey.generateAsymmetricKey(this);
    }

    /**
     * Generate a new Elliptic Asymmetric Key of a specified type.
     * @return the newly created Asymmetric Key
     * @throws JOceanusException on error
     */
    public AsymmetricKey generateEllipticAsymmetricKey() throws JOceanusException {
        /* Create the new Asymmetric Key */
        return AsymmetricKey.generateEllipticAsymmetricKey(this);
    }

    /**
     * Generate a new Asymmetric Key of the same type as the partner.
     * @param pPartner the partner asymmetric key
     * @return the newly created Asymmetric Key
     * @throws JOceanusException on error
     */
    public AsymmetricKey generateAsymmetricKey(final AsymmetricKey pPartner) throws JOceanusException {
        /* Determine the new keyMode */
        byte[] myExternalPublic = pPartner.getExternalPublic();

        /* Create the new Asymmetric Key */
        return new AsymmetricKey(this, myExternalPublic);
    }

    /**
     * Obtain a DataDigest of the specified type.
     * @param pDigestType the digest type required
     * @return the DataDigest
     * @throws JOceanusException on error
     */
    public final DataDigest generateDigest(final DigestType pDigestType) throws JOceanusException {
        /* Return a digest for the algorithm */
        return new DataDigest(this, pDigestType);
    }

    /**
     * Obtain a DataDigest of a random type.
     * @return the DataDigest
     * @throws JOceanusException on error
     */
    public final DataDigest generateDigest() throws JOceanusException {
        /* Return a random digest */
        return DataDigest.generateRandomDigest(this);
    }

    /**
     * Obtain external DigestId.
     * @param pDigest the digestType
     * @return the external id
     */
    public int getExternalId(final DigestType pDigest) {
        return theIdManager.getExternalId(pDigest);
    }

    /**
     * Obtain digestType from external digestId.
     * @param pId the external id
     * @return the digestType
     */
    public DigestType deriveDigestTypeFromExternalId(final int pId) {
        return theIdManager.deriveDigestTypeFromExternalId(pId, theDigestPredicate);
    }

    /**
     * Obtain an HMac for a password.
     * @param pDigestType the digest type required
     * @param pPassword the password in byte format
     * @return the HMac
     * @throws JOceanusException on error
     */
    public DataMac generateMac(final DigestType pDigestType,
                               final byte[] pPassword) throws JOceanusException {
        /* Create the mac */
        DataMac myMac = new DataMac(this, pDigestType, null);
        myMac.setSecretKey(pPassword);
        return myMac;
    }

    /**
     * Obtain a random HMac of specific type.
     * @param pDigestType the DigestType
     * @return the HMac
     * @throws JOceanusException on error
     */
    public DataMac generateMac(final DigestType pDigestType) throws JOceanusException {
        /* Create the mac */
        return DataMac.generateRandomDigestMac(this, pDigestType);
    }

    /**
     * Obtain a random MAC of specific type and SymKeyType.
     * @param pMacType the MacType
     * @param pKeyType the KeyType
     * @return the MAC
     * @throws JOceanusException on error
     */
    public DataMac generateMac(final MacType pMacType,
                               final SymKeyType pKeyType) throws JOceanusException {
        /* Create the mac */
        return DataMac.generateRandomSymKeyMac(this, pMacType, pKeyType);
    }

    /**
     * Obtain a random MAC of specific type.
     * @param pMacType the MacType
     * @return the MAC
     * @throws JOceanusException on error
     */
    public DataMac generateMac(final MacType pMacType) throws JOceanusException {
        /* Create the mac */
        return DataMac.generateRandomMac(this, pMacType);
    }

    /**
     * Obtain a random MAC.
     * @return the MAC
     * @throws JOceanusException on error
     */
    public DataMac generateMac() throws JOceanusException {
        /* Create the mac */
        return DataMac.generateRandomMac(this);
    }

    /**
     * Obtain external MacId.
     * @param pMac the macType
     * @return the external id
     */
    public int getExternalId(final MacType pMac) {
        return theIdManager.getExternalId(pMac);
    }

    /**
     * Obtain macType from external macId.
     * @param pId the external id
     * @return the macType
     */
    public MacType deriveMacTypeFromExternalId(final int pId) {
        return theIdManager.deriveMacTypeFromExternalId(pId, MacType.allTypes());
    }
}
