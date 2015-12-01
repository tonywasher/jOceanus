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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuToggleItem;

/**
 * ListButton Manager.
 * @param <T> the object type
 * @param <I> the Icon type
 */
public abstract class TethysListButtonManager<T, I>
        implements TethysEventProvider {
    /**
     * List Button.
     * @param <I> the Icon type
     */
    public interface TethysListButton<I> {
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
    public static final int ACTION_ITEM_TOGGLED = TethysScrollButtonManager.ACTION_NEW_VALUE + 1;

    /**
     * Menu build.
     */
    public static final int ACTION_MENU_BUILD = TethysScrollButtonManager.ACTION_MENU_BUILD;

    /**
     * Menu cancelled.
     */
    public static final int ACTION_MENU_CANCELLED = TethysScrollButtonManager.ACTION_MENU_CANCELLED;

    /**
     * The Event Manager.
     */
    private final TethysEventManager theEventManager;

    /**
     * Map of items.
     */
    private final Map<T, TethysScrollMenuToggleItem<T>> theItemMap;

    /**
     * The Button.
     */
    private TethysListButton<I> theButton;

    /**
     * The ScrollListMenu.
     */
    private TethysScrollMenu<T, I> theMenu;

    /**
     * Constructor.
     */
    protected TethysListButtonManager() {
        /* Create event manager */
        theEventManager = new TethysEventManager();
        theItemMap = new LinkedHashMap<T, TethysScrollMenuToggleItem<T>>();
    }

    /**
     * Obtain button.
     * @return the button
     */
    public TethysListButton<I> getButton() {
        return theButton;
    }

    /**
     * Obtain menu.
     * @return the menu
     */
    public TethysScrollMenu<T, I> getMenu() {
        return theMenu;
    }

    @Override
    public TethysEventRegistrar getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Declare button.
     * @param pButton the button
     */
    protected void declareButton(final TethysListButton<I> pButton) {
        /* Store the button */
        theButton = pButton;
    }

    /**
     * Declare menu.
     * @param pMenu the menu
     */
    protected void declareMenu(final TethysScrollMenu<T, I> pMenu) {
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
        theButton.setButtonText(null);
    }

    /**
     * Set available item.
     * @param pItem the available item
     */
    public void setAvailableItem(final T pItem) {
        /* If the item is not already in the map */
        if (theItemMap.get(pItem) == null) {
            /* Create the item and place into map */
            TethysScrollMenuToggleItem<T> myItem = theMenu.addToggleItem(pItem);
            theItemMap.put(pItem, myItem);
        }
    }

    /**
     * clear all selected items.
     */
    public void clearAllSelected() {
        /* Loop through the menu items */
        for (TethysScrollMenuToggleItem<T> myItem : theItemMap.values()) {
            /* Clear the item */
            myItem.setSelected(false);
        }
        theButton.setButtonText(null);
    }

    /**
     * Set selected item.
     * @param pItem the item to select
     */
    public void setSelectedItem(final T pItem) {
        /* Access and select the item */
        TethysScrollMenuToggleItem<T> myItem = theItemMap.get(pItem);
        if (myItem != null) {
            myItem.setSelected(true);
            updateText();
        }
    }

    /**
     * Clear selected item.
     * @param pItem the item to clear
     */
    public void clearSelectedItem(final T pItem) {
        /* Access and select the item */
        TethysScrollMenuToggleItem<T> myItem = theItemMap.get(pItem);
        if (myItem != null) {
            myItem.setSelected(false);
            updateText();
        }
    }

    /**
     * Is item selected?
     * @param pItem the item to check
     * @return true/false
     */
    public boolean isItemSelected(final T pItem) {
        /* Access and determine status of the item */
        TethysScrollMenuToggleItem<T> myItem = theItemMap.get(pItem);
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
        TethysScrollMenuItem<T> mySelected = theMenu.getSelectedItem();
        if ((mySelected != null)
            && (mySelected instanceof TethysScrollMenuToggleItem)) {
            /* Set the new value */
            theEventManager.fireActionEvent(ACTION_ITEM_TOGGLED, mySelected);
            updateText();
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

    /**
     * Update the button text.
     */
    private void updateText() {
        theButton.setButtonText(getText());
    }

    /**
     * Obtain the list of selected values.
     * @return the selected values
     */
    public List<T> getSelected() {
        /* Build list */
        List<T> myList = new ArrayList<T>();
        for (TethysScrollMenuToggleItem<T> myItem : theItemMap.values()) {
            if (myItem.isSelected()) {
                myList.add(myItem.getValue());
            }
        }
        return myList;
    }

    /**
     * Obtain the text value.
     * @return the formatted values
     */
    public String getText() {
        /* Build text */
        StringBuilder myBuilder = new StringBuilder();
        for (TethysScrollMenuToggleItem<T> myItem : theItemMap.values()) {
            if (myItem.isSelected()) {
                if (myBuilder.length() > 0) {
                    myBuilder.append(',');
                }
                myBuilder.append(myItem.getText());
            }
        }
        return myBuilder.toString();
    }
}
