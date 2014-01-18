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
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket.AccountBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountCategoryBucket.AccountCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.data.AccountCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryClass;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.AccountFilter;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.ArrowIcon;
import net.sourceforge.joceanus.jtethys.swing.JScrollMenu;
import net.sourceforge.joceanus.jtethys.swing.JScrollPopupMenu;

/**
 * Security Analysis Selection.
 */
public class AccountAnalysisSelect
        extends JEventPanel
        implements AnalysisFilterSelection {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 2264147825388154486L;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(AccountAnalysisSelect.class.getName());

    /**
     * Text for Category Label.
     */
    private static final String NLS_CATEGORY = NLS_BUNDLE.getString("Category");

    /**
     * Text for Account Label.
     */
    private static final String NLS_ACCOUNT = NLS_BUNDLE.getString("Account");

    /**
     * The active category bucket list.
     */
    private AccountCategoryBucketList theCategories;

    /**
     * The active account bucket list.
     */
    private AccountBucketList theAccounts;

    /**
     * The state.
     */
    private AccountState theState;

    /**
     * The savePoint.
     */
    private AccountState theSavePoint;

    /**
     * The account button.
     */
    private final JButton theAccountButton;

    /**
     * The category button.
     */
    private final JButton theCatButton;

    @Override
    public AccountFilter getFilter() {
        return new AccountFilter(theState.getAccount());
    }

    @Override
    public boolean isAvailable() {
        return (theAccounts != null)
               && !theAccounts.isEmpty();
    }

    /**
     * Constructor.
     */
    public AccountAnalysisSelect() {
        /* Create the account button */
        theAccountButton = new JButton(ArrowIcon.DOWN);
        theAccountButton.setVerticalTextPosition(AbstractButton.CENTER);
        theAccountButton.setHorizontalTextPosition(AbstractButton.LEFT);

        /* Create the category button */
        theCatButton = new JButton(ArrowIcon.DOWN);
        theCatButton.setVerticalTextPosition(AbstractButton.CENTER);
        theCatButton.setHorizontalTextPosition(AbstractButton.LEFT);

        /* Create the labels */
        JLabel myCatLabel = new JLabel(NLS_CATEGORY);
        JLabel myActLabel = new JLabel(NLS_ACCOUNT);

        /* Define the layout */
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        add(myCatLabel);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        add(theCatButton);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE << 2, 0)));
        add(myActLabel);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));
        add(theAccountButton);
        add(Box.createRigidArea(new Dimension(AnalysisSelect.STRUT_SIZE, 0)));

        /* Create initial state */
        theState = new AccountState();
        theState.applyState();

        /* Create the listener */
        AccountListener myListener = new AccountListener();
        theAccountButton.addActionListener(myListener);
        theCatButton.addActionListener(myListener);
    }

    /**
     * Create SavePoint.
     */
    protected void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new AccountState(theState);
    }

    /**
     * Restore SavePoint.
     */
    protected void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new AccountState(theSavePoint);

        /* Apply the state */
        theState.applyState();
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* Determine whether there are any Accounts to select */
        boolean acAvailable = bEnabled
                              && isAvailable();

        /* Pass call on to buttons */
        theAccountButton.setEnabled(acAvailable);
        theCatButton.setEnabled(acAvailable);
    }

    /**
     * Set analysis.
     * @param pAnalysis the analysis.
     */
    public void setAnalysis(final Analysis pAnalysis) {
        /* Access buckets */
        theCategories = pAnalysis.getAccountCategories();
        theAccounts = pAnalysis.getAccounts();

        /* Obtain the current account */
        AccountBucket myAccount = theState.getAccount();

        /* If we have a selected Account */
        if (myAccount != null) {
            /* Look for the equivalent bucket */
            myAccount = theAccounts.findItemById(myAccount.getOrderedId());
        }

        /* If we do not have an active bucket and the list is non-empty */
        if ((myAccount == null)
            && (!theAccounts.isEmpty())) {
            /* Check for an account in the same category */
            AccountCategory myCategory = theState.getCategory();
            AccountCategoryBucket myCatBucket = (myCategory == null)
                    ? null
                    : theCategories.findItemById(myCategory.getId());

            /* If the category no longer exists */
            if (myCatBucket == null) {
                /* Access the first category */
                myCatBucket = theCategories.peekFirst();
                myCategory = myCatBucket.getAccountCategory();
            }

            /* Use the first account for category */
            myAccount = getFirstAccount(myCategory);
        }

        /* Set the account */
        theState.setAccount(myAccount);
        theState.applyState();
    }

    @Override
    public void setFilter(final AnalysisFilter<?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof AccountFilter) {
            /* Access filter */
            AccountFilter myFilter = (AccountFilter) pFilter;

            /* Obtain the filter bucket */
            AccountBucket myAccount = myFilter.getBucket();

            /* Obtain equivalent bucket */
            myAccount = theAccounts.findItemById(myAccount.getOrderedId());

            /* Set the account */
            theState.setAccount(myAccount);
            theState.applyState();
        }
    }

    /**
     * Obtain first account for category.
     * @param pCategory the category
     * @return the first account
     */
    private AccountBucket getFirstAccount(final AccountCategory pCategory) {
        /* Loop through the available account values */
        Iterator<AccountBucket> myIterator = theAccounts.iterator();
        while (myIterator.hasNext()) {
            AccountBucket myBucket = myIterator.next();

            /* Ignore if not the correct portfolio */
            if (!Difference.isEqual(pCategory, myBucket.getAccountCategory())) {
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
    private final class AccountListener
            implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent evt) {
            /* Access source of the event */
            Object o = evt.getSource();

            /* Handle buttons */
            if (theCatButton.equals(o)) {
                showCategoryMenu();
            } else if (theAccountButton.equals(o)) {
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
            Iterator<AccountCategoryBucket> myIterator = theCategories.iterator();
            while (myIterator.hasNext()) {
                AccountCategoryBucket myBucket = myIterator.next();

                /* Only process subTotal items */
                AccountCategoryClass myClass = myBucket.getAccountCategory().getCategoryTypeClass();
                if (!myClass.isSubTotal()) {
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
                AccountCategoryBucket myBucket = myIterator.next();

                /* Only process low-level items */
                AccountCategoryClass myClass = myBucket.getAccountCategory().getCategoryTypeClass();
                if (myClass.canParentAccount()
                    || myClass.isParentCategory()) {
                    continue;
                }

                /* Determine menu to add to */
                AccountCategory myParent = myBucket.getAccountCategory().getParentCategory();
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

            /* Access current category and Account */
            AccountCategory myCategory = theState.getCategory();
            AccountBucket myAccount = theState.getAccount();

            /* Record active item */
            JMenuItem myActive = null;

            /* Loop through the available account values */
            Iterator<AccountBucket> myIterator = theAccounts.iterator();
            while (myIterator.hasNext()) {
                AccountBucket myBucket = myIterator.next();

                /* Ignore if not the correct category */
                if (!Difference.isEqual(myCategory, myBucket.getAccountCategory())) {
                    continue;
                }

                /* Create a new JMenuItem and add it to the popUp */
                AccountAction myAction = new AccountAction(myBucket);
                JMenuItem myItem = new JMenuItem(myAction);
                myPopUp.addMenuItem(myItem);

                /* If this is the active account */
                if (myAccount.equals(myBucket)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            myPopUp.showItem(myActive);

            /* Show the Account menu in the correct place */
            Rectangle myLoc = theAccountButton.getBounds();
            myPopUp.show(theAccountButton, 0, myLoc.height);
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
        private static final long serialVersionUID = 6923527853434260226L;

        /**
         * Category.
         */
        private final AccountCategory theCategory;

        /**
         * Constructor.
         * @param pCategory the category
         */
        private CategoryAction(final AccountCategory pCategory) {
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
     * Account action class.
     */
    private final class AccountAction
            extends AbstractAction {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 3957463218996802055L;

        /**
         * Account.
         */
        private final AccountBucket theAccount;

        /**
         * Constructor.
         * @param pAccount the account bucket
         */
        private AccountAction(final AccountBucket pAccount) {
            super(pAccount.getName());
            theAccount = pAccount;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* Select the new account */
            if (theState.setAccount(theAccount)) {
                theState.applyState();
                fireStateChanged();
            }
        }
    }

    /**
     * SavePoint values.
     */
    private final class AccountState {
        /**
         * The active Category.
         */
        private AccountCategory theCategory;

        /**
         * The active AccountBucket.
         */
        private AccountBucket theAccount;

        /**
         * Obtain the Account Bucket.
         * @return the Account
         */
        private AccountBucket getAccount() {
            return theAccount;
        }

        /**
         * Obtain the Category.
         * @return the category
         */
        private AccountCategory getCategory() {
            return theCategory;
        }

        /**
         * Constructor.
         */
        private AccountState() {
            /* Initialise the account */
            theAccount = null;
            theCategory = null;
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private AccountState(final AccountState pState) {
            /* Initialise state */
            theAccount = pState.getAccount();
            theCategory = pState.getCategory();
        }

        /**
         * Set new Account.
         * @param pAccount the Account
         * @return true/false did a change occur
         */
        private boolean setAccount(final AccountBucket pAccount) {
            /* Adjust the selected account */
            if (!Difference.isEqual(pAccount, theAccount)) {
                /* Store the account */
                theAccount = pAccount;

                /* Access category for account */
                theCategory = (theAccount == null)
                        ? null
                        : theAccount.getAccountCategory();

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
        private boolean setCategory(final AccountCategory pCategory) {
            /* Adjust the selected category */
            if (!Difference.isEqual(pCategory, theCategory)) {
                theCategory = pCategory;
                theAccount = getFirstAccount(theCategory);
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
            theAccountButton.setText((theAccount == null)
                    ? null
                    : theAccount.getName());
            theCatButton.setText((theCategory == null)
                    ? null
                    : theCategory.getName());
        }
    }
}
