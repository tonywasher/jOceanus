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
package net.sourceforge.jOceanus.jMoneyWise.views;

import java.util.Iterator;

import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDateDay.JDateDayRange;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYear;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket.AccountBucketList;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountCategoryBucket.AccountCategoryBucketList;
import net.sourceforge.jOceanus.jMoneyWise.views.ChargeableEvent.ChargeableEventList;
import net.sourceforge.jOceanus.jMoneyWise.views.EventCategoryBucket.EventCategoryBucketList;
import net.sourceforge.jOceanus.jMoneyWise.views.InvestmentAnalysis.InvestmentAnalysisList;
import net.sourceforge.jOceanus.jMoneyWise.views.TaxCategoryBucket.TaxCategoryBucketList;

/**
 * Data Analysis.
 * @author Tony Washer
 */
public class Analysis
        implements JDataContents {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(Analysis.class.getSimpleName());

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * AccountBuckets Field Id.
     */
    public static final JDataField FIELD_ACCOUNTS = FIELD_DEFS.declareLocalField("Accounts");

    /**
     * AccountCategoryBuckets Field Id.
     */
    public static final JDataField FIELD_ACTCATS = FIELD_DEFS.declareLocalField("AccountCategories");

    /**
     * EventCategoryBuckets Field Id.
     */
    public static final JDataField FIELD_EVTCATS = FIELD_DEFS.declareLocalField("EventCategories");

    /**
     * TaxCategoryBuckets Field Id.
     */
    public static final JDataField FIELD_TAXCATS = FIELD_DEFS.declareLocalField("TaxCategories");

    /**
     * Charges Field Id.
     */
    public static final JDataField FIELD_CHARGES = FIELD_DEFS.declareLocalField("Charges");

    /**
     * Account Field Id.
     */
    public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareLocalField("Account");

    /**
     * DateRange Field Id.
     */
    public static final JDataField FIELD_DATERANGE = FIELD_DEFS.declareLocalField("DateRange");

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_ACCOUNTS.equals(pField)) {
            return (theAccounts.size() > 0)
                    ? theAccounts
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_ACTCATS.equals(pField)) {
            return (theAccountCategories.size() > 0)
                    ? theAccountCategories
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_EVTCATS.equals(pField)) {
            return (theEventCategories.size() > 0)
                    ? theEventCategories
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_TAXCATS.equals(pField)) {
            return ((theTaxCategories != null) && (theTaxCategories.size() > 0))
                    ? theTaxCategories
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_CHARGES.equals(pField)) {
            return (theCharges.size() > 0)
                    ? theCharges
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_DATERANGE.equals(pField)) {
            return theDateRange;
        }
        if (FIELD_ACCOUNT.equals(pField)) {
            return (theAccount == null)
                    ? JDataFieldValue.SkipField
                    : theAccount;
        }

        /* Unknown */
        return JDataFieldValue.UnknownField;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    /**
     * The DataSet.
     */
    private final FinanceData theData;

    /**
     * The account buckets.
     */
    private final AccountBucketList theAccounts;

    /**
     * The account category buckets.
     */
    private final AccountCategoryBucketList theAccountCategories;

    /**
     * The event category buckets.
     */
    private final EventCategoryBucketList theEventCategories;

    /**
     * The tax category buckets.
     */
    private final TaxCategoryBucketList theTaxCategories;

    /**
     * The charges.
     */
    private final ChargeableEventList theCharges;

    /**
     * The account.
     */
    private final Account theAccount;

    /**
     * The Date Range.
     */
    private final JDateDayRange theDateRange;

    /**
     * Obtain the data.
     * @return the data
     */
    public FinanceData getData() {
        return theData;
    }

    /**
     * Obtain the account buckets list.
     * @return the list
     */
    public AccountBucketList getAccounts() {
        return theAccounts;
    }

    /**
     * Obtain the account categories list.
     * @return the list
     */
    public AccountCategoryBucketList getAccountCategories() {
        return theAccountCategories;
    }

    /**
     * Obtain the event categories list.
     * @return the list
     */
    public EventCategoryBucketList getEventCategories() {
        return theEventCategories;
    }

    /**
     * Obtain the tax categories list.
     * @return the list
     */
    public TaxCategoryBucketList getTaxCategories() {
        return theTaxCategories;
    }

    /**
     * Obtain the account.
     * @return the account
     */
    public Account getAccount() {
        return theAccount;
    }

    /**
     * Obtain the date range.
     * @return the date range
     */
    public JDateDayRange getDateRange() {
        return theDateRange;
    }

    /**
     * Obtain the charges.
     * @return the charges
     */
    public ChargeableEventList getCharges() {
        return theCharges;
    }

    /**
     * Constructor for a dated analysis.
     * @param pData the data to analyse events for
     * @param pDateRange the Date for the analysis
     */
    public Analysis(final FinanceData pData,
                    final JDateDayRange pDateRange) {
        /* Store the data */
        theData = pData;
        theDateRange = pDateRange;
        theAccount = null;

        /* Create a new set of buckets */
        theAccounts = new AccountBucketList(this);
        theAccountCategories = new AccountCategoryBucketList(this);
        theEventCategories = new EventCategoryBucketList(this);
        theCharges = new ChargeableEventList();
        theTaxCategories = null;

        /* Add opening balances */
        addOpeningBalances();
    }

    /**
     * Constructor for a taxYear analysis.
     * @param pData the data to analyse events for
     * @param pYear the year to analyse
     * @param pAnalysis the previous year analysis (if present)
     */
    public Analysis(final FinanceData pData,
                    final TaxYear pYear,
                    final Analysis pAnalysis) {
        /* Store the data */
        theData = pData;
        theDateRange = pYear.getDateRange();
        theAccount = null;

        /* Create a new list */
        theCharges = new ChargeableEventList();
        theTaxCategories = new TaxCategoryBucketList(this, pYear);

        /* Build new bucket lists */
        if (pAnalysis != null) {
            theAccounts = new AccountBucketList(this, pAnalysis.getAccounts());
            theAccountCategories = new AccountCategoryBucketList(this, pAnalysis.getAccountCategories());
            theEventCategories = new EventCategoryBucketList(this);

        } else {
            theAccounts = new AccountBucketList(this);
            theAccountCategories = new AccountCategoryBucketList(this);
            theEventCategories = new EventCategoryBucketList(this);

            /* Add opening balances */
            addOpeningBalances();
        }
    }

    /**
     * Constructor for an account analysis.
     * @param pData the data to analyse events for
     * @param pAccount the account to analyse
     * @param pDate the date to analyse to
     */
    public Analysis(final FinanceData pData,
                    final Account pAccount,
                    final JDateDay pDate) {
        /* Store the data */
        theData = pData;
        theAccount = pAccount;

        /* Access start date */
        JDateDay myStart = pData.getDateRange().getStart();
        theDateRange = new JDateDayRange(myStart, pDate);

        /* Create a new set of buckets */
        theAccounts = new AccountBucketList(this);
        theAccountCategories = new AccountCategoryBucketList(this);
        theEventCategories = new EventCategoryBucketList(this);
        theCharges = new ChargeableEventList();
        theTaxCategories = null;

        /* If the account has an opening balance */
        if (theAccount.getOpeningBalance() != null) {
            /* Add the opening balance */
            addOpeningBalance(theAccount);
        }
    }

    /**
     * Add opening balances for accounts.
     */
    private void addOpeningBalances() {
        /* Iterate through the accounts */
        Iterator<Account> myIterator = theData.getAccounts().iterator();
        while (myIterator.hasNext()) {
            Account myAccount = myIterator.next();

            /* If the account has an opening balance */
            if (myAccount.getOpeningBalance() != null) {
                /* Add the opening balance */
                addOpeningBalance(myAccount);
            }
        }
    }

    /**
     * Add opening balance for an account.
     * @param pAccount the account to add the opening balance for
     */
    private void addOpeningBalance(final Account pAccount) {
        /* Access the money */
        JMoney myBalance = pAccount.getOpeningBalance();

        /* Obtain the actual account bucket */
        AccountBucket myBucket = theAccounts.getBucket(pAccount);
        myBucket.setOpenBalance(myBalance);

        /* Obtain the opening income account bucket */
        Account myAccount = theData.getAccounts().getSingularClass(AccountCategoryClass.OpeningBalance);
        AccountBucket myOpeningAccount = theAccounts.getBucket(myAccount);

        /* Add income value */
        myOpeningAccount.addIncome(myBalance);

        /* Obtain the opening income category bucket */
        EventCategory myCategory = theData.getEventCategories().getSingularClass(EventCategoryClass.OpeningBalance);
        EventCategoryBucket myOpeningCategory = theEventCategories.getBucket(myCategory);

        /* Add value value */
        myOpeningCategory.addIncome(myBalance);
    }

    /**
     * Obtain Investment Analysis for Investment Event.
     * @param pEvent the event
     * @param pSecurity the security for the event
     * @return the analysis
     */
    public InvestmentAnalysis getInvestmentAnalysis(final Event pEvent,
                                                    final Account pSecurity) {
        /* Locate the security bucket */
        AccountBucket mySecurity = theAccounts.findItemById(pSecurity.getId());
        if (mySecurity == null) {
            return null;
        }

        /* Obtain the investment analysis for the event */
        InvestmentAnalysisList myAnalyses = mySecurity.getInvestmentAnalyses();
        return (myAnalyses == null)
                ? null
                : myAnalyses.findItemById(pEvent.getId());
    }
}
