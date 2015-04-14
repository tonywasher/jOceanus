/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.views;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.Difference;
import net.sourceforge.joceanus.jmetis.data.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket.AccountValues;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisType;
import net.sourceforge.joceanus.jmoneywise.analysis.BucketAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.BucketValues;
import net.sourceforge.joceanus.jmoneywise.analysis.CashBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.DepositBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.LoanBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeBucket.PayeeValues;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioCashBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket.TaxBasisValues;
import net.sourceforge.joceanus.jmoneywise.analysis.TransactionAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.TransactionCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.TransactionCategoryBucket.CategoryValues;
import net.sourceforge.joceanus.jmoneywise.analysis.TransactionTagBucket;
import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.Cash;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.Loan;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionBuilder;
import net.sourceforge.joceanus.jmoneywise.data.TransactionGroup;
import net.sourceforge.joceanus.jtethys.decimal.JDecimal;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;

/**
 * Analysis Filter Classes.
 * @param <B> the underlying bucket type
 * @param <T> the attribute for the filter
 */
public abstract class AnalysisFilter<B, T extends Enum<T> & BucketAttribute>
        implements JDataContents {
    /**
     * AllFilter.
     */
    public static final AllFilter FILTER_ALL = new AllFilter();

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(MoneyWiseViewResource.FILTER_NAME.getValue());

    /**
     * Bucket Field Id.
     */
    private static final JDataField FIELD_BUCKET = FIELD_DEFS.declareLocalField(MoneyWiseViewResource.FILTER_BUCKET.getValue());

    /**
     * Attribute Field Id.
     */
    private static final JDataField FIELD_ATTR = FIELD_DEFS.declareLocalField(MoneyWiseViewResource.FILTER_ATTR.getValue());

    /**
     * Combine groups Field Id.
     */
    private static final JDataField FIELD_COMBINE = FIELD_DEFS.declareLocalField(MoneyWiseViewResource.FILTER_COMBINE.getValue());

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
     * Do we combine grouped transactions?
     */
    private Boolean doCombineGroups;

    /**
     * Constructor.
     * @param pBucket the underlying bucket
     * @param pClass the attribute class
     */
    protected AnalysisFilter(final B pBucket,
                             final Class<T> pClass) {
        theBucket = pBucket;
        theClass = pClass;
        doCombineGroups = Boolean.FALSE;
    }

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_ATTR.equals(pField)) {
            return theAttr;
        }
        if (FIELD_BUCKET.equals(pField)) {
            return theBucket == null
                                    ? JDataFieldValue.SKIP
                                    : theBucket;
        }
        if (FIELD_COMBINE.equals(pField)) {
            return doCombineGroups
                                  ? doCombineGroups
                                  : JDataFieldValue.SKIP;
        }

        /* Unknown */
        return JDataFieldValue.UNKNOWN;
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
     * Obtain combine groups setting.
     * @return true/false
     */
    public Boolean getCombineGroups() {
        return doCombineGroups;
    }

    /**
     * Set combine groups setting.
     * @param pCombineGroups true/false
     */
    public void setCombineGroups(final Boolean pCombineGroups) {
        doCombineGroups = pCombineGroups;
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
        /* If this is a split event */
        if (doCombineGroups
            && pTrans.isSplit()) {
            /* Filter out children */
            if (pTrans.isChild()) {
                return true;
            }

            /* Access the group */
            TransactionList myList = pTrans.getList();
            TransactionGroup myGroup = myList.getGroup(pTrans);

            /* Check parent */
            if (!filterSingleTransaction(pTrans)) {
                return false;
            }

            /* Loop through the children */
            Iterator<Transaction> myIterator = myGroup.iterator();
            while (myIterator.hasNext()) {
                Transaction myTrans = myIterator.next();

                /* Check transaction */
                if (!filterSingleTransaction(myTrans)) {
                    return false;
                }
            }

            /* Ignore Transaction Group */
            return true;
        }

        /* Check as a single transaction */
        return filterSingleTransaction(pTrans);
    }

    /**
     * Should we filter this transaction out?
     * @param pTrans the transaction to check
     * @return true/false
     */
    protected boolean filterSingleTransaction(final Transaction pTrans) {
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
    public abstract BucketValues<?, T> getValuesForTransaction(final Transaction pTrans);

    /**
     * Obtain delta value for transaction.
     * @param pTrans the transaction
     * @return the delta value
     */
    public abstract JDecimal getDeltaForTransaction(final Transaction pTrans);

    /**
     * Populate new transaction.
     * @param pBuilder the transaction builder
     * @return the new transaction (or null)
     */
    public abstract Transaction buildNewTransaction(final TransactionBuilder pBuilder);

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
    public JDecimal getStartingBalance() {
        BucketValues<?, T> myValues = getBaseValues();
        return myValues.getDecimalValue(getCurrentAttribute());
    }

    /**
     * Obtain total money value for attribute.
     * @param pTrans the transaction to check
     * @return the value
     */
    public JDecimal getBalanceForTransaction(final Transaction pTrans) {
        /* If this is a split transaction */
        if (doCombineGroups
            && pTrans.isSplit()) {
            /* Access the group */
            TransactionList myList = pTrans.getList();
            TransactionGroup myGroup = myList.getGroup(pTrans);

            /* Initialise return as the balance for the parent */
            JDecimal myBalance = getSingleBalanceForTransaction(pTrans);

            /* Loop through the children */
            Iterator<Transaction> myIterator = myGroup.iterator();
            while (myIterator.hasNext()) {
                Transaction myTrans = myIterator.next();

                /* Access Balance for transaction */
                JDecimal myValue = getSingleBalanceForTransaction(myTrans);
                if (myValue != null) {
                    /* We need the latest value */
                    myBalance = myValue;
                }
            }

            /* Return the balance */
            return myBalance;
        }

        /* Obtain single transaction value */
        return getSingleBalanceForTransaction(pTrans);
    }

    /**
     * Obtain delta debit value for attribute.
     * @param pTrans the transaction to check
     * @return the value
     */
    public JDecimal getDebitForTransaction(final Transaction pTrans) {
        JDecimal myValue = getDeltaValueForTransaction(pTrans);
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
    public JDecimal getCreditForTransaction(final Transaction pTrans) {
        JDecimal myValue = getDeltaValueForTransaction(pTrans);
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
    private JDecimal getDeltaValueForTransaction(final Transaction pTrans) {
        /* If this is a split transaction */
        if (doCombineGroups
            && pTrans.isSplit()) {
            /* Access the group */
            TransactionList myList = pTrans.getList();
            TransactionGroup myGroup = myList.getGroup(pTrans);

            /* Initialise return value as delta for parent */
            JDecimal myTotal = getDeltaForTransaction(pTrans);

            /* Loop through the children */
            Iterator<Transaction> myIterator = myGroup.iterator();
            while (myIterator.hasNext()) {
                Transaction myTrans = myIterator.next();

                /* Access Delta for transaction */
                JDecimal myDelta = getDeltaForTransaction(myTrans);
                if (myDelta != null) {
                    /* If this is the first value */
                    if (myTotal == null) {
                        /* Record as value */
                        myTotal = myDelta;

                        /* else need to add values */
                    } else {
                        /* add values appropriately */
                        myTotal = addDecimals(myTotal, myDelta);
                    }
                }
            }

            /* Return the total */
            return myTotal;
        }

        /* Obtain single transaction value */
        return getDeltaForTransaction(pTrans);
    }

    /**
     * Add decimal values.
     * @param pFirst the first decimal
     * @param pSecond the second decimal
     * @return the sum
     */
    private JDecimal addDecimals(final JDecimal pFirst,
                                 final JDecimal pSecond) {
        switch (theAttr.getDataType()) {
            case MONEY:
                ((JMoney) pFirst).addAmount((JMoney) pSecond);
                return pFirst;
            case UNITS:
                ((JUnits) pFirst).addUnits((JUnits) pSecond);
                return pFirst;
            default:
                return null;
        }
    }

    /**
     * Obtain total money value for attribute.
     * @param pTrans the transaction to check
     * @return the value
     */
    public JDecimal getSingleBalanceForTransaction(final Transaction pTrans) {
        BucketValues<?, T> myValues = getValuesForTransaction(pTrans);
        return (myValues == null)
                                 ? null
                                 : myValues.getDecimalValue(getCurrentAttribute());
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
        return Difference.isEqual(getBucket(), myThat.getBucket());
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
        public JDecimal getDeltaForTransaction(final Transaction pTrans) {
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
                case BADDEBT:
                    return getBucket().isPeer2Peer();
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
                case BADDEBT:
                case SPEND:
                    return false;
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
                case BADDEBT:
                    return false;
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
        public JDecimal getDeltaForTransaction(final Transaction pTrans) {
            return getBucket().getDeltaForTransaction(pTrans, getCurrentAttribute());
        }

        @Override
        public Transaction buildNewTransaction(final TransactionBuilder pBuilder) {
            return pBuilder.buildTransaction(getBucket().getSecurityHolding());
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
        public JDecimal getDeltaForTransaction(final Transaction pTrans) {
            return getBucket().getDeltaForTransaction(pTrans, getCurrentAttribute());
        }

        @Override
        public Transaction buildNewTransaction(final TransactionBuilder pBuilder) {
            return pBuilder.buildTransaction(getBucket().getAccount());
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
        public JDecimal getDeltaForTransaction(final Transaction pTrans) {
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
        public JDecimal getDeltaForTransaction(final Transaction pTrans) {
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
        public JDecimal getDeltaForTransaction(final Transaction pTrans) {
            return getBucket().getDeltaForTransaction(pTrans, getCurrentAttribute());
        }

        @Override
        public Transaction buildNewTransaction(final TransactionBuilder pBuilder) {
            return null;
        }
    }

    /**
     * TransactionTag filter class.
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
        protected boolean filterSingleTransaction(final Transaction pTrans) {
            return pTrans.isHeader() || !getBucket().hasTransaction(pTrans);
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
        public JDecimal getDeltaForTransaction(final Transaction pTrans) {
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
        protected boolean filterSingleTransaction(final Transaction pTrans) {
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
        public JDecimal getDeltaForTransaction(final Transaction pTrans) {
            return null;
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Only ever one instance */
            return this == pThat;
        }

        @Override
        public int hashCode() {
            return JDataFields.HASH_PRIME;
        }

        @Override
        public Transaction buildNewTransaction(final TransactionBuilder pBuilder) {
            return pBuilder.buildTransaction(null);
        }
    }
}
