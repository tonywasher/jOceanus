/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.ui.controls;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.AnalysisResource;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TaxBasisAccountBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TaxBasisBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TaxBasisBucket.TaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter.TaxBasisFilter;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * TaxBasisAnalysis Selection.
 */
public class MoneyWiseTaxBasisAnalysisSelect
        implements MoneyWiseAnalysisFilterSelection, TethysEventProvider<PrometheusDataEvent> {
    /**
     * Text for TaxBasis Label.
     */
    private static final String NLS_BASIS = MoneyWiseDataType.TAXBASIS.getItemName();

    /**
     * Text for Account Label.
     */
    private static final String NLS_ACCOUNT = AnalysisResource.BUCKET_ACCOUNT.getValue();

    /**
     * Text for All Item.
     */
    private static final String NLS_ALL = "All";

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final TethysBoxPaneManager thePanel;

    /**
     * The basis button.
     */
    private final TethysScrollButtonManager<TaxBasisBucket> theBasisButton;

    /**
     * The account button.
     */
    private final TethysScrollButtonManager<TaxBasisAccountBucket> theAccountButton;

    /**
     * Tax menu.
     */
    private final TethysScrollMenu<TaxBasisBucket> theTaxMenu;

    /**
     * Account menu.
     */
    private final TethysScrollMenu<TaxBasisAccountBucket> theAccountMenu;

    /**
     * The active tax basis bucket list.
     */
    private TaxBasisBucketList theTaxBases;

    /**
     * The state.
     */
    private TaxBasisState theState;

    /**
     * The savePoint.
     */
    private TaxBasisState theSavePoint;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected MoneyWiseTaxBasisAnalysisSelect(final TethysGuiFactory pFactory) {
        /* Create the buttons */
        theBasisButton = pFactory.newScrollButton();
        theAccountButton = pFactory.newScrollButton();

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Create the labels */
        final TethysLabel myBasisLabel = pFactory.newLabel(NLS_BASIS + TethysLabel.STR_COLON);
        final TethysLabel myAccountLabel = pFactory.newLabel(NLS_ACCOUNT + TethysLabel.STR_COLON);

        /* Define the layout */
        thePanel = pFactory.newHBoxPane();
        thePanel.addSpacer();
        thePanel.addNode(myBasisLabel);
        thePanel.addNode(theBasisButton);
        thePanel.addStrut();
        thePanel.addNode(myAccountLabel);
        thePanel.addNode(theAccountButton);

        /* Create initial state */
        theState = new TaxBasisState();
        theState.applyState();

        /* Access the menus */
        theTaxMenu = theBasisButton.getMenu();
        theAccountMenu = theAccountButton.getMenu();

        /* Create the listener */
        TethysEventRegistrar<TethysUIEvent> myRegistrar = theBasisButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewBasis());
        theBasisButton.setMenuConfigurator(e -> buildBasisMenu());
        myRegistrar = theAccountButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewAccount());
        theAccountButton.setMenuConfigurator(e -> buildAccountMenu());
    }

    @Override
    public Integer getId() {
        return thePanel.getId();
    }

    @Override
    public TethysNode getNode() {
        return thePanel.getNode();
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public TaxBasisFilter getFilter() {
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
        theSavePoint = new TaxBasisState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new TaxBasisState(theSavePoint);

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
    public void setAnalysis(final Analysis pAnalysis) {
        /* Access buckets */
        theTaxBases = pAnalysis.getTaxBasis();

        /* Obtain the current basis */
        TaxBasisBucket myBasis = theState.getTaxBasis();

        /* Switch to versions from the analysis */
        myBasis = myBasis != null
                                  ? theTaxBases.getMatchingBasis(myBasis)
                                  : theTaxBases.getDefaultBasis();

        /* Set the basis */
        theState.setTheTaxBasis(myBasis);
        theState.applyState();
    }

    @Override
    public void setFilter(final AnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof TaxBasisFilter) {
            /* Access filter */
            final TaxBasisFilter myFilter = (TaxBasisFilter) pFilter;

            /* Obtain the filter bucket */
            TaxBasisBucket myTaxBasis = myFilter.getBucket();

            /* Obtain equivalent bucket */
            myTaxBasis = theTaxBases.getMatchingBasis(myTaxBasis);

            /* Set the taxBasis */
            theState.setTheTaxBasis(myTaxBasis);
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
        TethysScrollMenuItem<TaxBasisBucket> myActive = null;
        final TaxBasisBucket myCurr = theState.getTaxBasis();

        /* Loop through the available basis values */
        final Iterator<TaxBasisBucket> myIterator = theTaxBases.iterator();
        while (myIterator.hasNext()) {
            final TaxBasisBucket myBucket = myIterator.next();

            /* Create a new MenuItem and add it to the popUp */
            final TethysScrollMenuItem<TaxBasisBucket> myItem = theTaxMenu.addItem(myBucket);

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
        final TaxBasisBucket myBasis = theState.getTaxBasis();
        final TaxBasisAccountBucket myCurr = theState.getAccount();

        /* Add the all item menu */
        TethysScrollMenuItem<TaxBasisAccountBucket> myActive = theAccountMenu.addItem(null, NLS_ALL);

        /* Loop through the available account values */
        final Iterator<TaxBasisAccountBucket> myIterator = myBasis.accountIterator();
        while (myIterator.hasNext()) {
            final TaxBasisAccountBucket myBucket = myIterator.next();

            /* Create a new MenuItem and add it to the popUp */
            final TethysScrollMenuItem<TaxBasisAccountBucket> myItem = theAccountMenu.addItem(myBucket, myBucket.getSimpleName());

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
    private final class TaxBasisState {
        /**
         * The active TaxBasisBucket.
         */
        private TaxBasisBucket theBasis;

        /**
         * The account TaxBasisBucket.
         */
        private TaxBasisAccountBucket theAccount;

        /**
         * The active filter.
         */
        private TaxBasisFilter theFilter;

        /**
         * Constructor.
         */
        private TaxBasisState() {
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private TaxBasisState(final TaxBasisState pState) {
            /* Initialise state */
            theBasis = pState.getTaxBasis();
            theAccount = pState.getAccount();
            theFilter = pState.getFilter();
        }

        /**
         * Obtain the TaxBasis Bucket.
         * @return the Basis
         */
        private TaxBasisBucket getTaxBasis() {
            return theBasis;
        }

        /**
         * Obtain the Account Bucket.
         * @return the Account
         */
        private TaxBasisAccountBucket getAccount() {
            return theAccount;
        }

        /**
         * Obtain the Filter.
         * @return the Filter
         */
        private TaxBasisFilter getFilter() {
            return theFilter;
        }

        /**
         * Set new TaxBasis.
         * @param pTaxBasis the TaxBasis
         * @return true/false did a change occur
         */
        private boolean setTaxBasis(final TaxBasisBucket pTaxBasis) {
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
        private void setTheTaxBasis(final TaxBasisBucket pTaxBasis) {
            /* Adjust the selected taxBasis */
            if (pTaxBasis instanceof TaxBasisAccountBucket) {
                theAccount = (TaxBasisAccountBucket) pTaxBasis;
                theBasis = theAccount.getParent();
                theFilter = new TaxBasisFilter(theAccount);
            } else {
                theAccount = null;
                theBasis = pTaxBasis;
                theFilter = theBasis != null
                                             ? new TaxBasisFilter(theBasis)
                                             : null;
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
