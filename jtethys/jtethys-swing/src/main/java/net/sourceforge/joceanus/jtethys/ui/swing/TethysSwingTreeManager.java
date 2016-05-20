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
package net.sourceforge.joceanus.jtethys.ui.swing;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.joceanus.jtethys.ui.TethysTreeManager;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Swing Tree Manager.
 * @param <T> the data type
 */
public class TethysSwingTreeManager<T>
        extends TethysTreeManager<T, JComponent, Icon> {
    /**
     * The treeView.
     */
    private final JTree theTree;

    /**
     * The root.
     */
    private final TethysSwingTreeItem<T> theRoot;

    /**
     * The treeModel.
     */
    private final DefaultTreeModel theTreeModel;

    /**
     * The focused item.
     */
    private TethysSwingTreeItem<T> theFocusedItem;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysSwingTreeManager(final TethysSwingGuiFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create the tree */
        theRoot = new TethysSwingTreeItem<>(this);
        theTreeModel = new DefaultTreeModel(theRoot.getNode());
        theTree = new JTree(theTreeModel);
        TreeSelectionModel mySelectionModel = theTree.getSelectionModel();
        setRoot(theRoot);

        /* Configure the tree */
        theTree.setEditable(false);
        theTree.setRootVisible(false);
        theTree.setExpandsSelectedPaths(true);
        mySelectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        /* Create the listener */
        mySelectionModel.addTreeSelectionListener(e -> fireEvent(TethysUIEvent.NEWVALUE, getSelectedItemFromPath(e.getPath())));
    }

    @Override
    public JTree getNode() {
        return theTree;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theTree.setEnabled(pEnabled);
    }

    @Override
    public TethysSwingTreeItem<T> getRoot() {
        return theRoot;
    }

    @Override
    public void setRootVisible() {
        theTree.setRootVisible(true);
    }

    @Override
    public T getSelectedItem() {
        return getSelectedItemFromPath(theTree.getSelectionPath());
    }

    /**
     * Obtain selected item from path.
     * @param pPath the selected path
     * @return the selected item
     */
    @SuppressWarnings("unchecked")
    private T getSelectedItemFromPath(final TreePath pPath) {
        TethysSwingTreeNode<T> myNode = (TethysSwingTreeNode<T>) pPath.getLastPathComponent();
        return myNode.getValue().getItem();
    }

    @Override
    public TethysSwingTreeItem<T> lookUpItem(final String pName) {
        return (TethysSwingTreeItem<T>) super.lookUpItem(pName);
    }

    @Override
    protected void detachTree() {
        theRoot.removeChildren();
    }

    @Override
    public boolean lookUpAndSelectItem(final String pName) {
        /* Look up the item */
        TethysSwingTreeItem<T> myItem = lookUpItem(pName);

        /* If we found the item */
        if (myItem != null) {
            /* Set focus on the item */
            setFocusedItem(myItem);
        }

        /* Return result */
        return myItem != null;
    }

    @Override
    public TethysSwingTreeItem<T> addRootItem(final String pName,
                                              final T pItem) {
        return new TethysSwingTreeItem<>(this, theRoot, pName, pItem);
    }

    @Override
    public TethysSwingTreeItem<T> addChildItem(final TethysTreeItem<T, JComponent, Icon> pParent,
                                               final String pName,
                                               final T pItem) {
        return new TethysSwingTreeItem<>(this, (TethysSwingTreeItem<T>) pParent, pName, pItem);
    }

    @Override
    protected void applyFocus() {
        if (theFocusedItem != null) {
            setFocusedItem(theFocusedItem);
        }
    }

    /**
     * Set focused item.
     * @param pItem the item
     */
    private void setFocusedItem(final TethysSwingTreeItem<T> pItem) {
        /* Record the item */
        theFocusedItem = pItem;

        /* Ensure the visibility */
        pItem.setVisible(true);

        /* If the tree is visible */
        if (isVisible()) {
            /* Select this item */
            TreePath myPath = new TreePath(pItem.getNode().getPath());
            theTree.setSelectionPath(myPath);
            theTree.scrollPathToVisible(myPath);
        }
    }

    /**
     * TreeItem class.
     * @param <T> the data type
     */
    public static class TethysSwingTreeItem<T>
            extends TethysTreeItem<T, JComponent, Icon> {
        /**
         * Associated Node.
         */
        private TethysSwingTreeNode<T> theNode;

        /**
         * Constructor for root item.
         * @param pTree the tree
         */
        private TethysSwingTreeItem(final TethysSwingTreeManager<T> pTree) {
            /* build underlying item */
            super(pTree);

            /* Create the node */
            theNode = new TethysSwingTreeNode<>(this);
        }

        /**
         * Constructor.
         * @param pTree the tree
         * @param pParent the parent
         * @param pName the unique name of the item
         * @param pItem the tree item
         */
        public TethysSwingTreeItem(final TethysSwingTreeManager<T> pTree,
                                   final TethysSwingTreeItem<T> pParent,
                                   final String pName,
                                   final T pItem) {
            /* build underlying item */
            super(pTree, pParent, pName, pItem);
        }

        @Override
        public TethysSwingTreeManager<T> getTree() {
            return (TethysSwingTreeManager<T>) super.getTree();
        }

        @Override
        public TethysSwingTreeItem<T> getParent() {
            return (TethysSwingTreeItem<T>) super.getParent();
        }

        /**
         * Obtain the node.
         * @return the node
         */
        private TethysSwingTreeNode<T> getNode() {
            return theNode;
        }

        @Override
        protected void attachToTree() {
            /* Obtain the parent */
            TethysSwingTreeItem<T> myParent = getParent();

            /* Access the model */
            DefaultTreeModel myModel = getTree().theTreeModel;

            /* If we are not the root */
            if (myParent != null) {
                /* Create the node */
                theNode = new TethysSwingTreeNode<>(this);

                /* add to list of children */
                int myNumChildren = myModel.getChildCount(myParent.getNode());
                myModel.insertNodeInto(theNode, myParent.getNode(), myNumChildren);
            }

            /* handle children */
            super.attachToTree();

            /* If we are the root */
            if (myParent == null) {
                /* Ensure that the node is expanded */
                TreePath myPath = new TreePath(theNode);
                JTree myTree = getTree().theTree;
                if (myTree.isCollapsed(myPath)) {
                    myTree.expandPath(myPath);
                }
            }
        }

        @Override
        protected void detachFromTree() {
            /* handle children */
            super.detachFromTree();

            /* Delete reference if we are not root */
            if (getParent() != null) {
                /* Remove reference */
                getTree().theTreeModel.removeNodeFromParent(theNode);
                theNode = null;
            }
        }

        @Override
        protected void attachAsChildNo(final int pChildNo) {
            /* Create the node */
            theNode = new TethysSwingTreeNode<>(this);

            /* Obtain the parent */
            TethysSwingTreeItem<T> myParent = getParent();

            /* Ignore if we are the root */
            if (myParent != null) {
                /* Access the model */
                DefaultTreeModel myModel = getTree().theTreeModel;

                /* Add at index in list */
                myModel.insertNodeInto(theNode, myParent.getNode(), pChildNo);
            }

            /* attach all visible children */
            super.attachToTree();
        }

        @Override
        public void setFocus() {
            getTree().setFocusedItem(this);
        }
    }

    /**
     * TreeNode class.
     * @param <X> the data type
     */
    private static final class TethysSwingTreeNode<X>
            extends DefaultMutableTreeNode {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -5408260987317489068L;

        /**
         * Constructor.
         * @param pItem the tree item
         */
        private TethysSwingTreeNode(final TethysSwingTreeItem<X> pItem) {
            super(pItem);
        }

        /**
         * Obtain value.
         * @return the value
         */
        @SuppressWarnings("unchecked")
        protected TethysSwingTreeItem<X> getValue() {
            return (TethysSwingTreeItem<X>) getUserObject();
        }

        @Override
        public String toString() {
            TethysSwingTreeItem<X> myValue = getValue();
            return myValue == null
                                   ? null
                                   : myValue.toString();
        }
    }
}
