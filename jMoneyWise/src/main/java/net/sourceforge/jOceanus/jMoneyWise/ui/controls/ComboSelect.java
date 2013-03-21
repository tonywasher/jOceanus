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
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountCategoryType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryType.EventCategoryTypeList;
import net.sourceforge.jOceanus.jMoneyWise.views.View;

/**
 * ComboBox selection class.
 * @author Tony Washer
 */
public class ComboSelect {
    /**
     * The JComboBox for the whole set of category types.
     */
    private final JComboBox<EventCategoryType> theCategoryTypeBox;

    /**
     * The View.
     */
    private final View theView;

    /**
     * The DataSet.
     */
    private FinanceData theData;

    /**
     * The map of categoryTypes for a pair of accounts.
     */
    private final Map<String, JComboBox<EventCategoryType>> theCategoryMap;

    /**
     * The map of accounts for a categoryType and account.
     */
    private final Map<String, JComboBox<Account>> theAccountMap;

    /**
     * Constructor.
     * @param pView the data view
     */
    public ComboSelect(final View pView) {
        /* Store the view */
        theView = pView;

        /* Allocate the category type box */
        theCategoryTypeBox = new JComboBox<EventCategoryType>();

        /* Allocate the maps */
        theCategoryMap = new HashMap<String, JComboBox<EventCategoryType>>();
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
        theCategoryMap.clear();
        theAccountMap.clear();

        /* If we have items in the list */
        if (theCategoryTypeBox.getItemCount() > 0) {
            /* Clear category types */
            theCategoryTypeBox.removeAllItems();
        }

        /* Access the category types */
        EventCategoryTypeList myList = theData.getEventCategoryTypes();

        /* Create the iterator */
        Iterator<EventCategoryType> myIterator = myList.iterator();

        /* Loop through the Category types */
        while (myIterator.hasNext()) {
            EventCategoryType myCat = myIterator.next();

            /* Skip hidden/disabled values */
            if ((myCat.isHiddenType())
                || (!myCat.getEnabled())) {
                continue;
            }

            /* Add the item to the list */
            theCategoryTypeBox.addItem(myCat);
        }
    }

    /**
     * Obtain the pure category type ComboBox.
     * @return a ComboBox with all the category types
     */
    public JComboBox<EventCategoryType> getAllCategoryTypes() {
        /* return to caller */
        return theCategoryTypeBox;
    }

    /**
     * Obtain the ComboBox of category types for a Credit to an Account.
     * @param pAccount the account
     * @return the ComboBox
     */
    public JComboBox<EventCategoryType> getCreditCategoryTypes(final Account pAccount) {
        /* Create the key */
        String myKey = "*_"
                       + pAccount.getName();

        /* Look for the existing comboBox */
        JComboBox<EventCategoryType> myCombo = theCategoryMap.get(myKey);
        if (myCombo != null) {
            return myCombo;
        }

        /* Create the iterator */
        EventCategoryTypeList myList = theData.getEventCategoryTypes();
        Iterator<EventCategoryType> myIterator = myList.iterator();

        /* Create the ComboBox */
        myCombo = new JComboBox<EventCategoryType>();

        /* Loop through the Category types */
        while (myIterator.hasNext()) {
            EventCategoryType myCat = myIterator.next();

            /* Skip hidden/disabled values */
            if ((myCat.isHiddenType())
                || (!myCat.getEnabled())) {
                continue;
            }

            /* Obtain the debit comboList */
            JComboBox<Account> mySubCombo = getDebitAccounts(myCat, pAccount);

            /* If there are valid combinations */
            if (mySubCombo.getItemCount() > 0) {
                /* Add the item to the list */
                myCombo.addItem(myCat);
            }
        }

        /* Add to map and return to caller */
        theCategoryMap.put(myKey, myCombo);
        return myCombo;
    }

    /**
     * Obtain the ComboBox of category types for a Debit from an Account.
     * @param pAccount the account
     * @return the ComboBox
     */
    public JComboBox<EventCategoryType> getDebitCategoryTypes(final Account pAccount) {
        /* Create the key */
        String myKey = pAccount.getName()
                       + "_*";

        /* Look for the existing comboBox */
        JComboBox<EventCategoryType> myCombo = theCategoryMap.get(myKey);
        if (myCombo != null) {
            return myCombo;
        }

        /* Create the iterator */
        EventCategoryTypeList myList = theData.getEventCategoryTypes();
        Iterator<EventCategoryType> myIterator = myList.listIterator();

        /* Create the ComboBox */
        myCombo = new JComboBox<EventCategoryType>();

        /* Loop through the Category types */
        while (myIterator.hasNext()) {
            EventCategoryType myCat = myIterator.next();

            /* Skip hidden/disabled values */
            if ((myCat.isHiddenType())
                || (!myCat.getEnabled())) {
                continue;
            }

            /* Obtain the credit comboList */
            JComboBox<Account> mySubCombo = getCreditAccounts(myCat, pAccount);

            /* If there are valid combinations */
            if (mySubCombo.getItemCount() > 0) {
                /* Add the item to the list */
                myCombo.addItem(myCat);
            }
        }

        /* Add to map and return to caller */
        theCategoryMap.put(myKey, myCombo);
        return myCombo;
    }

    /**
     * Obtain the ComboBox of accounts for a Debit for a Category Type.
     * @param pType the Category type
     * @return the ComboBox
     */
    public JComboBox<Account> getDebitAccounts(final EventCategoryType pType) {
        /* Create the key */
        String myKey = pType.getName();

        /* Look for the existing comboBox */
        JComboBox<Account> myCombo = theAccountMap.get(myKey);
        if (myCombo != null) {
            return myCombo;
        }

        AccountCategoryType myType = null;
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
            if ((!isValid)
                || (myAccount.isClosed())) {
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
     * Obtain the ComboBox of accounts for a Credit for a category Type and Debit Account.
     * @param pType the category type
     * @param pDebit the debit account
     * @return the ComboBox
     */
    public JComboBox<Account> getCreditAccounts(final EventCategoryType pType,
                                                final Account pDebit) {
        /* Create the key */
        String myKey = pType.getName()
                       + "_"
                       + pDebit.getName()
                       + "_*";

        /* Look for the existing comboBox */
        JComboBox<Account> myCombo = theAccountMap.get(myKey);
        if (myCombo != null) {
            return myCombo;
        }

        AccountCategoryType myType = null;
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
            if ((!isValid)
                || (myAccount.isClosed())) {
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
     * Obtain the ComboBox of accounts for a Debit for a category Type and Credit Account.
     * @param pType the category type
     * @param pCredit the credit account
     * @return the ComboBox
     */
    public JComboBox<Account> getDebitAccounts(final EventCategoryType pType,
                                               final Account pCredit) {
        /* Create the key */
        String myKey = pType.getName()
                       + "_*_"
                       + pCredit.getName();

        /* Look for the existing comboBox */
        JComboBox<Account> myCombo = theAccountMap.get(myKey);
        if (myCombo != null) {
            return myCombo;
        }

        AccountCategoryType myType = null;
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
            if ((!isValid)
                || (myAccount.isClosed())) {
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
