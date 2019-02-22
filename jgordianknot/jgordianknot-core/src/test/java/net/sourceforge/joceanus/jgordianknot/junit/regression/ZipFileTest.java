/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.junit.regression;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.api.impl.GordianGenerator;
import net.sourceforge.joceanus.jgordianknot.api.impl.GordianSecurityManager;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipFactory;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipFileContents;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipFileEntry;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipReadFile;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipWriteFile;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Security Test suite - Zip File.
 */
public class ZipFileTest {
    /**
     * Default password.
     */
    private static final char[] DEF_PASSWORD = "SimplePassword".toCharArray();

    /**
     * Create the zipFile test suite.
     * @return the test stream
     * @throws OceanusException on error
     */
    @TestFactory
    public Stream<DynamicNode> zipFileTests() throws OceanusException {
        /* Create tests */
        Stream<DynamicNode> myStream = zipFileTests(false, GordianFactoryType.BC);
        myStream = Stream.concat(myStream, zipFileTests(true, GordianFactoryType.BC));
        myStream = Stream.concat(myStream, zipFileTests(false, GordianFactoryType.JCA));
        return Stream.concat(myStream, zipFileTests(true, GordianFactoryType.JCA));
    }

    /**
     * Create the keySet test suite for a factory.
     * @param pRestricted is the factory restricted
     * @param pType the factoryType
     * @return the test stream
     * @throws OceanusException on error
     */
    private Stream<DynamicNode> zipFileTests(final boolean pRestricted,
                                             final GordianFactoryType pType) throws OceanusException {
        /* Create the factory */
        final GordianFactory myFactory = GordianGenerator.createFactory(new GordianParameters(pRestricted, pType));

        /* Return the stream */
        final String myName = pType.toString()
                + (pRestricted ? "-Restricted" : "-Full");
        return Stream.of(DynamicContainer.dynamicContainer(myName, Stream.of(
                DynamicTest.dynamicTest("standard", () -> testZipFile(myFactory, false)),
                DynamicTest.dynamicTest("encrypted", () -> testZipFile(myFactory, true))
        )));
    }

    /**
     * Test security.
     * @param pFactory the factory.
     * @param pSecure is the zip file secure?
     * @throws OceanusException on error
     */
    private void testZipFile(final GordianFactory pFactory,
                             final boolean pSecure) throws OceanusException {
        /* Obtain the home directory */
        final String myHome = System.getProperty("user.home");

        /* Run the tests */
        final File myZipFile = new File(myHome, pSecure
                                                ? "TestEncZip" + GordianSecurityManager.SECUREZIPFILE_EXT
                                                : "TestStdZip" + GordianSecurityManager.ZIPFILE_EXT);
        final File myDirectory = new File(myHome, "tester");
        createZipFile(pFactory, myZipFile, myDirectory, pSecure);
        extractZipFile(pFactory, myZipFile, myDirectory);
    }

    /**
     * Create a Zip File of files in a directory.
     * @param pFactory the factory to use
     * @param pZipFile the name of the zip file to create
     * @param pDirectory the directory to archive
     * @param bSecure encrypt the zip file (true/false)
     * @throws OceanusException on error
     */
    private void createZipFile(final GordianFactory pFactory,
                               final File pZipFile,
                               final File pDirectory,
                               final boolean bSecure) throws OceanusException {
        /* Protect against exceptions */
        try (GordianZipWriteFile myZipFile = createZipFile(pFactory, pZipFile, bSecure)) {
            /* Make sure that we have a directory */
            if (!pDirectory.isDirectory()) {
                throw new GordianTestException("Invalid source directory");
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
                     OutputStream myOutput = myZipFile.createOutputStream(new File(myFile.getName()), true)) {
                    /* Copy file content to zip file */
                    myInBuffer.transferTo(myOutput);

                } catch (IOException e) {
                    throw new GordianTestException("Failed to create Zip File", e);
                }
            }

        } catch (IOException e) {
            throw new GordianTestException("Failed to create Zip File", e);
        }
    }

    /**
     * Create a Zip File of files in a directory.
     * @param pFactory the factory to use
     * @param pZipFile the name of the zip file to create
     * @param bSecure encrypt the zip file (true/false)
     * @return the new zip file
     * @throws OceanusException on error
     */
    private GordianZipWriteFile createZipFile(final GordianFactory pFactory,
                                              final File pZipFile,
                                              final boolean bSecure) throws OceanusException {
        /* Access ZipManager */
        final GordianZipFactory myZipMgr = pFactory.getZipFactory();
        final GordianKeySetFactory myKeySets = pFactory.getKeySetFactory();

        /* If we are creating a secure zip file */
        if (bSecure) {
            /* Create new Password Hash */
            final GordianKeySetHash myHash = myKeySets.generateKeySetHash(DEF_PASSWORD.clone());

            /* Initialise the Zip file */
            return myZipMgr.createZipFile(myHash, pZipFile);

            /* else */
        } else {
            /* Just create a standard zip file */
            return myZipMgr.createZipFile(pZipFile);
        }
    }

    /**
     * Extract a Zip File and compare to a directory.
     * @param pZipFile the name of the zip file to extract from
     * @param pDirectory the directory to compare against
     * @throws OceanusException on error
     */
    private void extractZipFile(final GordianFactory pFactory,
                                final File pZipFile,
                                final File pDirectory) throws OceanusException {
        /* Access ZipManager */
        final GordianZipFactory myZipMgr = pFactory.getZipFactory();
        final GordianKeySetFactory myKeySets = pFactory.getKeySetFactory();

        /* Access the file */
        final GordianZipReadFile myZipFile = myZipMgr.openZipFile(pZipFile);

        /* Check for security */
        final byte[] myHashBytes = myZipFile.getHashBytes();
        if (myHashBytes != null) {
            /* Resolve security and unlock file */
            final GordianKeySetHash myHash = myKeySets.deriveKeySetHash(myHashBytes, DEF_PASSWORD.clone());
            myZipFile.setKeySetHash(myHash);
        }

        /* Access the contents */
        final GordianZipFileContents myContents = myZipFile.getContents();
        final List<File> myFiles = new ArrayList<>(Arrays.asList(pDirectory.listFiles()));
        myFiles.removeIf(File::isDirectory);

        /* Loop through the entries */
        final Iterator<GordianZipFileEntry> myIterator = myContents.iterator();
        while (myIterator.hasNext()) {
            /* Access next entry */
            final GordianZipFileEntry myEntry = myIterator.next();
            final File myFile = findFileForEntry(myFiles, myEntry.getFileName());

            /* Protect against exceptions */
            try (InputStream myZipInput = myZipFile.createInputStream(myEntry);
                 InputStream myInFile = new FileInputStream(myFile);
                 InputStream myInBuffer = new BufferedInputStream(myInFile)) {
                /* Read the entries */
                final byte[] myZipData = myZipInput.readAllBytes();
                final byte[] myFileData = myInBuffer.readAllBytes();
                Assertions.assertArrayEquals(myZipData, myFileData, "File MisMatch");

            } catch (IOException e) {
                throw new GordianTestException("Failed to extract Zip File", e);
            }
        }

        /* File list should be empty */
        Assertions.assertTrue(myFiles.isEmpty(), "Excess files");
    }

    /**
     * Find file for entry.
     * @param pFiles the file list
     * @param pFileName the file name
     * @return the found file
     */
    private File findFileForEntry(final List<File> pFiles,
                                  final String pFileName) {
        Iterator<File> myIterator = pFiles.iterator();
        while (myIterator.hasNext()) {
            final File myFile = myIterator.next();
            if (pFileName.equals(myFile.getName())) {
                myIterator.remove();
                return myFile;
            }
        }
        Assertions.fail("File not found");
        return null;
    }

    /**
     * Cryptography Exception.
     */
    public static class GordianTestException
            extends OceanusException {
        /**
         * SerialId.
         */
        private static final long serialVersionUID = 5856242835104016341L;

        /**
         * Create a new GordianKnot Exception object based on a string.
         * @param s the description of the exception
         */
        GordianTestException(final String s) {
            super(s);
        }

        /**
         * Create a new GordianKnot Exception object based on a string and an underlying exception.
         * @param s the description of the exception
         * @param e the underlying exception
         */
        GordianTestException(final String s,
                             final Throwable e) {
            super(s, e);
        }
    }
}
