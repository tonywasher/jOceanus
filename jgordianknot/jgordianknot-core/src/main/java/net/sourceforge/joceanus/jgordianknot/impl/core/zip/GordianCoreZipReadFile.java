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
package net.sourceforge.joceanus.jgordianknot.impl.core.zip;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipLock;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipFileContents;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipFileEntry;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipReadFile;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySet;
import net.sourceforge.joceanus.jgordianknot.impl.core.stream.GordianStreamManager;
import net.sourceforge.joceanus.jgordianknot.impl.core.zip.GordianCoreZipLock.GordianUnlockNotify;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Class used to extract from a ZipFile.
 */
public class GordianCoreZipReadFile
    implements GordianZipReadFile, GordianUnlockNotify {
    /**
     * The extension size for the buffer.
     */
    private static final int BUFFERSIZE = 1024;

    /**
     * Lock for this zip file.
     */
    private final GordianCoreZipLock theLock;

    /**
     * The contents of this zip file.
     */
    private GordianCoreZipFileContents theContents;

    /**
     * The zip file contents.
     */
    private final byte[] theZipFile;

    /**
     * KeySet for this zip file.
     */
    private GordianCoreKeySet theKeySet;

    /**
     * The header bytes.
     */
    private final byte[] theHeader;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pInputStream the input stream to read
     * @throws OceanusException on error
     */
    GordianCoreZipReadFile(final GordianFactory pFactory,
                           final InputStream pInputStream) throws OceanusException {
        /* Protect against exceptions */
        try (BufferedInputStream myInBuffer = new BufferedInputStream(pInputStream);
             ByteArrayOutputStream myOutBuffer = new ByteArrayOutputStream()) {
            /* Read the Zip file into memory */
            myInBuffer.transferTo(myOutBuffer);
            theZipFile = myOutBuffer.toByteArray();

            /* Handle exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Exception accessing Zip file", e);
        }

        /* Protect against exceptions */
        try (ByteArrayInputStream myInBuffer = new ByteArrayInputStream(theZipFile);
             ZipInputStream myHdrStream = new ZipInputStream(myInBuffer)) {
            /* Create the file contents */
            theContents = new GordianCoreZipFileContents();

            /* Loop through the Zip file entries */
            ZipEntry myEntry;
            for (;;) {
                /* Read next entry */
                myEntry = myHdrStream.getNextEntry();

                /* If this is EOF or a header record break the loop */
                if (myEntry == null
                        || myEntry.getExtra() != null) {
                    break;
                }

                /* Add to list of contents */
                theContents.addZipFileEntry(myEntry);
            }

            /* If we have a header */
            if (myEntry != null) {
                /* Pick up security lock */
                theLock = new GordianCoreZipLock(pFactory, this, myEntry.getExtra());
                theHeader = readHeader(myHdrStream);
            } else {
                /* Record no security */
                theLock = null;
                theHeader = null;
            }

            /* Catch exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Exception accessing Zip file", e);
        }
    }

    @Override
    public boolean isEncrypted() {
        return theLock != null;
    }

    @Override
    public GordianZipFileContents getContents() {
        return theContents;
    }

    @Override
    public GordianZipLock getLock() {
        return theLock;
    }

    @Override
    public void notifyUnlock() throws OceanusException {
        /* Access the keySet */
        final GordianCoreKeySet myKeySet = (GordianCoreKeySet) theLock.getKeySet();

        /* Parse the decrypted header */
        final byte[] myBytes = myKeySet.decryptBytes(theHeader);
        theContents = new GordianCoreZipFileContents(TethysDataConverter.byteArrayToString(myBytes));

        /* Access the security details */
        final GordianCoreZipFileEntry myHeader = theContents.getHeader();

        /* Reject if the entry is not found */
        if (myHeader == null) {
            throw new GordianDataException("Header record not found.");
        }

        /* Obtain encoded keySet */
        final byte[] mySecuredKeySet = myHeader.getHash();
        theKeySet = myKeySet.deriveKeySet(mySecuredKeySet);
   }

    /**
     * Read the header.
     * @param pHdrStream the header stream
     * @return the header
     * @throws IOException on error
     */
    private static byte[] readHeader(final InputStream pHdrStream) throws IOException {
        /* Initialise variables */
        int myLen = 0;
        int mySpace = BUFFERSIZE;
        byte[] myBuffer = new byte[BUFFERSIZE];

        /* Loop */
        for (;;) {
            /* Read the header entry */
            final int myRead = pHdrStream.read(myBuffer, myLen, mySpace);
            if (myRead == -1) {
                break;
            }

            /* Adjust buffer */
            myLen += myRead;
            mySpace -= myRead;

            /* If we have finished up the buffer */
            if (mySpace == 0) {
                /* Increase the buffer */
                myBuffer = Arrays.copyOf(myBuffer, myLen
                        + BUFFERSIZE);
                mySpace += BUFFERSIZE;
            }
        }

        /* Cut down the buffer to size */
        return Arrays.copyOf(myBuffer, myLen);
    }

    @Override
    public Document readXMLDocument(final GordianZipFileEntry pFile) throws OceanusException {
        /* Access the entry as an input stream */
        try (InputStream myInputStream = createInputStream(pFile)) {
            /* Create a Document builder */
            final DocumentBuilderFactory myFactory = DocumentBuilderFactory.newInstance();
            myFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            myFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            myFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            final DocumentBuilder myBuilder = myFactory.newDocumentBuilder();

            /* Build the document from the input stream */
            return myBuilder.parse(myInputStream);

            /* Catch exceptions */
        } catch (IOException
                | ParserConfigurationException
                | SAXException e) {
            throw new GordianIOException("Failed to parse Document", e);
        }
    }

    @Override
    public InputStream createInputStream(final GordianZipFileEntry pFile) throws OceanusException {
        /* Check that entry belongs to this zip file */
        if (!pFile.getParent().equals(theContents)) {
            throw new GordianDataException("File does not belong to Zip file");
        }

        /* Declare control variables */
        ZipInputStream myZipFile = null;
        InputStream myResult = null;

        /* Protect against exceptions */
        final GordianCoreZipFileEntry myFile = (GordianCoreZipFileEntry) pFile;
        try {
            /* Open the zip file for reading */
            final ByteArrayInputStream myInBuffer = new ByteArrayInputStream(theZipFile);
            myZipFile = new ZipInputStream(myInBuffer);

            /* Access the name of the file entry */
            final String myName = myFile.getZipName();
            ZipEntry myEntry;

            /* Loop through the Zip file entries */
            do {
                /* Read the entry */
                myEntry = myZipFile.getNextEntry();

                /* Break if we reached EOF or found the correct entry */
            } while (myEntry != null
                    && myEntry.getName().compareTo(myName) != 0);

            /* Handle entry not found */
            if (myEntry == null) {
                myZipFile.close();
                throw new GordianDataException("File not found - "
                        + pFile.getFileName());
            }

            /* If the file is encrypted */
            if (isEncrypted()) {
                /* Create a StreamManager */
                final GordianStreamManager myManager = new GordianStreamManager(theKeySet);

                /* Build input stream */
                myResult = myManager.buildInputStream(myFile.buildInputList(), myZipFile);

                /* Else we are already OK */
            } else {
                myResult = myZipFile;
            }

            /* return the new stream */
            return myResult;

            /* Catch exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Exception creating new Input stream", e);

        } finally {
            /* Close the ZipFile on error */
            if (myZipFile != null && myResult == null) {
                GordianStreamManager.cleanUpInputStream(myZipFile);
            }
        }
    }
}
