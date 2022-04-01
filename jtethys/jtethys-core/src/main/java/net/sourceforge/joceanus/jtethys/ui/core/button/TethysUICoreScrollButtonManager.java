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

import java.util.Objects;
import java.util.function.Consumer;

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIArrowIconId;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIIconId;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUINode;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIXEvent;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButton;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;

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
public abstract class TethysUICoreScrollButtonManager<T>
        extends TethysUICoreComponent
        implements TethysUIScrollButtonManager<T> {
    /**
     * The GUI Manager.
     */
    private final TethysUICoreFactory<?> theGuiFactory;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIXEvent> theEventManager;

    /**
     * The Button.
     */
    private final TethysUIButton theButton;

    /**
     * The MenuConfigurator.
     */
    private Consumer<TethysUIScrollMenu<T>> theMenuConfigurator = p -> {
    };

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
    private TethysUIScrollMenu<T> theMenu;

    /**
     * Is the menu Showing?
     */
    private boolean menuShowing;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysUICoreScrollButtonManager(final TethysUICoreFactory<?> pFactory) {
        /* Allocate resources */
        theGuiFactory = pFactory;
        theEventManager = new TethysEventManager<>();
        theButton = pFactory.buttonFactory().newButton();
        isFixedText = false;

        /* Note that the button should be Text and Icon and set down arrow icon */
        theButton.setTextAndIcon();
        theButton.setIcon(TethysUIArrowIconId.DOWN);

        /* Set action handler */
        theButton.getEventRegistrar().addEventListener(e -> handleMenuRequest());
    }

    @Override
    public Integer getId() {
        return theButton.getId();
    }

    @Override
    public T getValue() {
        return theValue;
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
    public TethysEventRegistrar<TethysUIXEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public void setMenuConfigurator(final Consumer<TethysUIScrollMenu<T>> pConfigurator) {
        theMenuConfigurator = pConfigurator;
    }

    @Override
    public void setValue(final T pValue) {
        setValue(pValue, pValue == null
                ? null
                : pValue.toString());
    }

    @Override
    public void setFixedText(final String pText) {
        /* Set the button text and flag */
        theButton.setText(pText);
        isFixedText = true;
    }

    @Override
    public void setValue(final T pValue,
                         final String pName) {
        /* Store the value */
        theValue = pValue;

        /* Set the button text if required */
        if (!isFixedText) {
            theButton.setText(pName);
        }
    }

    @Override
    public void setSimpleDetails(final TethysUIIconId pId,
                                 final int pWidth,
                                 final String pToolTip) {
        setFixedText(null);
        setValue(null);
        setIcon(pId, pWidth);
        theButton.setToolTip(pToolTip);
    }

    /**
     * Set Icon.
     * @param pId the IconId
     * @param pWidth the icon width
     */
    protected abstract void setIcon(TethysUIIconId pId,
                                    int pWidth);

    @Override
    public void setEnabled(final boolean pEnabled) {
        theButton.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theButton.setVisible(pVisible);
    }

    @Override
    public void setNullMargins() {
        theButton.setNullMargins();
    }

    @Override
    public void refreshText() {
        if (!isFixedText
                && (theValue != null)) {
            setValue(theValue);
        }
    }

    @Override
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
        final TethysUIScrollItem<T> mySelected = theMenu.getSelectedItem();
        if ((mySelected != null)
                && valueChanged(mySelected.getValue())) {
            /* Set the new value */
            setValue(mySelected.getValue(), mySelected.getText());

            /* fire new value Event */
            theEventManager.fireEvent(TethysUIXEvent.NEWVALUE, theValue);

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
        return !Objects.equals(theValue, pNew);
    }

    /**
     * notifyCancelled.
     */
    private void notifyCancelled() {
        /* fire menu cancelled event */
        theEventManager.fireEvent(TethysUIXEvent.EDITFOCUSLOST);
    }
}
