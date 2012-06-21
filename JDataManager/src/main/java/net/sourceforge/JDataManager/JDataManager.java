/*******************************************************************************
 * JDataManager: Java Data Manager
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
package net.sourceforge.JDataManager;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Data Manager.
 * @author Tony Washer
 */
public class JDataManager {
    /**
     * Tree Model.
     */
    private final DefaultTreeModel theModel;

    /**
     * The root of the tree.
     */
    private final JDataEntry theRoot;

    /**
     * The focused entry.
     */
    private JDataEntry theFocus = null;

    /**
     * The title.
     */
    private static final String WINDOW_TITLE = "Data Manager";

    /**
     * The owning window.
     */
    private JDataWindow theWindow = null;

    /**
     * Get tree model.
     * @return the model
     */
    public TreeModel getModel() {
        return theModel;
    }

    /**
     * Get title.
     * @return the title
     */
    public String getTitle() {
        return WINDOW_TITLE;
    }

    /**
     * Get focus.
     * @return the focus
     */
    public JDataEntry getFocus() {
        return theFocus;
    }

    /**
     * Constructor.
     */
    public JDataManager() {
        /* Create the root node */
        theRoot = new JDataEntry(WINDOW_TITLE);

        /* Create the tree model */
        theModel = new DefaultTreeModel(theRoot.getNode());
    }

    /**
     * Declare window object.
     * @param pWindow the window
     */
    public void declareWindow(final JDataWindow pWindow) {
        /* Store window */
        theWindow = pWindow;
    }

    /**
     * Create a child entry for parent.
     * @param pParent the parent to add to
     * @param pName the name of the new entry
     * @param pObject the object for the child
     * @return the new child entry
     */
    public JDataEntry addChildEntry(final JDataEntry pParent,
                                    final String pName,
                                    final Object pObject) {
        JDataEntry myEntry = new JDataEntry(pName);
        myEntry.addAsChildOf(pParent);
        myEntry.setObject(pObject);
        return myEntry;
    }

    /**
     * The Data Entry class.
     */
    public final class JDataEntry {
        /**
         * The name of the entry.
         */
        private final String theName;

        /**
         * The object for the entry.
         */
        private Object theObject = null;

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
         * Is the entry visible.
         */
        private boolean isVisible = false;

        /**
         * Does the entry have children.
         */
        private boolean hasChildren = false;

        /**
         * Get name.
         * @return the name
         */
        public String getName() {
            return theName;
        }

        /**
         * Get object.
         * @return the object
         */
        public Object getObject() {
            return theObject;
        }

        /**
         * Get node.
         * @return the node
         */
        public DefaultMutableTreeNode getNode() {
            return theNode;
        }

        /**
         * Get path.
         * @return the path
         */
        public TreePath getPath() {
            return thePath;
        }

        /**
         * Is the entry visible.
         * @return true/false
         */
        public boolean isVisible() {
            return isVisible;
        }

        /**
         * Does the entry have children.
         * @return true/false
         */
        public boolean hasChildren() {
            return hasChildren;
        }

        /**
         * Constructor.
         * @param pName the object name
         */
        public JDataEntry(final String pName) {
            /* Store name */
            theName = pName;

            /* Create node */
            theNode = new DefaultMutableTreeNode(this);
        }

        /**
         * Add as a child into the tree.
         * @param pParent the parent object
         */
        public void addAsChildOf(final JDataEntry pParent) {
            /* Add as child of parent */
            theParent = pParent.getNode();
            theParent.add(theNode);

            /* Access the tree path */
            thePath = new TreePath(theNode.getPath());

            /* Note that we are visible */
            isVisible = true;

            /* Note that parent has Children */
            pParent.hasChildren = true;
        }

        /**
         * Add as a child into the tree.
         * @param pParent the parent object
         */
        public void addAsFirstChildOf(final JDataEntry pParent) {
            /* Add as child of parent */
            theParent = pParent.getNode();
            theParent.insert(theNode, 0);

            /* Access the tree path */
            thePath = new TreePath(theNode.getPath());

            /* Note that we are visible */
            isVisible = true;

            /* Note that parent has Children */
            pParent.hasChildren = true;
        }

        /**
         * Add as a root child into the tree.
         */
        public void addAsRootChild() {
            /* Add as child of root node */
            theParent = theRoot.getNode();
            theParent.add(theNode);

            /* Access the tree path */
            thePath = new TreePath(theNode.getPath());

            /* Note that we are visible */
            isVisible = true;
        }

        @Override
        public String toString() {
            return theName;
        }

        /**
         * Set Focus onto this debug entry.
         */
        public void setFocus() {
            /* Record the focus */
            theFocus = this;

            /* If we have a window */
            if (theWindow != null) {
                /* Set selection path and ensure visibility */
                theWindow.displayData(this);
            }
        }

        /**
         * Hide the entry.
         */
        public void hideEntry() {
            /* If the node is visible */
            if (theNode.getParent() != null) {
                /* Remove it from the view using the model */
                theModel.removeNodeFromParent(theNode);
            }

            /* Note that this entry is hidden */
            isVisible = false;
        }

        /**
         * Ensure that the entry is visible.
         */
        public void showEntry() {
            /* If the node is not visible */
            if (theNode.getParent() == null) {
                /* Insert it into the view using model */
                theModel.insertNodeInto(theNode, theParent, theParent.getChildCount());
            }

            /* Note that this entry is visible */
            isVisible = true;
        }

        /**
         * Ensure that the entry is visible.
         */
        public void showPrimeEntry() {
            /* If the node is not visible */
            if (theNode.getParent() == null) {
                /* Insert it into the view using model */
                theModel.insertNodeInto(theNode, theParent, 0);
            }

            /* Note that this entry is visible */
            isVisible = true;
        }

        /**
         * Set the object referred to by the entry.
         * @param pObject the new object
         */
        public void setObject(final Object pObject) {
            /* Set the new object */
            theObject = pObject;

            /* Remove all the children */
            removeChildren();

            /* Add all the new children */
            // if (pObject != null)
            // pObject.addChildEntries(theManager, this);

            /* Note that this entry and its children have changed */
            theModel.nodeStructureChanged(theNode);

            /* Ensure that display is updated if this is active */
            if (theWindow != null) {
                theWindow.updateData(this);
            }
        }

        /**
         * Note that the object has been changed.
         */
        public void setChanged() {
            /* If the node is visible */
            if (isVisible) {
                /* Note the object has changed if it exists */
                if (theObject != null) {
                    theModel.nodeChanged(theNode);
                }

                /* Note that any children have changed */
                theModel.nodeStructureChanged(theNode);

                /* Ensure that display is updated if this is active */
                if (theWindow != null) {
                    theWindow.updateData(this);
                }
            }
        }

        /**
         * Remove children of an object.
         */
        public void removeChildren() {
            /* Remove all the children */
            theNode.removeAllChildren();
            hasChildren = false;
        }
    }
}
