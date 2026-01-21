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
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter;
import io.github.tonywasher.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisSecurityFilter;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusDataEvent;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIComponent;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIConstant;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIEvent;
import io.github.tonywasher.joceanus.tethys.api.button.TethysUIButtonFactory;
import io.github.tonywasher.joceanus.tethys.api.button.TethysUIScrollButtonManager;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUIControlFactory;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUILabel;
import io.github.tonywasher.joceanus.tethys.api.factory.TethysUIFactory;
import io.github.tonywasher.joceanus.tethys.api.menu.TethysUIScrollItem;
import io.github.tonywasher.joceanus.tethys.api.menu.TethysUIScrollMenu;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIBoxPaneManager;

import java.util.Iterator;

/**
 * Security Analysis Selection.
 */
public class MoneyWiseXSecurityAnalysisSelect
        implements MoneyWiseXAnalysisFilterSelection, OceanusEventProvider<PrometheusDataEvent> {
    /**
     * Text for Portfolio Label.
     */
    private static final String NLS_PORTFOLIO = MoneyWiseBasicDataType.PORTFOLIO.getItemName();

    /**
     * Text for Security Label.
     */
    private static final String NLS_SECURITY = MoneyWiseBasicDataType.SECURITY.getItemName();

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * The security button.
     */
    private final TethysUIScrollButtonManager<MoneyWiseXAnalysisSecurityBucket> theSecButton;

    /**
     * The portfolio button.
     */
    private final TethysUIScrollButtonManager<MoneyWiseXAnalysisPortfolioBucket> thePortButton;

    /**
     * Portfolio menu.
     */
    private final TethysUIScrollMenu<MoneyWiseXAnalysisPortfolioBucket> thePortfolioMenu;

    /**
     * Security menu.
     */
    private final TethysUIScrollMenu<MoneyWiseXAnalysisSecurityBucket> theSecurityMenu;

    /**
     * The active portfolio bucket list.
     */
    private MoneyWiseXAnalysisPortfolioBucketList thePortfolios;

    /**
     * The state.
     */
    private MoneyWiseSecurityState theState;

    /**
     * The savePoint.
     */
    private MoneyWiseSecurityState theSavePoint;

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     */
    protected MoneyWiseXSecurityAnalysisSelect(final TethysUIFactory<?> pFactory) {
        /* Create the security button */
        final TethysUIButtonFactory<?> myButtons = pFactory.buttonFactory();
        theSecButton = myButtons.newScrollButton(MoneyWiseXAnalysisSecurityBucket.class);

        /* Create the portfolio button */
        thePortButton = myButtons.newScrollButton(MoneyWiseXAnalysisPortfolioBucket.class);

        /* Create Event Manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the labels */
        final TethysUIControlFactory myControls = pFactory.controlFactory();
        final TethysUILabel myPortLabel = myControls.newLabel(NLS_PORTFOLIO + TethysUIConstant.STR_COLON);
        final TethysUILabel mySecLabel = myControls.newLabel(NLS_SECURITY + TethysUIConstant.STR_COLON);

        /* Define the layout */
        thePanel = pFactory.paneFactory().newHBoxPane();
        thePanel.addSpacer();
        thePanel.addNode(myPortLabel);
        thePanel.addNode(thePortButton);
        thePanel.addStrut();
        thePanel.addNode(mySecLabel);
        thePanel.addNode(theSecButton);

        /* Create initial state */
        theState = new MoneyWiseSecurityState();
        theState.applyState();

        /* Access the menus */
        thePortfolioMenu = thePortButton.getMenu();
        theSecurityMenu = theSecButton.getMenu();

        /* Create the listener */
        OceanusEventRegistrar<TethysUIEvent> myRegistrar = thePortButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewPortfolio());
        thePortButton.setMenuConfigurator(e -> buildPortfolioMenu());
        myRegistrar = theSecButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewSecurity());
        theSecButton.setMenuConfigurator(e -> buildSecurityMenu());
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
    public MoneyWiseXAnalysisSecurityFilter getFilter() {
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
        theSavePoint = new MoneyWiseSecurityState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new MoneyWiseSecurityState(theSavePoint);

        /* Apply the state */
        theState.applyState();
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* Determine whether there are any Securities to select */
        final boolean secAvailable = bEnabled && isAvailable();

        /* Pass call on to buttons */
        theSecButton.setEnabled(secAvailable);
        thePortButton.setEnabled(secAvailable);
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

        /* Obtain the current security */
        MoneyWiseXAnalysisSecurityBucket mySecurity = theState.getSecurity();

        /* Switch to versions from the analysis */
        mySecurity = mySecurity != null
                ? thePortfolios.getMatchingSecurityHolding(mySecurity.getSecurityHolding())
                : thePortfolios.getDefaultSecurityHolding();
        final MoneyWiseXAnalysisPortfolioBucket myPortfolio = mySecurity != null
                ? thePortfolios.getMatchingPortfolio(mySecurity.getPortfolio())
                : thePortfolios.getDefaultPortfolio();

        /* Set the security */
        theState.setTheSecurity(myPortfolio, mySecurity);
        theState.setDateRange(pAnalysis.getDateRange());
        theState.applyState();
    }

    @Override
    public void setFilter(final MoneyWiseXAnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof MoneyWiseXAnalysisSecurityFilter myFilter) {
            /* Obtain the filter buckets */
            MoneyWiseXAnalysisSecurityBucket mySecurity = myFilter.getBucket();

            /* Look for the equivalent buckets */
            mySecurity = thePortfolios.getMatchingSecurityHolding(mySecurity.getSecurityHolding());

            /* Determine the matching portfolio bucket */
            final MoneyWiseXAnalysisPortfolioBucket myPortfolio = thePortfolios.getMatchingPortfolio(mySecurity.getPortfolio());

            /* Set the security */
            theState.setTheSecurity(myPortfolio, mySecurity);
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
     * Handle new Security.
     */
    private void handleNewSecurity() {
        /* Select the new security */
        if (theState.setSecurity(theSecButton.getValue())) {
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
     * Build Security menu.
     */
    private void buildSecurityMenu() {
        /* Reset the popUp menu */
        theSecurityMenu.removeAllItems();

        /* Access current portfolio */
        final MoneyWiseXAnalysisPortfolioBucket myPortfolio = theState.getPortfolio();
        final MoneyWiseXAnalysisSecurityBucket myCurr = theState.getSecurity();
        TethysUIScrollItem<MoneyWiseXAnalysisSecurityBucket> myActive = null;

        /* Loop through the available security values */
        final Iterator<MoneyWiseXAnalysisSecurityBucket> myIterator = myPortfolio.securityIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisSecurityBucket myBucket = myIterator.next();

            /* Create a new MenuItem and add it to the popUp */
            final TethysUIScrollItem<MoneyWiseXAnalysisSecurityBucket> myItem = theSecurityMenu.addItem(myBucket, myBucket.getSecurityName());

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
    private final class MoneyWiseSecurityState {
        /**
         * The active Portfolio.
         */
        private MoneyWiseXAnalysisPortfolioBucket thePortfolio;

        /**
         * The active SecurityBucket.
         */
        private MoneyWiseXAnalysisSecurityBucket theSecurity;

        /**
         * The dateRange.
         */
        private OceanusDateRange theDateRange;

        /**
         * The filter.
         */
        private MoneyWiseXAnalysisSecurityFilter theFilter;

        /**
         * Constructor.
         */
        private MoneyWiseSecurityState() {
        }

        /**
         * Constructor.
         *
         * @param pState state to copy from
         */
        private MoneyWiseSecurityState(final MoneyWiseSecurityState pState) {
            /* Initialise state */
            theSecurity = pState.getSecurity();
            thePortfolio = pState.getPortfolio();
            theDateRange = pState.getDateRange();
            theFilter = pState.getFilter();
        }

        /**
         * Obtain the Security Bucket.
         *
         * @return the Security
         */
        private MoneyWiseXAnalysisSecurityBucket getSecurity() {
            return theSecurity;
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
        private MoneyWiseXAnalysisSecurityFilter getFilter() {
            return theFilter;
        }

        /**
         * Set new Security.
         *
         * @param pSecurity the Security
         * @return true/false did a change occur
         */
        private boolean setSecurity(final MoneyWiseXAnalysisSecurityBucket pSecurity) {
            /* Adjust the selected security */
            if (!MetisDataDifference.isEqual(pSecurity, theSecurity)) {
                /* Store the security */
                setTheSecurity(thePortfolio, pSecurity);
                return true;
            }
            return false;
        }

        /**
         * Set new Security.
         *
         * @param pPortfolio the Portfolio
         * @param pSecurity  the Security
         */
        private void setTheSecurity(final MoneyWiseXAnalysisPortfolioBucket pPortfolio,
                                    final MoneyWiseXAnalysisSecurityBucket pSecurity) {
            /* Store the portfolio and security */
            thePortfolio = pPortfolio;
            theSecurity = pSecurity;
            if (theSecurity != null) {
                theFilter = new MoneyWiseXAnalysisSecurityFilter(theSecurity);
                theFilter.setDateRange(theDateRange);
            } else {
                theFilter = null;
            }
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
                setTheSecurity(pPortfolio, getFirstSecurity(pPortfolio));
                return true;
            }
            return false;
        }

        /**
         * Obtain first security for portfolio.
         *
         * @param pPortfolio the portfolio
         * @return the first security
         */
        private MoneyWiseXAnalysisSecurityBucket getFirstSecurity(final MoneyWiseXAnalysisPortfolioBucket pPortfolio) {
            /* Loop through the available security values */
            final Iterator<MoneyWiseXAnalysisSecurityBucket> myIterator = pPortfolio.securityIterator();
            return myIterator.hasNext()
                    ? myIterator.next()
                    : null;
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
            theSecButton.setValue(theSecurity, theSecurity == null
                    ? null
                    : theSecurity.getSecurityName());
            thePortButton.setValue(thePortfolio);
        }
    }
}
