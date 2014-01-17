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
package net.sourceforge.joceanus.jgordianknot;

import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.SecretKey;

import net.sourceforge.joceanus.jgordianknot.SecurityRegister.SymmetricRegister;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Symmetric Key implementation.
 */
public class SymmetricKey {
    /**
     * Encrypted ID Key Size.
     */
    public static final int IDSIZE = 128;

    /**
     * The Secret Key.
     */
    private final SecretKey theKey;

    /**
     * The Key Type.
     */
    private final SymKeyType theKeyType;

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
     * Obtain the symmetric key type.
     * @return the symmetric key type
     */
    public SymKeyType getKeyType() {
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
     * SymmetricKey Generator.
     * @param pGenerator the security generator
     * @return the new SymmetricKey
     * @throws JOceanusException on error
     */
    protected static SymmetricKey generateSymmetricKey(final SecurityGenerator pGenerator) throws JOceanusException {
        /* Access random generator */
        SecureRandom myRandom = pGenerator.getRandom();
        SymKeyType[] myType = SymKeyType.getRandomTypes(1, myRandom);

        /* Generate a SymKey for the SymKey type */
        return generateSymmetricKey(pGenerator, myType[0]);
    }

    /**
     * SymmetricKey Generator.
     * @param pGenerator the security generator
     * @param pKeyType Symmetric KeyType
     * @return the new SymmetricKey
     * @throws JOceanusException on error
     */
    protected static SymmetricKey generateSymmetricKey(final SecurityGenerator pGenerator,
                                                       final SymKeyType pKeyType) throws JOceanusException {
        /* Generate a new Secret Key */
        SecurityRegister myRegister = pGenerator.getRegister();
        SymmetricRegister myReg = myRegister.getSymRegistration(pKeyType, pGenerator.getKeyLen());
        SecretKey myKey = myReg.generateKey();

        /* Generate a SymKey for the SymKey type */
        return new SymmetricKey(pGenerator, pKeyType, myKey);
    }

    /**
     * Constructor for a symmetric key.
     * @param pGenerator the security generator
     * @param pKeyType Symmetric KeyType
     * @param pKey Secret Key for algorithm
     * @throws JOceanusException on error
     */
    protected SymmetricKey(final SecurityGenerator pGenerator,
                           final SymKeyType pKeyType,
                           final SecretKey pKey) throws JOceanusException {
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

        /* Make sure that the object is a Symmetric Key */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target Key */
        SymmetricKey myThat = (SymmetricKey) pThat;

        /* Not equal if different key-types */
        if (myThat.theKeyType != theKeyType) {
            return false;
        }

        /* Ensure that the secret key is identical */
        return Arrays.equals(myThat.theEncodedKeyDef, theEncodedKeyDef);
    }

    /**
     * Obtain data cipher for encryption/decryption.
     * @return the Data Cipher
     * @throws JOceanusException on error
     */
    public DataCipher getDataCipher() throws JOceanusException {
        /* Create the Data Cipher */
        return new DataCipher(this);
    }

    /**
     * Obtain stream cipher for encryption/decryption.
     * @param pMode the cipher mode
     * @return the Stream Cipher
     * @throws JOceanusException on error
     */
    public StreamCipher getStreamCipher(final CipherMode pMode) throws JOceanusException {
        /* Create the Stream Cipher */
        return new StreamCipher(this, pMode);
    }
}
