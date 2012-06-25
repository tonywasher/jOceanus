/*******************************************************************************
 * JGordianKnot: Security Suite
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
package net.sourceforge.JGordianKnot.ZipFile;

import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;

/**
 * Wrapper class for a stream cipher.
 * @author Tony Washer
 */
public class StreamCipher {
    /**
     * Buffer size for transfers.
     */
    public static final int BUFSIZE = 1024;

    /**
     * The cipher.
     */
    private final Cipher theCipher;

    /**
     * The transfer buffer.
     */
    private byte[] theBuffer = null;

    /**
     * The initialisation vector.
     */
    private final byte[] theInitVector;

    /**
     * Obtain the output buffer.
     * @return the output buffer
     */
    protected byte[] getBuffer() {
        return theBuffer;
    }

    /**
     * Get Initialisation vector.
     * @return the initialisation vector
     */
    protected byte[] getInitVector() {
        return theInitVector;
    }

    /**
     * Constructor.
     * @param pCipher the cipher
     * @param pVector the initialisation vector
     */
    public StreamCipher(final Cipher pCipher,
                        final byte[] pVector) {
        theCipher = pCipher;
        theInitVector = Arrays.copyOf(pVector, pVector.length);
        theBuffer = new byte[BUFSIZE];
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
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to update cipher", e);
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
        } catch (IllegalBlockSizeException e) {
            throw new JDataException(ExceptionClass.CRYPTO, e.getMessage(), e);
        } catch (BadPaddingException e) {
            throw new JDataException(ExceptionClass.CRYPTO, e.getMessage(), e);
        } catch (ShortBufferException e) {
            throw new JDataException(ExceptionClass.CRYPTO, e.getMessage(), e);
        }
    }
}
