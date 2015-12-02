/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.viewer.swing;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;

/**
 * Data Manager.
 * @author Tony Washer
 */
public class MetisSwingViewerEntry
        extends MetisViewerEntry {
    /**
     * Is the entry visible.
     */
    private boolean isVisible = false;

    /**
     * Tree Model.
     */
    private final DefaultTreeModel theModel;

    /**
     * The tree node for the entry.
     */
    private final DefaultMutableTreeNode theNode;

    /**
     * The parent of the entry.
     */
    private DefaultMutableTreeNode theParent = null;

    /**
     * The tree path.
     */
    private TreePath thePath = null;

    /**
     * Constructor.
     * @param pManager the viewer manager
     * @param pName the entry name
     * @param pId the entry id
     */
    protected MetisSwingViewerEntry(final MetisSwingViewerManager pManager,
                                    final String pName,
                                    final Integer pId) {
        /* Store parameters */
        super(pManager, pName, pId);

        /* Create node */
        theModel = pManager.getModel();
        theNode = new DefaultMutableTreeNode(this);
    }

    /**
     * Is the entry visible.
     * @return true/false
     */
    protected boolean isVisible() {
        return isVisible;
    }

    /**
     * Get node.
     * @return the node
     */
    protected DefaultMutableTreeNode getNode() {
        return theNode;
    }

    /**
     * Get path.
     * @return the path
     */
    protected TreePath getPath() {
        return thePath;
    }

    @Override
    protected MetisSwingViewerManager getManager() {
        return (MetisSwingViewerManager) super.getManager();
    }

    @Override
    public void addAsChildOf(final MetisViewerEntry pParent) {
        /* If this is a swing entry */
        if (pParent instanceof MetisSwingViewerEntry) {
            /* Add as child of parent */
            MetisSwingViewerEntry myParent = (MetisSwingViewerEntry) pParent;
            theParent = myParent.getNode();
            theParent.add(theNode);

            /* Access the tree path */
            thePath = new TreePath(theNode.getPath());

            /* Note that we are visible */
            isVisible = true;
        }
    }

    @Override
    public void addAsFirstChildOf(final MetisViewerEntry pParent) {
        /* If this is a swing entry */
        if (pParent instanceof MetisSwingViewerEntry) {
            /* Add as child of parent */
            MetisSwingViewerEntry myParent = (MetisSwingViewerEntry) pParent;
            theParent = myParent.getNode();
            theParent.insert(theNode, 0);

            /* Access the tree path */
            thePath = new TreePath(theNode.getPath());

            /* Note that we are visible */
            isVisible = true;
        }
    }

    @Override
    public void addAsRootChild() {
        /* Add as child of root node */
        MetisSwingViewerEntry myRoot = getManager().getRoot();
        theParent = myRoot.getNode();
        theParent.add(theNode);

        /* Access the tree path */
        thePath = new TreePath(theNode.getPath());

        /* Note that we are visible */
        isVisible = true;
    }

    @Override
    public void setFocus(final String pName) {
        /* Loop through the children */
        int iCount = theNode.getChildCount();
        for (int i = 0; i < iCount; i++) {
            /* Access child */
            DefaultMutableTreeNode myChild = (DefaultMutableTreeNode) theNode.getChildAt(i);
            MetisViewerEntry myEntry = (MetisViewerEntry) myChild.getUserObject();

            /* If we match the object */
            if (pName.equals(myEntry.getName())) {
                /* Set the focus and return */
                myEntry.setFocus();
                return;
            }
        }
    }

    @Override
    public void hideEntry() {
        /* If the node is visible */
        if (theNode.getParent() != null) {
            /* Remove it from the view using the model */
            theModel.removeNodeFromParent(theNode);
        }

        /* Note that this entry is hidden */
        isVisible = false;
    }

    @Override
    public void showEntry() {
        /* If the node is not visible */
        if (theNode.getParent() == null) {
            /* Insert it into the view using model */
            theModel.insertNodeInto(theNode, theParent, theParent.getChildCount());
        }

        /* Note that this entry is visible */
        isVisible = true;
    }

    @Override
    public void showPrimeEntry() {
        /* If the node is not visible */
        if (theNode.getParent() == null) {
            /* Insert it into the view using model */
            theModel.insertNodeInto(theNode, theParent, 0);
        }

        /* Note that this entry is visible */
        isVisible = true;
    }

    @Override
    public void setObject(final Object pObject) {
        /* Set the new object */
        super.setObject(pObject);

        /* Note that this entry and its children have changed */
        theModel.nodeStructureChanged(theNode);

        /* Ensure that display is updated if this is active */
        getManager().updateWindow(this);
    }

    @Override
    public void setChanged() {
        /* If the node is visible */
        if (isVisible) {
            /* Note the object has changed if it exists */
            if (getObject() != null) {
                theModel.nodeChanged(theNode);
            }

            /* Note that any children have changed */
            theModel.nodeStructureChanged(theNode);

            /* Ensure that display is updated if this is active */
            getManager().updateWindow(this);
        }
    }

    @Override
    public void removeChildren() {
        /* Remove all the children */
        theNode.removeAllChildren();
    }
}
