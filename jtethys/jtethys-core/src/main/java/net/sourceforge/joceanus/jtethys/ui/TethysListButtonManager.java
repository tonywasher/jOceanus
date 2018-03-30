/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2017 Tony Washer
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysListButton;
import net.sourceforge.joceanus.jtethys.ui.TethysItemList.TethysItem;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuToggleItem;

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
 * @param <N> the node type
 * @param <I> the Icon type
 */
public abstract class TethysListButtonManager<T extends Comparable<T>, N, I>
        implements TethysListButton<T>, TethysEventProvider<TethysUIEvent>, TethysNode<N> {
    /**
     * Item separator.
     */
    public static final String ITEM_SEP = ",";

    /**
     * The GUI Manager.
     */
    private final TethysGuiFactory<N, I> theGuiFactory;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The Button.
     */
    private final TethysButton<N, I> theButton;

    /**
     * The ScrollListMenu.
     */
    private TethysScrollMenu<T, I> theMenu;

    /**
     * The Padding.
     */
    private Integer thePadding;

    /**
     * The Title.
     */
    private String theTitle;

    /**
     * The Value.
     */
    private List<T> theValue;

    /**
     * The ActiveValue.
     */
    private TethysItemList<T> theActiveValue;

    /**
     * The NewValue.
     */
    private TethysItemList<T> theNewValue;

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
    protected TethysListButtonManager(final TethysGuiFactory<N, I> pFactory) {
        /* Allocate resources */
        theGuiFactory = pFactory;
        theEventManager = new TethysEventManager<>();
        theButton = pFactory.newButton();

        /* Note that the button should be Text and Icon and set down arrow icon */
        theButton.setTextAndIcon();
        theButton.setIcon(TethysArrowIconId.DOWN);

        /* Set action handler */
        theButton.getEventRegistrar().addEventListener(e -> handleMenuRequest());
        theSelectables = Collections::emptyIterator;
    }

    @Override
    public Integer getId() {
        return theButton.getId();
    }

    @Override
    public N getNode() {
        return theButton.getNode();
    }

    /**
     * Obtain button.
     * @return the button
     */
    protected TethysButton<N, I> getButton() {
        return theButton;
    }

    /**
     * Obtain menu.
     * @return the menu
     */
    public TethysScrollMenu<T, I> getMenu() {
        ensureMenu();
        return theMenu;
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Set Text.
     * @param pText the text
     */
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
            theMenu = theGuiFactory.newContextMenu();
            theMenu.setCloseOnToggle(false);

            /* Register listeners */
            registerListeners();
        }
    }

    /**
     * Obtain the Border Padding.
     * @return the Padding.
     */
    protected Integer getBorderPadding() {
        return thePadding;
    }

    /**
     * Obtain the Border Title.
     * @return the Title.
     */
    protected String getBorderTitle() {
        return theTitle;
    }

    /**
     * Set the Border Padding.
     * @param pPadding the border padding
     */
    public void setBorderPadding(final Integer pPadding) {
        thePadding = pPadding;
    }

    /**
     * Set the Border Title.
     * @param pTitle the border title
     */
    public void setBorderTitle(final String pTitle) {
        theTitle = pTitle;
    }

    /**
     * Set the Preferred Width.
     * @param pWidth the width
     */
    public abstract void setPreferredWidth(Integer pWidth);

    /**
     * Set the Preferred Height.
     * @param pHeight the height
     */
    public abstract void setPreferredHeight(Integer pHeight);

    /**
     * Register listeners.
     */
    protected abstract void registerListeners();

    @Override
    public void setSelectables(final Supplier<Iterator<T>> pSelectables) {
        theSelectables = pSelectables;
    }

    /**
     * Set the value.
     * @param pValue the value
     */
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
            theActiveValue = new TethysItemList<>();
            theValue.sort(null);

            /* Iterate through the list */
            final Iterator<T> myIterator = theValue.iterator();
            while (myIterator.hasNext()) {
                /* Create the list item */
                final T myItem = myIterator.next();
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
    private void setValue(final TethysItemList<T> pValue) {
        /* Create the new list */
        theValue = new ArrayList<>();
        theActiveValue = pValue;

        /* Iterate through the list */
        if (theActiveValue != null) {
            final Iterator<TethysItem<T>> myIterator = theActiveValue.iterator();
            while (myIterator.hasNext()) {
                /* Create the list item */
                final TethysItem<T> myItem = myIterator.next();
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

    /**
     * Obtain the value.
     * @return the value
     */
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

    /**
     * handleMenuRequest.
     */
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

    /**
     * buildMenu.
     * @return is menu display-able?
     */
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
            theNewValue = new TethysItemList<>(theActiveValue);

            /* Iterate through the list */
            final Iterator<TethysItem<T>> myIterator = theActiveValue.iterator();
            while (myIterator.hasNext()) {
                /* Create the menu item */
                final TethysItem<T> myItem = myIterator.next();
                final TethysScrollMenuToggleItem<T> myMenuItem = theMenu.addToggleItem(myItem.getItem());
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
                theActiveValue = new TethysItemList<>();
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
        final TethysScrollMenuItem<T> mySelected = theMenu.getSelectedItem();
        if ((mySelected != null)
            && (mySelected instanceof TethysScrollMenuToggleItem)) {
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

    /**
     * Obtain the text value.
     * @return the formatted values
     */
    public String getText() {
        if (theValue == null) {
            return null;
        }

        /* Create the string builder */
        final StringBuilder myBuilder = new StringBuilder();
        boolean isFirst = true;

        /* Loop through the list */
        final Iterator<T> myIterator = theValue.iterator();
        while (myIterator.hasNext()) {
            final T myLink = myIterator.next();

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
