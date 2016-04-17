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

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.PortfolioCashFilter;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
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
 * Portfolio Analysis Selection.
 * @param <N> the node type
 * @param <I> the Icon Type
 */
public class MoneyWisePortfolioAnalysisSelect<N, I>
        implements MoneyWiseAnalysisFilterSelection<N>, TethysEventProvider<PrometheusDataEvent> {
    /**
     * Text for Portfolio Label.
     */
    private static final String NLS_PORTFOLIO = MoneyWiseDataType.PORTFOLIO.getItemName();

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final TethysBoxPaneManager<N, I> thePanel;

    /**
     * The portfolio button.
     */
    private final TethysScrollButtonManager<PortfolioBucket, N, I> thePortButton;

    /**
     * Portfolio menu.
     */
    private final TethysScrollMenu<PortfolioBucket, I> thePortfolioMenu;

    /**
     * The active portfolio bucket list.
     */
    private PortfolioBucketList thePortfolios;

    /**
     * The state.
     */
    private PortfolioState theState;

    /**
     * The savePoint.
     */
    private PortfolioState theSavePoint;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected MoneyWisePortfolioAnalysisSelect(final TethysGuiFactory<N, I> pFactory) {
        /* Create the portfolio button */
        thePortButton = pFactory.newScrollButton();

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Create the labels */
        TethysLabel<N, I> myPortLabel = pFactory.newLabel(NLS_PORTFOLIO + TethysLabel.STR_COLON);

        /* Define the layout */
        thePanel = pFactory.newHBoxPane();
        thePanel.addSpacer();
        thePanel.addNode(myPortLabel);
        thePanel.addNode(thePortButton);

        /* Create initial state */
        theState = new PortfolioState();
        theState.applyState();

        /* Access the menus */
        thePortfolioMenu = thePortButton.getMenu();

        /* Create the listeners */
        TethysEventRegistrar<TethysUIEvent> myRegistrar = thePortButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewPortfolio());
        myRegistrar.addEventListener(TethysUIEvent.PREPAREDIALOG, e -> buildPortfolioMenu());
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
            if (!MetisDifference.isEqual(pPortfolio, thePortfolio)) {
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
