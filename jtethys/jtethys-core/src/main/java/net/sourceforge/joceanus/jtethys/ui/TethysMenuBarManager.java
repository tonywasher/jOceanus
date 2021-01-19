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
package net.sourceforge.joceanus.jtethys.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * MenuBar Manager.
 */
public abstract class TethysMenuBarManager {
    /**
     * The Element Map.
     */
    private final Map<Object, TethysMenuElement<?>> theElementMap;

    /**
     * Constructor.
     */
    protected TethysMenuBarManager() {
        /* Create the map */
        theElementMap = new HashMap<>();
    }

    /**
     * Add subMenu.
     * @param <I> the type of the id
     * @param pId the id of the subMenu
     * @return the new subMenu
     */
    public abstract <I> TethysMenuSubMenu<I> newSubMenu(I pId);

    /**
     * Set visible state for element.
     * @param <I> the type of the id
     * @param pId the id of the element
     * @param pVisible true/false
     */
    public <I> void setVisible(final I pId,
                               final boolean pVisible) {
        final TethysMenuElement<?> myElement = theElementMap.get(pId);
        if (myElement != null) {
            myElement.setVisible(pVisible);
        }
    }

    /**
     * Set enabled state for element.
     * @param <I> the type of the id
     * @param pId the id of the element
     * @param pEnabled true/false
     */
    public <I> void setEnabled(final I pId,
                               final boolean pEnabled) {
        final TethysMenuElement<?> myElement = theElementMap.get(pId);
        if (myElement != null) {
            myElement.setEnabled(pEnabled);
        }
    }

    /**
     * Look up subMenu.
     * @param <I> the type of the id
     * @param pId the id of the element
     * @return the subMenu
     */
    @SuppressWarnings("unchecked")
    public <I> TethysMenuSubMenu<I> lookUpSubMenu(final I pId) {
        final TethysMenuElement<?> myElement = theElementMap.get(pId);
        return myElement instanceof TethysMenuSubMenu
                                                      ? (TethysMenuSubMenu<I>) myElement
                                                      : null;
    }

    /**
     * MenuElement.
     * @param <I> the id type
     */
    public abstract static class TethysMenuElement<I> {
        /**
         * The Manager.
         */
        private final TethysMenuBarManager theManager;

        /**
         * The Id.
         */
        private final I theId;

        /**
         * Is the element enabled?
         */
        private boolean isEnabled;

        /**
         * Constructor.
         * @param pManager the manager
         * @param pId the id
         */
        protected TethysMenuElement(final TethysMenuBarManager pManager,
                                    final I pId) {
            /* record details */
            theManager = pManager;
            theId = pId;
            isEnabled = true;

            /* Access the element map */
            final Map<Object, TethysMenuElement<?>> myElementMap = pManager.theElementMap;

            /* Check uniqueness of item */
            if (myElementMap.containsKey(pId)) {
                throw new IllegalArgumentException("Duplicate MenuId: " + pId);
            }

            /* Store into map */
            myElementMap.put(pId, this);
        }

        /**
         * Obtain the manager.
         * @return the manager
         */
        protected TethysMenuBarManager getManager() {
            return theManager;
        }

        /**
         * Obtain the id.
         * @return the id
         */
        public I getId() {
            return theId;
        }

        /**
         * Is the menu enabled?
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
        protected abstract void enableItem(boolean pEnabled);

        /**
         * Set item visibility.
         * @param pVisible true/false
         */
        protected abstract void setVisible(boolean pVisible);
    }

    /**
     * SubMenu.
     * @param <S> the id type
     */
    public abstract static class TethysMenuSubMenu<S>
            extends TethysMenuElement<S> {
        /**
         * The item count.
         */
        private int theItemCount;

        /**
         * Constructor.
         * @param pManager the manager
         * @param pId the id
         */
        protected TethysMenuSubMenu(final TethysMenuBarManager pManager,
                                    final S pId) {
            super(pManager, pId);
        }

        /**
         * Add Separator.
         */
        public abstract void newSeparator();

        /**
         * Add subMenu.
         * @param <I> the type of the id
         * @param pId the id of the subMenu
         * @return the new subMenu
         */
        public abstract <I> TethysMenuSubMenu<I> newSubMenu(I pId);

        /**
         * Add MenuItem.
         * @param <I> the type of the id
         * @param pId the id of the item
         * @param pAction the action
         * @return the new item
         */
        public abstract <I> TethysMenuItem<I> newMenuItem(I pId,
                                                          Consumer<I> pAction);

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
    }

    /**
     * MenuItem.
     * @param <I> the id type
     */
    public abstract static class TethysMenuItem<I>
            extends TethysMenuElement<I> {
        /**
         * The consumer.
         */
        private Consumer<I> theAction;

        /**
         * Constructor.
         * @param pManager the manager
         * @param pId the id
         * @param pAction the action
         */
        protected TethysMenuItem(final TethysMenuBarManager pManager,
                                 final I pId,
                                 final Consumer<I> pAction) {
            super(pManager, pId);
            theAction = pAction;
        }

        /**
         * notify of the action.
         */
        protected void notifyAction() {
            theAction.accept(getId());
        }
    }
}
