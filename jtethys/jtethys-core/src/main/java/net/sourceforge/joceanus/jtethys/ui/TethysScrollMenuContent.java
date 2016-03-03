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

/**
 * ContextMenuItems.
 */
public abstract class TethysScrollMenuContent {
    /**
     * Default number of items for scroll window.
     */
    public static final int DEFAULT_ITEMCOUNT = 15;

    /**
     * Default icon width.
     */
    public static final int DEFAULT_ICONWIDTH = 16;

    /**
     * Initial scroll delay when hovering over icon.
     */
    public static final int INITIAL_SCROLLDELAY = 1000;

    /**
     * Default scroll delay when hovering over icon.
     */
    public static final int REPEAT_SCROLLDELAY = 150;

    /**
     * MaxDisplayItems error.
     */
    public static final String ERROR_MAXITEMS = "Maximum Display items must be greater than 0";

    /**
     * Private constructor.
     */
    private TethysScrollMenuContent() {
    }

    /**
     * ScrollMenu.
     * @param <T> the value type
     * @param <I> the Icon type
     */
    public interface TethysScrollMenu<T, I> {
        /**
         * Obtain the selected value.
         * @return the selected value
         */
        TethysScrollMenuItem<T> getSelectedItem();

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
        void setMaxDisplayItems(final int pMaxDisplayItems);

        /**
         * Set whether menu should auto-close on selection of a toggle item.
         * @param pCloseOnToggle true/false
         */
        void setCloseOnToggle(final boolean pCloseOnToggle);

        /**
         * Remove all contents.
         */
        void removeAllItems();

        /**
         * Add Element.
         * @param pValue the value
         * @return the item added
         */
        TethysScrollMenuItem<T> addItem(final T pValue);

        /**
         * Add Element.
         * @param pValue the value
         * @param pName the display name
         * @return the item added
         */
        TethysScrollMenuItem<T> addItem(final T pValue,
                                        final String pName);

        /**
         * Add Element.
         * @param pValue the value
         * @param pGraphic the icon for the item
         * @return the item added
         */
        TethysScrollMenuItem<T> addItem(final T pValue,
                                        final I pGraphic);

        /**
         * Add Null Element.
         * @param pName the display name
         * @return the item added
         */
        TethysScrollMenuItem<T> addNullItem(final String pName);

        /**
         * Add Null Element.
         * @param pName the display name
         * @param pGraphic the icon for the item
         * @return the item added
         */
        TethysScrollMenuItem<T> addNullItem(final String pName,
                                            final I pGraphic);

        /**
         * Add Element.
         * @param pValue the value
         * @param pName the display name
         * @param pGraphic the icon for the item
         * @return the item added
         */
        TethysScrollMenuItem<T> addItem(final T pValue,
                                        final String pName,
                                        final I pGraphic);

        /**
         * Add subMenu.
         * @param pName the display name
         * @return the menu added
         */
        TethysScrollSubMenu<T, I> addSubMenu(final String pName);

        /**
         * Add subMenu.
         * @param pName the display name
         * @param pGraphic the icon for the menu
         * @return the menu added
         */
        TethysScrollSubMenu<T, I> addSubMenu(final String pName,
                                             final I pGraphic);

        /**
         * Add New toggle item.
         * @param pItem the available item
         * @return the added item
         */
        TethysScrollMenuToggleItem<T> addToggleItem(final T pItem);

        /**
         * Add New toggle item.
         * @param pItem the available item
         * @param pName the display name
         * @return the added item
         */
        TethysScrollMenuToggleItem<T> addToggleItem(final T pItem,
                                                    final String pName);

    }

    /**
     * ScrollMenuItem.
     * @param <T> the value type
     */
    public interface TethysScrollMenuItem<T> {
        /**
         * Obtain the value.
         * @return the value
         */
        T getValue();

        /**
         * Obtain the text.
         * @return the text
         */
        String getText();

        /**
         * Ensure that this item is visible immediately the context is displayed.
         */
        void scrollToItem();
    }

    /**
     * ScrollMenuToggleItem.
     * @param <T> the value type
     */
    public interface TethysScrollMenuToggleItem<T>
            extends TethysScrollMenuItem<T> {
        /**
         * is the item selected?
         * @return true/false
         */
        boolean isSelected();

        /**
         * Set selection status.
         * @param pSelected true/false
         */
        void setSelected(final boolean pSelected);

        /**
         * Toggle selected status.
         */
        void toggleSelected();
    }

    /**
     * ScrollSubMenu.
     * @param <T> the value type
     * @param <I> the Icon type
     */
    @FunctionalInterface
    public interface TethysScrollSubMenu<T, I> {
        /**
         * Obtain the subMenu.
         * @return the subMenu
         */
        TethysScrollMenu<T, I> getSubMenu();
    }
}
