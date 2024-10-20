/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.quicken.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmoneywise.lethe.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDeposit.MoneyWiseDepositList;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityPrice;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityPrice.MoneyWiseSecurityPriceList;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransTag;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransaction.MoneyWiseTransactionList;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.MoneyWiseQIFPreference.MoneyWiseQIFPreferenceKey;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.MoneyWiseQIFPreference.MoneyWiseQIFPreferences;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.MoneyWiseQIFType;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * QIF File representation.
 */
public class MoneyWiseQIFFile {
    /**
     * Hash multiplier.
     */
    protected static final int HASH_BASE = 37;

    /**
     * Holding suffix.
     */
    protected static final String HOLDING_SUFFIX = "Holding";

    /**
     * Type of file.
     */
    private final MoneyWiseQIFType theFileType;

    /**
     * Start event Date.
     */
    private TethysDate theStartDate;

    /**
     * Last event Date.
     */
    private TethysDate theLastDate;

    /**
     * Map of Accounts with Events.
     */
    private final Map<String, MoneyWiseQIFAccountEvents> theAccountMap;

    /**
     * Sorted List of Accounts with Events.
     */
    private final List<MoneyWiseQIFAccountEvents> theAccounts;

    /**
     * Map of Payees.
     */
    private final Map<String, MoneyWiseQIFPayee> thePayeeMap;

    /**
     * Sorted List of Payees.
     */
    private final List<MoneyWiseQIFPayee> thePayees;

    /**
     * Map of Securities with Prices.
     */
    private final Map<String, MoneyWiseQIFSecurityPrices> theSecurityMap;

    /**
     * Sorted List of Securities with Prices.
     */
    private final List<MoneyWiseQIFSecurityPrices> theSecurities;

    /**
     * Map of Symbols to Securities.
     */
    private final Map<String, MoneyWiseQIFSecurity> theSymbolMap;

    /**
     * Map of Parent Categories.
     */
    private final Map<String, MoneyWiseQIFParentCategory> theParentMap;

    /**
     * Sorted List of Parent Categories.
     */
    private final List<MoneyWiseQIFParentCategory> theParentCategories;

    /**
     * Map of Categories.
     */
    private final Map<String, MoneyWiseQIFEventCategory> theCategories;

    /**
     * Map of Classes.
     */
    private final Map<String, MoneyWiseQIFClass> theClassMap;

    /**
     * Sorted List of Classes.
     */
    private final List<MoneyWiseQIFClass> theClasses;

    /**
     * Constructor.
     * @param pType the file type
     */
    public MoneyWiseQIFFile(final MoneyWiseQIFType pType) {
        /* Store file type */
        theFileType = pType;

        /* Allocate maps */
        theAccountMap = new HashMap<>();
        thePayeeMap = new HashMap<>();
        theSecurityMap = new HashMap<>();
        theSymbolMap = new HashMap<>();
        theParentMap = new HashMap<>();
        theCategories = new HashMap<>();
        theClassMap = new HashMap<>();

        /* Allocate maps */
        theAccounts = new ArrayList<>();
        thePayees = new ArrayList<>();
        theSecurities = new ArrayList<>();
        theParentCategories = new ArrayList<>();
        theClasses = new ArrayList<>();
    }

    /**
     * Obtain the file type.
     * @return the file type
     */
    public MoneyWiseQIFType getFileType() {
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
    protected Iterator<MoneyWiseQIFClass> classIterator() {
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
    protected Iterator<MoneyWiseQIFParentCategory> categoryIterator() {
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
    protected Iterator<MoneyWiseQIFAccountEvents> accountIterator() {
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
    protected Iterator<MoneyWiseQIFSecurityPrices> securityIterator() {
        return theSecurities.iterator();
    }

    /**
     * Sort the lists.
     */
    protected void sortLists() {
        /* Sort the classes */
        theClasses.sort(null);

        /* Sort the payees */
        thePayees.sort(null);

        /* Sort the categories */
        theParentCategories.sort(null);
        final Iterator<MoneyWiseQIFParentCategory> myCatIterator = categoryIterator();
        while (myCatIterator.hasNext()) {
            final MoneyWiseQIFParentCategory myParent = myCatIterator.next();

            /* Sort the children */
            myParent.sortChildren();
        }

        /* Sort the securities */
        theSecurities.sort(null);
        final Iterator<MoneyWiseQIFSecurityPrices> mySecIterator = securityIterator();
        while (mySecIterator.hasNext()) {
            final MoneyWiseQIFSecurityPrices mySecurity = mySecIterator.next();

            /* Sort the prices */
            mySecurity.sortPrices();
        }

        /* Sort the accounts */
        theAccounts.sort(null);
        final Iterator<MoneyWiseQIFAccountEvents> myAccIterator = accountIterator();
        while (myAccIterator.hasNext()) {
            final MoneyWiseQIFAccountEvents myAccount = myAccIterator.next();

            /* Sort the events */
            myAccount.sortEvents();
        }
    }

    /**
     * Build QIF File from data.
     * @param pData the data
     * @param pAnalysis the analysis
     * @param pPreferences the preferences
     * @return the QIF File
     */
    public static MoneyWiseQIFFile buildQIFFile(final MoneyWiseDataSet pData,
                                                final MoneyWiseAnalysis pAnalysis,
                                                final MoneyWiseQIFPreferences pPreferences) {
        /* Access preference details */
        final MoneyWiseQIFType myType = pPreferences.getEnumValue(MoneyWiseQIFPreferenceKey.QIFTYPE, MoneyWiseQIFType.class);
        final TethysDate myLastDate = pPreferences.getDateValue(MoneyWiseQIFPreferenceKey.LASTEVENT);

        /* Create new QIF File */
        final MoneyWiseQIFFile myFile = new MoneyWiseQIFFile(myType);

        /* Build the data for the accounts */
        myFile.buildData(pData, pAnalysis, myLastDate);
        myFile.sortLists();

        /* Return the QIF File */
        return myFile;
    }

    /**
     * Register class.
     * @param pClass the class
     * @return the QIFClass representation
     */
    public MoneyWiseQIFClass registerClass(final MoneyWiseTransTag pClass) {
        /* Locate an existing class */
        final String myName = pClass.getName();
        MoneyWiseQIFClass myClass = theClassMap.get(myName);
        if (myClass == null) {
            /* Create the new Class */
            myClass = new MoneyWiseQIFClass(this, pClass);
            theClassMap.put(myName, myClass);
            theClasses.add(myClass);
        }

        /* Return the class */
        return myClass;
    }

    /**
     * Register class.
     * @param pClass the class
     */
    public void registerClass(final MoneyWiseQIFClass pClass) {
        /* Locate an existing class */
        final String myName = pClass.getName();
        final MoneyWiseQIFClass myClass = theClassMap.get(myName);
        if (myClass == null) {
            /* Register the new Class */
            theClassMap.put(myName, pClass);
            theClasses.add(pClass);
        }
    }

    /**
     * Register category.
     * @param pCategory the category
     * @return the QIFEventCategory representation
     */
    public MoneyWiseQIFEventCategory registerCategory(final MoneyWiseTransCategory pCategory) {
        /* Locate an existing category */
        final String myName = pCategory.getName();
        MoneyWiseQIFEventCategory myCat = theCategories.get(myName);
        if (myCat == null) {
            /* Create the new Category and add to the map */
            myCat = new MoneyWiseQIFEventCategory(this, pCategory);
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
    private void registerCategoryToParent(final MoneyWiseTransCategory pParent,
                                          final MoneyWiseQIFEventCategory pCategory) {
        /* Locate an existing parent category */
        final String myName = pParent.getName();
        MoneyWiseQIFParentCategory myParent = theParentMap.get(myName);
        if (myParent == null) {
            /* Create the new Parent Category */
            myParent = new MoneyWiseQIFParentCategory(this, pParent);
            theParentMap.put(myName, myParent);
            theParentCategories.add(myParent);
        }

        /* Register the category */
        myParent.registerChild(pCategory);
    }

    /**
     * Register category.
     * @param pCategory the category
     */
    public void registerCategory(final MoneyWiseQIFEventCategory pCategory) {
        /* Locate an existing category */
        final String myName = pCategory.getName();
        final MoneyWiseQIFEventCategory myCat = theCategories.get(myName);
        if (myCat == null) {
            /* Locate parent separator */
            final int myPos = myName.indexOf(MoneyWiseTransCategory.STR_SEP);

            /* If this is a parent category */
            if (myPos < 0) {
                /* Create the new Parent Category */
                final MoneyWiseQIFParentCategory myParent = new MoneyWiseQIFParentCategory(pCategory);
                theParentMap.put(myName, myParent);
                theParentCategories.add(myParent);

                /* else this is a standard category */
            } else {
                /* Register the new category */
                theCategories.put(myName, pCategory);

                /* Determine parent name */
                final String myParentName = myName.substring(0, myPos);

                /* Locate an existing parent category */
                final MoneyWiseQIFParentCategory myParent = theParentMap.get(myParentName);

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
    public MoneyWiseQIFAccountEvents registerAccount(final MoneyWiseTransAsset pAccount) {
        /* Locate an existing account */
        final String myName = pAccount.getName();
        MoneyWiseQIFAccountEvents myAccount = theAccountMap.get(myName);
        if (myAccount == null) {
            /* Create the new Account and add to the map and list */
            myAccount = new MoneyWiseQIFAccountEvents(this, pAccount);
            theAccountMap.put(myName, myAccount);
            theAccounts.add(myAccount);
        }

        /* Return the account */
        return myAccount;
    }

    /**
     * Register holding account.
     * @param pPortfolio the portfolio
     * @return the QIFAccount representation
     */
    public MoneyWiseQIFAccountEvents registerHoldingAccount(final MoneyWisePortfolio pPortfolio) {
        /* Locate an existing account */
        final String myName = pPortfolio.getName() + HOLDING_SUFFIX;
        MoneyWiseQIFAccountEvents myAccount = theAccountMap.get(myName);
        if (myAccount == null) {
            /* Create the new Account and add to the map and list */
            myAccount = new MoneyWiseQIFAccountEvents(this, myName);
            theAccountMap.put(myName, myAccount);
            theAccounts.add(myAccount);
        }

        /* Return the account */
        return myAccount;
    }

    /**
     * Register account.
     * @param pAccount the account
     * @return the QIFAccount representation
     */
    public MoneyWiseQIFAccountEvents registerAccount(final MoneyWiseQIFAccount pAccount) {
        /* Locate an existing account */
        final String myName = pAccount.getName();
        MoneyWiseQIFAccountEvents myAccount = theAccountMap.get(myName);
        if (myAccount == null) {
            /* Create the new Account and add to the map/list */
            myAccount = new MoneyWiseQIFAccountEvents(pAccount);
            theAccountMap.put(myName, myAccount);
            theAccounts.add(myAccount);
        }

        /* Return the account */
        return myAccount;
    }

    /**
     * Register payee.
     * @param pPayee the payee
     * @return the QIFPayee representation
     */
    public MoneyWiseQIFPayee registerPayee(final MoneyWisePayee pPayee) {
        /* Locate an existing payee */
        final String myName = pPayee.getName();
        MoneyWiseQIFPayee myPayee = thePayeeMap.get(myName);
        if (myPayee == null) {
            /* Create the new Payee and add to the map and list */
            myPayee = new MoneyWiseQIFPayee(pPayee);
            thePayeeMap.put(myName, myPayee);
            thePayees.add(myPayee);
        }

        /* Return the payee */
        return myPayee;
    }

    /**
     * Register payee.
     * @param pPayee the payee
     * @return the QIFPayee representation
     */
    public MoneyWiseQIFPayee registerPayee(final String pPayee) {
        /* Locate an existing payee */
        MoneyWiseQIFPayee myPayee = thePayeeMap.get(pPayee);
        if (myPayee == null) {
            /* Create the new Payee and add to the map and list */
            myPayee = new MoneyWiseQIFPayee(pPayee);
            thePayeeMap.put(pPayee, myPayee);
            thePayees.add(myPayee);
        }

        /* Return the payee */
        return myPayee;
    }

    /**
     * Register security.
     * @param pSecurity the security
     * @return the QIFSecurity representation
     */
    public MoneyWiseQIFSecurity registerSecurity(final MoneyWiseSecurity pSecurity) {
        /* Locate an existing security */
        final String myName = pSecurity.getName();
        MoneyWiseQIFSecurityPrices mySecurity = theSecurityMap.get(myName);
        if (mySecurity == null) {
            /* Create the new Security and add to the maps/list */
            mySecurity = new MoneyWiseQIFSecurityPrices(this, pSecurity);
            theSecurityMap.put(myName, mySecurity);
            theSymbolMap.put(pSecurity.getSymbol(), mySecurity.getSecurity());
            theSecurities.add(mySecurity);
        }

        /* Return the security */
        return mySecurity.getSecurity();
    }

    /**
     * Register security.
     * @param pSecurity the security
     */
    public void registerSecurity(final MoneyWiseQIFSecurity pSecurity) {
        /* Locate an existing security */
        final String myName = pSecurity.getName();
        MoneyWiseQIFSecurityPrices mySecurity = theSecurityMap.get(myName);
        if (mySecurity == null) {
            /* Create the new Security and add to the map */
            mySecurity = new MoneyWiseQIFSecurityPrices(this, pSecurity);
            theSecurityMap.put(myName, mySecurity);
            theSymbolMap.put(pSecurity.getSymbol(), mySecurity.getSecurity());
            theSecurities.add(mySecurity);
        }
    }

    /**
     * Register price.
     * @param pPrice the price
     */
    public void registerPrice(final MoneyWiseSecurityPrice pPrice) {
        /* Locate an existing security price list */
        final MoneyWiseSecurity mySecurity = pPrice.getSecurity();
        final MoneyWiseQIFSecurityPrices mySecurityList = theSecurityMap.get(mySecurity.getName());
        if (mySecurityList != null) {
            /* Add price to the list */
            mySecurityList.addPrice(pPrice);
        }
    }

    /**
     * Register price.
     * @param pPrice the price
     */
    public void registerPrice(final MoneyWiseQIFPrice pPrice) {
        /* Locate an existing security price list */
        final MoneyWiseQIFSecurity mySecurity = pPrice.getSecurity();
        final MoneyWiseQIFSecurityPrices mySecurityList = theSecurityMap.get(mySecurity.getName());
        if (mySecurityList != null) {
            /* Loop through the prices */
            final Iterator<MoneyWiseQIFPrice> myIterator = pPrice.priceIterator();
            while (myIterator.hasNext()) {
                final MoneyWiseQIFPrice myPrice = myIterator.next();

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
    protected MoneyWiseQIFEventCategory getCategory(final String pName) {
        /* Lookup the category */
        return theCategories.get(pName);
    }

    /**
     * Obtain account.
     * @param pName the name of the account
     * @return the account
     */
    protected MoneyWiseQIFAccount getAccount(final String pName) {
        /* Lookup the security */
        final MoneyWiseQIFAccountEvents myAccount = getAccountEvents(pName);
        return (myAccount == null)
                ? null
                : myAccount.getAccount();
    }

    /**
     * Obtain account events.
     * @param pName the name of the account
     * @return the account
     */
    protected MoneyWiseQIFAccountEvents getAccountEvents(final String pName) {
        /* Lookup the account */
        return theAccountMap.get(pName);
    }

    /**
     * Obtain security.
     * @param pName the name of the security
     * @return the security
     */
    protected MoneyWiseQIFSecurity getSecurity(final String pName) {
        /* Lookup the security */
        final MoneyWiseQIFSecurityPrices myList = getSecurityPrices(pName);
        return myList == null
                ? null
                : myList.getSecurity();
    }

    /**
     * Obtain security by Symbol.
     * @param pSymbol the symbol of the security
     * @return the security
     */
    protected MoneyWiseQIFSecurity getSecurityBySymbol(final String pSymbol) {
        /* Lookup the security */
        return theSymbolMap.get(pSymbol);
    }

    /**
     * Obtain security prices.
     * @param pName the name of the security
     * @return the security
     */
    protected MoneyWiseQIFSecurityPrices getSecurityPrices(final String pName) {
        /* Lookup the security */
        return theSecurityMap.get(pName);
    }

    /**
     * Obtain class.
     * @param pName the name of the class
     * @return the class
     */
    protected MoneyWiseQIFClass getClass(final String pName) {
        /* Lookup the class */
        return theClassMap.get(pName);
    }

    /**
     * Build data.
     * @param pData the data
     * @param pAnalysis the analysis
     * @param pLastDate the last date
     */
    public void buildData(final MoneyWiseDataSet pData,
                          final MoneyWiseAnalysis pAnalysis,
                          final TethysDate pLastDate) {
        /* Create a builder */
        final MoneyWiseQIFBuilder myBuilder = new MoneyWiseQIFBuilder(this, pData, pAnalysis);

        /* Store dates */
        theStartDate = pData.getDateRange().getStart();
        theLastDate = pLastDate;

        /* Build opening balances */
        buildOpeningBalances(myBuilder, pData.getDeposits());

        /* Loop through the events */
        final MoneyWiseTransactionList myEvents = pData.getTransactions();
        final Iterator<MoneyWiseTransaction> myIterator = myEvents.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseTransaction myEvent = myIterator.next();

            /* Break loop if the event is too late */
            final TethysDate myDate = myEvent.getDate();
            if (myDate.compareTo(pLastDate) > 0) {
                break;
            }

            /* Process the event */
            myBuilder.processEvent(myEvent);
        }

        /* Build prices for securities */
        buildPrices(pData.getSecurityPrices());
    }

    /**
     * Build opening balances.
     * @param pBuilder the builder
     * @param pDepositList the deposit list
     */
    private void buildOpeningBalances(final MoneyWiseQIFBuilder pBuilder,
                                      final MoneyWiseDepositList pDepositList) {
        /* Loop through the prices */
        final Iterator<MoneyWiseDeposit> myIterator = pDepositList.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseDeposit myDeposit = myIterator.next();

            /* Ignore if no opening balance */
            final TethysMoney myBalance = myDeposit.getOpeningBalance();
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
    private void buildPrices(final MoneyWiseSecurityPriceList pPriceList) {
        /* Loop through the prices */
        final Iterator<MoneyWiseSecurityPrice> myIterator = pPriceList.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseSecurityPrice myPrice = myIterator.next();

            /* Break loop if the price is too late */
            final TethysDate myDate = myPrice.getDate();
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
        if (!(pThat instanceof MoneyWiseQIFFile)) {
            return false;
        }

        /* Cast correctly */
        final MoneyWiseQIFFile myThat = (MoneyWiseQIFFile) pThat;

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
        int myResult = MoneyWiseQIFFile.HASH_BASE * theFileType.hashCode();
        myResult += theClasses.hashCode();
        myResult *= MoneyWiseQIFFile.HASH_BASE;
        myResult += theParentCategories.hashCode();
        myResult *= MoneyWiseQIFFile.HASH_BASE;
        myResult += theSecurities.hashCode();
        myResult *= MoneyWiseQIFFile.HASH_BASE;
        myResult += thePayees.hashCode();
        myResult *= MoneyWiseQIFFile.HASH_BASE;
        myResult += theAccounts.hashCode();
        return myResult;
    }
}
