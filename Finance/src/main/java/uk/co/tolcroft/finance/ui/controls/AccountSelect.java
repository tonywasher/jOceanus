/*******************************************************************************
 * JFinanceApp: Finance Application
 * Copyright 2012 Tony Washer
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
package uk.co.tolcroft.finance.ui.controls;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.LayoutStyle;

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.JPanelWithEvents;
import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.Account.AccountList;
import uk.co.tolcroft.finance.data.AccountType;
import uk.co.tolcroft.finance.data.AccountType.AccountTypeList;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.views.View;

/**
 * Account selection panel.
 * @author Tony Washer
 */
public class AccountSelect extends JPanelWithEvents {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -3537658425524334120L;

    /**
     * Width of Account Types box.
     */
    private static final int TYPES_WIDTH = 100;

    /**
     * Data view.
     */
    private final transient View theView;

    /**
     * Account types comboBox.
     */
    private final JComboBox theTypesBox;

    /**
     * Accounts comboBox.
     */
    private final JComboBox theAccountBox;

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
     * Get the account type.
     * @return the account type
     */
    public final AccountType getType() {
        return theState.getType();
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
        theTypesBox = new JComboBox();
        theAccountBox = new JComboBox();
        theShowClosed = new JCheckBox();
        theShowDeleted = new JCheckBox();

        /* Create initial state */
        theState = new AccountState();

        /* Initialise the data from the view */
        buildAccountTypes();
        buildAccounts();

        /* Set the text for the check-box */
        theShowClosed.setText("Show Closed");
        theShowClosed.setSelected(doShowClosed());

        /* Set the text for the check-box */
        theShowDeleted.setText("Show Deleted");
        theShowDeleted.setSelected(doShowDeleted());

        /* Create the labels */
        JLabel myTypeLabel = new JLabel("Account Type:");
        JLabel myAccountLabel = new JLabel("Account:");

        /* Create the panel */
        setBorder(BorderFactory.createTitledBorder("Account Selection"));

        /* Create the layout for the panel */
        GroupLayout panelLayout = new GroupLayout(this);
        setLayout(panelLayout);

        /* If we are showing deleted */
        if (showDeleted) {
            /* Set the layout */
            panelLayout.setHorizontalGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(panelLayout
                                      .createSequentialGroup()
                                      .addContainerGap()
                                      .addComponent(myTypeLabel)
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                      .addComponent(theTypesBox, GroupLayout.PREFERRED_SIZE, TYPES_WIDTH,
                                                    GroupLayout.PREFERRED_SIZE)
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                      .addComponent(myAccountLabel)
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                      .addComponent(theAccountBox)
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                      .addComponent(theShowClosed, GroupLayout.PREFERRED_SIZE,
                                                    GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                      .addComponent(theShowDeleted, GroupLayout.PREFERRED_SIZE,
                                                    GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                      .addContainerGap()));
            panelLayout.setVerticalGroup(panelLayout
                    .createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(panelLayout
                                      .createParallelGroup(GroupLayout.Alignment.BASELINE)
                                      .addComponent(myTypeLabel)
                                      .addComponent(theTypesBox, GroupLayout.PREFERRED_SIZE,
                                                    GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                      .addComponent(myAccountLabel)
                                      .addComponent(theAccountBox, GroupLayout.PREFERRED_SIZE,
                                                    GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addComponent(theShowClosed).addComponent(theShowDeleted));
        } else {
            /* Set the layout */
            panelLayout.setHorizontalGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(panelLayout
                                      .createSequentialGroup()
                                      .addContainerGap()
                                      .addComponent(myTypeLabel)
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                      .addComponent(theTypesBox, GroupLayout.PREFERRED_SIZE, TYPES_WIDTH,
                                                    GroupLayout.PREFERRED_SIZE)
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,
                                                       GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                      .addComponent(myAccountLabel)
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                      .addComponent(theAccountBox)
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,
                                                       GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                      .addComponent(theShowClosed, GroupLayout.PREFERRED_SIZE,
                                                    GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                      .addContainerGap()));
            panelLayout.setVerticalGroup(panelLayout
                    .createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(panelLayout
                                      .createParallelGroup(GroupLayout.Alignment.BASELINE)
                                      .addComponent(myTypeLabel)
                                      .addComponent(theTypesBox, GroupLayout.PREFERRED_SIZE,
                                                    GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                      .addComponent(myAccountLabel)
                                      .addComponent(theAccountBox, GroupLayout.PREFERRED_SIZE,
                                                    GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addComponent(theShowClosed));
        }

        /* Apply the current state */
        theState.applyState();

        /* Add the listener for item changes */
        theTypesBox.addItemListener(myListener);
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
        theState = new AccountState(theSavePoint);

        /* Build the range and apply the state */
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
        AccountType myType = null;
        AccountType myFirst = null;
        boolean doShowDeleted;
        boolean doShowClosed;

        /* Access the data */
        myData = theView.getData();

        /* Access types and accounts */
        AccountTypeList myTypes = myData.getAccountTypes();
        theAccounts = myData.getAccounts();

        /* Access current values */
        doShowDeleted = doShowDeleted();
        doShowClosed = doShowClosed();

        /* Note that we are refreshing data */
        refreshingData = true;

        /* If we have types already populated */
        if (theTypesBox.getItemCount() > 0) {
            /* If we have a selected type */
            if (getType() != null) {
                /* Find it in the new list */
                theState.setType(myTypes.findItemByName(getType().getName()));
            }

            /* Remove the types */
            theTypesBox.removeAllItems();
        }

        /* Access the iterator */
        Iterator<Account> myIterator = theAccounts.iterator();

        /* Loop through the non-owner accounts */
        while (myIterator.hasNext()) {
            Account myAccount = myIterator.next();
            /* Skip owner items */
            if (myAccount.isOwner()) {
                continue;
            }

            /* Skip deleted items */
            if ((!doShowDeleted) && (myAccount.isDeleted())) {
                continue;
            }

            /* Skip closed items if required */
            if ((!doShowClosed) && (myAccount.isClosed())) {
                continue;
            }

            /* If the type of this account is new */
            if (!Difference.isEqual(myType, myAccount.getActType())) {
                /* Note the type */
                myType = myAccount.getActType();
                if (myFirst == null) {
                    myFirst = myType;
                }

                /* Add the item to the list */
                theTypesBox.addItem(myType);
            }
        }

        /* Access the iterator */
        myIterator = theAccounts.iterator();

        /* Loop through the owner accounts */
        while (myIterator.hasNext()) {
            Account myAccount = myIterator.next();
            /* Skip child items */
            if (!myAccount.isOwner()) {
                continue;
            }

            /* Skip deleted items */
            if ((!doShowDeleted) && (myAccount.isDeleted())) {
                continue;
            }

            /* Skip closed items if required */
            if ((!doShowClosed) && (myAccount.isClosed())) {
                continue;
            }

            /* If the type of this account is new */
            if (!Difference.isEqual(myType, myAccount.getActType())) {
                /* Note the type */
                myType = myAccount.getActType();
                if (myFirst == null) {
                    myFirst = myType;
                }

                /* Add the item to the list */
                theTypesBox.addItem(myType);
            }
        }

        /* If we have a selected type */
        if (getType() != null) {
            /* Select it in the new list */
            theTypesBox.setSelectedItem(getType());

            /* Else we have no type currently selected */
        } else if (theTypesBox.getItemCount() > 0) {
            /* Select the first account type */
            theTypesBox.setSelectedIndex(0);
            theState.setType(myFirst);
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
        AccountType myType = getType();
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
            isInvalid |= (myType.compareTo(mySelected.getActType()) != 0);

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
            if ((!doShowDeleted) && (myAcct.isDeleted())) {
                continue;
            }

            /* Skip closed items if required */
            if ((!doShowClosed) && (myAcct.isClosed())) {
                continue;
            }

            /* Skip items that are the wrong type */
            if (myType.compareTo(myAcct.getActType()) != 0) {
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
            theAccountBox.setSelectedItem(mySelected.getName());

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

        /* Select the correct account type */
        theState.setType(pAccount.getActType());
        theTypesBox.setSelectedItem(getType());

        /* If we need to show closed items */
        if ((!doShowClosed()) && (myAccount != null) && (myAccount.isClosed())) {
            /* Set the flag correctly */
            theState.setDoShowClosed(true);
            theShowClosed.setSelected(true);
        }

        /* If we need to show deleted items */
        if ((!doShowDeleted()) && (myAccount != null) && (myAccount.isDeleted())) {
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
        Account mySelected = getSelected();

        /* Lock/Unlock the selection */
        theTypesBox.setEnabled(bEnable);
        theAccountBox.setEnabled(bEnable);

        /* Can't switch off show closed if account is closed */
        boolean bLock = ((mySelected != null) && (mySelected.isClosed()));

        /* Lock Show Closed */
        theShowClosed.setEnabled(bEnable && !bLock);

        /* Can't switch off show deleted if account is deleted */
        bLock = ((mySelected != null) && (mySelected.isDeleted()));

        /* Lock Show Deleted */
        theShowDeleted.setEnabled(bEnable && !bLock);
    }

    /**
     * Account Listener class.
     */
    private final class AccountListener implements ItemListener {
        @Override
        public void itemStateChanged(final ItemEvent evt) {
            Object o = evt.getSource();

            /* Ignore selection if refreshing data */
            if (refreshingData) {
                return;
            }

            /* If this event relates to the types box */
            if ((theTypesBox.equals(o)) && (evt.getStateChange() == ItemEvent.SELECTED)) {
                /* Select the new type and rebuild account list */
                AccountType myType = (AccountType) evt.getItem();
                if (theState.setType(myType)) {
                    /* Rebuild accounts */
                    buildAccounts();
                    fireStateChanged();
                }

                /* If this event relates to the account box */
            } else if ((theAccountBox.equals(o)) && (evt.getStateChange() == ItemEvent.SELECTED)) {
                /* Select the new account */
                Account myAcct = (Account) evt.getItem();
                if (theState.setSelected(myAcct)) {
                    fireStateChanged();
                }

                /* If this event relates to the showClosed box */
            } else if ((theShowClosed.equals(o)) && (theState.setDoShowClosed(theShowClosed.isSelected()))) {
                /* Build lists */
                buildAccountTypes();
                buildAccounts();

                /* If this event relates to the showDeleted box */
            } else if ((theShowDeleted.equals(o)) && (theState.setDoShowDeleted(theShowDeleted.isSelected()))) {
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
         * Account type.
         */
        private AccountType theType = null;

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
         * Get the account type.
         * @return the account type
         */
        private AccountType getType() {
            return theType;
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
            theType = pState.getType();
            theSelected = pState.getSelected();
        }

        /**
         * Set new Account Type.
         * @param pType the AccountType
         * @return true/false did a change occur
         */
        private boolean setType(final AccountType pType) {
            if (!Difference.isEqual(pType, theType)) {
                theType = pType;
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
            theTypesBox.setSelectedItem((theType == null) ? null : theType.getName());
            theAccountBox.setSelectedItem((theSelected == null) ? null : theSelected.getName());
        }
    }
}
