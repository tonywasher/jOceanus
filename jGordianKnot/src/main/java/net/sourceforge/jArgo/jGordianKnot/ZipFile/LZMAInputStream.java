/*******************************************************************************
 * JGordianKnot: Security Suite
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
package net.sourceforge.jArgo.jGordianKnot.ZipFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import SevenZip.Compression.LZMA.Decoder;
import SevenZip.Compression.LZMA.Encoder;

/**
 * Provides an LZMA decompression InputStream. Due to the design of the 7-Zip libraries the decompression must
 * be performed on a separate thread. A thread is created to read the compressed data from the input stream,
 * decompress the data and write the output to a PipeStream. This class works as the wrapper to read the
 * decompressed data from the PipedStream.
 */
public final class LZMAInputStream extends InputStream {
    /**
     * The sink stream to write to for the decoder thread.
     */
    private final OutputStream theSink;

    /**
     * The source stream for the decoder thread.
     */
    private final InputStream theInput;

    /**
     * The source stream to read from the decoder thread.
     */
    private final InputStream theSource;

    /**
     * The decoder thread.
     */
    private final DecoderThread theThread;

    /**
     * Constructor.
     * @param pInput the input stream to wrap
     */
    public LZMAInputStream(final InputStream pInput) {
        /* Store the target */
        theInput = pInput;

        /* Create the piped stream */
        PipedStream myPipe = new PipedStream();
        theSink = myPipe.getSink();
        theSource = myPipe.getSource();

        /* Create decoder thread */
        theThread = new DecoderThread();
        theThread.start();
    }

    @Override
    public int read(final byte[] pBytes) throws IOException {
        /* Read the bytes from the source */
        int myResult = read(pBytes, 0, pBytes.length);

        /* Check for error */
        theThread.checkForError();

        /* Read the bytes from the source */
        return myResult;
    }

    @Override
    public int read() throws IOException {
        /* Read the bytes from the source */
        int myResult = theSource.read();

        /* Check for error */
        theThread.checkForError();

        /* Read the bytes from the source */
        return myResult;
    }

    @Override
    public int read(final byte[] pBuffer,
                    final int pOffset,
                    final int pLength) throws IOException {

        /* Read the bytes from the source */
        int myResult = theSource.read(pBuffer, pOffset, pLength);

        /* Check for error */
        theThread.checkForError();

        /* Read the bytes from the source */
        return myResult;
    }

    @Override
    public void close() throws IOException {
        /* Close the source */
        theSource.close();
    }

    /**
     * The decoder thread.
     */
    private final class DecoderThread extends Thread {
        /**
         * The decoder.
         */
        private final Decoder theDecoder;

        /**
         * The error.
         */
        private IOException theError;

        /**
         * Constructor.
         */
        private DecoderThread() {
            /* Create the decoder */
            theDecoder = new Decoder();
            theError = null;
        }

        /**
         * Check for error.
         * @throws IOException on error
         */
        private void checkForError() throws IOException {
            if (theError != null) {
                throw theError;
            }
        }

        @Override
        public void run() {
            try {
                byte[] myProperties = new byte[Encoder.kPropSize];

                /* Read the decoder properties */
                int n = theInput.read(myProperties, 0, Encoder.kPropSize);
                if (n != Encoder.kPropSize) {
                    theError = new IOException("input stream too short");
                    return;
                }

                /* Set the decoder properties */
                theDecoder.SetDecoderProperties(myProperties);

                /* Decode the stream */
                theDecoder.Code(theInput, theSink, -1);

                /* Close the input/output streams */
                theInput.close();
                theSink.close();
            } catch (IOException e) {
                theError = e;
            }
        }
    }
}
