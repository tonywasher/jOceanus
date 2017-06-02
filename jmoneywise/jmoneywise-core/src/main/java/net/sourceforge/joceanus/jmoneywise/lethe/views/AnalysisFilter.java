/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.views;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.AccountAttribute;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.AccountBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.AccountBucket.AccountValues;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.AnalysisType;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.BucketAttribute;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.BucketValues;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.CashBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.DepositBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.LoanBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PayeeAttribute;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PayeeBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PayeeBucket.PayeeValues;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PortfolioCashBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityAttribute;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TaxBasisAttribute;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TaxBasisBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TaxBasisBucket.TaxBasisValues;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TransactionAttribute;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TransactionCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TransactionCategoryBucket.CategoryValues;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TransactionTagBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Cash;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Loan;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionBuilder;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;

/**
 * Analysis Filter Classes.
 * @param <B> the underlying bucket type
 * @param <T> the attribute for the filter
 */
public abstract class AnalysisFilter<B, T extends Enum<T> & BucketAttribute>
        implements MetisDataContents {
    /**
     * AllFilter.
     */
    public static final AllFilter FILTER_ALL = new AllFilter();

    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseViewResource.FILTER_NAME.getValue());

    /**
     * Bucket Field Id.
     */
    private static final MetisField FIELD_BUCKET = FIELD_DEFS.declareLocalField(MoneyWiseViewResource.FILTER_BUCKET.getValue());

    /**
     * Attribute Field Id.
     */
    private static final MetisField FIELD_ATTR = FIELD_DEFS.declareLocalField(MoneyWiseViewResource.FILTER_ATTR.getValue());

    /**
     * The Underlying bucket.
     */
    private final B theBucket;

    /**
     * The Current Attribute.
     */
    private T theAttr;

    /**
     * The Attribute class.
     */
    private final Class<T> theClass;

    /**
     * Constructor.
     * @param pBucket the underlying bucket
     * @param pClass the attribute class
     */
    protected AnalysisFilter(final B pBucket,
                             final Class<T> pClass) {
        theBucket = pBucket;
        theClass = pClass;
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        if (FIELD_ATTR.equals(pField)) {
            return theAttr;
        }
        if (FIELD_BUCKET.equals(pField)) {
            return theBucket == null
                                     ? MetisFieldValue.SKIP
                                     : theBucket;
        }

        /* Unknown */
        return MetisFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return getName();
    }

    /**
     * Set attribute.
     * @param pAttr the attribute
     */
    public void setCurrentAttribute(final BucketAttribute pAttr) {
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
    public abstract AnalysisType getAnalysisType();

    /**
     * Obtain underlying bucket.
     * @return theBucket
     */
    public B getBucket() {
        return theBucket;
    }

    /**
     * Should we filter this transaction out?
     * @param pTrans the transaction to check
     * @return true/false
     */
    public boolean filterTransaction(final Transaction pTrans) {
        /* Check whether this transaction is registered */
        return !pTrans.isHeader()
               && getValuesForTransaction(pTrans) == null;
    }

    /**
     * Obtain base bucket values.
     * @return the value
     */
    protected abstract BucketValues<?, T> getBaseValues();

    /**
     * Obtain values for transaction.
     * @param pTrans the transaction
     * @return the values
     */
    public abstract BucketValues<?, T> getValuesForTransaction(Transaction pTrans);

    /**
     * Obtain delta value for transaction.
     * @param pTrans the transaction
     * @return the delta value
     */
    public abstract TethysDecimal getDeltaForTransaction(Transaction pTrans);

    /**
     * Populate new transaction.
     * @param pBuilder the transaction builder
     * @return the new transaction (or null)
     */
    public abstract Transaction buildNewTransaction(TransactionBuilder pBuilder);

    /**
     * is the counter relevant?
     * @param pCounter the counter
     * @return true/false
     */
    public boolean isRelevantCounter(final BucketAttribute pCounter) {
        return true;
    }

    /**
     * Obtain starting value for attribute.
     * @return the value
     */
    public TethysDecimal getStartingBalance() {
        BucketValues<?, T> myValues = getBaseValues();
        return myValues.getDecimalValue(getCurrentAttribute());
    }

    /**
     * Obtain total money value for attribute.
     * @param pTrans the transaction to check
     * @return the value
     */
    public TethysDecimal getBalanceForTransaction(final Transaction pTrans) {
        BucketValues<?, T> myValues = getValuesForTransaction(pTrans);
        return (myValues == null)
                                  ? null
                                  : myValues.getDecimalValue(getCurrentAttribute());
    }

    /**
     * Obtain delta debit value for attribute.
     * @param pTrans the transaction to check
     * @return the value
     */
    public TethysDecimal getDebitForTransaction(final Transaction pTrans) {
        TethysDecimal myValue = getDeltaValueForTransaction(pTrans);
        if (myValue != null) {
            if (myValue.isPositive()
                || myValue.isZero()) {
                myValue = null;
            } else {
                myValue.negate();
            }
        }
        return (myValue != null)
                                 ? myValue
                                 : null;
    }

    /**
     * Obtain delta credit value for attribute.
     * @param pTrans the transaction to check
     * @return the value
     */
    public TethysDecimal getCreditForTransaction(final Transaction pTrans) {
        TethysDecimal myValue = getDeltaValueForTransaction(pTrans);
        return ((myValue != null)
                && myValue.isPositive() && myValue.isNonZero())
                                                                ? myValue
                                                                : null;
    }

    /**
     * Obtain delta value for attribute.
     * @param pTrans the transaction to check
     * @return the value
     */
    private TethysDecimal getDeltaValueForTransaction(final Transaction pTrans) {
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
        AnalysisFilter<?, ?> myThat = (AnalysisFilter<?, ?>) pThat;

        /* Check equality */
        return MetisDifference.isEqual(getBucket(), myThat.getBucket());
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
    public abstract static class AccountFilter<B extends AccountBucket<T>, T extends AssetBase<T>>
            extends AnalysisFilter<B, AccountAttribute> {
        /**
         * Constructor.
         * @param pAccount the account bucket
         */
        public AccountFilter(final B pAccount) {
            /* Store parameter */
            super(pAccount, AccountAttribute.class);
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        @Override
        public String getName() {
            return getBucket().getName();
        }

        @Override
        protected AccountValues getBaseValues() {
            return getBucket().getBaseValues();
        }

        @Override
        public AccountValues getValuesForTransaction(final Transaction pTrans) {
            return getBucket().getValuesForTransaction(pTrans);
        }

        @Override
        public TethysDecimal getDeltaForTransaction(final Transaction pTrans) {
            return getBucket().getDeltaForTransaction(pTrans, getCurrentAttribute());
        }

        @Override
        public Transaction buildNewTransaction(final TransactionBuilder pBuilder) {
            return pBuilder.buildTransaction(getBucket().getAccount());
        }
    }

    /**
     * Deposit Bucket filter class.
     */
    public static class DepositFilter
            extends AccountFilter<DepositBucket, Deposit> {
        /**
         * Constructor.
         * @param pDeposit the deposit bucket
         */
        public DepositFilter(final DepositBucket pDeposit) {
            /* Call super-constructor */
            super(pDeposit);
        }

        @Override
        public AnalysisType getAnalysisType() {
            return AnalysisType.DEPOSIT;
        }

        @Override
        public boolean isRelevantCounter(final BucketAttribute pCounter) {
            if (!(pCounter instanceof AccountAttribute)) {
                return false;
            }

            switch ((AccountAttribute) pCounter) {
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
    public static class CashFilter
            extends AccountFilter<CashBucket, Cash> {
        /**
         * Constructor.
         * @param pCash the cash bucket
         */
        public CashFilter(final CashBucket pCash) {
            /* Call super-constructor */
            super(pCash);
        }

        @Override
        public AnalysisType getAnalysisType() {
            return AnalysisType.CASH;
        }

        @Override
        public boolean isRelevantCounter(final BucketAttribute pCounter) {
            if (!(pCounter instanceof AccountAttribute)) {
                return false;
            }

            switch ((AccountAttribute) pCounter) {
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
    public static class LoanFilter
            extends AccountFilter<LoanBucket, Loan> {
        /**
         * Constructor.
         * @param pLoan the loan bucket
         */
        public LoanFilter(final LoanBucket pLoan) {
            /* Call super-constructor */
            super(pLoan);
        }

        @Override
        public AnalysisType getAnalysisType() {
            return AnalysisType.LOAN;
        }

        @Override
        public boolean isRelevantCounter(final BucketAttribute pCounter) {
            if (!(pCounter instanceof AccountAttribute)) {
                return false;
            }

            switch ((AccountAttribute) pCounter) {
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
    public static class SecurityFilter
            extends AnalysisFilter<SecurityBucket, SecurityAttribute> {
        /**
         * Constructor.
         * @param pSecurity the security bucket
         */
        public SecurityFilter(final SecurityBucket pSecurity) {
            /* Store parameter */
            super(pSecurity, SecurityAttribute.class);
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        @Override
        public String getName() {
            return getBucket().getDecoratedName();
        }

        @Override
        public AnalysisType getAnalysisType() {
            return AnalysisType.SECURITY;
        }

        @Override
        protected SecurityValues getBaseValues() {
            return getBucket().getBaseValues();
        }

        @Override
        public SecurityValues getValuesForTransaction(final Transaction pTrans) {
            return getBucket().getValuesForTransaction(pTrans);
        }

        @Override
        public TethysDecimal getDeltaForTransaction(final Transaction pTrans) {
            return getBucket().getDeltaForTransaction(pTrans, getCurrentAttribute());
        }

        @Override
        public Transaction buildNewTransaction(final TransactionBuilder pBuilder) {
            return pBuilder.buildTransaction(getBucket().getSecurityHolding());
        }

        @Override
        public boolean isRelevantCounter(final BucketAttribute pCounter) {
            if (!(pCounter instanceof SecurityAttribute)) {
                return false;
            }

            switch ((SecurityAttribute) pCounter) {
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
    public static class PortfolioCashFilter
            extends AnalysisFilter<PortfolioCashBucket, AccountAttribute> {
        /**
         * The portfolio bucket.
         */
        private final PortfolioBucket thePortfolio;

        /**
         * Constructor.
         * @param pPortfolio the portfolio bucket
         */
        public PortfolioCashFilter(final PortfolioBucket pPortfolio) {
            /* Store parameter */
            super(pPortfolio.getPortfolioCash(), AccountAttribute.class);
            thePortfolio = pPortfolio;
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        /**
         * Obtain portfolio bucket.
         * @return the portfolio bucket
         */
        public PortfolioBucket getPortfolioBucket() {
            return thePortfolio;
        }

        @Override
        public String getName() {
            return thePortfolio.getName();
        }

        @Override
        public AnalysisType getAnalysisType() {
            return AnalysisType.PORTFOLIO;
        }

        @Override
        protected AccountValues getBaseValues() {
            return getBucket().getBaseValues();
        }

        @Override
        public AccountValues getValuesForTransaction(final Transaction pTrans) {
            return getBucket().getValuesForTransaction(pTrans);
        }

        @Override
        public TethysDecimal getDeltaForTransaction(final Transaction pTrans) {
            return getBucket().getDeltaForTransaction(pTrans, getCurrentAttribute());
        }

        @Override
        public Transaction buildNewTransaction(final TransactionBuilder pBuilder) {
            return pBuilder.buildTransaction(getBucket().getAccount());
        }

        @Override
        public boolean isRelevantCounter(final BucketAttribute pCounter) {
            if (!(pCounter instanceof AccountAttribute)) {
                return false;
            }

            switch ((AccountAttribute) pCounter) {
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
    public static class PayeeFilter
            extends AnalysisFilter<PayeeBucket, PayeeAttribute> {
        /**
         * Constructor.
         * @param pPayee the payee bucket
         */
        public PayeeFilter(final PayeeBucket pPayee) {
            /* Store parameter */
            super(pPayee, PayeeAttribute.class);
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        @Override
        public String getName() {
            return getBucket().getName();
        }

        @Override
        public AnalysisType getAnalysisType() {
            return AnalysisType.PAYEE;
        }

        @Override
        protected PayeeValues getBaseValues() {
            return getBucket().getBaseValues();
        }

        @Override
        public PayeeValues getValuesForTransaction(final Transaction pTrans) {
            return getBucket().getValuesForTransaction(pTrans);
        }

        @Override
        public TethysDecimal getDeltaForTransaction(final Transaction pTrans) {
            return getBucket().getDeltaForTransaction(pTrans, getCurrentAttribute());
        }

        @Override
        public Transaction buildNewTransaction(final TransactionBuilder pBuilder) {
            return pBuilder.buildTransaction(getBucket().getPayee());
        }
    }

    /**
     * TransactionCategory Bucket filter class.
     */
    public static class TransactionCategoryFilter
            extends AnalysisFilter<TransactionCategoryBucket, TransactionAttribute> {
        /**
         * Constructor.
         * @param pCategory the category bucket
         */
        public TransactionCategoryFilter(final TransactionCategoryBucket pCategory) {
            /* Store parameter */
            super(pCategory, TransactionAttribute.class);
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        @Override
        public String getName() {
            return getBucket().getName();
        }

        @Override
        public AnalysisType getAnalysisType() {
            return AnalysisType.CATEGORY;
        }

        @Override
        protected CategoryValues getBaseValues() {
            return getBucket().getBaseValues();
        }

        @Override
        public CategoryValues getValuesForTransaction(final Transaction pTrans) {
            return getBucket().getValuesForTransaction(pTrans);
        }

        @Override
        public TethysDecimal getDeltaForTransaction(final Transaction pTrans) {
            return getBucket().getDeltaForTransaction(pTrans, getCurrentAttribute());
        }

        @Override
        public Transaction buildNewTransaction(final TransactionBuilder pBuilder) {
            return pBuilder.buildTransaction(getBucket().getTransactionCategory());
        }
    }

    /**
     * TaxBasis Bucket filter class.
     */
    public static class TaxBasisFilter
            extends AnalysisFilter<TaxBasisBucket, TaxBasisAttribute> {
        /**
         * Constructor.
         * @param pTaxBasis the taxBasis bucket
         */
        public TaxBasisFilter(final TaxBasisBucket pTaxBasis) {
            /* Store parameter */
            super(pTaxBasis, TaxBasisAttribute.class);
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        @Override
        public String getName() {
            return getBucket().getName();
        }

        @Override
        public AnalysisType getAnalysisType() {
            return AnalysisType.TAXBASIS;
        }

        @Override
        protected TaxBasisValues getBaseValues() {
            return getBucket().getBaseValues();
        }

        @Override
        public TaxBasisValues getValuesForTransaction(final Transaction pTrans) {
            return getBucket().getValuesForTransaction(pTrans);
        }

        @Override
        public TethysDecimal getDeltaForTransaction(final Transaction pTrans) {
            return getBucket().getDeltaForTransaction(pTrans, getCurrentAttribute());
        }

        @Override
        public Transaction buildNewTransaction(final TransactionBuilder pBuilder) {
            return null;
        }
    }

    /**
     * --== * TransactionTag filter class.
     */
    public static class TagFilter
            extends AnalysisFilter<TransactionTagBucket, AccountAttribute> {
        /**
         * Constructor.
         * @param pTag the transactionTag
         */
        public TagFilter(final TransactionTagBucket pTag) {
            /* Store parameter */
            super(pTag, AccountAttribute.class);
            setCurrentAttribute(null);
        }

        @Override
        public String getName() {
            return getBucket().getName();
        }

        @Override
        public AnalysisType getAnalysisType() {
            return AnalysisType.TRANSTAG;
        }

        @Override
        public boolean filterTransaction(final Transaction pTrans) {
            return pTrans.isHeader()
                   || !getBucket().hasTransaction(pTrans);
        }

        @Override
        protected AccountValues getBaseValues() {
            return null;
        }

        @Override
        public AccountValues getValuesForTransaction(final Transaction pTrans) {
            return null;
        }

        @Override
        public TethysDecimal getDeltaForTransaction(final Transaction pTrans) {
            return null;
        }

        @Override
        public Transaction buildNewTransaction(final TransactionBuilder pBuilder) {
            return null;
        }
    }

    /**
     * All filter class.
     */
    public static final class AllFilter
            extends AnalysisFilter<Void, AccountAttribute> {
        /**
         * Constructor.
         */
        private AllFilter() {
            /* Store parameter */
            super(null, AccountAttribute.class);
            setCurrentAttribute(null);
        }

        @Override
        public String getName() {
            return AnalysisType.ALL.toString();
        }

        @Override
        public AnalysisType getAnalysisType() {
            return AnalysisType.ALL;
        }

        @Override
        public Void getBucket() {
            return null;
        }

        @Override
        public boolean filterTransaction(final Transaction pTrans) {
            return pTrans.isHeader();
        }

        @Override
        protected AccountValues getBaseValues() {
            return null;
        }

        @Override
        public AccountValues getValuesForTransaction(final Transaction pTrans) {
            return null;
        }

        @Override
        public TethysDecimal getDeltaForTransaction(final Transaction pTrans) {
            return null;
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Only ever one instance */
            return this == pThat;
        }

        @Override
        public int hashCode() {
            return MetisFields.HASH_PRIME;
        }

        @Override
        public Transaction buildNewTransaction(final TransactionBuilder pBuilder) {
            return pBuilder.buildTransaction(null);
        }
    }
}
