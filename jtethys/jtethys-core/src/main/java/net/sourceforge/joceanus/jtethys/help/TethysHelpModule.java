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
import java.util.ArrayList;
import java.util.List;

/**
 * The help module that is implemented by each Help System.
 */
public abstract class TethysHelpModule {
    /**
     * Byte encoding.
     */
    private static final String ENCODING = "UTF-8";

    /**
     * The Buffer length.
     */
    private static final int BUFFER_LEN = 10000;

    /**
     * The Hash prime.
     */
    protected static final int HASH_PRIME = 19;

    /**
     * Document name for Help Contents.
     */
    protected static final String DOC_NAME = "HelpContents";

    /**
     * Attribute name for Initial page.
     */
    protected static final String ATTR_INITIAL = "initial";

    /**
     * The stream close error.
     */
    public static final String ERROR_STREAM = "Failed to close stream";

    /**
     * The Help Entries.
     */
    private final List<TethysHelpEntry> theEntries;

    /**
     * The title of the Help System.
     */
    private final Class<?> theClass;

    /**
     * The title of the Help System.
     */
    private final String theTitle;

    /**
     * The initial entry of the help system.
     */
    private String theInitial;

    /**
     * Constructor.
     * @param pClass the class representing the resource
     * @param pTitle the title
     * @throws TethysHelpException on error
     */
    public TethysHelpModule(final Class<?> pClass,
                            final String pTitle) throws TethysHelpException {
        /* Store parameters */
        theClass = pClass;
        theTitle = pTitle;

        /* Create entry list */
        theEntries = new ArrayList<>();
    }

    /**
     * Set the initial name.
     * @param pInitial the initial name
     */
    public void setInitialName(final String pInitial) {
        theInitial = pInitial;
    }

    /**
     * Obtain the initial name.
     * @return the initial name
     */
    public String getInitialName() {
        return theInitial;
    }

    /**
     * Obtain the title.
     * @return the title
     */
    public String getTitle() {
        return theTitle;
    }

    /**
     * Obtain the help entries.
     * @return the help entries
     */
    public List<TethysHelpEntry> getHelpEntries() {
        return theEntries;
    }

    /**
     * Add root entry.
     * @param pEntry the entry
     * @return the HelpEntry
     */
    public TethysHelpEntry addRootEntry(final TethysHelpEntry pEntry) {
        theEntries.add(pEntry);
        return pEntry;
    }

    /**
     * Define Standard Help entry.
     * @param pName the name
     * @param pFileName the filename
     * @return the HelpEntry
     */
    public static TethysHelpEntry defineHelpEntry(final String pName,
                                                  final String pFileName) {
        return defineTitledHelpEntry(pName, pName, pFileName);
    }

    /**
     * Define Titled Help entry.
     * @param pName the name
     * @param pTitle the title
     * @param pFileName the filename
     * @return the HelpEntry
     */
    public static TethysHelpEntry defineTitledHelpEntry(final String pName,
                                                        final String pTitle,
                                                        final String pFileName) {
        return new TethysHelpEntry(pName, pTitle, pFileName);
    }

    /**
     * Define Contents Help entry.
     * @param pName the name
     * @return the HelpEntry
     */
    public static TethysHelpEntry defineContentsEntry(final String pName) {
        return defineTitledContentsEntry(pName, pName);
    }

    /**
     * Define Contents Help entry.
     * @param pName the name
     * @param pTitle the title
     * @return the HelpEntry
     */
    public static TethysHelpEntry defineTitledContentsEntry(final String pName,
                                                            final String pTitle) {
        return new TethysHelpEntry(pName, pTitle);
    }

    /**
     * Load Help entries from the file system.
     * @throws TethysHelpException on error
     */
    protected void loadHelpPages() throws TethysHelpException {
        loadHelpPages(theEntries);
    }

    /**
     * Load Help entries from the file system.
     * @param pEntries the Help Entries
     * @throws TethysHelpException on error
     */
    private void loadHelpPages(final List<TethysHelpEntry> pEntries) throws TethysHelpException {
        /* Allocate a string builder */
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Loop through the entities */
        for (TethysHelpEntry myEntry : pEntries) {
            /* If we have a file name */
            if (myEntry.getFileName() != null) {
                /* Reset the builder */
                myBuilder.setLength(0);

                /* Protect against exceptions */
                try (InputStream myStream = theClass.getResourceAsStream(myEntry.getFileName());
                     InputStreamReader myInputReader = new InputStreamReader(myStream, ENCODING);
                     BufferedReader myReader = new BufferedReader(myInputReader)) {

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

                    /* Set the HTML for the entry */
                    myEntry.setHtml(myBuilder.toString());

                    /* Catch exceptions */
                } catch (IOException e) {
                    /* Throw an exception */
                    throw new TethysHelpException("Failed to load help file "
                                                  + myEntry.getName(), e);
                }
            }

            /* If we have children */
            if (myEntry.getChildren() != null) {
                /* Load the entries */
                loadHelpPages(myEntry.getChildren());
            }
        }
    }
}
