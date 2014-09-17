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
package net.sourceforge.joceanus.jmoneywise.ui.controls;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.CashBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.CashBucket.CashBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.CashCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.CashCategoryBucket.CashCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.CashFilter;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.swing.JScrollMenu;

/**
 * Cash Analysis Selection.
 */
public class CashAnalysisSelect
        extends JEventPanel
        implements AnalysisFilterSelection {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 3458135144597888214L;

    /**
     * Text for Category Label.
     */
    private static final String NLS_CATEGORY = MoneyWiseDataType.CASHCATEGORY.getItemName();

    /**
     * Text for Account Label.
     */
    private static final String NLS_CASH = MoneyWiseDataType.CASH.getItemName();

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
    private final JScrollButton<CashBucket> theCashButton;

    /**
     * The category button.
     */
    private final JScrollButton<CashCategory> theCatButton;

    @Override
    public CashFilter getFilter() {
        return new CashFilter(theState.getCash());
    }

    @Override
    public boolean isAvailable() {
        return (theCash != null) && !theCash.isEmpty();
    }

    /**
     * Constructor.
     */
    public CashAnalysisSelect() {
        /* Create the cash button */
        theCashButton = new JScrollButton<CashBucket>();

        /* Create the category button */
        theCatButton = new JScrollButton<CashCategory>();

        /* Create the labels */
        JLabel myCatLabel = new JLabel(NLS_CATEGORY);
        JLabel myCshLabel = new JLabel(NLS_CASH);

        /* Define the layout */
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createHorizontalGlue());
        add(myCatLabel);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        add(theCatButton);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE << 2, 0)));
        add(myCshLabel);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        add(theCashButton);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));

        /* Create initial state */
        theState = new CashState();
        theState.applyState();

        /* Create the listener */
        CashListener myListener = new CashListener();
        theCashButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, myListener);
        theCatButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, myListener);
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
            myCash = theCash.findItemById(myCash.getOrderedId());
        }

        /* If we do not have an active bucket and the list is non-empty */
        if ((myCash == null) && (!theCash.isEmpty())) {
            /* Check for an account in the same category */
            CashCategory myCategory = theState.getCategory();
            CashCategoryBucket myCatBucket = (myCategory == null)
                                                                 ? null
                                                                 : theCategories.findItemById(myCategory.getId());

            /* If the category no longer exists */
            if (myCatBucket == null) {
                /* Access the first category */
                myCatBucket = theCategories.peekFirst();
                myCategory = myCatBucket.getAccountCategory();
            }

            /* Use the first cash account for category */
            myCash = getFirstCash(myCategory);
        }

        /* Set the cash */
        theState.setCash(myCash);
        theState.applyState();
    }

    @Override
    public void setFilter(final AnalysisFilter<?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof CashFilter) {
            /* Access filter */
            CashFilter myFilter = (CashFilter) pFilter;

            /* Obtain the filter bucket */
            CashBucket myCash = myFilter.getBucket();

            /* Obtain equivalent bucket */
            myCash = theCash.findItemById(myCash.getOrderedId());

            /* Set the cash */
            theState.setCash(myCash);
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
            if (Difference.isEqual(pCategory, myBucket.getCategory())) {
                return myBucket;
            }
        }

        /* No such account */
        return null;
    }

    /**
     * Listener class.
     */
    private final class CashListener
            implements PropertyChangeListener, ChangeListener {
        /**
         * Category menu builder.
         */
        private final JScrollMenuBuilder<CashCategory> theCategoryMenuBuilder;

        /**
         * Cash menu builder.
         */
        private final JScrollMenuBuilder<CashBucket> theCashMenuBuilder;

        /**
         * Constructor.
         */
        private CashListener() {
            /* Access builders */
            theCategoryMenuBuilder = theCatButton.getMenuBuilder();
            theCategoryMenuBuilder.addChangeListener(this);
            theCashMenuBuilder = theCashButton.getMenuBuilder();
            theCashMenuBuilder.addChangeListener(this);
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            /* Access source of the event */
            Object o = pEvent.getSource();

            /* Handle buttons */
            if (theCategoryMenuBuilder.equals(o)) {
                buildCategoryMenu();
            } else if (theCashMenuBuilder.equals(o)) {
                buildCashMenu();
            }
        }

        /**
         * Build Category menu.
         */
        private void buildCategoryMenu() {
            /* Reset the popUp menu */
            theCategoryMenuBuilder.clearMenu();

            /* Create a simple map for top-level categories */
            Map<String, JScrollMenu> myMap = new HashMap<String, JScrollMenu>();

            /* Record active item */
            CashCategory myCurrent = theState.getCategory();
            JMenuItem myActive = null;

            /* Loop through the available category values */
            Iterator<CashCategoryBucket> myIterator = theCategories.iterator();
            while (myIterator.hasNext()) {
                CashCategoryBucket myBucket = myIterator.next();

                /* Only process parent items */
                if (!myBucket.getAccountCategory().isCategoryClass(CashCategoryClass.PARENT)) {
                    continue;
                }

                /* Create a new JMenu and add it to the popUp */
                String myName = myBucket.getName();
                JScrollMenu myMenu = theCategoryMenuBuilder.addSubMenu(myName);
                myMap.put(myName, myMenu);
            }

            /* Re-Loop through the available category values */
            myIterator = theCategories.iterator();
            while (myIterator.hasNext()) {
                CashCategoryBucket myBucket = myIterator.next();

                /* Only process low-level items */
                if (myBucket.getAccountCategory().isCategoryClass(CashCategoryClass.PARENT)) {
                    continue;
                }

                /* Determine menu to add to */
                CashCategory myParent = myBucket.getAccountCategory().getParentCategory();
                JScrollMenu myMenu = myMap.get(myParent.getName());

                /* Create a new JMenuItem and add it to the popUp */
                CashCategory myCategory = myBucket.getAccountCategory();
                JMenuItem myItem = theCategoryMenuBuilder.addItem(myMenu, myCategory, myCategory.getSubCategory());

                /* If this is the active category */
                if (myCategory.equals(myCurrent)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            theCategoryMenuBuilder.showItem(myActive);
        }

        /**
         * Build Cash menu.
         */
        private void buildCashMenu() {
            /* Reset the popUp menu */
            theCashMenuBuilder.clearMenu();

            /* Access current category and Account */
            CashCategory myCategory = theState.getCategory();
            CashBucket myCash = theState.getCash();

            /* Record active item */
            JMenuItem myActive = null;

            /* Loop through the available account values */
            Iterator<CashBucket> myIterator = theCash.iterator();
            while (myIterator.hasNext()) {
                CashBucket myBucket = myIterator.next();

                /* Ignore if not the correct category */
                if (!Difference.isEqual(myCategory, myBucket.getCategory())) {
                    continue;
                }

                /* Create a new JMenuItem and add it to the popUp */
                JMenuItem myItem = theCashMenuBuilder.addItem(myBucket);

                /* If this is the active cash */
                if (myBucket.equals(myCash)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            theCashMenuBuilder.showItem(myActive);
        }

        @Override
        public void propertyChange(final PropertyChangeEvent pEvent) {
            /* Access the source */
            Object o = pEvent.getSource();

            /* If this is the category button */
            if (theCatButton.equals(o)) {
                /* Select the new category */
                if (theState.setCategory(theCatButton.getValue())) {
                    theState.applyState();
                    fireStateChanged();
                }
            }

            /* If this is the cash button */
            if (theCashButton.equals(o)) {
                /* Select the new cash */
                if (theState.setCash(theCashButton.getValue())) {
                    theState.applyState();
                    fireStateChanged();
                }
            }
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
         * Set new Cash Account.
         * @param pCash the Cash Account
         * @return true/false did a change occur
         */
        private boolean setCash(final CashBucket pCash) {
            /* Adjust the selected cash */
            if (!Difference.isEqual(pCash, theCash)) {
                /* Store the cash */
                theCash = pCash;

                /* Access category for cash */
                theCategory = (theCash == null)
                                               ? null
                                               : theCash.getCategory();

                /* We have changed */
                return true;
            }
            return false;
        }

        /**
         * Set new Category.
         * @param pCategory the Category
         * @return true/false did a change occur
         */
        private boolean setCategory(final CashCategory pCategory) {
            /* Adjust the selected category */
            if (!Difference.isEqual(pCategory, theCategory)) {
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
