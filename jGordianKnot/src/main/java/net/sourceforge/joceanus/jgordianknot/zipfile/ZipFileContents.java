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
package net.sourceforge.joceanus.jgordianknot.zipfile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;

import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Class represents the contents of an encrypted Zip file.
 */
public class ZipFileContents {
    /**
     * The Header file name.
     */
    protected static final String NAME_HEADER = "jGordianKnotHeader";

    /**
     * The Buffer length.
     */
    private static final int BUFFER_LEN = 100;

    /**
     * The file separator.
     */
    private static final char SEPARATOR_FILE = ';';

    /**
     * Zip File Header.
     */
    private ZipFileEntry theHeader;

    /**
     * List of files.
     */
    private final List<ZipFileEntry> theList;

    /**
     * Obtain the header.
     * @return the header
     */
    protected ZipFileEntry getHeader() {
        return theHeader;
    }

    /**
     * Obtain and iterator.
     * @return the header
     */
    public Iterator<ZipFileEntry> iterator() {
        return theList.iterator();
    }

    /**
     * Constructor.
     */
    protected ZipFileContents() {
        /* Allocate the list */
        theList = new ArrayList<ZipFileEntry>();
    }

    /**
     * Constructor from encoded string.
     * @param pCodedString the encoded string
     * @throws JOceanusException on error
     */
    protected ZipFileContents(final String pCodedString) throws JOceanusException {
        /* Allocate the list */
        theList = new ArrayList<ZipFileEntry>();

        /* Wrap string in a string builder */
        StringBuilder myString = new StringBuilder(pCodedString);
        String myFileSep = Character.toString(SEPARATOR_FILE);

        /* while we have separators in the string */
        for (;;) {
            /* Locate End of entry and break loop if not found */
            int myLoc = myString.indexOf(myFileSep);
            if (myLoc == -1) {
                break;
            }

            /* Parse the encoded entry and remove it from the buffer */
            parseEncodedEntry(myString.substring(0, myLoc));
            myString.delete(0, myLoc + 1);
        }

        /* Parse the remaining entry */
        parseEncodedEntry(myString.toString());
    }

    /**
     * Add a header entry to the contents.
     * @return the newly added entry
     */
    protected ZipFileEntry addZipFileHeader() {
        /* Create the new entry */
        theHeader = new ZipFileEntry(NAME_HEADER);
        theHeader.setHeader();
        theHeader.setParent(this);

        /* Return it */
        return theHeader;
    }

    /**
     * Add a File entry to the contents.
     * @param pName the file name
     * @return the newly added entry
     */
    protected final ZipFileEntry addZipFileEntry(final String pName) {
        /* Create the new entry */
        ZipFileEntry myEntry = new ZipFileEntry(pName);

        /* Add it to the list */
        addZipFileEntry(myEntry);

        /* Return it */
        return myEntry;
    }

    /**
     * Add a File entry to the contents.
     * @param pEntry the zip entry
     */
    protected final void addZipFileEntry(final ZipEntry pEntry) {
        /* Create the new entry */
        ZipFileEntry myEntry = addZipFileEntry(pEntry.getName());

        /* Record details */
        myEntry.setZipEntry(pEntry);
    }

    /**
     * Add a File entry to the contents.
     * @param pEntry the file entry
     */
    protected final void addZipFileEntry(final ZipFileEntry pEntry) {
        /* Access the name */
        String myName = pEntry.getFileName();

        /* Loop through the files in the list in the list */
        int iIndex = 0;
        Iterator<ZipFileEntry> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Access next entry */
            ZipFileEntry myEntry = myIterator.next();

            /* Check the entry name */
            int iDiff = myName.compareTo(myEntry.getFileName());

            /* If this file is later than us */
            if (iDiff < 0) {
                break;
            }

            /* Reject attempt to add duplicate name */
            if (iDiff == 0) {
                throw new IllegalArgumentException("Duplicate filename - "
                                                   + myName);
            }

            /* Increment index */
            iIndex++;
        }

        /* Set as child of these contents */
        pEntry.setParent(this);

        /* Add into the list at the correct point */
        theList.add(iIndex, pEntry);
    }

    /**
     * Locate the file by name.
     * @param pName the name of the file
     * @return the entry or null if not found
     */
    public ZipFileEntry findFileEntry(final String pName) {
        /* Loop through the file entries */
        Iterator<ZipFileEntry> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Access the entry */
            ZipFileEntry myEntry = myIterator.next();

            /* Check the entry name */
            int iDiff = pName.compareTo(myEntry.getFileName());

            /* If this is the required entry, return it */
            if (iDiff == 0) {
                return myEntry;
            }

            /* If this entry is later than the required name, no such entry */
            if (iDiff < 0) {
                break;
            }
        }

        /* Return not found */
        return null;
    }

    /**
     * Encode the contents.
     * @return the encoded string
     * @throws JOceanusException on error
     */
    protected String encodeContents() throws JOceanusException {
        StringBuilder myString = new StringBuilder(BUFFER_LEN);
        ZipFileProperties myProperties;

        /* Loop through the file entries */
        Iterator<ZipFileEntry> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Access the entry */
            ZipFileEntry myEntry = myIterator.next();

            /* Access the properties */
            myProperties = myEntry.allocateProperties();

            /* Add the value to the string */
            if (myString.length() > 0) {
                myString.append(SEPARATOR_FILE);
            }

            /* Encode the properties */
            myString.append(myProperties.encodeProperties());
        }

        /* Add the value to the string */
        if (myString.length() > 0) {
            myString.append(SEPARATOR_FILE);
        }

        /* Encode the header */
        myProperties = theHeader.allocateProperties();
        myString.append(myProperties.encodeProperties());

        /* Return the encoded string */
        return myString.toString();
    }

    /**
     * Add a File Entry from encoded string.
     * @param pCodedString the encoded string
     * @throws JOceanusException on error
     */
    private void parseEncodedEntry(final String pCodedString) throws JOceanusException {
        /* Parse the properties */
        ZipFileProperties myProperties = new ZipFileProperties(pCodedString);

        /* Add the zip file entry */
        ZipFileEntry myEntry = new ZipFileEntry(myProperties);

        /* If this is a header */
        if (myEntry.isHeader()) {
            /* Store as header */
            theHeader = myEntry;
            theHeader.setParent(this);

            /* else standard file */
        } else {
            /* Add the entry to the list */
            addZipFileEntry(myEntry);
        }
    }
}
