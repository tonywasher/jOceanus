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
package net.sourceforge.joceanus.jmoneywise.quicken.file;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice.SecurityPriceList;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QIFPreference;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QIFType;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;

/**
 * QIF File representation.
 */
public class QIFFile {
    /**
     * Type of file.
     */
    private final QIFType theFileType;

    /**
     * Start event Date.
     */
    private JDateDay theStartDate;

    /**
     * Last event Date.
     */
    private JDateDay theLastDate;

    /**
     * Map of Accounts with Events.
     */
    private final Map<String, QIFAccountEvents> theAccounts;

    /**
     * Map of Payees.
     */
    private final Map<String, QIFPayee> thePayees;

    /**
     * Map of Securities with Prices.
     */
    private final Map<String, QIFSecurityPrices> theSecurities;

    /**
     * Map of Parent Categories.
     */
    private final Map<String, QIFParentCategory> theParentCategories;

    /**
     * Map of Categories.
     */
    private final Map<String, QIFEventCategory> theCategories;

    /**
     * Map of Classes.
     */
    private final Map<String, QIFClass> theClasses;

    /**
     * Obtain the file type.
     * @return the file type
     */
    protected QIFType getFileType() {
        return theFileType;
    }

    /**
     * Does the file have classes?
     * @return true/false
     */
    protected boolean hasClasses() {
        return !theClasses.isEmpty();
    }

    /**
     * Obtain the number of class.
     * @return the number
     */
    protected int numClasses() {
        return theClasses.size();
    }

    /**
     * Obtain the classes iterator.
     * @return the iterator
     */
    protected Iterator<QIFClass> classIterator() {
        return theClasses.values().iterator();
    }

    /**
     * Obtain the number of categories.
     * @return the number
     */
    protected int numCategories() {
        return theCategories.size();
    }

    /**
     * Obtain the category iterator.
     * @return the iterator
     */
    protected Iterator<QIFParentCategory> categoryIterator() {
        return theParentCategories.values().iterator();
    }

    /**
     * Obtain the number of accounts.
     * @return the number
     */
    protected int numAccounts() {
        return theAccounts.size();
    }

    /**
     * Obtain the account iterator.
     * @return the iterator
     */
    protected Iterator<QIFAccountEvents> accountIterator() {
        return theAccounts.values().iterator();
    }

    /**
     * Does the file have securities?
     * @return true/false
     */
    protected boolean hasSecurities() {
        return !theSecurities.isEmpty();
    }

    /**
     * Obtain the number of securities.
     * @return the number
     */
    protected int numSecurities() {
        return theSecurities.size();
    }

    /**
     * Obtain the account iterator.
     * @return the iterator
     */
    protected Iterator<QIFSecurityPrices> securityIterator() {
        return theSecurities.values().iterator();
    }

    /**
     * Constructor.
     * @param pType the file type
     */
    public QIFFile(final QIFType pType) {
        /* Store file type */
        theFileType = pType;

        /* Allocate maps */
        theAccounts = new LinkedHashMap<String, QIFAccountEvents>();
        thePayees = new LinkedHashMap<String, QIFPayee>();
        theSecurities = new LinkedHashMap<String, QIFSecurityPrices>();
        theParentCategories = new LinkedHashMap<String, QIFParentCategory>();
        theCategories = new LinkedHashMap<String, QIFEventCategory>();
        theClasses = new LinkedHashMap<String, QIFClass>();
    }

    /**
     * Build QIF File from data.
     * @param pView the view
     * @param pPreferences the preferences
     * @return the QIF File
     */
    public static QIFFile buildQIFFile(final View pView,
                                       final QIFPreference pPreferences) {
        /* Access preference details */
        QIFType myType = pPreferences.getEnumValue(QIFPreference.NAME_QIFTYPE, QIFType.class);
        JDateDay myLastDate = pPreferences.getDateValue(QIFPreference.NAME_LASTEVENT);

        /* Create new QIF File */
        QIFFile myFile = new QIFFile(myType);

        /* Build the data for the accounts */
        myFile.buildData(pView, myLastDate);

        /* Return the QIF File */
        return myFile;
    }

    /**
     * Register class.
     * @param pClass the class
     * @return the QIFClass representation
     */
    public QIFClass registerClass(final TransactionTag pClass) {
        /* Locate an existing class */
        QIFClass myClass = theClasses.get(pClass.getName());
        if (myClass != null) {
            /* Create the new Class */
            myClass = new QIFClass(this, pClass);
            theClasses.put(myClass.getName(), myClass);
        }

        /* Return the class */
        return myClass;
    }

    /**
     * Register category.
     * @param pCategory the category
     * @return the QIFEventCategory representation
     */
    public QIFEventCategory registerCategory(final TransactionCategory pCategory) {
        /* Locate an existing category */
        QIFEventCategory myCat = theCategories.get(pCategory.getName());
        if (myCat == null) {
            /* Create the new Category and add to the map */
            myCat = new QIFEventCategory(this, pCategory);
            theCategories.put(myCat.getName(), myCat);

            /* Register against parent */
            registerCategoryToParent(pCategory.getParentCategory(), myCat);
        }

        /* Return the category */
        return myCat;
    }

    /**
     * Register parent category.
     * @param pParent the parent category
     * @param pCategory the QIFEventCategory to register
     */
    private void registerCategoryToParent(final TransactionCategory pParent,
                                          final QIFEventCategory pCategory) {
        /* Locate an existing parent category */
        QIFParentCategory myParent = theParentCategories.get(pParent.getName());
        if (myParent == null) {
            /* Create the new Parent Category */
            myParent = new QIFParentCategory(this, pParent);
            theParentCategories.put(pParent.getName(), myParent);
        }

        /* Register the category */
        myParent.registerChild(pCategory);
    }

    /**
     * Register account.
     * @param pAccount the account
     * @return the QIFAccount representation
     */
    public QIFAccountEvents registerAccount(final AssetBase<?> pAccount) {
        /* Locate an existing account */
        QIFAccountEvents myAccount = theAccounts.get(pAccount.getName());
        if (myAccount == null) {
            /* Create the new Account and add to the map */
            myAccount = new QIFAccountEvents(this, pAccount);
            theAccounts.put(pAccount.getName(), myAccount);
        }

        /* Return the account */
        return myAccount;
    }

    /**
     * Register payee.
     * @param pPayee the payee
     * @return the QIFPayee representation
     */
    public QIFPayee registerPayee(final Payee pPayee) {
        /* Locate an existing payee */
        QIFPayee myPayee = thePayees.get(pPayee.getName());
        if (myPayee == null) {
            /* Create the new Payee and add to the map */
            myPayee = new QIFPayee(pPayee);
            thePayees.put(myPayee.getName(), myPayee);
        }

        /* Return the payee */
        return myPayee;
    }

    /**
     * Register security.
     * @param pSecurity the security
     * @return the QIFSecurity representation
     */
    public QIFSecurity registerSecurity(final Security pSecurity) {
        /* Locate an existing security */
        QIFSecurityPrices mySecurity = theSecurities.get(pSecurity.getName());
        if (mySecurity == null) {
            /* Create the new Security and add to the map */
            mySecurity = new QIFSecurityPrices(this, pSecurity);
            theSecurities.put(pSecurity.getName(), mySecurity);
        }

        /* Return the security */
        return mySecurity.getSecurity();
    }

    /**
     * Register price.
     * @param pPrice the price
     */
    public void registerPrice(final SecurityPrice pPrice) {
        /* Locate an existing security price list */
        Security mySecurity = pPrice.getSecurity();
        QIFSecurityPrices mySecurityList = theSecurities.get(mySecurity.getName());
        if (mySecurityList != null) {
            /* Add price to the list */
            mySecurityList.addPrice(pPrice);
        }
    }

    /**
     * Obtain category.
     * @param pName the name of the category
     * @return the category
     */
    protected QIFEventCategory getCategory(final String pName) {
        /* Lookup the category */
        return theCategories.get(pName);
    }

    /**
     * Obtain account.
     * @param pName the name of the account
     * @return the account
     */
    protected QIFAccount getAccount(final String pName) {
        /* Lookup the security */
        QIFAccountEvents myAccount = getAccountEvents(pName);
        return (myAccount == null)
                                  ? null
                                  : myAccount.getAccount();
    }

    /**
     * Obtain account events.
     * @param pName the name of the account
     * @return the account
     */
    protected QIFAccountEvents getAccountEvents(final String pName) {
        /* Lookup the account */
        return theAccounts.get(pName);
    }

    /**
     * Obtain security.
     * @param pName the name of the security
     * @return the security
     */
    protected QIFSecurity getSecurity(final String pName) {
        /* Lookup the security */
        QIFSecurityPrices myList = getSecurityPrices(pName);
        return (myList == null)
                               ? null
                               : myList.getSecurity();
    }

    /**
     * Obtain security prices.
     * @param pName the name of the security
     * @return the security
     */
    protected QIFSecurityPrices getSecurityPrices(final String pName) {
        /* Lookup the security */
        return theSecurities.get(pName);
    }

    /**
     * Obtain class.
     * @param pName the name of the class
     * @return the class
     */
    protected QIFClass getClass(final String pName) {
        /* Lookup the class */
        return theClasses.get(pName);
    }

    /**
     * Build data.
     * @param pView the view
     * @param pLastDate the last date
     */
    public void buildData(final View pView,
                          final JDateDay pLastDate) {
        /* Create a builder */
        QIFBuilder myBuilder = new QIFBuilder(this, pView);

        /* Store dates */
        MoneyWiseData myData = pView.getData();
        theStartDate = myData.getDateRange().getStart();
        theLastDate = pLastDate;

        /* Build opening balances */
        buildOpeningBalances(myBuilder, myData.getDeposits());

        /* Loop through the events */
        TransactionList myEvents = myData.getTransactions();
        Iterator<Transaction> myIterator = myEvents.iterator();
        while (myIterator.hasNext()) {
            Transaction myEvent = myIterator.next();

            /* Break loop if the event is too late */
            JDateDay myDate = myEvent.getDate();
            if (myDate.compareTo(pLastDate) > 0) {
                break;
            }

            /* Process the event */
            myBuilder.processEvent(myEvent);
        }

        /* Build prices for securities */
        buildPrices(myData.getSecurityPrices());
    }

    /**
     * Build opening balances.
     * @param pBuilder the builder
     * @param pDepositList the deposit list
     */
    private void buildOpeningBalances(final QIFBuilder pBuilder,
                                      final DepositList pDepositList) {
        /* Loop through the prices */
        Iterator<Deposit> myIterator = pDepositList.iterator();
        while (myIterator.hasNext()) {
            Deposit myDeposit = myIterator.next();

            /* Ignore if no opening balance */
            JMoney myBalance = myDeposit.getOpeningBalance();
            if (myBalance == null) {
                continue;
            }

            /* Process the balance */
            pBuilder.processBalance(myDeposit, theStartDate, myBalance);
        }
    }

    /**
     * Build prices.
     * @param pPriceList the price list
     */
    private void buildPrices(final SecurityPriceList pPriceList) {
        /* Loop through the prices */
        Iterator<SecurityPrice> myIterator = pPriceList.iterator();
        while (myIterator.hasNext()) {
            SecurityPrice myPrice = myIterator.next();

            /* Break loop if the price is too late */
            JDateDay myDate = myPrice.getDate();
            if (myDate.compareTo(theLastDate) > 0) {
                break;
            }

            /* Register the price */
            registerPrice(myPrice);
        }
    }
}
