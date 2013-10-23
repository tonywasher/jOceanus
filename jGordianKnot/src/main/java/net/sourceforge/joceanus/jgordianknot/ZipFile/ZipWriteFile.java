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
package net.sourceforge.jOceanus.jGordianKnot.ZipFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Signature;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jGordianKnot.AsymmetricKey;
import net.sourceforge.jOceanus.jGordianKnot.MsgDigest;
import net.sourceforge.jOceanus.jGordianKnot.PasswordHash;
import net.sourceforge.jOceanus.jGordianKnot.SecurityGenerator;
import net.sourceforge.jOceanus.jGordianKnot.SymKeyType;
import net.sourceforge.jOceanus.jGordianKnot.SymmetricKey;

/**
 * Class used to build a ZipFile.
 */
public class ZipWriteFile {
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
     * The compressed output stream.
     */
    private DigestOutputStream[] theDigests = null;

    /**
     * The encryption output stream.
     */
    private EncryptionOutputStream[] theEncrypts = null;

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
        FileOutputStream myOutFile;
        BufferedOutputStream myOutBuffer;

        /* Protect against exceptions */
        try {
            /* Record hash and generator */
            theHash = pHash;
            theGenerator = theHash.getSecurityGenerator();

            /* reSeed the random number generator */
            theGenerator.reSeedRandom();

            /* Create an asymmetric key */
            theAsymKey = theGenerator.generateAsymmetricKey();
            theNumEncrypts = theGenerator.getNumCipherSteps();

            /* Create the output streams */
            myOutFile = new FileOutputStream(pFile);
            myOutBuffer = new BufferedOutputStream(myOutFile);
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
            throw new JDataException(ExceptionClass.DATA, "Exception creating new Zip file", e);
        }
    }

    /**
     * Constructor for new output zip file with no security.
     * @param pFile the file details for the new zip file
     * @throws JDataException on error
     */
    public ZipWriteFile(final File pFile) throws JDataException {
        FileOutputStream myOutFile;
        BufferedOutputStream myOutBuffer;

        /* Protect against exceptions */
        try {
            /* record the password hash */
            theHash = null;
            theGenerator = null;
            theAsymKey = null;
            theNumEncrypts = -1;

            /* Create the output streams */
            myOutFile = new FileOutputStream(pFile);
            myOutBuffer = new BufferedOutputStream(myOutFile);
            theStream = new ZipOutputStream(myOutBuffer);

            /* Create the file contents */
            theContents = new ZipFileContents();

            /* Catch exceptions */
        } catch (IOException e) {
            throw new JDataException(ExceptionClass.DATA, "Exception creating new Zip file", e);
        }
    }

    /**
     * Obtain a debug output stream for an entry in the zip file.
     * @param pFile the file details for the new zip entry
     * @return the output stream
     * @throws JDataException on error
     */
    public OutputStream getDebugOutputStream(final File pFile) throws JDataException {
        /* Obtain debug output stream */
        return getOutputStream(pFile, true);
    }

    /**
     * Obtain a standard output stream for an entry in the zip file.
     * @param pFile the file details for the new zip entry
     * @return the output stream
     * @throws JDataException on error
     */
    public OutputStream getOutputStream(final File pFile) throws JDataException {
        /* Obtain standard output stream */
        return getOutputStream(pFile, false);
    }

    /**
     * Obtain an output stream for an entry in the zip file.
     * @param pFile the file details for the new zip entry
     * @param pDebug is this entry to be debugged
     * @return the output stream
     * @throws JDataException on error
     */
    private OutputStream getOutputStream(final File pFile,
                                         final boolean pDebug) throws JDataException {
        LZMAOutputStream myZip;
        DigestOutputStream myDigest;
        EncryptionOutputStream myEncrypt;
        int iDigest;
        int iEncrypt;

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

            /* Add debug indication if required */
            if (pDebug) {
                theFileEntry.setDebug();
            }

            /* Simply create a wrapper on the output stream */
            theOutput = new WrapOutputStream();

            /* If we are encrypting */
            if (isEncrypted()) {
                /* Determine number of digests */
                int iNumDigest = 2 + (pDebug
                        ? theNumEncrypts
                        : 0);

                /* Create the arrays */
                theDigests = new DigestOutputStream[iNumDigest];
                theEncrypts = new EncryptionOutputStream[theNumEncrypts];
                iDigest = 0;

                /* Create an initial digest stream */
                myDigest = new DigestOutputStream(new MsgDigest(theGenerator), theOutput);
                theOutput = myDigest;
                theDigests[iDigest++] = myDigest;

                /* Generate a list of encryption types */
                SymKeyType[] myTypes = theGenerator.generateSymKeyTypes();

                /* For each encryption stream */
                for (iEncrypt = 0; iEncrypt < theNumEncrypts; iEncrypt++) {
                    /* Create the encryption stream */
                    SymmetricKey myKey = theGenerator.generateSymmetricKey(myTypes[iEncrypt]);
                    myEncrypt = new EncryptionOutputStream(myKey, theOutput);
                    theOutput = myEncrypt;
                    theEncrypts[iEncrypt] = myEncrypt;

                    /* if we are debugging */
                    if (pDebug) {
                        /* Create an extra digest stream */
                        myDigest = new DigestOutputStream(new MsgDigest(theGenerator), theOutput);
                        theOutput = myDigest;
                        theDigests[iDigest++] = myDigest;
                    }
                }

                /* Attach an LZMA output stream onto the output */
                myZip = new LZMAOutputStream(theOutput);
                theOutput = myZip;

                /* Create a final digest stream */
                myDigest = new DigestOutputStream(new MsgDigest(theGenerator), theOutput);
                theOutput = myDigest;
                theDigests[iDigest++] = myDigest;
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
                    /* Record the digests */
                    theFileEntry.setDigests(theDigests);

                    /* Record the secret keys */
                    theFileEntry.setSecretKeys(theEncrypts, theAsymKey);

                    /* Calculate the signature and declare it */
                    Signature mySignature = theAsymKey.getSignature(true);
                    theFileEntry.signEntry(mySignature, false);
                }

                /* Release the entry */
                theEntry = null;
                theFileName = null;
                theFileEntry = null;
            }

            /* Reset streams */
            theOutput = null;
            theDigests = null;
            theEncrypts = null;

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
        String myHeader;

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
                    myEntry.setPublicKey(theAsymKey.getExternalDef());

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
                    myHeader = theContents.encodeContents();

                    /* Write the bytes to the zip file and close the entry */
                    theStream.write(theHash.encryptString(myHeader));
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
