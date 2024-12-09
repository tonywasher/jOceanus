/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.tethys.javafx.control;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;

import net.sourceforge.joceanus.tethys.api.base.TethysUIIconId;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.core.control.TethysUICoreTreeManager;
import net.sourceforge.joceanus.tethys.javafx.base.TethysUIFXIcon;
import net.sourceforge.joceanus.tethys.javafx.base.TethysUIFXNode;

/**
 * Tree Manager.
 *
 * @param <T> the data type
 */
public class TethysUIFXTreeManager<T>
        extends TethysUICoreTreeManager<T> {
    /**
     * The Node.
     */
    private final TethysUIFXNode theNode;

    /**
     * The treeView.
     */
    private final TreeView<TethysUIFXTreeItem<T>> theTree;

    /**
     * The root.
     */
    private final TethysUIFXTreeItem<T> theRoot;

    /**
     * The focused item.
     */
    private TethysUIFXTreeItem<T> theFocusedItem;

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     */
    TethysUIFXTreeManager(final TethysUICoreFactory<?> pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create the tree */
        theRoot = new TethysUIFXTreeItem<>(this);
        theTree = new TreeView<>(theRoot.getNode());
        theNode = new TethysUIFXNode(theTree);
        setRoot(theRoot);

        /* Configure the tree */
        theTree.setEditable(false);
        theTree.setShowRoot(false);

        /* Add listener */
        theTree.getSelectionModel().selectedItemProperty().addListener((v, o, n) -> {
            final TethysUIFXTreeItem<T> myValue = n == null
                    ? null
                    : n.getValue();
            fireEvent(TethysUIEvent.NEWVALUE, myValue == null
                    ? null
                    : myValue.getItem());
        });
    }

    @Override
    public TethysUIFXNode getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theTree.setDisable(!pEnabled);
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theTree.setPrefWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theTree.setPrefHeight(pHeight);
    }

    @Override
    public TethysUIFXTreeItem<T> getRoot() {
        return theRoot;
    }

    @Override
    public T getSelectedItem() {
        final TreeItem<TethysUIFXTreeItem<T>> myItem = theTree.getSelectionModel().getSelectedItem();
        final TethysUIFXTreeItem<T> myValue = myItem == null
                ? null
                : myItem.getValue();
        return myValue == null
                ? null
                : myValue.getItem();
    }

    @Override
    public TethysUIFXTreeItem<T> lookUpItem(final String pName) {
        return (TethysUIFXTreeItem<T>) super.lookUpItem(pName);
    }

    @Override
    public void setRootVisible() {
        theTree.setShowRoot(true);
    }

    @Override
    public boolean lookUpAndSelectItem(final String pName) {
        /* Look up the item */
        final TethysUIFXTreeItem<T> myItem = lookUpItem(pName);

        /* If we found the item */
        if (myItem != null) {
            /* Set focus on the item */
            setFocusedItem(myItem);
        }

        /* Return result */
        return myItem != null;
    }

    @Override
    protected void applyFocus() {
        if (theFocusedItem != null) {
            setFocusedItem(theFocusedItem);
        }
    }

    /**
     * Set focused item.
     *
     * @param pItem the item
     */
    void setFocusedItem(final TethysUIFXTreeItem<T> pItem) {
        /* Record the item */
        theFocusedItem = pItem;

        /* Ensure the visibility */
        pItem.setVisible(true);

        /* If the tree is visible */
        if (isVisible()) {
            /* Access parent and ensure that it is expanded */
            final TethysUIFXTreeNode<T> myParent = pItem.getParent().getNode();
            myParent.setExpanded(true);

            /* Scroll to the item */
            final TethysUIFXTreeNode<T> myNode = pItem.getNode();
            final int myIndex = theTree.getRow(myNode);
            theTree.scrollTo(myIndex);

            /* Select this item */
            theTree.getSelectionModel().select(myNode);
        }
    }

    @Override
    public TethysUIFXTreeItem<T> addRootItem(final String pName,
                                           final T pItem) {
        return new TethysUIFXTreeItem<>(this, theRoot, pName, pItem);
    }

    @Override
    public TethysUIFXTreeItem<T> addChildItem(final TethysUITreeItem<T> pParent,
                                              final String pName,
                                              final T pItem) {
        return new TethysUIFXTreeItem<>(this, (TethysUIFXTreeItem<T>) pParent, pName, pItem);
    }

    /**
     * TreeItem class.
     *
     * @param <T> the data type
     */
    public static class TethysUIFXTreeItem<T>
            extends TethysUICoreTreeItem<T> {
        /**
         * Associated Node.
         */
        private TethysUIFXTreeNode<T> theNode;

        /**
         * Constructor for root item.
         *
         * @param pTree the tree
         */
        TethysUIFXTreeItem(final TethysUIFXTreeManager<T> pTree) {
            /* build underlying item */
            super(pTree);

            /* Create the node */
            theNode = new TethysUIFXTreeNode<>(this);
        }

        /**
         * Constructor.
         *
         * @param pTree   the tree
         * @param pParent the parent
         * @param pName   the unique name of the item
         * @param pItem   the tree item
         */
        public TethysUIFXTreeItem(final TethysUIFXTreeManager<T> pTree,
                                  final TethysUIFXTreeItem<T> pParent,
                                  final String pName,
                                  final T pItem) {
            /* build underlying item */
            super(pTree, pParent, pName, pItem);
        }

        @Override
        public TethysUIFXTreeManager<T> getTree() {
            return (TethysUIFXTreeManager<T>) super.getTree();
        }

        @Override
        public TethysUIFXTreeItem<T> getParent() {
            return (TethysUIFXTreeItem<T>) super.getParent();
        }

        /**
         * Obtain the node.
         *
         * @return the node
         */
        TethysUIFXTreeNode<T> getNode() {
            return theNode;
        }

        @Override
        protected void attachToTree() {
            /* Obtain the parent */
            final TethysUIFXTreeItem<T> myParent = getParent();

            /* If we are not the root */
            if (myParent != null) {
                /* Create the node */
                theNode = new TethysUIFXTreeNode<>(this);

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

            /* Obtain the parent */
            final TethysUIFXTreeItem<T> myParent = getParent();

            /* Delete reference if we are not root */
            if (theNode != null
                    && myParent != null) {
                /* remove from list of children */
                myParent.getNode().getChildren().remove(theNode);
                theNode = null;
            }
        }

        @Override
        protected void attachAsChildNo(final int pChildNo) {
            /* Create the node */
            theNode = new TethysUIFXTreeNode<>(this);

            /* Obtain the parent */
            final TethysUIFXTreeItem<T> myParent = getParent();

            /* Add at index in list */
            myParent.getNode().getChildren().add(pChildNo, theNode);

            /* attach all visible children */
            super.attachToTree();
        }

        @Override
        public void setFocus() {
            getTree().setFocusedItem(this);
        }

        @Override
        public void setIcon(final TethysUIIconId pIconId) {
            super.setIcon(pIconId);
            if (pIconId != null) {
                final TethysUIFXIcon myIcon = (TethysUIFXIcon) getTree().getIcon(pIconId);
                final ImageView myImage = new ImageView();
                myImage.setImage(myIcon.getImage());
                myImage.setFitWidth(ICONWIDTH);
                myImage.setPreserveRatio(true);
                myImage.setSmooth(true);
                myImage.setCache(true);
                theNode.setGraphic(myImage);
            } else {
                theNode.setGraphic(null);
            }
        }
    }

    /**
     * TreeNode class.
     *
     * @param <X> the data type
     */
    private static final class TethysUIFXTreeNode<X>
            extends TreeItem<TethysUIFXTreeItem<X>> {
        /**
         * Constructor.
         *
         * @param pItem the tree item
         */
        TethysUIFXTreeNode(final TethysUIFXTreeItem<X> pItem) {
            super(pItem);
        }

        @Override
        public String toString() {
            final TethysUIFXTreeItem<X> myValue = getValue();
            return myValue == null
                    ? null
                    : myValue.toString();
        }
    }
}
