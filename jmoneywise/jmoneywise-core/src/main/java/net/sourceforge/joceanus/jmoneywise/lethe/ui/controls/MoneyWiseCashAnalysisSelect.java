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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.CashBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.CashBucket.CashBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.CashCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.CashCategoryBucket.CashCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.CashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter.CashFilter;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollSubMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Cash Analysis Selection.
 * @param <N> the node type
 * @param <I> the Icon Type
 */
public class MoneyWiseCashAnalysisSelect<N, I>
        implements MoneyWiseAnalysisFilterSelection<N>, TethysEventProvider<PrometheusDataEvent> {
    /**
     * Text for Category Label.
     */
    private static final String NLS_CATEGORY = MoneyWiseDataType.CASHCATEGORY.getItemName();

    /**
     * Text for Account Label.
     */
    private static final String NLS_CASH = MoneyWiseDataType.CASH.getItemName();

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final TethysBoxPaneManager<N, I> thePanel;

    /**
     * The cash button.
     */
    private final TethysScrollButtonManager<CashBucket, N, I> theCashButton;

    /**
     * The category button.
     */
    private final TethysScrollButtonManager<CashCategory, N, I> theCatButton;

    /**
     * Category menu.
     */
    private final TethysScrollMenu<CashCategory, I> theCategoryMenu;

    /**
     * Cash menu.
     */
    private final TethysScrollMenu<CashBucket, I> theCashMenu;

    /**
     * The active category bucket list.
     */
    private CashCategoryBucketList theCategories;

    /**
     * The active cash bucket list.
     */
    private CashBucketList theCash;

    /**
     * The state.
     */
    private CashState theState;

    /**
     * The savePoint.
     */
    private CashState theSavePoint;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected MoneyWiseCashAnalysisSelect(final TethysGuiFactory<N, I> pFactory) {
        /* Create the cash button */
        theCashButton = pFactory.newScrollButton();

        /* Create the category button */
        theCatButton = pFactory.newScrollButton();

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Create the labels */
        final TethysLabel<N, I> myCatLabel = pFactory.newLabel(NLS_CATEGORY + TethysLabel.STR_COLON);
        final TethysLabel<N, I> myCshLabel = pFactory.newLabel(NLS_CASH + TethysLabel.STR_COLON);

        /* Define the layout */
        thePanel = pFactory.newHBoxPane();
        thePanel.addSpacer();
        thePanel.addNode(myCatLabel);
        thePanel.addNode(theCatButton);
        thePanel.addStrut();
        thePanel.addNode(myCshLabel);
        thePanel.addNode(theCashButton);

        /* Create initial state */
        theState = new CashState();
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
    public CashFilter getFilter() {
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
        theSavePoint = new CashState(theState);
    }

    /**
     * Restore SavePoint.
     */
    protected void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new CashState(theSavePoint);

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
    public void setAnalysis(final Analysis pAnalysis) {
        /* Access buckets */
        theCategories = pAnalysis.getCashCategories();
        theCash = pAnalysis.getCash();

        /* Obtain the current cash */
        CashBucket myCash = theState.getCash();

        /* Switch to versions from the analysis */
        myCash = myCash != null
                                ? theCash.getMatchingCash(myCash.getAccount())
                                : theCash.getDefaultCash();

        /* Set the cash */
        theState.setTheCash(myCash);
        theState.applyState();
    }

    @Override
    public void setFilter(final AnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof CashFilter) {
            /* Access filter */
            final CashFilter myFilter = (CashFilter) pFilter;

            /* Obtain the filter bucket */
            CashBucket myCash = myFilter.getBucket();

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
    protected CashBucket getDefaultCash(final CashCategory pCategory) {
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
        final Map<String, TethysScrollSubMenu<CashCategory, ?>> myMap = new HashMap<>();

        /* Record active item */
        final CashCategory myCurrent = theState.getCategory();
        TethysScrollMenuItem<CashCategory> myActive = null;

        /* Loop through the available category values */
        final Iterator<CashCategoryBucket> myIterator = theCategories.iterator();
        while (myIterator.hasNext()) {
            final CashCategoryBucket myBucket = myIterator.next();

            /* Only process low-level items */
            if (myBucket.getAccountCategory().isCategoryClass(CashCategoryClass.PARENT)) {
                continue;
            }

            /* Determine menu to add to */
            final CashCategory myParent = myBucket.getAccountCategory().getParentCategory();
            final String myParentName = myParent.getName();
            TethysScrollSubMenu<CashCategory, ?> myMenu = myMap.get(myParentName);

            /* If this is a new menu */
            if (myMenu == null) {
                /* Create a new JMenu and add it to the popUp */
                myMenu = theCategoryMenu.addSubMenu(myParentName);
                myMap.put(myParentName, myMenu);
            }

            /* Create a new JMenuItem and add it to the popUp */
            final CashCategory myCategory = myBucket.getAccountCategory();
            final TethysScrollMenuItem<CashCategory> myItem = myMenu.getSubMenu().addItem(myCategory, myCategory.getSubCategory());

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
        final CashCategory myCategory = theState.getCategory();
        final CashBucket myCash = theState.getCash();

        /* Record active item */
        TethysScrollMenuItem<CashBucket> myActive = null;

        /* Loop through the available account values */
        final Iterator<CashBucket> myIterator = theCash.iterator();
        while (myIterator.hasNext()) {
            final CashBucket myBucket = myIterator.next();

            /* Ignore if not the correct category */
            if (!MetisDataDifference.isEqual(myCategory, myBucket.getCategory())) {
                continue;
            }

            /* Create a new JMenuItem and add it to the popUp */
            final TethysScrollMenuItem<CashBucket> myItem = theCashMenu.addItem(myBucket);

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
    private final class CashState {
        /**
         * The active Category.
         */
        private CashCategory theCategory;

        /**
         * The active CashBucket.
         */
        private CashBucket theCash;

        /**
         * The active Filter.
         */
        private CashFilter theFilter;

        /**
         * Constructor.
         */
        private CashState() {
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private CashState(final CashState pState) {
            /* Initialise state */
            theCash = pState.getCash();
            theCategory = pState.getCategory();
            theFilter = pState.getFilter();
        }

        /**
         * Obtain the Cash Bucket.
         * @return the Cash
         */
        private CashBucket getCash() {
            return theCash;
        }

        /**
         * Obtain the Category.
         * @return the category
         */
        private CashCategory getCategory() {
            return theCategory;
        }

        /**
         * Obtain the Filter.
         * @return the filter
         */
        private CashFilter getFilter() {
            return theFilter;
        }

        /**
         * Set new Cash Account.
         * @param pCash the Cash Account
         * @return true/false did a change occur
         */
        private boolean setCash(final CashBucket pCash) {
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
        private void setTheCash(final CashBucket pCash) {
            /* Access category for account */
            final CashCategory myCategory = pCash == null
                                                          ? null
                                                          : pCash.getCategory();
            setTheCash(myCategory, pCash);
        }

        /**
         * Set the Cash.
         * @param pCategory the category
         * @param pCash the Cash
         */
        private void setTheCash(final CashCategory pCategory,
                                final CashBucket pCash) {
            /* Store the cash */
            theCash = pCash;
            theCategory = pCategory;

            /* Access filter */
            theFilter = theCash != null
                                        ? new CashFilter(theCash)
                                        : null;
        }

        /**
         * Set new Category.
         * @param pCategory the Category
         * @return true/false did a change occur
         */
        private boolean setCategory(final CashCategory pCategory) {
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
