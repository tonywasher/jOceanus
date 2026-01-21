/*
 * Tethys: GUI Utilities
 * Copyright 2012-2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.tonywasher.joceanus.tethys.core.button;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDateConfig;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDateFormatter;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventManager;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIArrowIconId;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIEvent;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUINode;
import io.github.tonywasher.joceanus.tethys.api.button.TethysUIButton;
import io.github.tonywasher.joceanus.tethys.api.button.TethysUIDateButtonManager;
import io.github.tonywasher.joceanus.tethys.core.base.TethysUICoreComponent;
import io.github.tonywasher.joceanus.tethys.core.factory.TethysUICoreFactory;

import java.util.Objects;
import java.util.function.Consumer;

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
    private final OceanusEventManager<TethysUIEvent> theEventManager;

    /**
     * The button.
     */
    private final TethysUIButton theButton;

    /**
     * The date formatter.
     */
    private final OceanusDateFormatter theFormatter;

    /**
     * The Configuration.
     */
    private final OceanusDateConfig theConfig;

    /**
     * The DateConfigurator.
     */
    private Consumer<OceanusDateConfig> theDateConfigurator = p -> {
    };

    /**
     * The Value.
     */
    private OceanusDate theValue;

    /**
     * Is the menu showing?
     */
    private boolean menuShowing;

    /**
     * Constructor.
     *
     * @param pFactory the GUI Factory
     */
    protected TethysUICoreDateButtonManager(final TethysUICoreFactory<?> pFactory) {
        /* Create configuration */
        theFormatter = pFactory.getDataFormatter().getDateFormatter();
        theConfig = new OceanusDateConfig(theFormatter);

        /* Create resources */
        theEventManager = new OceanusEventManager<>();
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
     *
     * @return the button
     */
    protected TethysUIButton getButton() {
        return theButton;
    }

    @Override
    public OceanusDateConfig getConfig() {
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
    public OceanusEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * show the dialog.
     */
    protected abstract void showDialog();

    @Override
    public OceanusDate getSelectedDate() {
        return theValue;
    }

    @Override
    public OceanusDate getEarliestDate() {
        return theConfig.getEarliestDate();
    }

    @Override
    public OceanusDate getLatestDate() {
        return theConfig.getLatestDate();
    }

    @Override
    public void setSelectedDate(final OceanusDate pDate) {
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

    @Override
    public String getText() {
        return theFormatter.formatDate(theValue);
    }

    @Override
    public void setEarliestDate(final OceanusDate pDate) {
        theConfig.setEarliestDate(pDate);
    }

    @Override
    public void setLatestDate(final OceanusDate pDate) {
        theConfig.setLatestDate(pDate);
    }

    @Override
    public boolean allowNullDateSelection() {
        return theConfig.allowNullDateSelection();
    }

    @Override
    public void setAllowNullDateSelection(final boolean pAllowNullDateSelection) {
        theConfig.setAllowNullDateSelection(pAllowNullDateSelection);
    }

    @Override
    public void setShowNarrowDays(final boolean pShowNarrowDays) {
        theConfig.setShowNarrowDays(pShowNarrowDays);
    }

    @Override
    public void setDateConfigurator(final Consumer<OceanusDateConfig> pConfigurator) {
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
        final OceanusDate myNewValue = theConfig.getSelectedDate();
        if (valueChanged(myNewValue)) {
            theValue = myNewValue;
            theEventManager.fireEvent(TethysUIEvent.NEWVALUE, myNewValue);
            setButtonText();
        } else if (menuShowing) {
            theEventManager.fireEvent(TethysUIEvent.EDITFOCUSLOST, theConfig);
        }
        menuShowing = false;
    }

    /**
     * has value changed?
     *
     * @param pNew the new value
     * @return true/false
     */
    private boolean valueChanged(final OceanusDate pNew) {
        return !Objects.equals(theValue, pNew);
    }
}
