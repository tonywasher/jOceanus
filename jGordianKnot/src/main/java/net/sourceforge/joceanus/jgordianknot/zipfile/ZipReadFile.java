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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Signature;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jgordianknot.AsymmetricKey;
import net.sourceforge.joceanus.jgordianknot.DataDigest;
import net.sourceforge.joceanus.jgordianknot.DigestType;
import net.sourceforge.joceanus.jgordianknot.PasswordHash;
import net.sourceforge.joceanus.jgordianknot.SecurityGenerator;
import net.sourceforge.joceanus.jgordianknot.SymmetricKey;

/**
 * Class used to extract from a ZipFile.
 */
public class ZipReadFile {
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
    private ZipFileContents theContents;

    /**
     * The name of the Zip file.
     */
    private File theZipFile = null;

    /**
     * The Header input stream.
     */
    private ZipInputStream theHdrStream = null;

    /**
     * AsymmetricKey for this zip file.
     */
    private AsymmetricKey theAsymKey = null;

    /**
     * Security Generator for this zip file.
     */
    private SecurityGenerator theGenerator = null;

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
    public ZipFileContents getContents() {
        return theContents;
    }

    /**
     * Obtain the hash bytes for the file.
     * @return the hash bytes
     */
    public byte[] getHashBytes() {
        return Arrays.copyOf(theHashBytes, theHashBytes.length);
    }

    /**
     * Constructor.
     * @param pFile the file to read
     * @throws JDataException on error
     */
    public ZipReadFile(final File pFile) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Store the zipFile name */
            theZipFile = new File(pFile.getPath());

            /* Create the file contents */
            theContents = new ZipFileContents();

            /* Open the zip file for reading */
            FileInputStream myInFile = new FileInputStream(pFile);
            BufferedInputStream myInBuffer = new BufferedInputStream(myInFile);
            theHdrStream = new ZipInputStream(myInBuffer);

            /* Loop through the Zip file entries */
            ZipEntry myEntry;
            for (;;) {
                /* Read next entry */
                myEntry = theHdrStream.getNextEntry();

                /* If this is EOF or a header record break the loop */
                if ((myEntry == null)
                    || (myEntry.getExtra() != null)) {
                    break;
                }

                /* Add to list of contents */
                theContents.addZipFileEntry(myEntry);
            }

            /* Pick up security key if it is present */
            theHashBytes = (myEntry == null)
                    ? null
                    : myEntry.getExtra();

            /* Catch exceptions */
        } catch (IOException e) {
            throw new JDataException(ExceptionClass.DATA, "Exception accessing Zip file", e);
        }
    }

    /**
     * Set the password hash.
     * @param pHash the password hash
     * @throws JDataException on error
     */
    public void setPasswordHash(final PasswordHash pHash) throws JDataException {
        /* Ignore if we have no security */
        if (!isEncrypted()) {
            return;
        }

        /* Protect against exceptions */
        try {
            /* Reject this is the wrong security control */
            if (!Arrays.equals(pHash.getHashBytes(), theHashBytes)) {
                throw new JDataException(ExceptionClass.LOGIC, "Password Hash does not match ZipFile Security.");
            }

            /* Store the hash and obtain the generator */
            PasswordHash myHash = pHash;
            theGenerator = myHash.getSecurityGenerator();

            /* Initialise variables */
            int myLen = 0;
            int mySpace = BUFFERSIZE;
            byte[] myBuffer = new byte[BUFFERSIZE];

            /* Loop */
            for (;;) {
                /* Read the header entry */
                int myRead = theHdrStream.read(myBuffer, myLen, mySpace);
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
            myBuffer = Arrays.copyOf(myBuffer, myLen);

            /* Parse the decrypted header */
            theContents = new ZipFileContents(myHash.decryptString(myBuffer));

            /* Access the security details */
            ZipFileEntry myHeader = theContents.getHeader();

            /* Reject if the entry is not found */
            if (myHeader == null) {
                throw new JDataException(ExceptionClass.LOGIC, "Header record not found.");
            }

            /* Obtain encoded private/public keys */
            byte[] myPublic = myHeader.getPublicKey();
            byte[] myPrivate = myHeader.getPrivateKey();

            /* Obtain the asymmetric key */
            theAsymKey = myHash.deriveAsymmetricKey(myPrivate, myPublic);

            /* Catch exceptions */
        } catch (IOException e) {
            throw new JDataException(ExceptionClass.DATA, "Exception reading header of Zip file", e);
        } finally {
            /* Close the file */
            try {
                if (theHdrStream != null) {
                    theHdrStream.close();
                }
            } catch (IOException e) {
                theHdrStream = null;
            }
            theHdrStream = null;
        }
    }

    /**
     * Obtain an input stream for an entry in the zip file.
     * @param pFile the file details for the new zip entry
     * @return the input stream
     * @throws JDataException on error
     */
    public InputStream getInputStream(final ZipFileEntry pFile) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Check that entry belongs to this zip file */
            if (pFile.getParent() != theContents) {
                throw new JDataException(ExceptionClass.DATA, "File does not belong to Zip file");
            }

            /* Open the zip file for reading */
            FileInputStream myInFile = new FileInputStream(theZipFile);
            BufferedInputStream myInBuffer = new BufferedInputStream(myInFile);
            ZipInputStream myZipFile = new ZipInputStream(myInBuffer);

            /* Access the name of the file entry */
            String myName = pFile.getZipName();
            ZipEntry myEntry;

            /* Loop through the Zip file entries */
            for (;;) {
                /* Read the entry */
                myEntry = myZipFile.getNextEntry();
                /* Break if we reached EOF or found the correct entry */
                if ((myEntry == null)
                    || (myEntry.getName().compareTo(myName) == 0)) {
                    break;
                }
            }

            /* Handle entry not found */
            if (myEntry == null) {
                myZipFile.close();
                throw new JDataException(ExceptionClass.DATA, "File not found - "
                                                              + pFile.getFileName());
            }

            /* Note the current input stream */
            InputStream myCurrent = myZipFile;
            int iDigest = 0;

            /* If the file is encrypted */
            if (isEncrypted()) {
                /* Verify Encryption details */
                Signature mySignature = theAsymKey.getSignature(false);
                pFile.signEntry(mySignature, true);

                /* Access the digests */
                byte[][] myDigests = pFile.getDigests();
                // long[] myDigestLens = pFile.getDigestLens();

                /* Access the secretKeys */
                byte[][] mySecretKeys = pFile.getSecretKeys();
                byte[][] myInitVectors = pFile.getInitVectors();

                /* Wrap a digest input stream around the zip file */
                DataDigest myDataDigest = theGenerator.generateDigest(DigestType.SHA3);
                myCurrent = new DigestInputStream(myDataDigest, myDigests[iDigest], myCurrent);

                /* For each decryption stream */
                for (int iDecrypt = 0; iDecrypt < mySecretKeys.length; iDecrypt++) {
                    /* Create the decryption stream */
                    SymmetricKey myKey = theAsymKey.deriveSymmetricKey(mySecretKeys[iDecrypt]);
                    myCurrent = new DecryptionInputStream(myKey, myInitVectors[iDecrypt], myCurrent);
                }

                /* Wrap a LZMAInputStream around the stream */
                myCurrent = new LZMAInputStream(myCurrent);

                /* Wrap a digest input stream around the stream */
                myDataDigest = theGenerator.generateDigest(DigestType.SHA3);
                myCurrent = new DigestInputStream(myDataDigest, myDigests[iDigest], myCurrent);
            }

            /* return the new stream */
            return myCurrent;

            /* Catch exceptions */
        } catch (IOException e) {
            throw new JDataException(ExceptionClass.DATA, "Exception creating new Input stream", e);
        }
    }
}
