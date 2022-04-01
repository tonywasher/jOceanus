/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.core.menu;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIMenuBarManager;

/**
 * MenuBar Manager.
 */
public abstract class TethysUICoreMenuBarManager
    implements TethysUIMenuBarManager {
    /**
     * The Element Map.
     */
    private final Map<TethysUIMenuId, TethysUICoreMenuElement> theElementMap;

    /**
     * Constructor.
     */
    protected TethysUICoreMenuBarManager() {
        /* Create the map */
        theElementMap = new HashMap<>();
    }

    @Override
    public void setVisible(final TethysUIMenuId pId,
                           final boolean pVisible) {
        final TethysUICoreMenuElement myElement = theElementMap.get(pId);
        if (myElement != null) {
            myElement.setVisible(pVisible);
        }
    }

    @Override
    public void setEnabled(final TethysUIMenuId pId,
                           final boolean pEnabled) {
        final TethysUICoreMenuElement myElement = theElementMap.get(pId);
        if (myElement != null) {
            myElement.setEnabled(pEnabled);
        }
    }

    @Override
    public TethysUIMenuSubMenu lookUpSubMenu(final TethysUIMenuId pId) {
        final TethysUICoreMenuElement myElement = theElementMap.get(pId);
        return myElement instanceof TethysUIMenuSubMenu
                ? (TethysUIMenuSubMenu) myElement
                : null;
    }

    /**
     * MenuElement.
     */
    public abstract static class TethysUICoreMenuElement
            implements TethysUIMenuElement {
        /**
         * The Manager.
         */
        private final TethysUICoreMenuBarManager theManager;

        /**
         * The Id.
         */
        private final TethysUIMenuId theId;

        /**
         * Is the element enabled?
         */
        private boolean isEnabled;

        /**
         * Constructor.
         * @param pManager the manager
         * @param pId the id
         */
        protected TethysUICoreMenuElement(final TethysUICoreMenuBarManager pManager,
                                          final TethysUIMenuId pId) {
            /* record details */
            theManager = pManager;
            theId = pId;
            isEnabled = true;

            /* Access the element map */
            final Map<TethysUIMenuId, TethysUICoreMenuElement> myElementMap = pManager.theElementMap;

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
        protected TethysUICoreMenuBarManager getManager() {
            return theManager;
        }

        @Override
        public TethysUIMenuId getId() {
            return theId;
        }

        @Override
        public boolean isEnabled() {
            return isEnabled;
        }

        @Override
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
     */
    public abstract static class TethysUICoreMenuSubMenu
            extends TethysUICoreMenuElement
            implements TethysUIMenuSubMenu {
        /**
         * The item count.
         */
        private int theItemCount;

        /**
         * Constructor.
         * @param pManager the manager
         * @param pId the id
         */
        protected TethysUICoreMenuSubMenu(final TethysUICoreMenuBarManager pManager,
                                          final TethysUIMenuId pId) {
            super(pManager, pId);
        }

        @Override
        public void clearItems() {
            theItemCount = 0;
        }

        /**
         * Increment count.
         */
        protected void incrementItemCount() {
            theItemCount++;
        }

        @Override
        public int countItems() {
            return theItemCount;
        }
    }

    /**
     * MenuItem.
     */
    public abstract static class TethysUICoreMenuItem
            extends TethysUICoreMenuElement
            implements TethysUIMenuItem {
        /**
         * The consumer.
         */
        private Consumer<TethysUIMenuId> theAction;

        /**
         * Constructor.
         * @param pManager the manager
         * @param pId the id
         * @param pAction the action
         */
        protected TethysUICoreMenuItem(final TethysUICoreMenuBarManager pManager,
                                       final TethysUIMenuId pId,
                                       final Consumer<TethysUIMenuId> pAction) {
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
