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
package net.sourceforge.joceanus.tethys.ui.api.pane;

import net.sourceforge.joceanus.tethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIEvent;

/**
 * Tab Manager.
 */
public interface TethysUITabPaneManager
        extends TethysEventProvider<TethysUIEvent>, TethysUIComponent {
    /**
     * Obtain selected tab.
     * @return the selected tab
     */
    TethysUITabItem getSelectedTab();

    /**
     * find Item by name.
     * @param pName the name of the item
     * @return the item (or null)
     */
    TethysUITabItem findItemByName(String pName);

    /**
     * find visible Item by index.
     * @param pIndex the index of the item
     * @return the item (or null)
     */
    TethysUITabItem findItemByIndex(int pIndex);

    /**
     * enable Item by name.
     * @param pName the name of the item
     * @param pEnabled the enabled state
     */
    void enableItemByName(String pName,
                          boolean pEnabled);

    /**
     * Add tab item.
     * @param pName the name
     * @param pItem the item
     * @return the new tab item
     */
    TethysUITabItem addTabItem(String pName,
                               TethysUIComponent pItem);

    /**
     * Tab Iten.
     */
    interface TethysUITabItem {
        /**
         * Obtain the name.
         * @return the name
         */
        String getName();

        /**
         * Obtain the name.
         * @return the name
         */
        Integer getId();

        /**
         * Obtain the pane.
         * @return the pane
         */
        TethysUITabPaneManager getPane();

        /**
         * Is the item visible?
         * @return true/false
         */
        boolean isVisible();

        /**
         * Is the item enabled?
         * @return true/false
         */
        boolean isEnabled();

        /**
         * Set Enabled status.
         * @param pEnabled true/false
         */
        void setEnabled(boolean pEnabled);

        /**
         * Set Visible.
         * @param pVisible true/false
         */
        void setVisible(boolean pVisible);

        /**
         * Select item.
         */
        void selectItem();
    }
}
