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
 * @param <I> the icon type
 */
public abstract class TethysDateRangeSelector<N, I>
        implements TethysEventProvider<TethysUIEvent>, TethysNode<N> {
    /**
     * ToolTip for Next Button.
     */
    private static final String NLS_NEXTTIP = TethysDateResource.TIP_NEXTDATE.getValue();

    /**
     * ToolTip for Previous Button.
     */
    private static final String NLS_PREVTIP = TethysDateResource.TIP_PREVDATE.getValue();

    /**
     * Text for Start Label.
     */
    private static final String NLS_START = TethysDateResource.LABEL_STARTING.getValue();

    /**
     * Text for End Label.
     */
    private static final String NLS_END = TethysDateResource.LABEL_ENDING.getValue();

    /**
     * Text for Containing Label.
     */
    private static final String NLS_CONTAIN = TethysDateResource.LABEL_CONTAINING.getValue();

    /**
     * Text for Period Label.
     */
    private static final String NLS_PERIOD = TethysDateResource.LABEL_PERIOD.getValue();

    /**
     * Text for Box Title.
     */
    protected static final String NLS_TITLE = TethysDateResource.TITLE_BOX.getValue();

    /**
     * The GUI Factory.
     */
    private final TethysGuiFactory<N, I> theGuiFactory;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The formatter.
     */
    private final TethysDateFormatter theFormatter;

    /**
     * The Control.
     */
    private final TethysBoxPaneManager<N, I> theControl;

    /**
     * The Period Box.
     */
    private final TethysBoxPaneManager<N, I> thePeriodBox;

    /**
     * The Standard Box.
     */
    private final TethysBoxPaneManager<N, I> theStandardBox;

    /**
     * The Period Box.
     */
    private final TethysBoxPaneManager<N, I> theCustomBox;

    /**
     * The Standard Label.
     */
    private final TethysLabel<N, I> theStandardLabel;

    /**
     * The Next button.
     */
    private final TethysButton<N, I> theNextButton;

    /**
     * The Previous button.
     */
    private final TethysButton<N, I> thePrevButton;

    /**
     * The Start Date button.
     */
    private final TethysDateButtonManager<N, I> theStartButton;

    /**
     * The End Date button.
     */
    private final TethysDateButtonManager<N, I> theEndButton;

    /**
     * The Base Date button.
     */
    private final TethysDateButtonManager<N, I> theBaseButton;

    /**
     * The Period Button.
     */
    private final TethysScrollButtonManager<TethysDatePeriod, N, I> thePeriodButton;

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
     * @param pFactory the GUI factory
     * @param pBaseIsStart is the baseDate the start of the period? (true/false)
     */
    protected TethysDateRangeSelector(final TethysGuiFactory<N, I> pFactory,
                                      final boolean pBaseIsStart) {
        /* Store the parameters */
        theFormatter = pFactory.getDataFormatter().getDateFormatter();

        /* Record the factory */
        theGuiFactory = pFactory;

        /* Create event manager */
        theEventManager = new TethysEventManager<>();

        /* Create initial state */
        theState = new TethysDateRangeState(pBaseIsStart);

        /* Create the buttons */
        theStartButton = theGuiFactory.newDateButton();
        theEndButton = theGuiFactory.newDateButton();
        theBaseButton = theGuiFactory.newDateButton();
        thePeriodButton = theGuiFactory.newScrollButton();
        buildPeriodMenu(thePeriodButton.getMenu());

        /* Create the period box */
        TethysLabel<N, I> myPeriodLabel = pFactory.newLabel(NLS_PERIOD);
        thePeriodBox = pFactory.newHBoxPane();
        thePeriodBox.addNode(myPeriodLabel);
        thePeriodBox.addNode(thePeriodButton);

        /* Create the next button */
        theNextButton = pFactory.newButton();
        theNextButton.setToolTip(NLS_NEXTTIP);
        theNextButton.getEventRegistrar().addEventListener(e -> handleNextDate());

        /* Create the Previous button */
        thePrevButton = pFactory.newButton();
        thePrevButton.setToolTip(NLS_PREVTIP);
        thePrevButton.getEventRegistrar().addEventListener(e -> handlePreviousDate());

        /* Create the Custom HBox */
        theCustomBox = pFactory.newHBoxPane();
        TethysLabel<N, I> myStartLabel = pFactory.newLabel(NLS_START);
        TethysLabel<N, I> myEndLabel = pFactory.newLabel(NLS_END);
        theCustomBox.addNode(myStartLabel);
        theCustomBox.addNode(theStartButton);
        theCustomBox.addNode(myEndLabel);
        theCustomBox.addNode(theEndButton);

        /* Create the Standard HBox */
        theStandardBox = pFactory.newHBoxPane();
        theStandardLabel = pFactory.newLabel();
        theStandardBox.addNode(theStandardLabel);
        theStandardBox.addNode(thePrevButton);
        theStandardBox.addNode(theBaseButton);
        theStandardBox.addNode(theNextButton);

        /* Create the Main Node */
        theControl = theGuiFactory.newHBoxPane();

        /* Configure the node */
        theControl.addNode(thePeriodBox);
        theControl.addSpacer();
        theControl.addNode(theStandardBox);
        theControl.addNode(theCustomBox);

        /* Add the listeners */
        theStartButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewStartDate(e.getDetails(TethysDate.class)));
        theEndButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewEndDate(e.getDetails(TethysDate.class)));
        theBaseButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewBaseDate(e.getDetails(TethysDate.class)));
        thePeriodButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> setPeriod(thePeriodButton.getValue()));
    }

    @Override
    public Integer getId() {
        return theControl.getId();
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
     * Set the Border Title.
     * @param pTitle the title text
     */
    public abstract void setBorderTitle(final String pTitle);

    /**
     * Obtain the previous button.
     * @return the previous button
     */
    protected TethysButton<N, I> getPrevButton() {
        return thePrevButton;
    }

    /**
     * Obtain the next button.
     * @return the next button
     */
    protected TethysButton<N, I> getNextButton() {
        return theNextButton;
    }

    /**
     * Obtain the control.
     * @return the control
     */
    protected TethysBoxPaneManager<N, I> getControl() {
        return theControl;
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
    public void setSelection(final TethysDateRangeSelector<N, I> pSource) {
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
    protected void applyState(final TethysDateRangeState pState) {
        /* Determine flags */
        boolean isUpTo = pState.isUpTo()
                         && pState.isLocked();
        boolean isAdjust = pState.isAdjustable();
        boolean isFull = pState.isFull();
        boolean isContaining = pState.isContaining();
        boolean isBaseStartOfPeriod = pState.isBaseStartOfPeriod();

        /* Adjust visibility */
        theGuiFactory.setNodeVisible(thePeriodBox, !isUpTo);

        /* If this is a custom state */
        if (pState.isCustom()) {
            theGuiFactory.setNodeVisible(theStandardBox, false);
            theGuiFactory.setNodeVisible(theCustomBox, true);

            /* else this is a standard state */
        } else if (!isFull) {
            /* Enable/disable the adjustment buttons */
            theNextButton.setEnabled(pState.isNextOK());
            thePrevButton.setEnabled(pState.isPrevOK());

            /* Hide Next/Previous if necessary */
            theNextButton.setVisible(isAdjust);
            thePrevButton.setVisible(isAdjust);

            /* Label is hidden for UpTo range */
            theStandardLabel.setVisible(!isUpTo);

            /* Set correct text for label */
            theStandardLabel.setText(isContaining
                                                  ? NLS_CONTAIN
                                                  : isBaseStartOfPeriod
                                                                        ? NLS_START
                                                                        : NLS_END);

            /* Display the standard box */
            theGuiFactory.setNodeVisible(theCustomBox, false);
            theGuiFactory.setNodeVisible(theStandardBox, true);
        } else {
            /* Hide the boxes */
            theGuiFactory.setNodeVisible(theCustomBox, false);
            theGuiFactory.setNodeVisible(theStandardBox, false);
        }
    }
}
