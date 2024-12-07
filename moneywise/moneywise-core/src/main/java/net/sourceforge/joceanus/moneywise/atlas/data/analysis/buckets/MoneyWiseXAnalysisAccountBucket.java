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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets;

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.list.MetisListIndexed;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisBaseResource;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisHistory;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisInterfaces.MoneyWiseXAnalysisBucketForeign;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisInterfaces.MoneyWiseXAnalysisCursor;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountValues;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;

import java.text.DecimalFormatSymbols;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;

/**
 * The Account Bucket class.
 * @param <T> the account data type
 */
public abstract class MoneyWiseXAnalysisAccountBucket<T extends MoneyWiseAssetBase>
        implements MetisFieldTableItem, MoneyWiseXAnalysisBucketForeign {
    /**
     * Default currency.
     */
    protected static final Currency DEFAULT_CURRENCY = DecimalFormatSymbols.getInstance().getCurrency();

    /**
     * Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisFieldSet<MoneyWiseXAnalysisAccountBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisAccountBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_NAME, MoneyWiseXAnalysisAccountBucket::getAnalysis);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.BUCKET_ACCOUNT, MoneyWiseXAnalysisAccountBucket::getAccount);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.BUCKET_BASEVALUES, MoneyWiseXAnalysisAccountBucket::getBaseValues);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBaseResource.BUCKET_HISTORY, MoneyWiseXAnalysisAccountBucket::getHistoryMap);
        FIELD_DEFS.declareLocalFieldsForEnum(MoneyWiseXAnalysisAccountAttr.class, MoneyWiseXAnalysisAccountBucket::getAttributeValue);
    }

    /**
     * Totals bucket name.
     */
    private static final String NAME_TOTALS = MoneyWiseXAnalysisBucketResource.ANALYSIS_TOTALS.getValue();

    /**
     * The analysis.
     */
    private final MoneyWiseXAnalysis theAnalysis;

    /**
     * The account.
     */
    private final T theAccount;

    /**
     * Is this a foreign currency?
     */
    private final boolean isForeignCurrency;

    /**
     * Values.
     */
    private final MoneyWiseXAnalysisAccountValues theValues;

    /**
     * The base values.
     */
    private final MoneyWiseXAnalysisAccountValues theBaseValues;

    /**
     * History Map.
     */
    private final MoneyWiseXAnalysisHistory<MoneyWiseXAnalysisAccountValues, MoneyWiseXAnalysisAccountAttr> theHistory;

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pAccount  the account
     */
    protected MoneyWiseXAnalysisAccountBucket(final MoneyWiseXAnalysis pAnalysis,
                                              final T pAccount) {
        /* Store the details */
        theAccount = pAccount;
        theAnalysis = pAnalysis;

        /* Determine currency */
        final MoneyWiseCurrency myReportingCurrency = pAnalysis.getCurrency();
        final MoneyWiseCurrency myAccountCurrency = pAccount == null
                ? myReportingCurrency
                : pAccount.getAssetCurrency();

        /* Determine whether we are a foreign currency */
        isForeignCurrency = !MetisDataDifference.isEqual(myReportingCurrency, myAccountCurrency);
        final Currency myCurrency = deriveCurrency(myAccountCurrency);

        /* Create the history map */
        final MoneyWiseXAnalysisAccountValues myValues = allocateValues(myCurrency);
        theHistory = new MoneyWiseXAnalysisHistory<>(myValues);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();

        /* If this is a foreign currency account */
        if (isForeignCurrency) {
            /* Register for xchangeRate Updates */
            theAnalysis.getCursor().registerForXchgRateUpdates(this);

            /* Record the exchangeRate and copy to base */
            recordExchangeRate();
            theBaseValues.setValue(MoneyWiseXAnalysisAccountAttr.EXCHANGERATE, getValue(MoneyWiseXAnalysisAccountAttr.EXCHANGERATE));

            /* Create a new reportedValuation */
            final OceanusMoney myReported = new OceanusMoney(theAnalysis.getCurrency().getCurrency());
            theValues.setValue(MoneyWiseXAnalysisAccountAttr.VALUATION, myReported);
            theBaseValues.setValue(MoneyWiseXAnalysisAccountAttr.VALUATION, myReported);
        }
    }

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pBase     the underlying bucket
     */
    protected MoneyWiseXAnalysisAccountBucket(final MoneyWiseXAnalysis pAnalysis,
                                              final MoneyWiseXAnalysisAccountBucket<T> pBase) {
        /* Copy details from base */
        theAccount = pBase.getAccount();
        theAnalysis = pAnalysis;
        isForeignCurrency = pBase.isForeignCurrency();

        /* Access the relevant history */
        theHistory = new MoneyWiseXAnalysisHistory<>(pBase.getHistoryMap());

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pBase     the underlying bucket
     * @param pDate     the date for the bucket
     */
    protected MoneyWiseXAnalysisAccountBucket(final MoneyWiseXAnalysis pAnalysis,
                                              final MoneyWiseXAnalysisAccountBucket<T> pBase,
                                              final OceanusDate pDate) {
        /* Copy details from base */
        theAccount = pBase.getAccount();
        theAnalysis = pAnalysis;
        isForeignCurrency = pBase.isForeignCurrency();

        /* Access the relevant history */
        theHistory = new MoneyWiseXAnalysisHistory<>(pBase.getHistoryMap(), pDate);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pBase     the underlying bucket
     * @param pRange    the range for the bucket
     */
    protected MoneyWiseXAnalysisAccountBucket(final MoneyWiseXAnalysis pAnalysis,
                                              final MoneyWiseXAnalysisAccountBucket<T> pBase,
                                              final OceanusDateRange pRange) {
        /* Copy details from base */
        theAccount = pBase.getAccount();
        theAnalysis = pAnalysis;
        isForeignCurrency = pBase.isForeignCurrency();

        /* Access the relevant history */
        theHistory = new MoneyWiseXAnalysisHistory<>(pBase.getHistoryMap(), pRange);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * derive currency.
     *
     * @param pAssetCurrency the asset currency
     * @return the actual currency to use
     */
    protected static Currency deriveCurrency(final MoneyWiseCurrency pAssetCurrency) {
        return pAssetCurrency == null
                ? DEFAULT_CURRENCY
                : pAssetCurrency.getCurrency();
    }

    @Override
    public MoneyWiseCurrency getCurrency() {
        return theAccount == null
                ? theAnalysis.getCurrency()
                : theAccount.getAssetCurrency();
    }

    /**
     * allocate standard values.
     * @param pCurrency the asset currency
     * @return the actual currency to use
     */
    protected MoneyWiseXAnalysisAccountValues allocateValues(final Currency pCurrency) {
        return new MoneyWiseXAnalysisAccountValues(pCurrency);
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return theAccount == null
                ? NAME_TOTALS
                : theAccount.getName();
    }

    /**
     * Obtain the account.
     * @return the account
     */
    public T getAccount() {
        return theAccount;
    }

    /**
     * Is this a foreign currency?
     * @return true/false
     */
    public boolean isForeignCurrency() {
        return isForeignCurrency;
    }

    @Override
    public Integer getIndexedId() {
        return theAccount.getIndexedId();
    }

    /**
     * Is this bucket idle?
     * @return true/false
     */
    public boolean isIdle() {
        return theHistory.isIdle();
    }

    /**
     * Obtain the analysis.
     * @return the analysis
     */
    protected MoneyWiseXAnalysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Obtain date range.
     * @return the range
     */
    public OceanusDateRange getDateRange() {
        return theAnalysis.getDateRange();
    }

    /**
     * Obtain the value map.
     * @return the value map
     */
    public MoneyWiseXAnalysisAccountValues getValues() {
        return theValues;
    }

    /**
     * Obtain the base value map.
     * @return the base value map
     */
    public MoneyWiseXAnalysisAccountValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Obtain values for event.
     * @param pEvent the event
     * @return the values (or null)
     */
    public MoneyWiseXAnalysisAccountValues getValuesForEvent(final MoneyWiseXAnalysisEvent pEvent) {
        return theHistory.getValuesForEvent(pEvent);
    }

    /**
     * Obtain previous values for event.
     * @param pEvent the event
     * @return the values (or null)
     */
    public MoneyWiseXAnalysisAccountValues getPreviousValuesForEvent(final MoneyWiseXAnalysisEvent pEvent) {
        return theHistory.getPreviousValuesForEvent(pEvent);
    }

    /**
     * Obtain delta for event.
     * @param pEvent the event
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public OceanusDecimal getDeltaForEvent(final MoneyWiseXAnalysisEvent pEvent,
                                           final MoneyWiseXAnalysisAccountAttr pAttr) {
        /* Obtain delta for transaction */
        return theHistory.getDeltaValue(pEvent, pAttr);
    }

    /**
     * Obtain money delta for event.
     * @param pEvent the event
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public OceanusMoney getMoneyDeltaForEvent(final MoneyWiseXAnalysisEvent pEvent,
                                              final MoneyWiseXAnalysisAccountAttr pAttr) {
        /* Obtain delta for transaction */
        return theHistory.getDeltaMoneyValue(pEvent, pAttr);
    }

    /**
     * Obtain the history map.
     * @return the history map
     */
    private MoneyWiseXAnalysisHistory<MoneyWiseXAnalysisAccountValues, MoneyWiseXAnalysisAccountAttr> getHistoryMap() {
        return theHistory;
    }

    /**
     * Set Value.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    protected void setValue(final MoneyWiseXAnalysisAccountAttr pAttr,
                            final Object pValue) {
        /* Set the value */
        theValues.setValue(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final MoneyWiseXAnalysisAccountAttr pAttr) {
        /* Access value of object */
        final Object myValue = getValue(pAttr);

        /* Return the value */
        return myValue != null
                ? myValue
                : MetisDataFieldValue.SKIP;
    }

    /**
     * Obtain an attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    private Object getValue(final MoneyWiseXAnalysisAccountAttr pAttr) {
        /* Obtain the attribute value */
        return theValues.getValue(pAttr);
    }

    /**
     * Record opening balance.
     */
    public void recordOpeningBalance() {
        /* Obtain the base valuation */
        final MoneyWiseXAnalysisAccountValues myValues = getBaseValues();
        final OceanusMoney myBaseValue = myValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.BALANCE);

        /* Set the base value (this will set the current value as well) */
        myBaseValue.addAmount(getAccount().getOpeningBalance());

        /* If this is a foreign currency */
        if (isForeignCurrency) {
            /* Access the current exchange Rate */
            final MoneyWiseCurrency myCurrency = theAnalysis.getCurrency();
            final OceanusRatio myRate = theValues.getRatioValue(MoneyWiseXAnalysisAccountAttr.EXCHANGERATE);

            /* Record the starting reporting balance and copy to base values */
            final OceanusMoney myReport = myBaseValue.convertCurrency(myCurrency.getCurrency(), myRate);
            theValues.setValue(MoneyWiseXAnalysisAccountAttr.VALUATION, myReport);
            myValues.setValue(MoneyWiseXAnalysisAccountAttr.VALUATION, myReport);
            myValues.setValue(MoneyWiseXAnalysisAccountAttr.EXCHANGERATE, myRate);
        }
    }

    /**
     * Add to balance.
     * @param pDelta the delta
     */
    public void addToBalance(final OceanusMoney pDelta) {
        OceanusMoney myValue = theValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.BALANCE);
        myValue = new OceanusMoney(myValue);
        myValue.addAmount(pDelta);
        setValue(MoneyWiseXAnalysisAccountAttr.BALANCE, myValue);
    }

    /**
     * Subtract from balance.
     * @param pDelta the delta
     */
    public void subtractFromBalance(final OceanusMoney pDelta) {
        OceanusMoney myValue = theValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.BALANCE);
        myValue = new OceanusMoney(myValue);
        myValue.subtractAmount(pDelta);
        setValue(MoneyWiseXAnalysisAccountAttr.BALANCE, myValue);
    }

    /**
     * Set maturity.
      */
    public void recordMaturity() {
        final OceanusDate myMaturity = ((MoneyWiseDeposit) getAccount()).getMaturity();
        if (myMaturity != null) {
            theValues.setValue(MoneyWiseXAnalysisAccountAttr.MATURITY, ((MoneyWiseDeposit) getAccount()).getMaturity());
        }
    }

    @Override
    public void recordExchangeRate() {
        final MoneyWiseXAnalysisCursor myCursor = theAnalysis.getCursor();
        final OceanusRatio myRate = myCursor.getCurrentXchgRate(getAccount().getAssetCurrency());
        theValues.setValue(MoneyWiseXAnalysisAccountAttr.EXCHANGERATE, myRate);
    }

    /**
     * Record depositRate.
     */
    public void recordDepositRate() {
        final MoneyWiseXAnalysisCursor myCursor = theAnalysis.getCursor();
        final OceanusRate myRate = myCursor.getCurrentDepositRate((MoneyWiseDeposit) getAccount());
        theValues.setValue(MoneyWiseXAnalysisAccountAttr.DEPOSITRATE, myRate);
    }

    @Override
    public void adjustValuation() {
        /* Determine reported balance */
        OceanusMoney myBalance = theValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.BALANCE);
        if (isForeignCurrency) {
            final OceanusRatio myRate = theValues.getRatioValue(MoneyWiseXAnalysisAccountAttr.EXCHANGERATE);
            myBalance = myBalance.convertCurrency(theAnalysis.getCurrency().getCurrency(), myRate);
        }
        theValues.setValue(MoneyWiseXAnalysisAccountAttr.VALUATION, myBalance);
    }

    @Override
    public OceanusMoney getDeltaValuation() {
        /* Determine the delta */
        final OceanusMoney myDelta = new OceanusMoney(theValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
        myDelta.subtractAmount(theHistory.getLastValues().getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION));
        return myDelta;
    }

    @Override
    public void registerEvent(final MoneyWiseXAnalysisEvent pEvent) {
        /* Make sure that valuation is correct */
        adjustValuation();

        /* Register the transaction in the history */
        theHistory.registerEvent(pEvent, theValues);
    }

    /**
     * Calculate delta.
     */
    protected void calculateDelta() {
        /* Obtain a copy of the value */
        OceanusMoney myDelta = theValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION);
        myDelta = new OceanusMoney(myDelta);

        /* Subtract any base value */
        final OceanusMoney myBase = theBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION);
        myDelta.subtractAmount(myBase);

        /* Set the delta */
        setValue(MoneyWiseXAnalysisAccountAttr.VALUEDELTA, myDelta);

        /* Adjust to base values */
        theValues.adjustToBaseValues(theBaseValues);
        theBaseValues.resetBaseValues();
    }

    /**
     * Is the bucket active?
     * @return true/false
     */
    public boolean isActive() {
        return theValues.isActive();
    }

    /**
     * AccountBucket list class.
     * @param <B> the account bucket data type
     * @param <T> the account data type
     */
    public abstract static class MoneyWiseXAnalysisAccountBucketList<B extends MoneyWiseXAnalysisAccountBucket<T>, T extends MoneyWiseAssetBase>
            implements MetisFieldItem, MetisDataList<B> {
        /**
         * Local Report fields.
         */
        @SuppressWarnings("rawtypes")
        private static final MetisFieldSet<MoneyWiseXAnalysisAccountBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisAccountBucketList.class);

        /*
         * Field IDs.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_NAME, MoneyWiseXAnalysisAccountBucketList::getAnalysis);
        }

        /**
         * The analysis.
         */
        private final MoneyWiseXAnalysis theAnalysis;

        /**
         * The list.
         */
        private final MetisListIndexed<B> theList;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        protected MoneyWiseXAnalysisAccountBucketList(final MoneyWiseXAnalysis pAnalysis) {
            /* Initialise class */
            theAnalysis = pAnalysis;
            theList = new MetisListIndexed<>();
            theList.setComparator((l, r) -> l.getAccount().compareTo(r.getAccount()));
        }

        @Override
        public List<B> getUnderlyingList() {
            return theList.getUnderlyingList();
        }

        @Override
        public String formatObject(final OceanusDataFormatter pFormatter) {
            return getDataFieldSet().getName();
        }

        /**
         * Obtain the analysis.
         * @return the analysis
         */
        protected MoneyWiseXAnalysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Construct a view bucket.
         * @param pBase the base bucket
         * @return the new bucket
         */
        protected abstract B newBucket(B pBase);

        /**
         * Construct a dated List.
         * @param pBase the base list
         * @param pDate the Date
         */
        protected void constructFromBase(final MoneyWiseXAnalysisAccountBucketList<B, T> pBase,
                                         final OceanusDate pDate) {
            /* Loop through the buckets */
            final Iterator<B> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final B myCurr = myIterator.next();

                /* Access the bucket for this date */
                final B myBucket = newBucket(myCurr, pDate);

                /* If the bucket is non-idle or active */
                if (myBucket.isActive() || !myBucket.isIdle()) {
                    theList.add(myBucket);
                }
            }
        }

        /**
         * Construct a dated bucket.
         * @param pBase the base bucket
         * @param pDate the Date
         * @return the new bucket
         */
        protected abstract B newBucket(B pBase,
                                       OceanusDate pDate);

        /**
         * Construct a ranged List.
         * @param pBase the base list
         * @param pRange the Date Range
         */
        protected void constructFromBase(final MoneyWiseXAnalysisAccountBucketList<B, T> pBase,
                                         final OceanusDateRange pRange) {
            /* Loop through the buckets */
            final Iterator<B> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final B myCurr = myIterator.next();

                /* Access the bucket for this range */
                final B myBucket = newBucket(myCurr, pRange);

                /* If the bucket is non-idle or active */
                if (myBucket.isActive() || !myBucket.isIdle()) {
                    /* Add to the list */
                    theList.add(myBucket);
                }
            }
        }

        /**
         * Obtain item by id.
         * @param pId the id to lookup
         * @return the item (or null if not present)
         */
        public B findItemById(final Integer pId) {
            /* Return results */
            return theList.getItemById(pId);
        }

        /**
         * Construct a ranged bucket.
         * @param pBase the base bucket
         * @param pRange the Range
         * @return the new bucket
         */
        protected abstract B newBucket(B pBase,
                                       OceanusDateRange pRange);

        /**
         * Obtain the AccountBucket for a given account.
         * @param pAccount the account
         * @return the bucket
         */
        public B getBucket(final T pAccount) {
            /* Locate the bucket in the list */
            B myItem = findItemById(pAccount.getIndexedId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = newBucket(pAccount);

                /* Add to the list */
                theList.add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Construct a standard bucket.
         * @param pAccount the Account
         * @return the new bucket
         */
        protected abstract B newBucket(T pAccount);

        /**
         * SortBuckets.
         */
        protected void sortBuckets() {
            theList.sortList();
        }

        /**
         * Mark active accounts.
         * @throws OceanusException on error
         */
        public void markActiveAccounts() throws OceanusException {
            /* Loop through the buckets */
            final Iterator<B> myIterator = iterator();
            while (myIterator.hasNext()) {
                final B myCurr = myIterator.next();
                final T myAccount = myCurr.getAccount();

                /* If we are active */
                if (myCurr.isActive()) {
                    /* Set the account as relevant */
                    myAccount.setRelevant();
                }

                /* If we are closed */
                if (Boolean.TRUE.equals(myAccount.isClosed())) {
                    /* Ensure that we have correct closed/maturity dates */
                    myAccount.adjustClosed();

                    /* If we are Relevant */
                    if (myAccount.isRelevant()
                            && theAnalysis.getData().checkClosedAccounts()) {
                        /* throw exception */
                        throw new MoneyWiseDataException(myCurr, "Illegally closed account");
                    }
                }
            }
        }
    }
}
