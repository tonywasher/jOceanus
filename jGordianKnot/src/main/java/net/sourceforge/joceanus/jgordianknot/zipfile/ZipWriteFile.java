/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.zipfile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.sourceforge.joceanus.jdatamanager.DataConverter;
import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jgordianknot.AsymmetricKey;
import net.sourceforge.joceanus.jgordianknot.CipherMode;
import net.sourceforge.joceanus.jgordianknot.PasswordHash;
import net.sourceforge.joceanus.jgordianknot.SecurityGenerator;
import net.sourceforge.joceanus.jgordianknot.StreamKey;
import net.sourceforge.joceanus.jgordianknot.StreamKeyType;
import net.sourceforge.joceanus.jgordianknot.SymKeyType;
import net.sourceforge.joceanus.jgordianknot.SymmetricKey;

/**
 * Class used to build a ZipFile.
 */
public class ZipWriteFile {
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
    private final PasswordHash theHash;

    /**
     * Security Generator for this zip file.
     */
    private final SecurityGenerator theGenerator;

    /**
     * The number of encryption steps.
     */
    private final int theNumEncrypts;

    /**
     * AsymmetricKey for this zip file.
     */
    private final AsymmetricKey theAsymKey;

    /**
     * The underlying Zip output stream.
     */
    private ZipOutputStream theStream = null;

    /**
     * The list of contents.
     */
    private final ZipFileContents theContents;

    /**
     * The active zipEntry.
     */
    private ZipEntry theEntry = null;

    /**
     * The active zipFileEntry.
     */
    private ZipFileEntry theFileEntry = null;

    /**
     * The active filename.
     */
    private String theFileName = null;

    /**
     * The active output stream.
     */
    private OutputStream theOutput = null;

    /**
     * The fileNumber.
     */
    private int theFileNo = 0;

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
    public ZipFileContents getContents() {
        return theContents;
    }

    /**
     * Obtain the currently active ZipFileEntry.
     * @return the ZipFile Entry
     */
    public ZipFileEntry getCurrentEntry() {
        return theFileEntry;
    }

    /**
     * Constructor for new output zip file with security.
     * @param pHash the password hash to use
     * @param pFile the file details for the new zip file
     * @throws JDataException on error
     */
    public ZipWriteFile(final PasswordHash pHash,
                        final File pFile) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Record hash and generator */
            theHash = pHash;
            theGenerator = theHash.getSecurityGenerator();

            /* reSeed the random number generator */
            theGenerator.reSeedRandom();

            /* Create an Elliptic asymmetric key */
            theAsymKey = theGenerator.generateEllipticAsymmetricKey();
            theNumEncrypts = theGenerator.getNumCipherSteps();

            /* Create the output streams */
            FileOutputStream myOutFile = new FileOutputStream(pFile);
            BufferedOutputStream myOutBuffer = new BufferedOutputStream(myOutFile);
            theStream = new ZipOutputStream(myOutBuffer);

            /*
             * Set compression level to zero to speed things up. It would be nice to use the STORED method, but this requires calculating the CRC and file size
             * prior to writing data to the Zip file which will badly affect performance.
             */
            theStream.setLevel(ZipOutputStream.STORED);

            /* Create the file contents */
            theContents = new ZipFileContents();

            /* Catch exceptions */
        } catch (IOException e) {
            throw new JDataException(ExceptionClass.DATA, ERROR_CREATE, e);
        }
    }

    /**
     * Constructor for new output zip file with no security.
     * @param pFile the file details for the new zip file
     * @throws JDataException on error
     */
    public ZipWriteFile(final File pFile) throws JDataException {
        /* Protect against exceptions */
        try {
            /* record the password hash */
            theHash = null;
            theGenerator = null;
            theAsymKey = null;
            theNumEncrypts = -1;

            /* Create the output streams */
            FileOutputStream myOutFile = new FileOutputStream(pFile);
            BufferedOutputStream myOutBuffer = new BufferedOutputStream(myOutFile);
            theStream = new ZipOutputStream(myOutBuffer);

            /* Create the file contents */
            theContents = new ZipFileContents();

            /* Catch exceptions */
        } catch (IOException e) {
            throw new JDataException(ExceptionClass.DATA, ERROR_CREATE, e);
        }
    }

    /**
     * Obtain an output stream for an entry in the zip file.
     * @param pFile the file details for the new zip entry
     * @return the output stream
     * @throws JDataException on error
     */
    public OutputStream getOutputStream(final File pFile) throws JDataException {
        /* Reject call if we have closed the stream */
        if (theStream == null) {
            throw new JDataException(ExceptionClass.LOGIC, "ZipFile is closed");
        }

        /* Reject call if we have an open stream */
        if (theOutput != null) {
            throw new JDataException(ExceptionClass.LOGIC, "Output stream already open");
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
            theOutput = new WrapOutputStream();

            /* If we are encrypting */
            if (isEncrypted()) {
                /* Create an initial digest stream */
                DigestOutputStream myDigest = new DigestOutputStream(theGenerator.generateDigest(), theOutput);
                theOutput = myDigest;

                /* Generate a list of encryption types */
                SymKeyType[] mySymKeyTypes = theGenerator.generateSymKeyTypes();
                CipherMode[] myModes = CipherMode.values();
                StreamKeyType[] myStreamKeyTypes = StreamKeyType.getRandomTypes(1, theGenerator.getRandom());

                /* For each encryption stream */
                for (int iEncrypt = 0; iEncrypt < theNumEncrypts; iEncrypt++) {
                    /* Create the encryption stream */
                    SymmetricKey myKey = theGenerator.generateSymmetricKey(mySymKeyTypes[iEncrypt]);
                    EncryptionOutputStream myEncrypt = new EncryptionOutputStream(myKey, myModes[iEncrypt], theOutput);
                    theOutput = myEncrypt;
                }

                /* Create the encryption stream for a stream key */
                StreamKey myKey = theGenerator.generateStreamKey(myStreamKeyTypes[0]);
                EncryptionOutputStream myEncrypt = new EncryptionOutputStream(myKey, theOutput);
                theOutput = myEncrypt;

                /* Attach an LZMA output stream onto the output */
                LZMAOutputStream myZip = new LZMAOutputStream(theOutput);
                theOutput = myZip;

                /* Create a final mac stream */
                MacOutputStream myMac = new MacOutputStream(theGenerator.generateMac(), theOutput);
                theOutput = myMac;
            }

            /* Catch exceptions */
        } catch (IOException e) {
            throw new JDataException(ExceptionClass.DATA, "Exception creating new Output stream", e);
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
                    /* Analyse the stream */
                    theFileEntry.analyseOutputStream(theOutput, theAsymKey);
                }

                /* Release the entry */
                theEntry = null;
                theFileName = null;
                theFileEntry = null;
            }

            /* Reset streams */
            theOutput = null;

            /* Catch exceptions */
        } catch (JDataException e) {
            throw new IOException(e);
        }
    }

    /**
     * Close the Zip file and write the header.
     * @throws IOException on error
     */
    public void close() throws IOException {
        /* Close any open output stream */
        closeOutputStream();

        /* If the stream is open */
        if (theStream != null) {
            /* Protect against exceptions */
            try {
                /* If we have stored files and are encrypted */
                if ((theFileNo > 0)
                    && (isEncrypted())) {
                    /* Create a new zipFileEntry */
                    ZipFileEntry myEntry = theContents.addZipFileHeader();

                    /* Add the public key properties */
                    myEntry.setPublicKey(theAsymKey.getExternalPublic());

                    /* Add the wrapped private key property */
                    myEntry.setPrivateKey(theHash.securePrivateKey(theAsymKey));

                    /* Create the header entry */
                    ++theFileNo;
                    theEntry = new ZipEntry(FILE_PREFIX
                                            + theFileNo);

                    /* Declare the password hash and encrypt the header */
                    theEntry.setExtra(theHash.getHashBytes());

                    /* Start the new entry */
                    theStream.putNextEntry(theEntry);

                    /* Declare the details */
                    myEntry.setZipEntry(theEntry);

                    /* Access the encoded file string */
                    String myHeader = theContents.encodeContents();

                    /* Write the bytes to the zip file and close the entry */
                    byte[] myBytes = DataConverter.stringToByteArray(myHeader);
                    theStream.write(theHash.encryptBytes(myBytes));
                    theStream.closeEntry();
                }

                /* close the stream */
                theStream.flush();
                theStream.close();
                theStream = null;

                /* Catch exceptions */
            } catch (JDataException e) {
                throw new IOException(e);
            }
        }
    }

    /**
     * Wrapper class to catch close of output stream and prevent it from closing the ZipFile.
     */
    private final class WrapOutputStream
            extends OutputStream {
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
