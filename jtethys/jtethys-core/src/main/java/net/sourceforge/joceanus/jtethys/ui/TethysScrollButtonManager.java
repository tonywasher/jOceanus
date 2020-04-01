/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui;

import java.util.function.Consumer;

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysScrollButton;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;

/**
 * ScrollButton Manager.
 * <p>
 * Provides the following events.
 * <dl>
 * <dt>NEWVALUE
 * <dd>fired when a new value is selected. <br>
 * Detail is new value
 * <dt>EDITFOCUSLOST
 * <dd>fired when the dialog is cancelled without a value being selected.
 * </dl>
 * @param <T> the object type
 */
public abstract class TethysScrollButtonManager<T>
        implements TethysScrollButton<T>, TethysEventProvider<TethysUIEvent>, TethysComponent {
    /**
     * The GUI Manager.
     */
    private final TethysGuiFactory theGuiFactory;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The Button.
     */
    private final TethysButton theButton;

    /**
     * The MenuConfigurator.
     */
    private Consumer<TethysScrollMenu<T>> theMenuConfigurator = p -> {
    };

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
    private TethysScrollMenu<T> theMenu;

    /**
     * Is the menu Showing?
     */
    private boolean menuShowing;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysScrollButtonManager(final TethysGuiFactory pFactory) {
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
    public TethysNode getNode() {
        return theButton.getNode();
    }

    /**
     * Obtain button.
     * @return the button
     */
    protected TethysButton getButton() {
        return theButton;
    }

    /**
     * Obtain menu.
     * @return the menu
     */
    public TethysScrollMenu<T> getMenu() {
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
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public void setMenuConfigurator(final Consumer<TethysScrollMenu<T>> pConfigurator) {
        theMenuConfigurator = pConfigurator;
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
    protected abstract <K extends Enum<K> & TethysIconId> void setIcon(K pId,
                                                                       int pWidth);

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
        theMenuConfigurator.accept(theMenu);

        /* If a menu is provided */
        if (!theMenu.isEmpty()) {
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
     * showMenu.
     */
    protected abstract void showMenu();

    /**
     * handleMenuClosed.
     */
    protected void handleMenuClosed() {
        /* If we selected a value */
        final TethysScrollMenuItem<T> mySelected = theMenu.getSelectedItem();
        if ((mySelected != null)
            && valueChanged(mySelected.getValue())) {
            /* Set the new value */
            setValue(mySelected.getValue(), mySelected.getText());

            /* fire new value Event */
            theEventManager.fireEvent(TethysUIEvent.NEWVALUE, theValue);

            /* Else if the menu was showing */
        } else if (menuShowing) {
            /* notify cancellation */
            notifyCancelled();
        }

        /* Release the menuShowing flag */
        menuShowing = false;
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
    private void notifyCancelled() {
        /* fire menu cancelled event */
        theEventManager.fireEvent(TethysUIEvent.EDITFOCUSLOST);
    }
}
