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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/src/main/java/net/sourceforge/joceanus/jtethys/swing/JIconButton.java $
 * $Revision: 570 $
 * $Author: Tony $
 * $Date: 2015-02-14 06:54:38 +0000 (Sat, 14 Feb 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui;

import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.event.JOceanusEventManager;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar.JOceanusEventProvider;
import net.sourceforge.joceanus.jtethys.ui.ScrollMenuContent.ScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.ScrollMenuContent.ScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.ScrollMenuContent.ScrollMenuToggleItem;

/**
 * ListButton Manager.
 * @param <T> the object type
 * @param <I> the Icon type
 */
public abstract class ListButtonManager<T, I>
        implements JOceanusEventProvider {
    /**
     * List Button.
     * @param <I> the Icon type
     */
    public interface ListButton<I> {
        /**
         * Set the button text.
         * @param pText the button text to set.
         */
        void setButtonText(final String pText);

        /**
         * Set the button icon.
         * @param pIcon the button icon to set.
         */
        void setButtonIcon(final I pIcon);

        /**
         * Set the button toolTip.
         * @param pToolTip the toolTip to set.
         */
        void setButtonToolTip(final String pToolTip);
    }

    /**
     * Value updated.
     */
    public static final int ACTION_TOGGLED = 100;

    /**
     * Menu build.
     */
    public static final int ACTION_MENU_BUILD = 101;

    /**
     * Menu cancelled.
     */
    public static final int ACTION_MENU_CANCELLED = 102;

    /**
     * The Event Manager.
     */
    private final JOceanusEventManager theEventManager;

    /**
     * Map of items.
     */
    private final Map<T, ScrollMenuToggleItem<T>> theItemMap;

    /**
     * The Button.
     */
    private ListButton<I> theButton;

    /**
     * The ScrollListMenu.
     */
    private ScrollMenu<T, I> theMenu;

    /**
     * Constructor.
     */
    protected ListButtonManager() {
        /* Create event manager */
        theEventManager = new JOceanusEventManager();
        theItemMap = new LinkedHashMap<T, ScrollMenuToggleItem<T>>();
    }

    /**
     * Obtain button.
     * @return the button
     */
    public ListButton<I> getButton() {
        return theButton;
    }

    /**
     * Obtain menu.
     * @return the menu
     */
    public ScrollMenu<T, I> getMenu() {
        return theMenu;
    }

    @Override
    public JOceanusEventRegistrar getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Declare button.
     * @param pButton the button
     */
    protected void declareButton(final ListButton<I> pButton) {
        /* Store the button */
        theButton = pButton;
    }

    /**
     * Declare menu.
     * @param pMenu the menu
     */
    protected void declareMenu(final ScrollMenu<T, I> pMenu) {
        /* Store the menu */
        theMenu = pMenu;
    }

    /**
     * Clear available items.
     */
    public void clearAvailableItems() {
        /* reset the list of available items */
        theMenu.removeAllItems();
        theItemMap.clear();
    }

    /**
     * Set available item.
     * @param pItem the available item
     */
    public void setAvailableItem(final T pItem) {
        /* If the item is not already in the map */
        if (theItemMap.get(pItem) == null) {
            /* Create the item and place into map */
            ScrollMenuToggleItem<T> myItem = theMenu.addToggleItem(pItem);
            theItemMap.put(pItem, myItem);
        }
    }

    /**
     * clear all selected items.
     */
    public void clearAllSelected() {
        /* Loop through the menu items */
        for (ScrollMenuToggleItem<T> myItem : theItemMap.values()) {
            /* Clear the item */
            myItem.setSelected(false);
        }
    }

    /**
     * Set selected item.
     * @param pItem the item to select
     */
    public void setSelectedItem(final T pItem) {
        /* Access and select the item */
        ScrollMenuToggleItem<T> myItem = theItemMap.get(pItem);
        if (myItem != null) {
            myItem.setSelected(true);
        }
    }

    /**
     * Clear selected item.
     * @param pItem the item to clear
     */
    public void clearSelectedItem(final T pItem) {
        /* Access and select the item */
        ScrollMenuToggleItem<T> myItem = theItemMap.get(pItem);
        if (myItem != null) {
            myItem.setSelected(false);
        }
    }

    /**
     * Is item selected?
     * @param pItem the item to check
     * @return true/false
     */
    public boolean isItemSelected(final T pItem) {
        /* Access and determine status of the item */
        ScrollMenuToggleItem<T> myItem = theItemMap.get(pItem);
        return (myItem != null) && myItem.isSelected();
    }

    /**
     * handleMenuRequest.
     */
    public void handleMenuRequest() {
        /* fire menuBuild actionEvent */
        theEventManager.fireActionEvent(ACTION_MENU_BUILD, theMenu);

        /* If a menu is provided */
        if (!theMenu.isEmpty()) {
            /* Show the menu */
            showMenu();

            /* Else nothing to display */
        } else {
            /* notify cancellation */
            notifyCancelled();
        }
    }

    /**
     * showMenu.
     */
    protected abstract void showMenu();

    /**
     * handle toggle item.
     */
    protected void handleToggleItem() {
        /* If we selected a value */
        ScrollMenuItem<T> mySelected = theMenu.getSelectedItem();
        if ((mySelected != null)
            && (mySelected instanceof ScrollMenuToggleItem)) {
            /* Set the new value */
            theEventManager.fireActionEvent(ACTION_TOGGLED, mySelected);
        }
    }

    /**
     * handle menu closed.
     */
    protected void handleMenuClosed() {
        /* notify cancellation */
        notifyCancelled();
    }

    /**
     * notifyCancelled.
     */
    private void notifyCancelled() {
        /* fire menu cancelled event */
        theEventManager.fireActionEvent(ACTION_MENU_CANCELLED);
    }
}
