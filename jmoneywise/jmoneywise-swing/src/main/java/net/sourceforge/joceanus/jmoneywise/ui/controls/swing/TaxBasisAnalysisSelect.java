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
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisResource;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisAccountBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket.TaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionAsset;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasis;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.TaxBasisFilter;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysChangeEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysChangeEventListener;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistration.TethysChangeRegistration;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton.JScrollMenuBuilder;

/**
 * TaxBasisAnalysis Selection.
 */
public class TaxBasisAnalysisSelect
        extends JPanel
        implements AnalysisFilterSelection, TethysEventProvider {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 2653125674925955281L;

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
    private final transient TethysEventManager theEventManager;

    /**
     * The active tax basis bucket list.
     */
    private transient TaxBasisBucketList theTaxBases;

    /**
     * The state.
     */
    private transient TaxBasisState theState;

    /**
     * The savePoint.
     */
    private transient TaxBasisState theSavePoint;

    /**
     * The basis button.
     */
    private final JScrollButton<TaxBasisBucket> theBasisButton;

    /**
     * The account button.
     */
    private final JScrollButton<TaxBasisAccountBucket> theAccountButton;

    /**
     * Constructor.
     */
    public TaxBasisAnalysisSelect() {
        /* Create the buttons */
        theBasisButton = new JScrollButton<TaxBasisBucket>();
        theAccountButton = new JScrollButton<TaxBasisAccountBucket>();

        /* Create Event Manager */
        theEventManager = new TethysEventManager();

        /* Create the labels */
        JLabel myBasisLabel = new JLabel(NLS_BASIS + JFieldElement.STR_COLON);
        JLabel myAccountLabel = new JLabel(NLS_ACCOUNT + JFieldElement.STR_COLON);

        /* Define the layout */
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createHorizontalGlue());
        add(myBasisLabel);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        add(theBasisButton);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE << 2, 0)));
        add(myAccountLabel);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        add(theAccountButton);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));

        /* Create initial state */
        theState = new TaxBasisState();
        theState.applyState();

        /* Create the listener */
        new BasisListener();
    }

    @Override
    public TethysEventRegistrar getEventRegistrar() {
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
     * Listener class.
     */
    private final class BasisListener
            implements PropertyChangeListener, TethysChangeEventListener {
        /**
         * Tax menu builder.
         */
        private final JScrollMenuBuilder<TaxBasisBucket> theTaxMenuBuilder;

        /**
         * Account menu builder.
         */
        private final JScrollMenuBuilder<TaxBasisAccountBucket> theAccountMenuBuilder;

        /**
         * TaxBasisMenu Registration.
         */
        private final TethysChangeRegistration theBasisMenuReg;

        /**
         * AccountMenu Registration.
         */
        private final TethysChangeRegistration theAccountMenuReg;

        /**
         * Constructor.
         */
        private BasisListener() {
            /* Access builders */
            theTaxMenuBuilder = theBasisButton.getMenuBuilder();
            theAccountMenuBuilder = theAccountButton.getMenuBuilder();
            theBasisMenuReg = theTaxMenuBuilder.getEventRegistrar().addChangeListener(this);
            theAccountMenuReg = theAccountMenuBuilder.getEventRegistrar().addChangeListener(this);

            /* Add swing listeners */
            theBasisButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, this);
            theAccountButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, this);
        }

        @Override
        public void processChange(final TethysChangeEvent pEvent) {
            /* If this is the TaxBasisMenu */
            if (theBasisMenuReg.isRelevant(pEvent)) {
                buildBasisMenu();
            } else if (theAccountMenuReg.isRelevant(pEvent)) {
                buildAccountMenu();
            }
        }

        /**
         * Build Basis menu.
         */
        private void buildBasisMenu() {
            /* Reset the popUp menu */
            theTaxMenuBuilder.clearMenu();

            /* Record active item */
            JMenuItem myActive = null;
            TaxBasisBucket myCurr = theState.getTaxBasis();

            /* Loop through the available basis values */
            Iterator<TaxBasisBucket> myIterator = theTaxBases.iterator();
            while (myIterator.hasNext()) {
                TaxBasisBucket myBucket = myIterator.next();

                /* Create a new JMenuItem and add it to the popUp */
                JMenuItem myItem = theTaxMenuBuilder.addItem(myBucket);

                /* If this is the active bucket */
                if (myBucket.equals(myCurr)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            theTaxMenuBuilder.showItem(myActive);
        }

        /**
         * Build Account menu.
         */
        private void buildAccountMenu() {
            /* Reset the popUp menu */
            theAccountMenuBuilder.clearMenu();

            /* Record active item */
            TaxBasisBucket myBasis = theState.getTaxBasis();
            TaxBasisAccountBucket myCurr = theState.getAccount();

            /* Add the all item menu */
            JMenuItem myActive = theAccountMenuBuilder.addItem(null, NLS_ALL);

            /* Loop through the available account values */
            Iterator<TaxBasisAccountBucket> myIterator = myBasis.accountIterator();
            while (myIterator.hasNext()) {
                TaxBasisAccountBucket myBucket = myIterator.next();

                /* Create a new JMenuItem and add it to the popUp */
                JMenuItem myItem = theAccountMenuBuilder.addItem(myBucket, myBucket.getSimpleName());

                /* If this is the active bucket */
                if (myBucket.equals(myCurr)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            theAccountMenuBuilder.showItem(myActive);
        }

        @Override
        public void propertyChange(final PropertyChangeEvent pEvent) {
            /* Access the source */
            Object o = pEvent.getSource();

            /* If this is the tax button */
            if (theBasisButton.equals(o)) {
                /* Select the new basis */
                if (theState.setTaxBasis(theBasisButton.getValue())) {
                    theState.applyState();
                    theEventManager.fireStateChanged();
                }
            } else if (theAccountButton.equals(o)) {
                /* Select the new basis */
                if (theState.setTaxBasis(theAccountButton.getValue())) {
                    theState.applyState();
                    theEventManager.fireStateChanged();
                }
            }
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
            if (!Difference.isEqual(pTaxBasis, theBasis)) {
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
