/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.jOceanus.jMoneyWise.ui.controls;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JComboBox;

import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.Account.AccountList;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TransactionType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TransactionType.TransTypeList;
import net.sourceforge.jOceanus.jMoneyWise.views.View;

/**
 * ComboBox selection class.
 * @author Tony Washer
 */
public class ComboSelect {
    /**
     * The JComboBox for the whole set of transaction types.
     */
    private final JComboBox<TransactionType> theTranTypeBox;

    /**
     * The View.
     */
    private final View theView;

    /**
     * The DataSet.
     */
    private FinanceData theData;

    /**
     * The map of transactionTypes for a pair of accounts.
     */
    private final Map<String, JComboBox<TransactionType>> theTranMap;

    /**
     * The map of accounts for a transactionType and account.
     */
    private final Map<String, JComboBox<Account>> theAccountMap;

    /**
     * Constructor.
     * @param pView the data view
     */
    public ComboSelect(final View pView) {
        /* Store the view */
        theView = pView;

        /* Allocate the transaction type box */
        theTranTypeBox = new JComboBox<TransactionType>();

        /* Allocate the maps */
        theTranMap = new HashMap<String, JComboBox<TransactionType>>();
        theAccountMap = new HashMap<String, JComboBox<Account>>();

        /* Refresh the data */
        refreshData();
    }

    /**
     * Refresh data after load/update.
     */
    public void refreshData() {
        /* Store the data */
        theData = theView.getData();

        /* Clear the maps */
        theTranMap.clear();
        theAccountMap.clear();

        /* If we have items in the list */
        if (theTranTypeBox.getItemCount() > 0) {
            /* Clear transaction types */
            theTranTypeBox.removeAllItems();
        }

        /* Access the transaction types */
        TransTypeList myList = theData.getTransTypes();

        /* Create the iterator */
        Iterator<TransactionType> myIterator = myList.iterator();

        /* Loop through the Transaction types */
        while (myIterator.hasNext()) {
            TransactionType myTrans = myIterator.next();

            /* Skip hidden/disabled values */
            if ((myTrans.isHiddenType()) || (!myTrans.getEnabled())) {
                continue;
            }

            /* Add the item to the list */
            theTranTypeBox.addItem(myTrans);
        }
    }

    /**
     * Obtain the pure transaction type ComboBox.
     * @return a ComboBox with all the transaction types
     */
    public JComboBox<TransactionType> getAllTransTypes() {
        /* return to caller */
        return theTranTypeBox;
    }

    /**
     * Obtain the ComboBox of transaction types for a Credit to an Account.
     * @param pAccount the account
     * @return the ComboBox
     */
    public JComboBox<TransactionType> getCreditTranTypes(final Account pAccount) {
        /* Create the key */
        String myKey = "*_" + pAccount.getName();

        /* Look for the existing comboBox */
        JComboBox<TransactionType> myCombo = theTranMap.get(myKey);
        if (myCombo != null) {
            return myCombo;
        }

        /* Create the iterator */
        TransTypeList myList = theData.getTransTypes();
        Iterator<TransactionType> myIterator = myList.iterator();

        /* Create the ComboBox */
        myCombo = new JComboBox<TransactionType>();

        /* Loop through the Transaction types */
        while (myIterator.hasNext()) {
            TransactionType myTrans = myIterator.next();

            /* Skip hidden/disabled values */
            if ((myTrans.isHiddenType()) || (!myTrans.getEnabled())) {
                continue;
            }

            /* Obtain the debit comboList */
            JComboBox<Account> mySubCombo = getDebitAccounts(myTrans, pAccount);

            /* If there are valid combinations */
            if (mySubCombo.getItemCount() > 0) {
                /* Add the item to the list */
                myCombo.addItem(myTrans);
            }
        }

        /* Add to map and return to caller */
        theTranMap.put(myKey, myCombo);
        return myCombo;
    }

    /**
     * Obtain the ComboBox of transaction types for a Debit from an Account.
     * @param pAccount the account
     * @return the ComboBox
     */
    public JComboBox<TransactionType> getDebitTranTypes(final Account pAccount) {
        /* Create the key */
        String myKey = pAccount.getName() + "_*";

        /* Look for the existing comboBox */
        JComboBox<TransactionType> myCombo = theTranMap.get(myKey);
        if (myCombo != null) {
            return myCombo;
        }

        /* Create the iterator */
        TransTypeList myList = theData.getTransTypes();
        Iterator<TransactionType> myIterator = myList.listIterator();

        /* Create the ComboBox */
        myCombo = new JComboBox<TransactionType>();

        /* Loop through the Transaction types */
        while (myIterator.hasNext()) {
            TransactionType myTrans = myIterator.next();

            /* Skip hidden/disabled values */
            if ((myTrans.isHiddenType()) || (!myTrans.getEnabled())) {
                continue;
            }

            /* Obtain the credit comboList */
            JComboBox<Account> mySubCombo = getCreditAccounts(myTrans, pAccount);

            /* If there are valid combinations */
            if (mySubCombo.getItemCount() > 0) {
                /* Add the item to the list */
                myCombo.addItem(myTrans);
            }
        }

        /* Add to map and return to caller */
        theTranMap.put(myKey, myCombo);
        return myCombo;
    }

    /**
     * Obtain the ComboBox of accounts for a Debit for a Transaction Type.
     * @param pType the transaction type
     * @return the ComboBox
     */
    public JComboBox<Account> getDebitAccounts(final TransactionType pType) {
        /* Create the key */
        String myKey = pType.getName();

        /* Look for the existing comboBox */
        JComboBox<Account> myCombo = theAccountMap.get(myKey);
        if (myCombo != null) {
            return myCombo;
        }

        AccountType myType = null;
        boolean isValid = false;

        /* Access the iterator */
        AccountList myList = theData.getAccounts();
        Iterator<Account> myIterator = myList.listIterator();

        /* Create the ComboBox */
        myCombo = new JComboBox<Account>();

        /* Loop through the accounts */
        while (myIterator.hasNext()) {
            Account myAccount = myIterator.next();

            /* If the type of this account is new */
            if (!Difference.isEqual(myType, myAccount.getActType())) {
                /* Note the type */
                myType = myAccount.getActType();

                /* Determine whether we are a valid type */
                isValid = Event.isValidEvent(pType, myType, false);
            }

            /* Skip invalid types/closed accounts */
            if ((!isValid) || (myAccount.isClosed())) {
                continue;
            }

            /* Obtain the credit comboList */
            JComboBox<Account> mySubCombo = getCreditAccounts(pType, myAccount);

            /* If there are valid combinations */
            if (mySubCombo.getItemCount() > 0) {
                /* Add the item to the list */
                myCombo.addItem(myAccount);
            }
        }

        /* Add to map and return to caller */
        theAccountMap.put(myKey, myCombo);
        return myCombo;
    }

    /**
     * Obtain the ComboBox of accounts for a Credit for a Transaction Type and Debit Account.
     * @param pType the transaction type
     * @param pDebit the debit account
     * @return the ComboBox
     */
    public JComboBox<Account> getCreditAccounts(final TransactionType pType,
                                                final Account pDebit) {
        /* Create the key */
        String myKey = pType.getName() + "_" + pDebit.getName() + "_*";

        /* Look for the existing comboBox */
        JComboBox<Account> myCombo = theAccountMap.get(myKey);
        if (myCombo != null) {
            return myCombo;
        }

        AccountType myType = null;
        boolean isValid = false;

        /* Access the iterator */
        AccountList myList = theData.getAccounts();
        Iterator<Account> myIterator = myList.listIterator();

        /* Create the ComboBox */
        myCombo = new JComboBox<Account>();

        /* Loop through the accounts */
        while (myIterator.hasNext()) {
            Account myAccount = myIterator.next();

            /* If the type of this account is new */
            if (!Difference.isEqual(myType, myAccount.getActType())) {
                /* Note the type */
                myType = myAccount.getActType();

                /* Determine whether we are a valid type */
                isValid = Event.isValidEvent(pType, myType, true);
            }

            /* Skip invalid types/closed accounts */
            if ((!isValid) || (myAccount.isClosed())) {
                continue;
            }

            /* If the account is identical to the selected account */
            if (Difference.isEqual(myAccount, pDebit)) {
                /* If this combination is allowed */
                if (Event.isValidEvent(pType, pDebit, myAccount)) {
                    /* Add to beginning of list */
                    myCombo.insertItemAt(myAccount, 0);
                }

                /* else it is a different account */
            } else {
                /* If this combination is allowed */
                if (Event.isValidEvent(pType, pDebit, myAccount)) {
                    /* Add the item to the list */
                    myCombo.addItem(myAccount);
                }
            }
        }

        /* Add to map and return to caller */
        theAccountMap.put(myKey, myCombo);
        return myCombo;
    }

    /**
     * Obtain the ComboBox of accounts for a Debit for a Transaction Type and Credit Account.
     * @param pType the transaction type
     * @param pCredit the credit account
     * @return the ComboBox
     */
    public JComboBox<Account> getDebitAccounts(final TransactionType pType,
                                               final Account pCredit) {
        /* Create the key */
        String myKey = pType.getName() + "_*_" + pCredit.getName();

        /* Look for the existing comboBox */
        JComboBox<Account> myCombo = theAccountMap.get(myKey);
        if (myCombo != null) {
            return myCombo;
        }

        AccountType myType = null;
        boolean isValid = false;

        /* Access the iterator */
        AccountList myList = theData.getAccounts();
        Iterator<Account> myIterator = myList.listIterator();

        /* Create the ComboBox */
        myCombo = new JComboBox<Account>();

        /* Loop through the accounts */
        while (myIterator.hasNext()) {
            Account myAccount = myIterator.next();

            /* If the type of this account is new */
            if (!Difference.isEqual(myType, myAccount.getActType())) {
                /* Note the type */
                myType = myAccount.getActType();

                /* Determine whether we are a valid type */
                isValid = Event.isValidEvent(pType, myType, false);
            }

            /* Skip invalid types/closed accounts */
            if ((!isValid) || (myAccount.isClosed())) {
                continue;
            }

            /* If the account is identical to the selected account */
            if (Difference.isEqual(myAccount, pCredit)) {
                /* If this combination is allowed */
                if (Event.isValidEvent(pType, myAccount, pCredit)) {
                    /* Add to beginning of list */
                    myCombo.insertItemAt(myAccount, 0);
                }

                /* else it is a different account */
            } else {
                /* If this combination is allowed */
                if (Event.isValidEvent(pType, myAccount, pCredit)) {
                    /* Add the item to the list */
                    myCombo.addItem(myAccount);
                }
            }
        }

        /* Add to map and return to caller */
        theAccountMap.put(myKey, myCombo);
        return myCombo;
    }
}
