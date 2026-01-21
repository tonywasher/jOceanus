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
import io.github.tonywasher.joceanus.oceanus.date.OceanusDateFormatter;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDatePeriod;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDateRange;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDateRangeState;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDateResource;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventManager;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIArrowIconId;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIEvent;
import io.github.tonywasher.joceanus.tethys.api.button.TethysUIButton;
import io.github.tonywasher.joceanus.tethys.api.button.TethysUIButtonFactory;
import io.github.tonywasher.joceanus.tethys.api.button.TethysUIDateButtonManager;
import io.github.tonywasher.joceanus.tethys.api.button.TethysUIDateRangeSelector;
import io.github.tonywasher.joceanus.tethys.api.button.TethysUIScrollButtonManager;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUIControlFactory;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUILabel;
import io.github.tonywasher.joceanus.tethys.api.menu.TethysUIScrollMenu;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIBoxPaneManager;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIPaneFactory;
import io.github.tonywasher.joceanus.tethys.core.base.TethysUICoreComponent;
import io.github.tonywasher.joceanus.tethys.core.base.TethysUIResource;
import io.github.tonywasher.joceanus.tethys.core.factory.TethysUICoreFactory;

import java.util.Locale;

/**
 * DateRange Selector.
 */
public abstract class TethysUICoreDateRangeSelector
        extends TethysUICoreComponent
        implements TethysUIDateRangeSelector {
    /**
     * ToolTip for Next Button.
     */
    private static final String NLS_NEXTTIP = TethysUIResource.TIP_NEXTDATE.getValue();

    /**
     * ToolTip for Previous Button.
     */
    private static final String NLS_PREVTIP = TethysUIResource.TIP_PREVDATE.getValue();

    /**
     * Text for Start Label.
     */
    private static final String NLS_START = TethysUIResource.LABEL_STARTING.getValue();

    /**
     * Text for End Label.
     */
    private static final String NLS_END = TethysUIResource.LABEL_ENDING.getValue();

    /**
     * Text for Containing Label.
     */
    private static final String NLS_CONTAIN = TethysUIResource.LABEL_CONTAINING.getValue();

    /**
     * Text for Period Label.
     */
    private static final String NLS_PERIOD = TethysUIResource.LABEL_PERIOD.getValue();

    /**
     * Text for Box Title.
     */
    protected static final String NLS_TITLE = OceanusDateResource.TITLE_BOX.getValue();

    /**
     * The GUI Factory.
     */
    private final TethysUICoreFactory<?> theGuiFactory;

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<TethysUIEvent> theEventManager;

    /**
     * The formatter.
     */
    private final OceanusDateFormatter theFormatter;

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
    private final TethysUIScrollButtonManager<OceanusDatePeriod> thePeriodButton;

    /**
     * The Published DateRange.
     */
    private OceanusDateRange thePublishedRange;

    /**
     * The DateRange State.
     */
    private OceanusDateRangeState theState;

    /**
     * The Saved state.
     */
    private OceanusDateRangeState theSavePoint;

    /**
     * Constructor.
     *
     * @param pFactory     the GUI factory
     * @param pBaseIsStart is the baseDate the start of the period? (true/false)
     */
    protected TethysUICoreDateRangeSelector(final TethysUICoreFactory<?> pFactory,
                                            final boolean pBaseIsStart) {
        /* Store the parameters */
        theFormatter = pFactory.getDataFormatter().getDateFormatter();

        /* Record the factory */
        theGuiFactory = pFactory;

        /* Create event manager */
        theEventManager = new OceanusEventManager<>();

        /* Create initial state */
        theState = new OceanusDateRangeState(pBaseIsStart);

        /* Access the factories */
        final TethysUIButtonFactory<?> myButtonFactory = pFactory.buttonFactory();
        final TethysUIControlFactory myControlFactory = pFactory.controlFactory();
        final TethysUIPaneFactory myPaneFactory = pFactory.paneFactory();

        /* Create the buttons */
        theStartButton = myButtonFactory.newDateButton();
        theEndButton = myButtonFactory.newDateButton();
        theBaseButton = myButtonFactory.newDateButton();
        thePeriodButton = myButtonFactory.newScrollButton(OceanusDatePeriod.class);
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
        theStartButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewStartDate(e.getDetails(OceanusDate.class)));
        theEndButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewEndDate(e.getDetails(OceanusDate.class)));
        theBaseButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewBaseDate(e.getDetails(OceanusDate.class)));
        thePeriodButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> setPeriod(thePeriodButton.getValue()));
    }

    @Override
    public Integer getId() {
        return theControl.getId();
    }

    @Override
    public OceanusEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the control.
     *
     * @return the control
     */
    protected TethysUIBoxPaneManager getControl() {
        return theControl;
    }

    /**
     * Build period menu.
     *
     * @param pMenu the menu
     */
    private static void buildPeriodMenu(final TethysUIScrollMenu<OceanusDatePeriod> pMenu) {
        /* Loop through the periods */
        for (OceanusDatePeriod myPeriod : OceanusDatePeriod.values()) {
            /* Create a new MenuItem for the period */
            pMenu.addItem(myPeriod);
        }
    }

    @Override
    public OceanusDateRange getRange() {
        return theState.getRange();
    }

    /**
     * Obtain current state.
     *
     * @return the current state
     */
    private OceanusDateRangeState getState() {
        return theState;
    }

    @Override
    public final void setOverallRange(final OceanusDateRange pRange) {
        theState.adjustOverallRange(pRange);
        applyState();
    }

    @Override
    public void setLocale(final Locale pLocale) {
        theFormatter.setLocale(pLocale);
        theState.setLocale(pLocale);
        applyState();
    }

    @Override
    public void setPeriod(final OceanusDatePeriod pPeriod) {
        theState.setPeriod(pPeriod);
        applyState();
    }

    @Override
    public void lockPeriod(final boolean isLocked) {
        theState.lockPeriod(isLocked);
        applyState();
    }

    @Override
    public void setSelection(final TethysUIDateRangeSelector pSource) {
        /* Access the state */
        final OceanusDateRangeState myState = ((TethysUICoreDateRangeSelector) pSource).getState();

        /* Accept this state */
        theState = new OceanusDateRangeState(myState);

        /* Build the range */
        applyState();
    }

    @Override
    public void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new OceanusDateRangeState(theState);
    }

    @Override
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new OceanusDateRangeState(theSavePoint);
        applyState();
    }

    /**
     * Handle new startDate.
     *
     * @param pDate the new date
     */
    private void handleNewStartDate(final OceanusDate pDate) {
        theState.setStartDate(pDate);
        applyState();
    }

    /**
     * Handle new endDate.
     *
     * @param pDate the new date
     */
    private void handleNewEndDate(final OceanusDate pDate) {
        theState.setEndDate(pDate);
        applyState();
    }

    /**
     * Handle new baseDate.
     *
     * @param pDate the new date
     */
    private void handleNewBaseDate(final OceanusDate pDate) {
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
        final OceanusDateRange myOverallRange = theState.getOverallRange();

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
        OceanusDateRange myNew = getRange();
        if (OceanusDateRange.isDifferent(thePublishedRange, myNew)) {
            /* Record the new range and create a copy */
            thePublishedRange = myNew;
            myNew = new OceanusDateRange(myNew);

            /* Fire the value change */
            theEventManager.fireEvent(TethysUIEvent.NEWVALUE, myNew);
        }
    }

    /**
     * Apply the state.
     *
     * @param pState the state
     */
    protected void applyState(final OceanusDateRangeState pState) {
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
