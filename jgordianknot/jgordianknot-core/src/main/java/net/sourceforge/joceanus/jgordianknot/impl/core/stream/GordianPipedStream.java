/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.stream;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;

import org.bouncycastle.util.Arrays;

/**
 * Class to provide a pipe enabling data to be passed between threads via writing to an output
 * stream and reading from an input stream.
 */
class GordianPipedStream {
    /**
     * The Queue Capacity.
     */
    private static final int QUEUE_LEN = 4096;

    /**
     * The output buffer size.
     */
    private static final int BUFFER_LEN = 16384;

    /**
     * The Array Blocking queue that implements the pipe.
     */
    private final ArrayBlockingQueue<byte[]> theQueue;

    /**
     * The Input Stream.
     */
    private final GordianPipedInputStream theSource;

    /**
     * The Output Stream.
     */
    private final BufferedOutputStream theSink;

    /**
     * Constructor.
     */
    GordianPipedStream() {
        /* Create the queue */
        theQueue = new ArrayBlockingQueue<>(QUEUE_LEN);

        /* Create the source stream */
        theSource = new GordianPipedInputStream();

        /* Create the sink stream */
        theSink = new BufferedOutputStream(new GordianPipedOutputStream(), BUFFER_LEN);
    }

    /**
     * Obtain the source stream.
     * @return the source stream
     */
    public InputStream getSource() {
        return theSource;
    }

    /**
     * Obtain the sink stream.
     * @return the sink stream
     */
    OutputStream getSink() {
        return theSink;
    }

    /**
     * The inputStream class.
     */
    private class GordianPipedInputStream
            extends InputStream {
        /**
         * The currently active element.
         */
        private byte[] theElement;

        /**
         * The length of the current element.
         */
        private int theDataLen = -1;

        /**
         * The offset within the element.
         */
        private int theReadOffset = -1;

        /**
         * has this stream been closed.
         */
        private boolean isClosed;

        /**
         * have we seen EOF.
         */
        private boolean hasEOFbeenSeen;

        /**
         * A buffer for single byte reads.
         */
        private final byte[] theByte = new byte[1];

        @Override
        public int read(final byte[] pBytes) throws IOException {
            /* Read the bytes from the stream */
            return read(pBytes, 0, pBytes.length);
        }

        @Override
        public int read() throws IOException {
            int iNumRead;

            /* Loop until we get a byte or EOF */
            do {
                iNumRead = read(theByte, 0, 1);
            } while (iNumRead == 0);

            /* Convert the byte read into an integer */
            if (iNumRead > 0) {
                iNumRead = theByte[0] & GordianInputStream.BYTE_MASK;
            }

            /* Return to the caller */
            return iNumRead;
        }

        @Override
        public int read(final byte[] pBuffer,
                        final int pOffset,
                        final int pLength) throws IOException {
            /* If we are already closed throw IO Exception */
            if (isClosed) {
                throw new IOException(GordianInputStream.ERROR_CLOSED);
            }

            /* If we have already seen EOF return now */
            if (hasEOFbeenSeen) {
                return -1;
            }

            /* If we have no current element */
            if (theElement == null) {
                /* Obtain the next element from the queue */
                try {
                    theElement = theQueue.take();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException(e.getMessage(), e);
                }

                /* Set variables */
                theDataLen = theElement.length;
                theReadOffset = 0;

                /* If the dataLen is zero, it is EOF */
                if (theDataLen == 0) {
                    hasEOFbeenSeen = true;
                    return -1;
                }
            }

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
                System.arraycopy(theElement, theReadOffset, pBuffer, pOffset, iNumRead);

                /* Adjust ReadOffset */
                theReadOffset += iNumRead;

                /* If we have finished with the data in the element */
                if (theReadOffset >= theDataLen) {
                    /* Reset the values */
                    Arrays.fill(theElement, (byte) 0);
                    theElement = null;
                    theDataLen = 0;
                    theReadOffset = 0;
                }
            }

            /* Return the number of bytes read */
            return iNumRead;
        }

        @Override
        public void close() throws IOException {
            /* Note that we have closed the stream */
            isClosed = true;

            /* Clear any data buffer */
            if (theElement != null) {
                Arrays.fill(theElement, (byte) 0);
                theElement = null;
            }
        }
    }

    /**
     * The outputStream class.
     */
    private class GordianPipedOutputStream
            extends OutputStream {
        /**
         * has this stream been closed.
         */
        private boolean isClosed;

        /**
         * A buffer for single byte writes.
         */
        private final byte[] theByte = new byte[1];

        @Override
        public void write(final byte[] pBytes,
                          final int pOffset,
                          final int pLength) throws IOException {
            /* If we are already closed throw IO Exception */
            if (isClosed) {
                throw new IOException(GordianInputStream.ERROR_CLOSED);
            }

            /* no data is to be written, just ignore */
            if (pLength == 0) {
                return;
            }

            /* Create a copy of the data */
            final byte[] myBuffer = new byte[pLength];
            System.arraycopy(pBytes, pOffset, myBuffer, 0, pLength);

            /* Write the element to the queue */
            try {
                theQueue.put(myBuffer);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException(e.getMessage(), e);
            }
        }

        @Override
        public void write(final byte[] pBytes) throws IOException {
            /* Write the bytes to the stream */
            write(pBytes, 0, pBytes.length);
        }

        @Override
        public void write(final int pByte) throws IOException {
            /* Store the byte */
            theByte[0] = (byte) pByte;

            /* Write the byte to the stream */
            write(theByte, 0, 1);
        }

        @Override
        public void flush() throws IOException {
            /* No need to flush */
        }

        @Override
        public void close() throws IOException {
            /* Ignore if already closed */
            if (isClosed) {
                return;
            }

            /* Write a zero length element */
            try {
                theQueue.put(new byte[0]);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException(e);
            }

            /* Note that we have closed */
            isClosed = true;
        }
    }
}
