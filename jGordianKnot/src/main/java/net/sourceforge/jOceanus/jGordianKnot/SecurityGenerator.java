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
package net.sourceforge.jOceanus.jGordianKnot;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import net.sourceforge.jOceanus.jDataManager.DataConverter;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;

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
     * The Security phrase.
     */
    private final String theSecurityPhrase;

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
     * List of asymmetric registrations.
     */
    private List<AsymRegistration> theAsymRegister = new ArrayList<AsymRegistration>();

    /**
     * List of symmetric registrations.
     */
    private List<SymRegistration> theSymRegister = new ArrayList<SymRegistration>();

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
     * @throws JDataException on error
     */
    protected byte[] getSecurityBytes() throws JDataException {
        String myPhrase = theSecurityPhrase;
        return DataConverter.stringToByteArray(myPhrase);
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
     * Constructor for explicit provider.
     * @param pProvider the Security provider
     * @param pRestricted do we use restricted security
     * @param pNumCipherSteps the number of cipher steps
     * @param pHashIterations the number of hash iterations
     * @param pSecurityPhrase the security phrase
     * @throws JDataException on error
     */
    public SecurityGenerator(final SecurityProvider pProvider,
                             final boolean pRestricted,
                             final int pNumCipherSteps,
                             final int pHashIterations,
                             final String pSecurityPhrase) throws JDataException {
        /* Store the provider */
        theProvider = pProvider;
        theProviderName = theProvider.getProvider();

        /* Ensure that the provider is installed */
        theProvider.ensureInstalled();

        /* Store parameters */
        useRestricted = pRestricted;
        theCipherSteps = pNumCipherSteps;
        theIterations = pHashIterations;
        theSecurityPhrase = pSecurityPhrase;

        /* Create the random builder */
        theRandomBuilder = new SP800SecureRandomBuilder();
        theRandomBuilder.setSecurityBytes(getSecurityBytes());

        /* Create a new secure random generator */
        theRandom = generateHashSecureRandom(DigestType.SHA3, false);
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
     * Generate an SP800HashDRBG SecureRandom.
     * @param pType the digest type
     * @param isPredictionResistant true/false
     * @return the SecureRandom
     * @throws JDataException on error
     */
    public SecureRandom generateHashSecureRandom(final DigestType pType,
                                                 final boolean isPredictionResistant) throws JDataException {
        /* Create the digest */
        MessageDigest myDigest = accessDigest(pType);

        /* Create the new SecureRandom */
        return theRandomBuilder.buildHash(myDigest, null, isPredictionResistant);
    }

    /**
     * Generate an SP800HMacDRBG SecureRandom.
     * @param pType the digest type
     * @param isPredictionResistant true/false
     * @return the SecureRandom
     * @throws JDataException on error
     */
    public SecureRandom generateHMacSecureRandom(final DigestType pType,
                                                 final boolean isPredictionResistant) throws JDataException {
        /* Create the digest */
        Mac myMac = accessMac(pType);

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
     * @throws WrongPasswordException if password does not match
     */
    public PasswordHash derivePasswordHash(final byte[] pHashBytes,
                                           final char[] pPassword) throws JDataException, WrongPasswordException {
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
        return new SymmetricKey(this, pKeyType, useRestricted());
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
        AsymRegistration myReg = getAsymRegistration(pKeyType);

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
        AsymRegistration myReg = getAsymRegistration(pKeyType);

        /* Derive the KeyPair */
        return myReg.deriveKeyPair(pPrivate, pPublic);
    }

    /**
     * Generate new KeyPair.
     * @param pKeyType the key type
     * @param pKeyLen the key length
     * @return the SecretKey
     * @throws JDataException on error
     */
    protected SecretKey generateSecretKey(final SymKeyType pKeyType,
                                          final int pKeyLen) throws JDataException {
        /* Obtain the registration */
        SymRegistration myReg = getSymRegistration(pKeyType, pKeyLen);

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
        } catch (NoSuchProviderException e) {
            /* Throw the exception */
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to create KeyAgreement", e);
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to create KeyAgreement", e);
        }
    }

    /**
     * Obtain a Cipher.
     * @param pAlgorithm the algorithm required
     * @return the cipher
     * @throws JDataException on error
     */
    protected Cipher accessCipher(final String pAlgorithm) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Return a cipher for the algorithm */
            return Cipher.getInstance(pAlgorithm, theProviderName);

            /* Catch exceptions */
        } catch (NoSuchPaddingException e) {
            /* Throw the exception */
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to create Cipher", e);
        } catch (NoSuchProviderException e) {
            /* Throw the exception */
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to create Cipher", e);
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to create Cipher", e);
        }
    }

    /**
     * Obtain a MessageDigest.
     * @param pDigestType the digest type required
     * @return the MessageDigest
     * @throws JDataException on error
     */
    public MessageDigest accessDigest(final DigestType pDigestType) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Return a digest for the algorithm */
            return MessageDigest.getInstance(pDigestType.getAlgorithm(), theProviderName);

            /* Catch exceptions */
        } catch (NoSuchProviderException e) {
            /* Throw the exception */
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to create Digest", e);
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to create Digest", e);
        }
    }

    /**
     * Obtain a MAC.
     * @param pDigestType the digest type required
     * @return the MAC
     * @throws JDataException on error
     */
    protected Mac accessMac(final DigestType pDigestType) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Access the MAC */
            return Mac.getInstance(pDigestType.getHMacAlgorithm(), theProviderName);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new JDataException(ExceptionClass.CRYPTO, e.getMessage(), e);
        } catch (NoSuchProviderException e) {
            /* Throw the exception */
            throw new JDataException(ExceptionClass.CRYPTO, e.getMessage(), e);
        }
    }

    /**
     * Obtain a MAC for a password.
     * @param pDigestType the digest type required
     * @param pPassword the password in byte format
     * @return the MAC
     * @throws JDataException on error
     */
    protected Mac accessMac(final DigestType pDigestType,
                            final byte[] pPassword) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Access the MAC */
            Mac myMac = accessMac(pDigestType);

            /* Initialise the MAC */
            SecretKey myKey = new SecretKeySpec(pPassword, pDigestType.getHMacAlgorithm());
            myMac.init(myKey);

            /* Return the initialised MAC */
            return myMac;

            /* Catch exceptions */
        } catch (InvalidKeyException e) {
            /* Throw the exception */
            throw new JDataException(ExceptionClass.CRYPTO, e.getMessage(), e);
        }
    }

    /**
     * Obtain a MAC for a symmetricKey.
     * @param pDigestType the digest type required
     * @param pKey the symmetricKey
     * @return the MAC
     * @throws JDataException on error
     */
    protected Mac accessMac(final DigestType pDigestType,
                            final SymmetricKey pKey) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Access the MAC */
            Mac myMac = accessMac(pDigestType);

            /* Initialise the MAC */
            myMac.init(pKey.getSecretKey());

            /* Return the initialised MAC */
            return myMac;

            /* Catch exceptions */
        } catch (InvalidKeyException e) {
            /* Throw the exception */
            throw new JDataException(ExceptionClass.CRYPTO, e.getMessage(), e);
        }
    }

    /**
     * Obtain a Signature.
     * @param pAlgorithm the algorithm required
     * @return the signature
     * @throws JDataException on error
     */
    protected Signature accessSignature(final String pAlgorithm) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Return a signature for the algorithm */
            return Signature.getInstance(pAlgorithm, theProviderName);

            /* Catch exceptions */
        } catch (NoSuchProviderException e) {
            /* Throw the exception */
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to create Signature", e);
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to create Signature", e);
        }
    }

    /**
     * Obtain the Asymmetric Registration.
     * @param pKeyType the key type
     * @return the registration
     */
    private AsymRegistration getAsymRegistration(final AsymKeyType pKeyType) {
        /* Loop through the list */
        Iterator<AsymRegistration> myIterator = theAsymRegister.iterator();
        while (myIterator.hasNext()) {
            /* Access the item */
            AsymRegistration myReg = myIterator.next();

            /* If this is the right one, return it */
            if (myReg.getKeyType() == pKeyType) {
                return myReg;
            }
        }

        /* Return the new registration */
        return new AsymRegistration(pKeyType);
    }

    /**
     * Obtain the Symmetric Registration.
     * @param pKeyType the key type
     * @param pKeyLen the key length
     * @return the registration
     */
    private SymRegistration getSymRegistration(final SymKeyType pKeyType,
                                               final int pKeyLen) {
        /* Loop through the list */
        Iterator<SymRegistration> myIterator = theSymRegister.iterator();
        while (myIterator.hasNext()) {
            /* Access the item */
            SymRegistration myReg = myIterator.next();

            /* If this is the right one, return it */
            if ((myReg.getKeyType() == pKeyType)
                && (myReg.getKeyLen() == pKeyLen)) {
                return myReg;
            }
        }

        /* Return the new registration */
        return new SymRegistration(pKeyType, pKeyLen);
    }

    /**
     * AsymRegistration class.
     */
    private final class AsymRegistration {
        /**
         * Asymmetric Key Type.
         */
        private final AsymKeyType theKeyType;

        /**
         * Asymmetric Algorithm.
         */
        private final String theAlgorithm;

        /**
         * Key Factory for Asymmetric Key Type.
         */
        private KeyFactory theFactory = null;

        /**
         * KeyPair Generator for Asymmetric Key Type.
         */
        private KeyPairGenerator theGenerator = null;

        /**
         * Obtain the KeyType.
         * @return the Key type
         */
        private AsymKeyType getKeyType() {
            return theKeyType;
        }

        /**
         * Constructor.
         * @param pKeyType the key type
         */
        private AsymRegistration(final AsymKeyType pKeyType) {
            /* Store the key type */
            theKeyType = pKeyType;
            theAlgorithm = theKeyType.getAlgorithm();

            /* Add it to the registrations */
            theAsymRegister.add(this);
        }

        /**
         * Derive the KeyPair from encoded forms.
         * @param pPrivate the Encoded private form (may be null for public-only)
         * @param pPublic the Encoded public form
         * @return the KeyPair
         * @throws JDataException on error
         */
        private KeyPair deriveKeyPair(final byte[] pPrivate,
                                      final byte[] pPublic) throws JDataException {
            /* If we have not allocated the factory */
            if (theFactory == null) {
                /* Protect against Exceptions */
                try {
                    /* Allocate the new factory */
                    theFactory = KeyFactory.getInstance(theAlgorithm, theProviderName);
                } catch (NoSuchProviderException e) {
                    /* Throw the exception */
                    throw new JDataException(ExceptionClass.CRYPTO, "Failed to create key factory", e);
                } catch (NoSuchAlgorithmException e) {
                    /* Throw the exception */
                    throw new JDataException(ExceptionClass.CRYPTO, "Failed to create key factory", e);
                }
            }

            /* Protect against exceptions */
            try {
                PrivateKey myPrivate = null;
                PublicKey myPublic = null;

                /* if we have a private key */
                if (pPrivate != null) {
                    /* Build the private key */
                    PKCS8EncodedKeySpec myPrivSpec = new PKCS8EncodedKeySpec(pPrivate);
                    myPrivate = theFactory.generatePrivate(myPrivSpec);
                }

                /* Build the public key */
                X509EncodedKeySpec myPubSpec = new X509EncodedKeySpec(pPublic);
                myPublic = theFactory.generatePublic(myPubSpec);

                /* Return the private key */
                return new KeyPair(myPublic, myPrivate);

                /* Catch exceptions */
            } catch (InvalidKeySpecException e) {
                /* Throw the exception */
                throw new JDataException(ExceptionClass.CRYPTO, "Failed to re-build KeyPair", e);
            }
        }

        /**
         * Generate new KeyPair.
         * @return the KeyPair
         * @throws JDataException on error
         */
        private KeyPair generateKeyPair() throws JDataException {
            /* If we have not allocated the generator */
            if (theGenerator == null) {
                /* Protect against Exceptions */
                try {
                    /* Allocate the new factory */
                    theGenerator = KeyPairGenerator.getInstance(theAlgorithm, theProviderName);

                    /* Handle elliptic curve key types differently */
                    if (theKeyType.isElliptic()) {
                        /* Initialise with the parameter specification for the curve */
                        ECGenParameterSpec parms = new ECGenParameterSpec(theKeyType.getCurve());
                        theGenerator.initialize(parms, theRandom);

                        /* Else standard RSA type */
                    } else {
                        /* Initialise to required key size */
                        theGenerator.initialize(theKeyType.getKeySize(), theRandom);
                    }
                } catch (NoSuchProviderException e) {
                    /* Throw the exception */
                    throw new JDataException(ExceptionClass.CRYPTO, e.getMessage(), e);
                } catch (NoSuchAlgorithmException e) {
                    /* Throw the exception */
                    throw new JDataException(ExceptionClass.CRYPTO, e.getMessage(), e);
                } catch (InvalidAlgorithmParameterException e) {
                    /* Throw the exception */
                    throw new JDataException(ExceptionClass.CRYPTO, e.getMessage(), e);
                }
            }

            /* Generate the Key Pair */
            return theGenerator.generateKeyPair();
        }
    }

    /**
     * SymRegistration class.
     */
    private final class SymRegistration {
        /**
         * Symmetric Key Type.
         */
        private final SymKeyType theKeyType;

        /**
         * Symmetric Algorithm.
         */
        private final String theAlgorithm;

        /**
         * Key Length.
         */
        private final int theKeyLen;

        /**
         * Key Generator for Symmetric Key Type.
         */
        private KeyGenerator theGenerator = null;

        /**
         * Obtain the KeyType.
         * @return the Key type
         */
        private SymKeyType getKeyType() {
            return theKeyType;
        }

        /**
         * Obtain the KeyLength.
         * @return the Key length
         */
        private int getKeyLen() {
            return theKeyLen;
        }

        /**
         * Constructor.
         * @param pKeyType the key type
         * @param pKeyLen the key length
         */
        private SymRegistration(final SymKeyType pKeyType,
                                final int pKeyLen) {
            /* Store the key type */
            theKeyType = pKeyType;
            theKeyLen = pKeyLen;
            theAlgorithm = theKeyType.getAlgorithm();

            /* Add it to the registrations */
            theSymRegister.add(this);
        }

        /**
         * Generate a new key of the required keyLength.
         * @return the Secret Key
         * @throws JDataException on error
         */
        private SecretKey generateKey() throws JDataException {
            /* If we have not allocated the generator */
            if (theGenerator == null) {
                /* Protect against Exceptions */
                try {
                    /* Create the key generator */
                    theGenerator = KeyGenerator.getInstance(theAlgorithm, theProviderName);
                    theGenerator.init(theKeyLen, theRandom);
                } catch (NoSuchProviderException e) {
                    /* Throw the exception */
                    throw new JDataException(ExceptionClass.CRYPTO, e.getMessage(), e);
                } catch (NoSuchAlgorithmException e) {
                    /* Throw the exception */
                    throw new JDataException(ExceptionClass.CRYPTO, e.getMessage(), e);
                }
            }

            /* Generate the Secret key */
            return theGenerator.generateKey();
        }
    }
}
