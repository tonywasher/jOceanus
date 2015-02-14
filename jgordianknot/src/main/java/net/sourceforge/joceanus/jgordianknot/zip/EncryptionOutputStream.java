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
import java.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.crypto.CipherMode;
import net.sourceforge.joceanus.jgordianknot.crypto.StreamCipher;
import net.sourceforge.joceanus.jgordianknot.crypto.StreamKey;
import net.sourceforge.joceanus.jgordianknot.crypto.StreamKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.SymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.SymmetricKey;
import net.sourceforge.joceanus.jgordianknot.zip.ZipStreamSpec.ZipStreamType;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Provide an encrypt OutputStream wrapper. This class simply wraps an output buffer and encrypts the data before passing it on.
 */
public class EncryptionOutputStream
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
     * The Stream Cipher.
     */
    private final StreamCipher theCipher;

    /**
     * The initialisation vector.
     */
    private final byte[] theInitVector;

    /**
     * The Stream type.
     */
    private final ZipStreamType theStreamType;

    /**
     * The symmetric key.
     */
    private final SymmetricKey theSymKey;

    /**
     * The cipherMode.
     */
    private final CipherMode theMode;

    /**
     * The stream Key.
     */
    private final StreamKey theStreamKey;

    /**
     * A buffer for single byte writes.
     */
    private final byte[] theByte;

    /**
     * Construct a symmetric key encryption output stream.
     * @param pKey the symmetric key to use
     * @param pMode the cipher mode to use
     * @param pStream the stream to encrypt to
     * @throws JOceanusException on error
     */
    public EncryptionOutputStream(final SymmetricKey pKey,
                                  final CipherMode pMode,
                                  final OutputStream pStream) throws JOceanusException {
        /* Store the key */
        theSymKey = pKey;
        theStreamKey = null;
        theMode = pMode;

        /* Create the byte buffer */
        theByte = new byte[1];

        /* record the output stream */
        theStream = pStream;
        theStreamType = ZipStreamType.SYMMETRIC;

        /* Initialise the cipher */
        theCipher = new StreamCipher(pKey, pMode);
        theInitVector = theCipher.initialiseEncryption(pKey.getRandom());
    }

    /**
     * Construct a stream key encryption output stream.
     * @param pKey the stream key to use
     * @param pStream the stream to encrypt to
     * @throws JOceanusException on error
     */
    public EncryptionOutputStream(final StreamKey pKey,
                                  final OutputStream pStream) throws JOceanusException {
        /* Store the key */
        theStreamKey = pKey;
        theSymKey = null;
        theMode = null;

        /* Create the byte buffer */
        theByte = new byte[1];

        /* record the output stream */
        theStream = pStream;
        theStreamType = ZipStreamType.STREAM;

        /* Initialise the cipher */
        theCipher = new StreamCipher(pKey);
        theInitVector = theCipher.initialiseEncryption(pKey.getRandom());
    }

    /**
     * Access the symmetric key.
     * @return the key
     */
    public SymmetricKey getSymKey() {
        return theSymKey;
    }

    /**
     * Access the stream key.
     * @return the key
     */
    public StreamKey getStreamKey() {
        return theStreamKey;
    }

    /**
     * Access the stream type.
     * @return the stream type
     */
    public ZipStreamType getStreamType() {
        return theStreamType;
    }

    /**
     * Access the symKey type.
     * @return the symKey type
     */
    public SymKeyType getSymKeyType() {
        return (theSymKey == null)
                                  ? null
                                  : theSymKey.getKeyType();
    }

    /**
     * Access the cipher mode.
     * @return the mode
     */
    public CipherMode getCipherMode() {
        return theMode;
    }

    /**
     * Access the streamKey type.
     * @return the streamKey type
     */
    public StreamKeyType getStreamKeyType() {
        return (theStreamKey == null)
                                     ? null
                                     : theStreamKey.getKeyType();
    }

    /**
     * Access the initialisation vector.
     * @return the initialisation vector
     */
    public byte[] getInitVector() {
        return Arrays.copyOf(theInitVector, theInitVector.length);
    }

    /**
     * Obtain the next stream.
     * @return the stream
     */
    protected OutputStream getNextStream() {
        return theStream;
    }

    @Override
    public void close() throws IOException {
        /* Protect against exceptions */
        try {
            /* Null operation if we are already closed */
            if (!isClosed) {
                /* Finish the cipher operation */
                int iNumBytes = theCipher.finish();
                byte[] myBytes = theCipher.getBuffer();

                /* If we have data to write then write it */
                if (iNumBytes > 0) {
                    theStream.write(myBytes, 0, iNumBytes);
                }

                /* Close the output stream */
                theStream.close();
                isClosed = true;
            }

            /* Catch exceptions */
        } catch (JOceanusException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void flush() throws IOException {
        /* If we are already closed throw IO Exception */
        if (isClosed) {
            throw new IOException(StreamCipher.ERROR_CLOSED);
        }

        /* Flush the output stream */
        theStream.flush();
    }

    @Override
    public void write(final byte[] pBytes,
                      final int pOffset,
                      final int pLength) throws IOException {
        /* If we are already closed throw IO Exception */
        if (isClosed) {
            throw new IOException(StreamCipher.ERROR_CLOSED);
        }

        /* Ignore a null write */
        if (pLength == 0) {
            return;
        }

        /* Protect against exceptions */
        try {
            /* Update the cipher with these bytes */
            int iNumBytes = theCipher.update(pBytes, pOffset, pLength);
            byte[] myBytes = theCipher.getBuffer();

            /* Write the bytes to the stream */
            theStream.write(myBytes, 0, iNumBytes);

            /* Catch exceptions */
        } catch (JOceanusException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void write(final byte[] pBytes) throws IOException {
        /* Write the bytes to the stream */
        write(pBytes, 0, pBytes.length);
    }

    @Override
    public void write(final int pByte) throws IOException {
        /* Copy the byte to the buffer */
        theByte[0] = (byte) pByte;

        /* Write the byte to the stream */
        write(theByte, 0, 1);
    }
}
