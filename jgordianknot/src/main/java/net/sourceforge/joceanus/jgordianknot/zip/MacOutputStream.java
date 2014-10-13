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
import java.io.OutputStream;

import net.sourceforge.joceanus.jgordianknot.crypto.DataMac;
import net.sourceforge.joceanus.jgordianknot.crypto.MacSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.StreamCipher;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Provides a Mac OutputStream. This class simply calculates a Mac of the data in the stream at this point and passes the data onto the next Output Stream in
 * the chain.
 */
public class MacOutputStream
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
     * The Data Mac of the data written.
     */
    private final DataMac theMac;

    /**
     * The length of the data digested.
     */
    private long theDataLen = 0;

    /**
     * Access the Authentication Code of the data written.
     * @return the Mac of data written
     */
    public byte[] getAuthCode() {
        return theMac.finish();
    }

    /**
     * Access the DataMac.
     * @return the DataMac
     */
    protected DataMac getDataMac() {
        return theMac;
    }

    /**
     * Access the Mac of the data written.
     * @return the Mac of data written
     */
    protected MacSpec getMacSpec() {
        return theMac.getMacSpec();
    }

    /**
     * Access the data length.
     * @return the data length
     */
    public long getDataLen() {
        return theDataLen;
    }

    /**
     * Obtain the next stream.
     * @return the stream
     */
    protected OutputStream getNextStream() {
        return theStream;
    }

    /**
     * Construct the output stream.
     * @param pMac the data mac
     * @param pStream the stream to write encrypted data to
     * @throws JOceanusException on error
     */
    public MacOutputStream(final DataMac pMac,
                           final OutputStream pStream) throws JOceanusException {
        /* Store the data mac */
        theMac = pMac;

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
            throw new IOException(StreamCipher.ERROR_CLOSED);
        }

        /* Update the Mac */
        theMac.update(pBytes, pOffset, pLength);

        /* Adjust the data length */
        theDataLen += pLength;

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
            throw new IOException(StreamCipher.ERROR_CLOSED);
        }

        /* Update the data mac */
        theMac.update((byte) pByte);

        /* Adjust the data length */
        theDataLen++;

        /* Write the byte to the stream */
        theStream.write(pByte);
    }
}
