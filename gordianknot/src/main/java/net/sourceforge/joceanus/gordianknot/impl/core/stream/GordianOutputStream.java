/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
import java.io.OutputStream;

/**
 * Output stream base implementation.
 */
abstract class GordianOutputStream
        extends OutputStream {
    /**
     * Closed stream failure.
     */
    static final String ERROR_CLOSED = GordianInputStream.ERROR_CLOSED;

    /**
     * The underlying output stream.
     */
    private final OutputStream theStream;

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
     * @param pOutput the underlying output stream
     */
    GordianOutputStream(final OutputStream pOutput) {
        theStream = pOutput;
    }

    /**
     * Obtain the next stream.
     * @return the stream
     */
    OutputStream getNextStream() {
        return theStream;
    }

    @Override
    public void close() throws IOException {
        /* Null operation if we are already closed */
        if (!isClosed) {
            /* Flush the output stream */
            theStream.flush();

            /* Finish processing the data */
            finishData();

            /* Close the output stream */
            theStream.close();
            isClosed = true;
        }
    }

    @Override
    public void flush() throws IOException {
        /* Null operation if we are already closed */
        if (!isClosed) {
            /* Flush the output stream */
            theStream.flush();
        }
    }

    @Override
    public void write(final byte[] pBytes) throws IOException {
        /* Write the bytes to the stream */
        write(pBytes, 0, pBytes.length);
    }

    @Override
    public void write(final int pByte) throws IOException {
        /* Store into byte buffer and write */
        theByteBuffer[0] = (byte) pByte;
        write(theByteBuffer, 0, 1);
    }

    @Override
    public void write(final byte[] pBytes,
                      final int pOffset,
                      final int pLength) throws IOException {
        /* If we are already closed throw IO Exception */
        if (isClosed) {
            throw new IOException(ERROR_CLOSED);
        }

        /* Process the data */
        processData(pBytes, pOffset, pLength);
    }

    /**
     * Process the data.
     * @param pBytes the bytes to process.
     * @param pOffset the offset of the data within the byte array
     * @param pLength the length of the data to use
     * @throws IOException on error
     */
    void writeToStream(final byte[] pBytes,
                       final int pOffset,
                       final int pLength) throws IOException {
        /* Write the bytes to the stream */
        theStream.write(pBytes, pOffset, pLength);
    }

    /**
     * Process the data.
     * @param pBytes the bytes to process.
     * @param pOffset the offset of the data within the byte array
     * @param pLength the length of the data to use
     * @throws IOException on error
     */
    protected abstract void processData(byte[] pBytes,
                                        int pOffset,
                                        int pLength) throws IOException;

    /**
     * Complete processing of the data.
     * @throws IOException on error
     */
    protected abstract void finishData() throws IOException;
}
