/*
 * MoneyWise: Finance Application
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
package net.sourceforge.joceanus.moneywise.ui.controls;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDateRange;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventManager;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import io.github.tonywasher.joceanus.metis.data.MetisDataDifference;
import io.github.tonywasher.joceanus.metis.ui.MetisIcon;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisManager;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPortfolioBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPortfolioBucket.MoneyWiseAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIArrowIconId;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIConstant;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButton;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.tethys.api.button.TethysUIDateButtonManager;
import net.sourceforge.joceanus.tethys.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUICheckBox;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.tethys.api.control.TethysUILabel;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBoxPaneManager;

import java.util.Iterator;

/**
 * SpotPrice selection panel.
 */
public class MoneyWiseSpotPricesSelect
        implements OceanusEventProvider<PrometheusDataEvent>, TethysUIComponent {
    /**
     * Text for Date Label.
     */
    private static final String NLS_DATE = MoneyWiseUIResource.SPOTEVENT_DATE.getValue();

    /**
     * Text for Portfolio Label.
     */
    private static final String NLS_PORT = MoneyWiseBasicDataType.PORTFOLIO.getItemName() + TethysUIConstant.STR_COLON;

    /**
     * Text for Show Closed.
     */
    private static final String NLS_CLOSED = MoneyWiseUIResource.UI_PROMPT_SHOWCLOSED.getValue();

    /**
     * Text for Title.
     */
    private static final String NLS_TITLE = MoneyWiseUIResource.SPOTPRICE_TITLE.getValue();

    /**
     * Text for Next toolTip.
     */
    private static final String NLS_NEXTTIP = MoneyWiseUIResource.SPOTPRICE_NEXT.getValue();

    /**
     * Text for Previous toolTip.
     */
    private static final String NLS_PREVTIP = MoneyWiseUIResource.SPOTPRICE_PREV.getValue();

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
     * The date button.
     */
    private final TethysUIDateButtonManager theDateButton;

    /**
     * The showClosed checkBox.
     */
    private final TethysUICheckBox theShowClosed;

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
     * The portfolio button.
     */
    private final TethysUIScrollButtonManager<MoneyWiseAnalysisPortfolioBucket> thePortButton;

    /**
     * The portfolio menu.
     */
    private final TethysUIScrollMenu<MoneyWiseAnalysisPortfolioBucket> thePortMenu;

    /**
     * The Portfolio list.
     */
    private MoneyWiseAnalysisPortfolioBucketList thePortfolios;

    /**
     * The current state.
     */
    private MoneyWiseSpotPricesState theState;

    /**
     * The saved state.
     */
    private MoneyWiseSpotPricesState theSavePoint;

    /**
     * Are we refreshing data?
     */
    private boolean refreshingData;

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     * @param pView    the data view
     */
    public MoneyWiseSpotPricesSelect(final TethysUIFactory<?> pFactory,
                                     final MoneyWiseView pView) {
        /* Store table and view details */
        theView = pView;

        /* Create Event Manager */
        theEventManager = new OceanusEventManager<>();

        /* Create Labels */
        final TethysUIControlFactory myControls = pFactory.controlFactory();
        final TethysUILabel myDate = myControls.newLabel(NLS_DATE);
        final TethysUILabel myPort = myControls.newLabel(NLS_PORT);

        /* Create the check box */
        theShowClosed = myControls.newCheckBox(NLS_CLOSED);

        /* Create the DateButton */
        final TethysUIButtonFactory<?> myButtons = pFactory.buttonFactory();
        theDateButton = myButtons.newDateButton();

        /* Create the Download Button */
        theDownloadButton = myButtons.newButton();
        MetisIcon.configureDownloadIconButton(theDownloadButton);

        /* Create the Buttons */
        theNext = myButtons.newButton();
        theNext.setIcon(TethysUIArrowIconId.RIGHT);
        theNext.setToolTip(NLS_NEXTTIP);
        thePrev = myButtons.newButton();
        thePrev.setIcon(TethysUIArrowIconId.LEFT);
        thePrev.setToolTip(NLS_PREVTIP);

        /* Create the portfolio button */
        thePortButton = myButtons.newScrollButton(MoneyWiseAnalysisPortfolioBucket.class);

        /* Create initial state */
        theState = new MoneyWiseSpotPricesState();

        /* Create the panel */
        thePanel = pFactory.paneFactory().newHBoxPane();
        thePanel.setBorderTitle(NLS_TITLE);

        /* Define the layout */
        thePanel.addNode(myDate);
        thePanel.addNode(thePrev);
        thePanel.addNode(theDateButton);
        thePanel.addNode(theNext);
        thePanel.addSpacer();
        thePanel.addNode(myPort);
        thePanel.addNode(thePortButton);
        thePanel.addSpacer();
        thePanel.addNode(theShowClosed);
        thePanel.addSpacer();
        thePanel.addNode(theDownloadButton);

        /* Initialise the data from the view */
        refreshData();

        /* Apply the current state */
        theState.applyState();

        /* Access the menus */
        thePortMenu = thePortButton.getMenu();

        /* Add the listeners */
        final OceanusEventRegistrar<TethysUIEvent> myRegistrar = thePortButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewPortfolio());
        thePortButton.setMenuConfigurator(e -> buildPortfolioMenu());
        theDownloadButton.getEventRegistrar().addEventListener(TethysUIEvent.PRESSED, e -> theEventManager.fireEvent(PrometheusDataEvent.DOWNLOAD));
        theDateButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewDate());
        theShowClosed.getEventRegistrar().addEventListener(e -> handleNewClosed());
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
     *
     * @return the date
     */
    public OceanusDate getDate() {
        return theState.getDate();
    }

    /**
     * Get the selected portfolio.
     *
     * @return the portfolio
     */
    public final MoneyWisePortfolio getPortfolio() {
        final MoneyWiseAnalysisPortfolioBucket myBucket = theState.getPortfolio();
        return myBucket == null
                ? null
                : myBucket.getPortfolio();
    }

    /**
     * Do we show closed accounts?.
     *
     * @return the date
     */
    public boolean getShowClosed() {
        return theState.showClosed();
    }

    /**
     * Refresh data.
     */
    public final void refreshData() {
        /* Access the data */
        final OceanusDateRange myRange = theView.getRange();

        /* Set the range for the Date Button */
        setRange(myRange);

        /* Access portfolio list */
        final MoneyWiseAnalysisManager myManager = theView.getAnalysisManager();
        final MoneyWiseAnalysis myAnalysis = myManager.getAnalysis();
        thePortfolios = myAnalysis.getPortfolios();

        /* Note that we are refreshing data */
        refreshingData = true;

        /* Obtain the current portfolio */
        MoneyWiseAnalysisPortfolioBucket myPortfolio = theState.getPortfolio();

        /* Switch to portfolio in this analysis */
        myPortfolio = myPortfolio != null
                ? thePortfolios.getMatchingPortfolio(myPortfolio.getPortfolio())
                : thePortfolios.getDefaultPortfolio();

        /* Set the portfolio */
        theState.setPortfolio(myPortfolio);
        theState.applyState();

        /* Note that we have finished refreshing data */
        refreshingData = false;
    }

    /**
     * Set the range for the date box.
     *
     * @param pRange the Range to set
     */
    public final void setRange(final OceanusDateRange pRange) {
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
        thePortButton.setEnabled(bEnabled);
        theDownloadButton.setEnabled(bEnabled);
        theShowClosed.setEnabled(bEnabled);
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
        theSavePoint = new MoneyWiseSpotPricesState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new MoneyWiseSpotPricesState(theSavePoint);

        /* Apply the state */
        theState.applyState();
    }

    /**
     * Set Adjacent dates.
     *
     * @param pPrev the previous Date
     * @param pNext the next Date
     */
    public void setAdjacent(final OceanusDate pPrev,
                            final OceanusDate pNext) {
        /* Record the dates */
        theState.setAdjacent(pPrev, pNext);
    }

    /**
     * Build the portfolio menu.
     */
    private void buildPortfolioMenu() {
        /* Reset the popUp menu */
        thePortMenu.removeAllItems();

        /* Record active item */
        TethysUIScrollItem<MoneyWiseAnalysisPortfolioBucket> myActive = null;
        final MoneyWiseAnalysisPortfolioBucket myCurr = theState.getPortfolio();

        /* Loop through the available portfolio values */
        final Iterator<MoneyWiseAnalysisPortfolioBucket> myIterator = thePortfolios.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisPortfolioBucket myBucket = myIterator.next();

            /* Skip if the portfolio is closed and we are not showing closed accounts */
            if (!myBucket.isActive()
                    && !theState.showClosed()) {
                continue;
            }

            /* Create a new MenuItem and add it to the popUp */
            final TethysUIScrollItem<MoneyWiseAnalysisPortfolioBucket> myItem = thePortMenu.addItem(myBucket);

            /* If this is the active bucket */
            if (myBucket.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        if (myActive != null) {
            myActive.scrollToItem();
        }
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
     * Handle new Portfolio.
     */
    private void handleNewPortfolio() {
        /* Select the new portfolio */
        if (theState.setPortfolio(thePortButton.getValue())) {
            theState.applyState();
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * Handle new Closed.
     */
    private void handleNewClosed() {
        if (!refreshingData) {
            theState.setShowClosed(theShowClosed.isSelected());
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * SavePoint values.
     */
    private final class MoneyWiseSpotPricesState {
        /**
         * Portfolio.
         */
        private MoneyWiseAnalysisPortfolioBucket thePortfolio;

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
         * showClosed.
         */
        private boolean showClosed;

        /**
         * Constructor.
         */
        private MoneyWiseSpotPricesState() {
            theDate = new OceanusDate();
        }

        /**
         * Constructor.
         *
         * @param pState state to copy from
         */
        private MoneyWiseSpotPricesState(final MoneyWiseSpotPricesState pState) {
            thePortfolio = pState.getPortfolio();
            theDate = new OceanusDate(pState.getDate());
            if (pState.getNextDate() != null) {
                theNextDate = new OceanusDate(pState.getNextDate());
            }
            if (pState.getPrevDate() != null) {
                thePrevDate = new OceanusDate(pState.getPrevDate());
            }
            showClosed = pState.showClosed();
        }

        /**
         * Get the portfolio.
         *
         * @return the portfolio
         */
        private MoneyWiseAnalysisPortfolioBucket getPortfolio() {
            return thePortfolio;
        }

        /**
         * Get the selected date.
         *
         * @return the date
         */
        private OceanusDate getDate() {
            return theDate;
        }

        /**
         * Get the next date.
         *
         * @return the date
         */
        private OceanusDate getNextDate() {
            return theNextDate;
        }

        /**
         * Get the previous date.
         *
         * @return the date
         */
        private OceanusDate getPrevDate() {
            return thePrevDate;
        }

        /**
         * Get the showClosed flag.
         *
         * @return the showClosed
         */
        private boolean showClosed() {
            return showClosed;
        }

        /**
         * Set new Portfolio.
         *
         * @param pPortfolio the Portfolio
         * @return true/false did a change occur
         */
        private boolean setPortfolio(final MoneyWiseAnalysisPortfolioBucket pPortfolio) {
            /* Adjust the selected portfolio */
            if (!MetisDataDifference.isEqual(pPortfolio, thePortfolio)) {
                thePortfolio = pPortfolio;
                return true;
            }
            return false;
        }

        /**
         * Set new Date.
         *
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
         * Set showClosed.
         *
         * @param pShowClosed true/false
         */
        private void setShowClosed(final boolean pShowClosed) {
            /* Set flag */
            showClosed = pShowClosed;
            applyState();
        }

        /**
         * Set Adjacent dates.
         *
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
            thePortButton.setValue(thePortfolio);
            theShowClosed.setSelected(showClosed);

            /* Determine whether we are todays date */
            final boolean isToday = MetisDataDifference.isEqual(theDate, new OceanusDate());
            final boolean isActive = thePortfolio != null && thePortfolio.isActive();
            theDownloadButton.setVisible(isToday && isActive);
        }
    }
}
