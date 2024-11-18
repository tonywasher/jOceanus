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
package net.sourceforge.joceanus.moneywise.atlas.views;

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisAttribute;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisValues;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisAccountBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPayeeBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioCashBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransCategoryBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransTagBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisType;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountValues;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisPayeeAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisPayeeValues;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityValues;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisTaxBasisAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisTaxBasisValues;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisTransAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisTransValues;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCash;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoan;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransDefaults;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseViewResource;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Analysis Filter Classes.
 * @param <B> the underlying bucket type
 * @param <T> the attribute for the filter
 */
public abstract class MoneyWiseXAnalysisFilter<B, T extends Enum<T> & MoneyWiseXAnalysisAttribute>
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisFieldSet<MoneyWiseXAnalysisFilter> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisFilter.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.MONEYWISEDATA_RANGE, MoneyWiseXAnalysisFilter::getDateRange);
        FIELD_DEFS.declareLocalField(MoneyWiseViewResource.FILTER_BUCKET, MoneyWiseXAnalysisFilter::getBucket);
        FIELD_DEFS.declareLocalField(MoneyWiseViewResource.FILTER_ATTR, MoneyWiseXAnalysisFilter::getCurrentAttribute);
    }

    /**
     * The Underlying bucket.
     */
    private final B theBucket;

    /**
     * The Attribute class.
     */
    private final Class<T> theClass;

    /**
     * The Date Range.
     */
    private TethysDateRange theDateRange;

    /**
     * The Current Attribute.
     */
    private T theAttr;

    /**
     * Constructor.
     * @param pBucket the underlying bucket
     * @param pClass the attribute class
     */
    protected MoneyWiseXAnalysisFilter(final B pBucket,
                                       final Class<T> pClass) {
        theBucket = pBucket;
        theClass = pClass;
        theDateRange = new TethysDateRange();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public MetisFieldSet<MoneyWiseXAnalysisFilter> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return getName();
    }

    /**
     * Set attribute.
     * @param pAttr the attribute
     */
    public void setCurrentAttribute(final MoneyWiseXAnalysisAttribute pAttr) {
        theAttr = theClass.cast(pAttr);
    }

    /**
     * Obtain current attribute.
     * @return the current attribute
     */
    public T getCurrentAttribute() {
        return theAttr;
    }

    /**
     * Get Analysis Type.
     * @return the Analysis Type
     */
    public abstract MoneyWiseXAnalysisType getAnalysisType();

    /**
     * Obtain underlying bucket.
     * @return theBucket
     */
    public B getBucket() {
        return theBucket;
    }

    /**
     * Obtain the dateRange.
     * @return the dateRange
     */
    public TethysDateRange getDateRange() {
        return theDateRange;
    }

    /**
     * Obtain the dateRange.
     * @param pRange the dateRange
     */
    public void setDateRange(final TethysDateRange pRange) {
        theDateRange = pRange;
    }

    /**
     * Should we filter this event out?
     * @param pEvent the event to check
     * @return true/false
     */
    public boolean filterEvent(final MoneyWiseXAnalysisEvent pEvent) {
        /* Check whether this transaction is registered */
        return !pEvent.isHeader()
                && (theDateRange.compareToDate(pEvent.getDate()) != 0
                || getValuesForEvent(pEvent) == null);
    }

    /**
     * Obtain base bucket values.
     * @return the value
     */
    protected abstract MoneyWiseXAnalysisValues<?, T> getBaseValues();

    /**
     * Obtain values for event.
     * @param pEvent the event
     * @return the values
     */
    public abstract MoneyWiseXAnalysisValues<?, T> getValuesForEvent(MoneyWiseXAnalysisEvent pEvent);

    /**
     * Obtain delta value for event.
     * @param pEvent the event
     * @return the delta value
     */
    public abstract TethysDecimal getDeltaForEvent(MoneyWiseXAnalysisEvent pEvent);

    /**
     * Populate new transaction.
     * @param pBuilder the transaction builder
     * @return the new transaction (or null)
     */
    public abstract MoneyWiseTransaction buildNewTransaction(MoneyWiseTransDefaults pBuilder);

    /**
     * is the counter relevant?
     * @param pCounter the counter
     * @return true/false
     */
    public boolean isRelevantCounter(final MoneyWiseXAnalysisAttribute pCounter) {
        return true;
    }

    /**
     * Obtain starting value for attribute.
     * @return the value
     */
    public TethysDecimal getStartingBalance() {
        final MoneyWiseXAnalysisValues<?, T> myValues = getBaseValues();
        return myValues.getDecimalValue(getCurrentAttribute());
    }

    /**
     * Obtain total money value for attribute.
     * @param pEvent the event to check
     * @return the value
     */
    public TethysDecimal getBalanceForEvent(final MoneyWiseXAnalysisEvent pEvent) {
        final MoneyWiseXAnalysisValues<?, T> myValues = getValuesForEvent(pEvent);
        return myValues == null
                ? null
                : myValues.getDecimalValue(getCurrentAttribute());
    }

    /**
     * Obtain delta debit value for attribute.
     * @param pEvent the event to check
     * @return the value
     */
    public TethysDecimal getDebitForEvent(final MoneyWiseXAnalysisEvent pEvent) {
        TethysDecimal myValue = getDeltaValueForEvent(pEvent);
        if (myValue != null) {
            if (myValue.isPositive()
                    || myValue.isZero()) {
                myValue = null;
            } else {
                myValue.negate();
            }
        }
        return myValue;
    }

    /**
     * Obtain delta credit value for attribute.
     * @param pEvent the event to check
     * @return the value
     */
    public TethysDecimal getCreditForEvent(final MoneyWiseXAnalysisEvent pEvent) {
        final TethysDecimal myValue = getDeltaValueForEvent(pEvent);
        return (myValue != null
                && myValue.isPositive() && myValue.isNonZero())
                ? myValue
                : null;
    }

    /**
     * Obtain delta value for attribute.
     * @param pEvent the event to check
     * @return the value
     */
    private TethysDecimal getDeltaValueForEvent(final MoneyWiseXAnalysisEvent pEvent) {
        return getDeltaForEvent(pEvent);
    }

    /**
     * Obtain analysis name.
     * @return the name
     */
    public abstract String getName();

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check class */
        if (getClass() != pThat.getClass()) {
            return false;
        }

        /* Access as AccountFilter */
        final MoneyWiseXAnalysisFilter<?, ?> myThat = (MoneyWiseXAnalysisFilter<?, ?>) pThat;

        /* Check equality */
        return MetisDataDifference.isEqual(getBucket(), myThat.getBucket());
    }

    @Override
    public int hashCode() {
        return getBucket().hashCode();
    }

    /**
     * Account filter class.
     * @param <B> the underlying bucket type
     * @param <T> the account data type
     */
    public abstract static class MoneyWiseXAnalysisAccountFilter<B extends MoneyWiseXAnalysisAccountBucket<T>, T extends MoneyWiseAssetBase>
            extends MoneyWiseXAnalysisFilter<B, MoneyWiseXAnalysisAccountAttr> {
        /**
         * Constructor.
         * @param pAccount the account bucket
         */
        protected MoneyWiseXAnalysisAccountFilter(final B pAccount) {
            /* Store parameter */
            super(pAccount, MoneyWiseXAnalysisAccountAttr.class);
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        @Override
        public String getName() {
            return getBucket().getName();
        }

        @Override
        protected MoneyWiseXAnalysisAccountValues getBaseValues() {
            return getBucket().getBaseValues();
        }

        @Override
        public MoneyWiseXAnalysisAccountValues getValuesForEvent(final MoneyWiseXAnalysisEvent pEvent) {
            return getBucket().getValuesForEvent(pEvent);
        }

        @Override
        public TethysDecimal getDeltaForEvent(final MoneyWiseXAnalysisEvent pEvent) {
            return getBucket().getDeltaForEvent(pEvent, getCurrentAttribute());
        }

        @Override
        public MoneyWiseTransaction buildNewTransaction(final MoneyWiseTransDefaults pBuilder) {
            return pBuilder.buildTransaction(getBucket().getAccount());
        }
    }

    /**
     * Deposit Bucket filter class.
     */
    public static class MoneyWiseXAnalysisDepositFilter
            extends MoneyWiseXAnalysisAccountFilter<MoneyWiseXAnalysisDepositBucket, MoneyWiseDeposit> {
        /**
         * Constructor.
         * @param pDeposit the deposit bucket
         */
        public MoneyWiseXAnalysisDepositFilter(final MoneyWiseXAnalysisDepositBucket pDeposit) {
            /* Call super-constructor */
            super(pDeposit);
        }

        @Override
        public MoneyWiseXAnalysisType getAnalysisType() {
            return MoneyWiseXAnalysisType.DEPOSIT;
        }

        @Override
        public boolean isRelevantCounter(final MoneyWiseXAnalysisAttribute pCounter) {
            if (!(pCounter instanceof MoneyWiseXAnalysisAccountAttr)) {
                return false;
            }

            switch ((MoneyWiseXAnalysisAccountAttr) pCounter) {
                case VALUATION:
                    return true;
                case BALANCE:
                    return getBucket().isForeignCurrency();
                case EXCHANGERATE:
                case DEPOSITRATE:
                case VALUEDELTA:
                case MATURITY:
                default:
                    return false;
            }
        }
    }

    /**
     * Cash Bucket filter class.
     */
    public static class MoneyWiseXAnalysisCashFilter
            extends MoneyWiseXAnalysisAccountFilter<MoneyWiseXAnalysisCashBucket, MoneyWiseCash> {
        /**
         * Constructor.
         * @param pCash the cash bucket
         */
        public MoneyWiseXAnalysisCashFilter(final MoneyWiseXAnalysisCashBucket pCash) {
            /* Call super-constructor */
            super(pCash);
        }

        @Override
        public MoneyWiseXAnalysisType getAnalysisType() {
            return MoneyWiseXAnalysisType.CASH;
        }

        @Override
        public boolean isRelevantCounter(final MoneyWiseXAnalysisAttribute pCounter) {
            if (!(pCounter instanceof MoneyWiseXAnalysisAccountAttr)) {
                return false;
            }

            switch ((MoneyWiseXAnalysisAccountAttr) pCounter) {
                case VALUATION:
                    return true;
                case BALANCE:
                    return getBucket().isForeignCurrency();
                case EXCHANGERATE:
                case DEPOSITRATE:
                case VALUEDELTA:
                case MATURITY:
                default:
                    return false;
            }
        }
    }

    /**
     * Loan Bucket filter class.
     */
    public static class MoneyWiseXAnalysisLoanFilter
            extends MoneyWiseXAnalysisAccountFilter<MoneyWiseXAnalysisLoanBucket, MoneyWiseLoan> {
        /**
         * Constructor.
         * @param pLoan the loan bucket
         */
        public MoneyWiseXAnalysisLoanFilter(final MoneyWiseXAnalysisLoanBucket pLoan) {
            /* Call super-constructor */
            super(pLoan);
        }

        @Override
        public MoneyWiseXAnalysisType getAnalysisType() {
            return MoneyWiseXAnalysisType.LOAN;
        }

        @Override
        public boolean isRelevantCounter(final MoneyWiseXAnalysisAttribute pCounter) {
            if (!(pCounter instanceof MoneyWiseXAnalysisAccountAttr)) {
                return false;
            }

            switch ((MoneyWiseXAnalysisAccountAttr) pCounter) {
                case VALUATION:
                    return true;
                case BALANCE:
                    return getBucket().isForeignCurrency();
                case EXCHANGERATE:
                case DEPOSITRATE:
                case VALUEDELTA:
                case MATURITY:
                default:
                    return false;
            }
        }
    }

    /**
     * Security Bucket filter class.
     */
    public static class MoneyWiseXAnalysisSecurityFilter
            extends MoneyWiseXAnalysisFilter<MoneyWiseXAnalysisSecurityBucket, MoneyWiseXAnalysisSecurityAttr> {
        /**
         * Constructor.
         * @param pSecurity the security bucket
         */
        public MoneyWiseXAnalysisSecurityFilter(final MoneyWiseXAnalysisSecurityBucket pSecurity) {
            /* Store parameter */
            super(pSecurity, MoneyWiseXAnalysisSecurityAttr.class);
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        @Override
        public String getName() {
            return getBucket().getDecoratedName();
        }

        @Override
        public MoneyWiseXAnalysisType getAnalysisType() {
            return MoneyWiseXAnalysisType.SECURITY;
        }

        @Override
        protected MoneyWiseXAnalysisSecurityValues getBaseValues() {
            return getBucket().getBaseValues();
        }

        @Override
        public MoneyWiseXAnalysisSecurityValues getValuesForEvent(final MoneyWiseXAnalysisEvent pEvent) {
            return getBucket().getValuesForEvent(pEvent);
        }

        @Override
        public TethysDecimal getDeltaForEvent(final MoneyWiseXAnalysisEvent pEvent) {
            return getBucket().getDeltaForEvent(pEvent, getCurrentAttribute());
        }

        @Override
        public MoneyWiseTransaction buildNewTransaction(final MoneyWiseTransDefaults pBuilder) {
            return pBuilder.buildTransaction(getBucket().getSecurityHolding());
        }

        @Override
        public boolean isRelevantCounter(final MoneyWiseXAnalysisAttribute pCounter) {
            if (!(pCounter instanceof MoneyWiseXAnalysisSecurityAttr)) {
                return false;
            }

            switch ((MoneyWiseXAnalysisSecurityAttr) pCounter) {
                case DIVIDEND:
                case RESIDUALCOST:
                case REALISEDGAINS:
                case UNREALISEDGAINS:
                case UNITS:
                    return true;
                case VALUE:
                    return getBucket().isForeignCurrency();
                case PRICE:
                case EXCHANGERATE:
                case VALUEDELTA:
                case PROFIT:
                case MARKETPROFIT:
                case CASHINVESTED:
                case RETURNEDCASH:
                case XFERREDVALUE:
                case XFERREDCOST:
                case CAPITALGAIN:
                case ALLOWEDCOST:
                case FUNDED:
                case STARTDATE:
                case SLICEGAIN:
                case SLICEYEARS:
                case CASHTYPE:
                default:
                    return false;
            }
        }
    }

    /**
     * Portfolio Bucket filter class.
     */
    public static class MoneyWiseXAnalysisPortfolioCashFilter
            extends MoneyWiseXAnalysisFilter<MoneyWiseXAnalysisPortfolioCashBucket, MoneyWiseXAnalysisAccountAttr> {
        /**
         * The portfolio bucket.
         */
        private final MoneyWiseXAnalysisPortfolioBucket thePortfolio;

        /**
         * Constructor.
         * @param pPortfolio the portfolio bucket
         */
        public MoneyWiseXAnalysisPortfolioCashFilter(final MoneyWiseXAnalysisPortfolioBucket pPortfolio) {
            /* Store parameter */
            super(pPortfolio.getPortfolioCash(), MoneyWiseXAnalysisAccountAttr.class);
            thePortfolio = pPortfolio;
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        /**
         * Obtain portfolio bucket.
         * @return the portfolio bucket
         */
        public MoneyWiseXAnalysisPortfolioBucket getPortfolioBucket() {
            return thePortfolio;
        }

        @Override
        public String getName() {
            return thePortfolio.getName();
        }

        @Override
        public MoneyWiseXAnalysisType getAnalysisType() {
            return MoneyWiseXAnalysisType.PORTFOLIO;
        }

        @Override
        protected MoneyWiseXAnalysisAccountValues getBaseValues() {
            return getBucket().getBaseValues();
        }

        @Override
        public MoneyWiseXAnalysisAccountValues getValuesForEvent(final MoneyWiseXAnalysisEvent pEvent) {
            return getBucket().getValuesForEvent(pEvent);
        }

        @Override
        public TethysDecimal getDeltaForEvent(final MoneyWiseXAnalysisEvent pEvent) {
            return getBucket().getDeltaForEvent(pEvent, getCurrentAttribute());
        }

        @Override
        public MoneyWiseTransaction buildNewTransaction(final MoneyWiseTransDefaults pBuilder) {
            return pBuilder.buildTransaction(getBucket().getAccount());
        }

        @Override
        public boolean isRelevantCounter(final MoneyWiseXAnalysisAttribute pCounter) {
            if (!(pCounter instanceof MoneyWiseXAnalysisAccountAttr)) {
                return false;
            }

            switch ((MoneyWiseXAnalysisAccountAttr) pCounter) {
                case BALANCE:
                    return getBucket().isForeignCurrency();
                case EXCHANGERATE:
                case DEPOSITRATE:
                case VALUEDELTA:
                case MATURITY:
                    return false;
                case VALUATION:
                default:
                    return true;
            }
        }
    }

    /**
     * Payee Bucket filter class.
     */
    public static class MoneyWiseXAnalysisPayeeFilter
            extends MoneyWiseXAnalysisFilter<MoneyWiseXAnalysisPayeeBucket, MoneyWiseXAnalysisPayeeAttr> {
        /**
         * Constructor.
         * @param pPayee the payee bucket
         */
        public MoneyWiseXAnalysisPayeeFilter(final MoneyWiseXAnalysisPayeeBucket pPayee) {
            /* Store parameter */
            super(pPayee, MoneyWiseXAnalysisPayeeAttr.class);
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        @Override
        public String getName() {
            return getBucket().getName();
        }

        @Override
        public MoneyWiseXAnalysisType getAnalysisType() {
            return MoneyWiseXAnalysisType.PAYEE;
        }

        @Override
        protected MoneyWiseXAnalysisPayeeValues getBaseValues() {
            return getBucket().getBaseValues();
        }

        @Override
        public MoneyWiseXAnalysisPayeeValues getValuesForEvent(final MoneyWiseXAnalysisEvent pEvent) {
            return getBucket().getValuesForEvent(pEvent);
        }

        @Override
        public TethysDecimal getDeltaForEvent(final MoneyWiseXAnalysisEvent pEvent) {
            return getBucket().getDeltaForEvent(pEvent, getCurrentAttribute());
        }

        @Override
        public MoneyWiseTransaction buildNewTransaction(final MoneyWiseTransDefaults pBuilder) {
            return pBuilder.buildTransaction(getBucket().getPayee());
        }
    }

    /**
     * TransactionCategory Bucket filter class.
     */
    public static class MoneyWiseXAnalysisTransCategoryFilter
            extends MoneyWiseXAnalysisFilter<MoneyWiseXAnalysisTransCategoryBucket, MoneyWiseXAnalysisTransAttr> {
        /**
         * Constructor.
         * @param pCategory the category bucket
         */
        public MoneyWiseXAnalysisTransCategoryFilter(final MoneyWiseXAnalysisTransCategoryBucket pCategory) {
            /* Store parameter */
            super(pCategory, MoneyWiseXAnalysisTransAttr.class);
            final boolean isExpense = pCategory.getTransactionCategory().getCategoryTypeClass().isExpense();
            setCurrentAttribute(isExpense
                    ? MoneyWiseXAnalysisTransAttr.EXPENSE
                    : MoneyWiseXAnalysisTransAttr.INCOME);
        }

        @Override
        public String getName() {
            return getBucket().getName();
        }

        @Override
        public MoneyWiseXAnalysisType getAnalysisType() {
            return MoneyWiseXAnalysisType.CATEGORY;
        }

        @Override
        protected MoneyWiseXAnalysisTransValues getBaseValues() {
            return getBucket().getBaseValues();
        }

        @Override
        public MoneyWiseXAnalysisTransValues getValuesForEvent(final MoneyWiseXAnalysisEvent pEvent) {
            return getBucket().getValuesForEvent(pEvent);
        }

        @Override
        public TethysDecimal getDeltaForEvent(final MoneyWiseXAnalysisEvent pEvent) {
            return getBucket().getDeltaForEvent(pEvent, getCurrentAttribute());
        }

        @Override
        public MoneyWiseTransaction buildNewTransaction(final MoneyWiseTransDefaults pBuilder) {
            return pBuilder.buildTransaction(getBucket().getTransactionCategory());
        }
    }

    /**
     * TaxBasis Bucket filter class.
     */
    public static class MoneyWiseXAnalysisTaxBasisFilter
            extends MoneyWiseXAnalysisFilter<MoneyWiseXAnalysisTaxBasisBucket, MoneyWiseXAnalysisTaxBasisAttr> {
        /**
         * Constructor.
         * @param pTaxBasis the taxBasis bucket
         */
        public MoneyWiseXAnalysisTaxBasisFilter(final MoneyWiseXAnalysisTaxBasisBucket pTaxBasis) {
            /* Store parameter */
            super(pTaxBasis, MoneyWiseXAnalysisTaxBasisAttr.class);
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        @Override
        public String getName() {
            return getBucket().getName();
        }

        @Override
        public MoneyWiseXAnalysisType getAnalysisType() {
            return MoneyWiseXAnalysisType.TAXBASIS;
        }

        @Override
        protected MoneyWiseXAnalysisTaxBasisValues getBaseValues() {
            return getBucket().getBaseValues();
        }

        @Override
        public MoneyWiseXAnalysisTaxBasisValues getValuesForEvent(final MoneyWiseXAnalysisEvent pEvent) {
            return getBucket().getValuesForEvent(pEvent);
        }

        @Override
        public TethysDecimal getDeltaForEvent(final MoneyWiseXAnalysisEvent pEvent) {
            return getBucket().getDeltaForEvent(pEvent, getCurrentAttribute());
        }

        @Override
        public MoneyWiseTransaction buildNewTransaction(final MoneyWiseTransDefaults pBuilder) {
            return null;
        }
    }

    /**
     * TransactionTag filter class.
     */
    public static class MoneyWiseXAnalysisTagFilter
            extends MoneyWiseXAnalysisFilter<MoneyWiseXAnalysisTransTagBucket, MoneyWiseXAnalysisAccountAttr> {
        /**
         * Constructor.
         * @param pTag the transactionTag
         */
        public MoneyWiseXAnalysisTagFilter(final MoneyWiseXAnalysisTransTagBucket pTag) {
            /* Store parameter */
            super(pTag, MoneyWiseXAnalysisAccountAttr.class);
            setCurrentAttribute(null);
        }

        @Override
        public String getName() {
            return getBucket().getName();
        }

        @Override
        public MoneyWiseXAnalysisType getAnalysisType() {
            return MoneyWiseXAnalysisType.TRANSTAG;
        }

        @Override
        public boolean filterEvent(final MoneyWiseXAnalysisEvent pEvent) {
            return pEvent.isHeader()
                    || !getBucket().hasEvent(pEvent);
        }

        @Override
        protected MoneyWiseXAnalysisAccountValues getBaseValues() {
            return null;
        }

        @Override
        public MoneyWiseXAnalysisAccountValues getValuesForEvent(final MoneyWiseXAnalysisEvent pEvent) {
            return null;
        }

        @Override
        public TethysDecimal getDeltaForEvent(final MoneyWiseXAnalysisEvent pEvent) {
            return null;
        }

        @Override
        public MoneyWiseTransaction buildNewTransaction(final MoneyWiseTransDefaults pBuilder) {
            return null;
        }
    }

    /**
     * All filter class.
     */
    public static final class MoneyWiseXAnalysisAllFilter
            extends MoneyWiseXAnalysisFilter<Void, MoneyWiseXAnalysisAccountAttr> {
        /**
         * Constructor.
         */
        public MoneyWiseXAnalysisAllFilter() {
            /* Store parameter */
            super(null, MoneyWiseXAnalysisAccountAttr.class);
            setCurrentAttribute(null);
        }

        @Override
        public String getName() {
            return MoneyWiseXAnalysisType.ALL.toString();
        }

        @Override
        public MoneyWiseXAnalysisType getAnalysisType() {
            return MoneyWiseXAnalysisType.ALL;
        }

        @Override
        public Void getBucket() {
            return null;
        }

        @Override
        public boolean filterEvent(final MoneyWiseXAnalysisEvent pEvent) {
            return pEvent.isHeader();
        }

        @Override
        protected MoneyWiseXAnalysisAccountValues getBaseValues() {
            return null;
        }

        @Override
        public MoneyWiseXAnalysisAccountValues getValuesForEvent(final MoneyWiseXAnalysisEvent pEvent) {
            return null;
        }

        @Override
        public TethysDecimal getDeltaForEvent(final MoneyWiseXAnalysisEvent pEvent) {
            return null;
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Only ever one instance */
            return this == pThat;
        }

        @Override
        public int hashCode() {
            return MetisFieldSet.HASH_PRIME;
        }

        @Override
        public MoneyWiseTransaction buildNewTransaction(final MoneyWiseTransDefaults pBuilder) {
            return pBuilder.buildTransaction(null);
        }
    }
}
