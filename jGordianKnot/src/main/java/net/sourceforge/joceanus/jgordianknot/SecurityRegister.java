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

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;

/**
 * Factory and Key Generator register.
 */
public class SecurityRegister {
    /**
     * The Security provider name.
     */
    private final String theProviderName;

    /**
     * The Secure Random generator.
     */
    private final SecureRandom theRandom;

    /**
     * List of asymmetric registrations.
     */
    private final List<AsymmetricRegister> theAsymRegister;

    /**
     * List of symmetric registrations.
     */
    private final List<SymmetricRegister> theSymRegister;

    /**
     * List of stream registrations.
     */
    private final List<StreamRegister> theStreamRegister;

    /**
     * The constructor.
     * @param pGenerator the security generator
     */
    protected SecurityRegister(final SecurityGenerator pGenerator) {
        /* Initialise values */
        SecurityProvider myProvider = pGenerator.getProvider();
        theRandom = pGenerator.getRandom();
        theProviderName = myProvider.getProvider();

        /* Allocate the lists */
        theAsymRegister = new ArrayList<AsymmetricRegister>();
        theSymRegister = new ArrayList<SymmetricRegister>();
        theStreamRegister = new ArrayList<StreamRegister>();
    }

    /**
     * Obtain the Asymmetric Registration.
     * @param pKeyType the key type
     * @return the registration
     */
    protected AsymmetricRegister getAsymRegistration(final AsymKeyType pKeyType) {
        /* Loop through the list */
        Iterator<AsymmetricRegister> myIterator = theAsymRegister.iterator();
        while (myIterator.hasNext()) {
            /* Access the item */
            AsymmetricRegister myReg = myIterator.next();

            /* If this is the right one, return it */
            if (myReg.getKeyType() == pKeyType) {
                return myReg;
            }
        }

        /* Create the new registration */
        return new AsymmetricRegister(pKeyType);
    }

    /**
     * Obtain the Symmetric Registration.
     * @param pKeyType the key type
     * @param pKeyLen the key length
     * @return the registration
     */
    protected SymmetricRegister getSymRegistration(final SymKeyType pKeyType,
                                                   final int pKeyLen) {
        /* Loop through the list */
        Iterator<SymmetricRegister> myIterator = theSymRegister.iterator();
        while (myIterator.hasNext()) {
            /* Access the item */
            SymmetricRegister myReg = myIterator.next();

            /* If this is the right one, return it */
            if ((myReg.getKeyType() == pKeyType)
                && (myReg.getKeyLen() == pKeyLen)) {
                return myReg;
            }
        }

        /* Create the new registration */
        return new SymmetricRegister(pKeyType, pKeyLen);
    }

    /**
     * Obtain the Stream Registration.
     * @param pKeyType the key type
     * @param pKeyLen the key length
     * @return the registration
     */
    protected StreamRegister getStreamRegistration(final StreamKeyType pKeyType,
                                                   final int pKeyLen) {
        /* Loop through the list */
        Iterator<StreamRegister> myIterator = theStreamRegister.iterator();
        while (myIterator.hasNext()) {
            /* Access the item */
            StreamRegister myReg = myIterator.next();

            /* If this is the right one, return it */
            if ((myReg.getKeyType() == pKeyType)
                && (myReg.getKeyLen() == pKeyLen)) {
                return myReg;
            }
        }

        /* Create the new registration */
        return new StreamRegister(pKeyType, pKeyLen);
    }

    /**
     * Asymmetric Registration class.
     */
    protected final class AsymmetricRegister {
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
        private AsymmetricRegister(final AsymKeyType pKeyType) {
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
        protected KeyPair deriveKeyPair(final byte[] pPrivate,
                                        final byte[] pPublic) throws JDataException {
            /* If we have not allocated the factory */
            if (theFactory == null) {
                /* Protect against Exceptions */
                try {
                    /* Allocate the new factory */
                    theFactory = KeyFactory.getInstance(theAlgorithm, theProviderName);
                } catch (NoSuchProviderException | NoSuchAlgorithmException e) {
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
        protected KeyPair generateKeyPair() throws JDataException {
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
                } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
                    /* Throw the exception */
                    throw new JDataException(ExceptionClass.CRYPTO, e.getMessage(), e);
                }
            }

            /* Generate the Key Pair */
            return theGenerator.generateKeyPair();
        }
    }

    /**
     * Symmetric Registration class.
     */
    protected final class SymmetricRegister {
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
        private SymmetricRegister(final SymKeyType pKeyType,
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
        protected SecretKey generateKey() throws JDataException {
            /* If we have not allocated the generator */
            if (theGenerator == null) {
                /* Protect against Exceptions */
                try {
                    /* Create the key generator */
                    theGenerator = KeyGenerator.getInstance(theAlgorithm, theProviderName);
                    theGenerator.init(theKeyLen, theRandom);
                } catch (NoSuchProviderException | NoSuchAlgorithmException e) {
                    /* Throw the exception */
                    throw new JDataException(ExceptionClass.CRYPTO, e.getMessage(), e);
                }
            }

            /* Generate the Secret key */
            return theGenerator.generateKey();
        }
    }

    /**
     * Stream Registration class.
     */
    protected final class StreamRegister {
        /**
         * Stream Key Type.
         */
        private final StreamKeyType theKeyType;

        /**
         * Stream Algorithm.
         */
        private final String theAlgorithm;

        /**
         * Key Length.
         */
        private final int theKeyLen;

        /**
         * Key Generator for Stream Key Type.
         */
        private KeyGenerator theGenerator = null;

        /**
         * Obtain the KeyType.
         * @return the Key type
         */
        private StreamKeyType getKeyType() {
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
        private StreamRegister(final StreamKeyType pKeyType,
                               final int pKeyLen) {
            /* Store the key type */
            theKeyType = pKeyType;
            theKeyLen = pKeyLen;
            theAlgorithm = theKeyType.getAlgorithm(theKeyLen == SecurityGenerator.SMALL_KEYLEN);

            /* Add it to the registrations */
            theStreamRegister.add(this);
        }

        /**
         * Generate a new key of the required keyLength.
         * @return the Secret Key
         * @throws JDataException on error
         */
        protected SecretKey generateKey() throws JDataException {
            /* If we have not allocated the generator */
            if (theGenerator == null) {
                /* Protect against Exceptions */
                try {
                    /* Create the key generator */
                    theGenerator = KeyGenerator.getInstance(theAlgorithm, theProviderName);
                    theGenerator.init(theKeyLen, theRandom);
                } catch (NoSuchProviderException | NoSuchAlgorithmException e) {
                    /* Throw the exception */
                    throw new JDataException(ExceptionClass.CRYPTO, e.getMessage(), e);
                }
            }

            /* Generate the Secret key */
            return theGenerator.generateKey();
        }
    }
}
