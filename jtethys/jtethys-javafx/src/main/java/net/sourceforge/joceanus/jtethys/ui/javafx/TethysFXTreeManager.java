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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysTreeManager;

/**
 * Tree Manager.
 * @param <T> the data type
 */
public class TethysFXTreeManager<T>
        extends TethysTreeManager<T, Node> {
    /**
     * The treeView.
     */
    private TreeView<TethysFXTreeItem<T>> theTree;

    /**
     * The root.
     */
    private final TethysFXTreeItem<T> theRoot;

    /**
     * Constructor.
     * @throws OceanusException on error
     */
    public TethysFXTreeManager() {
        /* Create the tree */
        theRoot = new TethysFXTreeItem<T>(this);
        theTree = new TreeView<TethysFXTreeItem<T>>(theRoot.getNode());
        setRoot(theRoot);

        /* Configure the tree */
        theTree.setEditable(false);
        theTree.setShowRoot(false);

        /* Add listener */
        theTree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<TethysFXTreeItem<T>>>() {
            @Override
            public void changed(final ObservableValue<? extends TreeItem<TethysFXTreeItem<T>>> pProperty,
                                final TreeItem<TethysFXTreeItem<T>> pOldValue,
                                final TreeItem<TethysFXTreeItem<T>> pNewValue) {
                fireEvent(ACTION_NEW_VALUE, pNewValue.getValue().getItem());
            }
        });
    }

    @Override
    public Node getNode() {
        return theTree;
    }

    @Override
    public TethysFXTreeItem<T> getRoot() {
        return theRoot;
    }

    @Override
    public T getSelectedItem() {
        return theTree.getSelectionModel().getSelectedItem().getValue().getItem();
    }

    @Override
    public TethysFXTreeItem<T> lookUpItem(final String pName) {
        return (TethysFXTreeItem<T>) super.lookUpItem(pName);
    }

    @Override
    protected void detachTree() {
        theRoot.removeChildren();
    }

    @Override
    public void setRootVisible() {
        theTree.setShowRoot(true);
    }

    @Override
    public boolean lookUpAndSelectItem(final String pName) {
        /* Look up the item */
        TethysFXTreeItem<T> myItem = lookUpItem(pName);
        boolean bFound = false;

        /* If we found the item */
        if (myItem != null) {
            /* Access parent and ensure that it is expanded */
            TethysFXTreeNode<T> myParent = myItem.getParent().getNode();
            myParent.setExpanded(true);

            /* Scroll to the item */
            TethysFXTreeNode<T> myNode = myItem.getNode();
            int myIndex = theTree.getRow(myNode);
            theTree.scrollTo(myIndex);

            /* Select this item */
            theTree.getSelectionModel().select(myNode);
            bFound = true;
        }

        /* Return result */
        return bFound;
    }

    @Override
    public TethysFXTreeItem<T> addRootItem(final String pName,
                                           final T pItem) throws OceanusException {
        return new TethysFXTreeItem<T>(this, theRoot, pName, pItem);
    }

    @Override
    public TethysFXTreeItem<T> addChildItem(final TethysTreeItem<T, Node> pParent,
                                            final String pName,
                                            final T pItem) throws OceanusException {
        return new TethysFXTreeItem<T>(this, (TethysFXTreeItem<T>) pParent, pName, pItem);
    }

    /**
     * TreeItem class.
     * @param <X> the data type
     */
    public static class TethysFXTreeItem<X>
            extends TethysTreeItem<X, Node> {
        /**
         * Associated Node.
         */
        private TethysFXTreeNode<X> theNode;

        /**
         * Constructor for root item.
         * @param pTree the tree
         */
        private TethysFXTreeItem(final TethysFXTreeManager<X> pTree) {
            /* build underlying item */
            super(pTree);

            /* Create the node */
            theNode = new TethysFXTreeNode<X>(this);
        }

        /**
         * Constructor.
         * @param pTree the tree
         * @param pParent the parent
         * @param pName the unique name of the item
         * @param pItem the tree item
         * @throws OceanusException on error
         */
        public TethysFXTreeItem(final TethysFXTreeManager<X> pTree,
                                final TethysFXTreeItem<X> pParent,
                                final String pName,
                                final X pItem) throws OceanusException {
            /* build underlying item */
            super(pTree, pParent, pName, pItem);
        }

        @Override
        public TethysFXTreeManager<X> getTree() {
            return (TethysFXTreeManager<X>) super.getTree();
        }

        @Override
        public TethysFXTreeItem<X> getParent() {
            return (TethysFXTreeItem<X>) super.getParent();
        }

        /**
         * Obtain the node.
         * @return the node
         */
        private TethysFXTreeNode<X> getNode() {
            return theNode;
        }

        @Override
        protected void attachToTree() {
            /* Obtain the parent */
            TethysFXTreeItem<X> myParent = getParent();

            /* If we are not the root */
            if (myParent != null) {
                /* Create the node */
                theNode = new TethysFXTreeNode<X>(this);

                /* add to list of children */
                myParent.getNode().getChildren().add(theNode);
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
            theNode = new TethysFXTreeNode<X>(this);

            /* Obtain the parent */
            TethysFXTreeItem<X> myParent = getParent();

            /* Ignore if we are the root */
            if (myParent != null) {
                /* Add at index in list */
                myParent.getNode().getChildren().add(pChildNo, theNode);
            }

            /* attach all visible children */
            super.attachToTree();
        }
    }

    /**
     * TreeNode class.
     * @param <X> the data type
     */
    private static final class TethysFXTreeNode<X>
            extends TreeItem<TethysFXTreeItem<X>> {
        /**
         * Constructor.
         * @param pItem the tree item
         */
        private TethysFXTreeNode(final TethysFXTreeItem<X> pItem) {
            super(pItem);
        }

        @Override
        public String toString() {
            TethysFXTreeItem<X> myValue = getValue();
            return myValue == null
                                   ? null
                                   : myValue.toString();
        }
    }
}
