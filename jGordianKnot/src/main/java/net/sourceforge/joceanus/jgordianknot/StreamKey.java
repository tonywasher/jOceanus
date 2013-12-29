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

import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.SecretKey;

import net.sourceforge.joceanus.jdatamanager.JDataException;

/**
 * Stream Key implementation.
 */
public class StreamKey {
    /**
     * The Secret Key.
     */
    private final SecretKey theKey;

    /**
     * The Key Type.
     */
    private final StreamKeyType theKeyType;

    /**
     * The security generator.
     */
    private final SecurityGenerator theGenerator;

    /**
     * The Key Length.
     */
    private final int theKeyLen;

    /**
     * The Encoded KeyDef.
     */
    private final byte[] theEncodedKeyDef;

    /**
     * Obtain the generator.
     * @return the generator
     */
    protected SecurityGenerator getGenerator() {
        return theGenerator;
    }

    /**
     * Obtain the random generator.
     * @return the random generator
     */
    public SecureRandom getRandom() {
        return theGenerator.getRandom();
    }

    /**
     * Obtain the secret key.
     * @return the secret key
     */
    protected SecretKey getSecretKey() {
        return theKey;
    }

    /**
     * Obtain the stream key type.
     * @return the stream key type
     */
    public StreamKeyType getKeyType() {
        return theKeyType;
    }

    /**
     * Obtain the key length.
     * @return the secret key length
     */
    public int getKeyLength() {
        return theKeyLen;
    }

    /**
     * Constructor for a stream key of specified type.
     * @param pGenerator the security generator
     * @param pKeyType Stream KeyType
     * @throws JDataException on error
     */
    protected StreamKey(final SecurityGenerator pGenerator,
                        final StreamKeyType pKeyType) throws JDataException {
        /* Store the KeyType and the Generator */
        theKeyType = pKeyType;
        theKeyLen = pGenerator.getKeyLen();
        theGenerator = pGenerator;

        /* Generate the new key */
        theKey = theGenerator.generateSecretKey(theKeyType, theKeyLen);
        theEncodedKeyDef = theKey.getEncoded();
    }

    /**
     * Constructor for a stream key of random type.
     * @param pGenerator the security generator
     * @throws JDataException on error
     */
    protected StreamKey(final SecurityGenerator pGenerator) throws JDataException {
        /* Create StreamKey for random key type */
        this(pGenerator, StreamKeyType.getRandomTypes(1, pGenerator.getRandom())[0]);
    }

    /**
     * Constructor for a decoded stream key.
     * @param pGenerator the security generator
     * @param pKey Secret Key for algorithm
     * @param pKeyType Stream KeyType
     * @throws JDataException on error
     */
    protected StreamKey(final SecurityGenerator pGenerator,
                        final SecretKey pKey,
                        final StreamKeyType pKeyType) throws JDataException {
        /* Store the KeyType and the Generator */
        theKeyType = pKeyType;
        theKeyLen = pKey.getEncoded().length;
        theGenerator = pGenerator;
        theKey = pKey;
        theEncodedKeyDef = theKey.getEncoded();
    }

    @Override
    public int hashCode() {
        /* Calculate and return the hashCode for this symmetric key */
        int hashCode = SecurityGenerator.HASH_PRIME
                       * Arrays.hashCode(theEncodedKeyDef);
        hashCode += theKeyType.hashCode();
        return hashCode;
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is a Stream Key */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target Key */
        StreamKey myThat = (StreamKey) pThat;

        /* Not equal if different key-types */
        if (myThat.theKeyType != theKeyType) {
            return false;
        }

        /* Ensure that the secret key is identical */
        return Arrays.equals(myThat.theEncodedKeyDef, theEncodedKeyDef);
    }

    /**
     * Obtain stream cipher for encryption/decryption.
     * @return the Stream Cipher
     * @throws JDataException on error
     */
    public StreamCipher getStreamCipher() throws JDataException {
        /* Return the Stream Cipher */
        return new StreamCipher(this);
    }
}
