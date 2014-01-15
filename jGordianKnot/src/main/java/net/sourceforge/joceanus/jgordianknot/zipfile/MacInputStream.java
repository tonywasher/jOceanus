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
package net.sourceforge.joceanus.jgordianknot.zipfile;

import java.io.IOException;
import java.io.InputStream;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jgordianknot.DataMac;
import net.sourceforge.joceanus.jgordianknot.StreamCipher;

import org.bouncycastle.util.Arrays;

/**
 * Provides a Mac InputStream. This class simply calculates a Mac of the data in the stream at this point as it is read. On close of the file, the digest is
 * validated.
 */
public class MacInputStream
        extends InputStream {
    /**
     * Invalid Mac error.
     */
    private static final String ERROR_MAC = "Invalid Mac";

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
     * The Data Mac of the data read.
     */
    private final DataMac theMac;

    /**
     * The Skip buffer.
     */
    private final byte[] theSkipBuffer = new byte[BUFSIZE];

    /**
     * The expected mac value.
     */
    private final byte[] theExpectedMac;

    /**
     * Has EOF been reached.
     */
    private boolean hasEOFbeenSeen = false;

    /**
     * Construct the input stream.
     * @param pMac the data Mac
     * @param pExpected the expected mac value
     * @param pStream the Stream to read data from
     */
    public MacInputStream(final DataMac pMac,
                          final byte[] pExpected,
                          final InputStream pStream) {
        /* Store the data Mac */
        theMac = pMac;
        theExpectedMac = Arrays.copyOf(pExpected, pExpected.length);

        /* Store the stream details */
        theStream = pStream;
    }

    /**
     * Validate the digest.
     * @param pExpected the expected digest
     * @throws JDataException on error
     */
    public void validateMac(final byte[] pExpected) throws JDataException {
        /* Calculate mac */
        byte[] myMac = theMac.finish();

        /* Check valid digest */
        if (Arrays.areEqual(myMac, pExpected)) {
            throw new JDataException(ExceptionClass.DATA, ERROR_MAC);
        }
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
                /* Calculate mac */
                byte[] myMac = theMac.finish();

                /* Check valid mac */
                if (!Arrays.areEqual(myMac, theExpectedMac)) {
                    throw new IOException(ERROR_MAC);
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
            /* Update the data mac */
            theMac.update(pBuffer, pOffset, iNumRead);
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
            /* Update the data mac */
            theMac.update((byte) iByte);
        }

        /* Note if EOF has been seen */
        if (iByte == -1) {
            hasEOFbeenSeen = true;
        }

        /* Return to the caller */
        return iByte;
    }
}