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
 * @param <B> the button type
 * @param <I> the Icon type
 */
public abstract class TethysListButtonManager<T, B, I>
        implements TethysEventProvider<TethysUIEvent> {
    /**
     * List Button.
     * @param <B> the button type
     * @param <I> the Icon type
     */
    public interface TethysListButton<B, I> {
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

        /**
         * Obtain the node.
         * @return the node.
         */
        B getButton();

        /**
         * Set Enabled.
         * @param pEnabled the enabled flag
         */
        void setEnabled(final boolean pEnabled);
    }

    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The Button.
     */
    private TethysListButton<B, I> theButton;

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
     */
    protected TethysListButtonManager() {
        /* Create event manager */
        theEventManager = new TethysEventManager<>();
    }

    /**
     * Obtain Node.
     * @return the node
     */
    public B getNode() {
        return theButton.getButton();
    }

    /**
     * Obtain button.
     * @return the button
     */
    public TethysListButton<B, I> getButton() {
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
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Declare button.
     * @param pButton the button
     */
    protected void declareButton(final TethysListButton<B, I> pButton) {
        theButton = pButton;
    }

    /**
     * Declare menu.
     * @param pMenu the menu
     */
    protected void declareMenu(final TethysScrollMenu<T, I> pMenu) {
        /* Store the menu */
        theMenu = pMenu;
        theMenu.setCloseOnToggle(false);
    }

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

    /**
     * Set Enabled.
     * @param pEnabled the enabled flag
     */
    void setEnabled(final boolean pEnabled) {
        theButton.setEnabled(pEnabled);
    }

    /**
     * handleMenuRequest.
     */
    public void handleMenuRequest() {
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
            theEventManager.fireEvent(TethysUIEvent.NEWVALUE, theValue);

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
        theButton.setButtonText(getText());
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
