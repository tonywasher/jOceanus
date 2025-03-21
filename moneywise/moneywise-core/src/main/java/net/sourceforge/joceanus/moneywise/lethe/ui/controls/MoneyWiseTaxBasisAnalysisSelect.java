/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.lethe.ui.controls;

import java.util.Iterator;

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisDataResource;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisTaxBasisAccountBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisTaxBasisBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisTaxBasisBucket.MoneyWiseAnalysisTaxBasisBucketList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisTaxBasisFilter;
import net.sourceforge.joceanus.prometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIConstant;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.tethys.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.tethys.api.control.TethysUILabel;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBoxPaneManager;

/**
 * TaxBasisAnalysis Selection.
 */
public class MoneyWiseTaxBasisAnalysisSelect
        implements MoneyWiseAnalysisFilterSelection, OceanusEventProvider<PrometheusDataEvent> {
    /**
     * Text for TaxBasis Label.
     */
    private static final String NLS_BASIS = MoneyWiseStaticDataType.TAXBASIS.getItemName();

    /**
     * Text for Account Label.
     */
    private static final String NLS_ACCOUNT = MoneyWiseAnalysisDataResource.BUCKET_ACCOUNT.getValue();

    /**
     * Text for All Item.
     */
    private static final String NLS_ALL = "All";

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * The basis button.
     */
    private final TethysUIScrollButtonManager<MoneyWiseAnalysisTaxBasisBucket> theBasisButton;

    /**
     * The account button.
     */
    private final TethysUIScrollButtonManager<MoneyWiseAnalysisTaxBasisAccountBucket> theAccountButton;

    /**
     * Tax menu.
     */
    private final TethysUIScrollMenu<MoneyWiseAnalysisTaxBasisBucket> theTaxMenu;

    /**
     * Account menu.
     */
    private final TethysUIScrollMenu<MoneyWiseAnalysisTaxBasisAccountBucket> theAccountMenu;

    /**
     * The active tax basis bucket list.
     */
    private MoneyWiseAnalysisTaxBasisBucketList theTaxBases;

    /**
     * The state.
     */
    private MoneyWiseTaxBasisState theState;

    /**
     * The savePoint.
     */
    private MoneyWiseTaxBasisState theSavePoint;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected MoneyWiseTaxBasisAnalysisSelect(final TethysUIFactory<?> pFactory) {
        /* Create the buttons */
        final TethysUIButtonFactory<?> myButtons = pFactory.buttonFactory();
        theBasisButton = myButtons.newScrollButton(MoneyWiseAnalysisTaxBasisBucket.class);
        theAccountButton = myButtons.newScrollButton(MoneyWiseAnalysisTaxBasisAccountBucket.class);

        /* Create Event Manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the labels */
        final TethysUIControlFactory myControls = pFactory.controlFactory();
        final TethysUILabel myBasisLabel = myControls.newLabel(NLS_BASIS + TethysUIConstant.STR_COLON);
        final TethysUILabel myAccountLabel = myControls.newLabel(NLS_ACCOUNT + TethysUIConstant.STR_COLON);

        /* Define the layout */
        thePanel = pFactory.paneFactory().newHBoxPane();
        thePanel.addSpacer();
        thePanel.addNode(myBasisLabel);
        thePanel.addNode(theBasisButton);
        thePanel.addStrut();
        thePanel.addNode(myAccountLabel);
        thePanel.addNode(theAccountButton);

        /* Create initial state */
        theState = new MoneyWiseTaxBasisState();
        theState.applyState();

        /* Access the menus */
        theTaxMenu = theBasisButton.getMenu();
        theAccountMenu = theAccountButton.getMenu();

        /* Create the listener */
        OceanusEventRegistrar<TethysUIEvent> myRegistrar = theBasisButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewBasis());
        theBasisButton.setMenuConfigurator(e -> buildBasisMenu());
        myRegistrar = theAccountButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewAccount());
        theAccountButton.setMenuConfigurator(e -> buildAccountMenu());
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
    public MoneyWiseAnalysisTaxBasisFilter getFilter() {
        return theState.getFilter();
    }

    @Override
    public boolean isAvailable() {
        return theTaxBases != null
                && !theTaxBases.isEmpty();
    }

    /**
     * Create SavePoint.
     */
    public void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new MoneyWiseTaxBasisState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new MoneyWiseTaxBasisState(theSavePoint);

        /* Apply the state */
        theState.applyState();
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* Pass call on to basis button */
        theBasisButton.setEnabled(bEnabled && isAvailable());
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
        theTaxBases = pAnalysis.getTaxBasis();

        /* Obtain the current basis */
        MoneyWiseAnalysisTaxBasisBucket myBasis = theState.getTaxBasis();

        /* Switch to versions from the analysis */
        myBasis = myBasis != null
                ? theTaxBases.getMatchingBasis(myBasis)
                : theTaxBases.getDefaultBasis();

        /* Set the basis */
        theState.setTheTaxBasis(myBasis);
        theState.setDateRange(pAnalysis.getDateRange());
        theState.applyState();
    }

    @Override
    public void setFilter(final MoneyWiseAnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof MoneyWiseAnalysisTaxBasisFilter) {
            /* Access filter */
            final MoneyWiseAnalysisTaxBasisFilter myFilter = (MoneyWiseAnalysisTaxBasisFilter) pFilter;

            /* Obtain the filter bucket */
            MoneyWiseAnalysisTaxBasisBucket myTaxBasis = myFilter.getBucket();

            /* Obtain equivalent bucket */
            myTaxBasis = theTaxBases.getMatchingBasis(myTaxBasis);

            /* Set the taxBasis */
            theState.setTheTaxBasis(myTaxBasis);
            theState.setDateRange(myFilter.getDateRange());
            theState.applyState();
        }
    }

    /**
     * Handle new Basis.
     */
    private void handleNewBasis() {
        /* Select the new taxBasis */
        if (theState.setTaxBasis(theBasisButton.getValue())) {
            theState.applyState();
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * Handle new Account.
     */
    private void handleNewAccount() {
        /* Select the new account */
        if (theState.setTaxBasis(theAccountButton.getValue())) {
            theState.applyState();
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * Build Basis menu.
     */
    private void buildBasisMenu() {
        /* Reset the popUp menu */
        theTaxMenu.removeAllItems();

        /* Record active item */
        TethysUIScrollItem<MoneyWiseAnalysisTaxBasisBucket> myActive = null;
        final MoneyWiseAnalysisTaxBasisBucket myCurr = theState.getTaxBasis();

        /* Loop through the available basis values */
        final Iterator<MoneyWiseAnalysisTaxBasisBucket> myIterator = theTaxBases.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisTaxBasisBucket myBucket = myIterator.next();

            /* Create a new MenuItem and add it to the popUp */
            final TethysUIScrollItem<MoneyWiseAnalysisTaxBasisBucket> myItem = theTaxMenu.addItem(myBucket);

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
     * Build Account menu.
     */
    private void buildAccountMenu() {
        /* Reset the popUp menu */
        theAccountMenu.removeAllItems();

        /* Record active item */
        final MoneyWiseAnalysisTaxBasisBucket myBasis = theState.getTaxBasis();
        final MoneyWiseAnalysisTaxBasisAccountBucket myCurr = theState.getAccount();

        /* Add the all item menu */
        TethysUIScrollItem<MoneyWiseAnalysisTaxBasisAccountBucket> myActive = theAccountMenu.addItem(null, NLS_ALL);

        /* Loop through the available account values */
        final Iterator<MoneyWiseAnalysisTaxBasisAccountBucket> myIterator = myBasis.accountIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisTaxBasisAccountBucket myBucket = myIterator.next();

            /* Create a new MenuItem and add it to the popUp */
            final TethysUIScrollItem<MoneyWiseAnalysisTaxBasisAccountBucket> myItem = theAccountMenu.addItem(myBucket, myBucket.getSimpleName());

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
    private final class MoneyWiseTaxBasisState {
        /**
         * The active TaxBasisBucket.
         */
        private MoneyWiseAnalysisTaxBasisBucket theBasis;

        /**
         * The account TaxBasisBucket.
         */
        private MoneyWiseAnalysisTaxBasisAccountBucket theAccount;

        /**
         * The dateRange.
         */
        private OceanusDateRange theDateRange;

        /**
         * The active filter.
         */
        private MoneyWiseAnalysisTaxBasisFilter theFilter;

        /**
         * Constructor.
         */
        private MoneyWiseTaxBasisState() {
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private MoneyWiseTaxBasisState(final MoneyWiseTaxBasisState pState) {
            /* Initialise state */
            theBasis = pState.getTaxBasis();
            theAccount = pState.getAccount();
            theDateRange = pState.getDateRange();
            theFilter = pState.getFilter();
        }

        /**
         * Obtain the TaxBasis Bucket.
         * @return the Basis
         */
        private MoneyWiseAnalysisTaxBasisBucket getTaxBasis() {
            return theBasis;
        }

        /**
         * Obtain the Account Bucket.
         * @return the Account
         */
        private MoneyWiseAnalysisTaxBasisAccountBucket getAccount() {
            return theAccount;
        }

        /**
         * Obtain the dateRange.
         * @return the dateRange
         */
        private OceanusDateRange getDateRange() {
            return theDateRange;
        }

        /**
         * Obtain the Filter.
         * @return the Filter
         */
        private MoneyWiseAnalysisTaxBasisFilter getFilter() {
            return theFilter;
        }

        /**
         * Set new TaxBasis.
         * @param pTaxBasis the TaxBasis
         * @return true/false did a change occur
         */
        private boolean setTaxBasis(final MoneyWiseAnalysisTaxBasisBucket pTaxBasis) {
            /* Adjust the selected taxBasis */
            if (!MetisDataDifference.isEqual(pTaxBasis, theBasis)) {
                setTheTaxBasis(pTaxBasis);
                return true;
            }
            return false;
        }

        /**
         * Set the TaxBasis.
         * @param pTaxBasis the TaxBasis
         */
        private void setTheTaxBasis(final MoneyWiseAnalysisTaxBasisBucket pTaxBasis) {
            /* Adjust the selected taxBasis */
            if (pTaxBasis instanceof MoneyWiseAnalysisTaxBasisAccountBucket) {
                theAccount = (MoneyWiseAnalysisTaxBasisAccountBucket) pTaxBasis;
                theBasis = theAccount.getParent();
                theFilter = new MoneyWiseAnalysisTaxBasisFilter(theAccount);
                theFilter.setDateRange(theDateRange);
            } else {
                theAccount = null;
                theBasis = pTaxBasis;
                if (theBasis != null) {
                    theFilter = new MoneyWiseAnalysisTaxBasisFilter(theBasis);
                    theFilter.setDateRange(theDateRange);
                } else {
                    theFilter = null;
                }
            }
        }

        /**
         * Set the dateRange.
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
            theBasisButton.setValue(theBasis);
            if (theAccount == null) {
                theAccountButton.setValue(null, NLS_ALL);
            } else {
                theAccountButton.setValue(theAccount, theAccount.getSimpleName());
            }
            theAccountButton.setEnabled((theBasis != null) && theBasis.hasAccounts());
        }
    }
}
