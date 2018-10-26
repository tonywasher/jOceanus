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

import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Key Agreement Specification.
 */
public abstract class GordianAgreement {
    /**
     * InitVectorLength.
     */
    private static final int INITLEN = 32;

    /**
     * The factory.
     */
    private final GordianFactory theFactory;

    /**
     * The agreementSpec.
     */
    private final GordianAgreementSpec theSpec;

    /**
     * The shared secret.
     */
    private byte[] theSecret;

    /**
     * The initVector.
     */
    private byte[] theInitVector;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSpec the agreementSpec
     */
    GordianAgreement(final GordianFactory pFactory,
                     final GordianAgreementSpec pSpec) {
        theFactory = pFactory;
        theSpec = pSpec;
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    protected GordianFactory getFactory() {
        return theFactory;
    }

    /**
     * Obtain the agreementSpec.
     * @return the spec
     */
    protected GordianAgreementSpec getAgreementSpec() {
        return theSpec;
    }

    /**
     * Obtain the random.
     * @return the random
     */
    protected SecureRandom getRandom() {
        return theFactory.getRandom();
    }

    /**
     * CheckKeyPair.
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    protected void checkKeyPair(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Check that the KeyPair is valid */
        if (pKeyPair.getKeySpec().getKeyType() != theSpec.getAsymKeyType()) {
            throw new GordianDataException("Invalid KeyPair");
        }
    }

    /**
     * Obtain public key from pair.
     * @param pKeyPair the keyPair
     * @return the public key
     */
    protected GordianPublicKey getPublicKey(final GordianKeyPair pKeyPair) {
        return pKeyPair.getPublicKey();
    }

    /**
     * Obtain private key from pair.
     * @param pKeyPair the keyPair
     * @return the private key
     * @throws OceanusException on error
     */
    protected GordianPrivateKey getPrivateKey(final GordianKeyPair pKeyPair) throws OceanusException {
        if (pKeyPair.isPublicOnly()) {
            throw new GordianDataException("missing privateKey");
        }
        return pKeyPair.getPrivateKey();
    }

    /**
     * Create a new initVector.
     * @return the initVector
     */
    protected byte[] newInitVector() {
        theInitVector = new byte[INITLEN];
        theFactory.getRandom().nextBytes(theInitVector);
        return Arrays.copyOf(theInitVector, theInitVector.length);
    }

    /**
     * Store initVector.
     * @param pInitVector the initVectore
     */
    void storeInitVector(final byte[] pInitVector) {
        /* Store the details */
        theInitVector = Arrays.copyOf(pInitVector, pInitVector.length);
    }

    /**
     * Store secret.
     * @param pSecret the secret
     */
    protected void storeSecret(final byte[] pSecret) {
        /* Store the details */
        theSecret = Arrays.copyOf(pSecret, pSecret.length);
        Arrays.fill(pSecret, (byte) 0);
    }

    /**
     * Derive keySet.
     * @return the keySet
     * @throws OceanusException on error
     */
    public GordianKeySet deriveKeySet() throws OceanusException {
        final GordianKeySet myKeySet = new GordianKeySet(theFactory);
        myKeySet.buildFromSecret(theSecret, theInitVector);
        return myKeySet;
    }

    /**
     * Derive key.
     * @param <T> the type of key
     * @param pKeyType the key type
     * @return the key
     * @throws OceanusException on error
     */
    public <T> GordianKey<T> deriveKey(final T pKeyType) throws OceanusException {
        final GordianKeyGenerator<T> myGenerator = theFactory.getKeyGenerator(pKeyType);
        return myGenerator.generateKeyFromSecret(theSecret, theInitVector);
    }

    /**
     * Derive independent keySet.
     * @return the keySet
     * @throws OceanusException on error
     */
    public GordianKeySet deriveIndependentKeySet() throws OceanusException {
        /* The phrase is built of the first quarter of the secret and the first quarter of the initVector */
        final int myPhraseSecLen = theSecret.length >> 2;
        final int myPhraseIVLen = theInitVector.length >> 2;

        /* Build the phrase */
        final byte[] myPhrase = new byte[myPhraseSecLen + myPhraseIVLen];
        System.arraycopy(theSecret, 0, myPhrase, 0, myPhraseSecLen);
        System.arraycopy(theInitVector, 0, myPhrase,  myPhraseSecLen, myPhraseIVLen);

        /* Access shortened secret and IV */
        final byte[] mySecret = Arrays.copyOfRange(theSecret, myPhraseSecLen, theSecret.length);
        final byte[] myIV = Arrays.copyOfRange(theInitVector, myPhraseIVLen, theInitVector.length);

        /* Create a new Factory using the phrase */
        final GordianParameters myParms = new GordianParameters();
        myParms.setSecurityPhrase(myPhrase);
        final GordianFactory myFactory = theFactory.newFactory(myParms);

        /* Create the keySet */
        final GordianKeySet myKeySet = new GordianKeySet(myFactory);
        myKeySet.buildFromSecret(mySecret, myIV);
        return myKeySet;
    }

    /**
     * NullKeyDerivation.
     */
    public static final class GordianNullKeyDerivation
            implements DerivationFunction {
        /**
         * The key.
         */
        private byte[] theKey;

        @Override
        public int generateBytes(final byte[] pBuffer,
                                 final int pOffset,
                                 final int pLength) {
            /* Create the array that is to be copied */
            final byte[] myKey = pLength > theKey.length
                                 ? Arrays.copyOf(theKey, pLength)
                                 : theKey;
            System.arraycopy(myKey, 0, pBuffer, pOffset, pLength);
            return pLength;
        }

        @Override
        public void init(final DerivationParameters pParms) {
            final byte[] mySecret = ((KDFParameters) pParms).getSharedSecret();
            theKey = Arrays.copyOf(mySecret, mySecret.length);
        }
    }

    /**
     * Encapsulation Agreement.
     */
    public abstract static class GordianEncapsulationAgreement
            extends GordianAgreement {
        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the agreementSpec
         */
        protected GordianEncapsulationAgreement(final GordianFactory pFactory,
                                                final GordianAgreementSpec pSpec) {
            super(pFactory, pSpec);
        }

        /**
         * Initiate the agreement.
         * @param pTarget the target keyPair
         * @return the message
         * @throws OceanusException on error
         */
        public abstract byte[] initiateAgreement(GordianKeyPair pTarget) throws OceanusException;

        /**
         * Create the message.
         * @param pBase the base message
         * @return the composite message
         */
        protected byte[] createMessage(final byte[] pBase) {
            /* Create buffer for message */
            final int myLen = pBase.length;
            final byte[] myMessage = new byte[myLen + INITLEN];

            /* Create the message */
            System.arraycopy(newInitVector(), 0, myMessage, 0, INITLEN);
            System.arraycopy(pBase, 0, myMessage, INITLEN, myLen);
            return myMessage;
        }

        /**
         * Accept the agreement.
         * @param pTarget the target keyPair
         * @param pMessage the incoming message
         * @throws OceanusException on error
         */
        public abstract void acceptAgreement(GordianKeyPair pTarget,
                                             byte[] pMessage)  throws OceanusException;

        /**
         * Parse the incoming message.
         * @param pMessage the incoming message
         * @return the base message
         */
        protected byte[] parseMessage(final byte[] pMessage) {
            /* Obtain initVector */
            final byte[] myInitVector = new byte[INITLEN];
            System.arraycopy(pMessage, 0, myInitVector, 0, INITLEN);
            storeInitVector(myInitVector);

            /* Obtain base message */
            final int myBaseLen = pMessage.length - INITLEN;
            final byte[] myBase = new byte[myBaseLen];
            System.arraycopy(pMessage, INITLEN, myBase, 0, myBaseLen);
            return myBase;
        }
    }

    /**
     * Basic Agreement.
     */
    public abstract static class GordianBasicAgreement
            extends GordianAgreement {
        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the agreementSpec
         */
        protected GordianBasicAgreement(final GordianFactory pFactory,
                                        final GordianAgreementSpec pSpec) {
            super(pFactory, pSpec);
        }

        /**
         * Initiate the agreement.
         * @param pSelf the source keyPair
         * @param pTarget the target keyPair
         * @return the message
         * @throws OceanusException on error
         */
        public abstract byte[] initiateAgreement(GordianKeyPair pSelf,
                                                 GordianKeyPair pTarget) throws OceanusException;

        /**
         * Create the message.
         * @return the message
         */
        protected byte[] createMessage() {
            /* Create the message */
            return newInitVector();
        }

        /**
         * Accept the agreement.
         * @param pSource the source keyPair
         * @param pTarget the target keyPair
         * @param pMessage the incoming message
         * @throws OceanusException on error
         */
        public abstract void acceptAgreement(GordianKeyPair pSource,
                                             GordianKeyPair pTarget,
                                             byte[] pMessage)  throws OceanusException;

        /**
         * Parse the incoming message.
         * @param pMessage the incoming message
         */
        protected void parseMessage(final byte[] pMessage) {
            /* Obtain initVector */
            final byte[] myInitVector = new byte[INITLEN];
            System.arraycopy(pMessage, 0, myInitVector, 0, INITLEN);
            storeInitVector(myInitVector);
        }
    }

    /**
     * Ephemeral Agreement.
     */
    public abstract static class GordianEphemeralAgreement
            extends GordianAgreement {
        /**
         * The owning KeyPair.
         */
        private GordianKeyPair theOwner;

        /**
         * The ephemeral KeyPair.
         */
        private GordianKeyPair theEphemeral;

        /**
         * The partner ephemeral KeyPair.
         */
        private GordianKeyPair thePartnerEphemeral;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSpec the agreementSpec
         */
        protected GordianEphemeralAgreement(final GordianFactory pFactory,
                                            final GordianAgreementSpec pSpec) {
            super(pFactory, pSpec);
        }

        /**
         * Obtain the Ephemeral keyPair.
         * @return  the keyPair
         */
        protected GordianKeyPair getOwnerKeyPair() {
            return theOwner;
        }

        /**
         * Obtain the Ephemeral keyPair.
         * @return  the keyPair
         */
        protected GordianKeyPair getEphemeralKeyPair() {
            return theEphemeral;
        }

        /**
         * Obtain the partner Ephemeral keyPair.
         * @return  the keyPair
         */
        protected GordianKeyPair getPartnerEphemeralKeyPair() {
            return thePartnerEphemeral;
        }

        /**
         * Initiate the agreement.
         * @param pInitiator the initiating keyPair
         * @return the composite message
         * @throws OceanusException on error
         */
        protected byte[] initiateAgreement(final GordianKeyPair pInitiator) throws OceanusException {
            /* Check the keyPair */
            checkKeyPair(pInitiator);

            /* Store the keyPair */
            theOwner = pInitiator;

            /* Create ephemeral key */
            final GordianKeyPairGenerator myGenerator = getFactory().getKeyPairGenerator(theOwner.getKeySpec());
            theEphemeral = myGenerator.generateKeyPair();
            final X509EncodedKeySpec myKeySpec = myGenerator.getX509Encoding(theEphemeral);
            final byte[] myKeyBytes = myKeySpec.getEncoded();

             /* Create buffer for message */
            final int myLen = myKeyBytes.length;
            final byte[] myMessage = new byte[myLen + INITLEN];

            /* Create the message */
            System.arraycopy(newInitVector(), 0, myMessage, 0, INITLEN);
            System.arraycopy(myKeyBytes, 0, myMessage, INITLEN, myLen);
            return myMessage;
        }

        /**
         * Parse the incoming message.
         * @param pSource the source keyPair
         * @param pResponder the responding keyPair
         * @param pMessage the incoming message
         * @return the ephemeral keySpec
         * @throws OceanusException on error
         */
        public abstract byte[] acceptAgreement(GordianKeyPair pSource,
                                               GordianKeyPair pResponder,
                                               byte[] pMessage) throws OceanusException;

        /**
         * Parse the incoming message.
         * @param pResponder the responding keyPair
         * @param pMessage the incoming message
         * @return the ephemeral keySpec
         * @throws OceanusException on error
         */
        protected byte[] parseMessage(final GordianKeyPair pResponder,
                                      final byte[] pMessage) throws OceanusException {
            /* Check the keyPair */
            checkKeyPair(pResponder);

            /* Store the keyPair */
            theOwner = pResponder;

            /* Obtain initVector */
            final byte[] myInitVector = new byte[INITLEN];
            System.arraycopy(pMessage, 0, myInitVector, 0, INITLEN);
            storeInitVector(myInitVector);

            /* Obtain keySpec */
            final int myBaseLen = pMessage.length - INITLEN;
            final byte[] myBase = new byte[myBaseLen];
            final X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(myBase);

            /* Create ephemeral key */
            final GordianKeyPairGenerator myGenerator = getFactory().getKeyPairGenerator(theOwner.getKeySpec());
            theEphemeral = myGenerator.generateKeyPair();

            /* Derive partner ephemeral key */
            thePartnerEphemeral = myGenerator.derivePublicOnlyKeyPair(myKeySpec);

            /* Return the ephemeral keySpec */
            return myGenerator.getX509Encoding(theEphemeral).getEncoded();
        }

        /**
         * Confirm the agreement.
         * @param pResponder the responding keyPair
         * @param pKeySpec the target ephemeral keyPair
         * @throws OceanusException on error
         */
        public abstract void confirmAgreement(GordianKeyPair pResponder,
                                              byte[] pKeySpec) throws OceanusException;

        /**
         * Parse the ephemeral keySpec.
         * @param pKeySpec the target ephemeral keySpec
         * @throws OceanusException on error
         */
        protected void parseEphemeral(final byte[] pKeySpec) throws OceanusException {
            /* Obtain keySpec */
            final X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(pKeySpec);

            /* Derive partner ephemeral key */
            final GordianKeyPairGenerator myGenerator = getFactory().getKeyPairGenerator(theOwner.getKeySpec());
            thePartnerEphemeral = myGenerator.derivePublicOnlyKeyPair(myKeySpec);
        }
    }
}
