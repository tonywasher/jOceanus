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
package net.sourceforge.joceanus.jmoneywise.ui.controls;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.SecurityFilter;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;

/**
 * Security Analysis Selection.
 */
public class SecurityAnalysisSelect
        extends JEventPanel
        implements AnalysisFilterSelection {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -7059807216968295408L;

    /**
     * Text for Portfolio Label.
     */
    private static final String NLS_PORTFOLIO = MoneyWiseDataType.PORTFOLIO.getItemName();

    /**
     * Text for Security Label.
     */
    private static final String NLS_SECURITY = MoneyWiseDataType.SECURITY.getItemName();

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
    private final JScrollButton<SecurityBucket> theSecButton;

    /**
     * The portfolio button.
     */
    private final JScrollButton<PortfolioBucket> thePortButton;

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
     * Constructor.
     */
    public SecurityAnalysisSelect() {
        /* Create the security button */
        theSecButton = new JScrollButton<SecurityBucket>();

        /* Create the portfolio button */
        thePortButton = new JScrollButton<PortfolioBucket>();

        /* Create the labels */
        JLabel myPortLabel = new JLabel(NLS_PORTFOLIO);
        JLabel mySecLabel = new JLabel(NLS_SECURITY);

        /* Define the layout */
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createHorizontalGlue());
        add(myPortLabel);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        add(thePortButton);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE << 2, 0)));
        add(mySecLabel);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        add(theSecButton);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));

        /* Create initial state */
        theState = new SecurityState();
        theState.applyState();

        /* Create the listener */
        SecurityListener myListener = new SecurityListener();
        theSecButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, myListener);
        thePortButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, myListener);
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

    /**
     * Set analysis.
     * @param pAnalysis the analysis.
     */
    public void setAnalysis(final Analysis pAnalysis) {
        /* Access buckets */
        thePortfolios = pAnalysis.getPortfolios();

        /* Obtain the current security */
        SecurityBucket mySecurity = theState.getSecurity();

        /* If we have a selected Security */
        if (mySecurity != null) {
            /* Access the portfolio */
            Portfolio myPortfolio = mySecurity.getPortfolio();

            /* Look for the equivalent bucket */
            PortfolioBucket myPortBucket = thePortfolios.findItemById(myPortfolio.getOrderedId());
            mySecurity = (myPortBucket == null)
                                               ? null
                                               : myPortBucket.findSecurityBucket(mySecurity.getSecurity());
        }

        /* If we do not have an active bucket */
        if (mySecurity == null) {
            /* Check for a security in the same portfolio */
            PortfolioBucket myPortBucket = theState.getPortfolio();
            myPortBucket = (myPortBucket == null)
                                                 ? null
                                                 : thePortfolios.findItemById(myPortBucket.getOrderedId());

            /* If the portfolio no longer exists */
            if (myPortBucket == null) {
                /* Access the first portfolio */
                myPortBucket = thePortfolios.peekFirst();
            }

            /* Use the first security for portfolio */
            mySecurity = (myPortBucket == null)
                                               ? null
                                               : getFirstSecurity(myPortBucket);
        }

        /* Set the security */
        theState.setSecurity(mySecurity);
        theState.applyState();
    }

    @Override
    public void setFilter(final AnalysisFilter<?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof SecurityFilter) {
            /* Access filter */
            SecurityFilter myFilter = (SecurityFilter) pFilter;

            /* Obtain the filter bucket */
            SecurityBucket mySecurity = myFilter.getBucket();

            /* Access the portfolio */
            Portfolio myPortfolio = mySecurity.getPortfolio();

            /* Look for the equivalent bucket */
            PortfolioBucket myPortBucket = thePortfolios.findItemById(myPortfolio.getOrderedId());
            mySecurity = (myPortBucket == null)
                                               ? null
                                               : myPortBucket.findSecurityBucket(mySecurity.getSecurity());

            /* Set the security */
            theState.setSecurity(mySecurity);
            theState.applyState();
        }
    }

    /**
     * Obtain first security for portfolio.
     * @param pPortfolio the portfolio
     * @return the first security
     */
    private SecurityBucket getFirstSecurity(final PortfolioBucket pPortfolio) {
        /* Loop through the available security values */
        Iterator<SecurityBucket> myIterator = pPortfolio.securityIterator();
        return myIterator.hasNext()
                                   ? myIterator.next()
                                   : null;
    }

    /**
     * Listener class.
     */
    private final class SecurityListener
            implements PropertyChangeListener, ChangeListener {
        /**
         * Portfolio menu builder.
         */
        private final JScrollMenuBuilder<PortfolioBucket> thePortfolioMenuBuilder;

        /**
         * Security menu builder.
         */
        private final JScrollMenuBuilder<SecurityBucket> theSecurityMenuBuilder;

        /**
         * Constructor.
         */
        private SecurityListener() {
            /* Access builders */
            theSecurityMenuBuilder = theSecButton.getMenuBuilder();
            theSecurityMenuBuilder.addChangeListener(this);
            thePortfolioMenuBuilder = thePortButton.getMenuBuilder();
            thePortfolioMenuBuilder.addChangeListener(this);
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            /* Access source of the event */
            Object o = pEvent.getSource();

            /* Handle buttons */
            if (thePortfolioMenuBuilder.equals(o)) {
                buildPortfolioMenu();
            } else if (theSecurityMenuBuilder.equals(o)) {
                buildSecurityMenu();
            }
        }

        /**
         * Build Portfolio menu.
         */
        private void buildPortfolioMenu() {
            /* Reset the popUp menu */
            thePortfolioMenuBuilder.clearMenu();

            /* Record active item */
            JMenuItem myActive = null;
            PortfolioBucket myCurr = theState.getPortfolio();

            /* Loop through the available portfolio values */
            Iterator<PortfolioBucket> myIterator = thePortfolios.iterator();
            while (myIterator.hasNext()) {
                PortfolioBucket myBucket = myIterator.next();

                /* Create a new JMenuItem and add it to the popUp */
                JMenuItem myItem = thePortfolioMenuBuilder.addItem(myBucket);

                /* If this is the active bucket */
                if (myBucket.equals(myCurr)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            thePortfolioMenuBuilder.showItem(myActive);
        }

        /**
         * Build Security menu.
         */
        private void buildSecurityMenu() {
            /* Reset the popUp menu */
            theSecurityMenuBuilder.clearMenu();

            /* Access current portfolio */
            PortfolioBucket myPortfolio = theState.getPortfolio();
            SecurityBucket myCurr = theState.getSecurity();
            JMenuItem myActive = null;

            /* Loop through the available security values */
            Iterator<SecurityBucket> myIterator = myPortfolio.securityIterator();
            while (myIterator.hasNext()) {
                SecurityBucket myBucket = myIterator.next();

                /* Create a new JMenuItem and add it to the popUp */
                JMenuItem myItem = theSecurityMenuBuilder.addItem(myBucket);

                /* If this is the active bucket */
                if (myBucket.equals(myCurr)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            theSecurityMenuBuilder.showItem(myActive);
        }

        @Override
        public void propertyChange(final PropertyChangeEvent pEvent) {
            /* Access the source */
            Object o = pEvent.getSource();

            /* If this is the portfolio button */
            if (thePortButton.equals(o)) {
                /* Select the new portfolio */
                if (theState.setPortfolio(thePortButton.getValue())) {
                    theState.applyState();
                    fireStateChanged();
                }
            }

            /* If this is the security button */
            if (theSecButton.equals(o)) {
                /* Select the new security */
                if (theState.setSecurity(theSecButton.getValue())) {
                    theState.applyState();
                    fireStateChanged();
                }
            }
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
         * Set new Security.
         * @param pSecurity the Security
         * @return true/false did a change occur
         */
        private boolean setSecurity(final SecurityBucket pSecurity) {
            /* Adjust the selected security */
            if (!Difference.isEqual(pSecurity, theSecurity)) {
                /* Store the security */
                theSecurity = pSecurity;

                /* Access portfolio for security */
                thePortfolio = null;
                if (theSecurity != null) {
                    Portfolio myPortfolio = theSecurity.getPortfolio();
                    thePortfolio = thePortfolios.findItemById(myPortfolio.getId());
                }

                /* We have changed */
                return true;
            }
            return false;
        }

        /**
         * Set new Portfolio.
         * @param pPortfolio the Portfolio
         * @return true/false did a change occur
         */
        private boolean setPortfolio(final PortfolioBucket pPortfolio) {
            /* Adjust the selected portfolio */
            if (!Difference.isEqual(pPortfolio, thePortfolio)) {
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
