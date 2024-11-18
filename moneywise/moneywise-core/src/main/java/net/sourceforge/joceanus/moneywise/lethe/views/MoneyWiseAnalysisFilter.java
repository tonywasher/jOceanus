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
package net.sourceforge.joceanus.moneywise.lethe.views;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.base.MoneyWiseAnalysisAttribute;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.base.MoneyWiseAnalysisValues;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisAccountBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisCashBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisDepositBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisLoanBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPayeeBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPortfolioBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPortfolioCashBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisSecurityBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisTaxBasisBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisTransCategoryBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisTransTagBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisType;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisAccountAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisAccountValues;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisCategoryValues;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisPayeeAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisPayeeValues;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisSecurityAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisSecurityValues;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisTaxBasisAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisTaxBasisValues;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisTransAttr;
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
public abstract class MoneyWiseAnalysisFilter<B, T extends Enum<T> & MoneyWiseAnalysisAttribute>
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisFieldSet<MoneyWiseAnalysisFilter> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisFilter.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.MONEYWISEDATA_RANGE, MoneyWiseAnalysisFilter::getDateRange);
        FIELD_DEFS.declareLocalField(MoneyWiseViewResource.FILTER_BUCKET, MoneyWiseAnalysisFilter::getBucket);
        FIELD_DEFS.declareLocalField(MoneyWiseViewResource.FILTER_ATTR, MoneyWiseAnalysisFilter::getCurrentAttribute);
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
    protected MoneyWiseAnalysisFilter(final B pBucket,
                                      final Class<T> pClass) {
        theBucket = pBucket;
        theClass = pClass;
        theDateRange = new TethysDateRange();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public MetisFieldSet<MoneyWiseAnalysisFilter> getDataFieldSet() {
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
    public void setCurrentAttribute(final MoneyWiseAnalysisAttribute pAttr) {
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
    public abstract MoneyWiseAnalysisType getAnalysisType();

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
     * Should we filter this transaction out?
     * @param pTrans the transaction to check
     * @return true/false
     */
    public boolean filterTransaction(final MoneyWiseTransaction pTrans) {
        /* Check whether this transaction is registered */
        return !pTrans.isHeader()
                && (theDateRange.compareToDate(pTrans.getDate()) != 0
                    || getValuesForTransaction(pTrans) == null);
    }

    /**
     * Obtain base bucket values.
     * @return the value
     */
    protected abstract MoneyWiseAnalysisValues<?, T> getBaseValues();

    /**
     * Obtain values for transaction.
     * @param pTrans the transaction
     * @return the values
     */
    public abstract MoneyWiseAnalysisValues<?, T> getValuesForTransaction(MoneyWiseTransaction pTrans);

    /**
     * Obtain delta value for transaction.
     * @param pTrans the transaction
     * @return the delta value
     */
    public abstract TethysDecimal getDeltaForTransaction(MoneyWiseTransaction pTrans);

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
    public boolean isRelevantCounter(final MoneyWiseAnalysisAttribute pCounter) {
        return true;
    }

    /**
     * Obtain starting value for attribute.
     * @return the value
     */
    public TethysDecimal getStartingBalance() {
        final MoneyWiseAnalysisValues<?, T> myValues = getBaseValues();
        return myValues.getDecimalValue(getCurrentAttribute());
    }

    /**
     * Obtain total money value for attribute.
     * @param pTrans the transaction to check
     * @return the value
     */
    public TethysDecimal getBalanceForTransaction(final MoneyWiseTransaction pTrans) {
        final MoneyWiseAnalysisValues<?, T> myValues = getValuesForTransaction(pTrans);
        return myValues == null
                ? null
                : myValues.getDecimalValue(getCurrentAttribute());
    }

    /**
     * Obtain delta debit value for attribute.
     * @param pTrans the transaction to check
     * @return the value
     */
    public TethysDecimal getDebitForTransaction(final MoneyWiseTransaction pTrans) {
        TethysDecimal myValue = getDeltaValueForTransaction(pTrans);
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
     * @param pTrans the transaction to check
     * @return the value
     */
    public TethysDecimal getCreditForTransaction(final MoneyWiseTransaction pTrans) {
        final TethysDecimal myValue = getDeltaValueForTransaction(pTrans);
        return (myValue != null
                && myValue.isPositive() && myValue.isNonZero())
                ? myValue
                : null;
    }

    /**
     * Obtain delta value for attribute.
     * @param pTrans the transaction to check
     * @return the value
     */
    private TethysDecimal getDeltaValueForTransaction(final MoneyWiseTransaction pTrans) {
        return getDeltaForTransaction(pTrans);
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
        final MoneyWiseAnalysisFilter<?, ?> myThat = (MoneyWiseAnalysisFilter<?, ?>) pThat;

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
    public abstract static class MoneyWiseAnalysisAccountFilter<B extends MoneyWiseAnalysisAccountBucket<T>, T extends MoneyWiseAssetBase>
            extends MoneyWiseAnalysisFilter<B, MoneyWiseAnalysisAccountAttr> {
        /**
         * Constructor.
         * @param pAccount the account bucket
         */
        protected MoneyWiseAnalysisAccountFilter(final B pAccount) {
            /* Store parameter */
            super(pAccount, MoneyWiseAnalysisAccountAttr.class);
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        @Override
        public String getName() {
            return getBucket().getName();
        }

        @Override
        protected MoneyWiseAnalysisAccountValues getBaseValues() {
            return getBucket().getBaseValues();
        }

        @Override
        public MoneyWiseAnalysisAccountValues getValuesForTransaction(final MoneyWiseTransaction pTrans) {
            return getBucket().getValuesForTransaction(pTrans);
        }

        @Override
        public TethysDecimal getDeltaForTransaction(final MoneyWiseTransaction pTrans) {
            return getBucket().getDeltaForTransaction(pTrans, getCurrentAttribute());
        }

        @Override
        public MoneyWiseTransaction buildNewTransaction(final MoneyWiseTransDefaults pBuilder) {
            return pBuilder.buildTransaction(getBucket().getAccount());
        }
    }

    /**
     * Deposit Bucket filter class.
     */
    public static class MoneyWiseAnalysisDepositFilter
            extends MoneyWiseAnalysisAccountFilter<MoneyWiseAnalysisDepositBucket, MoneyWiseDeposit> {
        /**
         * Constructor.
         * @param pDeposit the deposit bucket
         */
        public MoneyWiseAnalysisDepositFilter(final MoneyWiseAnalysisDepositBucket pDeposit) {
            /* Call super-constructor */
            super(pDeposit);
        }

        @Override
        public MoneyWiseAnalysisType getAnalysisType() {
            return MoneyWiseAnalysisType.DEPOSIT;
        }

        @Override
        public boolean isRelevantCounter(final MoneyWiseAnalysisAttribute pCounter) {
            if (!(pCounter instanceof MoneyWiseAnalysisAccountAttr)) {
                return false;
            }

            switch ((MoneyWiseAnalysisAccountAttr) pCounter) {
                case BADDEBTCAPITAL:
                case BADDEBTINTEREST:
                    return getBucket().isPeer2Peer();
                case FOREIGNVALUE:
                    return getBucket().isForeignCurrency();
                case SPEND:
                    return false;
                default:
                    return true;
            }
        }
    }

    /**
     * Cash Bucket filter class.
     */
    public static class MoneyWiseAnalysisCashFilter
            extends MoneyWiseAnalysisAccountFilter<MoneyWiseAnalysisCashBucket, MoneyWiseCash> {
        /**
         * Constructor.
         * @param pCash the cash bucket
         */
        public MoneyWiseAnalysisCashFilter(final MoneyWiseAnalysisCashBucket pCash) {
            /* Call super-constructor */
            super(pCash);
        }

        @Override
        public MoneyWiseAnalysisType getAnalysisType() {
            return MoneyWiseAnalysisType.CASH;
        }

        @Override
        public boolean isRelevantCounter(final MoneyWiseAnalysisAttribute pCounter) {
            if (!(pCounter instanceof MoneyWiseAnalysisAccountAttr)) {
                return false;
            }

            switch ((MoneyWiseAnalysisAccountAttr) pCounter) {
                case BADDEBTCAPITAL:
                case BADDEBTINTEREST:
                case SPEND:
                    return false;
                case FOREIGNVALUE:
                    return getBucket().isForeignCurrency();
                default:
                    return true;
            }
        }
    }

    /**
     * Loan Bucket filter class.
     */
    public static class MoneyWiseAnalysisLoanFilter
            extends MoneyWiseAnalysisAccountFilter<MoneyWiseAnalysisLoanBucket, MoneyWiseLoan> {
        /**
         * Constructor.
         * @param pLoan the loan bucket
         */
        public MoneyWiseAnalysisLoanFilter(final MoneyWiseAnalysisLoanBucket pLoan) {
            /* Call super-constructor */
            super(pLoan);
        }

        @Override
        public MoneyWiseAnalysisType getAnalysisType() {
            return MoneyWiseAnalysisType.LOAN;
        }

        @Override
        public boolean isRelevantCounter(final MoneyWiseAnalysisAttribute pCounter) {
            if (!(pCounter instanceof MoneyWiseAnalysisAccountAttr)) {
                return false;
            }

            switch ((MoneyWiseAnalysisAccountAttr) pCounter) {
                case SPEND:
                    return getBucket().isCreditCard();
                case BADDEBTCAPITAL:
                case BADDEBTINTEREST:
                    return false;
                case FOREIGNVALUE:
                    return getBucket().isForeignCurrency();
                default:
                    return true;
            }
        }
    }

    /**
     * Security Bucket filter class.
     */
    public static class MoneyWiseAnalysisSecurityFilter
            extends MoneyWiseAnalysisFilter<MoneyWiseAnalysisSecurityBucket, MoneyWiseAnalysisSecurityAttr> {
        /**
         * Constructor.
         * @param pSecurity the security bucket
         */
        public MoneyWiseAnalysisSecurityFilter(final MoneyWiseAnalysisSecurityBucket pSecurity) {
            /* Store parameter */
            super(pSecurity, MoneyWiseAnalysisSecurityAttr.class);
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        @Override
        public String getName() {
            return getBucket().getDecoratedName();
        }

        @Override
        public MoneyWiseAnalysisType getAnalysisType() {
            return MoneyWiseAnalysisType.SECURITY;
        }

        @Override
        protected MoneyWiseAnalysisSecurityValues getBaseValues() {
            return getBucket().getBaseValues();
        }

        @Override
        public MoneyWiseAnalysisSecurityValues getValuesForTransaction(final MoneyWiseTransaction pTrans) {
            return getBucket().getValuesForTransaction(pTrans);
        }

        @Override
        public TethysDecimal getDeltaForTransaction(final MoneyWiseTransaction pTrans) {
            return getBucket().getDeltaForTransaction(pTrans, getCurrentAttribute());
        }

        @Override
        public MoneyWiseTransaction buildNewTransaction(final MoneyWiseTransDefaults pBuilder) {
            return pBuilder.buildTransaction(getBucket().getSecurityHolding());
        }

        @Override
        public boolean isRelevantCounter(final MoneyWiseAnalysisAttribute pCounter) {
            if (!(pCounter instanceof MoneyWiseAnalysisSecurityAttr)) {
                return false;
            }

            switch ((MoneyWiseAnalysisSecurityAttr) pCounter) {
                case FOREIGNINVESTED:
                    return getBucket().isForeignCurrency();
                case INVESTED:
                case DIVIDEND:
                case RESIDUALCOST:
                case REALISEDGAINS:
                case UNITS:
                case GROWTHADJUST:
                    return true;
                default:
                    return false;
            }
        }
    }

    /**
     * Portfolio Bucket filter class.
     */
    public static class MoneyWiseAnalysisPortfolioCashFilter
            extends MoneyWiseAnalysisFilter<MoneyWiseAnalysisPortfolioCashBucket, MoneyWiseAnalysisAccountAttr> {
        /**
         * The portfolio bucket.
         */
        private final MoneyWiseAnalysisPortfolioBucket thePortfolio;

        /**
         * Constructor.
         * @param pPortfolio the portfolio bucket
         */
        public MoneyWiseAnalysisPortfolioCashFilter(final MoneyWiseAnalysisPortfolioBucket pPortfolio) {
            /* Store parameter */
            super(pPortfolio.getPortfolioCash(), MoneyWiseAnalysisAccountAttr.class);
            thePortfolio = pPortfolio;
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        /**
         * Obtain portfolio bucket.
         * @return the portfolio bucket
         */
        public MoneyWiseAnalysisPortfolioBucket getPortfolioBucket() {
            return thePortfolio;
        }

        @Override
        public String getName() {
            return thePortfolio.getName();
        }

        @Override
        public MoneyWiseAnalysisType getAnalysisType() {
            return MoneyWiseAnalysisType.PORTFOLIO;
        }

        @Override
        protected MoneyWiseAnalysisAccountValues getBaseValues() {
            return getBucket().getBaseValues();
        }

        @Override
        public MoneyWiseAnalysisAccountValues getValuesForTransaction(final MoneyWiseTransaction pTrans) {
            return getBucket().getValuesForTransaction(pTrans);
        }

        @Override
        public TethysDecimal getDeltaForTransaction(final MoneyWiseTransaction pTrans) {
            return getBucket().getDeltaForTransaction(pTrans, getCurrentAttribute());
        }

        @Override
        public MoneyWiseTransaction buildNewTransaction(final MoneyWiseTransDefaults pBuilder) {
            return pBuilder.buildTransaction(getBucket().getAccount());
        }

        @Override
        public boolean isRelevantCounter(final MoneyWiseAnalysisAttribute pCounter) {
            if (!(pCounter instanceof MoneyWiseAnalysisAccountAttr)) {
                return false;
            }

            switch ((MoneyWiseAnalysisAccountAttr) pCounter) {
                case FOREIGNVALUE:
                    return getBucket().isForeignCurrency();
                case VALUATION:
                    return true;
                case BADDEBTCAPITAL:
                case BADDEBTINTEREST:
                case SPEND:
                    return false;
                default:
                    return true;
            }
        }
    }

    /**
     * Payee Bucket filter class.
     */
    public static class MoneyWiseAnalysisPayeeFilter
            extends MoneyWiseAnalysisFilter<MoneyWiseAnalysisPayeeBucket, MoneyWiseAnalysisPayeeAttr> {
        /**
         * Constructor.
         * @param pPayee the payee bucket
         */
        public MoneyWiseAnalysisPayeeFilter(final MoneyWiseAnalysisPayeeBucket pPayee) {
            /* Store parameter */
            super(pPayee, MoneyWiseAnalysisPayeeAttr.class);
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        @Override
        public String getName() {
            return getBucket().getName();
        }

        @Override
        public MoneyWiseAnalysisType getAnalysisType() {
            return MoneyWiseAnalysisType.PAYEE;
        }

        @Override
        protected MoneyWiseAnalysisPayeeValues getBaseValues() {
            return getBucket().getBaseValues();
        }

        @Override
        public MoneyWiseAnalysisPayeeValues getValuesForTransaction(final MoneyWiseTransaction pTrans) {
            return getBucket().getValuesForTransaction(pTrans);
        }

        @Override
        public TethysDecimal getDeltaForTransaction(final MoneyWiseTransaction pTrans) {
            return getBucket().getDeltaForTransaction(pTrans, getCurrentAttribute());
        }

        @Override
        public MoneyWiseTransaction buildNewTransaction(final MoneyWiseTransDefaults pBuilder) {
            return pBuilder.buildTransaction(getBucket().getPayee());
        }
    }

    /**
     * TransactionCategory Bucket filter class.
     */
    public static class MoneyWiseAnalysisTransCategoryFilter
            extends MoneyWiseAnalysisFilter<MoneyWiseAnalysisTransCategoryBucket, MoneyWiseAnalysisTransAttr> {
        /**
         * Constructor.
         * @param pCategory the category bucket
         */
        public MoneyWiseAnalysisTransCategoryFilter(final MoneyWiseAnalysisTransCategoryBucket pCategory) {
            /* Store parameter */
            super(pCategory, MoneyWiseAnalysisTransAttr.class);
            final boolean isExpense = pCategory.getTransactionCategory().getCategoryTypeClass().isExpense();
            setCurrentAttribute(isExpense
                    ? MoneyWiseAnalysisTransAttr.EXPENSE
                    : MoneyWiseAnalysisTransAttr.INCOME);
        }

        @Override
        public String getName() {
            return getBucket().getName();
        }

        @Override
        public MoneyWiseAnalysisType getAnalysisType() {
            return MoneyWiseAnalysisType.CATEGORY;
        }

        @Override
        protected MoneyWiseAnalysisCategoryValues getBaseValues() {
            return getBucket().getBaseValues();
        }

        @Override
        public MoneyWiseAnalysisCategoryValues getValuesForTransaction(final MoneyWiseTransaction pTrans) {
            return getBucket().getValuesForTransaction(pTrans);
        }

        @Override
        public TethysDecimal getDeltaForTransaction(final MoneyWiseTransaction pTrans) {
            return getBucket().getDeltaForTransaction(pTrans, getCurrentAttribute());
        }

        @Override
        public MoneyWiseTransaction buildNewTransaction(final MoneyWiseTransDefaults pBuilder) {
            return pBuilder.buildTransaction(getBucket().getTransactionCategory());
        }
    }

    /**
     * TaxBasis Bucket filter class.
     */
    public static class MoneyWiseAnalysisTaxBasisFilter
            extends MoneyWiseAnalysisFilter<MoneyWiseAnalysisTaxBasisBucket, MoneyWiseAnalysisTaxBasisAttr> {
        /**
         * Constructor.
         * @param pTaxBasis the taxBasis bucket
         */
        public MoneyWiseAnalysisTaxBasisFilter(final MoneyWiseAnalysisTaxBasisBucket pTaxBasis) {
            /* Store parameter */
            super(pTaxBasis, MoneyWiseAnalysisTaxBasisAttr.class);
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        @Override
        public String getName() {
            return getBucket().getName();
        }

        @Override
        public MoneyWiseAnalysisType getAnalysisType() {
            return MoneyWiseAnalysisType.TAXBASIS;
        }

        @Override
        protected MoneyWiseAnalysisTaxBasisValues getBaseValues() {
            return getBucket().getBaseValues();
        }

        @Override
        public MoneyWiseAnalysisTaxBasisValues getValuesForTransaction(final MoneyWiseTransaction pTrans) {
            return getBucket().getValuesForTransaction(pTrans);
        }

        @Override
        public TethysDecimal getDeltaForTransaction(final MoneyWiseTransaction pTrans) {
            return getBucket().getDeltaForTransaction(pTrans, getCurrentAttribute());
        }

        @Override
        public MoneyWiseTransaction buildNewTransaction(final MoneyWiseTransDefaults pBuilder) {
            return null;
        }
    }

    /**
     * TransactionTag filter class.
     */
    public static class MoneyWiseAnalysisTagFilter
            extends MoneyWiseAnalysisFilter<MoneyWiseAnalysisTransTagBucket, MoneyWiseAnalysisAccountAttr> {
        /**
         * Constructor.
         * @param pTag the transactionTag
         */
        public MoneyWiseAnalysisTagFilter(final MoneyWiseAnalysisTransTagBucket pTag) {
            /* Store parameter */
            super(pTag, MoneyWiseAnalysisAccountAttr.class);
            setCurrentAttribute(null);
        }

        @Override
        public String getName() {
            return getBucket().getName();
        }

        @Override
        public MoneyWiseAnalysisType getAnalysisType() {
            return MoneyWiseAnalysisType.TRANSTAG;
        }

        @Override
        public boolean filterTransaction(final MoneyWiseTransaction pTrans) {
            return pTrans.isHeader()
                    || !getBucket().hasTransaction(pTrans);
        }

        @Override
        protected MoneyWiseAnalysisAccountValues getBaseValues() {
            return null;
        }

        @Override
        public MoneyWiseAnalysisAccountValues getValuesForTransaction(final MoneyWiseTransaction pTrans) {
            return null;
        }

        @Override
        public TethysDecimal getDeltaForTransaction(final MoneyWiseTransaction pTrans) {
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
    public static final class MoneyWiseAnalysisAllFilter
            extends MoneyWiseAnalysisFilter<Void, MoneyWiseAnalysisAccountAttr> {
        /**
         * Constructor.
         */
        public MoneyWiseAnalysisAllFilter() {
            /* Store parameter */
            super(null, MoneyWiseAnalysisAccountAttr.class);
            setCurrentAttribute(null);
        }

        @Override
        public String getName() {
            return MoneyWiseAnalysisType.ALL.toString();
        }

        @Override
        public MoneyWiseAnalysisType getAnalysisType() {
            return MoneyWiseAnalysisType.ALL;
        }

        @Override
        public Void getBucket() {
            return null;
        }

        @Override
        public boolean filterTransaction(final MoneyWiseTransaction pTrans) {
            return pTrans.isHeader();
        }

        @Override
        protected MoneyWiseAnalysisAccountValues getBaseValues() {
            return null;
        }

        @Override
        public MoneyWiseAnalysisAccountValues getValuesForTransaction(final MoneyWiseTransaction pTrans) {
            return null;
        }

        @Override
        public TethysDecimal getDeltaForTransaction(final MoneyWiseTransaction pTrans) {
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
