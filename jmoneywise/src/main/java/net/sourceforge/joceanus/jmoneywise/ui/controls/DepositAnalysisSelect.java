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

import net.sourceforge.joceanus.jmetis.field.JFieldElement;
import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.DepositBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.DepositBucket.DepositBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.DepositCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.DepositCategoryBucket.DepositCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.DepositCategoryClass;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.DepositFilter;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.swing.JScrollMenu;

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
    private final JScrollButton<DepositBucket> theDepositButton;

    /**
     * The category button.
     */
    private final JScrollButton<DepositCategory> theCatButton;

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
     * Constructor.
     */
    public DepositAnalysisSelect() {
        /* Create the deposit button */
        theDepositButton = new JScrollButton<DepositBucket>();

        /* Create the category button */
        theCatButton = new JScrollButton<DepositCategory>();

        /* Create the labels */
        JLabel myCatLabel = new JLabel(NLS_CATEGORY + JFieldElement.STR_COLON);
        JLabel myDepLabel = new JLabel(NLS_DEPOSIT + JFieldElement.STR_COLON);

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
        theDepositButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, myListener);
        theCatButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, myListener);
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

            /* Determine the next deposit */
            myDeposit = (myCatBucket != null)
                                             ? getFirstDeposit(myCategory)
                                             : theDeposits.peekFirst();
        }

        /* Set the account */
        theState.setDeposit(myDeposit);
        theState.applyState();
    }

    @Override
    public void setFilter(final AnalysisFilter<?> pFilter) {
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
    private final class DepositListener
            implements PropertyChangeListener, ChangeListener {
        /**
         * Category menu builder.
         */
        private final JScrollMenuBuilder<DepositCategory> theCategoryMenuBuilder;

        /**
         * Deposit menu builder.
         */
        private final JScrollMenuBuilder<DepositBucket> theDepositMenuBuilder;

        /**
         * Constructor.
         */
        private DepositListener() {
            /* Access builders */
            theCategoryMenuBuilder = theCatButton.getMenuBuilder();
            theCategoryMenuBuilder.addChangeListener(this);
            theDepositMenuBuilder = theDepositButton.getMenuBuilder();
            theDepositMenuBuilder.addChangeListener(this);
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            /* Access source of the event */
            Object o = pEvent.getSource();

            /* Handle builders */
            if (theCategoryMenuBuilder.equals(o)) {
                buildCategoryMenu();
            } else if (theDepositMenuBuilder.equals(o)) {
                buildDepositMenu();
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
            DepositCategory myCurrent = theState.getCategory();
            JMenuItem myActive = null;

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
                JScrollMenu myMenu = theCategoryMenuBuilder.addSubMenu(myName);
                myMap.put(myName, myMenu);
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
                DepositCategory myCategory = myBucket.getAccountCategory();
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
         * Build Deposit menu.
         */
        private void buildDepositMenu() {
            /* Reset the popUp menu */
            theDepositMenuBuilder.clearMenu();

            /* Access current category */
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
                JMenuItem myItem = theDepositMenuBuilder.addItem(myBucket);

                /* If this is the active deposit */
                if (myBucket.equals(myDeposit)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            theDepositMenuBuilder.showItem(myActive);
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

            /* If this is the deposit button */
            if (theDepositButton.equals(o)) {
                /* Select the new deposit */
                if (theState.setDeposit(theDepositButton.getValue())) {
                    theState.applyState();
                    fireStateChanged();
                }
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
            theDepositButton.setValue(theDeposit);
            theCatButton.setValue(theCategory);
        }
    }
}
