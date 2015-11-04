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
public class HelpEntry {
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
    private final List<HelpEntry> theChildren;

    /**
     * HelpPage.
     */
    private HelpPage thePage;

    /**
     * Constructor for an HTML element built from an XML node.
     * @param pElement the XML element describing the help entry
     * @throws HelpException on error
     */
    public HelpEntry(final Element pElement) throws HelpException {
        /* Reject entry if it is not a HelpElement */
        if (!pElement.getNodeName().equals(ELEMENT_HELP)) {
            throw new HelpException("Invalid element name: "
                                    + pElement.getNodeName());
        }

        /* Access the name of the element */
        theName = pElement.getAttribute(ATTR_NAME);
        if (theName == null) {
            throw new HelpException("Node has no associated name");
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
    public HelpEntry(final String pName,
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
    public HelpEntry(final String pName,
                     final String pTitle,
                     final List<HelpEntry> pChildren) {
        theName = pName;
        theTitle = pTitle;
        theFileName = null;
        theChildren = new ArrayList<HelpEntry>(pChildren);
    }

    /**
     * Constructor for a table of contents HTML element.
     * @param pName the name by which this entry is referenced
     * @param pTitle the title for this page in the table of contents
     * @param pFileName the name of the file containing the HTML for this entry
     * @param pChildren the children for this element
     */
    protected HelpEntry(final String pName,
                        final String pTitle,
                        final String pFileName,
                        final List<HelpEntry> pChildren) {
        theName = pName;
        theTitle = pTitle;
        theFileName = pFileName;
        theChildren = new ArrayList<HelpEntry>(pChildren);
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
    public List<HelpEntry> getChildren() {
        return theChildren;
    }

    /**
     * Obtain the help page.
     * @return the help page
     */
    public HelpPage getHelpPage() {
        return thePage;
    }

    /**
     * Set the help page.
     * @param pPage the page to record
     */
    protected void setHelpPage(final HelpPage pPage) {
        thePage = pPage;
    }

    /**
     * Constructor for an HTML element built from an XML node.
     * @param pElement the XML element describing the help entry
     * @return the HelpEntry array
     * @throws HelpException on error
     */
    protected static List<HelpEntry> getHelpEntryList(final Element pElement) throws HelpException {
        /* Loop through the children */
        List<HelpEntry> myEntries = new ArrayList<HelpEntry>();
        for (Node myNode = pElement.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {

            /* Skip nonElement nodes */
            if (myNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            /* Access node as element */
            Element myChild = (Element) myNode;

            /* Create an entry based on this node */
            HelpEntry myEntry = new HelpEntry(myChild);
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
    protected HelpEntry searchFor(final String pName) {
        HelpEntry myResult = null;

        /* If we are the required entry return ourselves */
        if (pName.equals(theName)) {
            return this;
        }

        /* If we have children */
        if (theChildren != null) {
            /* Loop through the entries */
            for (HelpEntry myEntry : theChildren) {
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
