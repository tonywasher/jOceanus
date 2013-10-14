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
package net.sourceforge.jOceanus.jMoneyWise.analysis;

import java.util.Iterator;

import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jMoneyWise.analysis.AccountBucket.AccountBucketList;
import net.sourceforge.jOceanus.jMoneyWise.analysis.AccountCategoryBucket.AccountCategoryBucketList;
import net.sourceforge.jOceanus.jMoneyWise.analysis.ChargeableEvent.ChargeableEventList;
import net.sourceforge.jOceanus.jMoneyWise.analysis.EventCategoryBucket.EventCategoryBucketList;
import net.sourceforge.jOceanus.jMoneyWise.analysis.InvestmentAnalysis.InvestmentAnalysisList;
import net.sourceforge.jOceanus.jMoneyWise.analysis.PayeeBucket.PayeeBucketList;
import net.sourceforge.jOceanus.jMoneyWise.analysis.SecurityBucket.SecurityBucketList;
import net.sourceforge.jOceanus.jMoneyWise.analysis.TaxCategoryBucket.TaxCategoryBucketList;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryClass;

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
     * SecurityBuckets Field Id.
     */
    public static final JDataField FIELD_SECURITIES = FIELD_DEFS.declareLocalField("Securities");

    /**
     * PayeeBuckets Field Id.
     */
    public static final JDataField FIELD_PAYEES = FIELD_DEFS.declareLocalField("Payees");

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

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_ACCOUNTS.equals(pField)) {
            return (theAccounts.size() > 0)
                    ? theAccounts
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_SECURITIES.equals(pField)) {
            return (theSecurities.size() > 0)
                    ? theSecurities
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_PAYEES.equals(pField)) {
            return (thePayees.size() > 0)
                    ? thePayees
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
     * The security buckets.
     */
    private final SecurityBucketList theSecurities;

    /**
     * The payee buckets.
     */
    private final PayeeBucketList thePayees;

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
     * Obtain the security buckets list.
     * @return the list
     */
    public SecurityBucketList getSecurities() {
        return theSecurities;
    }

    /**
     * Obtain the payee buckets list.
     * @return the list
     */
    public PayeeBucketList getPayees() {
        return thePayees;
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
     * Obtain the charges.
     * @return the charges
     */
    public ChargeableEventList getCharges() {
        return theCharges;
    }

    /**
     * Constructor for a dated analysis.
     * @param pData the data to analyse events for
     */
    public Analysis(final FinanceData pData) {
        /* Store the data */
        theData = pData;

        /* Create a new set of buckets */
        theAccounts = new AccountBucketList(this);
        theSecurities = new SecurityBucketList(this);
        thePayees = new PayeeBucketList(this);
        theAccountCategories = new AccountCategoryBucketList(this);
        theEventCategories = new EventCategoryBucketList(this);
        theCharges = new ChargeableEventList();
        theTaxCategories = null;

        /* Add opening balances */
        addOpeningBalances();
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
        PayeeBucket myOpeningPayee = thePayees.getBucket(myAccount);

        /* Add income value */
        myOpeningPayee.addIncome(myBalance);

        /* Obtain the opening income category bucket */
        EventCategory myCategory = theData.getEventCategories().getSingularClass(EventCategoryClass.OpeningBalance);
        EventCategoryBucket myOpeningCategory = theEventCategories.getBucket(myCategory);

        /* Add income value */
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
        SecurityBucket mySecurity = theSecurities.findItemById(pSecurity.getId());
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
