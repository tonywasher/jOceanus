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

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter.SecurityFilter;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Security Analysis Selection.
 * @param <N> the node type
 * @param <I> the Icon Type
 */
public class MoneyWiseSecurityAnalysisSelect<N, I>
        implements MoneyWiseAnalysisFilterSelection<N>, TethysEventProvider<PrometheusDataEvent> {
    /**
     * Text for Portfolio Label.
     */
    private static final String NLS_PORTFOLIO = MoneyWiseDataType.PORTFOLIO.getItemName();

    /**
     * Text for Security Label.
     */
    private static final String NLS_SECURITY = MoneyWiseDataType.SECURITY.getItemName();

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final TethysBoxPaneManager<N, I> thePanel;

    /**
     * The security button.
     */
    private final TethysScrollButtonManager<SecurityBucket, N, I> theSecButton;

    /**
     * The portfolio button.
     */
    private final TethysScrollButtonManager<PortfolioBucket, N, I> thePortButton;

    /**
     * Portfolio menu.
     */
    private final TethysScrollMenu<PortfolioBucket, I> thePortfolioMenu;

    /**
     * Security menu.
     */
    private final TethysScrollMenu<SecurityBucket, I> theSecurityMenu;

    /**
     * The active portfolio bucket list.
     */
    private PortfolioBucketList thePortfolios;

    /**
     * The state.
     */
    private SecurityState theState;

    /**
     * The savePoint.
     */
    private SecurityState theSavePoint;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected MoneyWiseSecurityAnalysisSelect(final TethysGuiFactory<N, I> pFactory) {
        /* Create the security button */
        theSecButton = pFactory.newScrollButton();

        /* Create the portfolio button */
        thePortButton = pFactory.newScrollButton();

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Create the labels */
        final TethysLabel<N, I> myPortLabel = pFactory.newLabel(NLS_PORTFOLIO + TethysLabel.STR_COLON);
        final TethysLabel<N, I> mySecLabel = pFactory.newLabel(NLS_SECURITY + TethysLabel.STR_COLON);

        /* Define the layout */
        thePanel = pFactory.newHBoxPane();
        thePanel.addSpacer();
        thePanel.addNode(myPortLabel);
        thePanel.addNode(thePortButton);
        thePanel.addStrut();
        thePanel.addNode(mySecLabel);
        thePanel.addNode(theSecButton);

        /* Create initial state */
        theState = new SecurityState();
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

    @Override
    public SecurityFilter getFilter() {
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
        theSavePoint = new SecurityState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new SecurityState(theSavePoint);

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
    public void setAnalysis(final Analysis pAnalysis) {
        /* Access buckets */
        thePortfolios = pAnalysis.getPortfolios();

        /* Obtain the current security */
        SecurityBucket mySecurity = theState.getSecurity();

        /* Switch to versions from the analysis */
        mySecurity = mySecurity != null
                                        ? thePortfolios.getMatchingSecurityHolding(mySecurity.getSecurityHolding())
                                        : thePortfolios.getDefaultSecurityHolding();
        final PortfolioBucket myPortfolio = mySecurity != null
                                                               ? thePortfolios.getMatchingPortfolio(mySecurity.getPortfolio())
                                                               : thePortfolios.getDefaultPortfolio();

        /* Set the security */
        theState.setTheSecurity(myPortfolio, mySecurity);
        theState.applyState();
    }

    @Override
    public void setFilter(final AnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof SecurityFilter) {
            /* Access filter */
            final SecurityFilter myFilter = (SecurityFilter) pFilter;

            /* Obtain the filter buckets */
            SecurityBucket mySecurity = myFilter.getBucket();

            /* Look for the equivalent buckets */
            mySecurity = thePortfolios.getMatchingSecurityHolding(mySecurity.getSecurityHolding());

            /* Determine the matching portfolio bucket */
            final PortfolioBucket myPortfolio = thePortfolios.getMatchingPortfolio(mySecurity.getPortfolio());

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
        TethysScrollMenuItem<PortfolioBucket> myActive = null;
        final PortfolioBucket myCurr = theState.getPortfolio();

        /* Loop through the available portfolio values */
        final Iterator<PortfolioBucket> myIterator = thePortfolios.iterator();
        while (myIterator.hasNext()) {
            final PortfolioBucket myBucket = myIterator.next();

            /* Create a new MenuItem and add it to the popUp */
            final TethysScrollMenuItem<PortfolioBucket> myItem = thePortfolioMenu.addItem(myBucket);

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
        final PortfolioBucket myPortfolio = theState.getPortfolio();
        final SecurityBucket myCurr = theState.getSecurity();
        TethysScrollMenuItem<SecurityBucket> myActive = null;

        /* Loop through the available security values */
        final Iterator<SecurityBucket> myIterator = myPortfolio.securityIterator();
        while (myIterator.hasNext()) {
            final SecurityBucket myBucket = myIterator.next();

            /* Create a new MenuItem and add it to the popUp */
            final TethysScrollMenuItem<SecurityBucket> myItem = theSecurityMenu.addItem(myBucket, myBucket.getSecurityName());

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
    private final class SecurityState {
        /**
         * The active Portfolio.
         */
        private PortfolioBucket thePortfolio;

        /**
         * The active SecurityBucket.
         */
        private SecurityBucket theSecurity;

        /**
         * The filter.
         */
        private SecurityFilter theFilter;

        /**
         * Constructor.
         */
        private SecurityState() {
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private SecurityState(final SecurityState pState) {
            /* Initialise state */
            theSecurity = pState.getSecurity();
            thePortfolio = pState.getPortfolio();
            theFilter = pState.getFilter();
        }

        /**
         * Obtain the Security Bucket.
         * @return the Security
         */
        private SecurityBucket getSecurity() {
            return theSecurity;
        }

        /**
         * Obtain the Portfolio.
         * @return the portfolio
         */
        private PortfolioBucket getPortfolio() {
            return thePortfolio;
        }

        /**
         * Obtain the Filter.
         * @return the filter
         */
        private SecurityFilter getFilter() {
            return theFilter;
        }

        /**
         * Set new Security.
         * @param pSecurity the Security
         * @return true/false did a change occur
         */
        private boolean setSecurity(final SecurityBucket pSecurity) {
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
        private void setTheSecurity(final PortfolioBucket pPortfolio,
                                    final SecurityBucket pSecurity) {
            /* Store the portfolio and security */
            thePortfolio = pPortfolio;
            theSecurity = pSecurity;
            theFilter = theSecurity != null
                                            ? new SecurityFilter(theSecurity)
                                            : null;
        }

        /**
         * Set new Portfolio.
         * @param pPortfolio the Portfolio
         * @return true/false did a change occur
         */
        private boolean setPortfolio(final PortfolioBucket pPortfolio) {
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
        private SecurityBucket getFirstSecurity(final PortfolioBucket pPortfolio) {
            /* Loop through the available security values */
            final Iterator<SecurityBucket> myIterator = pPortfolio.securityIterator();
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
