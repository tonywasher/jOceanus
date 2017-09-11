/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;

import net.sourceforge.joceanus.jgordianknot.crypto.stream.GordianStreamDefinition;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Class represents an encrypted file in the Zip file.
 */
public class GordianZipFileEntry {
    /**
     * The NonHeader Error text.
     */
    private static final String ERROR_NONHEADER = "Entry is not a header";

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
     * The Hash property name of a file.
     */
    private static final String PROP_HASH = "Hash";

    /**
     * The Header property name of a file.
     */
    private static final String PROP_HEADER = "Header";

    /**
     * The Stream Type property.
     */
    private static final String PROP_TYPE = "StreamType";

    /**
     * The Stream Vector property.
     */
    private static final String PROP_VECTOR = "StreamVector";

    /**
     * The Stream Value property.
     */
    private static final String PROP_VALUE = "StreamValue";

    /**
     * The parent contents.
     */
    private GordianZipFileContents theParent;

    /**
     * The properties.
     */
    private final GordianZipFileProperties theProperties;

    /**
     * The file name.
     */
    private final String theFileName;

    /**
     * The zip name.
     */
    private String theZipName;

    /**
     * The original file size.
     */
    private Long theFileSize;

    /**
     * The compressed file size.
     */
    private Long theCompressedSize;

    /**
     * The Hash.
     */
    private byte[] theHash;

    /**
     * Is this the header.
     */
    private boolean isHeader;

    /**
     * Standard constructor from filename.
     * @param pFileName the file name
     */
    protected GordianZipFileEntry(final String pFileName) {
        /* Store the file name */
        theFileName = pFileName;

        /* Allocate the properties */
        theProperties = new GordianZipFileProperties();
    }

    /**
     * Standard constructor from properties.
     * @param pProperties the properties
     */
    protected GordianZipFileEntry(final GordianZipFileProperties pProperties) {
        /* Store the properties */
        theProperties = pProperties;

        /* Access the top-level details */
        theFileName = pProperties.getStringProperty(PROP_NAME);
        theZipName = pProperties.getStringProperty(PROP_ZIPNAME);

        /* Determine whether this is a header */
        isHeader = pProperties.getLongProperty(PROP_HEADER) != null;

        /* If this is the header */
        if (isHeader) {
            /* Set hash keys */
            theHash = pProperties.getByteProperty(PROP_HASH);

            /* Else standard entry */
        } else {
            /* Access file sizes */
            theFileSize = pProperties.getLongProperty(PROP_NAME);
            theCompressedSize = pProperties.getLongProperty(PROP_ZIPNAME);
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
    public GordianZipFileContents getParent() {
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
    public Long getSize() {
        return theFileSize;
    }

    /**
     * Obtain the compressed size of a file.
     * @return the compressed size
     */
    public Long getCompressedSize() {
        return theCompressedSize;
    }

    /**
     * Obtain the public key.
     * @return the public key
     */
    protected byte[] getHash() {
        return theHash;
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
     */
    protected GordianZipFileProperties allocateProperties() {
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
            theProperties.setProperty(PROP_HASH, theHash);
        }

        /* Return the properties */
        return theProperties;
    }

    /**
     * Set User String property.
     * @param pPropertyName the property name
     * @param pPropertyValue the property value
     */
    public void setUserStringProperty(final String pPropertyName,
                                      final String pPropertyValue) {
        /* Set the property */
        theProperties.setProperty(PROP_USERPFIX
                                  + pPropertyName, pPropertyValue);
    }

    /**
     * Set User Long property.
     * @param pPropertyName the property name
     * @param pPropertyValue the property value
     */
    public void setUserLongProperty(final String pPropertyName,
                                    final Long pPropertyValue) {
        /* Set the property */
        theProperties.setProperty(PROP_USERPFIX
                                  + pPropertyName, pPropertyValue);
    }

    /**
     * Get User String property.
     * @param pPropertyName the property name
     * @return the property value (or null)
     */
    public String getUserStringProperty(final String pPropertyName) {
        /* Set the property */
        return theProperties.getStringProperty(PROP_USERPFIX
                                               + pPropertyName);
    }

    /**
     * Get User Longproperty.
     * @param pPropertyName the property name
     * @return the property value (or null)
     */
    public Long getUserLongProperty(final String pPropertyName) {
        /* Set the property */
        return theProperties.getLongProperty(PROP_USERPFIX
                                             + pPropertyName);
    }

    /**
     * Set parent.
     * @param pParent the parent
     */
    protected void setParent(final GordianZipFileContents pParent) {
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
     * Set the hash.
     * @param pHash the hash value
     */
    protected void setHash(final byte[] pHash) {
        if (!isHeader) {
            throw new IllegalArgumentException(ERROR_NONHEADER);
        }
        theHash = Arrays.copyOf(pHash, pHash.length);
    }

    /**
     * Set header indication.
     */
    protected void setHeader() {
        isHeader = true;
    }

    /**
     * Build the properties.
     * @param pStreams the stream definitions
     */
    public void buildProperties(final List<GordianStreamDefinition> pStreams) {
        /* Loop through the streams */
        int iIndex = 0;
        final Iterator<GordianStreamDefinition> myIterator = pStreams.iterator();
        while (myIterator.hasNext()) {
            final GordianStreamDefinition myStream = myIterator.next();
            iIndex++;

            /* Set the type property */
            theProperties.setProperty(PROP_TYPE
                                      + iIndex, myStream.getExternalId());
            theProperties.setProperty(PROP_TYPE
                                      + iIndex, myStream.getTypeDefinition());

            /* Set the initVector property */
            theProperties.setProperty(PROP_VECTOR
                                      + iIndex, myStream.getInitVector());

            /* Set the value property */
            theProperties.setProperty(PROP_VALUE
                                      + iIndex, myStream.getValue());

            /* Handle extras */
            switch (myStream.getType()) {
                case MAC:
                    theCompressedSize = myStream.getDataLength();
                    break;
                case DIGEST:
                    theFileSize = myStream.getDataLength();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Build input list.
     * @return the new input list
     * @throws OceanusException on error
     */
    public List<GordianStreamDefinition> buildInputList() throws OceanusException {
        /* Create list */
        final List<GordianStreamDefinition> myList = new ArrayList<>();

        /* Loop through the streamProperties */
        int myIndex = 1;
        for (;;) {
            /* Check for property and break loop if not found */
            final Long myType = theProperties.getLongProperty(PROP_TYPE
                                                              + myIndex);
            if (myType == null) {
                break;
            }

            /* Access remaining properties */
            final byte[] myTypeDef = theProperties.getByteProperty(PROP_TYPE
                                                                   + myIndex);
            final byte[] myVector = theProperties.getByteProperty(PROP_VECTOR
                                                                  + myIndex);
            final byte[] myValue = theProperties.getByteProperty(PROP_VALUE
                                                                 + myIndex);

            /* Define the stream */
            myList.add(new GordianStreamDefinition(myType, myTypeDef, myVector, myValue));
            myIndex++;
        }

        /* Return the list */
        return myList;
    }
}
