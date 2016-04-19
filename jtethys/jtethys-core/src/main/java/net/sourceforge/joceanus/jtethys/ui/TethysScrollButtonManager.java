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
 * @param <N> the node type
 * @param <I> the Icon type
 */
public abstract class TethysScrollButtonManager<T, N, I>
        implements TethysEventProvider<TethysUIEvent>, TethysNode<N> {
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
     * The Padding.
     */
    private Integer thePadding;

    /**
     * The Title.
     */
    private String theTitle;

    /**
     * The value.
     */
    private T theValue;

    /**
     * Do we have fixed text?.
     */
    private boolean isFixedText;

    /**
     * The ScrollMenu.
     */
    private TethysScrollMenu<T, I> theMenu;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysScrollButtonManager(final TethysGuiFactory<N, I> pFactory) {
        /* Allocate resources */
        theGuiFactory = pFactory;
        theEventManager = new TethysEventManager<>();
        theButton = pFactory.newButton();
        isFixedText = false;

        /* Note that the button should be Text and Icon and set down arrow icon */
        theButton.setTextAndIcon();
        theButton.setIcon(TethysArrowIconId.DOWN);

        /* Set action handler */
        theButton.getEventRegistrar().addEventListener(e -> handleMenuRequest());
    }

    @Override
    public Integer getId() {
        return theButton.getId();
    }

    /**
     * Obtain value.
     * @return the value
     */
    public T getValue() {
        return theValue;
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
    public abstract void setPreferredWidth(final Integer pWidth);

    /**
     * Set the Preferred Height.
     * @param pHeight the height
     */
    public abstract void setPreferredHeight(final Integer pHeight);

    /**
     * Register listeners.
     */
    protected abstract void registerListeners();

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
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
        theButton.setText(pText);
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
            theButton.setText(pName);
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
        theButton.setToolTip(pToolTip);
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

    @Override
    public void setVisible(final boolean pVisible) {
        theButton.setVisible(pVisible);
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
        /* Ensure the menu */
        ensureMenu();

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
        if ((mySelected != null)
            && valueChanged(mySelected.getValue())) {
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
     * has value changed?
     * @param pNew the new value
     * @return true/false
     */
    private boolean valueChanged(final T pNew) {
        return theValue == null
                                ? pNew != null
                                : !theValue.equals(pNew);
    }

    /**
     * notifyCancelled.
     */
    protected void notifyCancelled() {
        /* fire menu cancelled event */
        theEventManager.fireEvent(TethysUIEvent.EDITFOCUSLOST);
    }
}