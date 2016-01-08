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

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;

/**
 * ScrollButton Manager.
 * @param <T> the object type
 * @param <I> the Icon type
 */
public abstract class TethysScrollButtonManager<T, I>
        implements TethysEventProvider<TethysUIEvent> {
    /**
     * Scroll Button.
     * @param <I> the Icon type
     */
    public interface TethysScrollButton<I> {
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
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The value.
     */
    private T theValue;

    /**
     * Do we have fixed text?.
     */
    private boolean isFixedText;

    /**
     * The Button.
     */
    private TethysScrollButton<I> theButton;

    /**
     * The ScrollMenu.
     */
    private TethysScrollMenu<T, I> theMenu;

    /**
     * Constructor.
     */
    protected TethysScrollButtonManager() {
        /* Create event manager */
        theEventManager = new TethysEventManager<>();
        isFixedText = false;
    }

    /**
     * Obtain value.
     * @return the value
     */
    public T getValue() {
        return theValue;
    }

    /**
     * Obtain button.
     * @return the button
     */
    public TethysScrollButton<I> getButton() {
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
    protected void declareButton(final TethysScrollButton<I> pButton) {
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
     * Set the value.
     * @param pValue the value to set.
     */
    public void setValue(final T pValue) {
        setValue(pValue, pValue == null
                                        ? null
                                        : pValue.toString());
    }

    /**
     * Set fixed text for the button.
     * @param pText the fixed text.
     */
    public void setFixedText(final String pText) {
        /* Set the button text and flag */
        theButton.setButtonText(pText);
        isFixedText = true;
    }

    /**
     * Set the value.
     * @param pValue the value to set.
     * @param pName the display name
     */
    public void setValue(final T pValue,
                         final String pName) {
        /* Store the value */
        theValue = pValue;

        /* Set the button text if required */
        if (!isFixedText) {
            theButton.setButtonText(pName);
        }
    }

    /**
     * Refresh Text from item.
     */
    public void refreshText() {
        if (!isFixedText
            && (theValue != null)) {
            setValue(theValue);
        }
    }

    /**
     * handleMenuRequest.
     */
    public void handleMenuRequest() {
        /* fire menuBuild Event */
        theEventManager.fireEvent(TethysUIEvent.PREPAREDIALOG, theMenu);

        /* If a menu is provided */
        if (!theMenu.isEmpty()) {
            /* Show the menu */
            showMenu();
        }
    }

    /**
     * showMenu.
     */
    protected abstract void showMenu();

    /**
     * handleMenuClosed.
     */
    protected void handleMenuClosed() {
        /* If we selected a value */
        TethysScrollMenuItem<T> mySelected = theMenu.getSelectedItem();
        if (mySelected != null) {
            /* Set the new value */
            setValue(mySelected.getValue(), mySelected.getText());

            /* fire new value Event */
            theEventManager.fireEvent(TethysUIEvent.NEWVALUE, theValue);
        }
    }
}
