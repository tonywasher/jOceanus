/*
 * Tethys: GUI Utilities
 * Copyright 2012-2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sourceforge.joceanus.tethys.core.pane;

import io.github.tonywasher.joceanus.oceanus.event.OceanusEventManager;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.pane.TethysUITabPaneManager;
import net.sourceforge.joceanus.tethys.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;

/**
 * Tab Manager.
 */
public abstract class TethysUICoreTabPaneManager
        extends TethysUICoreComponent
        implements TethysUITabPaneManager {
    /**
     * The gui factory.
     */
    private final TethysUICoreFactory<?> theGuiFactory;

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<TethysUIEvent> theEventManager;

    /**
     * The first child item.
     */
    private TethysUICoreTabItem theFirstChild;

    /**
     * The last child item.
     */
    private TethysUICoreTabItem theLastChild;

    /**
     * Is the pane enabled?
     */
    private boolean isEnabled;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    protected TethysUICoreTabPaneManager(final TethysUICoreFactory<?> pFactory) {
        theGuiFactory = pFactory;
        theId = pFactory.getNextId();
        theEventManager = new OceanusEventManager<>();
        isEnabled = true;
    }

    @Override
    public Integer getId() {
        return theId;
    }

    @Override
    public OceanusEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public TethysUICoreTabItem findItemByName(final String pName) {
        /* loop through children */
        TethysUICoreTabItem myChild = theFirstChild;
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

    @Override
    public TethysUICoreTabItem findItemByIndex(final int pIndex) {
        /* loop through children */
        TethysUICoreTabItem myChild = theFirstChild;
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

    @Override
    public void enableItemByName(final String pName,
                                 final boolean pEnabled) {
        /* Look up child and adjust */
        final TethysUICoreTabItem myItem = findItemByName(pName);
        if (myItem != null) {
            myItem.setEnabled(pEnabled);
        }
    }

    /**
     * Notify of selection.
     *
     * @param pItem the item that has been selected
     */
    protected void notifySelection(final Object pItem) {
        theEventManager.fireEvent(TethysUIEvent.NEWVALUE, pItem);
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        /* If we are changing enabled state */
        if (pEnabled != isEnabled) {
            /* Set new enabled state */
            isEnabled = pEnabled;

            /* enable the tab */
            enablePane(isEnabled);

            /* loop through children */
            TethysUICoreTabItem myChild = theFirstChild;
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
     *
     * @param pEnabled true/false
     */
    protected abstract void enablePane(boolean pEnabled);

    /**
     * TabItem class.
     */
    public abstract static class TethysUICoreTabItem
            implements TethysUITabItem {
        /**
         * The pane to which this item belongs.
         */
        private final TethysUICoreTabPaneManager thePane;

        /**
         * The name of this item.
         */
        private final String theName;

        /**
         * The id of this item.
         */
        private final Integer theId;

        /**
         * The previous sibling of this item.
         */
        private TethysUICoreTabItem thePrevSibling;

        /**
         * The next sibling of this item.
         */
        private TethysUICoreTabItem theNextSibling;

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
         *
         * @param pPane the containing pane
         * @param pName the name of the tab
         */
        protected TethysUICoreTabItem(final TethysUICoreTabPaneManager pPane,
                                      final String pName) {
            /* Store parameters */
            thePane = pPane;
            theName = pName;
            isVisible = true;
            isEnabled = true;

            /* Create id */
            theId = pPane.theGuiFactory.getNextId();

            /* If the pane already has children */
            final TethysUICoreTabItem myChild = thePane.theLastChild;
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
        public String getName() {
            return theName;
        }

        @Override
        public Integer getId() {
            return theId;
        }

        @Override
        public TethysUICoreTabPaneManager getPane() {
            return thePane;
        }

        @Override
        public boolean isVisible() {
            return isVisible;
        }

        @Override
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
         *
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
         * Notify of selection.
         */
        protected void notifySelection() {
            getPane().notifySelection(this);
        }

        /**
         * Count previous visible items.
         *
         * @return the count
         */
        public int countPreviousVisibleSiblings() {
            /* Determine the previous visible sibling */
            int myCount = 0;
            TethysUICoreTabItem mySibling = thePrevSibling;
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
