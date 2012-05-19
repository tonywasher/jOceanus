/*******************************************************************************
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JGordianKnot;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import net.sourceforge.JDataManager.DataConverter;
import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.ModelException.ExceptionClass;
import net.sourceforge.JDataManager.PreferenceSet.PreferenceManager;

public class SecurityGenerator {
    /**
     * Security Preferences
     */
    private final SecurityPreferences thePreferences;

    /**
     * The Security provider
     */
    private final SecurityProvider theProvider;

    /**
     * The Security provider name
     */
    private final String theProviderName;

    /**
     * The Secure Random generator
     */
    private final SecureRandom theRandom;

    /**
     * List of asymmetric registrations
     */
    private List<AsymRegistration> theAsymRegister = new ArrayList<AsymRegistration>();

    /**
     * List of asymmetric registrations
     */
    private List<SymRegistration> theSymRegister = new ArrayList<SymRegistration>();

    /**
     * Access the Security provider
     * @return the security provider
     */
    protected SecurityProvider getProvider() {
        return theProvider;
    }

    /**
     * Access the Default Security provider
     * @return the default security provider
     */
    protected SecurityProvider getDefaultProvider() {
        return thePreferences.getEnumValue(SecurityPreferences.nameProvider, SecurityProvider.class);
    }

    /**
     * Access the Secure Random
     * @return the secure random
     */
    protected SecureRandom getRandom() {
        return theRandom;
    }

    /**
     * Access the number of Hash Iterations
     * @return the number of hash iterations
     */
    protected int getNumHashIterations() {
        return thePreferences.getIntegerValue(SecurityPreferences.nameHashIterations);
    }

    /**
     * Access the security phrase in bytes format
     * @return the secure random
     */
    protected byte[] getSecurityBytes() {
        String myPhrase = thePreferences.getStringValue(SecurityPreferences.nameSecurityPhrase);
        return DataConverter.stringToByteArray(myPhrase);
    }

    /**
     * Access the number of Cipher Steps
     * @return the number of cipher steps
     */
    public int getNumCipherSteps() {
        return thePreferences.getIntegerValue(SecurityPreferences.nameCipherSteps);
    }

    /**
     * Do we use restricted security
     * @return true/false
     */
    protected boolean useRestricted() {
        return thePreferences.getBooleanValue(SecurityPreferences.nameRestricted);
    }

    /**
     * Constructor for default provider
     */
    public SecurityGenerator() {
        /* Access with default provider */
        this(null);
    }

    /**
     * Constructor for explicit provider
     * @param pProvider the Security provider
     */
    public SecurityGenerator(SecurityProvider pProvider) {
        /* Access the preferences */
        thePreferences = PreferenceManager.getPreferenceSet(SecurityPreferences.class);

        /* Store the provider */
        theProvider = (pProvider != null) ? pProvider : getDefaultProvider();
        theProviderName = theProvider.getProvider();

        /* Ensure that the provider is installed */
        theProvider.ensureInstalled();

        /* Create a new secure random generator */
        theRandom = new SecureRandom();
    }

    /**
     * ReSeed the random number generator
     */
    public void reSeedRandom() {
        /* Generate and apply the new seed */
        byte[] mySeed = SecureRandom.getSeed(32);
        theRandom.setSeed(mySeed);
    }

    /**
     * Generate a password Hash for the given password
     * @param pPassword the password
     * @return the Password hash
     * @throws ModelException
     */
    public PasswordHash generatePasswordHash(char[] pPassword) throws ModelException {
        /* Create the new Password Hash */
        return new PasswordHash(this, pPassword);
    }

    /**
     * Derive a password Hash for the given hash and password
     * @param pHashBytes the hash bytes
     * @param pPassword the password
     * @return the Password hash
     * @throws ModelException
     * @throws WrongPasswordException
     */
    public PasswordHash derivePasswordHash(byte[] pHashBytes,
                                           char[] pPassword) throws ModelException, WrongPasswordException {
        /* Create the new Password Hash */
        return new PasswordHash(this, pHashBytes, pPassword);
    }

    /**
     * Determine a list of random symmetric key types
     * @return the Symmetric Key types
     * @throws ModelException
     */
    public SymKeyType[] generateSymKeyTypes() throws ModelException {
        /* Create the new Symmetric Key */
        return SymKeyType.getRandomTypes(getNumCipherSteps(), theRandom);
    }

    /**
     * Generate a new Symmetric Key for the required KeyType
     * @param pKeyType the Symmetric Key type
     * @return the newly created Symmetric Key
     * @throws ModelException
     */
    public SymmetricKey generateSymmetricKey(SymKeyType pKeyType) throws ModelException {
        /* Create the new Symmetric Key */
        return new SymmetricKey(this, pKeyType, useRestricted());
    }

    /**
     * Generate a new Asymmetric Key of a random type
     * @return the newly created Asymmetric Key
     * @throws ModelException
     */
    public AsymmetricKey generateAsymmetricKey() throws ModelException {
        /* Generate the new asymmetric key mode */
        AsymKeyMode myMode = new AsymKeyMode(useRestricted(), theRandom);

        /* Create the new Asymmetric Key */
        return new AsymmetricKey(this, myMode);
    }

    /**
     * Generate a new Asymmetric Key of the same type as the partner
     * @param pPartner the partner asymmetric key
     * @return the newly created Asymmetric Key
     * @throws ModelException
     */
    public AsymmetricKey generateAsymmetricKey(AsymmetricKey pPartner) throws ModelException {
        /* Determine the new keyMode */
        AsymKeyMode myMode = new AsymKeyMode(useRestricted(), pPartner.getKeyMode());

        /* Create the new Asymmetric Key */
        return new AsymmetricKey(this, myMode);
    }

    /**
     * Generate new KeyPair
     * @param pKeyType the key type
     * @return the KeyPair
     * @throws ModelException
     */
    protected KeyPair generateKeyPair(AsymKeyType pKeyType) throws ModelException {
        /* Obtain the registration */
        AsymRegistration myReg = getAsymRegistration(pKeyType);

        /* Generate the KeyPair */
        return myReg.generateKeyPair();
    }

    /**
     * Derive the KeyPair from encoded forms
     * @param pKeyType the key type
     * @param pPrivate the Encoded private form (may be null for public-only)
     * @param pPublic the Encoded public form
     * @return the KeyPair
     * @throws ModelException
     */
    protected KeyPair deriveKeyPair(AsymKeyType pKeyType,
                                    byte[] pPrivate,
                                    byte[] pPublic) throws ModelException {
        /* Obtain the registration */
        AsymRegistration myReg = getAsymRegistration(pKeyType);

        /* Derive the KeyPair */
        return myReg.deriveKeyPair(pPrivate, pPublic);
    }

    /**
     * Generate new KeyPair
     * @param pKeyType the key type
     * @param pKeyLen the key length
     * @return the SecretKey
     * @throws ModelException
     */
    protected SecretKey generateSecretKey(SymKeyType pKeyType,
                                          int pKeyLen) throws ModelException {
        /* Obtain the registration */
        SymRegistration myReg = getSymRegistration(pKeyType, pKeyLen);

        /* Generate the SecretKey */
        return myReg.generateKey();
    }

    /**
     * Obtain a KeyAgreement
     * @param pAlgorithm the algorithm required
     * @return the key agreement
     * @throws ModelException
     */
    protected KeyAgreement accessKeyAgreement(String pAlgorithm) throws ModelException {
        /* Protect against exceptions */
        try {
            /* Return the key agreement for the algorithm */
            return KeyAgreement.getInstance(pAlgorithm, theProviderName);
        }

        /* Catch exceptions */
        catch (Exception e) {
            /* Throw the exception */
            throw new ModelException(ExceptionClass.CRYPTO, "Failed to create KeyAgreement", e);
        }
    }

    /**
     * Obtain a Cipher
     * @param pAlgorithm the algorithm required
     * @return the cipher
     * @throws ModelException
     */
    protected Cipher accessCipher(String pAlgorithm) throws ModelException {
        /* Protect against exceptions */
        try {
            /* Return a cipher for the algorithm */
            return Cipher.getInstance(pAlgorithm, theProviderName);
        }

        /* Catch exceptions */
        catch (Exception e) {
            /* Throw the exception */
            throw new ModelException(ExceptionClass.CRYPTO, "Failed to create Cipher", e);
        }
    }

    /**
     * Obtain a MessageDigest
     * @param pDigestType the digest type required
     * @return the MessageDigest
     * @throws ModelException
     */
    public MessageDigest accessDigest(DigestType pDigestType) throws ModelException {
        /* Protect against exceptions */
        try {
            /* Return a digest for the algorithm */
            return MessageDigest.getInstance(pDigestType.getAlgorithm(), theProviderName);
        }

        /* Catch exceptions */
        catch (Exception e) {
            /* Throw the exception */
            throw new ModelException(ExceptionClass.CRYPTO, "Failed to create Cipher", e);
        }
    }

    /**
     * Obtain a MAC for a password
     * @param pDigestType the digest type required
     * @param pPassword the password in byte format
     * @return the MAC
     * @throws ModelException
     */
    protected Mac accessMac(DigestType pDigestType,
                            byte[] pPassword) throws ModelException {
        /* Protect against exceptions */
        try {
            /* Access the MAC */
            Mac myMac = Mac.getInstance(pDigestType.getHMacAlgorithm(), theProviderName);

            /* Initialise the MAC */
            SecretKey myKey = new SecretKeySpec(pPassword, pDigestType.getHMacAlgorithm());
            myMac.init(myKey);

            /* Return the initialised MAC */
            return myMac;
        }

        /* Catch exceptions */
        catch (Exception e) {
            /* Throw the exception */
            throw new ModelException(ExceptionClass.CRYPTO, "Failed to create Mac", e);
        }
    }

    /**
     * Obtain a MAC for a symmetricKey
     * @param pDigestType the digest type required
     * @param pKey the symmetricKey
     * @return the MAC
     * @throws ModelException
     */
    protected Mac accessMac(DigestType pDigestType,
                            SymmetricKey pKey) throws ModelException {
        /* Protect against exceptions */
        try {
            /* Access the MAC */
            Mac myMac = Mac.getInstance(pDigestType.getHMacAlgorithm(), theProviderName);

            /* Initialise the MAC */
            myMac.init(pKey.getSecretKey());

            /* Return the initialised MAC */
            return myMac;
        }

        /* Catch exceptions */
        catch (Exception e) {
            /* Throw the exception */
            throw new ModelException(ExceptionClass.CRYPTO, "Failed to create Mac", e);
        }
    }

    /**
     * Obtain a Signature
     * @param pAlgorithm the algorithm required
     * @return the signature
     * @throws ModelException
     */
    protected Signature accessSignature(String pAlgorithm) throws ModelException {
        /* Protect against exceptions */
        try {
            /* Return a signature for the algorithm */
            return Signature.getInstance(pAlgorithm, theProviderName);
        }

        /* Catch exceptions */
        catch (Exception e) {
            /* Throw the exception */
            throw new ModelException(ExceptionClass.CRYPTO, "Failed to create Signature", e);
        }
    }

    /**
     * Obtain the Asymmetric Registration
     * @param pKeyType the key type
     * @return the registration
     */
    private AsymRegistration getAsymRegistration(AsymKeyType pKeyType) {
        /* Loop through the list */
        Iterator<AsymRegistration> myIterator = theAsymRegister.iterator();
        while (myIterator.hasNext()) {
            /* Access the item */
            AsymRegistration myReg = myIterator.next();

            /* If this is the right one, return it */
            if (myReg.getKeyType() == pKeyType)
                return myReg;
        }

        /* Return the new registration */
        return new AsymRegistration(pKeyType);
    }

    /**
     * Obtain the Symmetric Registration
     * @param pKeyType the key type
     * @param pKeyLen the key length
     * @return the registration
     */
    private SymRegistration getSymRegistration(SymKeyType pKeyType,
                                               int pKeyLen) {
        /* Loop through the list */
        Iterator<SymRegistration> myIterator = theSymRegister.iterator();
        while (myIterator.hasNext()) {
            /* Access the item */
            SymRegistration myReg = myIterator.next();

            /* If this is the right one, return it */
            if ((myReg.getKeyType() == pKeyType) && (myReg.getKeyLen() == pKeyLen))
                return myReg;
        }

        /* Return the new registration */
        return new SymRegistration(pKeyType, pKeyLen);
    }

    /**
     * AsymRegistration class
     */
    private class AsymRegistration {
        /**
         * Asymmetric Key Type
         */
        private final AsymKeyType theKeyType;

        /**
         * Asymmetric Algorithm
         */
        private final String theAlgorithm;

        /**
         * Key Factory for Asymmetric Key Type
         */
        private KeyFactory theFactory = null;

        /**
         * KeyPair Generator for Asymmetric Key Type
         */
        private KeyPairGenerator theGenerator = null;

        /**
         * Obtain the KeyType
         * @return the Key type
         */
        private AsymKeyType getKeyType() {
            return theKeyType;
        }

        /**
         * Constructor
         * @param pKeyType the key type
         */
        private AsymRegistration(AsymKeyType pKeyType) {
            /* Store the key type */
            theKeyType = pKeyType;
            theAlgorithm = theKeyType.getAlgorithm();

            /* Add it to the registrations */
            theAsymRegister.add(this);
        }

        /**
         * Derive the KeyPair from encoded forms
         * @param pPrivate the Encoded private form (may be null for public-only)
         * @param pPublic the Encoded public form
         * @return the KeyPair
         * @throws ModelException
         */
        private KeyPair deriveKeyPair(byte[] pPrivate,
                                      byte[] pPublic) throws ModelException {
            /* If we have not allocated the factory */
            if (theFactory == null) {
                /* Protect against Exceptions */
                try {
                    /* Allocate the new factory */
                    theFactory = KeyFactory.getInstance(theAlgorithm, theProviderName);
                } catch (Exception e) {
                    /* Throw the exception */
                    throw new ModelException(ExceptionClass.CRYPTO, "Failed to create key factory", e);
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
            }

            /* Catch exceptions */
            catch (Exception e) {
                /* Throw the exception */
                throw new ModelException(ExceptionClass.CRYPTO, "Failed to re-build KeyPair", e);
            }
        }

        /**
         * Generate new KeyPair
         * @return the KeyPair
         * @throws ModelException
         */
        private KeyPair generateKeyPair() throws ModelException {
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
                    }

                    /* Else standard RSA type */
                    else {
                        /* Initialise to required key size */
                        theGenerator.initialize(theKeyType.getKeySize(), theRandom);
                    }
                } catch (Exception e) {
                    /* Throw the exception */
                    throw new ModelException(ExceptionClass.CRYPTO, "Failed to create key pair generator", e);
                }
            }

            /* Generate the Key Pair */
            return theGenerator.generateKeyPair();
        }
    }

    /**
     * SymRegistration class
     */
    private class SymRegistration {
        /**
         * Symmetric Key Type
         */
        private final SymKeyType theKeyType;

        /**
         * Symmetric Algorithm
         */
        private final String theAlgorithm;

        /**
         * Key Length
         */
        private final int theKeyLen;

        /**
         * Key Generator for Symmetric Key Type
         */
        private KeyGenerator theGenerator = null;

        /**
         * Obtain the KeyType
         * @return the Key type
         */
        private SymKeyType getKeyType() {
            return theKeyType;
        }

        /**
         * Obtain the KeyLength
         * @return the Key length
         */
        private int getKeyLen() {
            return theKeyLen;
        }

        /**
         * Constructor
         * @param pKeyType the key type
         * @param pKeyLen the key length
         */
        private SymRegistration(SymKeyType pKeyType,
                                int pKeyLen) {
            /* Store the key type */
            theKeyType = pKeyType;
            theKeyLen = pKeyLen;
            theAlgorithm = theKeyType.getAlgorithm();

            /* Add it to the registrations */
            theSymRegister.add(this);
        }

        /**
         * Generate a new key of the required keyLength
         * @return the Secret Key
         * @throws ModelException
         */
        private SecretKey generateKey() throws ModelException {
            /* If we have not allocated the generator */
            if (theGenerator == null) {
                /* Protect against Exceptions */
                try {
                    /* Create the key generator */
                    theGenerator = KeyGenerator.getInstance(theAlgorithm, theProviderName);
                    theGenerator.init(theKeyLen, theRandom);
                } catch (Exception e) {
                    /* Throw the exception */
                    throw new ModelException(ExceptionClass.CRYPTO, "Failed to create key generator", e);
                }
            }

            /* Generate the Secret key */
            return theGenerator.generateKey();
        }
    }
}
