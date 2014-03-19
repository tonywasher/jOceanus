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
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.DepositCategoryClass;
import net.sourceforge.joceanus.jmoneywise.newanalysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.newanalysis.DepositBucket;
import net.sourceforge.joceanus.jmoneywise.newanalysis.DepositBucket.DepositBucketList;
import net.sourceforge.joceanus.jmoneywise.newanalysis.DepositCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.newanalysis.DepositCategoryBucket.DepositCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.views.NewAnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.NewAnalysisFilter.DepositFilter;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.ArrowIcon;
import net.sourceforge.joceanus.jtethys.swing.JScrollMenu;
import net.sourceforge.joceanus.jtethys.swing.JScrollPopupMenu;

/**
 * Deposit Analysis Selection.
 */
public class DepositAnalysisSelect
        extends JEventPanel
        implements AnalysisFilterSelection {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 4447175135483840139L;

    /**
     * Text for Category Label.
     */
    private static final String NLS_CATEGORY = MoneyWiseDataType.DEPOSITCATEGORY.getItemName();

    /**
     * Text for Deposit Label.
     */
    private static final String NLS_DEPOSIT = MoneyWiseDataType.DEPOSIT.getItemName();

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
     * The deposit button.
     */
    private final JButton theDepositButton;

    /**
     * The category button.
     */
    private final JButton theCatButton;

    @Override
    public DepositFilter getFilter() {
        return new DepositFilter(theState.getDeposit());
    }

    @Override
    public boolean isAvailable() {
        return (theDeposits != null) && !theDeposits.isEmpty();
    }

    /**
     * Constructor.
     */
    public DepositAnalysisSelect() {
        /* Create the deposit button */
        theDepositButton = new JButton(ArrowIcon.DOWN);
        theDepositButton.setVerticalTextPosition(AbstractButton.CENTER);
        theDepositButton.setHorizontalTextPosition(AbstractButton.LEFT);

        /* Create the category button */
        theCatButton = new JButton(ArrowIcon.DOWN);
        theCatButton.setVerticalTextPosition(AbstractButton.CENTER);
        theCatButton.setHorizontalTextPosition(AbstractButton.LEFT);

        /* Create the labels */
        JLabel myCatLabel = new JLabel(NLS_CATEGORY);
        JLabel myDepLabel = new JLabel(NLS_DEPOSIT);

        /* Define the layout */
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createHorizontalGlue());
        add(myCatLabel);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        add(theCatButton);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE << 2, 0)));
        add(myDepLabel);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        add(theDepositButton);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));

        /* Create initial state */
        theState = new DepositState();
        theState.applyState();

        /* Create the listener */
        DepositListener myListener = new DepositListener();
        theDepositButton.addActionListener(myListener);
        theCatButton.addActionListener(myListener);
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
            myDeposit = theDeposits.findItemById(myDeposit.getOrderedId());
        }

        /* If we do not have an active bucket and the list is non-empty */
        if ((myDeposit == null) && (!theDeposits.isEmpty())) {
            /* Check for an account in the same category */
            DepositCategory myCategory = theState.getCategory();
            DepositCategoryBucket myCatBucket = (myCategory == null)
                                                                    ? null
                                                                    : theCategories.findItemById(myCategory.getId());

            /* If the category no longer exists */
            if (myCatBucket == null) {
                /* Access the first category */
                myCatBucket = theCategories.peekFirst();
                myCategory = myCatBucket.getAccountCategory();
            }

            /* Use the first deposit for category */
            myDeposit = getFirstDeposit(myCategory);
        }

        /* Set the account */
        theState.setDeposit(myDeposit);
        theState.applyState();
    }

    @Override
    public void setFilter(final NewAnalysisFilter<?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof DepositFilter) {
            /* Access filter */
            DepositFilter myFilter = (DepositFilter) pFilter;

            /* Obtain the filter bucket */
            DepositBucket myDeposit = myFilter.getBucket();

            /* Obtain equivalent bucket */
            myDeposit = theDeposits.findItemById(myDeposit.getOrderedId());

            /* Set the deposit */
            theState.setDeposit(myDeposit);
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
    private final class DepositListener
            implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent evt) {
            /* Access source of the event */
            Object o = evt.getSource();

            /* Handle buttons */
            if (theCatButton.equals(o)) {
                showCategoryMenu();
            } else if (theDepositButton.equals(o)) {
                showAccountMenu();
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
            Iterator<DepositCategoryBucket> myIterator = theCategories.iterator();
            while (myIterator.hasNext()) {
                DepositCategoryBucket myBucket = myIterator.next();

                /* Only process parent items */
                if (!myBucket.getAccountCategory().isCategoryClass(DepositCategoryClass.PARENT)) {
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
                DepositCategoryBucket myBucket = myIterator.next();

                /* Only process low-level items */
                if (myBucket.getAccountCategory().isCategoryClass(DepositCategoryClass.PARENT)) {
                    continue;
                }

                /* Determine menu to add to */
                DepositCategory myParent = myBucket.getAccountCategory().getParentCategory();
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
         * Show Account menu.
         */
        private void showAccountMenu() {
            /* Create a new popUp menu */
            JScrollPopupMenu myPopUp = new JScrollPopupMenu();

            /* Access current category and Deposit */
            DepositCategory myCategory = theState.getCategory();
            DepositBucket myDeposit = theState.getDeposit();

            /* Record active item */
            JMenuItem myActive = null;

            /* Loop through the available account values */
            Iterator<DepositBucket> myIterator = theDeposits.iterator();
            while (myIterator.hasNext()) {
                DepositBucket myBucket = myIterator.next();

                /* Ignore if not the correct category */
                if (!Difference.isEqual(myCategory, myBucket.getCategory())) {
                    continue;
                }

                /* Create a new JMenuItem and add it to the popUp */
                DepositAction myAction = new DepositAction(myBucket);
                JMenuItem myItem = new JMenuItem(myAction);
                myPopUp.addMenuItem(myItem);

                /* If this is the active deposit */
                if (myDeposit.equals(myBucket)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            myPopUp.showItem(myActive);

            /* Show the Deposit menu in the correct place */
            Rectangle myLoc = theDepositButton.getBounds();
            myPopUp.show(theDepositButton, 0, myLoc.height);
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
        private static final long serialVersionUID = 4648108947359234200L;

        /**
         * Category.
         */
        private final DepositCategory theCategory;

        /**
         * Constructor.
         * @param pCategory the category
         */
        private CategoryAction(final DepositCategory pCategory) {
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
     * Deposit action class.
     */
    private final class DepositAction
            extends AbstractAction {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -1615409703735943243L;

        /**
         * Deposit.
         */
        private final DepositBucket theDeposit;

        /**
         * Constructor.
         * @param pDeposit the deposit bucket
         */
        private DepositAction(final DepositBucket pDeposit) {
            super(pDeposit.getName());
            theDeposit = pDeposit;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* Select the new deposit */
            if (theState.setDeposit(theDeposit)) {
                theState.applyState();
                fireStateChanged();
            }
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
         * Set new Deposit.
         * @param pDeposit the Deposit
         * @return true/false did a change occur
         */
        private boolean setDeposit(final DepositBucket pDeposit) {
            /* Adjust the selected deposit */
            if (!Difference.isEqual(pDeposit, theDeposit)) {
                /* Store the deposit */
                theDeposit = pDeposit;

                /* Access category for account */
                theCategory = (theDeposit == null)
                                                  ? null
                                                  : theDeposit.getCategory();

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
        private boolean setCategory(final DepositCategory pCategory) {
            /* Adjust the selected category */
            if (!Difference.isEqual(pCategory, theCategory)) {
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
            theDepositButton.setText((theDeposit == null)
                                                         ? null
                                                         : theDeposit.getName());
            theCatButton.setText((theCategory == null)
                                                      ? null
                                                      : theCategory.getName());
        }
    }
}
