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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.newanalysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.newanalysis.CashBucket;
import net.sourceforge.joceanus.jmoneywise.newanalysis.CashBucket.CashBucketList;
import net.sourceforge.joceanus.jmoneywise.newanalysis.CashCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.newanalysis.CashCategoryBucket.CashCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.views.NewAnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.NewAnalysisFilter.CashFilter;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.ArrowIcon;
import net.sourceforge.joceanus.jtethys.swing.JScrollMenu;
import net.sourceforge.joceanus.jtethys.swing.JScrollPopupMenu;

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
    private final JButton theCashButton;

    /**
     * The category button.
     */
    private final JButton theCatButton;

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
        /* Create the account button */
        theCashButton = new JButton(ArrowIcon.DOWN);
        theCashButton.setVerticalTextPosition(AbstractButton.CENTER);
        theCashButton.setHorizontalTextPosition(AbstractButton.LEFT);

        /* Create the category button */
        theCatButton = new JButton(ArrowIcon.DOWN);
        theCatButton.setVerticalTextPosition(AbstractButton.CENTER);
        theCatButton.setHorizontalTextPosition(AbstractButton.LEFT);

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
        theCashButton.addActionListener(myListener);
        theCatButton.addActionListener(myListener);
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
    public void setFilter(final NewAnalysisFilter<?> pFilter) {
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

            /* Ignore if not the correct category */
            if (!Difference.isEqual(pCategory, myBucket.getCategory())) {
                continue;
            }

            /* Return the bucket */
            return myBucket;
        }

        /* No such account */
        return null;
    }

    /**
     * Listener class.
     */
    private final class CashListener
            implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent evt) {
            /* Access source of the event */
            Object o = evt.getSource();

            /* Handle buttons */
            if (theCatButton.equals(o)) {
                showCategoryMenu();
            } else if (theCashButton.equals(o)) {
                showCashMenu();
            }
        }

        /**
         * Show Category menu.
         */
        private void showCategoryMenu() {
            /* Create a new popUp menu */
            JScrollPopupMenu myPopUp = new JScrollPopupMenu();

            /* Create a simple map for top-level categories */
            Map<String, JScrollMenu> myMap = new HashMap<String, JScrollMenu>();

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
                JScrollMenu myMenu = new JScrollMenu(myName);
                myMap.put(myName, myMenu);
                myPopUp.addMenuItem(myMenu);
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
                CategoryAction myAction = new CategoryAction(myBucket.getAccountCategory());
                JMenuItem myItem = new JMenuItem(myAction);
                myMenu.addMenuItem(myItem);
            }

            /* Show the Category menu in the correct place */
            Rectangle myLoc = theCatButton.getBounds();
            myPopUp.show(theCatButton, 0, myLoc.height);
        }

        /**
         * Show Cash menu.
         */
        private void showCashMenu() {
            /* Create a new popUp menu */
            JScrollPopupMenu myPopUp = new JScrollPopupMenu();

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
                CashAction myAction = new CashAction(myBucket);
                JMenuItem myItem = new JMenuItem(myAction);
                myPopUp.addMenuItem(myItem);

                /* If this is the active cash account */
                if (myCash.equals(myBucket)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            myPopUp.showItem(myActive);

            /* Show the Cash menu in the correct place */
            Rectangle myLoc = theCashButton.getBounds();
            myPopUp.show(theCashButton, 0, myLoc.height);
        }
    }

    /**
     * Category action class.
     */
    private final class CategoryAction
            extends AbstractAction {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 227719516993430947L;

        /**
         * Category.
         */
        private final CashCategory theCategory;

        /**
         * Constructor.
         * @param pCategory the category
         */
        private CategoryAction(final CashCategory pCategory) {
            super(pCategory.getSubCategory());
            theCategory = pCategory;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* Select the new category */
            if (theState.setCategory(theCategory)) {
                theState.applyState();
                fireStateChanged();
            }
        }
    }

    /**
     * Cash action class.
     */
    private final class CashAction
            extends AbstractAction {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -2762120402812149795L;

        /**
         * Cash.
         */
        private final CashBucket theCash;

        /**
         * Constructor.
         * @param pCash the cash bucket
         */
        private CashAction(final CashBucket pCash) {
            super(pCash.getName());
            theCash = pCash;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* Select the new cash */
            if (theState.setCash(theCash)) {
                theState.applyState();
                fireStateChanged();
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
            theCashButton.setText((theCash == null)
                                                   ? null
                                                   : theCash.getName());
            theCatButton.setText((theCategory == null)
                                                      ? null
                                                      : theCategory.getName());
        }
    }
}
