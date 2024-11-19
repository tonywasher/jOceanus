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
package net.sourceforge.joceanus.gordianknot.junit.regression;

import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianMLKEMSpec;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianPasswordLockSpec;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipFactory;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipFileContents;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipFileEntry;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipLock;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipReadFile;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipWriteFile;
import net.sourceforge.joceanus.gordianknot.util.GordianGenerator;
import net.sourceforge.joceanus.tethys.OceanusException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

/**
 * Security Test suite - Zip File.
 */
class ZipFileTest {
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
    Stream<DynamicNode> zipFileTests() throws OceanusException {
        /* Create tests */
        Stream<DynamicNode> myStream = zipFileTests(GordianFactoryType.BC);
        return Stream.concat(myStream, zipFileTests(GordianFactoryType.JCA));
    }

    /**
     * Create the keySet test suite for a factory.
     * @param pType the factoryType
     * @return the test stream
     * @throws OceanusException on error
     */
    private Stream<DynamicNode> zipFileTests(final GordianFactoryType pType) throws OceanusException {
        /* Create the factory */
        final GordianFactory myFactory = GordianGenerator.createFactory(pType);

        /* Create the keyPair */
        final GordianKeyPairFactory myAsymFactory = myFactory.getKeyPairFactory();
        GordianKeyPairGenerator myPairGenerator = myAsymFactory.getKeyPairGenerator(GordianKeyPairSpecBuilder.x448());
        final GordianKeyPair myKeyPair1 = myPairGenerator.generateKeyPair();
        myPairGenerator = myAsymFactory.getKeyPairGenerator(GordianKeyPairSpecBuilder.mlkem(GordianMLKEMSpec.MLKEM512));
        final GordianKeyPair myKeyPair2 = myPairGenerator.generateKeyPair();

        /* Return the stream */
        final String myName = pType.toString();
        return Stream.of(DynamicContainer.dynamicContainer(myName, Stream.of(
                DynamicTest.dynamicTest("standard", () -> testZipFile(myFactory, null, null)),
                DynamicContainer.dynamicContainer("encrypted128", lockedZipFileTests(myFactory, myKeyPair1, myKeyPair2, GordianLength.LEN_128)),
                DynamicContainer.dynamicContainer("encrypted192", lockedZipFileTests(myFactory, myKeyPair1, myKeyPair2, GordianLength.LEN_192)),
                DynamicContainer.dynamicContainer("encrypted256", lockedZipFileTests(myFactory, myKeyPair1, myKeyPair2, GordianLength.LEN_256))
        )));
    }

    /**
     * Test security.
     * @param pFactory the factory.
     * @param pKeyPair1 the first keyPair
     * @param pKeyPair2 the second keyPair
     * @param pKeyLen the keyLength
     * @throws OceanusException on error
     */
    private Stream<DynamicNode> lockedZipFileTests(final GordianFactory pFactory,
                                                   final GordianKeyPair pKeyPair1,
                                                   final GordianKeyPair pKeyPair2,
                                                   final GordianLength pKeyLen) throws OceanusException {
        return Stream.of(
                DynamicTest.dynamicTest("keySet/password", () -> testZipFile(pFactory,null, pKeyLen)),
                DynamicTest.dynamicTest("factory/password", () -> testZipFile(pFactory, Boolean.TRUE, pKeyLen)),
                DynamicTest.dynamicTest("keyPair1/password", () -> testZipFile(pFactory, pKeyPair1, pKeyLen)),
                DynamicTest.dynamicTest("keyPair2/password", () -> testZipFile(pFactory, pKeyPair2, pKeyLen))
        );
    }

    /**
     * Test security.
     * @param pFactory the factory.
     * @param pKeyPair the keyPair
     * @param pKeyLen the keyLength (or null)
     * @throws OceanusException on error
     */
    private void testZipFile(final GordianFactory pFactory,
                             final Object pKeyPair,
                             final GordianLength pKeyLen) throws OceanusException {
        /* Obtain the home directory */
        final String myHome = System.getProperty("user.home");

        /* Run the tests */
        final File myDirectory = new File(myHome, "tester");
        final byte[] myZipFile = createZipFile(pFactory, myDirectory, pKeyPair, pKeyLen);
        extractZipFile(pFactory, myZipFile, pKeyPair, myDirectory);
    }

    /**
     * Create a Zip File of files in a directory.
     * @param pFactory the factory to use
     * @param pDirectory the directory to archive
     * @param pKeyPair the keyPair (if any)
     * @param pKeyLen the keyLength (or null)
     * @return the in-memory ZipFile
     * @throws OceanusException on error
     */
    private byte[] createZipFile(final GordianFactory pFactory,
                                 final File pDirectory,
                                 final Object pKeyPair,
                                 final GordianLength pKeyLen) throws OceanusException {
        /* Protect against exceptions */
        final ByteArrayOutputStream myZipStream = new ByteArrayOutputStream();
        try (GordianZipWriteFile myZipFile = createZipFile(pFactory, myZipStream, pKeyPair, pKeyLen)) {
            /* Make sure that we have a directory */
            final File[] myFiles = pDirectory.listFiles();
            if (!pDirectory.isDirectory() || myFiles == null) {
                throw new GordianTestException("Invalid source directory");
            }

            /* Loop through the files in the directory */
            for (File myFile : myFiles) {
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


        /* Return the ZipFile */
        return myZipStream.toByteArray();
    }

    /**
     * Create a Zip File of files in a directory.
     * @param pFactory the factory to use
     * @param pZipStream the output stream to write the ZipFile to
     * @param pKeyPair the keyPair (if any)
     * @param pKeyLen the keyLength (or null)
     * @return the new zip file
     * @throws OceanusException on error
     */
    private GordianZipWriteFile createZipFile(final GordianFactory pFactory,
                                              final OutputStream pZipStream,
                                              final Object pKeyPair,
                                              final GordianLength pKeyLen) throws OceanusException {
        /* Access ZipManager */
        final GordianZipFactory myZipFactory = pFactory.getZipFactory();

        /* If we are creating a secure zip file */
        if (pKeyLen != null) {
            /* Create new zipLock */
            final GordianZipLock myLock = createZipLock(myZipFactory, pKeyPair, pKeyLen);

            /* Initialise the Zip file */
            return myZipFactory.createZipFile(myLock, pZipStream);

            /* else */
        } else {
            /* Just create a standard zip file */
            return myZipFactory.createZipFile(pZipStream);
        }
    }

    /**
     * Create an appropriate zipLock.
     * @param pFactory the factory to use
     * @param pKeyPair the keyPair (if any)
     * @param pKeyLen the keyLength
     * @return the new zip file
     * @throws OceanusException on error
     */
    private GordianZipLock createZipLock(final GordianZipFactory pFactory,
                                         final Object pKeyPair,
                                         final GordianLength pKeyLen) throws OceanusException {
        /* Create appropriate zipLock */
        final GordianPasswordLockSpec mySpec = new GordianPasswordLockSpec(new GordianKeySetSpec(pKeyLen));
        if (pKeyPair instanceof GordianKeyPair) {
            return pFactory.keyPairZipLock(mySpec, (GordianKeyPair) pKeyPair, DEF_PASSWORD.clone());
        }
        if (pKeyPair instanceof Boolean) {
            return pFactory.factoryZipLock(mySpec, DEF_PASSWORD.clone());
        }
        return pFactory.keySetZipLock(mySpec, DEF_PASSWORD.clone());
    }

    /**
     * Extract a Zip File and compare to a directory.
     * @param pZipFile the in-memory Zip file
     * @param pKeyPair the keyPair (if any)
     * @param pDirectory the directory to compare against
     * @throws OceanusException on error
     */
    private void extractZipFile(final GordianFactory pFactory,
                                final byte[] pZipFile,
                                final Object pKeyPair,
                                final File pDirectory) throws OceanusException {
        /* Access ZipManager */
        final GordianZipFactory myZipMgr = pFactory.getZipFactory();

        /* Access the file */
        final ByteArrayInputStream myInputStream = new ByteArrayInputStream(pZipFile);
        final GordianZipReadFile myZipFile = myZipMgr.openZipFile(myInputStream);

        /* Check for security */
        final GordianZipLock myLock = myZipFile.getLock();
        if (myLock != null) {
            /* switch on lockType */
            switch (myLock.getLockType()) {
                case KEYSET_PASSWORD:
                case FACTORY_PASSWORD:
                    myLock.unlock(DEF_PASSWORD.clone());
                    break;
                case KEYPAIR_PASSWORD:
                default:
                    myLock.unlock((GordianKeyPair) pKeyPair, DEF_PASSWORD.clone());
                    break;
            }
        }

        /* Access the contents */
        final GordianZipFileContents myContents = myZipFile.getContents();
        final File[] myDirFiles = pDirectory.listFiles();
        Assertions.assertNotNull(myDirFiles, "Invalid directory");
        final List<File> myFiles = new ArrayList<>(Arrays.asList(myDirFiles));
        myFiles.removeIf(File::isDirectory);

        /* Loop through the entries */
        final Iterator<GordianZipFileEntry> myIterator = myContents.iterator();
        while (myIterator.hasNext()) {
            /* Access next entry */
            final GordianZipFileEntry myEntry = myIterator.next();
            final File myFile = findFileForEntry(myFiles, myEntry.getFileName());
            Assertions.assertNotNull(myFile, "File not found");

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
