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
package net.sourceforge.joceanus.jgordianknot.test.crypto;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jgordianknot.test.crypto.GordianTestSuite.SecurityManagerCreator;
import net.sourceforge.joceanus.jgordianknot.zip.GordianZipFileContents;
import net.sourceforge.joceanus.jgordianknot.zip.GordianZipFileEntry;
import net.sourceforge.joceanus.jgordianknot.zip.GordianZipReadFile;
import net.sourceforge.joceanus.jgordianknot.zip.GordianZipWriteFile;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Security Test suite - Zip File.
 */
public class GordianTestZip {
    /**
     * Buffer length.
     */
    private static final int BUFFER_LEN = 1024;

    /**
     * The Security Manager creator.
     */
    private final SecurityManagerCreator theCreator;

    /**
     * Constructor.
     * @param pCreator the Secure Manager creator
     */
    public GordianTestZip(final SecurityManagerCreator pCreator) {
        theCreator = pCreator;
    }

    /**
     * Create a Zip File of files in a directory.
     * @param pZipFile the name of the zip file to create
     * @param pDirectory the directory to archive
     * @param bSecure encrypt the zip file (true/false)
     * @return the contents of the zip file
     * @throws OceanusException on error
     */
    protected GordianZipFileContents createZipFile(final File pZipFile,
                                                   final File pDirectory,
                                                   final boolean bSecure) throws OceanusException {
        /* Protect against exceptions */
        try (GordianZipWriteFile myZipFile = createZipFile(pZipFile, bSecure)) {
            /* Create a read buffer */
            final int myBufLen = BUFFER_LEN;
            final byte[] myBuffer = new byte[myBufLen];
            int myRead;

            /* Make sure that we have a directory */
            if (!pDirectory.isDirectory()) {
                myZipFile.close();
                throw new GordianDataException("Invalid source directory");
            }

            /* Loop through the files in the directory */
            for (File myFile : pDirectory.listFiles()) {
                /* Skip directories */
                if (myFile.isDirectory()) {
                    continue;
                }

                /* Open the file for reading */
                try (InputStream myInFile = new FileInputStream(myFile);
                     InputStream myInBuffer = new BufferedInputStream(myInFile);
                     OutputStream myOutput = myZipFile.getOutputStream(new File(myFile.getName()))) {
                    /* Read the header entry */
                    while ((myRead = myInBuffer.read(myBuffer, 0, myBufLen)) != -1) {
                        /* Write the data to the zip file */
                        myOutput.write(myBuffer, 0, myRead);
                    }

                } catch (IOException e) {
                    throw new GordianIOException("Failed to create Zip File", e);
                }
            }

            /* Close the Zip File */
            myZipFile.close();

            /* Return the zip file entries */
            return myZipFile.getContents();

        } catch (IOException e) {
            throw new GordianIOException("Failed to create Zip File", e);
        }
    }

    /**
     * Create a Zip File of files in a directory.
     * @param pZipFile the name of the zip file to create
     * @param bSecure encrypt the zip file (true/false)
     * @return the new zip file
     * @throws OceanusException on error
     */
    private GordianZipWriteFile createZipFile(final File pZipFile,
                                              final boolean bSecure) throws OceanusException {
        /* If we are creating a secure zip file */
        if (bSecure) {
            /* Create new Password Hash */
            final GordianHashManager myManager = theCreator.newSecureManager();
            final GordianKeySetHash myHash = myManager.newKeySetHash("New");

            /* Initialise the Zip file */
            return new GordianZipWriteFile(myHash, pZipFile);

            /* else */
        } else {
            /* Just create a standard zip file */
            return new GordianZipWriteFile(pZipFile);
        }
    }

    /**
     * Extract a Zip File to a directory.
     * @param pZipFile the name of the zip file to extract from
     * @param pDirectory the directory to extract to
     * @throws OceanusException on error
     */
    protected void extractZipFile(final File pZipFile,
                                  final File pDirectory) throws OceanusException {
        /* Access the file */
        final GordianZipReadFile myZipFile = new GordianZipReadFile(pZipFile);

        /* Check for security */
        final byte[] myHashBytes = myZipFile.getHashBytes();
        if (myHashBytes != null) {
            /* Resolve security and unlock file */
            final GordianHashManager myManager = theCreator.newSecureManager();
            final GordianKeySetHash myHash = myManager.resolveKeySetHash(myHashBytes, pZipFile.getName());
            myZipFile.setKeySetHash(myHash);
        }

        /* Access the contents */
        final GordianZipFileContents myContents = myZipFile.getContents();

        /* Create a read buffer */
        final int myBufLen = BUFFER_LEN;
        final byte[] myBuffer = new byte[myBufLen];
        int myRead;

        /* Make sure that we have a directory */
        if (!pDirectory.isDirectory()) {
            throw new GordianDataException("Invalid target directory");
        }

        /* Loop through the entries */
        final Iterator<GordianZipFileEntry> myIterator = myContents.iterator();
        while (myIterator.hasNext()) {
            /* Access next entry */
            final GordianZipFileEntry myEntry = myIterator.next();

            /* Protect against exceptions */
            try (InputStream myInput = myZipFile.getInputStream(myEntry);
                 OutputStream myOutFile = new FileOutputStream(new File(pDirectory, myEntry.getFileName()));
                 OutputStream myOutBuffer = new BufferedOutputStream(myOutFile)) {

                /* Read the entry */
                while ((myRead = myInput.read(myBuffer, 0, myBufLen)) != -1) {
                    /* Write the data to the new file */
                    myOutBuffer.write(myBuffer, 0, myRead);
                }

                /* Close the streams */
                myInput.close();
                myOutBuffer.close();

            } catch (OceanusException e) {
                throw e;
            } catch (IOException e) {
                throw new GordianIOException("Failed to extract Zip File", e);
            }
        }
    }
}
