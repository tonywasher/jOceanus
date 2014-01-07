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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;

/**
 * Wrapper class for a stream cipher.
 */
public class StreamCipher {
    /**
     * Closed stream failure.
     */
    public static final String ERROR_CLOSED = "Stream is closed";

    /**
     * Cipher creation failure.
     */
    private static final String ERROR_CIPHER = "Failed to create Cipher";

    /**
     * Cipher initialisation failure.
     */
    private static final String ERROR_INIT = "Failed to initialise Cipher";

    /**
     * Cipher initialisation failure.
     */
    private static final String ERROR_UPDATE = "Failed to update Cipher";

    /**
     * Buffer size for transfers.
     */
    public static final int BUFSIZE = 1024;

    /**
     * The cipher.
     */
    private final Cipher theCipher;

    /**
     * The SecretKey.
     */
    private final SecretKey theSecretKey;

    /**
     * The transfer buffer.
     */
    private byte[] theBuffer = null;

    /**
     * Obtain the output buffer.
     * @return the output buffer
     */
    public byte[] getBuffer() {
        return theBuffer;
    }

    /**
     * Constructor.
     * @param pKey the symmetric key
     * @param pMode the cipher mode
     * @throws JDataException on error
     */
    public StreamCipher(final SymmetricKey pKey,
                        final CipherMode pMode) throws JDataException {
        /* Record key */
        theSecretKey = pKey.getSecretKey();
        theBuffer = new byte[BUFSIZE];

        /* protect against exceptions */
        try {
            /* Create a new cipher */
            SecurityGenerator myGenerator = pKey.getGenerator();
            SymKeyType myKeyType = pKey.getKeyType();
            CipherMode myMode = myKeyType.adjustCipherMode(pMode);
            theCipher = Cipher.getInstance(myKeyType.getDataCipher(myMode), myGenerator.getProviderName());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException e) {
            throw new JDataException(ExceptionClass.CRYPTO, ERROR_CIPHER, e);
        }
    }

    /**
     * Constructor.
     * @param pKey the stream key
     * @throws JDataException on error
     */
    public StreamCipher(final StreamKey pKey) throws JDataException {
        /* Record key */
        theSecretKey = pKey.getSecretKey();
        theBuffer = new byte[BUFSIZE];

        /* protect against exceptions */
        try {
            /* Create a new cipher */
            SecurityGenerator myGenerator = pKey.getGenerator();
            StreamKeyType myKeyType = pKey.getKeyType();
            theCipher = Cipher.getInstance(myKeyType.getAlgorithm(myGenerator.useRestricted()), myGenerator.getProviderName());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException e) {
            throw new JDataException(ExceptionClass.CRYPTO, ERROR_CIPHER, e);
        }
    }

    /**
     * Initialise encryption.
     * @param pRandom the random generator
     * @return the initialisation vector
     * @throws JDataException on error
     */
    public byte[] initialiseEncryption(final SecureRandom pRandom) throws JDataException {
        try {
            /* Initialise the cipher using the vector */
            theCipher.init(Cipher.ENCRYPT_MODE, theSecretKey, pRandom);

            /* Return the initialisation vector */
            return theCipher.getIV();

        } catch (InvalidKeyException e) {
            throw new JDataException(ExceptionClass.CRYPTO, ERROR_INIT, e);
        }
    }

    /**
     * Initialise encryption.
     * @param pVector initialisation vector
     * @throws JDataException on error
     */
    public void initialiseEncryption(final byte[] pVector) throws JDataException {
        try {
            /* Initialise the cipher using the vector */
            AlgorithmParameterSpec myParms = new IvParameterSpec(pVector);
            theCipher.init(Cipher.ENCRYPT_MODE, theSecretKey, myParms);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new JDataException(ExceptionClass.CRYPTO, ERROR_INIT, e);
        }
    }

    /**
     * Initialise decryption.
     * @param pVector initialisation vector
     * @throws JDataException on error
     */
    public void initialiseDecryption(final byte[] pVector) throws JDataException {
        try {
            /* Initialise the cipher using the vector */
            AlgorithmParameterSpec myParms = new IvParameterSpec(pVector);
            theCipher.init(Cipher.DECRYPT_MODE, theSecretKey, myParms);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new JDataException(ExceptionClass.CRYPTO, ERROR_INIT, e);
        }
    }

    /**
     * Update Cipher.
     * @param pBytes Bytes to update cipher with
     * @param pOffset offset within pBytes to read bytes from
     * @param pLength length of data to update with
     * @return number of bytes transferred to output buffer
     * @throws JDataException on error
     */
    public int update(final byte[] pBytes,
                      final int pOffset,
                      final int pLength) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Check how long a buffer we need */
            int iNumBytes = theCipher.getOutputSize(pLength);

            /* Extend the buffer if required */
            if (iNumBytes > theBuffer.length) {
                theBuffer = new byte[iNumBytes];
            }

            /* Update the data */
            iNumBytes = theCipher.update(pBytes, pOffset, pLength, theBuffer);

            /* Return to caller */
            return iNumBytes;
        } catch (ShortBufferException e) {
            throw new JDataException(ExceptionClass.CRYPTO, ERROR_UPDATE, e);
        }
    }

    /**
     * Finish Cipher encrypting/decrypting any data buffered within the cipher.
     * @return number of bytes transferred to output buffer
     * @throws JDataException on error
     */
    public int finish() throws JDataException {
        /* Protect against exceptions */
        try {
            /* Check how long a buffer we need to handle buffered data */
            int iNumBytes = theCipher.getOutputSize(0);

            /* Extend the buffer if required */
            if (iNumBytes > theBuffer.length) {
                theBuffer = new byte[iNumBytes];
            }

            /* Update the data */
            iNumBytes = theCipher.doFinal(theBuffer, 0);

            /* Return to caller */
            return iNumBytes;
        } catch (IllegalBlockSizeException | BadPaddingException | ShortBufferException e) {
            throw new JDataException(ExceptionClass.CRYPTO, ERROR_UPDATE, e);
        }
    }
}
