/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.tethys.api.menu;

import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import net.sourceforge.joceanus.tethys.api.base.TethysUIIcon;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;

/**
 * ScrollMenu.
 * @param <T> the value type
 */
public interface TethysUIScrollMenu<T>
        extends OceanusEventProvider<TethysUIEvent> {
    /**
     * Obtain the selected value.
     * @return the selected value
     */
    TethysUIScrollItem<T> getSelectedItem();

    /**
     * is the menu empty?
     * @return true/false
     */
    boolean isEmpty();

    /**
     * Set the number of items in the scrolling portion of the menu.
     * @param pMaxDisplayItems the maximum number of items to display
     * @throws IllegalArgumentException if pMaxDisplayItems is 0 or negative
     */
    void setMaxDisplayItems(int pMaxDisplayItems);

    /**
     * Set whether menu should auto-close on selection of a toggle item.
     * @param pCloseOnToggle true/false
     */
    void setCloseOnToggle(boolean pCloseOnToggle);

    /**
     * Remove all contents.
     */
    void removeAllItems();

    /**
     * Add Element.
     * @param pValue the value
     * @return the item added
     */
    TethysUIScrollItem<T> addItem(T pValue);

    /**
     * Add Element.
     * @param pValue the value
     * @param pName the display name
     * @return the item added
     */
    TethysUIScrollItem<T> addItem(T pValue,
                                  String pName);

    /**
     * Add Element.
     * @param pValue the value
     * @param pGraphic the icon for the item
     * @return the item added
     */
    TethysUIScrollItem<T> addItem(T pValue,
                                  TethysUIIcon pGraphic);

    /**
     * Add Null Element.
     * @param pName the display name
     * @return the item added
     */
    TethysUIScrollItem<T> addNullItem(String pName);

    /**
     * Add Null Element.
     * @param pName the display name
     * @param pGraphic the icon for the item
     * @return the item added
     */
    TethysUIScrollItem<T> addNullItem(String pName,
                                      TethysUIIcon pGraphic);

    /**
     * Add Element.
     * @param pValue the value
     * @param pName the display name
     * @param pGraphic the icon for the item
     * @return the item added
     */
    TethysUIScrollItem<T> addItem(T pValue,
                                  String pName,
                                  TethysUIIcon pGraphic);

    /**
     * Add subMenu.
     * @param pName the display name
     * @return the menu added
     */
    TethysUIScrollSubMenu<T> addSubMenu(String pName);

    /**
     * Add subMenu.
     * @param pName the display name
     * @param pGraphic the icon for the menu
     * @return the menu added
     */
    TethysUIScrollSubMenu<T> addSubMenu(String pName,
                                        TethysUIIcon pGraphic);

    /**
     * Add New toggle item.
     * @param pItem the available item
     * @return the added item
     */
    TethysUIScrollToggle<T> addToggleItem(T pItem);

    /**
     * Add New toggle item.
     * @param pItem the available item
     * @param pName the display name
     * @return the added item
     */
    TethysUIScrollToggle<T> addToggleItem(T pItem,
                                          String pName);
}
