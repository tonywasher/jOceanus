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
import java.util.Arrays;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot Key Encapsulation.
 */
public abstract class GordianKeyEncapsulation {
    /**
     * InitVectorLength.
     */
    protected static final int INITLEN = 16;

    /**
     * The factory.
     */
    private final GordianFactory theFactory;

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
     */
    protected GordianKeyEncapsulation(final GordianFactory pFactory) {
        theFactory = pFactory;
    }

    /**
     * Obtain the random.
     * @return the random
     */
    protected SecureRandom getRandom() {
        return theFactory.getRandom();
    }

    /**
     * Obtain Digest.
     * @param pDigestSpec the digestSpec
     * @return the default digest
     * @throws OceanusException on error
     */
    protected GordianDigest getDigest(final GordianDigestSpec pDigestSpec) throws OceanusException {
        return theFactory.createDigest(pDigestSpec);
    }

    /**
     * Store secret.
     * @param pSecret the secret bytes
     * @param pInitVector the initVector
     */
    protected void storeSecret(final byte[] pSecret,
                               final byte[] pInitVector) {
        /* Store the details */
        theSecret = Arrays.copyOf(pSecret, pSecret.length);
        theInitVector = Arrays.copyOf(pInitVector, pInitVector.length);
    }

    /**
     * Derive keySet.
     * @return the keySet
     * @throws OceanusException on exception
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
     * @throws OceanusException on exception
     */
    public <T> GordianKey<T> deriveKey(final T pKeyType) throws OceanusException {
        final GordianKeyGenerator<T> myGenerator = theFactory.getKeyGenerator(pKeyType);
        return myGenerator.generateKeyFromSecret(theSecret, theInitVector);
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
     */
    protected GordianPrivateKey getPrivateKey(final GordianKeyPair pKeyPair) {
        return pKeyPair.getPrivateKey();
    }

    /**
     * GordianKnot Key Encapsulation Sender.
     */
    public abstract static class GordianKEMSender
            extends GordianKeyEncapsulation {
        /**
         * The cipherTextt.
         */
        private byte[] theCipherText;

        /**
         * Constructor.
         * @param pFactory the factory
         */
        protected GordianKEMSender(final GordianFactory pFactory) {
            super(pFactory);
        }

        /**
         * Store secret.
         * @param pCipherText the cipherText
         */
        protected void storeCipherText(final byte[] pCipherText) {
            theCipherText = Arrays.copyOf(pCipherText, pCipherText.length);
        }

        /**
         * Obtain the cipherText.
         * @return the cipherText
         */
        public byte[] getCipherText() {
            return theCipherText;
        }
    }
}
