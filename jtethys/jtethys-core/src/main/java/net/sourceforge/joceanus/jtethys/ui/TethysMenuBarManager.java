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

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * MenuBar Manager.
 * @param <T> the item type
 * @param <N> the Node type
 */
public abstract class TethysMenuBarManager<T, N>
        implements TethysEventProvider<TethysUIEvent> {
    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * Constructor.
     */
    protected TethysMenuBarManager() {
        theEventManager = new TethysEventManager<>();
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the node.
     * @return the node.
     */
    public abstract N getNode();

    /**
     * Add subMenu.
     * @param pText the menu text
     * @return the new subMenu
     */
    public abstract TethysMenuBarSubMenu addSubMenu(final String pText);

    /**
     * Notify of selection.
     * @param pItem the item that has been selected
     */
    protected void notifySelected(final TethysMenuBarItem pItem) {
        theEventManager.fireEvent(TethysUIEvent.NEWVALUE, pItem.getItem());
    }

    /**
     * SubMenu.
     */
    public abstract class TethysMenuBarSubMenu {
        /**
         * Is the menu enabled?
         */
        private boolean isEnabled;

        /**
         * The item count.
         */
        private int theItemCount;

        /**
         * Constructor.
         */
        protected TethysMenuBarSubMenu() {
            isEnabled = true;
        }

        /**
         * Is the menu enabled?
         * @return true/false
         */
        public boolean isEnabled() {
            return isEnabled;
        }

        /**
         * Add subMenu.
         * @param pText the menu text
         * @return the new subMenu
         */
        public abstract TethysMenuBarSubMenu addSubMenu(final String pText);

        /**
         * Add menuItem.
         * @param pItem the item
         * @return the new menu item
         */
        public abstract TethysMenuBarItem addMenuItem(final T pItem);

        /**
         * Add Separator.
         */
        public abstract void addSeparator();

        /**
         * Clear items.
         */
        public void clearItems() {
            theItemCount = 0;
        }

        /**
         * Increment count.
         */
        protected void incrementItemCount() {
            theItemCount++;
        }

        /**
         * Count items.
         * @return the count of items
         */
        public int countItems() {
            return theItemCount;
        }

        /**
         * Set the enabled state of the menu.
         * @param pEnabled true/false
         */
        public void setEnabled(final boolean pEnabled) {
            /* If we are changing enabled state */
            if (pEnabled != isEnabled) {
                /* Set new enabled state */
                isEnabled = pEnabled;

                /* enable the menu */
                enableMenu(isEnabled);
            }
        }

        /**
         * Enable/disable the menu.
         * @param pEnabled true/false
         */
        protected abstract void enableMenu(final boolean pEnabled);
    }

    /**
     * MenuItem.
     */
    public abstract class TethysMenuBarItem {
        /**
         * The item.
         */
        private final T theItem;

        /**
         * Is the item enabled?
         */
        private boolean isEnabled;

        /**
         * Constructor.
         * @param pItem the item
         */
        protected TethysMenuBarItem(final T pItem) {
            theItem = pItem;
            isEnabled = true;
        }

        /**
         * Obtain the item.
         * @return the item
         */
        public T getItem() {
            return theItem;
        }

        /**
         * Is the item enabled?
         * @return true/false
         */
        public boolean isEnabled() {
            return isEnabled;
        }

        /**
         * Set the enabled state of the menu.
         * @param pEnabled true/false
         */
        public void setEnabled(final boolean pEnabled) {
            /* If we are changing enabled state */
            if (pEnabled != isEnabled) {
                /* Set new enabled state */
                isEnabled = pEnabled;

                /* enable the item */
                enableItem(isEnabled);
            }
        }

        /**
         * Enable/disable the item.
         * @param pEnabled true/false
         */
        protected abstract void enableItem(final boolean pEnabled);

        /**
         * Notify selection.
         */
        protected void notifySelection() {
            notifySelected(this);
        }
    }
}
