/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.ui.controls;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIArrowIconId;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButton;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUILabel;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBoxPaneManager;

/**
 * SpotRates selection panel.
 */
public class MoneyWiseSpotRatesSelect
        implements TethysEventProvider<PrometheusDataEvent>, TethysUIComponent {
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
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

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
    private SpotRatesState theState;

    /**
     * The saved state.
     */
    private SpotRatesState theSavePoint;

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
        theEventManager = new TethysEventManager<>();

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
        theState = new SpotRatesState();

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
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Get the selected date.
     * @return the date
     */
    public TethysDate getDate() {
        return theState.getDate();
    }

    /**
     * Refresh data.
     */
    public final void refreshData() {
        /* Access the data */
        final TethysDateRange myRange = theView.getRange();

        /* Set the range for the Date Button */
        setRange(myRange);

        /* Set the currency name */
        final MoneyWiseData myData = theView.getData();
        final AssetCurrency myDefault = myData.getDefaultCurrency();
        theCurrLabel.setText(myDefault != null
                                               ? myDefault.getDesc() + " (" + myDefault.getName() + ")"
                                               : null);
    }

    /**
     * Set the range for the date box.
     * @param pRange the Range to set
     */
    private void setRange(final TethysDateRange pRange) {
        final TethysDate myStart = (pRange == null)
                                                    ? null
                                                    : pRange.getStart();
        final TethysDate myEnd = (pRange == null)
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
        theSavePoint = new SpotRatesState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new SpotRatesState(theSavePoint);

        /* Apply the state */
        theState.applyState();
    }

    /**
     * Set Adjacent dates.
     * @param pPrev the previous Date
     * @param pNext the next Date
     */
    public void setAdjacent(final TethysDate pPrev,
                            final TethysDate pNext) {
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
    private final class SpotRatesState {
        /**
         * Selected date.
         */
        private TethysDate theDate;

        /**
         * Next date.
         */
        private TethysDate theNextDate;

        /**
         * Previous date.
         */
        private TethysDate thePrevDate;

        /**
         * Constructor.
         */
        private SpotRatesState() {
            theDate = new TethysDate();
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private SpotRatesState(final SpotRatesState pState) {
            theDate = new TethysDate(pState.getDate());
            if (pState.getNextDate() != null) {
                theNextDate = new TethysDate(pState.getNextDate());
            }
            if (pState.getPrevDate() != null) {
                thePrevDate = new TethysDate(pState.getPrevDate());
            }
        }

        /**
         * Get the selected date.
         * @return the date
         */
        private TethysDate getDate() {
            return theDate;
        }

        /**
         * Get the next date.
         * @return the date
         */
        private TethysDate getNextDate() {
            return theNextDate;
        }

        /**
         * Get the previous date.
         * @return the date
         */
        private TethysDate getPrevDate() {
            return thePrevDate;
        }

        /**
         * Set new Date.
         * @param pButton the Button with the new date
         * @return true/false did a change occur
         */
        private boolean setDate(final TethysUIDateButtonManager pButton) {
            /* Adjust the date and build the new range */
            final TethysDate myDate = new TethysDate(pButton.getSelectedDate());
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
            theDate = new TethysDate(theNextDate);
            applyState();
        }

        /**
         * Set Previous Date.
         */
        private void setPrev() {
            /* Copy date */
            theDate = new TethysDate(thePrevDate);
            applyState();
        }

        /**
         * Set Adjacent dates.
         * @param pPrev the previous Date
         * @param pNext the next Date
         */
        private void setAdjacent(final TethysDate pPrev,
                                 final TethysDate pNext) {
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
            final boolean isToday = MetisDataDifference.isEqual(theDate, new TethysDate());
            theDownloadButton.setVisible(isToday);
        }
    }
}
