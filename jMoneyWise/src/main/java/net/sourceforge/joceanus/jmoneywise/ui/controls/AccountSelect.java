/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.ui.controls;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jEventManager.JEventPanel;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.Account.AccountList;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountCategory.AccountCategoryList;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.views.View;

/**
 * Account selection panel.
 * @author Tony Washer
 */
public class AccountSelect
        extends JEventPanel {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -3537658425524334120L;

    /**
     * Strut width.
     */
    private static final int STRUT_WIDTH = 10;

    /**
     * Box width.
     */
    private static final int BOX_WIDTH = 200;

    /**
     * Box height.
     */
    private static final int BOX_HEIGHT = 25;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(AccountSelect.class.getName());

    /**
     * Text for AccountCategory Label.
     */
    private static final String NLS_TYPE = NLS_BUNDLE.getString("AccountCategory");

    /**
     * Text for Account Label.
     */
    private static final String NLS_ACCOUNT = NLS_BUNDLE.getString("Account");

    /**
     * Text for Show Closed.
     */
    private static final String NLS_CLOSED = NLS_BUNDLE.getString("ShowClosed");

    /**
     * Text for Show Deleted.
     */
    private static final String NLS_DELETED = NLS_BUNDLE.getString("ShowDeleted");

    /**
     * Text for Selection Title.
     */
    private static final String NLS_TITLE = NLS_BUNDLE.getString("SelectTitle");

    /**
     * Data view.
     */
    private final transient View theView;

    /**
     * Account categories comboBox.
     */
    private final JComboBox<AccountCategory> theCategoriesBox;

    /**
     * Accounts comboBox.
     */
    private final JComboBox<Account> theAccountBox;

    /**
     * Show closed checkBox.
     */
    private final JCheckBox theShowClosed;

    /**
     * Show deleted checkBox.
     */
    private final JCheckBox theShowDeleted;

    /**
     * Accounts list.
     */
    private transient AccountList theAccounts = null;

    /**
     * Current State.
     */
    private transient AccountState theState = null;

    /**
     * Saved state.
     */
    private transient AccountState theSavePoint = null;

    /**
     * Are we refreshing data?
     */
    private boolean refreshingData = false;

    /**
     * Get the selected account.
     * @return the account
     */
    public final Account getSelected() {
        return theState.getSelected();
    }

    /**
     * Get the account category.
     * @return the account category
     */
    public final AccountCategory getCategory() {
        return theState.getCategory();
    }

    /**
     * Are we showing closed accounts?
     * @return true/false
     */
    public final boolean doShowClosed() {
        return theState.doShowClosed();
    }

    /**
     * Are we showing deleted accounts?
     * @return true/false
     */
    public final boolean doShowDeleted() {
        return theState.doShowDeleted();
    }

    /**
     * Constructor.
     * @param pView the data view
     * @param showDeleted should we show deleted accounts.
     */
    public AccountSelect(final View pView,
                         final boolean showDeleted) {
        AccountListener myListener = new AccountListener();

        /* Store table and view details */
        theView = pView;

        /* Create the boxes */
        theCategoriesBox = new JComboBox<AccountCategory>();
        theAccountBox = new JComboBox<Account>();
        theShowClosed = new JCheckBox();
        theShowDeleted = new JCheckBox();
        theCategoriesBox.setMaximumSize(new Dimension(BOX_WIDTH, BOX_HEIGHT));
        theAccountBox.setMaximumSize(new Dimension(BOX_WIDTH, BOX_HEIGHT));

        /* Create initial state */
        theState = new AccountState();

        /* Initialise the data from the view */
        buildAccountTypes();
        buildAccounts();

        /* Set the text for the check-box */
        theShowClosed.setText(NLS_CLOSED);
        theShowClosed.setSelected(doShowClosed());

        /* Set the text for the check-box */
        theShowDeleted.setText(NLS_DELETED);
        theShowDeleted.setSelected(doShowDeleted());

        /* Create the labels */
        JLabel myTypeLabel = new JLabel(NLS_TYPE);
        JLabel myAccountLabel = new JLabel(NLS_ACCOUNT);

        /* Create the panel */
        setBorder(BorderFactory.createTitledBorder(NLS_TITLE));

        /* Define the layout */
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(myTypeLabel);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theCategoriesBox);
        add(Box.createHorizontalGlue());
        add(myAccountLabel);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theAccountBox);
        add(Box.createHorizontalGlue());
        add(theShowClosed);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        if (showDeleted) {
            add(theShowDeleted);
            add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        }

        /* Apply the current state */
        theState.applyState();

        /* Add the listener for item changes */
        theCategoriesBox.addItemListener(myListener);
        theAccountBox.addItemListener(myListener);
        theShowClosed.addItemListener(myListener);
        theShowDeleted.addItemListener(myListener);
    }

    /**
     * Create SavePoint.
     */
    public void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new AccountState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = (theSavePoint == null)
                ? new AccountState()
                : new AccountState(theSavePoint);

        /* Build the range and apply the state */
        buildAccountTypes();
        buildAccounts();
        theState.applyState();
    }

    /**
     * refresh data.
     */
    public final void refreshData() {
        /* Build the account types */
        buildAccountTypes();

        /* Build the account list for the type */
        buildAccounts();
    }

    /**
     * build the account types.
     */
    private void buildAccountTypes() {
        FinanceData myData;
        AccountCategory myCategory = null;
        AccountCategory myFirst = null;
        boolean doShowDeleted;
        boolean doShowClosed;

        /* Access the data */
        myData = theView.getData();

        /* Access categories and accounts */
        AccountCategoryList myCategories = myData.getAccountCategories();
        theAccounts = myData.getAccounts();

        /* Access current values */
        doShowDeleted = doShowDeleted();
        doShowClosed = doShowClosed();

        /* Note that we are refreshing data */
        refreshingData = true;

        /* If we have categories already populated */
        if (theCategoriesBox.getItemCount() > 0) {
            /* If we have a selected category */
            if (getCategory() != null) {
                /* Find it in the new list */
                theState.setCategory(myCategories.findItemByName(getCategory().getName()));
            }

            /* Remove the categories */
            theCategoriesBox.removeAllItems();
        }

        /* Access the iterator */
        Iterator<Account> myIterator = theAccounts.iterator();

        /* Loop through the non-owner accounts */
        while (myIterator.hasNext()) {
            Account myAccount = myIterator.next();
            /* Skip owner items */
            if (myAccount.isParent()) {
                continue;
            }

            /* Skip deleted items */
            if ((!doShowDeleted)
                && (myAccount.isDeleted())) {
                continue;
            }

            /* Skip closed items if required */
            if ((!doShowClosed)
                && (myAccount.isClosed())) {
                continue;
            }

            /* If the category of this account is new */
            if (!Difference.isEqual(myCategory, myAccount.getAccountCategory())) {
                /* Note the category */
                myCategory = myAccount.getAccountCategory();
                if (myFirst == null) {
                    myFirst = myCategory;
                }

                /* Add the item to the list */
                theCategoriesBox.addItem(myCategory);
            }
        }

        /* Access the iterator */
        myIterator = theAccounts.iterator();

        /* Loop through the owner accounts */
        while (myIterator.hasNext()) {
            Account myAccount = myIterator.next();
            /* Skip child items */
            if (!myAccount.isParent()) {
                continue;
            }

            /* Skip deleted items */
            if ((!doShowDeleted)
                && (myAccount.isDeleted())) {
                continue;
            }

            /* Skip closed items if required */
            if ((!doShowClosed)
                && (myAccount.isClosed())) {
                continue;
            }

            /* If the type of this account is new */
            if (!Difference.isEqual(myCategory, myAccount.getAccountCategory())) {
                /* Note the category */
                myCategory = myAccount.getAccountCategory();
                if (myFirst == null) {
                    myFirst = myCategory;
                }

                /* Add the item to the list */
                theCategoriesBox.addItem(myCategory);
            }
        }

        /* If we have a selected category */
        if (getCategory() != null) {
            /* Select it in the new list */
            theCategoriesBox.setSelectedItem(getCategory());

            /* Else we have no type currently selected */
        } else if (theCategoriesBox.getItemCount() > 0) {
            /* Select the first account type */
            theCategoriesBox.setSelectedIndex(0);
            theState.setCategory(myFirst);
        }

        /* Note that we have finished refreshing data */
        refreshingData = false;
    }

    /**
     * build the accounts comboBox.
     * @return true/false
     */
    private boolean buildAccounts() {
        /* Access current values */
        boolean doShowDeleted = doShowDeleted();
        boolean doShowClosed = doShowClosed();
        AccountCategory myCategory = getCategory();
        Account mySelected = getSelected();
        Account myOld = mySelected;

        /* Note that we are refreshing data */
        refreshingData = true;

        /* If we have accounts already populated */
        if (theAccountBox.getItemCount() > 0) {
            /* If we have a selected account */
            if (mySelected != null) {
                /* Find it in the new list */
                theState.setSelected(theAccounts.findItemByName(mySelected.getName()));
                mySelected = getSelected();
            }

            /* Remove the accounts from the box */
            theAccountBox.removeAllItems();
        }

        /* If we have a selected item */
        if (mySelected != null) {
            /* Check its validity */
            boolean isInvalid = ((!doShowDeleted) && (mySelected.isDeleted()));
            isInvalid |= ((!doShowClosed) && (mySelected.isClosed()));
            isInvalid |= (myCategory.compareTo(mySelected.getAccountCategory()) != 0);

            /* If it is no longer valid */
            if (isInvalid) {
                /* Remove selection */
                theState.setSelected(null);
                mySelected = null;
            }
        }

        /* Access the iterator */
        Iterator<Account> myIterator = theAccounts.iterator();
        Account myFirst = null;

        /* Add the Account values to the types box */
        while (myIterator.hasNext()) {
            Account myAcct = myIterator.next();
            /* Skip deleted items */
            if ((!doShowDeleted)
                && (myAcct.isDeleted())) {
                continue;
            }

            /* Skip closed items if required */
            if ((!doShowClosed)
                && (myAcct.isClosed())) {
                continue;
            }

            /* Skip items that are the wrong category */
            if (myCategory.compareTo(myAcct.getAccountCategory()) != 0) {
                continue;
            }

            /* Note the first in the list */
            if (myFirst == null) {
                myFirst = myAcct;
            }

            /* Add the item to the list */
            theAccountBox.addItem(myAcct);
        }

        /* If we have a selected account */
        if (mySelected != null) {
            /* Select it in the new list */
            theAccountBox.setSelectedItem(mySelected);

            /* Else we have no account currently selected */
        } else if (theAccountBox.getItemCount() > 0) {
            /* Select the first account */
            theAccountBox.setSelectedIndex(0);
            theState.setSelected(myFirst);
        }

        /* Note that we have finished refreshing data */
        refreshingData = false;

        /* Return whether we have changed selection */
        return !Difference.isEqual(getSelected(), myOld);
    }

    /**
     * Set account explicitly.
     * @param pAccount the account
     */
    public void setSelection(final Account pAccount) {
        Account myAccount;

        /* Set the refreshing data flag */
        refreshingData = true;

        /* Access the edit-able account */
        myAccount = theAccounts.findItemByName(pAccount.getName());

        /* Select the correct account category */
        theState.setCategory(pAccount.getAccountCategory());
        theCategoriesBox.setSelectedItem(getCategory());

        /* If we need to show closed items */
        if ((!doShowClosed())
            && (myAccount != null)
            && (myAccount.isClosed())) {
            /* Set the flag correctly */
            theState.setDoShowClosed(true);
            theShowClosed.setSelected(true);
        }

        /* If we need to show deleted items */
        if ((!doShowDeleted())
            && (myAccount != null)
            && (myAccount.isDeleted())) {
            /* Set the flag correctly */
            theState.setDoShowDeleted(true);
            theShowDeleted.setSelected(true);
        }

        /* Select the account */
        theState.setSelected(myAccount);

        /* Reset the refreshing data flag */
        refreshingData = false;

        /* Build the accounts */
        buildAccounts();
    }

    @Override
    public void setEnabled(final boolean bEnable) {
        /* Lock/Unlock the selection */
        theCategoriesBox.setEnabled(bEnable);
        theAccountBox.setEnabled(bEnable);

        /* Can't switch off show closed if account is closed */
        // boolean bLock = ((mySelected != null) && (mySelected.isClosed()));

        /* Lock Show Closed */
        theShowClosed.setEnabled(bEnable);

        /* Can't switch off show deleted if account is deleted */
        // bLock = ((mySelected != null) && (mySelected.isDeleted()));

        /* Lock Show Deleted */
        theShowDeleted.setEnabled(bEnable);
    }

    /**
     * Account Listener class.
     */
    private final class AccountListener
            implements ItemListener {
        @Override
        public void itemStateChanged(final ItemEvent evt) {
            Object o = evt.getSource();

            /* Ignore selection if refreshing data */
            if (refreshingData) {
                return;
            }

            /* If this event relates to the categories box */
            if ((theCategoriesBox.equals(o))
                && (evt.getStateChange() == ItemEvent.SELECTED)) {
                /* Select the new category and rebuild account list */
                AccountCategory myCategory = (AccountCategory) evt.getItem();
                if (theState.setCategory(myCategory)) {
                    /* Rebuild accounts */
                    buildAccounts();
                    fireStateChanged();
                }

                /* If this event relates to the account box */
            } else if ((theAccountBox.equals(o))
                       && (evt.getStateChange() == ItemEvent.SELECTED)) {
                /* Select the new account */
                Account myAcct = (Account) evt.getItem();
                if (theState.setSelected(myAcct)) {
                    fireStateChanged();
                }

                /* If this event relates to the showClosed box */
            } else if ((theShowClosed.equals(o))
                       && (theState.setDoShowClosed(theShowClosed.isSelected()))) {
                /* Build lists */
                buildAccountTypes();
                buildAccounts();

                /* If this event relates to the showDeleted box */
            } else if ((theShowDeleted.equals(o))
                       && (theState.setDoShowDeleted(theShowDeleted.isSelected()))) {
                /* Build lists */
                buildAccountTypes();
                buildAccounts();
            }
        }
    }

    /**
     * SavePoint class.
     */
    private final class AccountState {
        /**
         * Account category.
         */
        private AccountCategory theCategory = null;

        /**
         * Selected account.
         */
        private Account theSelected = null;

        /**
         * Should we show closed accounts?
         */
        private boolean doShowClosed = false;

        /**
         * Should we show deleted accounts?
         */
        private boolean doShowDeleted = false;

        /**
         * Get the account category.
         * @return the account category
         */
        private AccountCategory getCategory() {
            return theCategory;
        }

        /**
         * Get the selected account.
         * @return the account type
         */
        private Account getSelected() {
            return theSelected;
        }

        /**
         * Are we showing closed accounts?
         * @return true/false
         */
        private boolean doShowClosed() {
            return doShowClosed;
        }

        /**
         * Are we showing deleted accounts?
         * @return true/false
         */
        private boolean doShowDeleted() {
            return doShowDeleted;
        }

        /**
         * Constructor.
         */
        private AccountState() {
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private AccountState(final AccountState pState) {
            theCategory = pState.getCategory();
            theSelected = pState.getSelected();
        }

        /**
         * Set new Account Category.
         * @param pCategory the AccountCategory
         * @return true/false did a change occur
         */
        private boolean setCategory(final AccountCategory pCategory) {
            if (!Difference.isEqual(pCategory, theCategory)) {
                theCategory = pCategory;
                return true;
            }
            return false;
        }

        /**
         * Set new Account.
         * @param pAccount the Account
         * @return true/false did a change occur
         */
        private boolean setSelected(final Account pAccount) {
            /* Adjust the selected account */
            if (!Difference.isEqual(pAccount, theSelected)) {
                theSelected = pAccount;
                return true;
            }
            return false;
        }

        /**
         * Set doShowClosed indication.
         * @return true/false did a change occur
         * @param pShowClosed true/false
         */
        private boolean setDoShowClosed(final boolean pShowClosed) {
            /* Adjust the flag */
            if (doShowClosed != pShowClosed) {
                doShowClosed = pShowClosed;
                return true;
            }
            return false;
        }

        /**
         * Set doShowDeleted indication.
         * @param pShowDeleted true/false
         * @return true/false did a change occur
         */
        private boolean setDoShowDeleted(final boolean pShowDeleted) {
            /* Adjust the flag */
            if (doShowDeleted != pShowDeleted) {
                doShowDeleted = pShowDeleted;
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
            theShowClosed.setSelected(doShowClosed);
            theShowDeleted.setSelected(doShowDeleted);
            theCategoriesBox.setSelectedItem((theCategory == null)
                    ? null
                    : theCategory.getName());
            theAccountBox.setSelectedItem((theSelected == null)
                    ? null
                    : theSelected.getName());
        }
    }
}