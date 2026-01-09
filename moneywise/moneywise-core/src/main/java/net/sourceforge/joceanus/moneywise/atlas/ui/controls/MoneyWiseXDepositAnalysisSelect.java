/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012-2026 Tony Washer
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
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositBucket.MoneyWiseXAnalysisDepositBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositCategoryBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositCategoryBucket.MoneyWiseXAnalysisDepositCategoryBucketList;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisDepositFilter;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseDepositCategoryClass;
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
 * Deposit Analysis Selection.
 */
public class MoneyWiseXDepositAnalysisSelect
        implements MoneyWiseXAnalysisFilterSelection, OceanusEventProvider<PrometheusDataEvent> {
    /**
     * Text for Category Label.
     */
    private static final String NLS_CATEGORY = MoneyWiseBasicDataType.DEPOSITCATEGORY.getItemName();

    /**
     * Text for Deposit Label.
     */
    private static final String NLS_DEPOSIT = MoneyWiseBasicDataType.DEPOSIT.getItemName();

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * The deposit button.
     */
    private final TethysUIScrollButtonManager<MoneyWiseXAnalysisDepositBucket> theDepositButton;

    /**
     * The category button.
     */
    private final TethysUIScrollButtonManager<MoneyWiseDepositCategory> theCatButton;

    /**
     * Category menu.
     */
    private final TethysUIScrollMenu<MoneyWiseDepositCategory> theCategoryMenu;

    /**
     * Deposit menu.
     */
    private final TethysUIScrollMenu<MoneyWiseXAnalysisDepositBucket> theDepositMenu;

    /**
     * The active category bucket list.
     */
    private MoneyWiseXAnalysisDepositCategoryBucketList theCategories;

    /**
     * The active deposit bucket list.
     */
    private MoneyWiseXAnalysisDepositBucketList theDeposits;

    /**
     * The state.
     */
    private MoneyWiseDepositState theState;

    /**
     * The savePoint.
     */
    private MoneyWiseDepositState theSavePoint;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected MoneyWiseXDepositAnalysisSelect(final TethysUIFactory<?> pFactory) {
        /* Create the deposit button */
        final TethysUIButtonFactory<?> myButtons = pFactory.buttonFactory();
        theDepositButton = myButtons.newScrollButton(MoneyWiseXAnalysisDepositBucket.class);

        /* Create the category button */
        theCatButton = myButtons.newScrollButton(MoneyWiseDepositCategory.class);

        /* Create Event Manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the labels */
        final TethysUIControlFactory myControls = pFactory.controlFactory();
        final TethysUILabel myCatLabel = myControls.newLabel(NLS_CATEGORY + TethysUIConstant.STR_COLON);
        final TethysUILabel myDepLabel = myControls.newLabel(NLS_DEPOSIT + TethysUIConstant.STR_COLON);

        /* Define the layout */
        thePanel = pFactory.paneFactory().newHBoxPane();
        thePanel.addSpacer();
        thePanel.addNode(myCatLabel);
        thePanel.addNode(theCatButton);
        thePanel.addStrut();
        thePanel.addNode(myDepLabel);
        thePanel.addNode(theDepositButton);

        /* Create initial state */
        theState = new MoneyWiseDepositState();
        theState.applyState();

        /* Access the menus */
        theCategoryMenu = theCatButton.getMenu();
        theDepositMenu = theDepositButton.getMenu();

        /* Create the listeners */
        OceanusEventRegistrar<TethysUIEvent> myRegistrar = theCatButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewCategory());
        theCatButton.setMenuConfigurator(e -> buildCategoryMenu());
        myRegistrar = theDepositButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewDeposit());
        theDepositButton.setMenuConfigurator(e -> buildDepositMenu());
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
    public MoneyWiseXAnalysisDepositFilter getFilter() {
        return theState.getFilter();
    }

    @Override
    public boolean isAvailable() {
        return theDeposits != null
                && !theDeposits.isEmpty();
    }

    /**
     * Create SavePoint.
     */
    protected void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new MoneyWiseDepositState(theState);
    }

    /**
     * Restore SavePoint.
     */
    protected void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new MoneyWiseDepositState(theSavePoint);

        /* Apply the state */
        theState.applyState();
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* Determine whether there are any Deposits to select */
        final boolean dpAvailable = bEnabled && isAvailable();

        /* Pass call on to buttons */
        theDepositButton.setEnabled(dpAvailable);
        theCatButton.setEnabled(dpAvailable);
    }

    /**
     * Set analysis.
     * @param pAnalysis the analysis.
     */
    public void setAnalysis(final MoneyWiseXAnalysis pAnalysis) {
        /* Access buckets */
        theCategories = pAnalysis.getDepositCategories();
        theDeposits = pAnalysis.getDeposits();

        /* Obtain the current deposit */
        MoneyWiseXAnalysisDepositBucket myDeposit = theState.getDeposit();

        /* Switch to versions from the analysis */
        myDeposit = myDeposit != null
                ? theDeposits.getMatchingDeposit(myDeposit.getAccount())
                : theDeposits.getDefaultDeposit();

        /* Set the deposit */
        theState.setTheDeposit(myDeposit);
        theState.setDateRange(pAnalysis.getDateRange());
        theState.applyState();
    }

    @Override
    public void setFilter(final MoneyWiseXAnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof MoneyWiseXAnalysisDepositFilter myFilter) {
            /* Obtain the filter bucket */
            MoneyWiseXAnalysisDepositBucket myDeposit = myFilter.getBucket();

            /* Obtain equivalent bucket */
            myDeposit = theDeposits.getMatchingDeposit(myDeposit.getAccount());

            /* Set the deposit */
            theState.setTheDeposit(myDeposit);
            theState.setDateRange(myFilter.getDateRange());
            theState.applyState();
        }
    }

    /**
     * Obtain the default Deposit for the category.
     * @param pCategory the category
     * @return the bucket
     */
    protected MoneyWiseXAnalysisDepositBucket getDefaultDeposit(final MoneyWiseDepositCategory pCategory) {
        return theDeposits.getDefaultDeposit(pCategory);
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
     * Handle new Deposit.
     */
    private void handleNewDeposit() {
        /* Select the new deposit */
        if (theState.setDeposit(theDepositButton.getValue())) {
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
        final Map<String, TethysUIScrollSubMenu<MoneyWiseDepositCategory>> myMap = new HashMap<>();

        /* Record active item */
        final MoneyWiseDepositCategory myCurrent = theState.getCategory();
        TethysUIScrollItem<MoneyWiseDepositCategory> myActive = null;

        /* Re-Loop through the available category values */
        final Iterator<MoneyWiseXAnalysisDepositCategoryBucket> myIterator = theCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisDepositCategoryBucket myBucket = myIterator.next();

            /* Only process low-level items */
            if (myBucket.getAccountCategory().isCategoryClass(MoneyWiseDepositCategoryClass.PARENT)) {
                continue;
            }

            /* Determine menu to add to */
            final MoneyWiseDepositCategory myParent = myBucket.getAccountCategory().getParentCategory();
            final String myParentName = myParent.getName();
            final TethysUIScrollSubMenu<MoneyWiseDepositCategory> myMenu = myMap.computeIfAbsent(myParentName, theCategoryMenu::addSubMenu);

            /* Create a new JMenuItem and add it to the popUp */
            final MoneyWiseDepositCategory myCategory = myBucket.getAccountCategory();
            final TethysUIScrollItem<MoneyWiseDepositCategory> myItem = myMenu.getSubMenu().addItem(myCategory, myCategory.getSubCategory());

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
     * Build Deposit menu.
     */
    private void buildDepositMenu() {
        /* Reset the popUp menu */
        theDepositMenu.removeAllItems();

        /* Access current category */
        final MoneyWiseDepositCategory myCategory = theState.getCategory();
        final MoneyWiseXAnalysisDepositBucket myDeposit = theState.getDeposit();

        /* Record active item */
        TethysUIScrollItem<MoneyWiseXAnalysisDepositBucket> myActive = null;

        /* Loop through the available account values */
        final Iterator<MoneyWiseXAnalysisDepositBucket> myIterator = theDeposits.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisDepositBucket myBucket = myIterator.next();

            /* Ignore if not the correct category */
            if (!MetisDataDifference.isEqual(myCategory, myBucket.getCategory())) {
                continue;
            }

            /* Create a new JMenuItem and add it to the popUp */
            final TethysUIScrollItem<MoneyWiseXAnalysisDepositBucket> myItem = theDepositMenu.addItem(myBucket);

            /* If this is the active deposit */
            if (myBucket.equals(myDeposit)) {
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
    private final class MoneyWiseDepositState {
        /**
         * The active Category.
         */
        private MoneyWiseDepositCategory theCategory;

        /**
         * The active DepositBucket.
         */
        private MoneyWiseXAnalysisDepositBucket theDeposit;

        /**
         * The dateRange.
         */
        private OceanusDateRange theDateRange;

        /**
         * The active Filter.
         */
        private MoneyWiseXAnalysisDepositFilter theFilter;

        /**
         * Constructor.
         */
        private MoneyWiseDepositState() {
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private MoneyWiseDepositState(final MoneyWiseDepositState pState) {
            /* Initialise state */
            theDeposit = pState.getDeposit();
            theCategory = pState.getCategory();
            theDateRange = pState.getDateRange();
            theFilter = pState.getFilter();
        }

        /**
         * Obtain the Deposit Bucket.
         * @return the Deposit
         */
        private MoneyWiseXAnalysisDepositBucket getDeposit() {
            return theDeposit;
        }

        /**
         * Obtain the Category.
         * @return the category
         */
        private MoneyWiseDepositCategory getCategory() {
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
        private MoneyWiseXAnalysisDepositFilter getFilter() {
            return theFilter;
        }

        /**
         * Set new Deposit.
         * @param pDeposit the Deposit
         * @return true/false did a change occur
         */
        private boolean setDeposit(final MoneyWiseXAnalysisDepositBucket pDeposit) {
            /* Adjust the selected deposit */
            if (!MetisDataDifference.isEqual(pDeposit, theDeposit)) {
                /* Store the deposit */
                setTheDeposit(pDeposit);
                return true;
            }
            return false;
        }

        /**
         * Set the Deposit.
         * @param pDeposit the Deposit
         */
        private void setTheDeposit(final MoneyWiseXAnalysisDepositBucket pDeposit) {
            /* Access category for account */
            final MoneyWiseDepositCategory myCategory = pDeposit == null
                    ? null
                    : pDeposit.getCategory();
            setTheDeposit(myCategory, pDeposit);
        }

        /**
         * Set the Deposit.
         * @param pCategory the category
         * @param pDeposit the Deposit
         */
        private void setTheDeposit(final MoneyWiseDepositCategory pCategory,
                                   final MoneyWiseXAnalysisDepositBucket pDeposit) {
            /* Store the deposit */
            theDeposit = pDeposit;
            theCategory = pCategory;

            /* Access filter */
            if (theDeposit != null) {
                theFilter = new MoneyWiseXAnalysisDepositFilter(theDeposit);
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
        private boolean setCategory(final MoneyWiseDepositCategory pCategory) {
            /* Adjust the selected category */
            if (!MetisDataDifference.isEqual(pCategory, theCategory)) {
                setTheDeposit(pCategory, getDefaultDeposit(pCategory));
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
            theDepositButton.setValue(theDeposit);
            theCatButton.setValue(theCategory);
        }
    }
}
