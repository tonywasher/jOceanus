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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JComboBox;

import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.Account.AccountList;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory.EventCategoryList;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.views.View;

/**
 * ComboBox selection class.
 * @author Tony Washer
 */
public class ComboSelect {
    /**
     * The JComboBox for the whole set of categories.
     */
    private final JComboBox<EventCategory> theCategoryBox;

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
    private final Map<String, JComboBox<EventCategory>> theCategoryMap;

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

        /* Allocate the categories box */
        theCategoryBox = new JComboBox<EventCategory>();

        /* Allocate the maps */
        theCategoryMap = new HashMap<String, JComboBox<EventCategory>>();
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
        if (theCategoryBox.getItemCount() > 0) {
            /* Clear categories */
            theCategoryBox.removeAllItems();
        }

        /* Access the categories */
        EventCategoryList myList = theData.getEventCategories();

        /* Create the iterator */
        Iterator<EventCategory> myIterator = myList.iterator();

        /* Loop through the Categories */
        while (myIterator.hasNext()) {
            EventCategory myCat = myIterator.next();

            /* Skip hidden/disabled values */
            if ((myCat.getCategoryTypeClass().isHiddenType())
                || (!myCat.getCategoryType().getEnabled())) {
                continue;
            }

            /* Add the item to the list */
            theCategoryBox.addItem(myCat);
        }
    }

    /**
     * Obtain the pure category ComboBox.
     * @return a ComboBox with all the categories
     */
    public JComboBox<EventCategory> getAllCategories() {
        /* return to caller */
        return theCategoryBox;
    }

    /**
     * Obtain the ComboBox of categories for a Credit to an Account.
     * @param pAccount the account
     * @return the ComboBox
     */
    public JComboBox<EventCategory> getCreditCategories(final Account pAccount) {
        /* Create the key */
        String myKey = "*_"
                       + pAccount.getName();

        /* Look for the existing comboBox */
        JComboBox<EventCategory> myCombo = theCategoryMap.get(myKey);
        if (myCombo != null) {
            return myCombo;
        }

        /* Create the iterator */
        EventCategoryList myList = theData.getEventCategories();
        Iterator<EventCategory> myIterator = myList.iterator();

        /* Create the ComboBox */
        myCombo = new JComboBox<EventCategory>();

        /* Loop through the Categories */
        while (myIterator.hasNext()) {
            EventCategory myCat = myIterator.next();

            /* Skip hidden/disabled values */
            if ((myCat.getCategoryTypeClass().isHiddenType())
                || (!myCat.getCategoryType().getEnabled())) {
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
     * Obtain the ComboBox of categories for a Debit from an Account.
     * @param pAccount the account
     * @return the ComboBox
     */
    public JComboBox<EventCategory> getDebitCategories(final Account pAccount) {
        /* Create the key */
        String myKey = pAccount.getName()
                       + "_*";

        /* Look for the existing comboBox */
        JComboBox<EventCategory> myCombo = theCategoryMap.get(myKey);
        if (myCombo != null) {
            return myCombo;
        }

        /* Create the iterator */
        EventCategoryList myList = theData.getEventCategories();
        Iterator<EventCategory> myIterator = myList.listIterator();

        /* Create the ComboBox */
        myCombo = new JComboBox<EventCategory>();

        /* Loop through the Categories */
        while (myIterator.hasNext()) {
            EventCategory myCat = myIterator.next();

            /* Skip hidden/disabled values */
            if ((myCat.getCategoryTypeClass().isHiddenType())
                || (!myCat.getCategoryType().getEnabled())) {
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
     * Obtain the ComboBox of accounts for a Debit for a Category.
     * @param pCategory the Category
     * @return the ComboBox
     */
    public JComboBox<Account> getDebitAccounts(final EventCategory pCategory) {
        /* Create the key */
        String myKey = pCategory.getName();

        /* Look for the existing comboBox */
        JComboBox<Account> myCombo = theAccountMap.get(myKey);
        if (myCombo != null) {
            return myCombo;
        }

        AccountCategory myActCat = null;
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
            if (!Difference.isEqual(myActCat, myAccount.getAccountCategory())) {
                /* Note the account category */
                myActCat = myAccount.getAccountCategory();

                /* Determine whether we are a valid category */
                isValid = Event.isValidEvent(pCategory.getCategoryTypeClass(), myActCat, false);
            }

            /* Skip invalid types/closed accounts */
            if ((!isValid)
                || (myAccount.isClosed())) {
                continue;
            }

            /* Obtain the credit comboList */
            JComboBox<Account> mySubCombo = getCreditAccounts(pCategory, myAccount);

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
     * Obtain the ComboBox of accounts for a Credit for a category and Debit Account.
     * @param pCategory the category
     * @param pDebit the debit account
     * @return the ComboBox
     */
    public JComboBox<Account> getCreditAccounts(final EventCategory pCategory,
                                                final Account pDebit) {
        /* Create the key */
        String myKey = pCategory.getName()
                       + "_"
                       + pDebit.getName()
                       + "_*";

        /* Look for the existing comboBox */
        JComboBox<Account> myCombo = theAccountMap.get(myKey);
        if (myCombo != null) {
            return myCombo;
        }

        AccountCategory myActCat = null;
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
            if (!Difference.isEqual(myActCat, myAccount.getAccountCategory())) {
                /* Note the account category */
                myActCat = myAccount.getAccountCategory();

                /* Determine whether we are a valid type */
                isValid = Event.isValidEvent(pCategory.getCategoryTypeClass(), myActCat, true);
            }

            /* Skip invalid types/closed accounts */
            if ((!isValid)
                || (myAccount.isClosed())) {
                continue;
            }

            /* If the account is identical to the selected account */
            if (Difference.isEqual(myAccount, pDebit)) {
                /* If this combination is allowed */
                if (Event.isValidEvent(pCategory, pDebit, myAccount)) {
                    /* Add to beginning of list */
                    myCombo.insertItemAt(myAccount, 0);
                }

                /* else it is a different account */
            } else {
                /* If this combination is allowed */
                if (Event.isValidEvent(pCategory, pDebit, myAccount)) {
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
     * Obtain the ComboBox of accounts for a Debit for a category and Credit Account.
     * @param pCategory the category
     * @param pCredit the credit account
     * @return the ComboBox
     */
    public JComboBox<Account> getDebitAccounts(final EventCategory pCategory,
                                               final Account pCredit) {
        /* Create the key */
        String myKey = pCategory.getName()
                       + "_*_"
                       + pCredit.getName();

        /* Look for the existing comboBox */
        JComboBox<Account> myCombo = theAccountMap.get(myKey);
        if (myCombo != null) {
            return myCombo;
        }

        AccountCategory myActCat = null;
        boolean isValid = false;

        /* Access the iterator */
        AccountList myList = theData.getAccounts();
        Iterator<Account> myIterator = myList.listIterator();

        /* Create the ComboBox */
        myCombo = new JComboBox<Account>();

        /* Loop through the accounts */
        while (myIterator.hasNext()) {
            Account myAccount = myIterator.next();

            /* If the category of this account is new */
            if (!Difference.isEqual(myActCat, myAccount.getAccountCategory())) {
                /* Note the account category */
                myActCat = myAccount.getAccountCategory();

                /* Determine whether we are a valid category */
                isValid = Event.isValidEvent(pCategory.getCategoryTypeClass(), myActCat, false);
            }

            /* Skip invalid types/closed accounts */
            if ((!isValid)
                || (myAccount.isClosed())) {
                continue;
            }

            /* If the account is identical to the selected account */
            if (Difference.isEqual(myAccount, pCredit)) {
                /* If this combination is allowed */
                if (Event.isValidEvent(pCategory, myAccount, pCredit)) {
                    /* Add to beginning of list */
                    myCombo.insertItemAt(myAccount, 0);
                }

                /* else it is a different account */
            } else {
                /* If this combination is allowed */
                if (Event.isValidEvent(pCategory, myAccount, pCredit)) {
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
