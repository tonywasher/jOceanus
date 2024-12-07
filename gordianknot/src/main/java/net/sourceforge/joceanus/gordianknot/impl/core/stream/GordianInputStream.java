/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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

import java.io.IOException;
import java.io.InputStream;

import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.format.OceanusDataConverter;

/**
 * Input stream base implementation.
 */
abstract class GordianInputStream
        extends InputStream {
    /**
     * Closed stream failure.
     */
    static final String ERROR_CLOSED = "Stream is closed";

    /**
     * Buffer size for transfers.
     */
    static final int BUFSIZE = 1024;

    /**
     * Byte mask.
     */
    static final int BYTE_MASK = OceanusDataConverter.BYTE_MASK;

    /**
     * The underlying input stream.
     */
    private final InputStream theStream;

    /**
     * The holding buffer for data that has been processed but not read.
     */
    private GordianProcessedBuffer theProcessed;

    /**
     * The buffer used for reading from input stream.
     */
    private final byte[] theBuffer = new byte[BUFSIZE];

    /**
     * The single byte buffer.
     */
    private final byte[] theByteBuffer = new byte[1];

    /**
     * has this stream been closed.
     */
    private boolean isClosed;

    /**
     * Constructor.
     * @param pInput the underlying input stream
     */
    GordianInputStream(final InputStream pInput) {
        theStream = pInput;
    }

    /**
     * Set the processed buffer.
     * @param pProcessed the processed buffer
     */
    void setProcessedBuffer(final GordianProcessedBuffer pProcessed) {
        theProcessed = pProcessed;
    }

    @Override
    public void close() throws IOException {
        /* Null operation if we are already closed */
        if (!isClosed) {
            /* Close the input stream */
            theStream.close();
            isClosed = true;

            /* Clear the buffers */
            Arrays.fill(theBuffer, (byte) 0);
            theProcessed.setBuffer(null, 0);
        }
    }

    @Override
    public long skip(final long pNumToSkip) throws IOException {
        /* If we are already closed throw IO Exception */
        if (isClosed) {
            throw new IOException(ERROR_CLOSED);
        }

        /* Loop while skipping bytes */
        long myNumToSkip = pNumToSkip;
        long myTotalSkipped = 0;
        while (myNumToSkip > 0) {
            /* Skip processed bytes */
            final long myNumSkipped = theProcessed.skipBytes(myNumToSkip);

            /* Adjust counts */
            myNumToSkip -= myNumSkipped;
            myTotalSkipped += myNumSkipped;

            /* If we need further bytes, bring more data in and handle EOF */
            if (myNumToSkip > 0
                    && processMoreData() == -1) {
                return -1;
            }
        }

        /* Return the number of bytes skipped */
        return myTotalSkipped;
    }

    @Override
    public int available() throws IOException {
        /* If we are already closed throw IO Exception */
        if (isClosed) {
            throw new IOException(ERROR_CLOSED);
        }

        /* Determine the number of bytes available */
        return theProcessed.availableInBuffer()
                + theStream.available();
    }

    @Override
    public boolean markSupported() {
        /* return not supported */
        return false;
    }

    @Override
    public synchronized void mark(final int pReadLimit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void reset() throws IOException {
        /* If we are already closed then throw IO Exception */
        if (isClosed) {
            throw new IOException(ERROR_CLOSED);
        }

        /* Not supported */
        throw new IOException("Mark is not supported");
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
            iNumBytesRead = read(theByteBuffer, 0, 1);
        } while (iNumBytesRead == 0);

        /* Convert the byte that has been read into an integer */
        if (iNumBytesRead > 0) {
            iNumBytesRead = theByteBuffer[0] & BYTE_MASK;
        }

        /* Return to the caller */
        return iNumBytesRead;
    }

    @Override
    public int read(final byte[] pBuffer,
                    final int pOffset,
                    final int pLength) throws IOException {
        /* If we are already closed throw IO Exception */
        if (isClosed) {
            throw new IOException(ERROR_CLOSED);
        }

        /* If there is no data in the processed buffer, bring more data in and handle EOF */
        if (theProcessed.availableInBuffer() == 0
                && processMoreData() == -1) {
            return -1;
        }

        /* Read from the processed buffer and Return the amount of data read */
        return theProcessed.readBytes(pBuffer, pOffset, pLength);
    }

    /**
     * Process more data from the input stream.
     * @return number of bytes read or -1 if EOF
     * @throws IOException on error
     */
    private int processMoreData() throws IOException {
        /* Protect against exceptions */
        try {
            /* If we have already exhausted the source return now */
            if (theProcessed.hasEOFbeenSeen()) {
                return -1;
            }

            /* Read more data from the input stream looping until bytes are available */
            int iNumRead;
            do {
                /* Read more data from the input stream */
                iNumRead = theStream.read(theBuffer, 0, BUFSIZE);

                /* Process any data read */
                if (iNumRead != 0) {
                    /* Process the bytes into the buffer */
                    iNumRead = theProcessed.processBytes(theBuffer, iNumRead);
                }
            } while (iNumRead == 0);

            /* Return number of bytes available */
            return iNumRead;

            /* Catch exceptions */
        } catch (OceanusException e) {
            throw new IOException(e);
        }
    }

    /**
     * Buffer to hold the processed data prior to returning it to the caller.
     */
    protected abstract static class GordianProcessedBuffer {
        /**
         * The buffer itself.
         */
        private byte[] theStore;

        /**
         * The length of data in the buffer.
         */
        private int theDataLen;

        /**
         * The read offset of data in the buffer.
         */
        private int theReadOffset;

        /**
         * have we seen EOF.
         */
        private boolean hasEOFbeenSeen;

        /**
         * Determine the amount of data in the buffer.
         * @return the number of data bytes in the buffer
         */
        int availableInBuffer() {
            return theDataLen
                    - theReadOffset;
        }

        /**
         * Has EOF been seen.
         * @return true/false
         */
        boolean hasEOFbeenSeen() {
            return hasEOFbeenSeen;
        }

        /**
         * Set EOF seen marker.
         */
        void setEOFSeen() {
            hasEOFbeenSeen = true;
        }

        /**
         * Set the new buffer.
         * @param pBuffer the buffer
         * @param pDataLength the dataLength
         */
        void setBuffer(final byte[] pBuffer,
                       final int pDataLength) {
            /* Set new data */
            theStore = pBuffer;
            theDataLen = pDataLength;
            theReadOffset = 0;
        }

        /**
         * Skip bytes.
         * @param pBytesToSkip the number of bytes to skip
         * @return the number of bytes skipped
         */
        long skipBytes(final long pBytesToSkip) {
            /* Determine number of bytes that we can skip */
            final int myAvailable = availableInBuffer();

            /* If we must skip all bytes */
            if (pBytesToSkip >= myAvailable) {
                /* Reset the values */
                theDataLen = 0;
                theReadOffset = 0;
                return myAvailable;

                /* else we only need to skip some bytes */
            } else {
                theReadOffset += pBytesToSkip;
                return pBytesToSkip;
            }
        }

        /**
         * Read a number of bytes out from the buffer.
         * @param pBuffer the buffer to read bytes into
         * @param pOffset the offset at which to start reading bytes
         * @param pLength the maximum length of data to read
         * @return the actual length of data read or -1 if EOF
         */
        int readBytes(final byte[] pBuffer,
                      final int pOffset,
                      final int pLength) {
            /* Determine how much data we have available */
            int iNumRead = theDataLen
                    - theReadOffset;

            /* Determine how much data we can transfer */
            iNumRead = iNumRead <= pLength
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
         * Process bytes into the buffer.
         * @param pBuffer the buffer from which to store bytes
         * @param pLength the number of bytes read into the buffer (must not be zero)
         * @return the number of bytes now available in the buffer
         * @throws OceanusException on error
         */
        protected abstract int processBytes(byte[] pBuffer,
                                            int pLength) throws OceanusException;
    }
}
