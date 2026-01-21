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
package io.github.tonywasher.joceanus.moneywise.atlas.ui.controls;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDateRange;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventManager;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import io.github.tonywasher.joceanus.metis.data.MetisDataDifference;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket.MoneyWiseXAnalysisPortfolioBucketList;
import io.github.tonywasher.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter;
import io.github.tonywasher.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisPortfolioCashFilter;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusDataEvent;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIComponent;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIConstant;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIEvent;
import io.github.tonywasher.joceanus.tethys.api.button.TethysUIScrollButtonManager;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUILabel;
import io.github.tonywasher.joceanus.tethys.api.factory.TethysUIFactory;
import io.github.tonywasher.joceanus.tethys.api.menu.TethysUIScrollItem;
import io.github.tonywasher.joceanus.tethys.api.menu.TethysUIScrollMenu;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIBoxPaneManager;

import java.util.Iterator;

/**
 * Portfolio Analysis Selection.
 */
public class MoneyWiseXPortfolioAnalysisSelect
        implements MoneyWiseXAnalysisFilterSelection, OceanusEventProvider<PrometheusDataEvent> {
    /**
     * Text for Portfolio Label.
     */
    private static final String NLS_PORTFOLIO = MoneyWiseBasicDataType.PORTFOLIO.getItemName();

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * The portfolio button.
     */
    private final TethysUIScrollButtonManager<MoneyWiseXAnalysisPortfolioBucket> thePortButton;

    /**
     * Portfolio menu.
     */
    private final TethysUIScrollMenu<MoneyWiseXAnalysisPortfolioBucket> thePortfolioMenu;

    /**
     * The active portfolio bucket list.
     */
    private MoneyWiseXAnalysisPortfolioBucketList thePortfolios;

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
     *
     * @param pFactory the GUI factory
     */
    protected MoneyWiseXPortfolioAnalysisSelect(final TethysUIFactory<?> pFactory) {
        /* Create the portfolio button */
        thePortButton = pFactory.buttonFactory().newScrollButton(MoneyWiseXAnalysisPortfolioBucket.class);

        /* Create Event Manager */
        theEventManager = new OceanusEventManager<>();

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
        final OceanusEventRegistrar<TethysUIEvent> myRegistrar = thePortButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewPortfolio());
        thePortButton.setMenuConfigurator(e -> buildPortfolioMenu());
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePanel;
    }

    @Override
    public OceanusEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public MoneyWiseXAnalysisPortfolioCashFilter getFilter() {
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
     *
     * @param pAnalysis the analysis.
     */
    public void setAnalysis(final MoneyWiseXAnalysis pAnalysis) {
        /* Access buckets */
        thePortfolios = pAnalysis.getPortfolios();

        /* Obtain the current portfolio */
        MoneyWiseXAnalysisPortfolioBucket myPortfolio = theState.getPortfolio();

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
    public void setFilter(final MoneyWiseXAnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof MoneyWiseXAnalysisPortfolioCashFilter myFilter) {
            /* Obtain the filter bucket */
            MoneyWiseXAnalysisPortfolioBucket myPortfolio = myFilter.getPortfolioBucket();

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
        TethysUIScrollItem<MoneyWiseXAnalysisPortfolioBucket> myActive = null;
        final MoneyWiseXAnalysisPortfolioBucket myCurr = theState.getPortfolio();

        /* Loop through the available portfolio values */
        final Iterator<MoneyWiseXAnalysisPortfolioBucket> myIterator = thePortfolios.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisPortfolioBucket myBucket = myIterator.next();

            /* Create a new MenuItem and add it to the popUp */
            final TethysUIScrollItem<MoneyWiseXAnalysisPortfolioBucket> myItem = thePortfolioMenu.addItem(myBucket);

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
        private MoneyWiseXAnalysisPortfolioBucket thePortfolio;

        /**
         * The dateRange.
         */
        private OceanusDateRange theDateRange;

        /**
         * The active filter.
         */
        private MoneyWiseXAnalysisPortfolioCashFilter theFilter;

        /**
         * Constructor.
         */
        private MoneyWisePortfolioState() {
        }

        /**
         * Constructor.
         *
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
         *
         * @return the portfolio
         */
        private MoneyWiseXAnalysisPortfolioBucket getPortfolio() {
            return thePortfolio;
        }

        /**
         * Obtain the dateRange.
         *
         * @return the dateRange
         */
        private OceanusDateRange getDateRange() {
            return theDateRange;
        }

        /**
         * Obtain the Filter.
         *
         * @return the filter
         */
        private MoneyWiseXAnalysisPortfolioCashFilter getFilter() {
            return theFilter;
        }

        /**
         * Set new Portfolio.
         *
         * @param pPortfolio the Portfolio
         * @return true/false did a change occur
         */
        private boolean setPortfolio(final MoneyWiseXAnalysisPortfolioBucket pPortfolio) {
            /* Adjust the selected portfolio */
            if (!MetisDataDifference.isEqual(pPortfolio, thePortfolio)) {
                setThePortfolio(pPortfolio);
                return true;
            }
            return false;
        }

        /**
         * Set the Portfolio.
         *
         * @param pPortfolio the Portfolio
         */
        private void setThePortfolio(final MoneyWiseXAnalysisPortfolioBucket pPortfolio) {
            /* Set the selected portfolio */
            thePortfolio = pPortfolio;
            if (thePortfolio != null) {
                theFilter = new MoneyWiseXAnalysisPortfolioCashFilter(thePortfolio);
                theFilter.setDateRange(theDateRange);
            } else {
                theFilter = null;
            }
        }

        /**
         * Set the dateRange.
         *
         * @param pRange the dateRange
         */
        private void setDateRange(final OceanusDateRange pRange) {
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
