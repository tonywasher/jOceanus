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
import java.io.OutputStream;

import SevenZip.Compression.LZMA.Decoder;

public class LZMAInputStream extends InputStream {
    /**
     * The pipe to the worker thread
     */
    private final PipedStream thePipe;

    /**
     * The sink stream to write to for the decoder thread
     */
    private final OutputStream theSink;

    /**
     * The source stream for the decoder thread
     */
    private final InputStream theInput;

    /**
     * The source stream to read from the decoder thread
     */
    private final InputStream theSource;

    /**
     * The decoder thread
     */
    private final DecoderThread theThread;

    /**
     * Constructor
     * @param pInput the input stream to wrap
     */
    public LZMAInputStream(InputStream pInput) {
        /* Store the target */
        theInput = pInput;

        /* Create the piped stream */
        thePipe = new PipedStream();
        theSink = thePipe.getSink();
        theSource = thePipe.getSource();

        /* Create decoder thread */
        theThread = new DecoderThread();
    }

    @Override
    public int read(byte[] pBytes) throws IOException {
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
    public int read(byte[] pBuffer,
                    int pOffset,
                    int pLength) throws IOException {

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
     * The decoder thread
     */
    private class DecoderThread extends Thread {
        /**
         * The decoder
         */
        private final Decoder theDecoder;

        /**
         * The error
         */
        private IOException theError;

        /**
         * Constructor
         */
        private DecoderThread() {
            /* Create the encoder */
            theDecoder = new Decoder();
            theError = null;
        }

        /**
         * Check for error
         * @throws IOException
         */
        private void checkForError() throws IOException {
            if (theError != null)
                throw theError;
        }

        @Override
        public void run() {
            try {
                int numProperties = 5;
                byte[] myProperties = new byte[numProperties];

                /* Read the decoder properties */
                int n = theInput.read(myProperties, 0, numProperties);
                if (n != numProperties) {
                    theError = new IOException("input stream too short");
                    return;
                }

                /* Set the decoder properties */
                theDecoder.SetDecoderProperties(myProperties);

                /* Decode the stream */
                theDecoder.Code(theInput, theSink, -1);

                /* Close the input stream */
                theInput.close();
            } catch (IOException e) {
                theError = e;
            }
        }
    }
}
