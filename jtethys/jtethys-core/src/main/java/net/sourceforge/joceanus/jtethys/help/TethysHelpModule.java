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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * The help module that is implemented by each Help System.
 */
public abstract class TethysHelpModule {
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
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TethysHelpModule.class);

    /**
     * The stream close error.
     */
    public static final String ERROR_STREAM = "Failed to close stream";

    /**
     * The list of Help pages.
     */
    private final List<TethysHelpPage> thePages;

    /**
     * The Help Entries.
     */
    private List<TethysHelpEntry> theEntries;

    /**
     * The title of the Help System.
     */
    private String theTitle = "Help System";

    /**
     * The initial entry of the help system.
     */
    private String theInitial;

    /**
     * Constructor.
     * @param pClass the class representing the resource
     * @param pDefinitions the definitions file name
     * @throws TethysHelpException on error
     */
    public TethysHelpModule(final Class<?> pClass,
                            final String pDefinitions) throws TethysHelpException {
        /* Allocate the page list */
        thePages = new ArrayList<TethysHelpPage>();

        /* Parse the help definitions */
        parseHelpDefinition(pClass, pDefinitions);

        /* Loop through the entities */
        loadHelpPages(pClass, theEntries);
    }

    /**
     * Obtain the list of pages.
     * @return the list of pages
     */
    public List<TethysHelpPage> getHelpPages() {
        return thePages;
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
     * Search for a help page in the list.
     * @param pName the name of the help page
     * @return the help page
     */
    public final TethysHelpPage searchFor(final String pName) {
        /* Loop through the entries */
        Iterator<TethysHelpPage> myIterator = thePages.iterator();
        while (myIterator.hasNext()) {
            /* Access the entry */
            TethysHelpPage myPage = myIterator.next();

            /* If we have found the page return it */
            if (pName.equals(myPage.getName())) {
                return myPage;
            }
        }

        /* Return not found */
        return null;
    }

    /**
     * Parse Help Definition.
     * @param pClass the class representing the resource
     * @param pFile the XML file containing the definitions
     * @throws TethysHelpException on error
     */
    private void parseHelpDefinition(final Class<?> pClass,
                                     final String pFile) throws TethysHelpException {
        /* Protect against exceptions */
        try (InputStream myStream = pClass.getResourceAsStream(pFile)) {
            /* Create the document builder */
            DocumentBuilderFactory myFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder myBuilder = myFactory.newDocumentBuilder();

            /* Access the XML document element */
            Document myDoc = myBuilder.parse(myStream);
            Element myElement = myDoc.getDocumentElement();

            /* Reject if this is not a Help Definitions file */
            if (!myElement.getNodeName().equals(DOC_NAME)) {
                throw new TethysHelpException("Invalid document name: " + myElement.getNodeName());
            }

            /* Set title of document */
            if (myElement.getAttribute(TethysHelpEntry.ATTR_TITLE) != null) {
                theTitle = myElement.getAttribute(TethysHelpEntry.ATTR_TITLE);
            }

            /* Access the entries */
            theEntries = TethysHelpEntry.getHelpEntryList(myElement);

            /* Default the initial entry */
            theInitial = theEntries.get(0).getName();

            /* Set initial element */
            if (myElement.getAttribute(ATTR_INITIAL) != null) {
                theInitial = myElement.getAttribute(ATTR_INITIAL);
            }

            /* Close the stream */
            myStream.close();

            /* Catch exceptions */
        } catch (ParserConfigurationException | SAXException | IOException e) {
            /* Throw Exception */
            throw new TethysHelpException("Failed to initiate parser", e);
        }
    }

    /**
     * Load Help entries from the file system.
     * @param pClass the class representing the resource
     * @param pEntries the Help Entries
     * @throws TethysHelpException on error
     */
    private void loadHelpPages(final Class<?> pClass,
                               final List<TethysHelpEntry> pEntries) throws TethysHelpException {
        /* Loop through the entities */
        for (TethysHelpEntry myEntry : pEntries) {
            /* Check that the entry is not already in the list */
            if (searchFor(myEntry.getName()) != null) {
                throw new TethysHelpException("Duplicate Help object Name: " + myEntry.getName());
            }

            /* If we have a file name */
            if (myEntry.getFileName() != null) {
                try (InputStream myStream = pClass.getResourceAsStream(myEntry.getFileName())) {
                    /* Build the help page */
                    TethysHelpPage myPage = new TethysHelpPage(myEntry, myStream);

                    /* Add it to the list */
                    thePages.add(myPage);

                } catch (IOException ex) {
                    LOGGER.error(ERROR_STREAM, ex);
                }
            }

            /* If we have children */
            if (myEntry.getChildren() != null) {
                /* Load the entries */
                loadHelpPages(pClass, myEntry.getChildren());
            }
        }
    }
}
