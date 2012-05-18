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
package uk.co.tolcroft.models.help;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sourceforge.JDataWalker.ModelException;
import net.sourceforge.JDataWalker.ModelException.ExceptionClass;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* The help module that is implemented by each Help System */
public abstract class HelpModule {
    /**
     * Document name for Help Contents
     */
    protected static final String documentHelp = "HelpContents";

    /**
     * Attribute name for Initial page
     */
    protected static final String attrInitial = "initial";

    /**
     * The list of Help pages
     */
    private HelpPage.List theList = null;

    /**
     * The Help Entries
     */
    private HelpEntry[] theEntries = null;

    /**
     * The title of the Help System
     */
    private String theTitle = "Help System";

    /**
     * The initial entry of the help system
     */
    private String theInitial = null;

    /* Access methods */
    public HelpPage.List getHelpPages() {
        return theList;
    }

    public String getInitialName() {
        return theInitial;
    }

    public String getTitle() {
        return theTitle;
    }

    public HelpEntry[] getHelpEntries() {
        return theEntries;
    }

    /**
     * Constructor
     * @param pDefinitions the definitions file name
     * @throws ModelException
     */
    public HelpModule(String pDefinitions) throws ModelException {
        /* Allocate the list */
        theList = new HelpPage.List();

        /* Parse the help definitions */
        parseHelpDefinition(pDefinitions);

        /* Loop through the entities */
        loadHelpPages(theEntries);
    }

    /**
     * Parse Help Definition
     * @param pFile the XML file containing the definitions
     * @throws ModelException
     */
    private void parseHelpDefinition(String pFile) throws ModelException {
        InputStream myStream;
        DocumentBuilderFactory myFactory;
        DocumentBuilder myBuilder;
        Document myDoc;
        Element myElement;

        /* Protect against exceptions */
        try {
            /* Access the input stream for the entity */
            myStream = this.getClass().getResourceAsStream(pFile);

            /* Create the document builder */
            myFactory = DocumentBuilderFactory.newInstance();
            myBuilder = myFactory.newDocumentBuilder();

            /* Access the XML document element */
            myDoc = myBuilder.parse(myStream);
            myElement = myDoc.getDocumentElement();

            /* Reject if this is not a Help Definitions file */
            if (!myElement.getNodeName().equals(documentHelp))
                throw new ModelException(ExceptionClass.DATA, "Invalid document name: "
                        + myElement.getNodeName());

            /* Set title of document */
            if (myElement.getAttribute(HelpEntry.attrTitle) != null)
                theTitle = myElement.getAttribute(HelpEntry.attrTitle);

            /* Access the entries */
            theEntries = HelpEntry.getHelpEntryArray(myElement);

            /* Default the initial entry */
            theInitial = theEntries[0].getName();

            /* Set initial element */
            if (myElement.getAttribute(attrInitial) != null)
                theInitial = myElement.getAttribute(attrInitial);

            /* Close the stream */
            try {
                myStream.close();
            } catch (Throwable e) {
            }
        }

        /* Cascade exceptions */
        catch (ModelException e) {
            throw e;
        }

        /* Catch exceptions */
        catch (Throwable e) {
            /* Throw Exception */
            throw new ModelException(ExceptionClass.DATA, "Failed to load XML Help Definitions", e);
        }
    }

    /**
     * Load Help entries from the file system
     * @param pEntries the Help Entries
     * @throws ModelException
     */
    private void loadHelpPages(HelpEntry[] pEntries) throws ModelException {
        InputStream myStream;

        /* Loop through the entities */
        for (HelpEntry myEntry : pEntries) {
            /* Check that the entry is not already in the list */
            if (theList.searchFor(myEntry.getName()) != null)
                throw new ModelException(ExceptionClass.DATA, "Duplicate Help object Name: "
                        + myEntry.getName());

            /* If we have a file name */
            if (myEntry.getFileName() != null) {
                /* Access the input stream for the entity */
                myStream = this.getClass().getResourceAsStream(myEntry.getFileName());

                /* Add it to the list */
                theList.addItem(myEntry, myStream);

                /* Close the stream */
                try {
                    myStream.close();
                } catch (Throwable e) {
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
