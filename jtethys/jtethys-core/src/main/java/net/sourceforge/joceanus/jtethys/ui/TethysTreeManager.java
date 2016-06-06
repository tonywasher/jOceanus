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
package net.sourceforge.joceanus.jtethys.ui;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Tree Manager.
 * @param <T> the item type
 * @param <N> the Node type
 * @param <I> the Icon type
 */
public abstract class TethysTreeManager<T, N, I>
        implements TethysEventProvider<TethysUIEvent>, TethysNode<N> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TethysTreeManager.class);

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * The map of items.
     */
    private final Map<String, TethysTreeItem<T, N, I>> theItemMap;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * Is the tree visible (i.e. part of the actual tree)?
     */
    private boolean isVisible;

    /**
     * The root of the tree.
     */
    private TethysTreeItem<T, N, I> theRoot;

    /**
     * The root name.
     */
    private String theRootName = "TreeRoot";

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysTreeManager(final TethysGuiFactory<N, I> pFactory) {
        theId = pFactory.getNextId();
        theItemMap = new HashMap<>();
        theEventManager = new TethysEventManager<>();
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public Integer getId() {
        return theId;
    }

    /**
     * Obtain the selected item.
     * @return the item.
     */
    public abstract T getSelectedItem();

    /**
     * Set the root as visible.
     */
    public abstract void setRootVisible();

    /**
     * Fire event.
     * @param pEventId the actionId
     * @param pValue the relevant value
     */
    protected void fireEvent(final TethysUIEvent pEventId,
                             final Object pValue) {
        theEventManager.fireEvent(pEventId, pValue);
    }

    /**
     * Set the root node.
     * @param pRoot the root node
     */
    protected void setRoot(final TethysTreeItem<T, N, I> pRoot) {
        theRoot = pRoot;
    }

    /**
     * Obtain the root.
     * @return the root
     */
    public TethysTreeItem<T, N, I> getRoot() {
        return theRoot;
    }

    /**
     * Set the root name.
     * @param pName the root name
     */
    public void setRootName(final String pName) {
        theRootName = pName;
    }

    /**
     * Is the tree visible?
     * @return true/false
     */
    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        /* If visibility is changing */
        if (isVisible != pVisible) {
            /* record new status */
            isVisible = pVisible;

            /* If we are making the tree visible */
            if (isVisible) {
                /* Attach the root and visible children to the tree */
                theRoot.attachToTree();
                applyFocus();

                /* else we are removing all nodes */
            } else {
                /* Detach the root and children from the tree */
                theRoot.detachFromTree();
            }
        }
    }

    /**
     * Select and display item.
     * @param pName the name of the item
     * @return was an item selected? true/false
     */
    public abstract boolean lookUpAndSelectItem(final String pName);

    /**
     * LookUp item by name.
     * @param pName the name of the item
     * @return the item (or null)
     */
    public TethysTreeItem<T, N, I> lookUpItem(final String pName) {
        return theItemMap.get(pName);
    }

    /**
     * Register item.
     * @param pItem the item to register
     */
    protected void registerItem(final TethysTreeItem<T, N, I> pItem) {
        /* Access unique names */
        String myName = pItem.getName();

        /* If this name already exists */
        if (theItemMap.get(myName) != null) {
            LOGGER.error("Name not unique: " + myName);
        }

        /* register item */
        theItemMap.put(myName, pItem);
    }

    /**
     * DeRegister item.
     * @param pItem the item to deRegister
     */
    protected void deRegisterItem(final TethysTreeItem<T, N, I> pItem) {
        /* Access unique names */
        String myName = pItem.getName();

        /* Remove the name if it exists */
        theItemMap.remove(myName);
    }

    /**
     * Apply the focus.
     */
    protected abstract void applyFocus();

    /**
     * Add item to root.
     * @param pName the name
     * @param pItem the item
     * @return the new tree item
     */
    public abstract TethysTreeItem<T, N, I> addRootItem(final String pName,
                                                        final T pItem);

    /**
     * Add item to parent.
     * @param pParent the parent
     * @param pName the name
     * @param pItem the item
     * @return the new tree item
     */
    public abstract TethysTreeItem<T, N, I> addChildItem(final TethysTreeItem<T, N, I> pParent,
                                                         final String pName,
                                                         final T pItem);

    /**
     * TreeItem class.
     * @param <T> the data type
     * @param <N> the node type
     * @param <I> the icon type
     */
    public abstract static class TethysTreeItem<T, N, I> {
        /**
         * The unique name of this item.
         */
        private final String theName;

        /**
         * The tree to which this item belongs.
         */
        private final TethysTreeManager<T, N, I> theTree;

        /**
         * The parent of this item.
         */
        private final TethysTreeItem<T, N, I> theParent;

        /**
         * The first child of this item.
         */
        private TethysTreeItem<T, N, I> theFirstChild;

        /**
         * The last child of this item.
         */
        private TethysTreeItem<T, N, I> theLastChild;

        /**
         * The previous sibling of this item.
         */
        private TethysTreeItem<T, N, I> thePrevSibling;

        /**
         * The next sibling of this item.
         */
        private TethysTreeItem<T, N, I> theNextSibling;

        /**
         * The underlying item.
         */
        private T theItem;

        /**
         * Is the item visible (i.e. part of the actual tree)?
         */
        private boolean isVisible;

        /**
         * Constructor for root item.
         * @param pTree the tree
         */
        protected TethysTreeItem(final TethysTreeManager<T, N, I> pTree) {
            /* Store parameters */
            theTree = pTree;
            theParent = null;
            theName = null;
            theItem = null;
            isVisible = false;
        }

        /**
         * Constructor.
         * @param pTree the tree
         * @param pParent the parent
         * @param pName the unique name of the item
         * @param pItem the contained item
         */
        protected TethysTreeItem(final TethysTreeManager<T, N, I> pTree,
                                 final TethysTreeItem<T, N, I> pParent,
                                 final String pName,
                                 final T pItem) {
            /* Store parameters */
            theTree = pTree;
            theParent = pParent;
            theName = pName;
            theItem = pItem;

            /* If we have a parent */
            if (theParent != null) {
                /* If we already have children */
                TethysTreeItem<T, N, I> myChild = theParent.theLastChild;
                if (myChild != null) {
                    /* Link to last child */
                    myChild.theNextSibling = this;
                    thePrevSibling = myChild;

                    /* else set as first child */
                } else {
                    theParent.theFirstChild = this;
                }

                /* Add as last child of parent */
                theParent.theLastChild = this;
            }

            /* If we have an item */
            if (pItem != null) {
                /* register it */
                theTree.registerItem(this);
            }

            /* Make visible */
            setVisible(true);
        }

        /**
         * Obtain the item.
         * @return the item
         */
        public T getItem() {
            return theItem;
        }

        /**
         * Obtain the unique name.
         * @return the name
         */
        public String getName() {
            return theName;
        }

        /**
         * Obtain the parent.
         * @return the parent
         */
        public TethysTreeItem<T, N, I> getParent() {
            return theParent;
        }

        /**
         * Obtain the tree.
         * @return the tree
         */
        public TethysTreeManager<T, N, I> getTree() {
            return theTree;
        }

        /**
         * Is the item visible?
         * @return true/false
         */
        public boolean isVisible() {
            return isVisible;
        }

        /**
         * Is the item root?
         * @return true/false
         */
        public boolean isRoot() {
            return theItem == null;
        }

        /**
         * Set the item.
         * @param pItem the item
         */
        public void setItem(final T pItem) {
            theItem = pItem;
        }

        /**
         * Set the visibility of the item.
         * @param pVisible true/false
         */
        public void setVisible(final boolean pVisible) {
            /* If we are changing visibility */
            if (pVisible != isVisible) {
                /* Set new visibility */
                isVisible = pVisible;

                /* Nothing further to do if we are not showing the tree */
                if (!theTree.isVisible()) {
                    return;
                }

                /* If we are showing the item */
                if (pVisible) {
                    /* Attach to parent at required position */
                    int myPos = countPreviousVisibleSiblings();
                    attachAsChildNo(myPos);

                    /* else just detach item and children */
                } else {
                    detachFromTree();
                }
            }
        }

        /**
         * Attach as particular child.
         * @param pChildNo the child #
         */
        protected abstract void attachAsChildNo(final int pChildNo);

        /**
         * Do we have children?
         * @return true/false
         */
        public boolean hasChildren() {
            return theFirstChild != null;
        }

        /**
         * Count previous visible items.
         * @return the count
         */
        public int countPreviousVisibleSiblings() {
            /* Determine the previous visible sibling */
            int myCount = 0;
            TethysTreeItem<T, N, I> mySibling = thePrevSibling;
            while (mySibling != null) {
                if (mySibling.isVisible) {
                    myCount++;
                }
                mySibling = mySibling.thePrevSibling;
            }
            return myCount;
        }

        /**
         * Attach this node and children to the tree.
         */
        protected void attachToTree() {
            /* loop through visible children */
            TethysTreeItem<T, N, I> myChild = theFirstChild;
            while (myChild != null) {
                /* If it is visible then attach to the tree */
                if (myChild.isVisible()) {
                    myChild.attachToTree();
                }
                myChild = myChild.theNextSibling;
            }
        }

        /**
         * Detach this node and children from the tree.
         */
        protected void detachFromTree() {
            /* loop through children */
            TethysTreeItem<T, N, I> myChild = theFirstChild;
            while (myChild != null) {
                /* Detach from tree */
                myChild.detachFromTree();
                myChild = myChild.theNextSibling;
            }
        }

        /**
         * Remove all children.
         */
        public void removeChildren() {
            /* Loop through the children */
            while (theFirstChild != null) {
                /* Remove children of child */
                theFirstChild.removeChildren();

                /* deRegister the item */
                theTree.deRegisterItem(theFirstChild);

                /* Unlink the child */
                TethysTreeItem<T, N, I> myCurr = theFirstChild;
                theFirstChild = theFirstChild.theNextSibling;
                myCurr.theNextSibling = null;
                if (theFirstChild != null) {
                    theFirstChild.thePrevSibling = null;
                }
            }

            /* Clear the last child */
            theLastChild = null;
        }

        /**
         * Set focus to this item.
         */
        public abstract void setFocus();

        @Override
        public String toString() {
            return isRoot()
                            ? theTree.theRootName
                            : theItem.toString();
        }
    }
}
