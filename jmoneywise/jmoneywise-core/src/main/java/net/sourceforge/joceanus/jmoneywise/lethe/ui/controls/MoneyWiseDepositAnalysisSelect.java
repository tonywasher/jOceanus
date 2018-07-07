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
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.DepositBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.DepositBucket.DepositBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.DepositCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.DepositCategoryBucket.DepositCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.DepositCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter.DepositFilter;
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
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollSubMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Deposit Analysis Selection.
 */
public class MoneyWiseDepositAnalysisSelect
        implements MoneyWiseAnalysisFilterSelection, TethysEventProvider<PrometheusDataEvent> {
    /**
     * Text for Category Label.
     */
    private static final String NLS_CATEGORY = MoneyWiseDataType.DEPOSITCATEGORY.getItemName();

    /**
     * Text for Deposit Label.
     */
    private static final String NLS_DEPOSIT = MoneyWiseDataType.DEPOSIT.getItemName();

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final TethysBoxPaneManager thePanel;

    /**
     * The deposit button.
     */
    private final TethysScrollButtonManager<DepositBucket> theDepositButton;

    /**
     * The category button.
     */
    private final TethysScrollButtonManager<DepositCategory> theCatButton;

    /**
     * Category menu.
     */
    private final TethysScrollMenu<DepositCategory> theCategoryMenu;

    /**
     * Deposit menu.
     */
    private final TethysScrollMenu<DepositBucket> theDepositMenu;

    /**
     * The active category bucket list.
     */
    private DepositCategoryBucketList theCategories;

    /**
     * The active deposit bucket list.
     */
    private DepositBucketList theDeposits;

    /**
     * The state.
     */
    private DepositState theState;

    /**
     * The savePoint.
     */
    private DepositState theSavePoint;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected MoneyWiseDepositAnalysisSelect(final TethysGuiFactory pFactory) {
        /* Create the deposit button */
        theDepositButton = pFactory.newScrollButton();

        /* Create the category button */
        theCatButton = pFactory.newScrollButton();

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Create the labels */
        final TethysLabel myCatLabel = pFactory.newLabel(NLS_CATEGORY + TethysLabel.STR_COLON);
        final TethysLabel myDepLabel = pFactory.newLabel(NLS_DEPOSIT + TethysLabel.STR_COLON);

        /* Define the layout */
        thePanel = pFactory.newHBoxPane();
        thePanel.addSpacer();
        thePanel.addNode(myCatLabel);
        thePanel.addNode(theCatButton);
        thePanel.addStrut();
        thePanel.addNode(myDepLabel);
        thePanel.addNode(theDepositButton);

        /* Create initial state */
        theState = new DepositState();
        theState.applyState();

        /* Access the menus */
        theCategoryMenu = theCatButton.getMenu();
        theDepositMenu = theDepositButton.getMenu();

        /* Create the listeners */
        TethysEventRegistrar<TethysUIEvent> myRegistrar = theCatButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewCategory());
        theCatButton.setMenuConfigurator(e -> buildCategoryMenu());
        myRegistrar = theDepositButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewDeposit());
        theDepositButton.setMenuConfigurator(e -> buildDepositMenu());
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
    public DepositFilter getFilter() {
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
        theSavePoint = new DepositState(theState);
    }

    /**
     * Restore SavePoint.
     */
    protected void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new DepositState(theSavePoint);

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
        theCategories = pAnalysis.getDepositCategories();
        theDeposits = pAnalysis.getDeposits();

        /* Obtain the current deposit */
        DepositBucket myDeposit = theState.getDeposit();

        /* Switch to versions from the analysis */
        myDeposit = myDeposit != null
                                      ? theDeposits.getMatchingDeposit(myDeposit.getAccount())
                                      : theDeposits.getDefaultDeposit();

        /* Set the deposit */
        theState.setTheDeposit(myDeposit);
        theState.applyState();
    }

    @Override
    public void setFilter(final AnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof DepositFilter) {
            /* Access filter */
            final DepositFilter myFilter = (DepositFilter) pFilter;

            /* Obtain the filter bucket */
            DepositBucket myDeposit = myFilter.getBucket();

            /* Obtain equivalent bucket */
            myDeposit = theDeposits.getMatchingDeposit(myDeposit.getAccount());

            /* Set the deposit */
            theState.setTheDeposit(myDeposit);
            theState.applyState();
        }
    }

    /**
     * Obtain the default Deposit for the category.
     * @param pCategory the category
     * @return the bucket
     */
    protected DepositBucket getDefaultDeposit(final DepositCategory pCategory) {
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
        final Map<String, TethysScrollSubMenu<DepositCategory>> myMap = new HashMap<>();

        /* Record active item */
        final DepositCategory myCurrent = theState.getCategory();
        TethysScrollMenuItem<DepositCategory> myActive = null;

        /* Re-Loop through the available category values */
        final Iterator<DepositCategoryBucket> myIterator = theCategories.iterator();
        while (myIterator.hasNext()) {
            final DepositCategoryBucket myBucket = myIterator.next();

            /* Only process low-level items */
            if (myBucket.getAccountCategory().isCategoryClass(DepositCategoryClass.PARENT)) {
                continue;
            }

            /* Determine menu to add to */
            final DepositCategory myParent = myBucket.getAccountCategory().getParentCategory();
            final String myParentName = myParent.getName();
            TethysScrollSubMenu<DepositCategory> myMenu = myMap.get(myParentName);

            /* If this is a new menu */
            if (myMenu == null) {
                /* Create a new JMenu and add it to the popUp */
                myMenu = theCategoryMenu.addSubMenu(myParentName);
                myMap.put(myParentName, myMenu);
            }

            /* Create a new JMenuItem and add it to the popUp */
            final DepositCategory myCategory = myBucket.getAccountCategory();
            final TethysScrollMenuItem<DepositCategory> myItem = myMenu.getSubMenu().addItem(myCategory, myCategory.getSubCategory());

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
        final DepositCategory myCategory = theState.getCategory();
        final DepositBucket myDeposit = theState.getDeposit();

        /* Record active item */
        TethysScrollMenuItem<DepositBucket> myActive = null;

        /* Loop through the available account values */
        final Iterator<DepositBucket> myIterator = theDeposits.iterator();
        while (myIterator.hasNext()) {
            final DepositBucket myBucket = myIterator.next();

            /* Ignore if not the correct category */
            if (!MetisDataDifference.isEqual(myCategory, myBucket.getCategory())) {
                continue;
            }

            /* Create a new JMenuItem and add it to the popUp */
            final TethysScrollMenuItem<DepositBucket> myItem = theDepositMenu.addItem(myBucket);

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
    private final class DepositState {
        /**
         * The active Category.
         */
        private DepositCategory theCategory;

        /**
         * The active DepositBucket.
         */
        private DepositBucket theDeposit;

        /**
         * The active Filter.
         */
        private DepositFilter theFilter;

        /**
         * Constructor.
         */
        private DepositState() {
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private DepositState(final DepositState pState) {
            /* Initialise state */
            theDeposit = pState.getDeposit();
            theCategory = pState.getCategory();
            theFilter = pState.getFilter();
        }

        /**
         * Obtain the Deposit Bucket.
         * @return the Deposit
         */
        private DepositBucket getDeposit() {
            return theDeposit;
        }

        /**
         * Obtain the Category.
         * @return the category
         */
        private DepositCategory getCategory() {
            return theCategory;
        }

        /**
         * Obtain the Filter.
         * @return the filter
         */
        private DepositFilter getFilter() {
            return theFilter;
        }

        /**
         * Set new Deposit.
         * @param pDeposit the Deposit
         * @return true/false did a change occur
         */
        private boolean setDeposit(final DepositBucket pDeposit) {
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
        private void setTheDeposit(final DepositBucket pDeposit) {
            /* Access category for account */
            final DepositCategory myCategory = pDeposit == null
                                                                ? null
                                                                : pDeposit.getCategory();
            setTheDeposit(myCategory, pDeposit);
        }

        /**
         * Set the Deposit.
         * @param pCategory the category
         * @param pDeposit the Deposit
         */
        private void setTheDeposit(final DepositCategory pCategory,
                                   final DepositBucket pDeposit) {
            /* Store the deposit */
            theDeposit = pDeposit;
            theCategory = pCategory;

            /* Access filter */
            theFilter = theDeposit != null
                                           ? new DepositFilter(theDeposit)
                                           : null;
        }

        /**
         * Set new Category.
         * @param pCategory the Category
         * @return true/false did a change occur
         */
        private boolean setCategory(final DepositCategory pCategory) {
            /* Adjust the selected category */
            if (!MetisDataDifference.isEqual(pCategory, theCategory)) {
                setTheDeposit(pCategory, getDefaultDeposit(pCategory));
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
            theDepositButton.setValue(theDeposit);
            theCatButton.setValue(theCategory);
        }
    }
}
