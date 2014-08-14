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
import net.sourceforge.joceanus.jmoneywise.analysis.LoanBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.LoanBucket.LoanBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.LoanCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.LoanCategoryBucket.LoanCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.LoanFilter;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.swing.JScrollMenu;

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
    private final JScrollButton<LoanBucket> theLoanButton;

    /**
     * The category button.
     */
    private final JScrollButton<LoanCategory> theCatButton;

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
        theLoanButton = new JScrollButton<LoanBucket>();

        /* Create the category button */
        theCatButton = new JScrollButton<LoanCategory>();

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
        theLoanButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, myListener);
        theCatButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, myListener);
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
    public void setFilter(final AnalysisFilter<?> pFilter) {
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
            implements PropertyChangeListener, ChangeListener {
        /**
         * Category menu builder.
         */
        private final JScrollMenuBuilder<LoanCategory> theCategoryMenuBuilder;

        /**
         * Loan menu builder.
         */
        private final JScrollMenuBuilder<LoanBucket> theLoanMenuBuilder;

        /**
         * Constructor.
         */
        private LoanListener() {
            /* Access builders */
            theCategoryMenuBuilder = theCatButton.getMenuBuilder();
            theCategoryMenuBuilder.addChangeListener(this);
            theLoanMenuBuilder = theLoanButton.getMenuBuilder();
            theLoanMenuBuilder.addChangeListener(this);
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            /* Access source of the event */
            Object o = pEvent.getSource();

            /* Handle buttons */
            if (theCategoryMenuBuilder.equals(o)) {
                buildCategoryMenu();
            } else if (theLoanMenuBuilder.equals(o)) {
                buildLoanMenu();
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
            LoanCategory myCurrent = theState.getCategory();
            JMenuItem myActive = null;

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
                JScrollMenu myMenu = theCategoryMenuBuilder.addSubMenu(myName);
                myMap.put(myName, myMenu);
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
                LoanCategory myCategory = myBucket.getAccountCategory();
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
         * Build Loan menu.
         */
        private void buildLoanMenu() {
            /* Reset the popUp menu */
            theLoanMenuBuilder.clearMenu();

            /* Access current category and Loan */
            LoanCategory myCategory = theState.getCategory();

            /* Loop through the available account values */
            Iterator<LoanBucket> myIterator = theLoans.iterator();
            while (myIterator.hasNext()) {
                LoanBucket myBucket = myIterator.next();

                /* Ignore if not the correct category */
                if (!Difference.isEqual(myCategory, myBucket.getCategory())) {
                    continue;
                }

                /* Create a new JMenuItem and add it to the popUp */
                theLoanMenuBuilder.addItem(myBucket);
            }
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

            /* If this is the loan button */
            if (theLoanButton.equals(o)) {
                /* Select the new loan */
                if (theState.setLoan(theLoanButton.getValue())) {
                    theState.applyState();
                    fireStateChanged();
                }
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
            theLoanButton.setValue(theLoan);
            theCatButton.setValue(theCategory);
        }
    }
}
