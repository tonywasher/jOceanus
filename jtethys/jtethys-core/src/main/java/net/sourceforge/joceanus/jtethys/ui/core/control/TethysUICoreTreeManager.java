/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.core.control;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIIcon;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIIconId;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIXEvent;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUITreeManager;
import net.sourceforge.joceanus.jtethys.ui.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;

/**
 * Tree Manager.
 * @param <T> the item type
 */
public abstract class TethysUICoreTreeManager<T>
        extends TethysUICoreComponent
        implements TethysUITreeManager<T> {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(TethysUICoreTreeManager.class);

    /**
     * Icon width.
     */
    protected static final int ICONWIDTH = 16;

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * The guiFactory.
     */
    private final TethysUICoreFactory<?> theFactory;

    /**
     * The map of items.
     */
    private final Map<String, TethysUITreeItem<T>> theItemMap;

    /**
     * The map of icons.
     */
    private final Map<TethysUIIconId, TethysUIIcon> theIconMap;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIXEvent> theEventManager;

    /**
     * Is the tree visible (i.e. part of the actual tree)?
     */
    private boolean isVisible;

    /**
     * The root of the tree.
     */
    private TethysUICoreTreeItem<T> theRoot;

    /**
     * The root name.
     */
    private String theRootName = "TreeRoot";

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysUICoreTreeManager(final TethysUICoreFactory<?> pFactory) {
        theFactory = pFactory;
        theId = theFactory.getNextId();
        theItemMap = new HashMap<>();
        theIconMap = new HashMap<>();
        theEventManager = new TethysEventManager<>();
    }

    @Override
    public TethysEventRegistrar<TethysUIXEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public Integer getId() {
        return theId;
    }

    /**
     * Fire event.
     * @param pEventId the actionId
     * @param pValue the relevant value
     */
    protected void fireEvent(final TethysUIXEvent pEventId,
                             final Object pValue) {
        theEventManager.fireEvent(pEventId, pValue);
    }

    /**
     * Set the root node.
     * @param pRoot the root node
     */
    protected void setRoot(final TethysUICoreTreeItem<T> pRoot) {
        theRoot = pRoot;
    }

    /**
     * Obtain the root.
     * @return the root
     */
    public TethysUITreeItem<T> getRoot() {
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
     * LookUp item by name.
     * @param pName the name of the item
     * @return the item (or null)
     */
    public TethysUITreeItem<T> lookUpItem(final String pName) {
        return theItemMap.get(pName);
    }

    /**
     * Register item.
     * @param pItem the item to register
     */
    protected void registerItem(final TethysUICoreTreeItem<T> pItem) {
        /* Access unique names */
        final String myName = pItem.getName();

        /* If this name already exists */
        if (theItemMap.get(myName) != null) {
            LOGGER.error("Name not unique: %s", myName);
        }

        /* register item */
        theItemMap.put(myName, pItem);
    }

    /**
     * DeRegister item.
     * @param pItem the item to deRegister
     */
    protected void deRegisterItem(final TethysUITreeItem<T> pItem) {
        /* Access unique names */
        final String myName = pItem.getName();

        /* Remove the name if it exists */
        theItemMap.remove(myName);
    }

    /**
     * Apply the focus.
     */
    protected abstract void applyFocus();

    /**
     * Obtain the icon for the iconId.
     * @param pIconId the iconId
     * @return the icon
     */
    public TethysUIIcon getIcon(final TethysUIIconId pIconId) {
        TethysUIIcon myIcon = theIconMap.get(pIconId);
        if (myIcon == null) {
            myIcon = theFactory.resolveIcon(pIconId, ICONWIDTH);
            theIconMap.put(pIconId, myIcon);
        }
        return myIcon;
    }

    /**
     * TreeItem class.
     * @param <T> the data type
     */
    public abstract static class TethysUICoreTreeItem<T>
        implements TethysUITreeItem<T> {
        /**
         * The unique name of this item.
         */
        private final String theName;

        /**
         * The tree to which this item belongs.
         */
        private final TethysUICoreTreeManager<T> theTree;

        /**
         * The parent of this item.
         */
        private final TethysUICoreTreeItem<T> theParent;

        /**
         * The first child of this item.
         */
        private TethysUICoreTreeItem<T> theFirstChild;

        /**
         * The last child of this item.
         */
        private TethysUICoreTreeItem<T> theLastChild;

        /**
         * The previous sibling of this item.
         */
        private TethysUICoreTreeItem<T> thePrevSibling;

        /**
         * The next sibling of this item.
         */
        private TethysUICoreTreeItem<T> theNextSibling;

        /**
         * The underlying item.
         */
        private T theItem;

        /**
         * The iconId.
         */
        private TethysUIIconId theIcon;

        /**
         * Is the item visible (i.e. part of the actual tree)?
         */
        private boolean isVisible;

        /**
         * Constructor for root item.
         * @param pTree the tree
         */
        protected TethysUICoreTreeItem(final TethysUICoreTreeManager<T> pTree) {
            /* Store parameters */
            theTree = pTree;
            theParent = null;
            theName = null;
            theIcon = null;
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
        protected TethysUICoreTreeItem(final TethysUICoreTreeManager<T> pTree,
                                       final TethysUICoreTreeItem<T> pParent,
                                       final String pName,
                                       final T pItem) {
            /* Store parameters */
            theTree = pTree;
            theParent = pParent;
            theName = pName;
            theItem = pItem;
            theIcon = null;

            /* If we have a parent */
            if (theParent != null) {
                /* If we already have children */
                final TethysUICoreTreeItem<T> myChild = theParent.theLastChild;
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
         * Obtain the iconId.
         * @return the iconId
         */
        public TethysUIIconId getIconId() {
            return theIcon;
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
        public TethysUICoreTreeItem<T> getParent() {
            return theParent;
        }

        /**
         * Obtain the tree.
         * @return the tree
         */
        public TethysUITreeManager<T> getTree() {
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
         * Set the iconId.
         * @param pIconId the iconId
         */
        public void setIcon(final TethysUIIconId pIconId) {
            theIcon = pIconId;
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
                    final int myPos = countPreviousVisibleSiblings();
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
        protected abstract void attachAsChildNo(int pChildNo);

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
            TethysUICoreTreeItem<T> mySibling = thePrevSibling;
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
            TethysUICoreTreeItem<T> myChild = theFirstChild;
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
            TethysUICoreTreeItem<T> myChild = theFirstChild;
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
                final TethysUICoreTreeItem<T> myCurr = theFirstChild;
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
