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

import java.util.function.Consumer;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateConfig;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIArrowIconId;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUINode;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIXEvent;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButton;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;

/**
 * DateButton Manager.
 * <p>
 * The EventProvider fires the following events.
 * <ul>
 *   <li>TethysUIEvent.NEWVALUE is fired when a new date value is selected.
 *   <li>TethysUIEvent.EDITFOCUSLOST is fired when the dialog is cancelled without a value being selected.
 * </ul>
 */
public abstract class TethysUICoreDateButtonManager
        extends TethysUICoreComponent
        implements TethysUIDateButtonManager {
    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIXEvent> theEventManager;

    /**
     * The button.
     */
    private final TethysUIButton theButton;

    /**
     * The date formatter.
     */
    private final TethysDateFormatter theFormatter;

    /**
     * The Configuration.
     */
    private final TethysDateConfig theConfig;

    /**
     * The DateConfigurator.
     */
    private Consumer<TethysDateConfig> theDateConfigurator = p -> {
    };

    /**
     * The Value.
     */
    private TethysDate theValue;

    /**
     * Is the menu showing?
     */
    private boolean menuShowing;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysUICoreDateButtonManager(final TethysUICoreFactory<?> pFactory) {
        /* Create configuration */
        theFormatter = pFactory.getDataFormatter().getDateFormatter();
        theConfig = new TethysDateConfig(theFormatter);

        /* Create resources */
        theEventManager = new TethysEventManager<>();
        theButton = pFactory.buttonFactory().newButton();

        /* Note that the button should be Text and Icon and set down Icon */
        theButton.setTextAndIcon();
        theButton.setIcon(TethysUIArrowIconId.DOWN);

        /* Add listener for button */
        theButton.getEventRegistrar().addEventListener(e -> showDialog());

        /* Add listener for locale changes */
        theFormatter.getEventRegistrar().addEventListener(e -> {
            theConfig.setLocale(theFormatter.getLocale());
            setButtonText();
        });
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

    /**
     * Obtain the configuration.
     * @return the configuration
     */
    public TethysDateConfig getConfig() {
        return theConfig;
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
    public TethysEventRegistrar<TethysUIXEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * show the dialog.
     */
    protected abstract void showDialog();

    /**
     * Obtain the selected Date.
     * @return the selected Date
     */
    public TethysDate getSelectedDate() {
        return theValue;
    }

    /**
     * Obtain the earliest Date.
     * @return the earliest Date
     */
    public TethysDate getEarliestDate() {
        return theConfig.getEarliestDate();
    }

    /**
     * Obtain the latest Date.
     * @return the latest Date
     */
    public TethysDate getLatestDate() {
        return theConfig.getLatestDate();
    }

    /**
     * Set selected Date.
     * @param pDate the selected date
     */
    public void setSelectedDate(final TethysDate pDate) {
        theValue = pDate;
        theConfig.setSelectedDate(pDate);
        setButtonText();
    }

    /**
     * Set button text.
     */
    private void setButtonText() {
        theButton.setText(theFormatter.formatDate(theValue));
    }

    /**
     * Get button text.
     * @return the text
     */
    public String getText() {
        return theFormatter.formatDate(theValue);
    }

    /**
     * Set earliest Date.
     * @param pDate the earliest date
     */
    public void setEarliestDate(final TethysDate pDate) {
        theConfig.setEarliestDate(pDate);
    }

    /**
     * Set latest Date.
     * @param pDate the latest date
     */
    public void setLatestDate(final TethysDate pDate) {
        theConfig.setLatestDate(pDate);
    }

    /**
     * Allow Null Date selection.
     * @return true/false
     */
    public boolean allowNullDateSelection() {
        return theConfig.allowNullDateSelection();
    }

    /**
     * Allow null date selection. If this flag is set an additional button will be displayed
     * allowing the user to explicitly select no date, thus setting the SelectedDate to null.
     * @param pAllowNullDateSelection true/false
     */
    public void setAllowNullDateSelection(final boolean pAllowNullDateSelection) {
        theConfig.setAllowNullDateSelection(pAllowNullDateSelection);
    }

    /**
     * Show Narrow Days. If this flag is set Days are show in narrow rather than short form.
     * @param pShowNarrowDays true/false
     */
    public void setShowNarrowDays(final boolean pShowNarrowDays) {
        theConfig.setShowNarrowDays(pShowNarrowDays);
    }

    @Override
    public void setDateConfigurator(final Consumer<TethysDateConfig> pConfigurator) {
        theDateConfigurator = pConfigurator;
    }

    /**
     * handleDialogRequest.
     */
    protected void handleDialogRequest() {
        theDateConfigurator.accept(theConfig);
        menuShowing = true;
    }

    /**
     * handleNewValue.
     */
    protected void handleNewValue() {
        final TethysDate myNewValue = theConfig.getSelectedDate();
        if (valueChanged(myNewValue)) {
            theValue = myNewValue;
            theEventManager.fireEvent(TethysUIXEvent.NEWVALUE, myNewValue);
            setButtonText();
        } else if (menuShowing) {
            theEventManager.fireEvent(TethysUIXEvent.EDITFOCUSLOST, theConfig);
        }
        menuShowing = false;
    }

    /**
     * has value changed?
     * @param pNew the new value
     * @return true/false
     */
    private boolean valueChanged(final TethysDate pNew) {
        return theValue == null
                ? pNew != null
                : !theValue.equals(pNew);
    }
}
