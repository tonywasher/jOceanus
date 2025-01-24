/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashBucket.MoneyWiseXAnalysisCashBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashCategoryBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashCategoryBucket.MoneyWiseXAnalysisCashCategoryBucketList;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisCashFilter;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCashCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCashCategoryClass;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.TethysEventProvider;
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
 * Cash Analysis Selection.
 */
public class MoneyWiseXCashAnalysisSelect
        implements MoneyWiseXAnalysisFilterSelection, TethysEventProvider<PrometheusDataEvent> {
    /**
     * Text for Category Label.
     */
    private static final String NLS_CATEGORY = MoneyWiseBasicDataType.CASHCATEGORY.getItemName();

    /**
     * Text for Account Label.
     */
    private static final String NLS_CASH = MoneyWiseBasicDataType.CASH.getItemName();

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * The cash button.
     */
    private final TethysUIScrollButtonManager<MoneyWiseXAnalysisCashBucket> theCashButton;

    /**
     * The category button.
     */
    private final TethysUIScrollButtonManager<MoneyWiseCashCategory> theCatButton;

    /**
     * Category menu.
     */
    private final TethysUIScrollMenu<MoneyWiseCashCategory> theCategoryMenu;

    /**
     * Cash menu.
     */
    private final TethysUIScrollMenu<MoneyWiseXAnalysisCashBucket> theCashMenu;

    /**
     * The active category bucket list.
     */
    private MoneyWiseXAnalysisCashCategoryBucketList theCategories;

    /**
     * The active cash bucket list.
     */
    private MoneyWiseXAnalysisCashBucketList theCash;

    /**
     * The state.
     */
    private MoneyWiseCashState theState;

    /**
     * The savePoint.
     */
    private MoneyWiseCashState theSavePoint;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected MoneyWiseXCashAnalysisSelect(final TethysUIFactory<?> pFactory) {
        /* Create the cash button */
        final TethysUIButtonFactory<?> myButtons = pFactory.buttonFactory();
        theCashButton = myButtons.newScrollButton(MoneyWiseXAnalysisCashBucket.class);

        /* Create the category button */
        theCatButton = myButtons.newScrollButton(MoneyWiseCashCategory.class);

        /* Create Event Manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the labels */
        final TethysUIControlFactory myControls = pFactory.controlFactory();
        final TethysUILabel myCatLabel = myControls.newLabel(NLS_CATEGORY + TethysUIConstant.STR_COLON);
        final TethysUILabel myCshLabel = myControls.newLabel(NLS_CASH + TethysUIConstant.STR_COLON);

        /* Define the layout */
        thePanel = pFactory.paneFactory().newHBoxPane();
        thePanel.addSpacer();
        thePanel.addNode(myCatLabel);
        thePanel.addNode(theCatButton);
        thePanel.addStrut();
        thePanel.addNode(myCshLabel);
        thePanel.addNode(theCashButton);

        /* Create initial state */
        theState = new MoneyWiseCashState();
        theState.applyState();

        /* Access the menus */
        theCategoryMenu = theCatButton.getMenu();
        theCashMenu = theCashButton.getMenu();

        /* Create the listeners */
        OceanusEventRegistrar<TethysUIEvent> myRegistrar = theCatButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewCategory());
        theCatButton.setMenuConfigurator(e -> buildCategoryMenu());
        myRegistrar = theCashButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewCash());
        theCashButton.setMenuConfigurator(e -> buildCashMenu());
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
    public MoneyWiseXAnalysisCashFilter getFilter() {
        return theState.getFilter();
    }

    @Override
    public boolean isAvailable() {
        return theCash != null
                && !theCash.isEmpty();
    }

    /**
     * Create SavePoint.
     */
    protected void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new MoneyWiseCashState(theState);
    }

    /**
     * Restore SavePoint.
     */
    protected void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new MoneyWiseCashState(theSavePoint);

        /* Apply the state */
        theState.applyState();
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* Determine whether there are any Accounts to select */
        final boolean csAvailable = bEnabled && isAvailable();

        /* Pass call on to buttons */
        theCashButton.setEnabled(csAvailable);
        theCatButton.setEnabled(csAvailable);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    /**
     * Set analysis.
     * @param pAnalysis the analysis.
     */
    public void setAnalysis(final MoneyWiseXAnalysis pAnalysis) {
        /* Access buckets */
        theCategories = pAnalysis.getCashCategories();
        theCash = pAnalysis.getCash();

        /* Obtain the current cash */
        MoneyWiseXAnalysisCashBucket myCash = theState.getCash();

        /* Switch to versions from the analysis */
        myCash = myCash != null
                ? theCash.getMatchingCash(myCash.getAccount())
                : theCash.getDefaultCash();

        /* Set the cash */
        theState.setTheCash(myCash);
        theState.setDateRange(pAnalysis.getDateRange());
        theState.applyState();
    }

    @Override
    public void setFilter(final MoneyWiseXAnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof MoneyWiseXAnalysisCashFilter) {
            /* Access filter */
            final MoneyWiseXAnalysisCashFilter myFilter = (MoneyWiseXAnalysisCashFilter) pFilter;

            /* Obtain the filter bucket */
            MoneyWiseXAnalysisCashBucket myCash = myFilter.getBucket();

            /* Obtain equivalent bucket */
            myCash = theCash.getMatchingCash(myCash.getAccount());

            /* Set the cash */
            theState.setTheCash(myCash);
            theState.setDateRange(myFilter.getDateRange());
            theState.applyState();
        }
    }

    /**
     * Obtain the default Cash for the category.
     * @param pCategory the category
     * @return the bucket
     */
    protected MoneyWiseXAnalysisCashBucket getDefaultCash(final MoneyWiseCashCategory pCategory) {
        return theCash.getDefaultCash(pCategory);
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
     * Handle new Cash.
     */
    private void handleNewCash() {
        /* Select the new cash */
        if (theState.setCash(theCashButton.getValue())) {
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
        final Map<String, TethysUIScrollSubMenu<MoneyWiseCashCategory>> myMap = new HashMap<>();

        /* Record active item */
        final MoneyWiseCashCategory myCurrent = theState.getCategory();
        TethysUIScrollItem<MoneyWiseCashCategory> myActive = null;

        /* Loop through the available category values */
        final Iterator<MoneyWiseXAnalysisCashCategoryBucket> myIterator = theCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisCashCategoryBucket myBucket = myIterator.next();

            /* Only process low-level items */
            if (myBucket.getAccountCategory().isCategoryClass(MoneyWiseCashCategoryClass.PARENT)) {
                continue;
            }

            /* Determine menu to add to */
            final MoneyWiseCashCategory myParent = myBucket.getAccountCategory().getParentCategory();
            final String myParentName = myParent.getName();
            final TethysUIScrollSubMenu<MoneyWiseCashCategory> myMenu = myMap.computeIfAbsent(myParentName, theCategoryMenu::addSubMenu);

            /* Create a new JMenuItem and add it to the popUp */
            final MoneyWiseCashCategory myCategory = myBucket.getAccountCategory();
            final TethysUIScrollItem<MoneyWiseCashCategory> myItem = myMenu.getSubMenu().addItem(myCategory, myCategory.getSubCategory());

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
     * Build Cash menu.
     */
    private void buildCashMenu() {
        /* Reset the popUp menu */
        theCashMenu.removeAllItems();

        /* Access current category and Account */
        final MoneyWiseCashCategory myCategory = theState.getCategory();
        final MoneyWiseXAnalysisCashBucket myCash = theState.getCash();

        /* Record active item */
        TethysUIScrollItem<MoneyWiseXAnalysisCashBucket> myActive = null;

        /* Loop through the available account values */
        final Iterator<MoneyWiseXAnalysisCashBucket> myIterator = theCash.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisCashBucket myBucket = myIterator.next();

            /* Ignore if not the correct category */
            if (!MetisDataDifference.isEqual(myCategory, myBucket.getCategory())) {
                continue;
            }

            /* Create a new JMenuItem and add it to the popUp */
            final TethysUIScrollItem<MoneyWiseXAnalysisCashBucket> myItem = theCashMenu.addItem(myBucket);

            /* If this is the active cash */
            if (myBucket.equals(myCash)) {
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
    private final class MoneyWiseCashState {
        /**
         * The active Category.
         */
        private MoneyWiseCashCategory theCategory;

        /**
         * The active CashBucket.
         */
        private MoneyWiseXAnalysisCashBucket theCash;

        /**
         * The dateRange.
         */
        private OceanusDateRange theDateRange;

        /**
         * The active Filter.
         */
        private MoneyWiseXAnalysisCashFilter theFilter;

        /**
         * Constructor.
         */
        private MoneyWiseCashState() {
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private MoneyWiseCashState(final MoneyWiseCashState pState) {
            /* Initialise state */
            theCash = pState.getCash();
            theCategory = pState.getCategory();
            theDateRange = pState.getDateRange();
            theFilter = pState.getFilter();
        }

        /**
         * Obtain the Cash Bucket.
         * @return the Cash
         */
        private MoneyWiseXAnalysisCashBucket getCash() {
            return theCash;
        }

        /**
         * Obtain the Category.
         * @return the category
         */
        private MoneyWiseCashCategory getCategory() {
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
        private MoneyWiseXAnalysisCashFilter getFilter() {
            return theFilter;
        }

        /**
         * Set new Cash Account.
         * @param pCash the Cash Account
         * @return true/false did a change occur
         */
        private boolean setCash(final MoneyWiseXAnalysisCashBucket pCash) {
            /* Adjust the selected cash */
            if (!MetisDataDifference.isEqual(pCash, theCash)) {
                /* Store the cash */
                setTheCash(pCash);
                return true;
            }
            return false;
        }

        /**
         * Set the Cash.
         * @param pCash the Cash
         */
        private void setTheCash(final MoneyWiseXAnalysisCashBucket pCash) {
            /* Access category for account */
            final MoneyWiseCashCategory myCategory = pCash == null
                    ? null
                    : pCash.getCategory();
            setTheCash(myCategory, pCash);
        }

        /**
         * Set the Cash.
         * @param pCategory the category
         * @param pCash the Cash
         */
        private void setTheCash(final MoneyWiseCashCategory pCategory,
                                final MoneyWiseXAnalysisCashBucket pCash) {
            /* Store the cash */
            theCash = pCash;
            theCategory = pCategory;

            /* Access filter */
            if (theCash != null) {
                theFilter = new MoneyWiseXAnalysisCashFilter(theCash);
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
        private boolean setCategory(final MoneyWiseCashCategory pCategory) {
            /* Adjust the selected category */
            if (!MetisDataDifference.isEqual(pCategory, theCategory)) {
                setTheCash(pCategory, getDefaultCash(pCategory));
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
            theCashButton.setValue(theCash);
            theCatButton.setValue(theCategory);
        }
    }
}
