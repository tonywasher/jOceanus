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
package net.sourceforge.joceanus.moneywise.lethe.data.analysis.data;

import java.text.DecimalFormatSymbols;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.list.MetisListIndexed;
import net.sourceforge.joceanus.moneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.base.MoneyWiseAnalysisBaseResource;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.base.MoneyWiseAnalysisHistory;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisAccountAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisAccountValues;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseExchangeRate.MoneyWiseExchangeRateDataMap;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIDataFormatter;

/**
 * The Account Bucket class.
 * @param <T> the account data type
 */
public abstract class MoneyWiseAnalysisAccountBucket<T extends MoneyWiseAssetBase>
        implements MetisFieldTableItem {
    /**
     * Default currency.
     */
    protected static final Currency DEFAULT_CURRENCY = DecimalFormatSymbols.getInstance().getCurrency();

    /**
     * Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisFieldSet<MoneyWiseAnalysisAccountBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisAccountBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_NAME, MoneyWiseAnalysisAccountBucket::getAnalysis);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.BUCKET_ACCOUNT, MoneyWiseAnalysisAccountBucket::getAccount);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.BUCKET_BASEVALUES, MoneyWiseAnalysisAccountBucket::getBaseValues);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisBaseResource.BUCKET_HISTORY, MoneyWiseAnalysisAccountBucket::getHistoryMap);
        FIELD_DEFS.declareLocalFieldsForEnum(MoneyWiseAnalysisAccountAttr.class, MoneyWiseAnalysisAccountBucket::getAttributeValue);
    }

    /**
     * Totals bucket name.
     */
    private static final String NAME_TOTALS = MoneyWiseAnalysisDataResource.ANALYSIS_TOTALS.getValue();

    /**
     * The analysis.
     */
    private final MoneyWiseAnalysis theAnalysis;

    /**
     * The account.
     */
    private final T theAccount;

    /**
     * Is this a foreign currency?
     */
    private final Boolean isForeignCurrency;

    /**
     * Values.
     */
    private final MoneyWiseAnalysisAccountValues theValues;

    /**
     * The base values.
     */
    private final MoneyWiseAnalysisAccountValues theBaseValues;

    /**
     * History Map.
     */
    private final MoneyWiseAnalysisHistory<MoneyWiseAnalysisAccountValues, MoneyWiseAnalysisAccountAttr> theHistory;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pAccount the account
     */
    protected MoneyWiseAnalysisAccountBucket(final MoneyWiseAnalysis pAnalysis,
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
        final Currency myRepCurrency = deriveCurrency(myReportingCurrency);

        /* Create the history map */
        final MoneyWiseAnalysisAccountValues myValues = Boolean.TRUE.equals(isForeignCurrency)
                ? allocateForeignValues(myCurrency, myRepCurrency)
                : allocateStandardValues(myCurrency);
        theHistory = new MoneyWiseAnalysisHistory<>(myValues);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     */
    protected MoneyWiseAnalysisAccountBucket(final MoneyWiseAnalysis pAnalysis,
                                             final MoneyWiseAnalysisAccountBucket<T> pBase) {
        /* Copy details from base */
        theAccount = pBase.getAccount();
        theAnalysis = pAnalysis;
        isForeignCurrency = pBase.isForeignCurrency();

        /* Access the relevant history */
        theHistory = new MoneyWiseAnalysisHistory<>(pBase.getHistoryMap());

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pDate the date for the bucket
     */
    protected MoneyWiseAnalysisAccountBucket(final MoneyWiseAnalysis pAnalysis,
                                             final MoneyWiseAnalysisAccountBucket<T> pBase,
                                             final OceanusDate pDate) {
        /* Copy details from base */
        theAccount = pBase.getAccount();
        theAnalysis = pAnalysis;
        isForeignCurrency = pBase.isForeignCurrency();

        /* Access the relevant history */
        theHistory = new MoneyWiseAnalysisHistory<>(pBase.getHistoryMap(), pDate);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pRange the range for the bucket
     */
    protected MoneyWiseAnalysisAccountBucket(final MoneyWiseAnalysis pAnalysis,
                                             final MoneyWiseAnalysisAccountBucket<T> pBase,
                                             final OceanusDateRange pRange) {
        /* Copy details from base */
        theAccount = pBase.getAccount();
        theAnalysis = pAnalysis;
        isForeignCurrency = pBase.isForeignCurrency();

        /* Access the relevant history */
        theHistory = new MoneyWiseAnalysisHistory<>(pBase.getHistoryMap(), pRange);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * derive currency.
     * @param pAssetCurrency the asset currency
     * @return the actual currency to use
     */
    protected static Currency deriveCurrency(final MoneyWiseCurrency pAssetCurrency) {
        return pAssetCurrency == null
                ? DEFAULT_CURRENCY
                : pAssetCurrency.getCurrency();
    }

    /**
     * allocate standard values.
     * @param pCurrency the asset currency
     * @return the actual currency to use
     */
    protected MoneyWiseAnalysisAccountValues allocateStandardValues(final Currency pCurrency) {
        return new MoneyWiseAnalysisAccountValues(pCurrency);
    }

    /**
     * allocate foreign values.
     * @param pCurrency the asset currency
     * @param pReportingCurrency the reporting currency
     * @return the actual currency to use
     */
    protected MoneyWiseAnalysisAccountValues allocateForeignValues(final Currency pCurrency,
                                                                   final Currency pReportingCurrency) {
        return new MoneyWiseAnalysisAccountValues(pCurrency, pReportingCurrency);
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
    public Boolean isForeignCurrency() {
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
    public Boolean isIdle() {
        return theHistory.isIdle();
    }

    /**
     * Obtain the analysis.
     * @return the analysis
     */
    protected MoneyWiseAnalysis getAnalysis() {
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
    public MoneyWiseAnalysisAccountValues getValues() {
        return theValues;
    }

    /**
     * Obtain the base value map.
     * @return the base value map
     */
    public MoneyWiseAnalysisAccountValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Obtain values for transaction.
     * @param pTrans the transaction
     * @return the values (or null)
     */
    public MoneyWiseAnalysisAccountValues getValuesForTransaction(final MoneyWiseTransaction pTrans) {
        return theHistory.getValuesForTransaction(pTrans);
    }

    /**
     * Obtain previous values for transaction.
     * @param pTrans the transaction
     * @return the values (or null)
     */
    public MoneyWiseAnalysisAccountValues getPreviousValuesForTransaction(final MoneyWiseTransaction pTrans) {
        return theHistory.getPreviousValuesForTransaction(pTrans);
    }

    /**
     * Obtain delta for transaction.
     * @param pTrans the transaction
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public OceanusDecimal getDeltaForTransaction(final MoneyWiseTransaction pTrans,
                                                 final MoneyWiseAnalysisAccountAttr pAttr) {
        /* Obtain delta for transaction */
        return theHistory.getDeltaValue(pTrans, pAttr);
    }

    /**
     * Obtain money delta for transaction.
     * @param pTrans the transaction
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public OceanusMoney getMoneyDeltaForTransaction(final MoneyWiseTransaction pTrans,
                                                    final MoneyWiseAnalysisAccountAttr pAttr) {
        /* Obtain delta for transaction */
        return theHistory.getDeltaMoneyValue(pTrans, pAttr);
    }

    /**
     * Obtain the history map.
     * @return the history map
     */
    private MoneyWiseAnalysisHistory<MoneyWiseAnalysisAccountValues, MoneyWiseAnalysisAccountAttr> getHistoryMap() {
        return theHistory;
    }

    /**
     * Set Value.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    protected void setValue(final MoneyWiseAnalysisAccountAttr pAttr,
                            final Object pValue) {
        /* Set the value */
        theValues.setValue(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final MoneyWiseAnalysisAccountAttr pAttr) {
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
    private Object getValue(final MoneyWiseAnalysisAccountAttr pAttr) {
        /* Obtain the attribute value */
        return theValues.getValue(pAttr);
    }

    /**
     * Adjust counter.
     * @param pAttr the attribute
     * @param pDelta the delta
     */
    protected void adjustCounter(final MoneyWiseAnalysisAccountAttr pAttr,
                                 final OceanusMoney pDelta) {
        OceanusMoney myValue = theValues.getMoneyValue(pAttr);
        myValue = new OceanusMoney(myValue);
        myValue.addAmount(pDelta);
        setValue(pAttr, myValue);
    }

    /**
     * Adjust account for debit.
     * @param pHelper the transaction helper
     */
    public void adjustForDebit(final MoneyWiseAnalysisTransactionHelper pHelper) {
        /* Access event amount */
        OceanusMoney myAmount = pHelper.getDebitAmount();

        /* If we have a non-zero amount */
        if (myAmount.isNonZero()) {
            /* Adjust valuation */
            myAmount = new OceanusMoney(myAmount);
            myAmount.negate();

            /* If we are a foreign account */
            if (Boolean.TRUE.equals(isForeignCurrency)) {
                /* Access local amount amount */
                final OceanusMoney myLocalAmount = pHelper.getLocalAmount();

                /* Adjust counters */
                adjustCounter(MoneyWiseAnalysisAccountAttr.FOREIGNVALUE, myAmount);
                adjustCounter(MoneyWiseAnalysisAccountAttr.LOCALVALUE, myLocalAmount);

                /* Obtain the debit exchangeRate and convert the foreign valuation */
                final OceanusRatio myRate = pHelper.getDebitExchangeRate();
                final OceanusMoney myLocalValue = myAmount.convertCurrency(theAnalysis.getCurrency().getCurrency(), myRate);

                /* Set the valuation */
                setValue(MoneyWiseAnalysisAccountAttr.VALUATION, myLocalValue);
                setValue(MoneyWiseAnalysisAccountAttr.EXCHANGERATE, myRate);

                /* Determine currency fluctuation */
                final OceanusMoney myFluct = new OceanusMoney(myLocalValue);
                myFluct.subtractAmount(theValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.LOCALVALUE));
                adjustCounter(MoneyWiseAnalysisAccountAttr.CURRENCYFLUCT, myFluct);

                /* else this is a standard account */
            } else {
                /* Adjust valuation */
                adjustCounter(MoneyWiseAnalysisAccountAttr.VALUATION, myAmount);
            }
        }

        /* Register the transaction in the history */
        registerTransaction(pHelper);
    }

    /**
     * Adjust account for credit. TODO rework
     * @param pHelper the transaction helper
     */
    public void adjustForCredit(final MoneyWiseAnalysisTransactionHelper pHelper) {
        /* Access event amount */
        final OceanusMoney myAmount = pHelper.getCreditAmount();

        /* If we have a non-zero amount */
        if (myAmount.isNonZero()) {
            /* If we are a foreign account */
            if (Boolean.TRUE.equals(isForeignCurrency)) {
                /* Access local amount */
                final OceanusMoney myLocalAmount = pHelper.getLocalAmount();

                /* Adjust counters */
                adjustCounter(MoneyWiseAnalysisAccountAttr.FOREIGNVALUE, myAmount);
                adjustCounter(MoneyWiseAnalysisAccountAttr.LOCALVALUE, myLocalAmount);

                /* Obtain the credit exchangeRate and convert the foreign valuation */
                final OceanusRatio myRate = pHelper.getCreditExchangeRate();
                final OceanusMoney myLocalValue = myAmount.convertCurrency(theAnalysis.getCurrency().getCurrency(), myRate);

                /* Set the valuation */
                setValue(MoneyWiseAnalysisAccountAttr.VALUATION, myLocalValue);
                setValue(MoneyWiseAnalysisAccountAttr.EXCHANGERATE, myRate);

                /* Determine currency fluctuation */
                final OceanusMoney myFluct = new OceanusMoney(myLocalValue);
                myFluct.subtractAmount(theValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.LOCALVALUE));
                setValue(MoneyWiseAnalysisAccountAttr.CURRENCYFLUCT, myFluct);

                /* else this is a standard account */
            } else {
                /* Adjust valuation */
                adjustCounter(MoneyWiseAnalysisAccountAttr.VALUATION, myAmount);
            }
        }

        /* Register the transaction in the history */
        registerTransaction(pHelper);
    }

    /**
     * Adjust account for credit.
     * @param pHelper the transaction helper
     */
    public void adjustForReturnedCashCredit(final MoneyWiseAnalysisTransactionHelper pHelper) {
        /* Access event amount */
        final OceanusMoney myAmount = pHelper.getReturnedCash();

        /* If we have a non-zero amount */
        if (myAmount.isNonZero()) {
            /* If we are a foreign account */
            if (Boolean.TRUE.equals(isForeignCurrency)) {
                /* Access local amount */
                final OceanusMoney myLocalAmount = pHelper.getLocalReturnedCash();

                /* Adjust counters */
                adjustCounter(MoneyWiseAnalysisAccountAttr.FOREIGNVALUE, myAmount);
                adjustCounter(MoneyWiseAnalysisAccountAttr.LOCALVALUE, myLocalAmount);

                /* Obtain the credit exchangeRate and convert the foreign valuation */
                final OceanusRatio myRate = pHelper.getReturnedCashExchangeRate();
                final OceanusMoney myLocalValue = myAmount.convertCurrency(theAnalysis.getCurrency().getCurrency(), myRate);

                /* Set the valuation */
                setValue(MoneyWiseAnalysisAccountAttr.VALUATION, myLocalValue);
                setValue(MoneyWiseAnalysisAccountAttr.EXCHANGERATE, myRate);

                /* Determine currency fluctuation */
                final OceanusMoney myFluct = new OceanusMoney(myLocalValue);
                myFluct.subtractAmount(theValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.LOCALVALUE));
                setValue(MoneyWiseAnalysisAccountAttr.CURRENCYFLUCT, myFluct);

                /* else this is a standard account */
            } else {
                /* Adjust valuation */
                adjustCounter(MoneyWiseAnalysisAccountAttr.VALUATION, myAmount);
            }
        }

        /* Register the transaction in the history */
        registerTransaction(pHelper);
    }

    /**
     * Set opening balance.
     * @param pHelper the transaction helper
     * @param pBalance the opening balance
     */
    protected void setOpeningBalance(final MoneyWiseAnalysisTransactionHelper pHelper,
                                     final OceanusMoney pBalance) {
        /* Obtain the base valuation */
        final MoneyWiseAnalysisAccountValues myValues = getBaseValues();
        final OceanusMoney myBaseValue = myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION);

        /* If we are a foreign account */
        if (Boolean.TRUE.equals(isForeignCurrency)) {
            /* Obtain the foreign valuation */
            final OceanusMoney myForeignValue = myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.FOREIGNVALUE);
            final OceanusMoney myLocalValue = myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.LOCALVALUE);

            /* Obtain exchange rate and reporting value */
            final OceanusRatio myRate = pHelper.getExchangeRate(theAccount.getAssetCurrency(), theAnalysis.getData().getDateRange().getStart());
            final OceanusMoney myLocalAmount = pBalance.convertCurrency(theAnalysis.getCurrency().getCurrency(), myRate);

            /* Record details */
            myBaseValue.addAmount(myLocalAmount);
            myLocalValue.addAmount(myLocalAmount);
            myForeignValue.addAmount(pBalance);
            myValues.setValue(MoneyWiseAnalysisAccountAttr.EXCHANGERATE, myRate);

            /* else this is a standard account */
        } else {
            /* Set the base value (this will set the current value as well) */
            myBaseValue.addAmount(pBalance);
        }
    }

    /**
     * Register the transaction.
     * @param pHelper the transaction helper
     */
    public void registerTransaction(final MoneyWiseAnalysisTransactionHelper pHelper) {
        /* Register the transaction in the history */
        theHistory.registerTransaction(pHelper.getTransaction(), theValues);
    }

    /**
     * Register the transaction.
     * @param pTrans the transaction
     */
    public void registerTransaction(final MoneyWiseTransaction pTrans) {
        /* Register the transaction in the history */
        theHistory.registerTransaction(pTrans, theValues);
    }

    /**
     * calculate currency fluctuations over the range.
     * @param pRange the range of valuation
     */
    protected void calculateFluctuations(final OceanusDateRange pRange) {
        /* Obtain the appropriate rates */
        final MoneyWiseDataSet myData = theAnalysis.getData();
        final MoneyWiseExchangeRateDataMap myRateMap = myData.getExchangeRateDataMap();
        final OceanusRatio[] myRates = myRateMap.getRatesForRange(theAccount.getAssetCurrency(), pRange);
        final Currency myBaseCurrency = theAnalysis.getCurrency().getCurrency();

        /* Access the base value */
        OceanusRatio myRate = myRates[0];
        OceanusMoney myForeignValue = theBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.FOREIGNVALUE);
        OceanusMoney myLocalValue = theBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.LOCALVALUE);

        /* Calculate the base value */
        OceanusMoney myLocalValuation = myForeignValue.convertCurrency(myBaseCurrency, myRate);
        theBaseValues.setValue(MoneyWiseAnalysisAccountAttr.EXCHANGERATE, myRate);
        theBaseValues.setValue(MoneyWiseAnalysisAccountAttr.VALUATION, myLocalValuation);

        /* Determine currency fluctuation */
        OceanusMoney myFluct = new OceanusMoney(myLocalValuation);
        myFluct.subtractAmount(myLocalValue);
        theBaseValues.setValue(MoneyWiseAnalysisAccountAttr.CURRENCYFLUCT, myFluct);

        /* Access current values */
        myRate = myRates[1];
        myForeignValue = theValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.FOREIGNVALUE);
        myLocalValue = theValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.LOCALVALUE);

        /* Calculate the current value */
        myLocalValuation = myForeignValue.convertCurrency(myBaseCurrency, myRate);
        theValues.setValue(MoneyWiseAnalysisAccountAttr.EXCHANGERATE, myRate);
        theValues.setValue(MoneyWiseAnalysisAccountAttr.VALUATION, myLocalValuation);

        /* Determine currency fluctuation */
        myFluct = new OceanusMoney(myLocalValuation);
        myFluct.subtractAmount(myLocalValue);
        theValues.setValue(MoneyWiseAnalysisAccountAttr.CURRENCYFLUCT, myFluct);
    }

    /**
     * Calculate delta.
     */
    protected void calculateDelta() {
        /* Obtain a copy of the value */
        OceanusMoney myDelta = theValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION);
        myDelta = new OceanusMoney(myDelta);

        /* Subtract any base value */
        final OceanusMoney myBase = theBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION);
        myDelta.subtractAmount(myBase);

        /* Set the delta */
        setValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA, myDelta);

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
     * record the rate of the account at a given date.
     * @param pDate the date of valuation
     */
    protected void recordRate(final OceanusDate pDate) {
    }

    /**
     * AccountBucket list class.
     * @param <B> the account bucket data type
     * @param <T> the account data type
     */
    public abstract static class MoneyWiseAnalysisAccountBucketList<B extends MoneyWiseAnalysisAccountBucket<T>, T extends MoneyWiseAssetBase>
            implements MetisFieldItem, MetisDataList<B> {
        /**
         * Local Report fields.
         */
        @SuppressWarnings("rawtypes")
        private static final MetisFieldSet<MoneyWiseAnalysisAccountBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisAccountBucketList.class);

        /*
         * Field IDs.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_NAME, MoneyWiseAnalysisAccountBucketList::getAnalysis);
        }

        /**
         * The analysis.
         */
        private final MoneyWiseAnalysis theAnalysis;

        /**
         * The list.
         */
        private final MetisListIndexed<B> theList;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        protected MoneyWiseAnalysisAccountBucketList(final MoneyWiseAnalysis pAnalysis) {
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
        public String formatObject(final TethysUIDataFormatter pFormatter) {
            return getDataFieldSet().getName();
        }

        /**
         * Obtain the analysis.
         * @return the analysis
         */
        protected MoneyWiseAnalysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Construct a view List.
         * @param pBase the base list
         */
        protected void constructFromBase(final MoneyWiseAnalysisAccountBucketList<B, T> pBase) {
            /* Loop through the buckets */
            final Iterator<B> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final B myCurr = myIterator.next();

                /* Access the bucket */
                final B myBucket = newBucket(myCurr);

                /* If the bucket is non-idle or active */
                if (myBucket.isActive() || Boolean.TRUE.equals(!myBucket.isIdle())) {
                    /* add to list */
                    theList.add(myBucket);
                }
            }
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
        protected void constructFromBase(final MoneyWiseAnalysisAccountBucketList<B, T> pBase,
                                         final OceanusDate pDate) {
            /* Loop through the buckets */
            final Iterator<B> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final B myCurr = myIterator.next();

                /* Access the bucket for this date */
                final B myBucket = newBucket(myCurr, pDate);

                /* If the bucket is non-idle or active */
                if (myBucket.isActive() || Boolean.TRUE.equals(!myBucket.isIdle())) {
                    /* Record the rate (if required) and add to list */
                    myBucket.recordRate(pDate);
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
        protected void constructFromBase(final MoneyWiseAnalysisAccountBucketList<B, T> pBase,
                                         final OceanusDateRange pRange) {
            /* Loop through the buckets */
            final Iterator<B> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final B myCurr = myIterator.next();

                /* Access the bucket for this range */
                final B myBucket = newBucket(myCurr, pRange);

                /* If the bucket is non-idle or active */
                if (myBucket.isActive()
                        || Boolean.TRUE.equals(!myBucket.isIdle())) {
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
