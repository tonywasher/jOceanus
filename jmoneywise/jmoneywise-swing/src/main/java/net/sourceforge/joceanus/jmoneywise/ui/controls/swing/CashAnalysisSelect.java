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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldElement;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.CashBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.CashBucket.CashBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.CashCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.CashCategoryBucket.CashCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.data.Cash;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.CashFilter;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollSubMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButtonManager;

/**
 * Cash Analysis Selection.
 */
public class CashAnalysisSelect
        implements AnalysisFilterSelection<JComponent>, TethysEventProvider<PrometheusDataEvent> {
    /**
     * Text for Category Label.
     */
    private static final String NLS_CATEGORY = MoneyWiseDataType.CASHCATEGORY.getItemName();

    /**
     * Text for Account Label.
     */
    private static final String NLS_CASH = MoneyWiseDataType.CASH.getItemName();

    /**
     * Id.
     */
    private final Integer theId;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final JPanel thePanel;

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
     * The cash button.
     */
    private final TethysSwingScrollButtonManager<CashBucket> theCashButton;

    /**
     * The category button.
     */
    private final TethysSwingScrollButtonManager<CashCategory> theCatButton;

    /**
     * Category menu.
     */
    private final TethysScrollMenu<CashCategory, ?> theCategoryMenu;

    /**
     * Cash menu.
     */
    private final TethysScrollMenu<CashBucket, ?> theCashMenu;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    public CashAnalysisSelect(final TethysSwingGuiFactory pFactory) {
        /* Create the cash button */
        theCashButton = pFactory.newScrollButton();

        /* Create the category button */
        theCatButton = pFactory.newScrollButton();

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();
        theId = pFactory.getNextId();

        /* Create the labels */
        JLabel myCatLabel = new JLabel(NLS_CATEGORY + MetisFieldElement.STR_COLON);
        JLabel myCshLabel = new JLabel(NLS_CASH + MetisFieldElement.STR_COLON);

        /* Define the layout */
        thePanel = new JPanel();
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.X_AXIS));
        thePanel.add(Box.createHorizontalGlue());
        thePanel.add(myCatLabel);
        thePanel.add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        thePanel.add(theCatButton.getNode());
        thePanel.add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE << 2, 0)));
        thePanel.add(myCshLabel);
        thePanel.add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        thePanel.add(theCashButton.getNode());
        thePanel.add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));

        /* Create initial state */
        theState = new CashState();
        theState.applyState();

        /* Access the menus */
        theCategoryMenu = theCatButton.getMenu();
        theCashMenu = theCashButton.getMenu();

        /* Create the listeners */
        TethysEventRegistrar<TethysUIEvent> myRegistrar = theCatButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewCategory());
        myRegistrar.addEventListener(TethysUIEvent.PREPAREDIALOG, e -> buildCategoryMenu());
        myRegistrar = theCashButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewCash());
        myRegistrar.addEventListener(TethysUIEvent.PREPAREDIALOG, e -> buildCashMenu());
    }

    @Override
    public Integer getId() {
        return theId;
    }

    @Override
    public JComponent getNode() {
        return thePanel;
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public CashFilter getFilter() {
        CashBucket myCash = theState.getCash();
        return myCash != null
                              ? new CashFilter(myCash)
                              : null;
    }

    @Override
    public boolean isAvailable() {
        return (theCash != null) && !theCash.isEmpty();
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
        boolean csAvailable = bEnabled && isAvailable();

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

        /* Obtain the current account */
        CashBucket myCash = theState.getCash();

        /* If we have a selected Cash */
        if (myCash != null) {
            /* Look for the equivalent bucket */
            myCash = getMatchingBucket(myCash);
        }

        /* If we do not have an active bucket and the list is non-empty */
        if ((myCash == null) && (!theCash.isEmpty())) {
            /* Check for an account in the same category */
            CashCategory myCategory = theState.getCategory();
            CashCategoryBucket myCatBucket = (myCategory == null)
                                                                  ? null
                                                                  : theCategories.findItemById(myCategory.getId());

            /* Determine the next cash */
            myCash = (myCatBucket != null)
                                           ? getFirstCash(myCategory)
                                           : theCash.peekFirst();
        }

        /* Set the cash */
        theState.setTheCash(myCash);
        theState.applyState();
    }

    @Override
    public void setFilter(final AnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof CashFilter) {
            /* Access filter */
            CashFilter myFilter = (CashFilter) pFilter;

            /* Obtain the filter bucket */
            CashBucket myCash = myFilter.getBucket();

            /* Obtain equivalent bucket */
            myCash = getMatchingBucket(myCash);

            /* Set the cash */
            theState.setTheCash(myCash);
            theState.applyState();
        }
    }

    /**
     * Obtain first cash account for category.
     * @param pCategory the category
     * @return the first cash account
     */
    private CashBucket getFirstCash(final CashCategory pCategory) {
        /* Loop through the available account values */
        Iterator<CashBucket> myIterator = theCash.iterator();
        while (myIterator.hasNext()) {
            CashBucket myBucket = myIterator.next();

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
    private CashBucket getMatchingBucket(final CashBucket pBucket) {
        /* Look up the matching CashBucket */
        Cash myCash = pBucket.getAccount();
        CashBucket myBucket = theCash.findItemById(myCash.getOrderedId());

        /* If there is no such bucket in the analysis */
        if (myBucket == null) {
            /* Allocate an orphan bucket */
            myBucket = theCash.getOrphanBucket(myCash);
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
        Map<String, TethysScrollSubMenu<CashCategory, ?>> myMap = new HashMap<>();

        /* Record active item */
        CashCategory myCurrent = theState.getCategory();
        TethysScrollMenuItem<CashCategory> myActive = null;

        /* Loop through the available category values */
        Iterator<CashCategoryBucket> myIterator = theCategories.iterator();
        while (myIterator.hasNext()) {
            CashCategoryBucket myBucket = myIterator.next();

            /* Only process low-level items */
            if (myBucket.getAccountCategory().isCategoryClass(CashCategoryClass.PARENT)) {
                continue;
            }

            /* Determine menu to add to */
            CashCategory myParent = myBucket.getAccountCategory().getParentCategory();
            String myParentName = myParent.getName();
            TethysScrollSubMenu<CashCategory, ?> myMenu = myMap.get(myParentName);

            /* If this is a new menu */
            if (myMenu == null) {
                /* Create a new JMenu and add it to the popUp */
                myMenu = theCategoryMenu.addSubMenu(myParentName);
                myMap.put(myParentName, myMenu);
            }

            /* Create a new JMenuItem and add it to the popUp */
            CashCategory myCategory = myBucket.getAccountCategory();
            TethysScrollMenuItem<CashCategory> myItem = myMenu.getSubMenu().addItem(myCategory, myCategory.getSubCategory());

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
        CashCategory myCategory = theState.getCategory();
        CashBucket myCash = theState.getCash();

        /* Record active item */
        TethysScrollMenuItem<CashBucket> myActive = null;

        /* Loop through the available account values */
        Iterator<CashBucket> myIterator = theCash.iterator();
        while (myIterator.hasNext()) {
            CashBucket myBucket = myIterator.next();

            /* Ignore if not the correct category */
            if (!MetisDifference.isEqual(myCategory, myBucket.getCategory())) {
                continue;
            }

            /* Create a new JMenuItem and add it to the popUp */
            TethysScrollMenuItem<CashBucket> myItem = theCashMenu.addItem(myBucket);

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
         * Constructor.
         */
        private CashState() {
            /* Initialise the cash */
            theCash = null;
            theCategory = null;
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private CashState(final CashState pState) {
            /* Initialise state */
            theCash = pState.getCash();
            theCategory = pState.getCategory();
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
         * Set new Cash Account.
         * @param pCash the Cash Account
         * @return true/false did a change occur
         */
        private boolean setCash(final CashBucket pCash) {
            /* Adjust the selected cash */
            if (!MetisDifference.isEqual(pCash, theCash)) {
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
            /* Store the cash */
            theCash = pCash;

            /* Access category for account */
            theCategory = (theCash == null)
                                            ? null
                                            : theCash.getCategory();
        }

        /**
         * Set new Category.
         * @param pCategory the Category
         * @return true/false did a change occur
         */
        private boolean setCategory(final CashCategory pCategory) {
            /* Adjust the selected category */
            if (!MetisDifference.isEqual(pCategory, theCategory)) {
                theCategory = pCategory;
                theCash = getFirstCash(theCategory);
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
