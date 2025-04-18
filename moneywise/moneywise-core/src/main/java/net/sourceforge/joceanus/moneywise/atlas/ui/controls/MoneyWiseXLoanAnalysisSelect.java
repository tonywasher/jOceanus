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
package net.sourceforge.joceanus.moneywise.atlas.ui.controls;

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanBucket.MoneyWiseXAnalysisLoanBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanCategoryBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanCategoryBucket.MoneyWiseXAnalysisLoanCategoryBucketList;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisLoanFilter;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryClass;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import net.sourceforge.joceanus.prometheus.views.PrometheusDataEvent;
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
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollSubMenu;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBoxPaneManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Loan Analysis Selection.
 */
public class MoneyWiseXLoanAnalysisSelect
        implements MoneyWiseXAnalysisFilterSelection, OceanusEventProvider<PrometheusDataEvent> {
    /**
     * Text for Category Label.
     */
    private static final String NLS_CATEGORY = MoneyWiseBasicDataType.LOANCATEGORY.getItemName();

    /**
     * Text for Loan Label.
     */
    private static final String NLS_LOAN = MoneyWiseBasicDataType.LOAN.getItemName();

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * The loan button.
     */
    private final TethysUIScrollButtonManager<MoneyWiseXAnalysisLoanBucket> theLoanButton;

    /**
     * The category button.
     */
    private final TethysUIScrollButtonManager<MoneyWiseLoanCategory> theCatButton;

    /**
     * Category menu.
     */
    private final TethysUIScrollMenu<MoneyWiseLoanCategory> theCategoryMenu;

    /**
     * Loan menu.
     */
    private final TethysUIScrollMenu<MoneyWiseXAnalysisLoanBucket> theLoanMenu;

    /**
     * The active category bucket list.
     */
    private MoneyWiseXAnalysisLoanCategoryBucketList theCategories;

    /**
     * The active loan bucket list.
     */
    private MoneyWiseXAnalysisLoanBucketList theLoans;

    /**
     * The state.
     */
    private MoneyWiseLoanState theState;

    /**
     * The savePoint.
     */
    private MoneyWiseLoanState theSavePoint;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected MoneyWiseXLoanAnalysisSelect(final TethysUIFactory<?> pFactory) {
        /* Create the loan button */
        final TethysUIButtonFactory<?> myButtons = pFactory.buttonFactory();
        theLoanButton = myButtons.newScrollButton(MoneyWiseXAnalysisLoanBucket.class);

        /* Create the category button */
        theCatButton = myButtons.newScrollButton(MoneyWiseLoanCategory.class);

        /* Create Event Manager */
        theEventManager = new OceanusEventManager<>();

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
        theState = new MoneyWiseLoanState();
        theState.applyState();

        /* Access the menus */
        theCategoryMenu = theCatButton.getMenu();
        theLoanMenu = theLoanButton.getMenu();

        /* Create the listener */
        OceanusEventRegistrar<TethysUIEvent> myRegistrar = theCatButton.getEventRegistrar();
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
    public OceanusEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public MoneyWiseXAnalysisLoanFilter getFilter() {
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
        theSavePoint = new MoneyWiseLoanState(theState);
    }

    /**
     * Restore SavePoint.
     */
    protected void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new MoneyWiseLoanState(theSavePoint);

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
    public void setAnalysis(final MoneyWiseXAnalysis pAnalysis) {
        /* Access buckets */
        theCategories = pAnalysis.getLoanCategories();
        theLoans = pAnalysis.getLoans();

        /* Obtain the current account */
        MoneyWiseXAnalysisLoanBucket myLoan = theState.getLoan();

        /* Switch to versions from the analysis */
        myLoan = myLoan != null
                ? theLoans.getMatchingLoan(myLoan.getAccount())
                : theLoans.getDefaultLoan();

        /* Set the loan */
        theState.setTheLoan(myLoan);
        theState.setDateRange(pAnalysis.getDateRange());
        theState.applyState();
    }

    @Override
    public void setFilter(final MoneyWiseXAnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof MoneyWiseXAnalysisLoanFilter myFilter) {
            /* Obtain the filter bucket */
            MoneyWiseXAnalysisLoanBucket myLoan = myFilter.getBucket();

            /* Obtain equivalent bucket */
            myLoan = theLoans.getMatchingLoan(myLoan.getAccount());

            /* Set the loan */
            theState.setTheLoan(myLoan);
            theState.setDateRange(myFilter.getDateRange());
            theState.applyState();
        }
    }

    /**
     * Obtain the default Loan for the category.
     * @param pCategory the category
     * @return the bucket
     */
    protected MoneyWiseXAnalysisLoanBucket getDefaultLoan(final MoneyWiseLoanCategory pCategory) {
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
        final Map<String, TethysUIScrollSubMenu<MoneyWiseLoanCategory>> myMap = new HashMap<>();

        /* Record active item */
        final MoneyWiseLoanCategory myCurrent = theState.getCategory();
        TethysUIScrollItem<MoneyWiseLoanCategory> myActive = null;

        /* Re-Loop through the available category values */
        final Iterator<MoneyWiseXAnalysisLoanCategoryBucket> myIterator = theCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisLoanCategoryBucket myBucket = myIterator.next();

            /* Only process low-level items */
            if (myBucket.getAccountCategory().isCategoryClass(MoneyWiseLoanCategoryClass.PARENT)) {
                continue;
            }

            /* Determine menu to add to */
            final MoneyWiseLoanCategory myParent = myBucket.getAccountCategory().getParentCategory();
            final String myParentName = myParent.getName();
            TethysUIScrollSubMenu<MoneyWiseLoanCategory> myMenu = myMap.get(myParent.getName());

            /* If this is a new menu */
            if (myMenu == null) {
                /* Create a new JMenu and add it to the popUp */
                myMenu = theCategoryMenu.addSubMenu(myParentName);
                myMap.put(myParentName, myMenu);
            }

            /* Create a new JMenuItem and add it to the popUp */
            final MoneyWiseLoanCategory myCategory = myBucket.getAccountCategory();
            final TethysUIScrollItem<MoneyWiseLoanCategory> myItem = myMenu.getSubMenu().addItem(myCategory, myCategory.getSubCategory());

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
        final MoneyWiseLoanCategory myCategory = theState.getCategory();
        final MoneyWiseXAnalysisLoanBucket myLoan = theState.getLoan();

        /* Record active item */
        TethysUIScrollItem<MoneyWiseXAnalysisLoanBucket> myActive = null;

        /* Loop through the available account values */
        final Iterator<MoneyWiseXAnalysisLoanBucket> myIterator = theLoans.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisLoanBucket myBucket = myIterator.next();

            /* Ignore if not the correct category */
            if (!MetisDataDifference.isEqual(myCategory, myBucket.getCategory())) {
                continue;
            }

            /* Create a new MenuItem and add it to the popUp */
            final TethysUIScrollItem<MoneyWiseXAnalysisLoanBucket> myItem = theLoanMenu.addItem(myBucket);

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
    private final class MoneyWiseLoanState {
        /**
         * The active Category.
         */
        private MoneyWiseLoanCategory theCategory;

        /**
         * The active LoanBucket.
         */
        private MoneyWiseXAnalysisLoanBucket theLoan;

        /**
         * The dateRange.
         */
        private OceanusDateRange theDateRange;

        /**
         * The active Filter.
         */
        private MoneyWiseXAnalysisLoanFilter theFilter;

        /**
         * Constructor.
         */
        private MoneyWiseLoanState() {
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private MoneyWiseLoanState(final MoneyWiseLoanState pState) {
            /* Initialise state */
            theLoan = pState.getLoan();
            theCategory = pState.getCategory();
            theDateRange = pState.getDateRange();
            theFilter = pState.getFilter();
        }

        /**
         * Obtain the Loan Bucket.
         * @return the Loan
         */
        private MoneyWiseXAnalysisLoanBucket getLoan() {
            return theLoan;
        }

        /**
         * Obtain the Category.
         * @return the category
         */
        private MoneyWiseLoanCategory getCategory() {
            return theCategory;
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
         * @return the filter
         */
        private MoneyWiseXAnalysisLoanFilter getFilter() {
            return theFilter;
        }

        /**
         * Set new Loan Account.
         * @param pLoan the Loan Account
         * @return true/false did a change occur
         */
        private boolean setLoan(final MoneyWiseXAnalysisLoanBucket pLoan) {
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
        private void setTheLoan(final MoneyWiseXAnalysisLoanBucket pLoan) {
            /* Access category for account */
            final MoneyWiseLoanCategory myCategory = pLoan == null
                    ? null
                    : pLoan.getCategory();
            setTheLoan(myCategory, pLoan);
        }

        /**
         * Set the Loan.
         * @param pCategory the category
         * @param pLoan the Loan
         */
        private void setTheLoan(final MoneyWiseLoanCategory pCategory,
                                final MoneyWiseXAnalysisLoanBucket pLoan) {
            /* Store the loan */
            theLoan = pLoan;
            theCategory = pCategory;

            /* Access filter */
            if (theLoan != null) {
                theFilter = new MoneyWiseXAnalysisLoanFilter(theLoan);
                theFilter.setDateRange(theDateRange);
            } else {
                theFilter = null;
            }
        }

        /**
         * Set new Category.
         * @param pCategory the Category
         * @return true/false did a change occur
         */
        private boolean setCategory(final MoneyWiseLoanCategory pCategory) {
            /* Adjust the selected category */
            if (!MetisDataDifference.isEqual(pCategory, theCategory)) {
                setTheLoan(pCategory, getDefaultLoan(pCategory));
                return true;
            }
            return false;
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
            theLoanButton.setValue(theLoan);
            theCatButton.setValue(theCategory);
        }
    }
}
