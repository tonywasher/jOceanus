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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.newanalysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.newanalysis.PortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.newanalysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.newanalysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.views.NewAnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.NewAnalysisFilter.SecurityFilter;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.ArrowIcon;
import net.sourceforge.joceanus.jtethys.swing.JScrollPopupMenu;

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
    private final JButton theSecButton;

    /**
     * The portfolio button.
     */
    private final JButton thePortButton;

    @Override
    public SecurityFilter getFilter() {
        return new SecurityFilter(theState.getSecurity());
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
        theSecButton = new JButton(ArrowIcon.DOWN);
        theSecButton.setVerticalTextPosition(AbstractButton.CENTER);
        theSecButton.setHorizontalTextPosition(AbstractButton.LEFT);

        /* Create the portfolio button */
        thePortButton = new JButton(ArrowIcon.DOWN);
        thePortButton.setVerticalTextPosition(AbstractButton.CENTER);
        thePortButton.setHorizontalTextPosition(AbstractButton.LEFT);

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
        theSecButton.addActionListener(myListener);
        thePortButton.addActionListener(myListener);
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
            mySecurity = getFirstSecurity(myPortBucket);
        }

        /* Set the security */
        theState.setSecurity(mySecurity);
        theState.applyState();
    }

    @Override
    public void setFilter(final NewAnalysisFilter<?> pFilter) {
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
        while (myIterator.hasNext()) {
            SecurityBucket myBucket = myIterator.next();

            /* Return the bucket */
            return myBucket;
        }

        /* No such security */
        return null;
    }

    /**
     * Listener class.
     */
    private final class SecurityListener
            implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent evt) {
            /* Access source of the event */
            Object o = evt.getSource();

            /* Handle buttons */
            if (thePortButton.equals(o)) {
                showPortfolioMenu();
            } else if (theSecButton.equals(o)) {
                showSecurityMenu();
            }
        }

        /**
         * Show Portfolio menu.
         */
        private void showPortfolioMenu() {
            /* Create a new popUp menu */
            JScrollPopupMenu myPopUp = new JScrollPopupMenu();

            /* Access current portfolio */
            PortfolioBucket myPortfolio = theState.getPortfolio();

            /* Record active item */
            JMenuItem myActive = null;

            /* Loop through the available portfolio values */
            Iterator<PortfolioBucket> myIterator = thePortfolios.iterator();
            while (myIterator.hasNext()) {
                PortfolioBucket myBucket = myIterator.next();

                /* Create a new JMenuItem and add it to the popUp */
                PortfolioAction myAction = new PortfolioAction(myBucket);
                JMenuItem myItem = new JMenuItem(myAction);
                myPopUp.addMenuItem(myItem);

                /* If this is the active portfolio */
                if (myPortfolio.equals(myBucket)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            myPopUp.showItem(myActive);

            /* Show the Portfolio menu in the correct place */
            Rectangle myLoc = thePortButton.getBounds();
            myPopUp.show(thePortButton, 0, myLoc.height);
        }

        /**
         * Show Security menu.
         */
        private void showSecurityMenu() {
            /* Create a new popUp menu */
            JScrollPopupMenu myPopUp = new JScrollPopupMenu();

            /* Access current portfolio and security */
            PortfolioBucket myPortfolio = theState.getPortfolio();
            SecurityBucket mySecurity = theState.getSecurity();

            /* Record active item */
            JMenuItem myActive = null;

            /* Loop through the available security values */
            Iterator<SecurityBucket> myIterator = myPortfolio.securityIterator();
            while (myIterator.hasNext()) {
                SecurityBucket myBucket = myIterator.next();

                /* Ignore if not the correct portfolio */
                if (!Difference.isEqual(myPortfolio, myBucket.getPortfolio())) {
                    continue;
                }

                /* Create a new JMenuItem and add it to the popUp */
                SecurityAction myAction = new SecurityAction(myBucket);
                JMenuItem myItem = new JMenuItem(myAction);
                myPopUp.addMenuItem(myItem);

                /* If this is the active security */
                if (mySecurity.equals(myBucket)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            myPopUp.showItem(myActive);

            /* Show the Security menu in the correct place */
            Rectangle myLoc = theSecButton.getBounds();
            myPopUp.show(theSecButton, 0, myLoc.height);
        }
    }

    /**
     * Portfolio action class.
     */
    private final class PortfolioAction
            extends AbstractAction {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 5893139520863655067L;

        /**
         * Portfolio.
         */
        private final PortfolioBucket thePortfolio;

        /**
         * Constructor.
         * @param pPortfolio the portfolio
         */
        private PortfolioAction(final PortfolioBucket pPortfolio) {
            super(pPortfolio.getName());
            thePortfolio = pPortfolio;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* Select the new portfolio */
            if (theState.setPortfolio(thePortfolio)) {
                theState.applyState();
                fireStateChanged();
            }
        }
    }

    /**
     * Security action class.
     */
    private final class SecurityAction
            extends AbstractAction {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -6698889706378932057L;

        /**
         * Security.
         */
        private final SecurityBucket theSecurity;

        /**
         * Constructor.
         * @param pSecurity the security bucket
         */
        private SecurityAction(final SecurityBucket pSecurity) {
            super(pSecurity.getName());
            theSecurity = pSecurity;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* Select the new security */
            if (theState.setSecurity(theSecurity)) {
                theState.applyState();
                fireStateChanged();
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
            theSecButton.setText((theSecurity == null)
                                                      ? null
                                                      : theSecurity.getName());
            thePortButton.setText((thePortfolio == null)
                                                        ? null
                                                        : thePortfolio.getName());
        }
    }
}
