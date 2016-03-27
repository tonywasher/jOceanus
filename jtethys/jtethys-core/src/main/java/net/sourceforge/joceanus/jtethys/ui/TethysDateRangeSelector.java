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

import java.util.Locale;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.date.TethysDatePeriod;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.date.TethysDateRangeState;
import net.sourceforge.joceanus.jtethys.date.TethysDateResource;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;

/**
 * DateRange Selector.
 * @param <N> the node type
 */
public abstract class TethysDateRangeSelector<N>
        implements TethysEventProvider<TethysUIEvent>, TethysNode<N> {
    /**
     * ToolTip for Next Button.
     */
    protected static final String NLS_NEXTTIP = TethysDateResource.TIP_NEXTDATE.getValue();

    /**
     * ToolTip for Previous Button.
     */
    protected static final String NLS_PREVTIP = TethysDateResource.TIP_PREVDATE.getValue();

    /**
     * Text for Start Label.
     */
    protected static final String NLS_START = TethysDateResource.LABEL_STARTING.getValue();

    /**
     * Text for End Label.
     */
    protected static final String NLS_END = TethysDateResource.LABEL_ENDING.getValue();

    /**
     * Text for Containing Label.
     */
    protected static final String NLS_CONTAIN = TethysDateResource.LABEL_CONTAINING.getValue();

    /**
     * Text for Period Label.
     */
    protected static final String NLS_PERIOD = TethysDateResource.LABEL_PERIOD.getValue();

    /**
     * Text for Box Title.
     */
    protected static final String NLS_TITLE = TethysDateResource.TITLE_BOX.getValue();

    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The formatter.
     */
    private final TethysDateFormatter theFormatter;

    /**
     * The Start Date button.
     */
    private TethysDateButtonManager<?> theStartButton;

    /**
     * The End Date button.
     */
    private TethysDateButtonManager<?> theEndButton;

    /**
     * The Base Date button.
     */
    private TethysDateButtonManager<?> theBaseButton;

    /**
     * The Period Button.
     */
    private TethysScrollButtonManager<TethysDatePeriod, N, ?> thePeriodButton;

    /**
     * The Published DateRange.
     */
    private TethysDateRange thePublishedRange;

    /**
     * The DateRange State.
     */
    private TethysDateRangeState theState;

    /**
     * The Saved state.
     */
    private TethysDateRangeState theSavePoint;

    /**
     * Constructor.
     * @param pFormatter the date formatter
     * @param pBaseIsStart is the baseDate the start of the period? (true/false)
     */
    protected TethysDateRangeSelector(final TethysDataFormatter pFormatter,
                                      final boolean pBaseIsStart) {
        /* Store the parameters */
        theFormatter = pFormatter.getDateFormatter();

        /* Create event manager */
        theEventManager = new TethysEventManager<>();

        /* Create initial state */
        theState = new TethysDateRangeState(pBaseIsStart);
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Is the panel visible?
     * @return true/false
     */
    public abstract boolean isVisible();

    /**
     * Declare startButton.
     * @param pButton the startButton
     */
    protected void declareStartButton(final TethysDateButtonManager<?> pButton) {
        theStartButton = pButton;
        theStartButton.getEventRegistrar().addEventListener(e -> handleNewStartDate(e.getDetails(TethysDate.class)));
    }

    /**
     * Declare endButton.
     * @param pButton the endButton
     */
    protected void declareEndButton(final TethysDateButtonManager<?> pButton) {
        theEndButton = pButton;
        theEndButton.getEventRegistrar().addEventListener(e -> handleNewEndDate(e.getDetails(TethysDate.class)));
    }

    /**
     * Declare baseButton.
     * @param pButton the baseButton
     */
    protected void declareBaseButton(final TethysDateButtonManager<?> pButton) {
        theBaseButton = pButton;
        theBaseButton.getEventRegistrar().addEventListener(e -> handleNewBaseDate(e.getDetails(TethysDate.class)));
    }

    /**
     * Declare periodButton.
     * @param pButton the periodButton
     */
    protected void declarePeriodButton(final TethysScrollButtonManager<TethysDatePeriod, N, ?> pButton) {
        thePeriodButton = pButton;
        buildPeriodMenu(thePeriodButton.getMenu());
        thePeriodButton.getEventRegistrar().addEventListener(e -> setPeriod(thePeriodButton.getValue()));
    }

    /**
     * Build period menu.
     * @param pMenu the menu
     */
    private static void buildPeriodMenu(final TethysScrollMenu<TethysDatePeriod, ?> pMenu) {
        /* Loop through the periods */
        for (TethysDatePeriod myPeriod : TethysDatePeriod.values()) {
            /* Add as long as it is not the datesUpTo period */
            if (!myPeriod.datesUpTo()) {
                /* Create a new MenuItem for the period */
                pMenu.addItem(myPeriod);
            }
        }
    }

    /**
     * Obtain selected DateRange.
     * @return the selected date range
     */
    public TethysDateRange getRange() {
        return theState.getRange();
    }

    /**
     * Obtain current state.
     * @return the current state
     */
    private TethysDateRangeState getState() {
        return theState;
    }

    /**
     * Set the overall range for the control.
     * @param pRange the range
     */
    public final void setOverallRange(final TethysDateRange pRange) {
        theState.adjustOverallRange(pRange);
        applyState();
    }

    /**
     * Set the locale.
     * @param pLocale the locale
     */
    public void setLocale(final Locale pLocale) {
        theFormatter.setLocale(pLocale);
        theState.setLocale(pLocale);
        applyState();
    }

    /**
     * Set period.
     * @param pPeriod the new period
     */
    public void setPeriod(final TethysDatePeriod pPeriod) {
        theState.setPeriod(pPeriod);
        applyState();
    }

    /**
     * Lock period.
     * @param isLocked true/false.
     */
    public void lockPeriod(final boolean isLocked) {
        theState.lockPeriod(isLocked);
        applyState();
    }

    /**
     * Copy date selection from other box.
     * @param pSource the source box
     */
    public void setSelection(final TethysDateRangeSelector<N> pSource) {
        /* Access the state */
        TethysDateRangeState myState = pSource.getState();

        /* Accept this state */
        theState = new TethysDateRangeState(myState);

        /* Build the range */
        applyState();
    }

    /**
     * Create SavePoint.
     */
    public void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new TethysDateRangeState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new TethysDateRangeState(theSavePoint);
        applyState();
    }

    /**
     * Handle new startDate.
     * @param pDate the new date
     */
    private void handleNewStartDate(final TethysDate pDate) {
        theState.setStartDate(pDate);
        applyState();
    }

    /**
     * Handle new endDate.
     * @param pDate the new date
     */
    private void handleNewEndDate(final TethysDate pDate) {
        theState.setEndDate(pDate);
        applyState();
    }

    /**
     * Handle new baseDate.
     * @param pDate the new date
     */
    private void handleNewBaseDate(final TethysDate pDate) {
        theState.setBaseDate(pDate);
        applyState();
    }

    /**
     * Handle nextDate.
     */
    protected void handleNextDate() {
        theState.setNextDate();
        applyState();
    }

    /**
     * Handle previousDate.
     */
    protected void handlePreviousDate() {
        theState.setPreviousDate();
        applyState();
    }

    /**
     * Apply the state.
     */
    protected void applyState() {
        /* Apply to panel */
        applyState(theState);

        /* Access overall range */
        TethysDateRange myOverallRange = theState.getOverallRange();

        /* Set the period value */
        thePeriodButton.setValue(theState.getPeriod());

        /* If this is a custom state */
        if (theState.isCustom()) {
            /* Set values for buttons */
            theStartButton.setSelectedDate(theState.getStartDate());
            theStartButton.setEarliestDate(myOverallRange.getStart());
            theStartButton.setLatestDate(theState.getEndDate());
            theEndButton.setSelectedDate(theState.getEndDate());
            theEndButton.setEarliestDate(theState.getStartDate());
            theEndButton.setLatestDate(myOverallRange.getEnd());

            /* else is this is not a full dates state */
        } else if (!theState.isFull()) {
            /* Set value for button */
            theBaseButton.setSelectedDate(theState.getBaseDate());
            theBaseButton.setEarliestDate(myOverallRange.getStart());
            theBaseButton.setLatestDate(myOverallRange.getEnd());
        }

        /* Determine whether a change has occurred */
        TethysDateRange myNew = getRange();
        if (TethysDateRange.isDifferent(thePublishedRange, myNew)) {
            /* Record the new range and create a copy */
            thePublishedRange = myNew;
            myNew = new TethysDateRange(myNew);

            /* Fire the value change */
            theEventManager.fireEvent(TethysUIEvent.NEWVALUE, myNew);
        }
    }

    /**
     * Apply the state.
     * @param pState the state
     */
    protected abstract void applyState(final TethysDateRangeState pState);
}
