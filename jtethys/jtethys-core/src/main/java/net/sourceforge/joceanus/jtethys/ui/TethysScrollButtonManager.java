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

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysIconBuilder.TethysIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;

/**
 * ScrollButton Manager.
 * <p>
 * Provides the following events.
 * <dl>
 * <dt>PREPAREDIALOG
 * <dd>fired prior to dialog being displayed to allow for configuration of dialog
 * <dt>NEWVALUE
 * <dd>fired when a new value is selected. <br>
 * Detail is new value
 * <dt>EDITFOCUSLOST
 * <dd>fired when the dialog is cancelled without a value being selected.
 * </dl>
 * @param <T> the object type
 * @param <B> the button type
 * @param <I> the Icon type
 */
public abstract class TethysScrollButtonManager<T, B, I>
        implements TethysEventProvider<TethysUIEvent>, TethysNode<B> {
    /**
     * Scroll Button.
     * @param <B> the button type
     * @param <I> the Icon type
     */
    public interface TethysScrollButton<B, I> {
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

        /**
         * Set Null Margins.
         */
        void setNullMargins();
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
    private TethysScrollButton<B, I> theButton;

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

    @Override
    public B getNode() {
        return theButton.getButton();
    }

    /**
     * Obtain button.
     * @return the button
     */
    public TethysScrollButton<B, I> getButton() {
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
    protected void declareButton(final TethysScrollButton<B, I> pButton) {
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
     * Set simple details.
     * @param <K> the keyId type
     * @param pId the mapped IconId
     * @param pWidth the icon width
     * @param pToolTip the toolTip for value
     */
    public <K extends Enum<K> & TethysIconId> void setSimpleDetails(final K pId,
                                                                    final int pWidth,
                                                                    final String pToolTip) {
        setFixedText(null);
        setValue(null);
        setIcon(pId, pWidth);
        theButton.setButtonToolTip(pToolTip);
    }

    /**
     * Set Icon.
     * @param <K> the keyId type
     * @param pId the IconId
     * @param pWidth the icon width
     */
    protected abstract <K extends Enum<K> & TethysIconId> void setIcon(final K pId,
                                                                       final int pWidth);

    @Override
    public void setEnabled(final boolean pEnabled) {
        theButton.setEnabled(pEnabled);
    }

    /**
     * Set Null Margins.
     */
    public void setNullMargins() {
        theButton.setNullMargins();
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

            /* Else no value was selected */
        } else {
            /* notify cancellation */
            notifyCancelled();
        }
    }

    /**
     * notifyCancelled.
     */
    protected void notifyCancelled() {
        /* fire menu cancelled event */
        theEventManager.fireEvent(TethysUIEvent.EDITFOCUSLOST);
    }
}
