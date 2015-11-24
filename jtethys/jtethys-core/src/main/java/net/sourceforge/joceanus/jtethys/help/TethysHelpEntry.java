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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Help Entry class. This class provides structure to the help system, providing parent child
 * relationships to implement chapters and also providing maps between the name of a help page and
 * the file that holds the HTML for the page
 */
public class TethysHelpEntry {
    /**
     * Element name for Help Entry.
     */
    protected static final String ELEMENT_HELP = "HelpElement";

    /**
     * Attribute for filename.
     */
    protected static final String ATTR_FILENAME = "file";

    /**
     * Attribute for title.
     */
    protected static final String ATTR_TITLE = "title";

    /**
     * Attribute for name.
     */
    protected static final String ATTR_NAME = "name";

    /**
     * Empty string.
     */
    private static final String STR_EMPTY = "";

    /**
     * Title of the entry.
     */
    private final String theTitle;

    /**
     * Name of the entry.
     */
    private final String theName;

    /**
     * FileName of the entry.
     */
    private final String theFileName;

    /**
     * Children of the entry.
     */
    private final List<TethysHelpEntry> theChildren;

    /**
     * HelpPage.
     */
    private TethysHelpPage thePage;

    /**
     * Constructor for an HTML element built from an XML node.
     * @param pElement the XML element describing the help entry
     * @throws TethysHelpException on error
     */
    public TethysHelpEntry(final Element pElement) throws TethysHelpException {
        /* Reject entry if it is not a HelpElement */
        if (!pElement.getNodeName().equals(ELEMENT_HELP)) {
            throw new TethysHelpException("Invalid element name: "
                                          + pElement.getNodeName());
        }

        /* Access the name of the element */
        theName = pElement.getAttribute(ATTR_NAME);
        if (theName == null) {
            throw new TethysHelpException("Node has no associated name");
        }

        /* Access the title of the element and default it if required */
        String myAttr = pElement.getAttribute(ATTR_TITLE);
        theTitle = STR_EMPTY.equals(myAttr)
                                            ? theName
                                            : myAttr;

        /* Access the filename for the element and default it if required */
        myAttr = pElement.getAttribute(ATTR_FILENAME);
        theFileName = STR_EMPTY.equals(myAttr)
                                               ? null
                                               : myAttr;

        /* Parse the children */
        theChildren = pElement.hasChildNodes()
                                               ? getHelpEntryList(pElement)
                                               : null;
    }

    /**
     * Constructor for an HTML leaf element (no children).
     * @param pName the name by which this entry is referenced
     * @param pTitle the title for this page in the table of contents
     * @param pFileName the name of the file containing the HTML for this entry
     */
    public TethysHelpEntry(final String pName,
                           final String pTitle,
                           final String pFileName) {
        theName = pName;
        theTitle = pTitle;
        theFileName = pFileName;
        theChildren = null;
    }

    /**
     * Constructor for a table of contents element.
     * @param pName the name by which this entry is referenced
     * @param pTitle the title for this page in the table of contents
     * @param pChildren the children for this element
     */
    public TethysHelpEntry(final String pName,
                           final String pTitle,
                           final List<TethysHelpEntry> pChildren) {
        theName = pName;
        theTitle = pTitle;
        theFileName = null;
        theChildren = new ArrayList<TethysHelpEntry>(pChildren);
    }

    /**
     * Constructor for a table of contents HTML element.
     * @param pName the name by which this entry is referenced
     * @param pTitle the title for this page in the table of contents
     * @param pFileName the name of the file containing the HTML for this entry
     * @param pChildren the children for this element
     */
    protected TethysHelpEntry(final String pName,
                              final String pTitle,
                              final String pFileName,
                              final List<TethysHelpEntry> pChildren) {
        theName = pName;
        theTitle = pTitle;
        theFileName = pFileName;
        theChildren = new ArrayList<TethysHelpEntry>(pChildren);
    }

    /**
     * Obtain the title.
     * @return the title
     */
    public String getTitle() {
        return theTitle;
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the filename.
     * @return the filename
     */
    public String getFileName() {
        return theFileName;
    }

    /**
     * Obtain the children.
     * @return the children
     */
    public List<TethysHelpEntry> getChildren() {
        return theChildren;
    }

    /**
     * Obtain the help page.
     * @return the help page
     */
    public TethysHelpPage getHelpPage() {
        return thePage;
    }

    /**
     * Set the help page.
     * @param pPage the page to record
     */
    protected void setHelpPage(final TethysHelpPage pPage) {
        thePage = pPage;
    }

    /**
     * Constructor for an HTML element built from an XML node.
     * @param pElement the XML element describing the help entry
     * @return the HelpEntry array
     * @throws TethysHelpException on error
     */
    protected static List<TethysHelpEntry> getHelpEntryList(final Element pElement) throws TethysHelpException {
        /* Loop through the children */
        List<TethysHelpEntry> myEntries = new ArrayList<TethysHelpEntry>();
        for (Node myNode = pElement.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {

            /* Skip nonElement nodes */
            if (myNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            /* Access node as element */
            Element myChild = (Element) myNode;

            /* Create an entry based on this node */
            TethysHelpEntry myEntry = new TethysHelpEntry(myChild);
            myEntries.add(myEntry);
        }

        /* Return the entries */
        return myEntries;
    }

    @Override
    public String toString() {
        return getTitle();
    }

    /**
     * Find help entry by Id.
     * @param pName the name of the entry
     * @return the matching entry or null
     */
    protected TethysHelpEntry searchFor(final String pName) {
        TethysHelpEntry myResult = null;

        /* If we are the required entry return ourselves */
        if (pName.equals(theName)) {
            return this;
        }

        /* If we have children */
        if (theChildren != null) {
            /* Loop through the entries */
            for (TethysHelpEntry myEntry : theChildren) {
                /* Search this entry and return if found */
                myResult = myEntry.searchFor(pName);
                if (myResult != null) {
                    return myResult;
                }
            }
        }

        /* Return the result */
        return myResult;
    }
}
