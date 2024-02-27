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

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisPortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisPortfolioBucket.MoneyWiseAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisSecurityBucket;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseAnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisSecurityFilter;
import net.sourceforge.joceanus.jprometheus.atlas.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIConstant;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUILabel;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBoxPaneManager;

/**
 * Security Analysis Selection.
 */
public class MoneyWiseSecurityAnalysisSelect
        implements MoneyWiseAnalysisFilterSelection, TethysEventProvider<PrometheusDataEvent> {
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
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * The security button.
     */
    private final TethysUIScrollButtonManager<MoneyWiseAnalysisSecurityBucket> theSecButton;

    /**
     * The portfolio button.
     */
    private final TethysUIScrollButtonManager<MoneyWiseAnalysisPortfolioBucket> thePortButton;

    /**
     * Portfolio menu.
     */
    private final TethysUIScrollMenu<MoneyWiseAnalysisPortfolioBucket> thePortfolioMenu;

    /**
     * Security menu.
     */
    private final TethysUIScrollMenu<MoneyWiseAnalysisSecurityBucket> theSecurityMenu;

    /**
     * The active portfolio bucket list.
     */
    private MoneyWiseAnalysisPortfolioBucketList thePortfolios;

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
     * @param pFactory the GUI factory
     */
    protected MoneyWiseSecurityAnalysisSelect(final TethysUIFactory<?> pFactory) {
        /* Create the security button */
        final TethysUIButtonFactory<?> myButtons = pFactory.buttonFactory();
        theSecButton = myButtons.newScrollButton(MoneyWiseAnalysisSecurityBucket.class);

        /* Create the portfolio button */
        thePortButton = myButtons.newScrollButton(MoneyWiseAnalysisPortfolioBucket.class);

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

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
        TethysEventRegistrar<TethysUIEvent> myRegistrar = thePortButton.getEventRegistrar();
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
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public MoneyWiseAnalysisSecurityFilter getFilter() {
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
     * @param pAnalysis the analysis.
     */
    public void setAnalysis(final MoneyWiseAnalysis pAnalysis) {
        /* Access buckets */
        thePortfolios = pAnalysis.getPortfolios();

        /* Obtain the current security */
        MoneyWiseAnalysisSecurityBucket mySecurity = theState.getSecurity();

        /* Switch to versions from the analysis */
        mySecurity = mySecurity != null
                ? thePortfolios.getMatchingSecurityHolding(mySecurity.getSecurityHolding())
                : thePortfolios.getDefaultSecurityHolding();
        final MoneyWiseAnalysisPortfolioBucket myPortfolio = mySecurity != null
                ? thePortfolios.getMatchingPortfolio(mySecurity.getPortfolio())
                : thePortfolios.getDefaultPortfolio();

        /* Set the security */
        theState.setTheSecurity(myPortfolio, mySecurity);
        theState.applyState();
    }

    @Override
    public void setFilter(final MoneyWiseAnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof MoneyWiseAnalysisSecurityFilter) {
            /* Access filter */
            final MoneyWiseAnalysisSecurityFilter myFilter = (MoneyWiseAnalysisSecurityFilter) pFilter;

            /* Obtain the filter buckets */
            MoneyWiseAnalysisSecurityBucket mySecurity = myFilter.getBucket();

            /* Look for the equivalent buckets */
            mySecurity = thePortfolios.getMatchingSecurityHolding(mySecurity.getSecurityHolding());

            /* Determine the matching portfolio bucket */
            final MoneyWiseAnalysisPortfolioBucket myPortfolio = thePortfolios.getMatchingPortfolio(mySecurity.getPortfolio());

            /* Set the security */
            theState.setTheSecurity(myPortfolio, mySecurity);
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
     * Build Security menu.
     */
    private void buildSecurityMenu() {
        /* Reset the popUp menu */
        theSecurityMenu.removeAllItems();

        /* Access current portfolio */
        final MoneyWiseAnalysisPortfolioBucket myPortfolio = theState.getPortfolio();
        final MoneyWiseAnalysisSecurityBucket myCurr = theState.getSecurity();
        TethysUIScrollItem<MoneyWiseAnalysisSecurityBucket> myActive = null;

        /* Loop through the available security values */
        final Iterator<MoneyWiseAnalysisSecurityBucket> myIterator = myPortfolio.securityIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisSecurityBucket myBucket = myIterator.next();

            /* Create a new MenuItem and add it to the popUp */
            final TethysUIScrollItem<MoneyWiseAnalysisSecurityBucket> myItem = theSecurityMenu.addItem(myBucket, myBucket.getSecurityName());

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
        private MoneyWiseAnalysisPortfolioBucket thePortfolio;

        /**
         * The active SecurityBucket.
         */
        private MoneyWiseAnalysisSecurityBucket theSecurity;

        /**
         * The filter.
         */
        private MoneyWiseAnalysisSecurityFilter theFilter;

        /**
         * Constructor.
         */
        private MoneyWiseSecurityState() {
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private MoneyWiseSecurityState(final MoneyWiseSecurityState pState) {
            /* Initialise state */
            theSecurity = pState.getSecurity();
            thePortfolio = pState.getPortfolio();
            theFilter = pState.getFilter();
        }

        /**
         * Obtain the Security Bucket.
         * @return the Security
         */
        private MoneyWiseAnalysisSecurityBucket getSecurity() {
            return theSecurity;
        }

        /**
         * Obtain the Portfolio.
         * @return the portfolio
         */
        private MoneyWiseAnalysisPortfolioBucket getPortfolio() {
            return thePortfolio;
        }

        /**
         * Obtain the Filter.
         * @return the filter
         */
        private MoneyWiseAnalysisSecurityFilter getFilter() {
            return theFilter;
        }

        /**
         * Set new Security.
         * @param pSecurity the Security
         * @return true/false did a change occur
         */
        private boolean setSecurity(final MoneyWiseAnalysisSecurityBucket pSecurity) {
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
         * @param pPortfolio the Portfolio
         * @param pSecurity the Security
         */
        private void setTheSecurity(final MoneyWiseAnalysisPortfolioBucket pPortfolio,
                                    final MoneyWiseAnalysisSecurityBucket pSecurity) {
            /* Store the portfolio and security */
            thePortfolio = pPortfolio;
            theSecurity = pSecurity;
            theFilter = theSecurity != null
                    ? new MoneyWiseAnalysisSecurityFilter(theSecurity)
                    : null;
        }

        /**
         * Set new Portfolio.
         * @param pPortfolio the Portfolio
         * @return true/false did a change occur
         */
        private boolean setPortfolio(final MoneyWiseAnalysisPortfolioBucket pPortfolio) {
            /* Adjust the selected portfolio */
            if (!MetisDataDifference.isEqual(pPortfolio, thePortfolio)) {
                setTheSecurity(pPortfolio, getFirstSecurity(pPortfolio));
                return true;
            }
            return false;
        }

        /**
         * Obtain first security for portfolio.
         * @param pPortfolio the portfolio
         * @return the first security
         */
        private MoneyWiseAnalysisSecurityBucket getFirstSecurity(final MoneyWiseAnalysisPortfolioBucket pPortfolio) {
            /* Loop through the available security values */
            final Iterator<MoneyWiseAnalysisSecurityBucket> myIterator = pPortfolio.securityIterator();
            return myIterator.hasNext()
                    ? myIterator.next()
                    : null;
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
