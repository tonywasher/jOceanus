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
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisResource;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisAccountBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket.TaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionAsset;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasis;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.TaxBasisFilter;
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
 * TaxBasisAnalysis Selection.
 * @param <N> the node type
 * @param <I> the Icon Type
 */
public class MoneyWiseTaxBasisAnalysisSelect<N, I>
        implements MoneyWiseAnalysisFilterSelection<N>, TethysEventProvider<PrometheusDataEvent> {
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
    private final TethysBoxPaneManager<N, I> thePanel;

    /**
     * The basis button.
     */
    private final TethysScrollButtonManager<TaxBasisBucket, N, I> theBasisButton;

    /**
     * The account button.
     */
    private final TethysScrollButtonManager<TaxBasisAccountBucket, N, I> theAccountButton;

    /**
     * Tax menu.
     */
    private final TethysScrollMenu<TaxBasisBucket, I> theTaxMenu;

    /**
     * Account menu.
     */
    private final TethysScrollMenu<TaxBasisAccountBucket, I> theAccountMenu;

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
    protected MoneyWiseTaxBasisAnalysisSelect(final TethysGuiFactory<N, I> pFactory) {
        /* Create the buttons */
        theBasisButton = pFactory.newScrollButton();
        theAccountButton = pFactory.newScrollButton();

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Create the labels */
        TethysLabel<N, I> myBasisLabel = pFactory.newLabel(NLS_BASIS + TethysLabel.STR_COLON);
        TethysLabel<N, I> myAccountLabel = pFactory.newLabel(NLS_ACCOUNT + TethysLabel.STR_COLON);

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
        myRegistrar.addEventListener(TethysUIEvent.PREPAREDIALOG, e -> buildBasisMenu());
        myRegistrar = theAccountButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewAccount());
        myRegistrar.addEventListener(TethysUIEvent.PREPAREDIALOG, e -> buildAccountMenu());
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
    public TaxBasisFilter getFilter() {
        TaxBasisAccountBucket myAccount = theState.getAccount();
        if (myAccount != null) {
            return new TaxBasisFilter(myAccount);
        }
        TaxBasisBucket myBasis = theState.getTaxBasis();
        return myBasis != null
                               ? new TaxBasisFilter(myBasis)
                               : null;
    }

    @Override
    public boolean isAvailable() {
        return (theTaxBases != null) && !theTaxBases.isEmpty();
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

        /* If we have a selected TaxBasis */
        if (myBasis != null) {
            /* Look for the equivalent bucket */
            myBasis = getMatchingBucket(myBasis);
        }

        /* If we do not have an active bucket and the list is non-empty */
        if ((myBasis == null) && (!theTaxBases.isEmpty())) {
            /* Use the first bucket */
            myBasis = theTaxBases.peekFirst();
        }

        /* Set the basis */
        theState.setTheTaxBasis(myBasis);
        theState.applyState();
    }

    @Override
    public void setFilter(final AnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof TaxBasisFilter) {
            /* Access filter */
            TaxBasisFilter myFilter = (TaxBasisFilter) pFilter;

            /* Obtain equivalent bucket */
            TaxBasisBucket myTaxBasis = getMatchingBucket(myFilter.getBucket());

            /* Set the taxBasis */
            theState.setTheTaxBasis(myTaxBasis);
            theState.applyState();
        }
    }

    /**
     * Obtain matching bucket.
     * @param pBucket the original bucket
     * @return the matching bucket
     */
    private TaxBasisBucket getMatchingBucket(final TaxBasisBucket pBucket) {
        /* Look up the matching TaxBasisBucket */
        TaxBasis myBasis = pBucket.getTaxBasis();
        TaxBasisBucket myBucket = theTaxBases.findItemById(myBasis.getOrderedId());

        /* If there is no such bucket in the analysis */
        if (myBucket == null) {
            /* Allocate an orphan bucket */
            myBucket = theTaxBases.getOrphanBucket(myBasis);
        }

        /* If we are trying to match an AccountBucket */
        if (pBucket instanceof TaxBasisAccountBucket) {
            /* Look up the asset bucket */
            TransactionAsset myAsset = ((TaxBasisAccountBucket) pBucket).getAccount();
            TaxBasisAccountBucket myAccountBucket = myBucket.findAccountBucket(myAsset);

            /* If there is no such bucket in the analysis */
            if (myAccountBucket == null) {
                /* Allocate an orphan bucket */
                myAccountBucket = myBucket.getOrphanAccountBucket(myAsset);
            }

            /* Set bucket as the account bucket */
            myBucket = myAccountBucket;
        }

        /* return the bucket */
        return myBucket;
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
        TaxBasisBucket myCurr = theState.getTaxBasis();

        /* Loop through the available basis values */
        Iterator<TaxBasisBucket> myIterator = theTaxBases.iterator();
        while (myIterator.hasNext()) {
            TaxBasisBucket myBucket = myIterator.next();

            /* Create a new MenuItem and add it to the popUp */
            TethysScrollMenuItem<TaxBasisBucket> myItem = theTaxMenu.addItem(myBucket);

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
        TaxBasisBucket myBasis = theState.getTaxBasis();
        TaxBasisAccountBucket myCurr = theState.getAccount();

        /* Add the all item menu */
        TethysScrollMenuItem<TaxBasisAccountBucket> myActive = theAccountMenu.addItem(null, NLS_ALL);

        /* Loop through the available account values */
        Iterator<TaxBasisAccountBucket> myIterator = myBasis.accountIterator();
        while (myIterator.hasNext()) {
            TaxBasisAccountBucket myBucket = myIterator.next();

            /* Create a new MenuItem and add it to the popUp */
            TethysScrollMenuItem<TaxBasisAccountBucket> myItem = theAccountMenu.addItem(myBucket, myBucket.getSimpleName());

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
         * Constructor.
         */
        private TaxBasisState() {
            /* Initialise the basis */
            theBasis = null;
            theAccount = null;
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private TaxBasisState(final TaxBasisState pState) {
            /* Initialise state */
            theBasis = pState.getTaxBasis();
            theAccount = pState.getAccount();
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
         * Set new TaxBasis.
         * @param pTaxBasis the TaxBasis
         * @return true/false did a change occur
         */
        private boolean setTaxBasis(final TaxBasisBucket pTaxBasis) {
            /* Adjust the selected taxBasis */
            if (!MetisDifference.isEqual(pTaxBasis, theBasis)) {
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
            } else if (pTaxBasis == null) {
                theAccount = null;
            } else {
                theBasis = pTaxBasis;
                theAccount = null;
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
