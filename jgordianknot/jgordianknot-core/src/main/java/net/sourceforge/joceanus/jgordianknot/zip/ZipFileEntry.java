/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.zip;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.zip.ZipEntry;

import net.sourceforge.joceanus.jgordianknot.JGordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.JGordianDataException;
import net.sourceforge.joceanus.jgordianknot.crypto.AsymmetricKey;
import net.sourceforge.joceanus.jgordianknot.crypto.CipherSet;
import net.sourceforge.joceanus.jgordianknot.zip.ZipStreamSpec.ZipStreamList;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Class represents an encrypted file in the Zip file.
 */
public class ZipFileEntry {
    /**
     * The Header Error text.
     */
    private static final String ERROR_HEADER = "Entry is a header";

    /**
     * The NonHeader Error text.
     */
    private static final String ERROR_NONHEADER = "Entry is not a header";

    /**
     * The Signature Error Text.
     */
    private static final String ERROR_SIGN = "Failed to calculate signature";

    /**
     * The User property prefix.
     */
    private static final String PROP_USERPFIX = "User";

    /**
     * The Name property of a file.
     */
    private static final String PROP_NAME = "Name";

    /**
     * The Zip Name property of a file.
     */
    private static final String PROP_ZIPNAME = "ZipName";

    /**
     * The Signature property name of a file.
     */
    private static final String PROP_SIGNATURE = "Signature";

    /**
     * The PublicKey property name of a file.
     */
    private static final String PROP_PUBLICKEY = "PublicKey";

    /**
     * The PrivateKey property name of a file.
     */
    private static final String PROP_PRIVATEKEY = "PrivateKey";

    /**
     * The Header property name of a file.
     */
    private static final String PROP_HEADER = "Header";

    /**
     * The parent contents.
     */
    private ZipFileContents theParent = null;

    /**
     * The properties.
     */
    private final ZipFileProperties theProperties;

    /**
     * The file name.
     */
    private String theFileName = null;

    /**
     * The zip name.
     */
    private String theZipName = null;

    /**
     * The original file size.
     */
    private long theFileSize = 0;

    /**
     * The compressed file size.
     */
    private long theCompressedSize = 0;

    /**
     * The ZipStreamList.
     */
    private ZipStreamList theStreamList = null;

    /**
     * The Signature.
     */
    private byte[] theSignature = null;

    /**
     * The Private Key.
     */
    private byte[] thePrivateKey = null;

    /**
     * The Public Key.
     */
    private byte[] thePublicKey = null;

    /**
     * Is this the header.
     */
    private boolean isHeader = false;

    /**
     * Standard constructor from filename.
     * @param pFileName the file name
     */
    protected ZipFileEntry(final String pFileName) {
        /* Store the file name */
        theFileName = pFileName;

        /* Allocate the properties */
        theProperties = new ZipFileProperties();
    }

    /**
     * Standard constructor from properties.
     * @param pProperties the properties
     * @throws JOceanusException on error
     */
    protected ZipFileEntry(final ZipFileProperties pProperties) throws JOceanusException {
        /* Store the properties */
        theProperties = pProperties;

        /* Access the top-level details */
        theFileName = pProperties.getStringProperty(PROP_NAME);
        theZipName = pProperties.getStringProperty(PROP_ZIPNAME);

        /* Determine whether this is a header */
        isHeader = pProperties.getLongProperty(PROP_HEADER) != null;

        /* If this is the header */
        if (isHeader) {
            /* Set private/public keys */
            thePrivateKey = pProperties.getByteProperty(PROP_PRIVATEKEY);
            thePublicKey = pProperties.getByteProperty(PROP_PUBLICKEY);

            /* Else standard entry */
        } else {
            /* Access file sizes */
            theFileSize = pProperties.getLongProperty(PROP_NAME);
            theCompressedSize = pProperties.getLongProperty(PROP_ZIPNAME);

            /* Determine the streamSpecs */
            theStreamList = new ZipStreamList(pProperties);

            /* Get signature */
            theSignature = pProperties.getByteProperty(PROP_SIGNATURE);
        }
    }

    /**
     * Obtain the name of the file.
     * @return the name of the file
     */
    public String getFileName() {
        return theFileName;
    }

    /**
     * Obtain the parent of the file.
     * @return the parent of the file
     */
    public ZipFileContents getParent() {
        return theParent;
    }

    /**
     * Obtain the Zip name of the file.
     * @return the Zip name of the file
     */
    public String getZipName() {
        return theZipName;
    }

    /**
     * Obtain the original size of a file.
     * @return the original size
     */
    public long getSize() {
        return theFileSize;
    }

    /**
     * Obtain the compressed size of a file.
     * @return the compressed size
     */
    public long getCompressedSize() {
        return theCompressedSize;
    }

    /**
     * Obtain the stream list.
     * @return the stream list
     */
    protected ZipStreamList getStreamList() {
        return theStreamList;
    }

    /**
     * Obtain the signature.
     * @return the signature
     */
    protected byte[] getSignature() {
        return theSignature;
    }

    /**
     * Obtain the private key.
     * @return the private key
     */
    protected byte[] getPrivateKey() {
        return thePrivateKey;
    }

    /**
     * Obtain the public key.
     * @return the public key
     */
    protected byte[] getPublicKey() {
        return thePublicKey;
    }

    /**
     * Is this entry the header.
     * @return true/false
     */
    protected boolean isHeader() {
        return isHeader;
    }

    /**
     * Set the properties of the file.
     * @return the properties
     * @throws JOceanusException on error
     */
    protected ZipFileProperties allocateProperties() throws JOceanusException {
        /* Set the top-level details */
        theProperties.setProperty(PROP_NAME, theFileName);
        theProperties.setProperty(PROP_NAME, theFileSize);
        theProperties.setProperty(PROP_ZIPNAME, theZipName);
        theProperties.setProperty(PROP_ZIPNAME, theCompressedSize);

        /* If this is the header */
        if (isHeader) {
            /* Note the header */
            theProperties.setProperty(PROP_HEADER, 1L);

            /* Set private/public keys */
            theProperties.setProperty(PROP_PRIVATEKEY, thePrivateKey);
            theProperties.setProperty(PROP_PUBLICKEY, thePublicKey);

            /* Else standard entry */
        } else {
            /* Allocate properties for the stream list */
            theStreamList.allocateProperties(theProperties);
            /* Store the signature */
            theProperties.setProperty(PROP_SIGNATURE, theSignature);
        }

        /* Return the properties */
        return theProperties;
    }

    /**
     * Set User String property.
     * @param pPropertyName the property name
     * @param pPropertyValue the property value
     * @throws JOceanusException on error
     */
    public void setUserStringProperty(final String pPropertyName,
                                      final String pPropertyValue) throws JOceanusException {
        /* Set the property */
        theProperties.setProperty(PROP_USERPFIX
                                  + pPropertyName, pPropertyValue);
    }

    /**
     * Set User Long property.
     * @param pPropertyName the property name
     * @param pPropertyValue the property value
     * @throws JOceanusException on error
     */
    public void setUserLongProperty(final String pPropertyName,
                                    final Long pPropertyValue) throws JOceanusException {
        /* Set the property */
        theProperties.setProperty(PROP_USERPFIX
                                  + pPropertyName, pPropertyValue);
    }

    /**
     * Get User String property.
     * @param pPropertyName the property name
     * @return the property value (or null)
     * @throws JOceanusException on error
     */
    public String getUserStringProperty(final String pPropertyName) throws JOceanusException {
        /* Set the property */
        return theProperties.getStringProperty(PROP_USERPFIX
                                               + pPropertyName);
    }

    /**
     * Get User String property.
     * @param pPropertyName the property name
     * @return the property value (or null)
     * @throws JOceanusException on error
     */
    public Long getUserLongProperty(final String pPropertyName) throws JOceanusException {
        /* Set the property */
        return theProperties.getLongProperty(PROP_USERPFIX
                                             + pPropertyName);
    }

    /**
     * Set parent.
     * @param pParent the parent
     */
    protected void setParent(final ZipFileContents pParent) {
        theParent = pParent;
    }

    /**
     * Set the ZipEntry for the file.
     * @param pEntry the zip entry
     */
    protected void setZipEntry(final ZipEntry pEntry) {
        theZipName = pEntry.getName();
        theFileSize = pEntry.getSize();
        theCompressedSize = pEntry.getCompressedSize();
    }

    /**
     * Set the public key.
     * @param pPublicKey the key value
     */
    protected void setPublicKey(final byte[] pPublicKey) {
        if (!isHeader) {
            throw new IllegalArgumentException(ERROR_NONHEADER);
        }
        thePublicKey = Arrays.copyOf(pPublicKey, pPublicKey.length);
    }

    /**
     * Set the private key.
     * @param pPrivateKey the key value
     */
    protected void setPrivateKey(final byte[] pPrivateKey) {
        if (!isHeader) {
            throw new IllegalArgumentException(ERROR_NONHEADER);
        }
        thePrivateKey = Arrays.copyOf(pPrivateKey, pPrivateKey.length);
    }

    /**
     * Set the signature.
     * @param pSignature the signature value
     */
    private void setSignature(final byte[] pSignature) {
        if (isHeader) {
            throw new IllegalArgumentException(ERROR_HEADER);
        }
        theSignature = Arrays.copyOf(pSignature, pSignature.length);
    }

    /**
     * Set header indication.
     */
    protected void setHeader() {
        isHeader = true;
    }

    /**
     * Analyse the output stream.
     * @param pStream the output stream
     * @param pAsymKey the asymmetric key.
     * @throws JOceanusException on error
     */
    public void analyseOutputStream(final OutputStream pStream,
                                    final AsymmetricKey pAsymKey) throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Update from the stream list */
            CipherSet myCipherSet = pAsymKey.getCipherSet();
            theStreamList = new ZipStreamList(pStream, myCipherSet);
            theFileSize = theStreamList.getDataLength();

            /* Calculate the signature and declare it */
            Signature mySignature = pAsymKey.getSignature(true);
            theStreamList.updateSignature(mySignature);
            setSignature(mySignature.sign());

            /* Catch exceptions */
        } catch (SignatureException e) {
            throw new JGordianCryptoException(ERROR_SIGN, e);
        }
    }

    /**
     * Build input stream.
     * @param pService the executor service
     * @param pStream the current input stream.
     * @param pAsymKey the asymmetric key.
     * @return the new input stream
     * @throws JOceanusException on error
     */
    public InputStream buildInputStream(final ExecutorService pService,
                                        final InputStream pStream,
                                        final AsymmetricKey pAsymKey) throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Calculate the signature and declare it */
            Signature mySignature = pAsymKey.getSignature(false);
            theStreamList.updateSignature(mySignature);

            /* Check the signature */
            if (!mySignature.verify(getSignature())) {
                /* Throw an exception */
                throw new JGordianDataException("Signature does not match");
            }

            /* Build the input stream */
            CipherSet myCipherSet = pAsymKey.getCipherSet();
            return theStreamList.buildInputStream(pService, pStream, myCipherSet);

            /* Catch exceptions */
        } catch (SignatureException e) {
            throw new JGordianCryptoException(ERROR_SIGN, e);
        }
    }
}
