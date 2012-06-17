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

import java.util.Iterator;

import javax.swing.JComboBox;

import net.sourceforge.JDataManager.Difference;
import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.Account.AccountList;
import uk.co.tolcroft.finance.data.AccountType;
import uk.co.tolcroft.finance.data.Event;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.data.TransactionType;
import uk.co.tolcroft.finance.data.TransactionType.TransTypeList;
import uk.co.tolcroft.finance.views.View;

/**
 * ComboBox selection class.
 * @author Tony Washer
 */
public class ComboSelect {
    /**
     * The JComboBox for the whole set of transaction types.
     */
    private final JComboBox theTranTypeBox;

    /**
     * The DataSet.
     */
    private final FinanceData theData;

    /**
     * Constructor.
     * @param pView the data view
     */
    public ComboSelect(final View pView) {
        /* Store the data */
        theData = pView.getData();

        /* Create the TransType box */
        theTranTypeBox = new JComboBox();

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
    public JComboBox getAllTransTypes() {
        /* return to caller */
        return theTranTypeBox;
    }

    /**
     * Obtain the ComboBox of transaction types for a Credit to an AccountType.
     * @param pType the account type
     * @return the ComboBox
     */
    public JComboBox getCreditTranTypes(final AccountType pType) {
        /* Create the iterator */
        TransTypeList myList = theData.getTransTypes();
        Iterator<TransactionType> myIterator = myList.iterator();

        /* Create the ComboBox */
        JComboBox myCombo = new JComboBox();

        /* Loop through the Transaction types */
        while (myIterator.hasNext()) {
            TransactionType myTrans = myIterator.next();

            /* Skip hidden/disabled values */
            if ((myTrans.isHiddenType()) || (!myTrans.getEnabled())) {
                continue;
            }

            /* If this is OK for a credit to this account type */
            if (Event.isValidEvent(myTrans, pType, true)) {
                /* Add the item to the list */
                myCombo.addItem(myTrans);
            }
        }

        /* return to caller */
        return myCombo;
    }

    /**
     * Obtain the ComboBox of transaction types for a Debit from an AccountType.
     * @param pType the transaction type
     * @return the ComboBox
     */
    public JComboBox getDebitTranTypes(final AccountType pType) {
        /* Create the iterator */
        TransTypeList myList = theData.getTransTypes();
        Iterator<TransactionType> myIterator = myList.listIterator();

        /* Create the ComboBox */
        JComboBox myCombo = new JComboBox();

        /* Loop through the Transaction types */
        while (myIterator.hasNext()) {
            TransactionType myTrans = myIterator.next();

            /* Skip hidden/disabled values */
            if ((myTrans.isHiddenType()) || (!myTrans.getEnabled())) {
                continue;
            }

            /* If this is OK for a debit from this account type */
            if (Event.isValidEvent(myTrans, pType, false)) {
                /* Add the item to the list */
                myCombo.addItem(myTrans);
            }
        }

        /* return to caller */
        return myCombo;
    }

    /**
     * Obtain the ComboBox of accounts for a Debit for a Transaction Type.
     * @param pType the transaction type
     * @return the ComboBox
     */
    public JComboBox getDebitAccounts(final TransactionType pType) {
        AccountType myType = null;
        boolean isValid = false;

        /* Access the iterator */
        AccountList myList = theData.getAccounts();
        Iterator<Account> myIterator = myList.listIterator();

        /* Create the ComboBox */
        JComboBox myCombo = new JComboBox();

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

            /* Add the item to the list */
            myCombo.addItem(myAccount);
        }

        /* return to caller */
        return myCombo;
    }

    /**
     * Obtain the ComboBox of accounts for a Credit for a Transaction Type and Account.
     * @param pType the transaction type
     * @param pDebit the debit account
     * @return the ComboBox
     */
    public JComboBox getCreditAccounts(final TransactionType pType,
                                       final Account pDebit) {
        AccountType myType = null;
        boolean isValid = false;

        /* Access the iterator */
        AccountList myList = theData.getAccounts();
        Iterator<Account> myIterator = myList.listIterator();

        /* Create the ComboBox */
        JComboBox myCombo = new JComboBox();

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

        /* return to caller */
        return myCombo;
    }

    /**
     * Obtain the ComboBox of accounts for a Debit for a Transaction Type and Account.
     * @param pType the transaction type
     * @param pCredit the credit account
     * @return the ComboBox
     */
    public JComboBox getDebitAccounts(final TransactionType pType,
                                      final Account pCredit) {
        AccountType myType = null;
        boolean isValid = false;

        /* Access the iterator */
        AccountList myList = theData.getAccounts();
        Iterator<Account> myIterator = myList.listIterator();

        /* Create the ComboBox */
        JComboBox myCombo = new JComboBox();

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

        /* return to caller */
        return myCombo;
    }
}
