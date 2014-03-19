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
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jmoneywise.newanalysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.newanalysis.LoanBucket;
import net.sourceforge.joceanus.jmoneywise.newanalysis.LoanBucket.LoanBucketList;
import net.sourceforge.joceanus.jmoneywise.newanalysis.LoanCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.newanalysis.LoanCategoryBucket.LoanCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.views.NewAnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.NewAnalysisFilter.LoanFilter;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.ArrowIcon;
import net.sourceforge.joceanus.jtethys.swing.JScrollMenu;
import net.sourceforge.joceanus.jtethys.swing.JScrollPopupMenu;

/**
 * Loan Analysis Selection.
 */
public class LoanAnalysisSelect
        extends JEventPanel
        implements AnalysisFilterSelection {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 6479845850828034425L;

    /**
     * Text for Category Label.
     */
    private static final String NLS_CATEGORY = MoneyWiseDataType.LOANCATEGORY.getItemName();

    /**
     * Text for Loan Label.
     */
    private static final String NLS_LOAN = MoneyWiseDataType.LOAN.getItemName();

    /**
     * The active category bucket list.
     */
    private LoanCategoryBucketList theCategories;

    /**
     * The active loan bucket list.
     */
    private LoanBucketList theLoans;

    /**
     * The state.
     */
    private LoanState theState;

    /**
     * The savePoint.
     */
    private LoanState theSavePoint;

    /**
     * The loan button.
     */
    private final JButton theLoanButton;

    /**
     * The category button.
     */
    private final JButton theCatButton;

    @Override
    public LoanFilter getFilter() {
        return new LoanFilter(theState.getLoan());
    }

    @Override
    public boolean isAvailable() {
        return (theLoans != null) && !theLoans.isEmpty();
    }

    /**
     * Constructor.
     */
    public LoanAnalysisSelect() {
        /* Create the loan button */
        theLoanButton = new JButton(ArrowIcon.DOWN);
        theLoanButton.setVerticalTextPosition(AbstractButton.CENTER);
        theLoanButton.setHorizontalTextPosition(AbstractButton.LEFT);

        /* Create the category button */
        theCatButton = new JButton(ArrowIcon.DOWN);
        theCatButton.setVerticalTextPosition(AbstractButton.CENTER);
        theCatButton.setHorizontalTextPosition(AbstractButton.LEFT);

        /* Create the labels */
        JLabel myCatLabel = new JLabel(NLS_CATEGORY);
        JLabel myLoanLabel = new JLabel(NLS_LOAN);

        /* Define the layout */
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createHorizontalGlue());
        add(myCatLabel);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        add(theCatButton);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE << 2, 0)));
        add(myLoanLabel);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        add(theLoanButton);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));

        /* Create initial state */
        theState = new LoanState();
        theState.applyState();

        /* Create the listener */
        LoanListener myListener = new LoanListener();
        theLoanButton.addActionListener(myListener);
        theCatButton.addActionListener(myListener);
    }

    /**
     * Create SavePoint.
     */
    protected void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new LoanState(theState);
    }

    /**
     * Restore SavePoint.
     */
    protected void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new LoanState(theSavePoint);

        /* Apply the state */
        theState.applyState();
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* Determine whether there are any Loans to select */
        boolean lnAvailable = bEnabled && isAvailable();

        /* Pass call on to buttons */
        theLoanButton.setEnabled(lnAvailable);
        theCatButton.setEnabled(lnAvailable);
    }

    /**
     * Set analysis.
     * @param pAnalysis the analysis.
     */
    public void setAnalysis(final Analysis pAnalysis) {
        /* Access buckets */
        theCategories = pAnalysis.getLoanCategories();
        theLoans = pAnalysis.getLoans();

        /* Obtain the current account */
        LoanBucket myLoan = theState.getLoan();

        /* If we have a selected Loan */
        if (myLoan != null) {
            /* Look for the equivalent bucket */
            myLoan = theLoans.findItemById(myLoan.getOrderedId());
        }

        /* If we do not have an active bucket and the list is non-empty */
        if ((myLoan == null) && (!theLoans.isEmpty())) {
            /* Check for a loan in the same category */
            LoanCategory myCategory = theState.getCategory();
            LoanCategoryBucket myCatBucket = (myCategory == null)
                                                                 ? null
                                                                 : theCategories.findItemById(myCategory.getId());

            /* If the category no longer exists */
            if (myCatBucket == null) {
                /* Access the first category */
                myCatBucket = theCategories.peekFirst();
                myCategory = myCatBucket.getAccountCategory();
            }

            /* Use the first loan for category */
            myLoan = getFirstLoan(myCategory);
        }

        /* Set the loan */
        theState.setLoan(myLoan);
        theState.applyState();
    }

    @Override
    public void setFilter(final NewAnalysisFilter<?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof LoanFilter) {
            /* Access filter */
            LoanFilter myFilter = (LoanFilter) pFilter;

            /* Obtain the filter bucket */
            LoanBucket myLoan = myFilter.getBucket();

            /* Obtain equivalent bucket */
            myLoan = theLoans.findItemById(myLoan.getOrderedId());

            /* Set the loan */
            theState.setLoan(myLoan);
            theState.applyState();
        }
    }

    /**
     * Obtain first loan for category.
     * @param pCategory the category
     * @return the first account
     */
    private LoanBucket getFirstLoan(final LoanCategory pCategory) {
        /* Loop through the available loan values */
        Iterator<LoanBucket> myIterator = theLoans.iterator();
        while (myIterator.hasNext()) {
            LoanBucket myBucket = myIterator.next();

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
    private final class LoanListener
            implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent evt) {
            /* Access source of the event */
            Object o = evt.getSource();

            /* Handle buttons */
            if (theCatButton.equals(o)) {
                showCategoryMenu();
            } else if (theLoanButton.equals(o)) {
                showLoanMenu();
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
            Iterator<LoanCategoryBucket> myIterator = theCategories.iterator();
            while (myIterator.hasNext()) {
                LoanCategoryBucket myBucket = myIterator.next();

                /* Only process parent items */
                if (!myBucket.getAccountCategory().isCategoryClass(LoanCategoryClass.PARENT)) {
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
                LoanCategoryBucket myBucket = myIterator.next();

                /* Only process low-level items */
                if (myBucket.getAccountCategory().isCategoryClass(LoanCategoryClass.PARENT)) {
                    continue;
                }

                /* Determine menu to add to */
                LoanCategory myParent = myBucket.getAccountCategory().getParentCategory();
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
         * Show Loan menu.
         */
        private void showLoanMenu() {
            /* Create a new popUp menu */
            JScrollPopupMenu myPopUp = new JScrollPopupMenu();

            /* Access current category and Loan */
            LoanCategory myCategory = theState.getCategory();
            LoanBucket myLoan = theState.getLoan();

            /* Record active item */
            JMenuItem myActive = null;

            /* Loop through the available account values */
            Iterator<LoanBucket> myIterator = theLoans.iterator();
            while (myIterator.hasNext()) {
                LoanBucket myBucket = myIterator.next();

                /* Ignore if not the correct category */
                if (!Difference.isEqual(myCategory, myBucket.getCategory())) {
                    continue;
                }

                /* Create a new JMenuItem and add it to the popUp */
                LoanAction myAction = new LoanAction(myBucket);
                JMenuItem myItem = new JMenuItem(myAction);
                myPopUp.addMenuItem(myItem);

                /* If this is the active loan */
                if (myLoan.equals(myBucket)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            myPopUp.showItem(myActive);

            /* Show the Loan menu in the correct place */
            Rectangle myLoc = theLoanButton.getBounds();
            myPopUp.show(theLoanButton, 0, myLoc.height);
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
        private static final long serialVersionUID = -7865415601019352316L;

        /**
         * Category.
         */
        private final LoanCategory theCategory;

        /**
         * Constructor.
         * @param pCategory the category
         */
        private CategoryAction(final LoanCategory pCategory) {
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
     * Loan action class.
     */
    private final class LoanAction
            extends AbstractAction {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 2994073805894328142L;

        /**
         * Loan.
         */
        private final LoanBucket theLoan;

        /**
         * Constructor.
         * @param pLoan the loan bucket
         */
        private LoanAction(final LoanBucket pLoan) {
            super(pLoan.getName());
            theLoan = pLoan;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* Select the new loan */
            if (theState.setLoan(theLoan)) {
                theState.applyState();
                fireStateChanged();
            }
        }
    }

    /**
     * SavePoint values.
     */
    private final class LoanState {
        /**
         * The active Category.
         */
        private LoanCategory theCategory;

        /**
         * The active LoanBucket.
         */
        private LoanBucket theLoan;

        /**
         * Obtain the Loan Bucket.
         * @return the Loan
         */
        private LoanBucket getLoan() {
            return theLoan;
        }

        /**
         * Obtain the Category.
         * @return the category
         */
        private LoanCategory getCategory() {
            return theCategory;
        }

        /**
         * Constructor.
         */
        private LoanState() {
            /* Initialise the loan */
            theLoan = null;
            theCategory = null;
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private LoanState(final LoanState pState) {
            /* Initialise state */
            theLoan = pState.getLoan();
            theCategory = pState.getCategory();
        }

        /**
         * Set new Loan Account.
         * @param pLoan the Loan Account
         * @return true/false did a change occur
         */
        private boolean setLoan(final LoanBucket pLoan) {
            /* Adjust the selected loan */
            if (!Difference.isEqual(pLoan, theLoan)) {
                /* Store the loan */
                theLoan = pLoan;

                /* Access category for loan */
                theCategory = (theLoan == null)
                                               ? null
                                               : theLoan.getCategory();

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
        private boolean setCategory(final LoanCategory pCategory) {
            /* Adjust the selected category */
            if (!Difference.isEqual(pCategory, theCategory)) {
                theCategory = pCategory;
                theLoan = getFirstLoan(theCategory);
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
            theLoanButton.setText((theLoan == null)
                                                   ? null
                                                   : theLoan.getName());
            theCatButton.setText((theCategory == null)
                                                      ? null
                                                      : theCategory.getName());
        }
    }
}
