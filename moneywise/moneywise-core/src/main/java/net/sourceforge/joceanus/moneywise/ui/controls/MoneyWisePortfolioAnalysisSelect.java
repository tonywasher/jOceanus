/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPortfolioBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPortfolioBucket.MoneyWiseAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisPortfolioCashFilter;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIConstant;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUILabel;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBoxPaneManager;

/**
 * Portfolio Analysis Selection.
 */
public class MoneyWisePortfolioAnalysisSelect
        implements MoneyWiseAnalysisFilterSelection, TethysEventProvider<PrometheusDataEvent> {
    /**
     * Text for Portfolio Label.
     */
    private static final String NLS_PORTFOLIO = MoneyWiseBasicDataType.PORTFOLIO.getItemName();

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * The portfolio button.
     */
    private final TethysUIScrollButtonManager<MoneyWiseAnalysisPortfolioBucket> thePortButton;

    /**
     * Portfolio menu.
     */
    private final TethysUIScrollMenu<MoneyWiseAnalysisPortfolioBucket> thePortfolioMenu;

    /**
     * The active portfolio bucket list.
     */
    private MoneyWiseAnalysisPortfolioBucketList thePortfolios;

    /**
     * The state.
     */
    private MoneyWisePortfolioState theState;

    /**
     * The savePoint.
     */
    private MoneyWisePortfolioState theSavePoint;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected MoneyWisePortfolioAnalysisSelect(final TethysUIFactory<?> pFactory) {
        /* Create the portfolio button */
        thePortButton = pFactory.buttonFactory().newScrollButton(MoneyWiseAnalysisPortfolioBucket.class);

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Create the labels */
        final TethysUILabel myPortLabel = pFactory.controlFactory().newLabel(NLS_PORTFOLIO + TethysUIConstant.STR_COLON);

        /* Define the layout */
        thePanel = pFactory.paneFactory().newHBoxPane();
        thePanel.addSpacer();
        thePanel.addNode(myPortLabel);
        thePanel.addNode(thePortButton);

        /* Create initial state */
        theState = new MoneyWisePortfolioState();
        theState.applyState();

        /* Access the menus */
        thePortfolioMenu = thePortButton.getMenu();

        /* Create the listeners */
        final TethysEventRegistrar<TethysUIEvent> myRegistrar = thePortButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewPortfolio());
        thePortButton.setMenuConfigurator(e -> buildPortfolioMenu());
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePanel;
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public MoneyWiseAnalysisPortfolioCashFilter getFilter() {
        return theState.getFilter();
    }

    @Override
    public boolean isAvailable() {
        return thePortfolios != null
                && !thePortfolios.isEmpty();
    }

    /**
     * Create SavePoint.
     */
    public void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new MoneyWisePortfolioState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new MoneyWisePortfolioState(theSavePoint);

        /* Apply the state */
        theState.applyState();
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* Determine whether there are any portfolios to select */
        final boolean portAvailable = bEnabled && isAvailable();

        /* Pass call on to buttons */
        thePortButton.setEnabled(portAvailable);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    /**
     * Set analysis.
     * @param pAnalysis the analysis.
     */
    public void setAnalysis(final MoneyWiseAnalysis pAnalysis) {
        /* Access buckets */
        thePortfolios = pAnalysis.getPortfolios();

        /* Obtain the current portfolio */
        MoneyWiseAnalysisPortfolioBucket myPortfolio = theState.getPortfolio();

        /* Switch to versions from the analysis */
        myPortfolio = myPortfolio != null
                ? thePortfolios.getMatchingPortfolio(myPortfolio.getPortfolio())
                : thePortfolios.getDefaultPortfolio();

        /* Set the portfolio */
        theState.setThePortfolio(myPortfolio);
        theState.setDateRange(pAnalysis.getDateRange());
        theState.applyState();
    }

    @Override
    public void setFilter(final MoneyWiseAnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof MoneyWiseAnalysisPortfolioCashFilter) {
            /* Access filter */
            final MoneyWiseAnalysisPortfolioCashFilter myFilter = (MoneyWiseAnalysisPortfolioCashFilter) pFilter;

            /* Obtain the filter bucket */
            MoneyWiseAnalysisPortfolioBucket myPortfolio = myFilter.getPortfolioBucket();

            /* Look for the equivalent bucket */
            myPortfolio = thePortfolios.getMatchingPortfolio(myPortfolio.getPortfolio());

            /* Set the portfolio */
            theState.setThePortfolio(myPortfolio);
            theState.setDateRange(myFilter.getDateRange());
            theState.applyState();
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
     * Build Portfolio menu.
     */
    private void buildPortfolioMenu() {
        /* Reset the popUp menu */
        thePortfolioMenu.removeAllItems();

        /* Record active item */
        TethysUIScrollItem<MoneyWiseAnalysisPortfolioBucket> myActive = null;
        final MoneyWiseAnalysisPortfolioBucket myCurr = theState.getPortfolio();

        /* Loop through the available portfolio values */
        final Iterator<MoneyWiseAnalysisPortfolioBucket> myIterator = thePortfolios.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisPortfolioBucket myBucket = myIterator.next();

            /* Create a new MenuItem and add it to the popUp */
            final TethysUIScrollItem<MoneyWiseAnalysisPortfolioBucket> myItem = thePortfolioMenu.addItem(myBucket);

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
     * SavePoint values.
     */
    private final class MoneyWisePortfolioState {
        /**
         * The active Portfolio.
         */
        private MoneyWiseAnalysisPortfolioBucket thePortfolio;

        /**
         * The dateRange.
         */
        private TethysDateRange theDateRange;

        /**
         * The active filter.
         */
        private MoneyWiseAnalysisPortfolioCashFilter theFilter;

        /**
         * Constructor.
         */
        private MoneyWisePortfolioState() {
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private MoneyWisePortfolioState(final MoneyWisePortfolioState pState) {
            /* Initialise state */
            thePortfolio = pState.getPortfolio();
            theDateRange = pState.getDateRange();
            theFilter = pState.getFilter();
        }

        /**
         * Obtain the Portfolio.
         * @return the portfolio
         */
        private MoneyWiseAnalysisPortfolioBucket getPortfolio() {
            return thePortfolio;
        }

        /**
         * Obtain the dateRange.
         * @return the dateRange
         */
        private TethysDateRange getDateRange() {
            return theDateRange;
        }

        /**
         * Obtain the Filter.
         * @return the filter
         */
        private MoneyWiseAnalysisPortfolioCashFilter getFilter() {
            return theFilter;
        }

        /**
         * Set new Portfolio.
         * @param pPortfolio the Portfolio
         * @return true/false did a change occur
         */
        private boolean setPortfolio(final MoneyWiseAnalysisPortfolioBucket pPortfolio) {
            /* Adjust the selected portfolio */
            if (!MetisDataDifference.isEqual(pPortfolio, thePortfolio)) {
                setThePortfolio(pPortfolio);
                return true;
            }
            return false;
        }

        /**
         * Set the Portfolio.
         * @param pPortfolio the Portfolio
         */
        private void setThePortfolio(final MoneyWiseAnalysisPortfolioBucket pPortfolio) {
            /* Set the selected portfolio */
            thePortfolio = pPortfolio;
            if (thePortfolio != null) {
                theFilter = new MoneyWiseAnalysisPortfolioCashFilter(thePortfolio);
                theFilter.setDateRange(theDateRange);
            } else {
                theFilter = null;
            }
        }

        /**
         * Set the dateRange.
         * @param pRange the dateRange
         */
        private void setDateRange(final TethysDateRange pRange) {
            /* Store the dateRange */
            theDateRange = pRange;
            if (theFilter != null) {
                theFilter.setDateRange(theDateRange);
            }
        }

        /**
         * Apply the State.
         */
        private void applyState() {
            /* Adjust the lock-down */
            setEnabled(true);
            thePortButton.setValue(thePortfolio);
        }
    }
}
