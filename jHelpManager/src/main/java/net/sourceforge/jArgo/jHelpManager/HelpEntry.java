/*******************************************************************************
 * JHelpManager: Java Help Manager
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
package net.sourceforge.jArgo.jHelpManager;

import java.util.Arrays;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Help Entry class. This class provides structure to the help system, providing parent child relationships to
 * implement chapters and also providing maps between the name of a help page and the file that holds the HTML
 * for the page
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
     * Title of the entry.
     */
    private String theTitle = null;

    /**
     * Name of the entry.
     */
    private String theName = null;

    /**
     * FileName of the entry.
     */
    private String theFileName = null;

    /**
     * Children of the entry.
     */
    private HelpEntry[] theChildren = null;

    /**
     * TreePath of the entry.
     */
    private TreePath thePath = null;

    /**
     * HelpPage.
     */
    private HelpPage thePage = null;

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
    protected HelpEntry[] getChildren() {
        return theChildren;
    }

    /**
     * Obtain the tree path.
     * @return the tree path
     */
    public TreePath getTreePath() {
        return thePath;
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
    protected static HelpEntry[] getHelpEntryArray(final Element pElement) throws HelpException {
        Node myNode;
        Element myChild;
        HelpEntry myEntry;
        HelpEntry[] myEntries = null;

        /* Loop through the children */
        for (myNode = pElement.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {

            /* Skip nonElement nodes */
            if (myNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            /* Access node as element */
            myChild = (Element) myNode;

            /* Create an entry based on this node */
            myEntry = new HelpEntry(myChild);

            /* If this is the first child */
            if (myEntries == null) {
                /* Allocate a single entry array and store the element */
                myEntries = new HelpEntry[1];
                myEntries[0] = myEntry;

                /* else we already have an entry */
            } else {
                /* Extend the array and add entry as last element */
                myEntries = Arrays.copyOf(myEntries, myEntries.length + 1);
                myEntries[myEntries.length - 1] = myEntry;
            }
        }

        /* Return the entries */
        return myEntries;
    }

    /**
     * Constructor for an HTML element built from an XML node.
     * @param pElement the XML element describing the help entry
     * @throws HelpException on error
     */
    public HelpEntry(final Element pElement) throws HelpException {
        /* Reject entry if it is not a HelpElement */
        if (!pElement.getNodeName().equals(ELEMENT_HELP)) {
            throw new HelpException("Invalid element name: " + pElement.getNodeName());
        }

        /* Access the name of the element */
        theName = pElement.getAttribute(ATTR_NAME);
        if (theName == null) {
            throw new HelpException("Node has no associated name");
        }

        /* Access the title of the element and default it if required */
        theTitle = pElement.getAttribute(ATTR_TITLE);
        if (theTitle.equals("")) {
            theTitle = theName;
        }

        /* Access the filename for the element and default it if required */
        theFileName = pElement.getAttribute(ATTR_FILENAME);
        if (theFileName.equals("")) {
            theFileName = null;
        }

        /* If the node has sub-elements */
        if (pElement.hasChildNodes()) {
            /* Parse the children */
            theChildren = getHelpEntryArray(pElement);
        }
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
    }

    /**
     * Constructor for a table of contents element.
     * @param pName the name by which this entry is referenced
     * @param pTitle the title for this page in the table of contents
     * @param pChildren the children for this element
     */
    public HelpEntry(final String pName,
                     final String pTitle,
                     final HelpEntry[] pChildren) {
        theName = pName;
        theTitle = pTitle;
        theChildren = Arrays.copyOf(pChildren, pChildren.length);
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
                        final HelpEntry[] pChildren) {
        theName = pName;
        theTitle = pTitle;
        theFileName = pFileName;
        theChildren = Arrays.copyOf(pChildren, pChildren.length);
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

    /**
     * Construct a top level Tree Node from a set of help entries.
     * @param pTitle the title for the tree
     * @param pEntries the help entries
     * @return the Tree node
     */
    protected static DefaultMutableTreeNode createTree(final String pTitle,
                                                       final HelpEntry[] pEntries) {
        /* Create an initial tree node */
        DefaultMutableTreeNode myTree = new DefaultMutableTreeNode(pTitle);

        /* Add the entries into the node */
        addHelpEntries(myTree, pEntries);

        /* Return the tree */
        return myTree;
    }

    /**
     * Add array of Help entries.
     * @param pNode the node to add to
     * @param pEntries the entries to add
     */
    private static void addHelpEntries(final DefaultMutableTreeNode pNode,
                                       final HelpEntry[] pEntries) {
        DefaultMutableTreeNode myNode;

        /* Loop through the entries */
        for (HelpEntry myEntry : pEntries) {
            /* Create a new entry and add it to the node */
            myNode = new DefaultMutableTreeNode(myEntry);
            pNode.add(myNode);

            /* Access the tree path for this item */
            myEntry.thePath = new TreePath(myNode.getPath());

            /* If we have children */
            if (myEntry.getChildren() != null) {
                /* Add the children into the tree */
                addHelpEntries(myNode, myEntry.getChildren());
            }
        }
    }
}
