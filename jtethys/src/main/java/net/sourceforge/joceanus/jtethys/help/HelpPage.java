/*******************************************************************************
 * jTethys: Java Utilities
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
package net.sourceforge.joceanus.jtethys.help;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Help Page class. This class maps between the name of a help page and the HTML that the name represents.
 */
public class HelpPage
        implements Comparable<Object> {
    /**
     * Byte encoding.
     */
    private static final String ENCODING = "UTF-8";

    /**
     * The Buffer length.
     */
    private static final int BUFFER_LEN = 10000;

    /**
     * The name of the page.
     */
    private final String theName;

    /**
     * The HTML of the page.
     */
    private final String theHtml;

    /**
     * The entry for the page.
     */
    private final HelpEntry theEntry;

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the HTML.
     * @return the HTML
     */
    public String getHtml() {
        return theHtml;
    }

    /**
     * Obtain the entry.
     * @return the entry
     */
    public HelpEntry getEntry() {
        return theEntry;
    }

    /**
     * Constructor.
     * @param pEntry the help entry for the help page
     * @param pStream the stream to read the help page from
     * @throws HelpException on error
     */
    public HelpPage(final HelpEntry pEntry,
                    final InputStream pStream) throws HelpException {
        /* Local variables */
        BufferedReader myReader;
        InputStreamReader myInputReader;

        /* Allocate a string builder */
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Protect against exceptions */
        try {
            /* Allocate a buffered reader on top of the input stream */
            myInputReader = new InputStreamReader(pStream, ENCODING);
            myReader = new BufferedReader(myInputReader);

            /* Read the header entry */
            for (;;) {
                /* Read next line */
                String myLine = myReader.readLine();
                if (myLine == null) {
                    break;
                }

                /* Add to the string buffer */
                myBuilder.append(myLine);
                myBuilder.append('\n');
            }

            /* Catch exceptions */
        } catch (IOException e) {
            /* Throw an exception */
            throw new HelpException("Failed to load help file "
                                    + pEntry.getName(), e);
        }

        /* Build the values */
        theName = pEntry.getName();
        theHtml = myBuilder.toString();

        /* Link the entry to us */
        pEntry.setHelpPage(this);
        theEntry = pEntry;
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is a HelpPage */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the object as a Help Page */
        HelpPage myPage = (HelpPage) pThat;

        /* Check for equality */
        boolean isEqual = (theName == null)
                                           ? myPage.getName() != null
                                           : theName.equals(myPage.getName());
        if (isEqual) {
            isEqual = (theHtml == null)
                                       ? myPage.getHtml() != null
                                       : theHtml.equals(myPage.getHtml());
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        int myHash = 1;
        if (theName != null) {
            myHash += theName.hashCode();
        }
        myHash *= HelpModule.HASH_PRIME;
        if (theHtml != null) {
            myHash += theHtml.hashCode();
        }
        return myHash;
    }

    @Override
    public int compareTo(final Object pThat) {
        int result;

        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Make sure that the object is a Help Page */
        if (pThat.getClass() != this.getClass()) {
            return -1;
        }

        /* Access the object as a HelpPage */
        HelpPage myThat = (HelpPage) pThat;

        /* Compare the name */
        result = theName.compareTo(myThat.theName);
        if (result == 0) {
            return 0;
        } else if (result < 0) {
            return -1;
        } else {
            return 1;
        }
    }
}
