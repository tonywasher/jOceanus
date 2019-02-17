/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui;

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Tab Manager.
 */
public abstract class TethysTabPaneManager
        implements TethysEventProvider<TethysUIEvent>, TethysComponent {
    /**
     * The Gui Manager.
     */
    private final TethysGuiFactory theGuiFactory;

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The Padding.
     */
    private Integer thePadding;

    /**
     * The Title.
     */
    private String theTitle;

    /**
     * The first child item.
     */
    private TethysTabItem theFirstChild;

    /**
     * The last child item.
     */
    private TethysTabItem theLastChild;

    /**
     * Is the pane enabled?
     */
    private boolean isEnabled;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysTabPaneManager(final TethysGuiFactory pFactory) {
        theGuiFactory = pFactory;
        theId = theGuiFactory.getNextId();
        theEventManager = new TethysEventManager<>();
        isEnabled = true;
    }

    @Override
    public Integer getId() {
        return theId;
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the Border Padding.
     * @return the Padding.
     */
    protected Integer getBorderPadding() {
        return thePadding;
    }

    /**
     * Obtain the Border Title.
     * @return the Title.
     */
    protected String getBorderTitle() {
        return theTitle;
    }

    /**
     * Set the Border Padding.
     * @param pPadding the border padding
     */
    public void setBorderPadding(final Integer pPadding) {
        thePadding = pPadding;
    }

    /**
     * Set the Border Title.
     * @param pTitle the border title
     */
    public void setBorderTitle(final String pTitle) {
        theTitle = pTitle;
    }

    /**
     * Set the Preferred Width.
     * @param pWidth the width
     */
    public abstract void setPreferredWidth(Integer pWidth);

    /**
     * Set the Preferred Height.
     * @param pHeight the height
     */
    public abstract void setPreferredHeight(Integer pHeight);

    /**
     * Obtain selected tab.
     * @return the selected tab
     */
    public abstract TethysTabItem getSelectedTab();

    /**
     * find Item by name.
     * @param pName the name of the item
     * @return the item (or null)
     */
    public TethysTabItem findItemByName(final String pName) {
        /* loop through children */
        TethysTabItem myChild = theFirstChild;
        while (myChild != null) {
            /* Break if we have found the item */
            if (myChild.getName().equals(pName)) {
                break;
            }

            /* Move to next child */
            myChild = myChild.theNextSibling;
        }

        /* return the relevant child */
        return myChild;
    }

    /**
     * find visible Item by index.
     * @param pIndex the index of the item
     * @return the item (or null)
     */
    public TethysTabItem findItemByIndex(final int pIndex) {
        /* loop through children */
        TethysTabItem myChild = theFirstChild;
        int myIndex = 0;
        while (myChild != null) {
            /* If the child is visible */
            if (myChild.isVisible()) {
                /* If this is the required index */
                if (pIndex == myIndex) {
                    break;
                }
                myIndex++;
            }

            /* Move to next child */
            myChild = myChild.theNextSibling;
        }

        /* return the relevant child */
        return myChild;
    }

    /**
     * enable Item by name.
     * @param pName the name of the item
     * @param pEnabled the enabled state
     */
    public void enableItemByName(final String pName,
                                 final boolean pEnabled) {
        /* Look up child and adjust */
        final TethysTabItem myItem = findItemByName(pName);
        if (myItem != null) {
            myItem.setEnabled(pEnabled);
        }
    }

    /**
     * Notify of selection.
     * @param pItem the item that has been selected
     */
    protected void notifySelection(final Object pItem) {
        theEventManager.fireEvent(TethysUIEvent.NEWVALUE, pItem);
    }

    /**
     * Add tab item.
     * @param pName the name
     * @param pItem the item
     * @return the new tab item
     */
    public abstract TethysTabItem addTabItem(String pName,
                                             TethysComponent pItem);

    @Override
    public void setEnabled(final boolean pEnabled) {
        /* If we are changing enabled state */
        if (pEnabled != isEnabled) {
            /* Set new enabled state */
            isEnabled = pEnabled;

            /* enable the tab */
            enablePane(isEnabled);

            /* loop through children */
            TethysTabItem myChild = theFirstChild;
            while (myChild != null) {
                /* If the child is visible */
                if (myChild.isVisible()) {
                    /* set correct enabled status */
                    myChild.enableTab(pEnabled
                                      && myChild.isEnabled());
                }

                /* Move to next child */
                myChild = myChild.theNextSibling;
            }
        }
    }

    /**
     * Enable/disable the pane.
     * @param pEnabled true/false
     */
    protected abstract void enablePane(boolean pEnabled);

    /**
     * TabItem class.
     */
    public abstract static class TethysTabItem
            implements TethysComponent {
        /**
         * The pane to which this item belongs.
         */
        private final TethysTabPaneManager thePane;

        /**
         * The id.
         */
        private final Integer theId;

        /**
         * The name of this item.
         */
        private final String theName;

        /**
         * The previous sibling of this item.
         */
        private TethysTabItem thePrevSibling;

        /**
         * The next sibling of this item.
         */
        private TethysTabItem theNextSibling;

        /**
         * Is the item visible (i.e. part of the actual tabs)?
         */
        private boolean isVisible;

        /**
         * Is the item enabled?
         */
        private boolean isEnabled;

        /**
         * Constructor.
         * @param pPane the containing pane
         * @param pName the name of the tab
         */
        protected TethysTabItem(final TethysTabPaneManager pPane,
                                final String pName) {
            /* Store parameters */
            thePane = pPane;
            theName = pName;
            isVisible = true;
            isEnabled = true;

            /* Determine the id */
            theId = pPane.theGuiFactory.getNextId();

            /* If the pane already has children */
            final TethysTabItem myChild = thePane.theLastChild;
            if (myChild != null) {
                /* Link to last child */
                myChild.theNextSibling = this;
                thePrevSibling = myChild;

                /* else set as first child */
            } else {
                thePane.theFirstChild = this;
            }

            /* Add as last child of pane */
            thePane.theLastChild = this;
        }

        @Override
        public Integer getId() {
            return theId;
        }

        /**
         * Obtain the name.
         * @return the name
         */
        public String getName() {
            return theName;
        }

        /**
         * Obtain the tree.
         * @return the tree
         */
        public TethysTabPaneManager getPane() {
            return thePane;
        }

        /**
         * Is the item visible?
         * @return true/false
         */
        public boolean isVisible() {
            return isVisible;
        }

        /**
         * Is the item enabled?
         * @return true/false
         */
        public boolean isEnabled() {
            return isEnabled;
        }

        @Override
        public void setVisible(final boolean pVisible) {
            /* If we are changing visibility */
            if (pVisible != isVisible) {
                /* Set new visibility */
                isVisible = pVisible;

                /* If we are showing the item */
                if (pVisible) {
                    /* Attach to parent at required position */
                    attachToPane();

                    /* make sure that we have correct enable state */
                    enableTab(isEnabled);

                    /* else just detach item */
                } else {
                    detachFromPane();
                }
            }
        }

        @Override
        public void setEnabled(final boolean pEnabled) {
            /* If we are changing enabled state */
            if (pEnabled != isEnabled) {
                /* Set new enabled state */
                isEnabled = pEnabled;

                /* If the pane is enabled and the tab is visible */
                if (thePane.isEnabled
                    && isVisible) {
                    /* enable the tab */
                    enableTab(isEnabled);
                }
            }
        }

        /**
         * Enable/disable the tab.
         * @param pEnabled true/false
         */
        protected abstract void enableTab(boolean pEnabled);

        /**
         * Attach to pane.
         */
        protected abstract void attachToPane();

        /**
         * Detach from pane.
         */
        protected abstract void detachFromPane();

        /**
         * Select item.
         */
        public abstract void selectItem();

        /**
         * Notify of selection.
         */
        protected void notifySelection() {
            getPane().notifySelection(this);
        }

        /**
         * Count previous visible items.
         * @return the count
         */
        public int countPreviousVisibleSiblings() {
            /* Determine the previous visible sibling */
            int myCount = 0;
            TethysTabItem mySibling = thePrevSibling;
            while (mySibling != null) {
                if (mySibling.isVisible) {
                    myCount++;
                }
                mySibling = mySibling.thePrevSibling;
            }
            return myCount;
        }
    }
}
