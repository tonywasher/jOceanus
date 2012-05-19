/*******************************************************************************
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JGordianKnot.ZipFile;

import java.security.Signature;
import java.util.zip.ZipEntry;

import javax.crypto.Mac;

import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.ModelException.ExceptionClass;
import net.sourceforge.JGordianKnot.AsymmetricKey;

public class ZipFileEntry {
    /**
     * The Name property of a file
     */
    private final static String propName = "Name";

    /**
     * The Zip Name property of a file
     */
    private final static String propZipName = "ZipName";

    /**
     * The Digest property name of a file
     */
    private final static String propDigest = "Digest";

    /**
     * The Signature property name of a file
     */
    private final static String propSignature = "Signature";

    /**
     * The SecretKey property name of a file
     */
    private final static String propSecretKey = "SecretKey";

    /**
     * The InitVector property name of a file
     */
    private final static String propInitVector = "InitVector";

    /**
     * The PublicKey property name of a file
     */
    private final static String propPublicKey = "PublicKey";

    /**
     * The PrivateKey property name of a file
     */
    private final static String propPrivateKey = "PrivateKey";

    /**
     * The NumDigests property name of a file
     */
    private final static String propNumDigests = "NumDigests";

    /**
     * The NumEncrypts property name of a file
     */
    private final static String propNumEncrypts = "NumEncrypts";

    /**
     * The Header property name of a file
     */
    private final static String propHeader = "Header";

    /**
     * The Debug property name of a file
     */
    private final static String propDebug = "Debug";

    /**
     * The parent contents
     */
    private ZipFileContents theParent = null;

    /**
     * The file name
     */
    private String theFileName = null;

    /**
     * The zip name
     */
    private String theZipName = null;

    /**
     * The original file size
     */
    private long theFileSize = 0;

    /**
     * The compressed file size
     */
    private long theCompressedSize = 0;

    /**
     * The Secret Keys
     */
    private byte[][] theSecretKeys = null;

    /**
     * The Initialisation vectors
     */
    private byte[][] theInitVectors = null;

    /**
     * The Digests
     */
    private byte[][] theDigests = null;

    /**
     * The Digest lengths
     */
    private long[] theDigestLens = null;

    /**
     * The Signature
     */
    private byte[] theSignature = null;

    /**
     * The Private Key
     */
    private byte[] thePrivateKey = null;

    /**
     * The Public Key
     */
    private byte[] thePublicKey = null;

    /**
     * Is this the header
     */
    private boolean isHeader = false;

    /**
     * Is this entry in debug mode
     */
    private boolean isDebug = false;

    /**
     * Obtain the name of the file
     * @return the name of the file
     */
    public String getFileName() {
        return theFileName;
    }

    /**
     * Obtain the parent of the file
     * @return the parent of the file
     */
    public ZipFileContents getParent() {
        return theParent;
    }

    /**
     * Obtain the zip name of the file
     * @return the zip name of the file
     */
    public String getZipName() {
        return theZipName;
    }

    /**
     * Obtain the original size of a file
     * @return the original size
     */
    public long getSize() {
        return theFileSize;
    }

    /**
     * Obtain the compressed size of a file
     * @return the compressed size
     */
    public long getCompressedSize() {
        return theCompressedSize;
    }

    /**
     * Obtain the digest array
     * @return the digest array
     */
    protected byte[][] getDigests() {
        return theDigests;
    }

    /**
     * Obtain the digest length array
     * @return the digest length array
     */
    protected long[] getDigestLens() {
        return theDigestLens;
    }

    /**
     * Obtain the secret key array
     * @return the secret key array
     */
    protected byte[][] getSecretKeys() {
        return theSecretKeys;
    }

    /**
     * Obtain the initVector array
     * @return the initVector array
     */
    protected byte[][] getInitVectors() {
        return theInitVectors;
    }

    /**
     * Obtain the signature
     * @return the signature
     */
    public byte[] getSignature() {
        return theSignature;
    }

    /**
     * Obtain the private key
     * @return the private key
     */
    protected byte[] getPrivateKey() {
        return thePrivateKey;
    }

    /**
     * Obtain the public key
     * @return the public key
     */
    protected byte[] getPublicKey() {
        return thePublicKey;
    }

    /**
     * Is this entry the header
     * @return true/false
     */
    protected boolean isHeader() {
        return isHeader;
    }

    /**
     * Is this entry in debug mode
     * @return true/false
     */
    protected boolean isDebug() {
        return isDebug;
    }

    /**
     * Standard constructor from filename
     * @param pFileName the file name
     */
    protected ZipFileEntry(String pFileName) {
        /* Store the file name */
        theFileName = pFileName;
    }

    /**
     * Standard constructor from properties
     * @param pProperties the properties
     * @throws ModelException
     */
    protected ZipFileEntry(ZipFileProperties pProperties) throws ModelException {
        /* Access the top-level details */
        theFileName = pProperties.getStringProperty(propName);
        theFileSize = pProperties.getLongProperty(propName);
        theZipName = pProperties.getStringProperty(propZipName);
        theCompressedSize = pProperties.getLongProperty(propZipName);

        /* Determine whether this is a header */
        isHeader = (pProperties.getLongProperty(propHeader) != -1);

        /* If this is the header */
        if (isHeader) {
            /* Set private/public keys */
            thePrivateKey = pProperties.getByteProperty(propPrivateKey);
            thePublicKey = pProperties.getByteProperty(propPublicKey);
        }

        /* Else standard entry */
        else {
            /* Determine whether this is a debug entry */
            isDebug = (pProperties.getLongProperty(propDebug) != -1);

            /* Determine the number of digests */
            int myNumDigests = (int) pProperties.getLongProperty(propNumDigests);
            theDigests = new byte[myNumDigests][];
            theDigestLens = new long[myNumDigests];

            /* Loop through the encrypts */
            for (int iIndex = 1; iIndex <= myNumDigests; iIndex++) {
                /* Get digest properties */
                theDigests[iIndex] = pProperties.getByteProperty(propDigest + iIndex);
                theDigestLens[iIndex] = pProperties.getLongProperty(propDigest + iIndex);
            }

            /* Determine the number of encryption steps */
            int myNumEncrypts = (int) pProperties.getLongProperty(propNumEncrypts);
            theSecretKeys = new byte[myNumEncrypts][];
            theInitVectors = new byte[myNumEncrypts][];

            /* Loop through the encrypts */
            for (int iIndex = 1; iIndex <= myNumEncrypts; iIndex++) {
                /* Get digest properties */
                theSecretKeys[iIndex] = pProperties.getByteProperty(propSecretKey + iIndex);
                theInitVectors[iIndex] = pProperties.getByteProperty(propInitVector + iIndex);
            }

            /* Get signature */
            theSignature = pProperties.getByteProperty(propSignature);
        }
    }

    /**
     * Set the properties of the file
     * @return the properties
     */
    protected ZipFileProperties allocateProperties() {
        /* Allocate the properties */
        ZipFileProperties myProperties = new ZipFileProperties();

        /* Set the top-level details */
        myProperties.setProperty(propName, theFileName);
        myProperties.setProperty(propName, theFileSize);
        myProperties.setProperty(propZipName, theZipName);
        myProperties.setProperty(propZipName, theCompressedSize);

        /* If this is the header */
        if (isHeader) {
            /* Note the header */
            myProperties.setProperty(propHeader, 1);

            /* Set private/public keys */
            myProperties.setProperty(propPrivateKey, thePrivateKey);
            myProperties.setProperty(propPublicKey, thePublicKey);
        }

        /* Else standard entry */
        else {
            /* Set debug flag if required */
            if (isDebug)
                myProperties.setProperty(propDebug, 1);

            /* Store the number of digests */
            int myNumDigests = theDigests.length;
            myProperties.setProperty(propNumDigests, myNumDigests);

            /* Loop through the digests */
            for (int iIndex = 1; iIndex <= myNumDigests; iIndex++) {
                /* Set digest properties */
                myProperties.setProperty(propDigest + iIndex, theDigests[iIndex]);
                myProperties.setProperty(propDigest + iIndex, theDigestLens[iIndex]);
            }

            /* Store the number of encryption steps */
            int myNumEncrypts = theSecretKeys.length;
            myProperties.setProperty(propNumEncrypts, myNumEncrypts);

            /* Loop through the secret keys and initVectors */
            for (int iIndex = 1; iIndex <= myNumEncrypts; iIndex++) {
                /* Set Secret key properties */
                myProperties.setProperty(propSecretKey + iIndex, theSecretKeys[iIndex]);
                myProperties.setProperty(propInitVector + iIndex, theInitVectors[iIndex]);
            }

            /* Store the signature */
            myProperties.setProperty(propSignature, theSignature);
        }

        /* Return the properties */
        return myProperties;
    }

    /**
     * Set parent
     * @param pParent the parent
     */
    protected void setParent(ZipFileContents pParent) {
        theParent = pParent;
    }

    /**
     * Set the ZipEntry for the file
     * @param pEntry the zip entry
     */
    protected void setZipEntry(ZipEntry pEntry) {
        theZipName = pEntry.getName();
        theFileSize = pEntry.getSize();
        theCompressedSize = pEntry.getCompressedSize();
    }

    /**
     * Set the public key
     * @param pPublicKey the key value
     */
    protected void setPublicKey(byte[] pPublicKey) {
        if (!isHeader)
            throw new IllegalArgumentException("Entry is not a header");
        thePublicKey = pPublicKey;
    }

    /**
     * Set the private key
     * @param pPrivateKey the key value
     */
    protected void setPrivateKey(byte[] pPrivateKey) {
        if (!isHeader)
            throw new IllegalArgumentException("Entry is not a header");
        thePrivateKey = pPrivateKey;
    }

    /**
     * Set the digests
     * @param pDigests the digest streams
     */
    protected void setDigests(DigestOutputStream[] pDigests) {
        if (isHeader)
            throw new IllegalArgumentException("Entry is a header");

        /* Allocate new arrays */
        int myLen = pDigests.length;
        theDigests = new byte[myLen][];
        theDigestLens = new long[myLen];

        /* Loop through the array */
        for (int iIndex = 0; iIndex < myLen; iIndex++) {
            /* Set the digest properties */
            theDigests[iIndex] = pDigests[iIndex].getDigest();
            theDigestLens[iIndex] = pDigests[iIndex].getDataLen();
        }

        /* Store the last length as the file size */
        theFileSize = theDigestLens[myLen - 1];
    }

    /**
     * Set the secretKeys
     * @param pEncrypts the encryption streams
     * @param pAsymKey the Asymmetric key to secure the keys
     * @throws ModelException
     */
    protected void setSecretKeys(EncryptionOutputStream[] pEncrypts,
                                 AsymmetricKey pAsymKey) throws ModelException {
        if (isHeader)
            throw new IllegalArgumentException("Entry is a header");

        /* Allocate new arrays */
        int myLen = pEncrypts.length;
        theSecretKeys = new byte[myLen][];
        theInitVectors = new byte[myLen][];

        /* Loop through the array */
        for (int iIndex = 0; iIndex < myLen; iIndex++) {
            /* Set the key properties */
            theSecretKeys[iIndex] = pAsymKey.secureSymmetricKey(pEncrypts[iIndex].getSymmetricKey());
            theInitVectors[iIndex] = pEncrypts[iIndex].getInitVector();
        }
    }

    /**
     * Set the signature
     * @param pSignature the signature value
     */
    protected void setSignature(byte[] pSignature) {
        if (isHeader)
            throw new IllegalArgumentException("Entry is a header");
        theSignature = pSignature;
    }

    /**
     * Set header indication
     */
    protected void setHeader() {
        isHeader = true;
    }

    /**
     * Set debug indication
     */
    protected void setDebug() {
        isDebug = true;
    }

    /**
     * Sign the file
     * @param pSignature the signature
     * @throws ModelException
     */
    public void signEntry(Signature pSignature) throws ModelException {
        int iIndex;

        /* Protect against exceptions */
        try {
            /* Loop through the digests */
            for (iIndex = 1; iIndex < theDigests.length; iIndex++) {
                pSignature.update(theDigests[iIndex]);
            }

            /* Loop through the secret keys and initVectors */
            for (iIndex = 1; iIndex < theSecretKeys.length; iIndex++) {
                pSignature.update(theSecretKeys[iIndex]);
                pSignature.update(theInitVectors[iIndex]);
            }
        }
        /* Catch exceptions */
        catch (Exception e) {
            throw new ModelException(ExceptionClass.CRYPTO, "Exception calculating signature", e);
        }
    }

    /**
     * Sign the file
     * @param pMac the Mac
     * @throws ModelException
     */
    public void signEntry(Mac pMac) throws ModelException {
        int iIndex;

        /* Protect against exceptions */
        try {
            /* Loop through the digests */
            for (iIndex = 1; iIndex < theDigests.length; iIndex++) {
                pMac.update(theDigests[iIndex]);
            }

            /* Loop through the secret keys and initVectors */
            for (iIndex = 1; iIndex < theSecretKeys.length; iIndex++) {
                pMac.update(theSecretKeys[iIndex]);
                pMac.update(theInitVectors[iIndex]);
            }
        }
        /* Catch exceptions */
        catch (Exception e) {
            throw new ModelException(ExceptionClass.CRYPTO, "Exception calculating signature", e);
        }
    }
}
