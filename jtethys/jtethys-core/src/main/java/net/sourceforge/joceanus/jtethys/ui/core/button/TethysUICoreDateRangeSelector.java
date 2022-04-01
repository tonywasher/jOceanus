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

import java.util.Locale;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.date.TethysDatePeriod;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.date.TethysDateRangeState;
import net.sourceforge.joceanus.jtethys.date.TethysDateResource;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIArrowIconId;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIXEvent;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButton;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIDateRangeSelector;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUILabel;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIPaneFactory;
import net.sourceforge.joceanus.jtethys.ui.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;

/**
 * DateRange Selector.
 */
public abstract class TethysUICoreDateRangeSelector
        extends TethysUICoreComponent
        implements TethysUIDateRangeSelector {
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
    private final TethysUICoreFactory<?> theGuiFactory;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIXEvent> theEventManager;

    /**
     * The formatter.
     */
    private final TethysDateFormatter theFormatter;

    /**
     * The Control.
     */
    private final TethysUIBoxPaneManager theControl;

    /**
     * The Period Box.
     */
    private final TethysUIBoxPaneManager thePeriodBox;

    /**
     * The Standard Box.
     */
    private final TethysUIBoxPaneManager theStandardBox;

    /**
     * The Period Box.
     */
    private final TethysUIBoxPaneManager theCustomBox;

    /**
     * The Standard Label.
     */
    private final TethysUILabel theStandardLabel;

    /**
     * The Next button.
     */
    private final TethysUIButton theNextButton;

    /**
     * The Previous button.
     */
    private final TethysUIButton thePrevButton;

    /**
     * The Start Date button.
     */
    private final TethysUIDateButtonManager theStartButton;

    /**
     * The End Date button.
     */
    private final TethysUIDateButtonManager theEndButton;

    /**
     * The Base Date button.
     */
    private final TethysUIDateButtonManager theBaseButton;

    /**
     * The Period Button.
     */
    private final TethysUIScrollButtonManager<TethysDatePeriod> thePeriodButton;

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
    protected TethysUICoreDateRangeSelector(final TethysUICoreFactory<?> pFactory,
                                            final boolean pBaseIsStart) {
        /* Store the parameters */
        theFormatter = pFactory.getDataFormatter().getDateFormatter();

        /* Record the factory */
        theGuiFactory = pFactory;

        /* Create event manager */
        theEventManager = new TethysEventManager<>();

        /* Create initial state */
        theState = new TethysDateRangeState(pBaseIsStart);

        /* Access the factories */
        final TethysUIButtonFactory myButtonFactory = pFactory.buttonFactory();
        final TethysUIControlFactory myControlFactory = pFactory.controlFactory();
        final TethysUIPaneFactory myPaneFactory = pFactory.paneFactory();

        /* Create the buttons */
        theStartButton = myButtonFactory.newDateButton();
        theEndButton = myButtonFactory.newDateButton();
        theBaseButton = myButtonFactory.newDateButton();
        thePeriodButton = myButtonFactory.newScrollButton();
        buildPeriodMenu(thePeriodButton.getMenu());

        /* Create the period box */
        final TethysUILabel myPeriodLabel = myControlFactory.newLabel(NLS_PERIOD);
        thePeriodBox = myPaneFactory.newHBoxPane();
        thePeriodBox.addNode(myPeriodLabel);
        thePeriodBox.addNode(thePeriodButton);

        /* Create the next button */
        theNextButton = myButtonFactory.newButton();
        theNextButton.setIcon(TethysUIArrowIconId.RIGHT);
        theNextButton.setToolTip(NLS_NEXTTIP);
        theNextButton.getEventRegistrar().addEventListener(e -> handleNextDate());

        /* Create the Previous button */
        thePrevButton = myButtonFactory.newButton();
        thePrevButton.setIcon(TethysUIArrowIconId.LEFT);
        thePrevButton.setToolTip(NLS_PREVTIP);
        thePrevButton.getEventRegistrar().addEventListener(e -> handlePreviousDate());

        /* Create the Custom HBox */
        theCustomBox = myPaneFactory.newHBoxPane();
        final TethysUILabel myStartLabel = myControlFactory.newLabel(NLS_START);
        final TethysUILabel myEndLabel = myControlFactory.newLabel(NLS_END);
        theCustomBox.addNode(myStartLabel);
        theCustomBox.addNode(theStartButton);
        theCustomBox.addNode(myEndLabel);
        theCustomBox.addNode(theEndButton);

        /* Create the Standard HBox */
        theStandardBox = myPaneFactory.newHBoxPane();
        theStandardLabel = myControlFactory.newLabel();
        theStandardBox.addNode(theStandardLabel);
        theStandardBox.addNode(thePrevButton);
        theStandardBox.addNode(theBaseButton);
        theStandardBox.addNode(theNextButton);

        /* Create the Main Node */
        theControl = myPaneFactory.newHBoxPane();

        /* Configure the node */
        theControl.addNode(thePeriodBox);
        theControl.addSpacer();
        theControl.addNode(theStandardBox);
        theControl.addNode(theCustomBox);

        /* Add the listeners */
        theStartButton.getEventRegistrar().addEventListener(TethysUIXEvent.NEWVALUE, e -> handleNewStartDate(e.getDetails(TethysDate.class)));
        theEndButton.getEventRegistrar().addEventListener(TethysUIXEvent.NEWVALUE, e -> handleNewEndDate(e.getDetails(TethysDate.class)));
        theBaseButton.getEventRegistrar().addEventListener(TethysUIXEvent.NEWVALUE, e -> handleNewBaseDate(e.getDetails(TethysDate.class)));
        thePeriodButton.getEventRegistrar().addEventListener(TethysUIXEvent.NEWVALUE, e -> setPeriod(thePeriodButton.getValue()));
    }

    @Override
    public Integer getId() {
        return theControl.getId();
    }

    @Override
    public TethysEventRegistrar<TethysUIXEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the control.
     * @return the control
     */
    protected TethysUIBoxPaneManager getControl() {
        return theControl;
    }

    /**
     * Build period menu.
     * @param pMenu the menu
     */
    private static void buildPeriodMenu(final TethysUIScrollMenu<TethysDatePeriod> pMenu) {
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
    public void setSelection(final TethysUIDateRangeSelector pSource) {
        /* Access the state */
        final TethysDateRangeState myState = ((TethysUICoreDateRangeSelector) pSource).getState();

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

    @Override
    public void setEnabled(final boolean pEnable) {
        /* Pass call on to node */
        theControl.setEnabled(pEnable);

        /* If we are enabling */
        if (pEnable) {
            /* Ensure correct values */
            applyState();
        }
    }

    /**
     * Apply the state.
     */
    protected void applyState() {
        /* Apply to panel */
        applyState(theState);

        /* Access overall range */
        final TethysDateRange myOverallRange = theState.getOverallRange();

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
            theEventManager.fireEvent(TethysUIXEvent.NEWVALUE, myNew);
        }
    }

    /**
     * Apply the state.
     * @param pState the state
     */
    protected void applyState(final TethysDateRangeState pState) {
        /* Determine flags */
        final boolean isUpTo = pState.isUpTo()
                && pState.isLocked();
        final boolean isAdjust = pState.isAdjustable();
        final boolean isFull = pState.isFull();
        final boolean isContaining = pState.isContaining();
        final boolean isBaseStartOfPeriod = pState.isBaseStartOfPeriod();

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
            String myText = NLS_CONTAIN;
            if (!isContaining) {
                myText = isBaseStartOfPeriod
                        ? NLS_START
                        : NLS_END;
            }
            theStandardLabel.setText(myText);

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
