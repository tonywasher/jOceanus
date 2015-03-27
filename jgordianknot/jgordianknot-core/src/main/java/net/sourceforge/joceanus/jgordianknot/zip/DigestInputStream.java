/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.zip;

import java.io.IOException;
import java.io.InputStream;

import net.sourceforge.joceanus.jgordianknot.crypto.DataDigest;
import net.sourceforge.joceanus.jgordianknot.crypto.StreamCipher;

import org.bouncycastle.util.Arrays;

/**
 * Provides a digest InputStream. This class simply calculates a digest of the data in the stream at this point as it is read. On close of the file, the digest
 * is validated.
 */
public class DigestInputStream
        extends InputStream {
    /**
     * Buffer size for skip reads.
     */
    private static final int BUFSIZE = 1024;

    /**
     * The underlying input stream.
     */
    private final InputStream theStream;

    /**
     * has this stream been closed.
     */
    private boolean isClosed = false;

    /**
     * The Data Digest of the data read.
     */
    private final DataDigest theDigest;

    /**
     * The length of the data digested.
     */
    private long theDataLen = 0;

    /**
     * The skip buffer.
     */
    private final byte[] theSkipBuffer = new byte[BUFSIZE];

    /**
     * The expected digest value.
     */
    private final byte[] theExpectedDigest;

    /**
     * Has EOF been reached.
     */
    private boolean hasEOFbeenSeen = false;

    /**
     * Construct the input stream.
     * @param pDigest the message digest
     * @param pExpected the expected digest value
     * @param pStream the Stream to read data from
     */
    public DigestInputStream(final DataDigest pDigest,
                             final byte[] pExpected,
                             final InputStream pStream) {
        /* Store the message digest */
        theDigest = pDigest;
        theExpectedDigest = Arrays.copyOf(pExpected, pExpected.length);

        /* Store the stream details */
        theStream = pStream;
    }

    /**
     * Access the data length.
     * @return the data length
     */
    public long getDataLen() {
        return theDataLen;
    }

    @Override
    public void close() throws IOException {
        /* Null operation if we are already closed */
        if (!isClosed) {
            /* Close the input stream */
            theStream.close();
            isClosed = true;

            /* If we have seen EOF */
            if (hasEOFbeenSeen) {
                /* Calculate digest */
                byte[] myDigest = theDigest.finish();

                /* Check valid digest */
                if (!Arrays.areEqual(myDigest, theExpectedDigest)) {
                    throw new IOException("Invalid Digest");
                }
            }
        }
    }

    @Override
    public long skip(final long pNumToSkip) throws IOException {
        /* If we are already closed throw IO Exception */
        if (isClosed) {
            throw new IOException(StreamCipher.ERROR_CLOSED);
        }

        /* while we have data left to skip */
        long iNumToSkip = pNumToSkip;
        long iNumSkipped = 0;
        while (iNumToSkip > 0) {
            /* Determine size of next read */
            int iNumToRead = BUFSIZE;
            if (iNumToRead > iNumToSkip) {
                iNumToRead = (int) iNumToSkip;
            }

            /* Read the next set of data */
            int iNumRead = read(theSkipBuffer, 0, iNumToRead);

            /* Break loop on EOF */
            if (iNumRead < 0) {
                break;
            }

            /* Adjust count */
            iNumToSkip -= iNumRead;
            iNumSkipped += iNumRead;
        }

        /* Return the skip count */
        return iNumSkipped;
    }

    @Override
    public int available() throws IOException {
        /* If we are already closed throw IO Exception */
        if (isClosed) {
            throw new IOException(StreamCipher.ERROR_CLOSED);
        }

        /* Determine the number of bytes available */
        return theStream.available();
    }

    @Override
    public boolean markSupported() {
        /* Always return false */
        return false;
    }

    @Override
    public void mark(final int readLimit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() throws IOException {
        /* If we are already closed throw IO Exception */
        if (isClosed) {
            throw new IOException(StreamCipher.ERROR_CLOSED);
        }

        /* Set the mark */
        throw new IOException("Mark not supported");
    }

    @Override
    public int read(final byte[] pBuffer,
                    final int pOffset,
                    final int pLength) throws IOException {
        /* If we are already closed throw IO Exception */
        if (isClosed) {
            throw new IOException(StreamCipher.ERROR_CLOSED);
        }

        /* Read the bytes from the stream */
        int iNumRead = theStream.read(pBuffer, pOffset, pLength);

        /* If we have read some data */
        if (iNumRead > 0) {
            /* Update the message digest */
            theDigest.update(pBuffer, pOffset, iNumRead);

            /* Adjust the data length */
            theDataLen += iNumRead;
        }

        /* Note if EOF has been seen */
        if (iNumRead == -1) {
            hasEOFbeenSeen = true;
        }

        /* Return the amount of data read */
        return iNumRead;
    }

    @Override
    public int read(final byte[] pBytes) throws IOException {
        /* Read the bytes from the stream */
        return read(pBytes, 0, pBytes.length);
    }

    @Override
    public int read() throws IOException {
        /* If we are already closed throw IO Exception */
        if (isClosed) {
            throw new IOException(StreamCipher.ERROR_CLOSED);
        }

        /* read the next byte */
        int iByte = theStream.read();

        /* If we read data */
        if (iByte > -1) {
            /* Update the message digest */
            theDigest.update((byte) iByte);

            /* Adjust the data length */
            theDataLen++;
        }

        /* Note if EOF has been seen */
        if (iByte == -1) {
            hasEOFbeenSeen = true;
        }

        /* Return to the caller */
        return iByte;
    }
}
