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
package net.sourceforge.joceanus.jtethys.ui.api.menu;

import java.util.function.Consumer;

/**
 * MenuBar Manager.
 */
public interface TethysUIMenuBarManager {
    /**
     * Add subMenu.
     * @param pId the id of the subMenu
     * @return the new subMenu
     */
    TethysUIMenuSubMenu newSubMenu(TethysUIMenuId pId);

    /**
     * Set visible state for element.
     * @param pId the id of the element
     * @param pVisible true/false
     */
    void setVisible(TethysUIMenuId pId,
                    boolean pVisible);

    /**
     * Set enabled state for element.
     * @param pId the id of the element
     * @param pEnabled true/false
     */
    void setEnabled(TethysUIMenuId pId,
                    boolean pEnabled);

    /**
     * Look up subMenu.
     * @param pId the id of the element
     * @return the subMenu
     */
    TethysUIMenuSubMenu lookUpSubMenu(TethysUIMenuId pId);

    /**
     * MenuId
     */
    interface TethysUIMenuId {
    }

    /**
     * MenuElement.
     */
    interface TethysUIMenuElement {
        /**
         * Obtain the id.
         * @return the id
         */
        TethysUIMenuId getId();

        /**
         * Is the menu enabled?
         * @return true/false
         */
        boolean isEnabled();

        /**
         * Set the enabled state of the menu.
         * @param pEnabled true/false
         */
        void setEnabled(boolean pEnabled);
    }

    /**
     * SubMenu.
     */
    interface TethysUIMenuSubMenu
            extends TethysUIMenuElement {
        /**
         * Add Separator.
         */
        void newSeparator();

        /**
         * Add subMenu.
         * @param pId the id of the subMenu
         * @return the new subMenu
         */
        TethysUIMenuSubMenu newSubMenu(TethysUIMenuId pId);

        /**
         * Add MenuItem.
         * @param pId the id of the item
         * @param pAction the action
         * @return the new item
         */
        TethysUIMenuItem newMenuItem(TethysUIMenuId pId,
                                     Consumer<TethysUIMenuId> pAction);

        /**
         * Clear items.
         */
        void clearItems();

        /**
         * Count items.
         * @return the count of items
         */
        int countItems();
    }

    /**
     * MenuItem.
     */
    interface TethysUIMenuItem
            extends TethysUIMenuElement {
    }
}
