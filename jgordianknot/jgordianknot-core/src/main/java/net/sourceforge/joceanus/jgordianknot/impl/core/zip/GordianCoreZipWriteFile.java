/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.zip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipFileEntry;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipWriteFile;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySet;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySetHash;
import net.sourceforge.joceanus.jgordianknot.impl.core.stream.GordianStreamDefinition;
import net.sourceforge.joceanus.jgordianknot.impl.core.stream.GordianStreamManager;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Class used to build a ZipFile.
 */
public class GordianCoreZipWriteFile
        implements GordianZipWriteFile, AutoCloseable {
    /**
     * The Create ZipFile Error text.
     */
    private static final String ERROR_CREATE = "Failed to create ZipFile";

    /**
     * The FileName prefix.
     */
    protected static final String FILE_PREFIX = "File";

    /**
     * Security Hash for this zip file.
     */
    private final GordianCoreKeySetHash theHash;

    /**
     * HashBytes for this zip file.
     */
    private final byte[] theHashBytes;

    /**
     * KeySet for this zip file.
     */
    private final GordianCoreKeySet theKeySet;

    /**
     * The StreamFactory.
     */
    private final GordianStreamManager theStreamFactory;

    /**
     * The underlying Zip output stream.
     */
    private ZipOutputStream theStream;

    /**
     * The list of contents.
     */
    private final GordianCoreZipFileContents theContents;

    /**
     * The active zipEntry.
     */
    private ZipEntry theEntry;

    /**
     * The active zipFileEntry.
     */
    private GordianCoreZipFileEntry theFileEntry;

    /**
     * The active filename.
     */
    private String theFileName;

    /**
     * The active output stream.
     */
    private OutputStream theOutput;

    /**
     * The fileNumber.
     */
    private int theFileNo;

    /**
     * Constructor for new output zip file with security.
     * @param pHash the password hash to use
     * @param pFile the file details for the new zip file
     * @throws OceanusException on error
     */
    GordianCoreZipWriteFile(final GordianKeySetHash pHash,
                            final File pFile) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Record hash */
            theHash = (GordianCoreKeySetHash) pHash;

            /* Create a similar hash and record details */
            final GordianCoreKeySetHash myHash = theHash.similarHash();
            theHashBytes = myHash.getHash();
            theKeySet = myHash.getKeySet();

            /* Create the Stream Manager */
            theStreamFactory = new GordianStreamManager(theKeySet);

            /* reSeed the random number generator */
            theKeySet.getFactory().reSeedRandom();

            /* Create the output streams */
            final FileOutputStream myOutFile = new FileOutputStream(pFile);
            final BufferedOutputStream myOutBuffer = new BufferedOutputStream(myOutFile);
            theStream = new ZipOutputStream(myOutBuffer);

            /*
             * Set compression level to zero to speed things up. It would be nice to use the STORED
             * method, but this requires calculating the CRC and file size prior to writing data to
             * the Zip file which will badly affect performance.
             */
            theStream.setLevel(ZipOutputStream.STORED);

            /* Create the file contents */
            theContents = new GordianCoreZipFileContents();

            /* Catch exceptions */
        } catch (IOException e) {
            throw new GordianIOException(ERROR_CREATE, e);
        }
    }

    /**
     * Constructor for new output zip file with no security.
     * @param pFile the file details for the new zip file
     * @throws OceanusException on error
     */
    GordianCoreZipWriteFile(final File pFile) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* record null security */
            theHash = null;
            theHashBytes = null;
            theKeySet = null;
            theStreamFactory = null;

            /* Create the output streams */
            final FileOutputStream myOutFile = new FileOutputStream(pFile);
            final BufferedOutputStream myOutBuffer = new BufferedOutputStream(myOutFile);
            theStream = new ZipOutputStream(myOutBuffer);

            /* Create the file contents */
            theContents = new GordianCoreZipFileContents();

            /* Catch exceptions */
        } catch (IOException e) {
            throw new GordianIOException(ERROR_CREATE, e);
        }
    }

    /**
     * Is the ZipFile encrypted.
     * @return is the Zip File encrypted
     */
    private boolean isEncrypted() {
        return theHash != null;
    }

    /**
     * Obtain the contents.
     * @return the ZipFile Contents
     */
    public GordianCoreZipFileContents getContents() {
        return theContents;
    }

    /**
     * Obtain the currently active ZipFileEntry.
     * @return the ZipFile Entry
     */
    public GordianZipFileEntry getCurrentEntry() {
        return theFileEntry;
    }

    @Override
    public OutputStream createOutputStream(final File pFile,
                                           final boolean pCompress) throws OceanusException {
        /* Reject call if we have closed the stream */
        if (theStream == null) {
            throw new GordianLogicException("ZipFile is closed");
        }

        /* Reject call if we have an open stream */
        if (theOutput != null) {
            throw new GordianLogicException("Output stream already open");
        }

        /* Increment file number */
        theFileNo++;

        /* Protect against exceptions */
        try {
            /* Start the new entry */
            theFileName = pFile.getPath();
            theEntry = new ZipEntry(isEncrypted()
                                    ? FILE_PREFIX
                                            + theFileNo
                                    : theFileName);
            theStream.putNextEntry(theEntry);

            /* Create a new zipFileEntry */
            theFileEntry = theContents.addZipFileEntry(theFileName);

            /* Simply create a wrapper on the output stream */
            theOutput = new WrapOutputStream(theStream);

            /* If we are encrypting */
            if (isEncrypted()) {
                /* Create an the output stream */
                theOutput = theStreamFactory.buildOutputStream(theOutput, pCompress);
            }

            /* Catch exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Exception creating new Output stream", e);
        }

        /* return the new stream */
        return theOutput;
    }

    /**
     * Close any active output stream and record digest values.
     * @throws IOException on error
     */
    void closeOutputStream() throws IOException {
        /* Protect against exceptions */
        try {
            /* If we have an output stream */
            if (theOutput != null) {
                /* Close the active entry */
                theStream.closeEntry();

                /* Add the details of the entry */
                theFileEntry.setZipEntry(theEntry);

                /* If we have encryption */
                if (isEncrypted()) {
                    /* Analyse the output stream */
                    final List<GordianStreamDefinition> myStreams = theStreamFactory.analyseStreams(theOutput);

                    /* Analyse the stream */
                    theFileEntry.buildProperties(myStreams);
                }

                /* Release the entry */
                theEntry = null;
                theFileName = null;
                theFileEntry = null;
            }

            /* Reset streams */
            theOutput = null;

            /* Catch exceptions */
        } catch (OceanusException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        /* Close any open output stream */
        closeOutputStream();

        /* If the stream is open */
        if (theStream != null) {
            /* Protect against exceptions */
            try {
                /* If we have stored files and are encrypted */
                if (theFileNo > 0
                        && isEncrypted()) {
                    /* Create a new zipFileEntry */
                    final GordianCoreZipFileEntry myEntry = theContents.addZipFileHeader();
                    myEntry.setHash(theHashBytes);

                    /* Create the header entry */
                    ++theFileNo;
                    theEntry = new ZipEntry(FILE_PREFIX
                            + theFileNo);

                    /* Declare the password hash and encrypt the header */
                    theEntry.setExtra(theHash.getHash());

                    /* Start the new entry */
                    theStream.putNextEntry(theEntry);

                    /* Declare the details */
                    myEntry.setZipEntry(theEntry);

                    /* Access the encoded file string */
                    final String myHeader = theContents.encodeContents();

                    /* Write the bytes to the Zip file and close the entry */
                    final byte[] myBytes = TethysDataConverter.stringToByteArray(myHeader);
                    final GordianKeySet myKeySet = theHash.getKeySet();
                    theStream.write(myKeySet.encryptBytes(myBytes));
                    theStream.closeEntry();
                }

                /* close the stream */
                theStream.flush();
                theStream.close();
                theStream = null;

                /* Catch exceptions */
            } catch (OceanusException e) {
                throw new IOException(e);
            }
        }
    }

    /**
     * Wrapper class to catch close of output stream and prevent it from closing the ZipFile.
     */
    private final class WrapOutputStream
            extends OutputStream {
        /**
         * The underlying Zip output stream.
         */
        private final ZipOutputStream theStream;

        /**
         * Constructor.
         * @param pStream the ZipStream
         */
        WrapOutputStream(final ZipOutputStream pStream) {
            theStream = pStream;
        }

        @Override
        public void flush() throws IOException {
            theStream.flush();
        }

        @Override
        public void write(final int b) throws IOException {
            theStream.write(b);
        }

        @Override
        public void write(final byte[] b) throws IOException {
            theStream.write(b);
        }

        @Override
        public void write(final byte[] b,
                          final int offset,
                          final int length) throws IOException {
            theStream.write(b, offset, length);
        }

        @Override
        public void close() throws IOException {
            closeOutputStream();
        }
    }
}
