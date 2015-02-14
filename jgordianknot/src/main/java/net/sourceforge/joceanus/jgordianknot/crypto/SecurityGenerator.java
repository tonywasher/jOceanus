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

import net.sourceforge.joceanus.jtethys.DataConverter;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Generator class for various security primitives.
 */
public class SecurityGenerator {
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
     * Do we use long hashes?
     */
    private final boolean useLongHash;

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
     * The Security phrase.
     */
    private final byte[] theSecurityPhrase;

    /**
     * The Security provider name.
     */
    private final String theProviderName;

    /**
     * The Secure Random builder.
     */
    private final SP800SecureRandomBuilder theRandomBuilder;

    /**
     * The Secure Random generator.
     */
    private final SecureRandom theRandom;

    /**
     * Security Register.
     */
    private final SecurityRegister theRegister;

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
        useLongHash = pParameters.useLongHash();
        theCipherSteps = pParameters.getNumCipherSteps();
        theIterations = pParameters.getNumHashIterations();
        theNumActiveKeySets = pParameters.getNumActiveKeySets();

        /* Store security phrase */
        String myPhrase = pParameters.getSecurityPhrase();
        theSecurityPhrase = (myPhrase == null)
                                              ? null
                                              : DataConverter.stringToByteArray(myPhrase);

        /* Create the random builder */
        theRandomBuilder = new SP800SecureRandomBuilder();
        theRandomBuilder.setSecurityBytes(theSecurityPhrase);

        /* Create a new secure random generator */
        SecureRandom myRandom = theRandomBuilder.getRandom();
        DigestType[] myType = DigestType.getRandomTypes(1, myRandom);
        theRandom = generateHashSecureRandom(myType[0], false);

        /* Create the register */
        theRegister = new SecurityRegister(this);
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
     * Access the security phrase in bytes format.
     * @return the security phrase
     */
    protected byte[] getSecurityBytes() {
        return theSecurityPhrase;
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
     * Do we use long hashes.
     * @return true/false
     */
    protected boolean useLongHash() {
        return useLongHash;
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
        /* Create the new Symmetric Key */
        return SymKeyType.getRandomTypes(getNumCipherSteps(), theRandom);
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
}
