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
package net.sourceforge.joceanus.jmoneywise.atlas.ui.controls;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisCashBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisCashBucket.MoneyWiseAnalysisCashBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisCashCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisCashCategoryBucket.MoneyWiseAnalysisCashCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseCashCategory;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseCashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.atlas.views.MoneyWiseAnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.atlas.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisCashFilter;
import net.sourceforge.joceanus.jprometheus.atlas.views.PrometheusDataEvent;
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
 * Cash Analysis Selection.
 */
public class MoneyWiseCashAnalysisSelect
        implements MoneyWiseAnalysisFilterSelection, TethysEventProvider<PrometheusDataEvent> {
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
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * The cash button.
     */
    private final TethysUIScrollButtonManager<MoneyWiseAnalysisCashBucket> theCashButton;

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
    private final TethysUIScrollMenu<MoneyWiseAnalysisCashBucket> theCashMenu;

    /**
     * The active category bucket list.
     */
    private MoneyWiseAnalysisCashCategoryBucketList theCategories;

    /**
     * The active cash bucket list.
     */
    private MoneyWiseAnalysisCashBucketList theCash;

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
    protected MoneyWiseCashAnalysisSelect(final TethysUIFactory<?> pFactory) {
        /* Create the cash button */
        final TethysUIButtonFactory<?> myButtons = pFactory.buttonFactory();
        theCashButton = myButtons.newScrollButton(MoneyWiseAnalysisCashBucket.class);

        /* Create the category button */
        theCatButton = myButtons.newScrollButton(MoneyWiseCashCategory.class);

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

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
        TethysEventRegistrar<TethysUIEvent> myRegistrar = theCatButton.getEventRegistrar();
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
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public MoneyWiseAnalysisCashFilter getFilter() {
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
    public void setAnalysis(final MoneyWiseAnalysis pAnalysis) {
        /* Access buckets */
        theCategories = pAnalysis.getCashCategories();
        theCash = pAnalysis.getCash();

        /* Obtain the current cash */
        MoneyWiseAnalysisCashBucket myCash = theState.getCash();

        /* Switch to versions from the analysis */
        myCash = myCash != null
                ? theCash.getMatchingCash(myCash.getAccount())
                : theCash.getDefaultCash();

        /* Set the cash */
        theState.setTheCash(myCash);
        theState.applyState();
    }

    @Override
    public void setFilter(final MoneyWiseAnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof MoneyWiseAnalysisCashFilter) {
            /* Access filter */
            final MoneyWiseAnalysisCashFilter myFilter = (MoneyWiseAnalysisCashFilter) pFilter;

            /* Obtain the filter bucket */
            MoneyWiseAnalysisCashBucket myCash = myFilter.getBucket();

            /* Obtain equivalent bucket */
            myCash = theCash.getMatchingCash(myCash.getAccount());

            /* Set the cash */
            theState.setTheCash(myCash);
            theState.applyState();
        }
    }

    /**
     * Obtain the default Cash for the category.
     * @param pCategory the category
     * @return the bucket
     */
    protected MoneyWiseAnalysisCashBucket getDefaultCash(final MoneyWiseCashCategory pCategory) {
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
        final Iterator<MoneyWiseAnalysisCashCategoryBucket> myIterator = theCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisCashCategoryBucket myBucket = myIterator.next();

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
        final MoneyWiseAnalysisCashBucket myCash = theState.getCash();

        /* Record active item */
        TethysUIScrollItem<MoneyWiseAnalysisCashBucket> myActive = null;

        /* Loop through the available account values */
        final Iterator<MoneyWiseAnalysisCashBucket> myIterator = theCash.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisCashBucket myBucket = myIterator.next();

            /* Ignore if not the correct category */
            if (!MetisDataDifference.isEqual(myCategory, myBucket.getCategory())) {
                continue;
            }

            /* Create a new JMenuItem and add it to the popUp */
            final TethysUIScrollItem<MoneyWiseAnalysisCashBucket> myItem = theCashMenu.addItem(myBucket);

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
        private MoneyWiseAnalysisCashBucket theCash;

        /**
         * The active Filter.
         */
        private MoneyWiseAnalysisCashFilter theFilter;

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
            theFilter = pState.getFilter();
        }

        /**
         * Obtain the Cash Bucket.
         * @return the Cash
         */
        private MoneyWiseAnalysisCashBucket getCash() {
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
         * Obtain the Filter.
         * @return the filter
         */
        private MoneyWiseAnalysisCashFilter getFilter() {
            return theFilter;
        }

        /**
         * Set new Cash Account.
         * @param pCash the Cash Account
         * @return true/false did a change occur
         */
        private boolean setCash(final MoneyWiseAnalysisCashBucket pCash) {
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
        private void setTheCash(final MoneyWiseAnalysisCashBucket pCash) {
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
                                final MoneyWiseAnalysisCashBucket pCash) {
            /* Store the cash */
            theCash = pCash;
            theCategory = pCategory;

            /* Access filter */
            theFilter = theCash != null
                    ? new MoneyWiseAnalysisCashFilter(theCash)
                    : null;
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
