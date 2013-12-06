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
package net.sourceforge.joceanus.jgordianknot.zipfile;

import java.io.IOException;
import java.io.InputStream;

import javax.crypto.Cipher;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jgordianknot.SymmetricKey;

/**
 * Provide an decryptInputStream wrapper. This class simply wraps an input buffer and processes it as a Zip file. It will read control information from the
 * HEADER zip entry and will use this information to decrypts the data from the DATA Zip Entry.
 */
public class DecryptionInputStream
        extends InputStream {
    /**
     * Stream closed failure message.
     */
    private static final String MSG_STREAM_CLOSED = "Stream is closed";

    /**
     * Buffer size for transfers.
     */
    protected static final int BUFSIZE = 1024;

    /**
     * Byte mask.
     */
    protected static final int BYTE_MASK = 255;

    /**
     * The underlying input stream.
     */
    private final InputStream theStream;

    /**
     * has this stream been closed.
     */
    private boolean isClosed = false;

    /**
     * The Stream Cipher.
     */
    private final StreamCipher theCipher;

    /**
     * A buffer for single byte reads.
     */
    private final byte[] theByte = new byte[1];

    /**
     * The buffer used for reading from input stream.
     */
    private final byte[] theBuffer = new byte[StreamCipher.BUFSIZE];

    /**
     * The holding buffer for data that has been decrypted but not read.
     */
    private final DecryptBuffer theDecrypted = new DecryptBuffer();

    /**
     * Construct the decryption input stream.
     * @param pKey the symmetric key
     * @param pInitVector the initialisation vector
     * @param pStream the stream to decrypt from
     * @throws JDataException on error
     */
    public DecryptionInputStream(final SymmetricKey pKey,
                                 final byte[] pInitVector,
                                 final InputStream pStream) throws JDataException {
        /* Protect from exceptions */
        try {
            /* record the input stream */
            theStream = pStream;

            /* Initialise the decryption */
            Cipher myCipher = pKey.initDecryptionStream(pInitVector);
            theCipher = new StreamCipher(myCipher, myCipher.getIV());

            /* Catch exceptions */
        } catch (JDataException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Exception deciphering secret key", e);
        }
    }

    @Override
    public void close() throws IOException {
        /* Null operation if we are already closed */
        if (!isClosed) {
            /* Close the input stream */
            theStream.close();
            isClosed = true;
        }
    }

    @Override
    public long skip(final long pNumToSkip) throws IOException {
        long iNumToSkip = pNumToSkip;
        long iNumSkipped = 0;
        int iNumToRead;
        int iNumRead;
        byte[] myBuffer = new byte[BUFSIZE];

        /* If we are already closed throw IO Exception */
        if (isClosed) {
            throw new IOException(MSG_STREAM_CLOSED);
        }

        /* while we have data left to skip */
        while (iNumToSkip > 0) {
            /* Determine size of next read */
            iNumToRead = BUFSIZE;
            if (iNumToRead > iNumToSkip) {
                iNumToRead = (int) iNumToSkip;
            }

            /* Read the next set of data */
            iNumRead = read(myBuffer, 0, iNumToRead);

            /* Break loop on EOF */
            if (iNumRead < 0) {
                break;
            }

            /* Adjust count */
            iNumToSkip -= iNumRead;
            iNumSkipped += iNumRead;
        }

        return iNumSkipped;
    }

    @Override
    public int available() throws IOException {
        /* If we are already closed throw IO Exception */
        if (isClosed) {
            throw new IOException(MSG_STREAM_CLOSED);
        }

        /* Determine the number of bytes available */
        return theDecrypted.available();
    }

    @Override
    public boolean markSupported() {
        /* return not supported */
        return false;
    }

    @Override
    public void mark(final int pReadLimit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() throws IOException {
        /* If we are already closed then throw IO Exception */
        if (isClosed) {
            throw new IOException(MSG_STREAM_CLOSED);
        }

        /* Not supported */
        throw new IOException("Mark is not supported");
    }

    @Override
    public int read(final byte[] pBuffer,
                    final int pOffset,
                    final int pLength) throws IOException {
        int iNumRead;

        /* If we are already closed throw IO Exception */
        if (isClosed) {
            throw new IOException(MSG_STREAM_CLOSED);
        }

        /* Protect against exceptions */
        try {
            /* If there is no data in the decrypt buffer */
            if (theDecrypted.available() == 0) {
                /* If we have already exhausted the source return now */
                if (theDecrypted.hasEOFbeenSeen) {
                    return -1;
                }

                /* Read more data from the input stream */
                iNumRead = theStream.read(theBuffer, 0, BUFSIZE);

                /* If we read no data just return details */
                if (iNumRead == 0) {
                    return iNumRead;
                }

                /* Decrypt and store the decrypted bytes into the decrypt buffer */
                theDecrypted.storeBytes(theBuffer, iNumRead);
            }

            /* Read from the decrypted buffer */
            iNumRead = theDecrypted.readBytes(pBuffer, pOffset, pLength);

            /* Return the amount of data read */
            return iNumRead;

            /* Catch exceptions */
        } catch (JDataException e) {
            throw new IOException(e);
        }
    }

    @Override
    public int read(final byte[] pOutBytes) throws IOException {
        /* Read the next bytes from the stream */
        return read(pOutBytes, 0, pOutBytes.length);
    }

    @Override
    public int read() throws IOException {
        int iNumBytesRead;

        /* Loop until we get a byte or EOF from the stream */
        do {
            iNumBytesRead = read(theByte, 0, 1);
        } while (iNumBytesRead == 0);

        /* Convert the byte that has been read into an integer */
        if (iNumBytesRead > 0) {
            iNumBytesRead = (theByte[0] & BYTE_MASK);
        }

        /* Return to the caller */
        return iNumBytesRead;
    }

    /**
     * Buffer to hold the decrypted data prior to returning to returning to caller.
     */
    private class DecryptBuffer {
        /**
         * The buffer itself.
         */
        private byte[] theStore = null;

        /**
         * The length of data in the buffer.
         */
        private int theDataLen = 0;

        /**
         * The read offset of data in the buffer.
         */
        private int theReadOffset = 0;

        /**
         * have we seen EOF.
         */
        private boolean hasEOFbeenSeen = false;

        /**
         * Determine the amount of data in the buffer.
         * @return the number of data bytes in the buffer
         */
        public int available() {
            return theDataLen
                   - theReadOffset;
        }

        /**
         * Read a number of bytes out from the buffer.
         * @param pBuffer the buffer to read bytes into
         * @param pOffset the offset from which to start reading
         * @param pLength the maximum length of data to read
         * @return the actual length of data read or -1 if EOF
         */
        public int readBytes(final byte[] pBuffer,
                             final int pOffset,
                             final int pLength) {
            /* Determine how much data we have available */
            int iNumRead = theDataLen
                           - theReadOffset;

            /* Determine how much data we can transfer */
            iNumRead = (iNumRead <= pLength)
                    ? iNumRead
                    : pLength;

            /* If we have data to copy */
            if (iNumRead > 0) {
                /* Transfer the bytes */
                System.arraycopy(theStore, theReadOffset, pBuffer, pOffset, iNumRead);

                /* Adjust ReadOffset */
                theReadOffset += iNumRead;

                /* If we have finished with the data in the buffer */
                if (theReadOffset >= theDataLen) {
                    /* Reset the values */
                    theDataLen = 0;
                    theReadOffset = 0;
                }

                /* else if we have no data check for EOF and report it if required */
            } else if (hasEOFbeenSeen) {
                iNumRead = -1;
            }

            /* Return the number of bytes transferred */
            return iNumRead;
        }

        /**
         * Decrypt bytes into the buffer and update the message digests.
         * @param pBuffer the buffer from which to store bytes
         * @param pLength the number of bytes read into the buffer (must not be zero)
         * @throws JDataException on error
         */
        public void storeBytes(final byte[] pBuffer,
                               final int pLength) throws JDataException {
            int iNumBytes = 0;
            int iLength = pLength;

            /* If we have EOF from the input stream */
            if (iLength == -1) {
                /* Record the fact and reset the read length to zero */
                hasEOFbeenSeen = true;
                iLength = 0;
            }

            /* If we have data that we read from the input stream */
            if (iLength > 0) {
                /* Decrypt the data */
                iNumBytes = theCipher.update(pBuffer, 0, iLength);

                /* else we have EOF */
            } else if (hasEOFbeenSeen) {
                /* Finish the cipher operation to pick up remaining bytes */
                iNumBytes = theCipher.finish();
            }

            /* Set up holding variables */
            theStore = theCipher.getBuffer();
            theDataLen = iNumBytes;
            theReadOffset = 0;
        }
    }
}
