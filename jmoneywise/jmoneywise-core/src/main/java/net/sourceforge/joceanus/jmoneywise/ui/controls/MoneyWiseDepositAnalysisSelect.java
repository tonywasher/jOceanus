/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.DepositBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.DepositBucket.DepositBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.DepositCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.DepositCategoryBucket.DepositCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.DepositCategoryClass;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.DepositFilter;
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
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollSubMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Deposit Analysis Selection.
 * @param <N> the node type
 * @param <I> the Icon Type
 */
public class MoneyWiseDepositAnalysisSelect<N, I>
        implements MoneyWiseAnalysisFilterSelection<N>, TethysEventProvider<PrometheusDataEvent> {
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
    private final TethysBoxPaneManager<N, I> thePanel;

    /**
     * The deposit button.
     */
    private final TethysScrollButtonManager<DepositBucket, N, I> theDepositButton;

    /**
     * The category button.
     */
    private final TethysScrollButtonManager<DepositCategory, N, I> theCatButton;

    /**
     * Category menu.
     */
    private final TethysScrollMenu<DepositCategory, I> theCategoryMenu;

    /**
     * Deposit menu.
     */
    private final TethysScrollMenu<DepositBucket, I> theDepositMenu;

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
    protected MoneyWiseDepositAnalysisSelect(final TethysGuiFactory<N, I> pFactory) {
        /* Create the deposit button */
        theDepositButton = pFactory.newScrollButton();

        /* Create the category button */
        theCatButton = pFactory.newScrollButton();

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Create the labels */
        TethysLabel<N, I> myCatLabel = pFactory.newLabel(NLS_CATEGORY + TethysLabel.STR_COLON);
        TethysLabel<N, I> myDepLabel = pFactory.newLabel(NLS_DEPOSIT + TethysLabel.STR_COLON);

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
        myRegistrar.addEventListener(TethysUIEvent.PREPAREDIALOG, e -> buildCategoryMenu());
        myRegistrar = theDepositButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewDeposit());
        myRegistrar.addEventListener(TethysUIEvent.PREPAREDIALOG, e -> buildDepositMenu());
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
    public DepositFilter getFilter() {
        DepositBucket myDeposit = theState.getDeposit();
        return myDeposit != null
                                 ? new DepositFilter(myDeposit)
                                 : null;
    }

    @Override
    public boolean isAvailable() {
        return (theDeposits != null) && !theDeposits.isEmpty();
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
        boolean dpAvailable = bEnabled && isAvailable();

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

        /* If we have a selected Deposit */
        if (myDeposit != null) {
            /* Look for the equivalent bucket */
            myDeposit = getMatchingBucket(myDeposit);
        }

        /* If we do not have an active bucket and the list is non-empty */
        if ((myDeposit == null) && (!theDeposits.isEmpty())) {
            /* Check for an account in the same category */
            DepositCategory myCategory = theState.getCategory();
            DepositCategoryBucket myCatBucket = (myCategory == null)
                                                                     ? null
                                                                     : theCategories.findItemById(myCategory.getId());

            /* Determine the next deposit */
            myDeposit = (myCatBucket != null)
                                              ? getFirstDeposit(myCategory)
                                              : theDeposits.peekFirst();
        }

        /* Set the account */
        theState.setTheDeposit(myDeposit);
        theState.applyState();
    }

    @Override
    public void setFilter(final AnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof DepositFilter) {
            /* Access filter */
            DepositFilter myFilter = (DepositFilter) pFilter;

            /* Obtain the filter bucket */
            DepositBucket myDeposit = myFilter.getBucket();

            /* Obtain equivalent bucket */
            myDeposit = getMatchingBucket(myDeposit);

            /* Set the deposit */
            theState.setTheDeposit(myDeposit);
            theState.applyState();
        }
    }

    /**
     * Obtain first account for category.
     * @param pCategory the category
     * @return the first account
     */
    private DepositBucket getFirstDeposit(final DepositCategory pCategory) {
        /* Loop through the available account values */
        Iterator<DepositBucket> myIterator = theDeposits.iterator();
        while (myIterator.hasNext()) {
            DepositBucket myBucket = myIterator.next();

            /* Return if correct category */
            if (MetisDifference.isEqual(pCategory, myBucket.getCategory())) {
                return myBucket;
            }
        }

        /* No such account */
        return null;
    }

    /**
     * Obtain matching bucket.
     * @param pBucket the original bucket
     * @return the matching bucket
     */
    private DepositBucket getMatchingBucket(final DepositBucket pBucket) {
        /* Look up the matching DepositBucket */
        Deposit myDeposit = pBucket.getAccount();
        DepositBucket myBucket = theDeposits.findItemById(myDeposit.getOrderedId());

        /* If there is no such bucket in the analysis */
        if (myBucket == null) {
            /* Allocate an orphan bucket */
            myBucket = theDeposits.getOrphanBucket(myDeposit);
        }

        /* return the bucket */
        return myBucket;
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
        Map<String, TethysScrollSubMenu<DepositCategory, ?>> myMap = new HashMap<>();

        /* Record active item */
        DepositCategory myCurrent = theState.getCategory();
        TethysScrollMenuItem<DepositCategory> myActive = null;

        /* Re-Loop through the available category values */
        Iterator<DepositCategoryBucket> myIterator = theCategories.iterator();
        while (myIterator.hasNext()) {
            DepositCategoryBucket myBucket = myIterator.next();

            /* Only process low-level items */
            if (myBucket.getAccountCategory().isCategoryClass(DepositCategoryClass.PARENT)) {
                continue;
            }

            /* Determine menu to add to */
            DepositCategory myParent = myBucket.getAccountCategory().getParentCategory();
            String myParentName = myParent.getName();
            TethysScrollSubMenu<DepositCategory, ?> myMenu = myMap.get(myParentName);

            /* If this is a new menu */
            if (myMenu == null) {
                /* Create a new JMenu and add it to the popUp */
                myMenu = theCategoryMenu.addSubMenu(myParentName);
                myMap.put(myParentName, myMenu);
            }

            /* Create a new JMenuItem and add it to the popUp */
            DepositCategory myCategory = myBucket.getAccountCategory();
            TethysScrollMenuItem<DepositCategory> myItem = myMenu.getSubMenu().addItem(myCategory, myCategory.getSubCategory());

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
        DepositCategory myCategory = theState.getCategory();
        DepositBucket myDeposit = theState.getDeposit();

        /* Record active item */
        TethysScrollMenuItem<DepositBucket> myActive = null;

        /* Loop through the available account values */
        Iterator<DepositBucket> myIterator = theDeposits.iterator();
        while (myIterator.hasNext()) {
            DepositBucket myBucket = myIterator.next();

            /* Ignore if not the correct category */
            if (!MetisDifference.isEqual(myCategory, myBucket.getCategory())) {
                continue;
            }

            /* Create a new JMenuItem and add it to the popUp */
            TethysScrollMenuItem<DepositBucket> myItem = theDepositMenu.addItem(myBucket);

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
         * Constructor.
         */
        private DepositState() {
            /* Initialise the deposit */
            theDeposit = null;
            theCategory = null;
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private DepositState(final DepositState pState) {
            /* Initialise state */
            theDeposit = pState.getDeposit();
            theCategory = pState.getCategory();
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
         * Set new Deposit.
         * @param pDeposit the Deposit
         * @return true/false did a change occur
         */
        private boolean setDeposit(final DepositBucket pDeposit) {
            /* Adjust the selected deposit */
            if (!MetisDifference.isEqual(pDeposit, theDeposit)) {
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
            /* Store the deposit */
            theDeposit = pDeposit;

            /* Access category for account */
            theCategory = (theDeposit == null)
                                               ? null
                                               : theDeposit.getCategory();
        }

        /**
         * Set new Category.
         * @param pCategory the Category
         * @return true/false did a change occur
         */
        private boolean setCategory(final DepositCategory pCategory) {
            /* Adjust the selected category */
            if (!MetisDifference.isEqual(pCategory, theCategory)) {
                theCategory = pCategory;
                theDeposit = getFirstDeposit(theCategory);
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
