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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.data.Difference;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldElement;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.PortfolioCashFilter;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEventListener;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventManager;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar.JOceanusEventProvider;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistration.JOceanusChangeRegistration;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;

/**
 * Portfolio Analysis Selection.
 */
public class PortfolioAnalysisSelect
        extends JPanel
        implements AnalysisFilterSelection, JOceanusEventProvider {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 2005637648749797873L;

    /**
     * Text for Portfolio Label.
     */
    private static final String NLS_PORTFOLIO = MoneyWiseDataType.PORTFOLIO.getItemName();

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
    private transient PortfolioState theState;

    /**
     * The savePoint.
     */
    private transient PortfolioState theSavePoint;

    /**
     * The portfolio button.
     */
    private final JScrollButton<PortfolioBucket> thePortButton;

    /**
     * Constructor.
     */
    public PortfolioAnalysisSelect() {
        /* Create the portfolio button */
        thePortButton = new JScrollButton<PortfolioBucket>();

        /* Create Event Manager */
        theEventManager = new JOceanusEventManager();

        /* Create the labels */
        JLabel myPortLabel = new JLabel(NLS_PORTFOLIO + JFieldElement.STR_COLON);

        /* Define the layout */
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createHorizontalGlue());
        add(myPortLabel);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        add(thePortButton);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));

        /* Create initial state */
        theState = new PortfolioState();
        theState.applyState();

        /* Create the listener */
        new PortfolioListener();
    }

    @Override
    public JOceanusEventRegistrar getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public PortfolioCashFilter getFilter() {
        PortfolioBucket myPortfolio = theState.getPortfolio();
        return myPortfolio != null
                                  ? new PortfolioCashFilter(myPortfolio)
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
        theSavePoint = new PortfolioState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new PortfolioState(theSavePoint);

        /* Apply the state */
        theState.applyState();
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* Determine whether there are any portfolios to select */
        boolean portAvailable = bEnabled && isAvailable();

        /* Pass call on to buttons */
        thePortButton.setEnabled(portAvailable);
    }

    /**
     * Set analysis.
     * @param pAnalysis the analysis.
     */
    public void setAnalysis(final Analysis pAnalysis) {
        /* Access buckets */
        thePortfolios = pAnalysis.getPortfolios();

        /* Obtain the current portfolio */
        PortfolioBucket myPortfolio = theState.getPortfolio();

        /* If we have a selected Portfolio */
        if (myPortfolio != null) {
            /* Look for the equivalent bucket */
            myPortfolio = getMatchingBucket(myPortfolio);
        }

        /* If we do not have an active bucket */
        if (myPortfolio == null) {
            /* Access the first portfolio */
            myPortfolio = thePortfolios.peekFirst();
        }

        /* Set the portfolio */
        theState.setThePortfolio(myPortfolio);
        theState.applyState();
    }

    @Override
    public void setFilter(final AnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof PortfolioCashFilter) {
            /* Access filter */
            PortfolioCashFilter myFilter = (PortfolioCashFilter) pFilter;

            /* Obtain the filter bucket */
            PortfolioBucket myPortfolio = myFilter.getPortfolioBucket();

            /* Look for the equivalent bucket */
            myPortfolio = getMatchingBucket(myPortfolio);

            /* Set the portfolio */
            theState.setThePortfolio(myPortfolio);
            theState.applyState();
        }
    }

    /**
     * Obtain matching bucket.
     * @param pBucket the original bucket
     * @return the matching bucket
     */
    private PortfolioBucket getMatchingBucket(final PortfolioBucket pBucket) {
        /* Look up the matching PortfolioBucket */
        Portfolio myPortfolio = pBucket.getPortfolio();
        PortfolioBucket myBucket = thePortfolios.findItemById(myPortfolio.getOrderedId());

        /* If there is no such bucket in the analysis */
        if (myBucket == null) {
            /* Allocate an orphan bucket */
            myBucket = thePortfolios.getOrphanBucket(myPortfolio);
        }

        /* return the bucket */
        return myBucket;
    }

    /**
     * Listener class.
     */
    private final class PortfolioListener
            implements PropertyChangeListener, JOceanusChangeEventListener {
        /**
         * Portfolio menu builder.
         */
        private final JScrollMenuBuilder<PortfolioBucket> thePortfolioMenuBuilder;

        /**
         * PortfolioMenu Registration.
         */
        private final JOceanusChangeRegistration thePortfolioMenuReg;

        /**
         * Constructor.
         */
        private PortfolioListener() {
            /* Access builders */
            thePortfolioMenuBuilder = thePortButton.getMenuBuilder();
            thePortfolioMenuReg = thePortfolioMenuBuilder.getEventRegistrar().addChangeListener(this);

            /* Add swing listeners */
            thePortButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, this);
        }

        @Override
        public void processChangeEvent(final JOceanusChangeEvent pEvent) {
            /* If this is the PortfolioMenu */
            if (thePortfolioMenuReg.isRelevant(pEvent)) {
                buildPortfolioMenu();
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
            }
        }
    }

    /**
     * SavePoint values.
     */
    private final class PortfolioState {
        /**
         * The active Portfolio.
         */
        private PortfolioBucket thePortfolio;

        /**
         * Constructor.
         */
        private PortfolioState() {
            /* Initialise the portfolio */
            thePortfolio = null;
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private PortfolioState(final PortfolioState pState) {
            /* Initialise state */
            thePortfolio = pState.getPortfolio();
        }

        /**
         * Obtain the Portfolio.
         * @return the portfolio
         */
        private PortfolioBucket getPortfolio() {
            return thePortfolio;
        }

        /**
         * Set new Portfolio.
         * @param pPortfolio the Portfolio
         * @return true/false did a change occur
         */
        private boolean setPortfolio(final PortfolioBucket pPortfolio) {
            /* Adjust the selected portfolio */
            if (!Difference.isEqual(pPortfolio, thePortfolio)) {
                setThePortfolio(pPortfolio);
                return true;
            }
            return false;
        }

        /**
         * Set the Portfolio.
         * @param pPortfolio the Portfolio
         */
        private void setThePortfolio(final PortfolioBucket pPortfolio) {
            /* Set the selected portfolio */
            thePortfolio = pPortfolio;
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
