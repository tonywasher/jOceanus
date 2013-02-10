/*******************************************************************************
 * jGordianKnot: Security Suite
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
package net.sourceforge.jOceanus.jGordianKnot.ZipFile;

import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jGordianKnot.MsgDigest;

/**
 * Provides a digest OutputStream. This class simply calculates a digest of the data in the stream at this point and passes the data onto the next Output Stream
 * in the chain.
 */
public class DigestOutputStream
        extends OutputStream {
    /**
     * The underlying output stream.
     */
    private final OutputStream theStream;

    /**
     * has this stream been closed.
     */
    private boolean isClosed = false;

    /**
     * The Message Digest of the data written.
     */
    private final MsgDigest theDigest;

    /**
     * Access the length of the data written.
     * @return the length of data written
     */
    public long getDataLen() {
        return theDigest.getDataLength();
    }

    /**
     * Access the digest of the data written.
     * @return the digest of data written
     */
    public byte[] getDigest() {
        return theDigest.buildExternal();
    }

    /**
     * Construct the output stream.
     * @param pDigest the message digest
     * @param pStream the stream to write encrypted data to
     * @throws JDataException on error
     */
    public DigestOutputStream(final MsgDigest pDigest,
                              final OutputStream pStream) throws JDataException {
        /* Store the message digest */
        theDigest = pDigest;

        /* Store the stream */
        theStream = pStream;
    }

    @Override
    public void close() throws IOException {
        /* Null operation if we are already closed */
        if (!isClosed) {
            /* Flush the output stream */
            theStream.flush();

            /* Close the output stream */
            theStream.close();
            isClosed = true;
        }
    }

    @Override
    public void flush() throws IOException {
        /* If we are already closed throw IO Exception */
        if (!isClosed) {
            /* Flush the output stream */
            theStream.flush();
        }
    }

    @Override
    public void write(final byte[] pBytes,
                      final int pOffset,
                      final int pLength) throws IOException {
        /* If we are already closed throw IO Exception */
        if (isClosed) {
            throw new IOException("Stream is closed");
        }

        /* Update the data digest */
        theDigest.update(pBytes, pOffset, pLength);

        /* Write the bytes to the stream */
        theStream.write(pBytes, pOffset, pLength);
    }

    @Override
    public void write(final byte[] pBytes) throws IOException {
        /* Write the bytes to the stream */
        write(pBytes, 0, pBytes.length);
    }

    @Override
    public void write(final int pByte) throws IOException {
        /* If we are already closed throw IO Exception */
        if (isClosed) {
            throw new IOException("Stream is closed");
        }

        /* Update the data digest */
        theDigest.update((byte) pByte);

        /* Write the byte to the stream */
        theStream.write(pByte);
    }
}
