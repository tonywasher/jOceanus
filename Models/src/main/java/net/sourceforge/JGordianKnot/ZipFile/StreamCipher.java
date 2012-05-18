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
package net.sourceforge.JGordianKnot.ZipFile;

import javax.crypto.Cipher;

import net.sourceforge.JDataWalker.ModelException;
import net.sourceforge.JDataWalker.ModelException.ExceptionClass;

public class StreamCipher {
    /**
     * Buffer size for transfers
     */
    protected final static int BUFSIZE = 1024;

    /**
     * The cipher
     */
    private Cipher theCipher = null;

    /**
     * The transfer buffer
     */
    private byte[] theBuffer = null;

    /**
     * The initialisation vector
     */
    private byte[] theInitVector = null;

    /**
     * Obtain the output buffer
     * @return the output buffer
     */
    public byte[] getBuffer() {
        return theBuffer;
    }

    /**
     * Constructor
     * @param pCipher the cipher
     * @param pVector the initialisation vector
     */
    public StreamCipher(Cipher pCipher,
                        byte[] pVector) {
        theCipher = pCipher;
        theInitVector = pVector;
        theBuffer = new byte[BUFSIZE];
    }

    /**
     * Get Initialisation vector
     * @return the initialisation vector
     */
    public byte[] getInitVector() {
        return theInitVector;
    }

    /**
     * Update Cipher
     * @param pBytes Bytes to update cipher with
     * @param pOffset offset within pBytes to read bytes from
     * @param pLength length of data to update with
     * @return number of bytes transferred to output buffer
     * @throws ModelException
     */
    public int update(byte[] pBytes,
                      int pOffset,
                      int pLength) throws ModelException {
        /* Protect against exceptions */
        try {
            /* Check how long a buffer we need */
            int iNumBytes = theCipher.getOutputSize(pLength);

            /* Extend the buffer if required */
            if (iNumBytes > theBuffer.length)
                theBuffer = new byte[iNumBytes];

            /* Update the data */
            iNumBytes = theCipher.update(pBytes, pOffset, pLength, theBuffer);

            /* Return to caller */
            return iNumBytes;
        } catch (Exception e) {
            throw new ModelException(ExceptionClass.CRYPTO, "Failed to update cipher", e);
        }
    }

    /**
     * Finish Cipher encrypting/decrypting any data buffered within the cipher
     * @return number of bytes transferred to output buffer
     * @throws ModelException
     */
    public int finish() throws ModelException {
        /* Protect against exceptions */
        try {
            /* Check how long a buffer we need to handle buffered data */
            int iNumBytes = theCipher.getOutputSize(0);

            /* Extend the buffer if required */
            if (iNumBytes > theBuffer.length)
                theBuffer = new byte[iNumBytes];

            /* Update the data */
            iNumBytes = theCipher.doFinal(theBuffer, 0);
            /* Return to caller */
            return iNumBytes;
        } catch (Exception e) {
            throw new ModelException(ExceptionClass.CRYPTO, "Failed to finish cipher operation", e);
        }
    }
}
