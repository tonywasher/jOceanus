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

import java.awt.Color;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject.JDataContents;
import net.sourceforge.JDataManager.JDataObject.JDataValues;

/**
 * Data Manager.
 * @author Tony Washer
 */
public class JDataManager {
    /**
     * HTML formatter.
     */
    private JDataHTML theFormatter = null;

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
     * The next entry index.
     */
    private int theNextIndex = 0;

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
     * Get formatter.
     * @return the formatter
     */
    protected JDataHTML getFormatter() {
        return theFormatter;
    }

    /**
     * Constructor.
     */
    public JDataManager() {
        /* Create the root node */
        theRoot = new JDataEntry(WINDOW_TITLE);

        /* Create the tree model */
        theModel = new DefaultTreeModel(theRoot.getNode());

        /* Create the formatter */
        theFormatter = new JDataHTML();
    }

    /**
     * Set new formatter.
     * @param pStandard the standard colour
     * @param pChanged the changed colour
     * @param pLink the link colour
     * @param pChgLink the changed link colour
     */
    public void setFormatter(Color pStandard,
                             Color pChanged,
                             Color pLink,
                             Color pChgLink) {
        /* Set the colours */
        theFormatter = new JDataHTML(pStandard, pChanged, pLink, pChgLink);

        /* If we have a data window */
        if (theWindow != null) {
            /* Set the new formatter */
            theWindow.setFormatter(theFormatter);
        }
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
         * The index.
         */
        private final int theIndex;

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
        protected String getName() {
            return theName;
        }

        /**
         * Get object.
         * @return the object
         */
        protected Object getObject() {
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
        protected TreePath getPath() {
            return thePath;
        }

        /**
         * Is the entry visible.
         * @return true/false
         */
        protected boolean isVisible() {
            return isVisible;
        }

        /**
         * Does the entry have children.
         * @return true/false
         */
        protected boolean hasChildren() {
            return hasChildren;
        }

        /**
         * Obtain the index.
         * @return the index
         */
        protected int getIndex() {
            return theIndex;
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

            /* Determine index */
            theIndex = theNextIndex++;
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
         * Set Focus onto a child of this debug entry.
         * @param pName the name of the child
         */
        public void setFocus(final String pName) {
            /* Loop through the children */
            int iCount = theNode.getChildCount();
            for (int i = 0; i < iCount; i++) {
                /* Access child */
                DefaultMutableTreeNode myChild = (DefaultMutableTreeNode) theNode.getChildAt(i);
                JDataEntry myEntry = (JDataEntry) myChild.getUserObject();

                /* If we match the object */
                if (pName.equals(myEntry.theName)) {
                    /* Set the focus and return */
                    myEntry.setFocus();
                    return;
                }
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

            /* If we have contents */
            if (JDataContents.class.isInstance(pObject)) {
                /* Access the object */
                JDataContents myContent = (JDataContents) pObject;
                Object myValue;
                ValueSet myValues = null;

                /* Access valueSet if it exists */
                if (JDataValues.class.isInstance(pObject)) {
                    myValues = ((JDataValues) pObject).getValueSet();
                }

                /* Loop through the data fields */
                JDataFields myFields = myContent.getDataFields();
                Iterator<JDataField> myIterator = myFields.fieldIterator();
                while (myIterator.hasNext()) {
                    JDataField myField = myIterator.next();

                    /* Access the value */
                    if ((myField.isValueSetField()) && (myValues != null)) {
                        myValue = myValues.getValue(myField);
                    } else {
                        myValue = myContent.getFieldValue(myField);
                    }

                    /* If the field is a List that has contents */
                    if ((myValue instanceof List) && (myValue instanceof JDataContents)) {
                        /* Access as list */
                        List<?> myList = (List<?>) myValue;

                        /* If the list is not empty */
                        if (myList.size() > 0) {
                            /* Add as a child */
                            addChildEntry(this, myField.getName(), myValue);
                        }
                    }
                }
            }

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
