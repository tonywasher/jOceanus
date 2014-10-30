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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.list.OrderedList;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice.SecurityPriceList;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionAsset;
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
     * Hash multiplier.
     */
    protected static final int HASH_BASE = 37;

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
    private final Map<String, QIFAccountEvents> theAccountMap;

    /**
     * Sorted List of Accounts with Events.
     */
    private final OrderedList<QIFAccountEvents> theAccounts;

    /**
     * Map of Payees.
     */
    private final Map<String, QIFPayee> thePayeeMap;

    /**
     * Sorted List of Payees.
     */
    private final OrderedList<QIFPayee> thePayees;

    /**
     * Map of Securities with Prices.
     */
    private final Map<String, QIFSecurityPrices> theSecurityMap;

    /**
     * Sorted List of Securities with Prices.
     */
    private final OrderedList<QIFSecurityPrices> theSecurities;

    /**
     * Map of Symbols to Securities.
     */
    private final Map<String, QIFSecurity> theSymbolMap;

    /**
     * Map of Parent Categories.
     */
    private final Map<String, QIFParentCategory> theParentMap;

    /**
     * Sorted List of Parent Categories.
     */
    private final OrderedList<QIFParentCategory> theParentCategories;

    /**
     * Map of Categories.
     */
    private final Map<String, QIFEventCategory> theCategories;

    /**
     * Map of Classes.
     */
    private final Map<String, QIFClass> theClassMap;

    /**
     * Sorted List of Classes.
     */
    private final OrderedList<QIFClass> theClasses;

    /**
     * Obtain the file type.
     * @return the file type
     */
    public QIFType getFileType() {
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
        return theClasses.iterator();
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
        return theParentCategories.iterator();
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
        return theAccounts.iterator();
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
        return theSecurities.iterator();
    }

    /**
     * Constructor.
     * @param pType the file type
     */
    public QIFFile(final QIFType pType) {
        /* Store file type */
        theFileType = pType;

        /* Allocate maps */
        theAccountMap = new HashMap<String, QIFAccountEvents>();
        thePayeeMap = new HashMap<String, QIFPayee>();
        theSecurityMap = new HashMap<String, QIFSecurityPrices>();
        theSymbolMap = new HashMap<String, QIFSecurity>();
        theParentMap = new HashMap<String, QIFParentCategory>();
        theCategories = new HashMap<String, QIFEventCategory>();
        theClassMap = new HashMap<String, QIFClass>();

        /* Allocate maps */
        theAccounts = new OrderedList<QIFAccountEvents>(QIFAccountEvents.class);
        thePayees = new OrderedList<QIFPayee>(QIFPayee.class);
        theSecurities = new OrderedList<QIFSecurityPrices>(QIFSecurityPrices.class);
        theParentCategories = new OrderedList<QIFParentCategory>(QIFParentCategory.class);
        theClasses = new OrderedList<QIFClass>(QIFClass.class);
    }

    /**
     * Sort the lists.
     */
    protected void sortLists() {
        /* Sort the classes */
        theClasses.reSort();

        /* Sort the payees */
        thePayees.reSort();

        /* Sort the categories */
        theParentCategories.reSort();
        Iterator<QIFParentCategory> myCatIterator = categoryIterator();
        while (myCatIterator.hasNext()) {
            QIFParentCategory myParent = myCatIterator.next();

            /* Sort the children */
            myParent.sortChildren();
        }

        /* Sort the securities */
        theSecurities.reSort();
        Iterator<QIFSecurityPrices> mySecIterator = securityIterator();
        while (mySecIterator.hasNext()) {
            QIFSecurityPrices mySecurity = mySecIterator.next();

            /* Sort the prices */
            mySecurity.sortPrices();
        }

        /* Sort the accounts */
        theAccounts.reSort();
        Iterator<QIFAccountEvents> myAccIterator = accountIterator();
        while (myAccIterator.hasNext()) {
            QIFAccountEvents myAccount = myAccIterator.next();

            /* Sort the events */
            myAccount.sortEvents();
        }
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
        myFile.sortLists();

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
        String myName = pClass.getName();
        QIFClass myClass = theClassMap.get(myName);
        if (myClass == null) {
            /* Create the new Class */
            myClass = new QIFClass(this, pClass);
            theClassMap.put(myName, myClass);
            theClasses.append(myClass);
        }

        /* Return the class */
        return myClass;
    }

    /**
     * Register class.
     * @param pClass the class
     */
    public void registerClass(final QIFClass pClass) {
        /* Locate an existing class */
        String myName = pClass.getName();
        QIFClass myClass = theClassMap.get(myName);
        if (myClass == null) {
            /* Register the new Class */
            theClassMap.put(myName, pClass);
            theClasses.append(pClass);
        }
    }

    /**
     * Register category.
     * @param pCategory the category
     * @return the QIFEventCategory representation
     */
    public QIFEventCategory registerCategory(final TransactionCategory pCategory) {
        /* Locate an existing category */
        String myName = pCategory.getName();
        QIFEventCategory myCat = theCategories.get(myName);
        if (myCat == null) {
            /* Create the new Category and add to the map */
            myCat = new QIFEventCategory(this, pCategory);
            theCategories.put(myName, myCat);

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
        String myName = pParent.getName();
        QIFParentCategory myParent = theParentMap.get(myName);
        if (myParent == null) {
            /* Create the new Parent Category */
            myParent = new QIFParentCategory(this, pParent);
            theParentMap.put(myName, myParent);
            theParentCategories.append(myParent);
        }

        /* Register the category */
        myParent.registerChild(pCategory);
    }

    /**
     * Register category.
     * @param pCategory the category
     */
    public void registerCategory(final QIFEventCategory pCategory) {
        /* Locate an existing category */
        String myName = pCategory.getName();
        QIFEventCategory myCat = theCategories.get(myName);
        if (myCat == null) {
            /* Locate parent separator */
            int myPos = myName.indexOf(TransactionCategory.STR_SEP);

            /* If this is a parent category */
            if (myPos < 0) {
                /* Create the new Parent Category */
                QIFParentCategory myParent = new QIFParentCategory(pCategory);
                theParentMap.put(myName, myParent);
                theParentCategories.append(myParent);

                /* else this is a standard category */
            } else {
                /* Register the new category */
                theCategories.put(myName, pCategory);

                /* Determine parent name */
                String myParentName = myName.substring(0, myPos);

                /* Locate an existing parent category */
                QIFParentCategory myParent = theParentMap.get(myParentName);

                /* Register against parent */
                myParent.registerChild(pCategory);
            }
        }
    }

    /**
     * Register account.
     * @param pAccount the account
     * @return the QIFAccount representation
     */
    public QIFAccountEvents registerAccount(final TransactionAsset pAccount) {
        /* Locate an existing account */
        String myName = pAccount.getName();
        QIFAccountEvents myAccount = theAccountMap.get(myName);
        if (myAccount == null) {
            /* Create the new Account and add to the map and list */
            myAccount = new QIFAccountEvents(this, pAccount);
            theAccountMap.put(myName, myAccount);
            theAccounts.append(myAccount);
        }

        /* Return the account */
        return myAccount;
    }

    /**
     * Register account.
     * @param pAccount the account
     * @return the QIFAccount representation
     */
    public QIFAccountEvents registerAccount(final QIFAccount pAccount) {
        /* Locate an existing account */
        String myName = pAccount.getName();
        QIFAccountEvents myAccount = theAccountMap.get(myName);
        if (myAccount == null) {
            /* Create the new Account and add to the map/list */
            myAccount = new QIFAccountEvents(pAccount);
            theAccountMap.put(myName, myAccount);
            theAccounts.append(myAccount);
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
        String myName = pPayee.getName();
        QIFPayee myPayee = thePayeeMap.get(myName);
        if (myPayee == null) {
            /* Create the new Payee and add to the map and list */
            myPayee = new QIFPayee(pPayee);
            thePayeeMap.put(myName, myPayee);
            thePayees.append(myPayee);
        }

        /* Return the payee */
        return myPayee;
    }

    /**
     * Register payee.
     * @param pPayee the payee
     * @return the QIFPayee representation
     */
    public QIFPayee registerPayee(final String pPayee) {
        /* Locate an existing payee */
        QIFPayee myPayee = thePayeeMap.get(pPayee);
        if (myPayee == null) {
            /* Create the new Payee and add to the map and list */
            myPayee = new QIFPayee(pPayee);
            thePayeeMap.put(pPayee, myPayee);
            thePayees.append(myPayee);
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
        String myName = pSecurity.getName();
        QIFSecurityPrices mySecurity = theSecurityMap.get(myName);
        if (mySecurity == null) {
            /* Create the new Security and add to the maps/list */
            mySecurity = new QIFSecurityPrices(this, pSecurity);
            theSecurityMap.put(myName, mySecurity);
            theSymbolMap.put(pSecurity.getSymbol(), mySecurity.getSecurity());
            theSecurities.append(mySecurity);
        }

        /* Return the security */
        return mySecurity.getSecurity();
    }

    /**
     * Register security.
     * @param pSecurity the security
     */
    public void registerSecurity(final QIFSecurity pSecurity) {
        /* Locate an existing security */
        String myName = pSecurity.getName();
        QIFSecurityPrices mySecurity = theSecurityMap.get(myName);
        if (mySecurity == null) {
            /* Create the new Security and add to the map */
            mySecurity = new QIFSecurityPrices(this, pSecurity);
            theSecurityMap.put(myName, mySecurity);
            theSymbolMap.put(pSecurity.getSymbol(), mySecurity.getSecurity());
            theSecurities.append(mySecurity);
        }
    }

    /**
     * Register price.
     * @param pPrice the price
     */
    public void registerPrice(final SecurityPrice pPrice) {
        /* Locate an existing security price list */
        Security mySecurity = pPrice.getSecurity();
        QIFSecurityPrices mySecurityList = theSecurityMap.get(mySecurity.getName());
        if (mySecurityList != null) {
            /* Add price to the list */
            mySecurityList.addPrice(pPrice);
        }
    }

    /**
     * Register price.
     * @param pPrice the price
     */
    public void registerPrice(final QIFPrice pPrice) {
        /* Locate an existing security price list */
        QIFSecurity mySecurity = pPrice.getSecurity();
        QIFSecurityPrices mySecurityList = theSecurityMap.get(mySecurity.getName());
        if (mySecurityList != null) {
            /* Loop through the prices */
            Iterator<QIFPrice> myIterator = pPrice.priceIterator();
            while (myIterator.hasNext()) {
                QIFPrice myPrice = myIterator.next();

                /* Add price to the list */
                mySecurityList.addPrice(myPrice);
            }
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
        return theAccountMap.get(pName);
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
     * Obtain security by Symbol.
     * @param pSymbol the symbol of the security
     * @return the security
     */
    protected QIFSecurity getSecurityBySymbol(final String pSymbol) {
        /* Lookup the security */
        return theSymbolMap.get(pSymbol);
    }

    /**
     * Obtain security prices.
     * @param pName the name of the security
     * @return the security
     */
    protected QIFSecurityPrices getSecurityPrices(final String pName) {
        /* Lookup the security */
        return theSecurityMap.get(pName);
    }

    /**
     * Obtain class.
     * @param pName the name of the class
     * @return the class
     */
    protected QIFClass getClass(final String pName) {
        /* Lookup the class */
        return theClassMap.get(pName);
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

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check class */
        if (!(pThat instanceof QIFFile)) {
            return false;
        }

        /* Cast correctly */
        QIFFile myThat = (QIFFile) pThat;

        /* Check file type */
        if (!theFileType.equals(myThat.theFileType)) {
            return false;
        }

        /* Check class list */
        if (!theClasses.equals(myThat.theClasses)) {
            return false;
        }

        /* Check parent categories */
        if (!theParentCategories.equals(myThat.theParentCategories)) {
            return false;
        }

        /* Check securities list */
        if (!theSecurities.equals(myThat.theSecurities)) {
            return false;
        }

        /* Check payees list */
        if (!thePayees.equals(myThat.thePayees)) {
            return false;
        }

        /* Check accounts */
        return theAccounts.equals(myThat.theAccounts);
    }

    @Override
    public int hashCode() {
        int myResult = QIFFile.HASH_BASE * theFileType.hashCode();
        myResult += theClasses.hashCode();
        myResult *= QIFFile.HASH_BASE;
        myResult += theParentCategories.hashCode();
        myResult *= QIFFile.HASH_BASE;
        myResult += theSecurities.hashCode();
        myResult *= QIFFile.HASH_BASE;
        myResult += thePayees.hashCode();
        myResult *= QIFFile.HASH_BASE;
        myResult += theAccounts.hashCode();
        return myResult;
    }
}
