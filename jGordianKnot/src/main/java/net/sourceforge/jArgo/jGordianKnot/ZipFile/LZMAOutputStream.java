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
package net.sourceforge.JGordianKnot.ZipFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import SevenZip.Compression.LZMA.Encoder;

/**
 * Provides an LZMA compression OutputStream. Due to the design of the 7-Zip libraries the decompression must
 * be performed on a separate thread. A thread is created to read data from a PipedStream and to compress the
 * data to the target output stream. This class works as the wrapper to write the data to be compressed to the
 * PipedStream.
 */
public final class LZMAOutputStream extends OutputStream {
    /**
     * The sink stream to write to the encoder thread.
     */
    private final OutputStream theSink;

    /**
     * The source stream for the encoder thread.
     */
    private final InputStream theSource;

    /**
     * The target stream for the encoder thread.
     */
    private final OutputStream theTarget;

    /**
     * The encoder thread.
     */
    private final EncoderThread theThread;

    /**
     * Constructor.
     * @param pOutput the output stream to wrap
     */
    public LZMAOutputStream(final OutputStream pOutput) {
        /* Store the target */
        theTarget = pOutput;

        /* Create the piped stream */
        PipedStream myPipe = new PipedStream();
        theSink = myPipe.getSink();
        theSource = myPipe.getSource();

        /* Create encoder thread */
        theThread = new EncoderThread();
        theThread.start();
    }

    @Override
    public void write(final byte[] pBytes,
                      final int pOffset,
                      final int pLength) throws IOException {
        /* Check for error */
        theThread.checkForError();

        /* Write to the sink */
        theSink.write(pBytes, pOffset, pLength);
    }

    @Override
    public void write(final byte[] pBytes) throws IOException {
        /* Check for error */
        theThread.checkForError();

        /* Write to the sink */
        theSink.write(pBytes);
    }

    @Override
    public void write(final int pByte) throws IOException {
        /* Check for error */
        theThread.checkForError();

        /* Write to the sink */
        theSink.write(pByte);
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
        /* Close the sink */
        theSink.close();

        /* Wait for thread to terminate */
        try {
            theThread.join();
        } catch (InterruptedException e) {
            throw new IOException(e.getMessage(), e);
        }

        /* Check for error */
        theThread.checkForError();
    }

    /**
     * The encoder thread.
     */
    private final class EncoderThread extends Thread {
        /**
         * The encoder.
         */
        private final Encoder theEncoder;

        /**
         * The error.
         */
        private IOException theError;

        /**
         * Constructor.
         */
        private EncoderThread() {
            /* Create the encoder */
            theEncoder = new Encoder();
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
                /* Set end markerMode on */
                theEncoder.SetEndMarkerMode(true);

                /* Write encoder properties */
                theEncoder.WriteCoderProperties(theTarget);

                /* Encode the source stream to the target */
                theEncoder.Code(theSource, theTarget, -1, -1, null);

                /* Close the target */
                theTarget.close();
            } catch (IOException e) {
                theError = e;
            }
        }
    }
}
