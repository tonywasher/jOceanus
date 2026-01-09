/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012-2026 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.moneywise.ui.controls;

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.ui.MetisIcon;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import net.sourceforge.joceanus.prometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.tethys.api.base.TethysUIArrowIconId;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButton;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.tethys.api.button.TethysUIDateButtonManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.tethys.api.control.TethysUILabel;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBoxPaneManager;

/**
 * SpotRates selection panel.
 */
public class MoneyWiseSpotRatesSelect
        implements OceanusEventProvider<PrometheusDataEvent>, TethysUIComponent {
    /**
     * Text for Currency Prompt.
     */
    private static final String NLS_CURRENCY = MoneyWiseUIResource.SPOTRATE_PROMPT_CURR.getValue();

    /**
     * Text for Date Label.
     */
    private static final String NLS_DATE = MoneyWiseUIResource.SPOTEVENT_DATE.getValue();

    /**
     * Text for Title.
     */
    private static final String NLS_TITLE = MoneyWiseUIResource.SPOTRATE_TITLE.getValue();

    /**
     * Text for Next toolTip.
     */
    private static final String NLS_NEXTTIP = MoneyWiseUIResource.SPOTRATE_NEXT.getValue();

    /**
     * Text for Previous toolTip.
     */
    private static final String NLS_PREVTIP = MoneyWiseUIResource.SPOTRATE_PREV.getValue();

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * The data view.
     */
    private final MoneyWiseView theView;

    /**
     * The currency label.
     */
    private final TethysUILabel theCurrLabel;

    /**
     * The date button.
     */
    private final TethysUIDateButtonManager theDateButton;

    /**
     * The next button.
     */
    private final TethysUIButton theNext;

    /**
     * The previous button.
     */
    private final TethysUIButton thePrev;

    /**
     * The download button.
     */
    private final TethysUIButton theDownloadButton;

    /**
     * The current state.
     */
    private MoneyWiseSpotRatesState theState;

    /**
     * The saved state.
     */
    private MoneyWiseSpotRatesState theSavePoint;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pView the data view
     */
    public MoneyWiseSpotRatesSelect(final TethysUIFactory<?> pFactory,
                                    final MoneyWiseView pView) {
        /* Store table and view details */
        theView = pView;

        /* Create Event Manager */
        theEventManager = new OceanusEventManager<>();

        /* Create Labels */
        final TethysUIControlFactory myControls = pFactory.controlFactory();
        final TethysUILabel myCurr = myControls.newLabel(NLS_CURRENCY);
        final TethysUILabel myDate = myControls.newLabel(NLS_DATE);

        /* Create the DateButton */
        final TethysUIButtonFactory<?> myButtons = pFactory.buttonFactory();
        theDateButton = myButtons.newDateButton();

        /* Create the Download Button */
        theDownloadButton = myButtons.newButton();
        MetisIcon.configureDownloadIconButton(theDownloadButton);

        /* Create the Currency indication */
        theCurrLabel = myControls.newLabel();

        /* Create the Buttons */
        theNext = myButtons.newButton();
        theNext.setIcon(TethysUIArrowIconId.RIGHT);
        theNext.setToolTip(NLS_NEXTTIP);
        thePrev = myButtons.newButton();
        thePrev.setIcon(TethysUIArrowIconId.LEFT);
        thePrev.setToolTip(NLS_PREVTIP);

        /* Create initial state */
        theState = new MoneyWiseSpotRatesState();

        /* Create the panel */
        thePanel = pFactory.paneFactory().newHBoxPane();
        thePanel.setBorderTitle(NLS_TITLE);

        /* Define the layout */
        thePanel.addNode(myCurr);
        thePanel.addNode(theCurrLabel);
        thePanel.addSpacer();
        thePanel.addNode(myDate);
        thePanel.addNode(thePrev);
        thePanel.addNode(theDateButton);
        thePanel.addNode(theNext);
        thePanel.addSpacer();
        thePanel.addNode(theDownloadButton);

        /* Initialise the data from the view */
        refreshData();

        /* Apply the current state */
        theState.applyState();

        /* Add the listeners */
        theDateButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewDate());
        theDownloadButton.getEventRegistrar().addEventListener(TethysUIEvent.PRESSED, e -> theEventManager.fireEvent(PrometheusDataEvent.DOWNLOAD));
        theNext.getEventRegistrar().addEventListener(e -> {
            theState.setNext();
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        });
        thePrev.getEventRegistrar().addEventListener(e -> {
            theState.setPrev();
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        });
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePanel;
    }

    @Override
    public OceanusEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Get the selected date.
     * @return the date
     */
    public OceanusDate getDate() {
        return theState.getDate();
    }

    /**
     * Refresh data.
     */
    public final void refreshData() {
        /* Access the data */
        final OceanusDateRange myRange = theView.getRange();

        /* Set the range for the Date Button */
        setRange(myRange);

        /* Set the currency name */
        final MoneyWiseDataSet myData = theView.getData();
        final MoneyWiseCurrency myDefault = myData.getReportingCurrency();
        theCurrLabel.setText(myDefault != null
                ? myDefault.getDesc() + " (" + myDefault.getName() + ")"
                : null);
    }

    /**
     * Set the range for the date box.
     * @param pRange the Range to set
     */
    private void setRange(final OceanusDateRange pRange) {
        final OceanusDate myStart = (pRange == null)
                ? null
                : pRange.getStart();
        final OceanusDate myEnd = (pRange == null)
                ? null
                : pRange.getEnd();

        /* Set up range */
        theDateButton.setEarliestDate(myStart);
        theDateButton.setLatestDate(myEnd);
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        theNext.setEnabled(bEnabled && (theState.getNextDate() != null));
        thePrev.setEnabled(bEnabled && (theState.getPrevDate() != null));
        theDateButton.setEnabled(bEnabled);
        theDownloadButton.setEnabled(bEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    /**
     * Create SavePoint.
     */
    public void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new MoneyWiseSpotRatesState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new MoneyWiseSpotRatesState(theSavePoint);

        /* Apply the state */
        theState.applyState();
    }

    /**
     * Set Adjacent dates.
     * @param pPrev the previous Date
     * @param pNext the next Date
     */
    public void setAdjacent(final OceanusDate pPrev,
                            final OceanusDate pNext) {
        /* Record the dates */
        theState.setAdjacent(pPrev, pNext);
    }

    /**
     * Handle new Date.
     */
    private void handleNewDate() {
        /* Select the new date */
        if (theState.setDate(theDateButton)) {
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * SavePoint values.
     */
    private final class MoneyWiseSpotRatesState {
        /**
         * Selected date.
         */
        private OceanusDate theDate;

        /**
         * Next date.
         */
        private OceanusDate theNextDate;

        /**
         * Previous date.
         */
        private OceanusDate thePrevDate;

        /**
         * Constructor.
         */
        private MoneyWiseSpotRatesState() {
            theDate = new OceanusDate();
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private MoneyWiseSpotRatesState(final MoneyWiseSpotRatesState pState) {
            theDate = new OceanusDate(pState.getDate());
            if (pState.getNextDate() != null) {
                theNextDate = new OceanusDate(pState.getNextDate());
            }
            if (pState.getPrevDate() != null) {
                thePrevDate = new OceanusDate(pState.getPrevDate());
            }
        }

        /**
         * Get the selected date.
         * @return the date
         */
        private OceanusDate getDate() {
            return theDate;
        }

        /**
         * Get the next date.
         * @return the date
         */
        private OceanusDate getNextDate() {
            return theNextDate;
        }

        /**
         * Get the previous date.
         * @return the date
         */
        private OceanusDate getPrevDate() {
            return thePrevDate;
        }

        /**
         * Set new Date.
         * @param pButton the Button with the new date
         * @return true/false did a change occur
         */
        private boolean setDate(final TethysUIDateButtonManager pButton) {
            /* Adjust the date and build the new range */
            final OceanusDate myDate = new OceanusDate(pButton.getSelectedDate());
            if (!MetisDataDifference.isEqual(myDate, theDate)) {
                theDate = myDate;
                return true;
            }
            return false;
        }

        /**
         * Set Next Date.
         */
        private void setNext() {
            /* Copy date */
            theDate = new OceanusDate(theNextDate);
            applyState();
        }

        /**
         * Set Previous Date.
         */
        private void setPrev() {
            /* Copy date */
            theDate = new OceanusDate(thePrevDate);
            applyState();
        }

        /**
         * Set Adjacent dates.
         * @param pPrev the previous Date
         * @param pNext the next Date
         */
        private void setAdjacent(final OceanusDate pPrev,
                                 final OceanusDate pNext) {
            /* Record the dates */
            thePrevDate = pPrev;
            theNextDate = pNext;

            /* Adjust values */
            setEnabled(true);
        }

        /**
         * Apply the State.
         */
        private void applyState() {
            /* Adjust the lock-down */
            setEnabled(true);
            theDateButton.setSelectedDate(theDate);

            /* Determine whether we are todays date */
            final boolean isToday = MetisDataDifference.isEqual(theDate, new OceanusDate());
            theDownloadButton.setVisible(isToday);
        }
    }
}
