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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JGordianKnot.StreamCipher;
import net.sourceforge.JGordianKnot.SymmetricKey;

/**
 * Provide an encrypt OutputStream wrapper. This class simply wraps an output buffer and encrypts the data
 * before passing it on.
 */
public class EncryptionOutputStream extends OutputStream {
    /**
     * The underlying output stream.
     */
    private final OutputStream theStream;

    /**
     * has this stream been closed.
     */
    private boolean isClosed = false;

    /**
     * The Stream Cipher.
     */
    private final StreamCipher theCipher;

    /**
     * The Symmetric Key.
     */
    private final SymmetricKey theSymKey;

    /**
     * The initialisation vector.
     */
    private final byte[] theInitVector;

    /**
     * A buffer for single byte writes.
     */
    private final byte[] theByte;

    /**
     * Access the initialisation vector.
     * @return the initialisation vector
     */
    public byte[] getInitVector() {
        return Arrays.copyOf(theInitVector, theInitVector.length);
    }

    /**
     * Access the key.
     * @return the symmetric key
     */
    public SymmetricKey getSymmetricKey() {
        return theSymKey;
    }

    /**
     * Construct a symmetric key encryption output stream.
     * @param pKey the symmetric key to use
     * @param pStream the stream to encrypt to
     * @throws JDataException on error
     */
    public EncryptionOutputStream(final SymmetricKey pKey,
                                  final OutputStream pStream) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Create the byte buffer */
            theByte = new byte[1];

            /* record the output stream */
            theStream = pStream;

            /* Initialise the cipher */
            theSymKey = pKey;
            theCipher = theSymKey.initEncryptionStream();

            /* Access the initialisation vector */
            theInitVector = theCipher.getInitVector();

            /* Catch exceptions */
        } catch (Exception e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Exception creating encryption output stream", e);
        }
    }

    @Override
    public void close() throws IOException {
        /* Protect against exceptions */
        try {
            /* Null operation if we are already closed */
            if (!isClosed) {
                /* Finish the cipher operation */
                int iNumBytes = theCipher.finish();
                byte[] myBytes = theCipher.getBuffer();

                /* If we have data to write then write it */
                if (iNumBytes > 0) {
                    theStream.write(myBytes, 0, iNumBytes);
                }

                /* Close the output stream */
                theStream.close();
                isClosed = true;
            }

            /* Catch exceptions */
        } catch (JDataException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void flush() throws IOException {
        /* If we are already closed throw IO Exception */
        if (isClosed) {
            throw new IOException("Stream is closed");
        }

        /* Flush the output stream */
        theStream.flush();
    }

    @Override
    public void write(final byte[] pBytes,
                      final int pOffset,
                      final int pLength) throws IOException {
        int iNumBytes;
        byte[] myBytes;

        /* If we are already closed throw IO Exception */
        if (isClosed) {
            throw new IOException("Stream is closed");
        }

        /* Ignore a null write */
        if (pLength == 0) {
            return;
        }

        /* Protect against exceptions */
        try {
            /* Update the cipher with these bytes */
            iNumBytes = theCipher.update(pBytes, pOffset, pLength);
            myBytes = theCipher.getBuffer();

            /* Write the bytes to the stream */
            theStream.write(myBytes, 0, iNumBytes);

            /* Catch exceptions */
        } catch (JDataException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void write(final byte[] pBytes) throws IOException {
        /* Write the bytes to the stream */
        write(pBytes, 0, pBytes.length);
    }

    @Override
    public void write(final int pByte) throws IOException {
        /* Copy the byte to the buffer */
        theByte[0] = (byte) pByte;

        /* Write the byte to the stream */
        write(theByte, 0, 1);
    }
}
