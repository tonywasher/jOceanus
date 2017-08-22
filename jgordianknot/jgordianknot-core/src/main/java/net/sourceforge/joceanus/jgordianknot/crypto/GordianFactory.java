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
import net.sourceforge.joceanus.jgordianknot.crypto.prng.GordianSecureRandom;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot base for Factory.
 */
public abstract class GordianFactory {
    /**
     * The Hash Prime.
     */
    public static final int HASH_PRIME = 37;

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
    private final GordianPersonalisation thePersonalisation;

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
     * @throws OceanusException on error
     */
    protected GordianFactory(final GordianParameters pParameters) throws OceanusException {
        /* Store parameters */
        theParameters = pParameters;
        isRestricted = theParameters.useRestricted();

        /* Calculate personalisation bytes */
        thePersonalisation = new GordianPersonalisation(this, theParameters);
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
    protected GordianSecureRandom getRandom() {
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
    protected GordianPersonalisation getPersonalisation() {
        return thePersonalisation;
    }

    /**
     * Create a random randomSpec.
     * @param pRandom the random generator
     * @return the randomSpec
     */
    public GordianRandomSpec generateRandomSpec(final SecureRandom pRandom) {
        /* Determine the type of random generator */
        final GordianSP800Type myType = pRandom.nextBoolean()
                                                              ? GordianSP800Type.HMAC
                                                              : GordianSP800Type.HASH;

        /* Access the digestTypes */
        final GordianDigestType[] myDigestTypes = GordianDigestType.values();
        for (;;) {
            final int myInt = pRandom.nextInt(myDigestTypes.length);
            if (supportedHMacDigestTypes().test(myDigestTypes[myInt])) {
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
        getIdManager().setSecureRandom(pRandom);
    }

    /**
     * ReSeed the random number generator.
     */
    public void reSeedRandom() {
        /* Generate and apply the new seed */
        final byte[] mySeed = theRandom.generateSeed(SEED_SIZE);
        theRandom.setSeed(mySeed);
        theRandom.reseed(null);
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
    public abstract GordianSecureRandom createRandom(GordianRandomSpec pRandomSpec) throws OceanusException;

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
        final GordianDigestType myType = getIdManager().generateRandomDigestType();
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
     * Obtain predicate for supported poly1305 symKeySpecs.
     * @return the predicate
     */
    public abstract Predicate<GordianSymKeySpec> supportedPoly1305SymKeySpecs();

    /**
     * Obtain predicate for supported gMac symKeySpecs.
     * @return the predicate
     */
    public abstract Predicate<GordianSymKeySpec> supportedGMacSymKeySpecs();

    /**
     * Obtain predicate for supported cMac symKeyTypes.
     * @return the predicate
     */
    public abstract Predicate<GordianSymKeySpec> supportedCMacSymKeySpecs();

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
        final int myCount = getNumCipherSteps();
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
     * Obtain predicate for supported digestSpecs.
     * @return the predicate
     */
    public Predicate<GordianSymKeySpec> supportedSymKeySpecs() {
        return this::validSymKeySpec;
    }

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
     * Obtain predicate for supported poly1305 symKeySpecs.
     * @return the predicate
     */
    public Predicate<GordianSymKeySpec> supportedKeySetSymKeySpecs() {
        return p -> supportedKeySetSymKeyTypes().test(p.getSymKeyType()) && p.getBlockLength() == GordianLength.LEN_128;
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
     * @param pKeySpec the KeySpec
     * @return the new Cipher
     * @throws OceanusException on error
     */
    protected abstract GordianWrapCipher createWrapCipher(GordianSymKeySpec pKeySpec) throws OceanusException;

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
        final GordianSP800Type myType = pRandomSpec.getRandomType();
        final GordianDigestSpec mySpec = pRandomSpec.getDigestSpec();

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
     * Check HMacSpec.
     * @param pDigestSpec the digestSpec
     * @return true/false
     */
    private boolean validHMacSpec(final GordianDigestSpec pDigestSpec) {
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
        final GordianDigestSpec mySpec = pMacSpec.getDigestSpec();
        final GordianSymKeySpec mySymSpec = pMacSpec.getKeySpec();

        /* Check that the macType is supported */
        if (!supportedMacTypes().test(myType)) {
            return false;
        }

        /* Switch on MacType */
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
                return supportedDigestSpecs().test(mySpec)
                       && GordianDigestType.SKEIN.equals(mySpec.getDigestType());
            case KUPYNA:
                return supportedDigestSpecs().test(mySpec)
                       && GordianDigestType.KUPYNA.equals(mySpec.getDigestType());
            case KALYNA:
                return false;
            /**
             * TODO KalynaMac not currently usable! return validSymKeySpec(mySymSpec) &&
             * GordianSymKeyType.KALYNA.equals(mySymSpec.getSymKeyType());
             */
            case VMPC:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check SymKeySpec.
     * @param pSymKeySpec the symKeySpec
     * @return true/false
     */
    protected boolean validSymKeySpec(final GordianSymKeySpec pSymKeySpec) {
        /* Access details */
        final GordianSymKeyType myType = pSymKeySpec.getSymKeyType();
        final GordianLength myLen = pSymKeySpec.getBlockLength();

        /* Reject restrictedSpecs where the block length is too large */
        if (isRestricted
            && myLen.getLength() > GordianLength.LEN_128.getLength()) {
            return false;
        }

        /* Check validity */
        return supportedSymKeyTypes().test(myType)
               && myType.isLengthValid(myLen);
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
        final GordianAsymKeyType myType = pSignSpec.getAsymKeyType();
        final GordianSignatureType mySignType = pSignSpec.getSignatureType();
        final GordianDigestSpec mySpec = pSignSpec.getDigestSpec();

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
        final GordianAsymKeySpec myKeySpec = pKeyPair.getKeySpec();
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
