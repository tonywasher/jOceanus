/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.ui.controls;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.LoanBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.LoanBucket.LoanBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.LoanCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.LoanCategoryBucket.LoanCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter.LoanFilter;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIConstant;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUILabel;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollSubMenu;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBoxPaneManager;

/**
 * Loan Analysis Selection.
 */
public class MoneyWiseLoanAnalysisSelect
        implements MoneyWiseAnalysisFilterSelection, TethysEventProvider<PrometheusDataEvent> {
    /**
     * Text for Category Label.
     */
    private static final String NLS_CATEGORY = MoneyWiseDataType.LOANCATEGORY.getItemName();

    /**
     * Text for Loan Label.
     */
    private static final String NLS_LOAN = MoneyWiseDataType.LOAN.getItemName();

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * The loan button.
     */
    private final TethysUIScrollButtonManager<LoanBucket> theLoanButton;

    /**
     * The category button.
     */
    private final TethysUIScrollButtonManager<LoanCategory> theCatButton;

    /**
     * Category menu.
     */
    private final TethysUIScrollMenu<LoanCategory> theCategoryMenu;

    /**
     * Loan menu.
     */
    private final TethysUIScrollMenu<LoanBucket> theLoanMenu;

    /**
     * The active category bucket list.
     */
    private LoanCategoryBucketList theCategories;

    /**
     * The active loan bucket list.
     */
    private LoanBucketList theLoans;

    /**
     * The state.
     */
    private LoanState theState;

    /**
     * The savePoint.
     */
    private LoanState theSavePoint;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected MoneyWiseLoanAnalysisSelect(final TethysUIFactory<?> pFactory) {
        /* Create the loan button */
        final TethysUIButtonFactory<?> myButtons = pFactory.buttonFactory();
        theLoanButton = myButtons.newScrollButton(LoanBucket.class);

        /* Create the category button */
        theCatButton = myButtons.newScrollButton(LoanCategory.class);

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Create the labels */
        final TethysUIControlFactory myControls = pFactory.controlFactory();
        final TethysUILabel myCatLabel = myControls.newLabel(NLS_CATEGORY + TethysUIConstant.STR_COLON);
        final TethysUILabel myLoanLabel = myControls.newLabel(NLS_LOAN + TethysUIConstant.STR_COLON);

        /* Define the layout */
        thePanel = pFactory.paneFactory().newHBoxPane();
        thePanel.addSpacer();
        thePanel.addNode(myCatLabel);
        thePanel.addNode(theCatButton);
        thePanel.addStrut();
        thePanel.addNode(myLoanLabel);
        thePanel.addNode(theLoanButton);

        /* Create initial state */
        theState = new LoanState();
        theState.applyState();

        /* Access the menus */
        theCategoryMenu = theCatButton.getMenu();
        theLoanMenu = theLoanButton.getMenu();

        /* Create the listener */
        TethysEventRegistrar<TethysUIEvent> myRegistrar = theCatButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewCategory());
        theCatButton.setMenuConfigurator(e -> buildCategoryMenu());
        myRegistrar = theLoanButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewLoan());
        theLoanButton.setMenuConfigurator(e -> buildLoanMenu());
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePanel;
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public LoanFilter getFilter() {
        return theState.getFilter();
    }

    @Override
    public boolean isAvailable() {
        return theLoans != null
               && !theLoans.isEmpty();
    }

    /**
     * Create SavePoint.
     */
    protected void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new LoanState(theState);
    }

    /**
     * Restore SavePoint.
     */
    protected void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new LoanState(theSavePoint);

        /* Apply the state */
        theState.applyState();
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* Determine whether there are any Loans to select */
        final boolean lnAvailable = bEnabled && isAvailable();

        /* Pass call on to buttons */
        theLoanButton.setEnabled(lnAvailable);
        theCatButton.setEnabled(lnAvailable);
    }

    /**
     * Set analysis.
     * @param pAnalysis the analysis.
     */
    public void setAnalysis(final Analysis pAnalysis) {
        /* Access buckets */
        theCategories = pAnalysis.getLoanCategories();
        theLoans = pAnalysis.getLoans();

        /* Obtain the current account */
        LoanBucket myLoan = theState.getLoan();

        /* Switch to versions from the analysis */
        myLoan = myLoan != null
                                ? theLoans.getMatchingLoan(myLoan.getAccount())
                                : theLoans.getDefaultLoan();

        /* Set the loan */
        theState.setTheLoan(myLoan);
        theState.applyState();
    }

    @Override
    public void setFilter(final AnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof LoanFilter) {
            /* Access filter */
            final LoanFilter myFilter = (LoanFilter) pFilter;

            /* Obtain the filter bucket */
            LoanBucket myLoan = myFilter.getBucket();

            /* Obtain equivalent bucket */
            myLoan = theLoans.getMatchingLoan(myLoan.getAccount());

            /* Set the loan */
            theState.setTheLoan(myLoan);
            theState.applyState();
        }
    }

    /**
     * Obtain the default Loan for the category.
     * @param pCategory the category
     * @return the bucket
     */
    protected LoanBucket getDefaultLoan(final LoanCategory pCategory) {
        return theLoans.getDefaultLoan(pCategory);
    }

    /**
     * Handle new Category.
     */
    private void handleNewCategory() {
        /* Select the new category */
        if (theState.setCategory(theCatButton.getValue())) {
            theState.applyState();
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * Handle new Loan.
     */
    private void handleNewLoan() {
        /* Select the new loan */
        if (theState.setLoan(theLoanButton.getValue())) {
            theState.applyState();
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * Build Category menu.
     */
    private void buildCategoryMenu() {
        /* Reset the popUp menu */
        theCategoryMenu.removeAllItems();

        /* Create a simple map for top-level categories */
        final Map<String, TethysUIScrollSubMenu<LoanCategory>> myMap = new HashMap<>();

        /* Record active item */
        final LoanCategory myCurrent = theState.getCategory();
        TethysUIScrollItem<LoanCategory> myActive = null;

        /* Re-Loop through the available category values */
        final Iterator<LoanCategoryBucket> myIterator = theCategories.iterator();
        while (myIterator.hasNext()) {
            final LoanCategoryBucket myBucket = myIterator.next();

            /* Only process low-level items */
            if (myBucket.getAccountCategory().isCategoryClass(LoanCategoryClass.PARENT)) {
                continue;
            }

            /* Determine menu to add to */
            final LoanCategory myParent = myBucket.getAccountCategory().getParentCategory();
            final String myParentName = myParent.getName();
            TethysUIScrollSubMenu<LoanCategory> myMenu = myMap.get(myParent.getName());

            /* If this is a new menu */
            if (myMenu == null) {
                /* Create a new JMenu and add it to the popUp */
                myMenu = theCategoryMenu.addSubMenu(myParentName);
                myMap.put(myParentName, myMenu);
            }

            /* Create a new JMenuItem and add it to the popUp */
            final LoanCategory myCategory = myBucket.getAccountCategory();
            final TethysUIScrollItem<LoanCategory> myItem = myMenu.getSubMenu().addItem(myCategory, myCategory.getSubCategory());

            /* If this is the active category */
            if (myCategory.equals(myCurrent)) {
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
     * Build Loan menu.
     */
    private void buildLoanMenu() {
        /* Reset the popUp menu */
        theLoanMenu.removeAllItems();

        /* Access current category and Loan */
        final LoanCategory myCategory = theState.getCategory();
        final LoanBucket myLoan = theState.getLoan();

        /* Record active item */
        TethysUIScrollItem<LoanBucket> myActive = null;

        /* Loop through the available account values */
        final Iterator<LoanBucket> myIterator = theLoans.iterator();
        while (myIterator.hasNext()) {
            final LoanBucket myBucket = myIterator.next();

            /* Ignore if not the correct category */
            if (!MetisDataDifference.isEqual(myCategory, myBucket.getCategory())) {
                continue;
            }

            /* Create a new MenuItem and add it to the popUp */
            final TethysUIScrollItem<LoanBucket> myItem = theLoanMenu.addItem(myBucket);

            /* If this is the active loan */
            if (myBucket.equals(myLoan)) {
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
    private final class LoanState {
        /**
         * The active Category.
         */
        private LoanCategory theCategory;

        /**
         * The active LoanBucket.
         */
        private LoanBucket theLoan;

        /**
         * The active Filter.
         */
        private LoanFilter theFilter;

        /**
         * Constructor.
         */
        private LoanState() {
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private LoanState(final LoanState pState) {
            /* Initialise state */
            theLoan = pState.getLoan();
            theCategory = pState.getCategory();
            theFilter = pState.getFilter();
        }

        /**
         * Obtain the Loan Bucket.
         * @return the Loan
         */
        private LoanBucket getLoan() {
            return theLoan;
        }

        /**
         * Obtain the Category.
         * @return the category
         */
        private LoanCategory getCategory() {
            return theCategory;
        }

        /**
         * Obtain the Filter.
         * @return the filter
         */
        private LoanFilter getFilter() {
            return theFilter;
        }

        /**
         * Set new Loan Account.
         * @param pLoan the Loan Account
         * @return true/false did a change occur
         */
        private boolean setLoan(final LoanBucket pLoan) {
            /* Adjust the selected loan */
            if (!MetisDataDifference.isEqual(pLoan, theLoan)) {
                /* Set the Loan */
                setTheLoan(pLoan);
                return true;
            }
            return false;
        }

        /**
         * Set the Loan.
         * @param pLoan the Loan
         */
        private void setTheLoan(final LoanBucket pLoan) {
            /* Access category for account */
            final LoanCategory myCategory = pLoan == null
                                                          ? null
                                                          : pLoan.getCategory();
            setTheLoan(myCategory, pLoan);
        }

        /**
         * Set the Loan.
         * @param pCategory the category
         * @param pLoan the Loan
         */
        private void setTheLoan(final LoanCategory pCategory,
                                final LoanBucket pLoan) {
            /* Store the loan */
            theLoan = pLoan;
            theCategory = pCategory;

            /* Access filter */
            theFilter = theLoan != null
                                        ? new LoanFilter(theLoan)
                                        : null;
        }

        /**
         * Set new Category.
         * @param pCategory the Category
         * @return true/false did a change occur
         */
        private boolean setCategory(final LoanCategory pCategory) {
            /* Adjust the selected category */
            if (!MetisDataDifference.isEqual(pCategory, theCategory)) {
                setTheLoan(pCategory, getDefaultLoan(pCategory));
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
            theLoanButton.setValue(theLoan);
            theCatButton.setValue(theCategory);
        }
    }
}