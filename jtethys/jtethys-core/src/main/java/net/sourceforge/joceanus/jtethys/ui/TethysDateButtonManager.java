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

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateConfig;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * DateButton Manager.
 * <p>
 * Provides the following events.
 * <dl>
 * <dt>PREPAREDIALOG
 * <dd>fired prior to dialog being displayed to allow for configuration of dialog
 * <dt>NEWVALUE
 * <dd>fired when a new date value is selected. <br>
 * Detail is new date value
 * <dt>EDITFOCUSLOST
 * <dd>fired when the dialog is cancelled without a value being selected.
 * </dl>
 * @param <N> the node type
 * @param <I> the icon type
 */
public abstract class TethysDateButtonManager<N, I>
        implements TethysEventProvider<TethysUIEvent>, TethysNode<N> {
    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The button.
     */
    private final TethysButton<N, I> theButton;

    /**
     * The date formatter.
     */
    private final TethysDateFormatter theFormatter;

    /**
     * The Configuration.
     */
    private final TethysDateConfig theConfig;

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
    private TethysDate theValue;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysDateButtonManager(final TethysGuiFactory<N, I> pFactory) {
        /* Create configuration */
        theFormatter = pFactory.getDataFormatter().getDateFormatter();
        theConfig = new TethysDateConfig(theFormatter);

        /* Create resources */
        theEventManager = new TethysEventManager<>();
        theButton = pFactory.newButton();

        /* Note that the button should be Text and Icon and set down Icon */
        theButton.setTextAndIcon();
        theButton.setIcon(TethysArrowIconId.DOWN);

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
     * Obtain the configuration.
     * @return the configuration
     */
    protected TethysDateConfig getConfig() {
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
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
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

    /**
     * handleDialogRequest.
     */
    protected void handleDialogRequest() {
        theEventManager.fireEvent(TethysUIEvent.PREPAREDIALOG, theConfig);
    }

    /**
     * handleDialogClosed.
     */
    protected void handleDialogClosed() {
        theEventManager.fireEvent(TethysUIEvent.EDITFOCUSLOST, theConfig);
    }

    /**
     * handleNewValue.
     */
    protected void handleNewValue() {
        TethysDate myNewValue = theConfig.getSelectedDate();
        if (valueChanged(myNewValue)) {
            theValue = myNewValue;
            theEventManager.fireEvent(TethysUIEvent.NEWVALUE, myNewValue);
            setButtonText();
        } else {
            handleDialogClosed();
        }
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
