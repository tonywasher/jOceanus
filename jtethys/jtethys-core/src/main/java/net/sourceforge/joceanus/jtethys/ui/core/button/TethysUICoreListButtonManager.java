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
package net.sourceforge.joceanus.jtethys.ui.core.button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIArrowIconId;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUINode;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButton;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollToggle;
import net.sourceforge.joceanus.jtethys.ui.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.jtethys.ui.core.button.TethysUICoreItemList.TethysUICoreItem;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;

/**
 * ListButton Manager.
 * <p>
 * Provides the following events.
 * <dl>
 * <dt>NEWVALUE
 * <dd>fired when the dialog is closed with new values selected. <br>
 * Detail is the new set of values.
 * <dt>EDITFOCUSLOST
 * <dd>fired when the dialog is cancelled without a value being selected.
 * </dl>
 * @param <T> the object type
 */
public abstract class TethysUICoreListButtonManager<T extends Comparable<T>>
        extends TethysUICoreComponent
        implements TethysUIListButtonManager<T> {
    /**
     * Item separator.
     */
    public static final String ITEM_SEP = ",";

    /**
     * The GUI Manager.
     */
    private final TethysUICoreFactory<?> theGuiFactory;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The Button.
     */
    private final TethysUIButton theButton;

    /**
     * The ScrollListMenu.
     */
    private TethysUIScrollMenu<T> theMenu;

    /**
     * The Value.
     */
    private List<T> theValue;

    /**
     * The ActiveValue.
     */
    private TethysUICoreItemList<T> theActiveValue;

    /**
     * The NewValue.
     */
    private TethysUICoreItemList<T> theNewValue;

    /**
     * The selectable items supplier.
     */
    private Supplier<Iterator<T>> theSelectables;

    /**
     * Is the menu Showing?
     */
    private boolean menuShowing;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysUICoreListButtonManager(final TethysUICoreFactory<?> pFactory) {
        /* Allocate resources */
        theGuiFactory = pFactory;
        theEventManager = new TethysEventManager<>();
        theButton = pFactory.buttonFactory().newButton();

        /* Note that the button should be Text and Icon and set down arrow icon */
        theButton.setTextAndIcon();
        theButton.setIcon(TethysUIArrowIconId.DOWN);

        /* Set action handler */
        theButton.getEventRegistrar().addEventListener(e -> handleMenuRequest());
        theSelectables = Collections::emptyIterator;
    }

    @Override
    public Integer getId() {
        return theButton.getId();
    }

    @Override
    public TethysUINode getNode() {
        return theButton.getNode();
    }

    /**
     * Obtain button.
     * @return the button
     */
    protected TethysUIButton getButton() {
        return theButton;
    }

    @Override
    public TethysUIScrollMenu<T> getMenu() {
        ensureMenu();
        return theMenu;
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public void setText(final String pText) {
        theButton.setText(pText);
    }

    /**
     * Make sure that the menu is created.
     */
    private void ensureMenu() {
        /* If the menu does not exist */
        if (theMenu == null) {
            /* Create it */
            theMenu = theGuiFactory.menuFactory().newContextMenu();
            theMenu.setCloseOnToggle(false);

            /* Register listeners */
            registerListeners();
        }
    }

    /**
     * Register listeners.
     */
    protected abstract void registerListeners();

    @Override
    public void setSelectables(final Supplier<Iterator<T>> pSelectables) {
        theSelectables = pSelectables;
    }

    @Override
    public void setValue(final List<T> pValue) {
        /* If the value is null or empty */
        if (pValue == null || pValue.isEmpty()) {
            /* Set lists to null */
            theValue = null;
            theActiveValue = null;

            /* else build the new lists */
        } else {
            /* Create the new lists */
            theValue = new ArrayList<>(pValue);
            theActiveValue = new TethysUICoreItemList<>();
            theValue.sort(null);

            /* Iterate through the list */
            for (T myItem : theValue) {
                /* Create the list item */
                theActiveValue.setSelectedItem(myItem);
            }
        }

        /* Update the text */
        updateText();
    }

    /**
     * Set the value.
     * @param pValue the value
     */
    private void setValue(final TethysUICoreItemList<T> pValue) {
        /* Create the new list */
        theValue = new ArrayList<>();
        theActiveValue = pValue;

        /* Iterate through the list */
        if (theActiveValue != null) {
            final Iterator<TethysUICoreItem<T>> myIterator = theActiveValue.iterator();
            while (myIterator.hasNext()) {
                /* Create the list item */
                final TethysUICoreItem<T> myItem = myIterator.next();
                if (myItem.isSelected()) {
                    theValue.add(myItem.getItem());
                }
            }
        }

        /* Handle no selections */
        if (theValue.isEmpty()) {
            /* Set lists to null */
            theValue = null;
            theActiveValue = null;
        }

        /* Update the text */
        updateText();
    }

    @Override
    public List<T> getValue() {
        return theValue;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theButton.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theButton.setVisible(pVisible);
    }

    @Override
    public void handleMenuRequest() {
        /* If we should display the menu */
        if (buildMenu()) {
            /* Show the menu */
            showMenu();
            menuShowing = true;

            /* Else nothing to display */
        } else {
            /* notify cancellation */
            notifyCancelled();
        }
    }

    @Override
    public boolean buildMenu() {
        /* Ensure the menu */
        ensureMenu();

        /* Clear any existing elements from the menu */
        theMenu.removeAllItems();

        /* Build the activeValue */
        buildActiveValue();

        /* If we have any values */
        if (theActiveValue != null
                && theActiveValue.size() > 0) {
            /* Sort the list */
            theActiveValue.sortList();

            /* Create a clone of the list */
            theNewValue = new TethysUICoreItemList<>(theActiveValue);

            /* Iterate through the list */
            final Iterator<TethysUICoreItem<T>> myIterator = theActiveValue.iterator();
            while (myIterator.hasNext()) {
                /* Create the menu item */
                final TethysUICoreItem<T> myItem = myIterator.next();
                final TethysUIScrollToggle<T> myMenuItem = theMenu.addToggleItem(myItem.getItem());
                myMenuItem.setSelected(myItem.isSelected());
            }

            /* Show the menu */
            return true;

            /* Else nothing to display */
        } else {
            /* notify cancellation */
            theActiveValue = null;
            return false;
        }
    }

    /**
     * buildActiveValue.
     */
    private void buildActiveValue() {
        /* Clear out non-selected items */
        if (theActiveValue != null) {
            theActiveValue.clearNonSelectedItems();
        }

        /* Loop through the values */
        final Iterator<T> myIterator = theSelectables.get();
        while (myIterator.hasNext()) {
            /* Create the menu item */
            final T myItem = myIterator.next();

            /* Create the Active Value if it does not exist */
            if (theActiveValue == null) {
                theActiveValue = new TethysUICoreItemList<>();
            }

            /* If the list does not contain the item */
            if (theActiveValue.locateItem(myItem) == null) {
                /* Add it */
                theActiveValue.setSelectableItem(myItem);
            }
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
        final TethysUIScrollItem<T> mySelected = theMenu.getSelectedItem();
        if (mySelected instanceof TethysUIScrollToggle) {
            /* Toggle the item */
            theNewValue.toggleItem(mySelected.getValue());
        }
    }

    /**
     * handleMenuClosed.
     */
    protected void handleMenuClosed() {
        /* If there has been a change */
        if (!theActiveValue.equals(theNewValue)) {
            /* Record the new value */
            setValue(theNewValue);
            theEventManager.fireEvent(TethysUIEvent.NEWVALUE, theValue);
            theNewValue = null;

            /* else if the menu is showing */
        } else if (menuShowing) {
            /* notify cancellation */
            notifyCancelled();
        }

        /* Release the menuShowing flag */
        menuShowing = false;
    }

    /**
     * notifyCancelled.
     */
    private void notifyCancelled() {
        /* fire menu cancelled event */
        theEventManager.fireEvent(TethysUIEvent.EDITFOCUSLOST);
    }

    /**
     * Update the button text.
     */
    private void updateText() {
        theButton.setText(getText());
    }

    @Override
    public String getText() {
        if (theValue == null) {
            return null;
        }

        /* Create the string builder */
        final StringBuilder myBuilder = new StringBuilder();
        boolean isFirst = true;

        /* Loop through the list */
        for (T myLink : theValue) {
            /* Only add if selected */

            /* If this is not the first item */
            if (!isFirst) {
                /* add separator */
                myBuilder.append(ITEM_SEP);
            }

            /* Append the name */
            myBuilder.append(myLink.toString());
            isFirst = false;
        }

        /* Return the list */
        return myBuilder.toString();
    }
}
