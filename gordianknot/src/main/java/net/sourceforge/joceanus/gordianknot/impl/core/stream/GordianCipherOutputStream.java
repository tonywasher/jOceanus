/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.stream;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianKeyedCipher;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.gordianknot.impl.core.cipher.GordianCoreCipher;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Output stream cipher implementation.
 * @param <T> the key type
 */
class GordianCipherOutputStream<T extends GordianKeySpec>
        extends GordianOutputStream {
    /**
     * Default buffer size.
     */
    private static final int BUFSIZE = 1024;

    /**
     * The cipher.
     */
    private final GordianCoreCipher<T> theCipher;

    /**
     * The result.
     */
    private byte[] theBuffer = new byte[BUFSIZE];

    /**
     * Constructor.
     * @param pCipher the encryption cipher
     * @param pOutput the underlying output stream
     */
    GordianCipherOutputStream(final GordianKeyedCipher<T> pCipher,
                              final OutputStream pOutput) {
        super(pOutput);
        theCipher = (GordianCoreCipher<T>) pCipher;
    }

    /**
     * Obtain the Cipher.
     * @return the cipher
     */
    public GordianCoreCipher<T> getCipher() {
        return theCipher;
    }

    /**
     * Is this stream a SymKey Stream.
     * @return true/false
     */
    boolean isSymKeyStream() {
        return theCipher.getKeyType() instanceof GordianSymKeySpec;
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
    protected void processData(final byte[] pBytes,
                               final int pOffset,
                               final int pLength) throws IOException {
        /* Protect against exceptions */
        try {
            /* Ensure buffer is long enough */
            int iNumBytes = checkBufferLength(pLength);

            /* If we have data to encrypt */
            if (iNumBytes > 0) {
                /* Encrypt the data and write it out */
                iNumBytes = theCipher.update(pBytes, pOffset, pLength, theBuffer, 0);
                writeToStream(theBuffer, 0, iNumBytes);
            }

            /* Catch exceptions */
        } catch (GordianException e) {
            throw new IOException(e);
        }
    }

    @Override
    protected void finishData() throws IOException {
        /* Protect against exceptions */
        try {
            /* Ensure buffer is long enough */
            int iNumBytes = checkBufferLength(0);

            /* If we have data to encrypt */
            if (iNumBytes > 0) {
                /* Finish the data and write it out */
                iNumBytes = theCipher.finish(theBuffer, 0);
                writeToStream(theBuffer, 0, iNumBytes);
            }

            /* Catch exceptions */
        } catch (GordianException e) {
            throw new IOException(theCipher.getKeyType().toString(), e);
        }
    }
}
