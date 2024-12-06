/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.zip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipFileEntry;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipWriteFile;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianIOException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianLogicException;
import net.sourceforge.joceanus.gordianknot.impl.core.keyset.GordianCoreKeySet;
import net.sourceforge.joceanus.gordianknot.impl.core.stream.GordianStreamDefinition;
import net.sourceforge.joceanus.gordianknot.impl.core.stream.GordianStreamManager;
import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.oceanus.OceanusDataConverter;

/**
 * Class used to build a ZipFile.
 */
public class GordianCoreZipWriteFile
        implements GordianZipWriteFile {
    /**
     * The FileName prefix.
     */
    private static final String FILE_PREFIX = "File";

    /**
     * Security Lock for this zip file.
     */
    private final GordianCoreZipLock theLock;

    /**
     * securedKeySet for this zip file.
     */
    private final byte[] theSecuredKeySet;

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
     * @param pLock the lock to use
     * @param pOutputStream the output stream to write to
     * @throws OceanusException on error
     */
    GordianCoreZipWriteFile(final GordianCoreZipLock pLock,
                            final OutputStream pOutputStream) throws OceanusException {
        /* Check that the lock is usable */
        if (pLock == null || !pLock.isFresh()) {
            throw new GordianDataException("Invalid lock");
        }

        /* Record lock and mark as used */
        theLock = pLock;
        pLock.markAsUsed();

        /* Create a child hash and record details */
        final GordianCoreKeySet myKeySet = (GordianCoreKeySet) theLock.getKeySet();
        final GordianFactory myFactory = myKeySet.getFactory();
        theKeySet = (GordianCoreKeySet) myFactory.getKeySetFactory().generateKeySet(myKeySet.getKeySetSpec());
        theSecuredKeySet = myKeySet.secureKeySet(theKeySet);

        /* Create the Stream Manager */
        theStreamFactory = new GordianStreamManager(theKeySet);

        /* reSeed the random number generator */
        theKeySet.getFactory().reSeedRandom();

        /* Create the output streams */
        final BufferedOutputStream myOutBuffer = new BufferedOutputStream(pOutputStream);
        theStream = new ZipOutputStream(myOutBuffer);

        /*
         * Set compression level to zero to speed things up. It would be nice to use the STORED
         * method, but this requires calculating the CRC and file size prior to writing data to
         * the Zip file which will badly affect performance.
         */
        theStream.setLevel(ZipOutputStream.STORED);

        /* Create the file contents */
        theContents = new GordianCoreZipFileContents();
    }

    /**
     * Constructor for new output zip file with no security.
     * @param pOutputStream the output stream to write to
     */
    GordianCoreZipWriteFile(final OutputStream pOutputStream) {
        /* record null security */
        theLock = null;
        theSecuredKeySet = null;
        theKeySet = null;
        theStreamFactory = null;

        /* Create the output streams */
        final BufferedOutputStream myOutBuffer = new BufferedOutputStream(pOutputStream);
        theStream = new ZipOutputStream(myOutBuffer);

        /* Create the file contents */
        theContents = new GordianCoreZipFileContents();
    }

    /**
     * Is the ZipFile encrypted.
     * @return is the Zip File encrypted
     */
    private boolean isEncrypted() {
        return theLock != null;
    }

    @Override
    public GordianCoreZipFileContents getContents() {
        return theContents;
    }

    @Override
    public GordianZipFileEntry getCurrentEntry() {
        return theFileEntry;
    }

    @Override
    public void writeXMLDocument(final File pFile,
                                 final Document pDocument) throws OceanusException {
        /* Access the entry as an input stream */
        try (OutputStream myOutputStream = createOutputStream(pFile, true)) {
            /* Create the transformer */
            final TransformerFactory myXformFactory = TransformerFactory.newInstance();
            myXformFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            myXformFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            myXformFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            final Transformer myXformer = myXformFactory.newTransformer();

            /* Format the XML and write to stream */
            myXformer.transform(new DOMSource(pDocument), new StreamResult(myOutputStream));

            /* Catch exceptions */
        } catch (IOException
                | TransformerException e) {
            throw new GordianIOException("Failed to write Document", e);
        }
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
            theOutput = new GordianWrapOutputStream(theStream);

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
    private void closeOutputStream() throws IOException {
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
                    myEntry.setHash(theSecuredKeySet);

                    /* Create the header entry */
                    ++theFileNo;
                    theEntry = new ZipEntry(FILE_PREFIX
                            + theFileNo);

                    /* Declare the lock and encrypt the header */
                    theEntry.setExtra(theLock.getEncodedBytes());

                    /* Start the new entry */
                    theStream.putNextEntry(theEntry);

                    /* Declare the details */
                    myEntry.setZipEntry(theEntry);

                    /* Access the encoded file string */
                    final String myHeader = theContents.encodeContents();

                    /* Write the bytes to the Zip file and close the entry */
                    final byte[] myBytes = OceanusDataConverter.stringToByteArray(myHeader);
                    final GordianKeySet myKeySet = theLock.getKeySet();
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
    private final class GordianWrapOutputStream
            extends OutputStream {
        /**
         * The underlying Zip output stream.
         */
        private final ZipOutputStream theStream;

        /**
         * Constructor.
         * @param pStream the ZipStream
         */
        GordianWrapOutputStream(final ZipOutputStream pStream) {
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
