/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.lethe.ui.controls;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.AnalysisManager;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.views.View;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysArrowIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysCheckBox;
import net.sourceforge.joceanus.jtethys.ui.TethysDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * SpotPrice selection panel.
 * @param <N> the node type
 * @param <I> the Icon Type
 */
public class MoneyWiseSpotPricesSelect<N, I>
        implements TethysEventProvider<PrometheusDataEvent>, TethysNode<N> {
    /**
     * Text for Date Label.
     */
    private static final String NLS_DATE = MoneyWiseUIResource.SPOTEVENT_DATE.getValue();

    /**
     * Text for Portfolio Label.
     */
    private static final String NLS_PORT = MoneyWiseDataType.PORTFOLIO.getItemName() + TethysLabel.STR_COLON;

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
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final TethysBoxPaneManager<N, I> thePanel;

    /**
     * The data view.
     */
    private final View<N, I> theView;

    /**
     * The date button.
     */
    private final TethysDateButtonManager<N, I> theDateButton;

    /**
     * The showClosed checkBox.
     */
    private final TethysCheckBox<N, I> theShowClosed;

    /**
     * The next button.
     */
    private final TethysButton<N, I> theNext;

    /**
     * The previous button.
     */
    private final TethysButton<N, I> thePrev;

    /**
     * The download button.
     */
    private final TethysButton<N, I> theDownloadButton;

    /**
     * The portfolio button.
     */
    private final TethysScrollButtonManager<PortfolioBucket, N, I> thePortButton;

    /**
     * The portfolio menu.
     */
    private final TethysScrollMenu<PortfolioBucket, I> thePortMenu;

    /**
     * The Portfolio list.
     */
    private PortfolioBucketList thePortfolios;

    /**
     * The current state.
     */
    private SpotPricesState theState;

    /**
     * The saved state.
     */
    private SpotPricesState theSavePoint;

    /**
     * Do we show closed accounts.
     */
    private boolean doShowClosed;

    /**
     * Are we refreshing data?
     */
    private boolean refreshingData;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pView the data view
     */
    public MoneyWiseSpotPricesSelect(final TethysGuiFactory<N, I> pFactory,
                                     final View<N, I> pView) {
        /* Store table and view details */
        theView = pView;

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Create Labels */
        TethysLabel<N, I> myDate = pFactory.newLabel(NLS_DATE);
        TethysLabel<N, I> myPort = pFactory.newLabel(NLS_PORT);

        /* Create the check box */
        theShowClosed = pFactory.newCheckBox(NLS_CLOSED);
        theShowClosed.setSelected(doShowClosed);

        /* Create the DateButton */
        theDateButton = pFactory.newDateButton();

        /* Create the Download Button */
        theDownloadButton = pFactory.newButton();
        MetisIcon.configureDownloadIconButton(theDownloadButton);

        /* Create the Buttons */
        theNext = pFactory.newButton();
        theNext.setIcon(TethysArrowIconId.RIGHT);
        theNext.setToolTip(NLS_NEXTTIP);
        thePrev = pFactory.newButton();
        thePrev.setIcon(TethysArrowIconId.LEFT);
        thePrev.setToolTip(NLS_PREVTIP);

        /* Create the portfolio button */
        thePortButton = pFactory.newScrollButton();

        /* Create initial state */
        theState = new SpotPricesState();

        /* Create the panel */
        thePanel = pFactory.newHBoxPane();
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
        TethysEventRegistrar<TethysUIEvent> myRegistrar = thePortButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewPortfolio());
        myRegistrar.addEventListener(TethysUIEvent.PREPAREDIALOG, e -> buildPortfolioMenu());
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
    public Integer getId() {
        return thePanel.getId();
    }

    @Override
    public N getNode() {
        return thePanel.getNode();
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
     * Get the selected portfolio.
     * @return the portfolio
     */
    public final Portfolio getPortfolio() {
        PortfolioBucket myBucket = theState.getPortfolio();
        return (myBucket == null)
                                  ? null
                                  : myBucket.getPortfolio();
    }

    /**
     * Do we show closed accounts?.
     * @return the date
     */
    public boolean getShowClosed() {
        return doShowClosed;
    }

    /**
     * Refresh data.
     */
    public final void refreshData() {
        /* Access the data */
        TethysDateRange myRange = theView.getRange();

        /* Set the range for the Date Button */
        setRange(myRange);

        /* Access portfolio list */
        AnalysisManager myManager = theView.getAnalysisManager();
        Analysis myAnalysis = myManager.getAnalysis();
        thePortfolios = myAnalysis.getPortfolios();

        /* Note that we are refreshing data */
        refreshingData = true;

        /* Obtain the current portfolio */
        PortfolioBucket myPortfolio = theState.getPortfolio();

        /* If we have a selected Portfolio */
        if (myPortfolio != null) {
            /* Look for the equivalent bucket */
            myPortfolio = thePortfolios.findItemById(myPortfolio.getOrderedId());
        }

        /* If we do not have an active portfolio and the list is non-empty */
        if ((myPortfolio == null) && (!thePortfolios.isEmpty())) {
            /* Access the first portfolio */
            myPortfolio = getFirstPortfolio();
        }

        /* Set the portfolio */
        theState.setPortfolio(myPortfolio);
        theState.applyState();

        /* Note that we have finished refreshing data */
        refreshingData = false;
    }

    /**
     * Obtain first portfolio.
     * @return the first portfolio
     */
    private PortfolioBucket getFirstPortfolio() {
        /* Loop through the available account values */
        Iterator<PortfolioBucket> myIterator = thePortfolios.iterator();
        return myIterator.hasNext()
                                    ? myIterator.next()
                                    : null;
    }

    /**
     * Set the range for the date box.
     * @param pRange the Range to set
     */
    public final void setRange(final TethysDateRange pRange) {
        TethysDate myStart = (pRange == null)
                                              ? null
                                              : pRange.getStart();
        TethysDate myEnd = (pRange == null)
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
        theSavePoint = new SpotPricesState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new SpotPricesState(theSavePoint);

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
     * Build the portfolio menu.
     */
    private void buildPortfolioMenu() {
        /* Reset the popUp menu */
        thePortMenu.removeAllItems();

        /* Record active item */
        TethysScrollMenuItem<PortfolioBucket> myActive = null;
        PortfolioBucket myCurr = theState.getPortfolio();

        /* Loop through the available portfolio values */
        Iterator<PortfolioBucket> myIterator = thePortfolios.iterator();
        while (myIterator.hasNext()) {
            PortfolioBucket myBucket = myIterator.next();

            /* Create a new MenuItem and add it to the popUp */
            TethysScrollMenuItem<PortfolioBucket> myItem = thePortMenu.addItem(myBucket);

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
            doShowClosed = theShowClosed.isSelected();
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * SavePoint values.
     */
    private final class SpotPricesState {
        /**
         * Portfolio.
         */
        private PortfolioBucket thePortfolio;

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
        private SpotPricesState() {
            theDate = new TethysDate();
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private SpotPricesState(final SpotPricesState pState) {
            thePortfolio = pState.getPortfolio();
            theDate = new TethysDate(pState.getDate());
            if (pState.getNextDate() != null) {
                theNextDate = new TethysDate(pState.getNextDate());
            }
            if (pState.getPrevDate() != null) {
                thePrevDate = new TethysDate(pState.getPrevDate());
            }
        }

        /**
         * Get the portfolio.
         * @return the portfolio
         */
        private PortfolioBucket getPortfolio() {
            return thePortfolio;
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
         * Set new Portfolio.
         * @param pPortfolio the Portfolio
         * @return true/false did a change occur
         */
        private boolean setPortfolio(final PortfolioBucket pPortfolio) {
            /* Adjust the selected portfolio */
            if (!MetisDifference.isEqual(pPortfolio, thePortfolio)) {
                thePortfolio = pPortfolio;
                return true;
            }
            return false;
        }

        /**
         * Set new Date.
         * @param pButton the Button with the new date
         * @return true/false did a change occur
         */
        private boolean setDate(final TethysDateButtonManager<N, I> pButton) {
            /* Adjust the date and build the new range */
            TethysDate myDate = new TethysDate(pButton.getSelectedDate());
            if (!MetisDifference.isEqual(myDate, theDate)) {
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
            thePortButton.setValue(thePortfolio);

            /* Determine whether we are todays date */
            boolean isToday = MetisDifference.isEqual(theDate, new TethysDate());
            theDownloadButton.setVisible(isToday);
        }
    }
}