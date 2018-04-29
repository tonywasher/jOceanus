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
package net.sourceforge.joceanus.jgordianknot.crypto.stream;

import java.io.InputStream;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipher;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Input stream Cipher implementation.
 * @param <T> the key type
 */
public class GordianCipherInputStream<T>
        extends GordianInputStream {
    /**
     * Constructor.
     * @param pCipher the decryption cipher
     * @param pInput the underlying input stream
     */
    protected GordianCipherInputStream(final GordianCipher<T> pCipher,
                                       final InputStream pInput) {
        super(pInput);
        setProcessedBuffer(new CipherBuffer<T>(pCipher));
    }

    /**
     * Buffer to hold the processed data prior to returning it to the caller.
     * @param <T> the key type
     */
    private static final class CipherBuffer<T>
            extends ProcessedBuffer {
        /**
         * The cipher.
         */
        private final GordianCipher<T> theCipher;

        /**
         * The buffer.
         */
        private byte[] theBuffer = new byte[BUFSIZE];

        /**
         * Constructor.
         * @param pCipher the decryption cipher
         */
        CipherBuffer(final GordianCipher<T> pCipher) {
            theCipher = pCipher;
        }

        /**
         * Check buffer length.
         * @param pLength the length of the data to use
         * @return the number of bytes to process
         */
        private int checkBufferLength(final int pLength) {
            /* Check how long a buffer we need to handle the data */
            final int iNumBytes = theCipher.getOutputLength(pLength);

            /* Extend the buffer if required */
            if (iNumBytes > theBuffer.length) {
                theBuffer = new byte[iNumBytes];
            }

            /* return number of bytes to process */
            return iNumBytes;
        }

        @Override
        public int processBytes(final byte[] pBuffer,
                                final int pLength) throws OceanusException {
            /* Initialise variables */
            int iNumBytes;
            final int iLength = pLength;

            /* If we have EOF from the input stream */
            if (iLength == -1) {
                /* If we have already seen EOF */
                if (hasEOFbeenSeen()) {
                    /* signal true EOF */
                    return -1;
                }

                /* Record EOF */
                setEOFSeen();

                /* Make sure the buffer is large enough */
                iNumBytes = checkBufferLength(0);

                /* Finish the decryption if there is work to do */
                if (iNumBytes > 0) {
                    iNumBytes = theCipher.finish(theBuffer, 0);
                }

                /* else we have bytes to process */
            } else {
                /* Make sure the buffer is large enough */
                checkBufferLength(iLength);

                /* Decrypt the data */
                iNumBytes = theCipher.update(pBuffer, 0, pLength, theBuffer, 0);
            }

            /* Set up buffer variables */
            setBuffer(theBuffer, iNumBytes);
            return iNumBytes;
        }
    }
}
