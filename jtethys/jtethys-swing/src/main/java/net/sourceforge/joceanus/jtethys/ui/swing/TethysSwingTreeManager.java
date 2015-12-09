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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/src/main/java/net/sourceforge/joceanus/jtethys/swing/JIconButton.java $
 * $Revision: 570 $
 * $Author: Tony $
 * $Date: 2015-02-14 06:54:38 +0000 (Sat, 14 Feb 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.swing;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysTreeManager;

/**
 * Swing Tree Manager.
 * @param <T> the data type
 */
public class TethysSwingTreeManager<T>
        extends TethysTreeManager<T, JComponent> {
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
    private final TreeSelectionModel theModel;

    /**
     * Constructor.
     * @throws OceanusException on error
     */
    public TethysSwingTreeManager() throws OceanusException {
        /* Create the tree */
        theRoot = new TethysSwingTreeItem<T>(this);
        theTree = new JTree(theRoot.getNode());
        theModel = theTree.getSelectionModel();
        setRoot(theRoot);

        /* Configure the tree */
        theTree.setEditable(false);
        theTree.setRootVisible(false);
        theTree.setExpandsSelectedPaths(true);
        theModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        /* Create the listener */
        theModel.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(final TreeSelectionEvent e) {
                fireEvent(ACTION_NEW_VALUE, getSelectedItemFromPath(e.getPath()));
            }
        });
    }

    @Override
    public JTree getNode() {
        return theTree;
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
    public void lookUpAndSelectItem(final String pName) {
        /* Look up the item */
        TethysSwingTreeItem<T> myItem = lookUpItem(pName);

        /* If we found the item */
        if (myItem != null) {
            /* Select this item */
            TreePath myPath = new TreePath(myItem.getNode().getPath());
            theTree.setSelectionPath(myPath);
            theTree.scrollPathToVisible(myPath);
        }
    }

    /**
     * TreeItem class.
     * @param <X> the data type
     */
    public static class TethysSwingTreeItem<X>
            extends TethysTreeItem<X, JComponent> {
        /**
         * Associated Node.
         */
        private TethysSwingTreeNode<X> theNode;

        /**
         * Constructor for root item.
         * @param pTree the tree
         * @throws OceanusException on error
         */
        private TethysSwingTreeItem(final TethysSwingTreeManager<X> pTree) throws OceanusException {
            /* build underlying item */
            super(pTree);

            /* Create the node */
            theNode = new TethysSwingTreeNode<X>(this);
        }

        /**
         * Constructor.
         * @param pTree the tree
         * @param pParent the parent
         * @param pName the unique name of the item
         * @param pItem the tree item
         * @throws OceanusException on error
         */
        public TethysSwingTreeItem(final TethysSwingTreeManager<X> pTree,
                                   final TethysSwingTreeItem<X> pParent,
                                   final String pName,
                                   final X pItem) throws OceanusException {
            /* build underlying item */
            super(pTree, pParent, pName, pItem);
        }

        @Override
        public TethysSwingTreeManager<X> getTree() {
            return (TethysSwingTreeManager<X>) super.getTree();
        }

        @Override
        public TethysSwingTreeItem<X> getParent() {
            return (TethysSwingTreeItem<X>) super.getParent();
        }

        /**
         * Obtain the node
         * @return the node
         */
        private TethysSwingTreeNode<X> getNode() {
            return theNode;
        }

        @Override
        protected void attachToTree() {
            /* Obtain the parent */
            TethysSwingTreeItem<X> myParent = getParent();

            /* If we are not the root */
            if (myParent != null) {
                /* Create the node */
                theNode = new TethysSwingTreeNode<X>(this);

                /* add to list of children */
                myParent.getNode().add(theNode);
            }

            /* handle children */
            super.attachToTree();
        }

        @Override
        protected void detachFromTree() {
            /* handle children */
            super.detachFromTree();

            /* Delete reference if we are not root */
            if (getParent() != null) {
                theNode = null;
            }
        }

        @Override
        protected void attachAsChildNo(final int pChildNo) {
            /* Create the node */
            theNode = new TethysSwingTreeNode<X>(this);

            /* Obtain the parent */
            TethysSwingTreeItem<X> myParent = getParent();

            /* Ignore if we are the root */
            if (myParent != null) {
                /* Add at index in list */
                myParent.getNode().insert(theNode, pChildNo);
            }

            /* attach all visible children */
            super.attachToTree();
        }
    }

    /**
     * TreeNode class.
     * @param <X> the data type
     */
    private static class TethysSwingTreeNode<X>
            extends DefaultMutableTreeNode {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -5408260987317489068L;

        /**
         * Constructor.
         * @param pItem the tree item
         */
        public TethysSwingTreeNode(final TethysSwingTreeItem<X> pItem) {
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
