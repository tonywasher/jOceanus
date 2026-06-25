/*
 * MoneyWise: Finance Application
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.moneywise.quicken.file.atlas;

import io.github.tonywasher.joceanus.moneywise.analysis.atlas.base.MoneyWiseXAnalysisEvent;
import io.github.tonywasher.joceanus.moneywise.analysis.atlas.base.MoneyWiseXAnalysisEventList;
import io.github.tonywasher.joceanus.moneywise.analysis.atlas.base.MoneyWiseXAnalysisEventType;
import io.github.tonywasher.joceanus.moneywise.analysis.atlas.buckets.MoneyWiseXAnalysis;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseCategoryBase;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDeposit.MoneyWiseDepositList;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePayee;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurityPrice;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurityPrice.MoneyWiseSecurityPriceList;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransTag;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQIFPreference.MoneyWiseQIFPreferenceKey;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQIFPreference.MoneyWiseQIFPreferences;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQIFType;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * QIF File representation.
 */
public class MoneyWiseXQIFFile
        implements MoneyWiseXQIFRegister {
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
    private OceanusDate theStartDate;

    /**
     * Last event Date.
     */
    private OceanusDate theLastDate;

    /**
     * Map of Accounts with Events.
     */
    private final Map<String, MoneyWiseXQIFAccountEvents> theAccountMap;

    /**
     * Sorted List of Accounts with Events.
     */
    private final List<MoneyWiseXQIFAccountEvents> theAccounts;

    /**
     * Map of Payees.
     */
    private final Map<String, MoneyWiseXQIFPayee> thePayeeMap;

    /**
     * Sorted List of Payees.
     */
    private final List<MoneyWiseXQIFPayee> thePayees;

    /**
     * Map of Securities with Prices.
     */
    private final Map<String, MoneyWiseXQIFSecurityPrices> theSecurityMap;

    /**
     * Sorted List of Securities with Prices.
     */
    private final List<MoneyWiseXQIFSecurityPrices> theSecurities;

    /**
     * Map of Symbols to Securities.
     */
    private final Map<String, MoneyWiseXQIFSecurity> theSymbolMap;

    /**
     * Map of Parent Categories.
     */
    private final Map<String, MoneyWiseXQIFParentCategory> theParentMap;

    /**
     * Sorted List of Parent Categories.
     */
    private final List<MoneyWiseXQIFParentCategory> theParentCategories;

    /**
     * Map of Categories.
     */
    private final Map<String, MoneyWiseXQIFEventCategory> theCategories;

    /**
     * Map of Classes.
     */
    private final Map<String, MoneyWiseXQIFClass> theClassMap;

    /**
     * Sorted List of Classes.
     */
    private final List<MoneyWiseXQIFClass> theClasses;

    /**
     * Constructor.
     *
     * @param pType the file type
     */
    public MoneyWiseXQIFFile(final MoneyWiseQIFType pType) {
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

    @Override
    public MoneyWiseQIFType getFileType() {
        return theFileType;
    }

    /**
     * Does the file have classes?
     *
     * @return true/false
     */
    protected boolean hasClasses() {
        return !theClasses.isEmpty();
    }

    /**
     * Obtain the number of class.
     *
     * @return the number
     */
    protected int numClasses() {
        return theClasses.size();
    }

    /**
     * Obtain the classes iterator.
     *
     * @return the iterator
     */
    protected Iterator<MoneyWiseXQIFClass> classIterator() {
        return theClasses.iterator();
    }

    /**
     * Obtain the number of categories.
     *
     * @return the number
     */
    protected int numCategories() {
        return theCategories.size();
    }

    /**
     * Obtain the category iterator.
     *
     * @return the iterator
     */
    protected Iterator<MoneyWiseXQIFParentCategory> categoryIterator() {
        return theParentCategories.iterator();
    }

    /**
     * Obtain the number of accounts.
     *
     * @return the number
     */
    protected int numAccounts() {
        return theAccounts.size();
    }

    /**
     * Obtain the account iterator.
     *
     * @return the iterator
     */
    protected Iterator<MoneyWiseXQIFAccountEvents> accountIterator() {
        return theAccounts.iterator();
    }

    /**
     * Does the file have securities?
     *
     * @return true/false
     */
    protected boolean hasSecurities() {
        return !theSecurities.isEmpty();
    }

    /**
     * Obtain the number of securities.
     *
     * @return the number
     */
    protected int numSecurities() {
        return theSecurities.size();
    }

    /**
     * Obtain the account iterator.
     *
     * @return the iterator
     */
    protected Iterator<MoneyWiseXQIFSecurityPrices> securityIterator() {
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
        final Iterator<MoneyWiseXQIFParentCategory> myCatIterator = categoryIterator();
        while (myCatIterator.hasNext()) {
            final MoneyWiseXQIFParentCategory myParent = myCatIterator.next();

            /* Sort the children */
            myParent.sortChildren();
        }

        /* Sort the securities */
        theSecurities.sort(null);
        final Iterator<MoneyWiseXQIFSecurityPrices> mySecIterator = securityIterator();
        while (mySecIterator.hasNext()) {
            final MoneyWiseXQIFSecurityPrices mySecurity = mySecIterator.next();

            /* Sort the prices */
            mySecurity.sortPrices();
        }

        /* Sort the accounts */
        theAccounts.sort(null);
        final Iterator<MoneyWiseXQIFAccountEvents> myAccIterator = accountIterator();
        while (myAccIterator.hasNext()) {
            final MoneyWiseXQIFAccountEvents myAccount = myAccIterator.next();

            /* Sort the events */
            myAccount.sortEvents();
        }
    }

    /**
     * Build QIF File from data.
     *
     * @param pData        the data
     * @param pAnalysis    the analysis
     * @param pPreferences the preferences
     * @return the QIF File
     */
    public static MoneyWiseXQIFFile buildQIFFile(final MoneyWiseDataSet pData,
                                                 final MoneyWiseXAnalysis pAnalysis,
                                                 final MoneyWiseQIFPreferences pPreferences) {
        /* Access preference details */
        final MoneyWiseQIFType myType = pPreferences.getEnumValue(MoneyWiseQIFPreferenceKey.QIFTYPE, MoneyWiseQIFType.class);
        final OceanusDate myLastDate = pPreferences.getDateValue(MoneyWiseQIFPreferenceKey.LASTEVENT);

        /* Create new QIF File */
        final MoneyWiseXQIFFile myFile = new MoneyWiseXQIFFile(myType);

        /* Build the data for the accounts */
        myFile.buildData(pData, pAnalysis, myLastDate);
        myFile.sortLists();

        /* Return the QIF File */
        return myFile;
    }

    @Override
    public MoneyWiseXQIFClass registerClass(final MoneyWiseTransTag pClass) {
        /* Locate an existing class */
        final String myName = pClass.getName();
        return theClassMap.computeIfAbsent(myName, n -> {
            final MoneyWiseXQIFClass myClass = new MoneyWiseXQIFClass(pClass);
            theClasses.add(myClass);
            return myClass;
        });
    }

    @Override
    public void registerClass(final MoneyWiseXQIFClass pClass) {
        /* Locate an existing class */
        final String myName = pClass.getName();
        theClassMap.computeIfAbsent(myName, n -> {
            theClasses.add(pClass);
            return pClass;
        });
    }

    @Override
    public MoneyWiseXQIFEventCategory registerCategory(final MoneyWiseTransCategory pCategory) {
        /* Locate an existing category */
        final String myName = pCategory.getName();
        return theCategories.computeIfAbsent(myName, n -> {
            final MoneyWiseXQIFEventCategory myCat = new MoneyWiseXQIFEventCategory(pCategory);
            registerCategoryToParent(pCategory.getParentCategory(), myCat);
            return myCat;
        });
    }

    /**
     * Register parent category.
     *
     * @param pParent   the parent category
     * @param pCategory the QIFEventCategory to register
     */
    private void registerCategoryToParent(final MoneyWiseTransCategory pParent,
                                          final MoneyWiseXQIFEventCategory pCategory) {
        /* Locate an existing parent category */
        final String myName = pParent.getName();
        final MoneyWiseXQIFParentCategory myParent = theParentMap.computeIfAbsent(myName, n -> {
            final MoneyWiseXQIFParentCategory myParCat = new MoneyWiseXQIFParentCategory(pParent);
            theParentCategories.add(myParCat);
            return myParCat;
        });

        /* Register the category */
        myParent.registerChild(pCategory);
    }

    /**
     * Register category.
     *
     * @param pCategory the category
     */
    public void registerCategory(final MoneyWiseXQIFEventCategory pCategory) {
        /* Locate an existing category */
        final String myName = pCategory.getName();
        final MoneyWiseXQIFEventCategory myCat = theCategories.get(myName);
        if (myCat == null) {
            /* Locate parent separator */
            final int myPos = myName.indexOf(MoneyWiseCategoryBase.STR_SEP);

            /* If this is a parent category */
            if (myPos < 0) {
                /* Create the new Parent Category */
                final MoneyWiseXQIFParentCategory myParent = new MoneyWiseXQIFParentCategory(pCategory);
                theParentMap.put(myName, myParent);
                theParentCategories.add(myParent);

                /* else this is a standard category */
            } else {
                /* Register the new category */
                theCategories.put(myName, pCategory);

                /* Determine parent name */
                final String myParentName = myName.substring(0, myPos);

                /* Locate an existing parent category */
                final MoneyWiseXQIFParentCategory myParent = theParentMap.get(myParentName);

                /* Register against parent */
                myParent.registerChild(pCategory);
            }
        }
    }

    @Override
    public MoneyWiseXQIFAccountEvents registerAccount(final MoneyWiseTransAsset pAccount) {
        /* Locate an existing account */
        final String myName = pAccount.getName();
        return theAccountMap.computeIfAbsent(myName, n -> {
            final MoneyWiseXQIFAccountEvents myAccount = new MoneyWiseXQIFAccountEvents(pAccount);
            theAccounts.add(myAccount);
            return myAccount;
        });
    }

    /**
     * Register holding account.
     *
     * @param pPortfolio the portfolio
     * @return the QIFAccount representation
     */
    public MoneyWiseXQIFAccountEvents registerHoldingAccount(final MoneyWisePortfolio pPortfolio) {
        /* Locate an existing account */
        final String myName = pPortfolio.getName() + HOLDING_SUFFIX;
        return theAccountMap.computeIfAbsent(myName, n -> {
            final MoneyWiseXQIFAccountEvents myAccount = new MoneyWiseXQIFAccountEvents(myName);
            theAccounts.add(myAccount);
            return myAccount;
        });
    }

    /**
     * Register account.
     *
     * @param pAccount the account
     * @return the QIFAccount representation
     */
    public MoneyWiseXQIFAccountEvents registerAccount(final MoneyWiseXQIFAccount pAccount) {
        /* Locate an existing account */
        final String myName = pAccount.getName();
        return theAccountMap.computeIfAbsent(myName, n -> {
            final MoneyWiseXQIFAccountEvents myAccount = new MoneyWiseXQIFAccountEvents(pAccount);
            theAccounts.add(myAccount);
            return myAccount;
        });
    }

    @Override
    public MoneyWiseXQIFPayee registerPayee(final MoneyWisePayee pPayee) {
        /* Locate an existing payee */
        final String myName = pPayee.getName();
        return thePayeeMap.computeIfAbsent(myName, n -> {
            final MoneyWiseXQIFPayee myPayee = new MoneyWiseXQIFPayee(pPayee);
            thePayees.add(myPayee);
            return myPayee;
        });
    }

    @Override
    public MoneyWiseXQIFPayee registerPayee(final String pPayee) {
        /* Locate an existing payee */
        return thePayeeMap.computeIfAbsent(pPayee, n -> {
            final MoneyWiseXQIFPayee myPayee = new MoneyWiseXQIFPayee(pPayee);
            thePayees.add(myPayee);
            return myPayee;
        });
    }

    @Override
    public MoneyWiseXQIFSecurity registerSecurity(final MoneyWiseSecurity pSecurity) {
        /* Locate an existing security */
        final String myName = pSecurity.getName();
        final MoneyWiseXQIFSecurityPrices mySecurity = theSecurityMap.computeIfAbsent(myName, n -> {
            final MoneyWiseXQIFSecurityPrices mySec = new MoneyWiseXQIFSecurityPrices(this, pSecurity);
            theSymbolMap.put(pSecurity.getSymbol(), mySec.getSecurity());
            theSecurities.add(mySec);
            return mySec;
        });

        /* Return the security */
        return mySecurity.getSecurity();
    }

    /**
     * Register security.
     *
     * @param pSecurity the security
     */
    public void registerSecurity(final MoneyWiseXQIFSecurity pSecurity) {
        /* Locate an existing security */
        final String myName = pSecurity.getName();
        theSecurityMap.computeIfAbsent(myName, n -> {
            final MoneyWiseXQIFSecurityPrices mySecurity = new MoneyWiseXQIFSecurityPrices(this, pSecurity);
            theSymbolMap.put(pSecurity.getSymbol(), mySecurity.getSecurity());
            theSecurities.add(mySecurity);
            return mySecurity;
        });
    }

    /**
     * Register price.
     *
     * @param pPrice the price
     */
    public void registerPrice(final MoneyWiseSecurityPrice pPrice) {
        /* Locate an existing security price list */
        final MoneyWiseSecurity mySecurity = pPrice.getSecurity();
        final MoneyWiseXQIFSecurityPrices mySecurityList = theSecurityMap.get(mySecurity.getName());
        if (mySecurityList != null) {
            /* Add price to the list */
            mySecurityList.addPrice(pPrice);
        }
    }

    /**
     * Register price.
     *
     * @param pPrice the price
     */
    public void registerPrice(final MoneyWiseXQIFPrice pPrice) {
        /* Locate an existing security price list */
        final MoneyWiseXQIFSecurity mySecurity = pPrice.getSecurity();
        final MoneyWiseXQIFSecurityPrices mySecurityList = theSecurityMap.get(mySecurity.getName());
        if (mySecurityList != null) {
            /* Loop through the prices */
            final Iterator<MoneyWiseXQIFPrice> myIterator = pPrice.priceIterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXQIFPrice myPrice = myIterator.next();

                /* Add price to the list */
                mySecurityList.addPrice(myPrice);
            }
        }
    }

    @Override
    public MoneyWiseXQIFEventCategory getCategory(final String pName) {
        /* Lookup the category */
        return theCategories.get(pName);
    }

    @Override
    public MoneyWiseXQIFAccount getAccount(final String pName) {
        /* Lookup the security */
        final MoneyWiseXQIFAccountEvents myAccount = getAccountEvents(pName);
        return (myAccount == null)
                ? null
                : myAccount.getAccount();
    }

    /**
     * Obtain account events.
     *
     * @param pName the name of the account
     * @return the account
     */
    protected MoneyWiseXQIFAccountEvents getAccountEvents(final String pName) {
        /* Lookup the account */
        return theAccountMap.get(pName);
    }

    @Override
    public MoneyWiseXQIFSecurity getSecurity(final String pName) {
        /* Lookup the security */
        final MoneyWiseXQIFSecurityPrices myList = getSecurityPrices(pName);
        return myList == null
                ? null
                : myList.getSecurity();
    }

    @Override
    public MoneyWiseXQIFSecurity getSecurityBySymbol(final String pSymbol) {
        /* Lookup the security */
        return theSymbolMap.get(pSymbol);
    }

    /**
     * Obtain security prices.
     *
     * @param pName the name of the security
     * @return the security
     */
    protected MoneyWiseXQIFSecurityPrices getSecurityPrices(final String pName) {
        /* Lookup the security */
        return theSecurityMap.get(pName);
    }

    @Override
    public MoneyWiseXQIFClass getClass(final String pName) {
        /* Lookup the class */
        return theClassMap.get(pName);
    }

    /**
     * Build data.
     *
     * @param pData     the data
     * @param pAnalysis the analysis
     * @param pLastDate the last date
     */
    public void buildData(final MoneyWiseDataSet pData,
                          final MoneyWiseXAnalysis pAnalysis,
                          final OceanusDate pLastDate) {
        /* Create a builder */
        final MoneyWiseXQIFBuilder myBuilder = new MoneyWiseXQIFBuilder(this, pData, pAnalysis);

        /* Store dates */
        theStartDate = pData.getDateRange().getStart();
        theLastDate = pLastDate;

        /* Build opening balances */
        buildOpeningBalances(myBuilder, pData.getDeposits());

        /* Loop through the events */
        final MoneyWiseXAnalysisEventList myEvents = pAnalysis.getEvents();
        final Iterator<MoneyWiseXAnalysisEvent> myIterator = myEvents.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisEvent myEvent = myIterator.next();

            /* Break loop if the event is too late */
            final OceanusDate myDate = myEvent.getDate();
            if (myDate.compareTo(pLastDate) > 0) {
                break;
            }

            /* Process the event */
            if (MoneyWiseXAnalysisEventType.TRANSACTION.equals(myEvent.getEventType())) {
                myBuilder.processEvent(myEvent);
            }
        }

        /* Build prices for securities */
        buildPrices(pData.getSecurityPrices());
    }

    /**
     * Build opening balances.
     *
     * @param pBuilder     the builder
     * @param pDepositList the deposit list
     */
    private void buildOpeningBalances(final MoneyWiseXQIFBuilder pBuilder,
                                      final MoneyWiseDepositList pDepositList) {
        /* Loop through the prices */
        final Iterator<MoneyWiseDeposit> myIterator = pDepositList.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseDeposit myDeposit = myIterator.next();

            /* Ignore if no opening balance */
            final OceanusMoney myBalance = myDeposit.getOpeningBalance();
            if (myBalance == null) {
                continue;
            }

            /* Process the balance */
            pBuilder.processBalance(myDeposit, theStartDate, myBalance);
        }
    }

    /**
     * Build prices.
     *
     * @param pPriceList the price list
     */
    private void buildPrices(final MoneyWiseSecurityPriceList pPriceList) {
        /* Loop through the prices */
        final Iterator<MoneyWiseSecurityPrice> myIterator = pPriceList.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseSecurityPrice myPrice = myIterator.next();

            /* Break loop if the price is too late */
            final OceanusDate myDate = myPrice.getDate();
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
        if (!(pThat instanceof MoneyWiseXQIFFile myThat)) {
            return false;
        }

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
        return Objects.hash(theFileType, theClasses, theParentCategories, theSecurities, thePayees, theAccounts);
    }
}
