/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.analysis;

import java.text.DecimalFormatSymbols;
import java.util.Currency;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.list.MetisOrderedIdItem;
import net.sourceforge.joceanus.jmetis.lethe.list.MetisOrderedIdList;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.ExchangeRate;
import net.sourceforge.joceanus.jmoneywise.data.ExchangeRate.ExchangeRateDataMap;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;

/**
 * The Account Bucket class.
 * @param <T> the account data type
 */
public abstract class AccountBucket<T extends AssetBase<T>>
        implements MetisDataContents, Comparable<AccountBucket<T>>, MetisOrderedIdItem<Integer> {
    /**
     * Default currency.
     */
    protected static final Currency DEFAULT_CURRENCY = DecimalFormatSymbols.getInstance().getCurrency();

    /**
     * Local Report fields.
     */
    protected static final MetisFields FIELD_DEFS = new MetisFields(AccountBucket.class.getSimpleName());

    /**
     * Analysis Field Id.
     */
    private static final MetisField FIELD_ANALYSIS = FIELD_DEFS.declareEqualityField(AnalysisResource.ANALYSIS_NAME.getValue());

    /**
     * Account Field Id.
     */
    private static final MetisField FIELD_ACCOUNT = FIELD_DEFS.declareEqualityField(AnalysisResource.BUCKET_ACCOUNT.getValue());

    /**
     * Base Field Id.
     */
    private static final MetisField FIELD_BASE = FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_BASEVALUES.getValue());

    /**
     * History Field Id.
     */
    private static final MetisField FIELD_HISTORY = FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_HISTORY.getValue());

    /**
     * FieldSet map.
     */
    private static final Map<MetisField, AccountAttribute> FIELDSET_MAP = MetisFields.buildFieldMap(FIELD_DEFS, AccountAttribute.class);

    /**
     * Totals bucket name.
     */
    private static final String NAME_TOTALS = AnalysisResource.ANALYSIS_TOTALS.getValue();

    /**
     * The analysis.
     */
    private final Analysis theAnalysis;

    /**
     * The account.
     */
    private final T theAccount;

    /**
     * Is this a foreign currency?
     */
    private final Boolean isForeignCurrency;

    /**
     * The dataSet.
     */
    private final MoneyWiseData theData;

    /**
     * Values.
     */
    private final AccountValues theValues;

    /**
     * The base values.
     */
    private final AccountValues theBaseValues;

    /**
     * History Map.
     */
    private final BucketHistory<AccountValues, AccountAttribute> theHistory;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pAccount the account
     */
    protected AccountBucket(final Analysis pAnalysis,
                            final T pAccount) {
        /* Store the details */
        theAccount = pAccount;
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();

        /* Determine currency */
        AssetCurrency myReportingCurrency = pAnalysis.getCurrency();
        AssetCurrency myAccountCurrency = (pAccount == null)
                                                             ? myReportingCurrency
                                                             : pAccount.getAssetCurrency();

        /* Determine whether we are a foreign currency */
        isForeignCurrency = !MetisDifference.isEqual(myReportingCurrency, myAccountCurrency);
        Currency myCurrency = deriveCurrency(myAccountCurrency);
        Currency myRepCurrency = deriveCurrency(myReportingCurrency);

        /* Create the history map */
        AccountValues myValues = isForeignCurrency
                                                   ? allocateForeignValues(myCurrency, myRepCurrency)
                                                   : allocateStandardValues(myCurrency);
        theHistory = new BucketHistory<>(myValues);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     */
    protected AccountBucket(final Analysis pAnalysis,
                            final AccountBucket<T> pBase) {
        /* Copy details from base */
        theAccount = pBase.getAccount();
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();
        isForeignCurrency = pBase.isForeignCurrency();

        /* Access the relevant history */
        theHistory = new BucketHistory<>(pBase.getHistoryMap());

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
    protected AccountBucket(final Analysis pAnalysis,
                            final AccountBucket<T> pBase,
                            final TethysDate pDate) {
        /* Copy details from base */
        theAccount = pBase.getAccount();
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();
        isForeignCurrency = pBase.isForeignCurrency();

        /* Access the relevant history */
        theHistory = new BucketHistory<>(pBase.getHistoryMap(), pDate);

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
    protected AccountBucket(final Analysis pAnalysis,
                            final AccountBucket<T> pBase,
                            final TethysDateRange pRange) {
        /* Copy details from base */
        theAccount = pBase.getAccount();
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();
        isForeignCurrency = pBase.isForeignCurrency();

        /* Access the relevant history */
        theHistory = new BucketHistory<>(pBase.getHistoryMap(), pRange);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        if (FIELD_ANALYSIS.equals(pField)) {
            return theAnalysis;
        }
        if (FIELD_ACCOUNT.equals(pField)) {
            return theAccount;
        }
        if (FIELD_HISTORY.equals(pField)) {
            return theHistory;
        }
        if (FIELD_BASE.equals(pField)) {
            return theBaseValues;
        }

        /* Handle Attribute fields */
        AccountAttribute myClass = getClassForField(pField);
        if (myClass != null) {
            Object myValue = getAttributeValue(myClass);
            if (myValue instanceof TethysDecimal) {
                return ((TethysDecimal) myValue).isNonZero()
                                                             ? myValue
                                                             : MetisFieldValue.SKIP;
            }
            return myValue;
        }

        return MetisFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return getName();
    }

    @Override
    public String toString() {
        return formatObject();
    }

    /**
     * derive currency.
     * @param pAssetCurrency the asset currency
     * @return the actual currency to use
     */
    protected static Currency deriveCurrency(final AssetCurrency pAssetCurrency) {
        return pAssetCurrency == null
                                      ? DEFAULT_CURRENCY
                                      : pAssetCurrency.getCurrency();
    }

    /**
     * allocate standard values.
     * @param pCurrency the asset currency
     * @return the actual currency to use
     */
    protected AccountValues allocateStandardValues(final Currency pCurrency) {
        return new AccountValues(pCurrency);
    }

    /**
     * allocate foreign values.
     * @param pCurrency the asset currency
     * @param pReportingCurrency the reporting currency
     * @return the actual currency to use
     */
    protected AccountValues allocateForeignValues(final Currency pCurrency,
                                                  final Currency pReportingCurrency) {
        return new AccountValues(pCurrency, pReportingCurrency);
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
    public Integer getOrderedId() {
        return theAccount.getId();
    }

    /**
     * Is this bucket idle?
     * @return true/false
     */
    public Boolean isIdle() {
        return theHistory.isIdle();
    }

    /**
     * Obtain the dataSet.
     * @return the dataSet
     */
    protected MoneyWiseData getDataSet() {
        return theData;
    }

    /**
     * Obtain the analysis.
     * @return the analysis
     */
    protected Analysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Obtain date range.
     * @return the range
     */
    public TethysDateRange getDateRange() {
        return theAnalysis.getDateRange();
    }

    /**
     * Obtain the value map.
     * @return the value map
     */
    public AccountValues getValues() {
        return theValues;
    }

    /**
     * Obtain the base value map.
     * @return the base value map
     */
    public AccountValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Obtain values for transaction.
     * @param pTrans the transaction
     * @return the values (or null)
     */
    public AccountValues getValuesForTransaction(final Transaction pTrans) {
        return theHistory.getValuesForTransaction(pTrans);
    }

    /**
     * Obtain previous values for transaction.
     * @param pTrans the transaction
     * @return the values (or null)
     */
    public AccountValues getPreviousValuesForTransaction(final Transaction pTrans) {
        return theHistory.getPreviousValuesForTransaction(pTrans);
    }

    /**
     * Obtain delta for transaction.
     * @param pTrans the transaction
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public TethysDecimal getDeltaForTransaction(final Transaction pTrans,
                                                final AccountAttribute pAttr) {
        /* Obtain delta for transaction */
        return theHistory.getDeltaValue(pTrans, pAttr);
    }

    /**
     * Obtain money delta for transaction.
     * @param pTrans the transaction
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public TethysMoney getMoneyDeltaForTransaction(final Transaction pTrans,
                                                   final AccountAttribute pAttr) {
        /* Obtain delta for transaction */
        return theHistory.getDeltaMoneyValue(pTrans, pAttr);
    }

    /**
     * Obtain the history map.
     * @return the history map
     */
    private BucketHistory<AccountValues, AccountAttribute> getHistoryMap() {
        return theHistory;
    }

    /**
     * Set Value.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    protected void setValue(final AccountAttribute pAttr,
                            final Object pValue) {
        /* Set the value */
        theValues.setValue(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final AccountAttribute pAttr) {
        /* Access value of object */
        Object myValue = getValue(pAttr);

        /* Return the value */
        return (myValue != null)
                                 ? myValue
                                 : MetisFieldValue.SKIP;
    }

    /**
     * Obtain the class of the field if it is an attribute field.
     * @param pField the field
     * @return the class
     */
    private static AccountAttribute getClassForField(final MetisField pField) {
        /* Look up field in map */
        return FIELDSET_MAP.get(pField);
    }

    /**
     * Obtain an attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    private Object getValue(final AccountAttribute pAttr) {
        /* Obtain the attribute value */
        return theValues.getValue(pAttr);
    }

    @Override
    public int compareTo(final AccountBucket<T> pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the Accounts */
        return getAccount().compareTo(pThat.getAccount());
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }
        if (!(pThat instanceof AccountBucket)) {
            return false;
        }

        /* Compare the Accounts */
        AccountBucket<?> myThat = (AccountBucket<?>) pThat;
        if (!getAccount().equals(myThat.getAccount())) {
            return false;
        }

        /* Compare the date ranges */
        return getDateRange().equals(myThat.getDateRange());
    }

    @Override
    public int hashCode() {
        return getAccount().hashCode();
    }

    /**
     * Adjust counter.
     * @param pAttr the attribute
     * @param pDelta the delta
     */
    protected void adjustCounter(final AccountAttribute pAttr,
                                 final TethysMoney pDelta) {
        TethysMoney myValue = theValues.getMoneyValue(pAttr);
        myValue = new TethysMoney(myValue);
        myValue.addAmount(pDelta);
        setValue(pAttr, myValue);
    }

    /**
     * Adjust account for debit.
     * @param pHelper the transaction helper
     */
    protected void adjustForDebit(final TransactionHelper pHelper) {
        /* Access event amount */
        TethysMoney myAmount = pHelper.getDebitAmount();

        /* If we have a non-zero amount */
        if (myAmount.isNonZero()) {
            /* Adjust valuation */
            myAmount = new TethysMoney(myAmount);
            myAmount.negate();

            /* If we are a foreign account */
            if (isForeignCurrency) {
                /* Access local amount amount */
                TethysMoney myLocalAmount = pHelper.getLocalAmount();

                /* Adjust counters */
                adjustCounter(AccountAttribute.FOREIGNVALUE, myAmount);
                adjustCounter(AccountAttribute.LOCALVALUE, myLocalAmount);

                /* Obtain the debit exchangeRate and convert the foreign valuation */
                TethysRatio myRate = pHelper.getDebitExchangeRate();
                TethysMoney myLocalValue = myAmount.convertCurrency(theAnalysis.getCurrency().getCurrency(), myRate.getInverseRatio());

                /* Set the valuation */
                setValue(AccountAttribute.VALUATION, myLocalValue);
                setValue(AccountAttribute.EXCHANGERATE, myRate);

                /* Determine currency fluctuation */
                TethysMoney myFluct = new TethysMoney(myLocalValue);
                myFluct.subtractAmount(theValues.getMoneyValue(AccountAttribute.LOCALVALUE));
                adjustCounter(AccountAttribute.CURRENCYFLUCT, myFluct);

                /* else this is a standard account */
            } else {
                /* Adjust valuation */
                adjustCounter(AccountAttribute.VALUATION, myAmount);
            }
        }

        /* Register the transaction in the history */
        registerTransaction(pHelper);
    }

    /**
     * Adjust account for credit.
     * @param pHelper the transaction helper
     */
    protected void adjustForCredit(final TransactionHelper pHelper) {
        /* Access event amount */
        TethysMoney myAmount = pHelper.getCreditAmount();

        /* If we have a non-zero amount */
        if (myAmount.isNonZero()) {
            /* If we are a foreign account */
            if (isForeignCurrency) {
                /* Access local amount amount */
                TethysMoney myLocalAmount = pHelper.getLocalAmount();

                /* Adjust counters */
                adjustCounter(AccountAttribute.FOREIGNVALUE, myAmount);
                adjustCounter(AccountAttribute.LOCALVALUE, myLocalAmount);

                /* Obtain the credit exchangeRate and convert the foreign valuation */
                TethysRatio myRate = pHelper.getCreditExchangeRate();
                TethysMoney myLocalValue = myAmount.convertCurrency(theAnalysis.getCurrency().getCurrency(), myRate.getInverseRatio());

                /* Set the valuation */
                setValue(AccountAttribute.VALUATION, myLocalValue);
                setValue(AccountAttribute.EXCHANGERATE, myRate);

                /* Determine currency fluctuation */
                TethysMoney myFluct = new TethysMoney(myLocalValue);
                myFluct.subtractAmount(theValues.getMoneyValue(AccountAttribute.LOCALVALUE));
                setValue(AccountAttribute.CURRENCYFLUCT, myFluct);

                /* else this is a standard account */
            } else {
                /* Adjust valuation */
                adjustCounter(AccountAttribute.VALUATION, myAmount);
            }
        }

        /* Register the transaction in the history */
        registerTransaction(pHelper);
    }

    /**
     * Adjust account for credit.
     * @param pHelper the transaction helper
     */
    protected void adjustForThirdPartyCredit(final TransactionHelper pHelper) {
        /* Access event amount */
        TethysMoney myAmount = pHelper.getThirdPartyAmount();

        /* If we have a non-zero amount */
        if (myAmount.isNonZero()) {
            /* If we are a foreign account */
            if (isForeignCurrency) {
                /* Access local amount amount */
                TethysMoney myLocalAmount = pHelper.getLocalThirdPartyAmount();

                /* Adjust counters */
                adjustCounter(AccountAttribute.FOREIGNVALUE, myAmount);
                adjustCounter(AccountAttribute.LOCALVALUE, myLocalAmount);

                /* Obtain the credit exchangeRate and convert the foreign valuation */
                TethysRatio myRate = pHelper.getThirdPartyExchangeRate();
                TethysMoney myLocalValue = myAmount.convertCurrency(theAnalysis.getCurrency().getCurrency(), myRate.getInverseRatio());

                /* Set the valuation */
                setValue(AccountAttribute.VALUATION, myLocalValue);
                setValue(AccountAttribute.EXCHANGERATE, myRate);

                /* Determine currency fluctuation */
                TethysMoney myFluct = new TethysMoney(myLocalValue);
                myFluct.subtractAmount(theValues.getMoneyValue(AccountAttribute.LOCALVALUE));
                setValue(AccountAttribute.CURRENCYFLUCT, myFluct);

                /* else this is a standard account */
            } else {
                /* Adjust valuation */
                adjustCounter(AccountAttribute.VALUATION, myAmount);
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
    protected void setOpeningBalance(final TransactionHelper pHelper,
                                     final TethysMoney pBalance) {
        /* Obtain the base valuation */
        AccountValues myValues = getBaseValues();
        TethysMoney myBaseValue = myValues.getMoneyValue(AccountAttribute.VALUATION);

        /* If we are a foreign account */
        if (isForeignCurrency) {
            /* Obtain the foreign valuation */
            TethysMoney myForeignValue = myValues.getMoneyValue(AccountAttribute.FOREIGNVALUE);
            TethysMoney myLocalValue = myValues.getMoneyValue(AccountAttribute.LOCALVALUE);

            /* Obtain exchange rate and reporting value */
            TethysRatio myRate = pHelper.getExchangeRate(theAccount.getAssetCurrency(), theData.getDateRange().getStart());
            TethysMoney myLocalAmount = pBalance.convertCurrency(theAnalysis.getCurrency().getCurrency(), myRate.getInverseRatio());

            /* Record details */
            myBaseValue.addAmount(myLocalAmount);
            myLocalValue.addAmount(myLocalAmount);
            myForeignValue.addAmount(pBalance);
            myValues.setValue(AccountAttribute.EXCHANGERATE, myRate);

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
    protected void registerTransaction(final TransactionHelper pHelper) {
        /* Register the transaction in the history */
        theHistory.registerTransaction(pHelper.getTransaction(), theValues);
    }

    /**
     * Register the transaction.
     * @param pTrans the transaction
     */
    protected void registerTransaction(final Transaction pTrans) {
        /* Register the transaction in the history */
        theHistory.registerTransaction(pTrans, theValues);
    }

    /**
     * calculate currency fluctuations over the range.
     * @param pRange the range of valuation
     */
    protected void calculateFluctuations(final TethysDateRange pRange) {
        /* Obtain the appropriate rates */
        MoneyWiseData myData = theAnalysis.getData();
        ExchangeRateDataMap<ExchangeRate> myRateMap = myData.getExchangeRateDataMap();
        TethysRatio[] myRates = myRateMap.getRatesForRange(theAccount.getAssetCurrency(), pRange);
        Currency myBaseCurrency = theAnalysis.getCurrency().getCurrency();

        /* Access the base value */
        TethysRatio myRate = myRates[0];
        TethysMoney myForeignValue = theBaseValues.getMoneyValue(AccountAttribute.FOREIGNVALUE);
        TethysMoney myLocalValue = theBaseValues.getMoneyValue(AccountAttribute.LOCALVALUE);

        /* Calculate the base value */
        TethysMoney myLocalValuation = myForeignValue.convertCurrency(myBaseCurrency, myRate.getInverseRatio());
        theBaseValues.setValue(AccountAttribute.EXCHANGERATE, myRate);
        theBaseValues.setValue(AccountAttribute.VALUATION, myLocalValuation);

        /* Determine currency fluctuation */
        TethysMoney myFluct = new TethysMoney(myLocalValuation);
        myFluct.subtractAmount(myLocalValue);
        theBaseValues.setValue(AccountAttribute.CURRENCYFLUCT, myFluct);

        /* Access current values */
        myRate = myRates[1];
        myForeignValue = theValues.getMoneyValue(AccountAttribute.FOREIGNVALUE);
        myLocalValue = theValues.getMoneyValue(AccountAttribute.LOCALVALUE);

        /* Calculate the current value */
        myLocalValuation = myForeignValue.convertCurrency(myBaseCurrency, myRate.getInverseRatio());
        theValues.setValue(AccountAttribute.EXCHANGERATE, myRate);
        theValues.setValue(AccountAttribute.VALUATION, myLocalValuation);

        /* Determine currency fluctuation */
        myFluct = new TethysMoney(myLocalValuation);
        myFluct.subtractAmount(myLocalValue);
        theValues.setValue(AccountAttribute.CURRENCYFLUCT, myFluct);
    }

    /**
     * Calculate delta.
     */
    protected void calculateDelta() {
        /* Obtain a copy of the value */
        TethysMoney myDelta = theValues.getMoneyValue(AccountAttribute.VALUATION);
        myDelta = new TethysMoney(myDelta);

        /* Subtract any base value */
        TethysMoney myBase = theBaseValues.getMoneyValue(AccountAttribute.VALUATION);
        myDelta.subtractAmount(myBase);

        /* Set the delta */
        setValue(AccountAttribute.VALUEDELTA, myDelta);

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
    protected void recordRate(final TethysDate pDate) {
    }

    /**
     * AccountValues class.
     */
    public static class AccountValues
            extends BucketValues<AccountValues, AccountAttribute> {
        /**
         * Constructor.
         * @param pCurrency the account currency
         */
        protected AccountValues(final Currency pCurrency) {
            /* Initialise class */
            super(AccountAttribute.class);

            /* Initialise valuation to zero */
            setValue(AccountAttribute.VALUATION, new TethysMoney(pCurrency));
        }

        /**
         * Constructor.
         * @param pCurrency the account currency
         * @param pReportingCurrency the reporting currency
         */
        protected AccountValues(final Currency pCurrency,
                                final Currency pReportingCurrency) {
            /* Initialise class */
            this(pReportingCurrency);

            /* Initialise valuation to zero */
            setValue(AccountAttribute.FOREIGNVALUE, new TethysMoney(pCurrency));
            setValue(AccountAttribute.LOCALVALUE, new TethysMoney(pReportingCurrency));
            setValue(AccountAttribute.CURRENCYFLUCT, new TethysMoney(pReportingCurrency));
        }

        /**
         * Constructor.
         * @param pSource the source map.
         * @param pCountersOnly only copy counters
         */
        protected AccountValues(final AccountValues pSource,
                                final boolean pCountersOnly) {
            /* Initialise class */
            super(pSource, pCountersOnly);
        }

        @Override
        protected AccountValues getCounterSnapShot() {
            return new AccountValues(this, true);
        }

        @Override
        protected AccountValues getFullSnapShot() {
            return new AccountValues(this, false);
        }

        /**
         * Are the values active?
         * @return true/false
         */
        public boolean isActive() {
            TethysMoney myValuation = getMoneyValue(AccountAttribute.VALUATION);
            return (myValuation != null) && (myValuation.isNonZero());
        }

        @Override
        protected void adjustToBaseValues(final AccountValues pBase) {
            /* If we have a currency fluctuation */
            if (getMoneyValue(AccountAttribute.CURRENCYFLUCT) != null) {
                /* Adjust currency fluctuation values */
                adjustMoneyToBase(pBase, AccountAttribute.CURRENCYFLUCT);
            }
        }

        @Override
        protected void resetBaseValues() {
            /* If we have a currency fluctuation */
            TethysMoney myValue = getMoneyValue(AccountAttribute.CURRENCYFLUCT);
            if (myValue != null) {
                /* Create zero value */
                myValue = new TethysMoney(myValue);
                myValue.setZero();

                /* Adjust currency fluctuation values */
                setValue(AccountAttribute.CURRENCYFLUCT, myValue);
            }
        }
    }

    /**
     * AccountBucket list class.
     * @param <B> the account bucket data type
     * @param <T> the account data type
     */
    public abstract static class AccountBucketList<B extends AccountBucket<T>, T extends AssetBase<T>>
            extends MetisOrderedIdList<Integer, B>
            implements MetisDataContents {
        /**
         * Local Report fields.
         */
        protected static final MetisFields FIELD_DEFS = new MetisFields(AccountBucketList.class.getSimpleName());

        /**
         * Size Field Id.
         */
        private static final MetisField FIELD_SIZE = FIELD_DEFS.declareLocalField(MetisDataResource.LIST_SIZE.getValue());

        /**
         * Analysis field Id.
         */
        private static final MetisField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME.getValue());

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * Construct a top-level List.
         * @param pClass the bucket class
         * @param pAnalysis the analysis
         */
        protected AccountBucketList(final Class<B> pClass,
                                    final Analysis pAnalysis) {
            /* Initialise class */
            super(pClass);
            theAnalysis = pAnalysis;
        }

        @Override
        public String formatObject() {
            return getDataFields().getName() + "(" + size() + ")";
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            if (FIELD_ANALYSIS.equals(pField)) {
                return theAnalysis;
            }
            return MetisFieldValue.UNKNOWN;
        }

        /**
         * Obtain the analysis.
         * @return the analysis
         */
        protected Analysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Construct a view List.
         * @param pBase the base list
         */
        protected void constructFromBase(final AccountBucketList<B, T> pBase) {
            /* Loop through the buckets */
            Iterator<B> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                B myCurr = myIterator.next();

                /* Access the bucket */
                B myBucket = newBucket(myCurr);

                /* If the bucket is non-idle or active */
                if (myBucket.isActive() || !myBucket.isIdle()) {
                    /* add to list */
                    append(myBucket);
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
        protected void constructFromBase(final AccountBucketList<B, T> pBase,
                                         final TethysDate pDate) {
            /* Loop through the buckets */
            Iterator<B> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                B myCurr = myIterator.next();

                /* Access the bucket for this date */
                B myBucket = newBucket(myCurr, pDate);

                /* If the bucket is non-idle or active */
                if (myBucket.isActive() || !myBucket.isIdle()) {
                    /* Record the rate (if required) and add to list */
                    myBucket.recordRate(pDate);
                    append(myBucket);
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
                                       TethysDate pDate);

        /**
         * Construct a ranged List.
         * @param pBase the base list
         * @param pRange the Date Range
         */
        protected void constructFromBase(final AccountBucketList<B, T> pBase,
                                         final TethysDateRange pRange) {
            /* Loop through the buckets */
            Iterator<B> myIterator = pBase.listIterator();
            while (myIterator.hasNext()) {
                B myCurr = myIterator.next();

                /* Access the bucket for this range */
                B myBucket = newBucket(myCurr, pRange);

                /* If the bucket is non-idle or active */
                if (myBucket.isActive() || !myBucket.isIdle()) {
                    /* Add to the list */
                    append(myBucket);
                }
            }
        }

        /**
         * Construct a ranged bucket.
         * @param pBase the base bucket
         * @param pRange the Range
         * @return the new bucket
         */
        protected abstract B newBucket(B pBase,
                                       TethysDateRange pRange);

        /**
         * Obtain the AccountBucket for a given account.
         * @param pAccount the account
         * @return the bucket
         */
        protected B getBucket(final T pAccount) {
            /* Locate the bucket in the list */
            B myItem = findItemById(pAccount.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = newBucket(pAccount);

                /* Add to the list */
                add(myItem);
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
         * Mark active accounts.
         * @throws OceanusException on error
         */
        protected void markActiveAccounts() throws OceanusException {
            /* Loop through the buckets */
            Iterator<B> myIterator = iterator();
            while (myIterator.hasNext()) {
                B myCurr = myIterator.next();
                T myAccount = myCurr.getAccount();

                /* If we are active */
                if (myCurr.isActive()) {
                    /* Set the account as relevant */
                    myAccount.setRelevant();
                }

                /* If we are closed */
                if (myAccount.isClosed()) {
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
