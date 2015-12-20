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
package net.sourceforge.joceanus.jtethys.ui;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataException;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Tree Manager.
 * @param <T> the item type
 * @param <N> the Node type
 */
public abstract class TethysTreeManager<T, N>
        implements TethysEventProvider {
    /**
     * Value updated.
     */
    public static final int ACTION_NEW_VALUE = TethysScrollButtonManager.ACTION_NEW_VALUE;

    /**
     * The map of items.
     */
    private final Map<String, TethysTreeItem<T, N>> theItemMap;

    /**
     * The Event Manager.
     */
    private final TethysEventManager theEventManager;

    /**
     * Is the tree visible (i.e. part of the actual tree)?
     */
    private boolean isVisible;

    /**
     * The root of the tree.
     */
    private TethysTreeItem<T, N> theRoot;

    /**
     * The root name.
     */
    private String theRootName = "TreeRoot";

    /**
     * Constructor.
     */
    protected TethysTreeManager() {
        theItemMap = new HashMap<String, TethysTreeItem<T, N>>();
        theEventManager = new TethysEventManager();
    }

    @Override
    public TethysEventRegistrar getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the node.
     * @return the node.
     */
    public abstract N getNode();

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
     * @param pActionId the actionId
     * @param pValue the relevant value
     */
    protected void fireEvent(final int pActionId, final Object pValue) {
        theEventManager.fireActionEvent(pActionId, pValue);
    }

    /**
     * Set the root node.
     * @param pRoot the root node
     */
    protected void setRoot(final TethysTreeItem<T, N> pRoot) {
        theRoot = pRoot;
    }

    /**
     * Obtain the root.
     * @return the root
     */
    public TethysTreeItem<T, N> getRoot() {
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

    /**
     * Set visibility.
     * @param pVisible true/false
     */
    public void setVisible(final boolean pVisible) {
        /* If visibility is changing */
        if (isVisible != pVisible) {
            /* record new status */
            isVisible = pVisible;

            /* If we are making the tree visible */
            if (isVisible) {
                /* Attach the root and visible children to the tree */
                theRoot.attachToTree();
            } else {
                /* Detach the root and children from the tree */
                theRoot.detachFromTree();

                /* Detach the tree */
                detachTree();
            }
        }
    }

    /**
     * Detach the tree.
     */
    protected abstract void detachTree();

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
    public TethysTreeItem<T, N> lookUpItem(final String pName) {
        return theItemMap.get(pName);
    }

    /**
     * Register item.
     * @param pItem the item to register
     * @throws OceanusException on error
     */
    protected void registerItem(final TethysTreeItem<T, N> pItem) throws OceanusException {
        /* Access unique names */
        String myName = pItem.getName();

        /* If this name already exists */
        if (theItemMap.get(myName) != null) {
            throw new TethysDataException("Name not unique");
        }

        /* register item */
        theItemMap.put(myName, pItem);
    }

    /**
     * DeRegister item.
     * @param pItem the item to deRegister
     */
    protected void deRegisterItem(final TethysTreeItem<T, N> pItem) {
        /* Access unique names */
        String myName = pItem.getName();

        /* Remove the name if it exists */
        theItemMap.remove(myName);
    }

    /**
     * Add item to root.
     * @param pName the name
     * @param pItem the item
     * @return the new tree item
     * @throws OceanusException on error
     */
    public abstract TethysTreeItem<T, N> addRootItem(final String pName,
                                                     final T pItem) throws OceanusException;

    /**
     * Add item to parent.
     * @param pParent the parent
     * @param pName the name
     * @param pItem the item
     * @return the new tree item
     * @throws OceanusException on error
     */
    public abstract TethysTreeItem<T, N> addChildItem(final TethysTreeItem<T, N> pParent,
                                                      final String pName,
                                                      final T pItem) throws OceanusException;

    /**
     * TreeItem class.
     * @param <X> the data type
     * @param <C> the component type
     */
    public abstract static class TethysTreeItem<X, C> {
        /**
         * The unique name of this item.
         */
        private final String theName;

        /**
         * The tree to which this item belongs.
         */
        private final TethysTreeManager<X, C> theTree;

        /**
         * The parent of this item.
         */
        private final TethysTreeItem<X, C> theParent;

        /**
         * The first child of this item.
         */
        private TethysTreeItem<X, C> theFirstChild;

        /**
         * The last child of this item.
         */
        private TethysTreeItem<X, C> theLastChild;

        /**
         * The previous sibling of this item.
         */
        private TethysTreeItem<X, C> thePrevSibling;

        /**
         * The next sibling of this item.
         */
        private TethysTreeItem<X, C> theNextSibling;

        /**
         * The underlying item.
         */
        private X theItem;

        /**
         * Is the item visible (i.e. part of the actual tree)?
         */
        private boolean isVisible;

        /**
         * Constructor for root item.
         * @param pTree the tree
         */
        protected TethysTreeItem(final TethysTreeManager<X, C> pTree) {
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
         * @throws OceanusException on error
         */
        protected TethysTreeItem(final TethysTreeManager<X, C> pTree,
                                 final TethysTreeItem<X, C> pParent,
                                 final String pName,
                                 final X pItem) throws OceanusException {
            /* Store parameters */
            theTree = pTree;
            theParent = pParent;
            theName = pName;
            theItem = pItem;

            /* If we have a parent */
            if (theParent != null) {
                /* If we already have children */
                TethysTreeItem<X, C> myChild = theParent.theLastChild;
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

            /* Initially visible */
            isVisible = true;
        }

        /**
         * Obtain the item.
         * @return the item
         */
        public X getItem() {
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
        public TethysTreeItem<X, C> getParent() {
            return theParent;
        }

        /**
         * Obtain the tree.
         * @return the tree
         */
        public TethysTreeManager<X, C> getTree() {
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
        public void setItem(final X pItem) {
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

                /* If the tree is visible */
                if (theTree.isVisible()) {
                    /* If we are showing the item */
                    if (pVisible) {
                        /* Ensure that the parent is visible */
                        if (!theParent.isVisible()) {
                            theParent.setVisible(true);
                        }

                        /* Attach to parent at required position */
                        int myPos = countPreviousVisibleSiblings();
                        attachAsChildNo(myPos);

                        /* else just detach item and children */
                    } else {
                        detachFromTree();
                    }
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
            TethysTreeItem<X, C> mySibling = thePrevSibling;
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
            TethysTreeItem<X, C> myChild = theFirstChild;
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
            TethysTreeItem<X, C> myChild = theFirstChild;
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
                TethysTreeItem<X, C> myCurr = theFirstChild;
                theFirstChild = theFirstChild.theNextSibling;
                myCurr.theNextSibling = null;
                if (theFirstChild != null) {
                    theFirstChild.thePrevSibling = null;
                }
            }

            /* Clear the last child */
            theLastChild = null;
        }

        @Override
        public String toString() {
            return isRoot()
                            ? theTree.theRootName
                            : theItem.toString();
        }
    }
}
