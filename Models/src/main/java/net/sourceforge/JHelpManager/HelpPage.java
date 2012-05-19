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
package net.sourceforge.JHelpManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.ModelException.ExceptionClass;

/**
 * Help Page class. This class maps between the name of a help page and the HTML that the name represents.
 */
public class HelpPage implements Comparable<Object> {
    /* Members */
    private final String theName;
    private final String theHtml;
    private final HelpEntry theEntry;

    /* Access methods */
    public String getName() {
        return theName;
    }

    public String getHtml() {
        return theHtml;
    }

    public HelpEntry getEntry() {
        return theEntry;
    }

    /**
     * Constructor
     * @param pEntry the help entry for the help page
     * @param pStream the stream to read the help page from
     * @throws ModelException
     */
    public HelpPage(HelpEntry pEntry,
                    InputStream pStream) throws ModelException {
        /* Local variables */
        BufferedReader myReader;
        InputStreamReader myInputReader;
        String myLine;

        /* Allocate a buffered reader on top of the input stream */
        myInputReader = new InputStreamReader(pStream);
        myReader = new BufferedReader(myInputReader);

        /* Allocate a string builder */
        StringBuilder myBuilder = new StringBuilder(10000);

        /* Protect against exceptions */
        try {
            /* Read the header entry */
            while ((myLine = myReader.readLine()) != null) {
                /* Add to the string buffer */
                myBuilder.append(myLine);
                myBuilder.append('\n');
            }
        }

        /* Catch exceptions */
        catch (Exception e) {
            /* Throw an exception */
            throw new ModelException(ExceptionClass.DATA, "Failed to load help file " + pEntry.getName(), e);
        }

        /* Build the values */
        theName = pEntry.getName();
        theHtml = myBuilder.toString();

        /* Link the entry to us */
        pEntry.setHelpPage(this);
        theEntry = pEntry;
    }

    @Override
    public boolean equals(Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat)
            return true;
        if (pThat == null)
            return false;

        /* Make sure that the object is a HelpPage */
        if (pThat.getClass() != this.getClass())
            return false;

        /* Access the object as a Help Page */
        HelpPage myPage = (HelpPage) pThat;

        /* Check for equality */
        if (Difference.getDifference(getName(), myPage.getName()).isDifferent())
            return false;
        if (Difference.getDifference(getHtml(), myPage.getHtml()).isDifferent())
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int myHash = 0;
        if (theName != null)
            myHash += theName.hashCode();
        myHash *= 17;
        if (theHtml != null)
            myHash += theHtml.hashCode();
        return myHash;
    }

    @Override
    public int compareTo(Object pThat) {
        int result;

        /* Handle the trivial cases */
        if (this == pThat)
            return 0;
        if (pThat == null)
            return -1;

        /* Make sure that the object is a Help Page */
        if (pThat.getClass() != this.getClass())
            return -1;

        /* Access the object as a HelpPage */
        HelpPage myThat = (HelpPage) pThat;

        /* Compare the name */
        result = theName.compareTo(myThat.theName);
        if (result == 0)
            return 0;
        else if (result < 0)
            return -1;
        else
            return 1;
    }
}
