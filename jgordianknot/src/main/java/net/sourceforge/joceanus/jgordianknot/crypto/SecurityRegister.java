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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import net.sourceforge.joceanus.jgordianknot.JGordianCryptoException;
import net.sourceforge.joceanus.jtethys.DataConverter;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Factory and Key Generator register.
 */
public class SecurityRegister {
    /**
     * The Security generator.
     */
    private final SecurityGenerator theGenerator;

    /**
     * The Security provider name.
     */
    private final String theProviderName;

    /**
     * The Secure Random generator.
     */
    private final SecureRandom theRandom;

    /**
     * Do we use a long hash?
     */
    private final boolean useLongHash;

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
     * List of Mac registrations.
     */
    private final List<MacRegister> theMacRegister;

    /**
     * The constructor.
     * @param pGenerator the security generator
     */
    protected SecurityRegister(final SecurityGenerator pGenerator) {
        /* Initialise values */
        theGenerator = pGenerator;
        theRandom = pGenerator.getRandom();
        theProviderName = pGenerator.getProviderName();
        useLongHash = pGenerator.useLongHash();

        /* Allocate the lists */
        theAsymRegister = new ArrayList<AsymmetricRegister>();
        theSymRegister = new ArrayList<SymmetricRegister>();
        theStreamRegister = new ArrayList<StreamRegister>();
        theMacRegister = new ArrayList<MacRegister>();
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
            if (myReg.isMatch(pKeyType)) {
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
            if (myReg.isMatch(pKeyType, pKeyLen)) {
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
            if (myReg.isMatch(pKeyType, pKeyLen)) {
                return myReg;
            }
        }

        /* Create the new registration */
        return new StreamRegister(pKeyType, pKeyLen);
    }

    /**
     * Obtain the Mac Registration.
     * @param pDigestType the digest type
     * @param pKeyLen the key length
     * @return the registration
     */
    protected MacRegister getMacRegistration(final DigestType pDigestType,
                                             final int pKeyLen) {
        /* Loop through the list */
        Iterator<MacRegister> myIterator = theMacRegister.iterator();
        while (myIterator.hasNext()) {
            /* Access the item */
            MacRegister myReg = myIterator.next();

            /* If this is the right one, return it */
            if (myReg.isMatch(pDigestType, pKeyLen)) {
                return myReg;
            }
        }

        /* Create the new registration */
        return new MacRegister(pDigestType, pKeyLen);
    }

    /**
     * Obtain the Mac Registration.
     * @param pMacType the Mac type
     * @param pKeyType the key type
     * @param pKeyLen the key length
     * @return the registration
     */
    protected MacRegister getMacRegistration(final MacType pMacType,
                                             final SymKeyType pKeyType,
                                             final int pKeyLen) {
        /* Loop through the list */
        Iterator<MacRegister> myIterator = theMacRegister.iterator();
        while (myIterator.hasNext()) {
            /* Access the item */
            MacRegister myReg = myIterator.next();

            /* If this is the right one, return it */
            if (myReg.isMatch(pMacType, pKeyType, pKeyLen)) {
                return myReg;
            }
        }

        /* Create the new registration */
        return new MacRegister(pMacType, pKeyType, pKeyLen);
    }

    /**
     * Obtain the Mac Registration.
     * @param pMacType the Mac type
     * @param pKeyLen the key length
     * @return the registration
     */
    protected MacRegister getMacRegistration(final MacType pMacType,
                                             final int pKeyLen) {
        /* Loop through the list */
        Iterator<MacRegister> myIterator = theMacRegister.iterator();
        while (myIterator.hasNext()) {
            /* Access the item */
            MacRegister myReg = myIterator.next();

            /* If this is the right one, return it */
            if (myReg.isMatch(pMacType, pKeyLen)) {
                return myReg;
            }
        }

        /* Create the new registration */
        return new MacRegister(pMacType, pKeyLen);
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
         * @throws JOceanusException on error
         */
        protected KeyPair deriveKeyPair(final byte[] pPrivate,
                                        final byte[] pPublic) throws JOceanusException {
            /* If we have not allocated the factory */
            if (theFactory == null) {
                /* Protect against Exceptions */
                try {
                    /* Allocate the new factory */
                    theFactory = KeyFactory.getInstance(theAlgorithm, theProviderName);
                } catch (NoSuchProviderException | NoSuchAlgorithmException e) {
                    /* Throw the exception */
                    throw new JGordianCryptoException("Failed to create key factory", e);
                }
            }

            /* Protect against exceptions */
            try {
                /* if we have a private key */
                PrivateKey myPrivate = null;
                if (pPrivate != null) {
                    /* Build the private key */
                    PKCS8EncodedKeySpec myPrivSpec = new PKCS8EncodedKeySpec(pPrivate);
                    myPrivate = theFactory.generatePrivate(myPrivSpec);
                }

                /* Build the public key */
                X509EncodedKeySpec myPubSpec = new X509EncodedKeySpec(pPublic);
                PublicKey myPublic = theFactory.generatePublic(myPubSpec);

                /* Return the private key */
                return new KeyPair(myPublic, myPrivate);

                /* Catch exceptions */
            } catch (InvalidKeySpecException e) {
                /* Throw the exception */
                throw new JGordianCryptoException("Failed to re-build KeyPair", e);
            }
        }

        /**
         * Generate new KeyPair.
         * @return the KeyPair
         * @throws JOceanusException on error
         */
        protected KeyPair generateKeyPair() throws JOceanusException {
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
                    throw new JGordianCryptoException(e.getMessage(), e);
                }
            }

            /* Generate the Key Pair */
            return theGenerator.generateKeyPair();
        }

        /**
         * Does this registration match the request.
         * @param pKeyType the key type
         * @return true/false
         */
        protected boolean isMatch(final AsymKeyType pKeyType) {
            /* Check identity */
            return theKeyType.equals(pKeyType);
        }
    }

    /**
     * Secret Registration class.
     */
    private abstract class SecretRegister {
        /**
         * Algorithm.
         */
        private String theAlgorithm;

        /**
         * Key Length.
         */
        private final int theKeyLen;

        /**
         * Key Generator for Secret Key.
         */
        private KeyGenerator theKeyGenerator;

        /**
         * Set the algorithm.
         * @param pAlgorithm the algorithm
         */
        protected void setAlgorithm(final String pAlgorithm) {
            theAlgorithm = pAlgorithm;
        }

        /**
         * Obtain the KeyLength.
         * @return the Key length
         */
        protected int getKeyLen() {
            return theKeyLen;
        }

        /**
         * Obtain the Algorithm.
         * @return the algorithm
         */
        protected String getAlgorithm() {
            return theAlgorithm;
        }

        /**
         * Constructor.
         * @param pKeyLen the key length
         */
        private SecretRegister(final int pKeyLen) {
            /* Store the key type */
            theKeyLen = pKeyLen;
        }

        /**
         * Generate a new key of the required keyLength.
         * @return the Secret Key
         * @throws JOceanusException on error
         */
        protected SecretKey generateKey() throws JOceanusException {
            /* If we have not allocated the generator */
            if (theKeyGenerator == null) {
                /* Protect against Exceptions */
                try {
                    /* Create the key generator */
                    theKeyGenerator = KeyGenerator.getInstance(theAlgorithm, theProviderName);
                    theKeyGenerator.init(theKeyLen, theRandom);
                } catch (NoSuchProviderException | NoSuchAlgorithmException e) {
                    /* Throw the exception */
                    throw new JGordianCryptoException(e.getMessage(), e);
                }
            }

            /* Generate the Secret key */
            return theKeyGenerator.generateKey();
        }

        /**
         * Does this registration match the request.
         * @param pAlgorithm the algorithm
         * @param pKeyLen the key length
         * @return true/false
         */
        protected boolean isMatch(final String pAlgorithm,
                                  final int pKeyLen) {
            /* Check identity */
            return (theKeyLen == pKeyLen)
                   && theAlgorithm.equals(pAlgorithm);
        }

        /**
         * Does this registration match the request.
         * @param pKeyLen the key length
         * @return true/false
         */
        protected boolean isMatch(final int pKeyLen) {
            /* Check identity */
            return theKeyLen == pKeyLen;
        }

        /**
         * Build Secret Key from a Secret.
         * @param pSecret the derived Secret
         * @param pInitVector the initialisation vector
         * @return the new Secret Key
         * @throws JOceanusException on error
         */
        protected SecretKey buildSecretKey(final byte[] pSecret,
                                           final byte[] pInitVector) throws JOceanusException {
            /* Determine the key length in bytes */
            int myKeyLen = theKeyLen
                           / Byte.SIZE;

            /* Create a buffer to hold the resulting key and # of bytes built */
            byte[] myKeyBytes = new byte[myKeyLen];
            int myBuilt = 0;

            /* Access the last 8-bytes of the secret as a long */
            int myLen = pSecret.length;
            byte[] mySalt = Arrays.copyOfRange(pSecret, myLen
                                                        - DataConverter.BYTES_LONG, myLen);
            long mySeed = DataConverter.byteArrayToLong(mySalt);

            /* Calculate the DigestType to use */
            DigestType[] myDigest = DigestType.getSeedTypes(1, mySeed);

            /* Create the Mac and standard data */
            ByteArrayInteger myCount = new ByteArrayInteger();
            DataMac myMac = theGenerator.generateMac(myDigest[0], pSecret);

            /* while we need to generate more bytes */
            while (myBuilt < myKeyLen) {
                /* Build the key section */
                byte[] mySection = buildCipherSection(myMac, myCount.iterate(), pInitVector);

                /* Determine how many bytes of this hash should be used */
                int myNeeded = myKeyLen
                               - myBuilt;
                if (myNeeded > mySection.length) {
                    myNeeded = mySection.length;
                }

                /* Copy bytes across */
                System.arraycopy(mySection, 0, myKeyBytes, myBuilt, myNeeded);
                myBuilt += myNeeded;
            }

            /* Return the secret key specification */
            return new SecretKeySpec(myKeyBytes, theAlgorithm);
        }

        /**
         * Build Secret Key section.
         * @param pMac the Mac to utilise
         * @param pSection the section count
         * @param pInitVector the initialisation vector
         * @return the section
         * @throws JOceanusException on error
         */
        private byte[] buildCipherSection(final DataMac pMac,
                                          final byte[] pSection,
                                          final byte[] pInitVector) throws JOceanusException {
            /* Declare initial value */
            byte[] myResult = null;
            byte[] myHash = pInitVector;

            /* Access number of iterations */
            int iIterations = theGenerator.getNumHashIterations() >>> 1;

            /* Create the standard data */
            byte[] myAlgo = DataConverter.stringToByteArray(theAlgorithm);
            byte[] mySeed = theGenerator.getSecurityBytes();

            /* Update with security bytes, algorithm and section */
            pMac.update(mySeed);
            pMac.update(myAlgo);
            pMac.update(pSection);

            /* Loop through the iterations */
            for (int i = 0; i < iIterations; i++) {
                /* Add the existing result to hash */
                pMac.update(myHash);

                /* Calculate Mac */
                myHash = pMac.finish();
                myResult = DataConverter.combineHashes(myHash, myResult);
            }

            /* Return the result */
            return myResult;
        }
    }

    /**
     * Symmetric Registration class.
     */
    protected final class SymmetricRegister
            extends SecretRegister {
        /**
         * Symmetric Key Type.
         */
        private final SymKeyType theKeyType;

        /**
         * Constructor.
         * @param pKeyType the key type
         * @param pKeyLen the key length
         */
        private SymmetricRegister(final SymKeyType pKeyType,
                                  final int pKeyLen) {
            /* Call super constructor */
            super(pKeyLen);

            /* Store variables */
            theKeyType = pKeyType;
            setAlgorithm(theKeyType.getAlgorithm());

            /* Add it to the registrations */
            theSymRegister.add(this);
        }

        /**
         * Does this Key match the request.
         * @param pKeyType the Key type
         * @param pKeyLen the key length
         * @return true/false
         */
        private boolean isMatch(final SymKeyType pKeyType,
                                final int pKeyLen) {
            /* Ignore wrong key types */
            if (!theKeyType.equals(pKeyType)) {
                return false;
            }

            /* Check for match */
            return isMatch(pKeyLen);
        }
    }

    /**
     * Stream Registration class.
     */
    protected final class StreamRegister
            extends SecretRegister {
        /**
         * Stream Key Type.
         */
        private final StreamKeyType theKeyType;

        /**
         * Constructor.
         * @param pKeyType the key type
         * @param pKeyLen the key length
         */
        private StreamRegister(final StreamKeyType pKeyType,
                               final int pKeyLen) {
            /* Call super constructor */
            super(pKeyLen);

            /* Store variables */
            theKeyType = pKeyType;
            setAlgorithm(theKeyType.getAlgorithm(pKeyLen == SecurityGenerator.SMALL_KEYLEN));

            /* Add it to the registrations */
            theStreamRegister.add(this);
        }

        /**
         * Does this Key match the request.
         * @param pKeyType the Key type
         * @param pKeyLen the key length
         * @return true/false
         */
        private boolean isMatch(final StreamKeyType pKeyType,
                                final int pKeyLen) {
            /* Ignore wrong key types */
            if (!theKeyType.equals(pKeyType)) {
                return false;
            }

            /* Check for match */
            return isMatch(pKeyLen);
        }
    }

    /**
     * Mac Registration class.
     */
    protected final class MacRegister
            extends SecretRegister {
        /**
         * Mac Type.
         */
        private final MacType theMacType;

        /**
         * Constructor.
         * @param pDigestType the digest type
         * @param pKeyLen the key length
         */
        private MacRegister(final DigestType pDigestType,
                            final int pKeyLen) {
            /* Call super constructor */
            super(pKeyLen);

            /* Store the variables */
            theMacType = MacType.HMAC;
            setAlgorithm(pDigestType.getMacAlgorithm(useLongHash));

            /* Add it to the registrations */
            theMacRegister.add(this);
        }

        /**
         * Constructor.
         * @param pMacType the Mac type
         * @param pKeyType the key type
         * @param pKeyLen the key length
         */
        private MacRegister(final MacType pMacType,
                            final SymKeyType pKeyType,
                            final int pKeyLen) {
            /* Call super constructor */
            super(pKeyLen);

            /* Store the Mac type */
            theMacType = pMacType;
            setAlgorithm(pMacType.getAlgorithm(pKeyType));

            /* Add it to the registrations */
            theMacRegister.add(this);
        }

        /**
         * Constructor.
         * @param pMacType the Mac type
         * @param pKeyLen the key length
         */
        private MacRegister(final MacType pMacType,
                            final int pKeyLen) {
            /* Call super constructor */
            super(pKeyLen);

            /* Store the Mac type */
            theMacType = pMacType;
            setAlgorithm(pMacType.getKeyAlgorithm(useLongHash));

            /* Add it to the registrations */
            theMacRegister.add(this);
        }

        /**
         * Resolve KeySpec.
         * @param pKeySpec the Key specification.
         * @return the SecretKey
         */
        protected SecretKey resolveKeySpec(final byte[] pKeySpec) {
            return new SecretKeySpec(pKeySpec, getAlgorithm());
        }

        /**
         * Does this Mac match the request.
         * @param pDigestType the Digest type
         * @param pKeyLen the key length
         * @return true/false
         */
        private boolean isMatch(final DigestType pDigestType,
                                final int pKeyLen) {
            /* Ignore non-HMacs */
            if (!theMacType.equals(MacType.HMAC)) {
                return false;
            }

            /* Check for match */
            return isMatch(pDigestType.getMacAlgorithm(useLongHash), pKeyLen);
        }

        /**
         * Does this Mac match the request.
         * @param pMacType the Digest type
         * @param pKeyType the Key type
         * @param pKeyLen the key length
         * @return true/false
         */
        private boolean isMatch(final MacType pMacType,
                                final SymKeyType pKeyType,
                                final int pKeyLen) {
            /* Ignore wrong types */
            if (!theMacType.equals(pMacType)) {
                return false;
            }

            /* Check for match */
            return isMatch(pMacType.getAlgorithm(pKeyType), pKeyLen);
        }

        /**
         * Does this Mac match the request.
         * @param pMacType the Mac type
         * @param pKeyLen the key length
         * @return true/false
         */
        private boolean isMatch(final MacType pMacType,
                                final int pKeyLen) {
            /* Ignore wrong types */
            if (!theMacType.equals(pMacType)) {
                return false;
            }

            /* Check for match */
            return isMatch(pMacType.getKeyAlgorithm(useLongHash), pKeyLen);
        }
    }
}
