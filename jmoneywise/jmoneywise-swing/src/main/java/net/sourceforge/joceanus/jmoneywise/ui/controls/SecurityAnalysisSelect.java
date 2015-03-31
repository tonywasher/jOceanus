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
import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.data.Difference;
import net.sourceforge.joceanus.jmetis.field.JFieldElement;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.SecurityFilter;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEventListener;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventManager;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar.JOceanusEventProvider;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistration.JOceanusChangeRegistration;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;

/**
 * Security Analysis Selection.
 */
public class SecurityAnalysisSelect
        extends JPanel
        implements AnalysisFilterSelection, JOceanusEventProvider {
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
     * The Event Manager.
     */
    private final transient JOceanusEventManager theEventManager;

    /**
     * The active portfolio bucket list.
     */
    private transient PortfolioBucketList thePortfolios;

    /**
     * The state.
     */
    private transient SecurityState theState;

    /**
     * The savePoint.
     */
    private transient SecurityState theSavePoint;

    /**
     * The security button.
     */
    private final JScrollButton<SecurityBucket> theSecButton;

    /**
     * The portfolio button.
     */
    private final JScrollButton<PortfolioBucket> thePortButton;

    /**
     * Constructor.
     */
    public SecurityAnalysisSelect() {
        /* Create the security button */
        theSecButton = new JScrollButton<SecurityBucket>();

        /* Create the portfolio button */
        thePortButton = new JScrollButton<PortfolioBucket>();

        /* Create Event Manager */
        theEventManager = new JOceanusEventManager();

        /* Create the labels */
        JLabel myPortLabel = new JLabel(NLS_PORTFOLIO + JFieldElement.STR_COLON);
        JLabel mySecLabel = new JLabel(NLS_SECURITY + JFieldElement.STR_COLON);

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
        new SecurityListener();
    }

    @Override
    public JOceanusEventRegistrar getEventRegistrar() {
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
    public void setFilter(final AnalysisFilter<?, ?> pFilter) {
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
            implements PropertyChangeListener, JOceanusChangeEventListener {
        /**
         * Portfolio menu builder.
         */
        private final JScrollMenuBuilder<PortfolioBucket> thePortfolioMenuBuilder;

        /**
         * Security menu builder.
         */
        private final JScrollMenuBuilder<SecurityBucket> theSecurityMenuBuilder;

        /**
         * PortfolioMenu Registration.
         */
        private final JOceanusChangeRegistration thePortfolioMenuReg;

        /**
         * SecurityMenu Registration.
         */
        private final JOceanusChangeRegistration theSecurityMenuReg;

        /**
         * Constructor.
         */
        private SecurityListener() {
            /* Access builders */
            theSecurityMenuBuilder = theSecButton.getMenuBuilder();
            theSecurityMenuReg = theSecurityMenuBuilder.getEventRegistrar().addChangeListener(this);
            thePortfolioMenuBuilder = thePortButton.getMenuBuilder();
            thePortfolioMenuReg = thePortfolioMenuBuilder.getEventRegistrar().addChangeListener(this);

            /* Add swing listeners */
            theSecButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, this);
            thePortButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, this);
        }

        @Override
        public void processChangeEvent(final JOceanusChangeEvent pEvent) {
            /* If this is the PortfolioMenu */
            if (thePortfolioMenuReg.isRelevant(pEvent)) {
                buildPortfolioMenu();
            } else if (theSecurityMenuReg.isRelevant(pEvent)) {
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
                    theEventManager.fireStateChanged();
                }

                /* If this is the security button */
            } else if (theSecButton.equals(o)) {
                /* Select the new security */
                if (theState.setSecurity(theSecButton.getValue())) {
                    theState.applyState();
                    theEventManager.fireStateChanged();
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
