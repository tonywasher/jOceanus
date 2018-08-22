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
package net.sourceforge.joceanus.jgordianknot.zip;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.GordianLogicException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.crypto.stream.GordianStreamManager;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Class used to extract from a ZipFile.
 */
public class GordianZipReadFile {
    /**
     * The ZipFile extension.
     */
    public static final String ZIPFILE_EXT = ".zip";

    /**
     * The extension size for the buffer.
     */
    private static final int BUFFERSIZE = 1024;

    /**
     * HashBytes for this zip file.
     */
    private final byte[] theHashBytes;

    /**
     * The contents of this zip file.
     */
    private GordianZipFileContents theContents;

    /**
     * The name of the Zip file.
     */
    private File theZipFile;

    /**
     * KeySet for this zip file.
     */
    private GordianKeySet theKeySet;

    /**
     * The header bytes.
     */
    private final byte[] theHeader;

    /**
     * Constructor.
     * @param pFile the file to read
     * @throws OceanusException on error
     */
    public GordianZipReadFile(final File pFile) throws OceanusException {
        /* Protect against exceptions */
        try (FileInputStream myInFile = new FileInputStream(pFile);
             BufferedInputStream myInBuffer = new BufferedInputStream(myInFile);
             ZipInputStream myHdrStream = new ZipInputStream(myInBuffer)) {
            /* Store the zipFile name */
            theZipFile = new File(pFile.getPath());

            /* Create the file contents */
            theContents = new GordianZipFileContents();

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
                /* Pick up security detail */
                theHashBytes = myEntry.getExtra();
                theHeader = readHeader(myHdrStream);
            } else {
                /* Record no security */
                theHashBytes = null;
                theHeader = null;
            }

            /* Catch exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Exception accessing Zip file", e);
        }
    }

    /**
     * Is the ZipFile encrypted.
     * @return is the Zip File encrypted
     */
    private boolean isEncrypted() {
        return theHashBytes != null;
    }

    /**
     * Obtain the contents.
     * @return the contents
     */
    public GordianZipFileContents getContents() {
        return theContents;
    }

    /**
     * Obtain the hash bytes for the file.
     * @return the hash bytes
     */
    public byte[] getHashBytes() {
        return (theHashBytes == null)
                                      ? null
                                      : Arrays.copyOf(theHashBytes, theHashBytes.length);
    }

    /**
     * Set the keySet hash.
     * @param pHash the keySet hash
     * @throws OceanusException on error
     */
    public void setKeySetHash(final GordianKeySetHash pHash) throws OceanusException {
        /* Ignore if we have no security */
        if (!isEncrypted()) {
            return;
        }

        /* Reject this is the wrong security control */
        if (!Arrays.equals(pHash.getHash(), theHashBytes)) {
            throw new GordianLogicException("Hash does not match ZipFile Security.");
        }

        /* Access the keySet */
        final GordianKeySet myKeySet = pHash.getKeySet();

        /* Parse the decrypted header */
        final byte[] myBytes = myKeySet.decryptBytes(theHeader);
        theContents = new GordianZipFileContents(TethysDataConverter.byteArrayToString(myBytes));

        /* Access the security details */
        final GordianZipFileEntry myHeader = theContents.getHeader();

        /* Reject if the entry is not found */
        if (myHeader == null) {
            throw new GordianDataException("Header record not found.");
        }

        /* Obtain encoded keySet */
        final byte[] myHashBytes = myHeader.getHash();
        final GordianKeySetHash myHash = pHash.attemptPasswordForHash(myHashBytes);

        /* Reject if wrong password */
        if (myHash == null) {
            throw new GordianDataException("Incorrect internal hash.");
        }

        /* Obtain the keySet */
        theKeySet = myHash.getKeySet();
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

    /**
     * Obtain an input stream for an entry in the zip file.
     * @param pFile the file details for the new zip entry
     * @return the input stream
     * @throws OceanusException on error
     */
    public InputStream getInputStream(final GordianZipFileEntry pFile) throws OceanusException {
        /* Check that entry belongs to this zip file */
        if (!pFile.getParent().equals(theContents)) {
            throw new GordianDataException("File does not belong to Zip file");
        }

        /* Declare control variables */
        ZipInputStream myZipFile = null;
        InputStream myResult = null;

        /* Protect against exceptions */
        try {
            /* Open the zip file for reading */
            final FileInputStream myInFile = new FileInputStream(theZipFile);
            final BufferedInputStream myInBuffer = new BufferedInputStream(myInFile);
            myZipFile = new ZipInputStream(myInBuffer);

            /* Access the name of the file entry */
            final String myName = pFile.getZipName();
            ZipEntry myEntry;

            /* Loop through the Zip file entries */
            for (;;) {
                /* Read the entry */
                myEntry = myZipFile.getNextEntry();

                /* Break if we reached EOF or found the correct entry */
                if (myEntry == null
                    || myEntry.getName().compareTo(myName) == 0) {
                    break;
                }
            }

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
                myResult = myManager.buildInputStream(pFile.buildInputList(), myZipFile);

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
