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

import java.util.Iterator;

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
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
public abstract class TethysListButtonManager<T, N, I>
        implements TethysEventProvider<TethysUIEvent>, TethysNode<N> {
    /**
     * The GUI Manager.
     */
    private final TethysGuiFactory<N, I> theGuiFactory;

    /**
     * The id.
     */
    private final Integer theId;

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
     * The Value.
     */
    private TethysItemList<T> theValue;

    /**
     * The ActiveValue.
     */
    private TethysItemList<T> theActiveValue;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysListButtonManager(final TethysGuiFactory<N, I> pFactory) {
        /* Allocate resources */
        theId = pFactory.getNextId();
        theGuiFactory = pFactory;
        theEventManager = new TethysEventManager<>();
        theButton = pFactory.newButton();

        /* Note that the button should be Text and Icon */
        theButton.setTextAndIcon();

        /* Set action handler */
        theButton.getEventRegistrar().addEventListener(e -> handleMenuRequest());
    }

    @Override
    public Integer getId() {
        return theId;
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
     * Register listeners.
     */
    protected abstract void registerListeners();

    /**
     * Set the value.
     * @param pValue the value
     */
    public void setValue(final TethysItemList<T> pValue) {
        theValue = pValue;
        updateText();
    }

    /**
     * Obtain the value.
     * @return the value
     */
    public TethysItemList<T> getValue() {
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

        /* If we have any values */
        if ((theValue != null)
            && (theValue.size() > 0)) {
            /* Create a clone of the list */
            theActiveValue = new TethysItemList<>(theValue);

            /* Iterate through the list */
            Iterator<TethysItem<T>> myIterator = theActiveValue.iterator();
            while (myIterator.hasNext()) {
                /* Create the menu item */
                TethysItem<T> myItem = myIterator.next();
                TethysScrollMenuToggleItem<T> myMenuItem = theMenu.addToggleItem(myItem.getItem());
                myMenuItem.setSelected(myItem.isSelected());
            }

            /* Show the menu */
            return true;

            /* Else nothing to display */
        } else {
            /* notify cancellation */
            return false;
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
            /* Toggle the item */
            theActiveValue.toggleItem(mySelected.getValue());
        }
    }

    /**
     * handleMenuClosed.
     */
    protected void handleMenuClosed() {
        /* If there has been a change */
        if (!theActiveValue.equals(theValue)) {
            /* Record the new value */
            setValue(theActiveValue);
            theEventManager.fireEvent(TethysUIEvent.NEWVALUE, theActiveValue);

            /* else no change */
        } else {
            /* notify cancellation */
            notifyCancelled();
        }
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
        return theValue == null
                                ? null
                                : theValue.toString();
    }
}
