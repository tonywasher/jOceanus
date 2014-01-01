/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.logging.Logger;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;

import net.sourceforge.joceanus.jdatamanager.DataConverter;
import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jgordianknot.SecurityRegister.AsymmetricRegister;
import net.sourceforge.joceanus.jgordianknot.SecurityRegister.StreamRegister;
import net.sourceforge.joceanus.jgordianknot.SecurityRegister.SymmetricRegister;

/**
 * Generator class for various security primitives.
 * @author Tony Washer
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
     * The Logger.
     */
    private final Logger theLogger;

    /**
     * Security Register.
     */
    private final SecurityRegister theRegister;

    /**
     * Access the Security provider.
     * @return the security provider
     */
    protected SecurityProvider getProvider() {
        return theProvider;
    }

    /**
     * Access the Secure Random.
     * @return the secure random
     */
    protected SecureRandom getRandom() {
        return theRandom;
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
     * Obtain logger.
     * @return the Logger
     */
    public Logger getLogger() {
        return theLogger;
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
     * Default Constructor.
     * @param pLogger the logger
     * @throws JDataException on error
     */
    public SecurityGenerator(final Logger pLogger) throws JDataException {
        this(pLogger, new SecurityParameters());
    }

    /**
     * Constructor for explicit provider.
     * @param pLogger the logger
     * @param pParameters the Security parameters
     * @throws JDataException on error
     */
    public SecurityGenerator(final Logger pLogger,
                             final SecurityParameters pParameters) throws JDataException {
        /* Store the logger */
        theLogger = pLogger;

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
     * @throws JDataException on error
     */
    public final SecureRandom generateHashSecureRandom(final DigestType pType,
                                                       final boolean isPredictionResistant) throws JDataException {
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
     * @throws JDataException on error
     */
    public final SecureRandom generateHMacSecureRandom(final DigestType pType,
                                                       final byte[] pPassword,
                                                       final boolean isPredictionResistant) throws JDataException {
        /* Create the mac */
        DataMac myMac = generateMac(pType, pPassword);

        /* Create the new SecureRandom */
        return theRandomBuilder.buildHMAC(myMac, null, isPredictionResistant);
    }

    /**
     * Generate a password Hash for the given password.
     * @param pPassword the password
     * @return the Password hash
     * @throws JDataException on error
     */
    public PasswordHash generatePasswordHash(final char[] pPassword) throws JDataException {
        /* Create the new Password Hash */
        return new PasswordHash(this, pPassword);
    }

    /**
     * Derive a password Hash for the given hash and password.
     * @param pHashBytes the hash bytes
     * @param pPassword the password
     * @return the Password hash
     * @throws JDataException on error
     * @throws InvalidCredentialsException if password does not match
     */
    public PasswordHash derivePasswordHash(final byte[] pHashBytes,
                                           final char[] pPassword) throws JDataException {
        /* Create the new Password Hash */
        return new PasswordHash(this, pHashBytes, pPassword);
    }

    /**
     * Determine a list of random symmetric key types.
     * @return the Symmetric Key types
     * @throws JDataException on error
     */
    public SymKeyType[] generateSymKeyTypes() throws JDataException {
        /* Create the new Symmetric Key */
        return SymKeyType.getRandomTypes(getNumCipherSteps(), theRandom);
    }

    /**
     * Generate a new Symmetric Key for the required KeyType.
     * @param pKeyType the Symmetric Key type
     * @return the newly created Symmetric Key
     * @throws JDataException on error
     */
    public SymmetricKey generateSymmetricKey(final SymKeyType pKeyType) throws JDataException {
        /* Create the new Symmetric Key */
        return new SymmetricKey(this, pKeyType);
    }

    /**
     * Generate a new Symmetric Key of a random type.
     * @return the newly created Symmetric Key
     * @throws JDataException on error
     */
    public SymmetricKey generateSymmetricKey() throws JDataException {
        /* Create the new Symmetric Key */
        return new SymmetricKey(this);
    }

    /**
     * Generate a new Stream Key for the required KeyType.
     * @param pKeyType the Stream Key type
     * @return the newly created Stream Key
     * @throws JDataException on error
     */
    public StreamKey generateStreamKey(final StreamKeyType pKeyType) throws JDataException {
        /* Create the new Stream Key */
        return new StreamKey(this, pKeyType);
    }

    /**
     * Generate a new Stream Key of a random type.
     * @return the newly created Stream Key
     * @throws JDataException on error
     */
    public StreamKey generateStreamKey() throws JDataException {
        /* Create the new Stream Key */
        return new StreamKey(this);
    }

    /**
     * Generate a new Asymmetric Key of a random type.
     * @return the newly created Asymmetric Key
     * @throws JDataException on error
     */
    public AsymmetricKey generateAsymmetricKey() throws JDataException {
        /* Generate the new asymmetric key mode */
        AsymKeyMode myMode = new AsymKeyMode(useRestricted(), theRandom);

        /* Create the new Asymmetric Key */
        return new AsymmetricKey(this, myMode);
    }

    /**
     * Generate a new Asymmetric Key of the same type as the partner.
     * @param pPartner the partner asymmetric key
     * @return the newly created Asymmetric Key
     * @throws JDataException on error
     */
    public AsymmetricKey generateAsymmetricKey(final AsymmetricKey pPartner) throws JDataException {
        /* Determine the new keyMode */
        AsymKeyMode myMode = new AsymKeyMode(useRestricted(), pPartner.getKeyMode());

        /* Create the new Asymmetric Key */
        return new AsymmetricKey(this, myMode);
    }

    /**
     * Generate new KeyPair.
     * @param pKeyType the key type
     * @return the KeyPair
     * @throws JDataException on error
     */
    protected KeyPair generateKeyPair(final AsymKeyType pKeyType) throws JDataException {
        /* Obtain the registration */
        AsymmetricRegister myReg = theRegister.getAsymRegistration(pKeyType);

        /* Generate the KeyPair */
        return myReg.generateKeyPair();
    }

    /**
     * Derive the KeyPair from encoded forms.
     * @param pKeyType the key type
     * @param pPrivate the Encoded private form (may be null for public-only)
     * @param pPublic the Encoded public form
     * @return the KeyPair
     * @throws JDataException on error
     */
    protected KeyPair deriveKeyPair(final AsymKeyType pKeyType,
                                    final byte[] pPrivate,
                                    final byte[] pPublic) throws JDataException {
        /* Obtain the registration */
        AsymmetricRegister myReg = theRegister.getAsymRegistration(pKeyType);

        /* Derive the KeyPair */
        return myReg.deriveKeyPair(pPrivate, pPublic);
    }

    /**
     * Generate new Secret Key.
     * @param pKeyType the key type
     * @param pKeyLen the key length
     * @return the SecretKey
     * @throws JDataException on error
     */
    protected SecretKey generateSecretKey(final SymKeyType pKeyType,
                                          final int pKeyLen) throws JDataException {
        /* Obtain the registration */
        SymmetricRegister myReg = theRegister.getSymRegistration(pKeyType, pKeyLen);

        /* Generate the SecretKey */
        return myReg.generateKey();
    }

    /**
     * Generate new Stream Key.
     * @param pKeyType the key type
     * @param pKeyLen the key length
     * @return the StreamKey
     * @throws JDataException on error
     */
    protected SecretKey generateSecretKey(final StreamKeyType pKeyType,
                                          final int pKeyLen) throws JDataException {
        /* Obtain the registration */
        StreamRegister myReg = theRegister.getStreamRegistration(pKeyType, pKeyLen);

        /* Generate the SecretKey */
        return myReg.generateKey();
    }

    /**
     * Obtain a KeyAgreement.
     * @param pAlgorithm the algorithm required
     * @return the key agreement
     * @throws JDataException on error
     */
    protected KeyAgreement accessKeyAgreement(final String pAlgorithm) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Return the key agreement for the algorithm */
            return KeyAgreement.getInstance(pAlgorithm, theProviderName);

            /* Catch exceptions */
        } catch (NoSuchProviderException | NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to create KeyAgreement", e);
        }
    }

    /**
     * Obtain a DataDigest of the specified type.
     * @param pDigestType the digest type required
     * @return the DataDigest
     * @throws JDataException on error
     */
    public final DataDigest generateDigest(final DigestType pDigestType) throws JDataException {
        /* Return a digest for the algorithm */
        return new DataDigest(this, pDigestType);
    }

    /**
     * Obtain a DataDigest of a random type.
     * @return the DataDigest
     * @throws JDataException on error
     */
    public final DataDigest generateDigest() throws JDataException {
        /* Return a random digest */
        return new DataDigest(this);
    }

    /**
     * Obtain a MAC for a password.
     * @param pDigestType the digest type required
     * @param pPassword the password in byte format
     * @return the MAC
     * @throws JDataException on error
     */
    public DataMac generateMac(final DigestType pDigestType,
                               final byte[] pPassword) throws JDataException {
        /* Create the mac */
        return new DataMac(this, pDigestType, pPassword);
    }

    /**
     * Obtain a MAC for a symmetricKey.
     * @param pMacType the mac type required
     * @param pKey the symmetricKey
     * @return the MAC
     * @throws JDataException on error
     */
    public DataMac generateMac(final MacType pMacType,
                               final SymmetricKey pKey) throws JDataException {
        /* Create the mac */
        return new DataMac(this, pMacType, pKey, null);
    }
}
