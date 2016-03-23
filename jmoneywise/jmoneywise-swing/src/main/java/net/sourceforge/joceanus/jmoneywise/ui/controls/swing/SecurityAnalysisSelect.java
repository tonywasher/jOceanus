/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.ui.controls.swing;

import java.awt.Dimension;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldElement;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.SecurityFilter;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButton.TethysSwingScrollButtonManager;

/**
 * Security Analysis Selection.
 */
public class SecurityAnalysisSelect
        implements AnalysisFilterSelection<JComponent>, TethysEventProvider<PrometheusDataEvent> {
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
    private final JPanel thePanel;

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
     * The security button.
     */
    private final TethysSwingScrollButtonManager<SecurityBucket> theSecButton;

    /**
     * The portfolio button.
     */
    private final TethysSwingScrollButtonManager<PortfolioBucket> thePortButton;

    /**
     * Portfolio menu.
     */
    private final TethysScrollMenu<PortfolioBucket, ?> thePortfolioMenu;

    /**
     * Security menu.
     */
    private final TethysScrollMenu<SecurityBucket, ?> theSecurityMenu;

    /**
     * Constructor.
     */
    public SecurityAnalysisSelect() {
        /* Create the security button */
        theSecButton = new TethysSwingScrollButtonManager<>();

        /* Create the portfolio button */
        thePortButton = new TethysSwingScrollButtonManager<>();

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Create the labels */
        JLabel myPortLabel = new JLabel(NLS_PORTFOLIO + MetisFieldElement.STR_COLON);
        JLabel mySecLabel = new JLabel(NLS_SECURITY + MetisFieldElement.STR_COLON);

        /* Define the layout */
        thePanel = new JPanel();
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.X_AXIS));
        thePanel.add(Box.createHorizontalGlue());
        thePanel.add(myPortLabel);
        thePanel.add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        thePanel.add(thePortButton.getNode());
        thePanel.add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE << 2, 0)));
        thePanel.add(mySecLabel);
        thePanel.add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        thePanel.add(theSecButton.getNode());
        thePanel.add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));

        /* Create initial state */
        theState = new SecurityState();
        theState.applyState();

        /* Access the menus */
        thePortfolioMenu = thePortButton.getMenu();
        theSecurityMenu = theSecButton.getMenu();

        /* Create the listener */
        TethysEventRegistrar<TethysUIEvent> myRegistrar = thePortButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewPortfolio());
        myRegistrar.addEventListener(TethysUIEvent.PREPAREDIALOG, e -> buildPortfolioMenu());
        myRegistrar = theSecButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewSecurity());
        myRegistrar.addEventListener(TethysUIEvent.PREPAREDIALOG, e -> buildSecurityMenu());
    }

    @Override
    public JComponent getNode() {
        return thePanel;
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public SecurityFilter getFilter() {
        SecurityBucket mySecurity = theState.getSecurity();
        return mySecurity != null
                                  ? new SecurityFilter(mySecurity)
                                  : null;
    }

    @Override
    public boolean isAvailable() {
        return (thePortfolios != null) && !thePortfolios.isEmpty();
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
        boolean secAvailable = bEnabled && isAvailable();

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
        PortfolioBucket myPortfolio = theState.getPortfolio();
        SecurityBucket mySecurity = theState.getSecurity();

        /* If we have a selected Security */
        if (mySecurity != null) {
            /* Look for the equivalent buckets */
            myPortfolio = getMatchingPortfolioBucket(mySecurity.getPortfolio());
            mySecurity = getMatchingSecurityBucket(myPortfolio, mySecurity);
        }

        /* If we do not have an active bucket */
        if (mySecurity == null) {
            /* Check for a security in the same portfolio */
            myPortfolio = (myPortfolio == null)
                                                ? null
                                                : thePortfolios.findItemById(myPortfolio.getOrderedId());

            /* If the portfolio no longer exists */
            if (myPortfolio == null) {
                /* Access the first portfolio */
                myPortfolio = thePortfolios.peekFirst();
            }

            /* Use the first security for portfolio */
            mySecurity = (myPortfolio == null)
                                               ? null
                                               : getFirstSecurity(myPortfolio);
        }

        /* Set the security */
        theState.setSecurity(myPortfolio, mySecurity);
        theState.applyState();
    }

    @Override
    public void setFilter(final AnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof SecurityFilter) {
            /* Access filter */
            SecurityFilter myFilter = (SecurityFilter) pFilter;

            /* Obtain the filter bucket */
            SecurityBucket mySecurity = myFilter.getBucket();

            /* Look for the equivalent buckets */
            PortfolioBucket myPortfolio = getMatchingPortfolioBucket(mySecurity.getPortfolio());
            mySecurity = getMatchingSecurityBucket(myPortfolio, mySecurity);

            /* Set the security */
            theState.setSecurity(myPortfolio, mySecurity);
            theState.applyState();
        }
    }

    /**
     * Obtain first security for portfolio.
     * @param pPortfolio the portfolio
     * @return the first security
     */
    private static SecurityBucket getFirstSecurity(final PortfolioBucket pPortfolio) {
        /* Loop through the available security values */
        Iterator<SecurityBucket> myIterator = pPortfolio.securityIterator();
        return myIterator.hasNext()
                                    ? myIterator.next()
                                    : null;
    }

    /**
     * Obtain matching portfolio bucket.
     * @param pPortfolio the portfolio
     * @return the matching bucket
     */
    private PortfolioBucket getMatchingPortfolioBucket(final Portfolio pPortfolio) {
        /* Look up the matching PortfolioBucket */
        PortfolioBucket myBucket = thePortfolios.findItemById(pPortfolio.getOrderedId());

        /* If there is no such bucket in the analysis */
        if (myBucket == null) {
            /* Allocate an orphan bucket */
            myBucket = thePortfolios.getOrphanBucket(pPortfolio);
        }

        /* return the bucket */
        return myBucket;
    }

    /**
     * Obtain matching bucket.
     * @param pPortfolio the portfolio bucket
     * @param pBucket the original bucket
     * @return the matching bucket
     */
    private static SecurityBucket getMatchingSecurityBucket(final PortfolioBucket pPortfolio,
                                                            final SecurityBucket pBucket) {
        /* Look up the matching SecurityBucket */
        Security mySecurity = pBucket.getSecurity();
        SecurityBucket myBucket = pPortfolio.findSecurityBucket(mySecurity);

        /* If there is no such bucket in the analysis */
        if (myBucket == null) {
            /* Allocate an orphan bucket */
            myBucket = pPortfolio.getOrphanSecurityBucket(pBucket.getSecurityHolding());
        }

        /* return the bucket */
        return myBucket;
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
        PortfolioBucket myCurr = theState.getPortfolio();

        /* Loop through the available portfolio values */
        Iterator<PortfolioBucket> myIterator = thePortfolios.iterator();
        while (myIterator.hasNext()) {
            PortfolioBucket myBucket = myIterator.next();

            /* Create a new MenuItem and add it to the popUp */
            TethysScrollMenuItem<PortfolioBucket> myItem = thePortfolioMenu.addItem(myBucket);

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
        PortfolioBucket myPortfolio = theState.getPortfolio();
        SecurityBucket myCurr = theState.getSecurity();
        TethysScrollMenuItem<SecurityBucket> myActive = null;

        /* Loop through the available security values */
        Iterator<SecurityBucket> myIterator = myPortfolio.securityIterator();
        while (myIterator.hasNext()) {
            SecurityBucket myBucket = myIterator.next();

            /* Create a new MenuItem and add it to the popUp */
            TethysScrollMenuItem<SecurityBucket> myItem = theSecurityMenu.addItem(myBucket);

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
         * Constructor.
         */
        private SecurityState() {
            /* Initialise the security */
            theSecurity = null;
            thePortfolio = null;
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private SecurityState(final SecurityState pState) {
            /* Initialise state */
            theSecurity = pState.getSecurity();
            thePortfolio = pState.getPortfolio();
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
         * Set new Security.
         * @param pSecurity the Security
         * @return true/false did a change occur
         */
        private boolean setSecurity(final SecurityBucket pSecurity) {
            /* Adjust the selected security */
            if (!MetisDifference.isEqual(pSecurity, theSecurity)) {
                /* Store the security */
                theSecurity = pSecurity;
                return true;
            }
            return false;
        }

        /**
         * Set new Security.
         * @param pPortfolio the Portfolio
         * @param pSecurity the Security
         */
        private void setSecurity(final PortfolioBucket pPortfolio,
                                 final SecurityBucket pSecurity) {
            /* Store the portfolio and security */
            thePortfolio = pPortfolio;
            theSecurity = pSecurity;
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
                theSecurity = getFirstSecurity(thePortfolio);
                return true;
            }
            return false;
        }

        /**
         * Apply the State.
         */
        private void applyState() {
            /* Adjust the lock-down */
            setEnabled(true);
            theSecButton.setValue(theSecurity);
            thePortButton.setValue(thePortfolio);
        }
    }
}
