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

import java.io.IOException;
import java.io.InputStream;

import net.sourceforge.JDataWalker.ModelException;
import net.sourceforge.JDataWalker.ModelException.ExceptionClass;
import net.sourceforge.JGordianKnot.SymmetricKey;

/**
 * Provide an decryptInputStream wrapper. This class simply wraps an input buffer and processes it as a Zip
 * file. It will read control information from the HEADER zip entry and will use this information to decrypts
 * the data from the DATA Zip Entry
 */
public class DecryptionInputStream extends InputStream {
    /**
     * Buffer size for transfers
     */
    protected final static int BUFSIZE = 1024;

    /**
     * The underlying input stream
     */
    private InputStream theStream = null;

    /**
     * has this stream been closed
     */
    private boolean isClosed = false;

    /**
     * The Stream Cipher
     */
    private StreamCipher theCipher = null;

    /**
     * A buffer for single byte reads
     */
    private byte[] theByte = new byte[1];

    /**
     * The buffer used for reading from input stream
     */
    private byte[] theBuffer = new byte[StreamCipher.BUFSIZE];

    /**
     * The holding buffer for data that has been decrypted but not read
     */
    private decryptBuffer theDecrypted = new decryptBuffer();

    /**
     * Construct the decryption input stream
     * @param pKey the symmetric key
     * @param pInitVector the initialisation vector
     * @param pStream the stream to decrypt from
     * @throws ModelException
     */
    public DecryptionInputStream(SymmetricKey pKey,
                                 byte[] pInitVector,
                                 InputStream pStream) throws ModelException {
        /* Protect from exceptions */
        try {
            /* record the input stream */
            theStream = pStream;

            /* Initialise the decryption */
            theCipher = pKey.initDecryptionStream(pInitVector);
        }

        /* Catch exceptions */
        catch (ModelException e) {
            throw new ModelException(ExceptionClass.CRYPTO, "Exception deciphering secret key", e);
        }
    }

    @Override
    public void close() throws IOException {
        /* Null operation if we are already closed */
        if (!isClosed) {
            /* Close the input stream */
            theStream.close();
            isClosed = true;

            /* release buffers */
            theByte = null;
            theDecrypted = null;
            theBuffer = null;
        }
    }

    @Override
    public long skip(long pNumToSkip) throws IOException {
        long iNumToSkip = pNumToSkip;
        long iNumSkipped = 0;
        int iNumToRead;
        int iNumRead;
        byte[] myBuffer = new byte[BUFSIZE];

        /* If we are already closed throw IO Exception */
        if (isClosed)
            throw new IOException("Stream is closed");

        /* while we have data left to skip */
        while (iNumToSkip > 0) {
            /* Determine size of next read */
            iNumToRead = BUFSIZE;
            if (iNumToRead > iNumToSkip)
                iNumToRead = (int) iNumToSkip;

            /* Read the next set of data */
            iNumRead = read(myBuffer, 0, iNumToRead);

            /* Break loop on EOF */
            if (iNumRead < 0)
                break;

            /* Adjust count */
            iNumToSkip -= iNumRead;
            iNumSkipped += iNumRead;
        }

        return iNumSkipped;
    }

    @Override
    public int available() throws IOException {
        /* If we are already closed throw IO Exception */
        if (isClosed)
            throw new IOException("Stream is closed");

        /* Determine the number of bytes available */
        return theDecrypted.available();
    }

    @Override
    public boolean markSupported() {
        /* return false */
        return false;
    }

    @Override
    public void mark(int readLimit) {
        /* Just ignore */
        return;
    }

    @Override
    public void reset() throws IOException {
        /* If we are already closed throw IO Exception */
        if (isClosed)
            throw new IOException("Stream is closed");

        /* Set the mark */
        throw new IOException("Mark not supported");
    }

    @Override
    public int read(byte[] pBuffer,
                    int pOffset,
                    int pLength) throws IOException {
        int iNumRead;

        /* If we are already closed throw IO Exception */
        if (isClosed)
            throw new IOException("Stream is closed");

        /* Protect against exceptions */
        try {
            /* If there is no data in the decrypt buffer */
            if (theDecrypted.available() == 0) {
                /* If we have already exhausted the source return now */
                if (theDecrypted.hasEOFbeenSeen)
                    return -1;

                /* Read more data from the input stream */
                iNumRead = theStream.read(theBuffer, 0, BUFSIZE);

                /* If we read no data just return details */
                if (iNumRead == 0)
                    return iNumRead;

                /* Decrypt and store the decrypted bytes into the decrypt buffer */
                theDecrypted.storeBytes(theBuffer, iNumRead);
            }

            /* Read from the decrypted buffer */
            iNumRead = theDecrypted.readBytes(pBuffer, pOffset, pLength);
        }

        /* Catch exceptions */
        catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }

        /* Return the amount of data read */
        return iNumRead;
    }

    @Override
    public int read(byte[] pBytes) throws IOException {
        /* Read the bytes from the stream */
        return read(pBytes, 0, pBytes.length);
    }

    @Override
    public int read() throws IOException {
        int iNumRead;

        /* Loop until we get a byte or EOF */
        while ((iNumRead = read(theByte, 0, 1)) == 0)
            ;

        /* Convert the byte read into an integer */
        if (iNumRead > 0)
            iNumRead = (theByte[0] & 0xff);

        /* Return to the caller */
        return iNumRead;
    }

    /**
     * Buffer to hold the decrypted data prior to returning to returning to caller
     */
    private class decryptBuffer {
        /**
         * The buffer itself
         */
        private byte[] theStore = null;

        /**
         * The length of data in the buffer
         */
        private int theDataLen = 0;

        /**
         * The read offset of data in the buffer
         */
        private int theReadOffset = 0;

        /**
         * have we seen EOF
         */
        private boolean hasEOFbeenSeen = false;

        /**
         * Determine the amount of data in the buffer
         * @return the number of data bytes in the buffer
         */
        public int available() {
            return theDataLen - theReadOffset;
        }

        /**
         * Read a number of bytes out from the buffer
         * @param pBuffer the buffer to read bytes into
         * @param pOffset the offset from which to start reading
         * @param pLength the maximum length of data to read
         * @return the actual length of data read or -1 if EOF
         */
        public int readBytes(byte[] pBuffer,
                             int pOffset,
                             int pLength) {
            /* Determine how much data we have available */
            int iNumRead = theDataLen - theReadOffset;

            /* Determine how much data we can transfer */
            iNumRead = (iNumRead <= pLength) ? iNumRead : pLength;

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
            }

            /* else if we have no data check for EOF and report it if required */
            else if (hasEOFbeenSeen)
                iNumRead = -1;

            /* Return the number of bytes transferred */
            return iNumRead;
        }

        /**
         * Decrypt bytes into the buffer and update the message digests
         * @param pBuffer the buffer from which to store bytes
         * @param pLength the number of bytes read into the buffer (must not be zero)
         * @throws ModelException
         */
        public void storeBytes(byte[] pBuffer,
                               int pLength) throws ModelException {
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
            }

            /* else we have EOF */
            else if (hasEOFbeenSeen) {
                /* Finish the cipher operation to pick up remaining bytes */
                iNumBytes = theCipher.finish();
            }

            /* Set up holding variables */
            theStore = theCipher.getBuffer();
            theDataLen = iNumBytes;
            theReadOffset = 0;

            /* Return to caller */
            return;
        }
    }
}
