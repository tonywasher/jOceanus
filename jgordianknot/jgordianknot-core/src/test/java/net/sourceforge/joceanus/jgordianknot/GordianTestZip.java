/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import net.sourceforge.joceanus.jgordianknot.GordianTestSuite.SecurityManagerCreator;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
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
        GordianZipWriteFile myZipFile;

        try {
            /* If we are creating a secure zip file */
            if (bSecure) {
                /* Create new Password Hash */
                GordianHashManager myManager = theCreator.newSecureManager();
                GordianKeySetHash myHash = myManager.resolveKeySetHash(null, "New");

                /* Initialise the Zip file */
                myZipFile = new GordianZipWriteFile(myHash, pZipFile);

                /* else */
            } else {
                /* Just create a standard zip file */
                myZipFile = new GordianZipWriteFile(pZipFile);
            }

            /* Create a read buffer */
            int myBufLen = BUFFER_LEN;
            byte[] myBuffer = new byte[myBufLen];
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
                InputStream myInFile = new FileInputStream(myFile);
                InputStream myInBuffer = new BufferedInputStream(myInFile);

                /* Open the output stream */
                OutputStream myOutput = myZipFile.getOutputStream(new File(myFile.getName()));

                /* Read the header entry */
                while ((myRead = myInBuffer.read(myBuffer, 0, myBufLen)) != -1) {
                    /* Write the data to the zip file */
                    myOutput.write(myBuffer, 0, myRead);
                }

                /* Close the streams */
                myOutput.close();
                myInBuffer.close();
            }

            /* Close the Zip File */
            myZipFile.close();

            /* Return the zip file entries */
            return myZipFile.getContents();

        } catch (OceanusException e) {
            throw e;
        } catch (Exception e) {
            throw new GordianIOException("Failed to create Zip File", e);
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
        /* Protect against exceptions */
        try {
            /* Access the file */
            GordianZipReadFile myZipFile = new GordianZipReadFile(pZipFile);

            /* Check for security */
            byte[] myHashBytes = myZipFile.getHashBytes();
            if (myHashBytes != null) {
                /* Resolve security and unlock file */
                GordianHashManager myManager = theCreator.newSecureManager();
                GordianKeySetHash myHash = myManager.resolveKeySetHash(myHashBytes, pZipFile.getName());
                myZipFile.setKeySetHash(myHash);
            }

            /* Access the contents */
            GordianZipFileContents myContents = myZipFile.getContents();

            /* Create a read buffer */
            int myBufLen = BUFFER_LEN;
            byte[] myBuffer = new byte[myBufLen];
            int myRead;

            /* Make sure that we have a directory */
            if (!pDirectory.isDirectory()) {
                throw new GordianDataException("Invalid target directory");
            }

            /* Loop through the entries */
            Iterator<GordianZipFileEntry> myIterator = myContents.iterator();
            while (myIterator.hasNext()) {
                /* Access next entry */
                GordianZipFileEntry myEntry = myIterator.next();

                /* Open the input stream */
                InputStream myInput = myZipFile.getInputStream(myEntry);

                /* Open the output file for writing */
                OutputStream myOutFile = new FileOutputStream(new File(pDirectory, myEntry.getFileName()));
                OutputStream myOutBuffer = new BufferedOutputStream(myOutFile);

                /* Read the entry */
                while ((myRead = myInput.read(myBuffer, 0, myBufLen)) != -1) {
                    /* Write the data to the new file */
                    myOutBuffer.write(myBuffer, 0, myRead);
                }

                /* Close the streams */
                myInput.close();
                myOutBuffer.close();
            }

        } catch (OceanusException e) {
            throw e;
        } catch (Exception e) {
            throw new GordianIOException("Failed to extract Zip File", e);
        }
    }
}
