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

import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JGordianKnot.MsgDigest;

/**
 * Provides a digest InputStream. This class simply calculates a digest of the data in the stream at this
 * point as it is read. On close of the file, the digest is validated.
 */
public class DigestInputStream extends InputStream {
    /**
     * Buffer size for skip reads
     */
    private final static int BUFSIZE = 1024;

    /**
     * The underlying input stream
     */
    private final InputStream theStream;

    /**
     * has this stream been closed
     */
    private boolean isClosed = false;

    /**
     * The Message Digest of the data read
     */
    private final MsgDigest theDigest;

    /**
     * The Skip buffer
     */
    private byte[] theSkipBuffer = new byte[BUFSIZE];

    /**
     * Has EOF been reached
     */
    private boolean hasEOFbeenSeen = false;

    /**
     * Construct the input stream
     * @param pDigest the message digest
     * @param pStream the Stream to read data from
     */
    public DigestInputStream(MsgDigest pDigest,
                             InputStream pStream) {
        /* Store the message digest */
        theDigest = pDigest;

        /* Store the stream details */
        theStream = pStream;
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
                /* Validate the digest */
                try {
                    theDigest.validateDigest();
                } catch (ModelException e) {
                    throw new IOException(e.getMessage());
                }
            }

            /* Release allocated encryption resources */
            theSkipBuffer = null;
        }
    }

    @Override
    public long skip(long pNumToSkip) throws IOException {
        long iNumToSkip = pNumToSkip;
        long iNumSkipped = 0;
        int iNumToRead;
        int iNumRead;

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
            iNumRead = read(theSkipBuffer, 0, iNumToRead);

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
        return theStream.available();
    }

    @Override
    public boolean markSupported() {
        /* Always return false */
        return false;
    }

    @Override
    public void mark(int readLimit) {
        /* Just ignore the call */
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
        /* If we are already closed throw IO Exception */
        if (isClosed)
            throw new IOException("Stream is closed");

        /* Read the bytes from the stream */
        int iNumRead = theStream.read(pBuffer, pOffset, pLength);

        /* If we have read some data */
        if (iNumRead > 0) {
            /* Update the message digest */
            theDigest.update(pBuffer, pOffset, iNumRead);
        }

        /* Note if EOF has been seen */
        if (iNumRead == -1)
            hasEOFbeenSeen = true;

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
        /* If we are already closed throw IO Exception */
        if (isClosed)
            throw new IOException("Stream is closed");

        /* read the next byte */
        int iByte = theStream.read();

        /* If we read data */
        if (iByte > -1) {
            /* Update the message digest */
            theDigest.update((byte) iByte);
        }

        /* Note if EOF has been seen */
        if (iByte == -1)
            hasEOFbeenSeen = true;

        /* Return to the caller */
        return iByte;
    }
}
